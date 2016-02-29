begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|document
operator|.
name|LatLonPoint
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
name|document
operator|.
name|Document
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
name|util
operator|.
name|BaseGeoPointTestCase
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
name|util
operator|.
name|GeoDistanceUtils
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
name|util
operator|.
name|GeoRect
import|;
end_import
begin_class
DECL|class|TestLatLonPointQueries
specifier|public
class|class
name|TestLatLonPointQueries
extends|extends
name|BaseGeoPointTestCase
block|{
comment|// todo deconflict GeoPoint and BKD encoding methods and error tolerance
DECL|field|BKD_TOLERANCE
specifier|public
specifier|static
specifier|final
name|double
name|BKD_TOLERANCE
init|=
literal|1e-7
decl_stmt|;
DECL|field|ENCODING_TOLERANCE
specifier|public
specifier|static
specifier|final
name|double
name|ENCODING_TOLERANCE
init|=
literal|1e-7
decl_stmt|;
annotation|@
name|Override
DECL|method|addPointToDoc
specifier|protected
name|void
name|addPointToDoc
parameter_list|(
name|String
name|field
parameter_list|,
name|Document
name|doc
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
name|field
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newRectQuery
specifier|protected
name|Query
name|newRectQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|GeoRect
name|rect
parameter_list|)
block|{
return|return
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
name|field
argument_list|,
name|rect
operator|.
name|minLat
argument_list|,
name|rect
operator|.
name|maxLat
argument_list|,
name|rect
operator|.
name|minLon
argument_list|,
name|rect
operator|.
name|maxLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newDistanceQuery
specifier|protected
name|Query
name|newDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
comment|// TODO: fix this to be debuggable before enabling!
comment|// return LatLonPoint.newDistanceQuery(field, centerLat, centerLon, radiusMeters);
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|newDistanceRangeQuery
specifier|protected
name|Query
name|newDistanceRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|minRadiusMeters
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|newPolygonQuery
specifier|protected
name|Query
name|newPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
index|[]
name|lats
parameter_list|,
name|double
index|[]
name|lons
parameter_list|)
block|{
return|return
name|LatLonPoint
operator|.
name|newPolygonQuery
argument_list|(
name|FIELD_NAME
argument_list|,
name|lats
argument_list|,
name|lons
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rectContainsPoint
specifier|protected
name|Boolean
name|rectContainsPoint
parameter_list|(
name|GeoRect
name|rect
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
assert|assert
name|Double
operator|.
name|isNaN
argument_list|(
name|pointLat
argument_list|)
operator|==
literal|false
assert|;
comment|// false positive/negatives due to quantization error exist for both rectangles and polygons
if|if
condition|(
name|compare
argument_list|(
name|pointLat
argument_list|,
name|rect
operator|.
name|minLat
argument_list|)
operator|==
literal|0
operator|||
name|compare
argument_list|(
name|pointLat
argument_list|,
name|rect
operator|.
name|maxLat
argument_list|)
operator|==
literal|0
operator|||
name|compare
argument_list|(
name|pointLon
argument_list|,
name|rect
operator|.
name|minLon
argument_list|)
operator|==
literal|0
operator|||
name|compare
argument_list|(
name|pointLon
argument_list|,
name|rect
operator|.
name|maxLon
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|rectLatMinEnc
init|=
name|LatLonPoint
operator|.
name|encodeLat
argument_list|(
name|rect
operator|.
name|minLat
argument_list|)
decl_stmt|;
name|int
name|rectLatMaxEnc
init|=
name|LatLonPoint
operator|.
name|encodeLat
argument_list|(
name|rect
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|int
name|rectLonMinEnc
init|=
name|LatLonPoint
operator|.
name|encodeLon
argument_list|(
name|rect
operator|.
name|minLon
argument_list|)
decl_stmt|;
name|int
name|rectLonMaxEnc
init|=
name|LatLonPoint
operator|.
name|encodeLon
argument_list|(
name|rect
operator|.
name|maxLon
argument_list|)
decl_stmt|;
name|int
name|pointLatEnc
init|=
name|LatLonPoint
operator|.
name|encodeLat
argument_list|(
name|pointLat
argument_list|)
decl_stmt|;
name|int
name|pointLonEnc
init|=
name|LatLonPoint
operator|.
name|encodeLon
argument_list|(
name|pointLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|rect
operator|.
name|minLon
operator|<
name|rect
operator|.
name|maxLon
condition|)
block|{
return|return
name|pointLatEnc
operator|>=
name|rectLatMinEnc
operator|&&
name|pointLatEnc
operator|<=
name|rectLatMaxEnc
operator|&&
name|pointLonEnc
operator|>=
name|rectLonMinEnc
operator|&&
name|pointLonEnc
operator|<=
name|rectLonMaxEnc
return|;
block|}
else|else
block|{
comment|// Rect crosses dateline:
return|return
name|pointLatEnc
operator|>=
name|rectLatMinEnc
operator|&&
name|pointLatEnc
operator|<=
name|rectLatMaxEnc
operator|&&
operator|(
name|pointLonEnc
operator|>=
name|rectLonMinEnc
operator|||
name|pointLonEnc
operator|<=
name|rectLonMaxEnc
operator|)
return|;
block|}
block|}
comment|// todo reconcile with GeoUtils (see LUCENE-6996)
DECL|method|compare
specifier|public
specifier|static
name|double
name|compare
parameter_list|(
specifier|final
name|double
name|v1
parameter_list|,
specifier|final
name|double
name|v2
parameter_list|)
block|{
specifier|final
name|double
name|delta
init|=
name|v1
operator|-
name|v2
decl_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|delta
argument_list|)
operator|<=
name|BKD_TOLERANCE
condition|?
literal|0
else|:
name|delta
return|;
block|}
annotation|@
name|Override
DECL|method|polyRectContainsPoint
specifier|protected
name|Boolean
name|polyRectContainsPoint
parameter_list|(
name|GeoRect
name|rect
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
comment|// TODO write better random polygon tests
return|return
name|rectContainsPoint
argument_list|(
name|rect
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|circleContainsPoint
specifier|protected
name|Boolean
name|circleContainsPoint
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
name|double
name|distanceMeters
init|=
name|GeoDistanceUtils
operator|.
name|haversin
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|distanceMeters
operator|<=
name|radiusMeters
decl_stmt|;
comment|//System.out.println("  shouldMatch?  centerLon=" + centerLon + " centerLat=" + centerLat + " pointLon=" + pointLon + " pointLat=" + pointLat + " result=" + result + " distanceMeters=" + (distanceKM * 1000));
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|distanceRangeContainsPoint
specifier|protected
name|Boolean
name|distanceRangeContainsPoint
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|minRadiusMeters
parameter_list|,
name|double
name|radiusMeters
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
specifier|final
name|double
name|d
init|=
name|GeoDistanceUtils
operator|.
name|haversin
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
decl_stmt|;
return|return
name|d
operator|>=
name|minRadiusMeters
operator|&&
name|d
operator|<=
name|radiusMeters
return|;
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
name|latEnc
init|=
name|LatLonPoint
operator|.
name|decodeLat
argument_list|(
name|LatLonPoint
operator|.
name|encodeLat
argument_list|(
name|lat
argument_list|)
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
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|double
name|lon
init|=
name|randomLon
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|double
name|lonEnc
init|=
name|LatLonPoint
operator|.
name|decodeLon
argument_list|(
name|LatLonPoint
operator|.
name|encodeLon
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
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
name|ENCODING_TOLERANCE
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
name|double
name|latEnc
init|=
name|LatLonPoint
operator|.
name|decodeLat
argument_list|(
name|LatLonPoint
operator|.
name|encodeLat
argument_list|(
name|lat
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|lonEnc
init|=
name|LatLonPoint
operator|.
name|decodeLon
argument_list|(
name|LatLonPoint
operator|.
name|encodeLon
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|latEnc2
init|=
name|LatLonPoint
operator|.
name|decodeLat
argument_list|(
name|LatLonPoint
operator|.
name|encodeLat
argument_list|(
name|latEnc
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|lonEnc2
init|=
name|LatLonPoint
operator|.
name|decodeLon
argument_list|(
name|LatLonPoint
operator|.
name|encodeLon
argument_list|(
name|lonEnc
argument_list|)
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
block|}
end_class
end_unit
