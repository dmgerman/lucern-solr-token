begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_class
DECL|class|TestAssociationsFacetsExample
specifier|public
class|class
name|TestAssociationsFacetsExample
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testExamples
specifier|public
name|void
name|testExamples
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
operator|new
name|AssociationsFacetsExample
argument_list|()
operator|.
name|runSumAssociations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of results"
argument_list|,
literal|2
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=tags path=[] value=-1 childCount=2\n  lucene (4)\n  solr (2)\n"
argument_list|,
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=genre path=[] value=-1.0 childCount=2\n  computing (1.62)\n  software (0.34)\n"
argument_list|,
name|res
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
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
name|FacetResult
name|result
init|=
operator|new
name|AssociationsFacetsExample
argument_list|()
operator|.
name|runDrillDown
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dim=genre path=[] value=-1.0 childCount=2\n  computing (0.75)\n  software (0.34)\n"
argument_list|,
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
