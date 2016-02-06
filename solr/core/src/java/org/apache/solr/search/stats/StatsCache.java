begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
package|;
end_package
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Weight
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
name|ResponseBuilder
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
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
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
name|search
operator|.
name|QueryCommand
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
name|search
operator|.
name|SolrIndexSearcher
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
name|plugin
operator|.
name|PluginInfoInitialized
import|;
end_import
begin_comment
comment|/**  * This class represents a cache of global document frequency information for  * selected terms. This information is periodically updated from all shards,  * either through scheduled events of some kind, or on every request when there  * is no global stats available for terms involved in the query (or if this  * information is stale due to changes in the shards).  *<p>  * There are instances of this class at the aggregator node (where the partial  * data from shards is aggregated), and on each core involved in a shard request  * (where this data is maintained and updated from the central cache).  *</p>  */
end_comment
begin_class
DECL|class|StatsCache
specifier|public
specifier|abstract
class|class
name|StatsCache
implements|implements
name|PluginInfoInitialized
block|{
comment|// TODO: decouple use in response from use in request context for these keys
comment|/**    * Map of terms and {@link TermStats}.    */
DECL|field|TERM_STATS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TERM_STATS_KEY
init|=
literal|"org.apache.solr.stats.termStats"
decl_stmt|;
comment|/**    * Value of {@link CollectionStats}.    */
DECL|field|COL_STATS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COL_STATS_KEY
init|=
literal|"org.apache.solr.stats.colStats"
decl_stmt|;
comment|/**    * List of terms in the query.    */
DECL|field|TERMS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TERMS_KEY
init|=
literal|"org.apache.solr.stats.terms"
decl_stmt|;
comment|/**    * Creates a {@link ShardRequest} to retrieve per-shard stats related to the    * current query and the current state of the requester's {@link StatsCache}.    *    * @param rb contains current request    * @return shard request to retrieve stats for terms in the current request,    * or null if no additional request is needed (e.g. if the information    * in global cache is already sufficient to satisfy this request).    */
DECL|method|retrieveStatsRequest
specifier|public
specifier|abstract
name|ShardRequest
name|retrieveStatsRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
function_decl|;
comment|/**    * Prepare a local (from the local shard) response to a "retrieve stats" shard    * request.    *    * @param rb       response builder    * @param searcher current local searcher    */
DECL|method|returnLocalStats
specifier|public
specifier|abstract
name|void
name|returnLocalStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
function_decl|;
comment|/**    * Process shard responses that contain partial local stats. Usually this    * entails combining per-shard stats for each term.    *    * @param req       query request    * @param responses responses from shards containing local stats for each shard    */
DECL|method|mergeToGlobalStats
specifier|public
specifier|abstract
name|void
name|mergeToGlobalStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|List
argument_list|<
name|ShardResponse
argument_list|>
name|responses
parameter_list|)
function_decl|;
comment|/**    * Receive global stats data from the master and update a local cache of stats    * with this global data. This event occurs either as a separate request, or    * together with the regular query request, in which case this method is    * called first, before preparing a {@link QueryCommand} to be submitted to    * the local {@link SolrIndexSearcher}.    *    * @param req query request with global stats data    */
DECL|method|receiveGlobalStats
specifier|public
specifier|abstract
name|void
name|receiveGlobalStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
function_decl|;
comment|/**    * Prepare global stats data to be sent out to shards in this request.    *    * @param rb       response builder    * @param outgoing shard request to be sent    */
DECL|method|sendGlobalStats
specifier|public
specifier|abstract
name|void
name|sendGlobalStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|outgoing
parameter_list|)
function_decl|;
comment|/**    * Prepare local {@link StatsSource} to provide stats information to perform    * local scoring (to be precise, to build a local {@link Weight} from the    * query).    *    * @param req query request    * @return an instance of {@link StatsSource} to use in creating a query    * {@link Weight}    */
DECL|method|get
specifier|public
specifier|abstract
name|StatsSource
name|get
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
function_decl|;
block|}
end_class
end_unit
