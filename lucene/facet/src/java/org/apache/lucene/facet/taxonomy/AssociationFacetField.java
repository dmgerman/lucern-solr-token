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
begin_comment
comment|// javadocs
end_comment
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
name|facet
operator|.
name|FacetField
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
name|facet
operator|.
name|Facets
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
comment|/** Add an instance of this to your {@link Document} to add  *  a facet label associated with an arbitrary byte[].  *  This will require a custom {@link Facets}  *  implementation at search time; see {@link  *  IntAssociationFacetField} and {@link  *  FloatAssociationFacetField} to use existing {@link  *  Facets} implementations.  *   *  @lucene.experimental */
end_comment
begin_class
DECL|class|AssociationFacetField
specifier|public
class|class
name|AssociationFacetField
extends|extends
name|Field
block|{
comment|/** Indexed {@link FieldType}. */
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
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Dimension for this field. */
DECL|field|dim
specifier|public
specifier|final
name|String
name|dim
decl_stmt|;
comment|/** Facet path for this field. */
DECL|field|path
specifier|public
specifier|final
name|String
index|[]
name|path
decl_stmt|;
comment|/** Associated value. */
DECL|field|assoc
specifier|public
specifier|final
name|BytesRef
name|assoc
decl_stmt|;
comment|/** Creates this from {@code dim} and {@code path} and an    *  association */
DECL|method|AssociationFacetField
specifier|public
name|AssociationFacetField
parameter_list|(
name|BytesRef
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
literal|"dummy"
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|FacetField
operator|.
name|verifyLabel
argument_list|(
name|dim
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|label
range|:
name|path
control|)
block|{
name|FacetField
operator|.
name|verifyLabel
argument_list|(
name|label
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dim
operator|=
name|dim
expr_stmt|;
name|this
operator|.
name|assoc
operator|=
name|assoc
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path must have at least one element"
argument_list|)
throw|;
block|}
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
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
literal|"AssociationFacetField(dim="
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
literal|" bytes="
operator|+
name|assoc
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
