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
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
begin_comment
comment|// Collect useful operations for testing assigning properties to individual replicas
end_comment
begin_comment
comment|// Could probably expand this to do something creative with getting random slices
end_comment
begin_comment
comment|// and shards, but for now this will do.
end_comment
begin_class
DECL|class|ReplicaPropertiesBase
specifier|public
specifier|abstract
class|class
name|ReplicaPropertiesBase
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|doPropertyAction
name|NamedList
argument_list|<
name|Object
argument_list|>
name|doPropertyAction
parameter_list|(
name|CloudSolrServer
name|client
parameter_list|,
name|String
modifier|...
name|paramsIn
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|assertTrue
argument_list|(
literal|"paramsIn must be an even multiple of 2, it is: "
operator|+
name|paramsIn
operator|.
name|length
argument_list|,
operator|(
name|paramsIn
operator|.
name|length
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|paramsIn
operator|.
name|length
condition|;
name|idx
operator|+=
literal|2
control|)
block|{
name|params
operator|.
name|set
argument_list|(
name|paramsIn
index|[
name|idx
index|]
argument_list|,
name|paramsIn
index|[
name|idx
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|QueryRequest
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
return|return
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|verifyPropertyNotPresent
name|void
name|verifyPropertyNotPresent
parameter_list|(
name|CloudSolrServer
name|client
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|replicaName
parameter_list|,
name|String
name|property
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ClusterState
name|clusterState
init|=
literal|null
decl_stmt|;
name|Replica
name|replica
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|300
condition|;
operator|++
name|idx
control|)
block|{
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|replica
operator|=
name|clusterState
operator|.
name|getReplica
argument_list|(
name|collectionName
argument_list|,
name|replicaName
argument_list|)
expr_stmt|;
if|if
condition|(
name|replica
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not find collection/replica pair! "
operator|+
name|collectionName
operator|+
literal|"/"
operator|+
name|replicaName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|replica
operator|.
name|getStr
argument_list|(
name|property
argument_list|)
argument_list|)
condition|)
return|return;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Property "
operator|+
name|property
operator|+
literal|" not set correctly for collection/replica pair: "
operator|+
name|collectionName
operator|+
literal|"/"
operator|+
name|replicaName
operator|+
literal|". Replica props: "
operator|+
name|replica
operator|.
name|getProperties
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|". Cluster state is "
operator|+
name|clusterState
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// The params are triplets,
comment|// collection
comment|// shard
comment|// replica
DECL|method|verifyPropertyVal
name|void
name|verifyPropertyVal
parameter_list|(
name|CloudSolrServer
name|client
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|replicaName
parameter_list|,
name|String
name|property
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|Replica
name|replica
init|=
literal|null
decl_stmt|;
name|ClusterState
name|clusterState
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|300
condition|;
operator|++
name|idx
control|)
block|{
comment|// Keep trying while Overseer writes the ZK state for up to 30 seconds.
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|replica
operator|=
name|clusterState
operator|.
name|getReplica
argument_list|(
name|collectionName
argument_list|,
name|replicaName
argument_list|)
expr_stmt|;
if|if
condition|(
name|replica
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not find collection/replica pair! "
operator|+
name|collectionName
operator|+
literal|"/"
operator|+
name|replicaName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|equals
argument_list|(
name|val
argument_list|,
name|replica
operator|.
name|getStr
argument_list|(
name|property
argument_list|)
argument_list|)
condition|)
return|return;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Property '"
operator|+
name|property
operator|+
literal|"' with value "
operator|+
name|replica
operator|.
name|getStr
argument_list|(
name|property
argument_list|)
operator|+
literal|" not set correctly for collection/replica pair: "
operator|+
name|collectionName
operator|+
literal|"/"
operator|+
name|replicaName
operator|+
literal|" property map is "
operator|+
name|replica
operator|.
name|getProperties
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
comment|// Verify that
comment|// 1> the property is only set once in all the replicas in a slice.
comment|// 2> the property is balanced evenly across all the nodes hosting collection
DECL|method|verifyUniqueAcrossCollection
name|void
name|verifyUniqueAcrossCollection
parameter_list|(
name|CloudSolrServer
name|client
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|property
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|verifyUnique
argument_list|(
name|client
argument_list|,
name|collectionName
argument_list|,
name|property
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyUniquePropertyWithinCollection
name|void
name|verifyUniquePropertyWithinCollection
parameter_list|(
name|CloudSolrServer
name|client
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|property
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|verifyUnique
argument_list|(
name|client
argument_list|,
name|collectionName
argument_list|,
name|property
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyUnique
name|void
name|verifyUnique
parameter_list|(
name|CloudSolrServer
name|client
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|property
parameter_list|,
name|boolean
name|balanced
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|DocCollection
name|col
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|300
condition|;
operator|++
name|idx
control|)
block|{
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|col
operator|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not find collection "
operator|+
name|collectionName
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|uniqueNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|allSlicesHaveProp
init|=
literal|true
decl_stmt|;
name|boolean
name|badSlice
init|=
literal|false
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
name|boolean
name|thisSliceHasProp
init|=
literal|false
decl_stmt|;
name|int
name|propCount
init|=
literal|0
decl_stmt|;
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
name|uniqueNodes
operator|.
name|add
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|propVal
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotBlank
argument_list|(
name|propVal
argument_list|)
condition|)
block|{
operator|++
name|propCount
expr_stmt|;
if|if
condition|(
name|counts
operator|.
name|containsKey
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
name|counts
operator|.
name|get
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|thisSliceHasProp
operator|=
literal|true
expr_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|badSlice
operator|=
operator|(
name|propCount
operator|>
literal|1
operator|)
condition|?
literal|true
else|:
name|badSlice
expr_stmt|;
name|allSlicesHaveProp
operator|=
name|allSlicesHaveProp
condition|?
name|thisSliceHasProp
else|:
name|allSlicesHaveProp
expr_stmt|;
block|}
if|if
condition|(
name|balanced
operator|==
literal|false
operator|&&
name|badSlice
operator|==
literal|false
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|allSlicesHaveProp
operator|&&
name|balanced
condition|)
block|{
comment|// Check that the properties are evenly distributed.
name|int
name|minProps
init|=
name|col
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
operator|/
name|uniqueNodes
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|maxProps
init|=
name|minProps
decl_stmt|;
if|if
condition|(
name|col
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
operator|%
name|uniqueNodes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
operator|++
name|maxProps
expr_stmt|;
block|}
name|boolean
name|doSleep
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|counts
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|!=
name|minProps
operator|&&
name|ent
operator|.
name|getValue
argument_list|()
operator|!=
name|maxProps
condition|)
block|{
name|doSleep
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doSleep
operator|==
literal|false
condition|)
block|{
name|assertTrue
argument_list|(
literal|"We really shouldn't be calling this if there is no node with the property "
operator|+
name|property
argument_list|,
name|counts
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Collection "
operator|+
name|collectionName
operator|+
literal|" does not have roles evenly distributed. Collection is: "
operator|+
name|col
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
