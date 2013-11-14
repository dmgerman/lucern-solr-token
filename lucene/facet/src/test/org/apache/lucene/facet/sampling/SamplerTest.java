begin_unit
begin_package
DECL|package|org.apache.lucene.facet.sampling
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sampling
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
name|FacetTestBase
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
name|old
operator|.
name|OldFacetsAccumulator
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
name|CountFacetRequest
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
name|FacetResultNode
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
name|FacetLabel
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
name|MatchAllDocsQuery
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|SamplerTest
specifier|public
class|class
name|SamplerTest
extends|extends
name|FacetTestBase
block|{
DECL|field|fip
specifier|private
name|FacetIndexingParams
name|fip
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|fip
operator|=
name|getFacetIndexingParams
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|initIndex
argument_list|(
name|fip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numDocsToIndex
specifier|protected
name|int
name|numDocsToIndex
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
annotation|@
name|Override
DECL|method|getCategories
specifier|protected
name|List
argument_list|<
name|FacetLabel
argument_list|>
name|getCategories
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|FacetLabel
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|doc
operator|%
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getContent
specifier|protected
name|String
name|getContent
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|closeAll
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaultFixer
specifier|public
name|void
name|testDefaultFixer
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomSampler
name|randomSampler
init|=
operator|new
name|RandomSampler
argument_list|()
decl_stmt|;
name|SampleFixer
name|fixer
init|=
name|randomSampler
operator|.
name|samplingParams
operator|.
name|getSampleFixer
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|fixer
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomFixer
specifier|public
name|void
name|testCustomFixer
parameter_list|()
throws|throws
name|Exception
block|{
name|SamplingParams
name|sp
init|=
operator|new
name|SamplingParams
argument_list|()
decl_stmt|;
name|sp
operator|.
name|setSampleFixer
argument_list|(
operator|new
name|TakmiSampleFixer
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TakmiSampleFixer
operator|.
name|class
argument_list|,
name|sp
operator|.
name|getSampleFixer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoFixing
specifier|public
name|void
name|testNoFixing
parameter_list|()
throws|throws
name|Exception
block|{
name|SamplingParams
name|sp
init|=
operator|new
name|SamplingParams
argument_list|()
decl_stmt|;
name|sp
operator|.
name|setMaxSampleSize
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|sp
operator|.
name|setMinSampleSize
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|sp
operator|.
name|setSampleRatio
argument_list|(
literal|0.01d
argument_list|)
expr_stmt|;
name|sp
operator|.
name|setSamplingThreshold
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|sp
operator|.
name|setOversampleFactor
argument_list|(
literal|5d
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Fixer should be null as the test is for no-fixing"
argument_list|,
name|sp
operator|.
name|getSampleFixer
argument_list|()
argument_list|)
expr_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|fip
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|SamplingAccumulator
name|accumulator
init|=
operator|new
name|SamplingAccumulator
argument_list|(
operator|new
name|RandomSampler
argument_list|(
name|sp
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
comment|// Make sure no complements are in action
name|accumulator
operator|.
name|setComplementThreshold
argument_list|(
name|OldFacetsAccumulator
operator|.
name|DISABLE_COMPLEMENT
argument_list|)
expr_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|accumulator
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|FacetResultNode
name|node
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|value
operator|<
name|numDocsToIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
