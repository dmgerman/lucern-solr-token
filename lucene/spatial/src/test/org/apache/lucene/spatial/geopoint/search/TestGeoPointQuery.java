begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.geopoint.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
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
name|search
operator|.
name|Query
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
name|GeoEncodingUtils
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
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
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
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|TermEncoding
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
begin_comment
comment|/**  * random testing for GeoPoint query logic  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TestGeoPointQuery
specifier|public
class|class
name|TestGeoPointQuery
extends|extends
name|BaseGeoPointTestCase
block|{
annotation|@
name|Override
DECL|method|quantizeLat
specifier|protected
name|double
name|quantizeLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|lat
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|quantizeLon
specifier|protected
name|double
name|quantizeLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
literal|0
argument_list|,
name|lon
argument_list|)
argument_list|)
return|;
block|}
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
name|GeoPointField
argument_list|(
name|field
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
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
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
return|return
operator|new
name|GeoPointInBBoxQuery
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
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
return|return
operator|new
name|GeoPointDistanceQuery
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|)
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
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
return|return
operator|new
name|GeoPointInPolygonQuery
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|polygons
argument_list|)
return|;
block|}
comment|// TODO: remove these once we get tests passing!
annotation|@
name|Override
DECL|method|nextLongitude
specifier|protected
name|double
name|nextLongitude
parameter_list|()
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextLongitude
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextLongitudeNear
specifier|protected
name|double
name|nextLongitudeNear
parameter_list|(
name|double
name|other
parameter_list|)
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextLongitudeNear
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextLatitude
specifier|protected
name|double
name|nextLatitude
parameter_list|()
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextLatitude
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextLatitudeNear
specifier|protected
name|double
name|nextLatitudeNear
parameter_list|(
name|double
name|other
parameter_list|)
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextLatitudeNear
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextBox
specifier|protected
name|Rectangle
name|nextBox
parameter_list|()
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextBox
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextBoxNear
specifier|protected
name|Rectangle
name|nextBoxNear
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextBoxNear
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextPolygon
specifier|protected
name|Polygon
name|nextPolygon
parameter_list|()
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextPolygon
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextPolygonNear
specifier|protected
name|Polygon
name|nextPolygonNear
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
return|return
name|GeoPointTestUtil
operator|.
name|nextPolygonNear
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
return|;
block|}
block|}
end_class
end_unit
