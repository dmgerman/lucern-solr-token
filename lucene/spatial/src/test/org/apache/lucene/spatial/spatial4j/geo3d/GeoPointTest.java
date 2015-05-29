begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_comment
comment|/**  * Test basic GeoPoint functionality.  */
end_comment
begin_class
DECL|class|GeoPointTest
specifier|public
class|class
name|GeoPointTest
block|{
annotation|@
name|Test
DECL|method|testConversion
specifier|public
name|void
name|testConversion
parameter_list|()
block|{
specifier|final
name|double
name|pLat
init|=
literal|0.123
decl_stmt|;
specifier|final
name|double
name|pLon
init|=
operator|-
literal|0.456
decl_stmt|;
specifier|final
name|GeoPoint
name|p1
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|pLat
argument_list|,
name|pLon
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pLat
argument_list|,
name|p1
operator|.
name|getLatitude
argument_list|()
argument_list|,
literal|1e-12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pLon
argument_list|,
name|p1
operator|.
name|getLongitude
argument_list|()
argument_list|,
literal|1e-12
argument_list|)
expr_stmt|;
specifier|final
name|GeoPoint
name|p2
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|pLat
argument_list|,
name|pLon
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pLat
argument_list|,
name|p2
operator|.
name|getLatitude
argument_list|()
argument_list|,
literal|1e-12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pLon
argument_list|,
name|p2
operator|.
name|getLongitude
argument_list|()
argument_list|,
literal|1e-12
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
