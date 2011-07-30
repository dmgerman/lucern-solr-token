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
DECL|field|softCommitCallbacks
specifier|protected
name|Vector
argument_list|<
name|SolrEventListener
argument_list|>
name|softCommitCallbacks
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
comment|/**    * Called when a SolrCore using this UpdateHandler is closed.    */
DECL|method|decref
specifier|public
specifier|abstract
name|void
name|decref
parameter_list|()
function_decl|;
comment|/**    * Called when this UpdateHandler is shared with another SolrCore.    */
DECL|method|incref
specifier|public
specifier|abstract
name|void
name|incref
parameter_list|()
function_decl|;
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
DECL|method|callPostSoftCommitCallbacks
specifier|protected
name|void
name|callPostSoftCommitCallbacks
parameter_list|()
block|{
for|for
control|(
name|SolrEventListener
name|listener
range|:
name|softCommitCallbacks
control|)
block|{
name|listener
operator|.
name|postSoftCommit
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
name|parseEventListeners
argument_list|()
expr_stmt|;
block|}
comment|/**    * Allows the UpdateHandler to create the SolrIndexSearcher after it    * has issued a 'softCommit'.     *     * @param previousSearcher    * @throws IOException    */
DECL|method|reopenSearcher
specifier|public
specifier|abstract
name|SolrIndexSearcher
name|reopenSearcher
parameter_list|(
name|SolrIndexSearcher
name|previousSearcher
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called when the Writer should be opened again - eg when replication replaces    * all of the index files.    *     * @throws IOException    */
DECL|method|newIndexWriter
specifier|public
specifier|abstract
name|void
name|newIndexWriter
parameter_list|()
throws|throws
name|IOException
function_decl|;
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
DECL|method|registerSoftCommitCallback
specifier|public
name|void
name|registerSoftCommitCallback
parameter_list|(
name|SolrEventListener
name|listener
parameter_list|)
block|{
name|softCommitCallbacks
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
