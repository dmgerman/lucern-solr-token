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
name|Locale
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|FacetParams
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
begin_comment
comment|/**  * Models a single field somewhere in a hierarchy of fields as part of a pivot facet.    * This pivot field contains {@link PivotFacetValue}s which may each contain a nested  * {@link PivotFacetField} child.  This<code>PivotFacetField</code> may itself   * be a child of a {@link PivotFacetValue} parent.  *  * @see PivotFacetValue  * @see PivotFacetFieldValueCollection  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|PivotFacetField
specifier|public
class|class
name|PivotFacetField
block|{
DECL|field|field
specifier|public
specifier|final
name|String
name|field
decl_stmt|;
comment|// null if this is a top level pivot,
comment|// otherwise the value of the parent pivot we are nested under
DECL|field|parentValue
specifier|public
specifier|final
name|PivotFacetValue
name|parentValue
decl_stmt|;
DECL|field|valueCollection
specifier|public
specifier|final
name|PivotFacetFieldValueCollection
name|valueCollection
decl_stmt|;
comment|// Facet parameters relating to this field
DECL|field|facetFieldLimit
specifier|private
specifier|final
name|int
name|facetFieldLimit
decl_stmt|;
DECL|field|facetFieldMinimumCount
specifier|private
specifier|final
name|int
name|facetFieldMinimumCount
decl_stmt|;
DECL|field|facetFieldOffset
specifier|private
specifier|final
name|int
name|facetFieldOffset
decl_stmt|;
DECL|field|facetFieldSort
specifier|private
specifier|final
name|String
name|facetFieldSort
decl_stmt|;
DECL|field|numberOfValuesContributedByShard
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|numberOfValuesContributedByShard
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|shardLowestCount
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|shardLowestCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|needRefinementAtThisLevel
specifier|private
name|boolean
name|needRefinementAtThisLevel
init|=
literal|true
decl_stmt|;
DECL|method|PivotFacetField
specifier|private
name|PivotFacetField
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|PivotFacetValue
name|parent
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|field
operator|=
name|fieldName
expr_stmt|;
name|parentValue
operator|=
name|parent
expr_stmt|;
comment|// facet params
name|SolrParams
name|parameters
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|facetFieldMinimumCount
operator|=
name|parameters
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|FacetParams
operator|.
name|FACET_PIVOT_MINCOUNT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|facetFieldOffset
operator|=
name|parameters
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|FacetParams
operator|.
name|FACET_OFFSET
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|facetFieldLimit
operator|=
name|parameters
operator|.
name|getFieldInt
argument_list|(
name|field
argument_list|,
name|FacetParams
operator|.
name|FACET_LIMIT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|String
name|defaultSort
init|=
operator|(
name|facetFieldLimit
operator|>
literal|0
operator|)
condition|?
name|FacetParams
operator|.
name|FACET_SORT_COUNT
else|:
name|FacetParams
operator|.
name|FACET_SORT_INDEX
decl_stmt|;
name|facetFieldSort
operator|=
name|parameters
operator|.
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|FacetParams
operator|.
name|FACET_SORT
argument_list|,
name|defaultSort
argument_list|)
expr_stmt|;
name|valueCollection
operator|=
operator|new
name|PivotFacetFieldValueCollection
argument_list|(
name|facetFieldMinimumCount
argument_list|,
name|facetFieldOffset
argument_list|,
name|facetFieldLimit
argument_list|,
name|facetFieldSort
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|facetFieldLimit
operator|<
literal|0
operator|)
operator|||
comment|// TODO: possible refinement issue if limit=0& mincount=0& missing=true
comment|// (ie: we only want the missing count for this field)
operator|(
name|facetFieldLimit
operator|<=
literal|0
operator|&&
name|facetFieldMinimumCount
operator|==
literal|0
operator|)
operator|||
operator|(
name|facetFieldSort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_INDEX
argument_list|)
operator|&&
name|facetFieldMinimumCount
operator|<=
literal|0
operator|)
condition|)
block|{
comment|// in any of these cases, there's no need to refine this level of the pivot
name|needRefinementAtThisLevel
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**     * A recursive method that walks up the tree of pivot fields/values to build     * a list of String representations of the values that lead down to this     * PivotFacetField.    *    * @return A mutable List of the pivot values leading down to this pivot field,     *      will never be null but may contain nulls and may be empty if this is a top     *      level pivot field    * @see PivotFacetValue#getValuePath    */
DECL|method|getValuePath
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getValuePath
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|parentValue
condition|)
block|{
return|return
name|parentValue
operator|.
name|getValuePath
argument_list|()
return|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|3
argument_list|)
return|;
block|}
comment|/**    * A recursive method to construct a new<code>PivotFacetField</code> object from     * the contents of the {@link NamedList}s provided by the specified shard, relative     * to a parent value (if this is not the top field in the pivot hierarchy)    *    * The associated child {@link PivotFacetValue}s will be recursively built as well.    *    * @see PivotFacetValue#createFromNamedList    * @param shardNumber the id of the shard that provided this data    * @param rb The response builder of the current request    * @param owner the parent value in the current pivot (may be null)    * @param pivotValues the data from the specified shard for this pivot field, may be null or empty    * @return the new PivotFacetField, null if pivotValues is null or empty.    */
DECL|method|createFromListOfNamedLists
specifier|public
specifier|static
name|PivotFacetField
name|createFromListOfNamedLists
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|PivotFacetValue
name|owner
parameter_list|,
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|pivotValues
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|pivotValues
operator|||
name|pivotValues
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|firstValue
init|=
name|pivotValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|PivotFacetField
name|createdPivotFacetField
init|=
operator|new
name|PivotFacetField
argument_list|(
name|rb
argument_list|,
name|owner
argument_list|,
name|PivotFacetHelper
operator|.
name|getField
argument_list|(
name|firstValue
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|lowestCount
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|pivotValue
range|:
name|pivotValues
control|)
block|{
name|lowestCount
operator|=
name|Math
operator|.
name|min
argument_list|(
name|lowestCount
argument_list|,
name|PivotFacetHelper
operator|.
name|getCount
argument_list|(
name|pivotValue
argument_list|)
argument_list|)
expr_stmt|;
name|PivotFacetValue
name|newValue
init|=
name|PivotFacetValue
operator|.
name|createFromNamedList
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|createdPivotFacetField
argument_list|,
name|pivotValue
argument_list|)
decl_stmt|;
name|createdPivotFacetField
operator|.
name|valueCollection
operator|.
name|add
argument_list|(
name|newValue
argument_list|)
expr_stmt|;
block|}
name|createdPivotFacetField
operator|.
name|shardLowestCount
operator|.
name|put
argument_list|(
name|shardNumber
argument_list|,
name|lowestCount
argument_list|)
expr_stmt|;
name|createdPivotFacetField
operator|.
name|numberOfValuesContributedByShard
operator|.
name|put
argument_list|(
name|shardNumber
argument_list|,
name|pivotValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|createdPivotFacetField
return|;
block|}
comment|/**    * Destructive method that recursively prunes values from the data structure     * based on the counts for those values and the effective sort, mincount, limit,     * and offset being used for each field.    *<p>    * This method should only be called after all refinement is completed just prior     * calling {@link #convertToListOfNamedLists}    *</p>    *    * @see PivotFacet#getTrimmedPivotsAsListOfNamedLists    * @see PivotFacetFieldValueCollection#trim    */
DECL|method|trim
specifier|public
name|void
name|trim
parameter_list|()
block|{
comment|// SOLR-6331...
comment|//
comment|// we can probably optimize the memory usage by trimming each level of the pivot once
comment|// we know we've fully refined the values at that level
comment|// (ie: fold this logic into refineNextLevelOfFacets)
name|this
operator|.
name|valueCollection
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
comment|/**    * Recursively sorts the collection of values associated with this field, and     * any sub-pivots those values have.    *    * @see FacetParams#FACET_SORT    * @see PivotFacetFieldValueCollection#sort    */
DECL|method|sort
specifier|public
name|void
name|sort
parameter_list|()
block|{
name|this
operator|.
name|valueCollection
operator|.
name|sort
argument_list|()
expr_stmt|;
block|}
comment|/**     * A recursive method for generating<code>NamedLists</code> from this field     * suitable for including in a pivot facet response to the original distributed request.    */
DECL|method|convertToListOfNamedLists
specifier|public
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|convertToListOfNamedLists
parameter_list|()
block|{
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|convertedPivotList
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|valueCollection
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|convertedPivotList
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|PivotFacetValue
name|pivot
range|:
name|valueCollection
control|)
name|convertedPivotList
operator|.
name|add
argument_list|(
name|pivot
operator|.
name|convertToNamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|convertedPivotList
return|;
block|}
comment|/**     * A recursive method for determining which {@link PivotFacetValue}s need to be    * refined for this pivot.    *    * @see PivotFacet#queuePivotRefinementRequests    */
DECL|method|queuePivotRefinementRequests
specifier|public
name|void
name|queuePivotRefinementRequests
parameter_list|(
name|PivotFacet
name|pf
parameter_list|)
block|{
if|if
condition|(
name|needRefinementAtThisLevel
operator|&&
operator|!
name|valueCollection
operator|.
name|getExplicitValuesList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT
operator|.
name|equals
argument_list|(
name|facetFieldSort
argument_list|)
condition|)
block|{
comment|// we only need to things that are currently in our limit,
comment|// or might be in our limit if we get increased counts from shards that
comment|// didn't include this value the first time
specifier|final
name|int
name|indexOfCountThreshold
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCollection
operator|.
name|getExplicitValuesListSize
argument_list|()
argument_list|,
name|facetFieldOffset
operator|+
name|facetFieldLimit
argument_list|)
operator|-
literal|1
decl_stmt|;
specifier|final
name|int
name|countThreshold
init|=
name|valueCollection
operator|.
name|getAt
argument_list|(
name|indexOfCountThreshold
argument_list|)
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|int
name|positionInResults
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PivotFacetValue
name|value
range|:
name|valueCollection
operator|.
name|getExplicitValuesList
argument_list|()
control|)
block|{
if|if
condition|(
name|positionInResults
operator|<=
name|indexOfCountThreshold
condition|)
block|{
comment|// This element is within the top results, so we need to get information
comment|// from all of the shards.
name|processDefiniteCandidateElement
argument_list|(
name|pf
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This element is not within the top results, but may still need to be refined.
name|processPossibleCandidateElement
argument_list|(
name|pf
argument_list|,
name|value
argument_list|,
name|countThreshold
argument_list|)
expr_stmt|;
block|}
name|positionInResults
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// FACET_SORT_INDEX
comment|// everything needs refined to see what the per-shard mincount excluded
for|for
control|(
name|PivotFacetValue
name|value
range|:
name|valueCollection
operator|.
name|getExplicitValuesList
argument_list|()
control|)
block|{
name|processDefiniteCandidateElement
argument_list|(
name|pf
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|needRefinementAtThisLevel
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|pf
operator|.
name|isRefinementsRequired
argument_list|()
condition|)
block|{
comment|// if any refinements are needed, then we need to stop and wait to
comment|// see how the picture may change before drilling down to child pivot fields
return|return;
block|}
else|else
block|{
comment|// Since outstanding requests have been filled, then we can drill down
comment|// to the next deeper level and check it.
name|refineNextLevelOfFacets
argument_list|(
name|pf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Adds refinement requests for the value for each shard that has not already contributed     * a count for this value.    */
DECL|method|processDefiniteCandidateElement
specifier|private
name|void
name|processDefiniteCandidateElement
parameter_list|(
name|PivotFacet
name|pf
parameter_list|,
name|PivotFacetValue
name|value
parameter_list|)
block|{
for|for
control|(
name|int
name|shard
init|=
name|pf
operator|.
name|knownShards
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
literal|0
operator|<=
name|shard
condition|;
name|shard
operator|=
name|pf
operator|.
name|knownShards
operator|.
name|nextSetBit
argument_list|(
name|shard
operator|+
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|value
operator|.
name|shardHasContributed
argument_list|(
name|shard
argument_list|)
condition|)
block|{
if|if
condition|(
comment|// if we're doing index order, we need to refine anything
comment|// (mincount may have excluded from a shard)
name|FacetParams
operator|.
name|FACET_SORT_INDEX
operator|.
name|equals
argument_list|(
name|facetFieldSort
argument_list|)
comment|// if we are doing count order, we need to refine if the limit was hit
comment|// (if it not, the shard doesn't have the value or it would have returned already)
operator|||
name|numberOfValuesContributedByShardWasLimitedByFacetFieldLimit
argument_list|(
name|shard
argument_list|)
condition|)
block|{
name|pf
operator|.
name|addRefinement
argument_list|(
name|shard
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|numberOfValuesContributedByShardWasLimitedByFacetFieldLimit
specifier|private
name|boolean
name|numberOfValuesContributedByShardWasLimitedByFacetFieldLimit
parameter_list|(
name|int
name|shardNumber
parameter_list|)
block|{
return|return
name|facetFieldLimit
operator|<=
name|numberOfValuesContributedByShard
argument_list|(
name|shardNumber
argument_list|)
return|;
block|}
DECL|method|numberOfValuesContributedByShard
specifier|private
name|int
name|numberOfValuesContributedByShard
parameter_list|(
specifier|final
name|int
name|shardNumber
parameter_list|)
block|{
return|return
name|numberOfValuesContributedByShard
operator|.
name|containsKey
argument_list|(
name|shardNumber
argument_list|)
condition|?
name|numberOfValuesContributedByShard
operator|.
name|get
argument_list|(
name|shardNumber
argument_list|)
else|:
literal|0
return|;
block|}
comment|/**     * Checks the {@link #lowestCountContributedbyShard} for each shard, combined with the     * counts we already know, to see if this value is a viable candidate --     *<b>Does not make sense when using {@link FacetParams#FACET_SORT_INDEX}</b>    *    * @see #processDefiniteCandidateElement    */
DECL|method|processPossibleCandidateElement
specifier|private
name|void
name|processPossibleCandidateElement
parameter_list|(
name|PivotFacet
name|pf
parameter_list|,
name|PivotFacetValue
name|value
parameter_list|,
specifier|final
name|int
name|refinementThreshold
parameter_list|)
block|{
assert|assert
name|FacetParams
operator|.
name|FACET_SORT_COUNT
operator|.
name|equals
argument_list|(
name|facetFieldSort
argument_list|)
operator|:
literal|"Method only makes sense when sorting by count"
assert|;
name|int
name|maxPossibleCountAfterRefinement
init|=
name|value
operator|.
name|getCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|shard
init|=
name|pf
operator|.
name|knownShards
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
literal|0
operator|<=
name|shard
condition|;
name|shard
operator|=
name|pf
operator|.
name|knownShards
operator|.
name|nextSetBit
argument_list|(
name|shard
operator|+
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|value
operator|.
name|shardHasContributed
argument_list|(
name|shard
argument_list|)
condition|)
block|{
name|maxPossibleCountAfterRefinement
operator|+=
name|lowestCountContributedbyShard
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|refinementThreshold
operator|<=
name|maxPossibleCountAfterRefinement
condition|)
block|{
name|processDefiniteCandidateElement
argument_list|(
name|pf
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|lowestCountContributedbyShard
specifier|private
name|int
name|lowestCountContributedbyShard
parameter_list|(
name|int
name|shardNumber
parameter_list|)
block|{
return|return
operator|(
name|shardLowestCount
operator|.
name|containsKey
argument_list|(
name|shardNumber
argument_list|)
operator|)
condition|?
name|shardLowestCount
operator|.
name|get
argument_list|(
name|shardNumber
argument_list|)
else|:
literal|0
return|;
block|}
DECL|method|refineNextLevelOfFacets
specifier|private
name|void
name|refineNextLevelOfFacets
parameter_list|(
name|PivotFacet
name|pf
parameter_list|)
block|{
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
name|explicitValsToRefine
init|=
name|valueCollection
operator|.
name|getNextLevelValuesToRefine
argument_list|()
decl_stmt|;
for|for
control|(
name|PivotFacetValue
name|value
range|:
name|explicitValsToRefine
control|)
block|{
if|if
condition|(
literal|null
operator|!=
name|value
operator|.
name|getChildPivot
argument_list|()
condition|)
block|{
name|value
operator|.
name|getChildPivot
argument_list|()
operator|.
name|queuePivotRefinementRequests
argument_list|(
name|pf
argument_list|)
expr_stmt|;
block|}
block|}
name|PivotFacetValue
name|missing
init|=
name|this
operator|.
name|valueCollection
operator|.
name|getMissingValue
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|missing
operator|&&
literal|null
operator|!=
name|missing
operator|.
name|getChildPivot
argument_list|()
condition|)
block|{
name|missing
operator|.
name|getChildPivot
argument_list|()
operator|.
name|queuePivotRefinementRequests
argument_list|(
name|pf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|incrementShardValueCount
specifier|private
name|void
name|incrementShardValueCount
parameter_list|(
name|int
name|shardNumber
parameter_list|)
block|{
if|if
condition|(
operator|!
name|numberOfValuesContributedByShard
operator|.
name|containsKey
argument_list|(
name|shardNumber
argument_list|)
condition|)
block|{
name|numberOfValuesContributedByShard
operator|.
name|put
argument_list|(
name|shardNumber
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|numberOfValuesContributedByShard
operator|.
name|put
argument_list|(
name|shardNumber
argument_list|,
name|numberOfValuesContributedByShard
operator|.
name|get
argument_list|(
name|shardNumber
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|contributeValueFromShard
specifier|private
name|void
name|contributeValueFromShard
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|shardValue
parameter_list|)
block|{
name|incrementShardValueCount
argument_list|(
name|shardNumber
argument_list|)
expr_stmt|;
name|Comparable
name|value
init|=
name|PivotFacetHelper
operator|.
name|getValue
argument_list|(
name|shardValue
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|PivotFacetHelper
operator|.
name|getCount
argument_list|(
name|shardValue
argument_list|)
decl_stmt|;
comment|// We're changing values so we most mark the collection as dirty
name|valueCollection
operator|.
name|markDirty
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
operator|!
name|shardLowestCount
operator|.
name|containsKey
argument_list|(
name|shardNumber
argument_list|)
operator|)
operator|||
name|shardLowestCount
operator|.
name|get
argument_list|(
name|shardNumber
argument_list|)
operator|>
name|count
condition|)
block|{
name|shardLowestCount
operator|.
name|put
argument_list|(
name|shardNumber
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|PivotFacetValue
name|facetValue
init|=
name|valueCollection
operator|.
name|get
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|facetValue
condition|)
block|{
comment|// never seen before, we need to create it from scratch
name|facetValue
operator|=
name|PivotFacetValue
operator|.
name|createFromNamedList
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|this
argument_list|,
name|shardValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueCollection
operator|.
name|add
argument_list|(
name|facetValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|facetValue
operator|.
name|mergeContributionFromShard
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|shardValue
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Recursively merges the contributions from the specified shard for each     * {@link PivotFacetValue} represended in the<code>response</code>.    *     * @see PivotFacetValue#mergeContributionFromShard    * @param shardNumber the id of the shard that provided this data    * @param rb The response builder of the current request    * @param response the data from the specified shard for this pivot field, may be null    */
DECL|method|contributeFromShard
specifier|public
name|void
name|contributeFromShard
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|response
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|response
condition|)
return|return;
for|for
control|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|responseValue
range|:
name|response
control|)
block|{
name|contributeValueFromShard
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|responseValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"P:%s F:%s V:%s"
argument_list|,
name|parentValue
argument_list|,
name|field
argument_list|,
name|valueCollection
argument_list|)
return|;
block|}
block|}
end_class
end_unit
