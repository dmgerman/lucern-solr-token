begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|LeafFieldComparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TimSorter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
operator|.
name|PackedLongValues
import|;
end_import
begin_comment
comment|/**  * Sorts documents of a given index by returning a permutation on the document  * IDs.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Sorter
specifier|final
class|class
name|Sorter
block|{
DECL|field|sort
specifier|final
name|Sort
name|sort
decl_stmt|;
comment|/** Creates a new Sorter to sort the index with {@code sort} */
DECL|method|Sorter
name|Sorter
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
if|if
condition|(
name|sort
operator|.
name|needsScores
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot sort an index with a Sort that refers to the relevance score"
argument_list|)
throw|;
block|}
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
block|}
comment|/**    * A permutation of doc IDs. For every document ID between<tt>0</tt> and    * {@link IndexReader#maxDoc()},<code>oldToNew(newToOld(docID))</code> must    * return<code>docID</code>.    */
DECL|class|DocMap
specifier|static
specifier|abstract
class|class
name|DocMap
block|{
comment|/** Given a doc ID from the original index, return its ordinal in the      *  sorted index. */
DECL|method|oldToNew
specifier|abstract
name|int
name|oldToNew
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Given the ordinal of a doc ID, return its doc ID in the original index. */
DECL|method|newToOld
specifier|abstract
name|int
name|newToOld
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Return the number of documents in this map. This must be equal to the      *  {@link org.apache.lucene.index.LeafReader#maxDoc() number of documents} of the      *  {@link org.apache.lucene.index.LeafReader} which is sorted. */
DECL|method|size
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
block|}
comment|/** Check consistency of a {@link DocMap}, useful for assertions. */
DECL|method|isConsistent
specifier|static
name|boolean
name|isConsistent
parameter_list|(
name|DocMap
name|docMap
parameter_list|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|docMap
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|newID
init|=
name|docMap
operator|.
name|oldToNew
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|int
name|oldID
init|=
name|docMap
operator|.
name|newToOld
argument_list|(
name|newID
argument_list|)
decl_stmt|;
assert|assert
name|newID
operator|>=
literal|0
operator|&&
name|newID
operator|<
name|maxDoc
operator|:
literal|"doc IDs must be in [0-"
operator|+
name|maxDoc
operator|+
literal|"[, got "
operator|+
name|newID
assert|;
assert|assert
name|i
operator|==
name|oldID
operator|:
literal|"mapping is inconsistent: "
operator|+
name|i
operator|+
literal|" --oldToNew--> "
operator|+
name|newID
operator|+
literal|" --newToOld--> "
operator|+
name|oldID
assert|;
if|if
condition|(
name|i
operator|!=
name|oldID
operator|||
name|newID
operator|<
literal|0
operator|||
name|newID
operator|>=
name|maxDoc
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** A comparator of doc IDs. */
DECL|class|DocComparator
specifier|static
specifier|abstract
class|class
name|DocComparator
block|{
comment|/** Compare docID1 against docID2. The contract for the return value is the      *  same as {@link Comparator#compare(Object, Object)}. */
DECL|method|compare
specifier|public
specifier|abstract
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|docID2
parameter_list|)
function_decl|;
block|}
DECL|class|DocValueSorter
specifier|private
specifier|static
specifier|final
class|class
name|DocValueSorter
extends|extends
name|TimSorter
block|{
DECL|field|docs
specifier|private
specifier|final
name|int
index|[]
name|docs
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Sorter
operator|.
name|DocComparator
name|comparator
decl_stmt|;
DECL|field|tmp
specifier|private
specifier|final
name|int
index|[]
name|tmp
decl_stmt|;
DECL|method|DocValueSorter
name|DocValueSorter
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|Sorter
operator|.
name|DocComparator
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|docs
operator|.
name|length
operator|/
literal|64
argument_list|)
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|tmp
operator|=
operator|new
name|int
index|[
name|docs
operator|.
name|length
operator|/
literal|64
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|,
name|docs
index|[
name|j
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|swap
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|tmpDoc
init|=
name|docs
index|[
name|i
index|]
decl_stmt|;
name|docs
index|[
name|i
index|]
operator|=
name|docs
index|[
name|j
index|]
expr_stmt|;
name|docs
index|[
name|j
index|]
operator|=
name|tmpDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|protected
name|void
name|copy
parameter_list|(
name|int
name|src
parameter_list|,
name|int
name|dest
parameter_list|)
block|{
name|docs
index|[
name|dest
index|]
operator|=
name|docs
index|[
name|src
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|save
specifier|protected
name|void
name|save
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|docs
argument_list|,
name|i
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|restore
specifier|protected
name|void
name|restore
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|docs
index|[
name|j
index|]
operator|=
name|tmp
index|[
name|i
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareSaved
specifier|protected
name|int
name|compareSaved
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|tmp
index|[
name|i
index|]
argument_list|,
name|docs
index|[
name|j
index|]
argument_list|)
return|;
block|}
block|}
comment|/** Computes the old-to-new permutation over the given comparator. */
DECL|method|sort
specifier|private
specifier|static
name|Sorter
operator|.
name|DocMap
name|sort
parameter_list|(
specifier|final
name|int
name|maxDoc
parameter_list|,
name|DocComparator
name|comparator
parameter_list|)
block|{
comment|// check if the index is sorted
name|boolean
name|sorted
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|i
operator|-
literal|1
argument_list|,
name|i
argument_list|)
operator|>
literal|0
condition|)
block|{
name|sorted
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|sorted
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// sort doc IDs
specifier|final
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|maxDoc
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
name|DocValueSorter
name|sorter
init|=
operator|new
name|DocValueSorter
argument_list|(
name|docs
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
comment|// It can be common to sort a reader, add docs, sort it again, ... and in
comment|// that case timSort can save a lot of time
name|sorter
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|docs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// docs is now the newToOld mapping
comment|// The reason why we use MonotonicAppendingLongBuffer here is that it
comment|// wastes very little memory if the index is in random order but can save
comment|// a lot of memory if the index is already "almost" sorted
specifier|final
name|PackedLongValues
operator|.
name|Builder
name|newToOldBuilder
init|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|newToOldBuilder
operator|.
name|add
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PackedLongValues
name|newToOld
init|=
name|newToOldBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// invert the docs mapping:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|docs
index|[
operator|(
name|int
operator|)
name|newToOld
operator|.
name|get
argument_list|(
name|i
argument_list|)
index|]
operator|=
name|i
expr_stmt|;
block|}
comment|// docs is now the oldToNew mapping
specifier|final
name|PackedLongValues
operator|.
name|Builder
name|oldToNewBuilder
init|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|oldToNewBuilder
operator|.
name|add
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PackedLongValues
name|oldToNew
init|=
name|oldToNewBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|Sorter
operator|.
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|oldToNew
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|oldToNew
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|newToOld
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|newToOld
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a mapping from the old document ID to its new location in the    * sorted index. Implementations can use the auxiliary    * {@link #sort(int, DocComparator)} to compute the old-to-new permutation    * given a list of documents and their corresponding values.    *<p>    * A return value of<tt>null</tt> is allowed and means that    *<code>reader</code> is already sorted.    *<p>    *<b>NOTE:</b> deleted documents are expected to appear in the mapping as    * well, they will however be marked as deleted in the sorted view.    */
DECL|method|sort
name|DocMap
name|sort
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SortField
name|fields
index|[]
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|int
name|reverseMul
index|[]
init|=
operator|new
name|int
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|LeafFieldComparator
name|comparators
index|[]
init|=
operator|new
name|LeafFieldComparator
index|[
name|fields
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reverseMul
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|i
argument_list|)
operator|.
name|getLeafComparator
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setScorer
argument_list|(
name|FAKESCORER
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocComparator
name|comparator
init|=
operator|new
name|DocComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|docID2
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// TODO: would be better if copy() didnt cause a term lookup in TermOrdVal& co,
comment|// the segments are always the same here...
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|docID1
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|comp
init|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compareBottom
argument_list|(
name|docID2
argument_list|)
decl_stmt|;
if|if
condition|(
name|comp
operator|!=
literal|0
condition|)
block|{
return|return
name|comp
return|;
block|}
block|}
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|docID1
argument_list|,
name|docID2
argument_list|)
return|;
comment|// docid order tiebreak
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
return|return
name|sort
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Returns the identifier of this {@link Sorter}.    *<p>This identifier is similar to {@link Object#hashCode()} and should be    * chosen so that two instances of this class that sort documents likewise    * will have the same identifier. On the contrary, this identifier should be    * different on different {@link Sort sorts}.    */
DECL|method|getID
specifier|public
name|String
name|getID
parameter_list|()
block|{
return|return
name|sort
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getID
argument_list|()
return|;
block|}
DECL|field|FAKESCORER
specifier|static
specifier|final
name|Scorer
name|FAKESCORER
init|=
operator|new
name|Scorer
argument_list|(
literal|null
argument_list|)
block|{
name|float
name|score
decl_stmt|;
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|freq
init|=
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
