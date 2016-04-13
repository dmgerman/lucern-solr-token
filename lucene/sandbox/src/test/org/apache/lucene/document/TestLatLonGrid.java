begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|geo
operator|.
name|GeoTestUtil
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
name|geo
operator|.
name|GeoUtils
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
name|geo
operator|.
name|Polygon
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
name|geo
operator|.
name|Rectangle
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|decodeLatitude
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|decodeLongitude
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|encodeLatitude
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|encodeLongitude
import|;
end_import
begin_comment
comment|/** tests against LatLonGrid (avoiding indexing/queries) */
end_comment
begin_class
DECL|class|TestLatLonGrid
specifier|public
class|class
name|TestLatLonGrid
extends|extends
name|LuceneTestCase
block|{
comment|/** If the grid returns true, then any point in that cell should return true as well */
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|polygon
init|=
name|GeoTestUtil
operator|.
name|nextPolygon
argument_list|()
decl_stmt|;
name|Rectangle
name|box
init|=
name|Rectangle
operator|.
name|fromPolygon
argument_list|(
operator|new
name|Polygon
index|[]
block|{
name|polygon
block|}
argument_list|)
decl_stmt|;
name|int
name|minLat
init|=
name|encodeLatitude
argument_list|(
name|box
operator|.
name|minLat
argument_list|)
decl_stmt|;
name|int
name|maxLat
init|=
name|encodeLatitude
argument_list|(
name|box
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|int
name|minLon
init|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
decl_stmt|;
name|int
name|maxLon
init|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
decl_stmt|;
name|LatLonGrid
name|grid
init|=
operator|new
name|LatLonGrid
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|,
name|polygon
argument_list|)
decl_stmt|;
comment|// we are in integer space... but exhaustive testing is slow!
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10000
condition|;
name|j
operator|++
control|)
block|{
name|int
name|lat
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
name|int
name|lon
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
name|boolean
name|expected
init|=
name|polygon
operator|.
name|contains
argument_list|(
name|decodeLatitude
argument_list|(
name|lat
argument_list|)
argument_list|,
name|decodeLongitude
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|actual
init|=
name|grid
operator|.
name|contains
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testGrowingPolygon
specifier|public
name|void
name|testGrowingPolygon
parameter_list|()
block|{
name|double
name|centerLat
init|=
operator|-
literal|80.0
operator|+
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|160.0
decl_stmt|;
name|double
name|centerLon
init|=
operator|-
literal|170.0
operator|+
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|340.0
decl_stmt|;
name|double
name|radiusMeters
init|=
literal|0.0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|radiusMeters
operator|=
name|Math
operator|.
name|nextUp
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
block|}
comment|// Start with a miniscule polygon, and grow it:
name|int
name|gons
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
decl_stmt|;
while|while
condition|(
name|radiusMeters
operator|<
name|GeoUtils
operator|.
name|EARTH_MEAN_RADIUS_METERS
operator|*
name|Math
operator|.
name|PI
operator|/
literal|2.0
operator|+
literal|1.0
condition|)
block|{
name|Polygon
name|polygon
decl_stmt|;
try|try
block|{
name|polygon
operator|=
name|GeoTestUtil
operator|.
name|createRegularPolygon
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|,
name|gons
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// OK: we made a too-big poly and it crossed a pole or dateline
break|break;
block|}
name|radiusMeters
operator|*=
literal|1.1
expr_stmt|;
name|Rectangle
name|box
init|=
name|Rectangle
operator|.
name|fromPolygon
argument_list|(
operator|new
name|Polygon
index|[]
block|{
name|polygon
block|}
argument_list|)
decl_stmt|;
name|int
name|minLat
init|=
name|encodeLatitude
argument_list|(
name|box
operator|.
name|minLat
argument_list|)
decl_stmt|;
name|int
name|maxLat
init|=
name|encodeLatitude
argument_list|(
name|box
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|int
name|minLon
init|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
decl_stmt|;
name|int
name|maxLon
init|=
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
decl_stmt|;
name|LatLonGrid
name|grid
init|=
operator|new
name|LatLonGrid
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|,
name|polygon
argument_list|)
decl_stmt|;
comment|// we are in integer space... but exhaustive testing is slow!
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|int
name|lat
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
name|int
name|lon
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
name|boolean
name|expected
init|=
name|polygon
operator|.
name|contains
argument_list|(
name|decodeLatitude
argument_list|(
name|lat
argument_list|)
argument_list|,
name|decodeLongitude
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|actual
init|=
name|grid
operator|.
name|contains
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** create ever-increasing grids and check that too-small polygons don't blow it up */
DECL|method|testTinyGrids
specifier|public
name|void
name|testTinyGrids
parameter_list|()
block|{
name|double
name|ZERO
init|=
name|decodeLatitude
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|double
name|ONE
init|=
name|decodeLatitude
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Polygon
name|tiny
init|=
operator|new
name|Polygon
argument_list|(
operator|new
name|double
index|[]
block|{
name|ZERO
block|,
name|ZERO
block|,
name|ONE
block|,
name|ONE
block|,
name|ZERO
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
name|ZERO
block|,
name|ONE
block|,
name|ONE
block|,
name|ZERO
block|,
name|ZERO
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|max
init|=
literal|1
init|;
name|max
operator|<
literal|500000
condition|;
name|max
operator|++
control|)
block|{
name|LatLonGrid
name|grid
init|=
operator|new
name|LatLonGrid
argument_list|(
literal|0
argument_list|,
name|max
argument_list|,
literal|0
argument_list|,
name|max
argument_list|,
name|tiny
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|tiny
operator|.
name|contains
argument_list|(
name|decodeLatitude
argument_list|(
name|max
argument_list|)
argument_list|,
name|decodeLongitude
argument_list|(
name|max
argument_list|)
argument_list|)
argument_list|,
name|grid
operator|.
name|contains
argument_list|(
name|max
argument_list|,
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
