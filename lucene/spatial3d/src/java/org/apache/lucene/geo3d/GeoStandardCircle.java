begin_unit
begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Circular area with a center and radius.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoStandardCircle
specifier|public
class|class
name|GeoStandardCircle
extends|extends
name|GeoBaseCircle
block|{
comment|/** Center of circle */
DECL|field|center
specifier|protected
specifier|final
name|GeoPoint
name|center
decl_stmt|;
comment|/** Cutoff angle of circle (not quite the same thing as radius) */
DECL|field|cutoffAngle
specifier|protected
specifier|final
name|double
name|cutoffAngle
decl_stmt|;
comment|/** The plane describing the circle (really an ellipse on a non-spherical world) */
DECL|field|circlePlane
specifier|protected
specifier|final
name|SidedPlane
name|circlePlane
decl_stmt|;
comment|/** A point that is on the world and on the circle plane */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/** Notable points for a circle -- there aren't any */
DECL|field|circlePoints
specifier|protected
specifier|static
specifier|final
name|GeoPoint
index|[]
name|circlePoints
init|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
decl_stmt|;
comment|/** Constructor.    *@param planetModel is the planet model.    *@param lat is the center latitude.    *@param lon is the center longitude.    *@param cutoffAngle is the cutoff angle for the circle.    */
DECL|method|GeoStandardCircle
specifier|public
name|GeoStandardCircle
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|cutoffAngle
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
if|if
condition|(
name|lat
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|lat
argument_list|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Latitude out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|lon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|lon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|cutoffAngle
argument_list|<
literal|0.0
operator|||
name|cutoffAngle
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cutoff angle out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|cutoffAngle
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cutoff angle cannot be effectively zero"
argument_list|)
throw|;
name|this
operator|.
name|center
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
comment|// In an ellipsoidal world, cutoff distances make no sense, unfortunately.  Only membership
comment|// can be used to make in/out determination.
name|this
operator|.
name|cutoffAngle
operator|=
name|cutoffAngle
expr_stmt|;
comment|// Compute two points on the circle, with the right angle from the center.  We'll use these
comment|// to obtain the perpendicular plane to the circle.
name|double
name|upperLat
init|=
name|lat
operator|+
name|cutoffAngle
decl_stmt|;
name|double
name|upperLon
init|=
name|lon
decl_stmt|;
if|if
condition|(
name|upperLat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
block|{
name|upperLon
operator|+=
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|upperLon
operator|>
name|Math
operator|.
name|PI
condition|)
name|upperLon
operator|-=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
name|upperLat
operator|=
name|Math
operator|.
name|PI
operator|-
name|upperLat
expr_stmt|;
block|}
name|double
name|lowerLat
init|=
name|lat
operator|-
name|cutoffAngle
decl_stmt|;
name|double
name|lowerLon
init|=
name|lon
decl_stmt|;
if|if
condition|(
name|lowerLat
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
block|{
name|lowerLon
operator|+=
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|lowerLon
operator|>
name|Math
operator|.
name|PI
condition|)
name|lowerLon
operator|-=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
name|lowerLat
operator|=
operator|-
name|Math
operator|.
name|PI
operator|-
name|lowerLat
expr_stmt|;
block|}
specifier|final
name|GeoPoint
name|upperPoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|upperLat
argument_list|,
name|upperLon
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|lowerPoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|lowerLat
argument_list|,
name|lowerLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|cutoffAngle
operator|-
name|Math
operator|.
name|PI
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
comment|// Circle is the whole world
name|this
operator|.
name|circlePlane
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// Construct normal plane
specifier|final
name|Plane
name|normalPlane
init|=
name|Plane
operator|.
name|constructNormalizedZPlane
argument_list|(
name|upperPoint
argument_list|,
name|lowerPoint
argument_list|,
name|center
argument_list|)
decl_stmt|;
comment|// Construct a sided plane that goes through the two points and whose normal is in the normalPlane.
name|this
operator|.
name|circlePlane
operator|=
name|SidedPlane
operator|.
name|constructNormalizedPerpendicularSidedPlane
argument_list|(
name|center
argument_list|,
name|normalPlane
argument_list|,
name|upperPoint
argument_list|,
name|lowerPoint
argument_list|)
expr_stmt|;
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Couldn't construct circle plane, probably too small?  Cutoff angle = "
operator|+
name|cutoffAngle
operator|+
literal|"; upperPoint = "
operator|+
name|upperPoint
operator|+
literal|"; lowerPoint = "
operator|+
name|lowerPoint
argument_list|)
throw|;
specifier|final
name|GeoPoint
name|recomputedIntersectionPoint
init|=
name|circlePlane
operator|.
name|getSampleIntersectionPoint
argument_list|(
name|planetModel
argument_list|,
name|normalPlane
argument_list|)
decl_stmt|;
if|if
condition|(
name|recomputedIntersectionPoint
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Couldn't construct intersection point, probably circle too small?  Plane = "
operator|+
name|circlePlane
argument_list|)
throw|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|recomputedIntersectionPoint
block|}
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
block|{
return|return
name|cutoffAngle
return|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
return|return
name|center
return|;
block|}
annotation|@
name|Override
DECL|method|distance
specifier|protected
name|double
name|distance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
return|return
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|this
operator|.
name|center
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|outsideDistance
specifier|protected
name|double
name|outsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
return|return
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|circlePlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Fastest way of determining membership
return|return
name|circlePlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|public
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|edgePoints
return|;
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|p
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|notablePoints
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|circlePlane
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|p
argument_list|,
name|notablePoints
argument_list|,
name|circlePoints
argument_list|,
name|bounds
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
if|if
condition|(
name|circlePlane
operator|==
literal|null
condition|)
block|{
comment|// Entire world; should already be covered
return|return;
block|}
name|bounds
operator|.
name|addPoint
argument_list|(
name|center
argument_list|)
expr_stmt|;
name|bounds
operator|.
name|addPlane
argument_list|(
name|planetModel
argument_list|,
name|circlePlane
argument_list|)
expr_stmt|;
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoStandardCircle
operator|)
condition|)
return|return
literal|false
return|;
name|GeoStandardCircle
name|other
init|=
operator|(
name|GeoStandardCircle
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
name|other
operator|.
name|center
operator|.
name|equals
argument_list|(
name|center
argument_list|)
operator|&&
name|other
operator|.
name|cutoffAngle
operator|==
name|cutoffAngle
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
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|center
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|long
name|temp
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|cutoffAngle
argument_list|)
decl_stmt|;
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
parameter_list|()
block|{
return|return
literal|"GeoStandardCircle: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", center="
operator|+
name|center
operator|+
literal|", radius="
operator|+
name|cutoffAngle
operator|+
literal|"("
operator|+
name|cutoffAngle
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|")}"
return|;
block|}
block|}
end_class
end_unit
