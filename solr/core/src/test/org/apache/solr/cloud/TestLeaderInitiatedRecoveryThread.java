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
name|ZkCoreNodeProps
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
name|servlet
operator|.
name|SolrDispatchFilter
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
name|TimeOut
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
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import
begin_comment
comment|/**  * Test for {@link LeaderInitiatedRecoveryThread}  */
end_comment
begin_class
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
DECL|class|TestLeaderInitiatedRecoveryThread
specifier|public
class|class
name|TestLeaderInitiatedRecoveryThread
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|TestLeaderInitiatedRecoveryThread
specifier|public
name|TestLeaderInitiatedRecoveryThread
parameter_list|()
block|{
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|fixShardCount
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testPublishDownState
specifier|public
name|void
name|testPublishDownState
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|leaderCoreNodeName
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|coreNodeName
decl_stmt|;
specifier|final
name|CloudJettyRunner
name|leaderRunner
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
decl_stmt|;
name|SolrDispatchFilter
name|filter
init|=
operator|(
name|SolrDispatchFilter
operator|)
name|leaderRunner
operator|.
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|ZkController
name|zkController
init|=
name|filter
operator|.
name|getCores
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|CloudJettyRunner
name|notLeader
init|=
literal|null
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cloudJettyRunner
range|:
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
control|)
block|{
if|if
condition|(
name|cloudJettyRunner
operator|!=
name|leaderRunner
condition|)
block|{
name|notLeader
operator|=
name|cloudJettyRunner
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
name|notLeader
argument_list|)
expr_stmt|;
name|Replica
name|replica
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getReplica
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|notLeader
operator|.
name|coreNodeName
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|replicaCoreNodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
decl_stmt|;
comment|/*      1. Test that publishDownState throws exception when zkController.isReplicaInRecoveryHandling == false       */
try|try
block|{
name|LeaderInitiatedRecoveryThread
name|thread
init|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|zkController
operator|.
name|isReplicaInRecoveryHandling
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|thread
operator|.
name|run
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"publishDownState should not have succeeded because replica url is not marked in leader initiated recovery in ZkController"
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
operator|==
name|SolrException
operator|.
name|ErrorCode
operator|.
name|INVALID_STATE
operator|.
name|code
argument_list|)
expr_stmt|;
block|}
comment|/*      2. Test that a non-live replica cannot be put into LIR or down state       */
name|LeaderInitiatedRecoveryThread
name|thread
init|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
decl_stmt|;
comment|// kill the replica
name|int
name|children
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getChildren
argument_list|(
literal|"/live_nodes"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|notLeader
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|TimeOut
name|timeOut
init|=
operator|new
name|TimeOut
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|timeOut
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
if|if
condition|(
name|children
operator|>
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getChildren
argument_list|(
literal|"/live_nodes"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|children
operator|>
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getChildren
argument_list|(
literal|"/live_nodes"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|cversion
init|=
name|getOverseerCversion
argument_list|()
decl_stmt|;
comment|// Thread should not publish LIR and down state for node which is not live, regardless of whether forcePublish is true or false
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// lets assert that we did not publish anything to overseer queue, simplest way is to assert that cversion of overseer queue zk node is still the same
name|assertEquals
argument_list|(
name|cversion
argument_list|,
name|getOverseerCversion
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// lets assert that we did not publish anything to overseer queue
name|assertEquals
argument_list|(
name|cversion
argument_list|,
name|getOverseerCversion
argument_list|()
argument_list|)
expr_stmt|;
comment|/*     3. Test that if ZK connection loss then thread should not attempt to publish down state even if forcePublish=true      */
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|notLeader
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|updateLIRState
parameter_list|(
name|String
name|replicaCoreNodeName
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
operator|new
name|KeeperException
operator|.
name|ConnectionLossException
argument_list|()
argument_list|)
throw|;
block|}
block|}
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      4. Test that if ZK connection loss or session expired then thread should not attempt to publish down state even if forcePublish=true       */
name|thread
operator|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|updateLIRState
parameter_list|(
name|String
name|replicaCoreNodeName
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
operator|new
name|KeeperException
operator|.
name|SessionExpiredException
argument_list|()
argument_list|)
throw|;
block|}
block|}
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      5. Test that any exception other then ZK connection loss or session expired should publish down state only if forcePublish=true       */
name|thread
operator|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|updateLIRState
parameter_list|(
name|String
name|replicaCoreNodeName
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"bogus exception"
argument_list|)
throw|;
block|}
block|}
expr_stmt|;
comment|// the following should return true because regardless of the bogus exception in setting LIR state, we still want recovery commands to be sent,
comment|// however the following will not publish a down state
name|cversion
operator|=
name|getOverseerCversion
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// lets assert that we did not publish anything to overseer queue, simplest way is to assert that cversion of overseer queue zk node is still the same
name|assertEquals
argument_list|(
name|cversion
argument_list|,
name|getOverseerCversion
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// this should have published a down state so assert that cversion has incremented
name|assertTrue
argument_list|(
name|getOverseerCversion
argument_list|()
operator|>
name|cversion
argument_list|)
expr_stmt|;
name|timeOut
operator|=
operator|new
name|TimeOut
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|timeOut
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|Replica
name|r
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getReplica
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|DOWN
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Replica
operator|.
name|State
operator|.
name|DOWN
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getReplica
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|/*     6. Test that non-leader cannot set LIR nodes      */
name|filter
operator|=
operator|(
name|SolrDispatchFilter
operator|)
name|notLeader
operator|.
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
expr_stmt|;
name|zkController
operator|=
name|filter
operator|.
name|getCores
argument_list|()
operator|.
name|getZkController
argument_list|()
expr_stmt|;
name|thread
operator|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|updateLIRState
parameter_list|(
name|String
name|replicaCoreNodeName
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|updateLIRState
argument_list|(
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|ZkController
operator|.
name|NotLeaderException
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
expr_stmt|;
name|cversion
operator|=
name|getOverseerCversion
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cversion
argument_list|,
name|getOverseerCversion
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      7. assert that we can write a LIR state if everything else is fine       */
comment|// reset the zkcontroller to the one from the leader
name|filter
operator|=
operator|(
name|SolrDispatchFilter
operator|)
name|leaderRunner
operator|.
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
expr_stmt|;
name|zkController
operator|=
name|filter
operator|.
name|getCores
argument_list|()
operator|.
name|getZkController
argument_list|()
expr_stmt|;
name|thread
operator|=
operator|new
name|LeaderInitiatedRecoveryThread
argument_list|(
name|zkController
argument_list|,
name|filter
operator|.
name|getCores
argument_list|()
argument_list|,
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replicaCoreNodeProps
argument_list|,
literal|1
argument_list|,
name|leaderCoreNodeName
argument_list|)
expr_stmt|;
name|thread
operator|.
name|publishDownState
argument_list|(
name|replicaCoreNodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|replicaCoreNodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|timeOut
operator|=
operator|new
name|TimeOut
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|timeOut
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
name|Replica
operator|.
name|State
name|state
init|=
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
name|Replica
operator|.
name|State
operator|.
name|DOWN
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|zkController
operator|.
name|getLeaderInitiatedRecoveryStateObject
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Replica
operator|.
name|State
operator|.
name|DOWN
argument_list|,
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/*     7. Test that      */
block|}
DECL|method|getOverseerCversion
specifier|protected
name|int
name|getOverseerCversion
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
literal|"/overseer/queue"
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|stat
operator|.
name|getCversion
argument_list|()
return|;
block|}
block|}
end_class
end_unit
