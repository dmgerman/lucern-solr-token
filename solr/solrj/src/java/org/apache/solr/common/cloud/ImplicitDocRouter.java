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
name|params
operator|.
name|SolrParams
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
name|ShardParams
operator|.
name|_ROUTE_
import|;
end_import
begin_comment
comment|/** This document router is for custom sharding  */
end_comment
begin_class
DECL|class|ImplicitDocRouter
specifier|public
class|class
name|ImplicitDocRouter
extends|extends
name|DocRouter
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"implicit"
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
name|ImplicitDocRouter
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getTargetSlice
specifier|public
name|Slice
name|getTargetSlice
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
name|String
name|shard
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sdoc
operator|!=
literal|null
condition|)
block|{
name|String
name|f
init|=
name|getRouteField
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|sdoc
operator|.
name|getFieldValue
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
name|shard
operator|=
name|o
operator|.
name|toString
argument_list|()
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No value for field "
operator|+
name|f
operator|+
literal|" in "
operator|+
name|sdoc
argument_list|)
throw|;
block|}
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|sdoc
operator|.
name|getFieldValue
argument_list|(
name|_ROUTE_
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|shard
operator|=
name|o
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
block|{
name|shard
operator|=
name|params
operator|.
name|get
argument_list|(
name|_ROUTE_
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No shard called ="
operator|+
name|shard
operator|+
literal|" in "
operator|+
name|collection
argument_list|)
throw|;
block|}
return|return
name|slice
return|;
block|}
return|return
literal|null
return|;
comment|// no shard specified... use default.
block|}
annotation|@
name|Override
DECL|method|isTargetSlice
specifier|public
name|boolean
name|isTargetSlice
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|String
name|shardId
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
comment|// todo : how to handle this?
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getSearchSlicesSingle
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSearchSlicesSingle
parameter_list|(
name|String
name|shardKey
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|shardKey
operator|==
literal|null
condition|)
block|{
return|return
name|collection
operator|.
name|getActiveSlices
argument_list|()
return|;
block|}
comment|// assume the shardKey is just a slice name
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|shardKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"implicit router can't find shard "
operator|+
name|shardKey
operator|+
literal|" in collection "
operator|+
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|slice
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|partitionRange
specifier|public
name|List
argument_list|<
name|Range
argument_list|>
name|partitionRange
parameter_list|(
name|int
name|partitions
parameter_list|,
name|Range
name|range
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
