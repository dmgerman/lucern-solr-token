begin_unit
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
name|SolrQuery
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
name|response
operator|.
name|QueryResponse
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
name|ShardParams
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import
begin_comment
comment|/**  * Test which asserts that shards.tolerant=true works even if one shard is down  * and also asserts that a meaningful exception is thrown when shards.tolerant=false  * See SOLR-7566  */
end_comment
begin_class
DECL|class|TestDownShardTolerantSearch
specifier|public
class|class
name|TestDownShardTolerantSearch
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|TestDownShardTolerantSearch
specifier|public
name|TestDownShardTolerantSearch
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|searchingShouldFailWithoutTolerantSearchSetToTrue
specifier|public
name|void
name|searchingShouldFailWithoutTolerantSearchSetToTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|indexAbunchOfDocs
argument_list|()
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|response
init|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|is
argument_list|(
literal|66L
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|kill
argument_list|(
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
argument_list|)
expr_stmt|;
name|response
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
operator|.
name|setParam
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
operator|.
name|setParam
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Request should have failed because we killed shard1 jetty"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"error from server"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Error message from server should have the name of the down shard"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|SHARD1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
