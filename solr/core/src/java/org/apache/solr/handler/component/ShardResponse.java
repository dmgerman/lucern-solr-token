begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|SolrResponse
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
begin_class
DECL|class|ShardResponse
specifier|public
specifier|final
class|class
name|ShardResponse
block|{
DECL|field|req
specifier|private
name|ShardRequest
name|req
decl_stmt|;
DECL|field|shard
specifier|private
name|String
name|shard
decl_stmt|;
DECL|field|nodeName
specifier|private
name|String
name|nodeName
decl_stmt|;
DECL|field|shardAddress
specifier|private
name|String
name|shardAddress
decl_stmt|;
comment|// the specific shard that this response was received from
DECL|field|rspCode
specifier|private
name|int
name|rspCode
decl_stmt|;
DECL|field|exception
specifier|private
name|Throwable
name|exception
decl_stmt|;
DECL|field|rsp
specifier|private
name|SolrResponse
name|rsp
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ShardResponse:{shard="
operator|+
name|shard
operator|+
literal|",shardAddress="
operator|+
name|shardAddress
operator|+
literal|"\n\trequest="
operator|+
name|req
operator|+
literal|"\n\tresponse="
operator|+
name|rsp
operator|+
operator|(
name|exception
operator|==
literal|null
condition|?
literal|""
else|:
literal|"\n\texception="
operator|+
name|SolrException
operator|.
name|toStr
argument_list|(
name|exception
argument_list|)
operator|)
operator|+
literal|"\n}"
return|;
block|}
DECL|method|getException
specifier|public
name|Throwable
name|getException
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
DECL|method|getShardRequest
specifier|public
name|ShardRequest
name|getShardRequest
parameter_list|()
block|{
return|return
name|req
return|;
block|}
DECL|method|getSolrResponse
specifier|public
name|SolrResponse
name|getSolrResponse
parameter_list|()
block|{
return|return
name|rsp
return|;
block|}
DECL|method|getShard
specifier|public
name|String
name|getShard
parameter_list|()
block|{
return|return
name|shard
return|;
block|}
DECL|method|getNodeName
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
DECL|method|setShardRequest
specifier|public
name|void
name|setShardRequest
parameter_list|(
name|ShardRequest
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|rsp
expr_stmt|;
block|}
DECL|method|setSolrResponse
specifier|public
name|void
name|setSolrResponse
parameter_list|(
name|SolrResponse
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
block|}
DECL|method|setShard
name|void
name|setShard
parameter_list|(
name|String
name|shard
parameter_list|)
block|{
name|this
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
block|}
DECL|method|setException
name|void
name|setException
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
DECL|method|setResponseCode
name|void
name|setResponseCode
parameter_list|(
name|int
name|rspCode
parameter_list|)
block|{
name|this
operator|.
name|rspCode
operator|=
name|rspCode
expr_stmt|;
block|}
DECL|method|setNodeName
name|void
name|setNodeName
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
comment|/** What was the shard address that returned this response.  Example:  "http://localhost:8983/solr" */
DECL|method|getShardAddress
specifier|public
name|String
name|getShardAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardAddress
return|;
block|}
DECL|method|setShardAddress
name|void
name|setShardAddress
parameter_list|(
name|String
name|addr
parameter_list|)
block|{
name|this
operator|.
name|shardAddress
operator|=
name|addr
expr_stmt|;
block|}
block|}
end_class
end_unit
