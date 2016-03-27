begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.geopoint.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|search
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
name|index
operator|.
name|IndexReader
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
name|FieldValueQuery
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
name|LegacyNumericRangeQuery
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
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|TermEncoding
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
comment|/** Implements a simple bounding box query on a GeoPoint field. This is inspired by  * {@link LegacyNumericRangeQuery} and is implemented using a  * two phase approach. First, candidate terms are queried using a numeric  * range based on the morton codes of the min and max lat/lon pairs. Terms  * passing this initial filter are passed to a final check that verifies whether  * the decoded lat/lon falls within (or on the boundary) of the query bounding box.  * The value comparisons are subject to a precision tolerance defined in  * {@value org.apache.lucene.spatial.util.GeoEncodingUtils#TOLERANCE}  *  * NOTES:  *    1.  All latitude/longitude values must be in decimal degrees.  *    2.  Complex computational geometry (e.g., dateline wrapping) is not supported  *    3.  For more advanced GeoSpatial indexing and query operations see spatial module  *    4.  This is well suited for small rectangles, large bounding boxes may result  *        in many terms, depending whether the bounding box falls on the boundary of  *        many cells (degenerate case)  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointInBBoxQuery
specifier|public
class|class
name|GeoPointInBBoxQuery
extends|extends
name|Query
block|{
comment|/** field name */
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
comment|/** minimum latitude value (in degrees) */
DECL|field|minLat
specifier|protected
specifier|final
name|double
name|minLat
decl_stmt|;
comment|/** minimum longitude value (in degrees) */
DECL|field|minLon
specifier|protected
specifier|final
name|double
name|minLon
decl_stmt|;
comment|/** maximum latitude value (in degrees) */
DECL|field|maxLat
specifier|protected
specifier|final
name|double
name|maxLat
decl_stmt|;
comment|/** maximum longitude value (in degrees) */
DECL|field|maxLon
specifier|protected
specifier|final
name|double
name|maxLon
decl_stmt|;
comment|/** term encoding enum to define how the points are encoded (PREFIX or NUMERIC) */
DECL|field|termEncoding
specifier|protected
specifier|final
name|TermEncoding
name|termEncoding
decl_stmt|;
comment|/**    * Constructs a query for all {@link org.apache.lucene.spatial.geopoint.document.GeoPointField} types that fall within a    * defined bounding box    */
DECL|method|GeoPointInBBoxQuery
specifier|public
name|GeoPointInBBoxQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a query for all {@link org.apache.lucene.spatial.geopoint.document.GeoPointField} types that fall within a    * defined bounding box. Accepts optional {@link org.apache.lucene.spatial.geopoint.document.GeoPointField.TermEncoding} parameter    */
DECL|method|GeoPointInBBoxQuery
specifier|public
name|GeoPointInBBoxQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|TermEncoding
name|termEncoding
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|termEncoding
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"termEncoding cannot be null"
argument_list|)
throw|;
block|}
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|minLat
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|maxLat
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|minLon
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|maxLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
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
name|termEncoding
operator|=
name|termEncoding
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
comment|// short-circuit to match all if specifying the whole map
if|if
condition|(
name|minLat
operator|==
name|GeoUtils
operator|.
name|MIN_LAT_INCL
operator|&&
name|maxLat
operator|==
name|GeoUtils
operator|.
name|MAX_LAT_INCL
operator|&&
name|minLon
operator|==
name|GeoUtils
operator|.
name|MIN_LON_INCL
operator|&&
name|maxLon
operator|==
name|GeoUtils
operator|.
name|MAX_LON_INCL
condition|)
block|{
comment|// FieldValueQuery is valid since DocValues are *required* for GeoPointField
return|return
operator|new
name|FieldValueQuery
argument_list|(
name|field
argument_list|)
return|;
block|}
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bqb
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|GeoPointInBBoxQueryImpl
name|left
init|=
operator|new
name|GeoPointInBBoxQueryImpl
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
operator|-
literal|180.0D
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
name|bqb
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
name|GeoPointInBBoxQueryImpl
name|right
init|=
operator|new
name|GeoPointInBBoxQueryImpl
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
literal|180.0D
argument_list|)
decl_stmt|;
name|bqb
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
name|bqb
operator|.
name|build
argument_list|()
return|;
block|}
return|return
operator|new
name|GeoPointInBBoxQueryImpl
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
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
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|" Lower Left: ["
argument_list|)
operator|.
name|append
argument_list|(
name|minLat
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|minLon
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Upper Right: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxLat
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|maxLon
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|GeoPointInBBoxQuery
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|GeoPointInBBoxQuery
name|that
init|=
operator|(
name|GeoPointInBBoxQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|minLat
argument_list|,
name|minLat
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|maxLat
argument_list|,
name|maxLat
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|minLon
argument_list|,
name|minLon
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|maxLon
argument_list|,
name|maxLon
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|minLat
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|maxLat
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|minLon
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|maxLon
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** getter method for retrieving the field name */
DECL|method|getField
specifier|public
specifier|final
name|String
name|getField
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
comment|/** getter method for retrieving the minimum latitude (in degrees) */
DECL|method|getMinLat
specifier|public
specifier|final
name|double
name|getMinLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|minLat
return|;
block|}
comment|/** getter method for retrieving the maximum latitude (in degrees) */
DECL|method|getMaxLat
specifier|public
specifier|final
name|double
name|getMaxLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxLat
return|;
block|}
comment|/** getter method for retrieving the minimum longitude (in degrees) */
DECL|method|getMinLon
specifier|public
specifier|final
name|double
name|getMinLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|minLon
return|;
block|}
comment|/** getter method for retrieving the maximum longitude (in degrees) */
DECL|method|getMaxLon
specifier|public
specifier|final
name|double
name|getMaxLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxLon
return|;
block|}
block|}
end_class
end_unit
