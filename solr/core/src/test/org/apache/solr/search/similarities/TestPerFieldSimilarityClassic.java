begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/**  * Tests per-field similarity support in the schema when luceneMatchVersion indicates   * {@link ClassicSimilarity} should be the default.  * @see TestPerFieldSimilarity  */
end_comment
begin_class
DECL|class|TestPerFieldSimilarityClassic
specifier|public
class|class
name|TestPerFieldSimilarityClassic
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
comment|// any value below 6.0 should have this behavior
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LUCENE_5_3_1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-sim.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.luceneMatchVersion"
argument_list|)
expr_stmt|;
block|}
comment|/** test a field where the sim is specified directly */
DECL|method|testDirect
specifier|public
name|void
name|testDirect
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|SweetSpotSimilarity
operator|.
name|class
argument_list|,
name|getSimilarity
argument_list|(
literal|"sim1text"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** ... and for a dynamic field */
DECL|method|testDirectDynamic
specifier|public
name|void
name|testDirectDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|SweetSpotSimilarity
operator|.
name|class
argument_list|,
name|getSimilarity
argument_list|(
literal|"text_sim1"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test a field where a configurable sim factory is defined */
DECL|method|testFactory
specifier|public
name|void
name|testFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"sim2text"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
operator|(
operator|(
name|MockConfigurableSimilarity
operator|)
name|sim
operator|)
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** ... and for a dynamic field */
DECL|method|testFactoryDynamic
specifier|public
name|void
name|testFactoryDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_sim2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MockConfigurableSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"is there an echo?"
argument_list|,
operator|(
operator|(
name|MockConfigurableSimilarity
operator|)
name|sim
operator|)
operator|.
name|getPassthrough
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test a field where no similarity is specified */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"sim3text"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ClassicSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
comment|/** ... and for a dynamic field */
DECL|method|testDefaultsDynamic
specifier|public
name|void
name|testDefaultsDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_sim3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ClassicSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** test a field that does not exist */
DECL|method|testNonexistent
specifier|public
name|void
name|testNonexistent
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"sdfdsfdsfdswr5fsdfdsfdsfs"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ClassicSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit