begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package
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
name|similarities
operator|.
name|AfterEffectB
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
name|similarities
operator|.
name|AfterEffectL
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
name|similarities
operator|.
name|BasicModelIF
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
name|similarities
operator|.
name|BasicModelP
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
name|similarities
operator|.
name|DFRSimilarity
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
name|similarities
operator|.
name|NormalizationH2
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
name|similarities
operator|.
name|NormalizationH3
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
name|similarities
operator|.
name|Similarity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Tests {@link DFRSimilarityFactory}  */
end_comment
begin_class
DECL|class|TestDFRSimilarityFactory
specifier|public
class|class
name|TestDFRSimilarityFactory
extends|extends
name|BaseSimilarityTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-dfr.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** dfr with default parameters */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DFRSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|DFRSimilarity
name|dfr
init|=
operator|(
name|DFRSimilarity
operator|)
name|sim
decl_stmt|;
name|assertEquals
argument_list|(
name|BasicModelIF
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getBasicModel
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AfterEffectB
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getAfterEffect
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NormalizationH2
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getNormalization
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** dfr with parametrized normalization */
DECL|method|testParameters
specifier|public
name|void
name|testParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_params"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DFRSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|DFRSimilarity
name|dfr
init|=
operator|(
name|DFRSimilarity
operator|)
name|sim
decl_stmt|;
name|assertEquals
argument_list|(
name|BasicModelIF
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getBasicModel
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AfterEffectB
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getAfterEffect
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NormalizationH3
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getNormalization
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|NormalizationH3
name|norm
init|=
operator|(
name|NormalizationH3
operator|)
name|dfr
operator|.
name|getNormalization
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|900f
argument_list|,
name|norm
operator|.
name|getMu
argument_list|()
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
block|}
comment|/** LUCENE-3566 */
DECL|method|testParameterC
specifier|public
name|void
name|testParameterC
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_paramc"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DFRSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|DFRSimilarity
name|dfr
init|=
operator|(
name|DFRSimilarity
operator|)
name|sim
decl_stmt|;
name|assertEquals
argument_list|(
name|BasicModelP
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getBasicModel
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AfterEffectL
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getAfterEffect
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NormalizationH2
operator|.
name|class
argument_list|,
name|dfr
operator|.
name|getNormalization
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|NormalizationH2
name|norm
init|=
operator|(
name|NormalizationH2
operator|)
name|dfr
operator|.
name|getNormalization
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|7f
argument_list|,
name|norm
operator|.
name|getC
argument_list|()
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
