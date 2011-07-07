begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|Query
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
name|RamUsageEstimator
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
name|index
operator|.
name|BufferedDeletesStream
operator|.
name|QueryAndLimit
import|;
end_import
begin_comment
comment|/* Holds buffered deletes, by docID, term or query for a  * single segment. This is used to hold buffered pending  * deletes against the to-be-flushed segment.  Once the  * deletes are pushed (on flush in DocumentsWriter), these  * deletes are converted to a FrozenDeletes instance. */
end_comment
begin_comment
comment|// NOTE: we are sync'd by BufferedDeletes, ie, all access to
end_comment
begin_comment
comment|// instances of this class is via sync'd methods on
end_comment
begin_comment
comment|// BufferedDeletes
end_comment
begin_class
DECL|class|BufferedDeletes
class|class
name|BufferedDeletes
block|{
comment|/* Rough logic: HashMap has an array[Entry] w/ varying      load factor (say 2 * POINTER).  Entry is object w/ Term      key, Integer val, int hash, Entry next      (OBJ_HEADER + 3*POINTER + INT).  Term is object w/      String field and String text (OBJ_HEADER + 2*POINTER).      Term's field is String (OBJ_HEADER + 4*INT + POINTER +      OBJ_HEADER + string.length*CHAR).      Term's text is String (OBJ_HEADER + 4*INT + POINTER +      OBJ_HEADER + string.length*CHAR).  Integer is      OBJ_HEADER + INT. */
DECL|field|BYTES_PER_DEL_TERM
specifier|final
specifier|static
name|int
name|BYTES_PER_DEL_TERM
init|=
literal|9
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
literal|7
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
literal|10
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
comment|/* Rough logic: del docIDs are List<Integer>.  Say list      allocates ~2X size (2*POINTER).  Integer is OBJ_HEADER      + int */
DECL|field|BYTES_PER_DEL_DOCID
specifier|final
specifier|static
name|int
name|BYTES_PER_DEL_DOCID
init|=
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
comment|/* Rough logic: HashMap has an array[Entry] w/ varying      load factor (say 2 * POINTER).  Entry is object w/      Query key, Integer val, int hash, Entry next      (OBJ_HEADER + 3*POINTER + INT).  Query we often      undercount (say 24 bytes).  Integer is OBJ_HEADER + INT. */
DECL|field|BYTES_PER_DEL_QUERY
specifier|final
specifier|static
name|int
name|BYTES_PER_DEL_QUERY
init|=
literal|5
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|+
literal|24
decl_stmt|;
DECL|field|numTermDeletes
specifier|final
name|AtomicInteger
name|numTermDeletes
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|terms
specifier|final
name|Map
argument_list|<
name|Term
argument_list|,
name|Integer
argument_list|>
name|terms
decl_stmt|;
DECL|field|queries
specifier|final
name|Map
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|queries
init|=
operator|new
name|HashMap
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|docIDs
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|docIDs
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|MAX_INT
specifier|public
specifier|static
specifier|final
name|Integer
name|MAX_INT
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
DECL|field|bytesUsed
specifier|final
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|field|VERBOSE_DELETES
specifier|private
specifier|final
specifier|static
name|boolean
name|VERBOSE_DELETES
init|=
literal|false
decl_stmt|;
DECL|field|gen
name|long
name|gen
decl_stmt|;
DECL|method|BufferedDeletes
specifier|public
name|BufferedDeletes
parameter_list|(
name|boolean
name|sortTerms
parameter_list|)
block|{
name|this
argument_list|(
name|sortTerms
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|BufferedDeletes
name|BufferedDeletes
parameter_list|(
name|boolean
name|sortTerms
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
block|{
assert|assert
name|bytesUsed
operator|!=
literal|null
assert|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
if|if
condition|(
name|sortTerms
condition|)
block|{
name|terms
operator|=
operator|new
name|TreeMap
argument_list|<
name|Term
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|terms
operator|=
operator|new
name|HashMap
argument_list|<
name|Term
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|VERBOSE_DELETES
condition|)
block|{
return|return
literal|"gen="
operator|+
name|gen
operator|+
literal|" numTerms="
operator|+
name|numTermDeletes
operator|+
literal|", terms="
operator|+
name|terms
operator|+
literal|", queries="
operator|+
name|queries
operator|+
literal|", docIDs="
operator|+
name|docIDs
operator|+
literal|", bytesUsed="
operator|+
name|bytesUsed
return|;
block|}
else|else
block|{
name|String
name|s
init|=
literal|"gen="
operator|+
name|gen
decl_stmt|;
if|if
condition|(
name|numTermDeletes
operator|.
name|get
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" "
operator|+
name|numTermDeletes
operator|.
name|get
argument_list|()
operator|+
literal|" deleted terms (unique count="
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
if|if
condition|(
name|queries
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" "
operator|+
name|queries
operator|.
name|size
argument_list|()
operator|+
literal|" deleted queries"
expr_stmt|;
block|}
if|if
condition|(
name|docIDs
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" "
operator|+
name|docIDs
operator|.
name|size
argument_list|()
operator|+
literal|" deleted docIDs"
expr_stmt|;
block|}
if|if
condition|(
name|bytesUsed
operator|.
name|get
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" bytesUsed="
operator|+
name|bytesUsed
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|BufferedDeletes
name|in
parameter_list|)
block|{
name|numTermDeletes
operator|.
name|addAndGet
argument_list|(
name|in
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Term
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|in
operator|.
name|terms
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Term
name|term
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|terms
operator|.
name|containsKey
argument_list|(
name|term
argument_list|)
condition|)
block|{
comment|// only incr bytesUsed if this term wasn't already buffered:
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_TERM
argument_list|)
expr_stmt|;
block|}
name|terms
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|in
operator|.
name|queries
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Query
name|query
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|queries
operator|.
name|containsKey
argument_list|(
name|query
argument_list|)
condition|)
block|{
comment|// only incr bytesUsed if this query wasn't already buffered:
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_QUERY
argument_list|)
expr_stmt|;
block|}
name|queries
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
comment|// docIDs never move across segments and the docIDs
comment|// should already be cleared
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|FrozenBufferedDeletes
name|in
parameter_list|)
block|{
name|numTermDeletes
operator|.
name|addAndGet
argument_list|(
name|in
operator|.
name|numTermDeletes
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|in
operator|.
name|terms
control|)
block|{
if|if
condition|(
operator|!
name|terms
operator|.
name|containsKey
argument_list|(
name|term
argument_list|)
condition|)
block|{
comment|// only incr bytesUsed if this term wasn't already buffered:
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_TERM
argument_list|)
expr_stmt|;
block|}
name|terms
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|queryIdx
init|=
literal|0
init|;
name|queryIdx
operator|<
name|in
operator|.
name|queries
operator|.
name|length
condition|;
name|queryIdx
operator|++
control|)
block|{
specifier|final
name|Query
name|query
init|=
name|in
operator|.
name|queries
index|[
name|queryIdx
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|queries
operator|.
name|containsKey
argument_list|(
name|query
argument_list|)
condition|)
block|{
comment|// only incr bytesUsed if this query wasn't already buffered:
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_QUERY
argument_list|)
expr_stmt|;
block|}
name|queries
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addQuery
specifier|public
name|void
name|addQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
name|Integer
name|current
init|=
name|queries
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|docIDUpto
argument_list|)
decl_stmt|;
comment|// increment bytes used only if the query wasn't added so far.
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_QUERY
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDocID
specifier|public
name|void
name|addDocID
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|docIDs
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_DOCID
argument_list|)
expr_stmt|;
block|}
DECL|method|addTerm
specifier|public
name|void
name|addTerm
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|docIDUpto
parameter_list|)
block|{
name|Integer
name|current
init|=
name|terms
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
operator|&&
name|docIDUpto
operator|<
name|current
condition|)
block|{
comment|// Only record the new number if it's greater than the
comment|// current one.  This is important because if multiple
comment|// threads are replacing the same doc at nearly the
comment|// same time, it's possible that one thread that got a
comment|// higher docID is scheduled before the other
comment|// threads.  If we blindly replace than we can
comment|// incorrectly get both docs indexed.
return|return;
block|}
name|terms
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|docIDUpto
argument_list|)
argument_list|)
expr_stmt|;
name|numTermDeletes
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|BYTES_PER_DEL_TERM
operator|+
name|term
operator|.
name|bytes
operator|.
name|length
operator|+
operator|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
operator|*
name|term
operator|.
name|field
argument_list|()
operator|.
name|length
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|termsIterable
specifier|public
name|Iterable
argument_list|<
name|Term
argument_list|>
name|termsIterable
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Term
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|terms
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|method|queriesIterable
specifier|public
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
name|queriesIterable
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QueryAndLimit
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|QueryAndLimit
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|iter
init|=
name|queries
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryAndLimit
name|next
parameter_list|()
block|{
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|ent
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|QueryAndLimit
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|terms
operator|.
name|clear
argument_list|()
expr_stmt|;
name|queries
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docIDs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numTermDeletes
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|clearDocIDs
name|void
name|clearDocIDs
parameter_list|()
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|docIDs
operator|.
name|size
argument_list|()
operator|*
name|BYTES_PER_DEL_DOCID
argument_list|)
expr_stmt|;
name|docIDs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|any
name|boolean
name|any
parameter_list|()
block|{
return|return
name|terms
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|docIDs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|queries
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
end_class
end_unit
