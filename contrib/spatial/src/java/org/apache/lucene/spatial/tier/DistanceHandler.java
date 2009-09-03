begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Provide a high level access point to distances  * Used by DistanceSortSource and DistanceQuery  *    *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  *  */
end_comment
begin_class
DECL|class|DistanceHandler
specifier|public
class|class
name|DistanceHandler
block|{
DECL|field|distances
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|distances
decl_stmt|;
DECL|enum|Precision
DECL|enum constant|EXACT
DECL|enum constant|TWOFEET
DECL|enum constant|TWENTYFEET
DECL|enum constant|TWOHUNDREDFEET
specifier|public
enum|enum
name|Precision
block|{
name|EXACT
block|,
name|TWOFEET
block|,
name|TWENTYFEET
block|,
name|TWOHUNDREDFEET
block|}
empty_stmt|;
DECL|field|precise
specifier|private
name|Precision
name|precise
decl_stmt|;
DECL|method|DistanceHandler
specifier|public
name|DistanceHandler
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|distances
parameter_list|,
name|Precision
name|precise
parameter_list|)
block|{
name|this
operator|.
name|distances
operator|=
name|distances
expr_stmt|;
name|this
operator|.
name|precise
operator|=
name|precise
expr_stmt|;
block|}
DECL|method|getPrecision
specifier|public
specifier|static
name|double
name|getPrecision
parameter_list|(
name|double
name|x
parameter_list|,
name|Precision
name|thisPrecise
parameter_list|)
block|{
if|if
condition|(
name|thisPrecise
operator|!=
literal|null
condition|)
block|{
name|double
name|dif
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|thisPrecise
condition|)
block|{
case|case
name|EXACT
case|:
return|return
name|x
return|;
case|case
name|TWOFEET
case|:
name|dif
operator|=
name|x
operator|%
literal|0.0001
expr_stmt|;
break|break;
case|case
name|TWENTYFEET
case|:
name|dif
operator|=
name|x
operator|%
literal|0.001
expr_stmt|;
break|break;
case|case
name|TWOHUNDREDFEET
case|:
name|dif
operator|=
name|x
operator|%
literal|0.01
expr_stmt|;
break|break;
block|}
return|return
name|x
operator|-
name|dif
return|;
block|}
return|return
name|x
return|;
block|}
DECL|method|getPrecision
specifier|public
name|Precision
name|getPrecision
parameter_list|()
block|{
return|return
name|precise
return|;
block|}
DECL|method|getDistance
specifier|public
name|double
name|getDistance
parameter_list|(
name|int
name|docid
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLng
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lng
parameter_list|)
block|{
comment|// check to see if we have distances
comment|// if not calculate the distance
if|if
condition|(
name|distances
operator|==
literal|null
condition|)
block|{
return|return
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|centerLat
argument_list|,
name|centerLng
argument_list|,
name|lat
argument_list|,
name|lng
argument_list|)
return|;
block|}
comment|// check to see if the doc id has a cached distance
name|Double
name|docd
init|=
name|distances
operator|.
name|get
argument_list|(
name|docid
argument_list|)
decl_stmt|;
if|if
condition|(
name|docd
operator|!=
literal|null
condition|)
block|{
return|return
name|docd
operator|.
name|doubleValue
argument_list|()
return|;
block|}
comment|//check to see if we have a precision code
comment|// and if another lat/long has been calculated at
comment|// that rounded location
if|if
condition|(
name|precise
operator|!=
literal|null
condition|)
block|{
name|double
name|xLat
init|=
name|getPrecision
argument_list|(
name|lat
argument_list|,
name|precise
argument_list|)
decl_stmt|;
name|double
name|xLng
init|=
name|getPrecision
argument_list|(
name|lng
argument_list|,
name|precise
argument_list|)
decl_stmt|;
name|String
name|k
init|=
operator|new
name|Double
argument_list|(
name|xLat
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|","
operator|+
operator|new
name|Double
argument_list|(
name|xLng
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Double
name|d
init|=
operator|(
name|distances
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
return|return
name|d
operator|.
name|doubleValue
argument_list|()
return|;
block|}
block|}
comment|//all else fails calculate the distances
return|return
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|centerLat
argument_list|,
name|centerLng
argument_list|,
name|lat
argument_list|,
name|lng
argument_list|)
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|DistanceHandler
name|db
init|=
operator|new
name|DistanceHandler
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|,
name|Precision
operator|.
name|TWOHUNDREDFEET
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|DistanceHandler
operator|.
name|getPrecision
argument_list|(
operator|-
literal|1234.123456789
argument_list|,
name|db
operator|.
name|getPrecision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
