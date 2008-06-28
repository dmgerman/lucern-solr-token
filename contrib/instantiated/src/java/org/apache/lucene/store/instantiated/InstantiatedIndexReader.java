begin_unit
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
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
name|HashSet
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
name|LinkedList
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
name|Set
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|FieldSelector
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
name|CorruptIndexException
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
name|IndexReader
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
name|IndexWriter
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
name|Term
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
name|TermDocs
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
name|TermEnum
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
name|TermFreqVector
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
name|TermPositions
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
name|TermVectorMapper
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
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|/**  * An InstantiatedIndexReader is not a snapshot in time, it is completely in  * sync with the latest commit to the store!  *   * Consider using InstantiatedIndex as if it was immutable.  */
end_comment
begin_class
DECL|class|InstantiatedIndexReader
specifier|public
class|class
name|InstantiatedIndexReader
extends|extends
name|IndexReader
block|{
DECL|field|index
specifier|private
specifier|final
name|InstantiatedIndex
name|index
decl_stmt|;
DECL|method|InstantiatedIndexReader
specifier|public
name|InstantiatedIndexReader
parameter_list|(
name|InstantiatedIndex
name|index
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|/**    * @return always true.    */
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * An InstantiatedIndexReader is not a snapshot in time, it is completely in    * sync with the latest commit to the store!    *     * @return output from {@link InstantiatedIndex#getVersion()} in associated instantiated index.    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|index
operator|.
name|getVersion
argument_list|()
return|;
block|}
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * An InstantiatedIndexReader is always current!    *     * Check whether this IndexReader is still using the current (i.e., most    * recently committed) version of the index. If a writer has committed any    * changes to the index since this reader was opened, this will return    *<code>false</code>, in which case you must open a new IndexReader in    * order to see the changes. See the description of the<a    * href="IndexWriter.html#autoCommit"><code>autoCommit</code></a> flag    * which controls when the {@link IndexWriter} actually commits changes to the    * index.    *     * @return always true    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @throws UnsupportedOperationException unless overridden in subclass    */
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
DECL|method|getIndex
specifier|public
name|InstantiatedIndex
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|field|deletedDocuments
specifier|private
name|Set
argument_list|<
name|InstantiatedDocument
argument_list|>
name|deletedDocuments
init|=
operator|new
name|HashSet
argument_list|<
name|InstantiatedDocument
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deletedDocumentNumbers
specifier|private
name|Set
argument_list|<
name|Integer
argument_list|>
name|deletedDocumentNumbers
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|updatedNormsByFieldNameAndDocumentNumber
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|NormUpdate
argument_list|>
argument_list|>
name|updatedNormsByFieldNameAndDocumentNumber
init|=
literal|null
decl_stmt|;
DECL|class|NormUpdate
specifier|private
class|class
name|NormUpdate
block|{
DECL|field|doc
specifier|private
name|int
name|doc
decl_stmt|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|method|NormUpdate
specifier|public
name|NormUpdate
parameter_list|(
name|int
name|doc
parameter_list|,
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
operator|.
name|length
operator|-
name|index
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|size
argument_list|()
operator|-
name|deletedDocuments
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
operator|.
name|length
return|;
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|getIndex
argument_list|()
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|contains
argument_list|(
name|n
argument_list|)
operator|||
name|deletedDocumentNumbers
operator|.
name|contains
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|getIndex
argument_list|()
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|deletedDocumentNumbers
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|getIndex
argument_list|()
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|contains
argument_list|(
name|docNum
argument_list|)
condition|)
block|{
if|if
condition|(
name|deletedDocumentNumbers
operator|.
name|add
argument_list|(
name|docNum
argument_list|)
condition|)
block|{
name|deletedDocuments
operator|.
name|add
argument_list|(
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNum
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|deletedDocumentNumbers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deletedDocuments
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|()
throws|throws
name|IOException
block|{
comment|// todo: read/write lock
name|boolean
name|updated
init|=
literal|false
decl_stmt|;
comment|// 1. update norms
if|if
condition|(
name|updatedNormsByFieldNameAndDocumentNumber
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|NormUpdate
argument_list|>
argument_list|>
name|e
range|:
name|updatedNormsByFieldNameAndDocumentNumber
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|byte
index|[]
name|norms
init|=
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|NormUpdate
name|normUpdate
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|norms
index|[
name|normUpdate
operator|.
name|doc
index|]
operator|=
name|normUpdate
operator|.
name|value
expr_stmt|;
block|}
block|}
name|updatedNormsByFieldNameAndDocumentNumber
operator|=
literal|null
expr_stmt|;
name|updated
operator|=
literal|true
expr_stmt|;
block|}
comment|// 2. remove deleted documents
if|if
condition|(
name|deletedDocumentNumbers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Integer
name|doc
range|:
name|deletedDocumentNumbers
control|)
block|{
name|getIndex
argument_list|()
operator|.
name|getDeletedDocuments
argument_list|()
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|deletedDocumentNumbers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deletedDocuments
operator|.
name|clear
argument_list|()
expr_stmt|;
name|updated
operator|=
literal|true
expr_stmt|;
block|}
comment|// todo unlock read/writelock
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|// ignored
comment|// todo perhaps release all associated instances?
block|}
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|FieldOption
name|fieldOption
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldSetting
name|fi
range|:
name|index
operator|.
name|getFieldSettings
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|fi
operator|.
name|indexed
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storePayloads
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|STORES_PAYLOADS
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|indexed
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|indexed
operator|&&
name|fi
operator|.
name|storeTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_NO_TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storeTermVector
operator|==
literal|true
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|==
literal|false
operator|&&
name|fi
operator|.
name|storeOffsetWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|indexed
operator|&&
name|fi
operator|.
name|storeTermVector
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_WITH_TERMVECTOR
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storePositionWithTermVector
operator|&&
name|fi
operator|.
name|storeOffsetWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|storeOffsetWithTermVector
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|==
literal|false
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_OFFSET
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|fi
operator|.
name|storeOffsetWithTermVector
operator|&&
name|fi
operator|.
name|storePositionWithTermVector
operator|)
operator|&&
name|fieldOption
operator|==
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION_OFFSET
condition|)
block|{
name|fieldSet
operator|.
name|add
argument_list|(
name|fi
operator|.
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fieldSet
return|;
block|}
comment|/**    * Return the {@link org.apache.lucene.document.Document} at the<code>n</code><sup>th</sup>    * position.<p>    *<b>Warning!</b>    * The resulting document is the actual stored document instance    * and not a deserialized clone as retuned by an IndexReader    * over a {@link org.apache.lucene.store.Directory}.    * I.e., if you need to touch the document, clone it first!    *<p>    * This can also be seen as a feature for live canges of stored values,    * but be carful! Adding a field with an name unknown to the index    * or to a field with previously no stored values will make    * {@link org.apache.lucene.store.instantiated.InstantiatedIndexReader#getFieldNames(org.apache.lucene.index.IndexReader.FieldOption)}    * out of sync, causing problems for instance when merging the    * instantiated index to another index.<p>    * This implementation ignores the field selector! All stored fields are always returned!    *<p>    *    * @param n document number    * @param fieldSelector ignored    * @return The stored fields of the {@link org.apache.lucene.document.Document} at the nth position    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    *     * @see org.apache.lucene.document.Fieldable    * @see org.apache.lucene.document.FieldSelector    * @see org.apache.lucene.document.SetBasedFieldSelector    * @see org.apache.lucene.document.LoadFirstFieldSelector    */
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|document
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/**    * Returns the stored fields of the<code>n</code><sup>th</sup>    *<code>Document</code> in this index.    *<p>    *<b>Warning!</b>    * The resulting document is the actual stored document instance    * and not a deserialized clone as retuned by an IndexReader    * over a {@link org.apache.lucene.store.Directory}.    * I.e., if you need to touch the document, clone it first!    *<p>    * This can also be seen as a feature for live canges of stored values,    * but be carful! Adding a field with an name unknown to the index    * or to a field with previously no stored values will make    * {@link org.apache.lucene.store.instantiated.InstantiatedIndexReader#getFieldNames(org.apache.lucene.index.IndexReader.FieldOption)}    * out of sync, causing problems for instance when merging the    * instantiated index to another index.    *    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|isDeleted
argument_list|(
name|n
argument_list|)
condition|?
literal|null
else|:
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|n
index|]
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/**    * never ever touch these values. it is the true values, unless norms have    * been touched.    */
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|norms
init|=
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatedNormsByFieldNameAndDocumentNumber
operator|!=
literal|null
condition|)
block|{
name|norms
operator|=
name|norms
operator|.
name|clone
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|NormUpdate
argument_list|>
name|updated
init|=
name|updatedNormsByFieldNameAndDocumentNumber
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|updated
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NormUpdate
name|normUpdate
range|:
name|updated
control|)
block|{
name|norms
index|[
name|normUpdate
operator|.
name|doc
index|]
operator|=
name|normUpdate
operator|.
name|value
expr_stmt|;
block|}
block|}
block|}
return|return
name|norms
return|;
block|}
DECL|method|norms
specifier|public
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|norms
init|=
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|norms
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|,
name|norms
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|updatedNormsByFieldNameAndDocumentNumber
operator|==
literal|null
condition|)
block|{
name|updatedNormsByFieldNameAndDocumentNumber
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|NormUpdate
argument_list|>
argument_list|>
argument_list|(
name|getIndex
argument_list|()
operator|.
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NormUpdate
argument_list|>
name|list
init|=
name|updatedNormsByFieldNameAndDocumentNumber
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|NormUpdate
argument_list|>
argument_list|()
expr_stmt|;
name|updatedNormsByFieldNameAndDocumentNumber
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|NormUpdate
argument_list|(
name|doc
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedTerm
name|term
init|=
name|getIndex
argument_list|()
operator|.
name|findTerm
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
return|;
block|}
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InstantiatedTermEnum
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedTerm
name|it
init|=
name|getIndex
argument_list|()
operator|.
name|findTerm
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|InstantiatedTermEnum
argument_list|(
name|this
argument_list|,
name|it
operator|.
name|getTermIndex
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|startPos
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|index
operator|.
name|getOrderedTerms
argument_list|()
argument_list|,
name|t
argument_list|,
name|InstantiatedTerm
operator|.
name|termComparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|startPos
operator|<
literal|0
condition|)
block|{
name|startPos
operator|=
operator|-
literal|1
operator|-
name|startPos
expr_stmt|;
block|}
return|return
operator|new
name|InstantiatedTermEnum
argument_list|(
name|this
argument_list|,
name|startPos
argument_list|)
return|;
block|}
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InstantiatedTermDocs
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InstantiatedTermPositions
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|docNumber
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TermFreqVector
index|[]
name|ret
init|=
operator|new
name|TermFreqVector
index|[
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
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
name|ret
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
operator|new
name|InstantiatedTermPositionVector
argument_list|(
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|==
literal|null
operator|||
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|InstantiatedTermPositionVector
argument_list|(
name|doc
argument_list|,
name|field
argument_list|)
return|;
block|}
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|!=
literal|null
operator|&&
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
name|tv
init|=
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|mapper
operator|.
name|setExpectations
argument_list|(
name|field
argument_list|,
name|tv
operator|.
name|size
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|InstantiatedTermDocumentInformation
name|tdi
range|:
name|tv
control|)
block|{
name|mapper
operator|.
name|map
argument_list|(
name|tdi
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
operator|.
name|length
argument_list|,
name|tdi
operator|.
name|getTermOffsets
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|InstantiatedDocument
name|doc
init|=
name|getIndex
argument_list|()
operator|.
name|getDocumentsByNumber
argument_list|()
index|[
name|docNumber
index|]
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|>
name|e
range|:
name|doc
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|mapper
operator|.
name|setExpectations
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|InstantiatedTermDocumentInformation
name|tdi
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|mapper
operator|.
name|map
argument_list|(
name|tdi
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
operator|.
name|length
argument_list|,
name|tdi
operator|.
name|getTermOffsets
argument_list|()
argument_list|,
name|tdi
operator|.
name|getTermPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
