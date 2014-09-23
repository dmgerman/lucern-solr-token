begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|request
package|;
end_package
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
name|index
operator|.
name|LeafReaderContext
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Filter
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
name|analytics
operator|.
name|accumulator
operator|.
name|BasicAccumulator
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
name|analytics
operator|.
name|accumulator
operator|.
name|FacetingAccumulator
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
name|analytics
operator|.
name|accumulator
operator|.
name|ValueAccumulator
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
name|analytics
operator|.
name|plugin
operator|.
name|AnalyticsStatisticsCollector
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
name|DocSet
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
comment|/**  * Class which computes the set of {@link AnalyticsRequest}s.  */
end_comment
begin_class
DECL|class|AnalyticsStats
specifier|public
class|class
name|AnalyticsStats
block|{
DECL|field|docs
specifier|protected
name|DocSet
name|docs
decl_stmt|;
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
DECL|field|searcher
specifier|protected
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|req
specifier|protected
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|statsCollector
specifier|protected
name|AnalyticsStatisticsCollector
name|statsCollector
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AnalyticsStats
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AnalyticsStats
specifier|public
name|AnalyticsStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|AnalyticsStatisticsCollector
name|statsCollector
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|req
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|statsCollector
operator|=
name|statsCollector
expr_stmt|;
block|}
comment|/**    * Calculates the analytics requested in the Parameters.    *     * @return List of results formated to mirror the input XML.    * @throws IOException if execution fails    */
DECL|method|execute
specifier|public
name|NamedList
argument_list|<
name|?
argument_list|>
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
name|statsCollector
operator|.
name|startRequest
argument_list|()
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AnalyticsRequest
argument_list|>
name|requests
decl_stmt|;
name|requests
operator|=
name|AnalyticsRequestFactory
operator|.
name|parse
argument_list|(
name|searcher
operator|.
name|getSchema
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|requests
operator|==
literal|null
operator|||
name|requests
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|res
return|;
block|}
name|statsCollector
operator|.
name|addRequests
argument_list|(
name|requests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get filter to all docs
name|Filter
name|filter
init|=
name|docs
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
comment|// Computing each Analytics Request Seperately
for|for
control|(
name|AnalyticsRequest
name|areq
range|:
name|requests
control|)
block|{
comment|// The Accumulator which will control the statistics generation
comment|// for the entire analytics request
name|ValueAccumulator
name|accumulator
decl_stmt|;
comment|// The number of total facet requests
name|int
name|facets
init|=
name|areq
operator|.
name|getFieldFacets
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|areq
operator|.
name|getRangeFacets
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|areq
operator|.
name|getQueryFacets
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|facets
operator|==
literal|0
condition|)
block|{
name|accumulator
operator|=
name|BasicAccumulator
operator|.
name|create
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|areq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|accumulator
operator|=
name|FacetingAccumulator
operator|.
name|create
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|areq
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Analytics request '"
operator|+
name|areq
operator|.
name|getName
argument_list|()
operator|+
literal|"' failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|statsCollector
operator|.
name|addStatsCollected
argument_list|(
operator|(
operator|(
name|BasicAccumulator
operator|)
name|accumulator
operator|)
operator|.
name|getNumStatsCollectors
argument_list|()
argument_list|)
expr_stmt|;
name|statsCollector
operator|.
name|addStatsRequests
argument_list|(
name|areq
operator|.
name|getExpressions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|statsCollector
operator|.
name|addFieldFacets
argument_list|(
name|areq
operator|.
name|getFieldFacets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|statsCollector
operator|.
name|addRangeFacets
argument_list|(
name|areq
operator|.
name|getRangeFacets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|statsCollector
operator|.
name|addQueryFacets
argument_list|(
name|areq
operator|.
name|getQueryFacets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|statsCollector
operator|.
name|addQueries
argument_list|(
operator|(
operator|(
name|BasicAccumulator
operator|)
name|accumulator
operator|)
operator|.
name|getNumQueries
argument_list|()
argument_list|)
expr_stmt|;
comment|// Loop through the documents returned by the query and add to accumulator
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|contexts
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|leafNum
init|=
literal|0
init|;
name|leafNum
operator|<
name|contexts
operator|.
name|size
argument_list|()
condition|;
name|leafNum
operator|++
control|)
block|{
name|LeafReaderContext
name|context
init|=
name|contexts
operator|.
name|get
argument_list|(
name|leafNum
argument_list|)
decl_stmt|;
name|DocIdSet
name|dis
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// solr docsets already exclude any deleted docs
name|DocIdSetIterator
name|disi
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dis
operator|!=
literal|null
condition|)
block|{
name|disi
operator|=
name|dis
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|disi
operator|!=
literal|null
condition|)
block|{
name|accumulator
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|disi
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
while|while
condition|(
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|// Add a document to the statistics being generated
name|accumulator
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// do some post-processing
name|accumulator
operator|.
name|postProcess
argument_list|()
expr_stmt|;
comment|// compute the stats
name|accumulator
operator|.
name|compute
argument_list|()
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|areq
operator|.
name|getName
argument_list|()
argument_list|,
name|accumulator
operator|.
name|export
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|statsCollector
operator|.
name|endRequest
argument_list|()
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
