begin_unit
begin_package
DECL|package|org.apache.lucene.facet.complements
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|complements
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
name|index
operator|.
name|IndexReader
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
name|MultiReader
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
name|ParallelAtomicReader
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
name|SlowCompositeReaderWrapper
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestFacetsAccumulatorWithComplement
specifier|public
class|class
name|TestFacetsAccumulatorWithComplement
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
comment|/**    * Test that complements does not cause a failure when using a parallel reader    */
annotation|@
name|Test
DECL|method|testComplementsWithParallerReader
specifier|public
name|void
name|testComplementsWithParallerReader
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|origReader
init|=
name|indexReader
decl_stmt|;
name|ParallelAtomicReader
name|pr
init|=
operator|new
name|ParallelAtomicReader
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|origReader
argument_list|)
argument_list|)
decl_stmt|;
name|indexReader
operator|=
name|pr
expr_stmt|;
try|try
block|{
name|doTestComplements
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|=
name|origReader
expr_stmt|;
block|}
block|}
comment|/**    * Test that complements works with MultiReader    */
annotation|@
name|Test
DECL|method|testComplementsWithMultiReader
specifier|public
name|void
name|testComplementsWithMultiReader
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|IndexReader
name|origReader
init|=
name|indexReader
decl_stmt|;
name|indexReader
operator|=
operator|new
name|MultiReader
argument_list|(
name|origReader
argument_list|)
expr_stmt|;
try|try
block|{
name|doTestComplements
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|=
name|origReader
expr_stmt|;
block|}
block|}
comment|/**    * Test that score is indeed constant when using a constant score    */
annotation|@
name|Test
DECL|method|testComplements
specifier|public
name|void
name|testComplements
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestComplements
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestComplements
specifier|private
name|void
name|doTestComplements
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify by facet values
name|List
argument_list|<
name|FacetResult
argument_list|>
name|countResWithComplement
init|=
name|findFacets
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|countResNoComplement
init|=
name|findFacets
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facet count results with complement!"
argument_list|,
literal|1
argument_list|,
name|countResWithComplement
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of facet count results no complement!"
argument_list|,
literal|1
argument_list|,
name|countResNoComplement
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|parentResWithComp
init|=
name|countResWithComplement
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|FacetResultNode
name|parentResNoComp
init|=
name|countResWithComplement
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of top count aggregated categories with complement!"
argument_list|,
literal|3
argument_list|,
name|parentResWithComp
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of top count aggregated categories no complement!"
argument_list|,
literal|3
argument_list|,
name|parentResNoComp
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** compute facets with certain facet requests and docs */
DECL|method|findFacets
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|findFacets
parameter_list|(
name|boolean
name|withComplement
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|OldFacetsAccumulator
name|sfa
init|=
operator|new
name|OldFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
name|sfa
operator|.
name|setComplementThreshold
argument_list|(
name|withComplement
condition|?
name|OldFacetsAccumulator
operator|.
name|FORCE_COMPLEMENT
else|:
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
name|sfa
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
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
comment|// Results are ready, printing them...
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
for|for
control|(
name|FacetResult
name|facetResult
range|:
name|res
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Res "
operator|+
operator|(
name|i
operator|++
operator|)
operator|+
literal|": "
operator|+
name|facetResult
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|withComplement
argument_list|,
name|sfa
operator|.
name|isUsingComplements
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
