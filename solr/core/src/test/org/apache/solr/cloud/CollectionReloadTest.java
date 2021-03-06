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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|ZkStateReader
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
comment|/**  * Verifies cluster state remains consistent after collection reload.  */
end_comment
begin_class
annotation|@
name|Slow
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|CollectionReloadTest
specifier|public
class|class
name|CollectionReloadTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|CollectionReloadTest
specifier|public
name|CollectionReloadTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReloadedLeaderStateAfterZkSessionLoss
specifier|public
name|void
name|testReloadedLeaderStateAfterZkSessionLoss
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"testReloadedLeaderStateAfterZkSessionLoss initialized OK ... running test logic"
argument_list|)
expr_stmt|;
name|String
name|testCollectionName
init|=
literal|"c8n_1x1"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|createCollectionRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|Replica
name|leader
init|=
name|getShardLeader
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
literal|30
comment|/* timeout secs */
argument_list|)
decl_stmt|;
comment|// reload collection and wait to see the core report it has been reloaded
name|boolean
name|wasReloaded
init|=
name|reloadCollection
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Collection '"
operator|+
name|testCollectionName
operator|+
literal|"' failed to reload within a reasonable amount of time!"
argument_list|,
name|wasReloaded
argument_list|)
expr_stmt|;
comment|// cause session loss
name|chaosMonkey
operator|.
name|expireSession
argument_list|(
name|getJettyOnPort
argument_list|(
name|getReplicaPort
argument_list|(
name|leader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: have to wait a while for the node to get marked down after ZK session loss
comment|// but tests shouldn't be so timing dependent!
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
comment|// wait up to 15 seconds to see the replica in the active state
name|String
name|replicaState
init|=
literal|null
decl_stmt|;
name|int
name|timeoutSecs
init|=
literal|15
decl_stmt|;
name|long
name|timeout
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeoutSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
comment|// state of leader should be active after session loss recovery - see SOLR-7338
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|forceUpdateCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Slice
name|slice
init|=
name|cs
operator|.
name|getSlice
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|replicaState
operator|=
name|slice
operator|.
name|getReplica
argument_list|(
name|leader
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"active"
operator|.
name|equals
argument_list|(
name|replicaState
argument_list|)
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Leader state should be active after recovering from ZK session loss, but after "
operator|+
name|timeoutSecs
operator|+
literal|" seconds, it is "
operator|+
name|replicaState
argument_list|,
literal|"active"
argument_list|,
name|replicaState
argument_list|)
expr_stmt|;
comment|// try to clean up
try|try
block|{
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"testReloadedLeaderStateAfterZkSessionLoss succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
