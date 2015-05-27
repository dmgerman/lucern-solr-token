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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
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
name|SuppressSysoutChecks
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
name|embedded
operator|.
name|JettyConfig
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
name|embedded
operator|.
name|JettyConfig
operator|.
name|Builder
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
name|embedded
operator|.
name|JettySolrRunner
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
name|CloudSolrClient
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
name|core
operator|.
name|CoreDescriptor
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
name|RevertDefaultThreadHandlerRule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**  * Test of the MiniSolrCloudCluster functionality. Keep in mind,   * MiniSolrCloudCluster is designed to be used outside of the Lucene test  * hierarchy.  */
end_comment
begin_class
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Solr logs to JUL"
argument_list|)
DECL|class|TestMiniSolrCloudCluster
specifier|public
class|class
name|TestMiniSolrCloudCluster
extends|extends
name|LuceneTestCase
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MiniSolrCloudCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_SERVERS
specifier|protected
name|int
name|NUM_SERVERS
init|=
literal|5
decl_stmt|;
DECL|field|NUM_SHARDS
specifier|protected
name|int
name|NUM_SHARDS
init|=
literal|2
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|protected
name|int
name|REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
DECL|method|TestMiniSolrCloudCluster
specifier|public
name|TestMiniSolrCloudCluster
parameter_list|()
block|{
name|NUM_SERVERS
operator|=
literal|5
expr_stmt|;
name|NUM_SHARDS
operator|=
literal|2
expr_stmt|;
name|REPLICATION_FACTOR
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|ClassRule
DECL|field|solrClassRules
specifier|public
specifier|static
name|TestRule
name|solrClassRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
operator|.
name|around
argument_list|(
operator|new
name|RevertDefaultThreadHandlerRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|testCollectionCreateSearchDelete
argument_list|()
expr_stmt|;
comment|// sometimes run a second test e.g. to test collection create-delete-create scenario
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
name|testCollectionCreateSearchDelete
argument_list|()
expr_stmt|;
block|}
DECL|method|testCollectionCreateSearchDelete
specifier|protected
name|void
name|testCollectionCreateSearchDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr-no-core.xml"
argument_list|)
decl_stmt|;
name|Builder
name|jettyConfig
init|=
name|JettyConfig
operator|.
name|builder
argument_list|()
decl_stmt|;
name|jettyConfig
operator|.
name|waitForLoadingCoresToFinish
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|MiniSolrCloudCluster
name|miniCluster
init|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|,
name|solrXml
argument_list|,
name|jettyConfig
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
init|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|assertTrue
argument_list|(
name|jetty
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// shut down a server
name|JettySolrRunner
name|stoppedServer
init|=
name|miniCluster
operator|.
name|stopJettySolrRunner
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stoppedServer
operator|.
name|isStopped
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
operator|-
literal|1
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// create a server
name|JettySolrRunner
name|startedServer
init|=
name|miniCluster
operator|.
name|startJettySolrRunner
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|startedServer
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// create collection
name|String
name|collectionName
init|=
literal|"testSolrCloudCollection"
decl_stmt|;
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
name|File
name|configDir
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
decl_stmt|;
name|miniCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionProperties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"solr.tests.maxBufferedDocs"
argument_list|,
literal|"100000"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"solr.tests.maxIndexingThreads"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"solr.tests.ramBufferSizeMB"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
comment|// use non-test classes so RandomizedRunner isn't necessary
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"solr.tests.mergePolicy"
argument_list|,
literal|"org.apache.lucene.index.TieredMergePolicy"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"solr.tests.mergeScheduler"
argument_list|,
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.RAMDirectoryFactory"
argument_list|)
expr_stmt|;
name|miniCluster
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|NUM_SHARDS
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|configName
argument_list|,
name|collectionProperties
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|45000
argument_list|,
literal|null
argument_list|)
init|;
name|ZkStateReader
name|zkStateReader
operator|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
init|)
block|{
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
comment|// modify/query collection
name|CloudSolrClient
name|cloudSolrClient
init|=
name|miniCluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|cloudSolrClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove a server not hosting any replicas
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|JettySolrRunner
argument_list|>
name|jettyMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|JettySolrRunner
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
operator|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|getProtocol
argument_list|()
operator|+
literal|"://"
operator|)
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|jettyMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|jetty
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getSlices
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
comment|// track the servers not host repliacs
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|jettyMap
operator|.
name|remove
argument_list|(
name|slice
operator|.
name|getLeader
argument_list|()
operator|.
name|getNodeName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"_solr"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
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
name|jettyMap
operator|.
name|remove
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"_solr"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Expected to find a node without a replica"
argument_list|,
name|jettyMap
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jettyToStop
init|=
name|jettyMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|jettys
operator|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|jettys
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|jettys
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|jettyToStop
argument_list|)
condition|)
block|{
name|miniCluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
operator|-
literal|1
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now restore the original state so that this function could be called multiple times
comment|// re-create a server (to restore original NUM_SERVERS count)
name|startedServer
operator|=
name|miniCluster
operator|.
name|startJettySolrRunner
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|startedServer
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudSolrClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception on query because collection should not be ready - we have turned on async core loading"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|SolrException
name|rc
init|=
operator|(
name|SolrException
operator|)
name|e
operator|.
name|getRootCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rc
operator|.
name|code
argument_list|()
operator|>=
literal|500
operator|&&
name|rc
operator|.
name|code
argument_list|()
operator|<
literal|600
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
name|code
argument_list|()
operator|>=
literal|500
operator|&&
name|e
operator|.
name|code
argument_list|()
operator|<
literal|600
argument_list|)
expr_stmt|;
block|}
comment|// delete the collection we created earlier
name|miniCluster
operator|.
name|deleteCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForCollectionToDisappear
argument_list|(
name|collectionName
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testErrorsInStartup
specifier|public
name|void
name|testErrorsInStartup
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr-no-core.xml"
argument_list|)
decl_stmt|;
name|AtomicInteger
name|jettyIndex
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|MiniSolrCloudCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|3
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|,
name|solrXml
argument_list|,
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|JettySolrRunner
name|startJettySolrRunner
parameter_list|(
name|JettyConfig
name|config
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|jettyIndex
operator|.
name|incrementAndGet
argument_list|()
operator|!=
literal|2
condition|)
return|return
name|super
operator|.
name|startJettySolrRunner
argument_list|(
name|config
argument_list|)
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake exception on startup!"
argument_list|)
throw|;
block|}
block|}
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to be thrown from MiniSolrCloudCluster"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Error starting up MiniSolrCloudCluster"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected one suppressed exception"
argument_list|,
literal|1
argument_list|,
name|e
operator|.
name|getSuppressed
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fake exception on startup!"
argument_list|,
name|e
operator|.
name|getSuppressed
argument_list|()
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testErrorsInShutdown
specifier|public
name|void
name|testErrorsInShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr-no-core.xml"
argument_list|)
decl_stmt|;
name|AtomicInteger
name|jettyIndex
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|MiniSolrCloudCluster
name|cluster
init|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|3
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|,
name|solrXml
argument_list|,
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|JettySolrRunner
name|stopJettySolrRunner
parameter_list|(
name|JettySolrRunner
name|jetty
parameter_list|)
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|j
init|=
name|super
operator|.
name|stopJettySolrRunner
argument_list|(
name|jetty
argument_list|)
decl_stmt|;
if|if
condition|(
name|jettyIndex
operator|.
name|incrementAndGet
argument_list|()
operator|==
literal|2
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Fake IOException on shutdown!"
argument_list|)
throw|;
return|return
name|j
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an exception to be thrown on MiniSolrCloudCluster shutdown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Error shutting down MiniSolrCloudCluster"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected one suppressed exception"
argument_list|,
literal|1
argument_list|,
name|e
operator|.
name|getSuppressed
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fake IOException on shutdown!"
argument_list|,
name|e
operator|.
name|getSuppressed
argument_list|()
index|[
literal|0
index|]
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
