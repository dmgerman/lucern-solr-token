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
comment|/**  * Arc distance computation style.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ArcDistance
specifier|public
class|class
name|ArcDistance
implements|implements
name|DistanceStyle
block|{
comment|/** An instance of the ArcDistance DistanceStyle. */
DECL|field|INSTANCE
specifier|public
specifier|final
specifier|static
name|ArcDistance
name|INSTANCE
init|=
operator|new
name|ArcDistance
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point1
parameter_list|,
specifier|final
name|GeoPoint
name|point2
parameter_list|)
block|{
return|return
name|point1
operator|.
name|arcDistance
argument_list|(
name|point2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|GeoPoint
name|point1
parameter_list|,
specifier|final
name|double
name|x2
parameter_list|,
specifier|final
name|double
name|y2
parameter_list|,
specifier|final
name|double
name|z2
parameter_list|)
block|{
return|return
name|point1
operator|.
name|arcDistance
argument_list|(
name|x2
argument_list|,
name|y2
argument_list|,
name|z2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|Plane
name|plane
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|plane
operator|.
name|arcDistance
argument_list|(
name|planetModel
argument_list|,
name|point
argument_list|,
name|bounds
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|Plane
name|plane
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
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|plane
operator|.
name|arcDistance
argument_list|(
name|planetModel
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|bounds
argument_list|)
return|;
block|}
block|}
end_class
end_unit
