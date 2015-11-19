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
name|LinkedList
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
name|Queue
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
name|ConcurrentHashMap
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
name|embedded
operator|.
name|JettySolrRunner
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
name|MiniSolrCloudCluster
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
name|core
operator|.
name|CoreContainer
import|;
end_import
begin_comment
comment|/**  * A ShardHandlerFactory that extends HttpShardHandlerFactory and  * tracks requests made to nodes/shards such that interested parties  * can watch such requests and make assertions inside tests  *<p>  * This is a test helper only and should *not* be used for production.  */
end_comment
begin_class
DECL|class|TrackingShardHandlerFactory
specifier|public
class|class
name|TrackingShardHandlerFactory
extends|extends
name|HttpShardHandlerFactory
block|{
DECL|field|queue
specifier|private
name|Queue
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|queue
decl_stmt|;
comment|/**    * Set the tracking queue for this factory. All the ShardHandler instances    * created from this factory will share the queue and call {@link java.util.Queue#offer(Object)}    * with a {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams}    * instance whenever    * {@link org.apache.solr.handler.component.ShardHandler#submit(ShardRequest, String, org.apache.solr.common.params.ModifiableSolrParams)}    * is called before the request is actually submitted to the    * wrapped {@link org.apache.solr.handler.component.HttpShardHandlerFactory} instance.    *<p>    * If a tracking queue is already set then this call will overwrite and replace the    * previous queue with this one.    *    * @param queue the {@link java.util.Queue} to be used for tracking shard requests    */
DECL|method|setTrackingQueue
specifier|public
specifier|synchronized
name|void
name|setTrackingQueue
parameter_list|(
name|Queue
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
comment|/**    * @return the {@link java.util.Queue} being used for tracking, null if none    * has been set    */
DECL|method|getTrackingQueue
specifier|public
specifier|synchronized
name|Queue
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|getTrackingQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
comment|/**    * @return true if a tracking queue has been set through    * {@link #setTrackingQueue(java.util.List, java.util.Queue)}, false otherwise    */
DECL|method|isTracking
specifier|public
specifier|synchronized
name|boolean
name|isTracking
parameter_list|()
block|{
return|return
name|queue
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|()
block|{
specifier|final
name|ShardHandlerFactory
name|factory
init|=
name|this
decl_stmt|;
specifier|final
name|ShardHandler
name|wrapped
init|=
name|super
operator|.
name|getShardHandler
argument_list|()
decl_stmt|;
return|return
operator|new
name|ShardHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|prepDistributed
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|wrapped
operator|.
name|prepDistributed
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|submit
parameter_list|(
name|ShardRequest
name|sreq
parameter_list|,
name|String
name|shard
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|preferredHostAddress
parameter_list|)
block|{
synchronized|synchronized
init|(
name|TrackingShardHandlerFactory
operator|.
name|this
init|)
block|{
if|if
condition|(
name|isTracking
argument_list|()
condition|)
block|{
name|queue
operator|.
name|offer
argument_list|(
operator|new
name|ShardRequestAndParams
argument_list|(
name|sreq
argument_list|,
name|shard
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|wrapped
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|shard
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ShardResponse
name|takeCompletedIncludingErrors
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|takeCompletedIncludingErrors
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ShardResponse
name|takeCompletedOrError
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|takeCompletedOrError
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|cancelAll
parameter_list|()
block|{
name|wrapped
operator|.
name|cancelAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ShardHandlerFactory
name|getShardHandlerFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the tracking queue for all nodes participating in this cluster. Once this method returns,    * all search and core admin requests distributed to shards will be submitted to the given queue.    *<p>    * This is equivalent to calling:    *<code>TrackingShardHandlerFactory.setTrackingQueue(cluster.getJettySolrRunners(), queue)</code>    *    * @see org.apache.solr.handler.component.TrackingShardHandlerFactory#setTrackingQueue(java.util.List, java.util.Queue)    */
DECL|method|setTrackingQueue
specifier|public
specifier|static
name|void
name|setTrackingQueue
parameter_list|(
name|MiniSolrCloudCluster
name|cluster
parameter_list|,
name|Queue
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|queue
parameter_list|)
block|{
name|setTrackingQueue
argument_list|(
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the tracking queue for all nodes participating in this cluster. Once this method returns,    * all search and core admin requests distributed to shards will be submitted to the given queue.    *    * @param runners a list of {@link org.apache.solr.client.solrj.embedded.JettySolrRunner} nodes    * @param queue   an implementation of {@link java.util.Queue} which    *                accepts {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams}    *                instances    */
DECL|method|setTrackingQueue
specifier|public
specifier|static
name|void
name|setTrackingQueue
parameter_list|(
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|runners
parameter_list|,
name|Queue
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|queue
parameter_list|)
block|{
for|for
control|(
name|JettySolrRunner
name|runner
range|:
name|runners
control|)
block|{
name|CoreContainer
name|container
init|=
name|runner
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|ShardHandlerFactory
name|factory
init|=
name|container
operator|.
name|getShardHandlerFactory
argument_list|()
decl_stmt|;
assert|assert
name|factory
operator|instanceof
name|TrackingShardHandlerFactory
operator|:
literal|"not a TrackingShardHandlerFactory: "
operator|+
name|factory
operator|.
name|getClass
argument_list|()
assert|;
name|TrackingShardHandlerFactory
name|trackingShardHandlerFactory
init|=
operator|(
name|TrackingShardHandlerFactory
operator|)
name|factory
decl_stmt|;
name|trackingShardHandlerFactory
operator|.
name|setTrackingQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ShardRequestAndParams
specifier|public
specifier|static
class|class
name|ShardRequestAndParams
block|{
DECL|field|shard
specifier|public
name|String
name|shard
decl_stmt|;
DECL|field|sreq
specifier|public
name|ShardRequest
name|sreq
decl_stmt|;
DECL|field|params
specifier|public
name|ModifiableSolrParams
name|params
decl_stmt|;
DECL|method|ShardRequestAndParams
specifier|public
name|ShardRequestAndParams
parameter_list|(
name|ShardRequest
name|sreq
parameter_list|,
name|String
name|shard
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|sreq
operator|=
name|sreq
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ShardRequestAndParams{"
operator|+
literal|"shard='"
operator|+
name|shard
operator|+
literal|'\''
operator|+
literal|", sreq="
operator|+
name|sreq
operator|+
literal|", params="
operator|+
name|params
operator|+
literal|'}'
return|;
block|}
block|}
comment|/**    * A queue having helper methods to select requests by shard and purpose.    *    * @see org.apache.solr.handler.component.TrackingShardHandlerFactory#setTrackingQueue(java.util.List, java.util.Queue)    */
DECL|class|RequestTrackingQueue
specifier|public
specifier|static
class|class
name|RequestTrackingQueue
extends|extends
name|LinkedList
argument_list|<
name|ShardRequestAndParams
argument_list|>
block|{
DECL|field|requests
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
argument_list|>
name|requests
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|offer
specifier|public
name|boolean
name|offer
parameter_list|(
name|ShardRequestAndParams
name|shardRequestAndParams
parameter_list|)
block|{
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|list
init|=
name|requests
operator|.
name|get
argument_list|(
name|shardRequestAndParams
operator|.
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|shardRequestAndParams
argument_list|)
expr_stmt|;
name|requests
operator|.
name|put
argument_list|(
name|shardRequestAndParams
operator|.
name|shard
argument_list|,
name|list
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|offer
argument_list|(
name|shardRequestAndParams
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|requests
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Retrieve request recorded by this queue which were sent to given collection, shard and purpose      *      * @param zkStateReader  the {@link org.apache.solr.common.cloud.ZkStateReader} from which cluster state is read      * @param collectionName the given collection name for which requests have to be extracted      * @param shardId        the given shard name for which requests have to be extracted      * @param purpose        the shard purpose      * @return instance of {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams}      * or null if none is found      * @throws java.lang.RuntimeException if more than one request is found to the same shard with the same purpose      */
DECL|method|getShardRequestByPurpose
specifier|public
name|ShardRequestAndParams
name|getShardRequestByPurpose
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|shardId
parameter_list|,
name|int
name|purpose
parameter_list|)
throws|throws
name|RuntimeException
block|{
name|List
argument_list|<
name|TrackingShardHandlerFactory
operator|.
name|ShardRequestAndParams
argument_list|>
name|shardRequests
init|=
name|getShardRequests
argument_list|(
name|zkStateReader
argument_list|,
name|collectionName
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TrackingShardHandlerFactory
operator|.
name|ShardRequestAndParams
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|TrackingShardHandlerFactory
operator|.
name|ShardRequestAndParams
name|request
range|:
name|shardRequests
control|)
block|{
if|if
condition|(
operator|(
name|request
operator|.
name|sreq
operator|.
name|purpose
operator|&
name|purpose
operator|)
operator|!=
literal|0
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Multiple requests to the same shard with the same purpose were found. Requests: "
operator|+
name|result
argument_list|)
throw|;
block|}
return|return
name|result
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**      * Retrieve all requests recorded by this queue which were sent to given collection and shard      *      * @param zkStateReader  the {@link org.apache.solr.common.cloud.ZkStateReader} from which cluster state is read      * @param collectionName the given collection name for which requests have to be extracted      * @param shardId        the given shard name for which requests have to be extracted      * @return a list of {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams}      * or empty list if none are found      */
DECL|method|getShardRequests
specifier|public
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|getShardRequests
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|shardId
parameter_list|)
block|{
name|DocCollection
name|collection
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
assert|assert
name|collection
operator|!=
literal|null
assert|;
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
assert|assert
name|slice
operator|!=
literal|null
assert|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
argument_list|>
name|entry
range|:
name|requests
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// multiple shard addresses may be present separated by '|'
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|'|'
argument_list|)
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
name|replica
range|:
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|coreUrl
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|contains
argument_list|(
name|coreUrl
argument_list|)
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Retrieves all core admin requests distributed to nodes by Collection API commands      *      * @return a list of {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams}      * or empty if none found      */
DECL|method|getCoreAdminRequests
specifier|public
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|getCoreAdminRequests
parameter_list|()
block|{
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
argument_list|>
name|map
init|=
name|getAllRequests
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
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|ShardRequestAndParams
name|shardRequestAndParams
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|shardRequestAndParams
operator|.
name|sreq
operator|.
name|purpose
operator|==
name|ShardRequest
operator|.
name|PURPOSE_PRIVATE
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|shardRequestAndParams
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**      * Retrieves all requests recorded by this collection as a Map of shard address (string url)      * to a list of {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams}      *      * @return a {@link java.util.concurrent.ConcurrentHashMap} of url strings to {@link org.apache.solr.handler.component.TrackingShardHandlerFactory.ShardRequestAndParams} objects      * or empty map if none have been recorded      */
DECL|method|getAllRequests
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
argument_list|>
name|getAllRequests
parameter_list|()
block|{
return|return
name|requests
return|;
block|}
block|}
block|}
end_class
end_unit
