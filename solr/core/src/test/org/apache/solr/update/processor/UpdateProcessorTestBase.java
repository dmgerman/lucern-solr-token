begin_unit
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|params
operator|.
name|SolrParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SolrCore
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
name|LocalSolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_class
DECL|class|UpdateProcessorTestBase
specifier|public
class|class
name|UpdateProcessorTestBase
extends|extends
name|SolrTestCaseJ4
block|{
comment|/**    * Runs a document through the specified chain, and returns the final    * document used when the chain is completed (NOTE: some chains may    * modify the document in place    */
DECL|method|processAdd
specifier|protected
name|SolrInputDocument
name|processAdd
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|,
specifier|final
name|SolrInputDocument
name|docIn
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|processAdd
argument_list|(
name|chain
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|,
name|docIn
argument_list|)
return|;
block|}
comment|/**    * Runs a document through the specified chain, and returns the final    * document used when the chain is completed (NOTE: some chains may    * modify the document in place    */
DECL|method|processAdd
specifier|protected
name|SolrInputDocument
name|processAdd
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|,
specifier|final
name|SolrParams
name|requestParams
parameter_list|,
specifier|final
name|SolrInputDocument
name|docIn
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|pc
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No Chain named: "
operator|+
name|chain
argument_list|,
name|pc
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|requestParams
argument_list|)
decl_stmt|;
try|try
block|{
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|docIn
expr_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|pc
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|cmd
operator|.
name|solrDoc
return|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|processCommit
specifier|protected
name|void
name|processCommit
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|pc
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No Chain named: "
operator|+
name|chain
argument_list|,
name|pc
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|CommitUpdateCommand
name|cmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|pc
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|processDeleteById
specifier|protected
name|void
name|processDeleteById
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|pc
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No Chain named: "
operator|+
name|chain
argument_list|,
name|pc
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|pc
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|finish
specifier|protected
name|void
name|finish
parameter_list|(
specifier|final
name|String
name|chain
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|pc
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No Chain named: "
operator|+
name|chain
argument_list|,
name|pc
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|pc
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Convenience method for building up SolrInputDocuments    */
DECL|method|doc
specifier|final
name|SolrInputDocument
name|doc
parameter_list|(
name|SolrInputField
modifier|...
name|fields
parameter_list|)
block|{
name|SolrInputDocument
name|d
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrInputField
name|f
range|:
name|fields
control|)
block|{
name|d
operator|.
name|put
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|d
return|;
block|}
comment|/**    * Convenience method for building up SolrInputFields    */
DECL|method|field
specifier|final
name|SolrInputField
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|boost
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
name|SolrInputField
name|f
init|=
operator|new
name|SolrInputField
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|v
range|:
name|values
control|)
block|{
name|f
operator|.
name|addValue
argument_list|(
name|v
argument_list|,
literal|1.0F
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * Convenience method for building up SolrInputFields with default boost    */
DECL|method|f
specifier|final
name|SolrInputField
name|f
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
return|return
name|field
argument_list|(
name|name
argument_list|,
literal|1.0F
argument_list|,
name|values
argument_list|)
return|;
block|}
block|}
end_class
end_unit
