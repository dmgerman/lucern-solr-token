begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|Collections
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
name|Map
operator|.
name|Entry
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
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
operator|.
name|OrdinalPolicy
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
name|facet
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
operator|.
name|FacetArraysSource
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
operator|.
name|ResultMode
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
operator|.
name|SortOrder
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
name|facet
operator|.
name|search
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|ParallelTaxonomyArrays
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|IndexReader
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link FacetsAccumulator} suitable for accumulating categories that were  * indexed into a taxonomy index.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|TaxonomyFacetsAccumulator
specifier|public
class|class
name|TaxonomyFacetsAccumulator
extends|extends
name|FacetsAccumulator
block|{
DECL|field|taxonomyReader
specifier|public
specifier|final
name|TaxonomyReader
name|taxonomyReader
decl_stmt|;
DECL|field|indexReader
specifier|public
specifier|final
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|facetArrays
specifier|public
specifier|final
name|FacetArrays
name|facetArrays
decl_stmt|;
comment|/**    * Initializes the accumulator with the given search params, index reader and    * taxonomy reader. This constructor creates the default {@link FacetArrays},    * which do not support reuse. If you want to use {@link ReusingFacetArrays},    * you should use the    * {@link #TaxonomyFacetsAccumulator(FacetSearchParams, IndexReader, TaxonomyReader)}    * constructor.    */
DECL|method|TaxonomyFacetsAccumulator
specifier|public
name|TaxonomyFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|searchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
name|this
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the accumulator with the given parameters as well as    * {@link FacetArrays}. Note that the accumulator doesn't call    * {@link FacetArrays#free()}. If you require that (only makes sense if you    * use {@link ReusingFacetArrays}, you should do it after you've finished with    * the accumulator.    */
DECL|method|TaxonomyFacetsAccumulator
specifier|public
name|TaxonomyFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|searchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|super
argument_list|(
name|searchParams
argument_list|)
expr_stmt|;
name|this
operator|.
name|facetArrays
operator|=
name|facetArrays
operator|==
literal|null
condition|?
operator|new
name|FacetArrays
argument_list|(
name|taxonomyReader
operator|.
name|getSize
argument_list|()
argument_list|)
else|:
name|facetArrays
expr_stmt|;
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
name|this
operator|.
name|taxonomyReader
operator|=
name|taxonomyReader
expr_stmt|;
block|}
comment|/** Group all requests that belong to the same {@link CategoryListParams}. */
DECL|method|groupRequests
specifier|protected
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|List
argument_list|<
name|FacetRequest
argument_list|>
argument_list|>
name|groupRequests
parameter_list|()
block|{
if|if
condition|(
name|searchParams
operator|.
name|indexingParams
operator|.
name|getAllCategoryListParams
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
argument_list|,
name|searchParams
operator|.
name|facetRequests
argument_list|)
return|;
block|}
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|List
argument_list|<
name|FacetRequest
argument_list|>
argument_list|>
name|requestsPerCLP
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|List
argument_list|<
name|FacetRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|CategoryListParams
name|clp
init|=
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|requests
init|=
name|requestsPerCLP
operator|.
name|get
argument_list|(
name|clp
argument_list|)
decl_stmt|;
if|if
condition|(
name|requests
operator|==
literal|null
condition|)
block|{
name|requests
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
expr_stmt|;
name|requestsPerCLP
operator|.
name|put
argument_list|(
name|clp
argument_list|,
name|requests
argument_list|)
expr_stmt|;
block|}
name|requests
operator|.
name|add
argument_list|(
name|fr
argument_list|)
expr_stmt|;
block|}
return|return
name|requestsPerCLP
return|;
block|}
comment|/**    * Returns the {@link FacetsAggregator} to use for aggregating the categories    * found in the result documents.    */
DECL|method|getAggregator
specifier|public
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|List
argument_list|<
name|FacetRequest
argument_list|>
argument_list|>
name|requestsPerCLP
init|=
name|groupRequests
argument_list|()
decl_stmt|;
comment|// optimize for all-CountFacetRequest and single category list (common case)
if|if
condition|(
name|requestsPerCLP
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|boolean
name|allCount
init|=
literal|true
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|fr
operator|instanceof
name|CountFacetRequest
operator|)
condition|)
block|{
name|allCount
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|allCount
condition|)
block|{
return|return
name|requestsPerCLP
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|createFacetsAggregator
argument_list|(
name|searchParams
operator|.
name|indexingParams
argument_list|)
return|;
block|}
block|}
comment|// If we're here it means the facet requests are spread across multiple
comment|// category lists, or there are multiple types of facet requests, or both.
comment|// Therefore create a per-CategoryList mapping of FacetsAggregators.
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|FacetsAggregator
argument_list|>
name|perCLPAggregator
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|FacetsAggregator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|CategoryListParams
argument_list|,
name|List
argument_list|<
name|FacetRequest
argument_list|>
argument_list|>
name|e
range|:
name|requestsPerCLP
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|CategoryListParams
name|clp
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|requests
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|FacetsAggregator
argument_list|>
argument_list|,
name|FacetsAggregator
argument_list|>
name|aggClasses
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|FacetsAggregator
argument_list|>
argument_list|,
name|FacetsAggregator
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|FacetsAggregator
argument_list|>
name|perCategoryAggregator
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryPath
argument_list|,
name|FacetsAggregator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|requests
control|)
block|{
name|FacetsAggregator
name|fa
init|=
name|fr
operator|.
name|createFacetsAggregator
argument_list|(
name|searchParams
operator|.
name|indexingParams
argument_list|)
decl_stmt|;
if|if
condition|(
name|fa
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this accumulator only supports requests that create a FacetsAggregator: "
operator|+
name|fr
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|FacetsAggregator
argument_list|>
name|faClass
init|=
name|fa
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|aggClasses
operator|.
name|containsKey
argument_list|(
name|faClass
argument_list|)
condition|)
block|{
name|aggClasses
operator|.
name|put
argument_list|(
name|faClass
argument_list|,
name|fa
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fa
operator|=
name|aggClasses
operator|.
name|get
argument_list|(
name|faClass
argument_list|)
expr_stmt|;
block|}
name|perCategoryAggregator
operator|.
name|put
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|,
name|fa
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aggClasses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// only one type of facet request
name|perCLPAggregator
operator|.
name|put
argument_list|(
name|clp
argument_list|,
name|aggClasses
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|perCLPAggregator
operator|.
name|put
argument_list|(
name|clp
argument_list|,
operator|new
name|MultiFacetsAggregator
argument_list|(
name|perCategoryAggregator
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PerCategoryListAggregator
argument_list|(
name|perCLPAggregator
argument_list|,
name|searchParams
operator|.
name|indexingParams
argument_list|)
return|;
block|}
comment|/**    * Creates a {@link FacetResultsHandler} that matches the given    * {@link FacetRequest}.    */
DECL|method|createFacetResultsHandler
specifier|protected
name|FacetResultsHandler
name|createFacetResultsHandler
parameter_list|(
name|FacetRequest
name|fr
parameter_list|)
block|{
if|if
condition|(
name|fr
operator|.
name|getDepth
argument_list|()
operator|==
literal|1
operator|&&
name|fr
operator|.
name|getSortOrder
argument_list|()
operator|==
name|SortOrder
operator|.
name|DESCENDING
condition|)
block|{
name|FacetArraysSource
name|fas
init|=
name|fr
operator|.
name|getFacetArraysSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|fas
operator|==
name|FacetArraysSource
operator|.
name|INT
condition|)
block|{
return|return
operator|new
name|IntFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
if|if
condition|(
name|fas
operator|==
name|FacetArraysSource
operator|.
name|FLOAT
condition|)
block|{
return|return
operator|new
name|FloatFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|fr
operator|.
name|getResultMode
argument_list|()
operator|==
name|ResultMode
operator|.
name|PER_NODE_IN_TREE
condition|)
block|{
return|return
operator|new
name|TopKInEachNodeHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
return|return
operator|new
name|TopKFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
comment|/**    * Used by {@link FacetsCollector} to build the list of {@link FacetResult    * facet results} that match the {@link FacetRequest facet requests} that were    * given in the constructor.    *     * @param matchingDocs    *          the documents that matched the query, per-segment.    */
annotation|@
name|Override
DECL|method|accumulate
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|accumulate
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// aggregate facets per category list (usually onle one category list)
name|FacetsAggregator
name|aggregator
init|=
name|getAggregator
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|groupRequests
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
for|for
control|(
name|MatchingDocs
name|md
range|:
name|matchingDocs
control|)
block|{
name|aggregator
operator|.
name|aggregate
argument_list|(
name|md
argument_list|,
name|clp
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
block|}
name|ParallelTaxonomyArrays
name|arrays
init|=
name|taxonomyReader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
decl_stmt|;
comment|// compute top-K
specifier|final
name|int
index|[]
name|children
init|=
name|arrays
operator|.
name|children
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|siblings
init|=
name|arrays
operator|.
name|siblings
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|int
name|rootOrd
init|=
name|taxonomyReader
operator|.
name|getOrdinal
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootOrd
operator|==
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
comment|// category does not exist
comment|// Add empty FacetResult
name|res
operator|.
name|add
argument_list|(
name|emptyResult
argument_list|(
name|rootOrd
argument_list|,
name|fr
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|CategoryListParams
name|clp
init|=
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|fr
operator|.
name|categoryPath
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// someone might ask to aggregate the ROOT category
name|OrdinalPolicy
name|ordinalPolicy
init|=
name|clp
operator|.
name|getOrdinalPolicy
argument_list|(
name|fr
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinalPolicy
operator|==
name|OrdinalPolicy
operator|.
name|NO_PARENTS
condition|)
block|{
comment|// rollup values
name|aggregator
operator|.
name|rollupValues
argument_list|(
name|fr
argument_list|,
name|rootOrd
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
block|}
name|FacetResultsHandler
name|frh
init|=
name|createFacetResultsHandler
argument_list|(
name|fr
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|frh
operator|.
name|compute
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|requiresDocScores
specifier|public
name|boolean
name|requiresDocScores
parameter_list|()
block|{
return|return
name|getAggregator
argument_list|()
operator|.
name|requiresDocScores
argument_list|()
return|;
block|}
block|}
end_class
end_unit
