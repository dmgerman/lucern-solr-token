begin_unit
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
name|util
operator|.
name|GeoProjectionUtils
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
name|ToStringUtils
import|;
end_import
begin_comment
comment|/** Implements a simple point distance query on a GeoPoint field. This is based on  * {@link org.apache.lucene.search.GeoPointInBBoxQuery} and is implemented using a two phase approach. First,  * like {@code GeoPointInBBoxQueryImpl} candidate terms are queried using the numeric ranges based on  * the morton codes of the min and max lat/lon pairs that intersect the boundary of the point-radius  * circle (see {@link org.apache.lucene.util.GeoUtils#lineCrossesSphere}. Terms  * passing this initial filter are then passed to a secondary {@code postFilter} method that verifies whether the  * decoded lat/lon point fall within the specified query distance (see {@link org.apache.lucene.util.SloppyMath#haversin}.  * All morton value comparisons are subject to the same precision tolerance defined in  * {@value org.apache.lucene.util.GeoUtils#TOLERANCE} and distance comparisons are subject to the accuracy of the  * haversine formula (from R.W. Sinnott, "Virtues of the Haversine", Sky and Telescope, vol. 68, no. 2, 1984, p. 159)  *  *  * Note: This query currently uses haversine which is a sloppy distance calculation (see above reference). For large  * queries one can expect upwards of 400m error. Vincenty shrinks this to ~40m error but pays a penalty for computing  * using the spheroid  *  *    @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointDistanceQuery
specifier|public
specifier|final
class|class
name|GeoPointDistanceQuery
extends|extends
name|GeoPointInBBoxQuery
block|{
DECL|field|centerLon
specifier|protected
specifier|final
name|double
name|centerLon
decl_stmt|;
DECL|field|centerLat
specifier|protected
specifier|final
name|double
name|centerLat
decl_stmt|;
DECL|field|radius
specifier|protected
specifier|final
name|double
name|radius
decl_stmt|;
comment|/** NOTE: radius is in meters. */
DECL|method|GeoPointDistanceQuery
specifier|public
name|GeoPointDistanceQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|radius
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|computeBBox
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|radius
argument_list|)
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|radius
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoPointDistanceQuery
specifier|private
name|GeoPointDistanceQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|GeoBoundingBox
name|bbox
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|radius
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|bbox
operator|.
name|minLon
argument_list|,
name|bbox
operator|.
name|minLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|)
expr_stmt|;
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|centerLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid centerLon "
operator|+
name|centerLon
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|centerLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid centerLat "
operator|+
name|centerLat
argument_list|)
throw|;
block|}
name|this
operator|.
name|centerLon
operator|=
name|centerLon
expr_stmt|;
name|this
operator|.
name|centerLat
operator|=
name|centerLat
expr_stmt|;
name|this
operator|.
name|radius
operator|=
name|radius
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bqb
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|GeoPointDistanceQueryImpl
name|left
init|=
operator|new
name|GeoPointDistanceQueryImpl
argument_list|(
name|field
argument_list|,
name|this
argument_list|,
operator|new
name|GeoBoundingBox
argument_list|(
operator|-
literal|180.0D
argument_list|,
name|maxLon
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
argument_list|)
decl_stmt|;
name|left
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|left
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|GeoPointDistanceQueryImpl
name|right
init|=
operator|new
name|GeoPointDistanceQueryImpl
argument_list|(
name|field
argument_list|,
name|this
argument_list|,
operator|new
name|GeoBoundingBox
argument_list|(
name|minLon
argument_list|,
literal|180.0D
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
argument_list|)
decl_stmt|;
name|right
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|right
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bqb
operator|.
name|build
argument_list|()
return|;
block|}
return|return
operator|new
name|GeoPointDistanceQueryImpl
argument_list|(
name|field
argument_list|,
name|this
argument_list|,
operator|new
name|GeoBoundingBox
argument_list|(
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|maxLon
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLat
argument_list|)
argument_list|)
return|;
block|}
DECL|method|computeBBox
specifier|static
name|GeoBoundingBox
name|computeBBox
parameter_list|(
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|radius
parameter_list|)
block|{
name|double
index|[]
name|t
init|=
name|GeoProjectionUtils
operator|.
name|pointFromLonLatBearing
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
literal|0
argument_list|,
name|radius
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|double
index|[]
name|r
init|=
name|GeoProjectionUtils
operator|.
name|pointFromLonLatBearing
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
literal|90
argument_list|,
name|radius
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|double
index|[]
name|b
init|=
name|GeoProjectionUtils
operator|.
name|pointFromLonLatBearing
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
literal|180
argument_list|,
name|radius
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|double
index|[]
name|l
init|=
name|GeoProjectionUtils
operator|.
name|pointFromLonLatBearing
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
literal|270
argument_list|,
name|radius
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeoBoundingBox
argument_list|(
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|l
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|r
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|b
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|t
index|[
literal|1
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|GeoPointDistanceQuery
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|GeoPointDistanceQuery
name|that
init|=
operator|(
name|GeoPointDistanceQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|centerLat
argument_list|,
name|centerLat
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|centerLon
argument_list|,
name|centerLon
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|radius
argument_list|,
name|radius
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|centerLon
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|centerLat
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|radius
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|" Center: ["
argument_list|)
operator|.
name|append
argument_list|(
name|centerLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|centerLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Distance: "
argument_list|)
operator|.
name|append
argument_list|(
name|radius
argument_list|)
operator|.
name|append
argument_list|(
literal|" m"
argument_list|)
operator|.
name|append
argument_list|(
literal|" Lower Left: ["
argument_list|)
operator|.
name|append
argument_list|(
name|minLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|minLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Upper Right: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|maxLat
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getCenterLon
specifier|public
name|double
name|getCenterLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|centerLon
return|;
block|}
DECL|method|getCenterLat
specifier|public
name|double
name|getCenterLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|centerLat
return|;
block|}
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
block|{
return|return
name|this
operator|.
name|radius
return|;
block|}
block|}
end_class
end_unit
