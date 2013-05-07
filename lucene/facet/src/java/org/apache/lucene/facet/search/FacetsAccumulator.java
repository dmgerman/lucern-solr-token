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
name|HashSet
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
name|lucene
operator|.
name|facet
operator|.
name|encoding
operator|.
name|DGapVInt8IntDecoder
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
comment|/**  * Driver for Accumulating facets of faceted search requests over given  * documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetsAccumulator
specifier|public
class|class
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
DECL|field|searchParams
specifier|public
name|FacetSearchParams
name|searchParams
decl_stmt|;
comment|/**    * Initializes the accumulator with the given search params, index reader and    * taxonomy reader. This constructor creates the default {@link FacetArrays},    * which do not support reuse. If you want to use {@link ReusingFacetArrays},    * you should use the    * {@link #FacetsAccumulator(FacetSearchParams, IndexReader, TaxonomyReader, FacetArrays)}    * constructor.    */
DECL|method|FacetsAccumulator
specifier|public
name|FacetsAccumulator
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
operator|new
name|FacetArrays
argument_list|(
name|taxonomyReader
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an appropriate {@link FacetsAccumulator},    * returning {@link FacetsAccumulator} when all requests    * are {@link CountFacetRequest} and only one partition is    * in use, otherwise {@link StandardFacetsAccumulator}.    */
DECL|method|create
specifier|public
specifier|static
name|FacetsAccumulator
name|create
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|)
block|{
if|if
condition|(
name|fsp
operator|.
name|indexingParams
operator|.
name|getPartitionSize
argument_list|()
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
return|;
block|}
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
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
return|return
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|FacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
return|;
block|}
comment|/** Returns an empty {@link FacetResult}. */
DECL|method|emptyResult
specifier|protected
specifier|static
name|FacetResult
name|emptyResult
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|FacetRequest
name|fr
parameter_list|)
block|{
name|FacetResultNode
name|root
init|=
operator|new
name|FacetResultNode
argument_list|(
name|ordinal
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|root
operator|.
name|label
operator|=
name|fr
operator|.
name|categoryPath
expr_stmt|;
return|return
operator|new
name|FacetResult
argument_list|(
name|fr
argument_list|,
name|root
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Initializes the accumulator with the given parameters as well as    * {@link FacetArrays}. Note that the accumulator doesn't call    * {@link FacetArrays#free()}. If you require that (only makes sense if you    * use {@link ReusingFacetArrays}, you should do it after you've finished with    * the accumulator.    */
DECL|method|FacetsAccumulator
specifier|public
name|FacetsAccumulator
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
name|this
operator|.
name|facetArrays
operator|=
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
name|this
operator|.
name|searchParams
operator|=
name|searchParams
expr_stmt|;
block|}
comment|/**    * Returns the {@link FacetsAggregator} to use for aggregating the categories    * found in the result documents. The default implementation returns    * {@link CountingFacetsAggregator}, or {@link FastCountingFacetsAggregator}    * if all categories can be decoded with {@link DGapVInt8IntDecoder}.    */
DECL|method|getAggregator
specifier|public
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
if|if
condition|(
name|FastCountingFacetsAggregator
operator|.
name|verifySearchParams
argument_list|(
name|searchParams
argument_list|)
condition|)
block|{
return|return
operator|new
name|FastCountingFacetsAggregator
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|CountingFacetsAggregator
argument_list|()
return|;
block|}
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
DECL|method|getCategoryLists
specifier|protected
name|Set
argument_list|<
name|CategoryListParams
argument_list|>
name|getCategoryLists
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
name|singleton
argument_list|(
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
argument_list|)
return|;
block|}
name|HashSet
argument_list|<
name|CategoryListParams
argument_list|>
name|clps
init|=
operator|new
name|HashSet
argument_list|<
name|CategoryListParams
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
name|clps
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
return|return
name|clps
return|;
block|}
comment|/**    * Used by {@link FacetsCollector} to build the list of {@link FacetResult    * facet results} that match the {@link FacetRequest facet requests} that were    * given in the constructor.    *     * @param matchingDocs    *          the documents that matched the query, per-segment.    */
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
name|getCategoryLists
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
