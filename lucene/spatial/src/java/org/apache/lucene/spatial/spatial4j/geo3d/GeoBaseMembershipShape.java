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
begin_comment
comment|/**  * Membership shapes have capabilities of both geohashing and membership  * determination.  This is a useful baseclass for them.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoBaseMembershipShape
specifier|public
specifier|abstract
class|class
name|GeoBaseMembershipShape
extends|extends
name|GeoBaseShape
implements|implements
name|GeoMembershipShape
block|{
DECL|method|GeoBaseMembershipShape
specifier|public
name|GeoBaseMembershipShape
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|Vector
name|point
parameter_list|)
block|{
return|return
name|isWithin
argument_list|(
name|point
operator|.
name|x
argument_list|,
name|point
operator|.
name|y
argument_list|,
name|point
operator|.
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeOutsideDistance
specifier|public
name|double
name|computeOutsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|computeOutsideDistance
argument_list|(
name|distanceStyle
argument_list|,
name|point
operator|.
name|x
argument_list|,
name|point
operator|.
name|y
argument_list|,
name|point
operator|.
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeOutsideDistance
specifier|public
name|double
name|computeOutsideDistance
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
if|if
condition|(
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
block|{
return|return
literal|0.0
return|;
block|}
return|return
name|outsideDistance
argument_list|(
name|distanceStyle
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/** Called by a {@code computeOutsideDistance} method if X/Y/Z is not within this shape. */
DECL|method|outsideDistance
specifier|protected
specifier|abstract
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
function_decl|;
block|}
end_class
end_unit