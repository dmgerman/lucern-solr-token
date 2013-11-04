begin_unit
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package
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
name|collections
operator|.
name|ObjectToIntMap
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|LuceneTestCase
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
begin_comment
comment|// We require sorted set DVs:
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene40"
block|,
literal|"Lucene41"
block|}
argument_list|)
DECL|class|TestSimpleSortedSetFacetsExample
specifier|public
class|class
name|TestSimpleSortedSetFacetsExample
extends|extends
name|LuceneTestCase
block|{
DECL|field|expectedCounts
specifier|private
specifier|static
specifier|final
name|ObjectToIntMap
argument_list|<
name|CategoryPath
argument_list|>
name|expectedCounts
init|=
operator|new
name|ObjectToIntMap
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2012"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2010"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"1999"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Frank"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Susan"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expectedCounts
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|field|expectedCountsDrillDown
specifier|private
specifier|static
specifier|final
name|ObjectToIntMap
argument_list|<
name|CategoryPath
argument_list|>
name|expectedCountsDrillDown
init|=
operator|new
name|ObjectToIntMap
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|expectedCountsDrillDown
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|expectedCountsDrillDown
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExpectedCounts
specifier|private
name|void
name|assertExpectedCounts
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
parameter_list|,
name|ObjectToIntMap
argument_list|<
name|CategoryPath
argument_list|>
name|expCounts
parameter_list|)
block|{
for|for
control|(
name|FacetResult
name|res
range|:
name|facetResults
control|)
block|{
name|FacetResultNode
name|root
init|=
name|res
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|node
range|:
name|root
operator|.
name|subResults
control|)
block|{
name|assertEquals
argument_list|(
literal|"incorrect count for "
operator|+
name|node
operator|.
name|label
argument_list|,
name|expCounts
operator|.
name|get
argument_list|(
name|node
operator|.
name|label
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|node
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
operator|new
name|SimpleSortedSetFacetsExample
argument_list|()
operator|.
name|runSearch
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|facetResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertExpectedCounts
argument_list|(
name|facetResults
argument_list|,
name|expectedCounts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDrillDown
specifier|public
name|void
name|testDrillDown
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
operator|new
name|SimpleSortedSetFacetsExample
argument_list|()
operator|.
name|runDrillDown
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facetResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertExpectedCounts
argument_list|(
name|facetResults
argument_list|,
name|expectedCountsDrillDown
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
