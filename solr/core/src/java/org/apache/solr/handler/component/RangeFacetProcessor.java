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
name|Set
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|Query
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
name|FacetParams
operator|.
name|FacetRangeMethod
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
name|FacetParams
operator|.
name|FacetRangeOther
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
name|IntervalFacets
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
name|SimpleFacets
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
name|schema
operator|.
name|TrieField
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
name|SyntaxError
import|;
end_import
begin_comment
comment|/**  * Processor for Range Facets  */
end_comment
begin_class
DECL|class|RangeFacetProcessor
specifier|public
class|class
name|RangeFacetProcessor
extends|extends
name|SimpleFacets
block|{
DECL|field|log
specifier|private
specifier|final
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RangeFacetProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|RangeFacetProcessor
specifier|public
name|RangeFacetProcessor
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
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|super
argument_list|(
name|req
argument_list|,
name|docs
argument_list|,
name|params
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a list of value constraints and the associated facet    * counts for each facet numerical field, range, and interval    * specified in the SolrParams    *    * @see org.apache.solr.common.params.FacetParams#FACET_RANGE    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getFacetRangeCounts
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getFacetRangeCounts
parameter_list|()
throws|throws
name|IOException
throws|,
name|SyntaxError
block|{
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resOuter
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RangeFacetRequest
argument_list|>
name|rangeFacetRequests
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
try|try
block|{
name|FacetComponent
operator|.
name|FacetContext
name|facetContext
init|=
name|FacetComponent
operator|.
name|FacetContext
operator|.
name|getFacetContext
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|rangeFacetRequests
operator|=
name|facetContext
operator|.
name|getAllRangeFacetRequests
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to compute facet ranges, facet context is not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|rangeFacetRequests
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|resOuter
return|;
for|for
control|(
name|RangeFacetRequest
name|rangeFacetRequest
range|:
name|rangeFacetRequests
control|)
block|{
name|getFacetRangeCounts
argument_list|(
name|rangeFacetRequest
argument_list|,
name|resOuter
argument_list|)
expr_stmt|;
block|}
return|return
name|resOuter
return|;
block|}
comment|/**    * Returns a list of value constraints and the associated facet counts    * for each facet range specified by the given {@link RangeFacetRequest}    */
DECL|method|getFacetRangeCounts
specifier|public
name|void
name|getFacetRangeCounts
parameter_list|(
name|RangeFacetRequest
name|rangeFacetRequest
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resOuter
parameter_list|)
throws|throws
name|IOException
throws|,
name|SyntaxError
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
specifier|final
name|String
name|key
init|=
name|rangeFacetRequest
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|f
init|=
name|rangeFacetRequest
operator|.
name|facetOn
decl_stmt|;
name|FacetRangeMethod
name|method
init|=
name|rangeFacetRequest
operator|.
name|getMethod
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
name|f
argument_list|)
decl_stmt|;
specifier|final
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
name|FacetRangeMethod
operator|.
name|DV
argument_list|)
condition|)
block|{
assert|assert
name|ft
operator|instanceof
name|TrieField
assert|;
name|resOuter
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|getFacetRangeCountsDocValues
argument_list|(
name|rangeFacetRequest
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resOuter
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|getFacetRangeCounts
argument_list|(
name|rangeFacetRequest
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFacetRangeCounts
specifier|private
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|NamedList
name|getFacetRangeCounts
parameter_list|(
specifier|final
name|RangeFacetRequest
name|rfr
parameter_list|)
throws|throws
name|IOException
throws|,
name|SyntaxError
block|{
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"counts"
argument_list|,
name|counts
argument_list|)
expr_stmt|;
comment|// explicitly return the gap.
name|res
operator|.
name|add
argument_list|(
literal|"gap"
argument_list|,
name|rfr
operator|.
name|getGapObj
argument_list|()
argument_list|)
expr_stmt|;
name|DocSet
name|docSet
init|=
name|computeDocSet
argument_list|(
name|docsOrig
argument_list|,
name|rfr
operator|.
name|getExcludeTags
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RangeFacetRequest
operator|.
name|FacetRange
name|range
range|:
name|rfr
operator|.
name|getFacetRanges
argument_list|()
control|)
block|{
if|if
condition|(
name|range
operator|.
name|other
operator|!=
literal|null
condition|)
block|{
comment|// these are added to top-level NamedList
comment|// and we always include them regardless of mincount
name|res
operator|.
name|add
argument_list|(
name|range
operator|.
name|other
operator|.
name|toString
argument_list|()
argument_list|,
name|rangeCount
argument_list|(
name|docSet
argument_list|,
name|rfr
argument_list|,
name|range
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|count
init|=
name|rangeCount
argument_list|(
name|docSet
argument_list|,
name|rfr
argument_list|,
name|range
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|rfr
operator|.
name|getMinCount
argument_list|()
condition|)
block|{
name|counts
operator|.
name|add
argument_list|(
name|range
operator|.
name|lower
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// explicitly return the start and end so all the counts
comment|// (including before/after/between) are meaningful - even if mincount
comment|// has removed the neighboring ranges
name|res
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|rfr
operator|.
name|getStartObj
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
name|rfr
operator|.
name|getEndObj
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
DECL|method|getFacetRangeCountsDocValues
specifier|private
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getFacetRangeCountsDocValues
parameter_list|(
name|RangeFacetRequest
name|rfr
parameter_list|)
throws|throws
name|IOException
throws|,
name|SyntaxError
block|{
name|SchemaField
name|sf
init|=
name|rfr
operator|.
name|getSchemaField
argument_list|()
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"counts"
argument_list|,
name|counts
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|IntervalFacets
operator|.
name|FacetInterval
argument_list|>
name|intervals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// explicitly return the gap.  compute this early so we are more
comment|// likely to catch parse errors before attempting math
name|res
operator|.
name|add
argument_list|(
literal|"gap"
argument_list|,
name|rfr
operator|.
name|getGapObj
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|minCount
init|=
name|rfr
operator|.
name|getMinCount
argument_list|()
decl_stmt|;
name|boolean
name|includeBefore
init|=
literal|false
decl_stmt|;
name|boolean
name|includeBetween
init|=
literal|false
decl_stmt|;
name|boolean
name|includeAfter
init|=
literal|false
decl_stmt|;
name|Set
argument_list|<
name|FacetRangeOther
argument_list|>
name|others
init|=
name|rfr
operator|.
name|getOthers
argument_list|()
decl_stmt|;
comment|// Intervals must be in order (see IntervalFacets.getSortedIntervals), if "BEFORE" or
comment|// "BETWEEN" are set, they must be added first
comment|// no matter what other values are listed, we don't do
comment|// anything if "none" is specified.
if|if
condition|(
operator|!
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|NONE
argument_list|)
condition|)
block|{
if|if
condition|(
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|ALL
argument_list|)
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|BEFORE
argument_list|)
condition|)
block|{
comment|// We'll add an interval later in this position
name|intervals
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|includeBefore
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|ALL
argument_list|)
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|BETWEEN
argument_list|)
condition|)
block|{
comment|// We'll add an interval later in this position
name|intervals
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|includeBetween
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|ALL
argument_list|)
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|AFTER
argument_list|)
condition|)
block|{
name|includeAfter
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|IntervalFacets
operator|.
name|FacetInterval
name|after
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RangeFacetRequest
operator|.
name|FacetRange
name|range
range|:
name|rfr
operator|.
name|getFacetRanges
argument_list|()
control|)
block|{
try|try
block|{
name|FacetRangeOther
name|other
init|=
name|FacetRangeOther
operator|.
name|get
argument_list|(
name|range
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|other
condition|)
block|{
case|case
name|BEFORE
case|:
assert|assert
name|range
operator|.
name|lower
operator|==
literal|null
assert|;
name|intervals
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|new
name|IntervalFacets
operator|.
name|FacetInterval
argument_list|(
name|sf
argument_list|,
literal|"*"
argument_list|,
name|range
operator|.
name|upper
argument_list|,
name|range
operator|.
name|includeLower
argument_list|,
name|range
operator|.
name|includeUpper
argument_list|,
name|FacetRangeOther
operator|.
name|BEFORE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|AFTER
case|:
assert|assert
name|range
operator|.
name|upper
operator|==
literal|null
assert|;
name|after
operator|=
operator|new
name|IntervalFacets
operator|.
name|FacetInterval
argument_list|(
name|sf
argument_list|,
name|range
operator|.
name|lower
argument_list|,
literal|"*"
argument_list|,
name|range
operator|.
name|includeLower
argument_list|,
name|range
operator|.
name|includeUpper
argument_list|,
name|FacetRangeOther
operator|.
name|AFTER
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|BETWEEN
case|:
name|intervals
operator|.
name|set
argument_list|(
name|includeBefore
condition|?
literal|1
else|:
literal|0
argument_list|,
operator|new
name|IntervalFacets
operator|.
name|FacetInterval
argument_list|(
name|sf
argument_list|,
name|range
operator|.
name|lower
argument_list|,
name|range
operator|.
name|upper
argument_list|,
name|range
operator|.
name|includeLower
argument_list|,
name|range
operator|.
name|includeUpper
argument_list|,
name|FacetRangeOther
operator|.
name|BETWEEN
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
continue|continue;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// safe to ignore
block|}
name|intervals
operator|.
name|add
argument_list|(
operator|new
name|IntervalFacets
operator|.
name|FacetInterval
argument_list|(
name|sf
argument_list|,
name|range
operator|.
name|lower
argument_list|,
name|range
operator|.
name|upper
argument_list|,
name|range
operator|.
name|includeLower
argument_list|,
name|range
operator|.
name|includeUpper
argument_list|,
name|range
operator|.
name|lower
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeAfter
condition|)
block|{
assert|assert
name|after
operator|!=
literal|null
assert|;
name|intervals
operator|.
name|add
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
name|IntervalFacets
operator|.
name|FacetInterval
index|[]
name|intervalsArray
init|=
name|intervals
operator|.
name|toArray
argument_list|(
operator|new
name|IntervalFacets
operator|.
name|FacetInterval
index|[
name|intervals
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// don't use the ArrayList anymore
name|intervals
operator|=
literal|null
expr_stmt|;
operator|new
name|IntervalFacets
argument_list|(
name|sf
argument_list|,
name|searcher
argument_list|,
name|computeDocSet
argument_list|(
name|docsOrig
argument_list|,
name|rfr
operator|.
name|getExcludeTags
argument_list|()
argument_list|)
argument_list|,
name|intervalsArray
argument_list|)
expr_stmt|;
name|int
name|intervalIndex
init|=
literal|0
decl_stmt|;
name|int
name|lastIntervalIndex
init|=
name|intervalsArray
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// if the user requested "BEFORE", it will be the first of the intervals. Needs to be added to the
comment|// response named list instead of with the counts
if|if
condition|(
name|includeBefore
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
name|intervalsArray
index|[
name|intervalIndex
index|]
operator|.
name|getKey
argument_list|()
argument_list|,
name|intervalsArray
index|[
name|intervalIndex
index|]
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|intervalIndex
operator|++
expr_stmt|;
block|}
comment|// if the user requested "BETWEEN", it will be the first or second of the intervals (depending on if
comment|// "BEFORE" was also requested). Needs to be added to the response named list instead of with the counts
if|if
condition|(
name|includeBetween
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
name|intervalsArray
index|[
name|intervalIndex
index|]
operator|.
name|getKey
argument_list|()
argument_list|,
name|intervalsArray
index|[
name|intervalIndex
index|]
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|intervalIndex
operator|++
expr_stmt|;
block|}
comment|// if the user requested "AFTER", it will be the last of the intervals.
comment|// Needs to be added to the response named list instead of with the counts
if|if
condition|(
name|includeAfter
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
name|intervalsArray
index|[
name|lastIntervalIndex
index|]
operator|.
name|getKey
argument_list|()
argument_list|,
name|intervalsArray
index|[
name|lastIntervalIndex
index|]
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|lastIntervalIndex
operator|--
expr_stmt|;
block|}
comment|// now add all other intervals to the counts NL
while|while
condition|(
name|intervalIndex
operator|<=
name|lastIntervalIndex
condition|)
block|{
name|IntervalFacets
operator|.
name|FacetInterval
name|interval
init|=
name|intervalsArray
index|[
name|intervalIndex
index|]
decl_stmt|;
if|if
condition|(
name|interval
operator|.
name|getCount
argument_list|()
operator|>=
name|minCount
condition|)
block|{
name|counts
operator|.
name|add
argument_list|(
name|interval
operator|.
name|getKey
argument_list|()
argument_list|,
name|interval
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|intervalIndex
operator|++
expr_stmt|;
block|}
name|res
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
name|rfr
operator|.
name|getStartObj
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"end"
argument_list|,
name|rfr
operator|.
name|getEndObj
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * Macro for getting the numDocs of range over docs    *    * @see org.apache.solr.search.SolrIndexSearcher#numDocs    * @see org.apache.lucene.search.TermRangeQuery    */
DECL|method|rangeCount
specifier|protected
name|int
name|rangeCount
parameter_list|(
name|DocSet
name|subset
parameter_list|,
name|RangeFacetRequest
name|rfr
parameter_list|,
name|RangeFacetRequest
operator|.
name|FacetRange
name|fr
parameter_list|)
throws|throws
name|IOException
throws|,
name|SyntaxError
block|{
name|SchemaField
name|schemaField
init|=
name|rfr
operator|.
name|getSchemaField
argument_list|()
decl_stmt|;
name|Query
name|rangeQ
init|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
literal|null
argument_list|,
name|schemaField
argument_list|,
name|fr
operator|.
name|lower
argument_list|,
name|fr
operator|.
name|upper
argument_list|,
name|fr
operator|.
name|includeLower
argument_list|,
name|fr
operator|.
name|includeUpper
argument_list|)
decl_stmt|;
if|if
condition|(
name|rfr
operator|.
name|isGroupFacet
argument_list|()
condition|)
block|{
return|return
name|getGroupedFacetQueryCount
argument_list|(
name|rangeQ
argument_list|,
name|subset
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|searcher
operator|.
name|numDocs
argument_list|(
name|rangeQ
argument_list|,
name|subset
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit