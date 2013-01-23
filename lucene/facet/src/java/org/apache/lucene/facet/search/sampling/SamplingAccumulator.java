begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.sampling
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
operator|.
name|sampling
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
name|facet
operator|.
name|search
operator|.
name|FacetArrays
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
name|FacetResultsHandler
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
name|FacetsAccumulator
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
name|SamplingWrapper
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
name|ScoredDocIDs
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
name|StandardFacetsAccumulator
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
name|results
operator|.
name|FacetResult
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
name|sampling
operator|.
name|Sampler
operator|.
name|SampleResult
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
comment|/**  * Facets accumulation with sampling.<br>  *<p>  * Note two major differences between this class and {@link SamplingWrapper}:  *<ol>  *<li>Latter can wrap any other {@link FacetsAccumulator} while this class  * directly extends {@link StandardFacetsAccumulator}.</li>  *<li>This class can effectively apply sampling on the complement set of  * matching document, thereby working efficiently with the complement  * optimization - see {@link FacetsAccumulator#getComplementThreshold()}.</li>  *</ol>  *<p>  * Note: Sampling accumulation (Accumulation over a sampled-set of the results),  * does not guarantee accurate values for  * {@link FacetResult#getNumValidDescendants()}.  *   * @see Sampler  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SamplingAccumulator
specifier|public
class|class
name|SamplingAccumulator
extends|extends
name|StandardFacetsAccumulator
block|{
DECL|field|samplingRatio
specifier|private
name|double
name|samplingRatio
init|=
operator|-
literal|1d
decl_stmt|;
DECL|field|sampler
specifier|private
specifier|final
name|Sampler
name|sampler
decl_stmt|;
DECL|method|SamplingAccumulator
specifier|public
name|SamplingAccumulator
parameter_list|(
name|Sampler
name|sampler
parameter_list|,
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
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
name|this
operator|.
name|sampler
operator|=
name|sampler
expr_stmt|;
block|}
comment|/**    * Constructor...    */
DECL|method|SamplingAccumulator
specifier|public
name|SamplingAccumulator
parameter_list|(
name|Sampler
name|sampler
parameter_list|,
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
name|super
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|sampler
operator|=
name|sampler
expr_stmt|;
block|}
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
name|ScoredDocIDs
name|docids
parameter_list|)
throws|throws
name|IOException
block|{
comment|// first let delegee accumulate without labeling at all (though
comment|// currently it doesn't matter because we have to label all returned anyhow)
name|boolean
name|origAllowLabeling
init|=
name|isAllowLabeling
argument_list|()
decl_stmt|;
name|setAllowLabeling
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Replacing the original searchParams with the over-sampled
name|FacetSearchParams
name|original
init|=
name|searchParams
decl_stmt|;
name|searchParams
operator|=
name|sampler
operator|.
name|overSampledSearchParams
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|sampleRes
init|=
name|super
operator|.
name|accumulate
argument_list|(
name|docids
argument_list|)
decl_stmt|;
name|setAllowLabeling
argument_list|(
name|origAllowLabeling
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|fixedRes
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
name|FacetResult
name|fres
range|:
name|sampleRes
control|)
block|{
comment|// for sure fres is not null because this is guaranteed by the delegee.
name|FacetResultsHandler
name|frh
init|=
name|fres
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|createFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|)
decl_stmt|;
comment|// fix the result of current request
name|sampler
operator|.
name|getSampleFixer
argument_list|(
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|,
name|searchParams
argument_list|)
operator|.
name|fixResult
argument_list|(
name|docids
argument_list|,
name|fres
argument_list|)
expr_stmt|;
name|fres
operator|=
name|frh
operator|.
name|rearrangeFacetResult
argument_list|(
name|fres
argument_list|)
expr_stmt|;
comment|// let delegee's handler do any
comment|// Using the sampler to trim the extra (over-sampled) results
name|fres
operator|=
name|sampler
operator|.
name|trimResult
argument_list|(
name|fres
argument_list|)
expr_stmt|;
comment|// arranging it needs to
comment|// final labeling if allowed (because labeling is a costly operation)
if|if
condition|(
name|isAllowLabeling
argument_list|()
condition|)
block|{
name|frh
operator|.
name|labelResult
argument_list|(
name|fres
argument_list|)
expr_stmt|;
block|}
name|fixedRes
operator|.
name|add
argument_list|(
name|fres
argument_list|)
expr_stmt|;
comment|// add to final results
block|}
name|searchParams
operator|=
name|original
expr_stmt|;
comment|// Back to original params
return|return
name|fixedRes
return|;
block|}
annotation|@
name|Override
DECL|method|actualDocsToAccumulate
specifier|protected
name|ScoredDocIDs
name|actualDocsToAccumulate
parameter_list|(
name|ScoredDocIDs
name|docids
parameter_list|)
throws|throws
name|IOException
block|{
name|SampleResult
name|sampleRes
init|=
name|sampler
operator|.
name|getSampleSet
argument_list|(
name|docids
argument_list|)
decl_stmt|;
name|samplingRatio
operator|=
name|sampleRes
operator|.
name|actualSampleRatio
expr_stmt|;
return|return
name|sampleRes
operator|.
name|docids
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalCountsFactor
specifier|protected
name|double
name|getTotalCountsFactor
parameter_list|()
block|{
if|if
condition|(
name|samplingRatio
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Total counts ratio unavailable because actualDocsToAccumulate() was not invoked"
argument_list|)
throw|;
block|}
return|return
name|samplingRatio
return|;
block|}
block|}
end_class
end_unit
