begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
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
name|params
operator|.
name|ShardParams
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
name|params
operator|.
name|StatsParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|request
operator|.
name|UnInvertedField
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|DocIterator
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
begin_comment
comment|/**  * Stats component calculates simple statistics on numeric field values  * @since solr 1.4  */
end_comment
begin_class
DECL|class|StatsComponent
specifier|public
class|class
name|StatsComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"stats"
decl_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|StatsParams
operator|.
name|STATS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|rb
operator|.
name|setNeedDocSet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|rb
operator|.
name|doStats
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|rb
operator|.
name|doStats
condition|)
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SimpleStats
name|s
init|=
operator|new
name|SimpleStats
argument_list|(
name|rb
operator|.
name|req
argument_list|,
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docSet
argument_list|,
name|params
argument_list|)
decl_stmt|;
comment|// TODO ???? add this directly to the response, or to the builder?
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"stats"
argument_list|,
name|s
operator|.
name|getStatsCounts
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|distributedProcess
specifier|public
name|int
name|distributedProcess
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ResponseBuilder
operator|.
name|STAGE_DONE
return|;
block|}
annotation|@
name|Override
DECL|method|modifyRequest
specifier|public
name|void
name|modifyRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SearchComponent
name|who
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doStats
condition|)
return|return;
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_TOP_IDS
operator|)
operator|!=
literal|0
condition|)
block|{
name|sreq
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_GET_STATS
expr_stmt|;
name|StatsInfo
name|si
init|=
name|rb
operator|.
name|_statsInfo
decl_stmt|;
if|if
condition|(
name|si
operator|==
literal|null
condition|)
block|{
name|rb
operator|.
name|_statsInfo
operator|=
name|si
operator|=
operator|new
name|StatsInfo
argument_list|()
expr_stmt|;
name|si
operator|.
name|parse
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
name|rb
argument_list|)
expr_stmt|;
comment|// should already be true...
comment|// sreq.params.set(StatsParams.STATS, "true");
block|}
block|}
else|else
block|{
comment|// turn off stats on other requests
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|StatsParams
operator|.
name|STATS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// we could optionally remove stats params
block|}
block|}
annotation|@
name|Override
DECL|method|handleResponses
specifier|public
name|void
name|handleResponses
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doStats
operator|||
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_STATS
operator|)
operator|==
literal|0
condition|)
return|return;
name|StatsInfo
name|si
init|=
name|rb
operator|.
name|_statsInfo
decl_stmt|;
for|for
control|(
name|ShardResponse
name|srsp
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|NamedList
name|stats
init|=
operator|(
name|NamedList
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
name|NamedList
name|stats_fields
init|=
operator|(
name|NamedList
operator|)
name|stats
operator|.
name|get
argument_list|(
literal|"stats_fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats_fields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stats_fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
name|stats_fields
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|StatsValues
name|stv
init|=
name|si
operator|.
name|statsFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|NamedList
name|shardStv
init|=
operator|(
name|NamedList
operator|)
name|stats_fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|stv
operator|.
name|accumulate
argument_list|(
name|shardStv
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doStats
operator|||
name|rb
operator|.
name|stage
operator|!=
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
return|return;
comment|// wait until STAGE_GET_FIELDS
comment|// so that "result" is already stored in the response (for aesthetics)
name|StatsInfo
name|si
init|=
name|rb
operator|.
name|_statsInfo
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|stats
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|stats_fields
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|stats
operator|.
name|add
argument_list|(
literal|"stats_fields"
argument_list|,
name|stats_fields
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|si
operator|.
name|statsFields
operator|.
name|keySet
argument_list|()
control|)
block|{
name|NamedList
name|stv
init|=
name|si
operator|.
name|statsFields
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|getStatsValues
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|Long
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|stats_fields
operator|.
name|add
argument_list|(
name|field
argument_list|,
name|stv
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stats_fields
operator|.
name|add
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"stats"
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|rb
operator|.
name|_statsInfo
operator|=
literal|null
expr_stmt|;
block|}
comment|/////////////////////////////////////////////
comment|///  SolrInfoMBean
comment|////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Calculate Statistics"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
begin_class
DECL|class|StatsInfo
class|class
name|StatsInfo
block|{
DECL|field|statsFields
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|statsFields
decl_stmt|;
DECL|method|parse
name|void
name|parse
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|statsFields
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|()
expr_stmt|;
name|String
index|[]
name|statsFs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|StatsParams
operator|.
name|STATS_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsFs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|statsFs
control|)
block|{
name|SchemaField
name|sf
init|=
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|statsFields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|sf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
begin_class
DECL|class|SimpleStats
class|class
name|SimpleStats
block|{
comment|/** The main set of documents */
DECL|field|docs
specifier|protected
name|DocSet
name|docs
decl_stmt|;
comment|/** Configuration params behavior should be driven by */
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
comment|/** Searcher to use for all calculations */
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
DECL|method|SimpleStats
specifier|public
name|SimpleStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|SolrParams
name|params
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
block|}
DECL|method|getStatsCounts
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getStatsCounts
parameter_list|()
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"stats_fields"
argument_list|,
name|getStatsFields
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
DECL|method|getStatsFields
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getStatsFields
parameter_list|()
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|statsFs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|StatsParams
operator|.
name|STATS_FIELD
argument_list|)
decl_stmt|;
name|boolean
name|isShard
init|=
name|params
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|statsFs
condition|)
block|{
specifier|final
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|statsFs
control|)
block|{
name|String
index|[]
name|facets
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|f
argument_list|,
name|StatsParams
operator|.
name|STATS_FACET
argument_list|)
decl_stmt|;
if|if
condition|(
name|facets
operator|==
literal|null
condition|)
block|{
name|facets
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
comment|// make sure it is something...
block|}
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|?
argument_list|>
name|stv
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
operator|||
name|ft
operator|.
name|multiValuedFieldCache
argument_list|()
condition|)
block|{
comment|//use UnInvertedField for multivalued fields
name|UnInvertedField
name|uif
init|=
name|UnInvertedField
operator|.
name|getUnInvertedField
argument_list|(
name|f
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|stv
operator|=
name|uif
operator|.
name|getStats
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|facets
argument_list|)
operator|.
name|getStatsValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stv
operator|=
name|getFieldCacheStats
argument_list|(
name|f
argument_list|,
name|facets
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isShard
operator|==
literal|true
operator|||
operator|(
name|Long
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
operator|>
literal|0
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|stv
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|add
argument_list|(
name|f
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
DECL|method|getFieldCacheStats
specifier|public
name|NamedList
argument_list|<
name|?
argument_list|>
name|getFieldCacheStats
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
index|[]
name|facet
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
specifier|final
name|StatsValues
name|allstats
init|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|sf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FieldFacetStats
argument_list|>
name|facetStats
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldFacetStats
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|facetField
range|:
name|facet
control|)
block|{
name|SchemaField
name|fsf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|facetField
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsf
operator|.
name|multiValued
argument_list|()
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
literal|"Stats can only facet on single-valued fields, not: "
operator|+
name|facetField
argument_list|)
throw|;
block|}
name|facetStats
operator|.
name|add
argument_list|(
operator|new
name|FieldFacetStats
argument_list|(
name|searcher
argument_list|,
name|facetField
argument_list|,
name|sf
argument_list|,
name|fsf
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterator
argument_list|<
name|AtomicReaderContext
argument_list|>
name|ctxIt
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|AtomicReaderContext
name|ctx
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DocIterator
name|docsIt
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|docsIt
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|docsIt
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
operator|||
name|doc
operator|>=
name|ctx
operator|.
name|docBase
operator|+
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
comment|// advance
do|do
block|{
name|ctx
operator|=
name|ctxIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|ctx
operator|==
literal|null
operator|||
name|doc
operator|>=
name|ctx
operator|.
name|docBase
operator|+
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
do|;
assert|assert
name|doc
operator|>=
name|ctx
operator|.
name|docBase
assert|;
comment|// propagate the context among accumulators.
name|allstats
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldFacetStats
name|f
range|:
name|facetStats
control|)
block|{
name|f
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
comment|// accumulate
name|allstats
operator|.
name|accumulate
argument_list|(
name|doc
operator|-
name|ctx
operator|.
name|docBase
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldFacetStats
name|f
range|:
name|facetStats
control|)
block|{
name|f
operator|.
name|facet
argument_list|(
name|doc
operator|-
name|ctx
operator|.
name|docBase
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|FieldFacetStats
name|f
range|:
name|facetStats
control|)
block|{
name|allstats
operator|.
name|addFacet
argument_list|(
name|f
operator|.
name|name
argument_list|,
name|f
operator|.
name|facetStatsValues
argument_list|)
expr_stmt|;
block|}
return|return
name|allstats
operator|.
name|getStatsValues
argument_list|()
return|;
block|}
block|}
end_class
end_unit
