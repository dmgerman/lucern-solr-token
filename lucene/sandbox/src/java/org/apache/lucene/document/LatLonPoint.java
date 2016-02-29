begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|NumericUtils
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|ConstantScoreQuery
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
name|search
operator|.
name|PointDistanceQuery
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
name|search
operator|.
name|PointInPolygonQuery
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
name|search
operator|.
name|PointRangeQuery
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
name|search
operator|.
name|Query
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
name|spatial
operator|.
name|util
operator|.
name|GeoUtils
import|;
end_import
begin_comment
comment|/**   * An indexed location field.  *<p>  * Finding all documents within a range at search time is  * efficient.  Multiple values for the same field in one document  * is allowed.   *<p>  * This field defines static factory methods for creating common queries:  *<ul>  *<li>{@link #newBoxQuery newBoxQuery()} for matching points within a bounding box.  *<li>{@link #newDistanceQuery newDistanceQuery()} for matching points within a specified distance.  *<li>{@link #newPolygonQuery newPolygonQuery()} for matching points within an arbitrary polygon.  *</ul>  *<p>  *<b>WARNING</b>: Values are indexed with some loss of precision, incurring up to 1E-7 error from the  * original {@code double} values.   */
end_comment
begin_comment
comment|// TODO ^^^ that is very sandy and hurts the API, usage, and tests tremendously, because what the user passes
end_comment
begin_comment
comment|// to the field is not actually what gets indexed. Float would be 1E-5 error vs 1E-7, but it might be
end_comment
begin_comment
comment|// a better tradeoff? then it would be completely transparent to the user and lucene would be "lossless".
end_comment
begin_class
DECL|class|LatLonPoint
specifier|public
class|class
name|LatLonPoint
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
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Creates a new LatLonPoint with the specified lat and lon    * @param name field name    * @param lat double latitude    * @param lon double longitude    * @throws IllegalArgumentException if the field name is null or lat or lon are out of bounds    */
DECL|method|LatLonPoint
specifier|public
name|LatLonPoint
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
name|NumericUtils
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
name|NumericUtils
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
name|Integer
operator|.
name|BYTES
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeLat
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeLon
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
operator|.
name|bytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// public helper methods (e.g. for queries)
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
comment|/** Turns quantized value from byte array back into a double. */
DECL|method|decodeLat
specifier|public
specifier|static
name|double
name|decodeLat
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|decodeLat
argument_list|(
name|NumericUtils
operator|.
name|bytesToInt
argument_list|(
name|src
argument_list|,
name|offset
argument_list|)
argument_list|)
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
comment|/** Turns quantized value from byte array back into a double. */
DECL|method|decodeLon
specifier|public
specifier|static
name|double
name|decodeLon
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|decodeLon
argument_list|(
name|NumericUtils
operator|.
name|bytesToInt
argument_list|(
name|src
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/** sugar encodes a single point as a 2D byte array */
DECL|method|encode
specifier|private
specifier|static
name|byte
index|[]
index|[]
name|encode
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|bytes
index|[
literal|0
index|]
operator|=
operator|new
name|byte
index|[
literal|4
index|]
expr_stmt|;
name|NumericUtils
operator|.
name|intToBytes
argument_list|(
name|encodeLat
argument_list|(
name|lat
argument_list|)
argument_list|,
name|bytes
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
operator|new
name|byte
index|[
literal|4
index|]
expr_stmt|;
name|NumericUtils
operator|.
name|intToBytes
argument_list|(
name|encodeLon
argument_list|(
name|lon
argument_list|)
argument_list|,
name|bytes
index|[
literal|1
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|// static methods for generating queries
comment|/**    * Create a query for matching a bounding box.    *<p>    * The box may cross over the dateline.    */
DECL|method|newBoxQuery
specifier|public
specifier|static
name|Query
name|newBoxQuery
parameter_list|(
name|String
name|field
parameter_list|,
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
literal|"minLat="
operator|+
name|minLat
operator|+
literal|" is not a valid latitude"
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
literal|"maxLat="
operator|+
name|maxLat
operator|+
literal|" is not a valid latitude"
argument_list|)
throw|;
block|}
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
literal|"minLon="
operator|+
name|minLon
operator|+
literal|" is not a valid longitude"
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
literal|"maxLon="
operator|+
name|maxLon
operator|+
literal|" is not a valid longitude"
argument_list|)
throw|;
block|}
name|byte
index|[]
index|[]
name|lower
init|=
name|encode
argument_list|(
name|minLat
argument_list|,
name|minLon
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|upper
init|=
name|encode
argument_list|(
name|maxLat
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
comment|// Crosses date line: we just rewrite into OR of two bboxes, with longitude as an open range:
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
comment|// Disable coord here because a multi-valued doc could match both rects and get unfairly boosted:
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// E.g.: maxLon = -179, minLon = 179
name|byte
index|[]
index|[]
name|leftOpen
init|=
operator|new
name|byte
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|leftOpen
index|[
literal|0
index|]
operator|=
name|lower
index|[
literal|0
index|]
expr_stmt|;
comment|// leave longitude open (null)
name|Query
name|left
init|=
name|newBoxInternal
argument_list|(
name|field
argument_list|,
name|leftOpen
argument_list|,
name|upper
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|left
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
index|[]
name|rightOpen
init|=
operator|new
name|byte
index|[
literal|2
index|]
index|[]
decl_stmt|;
name|rightOpen
index|[
literal|0
index|]
operator|=
name|upper
index|[
literal|0
index|]
expr_stmt|;
comment|// leave longitude open (null)
name|Query
name|right
init|=
name|newBoxInternal
argument_list|(
name|field
argument_list|,
name|lower
argument_list|,
name|rightOpen
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|right
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|q
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|newBoxInternal
argument_list|(
name|field
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
return|;
block|}
block|}
DECL|method|newBoxInternal
specifier|private
specifier|static
name|Query
name|newBoxInternal
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
index|[]
name|min
parameter_list|,
name|byte
index|[]
index|[]
name|max
parameter_list|)
block|{
return|return
operator|new
name|PointRangeQuery
argument_list|(
name|field
argument_list|,
name|min
argument_list|,
operator|new
name|boolean
index|[]
block|{
literal|true
block|,
literal|true
block|}
argument_list|,
name|max
argument_list|,
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|false
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|int
name|dimension
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
if|if
condition|(
name|dimension
operator|==
literal|0
condition|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|decodeLat
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|dimension
operator|==
literal|1
condition|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|decodeLon
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Create a query for matching points within the specified distance of the supplied location.    */
DECL|method|newDistanceQuery
specifier|public
specifier|static
name|Query
name|newDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
return|return
operator|new
name|PointDistanceQuery
argument_list|(
name|field
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radiusMeters
argument_list|)
return|;
block|}
comment|/**     * Create a query for matching a polygon.    *<p>    * The supplied {@code polyLats}/{@code polyLons} must be clockwise or counter-clockwise.    */
DECL|method|newPolygonQuery
specifier|public
specifier|static
name|Query
name|newPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
index|[]
name|polyLats
parameter_list|,
name|double
index|[]
name|polyLons
parameter_list|)
block|{
return|return
operator|new
name|PointInPolygonQuery
argument_list|(
name|field
argument_list|,
name|polyLats
argument_list|,
name|polyLons
argument_list|)
return|;
block|}
block|}
end_class
end_unit
