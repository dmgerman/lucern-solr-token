begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|RequestStatusState
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
name|CollectionParams
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
name|CommonAdminParams
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
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
DECL|class|AsyncMigrateRouteKeyTest
specifier|public
class|class
name|AsyncMigrateRouteKeyTest
extends|extends
name|MigrateRouteKeyTest
block|{
DECL|method|AsyncMigrateRouteKeyTest
specifier|public
name|AsyncMigrateRouteKeyTest
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
block|}
DECL|field|MAX_WAIT_SECONDS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WAIT_SECONDS
init|=
literal|2
operator|*
literal|60
decl_stmt|;
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|multipleShardMigrateTest
argument_list|()
expr_stmt|;
name|printLayout
argument_list|()
expr_stmt|;
block|}
DECL|method|checkAsyncRequestForCompletion
specifier|protected
name|void
name|checkAsyncRequestForCompletion
parameter_list|(
name|String
name|asyncId
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|ModifiableSolrParams
name|params
decl_stmt|;
name|String
name|message
decl_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|REQUESTSTATUS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|OverseerCollectionMessageHandler
operator|.
name|REQUESTID
argument_list|,
name|asyncId
argument_list|)
expr_stmt|;
comment|// This task takes long enough to run. Also check for the current state of the task to be running.
name|message
operator|=
name|sendStatusRequestWithRetry
argument_list|(
name|params
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"found ["
operator|+
name|asyncId
operator|+
literal|"] in running tasks"
argument_list|,
name|message
argument_list|)
expr_stmt|;
comment|// Now wait until the task actually completes successfully/fails.
name|message
operator|=
name|sendStatusRequestWithRetry
argument_list|(
name|params
argument_list|,
name|MAX_WAIT_SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Task "
operator|+
name|asyncId
operator|+
literal|" not found in completed tasks."
argument_list|,
literal|"found ["
operator|+
name|asyncId
operator|+
literal|"] in completed tasks"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|invokeMigrateApi
specifier|protected
name|void
name|invokeMigrateApi
parameter_list|(
name|String
name|sourceCollection
parameter_list|,
name|String
name|splitKey
parameter_list|,
name|String
name|targetCollection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|String
name|asyncId
init|=
literal|"20140128"
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CollectionParams
operator|.
name|ACTION
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|MIGRATE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
name|sourceCollection
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"target.collection"
argument_list|,
name|targetCollection
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"split.key"
argument_list|,
name|splitKey
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"forward.timeout"
argument_list|,
literal|45
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonAdminParams
operator|.
name|ASYNC
argument_list|,
name|asyncId
argument_list|)
expr_stmt|;
name|invoke
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|checkAsyncRequestForCompletion
argument_list|(
name|asyncId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to send a status request with specific retry limit and return    * the message/null from the success response.    */
DECL|method|sendStatusRequestWithRetry
specifier|private
name|String
name|sendStatusRequestWithRetry
parameter_list|(
name|ModifiableSolrParams
name|params
parameter_list|,
name|int
name|maxCounter
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|NamedList
name|status
init|=
literal|null
decl_stmt|;
name|RequestStatusState
name|state
init|=
literal|null
decl_stmt|;
name|String
name|message
init|=
literal|null
decl_stmt|;
name|NamedList
name|r
decl_stmt|;
while|while
condition|(
name|maxCounter
operator|--
operator|>
literal|0
condition|)
block|{
name|r
operator|=
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|status
operator|=
operator|(
name|NamedList
operator|)
name|r
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
expr_stmt|;
name|state
operator|=
name|RequestStatusState
operator|.
name|fromKey
argument_list|(
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"state"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|=
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|RequestStatusState
operator|.
name|COMPLETED
operator|||
name|state
operator|==
name|RequestStatusState
operator|.
name|FAILED
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
return|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{        }
block|}
comment|// Return last state?
return|return
name|message
return|;
block|}
DECL|method|sendRequest
specifier|protected
name|NamedList
name|sendRequest
parameter_list|(
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
specifier|final
name|SolrRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|String
name|baseUrl
init|=
operator|(
operator|(
name|HttpSolrClient
operator|)
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|client
operator|.
name|solrClient
operator|)
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|baseUrl
operator|=
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseUrl
operator|.
name|length
argument_list|()
operator|-
literal|"collection1"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|baseServer
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
init|)
block|{
name|baseServer
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
return|return
name|baseServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
