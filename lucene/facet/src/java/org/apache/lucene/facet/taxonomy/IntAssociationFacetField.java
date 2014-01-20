begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Document
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
begin_comment
comment|/** Add an instance of this to your {@link Document} to add  *  a facet label associated with an int.  Use {@link  *  TaxonomyFacetSumIntAssociations} to aggregate int values  *  per facet label at search time.  *   *  @lucene.experimental */
end_comment
begin_class
DECL|class|IntAssociationFacetField
specifier|public
class|class
name|IntAssociationFacetField
extends|extends
name|AssociationFacetField
block|{
comment|/** Creates this from {@code dim} and {@code path} and an    *  int association */
DECL|method|IntAssociationFacetField
specifier|public
name|IntAssociationFacetField
parameter_list|(
name|int
name|assoc
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|intToBytesRef
argument_list|(
name|assoc
argument_list|)
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Encodes an {@code int} as a 4-byte {@link BytesRef},    *  big-endian. */
DECL|method|intToBytesRef
specifier|public
specifier|static
name|BytesRef
name|intToBytesRef
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
comment|// big-endian:
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|v
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|/** Decodes a previously encoded {@code int}. */
DECL|method|bytesRefToInt
specifier|public
specifier|static
name|int
name|bytesRefToInt
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
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
literal|"IntAssociationFacetField(dim="
operator|+
name|dim
operator|+
literal|" path="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|path
argument_list|)
operator|+
literal|" value="
operator|+
name|bytesRefToInt
argument_list|(
name|assoc
argument_list|)
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit