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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|index
operator|.
name|PointValues
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPoint
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoShape
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
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
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
begin_comment
comment|/**  * Add this to a document to index lat/lon or x/y/z point, indexed as a 3D point.  * Multiple values are allowed: just add multiple Geo3DPoint to the document with the  * same field name.  *<p>  * This field defines static factory methods for creating a shape query:  *<ul>  *<li>{@link #newShapeQuery newShapeQuery()} for matching all points inside a specified shape  *</ul>  * @see PointValues  *  @lucene.experimental */
end_comment
begin_class
DECL|class|Geo3DPoint
specifier|public
specifier|final
class|class
name|Geo3DPoint
extends|extends
name|Field
block|{
comment|/** Indexing {@link FieldType}. */
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
literal|3
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
comment|/**     * Creates a new Geo3DPoint field with the specified lat, lon (in radians).    *    * @throws IllegalArgumentException if the field name is null or lat or lon are out of bounds    */
DECL|method|Geo3DPoint
specifier|public
name|Geo3DPoint
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
comment|// Translate lat/lon to x,y,z:
specifier|final
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
name|fillFieldsData
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
expr_stmt|;
block|}
comment|/**     * Creates a new Geo3DPoint field with the specified x,y,z.    *    * @throws IllegalArgumentException if the field name is null or lat or lon are out of bounds    */
DECL|method|Geo3DPoint
specifier|public
name|Geo3DPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fillFieldsData
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
expr_stmt|;
block|}
DECL|method|fillFieldsData
specifier|private
name|void
name|fillFieldsData
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|12
index|]
decl_stmt|;
name|encodeDimension
argument_list|(
name|x
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|encodeDimension
argument_list|(
name|y
argument_list|,
name|bytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|encodeDimension
argument_list|(
name|z
argument_list|,
name|bytes
argument_list|,
literal|2
operator|*
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
comment|// public helper methods (e.g. for queries)
comment|/** Encode single dimension */
DECL|method|encodeDimension
specifier|public
specifier|static
name|void
name|encodeDimension
parameter_list|(
name|double
name|value
parameter_list|,
name|byte
name|bytes
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Geo3DUtil
operator|.
name|encodeValue
argument_list|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumMagnitude
argument_list|()
argument_list|,
name|value
argument_list|)
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
comment|/** Decode single dimension */
DECL|method|decodeDimension
specifier|public
specifier|static
name|double
name|decodeDimension
parameter_list|(
name|byte
name|value
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|Geo3DUtil
operator|.
name|decodeValueCenter
argument_list|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumMagnitude
argument_list|()
argument_list|,
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|value
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a query matching all points inside the provided shape.    *     * @param field field name. must not be {@code null}.    * @param shape Which {@link GeoShape} to match    */
DECL|method|newShapeQuery
specifier|public
specifier|static
name|Query
name|newShapeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|GeoShape
name|shape
parameter_list|)
block|{
return|return
operator|new
name|PointInGeo3DShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
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
literal|" x="
operator|+
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" y="
operator|+
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
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
literal|" z="
operator|+
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
literal|2
operator|*
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
block|}
end_class
end_unit
