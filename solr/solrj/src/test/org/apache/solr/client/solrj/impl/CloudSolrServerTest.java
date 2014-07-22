begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
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
name|File
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
name|net
operator|.
name|MalformedURLException
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
name|Collection
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|LuceneTestCase
operator|.
name|Slow
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
name|request
operator|.
name|AbstractUpdateRequest
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
name|request
operator|.
name|UpdateRequest
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
name|cloud
operator|.
name|AbstractZkTestCase
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
name|Overseer
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
name|SolrDocumentList
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
name|cloud
operator|.
name|ClusterState
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
name|DocRouter
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
name|Replica
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
name|Slice
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
name|SolrZkClient
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
name|CommonParams
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
name|params
operator|.
name|ShardParams
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
begin_comment
comment|/**  * This test would be faster if we simulated the zk state instead.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|CloudSolrServerTest
specifier|public
class|class
name|CloudSolrServerTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloudSolrServerTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOLR_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
name|getFile
argument_list|(
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{        }
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
DECL|method|SOLR_HOME
specifier|public
specifier|static
name|String
name|SOLR_HOME
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// we expect this time of exception as shards go up and down...
comment|//ignoreException(".*");
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|CloudSolrServerTest
specifier|public
name|CloudSolrServerTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|3
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|allTests
argument_list|()
expr_stmt|;
block|}
DECL|method|allTests
specifier|private
name|void
name|allTests
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"clientTestExternColl"
decl_stmt|;
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|controlClientCloud
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|CloudSolrServer
name|cloudClient
init|=
name|createCloudClient
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrInputDocument
name|doc1
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
name|id
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|doc1
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello1"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello2"
argument_list|)
expr_stmt|;
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAction
argument_list|(
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Test single threaded routed updates for UpdateRequest
name|NamedList
name|response
init|=
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|CloudSolrServer
operator|.
name|RouteResponse
name|rr
init|=
operator|(
name|CloudSolrServer
operator|.
name|RouteResponse
operator|)
name|response
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|routes
init|=
name|rr
operator|.
name|getRoutes
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
argument_list|>
name|it
init|=
name|routes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|QueryRequest
name|queryRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|solrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|queryRequest
operator|.
name|process
argument_list|(
name|solrServer
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docList
init|=
name|queryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docList
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Test the deleteById routing for UpdateRequest
name|UpdateRequest
name|delRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|delRequest
operator|.
name|deleteById
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|delRequest
operator|.
name|deleteById
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|delRequest
operator|.
name|setAction
argument_list|(
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|delRequest
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|qParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|qParams
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryRequest
name|qRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|qParams
argument_list|)
decl_stmt|;
name|QueryResponse
name|qResponse
init|=
name|qRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
name|qResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docs
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// Test Multi-Threaded routed updates for UpdateRequest
name|CloudSolrServer
name|threadedClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|threadedClient
operator|=
operator|new
name|CloudSolrServer
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|threadedClient
operator|.
name|setParallelUpdates
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threadedClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|response
operator|=
name|threadedClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|rr
operator|=
operator|(
name|CloudSolrServer
operator|.
name|RouteResponse
operator|)
name|response
expr_stmt|;
name|routes
operator|=
name|rr
operator|.
name|getRoutes
argument_list|()
expr_stmt|;
name|it
operator|=
name|routes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LBHttpSolrServer
operator|.
name|Req
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|UpdateRequest
name|updateRequest
init|=
operator|(
name|UpdateRequest
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|updateRequest
operator|.
name|getDocuments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|QueryRequest
name|queryRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|solrServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|queryRequest
operator|.
name|process
argument_list|(
name|solrServer
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docList
init|=
name|queryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docList
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|threadedClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// Test that queries with _route_ params are routed by the client
comment|// Track request counts on each node before query calls
name|ClusterState
name|clusterState
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|col
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|requestCountsMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|col
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|baseURL
init|=
operator|(
name|String
operator|)
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|requestCountsMap
operator|.
name|put
argument_list|(
name|baseURL
argument_list|,
name|getNumRequests
argument_list|(
name|baseURL
argument_list|,
name|collectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Collect the base URLs of the replicas of shard that's expected to be hit
name|DocRouter
name|router
init|=
name|col
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|expectedSlices
init|=
name|router
operator|.
name|getSearchSlicesSingle
argument_list|(
literal|"0"
argument_list|,
literal|null
argument_list|,
name|col
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedBaseURLs
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|expectedSlice
range|:
name|expectedSlices
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|expectedSlice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|baseURL
init|=
operator|(
name|String
operator|)
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|expectedBaseURLs
operator|.
name|add
argument_list|(
name|baseURL
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"expected urls is not fewer than all urls! expected="
operator|+
name|expectedBaseURLs
operator|+
literal|"; all="
operator|+
name|requestCountsMap
operator|.
name|keySet
argument_list|()
argument_list|,
name|expectedBaseURLs
operator|.
name|size
argument_list|()
operator|<
name|requestCountsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Calculate a number of shard keys that route to the same shard.
name|int
name|n
decl_stmt|;
if|if
condition|(
name|TEST_NIGHTLY
condition|)
block|{
name|n
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|999
argument_list|)
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
name|n
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|9
argument_list|)
operator|+
literal|2
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|sameShardRoutes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|sameShardRoutes
operator|.
name|add
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|String
name|shardKey
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|router
operator|.
name|getSearchSlicesSingle
argument_list|(
name|shardKey
argument_list|,
literal|null
argument_list|,
name|col
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Expected Slices {}"
argument_list|,
name|slices
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedSlices
operator|.
name|equals
argument_list|(
name|slices
argument_list|)
condition|)
block|{
name|sameShardRoutes
operator|.
name|add
argument_list|(
name|shardKey
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|sameShardRoutes
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// Do N queries with _route_ parameter to the same shard
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|solrParams
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
name|sameShardRoutes
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sameShardRoutes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"output  : {}"
argument_list|,
name|cloudClient
operator|.
name|query
argument_list|(
name|solrParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Request counts increase from expected nodes should aggregate to 1000, while there should be
comment|// no increase in unexpected nodes.
name|int
name|increaseFromExpectedUrls
init|=
literal|0
decl_stmt|;
name|int
name|increaseFromUnexpectedUrls
init|=
literal|0
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|numRequestsToUnexpectedUrls
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|col
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|baseURL
init|=
operator|(
name|String
operator|)
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|Long
name|prevNumRequests
init|=
name|requestCountsMap
operator|.
name|get
argument_list|(
name|baseURL
argument_list|)
decl_stmt|;
name|Long
name|curNumRequests
init|=
name|getNumRequests
argument_list|(
name|baseURL
argument_list|,
name|collectionName
argument_list|)
decl_stmt|;
name|long
name|delta
init|=
name|curNumRequests
operator|-
name|prevNumRequests
decl_stmt|;
if|if
condition|(
name|expectedBaseURLs
operator|.
name|contains
argument_list|(
name|baseURL
argument_list|)
condition|)
block|{
name|increaseFromExpectedUrls
operator|+=
name|delta
expr_stmt|;
block|}
else|else
block|{
name|increaseFromUnexpectedUrls
operator|+=
name|delta
expr_stmt|;
name|numRequestsToUnexpectedUrls
operator|.
name|put
argument_list|(
name|baseURL
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|"Unexpected number of requests to expected URLs"
argument_list|,
name|n
argument_list|,
name|increaseFromExpectedUrls
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of requests to unexpected URLs: "
operator|+
name|numRequestsToUnexpectedUrls
argument_list|,
literal|0
argument_list|,
name|increaseFromUnexpectedUrls
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|cloudClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|getNumRequests
specifier|private
name|Long
name|getNumRequests
parameter_list|(
name|String
name|baseUrl
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|baseUrl
operator|+
literal|"/"
operator|+
name|collectionName
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/mbeans"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"stats"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"key"
argument_list|,
literal|"org.apache.solr.handler.StandardRequestHandler"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"cat"
argument_list|,
literal|"QUERYHANDLER"
argument_list|)
expr_stmt|;
comment|// use generic request to avoid extra processing of queries
name|QueryRequest
name|req
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resp
init|=
name|server
operator|.
name|request
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|NamedList
name|mbeans
init|=
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"solr-mbeans"
argument_list|)
decl_stmt|;
name|NamedList
name|queryHandler
init|=
operator|(
name|NamedList
operator|)
name|mbeans
operator|.
name|get
argument_list|(
literal|"QUERYHANDLER"
argument_list|)
decl_stmt|;
name|NamedList
name|select
init|=
operator|(
name|NamedList
operator|)
name|queryHandler
operator|.
name|get
argument_list|(
literal|"org.apache.solr.handler.StandardRequestHandler"
argument_list|)
decl_stmt|;
name|NamedList
name|stats
init|=
operator|(
name|NamedList
operator|)
name|select
operator|.
name|get
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
return|return
operator|(
name|Long
operator|)
name|stats
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|getDoc
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|testShutdown
specifier|public
name|void
name|testShutdown
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|CloudSolrServer
name|server
init|=
operator|new
name|CloudSolrServer
argument_list|(
literal|"[ff01::114]:33332"
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|setZkConnectTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|server
operator|.
name|connect
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|TimeoutException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testWrongZkChrootTest
specifier|public
name|void
name|testWrongZkChrootTest
parameter_list|()
throws|throws
name|MalformedURLException
block|{
name|CloudSolrServer
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|CloudSolrServer
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
operator|+
literal|"/xyz/foo"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setDefaultCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
expr_stmt|;
name|server
operator|.
name|setZkClientTimeout
argument_list|(
literal|1000
operator|*
literal|60
argument_list|)
expr_stmt|;
name|server
operator|.
name|connect
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|KeeperException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// see SOLR-6146 - this test will fail by virtue of the zkClient tracking performed
comment|// in the afterClass method of the base class
block|}
block|}
end_class
end_unit
