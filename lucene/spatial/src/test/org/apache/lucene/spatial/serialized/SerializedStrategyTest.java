begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.serialized
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|serialized
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
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
name|spatial
operator|.
name|SpatialMatchConcern
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
name|spatial
operator|.
name|StrategyTestCase
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
begin_class
DECL|class|SerializedStrategyTest
specifier|public
class|class
name|SerializedStrategyTest
extends|extends
name|StrategyTestCase
block|{
annotation|@
name|Before
annotation|@
name|Override
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
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|SerializedDVStrategy
argument_list|(
name|ctx
argument_list|,
literal|"serialized"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsDocValues
specifier|protected
name|boolean
name|needsDocValues
parameter_list|()
block|{
return|return
operator|(
name|strategy
operator|instanceof
name|SerializedDVStrategy
operator|)
return|;
block|}
annotation|@
name|Test
DECL|method|testBasicOperaions
specifier|public
name|void
name|testBasicOperaions
parameter_list|()
throws|throws
name|IOException
block|{
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_SIMPLE_BBOX
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|EXACT
argument_list|,
name|QTEST_Simple_Queries_BBox
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatesBBox
specifier|public
name|void
name|testStatesBBox
parameter_list|()
throws|throws
name|IOException
block|{
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_STATES_BBOX
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_States_IsWithin_BBox
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_States_Intersects_BBox
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCitiesIntersectsBBox
specifier|public
name|void
name|testCitiesIntersectsBBox
parameter_list|()
throws|throws
name|IOException
block|{
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_WORLD_CITIES_POINTS
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_Cities_Intersects_BBox
argument_list|)
expr_stmt|;
block|}
comment|//sorting is tested in DistanceStrategyTest
block|}
end_class
end_unit
