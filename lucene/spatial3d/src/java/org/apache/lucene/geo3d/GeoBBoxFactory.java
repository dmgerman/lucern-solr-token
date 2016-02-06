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
comment|/**  * Factory for {@link org.apache.lucene.geo3d.GeoBBox}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoBBoxFactory
specifier|public
class|class
name|GeoBBoxFactory
block|{
DECL|method|GeoBBoxFactory
specifier|private
name|GeoBBoxFactory
parameter_list|()
block|{   }
comment|/**    * Create a geobbox of the right kind given the specified bounds.    *    * @param planetModel is the planet model    * @param topLat    is the top latitude    * @param bottomLat is the bottom latitude    * @param leftLon   is the left longitude    * @param rightLon  is the right longitude    * @return a GeoBBox corresponding to what was specified.    */
DECL|method|makeGeoBBox
specifier|public
specifier|static
name|GeoBBox
name|makeGeoBBox
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
name|double
name|topLat
parameter_list|,
name|double
name|bottomLat
parameter_list|,
name|double
name|leftLon
parameter_list|,
name|double
name|rightLon
parameter_list|)
block|{
comment|//System.err.println("Making rectangle for topLat="+topLat*180.0/Math.PI+", bottomLat="+bottomLat*180.0/Math.PI+", leftLon="+leftLon*180.0/Math.PI+", rightlon="+rightLon*180.0/Math.PI);
if|if
condition|(
name|topLat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
name|topLat
operator|=
name|Math
operator|.
name|PI
operator|*
literal|0.5
expr_stmt|;
if|if
condition|(
name|bottomLat
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
name|bottomLat
operator|=
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
expr_stmt|;
if|if
condition|(
name|leftLon
operator|<
operator|-
name|Math
operator|.
name|PI
condition|)
name|leftLon
operator|=
operator|-
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|rightLon
operator|>
name|Math
operator|.
name|PI
condition|)
name|rightLon
operator|=
name|Math
operator|.
name|PI
expr_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|leftLon
operator|+
name|Math
operator|.
name|PI
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|Math
operator|.
name|abs
argument_list|(
name|rightLon
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
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
name|Math
operator|.
name|abs
argument_list|(
name|bottomLat
operator|+
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
return|return
operator|new
name|GeoWorld
argument_list|(
name|planetModel
argument_list|)
return|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|bottomLat
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|+
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
return|return
operator|new
name|GeoDegeneratePoint
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
literal|0.0
argument_list|)
return|;
return|return
operator|new
name|GeoDegenerateLatitudeZone
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|)
return|;
block|}
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
return|return
operator|new
name|GeoNorthLatitudeZone
argument_list|(
name|planetModel
argument_list|,
name|bottomLat
argument_list|)
return|;
elseif|else
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|bottomLat
operator|+
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
return|return
operator|new
name|GeoSouthLatitudeZone
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|)
return|;
return|return
operator|new
name|GeoLatitudeZone
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|bottomLat
argument_list|)
return|;
block|}
comment|//System.err.println(" not latitude zone");
name|double
name|extent
init|=
name|rightLon
operator|-
name|leftLon
decl_stmt|;
if|if
condition|(
name|extent
operator|<
literal|0.0
condition|)
name|extent
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
if|if
condition|(
name|topLat
operator|==
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|&&
name|bottomLat
operator|==
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|leftLon
operator|-
name|rightLon
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
return|return
operator|new
name|GeoDegenerateLongitudeSlice
argument_list|(
name|planetModel
argument_list|,
name|leftLon
argument_list|)
return|;
if|if
condition|(
name|extent
operator|>=
name|Math
operator|.
name|PI
condition|)
return|return
operator|new
name|GeoWideLongitudeSlice
argument_list|(
name|planetModel
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
return|return
operator|new
name|GeoLongitudeSlice
argument_list|(
name|planetModel
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
comment|//System.err.println(" not longitude slice");
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|leftLon
operator|-
name|rightLon
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|bottomLat
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
return|return
operator|new
name|GeoDegeneratePoint
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|leftLon
argument_list|)
return|;
return|return
operator|new
name|GeoDegenerateVerticalLine
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|bottomLat
argument_list|,
name|leftLon
argument_list|)
return|;
block|}
comment|//System.err.println(" not vertical line");
if|if
condition|(
name|extent
operator|>=
name|Math
operator|.
name|PI
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|bottomLat
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
comment|//System.err.println(" wide degenerate line");
return|return
operator|new
name|GeoWideDegenerateHorizontalLine
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
return|return
operator|new
name|GeoWideNorthRectangle
argument_list|(
name|planetModel
argument_list|,
name|bottomLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|bottomLat
operator|+
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
return|return
operator|new
name|GeoWideSouthRectangle
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
comment|//System.err.println(" wide rect");
return|return
operator|new
name|GeoWideRectangle
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|bottomLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|bottomLat
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|+
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
return|return
operator|new
name|GeoDegeneratePoint
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
literal|0.0
argument_list|)
return|;
block|}
comment|//System.err.println(" horizontal line");
return|return
operator|new
name|GeoDegenerateHorizontalLine
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
return|return
operator|new
name|GeoNorthRectangle
argument_list|(
name|planetModel
argument_list|,
name|bottomLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|bottomLat
operator|+
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
return|return
operator|new
name|GeoSouthRectangle
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
comment|//System.err.println(" rectangle");
return|return
operator|new
name|GeoRectangle
argument_list|(
name|planetModel
argument_list|,
name|topLat
argument_list|,
name|bottomLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
block|}
end_class
end_unit
