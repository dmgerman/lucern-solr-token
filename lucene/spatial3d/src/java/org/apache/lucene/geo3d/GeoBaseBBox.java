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
comment|/**  * All bounding box shapes can derive from this base class, which furnishes  * some common code  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|GeoBaseBBox
specifier|public
specifier|abstract
class|class
name|GeoBaseBBox
extends|extends
name|GeoBaseMembershipShape
implements|implements
name|GeoBBox
block|{
comment|/** Construct, given planet model.    *@param planetModel is the planet model.    */
DECL|method|GeoBaseBBox
specifier|public
name|GeoBaseBBox
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
comment|// Signals for relationship of edge points to shape
comment|/** All edgepoints inside shape */
DECL|field|ALL_INSIDE
specifier|protected
specifier|final
specifier|static
name|int
name|ALL_INSIDE
init|=
literal|0
decl_stmt|;
comment|/** Some edgepoints inside shape */
DECL|field|SOME_INSIDE
specifier|protected
specifier|final
specifier|static
name|int
name|SOME_INSIDE
init|=
literal|1
decl_stmt|;
comment|/** No edgepoints inside shape */
DECL|field|NONE_INSIDE
specifier|protected
specifier|final
specifier|static
name|int
name|NONE_INSIDE
init|=
literal|2
decl_stmt|;
comment|/** Determine the relationship between this BBox and the provided    * shape's edgepoints.    *@param path is the shape.    *@return the relationship.    */
DECL|method|isShapeInsideBBox
specifier|protected
name|int
name|isShapeInsideBBox
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
specifier|final
name|GeoPoint
index|[]
name|pathPoints
init|=
name|path
operator|.
name|getEdgePoints
argument_list|()
decl_stmt|;
name|boolean
name|foundOutside
init|=
literal|false
decl_stmt|;
name|boolean
name|foundInside
init|=
literal|false
decl_stmt|;
for|for
control|(
name|GeoPoint
name|p
range|:
name|pathPoints
control|)
block|{
if|if
condition|(
name|isWithin
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|foundInside
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|foundOutside
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|foundInside
operator|&&
name|foundOutside
condition|)
block|{
return|return
name|SOME_INSIDE
return|;
block|}
block|}
if|if
condition|(
operator|!
name|foundInside
operator|&&
operator|!
name|foundOutside
condition|)
return|return
name|NONE_INSIDE
return|;
if|if
condition|(
name|foundInside
operator|&&
operator|!
name|foundOutside
condition|)
return|return
name|ALL_INSIDE
return|;
if|if
condition|(
name|foundOutside
operator|&&
operator|!
name|foundInside
condition|)
return|return
name|NONE_INSIDE
return|;
return|return
name|SOME_INSIDE
return|;
block|}
block|}
end_class
end_unit
