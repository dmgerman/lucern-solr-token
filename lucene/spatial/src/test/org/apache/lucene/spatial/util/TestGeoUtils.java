begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|util
operator|.
name|BytesRefBuilder
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
name|SloppyMath
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
comment|/**  * Tests class for methods in GeoUtils  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TestGeoUtils
specifier|public
class|class
name|TestGeoUtils
extends|extends
name|LuceneTestCase
block|{
comment|// Global bounding box we will "cover" in the random test; we have to make this "smallish" else the queries take very long:
DECL|field|originLat
specifier|private
specifier|static
name|double
name|originLat
decl_stmt|;
DECL|field|originLon
specifier|private
specifier|static
name|double
name|originLon
decl_stmt|;
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
name|originLon
operator|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
expr_stmt|;
name|originLat
operator|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
expr_stmt|;
block|}
DECL|method|randomLat
specifier|public
name|double
name|randomLat
parameter_list|(
name|boolean
name|small
parameter_list|)
block|{
name|double
name|result
decl_stmt|;
if|if
condition|(
name|small
condition|)
block|{
name|result
operator|=
name|GeoTestUtil
operator|.
name|nextLatitudeNear
argument_list|(
name|originLat
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|randomLon
specifier|public
name|double
name|randomLon
parameter_list|(
name|boolean
name|small
parameter_list|)
block|{
name|double
name|result
decl_stmt|;
if|if
condition|(
name|small
condition|)
block|{
name|result
operator|=
name|GeoTestUtil
operator|.
name|nextLongitudeNear
argument_list|(
name|originLon
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Tests stability of {@link GeoEncodingUtils#geoCodedToPrefixCoded}    */
DECL|method|testGeoPrefixCoding
specifier|public
name|void
name|testGeoPrefixCoding
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|long
name|hash
decl_stmt|;
name|long
name|decodedHash
decl_stmt|;
name|BytesRefBuilder
name|brb
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|numIters
operator|--
operator|>=
literal|0
condition|)
block|{
name|hash
operator|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|randomLat
argument_list|(
literal|false
argument_list|)
argument_list|,
name|randomLon
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|32
init|;
name|i
operator|<
literal|64
condition|;
operator|++
name|i
control|)
block|{
name|GeoEncodingUtils
operator|.
name|geoCodedToPrefixCoded
argument_list|(
name|hash
argument_list|,
name|i
argument_list|,
name|brb
argument_list|)
expr_stmt|;
name|decodedHash
operator|=
name|GeoEncodingUtils
operator|.
name|prefixCodedToGeoCoded
argument_list|(
name|brb
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|hash
operator|>>>
name|i
operator|)
operator|<<
name|i
argument_list|,
name|decodedHash
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMortonEncoding
specifier|public
name|void
name|testMortonEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|hash
init|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
literal|90
argument_list|,
literal|180
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|180.0
argument_list|,
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|hash
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90.0
argument_list|,
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|hash
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncodeDecode
specifier|public
name|void
name|testEncodeDecode
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|boolean
name|small
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|double
name|lat
init|=
name|randomLat
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|double
name|lon
init|=
name|randomLon
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|long
name|enc
init|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|double
name|latEnc
init|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|enc
argument_list|)
decl_stmt|;
name|double
name|lonEnc
init|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|enc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"lat="
operator|+
name|lat
operator|+
literal|" latEnc="
operator|+
name|latEnc
operator|+
literal|" diff="
operator|+
operator|(
name|lat
operator|-
name|latEnc
operator|)
argument_list|,
name|lat
argument_list|,
name|latEnc
argument_list|,
name|GeoEncodingUtils
operator|.
name|TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lon="
operator|+
name|lon
operator|+
literal|" lonEnc="
operator|+
name|lonEnc
operator|+
literal|" diff="
operator|+
operator|(
name|lon
operator|-
name|lonEnc
operator|)
argument_list|,
name|lon
argument_list|,
name|lonEnc
argument_list|,
name|GeoEncodingUtils
operator|.
name|TOLERANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testScaleUnscaleIsStable
specifier|public
name|void
name|testScaleUnscaleIsStable
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|boolean
name|small
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|double
name|lat
init|=
name|randomLat
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|double
name|lon
init|=
name|randomLon
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|long
name|enc
init|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|double
name|latEnc
init|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|enc
argument_list|)
decl_stmt|;
name|double
name|lonEnc
init|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|enc
argument_list|)
decl_stmt|;
name|long
name|enc2
init|=
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|double
name|latEnc2
init|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|enc2
argument_list|)
decl_stmt|;
name|double
name|lonEnc2
init|=
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|enc2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|latEnc
argument_list|,
name|latEnc2
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lonEnc
argument_list|,
name|lonEnc2
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// We rely heavily on GeoUtils.circleToBBox so we test it here:
DECL|method|testRandomCircleToBBox
specifier|public
name|void
name|testRandomCircleToBBox
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|boolean
name|useSmallRanges
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|double
name|radiusMeters
decl_stmt|;
name|double
name|centerLat
init|=
name|randomLat
argument_list|(
name|useSmallRanges
argument_list|)
decl_stmt|;
name|double
name|centerLon
init|=
name|randomLon
argument_list|(
name|useSmallRanges
argument_list|)
decl_stmt|;
if|if
condition|(
name|useSmallRanges
condition|)
block|{
comment|// Approx 4 degrees lon at the equator:
name|radiusMeters
operator|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|444000
expr_stmt|;
block|}
else|else
block|{
name|radiusMeters
operator|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|*
literal|50000000
expr_stmt|;
block|}
comment|// TODO: randomly quantize radius too, to provoke exact math errors?
name|GeoRect
name|bbox
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
name|int
name|numPointsToTry
init|=
literal|1000
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
name|numPointsToTry
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lat
decl_stmt|;
name|double
name|lon
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|lat
operator|=
name|randomLat
argument_list|(
name|useSmallRanges
argument_list|)
expr_stmt|;
name|lon
operator|=
name|randomLon
argument_list|(
name|useSmallRanges
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// pick a lat/lon within the bbox or "slightly" outside it to try to improve test efficiency
name|lat
operator|=
name|GeoTestUtil
operator|.
name|nextLatitudeAround
argument_list|(
name|bbox
operator|.
name|minLat
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|)
expr_stmt|;
if|if
condition|(
name|bbox
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|lon
operator|=
name|GeoTestUtil
operator|.
name|nextLongitudeAround
argument_list|(
name|bbox
operator|.
name|maxLon
argument_list|,
operator|-
literal|180
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lon
operator|=
name|GeoTestUtil
operator|.
name|nextLongitudeAround
argument_list|(
literal|0
argument_list|,
name|bbox
operator|.
name|minLon
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|lon
operator|=
name|GeoTestUtil
operator|.
name|nextLongitudeAround
argument_list|(
name|bbox
operator|.
name|minLon
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|)
expr_stmt|;
block|}
block|}
name|double
name|distanceMeters
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
comment|// Haversin says it's within the circle:
name|boolean
name|haversinSays
init|=
name|distanceMeters
operator|<=
name|radiusMeters
decl_stmt|;
comment|// BBox says its within the box:
name|boolean
name|bboxSays
decl_stmt|;
if|if
condition|(
name|bbox
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
if|if
condition|(
name|lat
operator|>=
name|bbox
operator|.
name|minLat
operator|&&
name|lat
operator|<=
name|bbox
operator|.
name|maxLat
condition|)
block|{
name|bboxSays
operator|=
name|lon
operator|<=
name|bbox
operator|.
name|maxLon
operator|||
name|lon
operator|>=
name|bbox
operator|.
name|minLon
expr_stmt|;
block|}
else|else
block|{
name|bboxSays
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|bboxSays
operator|=
name|lat
operator|>=
name|bbox
operator|.
name|minLat
operator|&&
name|lat
operator|<=
name|bbox
operator|.
name|maxLat
operator|&&
name|lon
operator|>=
name|bbox
operator|.
name|minLon
operator|&&
name|lon
operator|<=
name|bbox
operator|.
name|maxLon
expr_stmt|;
block|}
if|if
condition|(
name|haversinSays
condition|)
block|{
if|if
condition|(
name|bboxSays
operator|==
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"small="
operator|+
name|useSmallRanges
operator|+
literal|" centerLat="
operator|+
name|centerLat
operator|+
literal|" cetnerLon="
operator|+
name|centerLon
operator|+
literal|" radiusMeters="
operator|+
name|radiusMeters
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  bbox: lat="
operator|+
name|bbox
operator|.
name|minLat
operator|+
literal|" to "
operator|+
name|bbox
operator|.
name|maxLat
operator|+
literal|" lon="
operator|+
name|bbox
operator|.
name|minLon
operator|+
literal|" to "
operator|+
name|bbox
operator|.
name|maxLon
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  point: lat="
operator|+
name|lat
operator|+
literal|" lon="
operator|+
name|lon
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  haversin: "
operator|+
name|distanceMeters
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"point was within the distance according to haversin, but the bbox doesn't contain it"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// it's fine if haversin said it was outside the radius and bbox said it was inside the box
block|}
block|}
block|}
block|}
comment|// similar to testRandomCircleToBBox, but different, less evil, maybe simpler
DECL|method|testBoundingBoxOpto
specifier|public
name|void
name|testBoundingBoxOpto
parameter_list|()
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
name|double
name|lat
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|radius
init|=
literal|50000000
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|GeoRect
name|box
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|radius
argument_list|)
decl_stmt|;
specifier|final
name|GeoRect
name|box1
decl_stmt|;
specifier|final
name|GeoRect
name|box2
decl_stmt|;
if|if
condition|(
name|box
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
name|box1
operator|=
operator|new
name|GeoRect
argument_list|(
name|box
operator|.
name|minLat
argument_list|,
name|box
operator|.
name|maxLat
argument_list|,
operator|-
literal|180
argument_list|,
name|box
operator|.
name|maxLon
argument_list|)
expr_stmt|;
name|box2
operator|=
operator|new
name|GeoRect
argument_list|(
name|box
operator|.
name|minLat
argument_list|,
name|box
operator|.
name|maxLat
argument_list|,
name|box
operator|.
name|minLon
argument_list|,
literal|180
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|box1
operator|=
name|box
expr_stmt|;
name|box2
operator|=
literal|null
expr_stmt|;
block|}
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
name|double
name|lat2
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon2
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
comment|// if the point is within radius, then it should be in our bounding box
if|if
condition|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
operator|<=
name|radius
condition|)
block|{
name|assertTrue
argument_list|(
name|lat
operator|>=
name|box
operator|.
name|minLat
operator|&&
name|lat
operator|<=
name|box
operator|.
name|maxLat
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lon
operator|>=
name|box1
operator|.
name|minLon
operator|&&
name|lon
operator|<=
name|box1
operator|.
name|maxLon
operator|||
operator|(
name|box2
operator|!=
literal|null
operator|&&
name|lon
operator|>=
name|box2
operator|.
name|minLon
operator|&&
name|lon
operator|<=
name|box2
operator|.
name|maxLon
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// test we can use haversinSortKey() for distance queries.
DECL|method|testHaversinOpto
specifier|public
name|void
name|testHaversinOpto
parameter_list|()
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
name|double
name|lat
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|double
name|radius
init|=
literal|50000000
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|GeoRect
name|box
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|radius
argument_list|)
decl_stmt|;
if|if
condition|(
name|box
operator|.
name|maxLon
operator|-
name|lon
operator|<
literal|90
operator|&&
name|lon
operator|-
name|box
operator|.
name|minLon
operator|<
literal|90
condition|)
block|{
name|double
name|minPartialDistance
init|=
name|Math
operator|.
name|max
argument_list|(
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|lat
argument_list|,
name|box
operator|.
name|maxLon
argument_list|)
argument_list|,
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|box
operator|.
name|maxLat
argument_list|,
name|lon
argument_list|)
argument_list|)
decl_stmt|;
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
name|double
name|lat2
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|lon2
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
comment|// if the point is within radius, then it should be<= our sort key
if|if
condition|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
operator|<=
name|radius
condition|)
block|{
name|assertTrue
argument_list|(
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
operator|<=
name|minPartialDistance
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/** Test infinite radius covers whole earth */
DECL|method|testInfiniteRect
specifier|public
name|void
name|testInfiniteRect
parameter_list|()
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
name|double
name|centerLat
init|=
name|GeoTestUtil
operator|.
name|nextLatitude
argument_list|()
decl_stmt|;
name|double
name|centerLon
init|=
name|GeoTestUtil
operator|.
name|nextLongitude
argument_list|()
decl_stmt|;
name|GeoRect
name|rect
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|180.0
argument_list|,
name|rect
operator|.
name|minLon
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|180.0
argument_list|,
name|rect
operator|.
name|maxLon
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|90.0
argument_list|,
name|rect
operator|.
name|minLat
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90.0
argument_list|,
name|rect
operator|.
name|maxLat
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rect
operator|.
name|crossesDateline
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAxisLat
specifier|public
name|void
name|testAxisLat
parameter_list|()
block|{
name|double
name|earthCircumference
init|=
literal|2D
operator|*
name|Math
operator|.
name|PI
operator|*
name|GeoUtils
operator|.
name|EARTH_MEAN_RADIUS_METERS
decl_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|GeoRect
operator|.
name|axisLat
argument_list|(
literal|0
argument_list|,
name|earthCircumference
operator|/
literal|4
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
operator|++
name|i
control|)
block|{
name|boolean
name|reallyBig
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
decl_stmt|;
specifier|final
name|double
name|maxRadius
init|=
name|reallyBig
condition|?
literal|1.1
operator|*
name|earthCircumference
else|:
name|earthCircumference
operator|/
literal|8
decl_stmt|;
specifier|final
name|double
name|radius
init|=
name|maxRadius
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|prevAxisLat
init|=
name|GeoRect
operator|.
name|axisLat
argument_list|(
literal|0.0D
argument_list|,
name|radius
argument_list|)
decl_stmt|;
for|for
control|(
name|double
name|lat
init|=
literal|0.1D
init|;
name|lat
operator|<
literal|90D
condition|;
name|lat
operator|+=
literal|0.1D
control|)
block|{
name|double
name|nextAxisLat
init|=
name|GeoRect
operator|.
name|axisLat
argument_list|(
name|lat
argument_list|,
name|radius
argument_list|)
decl_stmt|;
name|GeoRect
name|bbox
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|lat
argument_list|,
literal|180D
argument_list|,
name|radius
argument_list|)
decl_stmt|;
name|double
name|dist
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|lat
argument_list|,
literal|180D
argument_list|,
name|nextAxisLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextAxisLat
operator|<
name|GeoUtils
operator|.
name|MAX_LAT_INCL
condition|)
block|{
name|assertEquals
argument_list|(
literal|"lat = "
operator|+
name|lat
argument_list|,
name|dist
argument_list|,
name|radius
argument_list|,
literal|0.1D
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"lat = "
operator|+
name|lat
argument_list|,
name|prevAxisLat
operator|<=
name|nextAxisLat
argument_list|)
expr_stmt|;
name|prevAxisLat
operator|=
name|nextAxisLat
expr_stmt|;
block|}
name|prevAxisLat
operator|=
name|GeoRect
operator|.
name|axisLat
argument_list|(
operator|-
literal|0.0D
argument_list|,
name|radius
argument_list|)
expr_stmt|;
for|for
control|(
name|double
name|lat
init|=
operator|-
literal|0.1D
init|;
name|lat
operator|>
operator|-
literal|90D
condition|;
name|lat
operator|-=
literal|0.1D
control|)
block|{
name|double
name|nextAxisLat
init|=
name|GeoRect
operator|.
name|axisLat
argument_list|(
name|lat
argument_list|,
name|radius
argument_list|)
decl_stmt|;
name|GeoRect
name|bbox
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|lat
argument_list|,
literal|180D
argument_list|,
name|radius
argument_list|)
decl_stmt|;
name|double
name|dist
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|lat
argument_list|,
literal|180D
argument_list|,
name|nextAxisLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextAxisLat
operator|>
name|GeoUtils
operator|.
name|MIN_LAT_INCL
condition|)
block|{
name|assertEquals
argument_list|(
literal|"lat = "
operator|+
name|lat
argument_list|,
name|dist
argument_list|,
name|radius
argument_list|,
literal|0.1D
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"lat = "
operator|+
name|lat
argument_list|,
name|prevAxisLat
operator|>=
name|nextAxisLat
argument_list|)
expr_stmt|;
name|prevAxisLat
operator|=
name|nextAxisLat
expr_stmt|;
block|}
block|}
block|}
comment|// TODO: does not really belong here, but we test it like this for now
comment|// we can make a fake IndexReader to send boxes directly to Point visitors instead?
DECL|method|testCircleOpto
specifier|public
name|void
name|testCircleOpto
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
comment|// circle
specifier|final
name|double
name|centerLat
init|=
operator|-
literal|90
operator|+
literal|180.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|double
name|centerLon
init|=
operator|-
literal|180
operator|+
literal|360.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|double
name|radius
init|=
literal|50_000_000D
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|GeoRect
name|box
init|=
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radius
argument_list|)
decl_stmt|;
comment|// TODO: remove this leniency!
if|if
condition|(
name|box
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
operator|--
name|i
expr_stmt|;
comment|// try again...
continue|continue;
block|}
specifier|final
name|double
name|axisLat
init|=
name|GeoRect
operator|.
name|axisLat
argument_list|(
name|centerLat
argument_list|,
name|radius
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|1000
condition|;
operator|++
name|k
control|)
block|{
name|double
index|[]
name|latBounds
init|=
block|{
operator|-
literal|90
block|,
name|box
operator|.
name|minLat
block|,
name|axisLat
block|,
name|box
operator|.
name|maxLat
block|,
literal|90
block|}
decl_stmt|;
name|double
index|[]
name|lonBounds
init|=
block|{
operator|-
literal|180
block|,
name|box
operator|.
name|minLon
block|,
name|centerLon
block|,
name|box
operator|.
name|maxLon
block|,
literal|180
block|}
decl_stmt|;
comment|// first choose an upper left corner
name|int
name|maxLatRow
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|double
name|latMax
init|=
name|randomInRange
argument_list|(
name|latBounds
index|[
name|maxLatRow
index|]
argument_list|,
name|latBounds
index|[
name|maxLatRow
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
name|int
name|minLonCol
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|double
name|lonMin
init|=
name|randomInRange
argument_list|(
name|lonBounds
index|[
name|minLonCol
index|]
argument_list|,
name|lonBounds
index|[
name|minLonCol
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
comment|// now choose a lower right corner
name|int
name|minLatMaxRow
init|=
name|maxLatRow
operator|==
literal|3
condition|?
literal|3
else|:
name|maxLatRow
operator|+
literal|1
decl_stmt|;
comment|// make sure it will at least cross into the bbox
name|int
name|minLatRow
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|minLatMaxRow
argument_list|)
decl_stmt|;
name|double
name|latMin
init|=
name|randomInRange
argument_list|(
name|latBounds
index|[
name|minLatRow
index|]
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|latBounds
index|[
name|minLatRow
operator|+
literal|1
index|]
argument_list|,
name|latMax
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|maxLonMinCol
init|=
name|Math
operator|.
name|max
argument_list|(
name|minLonCol
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// make sure it will at least cross into the bbox
name|int
name|maxLonCol
init|=
name|maxLonMinCol
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
operator|-
name|maxLonMinCol
argument_list|)
decl_stmt|;
name|double
name|lonMax
init|=
name|randomInRange
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|lonBounds
index|[
name|maxLonCol
index|]
argument_list|,
name|lonMin
argument_list|)
argument_list|,
name|lonBounds
index|[
name|maxLonCol
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
assert|assert
name|latMax
operator|>=
name|latMin
assert|;
assert|assert
name|lonMax
operator|>=
name|lonMin
assert|;
if|if
condition|(
name|isDisjoint
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radius
argument_list|,
name|axisLat
argument_list|,
name|latMin
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|,
name|lonMax
argument_list|)
condition|)
block|{
comment|// intersects says false: test a ton of points
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|200
condition|;
name|j
operator|++
control|)
block|{
name|double
name|lat
init|=
name|latMin
operator|+
operator|(
name|latMax
operator|-
name|latMin
operator|)
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|lon
init|=
name|lonMin
operator|+
operator|(
name|lonMax
operator|-
name|lonMin
operator|)
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// explicitly test an edge
name|int
name|edge
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|edge
operator|==
literal|0
condition|)
block|{
name|lat
operator|=
name|latMin
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|edge
operator|==
literal|1
condition|)
block|{
name|lat
operator|=
name|latMax
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|edge
operator|==
literal|2
condition|)
block|{
name|lon
operator|=
name|lonMin
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|edge
operator|==
literal|3
condition|)
block|{
name|lon
operator|=
name|lonMax
expr_stmt|;
block|}
block|}
name|double
name|distance
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"\nisDisjoint(\n"
operator|+
literal|"centerLat=%s\n"
operator|+
literal|"centerLon=%s\n"
operator|+
literal|"radius=%s\n"
operator|+
literal|"latMin=%s\n"
operator|+
literal|"latMax=%s\n"
operator|+
literal|"lonMin=%s\n"
operator|+
literal|"lonMax=%s) == false BUT\n"
operator|+
literal|"haversin(%s, %s, %s, %s) = %s\nbbox=%s"
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radius
argument_list|,
name|latMin
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|,
name|lonMax
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|distance
argument_list|,
name|GeoRect
operator|.
name|fromPointDistance
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radius
argument_list|)
argument_list|)
argument_list|,
name|distance
operator|>
name|radius
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
name|GeoTestUtil
operator|.
name|toWebGLEarth
argument_list|(
name|latMin
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|,
name|lonMax
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radius
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|randomInRange
specifier|static
name|double
name|randomInRange
parameter_list|(
name|double
name|min
parameter_list|,
name|double
name|max
parameter_list|)
block|{
return|return
name|min
operator|+
operator|(
name|max
operator|-
name|min
operator|)
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
return|;
block|}
DECL|method|isDisjoint
specifier|static
name|boolean
name|isDisjoint
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radius
parameter_list|,
name|double
name|axisLat
parameter_list|,
name|double
name|latMin
parameter_list|,
name|double
name|latMax
parameter_list|,
name|double
name|lonMin
parameter_list|,
name|double
name|lonMax
parameter_list|)
block|{
if|if
condition|(
operator|(
name|centerLon
argument_list|<
name|lonMin
operator|||
name|centerLon
argument_list|>
name|lonMax
operator|)
operator|&&
operator|(
name|axisLat
operator|+
name|GeoRect
operator|.
name|AXISLAT_ERROR
argument_list|<
name|latMin
operator|||
name|axisLat
operator|-
name|GeoRect
operator|.
name|AXISLAT_ERROR
argument_list|>
name|latMax
operator|)
condition|)
block|{
comment|// circle not fully inside / crossing axis
if|if
condition|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|latMin
argument_list|,
name|lonMin
argument_list|)
operator|>
name|radius
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|latMin
argument_list|,
name|lonMax
argument_list|)
operator|>
name|radius
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|)
operator|>
name|radius
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|latMax
argument_list|,
name|lonMax
argument_list|)
operator|>
name|radius
condition|)
block|{
comment|// no points inside
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
