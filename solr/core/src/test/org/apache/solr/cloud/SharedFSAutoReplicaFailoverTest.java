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
name|CompletionService
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
name|ExecutorCompletionService
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
name|Future
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
name|SynchronousQueue
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
name|ThreadPoolExecutor
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
name|TimeUnit
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Nightly
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
operator|.
name|SuppressSSL
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
name|hdfs
operator|.
name|HdfsTestUtil
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
name|ClusterStateUtil
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
name|util
operator|.
name|DefaultSolrThreadFactory
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import
begin_class
annotation|@
name|Nightly
annotation|@
name|Slow
annotation|@
name|SuppressSSL
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
comment|// hdfs client currently leaks thread(s)
DECL|class|SharedFSAutoReplicaFailoverTest
specifier|public
class|class
name|SharedFSAutoReplicaFailoverTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|true
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|executor
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"testExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|completionService
name|CompletionService
argument_list|<
name|Object
argument_list|>
name|completionService
decl_stmt|;
DECL|field|pending
name|Set
argument_list|<
name|Future
argument_list|<
name|Object
argument_list|>
argument_list|>
name|pending
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|hdfsFailoverBeforeClass
specifier|public
specifier|static
name|void
name|hdfsFailoverBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|hdfsFailoverAfterClass
specifier|public
specifier|static
name|void
name|hdfsFailoverAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
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
name|useJettyDataDir
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|SharedFSAutoReplicaFailoverTest
specifier|public
name|SharedFSAutoReplicaFailoverTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|4
expr_stmt|;
name|completionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|pending
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|checkCreatedVsState
operator|=
literal|false
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
try|try
block|{
name|testBasics
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// very slow tests, especially since jetty is started and stopped
comment|// serially
DECL|method|testBasics
specifier|private
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collection1
init|=
literal|"solrj_collection"
decl_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collection1
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|,
literal|"conf1"
argument_list|,
literal|"myOwnField"
argument_list|,
literal|true
argument_list|,
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collection1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|collection2
init|=
literal|"solrj_collection2"
decl_stmt|;
name|CollectionAdminResponse
name|response2
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collection2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|null
argument_list|,
literal|"conf1"
argument_list|,
literal|"myOwnField"
argument_list|,
literal|false
argument_list|,
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response2
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response2
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collection2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|120000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|getLiveAndActiveCount
argument_list|(
name|collection1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getLiveAndActiveCount
argument_list|(
name|collection2
argument_list|)
operator|<
literal|4
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|controlJetty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all not live"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllNotLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|45000
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|jettys
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|controlJetty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|120000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
name|int
name|jettyIndex
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
name|jettyIndex
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|jettys
operator|.
name|get
argument_list|(
name|jettyIndex
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Timeout waiting for all live and active"
argument_list|,
name|ClusterStateUtil
operator|.
name|waitForAllActiveAndLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collection1
argument_list|,
literal|60000
argument_list|)
argument_list|)
expr_stmt|;
name|assertSliceAndReplicaCount
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
block|}
DECL|method|getLiveAndActiveCount
specifier|private
name|int
name|getLiveAndActiveCount
parameter_list|(
name|String
name|collection1
parameter_list|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
decl_stmt|;
name|slices
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
name|collection1
argument_list|)
expr_stmt|;
name|int
name|liveAndActive
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
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
name|boolean
name|live
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|active
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
if|if
condition|(
name|live
operator|&&
name|active
condition|)
block|{
name|liveAndActive
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|liveAndActive
return|;
block|}
DECL|method|assertSliceAndReplicaCount
specifier|private
name|void
name|assertSliceAndReplicaCount
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
decl_stmt|;
name|slices
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|slices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|slice
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit