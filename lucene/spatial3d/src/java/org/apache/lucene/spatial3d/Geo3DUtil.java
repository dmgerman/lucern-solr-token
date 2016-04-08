begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
import|;
end_import
begin_class
DECL|class|Geo3DUtil
class|class
name|Geo3DUtil
block|{
DECL|field|MAX_VALUE
specifier|private
specifier|static
specifier|final
name|double
name|MAX_VALUE
init|=
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumMagnitude
argument_list|()
decl_stmt|;
DECL|field|BITS
specifier|private
specifier|static
specifier|final
name|int
name|BITS
init|=
literal|32
decl_stmt|;
DECL|field|MUL
specifier|private
specifier|static
specifier|final
name|double
name|MUL
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
operator|(
literal|2
operator|*
name|MAX_VALUE
operator|)
decl_stmt|;
DECL|field|DECODE
specifier|static
specifier|final
name|double
name|DECODE
init|=
literal|1
operator|/
name|MUL
decl_stmt|;
DECL|field|MIN_ENCODED_VALUE
specifier|private
specifier|static
specifier|final
name|int
name|MIN_ENCODED_VALUE
init|=
name|encodeValue
argument_list|(
operator|-
name|MAX_VALUE
argument_list|)
decl_stmt|;
DECL|method|encodeValue
specifier|public
specifier|static
name|int
name|encodeValue
parameter_list|(
name|double
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|>
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value="
operator|+
name|x
operator|+
literal|" is out-of-bounds (greater than WGS84's planetMax="
operator|+
name|MAX_VALUE
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|x
operator|<
operator|-
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value="
operator|+
name|x
operator|+
literal|" is out-of-bounds (less than than WGS84's -planetMax="
operator|+
operator|-
name|MAX_VALUE
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|x
operator|==
name|MAX_VALUE
condition|)
block|{
name|x
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
name|long
name|result
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|x
operator|/
name|DECODE
argument_list|)
decl_stmt|;
comment|//System.out.println("    enc: " + x + " -> " + result);
assert|assert
name|result
operator|>=
name|Integer
operator|.
name|MIN_VALUE
assert|;
assert|assert
name|result
operator|<=
name|Integer
operator|.
name|MAX_VALUE
assert|;
return|return
operator|(
name|int
operator|)
name|result
return|;
block|}
DECL|method|decodeValue
specifier|public
specifier|static
name|double
name|decodeValue
parameter_list|(
name|int
name|x
parameter_list|)
block|{
comment|// We decode to the center value; this keeps the encoding stable
return|return
operator|(
name|x
operator|+
literal|0.5
operator|)
operator|*
name|DECODE
return|;
block|}
comment|/** Returns smallest double that would encode to int x. */
comment|// NOTE: keep this package private!!
DECL|method|decodeValueFloor
specifier|static
name|double
name|decodeValueFloor
parameter_list|(
name|int
name|x
parameter_list|)
block|{
return|return
name|x
operator|*
name|DECODE
return|;
block|}
comment|/** Returns largest double that would encode to int x. */
comment|// NOTE: keep this package private!!
DECL|method|decodeValueCeil
specifier|static
name|double
name|decodeValueCeil
parameter_list|(
name|int
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|MAX_VALUE
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|nextDown
argument_list|(
operator|(
name|x
operator|+
literal|1
operator|)
operator|*
name|DECODE
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
