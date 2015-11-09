begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|GeoUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
operator|.
name|BKDUtil
import|;
end_import
begin_comment
comment|/** Add this to a document to index lat/lon point dimensionally */
end_comment
begin_class
DECL|class|DimensionalLatLonField
specifier|public
specifier|final
class|class
name|DimensionalLatLonField
extends|extends
name|Field
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setDimensions
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Creates a new DimensionalLatLonField with the specified lat and lon    * @param name field name    * @param lat double latitude    * @param lon double longitude    * @throws IllegalArgumentException if the field name is null or lat or lon are out of bounds    */
DECL|method|DimensionalLatLonField
specifier|public
name|DimensionalLatLonField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|lat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid lat ("
operator|+
name|lat
operator|+
literal|"): must be -90 to 90"
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|lon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid lon ("
operator|+
name|lon
operator|+
literal|"): must be -180 to 180"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|BKDUtil
operator|.
name|intToBytes
argument_list|(
name|encodeLat
argument_list|(
name|lat
argument_list|)
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|BKDUtil
operator|.
name|intToBytes
argument_list|(
name|encodeLon
argument_list|(
name|lon
argument_list|)
argument_list|,
name|bytes
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|field|TOLERANCE
specifier|public
specifier|static
specifier|final
name|double
name|TOLERANCE
init|=
literal|1E
operator|-
literal|7
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
DECL|field|LON_SCALE
specifier|private
specifier|static
specifier|final
name|double
name|LON_SCALE
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|360.0D
decl_stmt|;
DECL|field|LAT_SCALE
specifier|private
specifier|static
specifier|final
name|double
name|LAT_SCALE
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|180.0D
decl_stmt|;
comment|/** Quantizes double (64 bit) latitude into 32 bits */
DECL|method|encodeLat
specifier|public
specifier|static
name|int
name|encodeLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
assert|assert
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|lat
argument_list|)
operator|:
literal|"lat="
operator|+
name|lat
assert|;
name|long
name|x
init|=
call|(
name|long
call|)
argument_list|(
name|lat
operator|*
name|LAT_SCALE
argument_list|)
decl_stmt|;
assert|assert
name|x
operator|<
name|Integer
operator|.
name|MAX_VALUE
operator|:
literal|"lat="
operator|+
name|lat
operator|+
literal|" mapped to Integer.MAX_VALUE + "
operator|+
operator|(
name|x
operator|-
name|Integer
operator|.
name|MAX_VALUE
operator|)
assert|;
assert|assert
name|x
operator|>
name|Integer
operator|.
name|MIN_VALUE
operator|:
literal|"lat="
operator|+
name|lat
operator|+
literal|" mapped to Integer.MIN_VALUE"
assert|;
return|return
operator|(
name|int
operator|)
name|x
return|;
block|}
comment|/** Quantizes double (64 bit) longitude into 32 bits */
DECL|method|encodeLon
specifier|public
specifier|static
name|int
name|encodeLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
assert|assert
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|lon
argument_list|)
operator|:
literal|"lon="
operator|+
name|lon
assert|;
name|long
name|x
init|=
call|(
name|long
call|)
argument_list|(
name|lon
operator|*
name|LON_SCALE
argument_list|)
decl_stmt|;
assert|assert
name|x
operator|<
name|Integer
operator|.
name|MAX_VALUE
assert|;
assert|assert
name|x
operator|>
name|Integer
operator|.
name|MIN_VALUE
assert|;
return|return
operator|(
name|int
operator|)
name|x
return|;
block|}
comment|/** Turns quantized value from {@link #encodeLat} back into a double. */
DECL|method|decodeLat
specifier|public
specifier|static
name|double
name|decodeLat
parameter_list|(
name|int
name|x
parameter_list|)
block|{
return|return
name|x
operator|/
name|LAT_SCALE
return|;
block|}
comment|/** Turns quantized value from {@link #encodeLon} back into a double. */
DECL|method|decodeLon
specifier|public
specifier|static
name|double
name|decodeLon
parameter_list|(
name|int
name|x
parameter_list|)
block|{
return|return
name|x
operator|/
name|LON_SCALE
return|;
block|}
block|}
end_class
end_unit