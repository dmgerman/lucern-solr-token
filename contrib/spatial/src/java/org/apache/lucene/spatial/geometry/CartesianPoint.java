begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.geometry
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geometry
package|;
end_package
begin_comment
comment|/**  * Represents lat/lngs as fixed point numbers translated so that all  * world coordinates are in the first quadrant.  The same fixed point  * scale as is used for FixedLatLng is employed.  *  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment
begin_class
DECL|class|CartesianPoint
specifier|public
class|class
name|CartesianPoint
block|{
DECL|field|x
specifier|private
name|int
name|x
decl_stmt|;
DECL|field|y
specifier|private
name|int
name|y
decl_stmt|;
DECL|method|CartesianPoint
specifier|public
name|CartesianPoint
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
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
block|}
DECL|method|getX
specifier|public
name|int
name|getX
parameter_list|()
block|{
return|return
name|x
return|;
block|}
DECL|method|getY
specifier|public
name|int
name|getY
parameter_list|()
block|{
return|return
name|y
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
literal|"Point("
operator|+
name|x
operator|+
literal|","
operator|+
name|y
operator|+
literal|")"
return|;
block|}
comment|/**    * Return a new point translated in the x and y dimensions    */
DECL|method|translate
specifier|public
name|CartesianPoint
name|translate
parameter_list|(
name|int
name|deltaX
parameter_list|,
name|int
name|deltaY
parameter_list|)
block|{
return|return
operator|new
name|CartesianPoint
argument_list|(
name|this
operator|.
name|x
operator|+
name|deltaX
argument_list|,
name|this
operator|.
name|y
operator|+
name|deltaY
argument_list|)
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|x
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|y
expr_stmt|;
return|return
name|result
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|CartesianPoint
name|other
init|=
operator|(
name|CartesianPoint
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|x
operator|!=
name|other
operator|.
name|x
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|y
operator|!=
name|other
operator|.
name|y
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
