begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|Fieldable
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
name|Collector
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *<code>UpdateHandler</code> handles requests to change the index  * (adds, deletes, commits, optimizes, etc).  *  *  * @since solr 0.9  */
end_comment
begin_class
DECL|class|UpdateHandler
specifier|public
specifier|abstract
class|class
name|UpdateHandler
implements|implements
name|SolrInfoMBean
block|{
DECL|field|log
specifier|protected
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UpdateHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|idField
specifier|protected
specifier|final
name|SchemaField
name|idField
decl_stmt|;
DECL|field|idFieldType
specifier|protected
specifier|final
name|FieldType
name|idFieldType
decl_stmt|;
DECL|field|idTerm
specifier|protected
specifier|final
name|Term
name|idTerm
decl_stmt|;
comment|// prototype term to avoid interning fieldname
DECL|field|commitCallbacks
specifier|protected
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
name|commitCallbacks
init|=
operator|new
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|optimizeCallbacks
specifier|protected
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
name|optimizeCallbacks
init|=
operator|new
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|parseEventListeners
specifier|private
name|void
name|parseEventListeners
parameter_list|()
block|{
specifier|final
name|Class
argument_list|<
name|SolrEventListener
argument_list|>
name|clazz
init|=
name|SolrEventListener
operator|.
name|class
decl_stmt|;
specifier|final
name|String
name|label
init|=
literal|"Event Listener"
decl_stmt|;
for|for
control|(
name|PluginInfo
name|info
range|:
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getPluginInfos
argument_list|(
name|SolrEventListener
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
control|)
block|{
name|String
name|event
init|=
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"event"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"postCommit"
operator|.
name|equals
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|SolrEventListener
name|obj
init|=
name|core
operator|.
name|createInitInstance
argument_list|(
name|info
argument_list|,
name|clazz
argument_list|,
name|label
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|commitCallbacks
operator|.
name|add
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"added SolrEventListener for postCommit: "
operator|+
name|obj
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"postOptimize"
operator|.
name|equals
argument_list|(
name|event
argument_list|)
condition|)
block|{
name|SolrEventListener
name|obj
init|=
name|core
operator|.
name|createInitInstance
argument_list|(
name|info
argument_list|,
name|clazz
argument_list|,
name|label
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|optimizeCallbacks
operator|.
name|add
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"added SolrEventListener for postOptimize: "
operator|+
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|callPostCommitCallbacks
specifier|protected
name|void
name|callPostCommitCallbacks
parameter_list|()
block|{
for|for
control|(
name|SolrEventListener
name|listener
range|:
name|commitCallbacks
control|)
block|{
name|listener
operator|.
name|postCommit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|callPostOptimizeCallbacks
specifier|protected
name|void
name|callPostOptimizeCallbacks
parameter_list|()
block|{
for|for
control|(
name|SolrEventListener
name|listener
range|:
name|optimizeCallbacks
control|)
block|{
name|listener
operator|.
name|postCommit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|UpdateHandler
specifier|public
name|UpdateHandler
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|schema
operator|=
name|core
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|idField
operator|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
name|idFieldType
operator|=
name|idField
operator|!=
literal|null
condition|?
name|idField
operator|.
name|getType
argument_list|()
else|:
literal|null
expr_stmt|;
name|idTerm
operator|=
name|idField
operator|!=
literal|null
condition|?
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
literal|""
argument_list|)
else|:
literal|null
expr_stmt|;
name|parseEventListeners
argument_list|()
expr_stmt|;
block|}
DECL|method|createMainIndexWriter
specifier|protected
name|SolrIndexWriter
name|createMainIndexWriter
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|removeAllExisting
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SolrIndexWriter
argument_list|(
name|name
argument_list|,
name|core
operator|.
name|getNewIndexDir
argument_list|()
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
name|removeAllExisting
argument_list|,
name|schema
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|mainIndexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|core
operator|.
name|getCodecProvider
argument_list|()
argument_list|)
return|;
block|}
DECL|method|idTerm
specifier|protected
specifier|final
name|Term
name|idTerm
parameter_list|(
name|String
name|readableId
parameter_list|)
block|{
comment|// to correctly create the Term, the string needs to be run
comment|// through the Analyzer for that field.
return|return
operator|new
name|Term
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|,
name|idFieldType
operator|.
name|toInternal
argument_list|(
name|readableId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getIndexedId
specifier|protected
specifier|final
name|String
name|getIndexedId
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Operation requires schema to have a unique key field"
argument_list|)
throw|;
comment|// Right now, single valued fields that require value transformation from external to internal (indexed)
comment|// form have that transformation already performed and stored as the field value.
name|Fieldable
index|[]
name|id
init|=
name|doc
operator|.
name|getFieldables
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|id
operator|.
name|length
operator|<
literal|1
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Document is missing mandatory uniqueKey field: "
operator|+
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|id
operator|.
name|length
operator|>
literal|1
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Document contains multiple values for uniqueKey field: "
operator|+
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
return|return
name|idFieldType
operator|.
name|storedToIndexed
argument_list|(
name|id
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|getIndexedIdOptional
specifier|protected
specifier|final
name|String
name|getIndexedIdOptional
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
if|if
condition|(
name|idField
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Fieldable
name|f
init|=
name|doc
operator|.
name|getFieldable
argument_list|(
name|idField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|idFieldType
operator|.
name|storedToIndexed
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|addDoc
specifier|public
specifier|abstract
name|int
name|addDoc
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|delete
specifier|public
specifier|abstract
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deleteByQuery
specifier|public
specifier|abstract
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|mergeIndexes
specifier|public
specifier|abstract
name|int
name|mergeIndexes
parameter_list|(
name|MergeIndexesCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|commit
specifier|public
specifier|abstract
name|void
name|commit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|rollback
specifier|public
specifier|abstract
name|void
name|rollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|class|DeleteHitCollector
specifier|static
class|class
name|DeleteHitCollector
extends|extends
name|Collector
block|{
DECL|field|deleted
specifier|public
name|int
name|deleted
init|=
literal|0
decl_stmt|;
DECL|field|searcher
specifier|public
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|method|DeleteHitCollector
specifier|public
name|DeleteHitCollector
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|deleteDocument
argument_list|(
name|doc
operator|+
name|docBase
argument_list|)
expr_stmt|;
name|deleted
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// don't try to close the searcher on failure for now...
comment|// try { closeSearcher(); } catch (Exception ee) { SolrException.log(log,ee); }
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error deleting doc# "
operator|+
name|doc
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{            }
block|}
comment|/**    * NOTE: this function is not thread safe.  However, it is safe to call within the    *<code>inform( SolrCore core )</code> function for<code>SolrCoreAware</code> classes.    * Outside<code>inform</code>, this could potentially throw a ConcurrentModificationException    *    * @see SolrCoreAware    */
DECL|method|registerCommitCallback
specifier|public
name|void
name|registerCommitCallback
parameter_list|(
name|SolrEventListener
name|listener
parameter_list|)
block|{
name|commitCallbacks
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**    * NOTE: this function is not thread safe.  However, it is safe to call within the    *<code>inform( SolrCore core )</code> function for<code>SolrCoreAware</code> classes.    * Outside<code>inform</code>, this could potentially throw a ConcurrentModificationException    *    * @see SolrCoreAware    */
DECL|method|registerOptimizeCallback
specifier|public
name|void
name|registerOptimizeCallback
parameter_list|(
name|SolrEventListener
name|listener
parameter_list|)
block|{
name|optimizeCallbacks
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
