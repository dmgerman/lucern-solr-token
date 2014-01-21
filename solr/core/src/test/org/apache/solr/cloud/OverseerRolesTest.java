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
name|CloudSolrServer
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
name|MapSolrParams
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
name|Ignore
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Locale
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|MAX_SHARDS_PER_NODE
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|NUM_SLICES
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
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|REPLICATION_FACTOR
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
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
operator|.
name|makeMap
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
name|common
operator|.
name|params
operator|.
name|CollectionParams
operator|.
name|CollectionAction
import|;
end_import
begin_class
DECL|class|OverseerRolesTest
specifier|public
class|class
name|OverseerRolesTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|client
specifier|private
name|CloudSolrServer
name|client
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeThisClass2
specifier|public
specifier|static
name|void
name|beforeThisClass2
parameter_list|()
throws|throws
name|Exception
block|{    }
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
operator|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|client
operator|.
name|shutdown
argument_list|()
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
DECL|method|OverseerRolesTest
specifier|public
name|OverseerRolesTest
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
literal|6
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
name|addOverseerRole2ExistingNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|addOverseerRole2ExistingNodes
specifier|private
name|void
name|addOverseerRole2ExistingNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"testOverseerCol"
decl_stmt|;
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"All nodes {}"
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|String
name|currentLeader
init|=
name|OverseerCollectionProcessor
operator|.
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Current leader {} "
argument_list|,
name|currentLeader
argument_list|)
expr_stmt|;
name|l
operator|.
name|remove
argument_list|(
name|currentLeader
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|String
name|overseerDesignate
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"overseerDesignate {}"
argument_list|,
name|overseerDesignate
argument_list|)
expr_stmt|;
name|setOverseerRole
argument_list|(
name|CollectionAction
operator|.
name|ADDROLE
argument_list|,
name|overseerDesignate
argument_list|)
expr_stmt|;
name|long
name|timeout
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
decl_stmt|;
name|boolean
name|leaderchanged
init|=
literal|false
decl_stmt|;
for|for
control|(
init|;
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|timeout
condition|;
control|)
block|{
if|if
condition|(
name|overseerDesignate
operator|.
name|equals
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|getLeaderNode
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"overseer designate is the new overseer"
argument_list|)
expr_stmt|;
name|leaderchanged
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"could not set the new overseer"
argument_list|,
name|leaderchanged
argument_list|)
expr_stmt|;
comment|//add another node as overseer
name|l
operator|.
name|remove
argument_list|(
name|overseerDesignate
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|String
name|anotherOverseer
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding another overseer designate {}"
argument_list|,
name|anotherOverseer
argument_list|)
expr_stmt|;
name|setOverseerRole
argument_list|(
name|CollectionAction
operator|.
name|ADDROLE
argument_list|,
name|anotherOverseer
argument_list|)
expr_stmt|;
name|timeout
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
expr_stmt|;
name|leaderchanged
operator|=
literal|false
expr_stmt|;
for|for
control|(
init|;
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|timeout
condition|;
control|)
block|{
comment|//      log.info(" count {}", System.currentTimeMillis());
comment|//
name|List
argument_list|<
name|String
argument_list|>
name|sortedNodeNames
init|=
name|OverseerCollectionProcessor
operator|.
name|getSortedNodeNames
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortedNodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|anotherOverseer
argument_list|)
operator|||
name|sortedNodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|anotherOverseer
argument_list|)
condition|)
block|{
name|leaderchanged
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"New overseer not the frontrunner : "
operator|+
name|OverseerCollectionProcessor
operator|.
name|getSortedNodeNames
argument_list|(
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
operator|+
literal|" expected : "
operator|+
name|anotherOverseer
argument_list|,
name|leaderchanged
argument_list|)
expr_stmt|;
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|setOverseerRole
specifier|private
name|void
name|setOverseerRole
parameter_list|(
name|CollectionAction
name|action
parameter_list|,
name|String
name|overseerDesignate
parameter_list|)
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Adding overseer designate {} "
argument_list|,
name|overseerDesignate
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"action"
argument_list|,
name|action
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|"role"
argument_list|,
literal|"overseer"
argument_list|,
literal|"node"
argument_list|,
name|overseerDesignate
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
decl_stmt|;
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
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|createCollection
specifier|protected
name|void
name|createCollection
parameter_list|(
name|String
name|COLL_NAME
parameter_list|,
name|CloudSolrServer
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|replicationFactor
init|=
literal|2
decl_stmt|;
name|int
name|numShards
init|=
literal|4
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
operator|(
operator|(
operator|(
operator|(
name|numShards
operator|+
literal|1
operator|)
operator|*
name|replicationFactor
operator|)
operator|/
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|)
operator|+
literal|1
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|makeMap
argument_list|(
name|REPLICATION_FACTOR
argument_list|,
name|replicationFactor
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|,
name|maxShardsPerNode
argument_list|,
name|NUM_SLICES
argument_list|,
name|numShards
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|collectionInfos
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|createCollection
argument_list|(
name|collectionInfos
argument_list|,
name|COLL_NAME
argument_list|,
name|props
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
