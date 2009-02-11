begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
name|*
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
name|LockObtainFailedException
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
begin_comment
comment|/** Solr wrapper for IndexReader that contains extra context.  * This is currently experimental, for internal use only, and subject to change.  */
end_comment
begin_class
DECL|class|SolrIndexReader
specifier|public
class|class
name|SolrIndexReader
extends|extends
name|FilterIndexReader
block|{
DECL|field|subReaders
specifier|private
specifier|final
name|SolrIndexReader
index|[]
name|subReaders
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|SolrIndexReader
name|parent
decl_stmt|;
DECL|field|base
specifier|private
specifier|final
name|int
name|base
decl_stmt|;
comment|// docid offset of this reader within parent
comment|// top level searcher for this reader tree
comment|// a bit if a hack currently... searcher needs to set
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
comment|// Shared info about the wrapped reader.
DECL|field|info
specifier|private
name|SolrReaderInfo
name|info
decl_stmt|;
comment|/** Recursively wrap an IndexReader in SolrIndexReader instances.    * @param in  the reader to wrap    * @param parent the parent, if any (null if none)    * @param base the docid offset in the parent (0 if top level)    */
DECL|method|SolrIndexReader
specifier|public
name|SolrIndexReader
parameter_list|(
name|IndexReader
name|in
parameter_list|,
name|SolrIndexReader
name|parent
parameter_list|,
name|int
name|base
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
assert|assert
operator|(
operator|!
operator|(
name|in
operator|instanceof
name|SolrIndexReader
operator|)
operator|)
assert|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|IndexReader
name|subs
index|[]
init|=
name|in
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
name|subReaders
operator|=
name|subs
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|SolrIndexReader
index|[
name|subs
operator|.
name|length
index|]
expr_stmt|;
if|if
condition|(
name|subs
operator|!=
literal|null
condition|)
block|{
name|int
name|b
init|=
literal|0
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
name|subReaders
index|[
name|i
index|]
operator|=
operator|new
name|SolrIndexReader
argument_list|(
name|subs
index|[
name|i
index|]
argument_list|,
name|this
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|b
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|shortName
specifier|static
name|String
name|shortName
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"@"
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|o
operator|.
name|hashCode
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"SolrIndexReader{this="
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|this
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",r="
argument_list|)
operator|.
name|append
argument_list|(
name|shortName
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",segments="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|subReaders
operator|==
literal|null
condition|?
literal|1
else|:
name|subReaders
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",parent="
argument_list|)
operator|.
name|append
argument_list|(
name|parent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setSearcher
specifier|static
name|void
name|setSearcher
parameter_list|(
name|SolrIndexReader
name|sr
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|sr
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|SolrIndexReader
index|[]
name|readers
init|=
name|sr
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|readers
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|SolrIndexReader
name|r
range|:
name|readers
control|)
block|{
name|setSearcher
argument_list|(
name|r
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildInfoMap
specifier|private
specifier|static
name|void
name|buildInfoMap
parameter_list|(
name|SolrIndexReader
name|other
parameter_list|,
name|HashMap
argument_list|<
name|IndexReader
argument_list|,
name|SolrReaderInfo
argument_list|>
name|map
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
return|return;
name|map
operator|.
name|put
argument_list|(
name|other
operator|.
name|getWrappedReader
argument_list|()
argument_list|,
name|other
operator|.
name|info
argument_list|)
expr_stmt|;
name|SolrIndexReader
index|[]
name|readers
init|=
name|other
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|readers
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|SolrIndexReader
name|r
range|:
name|readers
control|)
block|{
name|buildInfoMap
argument_list|(
name|r
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setInfo
specifier|private
specifier|static
name|void
name|setInfo
parameter_list|(
name|SolrIndexReader
name|target
parameter_list|,
name|HashMap
argument_list|<
name|IndexReader
argument_list|,
name|SolrReaderInfo
argument_list|>
name|map
parameter_list|)
block|{
name|SolrReaderInfo
name|info
init|=
name|map
operator|.
name|get
argument_list|(
name|target
operator|.
name|getWrappedReader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
name|info
operator|=
operator|new
name|SolrReaderInfo
argument_list|(
name|target
operator|.
name|getWrappedReader
argument_list|()
argument_list|)
expr_stmt|;
name|target
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|SolrIndexReader
index|[]
name|readers
init|=
name|target
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|readers
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|SolrIndexReader
name|r
range|:
name|readers
control|)
block|{
name|setInfo
argument_list|(
name|r
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Copies SolrReaderInfo instances from the source to this SolrIndexReader */
DECL|method|associateInfo
specifier|public
name|void
name|associateInfo
parameter_list|(
name|SolrIndexReader
name|source
parameter_list|)
block|{
comment|// seemed safer to not mess with reopen() but simply set
comment|// one set of caches from another reader tree.
name|HashMap
argument_list|<
name|IndexReader
argument_list|,
name|SolrReaderInfo
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|IndexReader
argument_list|,
name|SolrReaderInfo
argument_list|>
argument_list|()
decl_stmt|;
name|buildInfoMap
argument_list|(
name|source
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|setInfo
argument_list|(
name|this
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|getWrappedReader
specifier|public
name|IndexReader
name|getWrappedReader
parameter_list|()
block|{
return|return
name|in
return|;
block|}
comment|/** returns the parent reader, or null of none */
DECL|method|getParent
specifier|public
name|SolrIndexReader
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/** returns the docid offset within the parent reader */
DECL|method|getBase
specifier|public
name|int
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
annotation|@
name|Override
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
return|return
name|in
operator|.
name|directory
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
name|in
operator|.
name|getTermFreqVectors
argument_list|(
name|docNumber
argument_list|)
return|;
block|}
annotation|@
name|Override
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
return|return
name|in
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|numDocs
argument_list|()
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
return|return
name|in
operator|.
name|maxDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|document
argument_list|(
name|n
argument_list|,
name|fieldSelector
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|isDeleted
argument_list|(
name|n
argument_list|)
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
return|return
name|in
operator|.
name|hasDeletions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|in
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|norms
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|norms
specifier|public
name|void
name|norms
parameter_list|(
name|String
name|f
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
name|in
operator|.
name|norms
argument_list|(
name|f
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|d
parameter_list|,
name|String
name|f
parameter_list|,
name|byte
name|b
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|in
operator|.
name|setNorm
argument_list|(
name|d
argument_list|,
name|f
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|terms
argument_list|()
return|;
block|}
annotation|@
name|Override
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
return|return
name|in
operator|.
name|terms
argument_list|(
name|t
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|termDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
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
return|return
name|in
operator|.
name|termDocs
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|termPositions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|in
operator|.
name|deleteDocument
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
comment|// Let FilterIndexReader handle commit()... we cannot override commit()
comment|// or call in.commit() ourselves.
comment|// protected void doCommit() throws IOException { in.commit(); }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
return|return
name|in
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|in
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|in
operator|.
name|isCurrent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
return|return
name|in
operator|.
name|isOptimized
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSequentialSubReaders
specifier|public
name|SolrIndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
name|subReaders
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|in
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|SolrIndexReader
condition|)
block|{
name|o
operator|=
operator|(
operator|(
name|SolrIndexReader
operator|)
name|o
operator|)
operator|.
name|in
expr_stmt|;
block|}
return|return
name|in
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reopen
specifier|public
name|SolrIndexReader
name|reopen
parameter_list|(
name|boolean
name|openReadOnly
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|r
init|=
name|in
operator|.
name|reopen
argument_list|(
name|openReadOnly
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|in
condition|)
block|{
return|return
name|this
return|;
block|}
name|SolrIndexReader
name|sr
init|=
operator|new
name|SolrIndexReader
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|sr
operator|.
name|associateInfo
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|sr
return|;
block|}
annotation|@
name|Override
DECL|method|reopen
specifier|public
name|SolrIndexReader
name|reopen
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|reopen
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|decRef
specifier|public
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteDocument
specifier|public
name|void
name|deleteDocument
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|in
operator|.
name|deleteDocument
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteDocuments
specifier|public
name|int
name|deleteDocuments
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
return|return
name|in
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|in
operator|.
name|document
argument_list|(
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCommitUserData
specifier|public
name|String
name|getCommitUserData
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCommitUserData
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getIndexCommit
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTermInfosIndexDivisor
specifier|public
name|int
name|getTermInfosIndexDivisor
parameter_list|()
block|{
return|return
name|in
operator|.
name|getTermInfosIndexDivisor
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|()
block|{
name|in
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDeletedDocs
specifier|public
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|numDeletedDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNorm
specifier|public
name|void
name|setNorm
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
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|in
operator|.
name|setNorm
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNorm
specifier|public
name|void
name|setNorm
parameter_list|(
name|int
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|in
operator|.
name|setNorm
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTermInfosIndexDivisor
specifier|public
name|void
name|setTermInfosIndexDivisor
parameter_list|(
name|int
name|indexDivisor
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|in
operator|.
name|setTermInfosIndexDivisor
argument_list|(
name|indexDivisor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|termPositions
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|undeleteAll
specifier|public
name|void
name|undeleteAll
parameter_list|()
throws|throws
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|in
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|/** SolrReaderInfo contains information that is the same for  * every SolrIndexReader that wraps the same IndexReader.  * Multiple SolrIndexReader instances will be accessing this  * class concurrently.  */
end_comment
begin_class
DECL|class|SolrReaderInfo
class|class
name|SolrReaderInfo
block|{
DECL|field|reader
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|method|SolrReaderInfo
specifier|public
name|SolrReaderInfo
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
DECL|method|getReader
specifier|public
name|IndexReader
name|getReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
block|}
end_class
end_unit
