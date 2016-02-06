begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/**  * A 3d vector in space, not necessarily  * going through the origin.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Vector
specifier|public
class|class
name|Vector
block|{
comment|/**    * Values that are all considered to be essentially zero have a magnitude    * less than this.    */
DECL|field|MINIMUM_RESOLUTION
specifier|public
specifier|static
specifier|final
name|double
name|MINIMUM_RESOLUTION
init|=
literal|1.0e-12
decl_stmt|;
comment|/**    * For squared quantities, the bound is squared too.    */
DECL|field|MINIMUM_RESOLUTION_SQUARED
specifier|public
specifier|static
specifier|final
name|double
name|MINIMUM_RESOLUTION_SQUARED
init|=
name|MINIMUM_RESOLUTION
operator|*
name|MINIMUM_RESOLUTION
decl_stmt|;
comment|/**    * For cubed quantities, cube the bound.    */
DECL|field|MINIMUM_RESOLUTION_CUBED
specifier|public
specifier|static
specifier|final
name|double
name|MINIMUM_RESOLUTION_CUBED
init|=
name|MINIMUM_RESOLUTION_SQUARED
operator|*
name|MINIMUM_RESOLUTION
decl_stmt|;
comment|/** The x value */
DECL|field|x
specifier|public
specifier|final
name|double
name|x
decl_stmt|;
comment|/** The y value */
DECL|field|y
specifier|public
specifier|final
name|double
name|y
decl_stmt|;
comment|/** The z value */
DECL|field|z
specifier|public
specifier|final
name|double
name|z
decl_stmt|;
comment|/**    * Construct from (U.S.) x,y,z coordinates.    *@param x is the x value.    *@param y is the y value.    *@param z is the z value.    */
DECL|method|Vector
specifier|public
name|Vector
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
name|this
operator|.
name|z
operator|=
name|z
expr_stmt|;
block|}
comment|/**    * Construct a vector that is perpendicular to    * two other (non-zero) vectors.  If the vectors are parallel,    * IllegalArgumentException will be thrown.    * Produces a normalized final vector.    *    * @param A is the first vector    * @param B is the second    */
DECL|method|Vector
specifier|public
name|Vector
parameter_list|(
specifier|final
name|Vector
name|A
parameter_list|,
specifier|final
name|Vector
name|B
parameter_list|)
block|{
comment|// x = u2v3 - u3v2
comment|// y = u3v1 - u1v3
comment|// z = u1v2 - u2v1
specifier|final
name|double
name|thisX
init|=
name|A
operator|.
name|y
operator|*
name|B
operator|.
name|z
operator|-
name|A
operator|.
name|z
operator|*
name|B
operator|.
name|y
decl_stmt|;
specifier|final
name|double
name|thisY
init|=
name|A
operator|.
name|z
operator|*
name|B
operator|.
name|x
operator|-
name|A
operator|.
name|x
operator|*
name|B
operator|.
name|z
decl_stmt|;
specifier|final
name|double
name|thisZ
init|=
name|A
operator|.
name|x
operator|*
name|B
operator|.
name|y
operator|-
name|A
operator|.
name|y
operator|*
name|B
operator|.
name|x
decl_stmt|;
specifier|final
name|double
name|magnitude
init|=
name|magnitude
argument_list|(
name|thisX
argument_list|,
name|thisY
argument_list|,
name|thisZ
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|magnitude
argument_list|)
operator|<
name|MINIMUM_RESOLUTION
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Degenerate/parallel vector constructed"
argument_list|)
throw|;
block|}
specifier|final
name|double
name|inverseMagnitude
init|=
literal|1.0
operator|/
name|magnitude
decl_stmt|;
name|this
operator|.
name|x
operator|=
name|thisX
operator|*
name|inverseMagnitude
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|thisY
operator|*
name|inverseMagnitude
expr_stmt|;
name|this
operator|.
name|z
operator|=
name|thisZ
operator|*
name|inverseMagnitude
expr_stmt|;
block|}
comment|/** Compute a magnitude of an x,y,z value.    */
DECL|method|magnitude
specifier|public
specifier|static
name|double
name|magnitude
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
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|x
operator|*
name|x
operator|+
name|y
operator|*
name|y
operator|+
name|z
operator|*
name|z
argument_list|)
return|;
block|}
comment|/**    * Compute a normalized unit vector based on the current vector.    *    * @return the normalized vector, or null if the current vector has    * a magnitude of zero.    */
DECL|method|normalize
specifier|public
name|Vector
name|normalize
parameter_list|()
block|{
name|double
name|denom
init|=
name|magnitude
argument_list|()
decl_stmt|;
if|if
condition|(
name|denom
operator|<
name|MINIMUM_RESOLUTION
condition|)
comment|// Degenerate, can't normalize
return|return
literal|null
return|;
name|double
name|normFactor
init|=
literal|1.0
operator|/
name|denom
decl_stmt|;
return|return
operator|new
name|Vector
argument_list|(
name|x
operator|*
name|normFactor
argument_list|,
name|y
operator|*
name|normFactor
argument_list|,
name|z
operator|*
name|normFactor
argument_list|)
return|;
block|}
comment|/**    * Do a dot product.    *    * @param v is the vector to multiply.    * @return the result.    */
DECL|method|dotProduct
specifier|public
name|double
name|dotProduct
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|this
operator|.
name|x
operator|*
name|v
operator|.
name|x
operator|+
name|this
operator|.
name|y
operator|*
name|v
operator|.
name|y
operator|+
name|this
operator|.
name|z
operator|*
name|v
operator|.
name|z
return|;
block|}
comment|/**    * Do a dot product.    *    * @param x is the x value of the vector to multiply.    * @param y is the y value of the vector to multiply.    * @param z is the z value of the vector to multiply.    * @return the result.    */
DECL|method|dotProduct
specifier|public
name|double
name|dotProduct
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
return|return
name|this
operator|.
name|x
operator|*
name|x
operator|+
name|this
operator|.
name|y
operator|*
name|y
operator|+
name|this
operator|.
name|z
operator|*
name|z
return|;
block|}
comment|/**    * Determine if this vector, taken from the origin,    * describes a point within a set of planes.    *    * @param bounds     is the first part of the set of planes.    * @param moreBounds is the second part of the set of planes.    * @return true if the point is within the bounds.    */
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|Membership
index|[]
name|bounds
parameter_list|,
specifier|final
name|Membership
index|[]
name|moreBounds
parameter_list|)
block|{
comment|// Return true if the point described is within all provided bounds
comment|//System.err.println("  checking if "+this+" is within bounds");
for|for
control|(
name|Membership
name|bound
range|:
name|bounds
control|)
block|{
if|if
condition|(
name|bound
operator|!=
literal|null
operator|&&
operator|!
name|bound
operator|.
name|isWithin
argument_list|(
name|this
argument_list|)
condition|)
block|{
comment|//System.err.println("    NOT within "+bound);
return|return
literal|false
return|;
block|}
block|}
for|for
control|(
name|Membership
name|bound
range|:
name|moreBounds
control|)
block|{
if|if
condition|(
name|bound
operator|!=
literal|null
operator|&&
operator|!
name|bound
operator|.
name|isWithin
argument_list|(
name|this
argument_list|)
condition|)
block|{
comment|//System.err.println("    NOT within "+bound);
return|return
literal|false
return|;
block|}
block|}
comment|//System.err.println("    is within");
return|return
literal|true
return|;
block|}
comment|/**    * Translate vector.    */
DECL|method|translate
specifier|public
name|Vector
name|translate
parameter_list|(
specifier|final
name|double
name|xOffset
parameter_list|,
specifier|final
name|double
name|yOffset
parameter_list|,
specifier|final
name|double
name|zOffset
parameter_list|)
block|{
return|return
operator|new
name|Vector
argument_list|(
name|x
operator|-
name|xOffset
argument_list|,
name|y
operator|-
name|yOffset
argument_list|,
name|z
operator|-
name|zOffset
argument_list|)
return|;
block|}
comment|/**    * Rotate vector counter-clockwise in x-y by an angle.    */
DECL|method|rotateXY
specifier|public
name|Vector
name|rotateXY
parameter_list|(
specifier|final
name|double
name|angle
parameter_list|)
block|{
return|return
name|rotateXY
argument_list|(
name|Math
operator|.
name|sin
argument_list|(
name|angle
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|angle
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Rotate vector counter-clockwise in x-y by an angle, expressed as sin and cos.    */
DECL|method|rotateXY
specifier|public
name|Vector
name|rotateXY
parameter_list|(
specifier|final
name|double
name|sinAngle
parameter_list|,
specifier|final
name|double
name|cosAngle
parameter_list|)
block|{
return|return
operator|new
name|Vector
argument_list|(
name|x
operator|*
name|cosAngle
operator|-
name|y
operator|*
name|sinAngle
argument_list|,
name|x
operator|*
name|sinAngle
operator|+
name|y
operator|*
name|cosAngle
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/**    * Rotate vector counter-clockwise in x-z by an angle.    */
DECL|method|rotateXZ
specifier|public
name|Vector
name|rotateXZ
parameter_list|(
specifier|final
name|double
name|angle
parameter_list|)
block|{
return|return
name|rotateXZ
argument_list|(
name|Math
operator|.
name|sin
argument_list|(
name|angle
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|angle
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Rotate vector counter-clockwise in x-z by an angle, expressed as sin and cos.    */
DECL|method|rotateXZ
specifier|public
name|Vector
name|rotateXZ
parameter_list|(
specifier|final
name|double
name|sinAngle
parameter_list|,
specifier|final
name|double
name|cosAngle
parameter_list|)
block|{
return|return
operator|new
name|Vector
argument_list|(
name|x
operator|*
name|cosAngle
operator|-
name|z
operator|*
name|sinAngle
argument_list|,
name|y
argument_list|,
name|x
operator|*
name|sinAngle
operator|+
name|z
operator|*
name|cosAngle
argument_list|)
return|;
block|}
comment|/**    * Rotate vector counter-clockwise in z-y by an angle.    */
DECL|method|rotateZY
specifier|public
name|Vector
name|rotateZY
parameter_list|(
specifier|final
name|double
name|angle
parameter_list|)
block|{
return|return
name|rotateZY
argument_list|(
name|Math
operator|.
name|sin
argument_list|(
name|angle
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|angle
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Rotate vector counter-clockwise in z-y by an angle, expressed as sin and cos.    */
DECL|method|rotateZY
specifier|public
name|Vector
name|rotateZY
parameter_list|(
specifier|final
name|double
name|sinAngle
parameter_list|,
specifier|final
name|double
name|cosAngle
parameter_list|)
block|{
return|return
operator|new
name|Vector
argument_list|(
name|x
argument_list|,
name|z
operator|*
name|sinAngle
operator|+
name|y
operator|*
name|cosAngle
argument_list|,
name|z
operator|*
name|cosAngle
operator|-
name|y
operator|*
name|sinAngle
argument_list|)
return|;
block|}
comment|/**    * Compute the square of a straight-line distance to a point described by the    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI.    *    * @param v is the vector to compute a distance to.    * @return the square of the linear distance.    */
DECL|method|linearDistanceSquared
specifier|public
name|double
name|linearDistanceSquared
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
name|double
name|deltaX
init|=
name|this
operator|.
name|x
operator|-
name|v
operator|.
name|x
decl_stmt|;
name|double
name|deltaY
init|=
name|this
operator|.
name|y
operator|-
name|v
operator|.
name|y
decl_stmt|;
name|double
name|deltaZ
init|=
name|this
operator|.
name|z
operator|-
name|v
operator|.
name|z
decl_stmt|;
return|return
name|deltaX
operator|*
name|deltaX
operator|+
name|deltaY
operator|*
name|deltaY
operator|+
name|deltaZ
operator|*
name|deltaZ
return|;
block|}
comment|/**    * Compute the square of a straight-line distance to a point described by the    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI.    *    * @param x is the x part of the vector to compute a distance to.    * @param y is the y part of the vector to compute a distance to.    * @param z is the z part of the vector to compute a distance to.    * @return the square of the linear distance.    */
DECL|method|linearDistanceSquared
specifier|public
name|double
name|linearDistanceSquared
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
name|double
name|deltaX
init|=
name|this
operator|.
name|x
operator|-
name|x
decl_stmt|;
name|double
name|deltaY
init|=
name|this
operator|.
name|y
operator|-
name|y
decl_stmt|;
name|double
name|deltaZ
init|=
name|this
operator|.
name|z
operator|-
name|z
decl_stmt|;
return|return
name|deltaX
operator|*
name|deltaX
operator|+
name|deltaY
operator|*
name|deltaY
operator|+
name|deltaZ
operator|*
name|deltaZ
return|;
block|}
comment|/**    * Compute the straight-line distance to a point described by the    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI.    *    * @param v is the vector to compute a distance to.    * @return the linear distance.    */
DECL|method|linearDistance
specifier|public
name|double
name|linearDistance
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|linearDistanceSquared
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the straight-line distance to a point described by the    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI.    *    * @param x is the x part of the vector to compute a distance to.    * @param y is the y part of the vector to compute a distance to.    * @param z is the z part of the vector to compute a distance to.    * @return the linear distance.    */
DECL|method|linearDistance
specifier|public
name|double
name|linearDistance
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
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|linearDistanceSquared
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the square of the normal distance to a vector described by a    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI/2.    *    * @param v is the vector to compute a distance to.    * @return the square of the normal distance.    */
DECL|method|normalDistanceSquared
specifier|public
name|double
name|normalDistanceSquared
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
name|double
name|t
init|=
name|dotProduct
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|double
name|deltaX
init|=
name|this
operator|.
name|x
operator|*
name|t
operator|-
name|v
operator|.
name|x
decl_stmt|;
name|double
name|deltaY
init|=
name|this
operator|.
name|y
operator|*
name|t
operator|-
name|v
operator|.
name|y
decl_stmt|;
name|double
name|deltaZ
init|=
name|this
operator|.
name|z
operator|*
name|t
operator|-
name|v
operator|.
name|z
decl_stmt|;
return|return
name|deltaX
operator|*
name|deltaX
operator|+
name|deltaY
operator|*
name|deltaY
operator|+
name|deltaZ
operator|*
name|deltaZ
return|;
block|}
comment|/**    * Compute the square of the normal distance to a vector described by a    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI/2.    *    * @param x is the x part of the vector to compute a distance to.    * @param y is the y part of the vector to compute a distance to.    * @param z is the z part of the vector to compute a distance to.    * @return the square of the normal distance.    */
DECL|method|normalDistanceSquared
specifier|public
name|double
name|normalDistanceSquared
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
name|double
name|t
init|=
name|dotProduct
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
name|double
name|deltaX
init|=
name|this
operator|.
name|x
operator|*
name|t
operator|-
name|x
decl_stmt|;
name|double
name|deltaY
init|=
name|this
operator|.
name|y
operator|*
name|t
operator|-
name|y
decl_stmt|;
name|double
name|deltaZ
init|=
name|this
operator|.
name|z
operator|*
name|t
operator|-
name|z
decl_stmt|;
return|return
name|deltaX
operator|*
name|deltaX
operator|+
name|deltaY
operator|*
name|deltaY
operator|+
name|deltaZ
operator|*
name|deltaZ
return|;
block|}
comment|/**    * Compute the normal (perpendicular) distance to a vector described by a    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI/2.    *    * @param v is the vector to compute a distance to.    * @return the normal distance.    */
DECL|method|normalDistance
specifier|public
name|double
name|normalDistance
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|normalDistanceSquared
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the normal (perpendicular) distance to a vector described by a    * vector taken from the origin.    * Monotonically increasing for arc distances up to PI/2.    *    * @param x is the x part of the vector to compute a distance to.    * @param y is the y part of the vector to compute a distance to.    * @param z is the z part of the vector to compute a distance to.    * @return the normal distance.    */
DECL|method|normalDistance
specifier|public
name|double
name|normalDistance
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
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|normalDistanceSquared
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the magnitude of this vector.    *    * @return the magnitude.    */
DECL|method|magnitude
specifier|public
name|double
name|magnitude
parameter_list|()
block|{
return|return
name|magnitude
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/** Compute the desired magnitude of a unit vector projected to a given    * planet model.    * @param planetModel is the planet model.    * @param x is the unit vector x value.    * @param y is the unit vector y value.    * @param z is the unit vector z value.    * @return a magnitude value for that (x,y,z) that projects the vector onto the specified ellipsoid.    */
DECL|method|computeDesiredEllipsoidMagnitude
specifier|protected
specifier|static
name|double
name|computeDesiredEllipsoidMagnitude
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
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
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|x
operator|*
name|x
operator|*
name|planetModel
operator|.
name|inverseAbSquared
operator|+
name|y
operator|*
name|y
operator|*
name|planetModel
operator|.
name|inverseAbSquared
operator|+
name|z
operator|*
name|z
operator|*
name|planetModel
operator|.
name|inverseCSquared
argument_list|)
return|;
block|}
comment|/** Compute the desired magnitude of a unit vector projected to a given    * planet model.  The unit vector is specified only by a z value.    * @param planetModel is the planet model.    * @param z is the unit vector z value.    * @return a magnitude value for that z value that projects the vector onto the specified ellipsoid.    */
DECL|method|computeDesiredEllipsoidMagnitude
specifier|protected
specifier|static
name|double
name|computeDesiredEllipsoidMagnitude
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
return|return
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
literal|1.0
operator|-
name|z
operator|*
name|z
operator|)
operator|*
name|planetModel
operator|.
name|inverseAbSquared
operator|+
name|z
operator|*
name|z
operator|*
name|planetModel
operator|.
name|inverseCSquared
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
operator|!
operator|(
name|o
operator|instanceof
name|Vector
operator|)
condition|)
return|return
literal|false
return|;
name|Vector
name|other
init|=
operator|(
name|Vector
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|other
operator|.
name|x
operator|==
name|x
operator|&&
name|other
operator|.
name|y
operator|==
name|y
operator|&&
name|other
operator|.
name|z
operator|==
name|z
operator|)
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
name|x
argument_list|)
expr_stmt|;
name|result
operator|=
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
name|y
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
name|z
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
parameter_list|()
block|{
return|return
literal|"[X="
operator|+
name|x
operator|+
literal|", Y="
operator|+
name|y
operator|+
literal|", Z="
operator|+
name|z
operator|+
literal|"]"
return|;
block|}
block|}
end_class
end_unit
