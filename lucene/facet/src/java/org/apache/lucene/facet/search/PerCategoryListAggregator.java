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
name|FacetIndexingParams
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link FacetsAggregator} which invokes the proper aggregator per  * {@link CategoryListParams}.  * {@link #rollupValues(FacetRequest, int, int[], int[], FacetArrays)} is  * delegated to the proper aggregator which handles the  * {@link CategoryListParams} the given {@link FacetRequest} belongs to.  */
end_comment
begin_class
DECL|class|PerCategoryListAggregator
specifier|public
class|class
name|PerCategoryListAggregator
implements|implements
name|FacetsAggregator
block|{
DECL|field|aggregators
specifier|private
specifier|final
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|FacetsAggregator
argument_list|>
name|aggregators
decl_stmt|;
DECL|field|fip
specifier|private
specifier|final
name|FacetIndexingParams
name|fip
decl_stmt|;
DECL|method|PerCategoryListAggregator
specifier|public
name|PerCategoryListAggregator
parameter_list|(
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|FacetsAggregator
argument_list|>
name|aggregators
parameter_list|,
name|FacetIndexingParams
name|fip
parameter_list|)
block|{
name|this
operator|.
name|aggregators
operator|=
name|aggregators
expr_stmt|;
name|this
operator|.
name|fip
operator|=
name|fip
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|MatchingDocs
name|matchingDocs
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
throws|throws
name|IOException
block|{
name|aggregators
operator|.
name|get
argument_list|(
name|clp
argument_list|)
operator|.
name|aggregate
argument_list|(
name|matchingDocs
argument_list|,
name|clp
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rollupValues
specifier|public
name|void
name|rollupValues
parameter_list|(
name|FacetRequest
name|fr
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|CategoryListParams
name|clp
init|=
name|fip
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
name|aggregators
operator|.
name|get
argument_list|(
name|clp
argument_list|)
operator|.
name|rollupValues
argument_list|(
name|fr
argument_list|,
name|ordinal
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requiresDocScores
specifier|public
name|boolean
name|requiresDocScores
parameter_list|()
block|{
for|for
control|(
name|FacetsAggregator
name|aggregator
range|:
name|aggregators
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|aggregator
operator|.
name|requiresDocScores
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
