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
name|DistributionLL
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
name|DistributionSPL
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
name|IBSimilarity
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
name|LambdaDF
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
name|LambdaTTF
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
comment|/**  * Tests {@link IBSimilarityFactory}  */
end_comment
begin_class
DECL|class|TestIBSimilarityFactory
specifier|public
class|class
name|TestIBSimilarityFactory
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
literal|"schema-ib.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** spl/df/h2 with default parameters */
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
name|IBSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|IBSimilarity
name|ib
init|=
operator|(
name|IBSimilarity
operator|)
name|sim
decl_stmt|;
name|assertEquals
argument_list|(
name|DistributionSPL
operator|.
name|class
argument_list|,
name|ib
operator|.
name|getDistribution
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LambdaDF
operator|.
name|class
argument_list|,
name|ib
operator|.
name|getLambda
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
name|ib
operator|.
name|getNormalization
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** ll/ttf/h3 with parametrized normalization */
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
name|IBSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|IBSimilarity
name|ib
init|=
operator|(
name|IBSimilarity
operator|)
name|sim
decl_stmt|;
name|assertEquals
argument_list|(
name|DistributionLL
operator|.
name|class
argument_list|,
name|ib
operator|.
name|getDistribution
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LambdaTTF
operator|.
name|class
argument_list|,
name|ib
operator|.
name|getLambda
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
name|ib
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
name|ib
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
block|}
end_class
end_unit
