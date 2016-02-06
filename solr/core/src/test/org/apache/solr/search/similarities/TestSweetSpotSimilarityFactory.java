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
name|misc
operator|.
name|SweetSpotSimilarity
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
name|ClassicSimilarity
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
comment|/**  * Tests {@link SweetSpotSimilarityFactory}  */
end_comment
begin_class
DECL|class|TestSweetSpotSimilarityFactory
specifier|public
class|class
name|TestSweetSpotSimilarityFactory
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
literal|"schema-sweetspot.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** default parameters */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|SweetSpotSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// SSS tf w/defaults should behave just like DS
name|ClassicSimilarity
name|d
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf: i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|// default norm sanity check
name|assertEquals
argument_list|(
literal|"norm 1"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 4"
argument_list|,
literal|0.50F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 16"
argument_list|,
literal|0.25F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|16
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|/** baseline with parameters */
DECL|method|testBaselineParameters
specifier|public
name|void
name|testBaselineParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|SweetSpotSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_baseline"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|ClassicSimilarity
name|d
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
comment|// constant up to 6
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf i="
operator|+
name|i
argument_list|,
literal|1.5F
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|// less then default sim above 6
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|+
literal|"< d="
operator|+
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// norms: plateau from 3-5
name|assertEquals
argument_list|(
literal|"norm 1 == 7"
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|1
argument_list|)
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|7
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 2 == 6"
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|1
argument_list|)
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|7
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 3"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 4"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 5"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 6 too high: "
operator|+
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|6
argument_list|)
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|6
argument_list|)
operator|<
literal|1.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 7 higher then norm 6"
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|7
argument_list|)
operator|<
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 20"
argument_list|,
literal|0.25F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|20
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
block|}
comment|/** hyperbolic with parameters */
DECL|method|testHyperbolicParameters
specifier|public
name|void
name|testHyperbolicParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|SweetSpotSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_hyperbolic"
argument_list|,
name|SweetSpotSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"MIN tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|3.3F
operator|<=
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MAX tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<=
literal|7.7F
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"MID tf"
argument_list|,
literal|3.3F
operator|+
operator|(
literal|7.7F
operator|-
literal|3.3F
operator|)
operator|/
literal|2.0F
argument_list|,
name|sim
operator|.
name|tf
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|0.00001F
argument_list|)
expr_stmt|;
comment|// norms: plateau from 1-5, shallow slope
name|assertEquals
argument_list|(
literal|"norm 1"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 2"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 3"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 4"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|4
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"norm 5"
argument_list|,
literal|1.00F
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|0.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 6 too high: "
operator|+
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|6
argument_list|)
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|6
argument_list|)
operator|<
literal|1.0F
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 7 higher then norm 6"
argument_list|,
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|7
argument_list|)
operator|<
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"norm 20 not high enough: "
operator|+
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|20
argument_list|)
argument_list|,
literal|0.25F
operator|<
name|sim
operator|.
name|computeLengthNorm
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
