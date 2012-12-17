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
name|ClosableThread
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
name|ZkNodeProps
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
name|cloud
operator|.
name|ZooKeeperException
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
name|CoreAdminParams
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
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
name|StrUtils
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
name|handler
operator|.
name|component
operator|.
name|ShardHandler
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
name|handler
operator|.
name|component
operator|.
name|ShardRequest
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
name|handler
operator|.
name|component
operator|.
name|ShardResponse
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
begin_class
DECL|class|OverseerCollectionProcessor
specifier|public
class|class
name|OverseerCollectionProcessor
implements|implements
name|Runnable
implements|,
name|ClosableThread
block|{
DECL|field|NUM_SLICES
specifier|public
specifier|static
specifier|final
name|String
name|NUM_SLICES
init|=
literal|"numShards"
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATION_FACTOR
init|=
literal|"replicationFactor"
decl_stmt|;
DECL|field|MAX_SHARDS_PER_NODE
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SHARDS_PER_NODE
init|=
literal|"maxShardsPerNode"
decl_stmt|;
DECL|field|CREATE_NODE_SET
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_NODE_SET
init|=
literal|"createNodeSet"
decl_stmt|;
DECL|field|DELETECOLLECTION
specifier|public
specifier|static
specifier|final
name|String
name|DELETECOLLECTION
init|=
literal|"deletecollection"
decl_stmt|;
DECL|field|CREATECOLLECTION
specifier|public
specifier|static
specifier|final
name|String
name|CREATECOLLECTION
init|=
literal|"createcollection"
decl_stmt|;
DECL|field|RELOADCOLLECTION
specifier|public
specifier|static
specifier|final
name|String
name|RELOADCOLLECTION
init|=
literal|"reloadcollection"
decl_stmt|;
comment|// TODO: use from Overseer?
DECL|field|QUEUE_OPERATION
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_OPERATION
init|=
literal|"operation"
decl_stmt|;
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
name|OverseerCollectionProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|workQueue
specifier|private
name|DistributedQueue
name|workQueue
decl_stmt|;
DECL|field|myId
specifier|private
name|String
name|myId
decl_stmt|;
DECL|field|shardHandler
specifier|private
name|ShardHandler
name|shardHandler
decl_stmt|;
DECL|field|adminPath
specifier|private
name|String
name|adminPath
decl_stmt|;
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|isClosed
specifier|private
name|boolean
name|isClosed
decl_stmt|;
DECL|method|OverseerCollectionProcessor
specifier|public
name|OverseerCollectionProcessor
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|myId
parameter_list|,
name|ShardHandler
name|shardHandler
parameter_list|,
name|String
name|adminPath
parameter_list|)
block|{
name|this
argument_list|(
name|zkStateReader
argument_list|,
name|myId
argument_list|,
name|shardHandler
argument_list|,
name|adminPath
argument_list|,
name|Overseer
operator|.
name|getCollectionQueue
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|OverseerCollectionProcessor
specifier|protected
name|OverseerCollectionProcessor
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|myId
parameter_list|,
name|ShardHandler
name|shardHandler
parameter_list|,
name|String
name|adminPath
parameter_list|,
name|DistributedQueue
name|workQueue
parameter_list|)
block|{
name|this
operator|.
name|zkStateReader
operator|=
name|zkStateReader
expr_stmt|;
name|this
operator|.
name|myId
operator|=
name|myId
expr_stmt|;
name|this
operator|.
name|shardHandler
operator|=
name|shardHandler
expr_stmt|;
name|this
operator|.
name|adminPath
operator|=
name|adminPath
expr_stmt|;
name|this
operator|.
name|workQueue
operator|=
name|workQueue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Process current queue of collection messages"
argument_list|)
expr_stmt|;
while|while
condition|(
name|amILeader
argument_list|()
operator|&&
operator|!
name|isClosed
condition|)
block|{
try|try
block|{
name|byte
index|[]
name|head
init|=
name|workQueue
operator|.
name|peek
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|//if (head != null) {    // should not happen since we block above
specifier|final
name|ZkNodeProps
name|message
init|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|head
argument_list|)
decl_stmt|;
specifier|final
name|String
name|operation
init|=
name|message
operator|.
name|getStr
argument_list|(
name|QUEUE_OPERATION
argument_list|)
decl_stmt|;
try|try
block|{
name|boolean
name|success
init|=
name|processMessage
argument_list|(
name|message
argument_list|,
name|operation
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// TODO: what to do on failure / partial failure
comment|// if we fail, do we clean up then ?
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Collection "
operator|+
name|operation
operator|+
literal|" of "
operator|+
name|message
operator|.
name|getStr
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" failed"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Collection "
operator|+
name|operation
operator|+
literal|" of "
operator|+
name|message
operator|.
name|getStr
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" failed"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
comment|//}
name|workQueue
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|SESSIONEXPIRED
operator|||
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|CONNECTIONLOSS
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Overseer cannot talk to ZK"
argument_list|)
expr_stmt|;
return|return;
block|}
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|isClosed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|amILeader
specifier|protected
name|boolean
name|amILeader
parameter_list|()
block|{
try|try
block|{
name|ZkNodeProps
name|props
init|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
literal|"/overseer_elect/leader"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|myId
operator|.
name|equals
argument_list|(
name|props
operator|.
name|getStr
argument_list|(
literal|"id"
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"According to ZK I (id="
operator|+
name|myId
operator|+
literal|") am no longer a leader."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|processMessage
specifier|protected
name|boolean
name|processMessage
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|,
name|String
name|operation
parameter_list|)
block|{
if|if
condition|(
name|CREATECOLLECTION
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
return|return
name|createCollection
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|message
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|DELETECOLLECTION
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
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
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminAction
operator|.
name|UNLOAD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_INSTANCE_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|collectionCmd
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|message
argument_list|,
name|params
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|RELOADCOLLECTION
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
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
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminAction
operator|.
name|RELOAD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|collectionCmd
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|message
argument_list|,
name|params
argument_list|)
return|;
block|}
comment|// unknown command, toss it from our queue
return|return
literal|true
return|;
block|}
DECL|method|createCollection
specifier|private
name|boolean
name|createCollection
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|)
block|{
name|String
name|collectionName
init|=
name|message
operator|.
name|getStr
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterState
operator|.
name|getCollections
argument_list|()
operator|.
name|contains
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"collection already exists: "
operator|+
name|collectionName
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
try|try
block|{
comment|// look at the replication factor and see if it matches reality
comment|// if it does not, find best nodes to create more cores
name|int
name|repFactor
init|=
name|msgStrToInt
argument_list|(
name|message
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|numSlices
init|=
name|msgStrToInt
argument_list|(
name|message
argument_list|,
name|NUM_SLICES
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
name|msgStrToInt
argument_list|(
name|message
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|createNodeSetStr
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|createNodeList
init|=
operator|(
operator|(
name|createNodeSetStr
operator|=
name|message
operator|.
name|getStr
argument_list|(
name|CREATE_NODE_SET
argument_list|)
operator|)
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|createNodeSetStr
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|repFactor
operator|<=
literal|0
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|REPLICATION_FACTOR
operator|+
literal|" must be> 0"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|numSlices
operator|<
literal|0
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|NUM_SLICES
operator|+
literal|" must be> 0"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|String
name|configName
init|=
name|message
operator|.
name|getStr
argument_list|(
literal|"collection.configName"
argument_list|)
decl_stmt|;
comment|// we need to look at every node and see how many cores it serves
comment|// add our new cores to existing nodes serving the least number of cores
comment|// but (for now) require that each core goes on a distinct node.
comment|// TODO: add smarter options that look at the current number of cores per
comment|// node?
comment|// for now we just go random
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodeList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|nodeList
operator|.
name|addAll
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|createNodeList
operator|!=
literal|null
condition|)
name|nodeList
operator|.
name|retainAll
argument_list|(
name|createNodeList
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeList
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot create collection "
operator|+
name|collectionName
operator|+
literal|". No live Solr-instaces"
operator|+
operator|(
operator|(
name|createNodeList
operator|!=
literal|null
operator|)
condition|?
literal|" among Solr-instances specified in "
operator|+
name|CREATE_NODE_SET
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|repFactor
operator|>
name|nodeList
operator|.
name|size
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Specified "
operator|+
name|REPLICATION_FACTOR
operator|+
literal|" of "
operator|+
name|repFactor
operator|+
literal|" on collection "
operator|+
name|collectionName
operator|+
literal|" is higher than or equal to the number of Solr instances currently live ("
operator|+
name|nodeList
operator|.
name|size
argument_list|()
operator|+
literal|"). Its unusual to run two replica of the same slice on the same Solr-instance."
argument_list|)
expr_stmt|;
block|}
name|int
name|maxShardsAllowedToCreate
init|=
name|maxShardsPerNode
operator|*
name|nodeList
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|requestedShardsToCreate
init|=
name|numSlices
operator|*
name|repFactor
decl_stmt|;
if|if
condition|(
name|maxShardsAllowedToCreate
operator|<
name|requestedShardsToCreate
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot create collection "
operator|+
name|collectionName
operator|+
literal|". Value of "
operator|+
name|MAX_SHARDS_PER_NODE
operator|+
literal|" is "
operator|+
name|maxShardsPerNode
operator|+
literal|", and the number of live nodes is "
operator|+
name|nodeList
operator|.
name|size
argument_list|()
operator|+
literal|". This allows a maximum of "
operator|+
name|maxShardsAllowedToCreate
operator|+
literal|" to be created. Value of "
operator|+
name|NUM_SLICES
operator|+
literal|" is "
operator|+
name|numSlices
operator|+
literal|" and value of "
operator|+
name|REPLICATION_FACTOR
operator|+
literal|" is "
operator|+
name|repFactor
operator|+
literal|". This requires "
operator|+
name|requestedShardsToCreate
operator|+
literal|" shards to be created (higher than the allowed number)"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numSlices
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|repFactor
condition|;
name|j
operator|++
control|)
block|{
name|String
name|nodeName
init|=
name|nodeList
operator|.
name|get
argument_list|(
operator|(
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
operator|(
name|j
operator|-
literal|1
operator|)
operator|)
operator|%
name|nodeList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|sliceName
init|=
literal|"shard"
operator|+
name|i
decl_stmt|;
name|String
name|shardName
init|=
name|collectionName
operator|+
literal|"_"
operator|+
name|sliceName
operator|+
literal|"_replica"
operator|+
name|j
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating shard "
operator|+
name|shardName
operator|+
literal|" as part of slice "
operator|+
name|sliceName
operator|+
literal|" of collection "
operator|+
name|collectionName
operator|+
literal|" on "
operator|+
name|nodeName
argument_list|)
expr_stmt|;
comment|// Need to create new params for each request
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
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|shardName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection.configName"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|COLLECTION
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|SHARD
argument_list|,
name|sliceName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
name|numSlices
argument_list|)
expr_stmt|;
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
name|adminPath
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|purpose
operator|=
literal|1
expr_stmt|;
name|String
name|replica
init|=
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
condition|)
name|replica
operator|=
name|replica
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|replica
block|}
expr_stmt|;
name|sreq
operator|.
name|actualShards
operator|=
name|sreq
operator|.
name|shards
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|shardHandler
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|replica
argument_list|,
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|failed
init|=
literal|0
decl_stmt|;
name|ShardResponse
name|srsp
decl_stmt|;
do|do
block|{
name|srsp
operator|=
name|shardHandler
operator|.
name|takeCompletedOrError
argument_list|()
expr_stmt|;
if|if
condition|(
name|srsp
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|e
init|=
name|srsp
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
comment|// should we retry?
comment|// TODO: we should return errors to the client
comment|// TODO: what if one fails and others succeed?
name|failed
operator|++
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Error talking to shard: "
operator|+
name|srsp
operator|.
name|getShard
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|srsp
operator|!=
literal|null
condition|)
do|;
comment|// if all calls succeeded, return true
if|if
condition|(
name|failed
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Successfully created all shards for collection "
operator|+
name|collectionName
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// Expecting that the necessary logging has already been performed
return|return
literal|false
return|;
block|}
block|}
DECL|method|collectionCmd
specifier|private
name|boolean
name|collectionCmd
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Executing Collection Cmd : "
operator|+
name|params
argument_list|)
expr_stmt|;
name|String
name|collectionName
init|=
name|message
operator|.
name|getStr
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|coll
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
literal|"Could not find collection:"
operator|+
name|collectionName
argument_list|)
throw|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|entry
range|:
name|coll
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Slice
name|slice
init|=
name|entry
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
argument_list|>
name|shardEntries
init|=
name|shards
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shardEntry
range|:
name|shardEntries
control|)
block|{
specifier|final
name|ZkNodeProps
name|node
init|=
name|shardEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|node
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
comment|// For thread safety, only simple clone the ModifiableSolrParams
name|ModifiableSolrParams
name|cloneParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|cloneParams
operator|.
name|add
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|cloneParams
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|node
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|replica
init|=
name|node
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
comment|// yes, they must use same admin handler path everywhere...
name|cloneParams
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
name|adminPath
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|purpose
operator|=
literal|1
expr_stmt|;
comment|// TODO: this sucks
if|if
condition|(
name|replica
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
condition|)
name|replica
operator|=
name|replica
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|replica
block|}
expr_stmt|;
name|sreq
operator|.
name|actualShards
operator|=
name|sreq
operator|.
name|shards
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
name|cloneParams
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Collection Admin sending CoreAdmin cmd to "
operator|+
name|replica
operator|+
literal|" params:"
operator|+
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
name|shardHandler
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|replica
argument_list|,
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|failed
init|=
literal|0
decl_stmt|;
name|ShardResponse
name|srsp
decl_stmt|;
do|do
block|{
name|srsp
operator|=
name|shardHandler
operator|.
name|takeCompletedOrError
argument_list|()
expr_stmt|;
if|if
condition|(
name|srsp
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|e
init|=
name|srsp
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
comment|// should we retry?
comment|// TODO: we should return errors to the client
comment|// TODO: what if one fails and others succeed?
name|failed
operator|++
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Error talking to shard: "
operator|+
name|srsp
operator|.
name|getShard
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|srsp
operator|!=
literal|null
condition|)
do|;
comment|// if all calls succeeded, return true
if|if
condition|(
name|failed
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|msgStrToInt
specifier|private
name|int
name|msgStrToInt
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|,
name|String
name|key
parameter_list|,
name|Integer
name|def
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|str
init|=
name|message
operator|.
name|getStr
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|str
operator|==
literal|null
condition|?
name|def
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Could not parse "
operator|+
name|key
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isClosed
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|isClosed
return|;
block|}
block|}
end_class
end_unit
