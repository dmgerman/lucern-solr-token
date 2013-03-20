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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
begin_comment
comment|/** Base class for implementing {@link CompositeReader}s based on an array  * of sub-readers. The implementing class has to add code for  * correctly refcounting and closing the sub-readers.  *   *<p>User code will most likely use {@link MultiReader} to build a  * composite reader on a set of sub-readers (like several  * {@link DirectoryReader}s).  *   *<p> For efficiency, in this API documents are often referred to via  *<i>document numbers</i>, non-negative integers which each name a unique  * document in the index.  These document numbers are ephemeral -- they may change  * as documents are added to and deleted from an index.  Clients should thus not  * rely on a given document having the same number between sessions.  *   *<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  * IndexReader} instances are completely thread  * safe, meaning multiple threads can call any of its methods,  * concurrently.  If your application requires external  * synchronization, you should<b>not</b> synchronize on the  *<code>IndexReader</code> instance; use your own  * (non-Lucene) objects instead.  * @see MultiReader  * @lucene.internal  */
end_comment
begin_class
DECL|class|BaseCompositeReader
specifier|public
specifier|abstract
class|class
name|BaseCompositeReader
parameter_list|<
name|R
extends|extends
name|IndexReader
parameter_list|>
extends|extends
name|CompositeReader
block|{
DECL|field|subReaders
specifier|private
specifier|final
name|R
index|[]
name|subReaders
decl_stmt|;
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
comment|// 1st docno for each reader
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|hasDeletions
specifier|private
specifier|final
name|boolean
name|hasDeletions
decl_stmt|;
comment|/** List view solely for {@link #getSequentialSubReaders()},    * for effectiveness the array is used internally. */
DECL|field|subReadersList
specifier|private
specifier|final
name|List
argument_list|<
name|R
argument_list|>
name|subReadersList
decl_stmt|;
comment|/**    * Constructs a {@code BaseCompositeReader} on the given subReaders.    * @param subReaders the wrapped sub-readers. This array is returned by    * {@link #getSequentialSubReaders} and used to resolve the correct    * subreader for docID-based methods.<b>Please note:</b> This array is<b>not</b>    * cloned and not protected for modification, the subclass is responsible     * to do this.    */
DECL|method|BaseCompositeReader
specifier|protected
name|BaseCompositeReader
parameter_list|(
name|R
index|[]
name|subReaders
parameter_list|)
block|{
name|this
operator|.
name|subReaders
operator|=
name|subReaders
expr_stmt|;
name|this
operator|.
name|subReadersList
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|subReaders
argument_list|)
argument_list|)
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|subReaders
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
comment|// build starts array
name|int
name|maxDoc
init|=
literal|0
decl_stmt|,
name|numDocs
init|=
literal|0
decl_stmt|;
name|boolean
name|hasDeletions
init|=
literal|false
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|starts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|subReaders
index|[
name|i
index|]
decl_stmt|;
name|maxDoc
operator|+=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|// compute maxDocs
if|if
condition|(
name|maxDoc
operator|<
literal|0
comment|/* overflow */
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Too many documents, composite IndexReaders cannot exceed "
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
throw|;
block|}
name|numDocs
operator|+=
name|r
operator|.
name|numDocs
argument_list|()
expr_stmt|;
comment|// compute numDocs
if|if
condition|(
name|r
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
name|r
operator|.
name|registerParentReader
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|starts
index|[
name|subReaders
operator|.
name|length
index|]
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|hasDeletions
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
specifier|final
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// find subreader num
return|return
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermVectors
argument_list|(
name|docID
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to subreader
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
specifier|final
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
specifier|final
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
specifier|final
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// find subreader num
name|subReaders
index|[
name|i
index|]
operator|.
name|document
argument_list|(
name|docID
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
comment|// dispatch to subreader
block|}
annotation|@
name|Override
DECL|method|hasDeletions
specifier|public
specifier|final
name|boolean
name|hasDeletions
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|hasDeletions
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
comment|// sum freqs in subreaders
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|total
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
specifier|final
name|long
name|totalTermFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|long
name|total
init|=
literal|0
decl_stmt|;
comment|// sum freqs in subreaders
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|sub
init|=
name|subReaders
index|[
name|i
index|]
operator|.
name|totalTermFreq
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|total
operator|+=
name|sub
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
specifier|final
name|long
name|getSumDocFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|long
name|total
init|=
literal|0
decl_stmt|;
comment|// sum doc freqs in subreaders
for|for
control|(
name|R
name|reader
range|:
name|subReaders
control|)
block|{
name|long
name|sub
init|=
name|reader
operator|.
name|getSumDocFreq
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// if any of the subs doesn't support it, return -1
block|}
name|total
operator|+=
name|sub
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
specifier|final
name|int
name|getDocCount
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
comment|// sum doc counts in subreaders
for|for
control|(
name|R
name|reader
range|:
name|subReaders
control|)
block|{
name|int
name|sub
init|=
name|reader
operator|.
name|getDocCount
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// if any of the subs doesn't support it, return -1
block|}
name|total
operator|+=
name|sub
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
specifier|final
name|long
name|getSumTotalTermFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|long
name|total
init|=
literal|0
decl_stmt|;
comment|// sum doc total term freqs in subreaders
for|for
control|(
name|R
name|reader
range|:
name|subReaders
control|)
block|{
name|long
name|sub
init|=
name|reader
operator|.
name|getSumTotalTermFreq
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// if any of the subs doesn't support it, return -1
block|}
name|total
operator|+=
name|sub
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
comment|/** Helper method for subclasses to get the corresponding reader for a doc ID */
DECL|method|readerIndex
specifier|protected
specifier|final
name|int
name|readerIndex
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docID must be>= 0 and< maxDoc="
operator|+
name|maxDoc
operator|+
literal|" (got docID="
operator|+
name|docID
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|this
operator|.
name|starts
argument_list|)
return|;
block|}
comment|/** Helper method for subclasses to get the docBase of the given sub-reader index. */
DECL|method|readerBase
specifier|protected
specifier|final
name|int
name|readerBase
parameter_list|(
name|int
name|readerIndex
parameter_list|)
block|{
if|if
condition|(
name|readerIndex
operator|<
literal|0
operator|||
name|readerIndex
operator|>=
name|subReaders
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"readerIndex must be>= 0 and< getSequentialSubReaders().size()"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|starts
index|[
name|readerIndex
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getSequentialSubReaders
specifier|protected
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|R
argument_list|>
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
name|subReadersList
return|;
block|}
block|}
end_class
end_unit
