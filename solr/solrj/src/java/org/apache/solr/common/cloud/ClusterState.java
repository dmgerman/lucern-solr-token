begin_unit
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Map
operator|.
name|Entry
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
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONWriter
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
name|SolrException
operator|.
name|ErrorCode
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
name|HashPartitioner
operator|.
name|Range
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
comment|/**  * Immutable state of the cloud. Normally you can get the state by using  * {@link ZkStateReader#getClusterState()}.  */
end_comment
begin_class
DECL|class|ClusterState
specifier|public
class|class
name|ClusterState
implements|implements
name|JSONWriter
operator|.
name|Writable
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
name|ClusterState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkClusterStateVersion
specifier|private
name|Integer
name|zkClusterStateVersion
decl_stmt|;
DECL|field|collectionStates
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|collectionStates
decl_stmt|;
comment|// Map<collectionName, Map<sliceName,Slice>>
DECL|field|liveNodes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
decl_stmt|;
DECL|field|hp
specifier|private
specifier|final
name|HashPartitioner
name|hp
init|=
operator|new
name|HashPartitioner
argument_list|()
decl_stmt|;
DECL|field|rangeInfos
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RangeInfo
argument_list|>
name|rangeInfos
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RangeInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|leaders
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
argument_list|>
name|leaders
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Use this constr when ClusterState is meant for publication.    *     * hashCode and equals will only depend on liveNodes and not clusterStateVersion.    */
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|collectionStates
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|liveNodes
argument_list|,
name|collectionStates
argument_list|)
expr_stmt|;
block|}
comment|/**    * Use this constr when ClusterState is meant for consumption.    */
DECL|method|ClusterState
specifier|public
name|ClusterState
parameter_list|(
name|Integer
name|zkClusterStateVersion
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|collectionStates
parameter_list|)
block|{
name|this
operator|.
name|zkClusterStateVersion
operator|=
name|zkClusterStateVersion
expr_stmt|;
name|this
operator|.
name|liveNodes
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|liveNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|liveNodes
operator|.
name|addAll
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionStates
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
argument_list|(
name|collectionStates
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionStates
operator|.
name|putAll
argument_list|(
name|collectionStates
argument_list|)
expr_stmt|;
name|addRangeInfos
argument_list|(
name|collectionStates
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|getShardLeaders
argument_list|()
expr_stmt|;
block|}
DECL|method|getShardLeaders
specifier|private
name|void
name|getShardLeaders
parameter_list|()
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
argument_list|>
name|collections
init|=
name|collectionStates
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|collection
range|:
name|collections
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|state
init|=
name|collection
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|slices
init|=
name|state
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|sliceEntry
range|:
name|slices
control|)
block|{
name|Slice
name|slice
init|=
name|sliceEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shards
init|=
name|slice
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
argument_list|>
name|shardsEntries
init|=
name|shards
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shardEntry
range|:
name|shardsEntries
control|)
block|{
name|ZkNodeProps
name|props
init|=
name|shardEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|props
operator|.
name|containsKey
argument_list|(
name|ZkStateReader
operator|.
name|LEADER_PROP
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|leadersForCollection
init|=
name|leaders
operator|.
name|get
argument_list|(
name|collection
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|leadersForCollection
operator|==
literal|null
condition|)
block|{
name|leadersForCollection
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
argument_list|()
expr_stmt|;
name|leaders
operator|.
name|put
argument_list|(
name|collection
operator|.
name|getKey
argument_list|()
argument_list|,
name|leadersForCollection
argument_list|)
expr_stmt|;
block|}
name|leadersForCollection
operator|.
name|put
argument_list|(
name|sliceEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|props
argument_list|)
expr_stmt|;
break|break;
comment|// we found the leader for this shard
block|}
block|}
block|}
block|}
block|}
comment|/**    * Get properties of a shard leader for specific collection.    */
DECL|method|getLeader
specifier|public
name|ZkNodeProps
name|getLeader
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|collectionLeaders
init|=
name|leaders
operator|.
name|get
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|collectionLeaders
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|collectionLeaders
operator|.
name|get
argument_list|(
name|shard
argument_list|)
return|;
block|}
comment|/**    * Get shard properties or null if shard is not found.    */
DECL|method|getShardProps
specifier|public
name|Replica
name|getShardProps
parameter_list|(
specifier|final
name|String
name|collection
parameter_list|,
specifier|final
name|String
name|coreNodeName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|getSlices
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|get
argument_list|(
name|coreNodeName
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|get
argument_list|(
name|coreNodeName
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|addRangeInfos
specifier|private
name|void
name|addRangeInfos
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|collections
parameter_list|)
block|{
for|for
control|(
name|String
name|collection
range|:
name|collections
control|)
block|{
name|addRangeInfo
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the index Slice for collection.    */
DECL|method|getSlice
specifier|public
name|Slice
name|getSlice
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|slice
parameter_list|)
block|{
if|if
condition|(
name|collectionStates
operator|.
name|containsKey
argument_list|(
name|collection
argument_list|)
operator|&&
name|collectionStates
operator|.
name|get
argument_list|(
name|collection
argument_list|)
operator|.
name|containsKey
argument_list|(
name|slice
argument_list|)
condition|)
return|return
name|collectionStates
operator|.
name|get
argument_list|(
name|collection
argument_list|)
operator|.
name|get
argument_list|(
name|slice
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/**    * Get all slices for collection.    */
DECL|method|getSlices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getSlices
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
if|if
condition|(
operator|!
name|collectionStates
operator|.
name|containsKey
argument_list|(
name|collection
argument_list|)
condition|)
return|return
literal|null
return|;
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|collectionStates
operator|.
name|get
argument_list|(
name|collection
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get collection names.    */
DECL|method|getCollections
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getCollections
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|collectionStates
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return Map&lt;collectionName, Map&lt;sliceName,Slice&gt;&gt;    */
DECL|method|getCollectionStates
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|getCollectionStates
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|collectionStates
argument_list|)
return|;
block|}
comment|/**    * Get names of the currently live nodes.    */
DECL|method|getLiveNodes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getLiveNodes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|liveNodes
argument_list|)
return|;
block|}
comment|/**    * Get shardId for core.    * @param coreNodeName in the form of nodeName_coreName    */
DECL|method|getShardId
specifier|public
name|String
name|getShardId
parameter_list|(
name|String
name|coreNodeName
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|states
range|:
name|collectionStates
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
range|:
name|states
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shards
range|:
name|slices
operator|.
name|getValue
argument_list|()
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|coreNodeName
operator|.
name|equals
argument_list|(
name|shards
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|slices
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Check if node is alive.     */
DECL|method|liveNodesContain
specifier|public
name|boolean
name|liveNodesContain
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|liveNodes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getRanges
specifier|public
name|RangeInfo
name|getRanges
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
comment|// TODO: store this in zk
name|RangeInfo
name|rangeInfo
init|=
name|rangeInfos
operator|.
name|get
argument_list|(
name|collection
argument_list|)
decl_stmt|;
return|return
name|rangeInfo
return|;
block|}
DECL|method|addRangeInfo
specifier|private
name|RangeInfo
name|addRangeInfo
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
decl_stmt|;
name|RangeInfo
name|rangeInfo
decl_stmt|;
name|rangeInfo
operator|=
operator|new
name|RangeInfo
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|getSlices
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can not find collection "
operator|+
name|collection
operator|+
literal|" in "
operator|+
name|this
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|shards
init|=
name|slices
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|shardList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|shards
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|shardList
operator|.
name|addAll
argument_list|(
name|shards
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|shardList
argument_list|)
expr_stmt|;
name|ranges
operator|=
name|hp
operator|.
name|partitionRange
argument_list|(
name|shards
operator|.
name|size
argument_list|()
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|rangeInfo
operator|.
name|ranges
operator|=
name|ranges
expr_stmt|;
name|rangeInfo
operator|.
name|shardList
operator|=
name|shardList
expr_stmt|;
name|rangeInfos
operator|.
name|put
argument_list|(
name|collection
argument_list|,
name|rangeInfo
argument_list|)
expr_stmt|;
return|return
name|rangeInfo
return|;
block|}
comment|/**    * Get shard id for hash. This is used when determining which Slice the    * document is to be submitted to.    */
DECL|method|getShard
specifier|public
name|String
name|getShard
parameter_list|(
name|int
name|hash
parameter_list|,
name|String
name|collection
parameter_list|)
block|{
name|RangeInfo
name|rangInfo
init|=
name|getRanges
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Range
name|range
range|:
name|rangInfo
operator|.
name|ranges
control|)
block|{
if|if
condition|(
name|range
operator|.
name|includes
argument_list|(
name|hash
argument_list|)
condition|)
block|{
return|return
name|rangInfo
operator|.
name|shardList
operator|.
name|get
argument_list|(
name|cnt
argument_list|)
return|;
block|}
name|cnt
operator|++
expr_stmt|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The HashPartitioner failed"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"live nodes:"
operator|+
name|liveNodes
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" collections:"
operator|+
name|collectionStates
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Create ClusterState by reading the current state from zookeeper.     */
DECL|method|load
specifier|public
specifier|static
name|ClusterState
name|load
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|)
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
name|byte
index|[]
name|state
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|load
argument_list|(
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|state
argument_list|,
name|liveNodes
argument_list|)
return|;
block|}
comment|/**    * Create ClusterState from json string that is typically stored in zookeeper.    *     * Use {@link ClusterState#load(SolrZkClient, Set)} instead, unless you want to    * do something more when getting the data - such as get the stat, set watch, etc.    *     * @param version zk version of the clusterstate.json file (bytes)    * @param bytes clusterstate.json as a byte array    * @param liveNodes list of live nodes    * @return the ClusterState    */
DECL|method|load
specifier|public
specifier|static
name|ClusterState
name|load
parameter_list|(
name|Integer
name|version
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|ClusterState
argument_list|(
name|version
argument_list|,
name|liveNodes
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
comment|// System.out.println("########## Loading ClusterState:" + new String(bytes));
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stateMap
init|=
operator|(
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ZkStateReader
operator|.
name|fromJSON
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|state
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|collectionName
range|:
name|stateMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collection
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|stateMap
operator|.
name|get
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sliceEntry
range|:
name|collection
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Slice
name|slice
init|=
operator|new
name|Slice
argument_list|(
name|sliceEntry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|sliceEntry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|slices
operator|.
name|put
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|,
name|slice
argument_list|)
expr_stmt|;
block|}
name|state
operator|.
name|put
argument_list|(
name|collectionName
argument_list|,
name|slices
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ClusterState
argument_list|(
name|version
argument_list|,
name|liveNodes
argument_list|,
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|jsonWriter
parameter_list|)
block|{
name|jsonWriter
operator|.
name|write
argument_list|(
name|collectionStates
argument_list|)
expr_stmt|;
block|}
DECL|class|RangeInfo
specifier|private
class|class
name|RangeInfo
block|{
DECL|field|ranges
specifier|private
name|List
argument_list|<
name|Range
argument_list|>
name|ranges
decl_stmt|;
DECL|field|shardList
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|shardList
decl_stmt|;
block|}
comment|/**    * The version of clusterstate.json in ZooKeeper.    *     * @return null if ClusterState was created for publication, not consumption    */
DECL|method|getZkClusterStateVersion
specifier|public
name|Integer
name|getZkClusterStateVersion
parameter_list|()
block|{
return|return
name|zkClusterStateVersion
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|zkClusterStateVersion
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|zkClusterStateVersion
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|liveNodes
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|liveNodes
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ClusterState
name|other
init|=
operator|(
name|ClusterState
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|zkClusterStateVersion
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|zkClusterStateVersion
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|zkClusterStateVersion
operator|.
name|equals
argument_list|(
name|other
operator|.
name|zkClusterStateVersion
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|liveNodes
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|liveNodes
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|liveNodes
operator|.
name|equals
argument_list|(
name|other
operator|.
name|liveNodes
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
