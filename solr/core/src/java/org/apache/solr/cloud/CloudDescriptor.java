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
name|SolrParams
import|;
end_import
begin_class
DECL|class|CloudDescriptor
specifier|public
class|class
name|CloudDescriptor
block|{
DECL|field|shardId
specifier|private
name|String
name|shardId
decl_stmt|;
DECL|field|collectionName
specifier|private
name|String
name|collectionName
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|roles
specifier|private
name|String
name|roles
init|=
literal|null
decl_stmt|;
DECL|field|numShards
specifier|private
name|Integer
name|numShards
decl_stmt|;
DECL|field|nodeName
specifier|private
name|String
name|nodeName
init|=
literal|null
decl_stmt|;
DECL|field|isLeader
specifier|volatile
name|boolean
name|isLeader
init|=
literal|false
decl_stmt|;
DECL|field|lastPublished
specifier|volatile
name|String
name|lastPublished
init|=
name|ZkStateReader
operator|.
name|ACTIVE
decl_stmt|;
DECL|method|getLastPublished
specifier|public
name|String
name|getLastPublished
parameter_list|()
block|{
return|return
name|lastPublished
return|;
block|}
DECL|method|isLeader
specifier|public
name|boolean
name|isLeader
parameter_list|()
block|{
return|return
name|isLeader
return|;
block|}
DECL|method|setShardId
specifier|public
name|void
name|setShardId
parameter_list|(
name|String
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
DECL|method|getShardId
specifier|public
name|String
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|getCollectionName
specifier|public
name|String
name|getCollectionName
parameter_list|()
block|{
return|return
name|collectionName
return|;
block|}
DECL|method|setCollectionName
specifier|public
name|void
name|setCollectionName
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
name|this
operator|.
name|collectionName
operator|=
name|collectionName
expr_stmt|;
block|}
DECL|method|getRoles
specifier|public
name|String
name|getRoles
parameter_list|()
block|{
return|return
name|roles
return|;
block|}
DECL|method|setRoles
specifier|public
name|void
name|setRoles
parameter_list|(
name|String
name|roles
parameter_list|)
block|{
name|this
operator|.
name|roles
operator|=
name|roles
expr_stmt|;
block|}
comment|/** Optional parameters that can change how a core is created. */
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|// setting only matters on core creation
DECL|method|getNumShards
specifier|public
name|Integer
name|getNumShards
parameter_list|()
block|{
return|return
name|numShards
return|;
block|}
DECL|method|setNumShards
specifier|public
name|void
name|setNumShards
parameter_list|(
name|int
name|numShards
parameter_list|)
block|{
name|this
operator|.
name|numShards
operator|=
name|numShards
expr_stmt|;
block|}
DECL|method|getCoreNodeName
specifier|public
name|String
name|getCoreNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
DECL|method|setCoreNodeName
specifier|public
name|void
name|setCoreNodeName
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
block|}
block|}
end_class
end_unit
