begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package
begin_comment
comment|/** Represents a lat/lon rectangle. */
end_comment
begin_class
DECL|class|GeoRect
specifier|public
class|class
name|GeoRect
block|{
comment|/** maximum longitude value (in degrees) */
DECL|field|minLat
specifier|public
specifier|final
name|double
name|minLat
decl_stmt|;
comment|/** minimum longitude value (in degrees) */
DECL|field|minLon
specifier|public
specifier|final
name|double
name|minLon
decl_stmt|;
comment|/** maximum latitude value (in degrees) */
DECL|field|maxLat
specifier|public
specifier|final
name|double
name|maxLat
decl_stmt|;
comment|/** minimum latitude value (in degrees) */
DECL|field|maxLon
specifier|public
specifier|final
name|double
name|maxLon
decl_stmt|;
comment|/**    * Constructs a bounding box by first validating the provided latitude and longitude coordinates    */
DECL|method|GeoRect
specifier|public
name|GeoRect
parameter_list|(
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
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|minLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid minLon "
operator|+
name|minLon
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|maxLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid maxLon "
operator|+
name|maxLon
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|minLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid minLat "
operator|+
name|minLat
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|maxLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid maxLat "
operator|+
name|maxLat
argument_list|)
throw|;
block|}
name|this
operator|.
name|minLon
operator|=
name|minLon
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|maxLon
expr_stmt|;
name|this
operator|.
name|minLat
operator|=
name|minLat
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|maxLat
expr_stmt|;
assert|assert
name|maxLat
operator|>=
name|minLat
assert|;
comment|// NOTE: cannot assert maxLon>= minLon since this rect could cross the dateline
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"GeoRect(lon="
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|minLon
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|maxLon
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" (crosses dateline!)"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|" lat="
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|minLat
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|maxLat
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true if this bounding box crosses the dateline */
DECL|method|crossesDateline
specifier|public
name|boolean
name|crossesDateline
parameter_list|()
block|{
return|return
name|maxLon
operator|<
name|minLon
return|;
block|}
block|}
end_class
end_unit
