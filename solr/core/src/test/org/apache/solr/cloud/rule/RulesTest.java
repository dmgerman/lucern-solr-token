begin_unit
begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|SolrClient
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
name|CollectionAdminRequest
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
name|GenericSolrRequest
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
name|CollectionAdminResponse
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|cloud
operator|.
name|DocCollection
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
name|cloud
operator|.
name|ImplicitDocRouter
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
name|cloud
operator|.
name|ZkStateReader
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
name|junit
operator|.
name|Test
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
import|import static
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
operator|.
name|METHOD
operator|.
name|POST
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CoreContainer
operator|.
name|COLLECTIONS_HANDLER_PATH
import|;
end_import
begin_class
DECL|class|RulesTest
specifier|public
class|class
name|RulesTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RulesTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|5
argument_list|)
DECL|method|doIntegrationTest
specifier|public
name|void
name|doIntegrationTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rulesColl
init|=
literal|"rulesColl"
decl_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|CollectionAdminResponse
name|rsp
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|rulesColl
argument_list|)
operator|.
name|setShards
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|setRouterName
argument_list|(
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
operator|.
name|setRule
argument_list|(
literal|"cores:<4"
argument_list|,
literal|"node:*,replica:<2"
argument_list|,
literal|"freedisk:>1"
argument_list|)
operator|.
name|setSnitch
argument_list|(
literal|"class:ImplicitSnitch"
argument_list|)
decl_stmt|;
name|rsp
operator|=
name|create
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rsp
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocCollection
name|rulesCollection
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|rulesColl
argument_list|)
decl_stmt|;
name|List
name|list
init|=
operator|(
name|List
operator|)
name|rulesCollection
operator|.
name|get
argument_list|(
literal|"rule"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<4"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"cores"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<2"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"replica"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|">1"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"freedisk"
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|=
operator|(
name|List
operator|)
name|rulesCollection
operator|.
name|get
argument_list|(
literal|"snitch"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ImplicitSnitch"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|CollectionAdminResponse
name|rsp
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|CreateShard
name|createShard
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|CreateShard
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|rulesColl
argument_list|)
operator|.
name|setShardName
argument_list|(
literal|"shard2"
argument_list|)
decl_stmt|;
name|rsp
operator|=
name|createShard
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rsp
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|AddReplica
name|addReplica
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|AddReplica
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|rulesColl
argument_list|)
operator|.
name|setShardName
argument_list|(
literal|"shard2"
argument_list|)
decl_stmt|;
name|rsp
operator|=
name|addReplica
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rsp
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPortRule
specifier|public
name|void
name|testPortRule
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rulesColl
init|=
literal|"portRuleColl"
decl_stmt|;
name|String
name|baseUrl
init|=
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|port
init|=
literal|"-1"
decl_stmt|;
name|Matcher
name|hostAndPortMatcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?:https?://)?([^:]+):(\\d+)"
argument_list|)
operator|.
name|matcher
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostAndPortMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|port
operator|=
name|hostAndPortMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|baseUrl
argument_list|)
init|)
block|{
name|CollectionAdminResponse
name|rsp
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|create
operator|.
name|setCollectionName
argument_list|(
name|rulesColl
argument_list|)
expr_stmt|;
name|create
operator|.
name|setShards
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setRouterName
argument_list|(
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|create
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|create
operator|.
name|setRule
argument_list|(
literal|"port:"
operator|+
name|port
argument_list|)
expr_stmt|;
name|create
operator|.
name|setSnitch
argument_list|(
literal|"class:ImplicitSnitch"
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|create
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rsp
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocCollection
name|rulesCollection
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|rulesColl
argument_list|)
decl_stmt|;
name|List
name|list
init|=
operator|(
name|List
operator|)
name|rulesCollection
operator|.
name|get
argument_list|(
literal|"rule"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|port
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"port"
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|=
operator|(
name|List
operator|)
name|rulesCollection
operator|.
name|get
argument_list|(
literal|"snitch"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ImplicitSnitch"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testModifyColl
specifier|public
name|void
name|testModifyColl
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rulesColl
init|=
literal|"modifyColl"
decl_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|CollectionAdminResponse
name|rsp
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|rulesColl
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
operator|.
name|setRule
argument_list|(
literal|"cores:<4"
argument_list|,
literal|"node:*,replica:1"
argument_list|,
literal|"freedisk:>1"
argument_list|)
operator|.
name|setSnitch
argument_list|(
literal|"class:ImplicitSnitch"
argument_list|)
decl_stmt|;
name|rsp
operator|=
name|create
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|rsp
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|p
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"collection"
argument_list|,
name|rulesColl
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"action"
argument_list|,
literal|"MODIFYCOLLECTION"
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"rule"
argument_list|,
literal|"cores:<5"
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"rule"
argument_list|,
literal|"node:*,replica:1"
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"rule"
argument_list|,
literal|"freedisk:>5"
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"autoAddReplicas"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
operator|new
name|GenericSolrRequest
argument_list|(
name|POST
argument_list|,
name|COLLECTIONS_HANDLER_PATH
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|DocCollection
name|rulesCollection
init|=
name|ZkStateReader
operator|.
name|getCollectionLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|rulesColl
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"version_of_coll {}  "
argument_list|,
name|rulesCollection
operator|.
name|getZNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
name|List
name|list
init|=
operator|(
name|List
operator|)
name|rulesCollection
operator|.
name|get
argument_list|(
literal|"rule"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"<5"
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"cores"
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|<
literal|19
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|assertEquals
argument_list|(
literal|"<5"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"cores"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"replica"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|">5"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"freedisk"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|rulesCollection
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"autoAddReplicas"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|=
operator|(
name|List
operator|)
name|rulesCollection
operator|.
name|get
argument_list|(
literal|"snitch"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ImplicitSnitch"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
