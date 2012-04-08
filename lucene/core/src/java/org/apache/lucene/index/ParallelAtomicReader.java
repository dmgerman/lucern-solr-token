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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
import|;
end_import
begin_comment
comment|/** An {@link AtomicReader} which reads multiple, parallel indexes.  Each index  * added must have the same number of documents, but typically each contains  * different fields. Deletions are taken from the first reader.  * Each document contains the union of the fields of all documents  * with the same document number.  When searching, matches for a  * query term are from the first index added that has the field.  *  *<p>This is useful, e.g., with collections that have large fields which  * change rarely and small fields that change more frequently.  The smaller  * fields may be re-indexed in a new index and both indexes may be searched  * together.  *   *<p><strong>Warning:</strong> It is up to you to make sure all indexes  * are created and modified the same way. For example, if you add  * documents to one index, you need to add the same documents in the  * same order to the other indexes.<em>Failure to do so will result in  * undefined behavior</em>.  */
end_comment
begin_class
DECL|class|ParallelAtomicReader
specifier|public
specifier|final
class|class
name|ParallelAtomicReader
extends|extends
name|AtomicReader
block|{
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|ParallelFields
name|fields
init|=
operator|new
name|ParallelFields
argument_list|()
decl_stmt|;
DECL|field|parallelReaders
DECL|field|storedFieldsReaders
specifier|private
specifier|final
name|AtomicReader
index|[]
name|parallelReaders
decl_stmt|,
name|storedFieldsReaders
decl_stmt|;
DECL|field|completeReaderSet
specifier|private
specifier|final
name|Set
argument_list|<
name|AtomicReader
argument_list|>
name|completeReaderSet
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|AtomicReader
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|closeSubReaders
specifier|private
specifier|final
name|boolean
name|closeSubReaders
decl_stmt|;
DECL|field|maxDoc
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|,
name|numDocs
decl_stmt|;
DECL|field|hasDeletions
specifier|private
specifier|final
name|boolean
name|hasDeletions
decl_stmt|;
DECL|field|fieldToReader
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|AtomicReader
argument_list|>
name|fieldToReader
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|AtomicReader
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|tvFieldToReader
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|String
argument_list|,
name|AtomicReader
argument_list|>
name|tvFieldToReader
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|AtomicReader
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Create a ParallelAtomicReader based on the provided    *  readers; auto-closes the given readers on {@link #close()}. */
DECL|method|ParallelAtomicReader
specifier|public
name|ParallelAtomicReader
parameter_list|(
name|AtomicReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
comment|/** Create a ParallelAtomicReader based on the provided    *  readers. */
DECL|method|ParallelAtomicReader
specifier|public
name|ParallelAtomicReader
parameter_list|(
name|boolean
name|closeSubReaders
parameter_list|,
name|AtomicReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|closeSubReaders
argument_list|,
name|readers
argument_list|,
name|readers
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: create a ParallelAtomicReader based on the provided    *  readers and storedFieldReaders; when a document is    *  loaded, only storedFieldsReaders will be used. */
DECL|method|ParallelAtomicReader
specifier|public
name|ParallelAtomicReader
parameter_list|(
name|boolean
name|closeSubReaders
parameter_list|,
name|AtomicReader
index|[]
name|readers
parameter_list|,
name|AtomicReader
index|[]
name|storedFieldsReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|closeSubReaders
operator|=
name|closeSubReaders
expr_stmt|;
if|if
condition|(
name|readers
operator|.
name|length
operator|==
literal|0
operator|&&
name|storedFieldsReaders
operator|.
name|length
operator|>
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"There must be at least one main reader if storedFieldsReaders are used."
argument_list|)
throw|;
name|this
operator|.
name|parallelReaders
operator|=
name|readers
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|storedFieldsReaders
operator|=
name|storedFieldsReaders
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|parallelReaders
operator|.
name|length
operator|>
literal|0
condition|)
block|{
specifier|final
name|AtomicReader
name|first
init|=
name|parallelReaders
index|[
literal|0
index|]
decl_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|first
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|first
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|first
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|maxDoc
operator|=
name|this
operator|.
name|numDocs
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
literal|false
expr_stmt|;
block|}
name|Collections
operator|.
name|addAll
argument_list|(
name|completeReaderSet
argument_list|,
name|this
operator|.
name|parallelReaders
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|completeReaderSet
argument_list|,
name|this
operator|.
name|storedFieldsReaders
argument_list|)
expr_stmt|;
comment|// check compatibility:
for|for
control|(
name|AtomicReader
name|reader
range|:
name|completeReaderSet
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|!=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All readers must have same maxDoc: "
operator|+
name|maxDoc
operator|+
literal|"!="
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// build FieldInfos and fieldToReader map:
for|for
control|(
specifier|final
name|AtomicReader
name|reader
range|:
name|this
operator|.
name|parallelReaders
control|)
block|{
specifier|final
name|FieldInfos
name|readerFieldInfos
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|readerFieldInfos
control|)
block|{
comment|// NOTE: first reader having a given field "wins":
if|if
condition|(
operator|!
name|fieldToReader
operator|.
name|containsKey
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|)
block|{
name|fieldInfos
operator|.
name|add
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|fieldToReader
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|storeTermVector
condition|)
block|{
name|tvFieldToReader
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// build InvertedFields instance
for|for
control|(
specifier|final
name|AtomicReader
name|reader
range|:
name|this
operator|.
name|parallelReaders
control|)
block|{
specifier|final
name|InvertedFields
name|readerFields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerFields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|FieldsEnum
name|it
init|=
name|readerFields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|name
decl_stmt|;
while|while
condition|(
operator|(
name|name
operator|=
name|it
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// only add if the reader responsible for that field name is the current:
if|if
condition|(
name|fieldToReader
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|==
name|reader
condition|)
block|{
name|this
operator|.
name|fields
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|it
operator|.
name|terms
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// do this finally so any Exceptions occurred before don't affect refcounts:
for|for
control|(
name|AtomicReader
name|reader
range|:
name|completeReaderSet
control|)
block|{
if|if
condition|(
operator|!
name|closeSubReaders
condition|)
block|{
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
name|reader
operator|.
name|registerParentReader
argument_list|(
name|this
argument_list|)
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
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ParallelAtomicReader("
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|AtomicReader
argument_list|>
name|iter
init|=
name|completeReaderSet
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|ParallelFieldsEnum
specifier|private
specifier|final
class|class
name|ParallelFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|currentField
specifier|private
name|String
name|currentField
decl_stmt|;
DECL|field|keys
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|keys
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|ParallelFields
name|fields
decl_stmt|;
DECL|method|ParallelFieldsEnum
name|ParallelFieldsEnum
parameter_list|(
name|ParallelFields
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|keys
operator|=
name|fields
operator|.
name|fields
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentField
operator|=
name|keys
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentField
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|currentField
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|terms
argument_list|(
name|currentField
argument_list|)
return|;
block|}
block|}
comment|// Single instance of this, per ParallelReader instance
DECL|class|ParallelFields
specifier|private
specifier|final
class|class
name|ParallelFields
extends|extends
name|InvertedFields
block|{
DECL|field|fields
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ParallelFields
name|ParallelFields
parameter_list|()
block|{     }
DECL|method|addField
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ParallelFieldsEnum
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueFieldCount
specifier|public
name|int
name|getUniqueFieldCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|hasDeletions
condition|?
name|parallelReaders
index|[
literal|0
index|]
operator|.
name|getLiveDocs
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|InvertedFields
name|fields
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
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
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|hasDeletions
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
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
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|AtomicReader
name|reader
range|:
name|storedFieldsReaders
control|)
block|{
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|InvertedFields
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
name|ParallelFields
name|fields
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AtomicReader
argument_list|>
name|ent
range|:
name|tvFieldToReader
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Terms
name|vector
init|=
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|getTermVector
argument_list|(
name|docID
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|ParallelFields
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|vector
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
specifier|synchronized
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|completeReaderSet
control|)
block|{
try|try
block|{
if|if
condition|(
name|closeSubReaders
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
name|ioe
operator|=
name|e
expr_stmt|;
block|}
block|}
comment|// throw the first exception
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
throw|throw
name|ioe
throw|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
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
name|AtomicReader
name|reader
init|=
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|null
else|:
name|reader
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normValues
specifier|public
name|DocValues
name|normValues
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
name|AtomicReader
name|reader
init|=
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|null
else|:
name|reader
operator|.
name|normValues
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
end_class
end_unit
