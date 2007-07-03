begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|ArrayList
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
name|logging
operator|.
name|Logger
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
name|SolrInputDocument
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
name|SolrInputField
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
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|SolrQueryRequest
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|CommitUpdateCommand
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
name|update
operator|.
name|DeleteUpdateCommand
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
name|update
operator|.
name|DocumentBuilder
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
name|update
operator|.
name|UpdateHandler
import|;
end_import
begin_comment
comment|/**  * This is a good place for subclassed update handlers to process the document before it is   * indexed.  You may wish to add/remove fields or check if the requested user is allowed to   * update the given document...  *   * Perhaps you continue adding an error message (without indexing the document)...  * perhaps you throw an error and halt indexing (remove anything already indexed??)  *   * This implementation (the default) passes the request command (as is) to the updateHandler  * and adds debug info to the response.  *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|UpdateRequestProcessor
specifier|public
class|class
name|UpdateRequestProcessor
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|UpdateRequestProcessor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|req
specifier|protected
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|updateHandler
specifier|protected
specifier|final
name|UpdateHandler
name|updateHandler
decl_stmt|;
DECL|field|startTime
specifier|protected
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|response
specifier|protected
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
decl_stmt|;
comment|// hold on to the added list for logging and the response
DECL|field|addedIds
specifier|protected
name|List
argument_list|<
name|Object
argument_list|>
name|addedIds
decl_stmt|;
DECL|method|UpdateRequestProcessor
specifier|public
name|UpdateRequestProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|updateHandler
operator|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return The response information    */
DECL|method|finish
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|finish
parameter_list|()
block|{
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"update"
operator|+
name|response
operator|+
literal|" 0 "
operator|+
operator|(
name|elapsed
operator|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|processDelete
specifier|public
name|void
name|processDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|id
operator|!=
literal|null
condition|)
block|{
name|updateHandler
operator|.
name|delete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"delete"
argument_list|,
name|cmd
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|updateHandler
operator|.
name|deleteByQuery
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"deleteByQuery"
argument_list|,
name|cmd
operator|.
name|query
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|updateHandler
operator|.
name|commit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|optimize
condition|?
literal|"optimize"
else|:
literal|"commit"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|,
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Add a list of added id's to the response
if|if
condition|(
name|addedIds
operator|==
literal|null
condition|)
block|{
name|addedIds
operator|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"added"
argument_list|,
name|addedIds
argument_list|)
expr_stmt|;
block|}
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|uniqueKeyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|Object
name|id
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|uniqueKeyField
operator|!=
literal|null
condition|)
block|{
name|SolrInputField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|uniqueKeyField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|id
operator|=
name|f
operator|.
name|getFirstValue
argument_list|()
expr_stmt|;
block|}
block|}
name|addedIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|=
name|DocumentBuilder
operator|.
name|toDocument
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
expr_stmt|;
name|updateHandler
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
