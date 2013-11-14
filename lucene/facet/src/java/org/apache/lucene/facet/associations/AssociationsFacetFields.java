begin_unit
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
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
name|document
operator|.
name|TextField
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
name|index
operator|.
name|DrillDownStream
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
name|index
operator|.
name|FacetFields
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
name|params
operator|.
name|CategoryListParams
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
name|params
operator|.
name|FacetIndexingParams
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
name|taxonomy
operator|.
name|FacetLabel
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
name|taxonomy
operator|.
name|TaxonomyWriter
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|IntsRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A utility class for adding facet fields to a document. Usually one field will  * be added for all facets, however per the  * {@link FacetIndexingParams#getCategoryListParams(FacetLabel)}, one field  * may be added for every group of facets.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|AssociationsFacetFields
specifier|public
class|class
name|AssociationsFacetFields
extends|extends
name|FacetFields
block|{
comment|// The drill-down field is added with a TokenStream, hence why it's based on
comment|// TextField type. However for associations, we store a payload with the
comment|// association value, therefore we set IndexOptions to include positions.
DECL|field|DRILL_DOWN_TYPE
specifier|private
specifier|static
specifier|final
name|FieldType
name|DRILL_DOWN_TYPE
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
static|static
block|{
name|DRILL_DOWN_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|DRILL_DOWN_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a new instance with the {@link FacetIndexingParams#DEFAULT    * default} facet indexing params.    *     * @param taxonomyWriter    *          used to resolve given categories to ordinals    */
DECL|method|AssociationsFacetFields
specifier|public
name|AssociationsFacetFields
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
block|{
name|super
argument_list|(
name|taxonomyWriter
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new instance with the given facet indexing params.    *     * @param taxonomyWriter    *          used to resolve given categories to ordinals    * @param params    *          determines under which fields the categories should be indexed    */
DECL|method|AssociationsFacetFields
specifier|public
name|AssociationsFacetFields
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|,
name|FacetIndexingParams
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|taxonomyWriter
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCategoryListMapping
specifier|protected
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
argument_list|>
name|createCategoryListMapping
parameter_list|(
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
name|categories
parameter_list|)
block|{
name|CategoryAssociationsContainer
name|categoryAssociations
init|=
operator|(
name|CategoryAssociationsContainer
operator|)
name|categories
decl_stmt|;
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
argument_list|>
name|categoryLists
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetLabel
name|cp
range|:
name|categories
control|)
block|{
comment|// each category may be indexed under a different field, so add it to the right list.
name|CategoryListParams
name|clp
init|=
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|CategoryAssociationsContainer
name|clpContainer
init|=
operator|(
name|CategoryAssociationsContainer
operator|)
name|categoryLists
operator|.
name|get
argument_list|(
name|clp
argument_list|)
decl_stmt|;
if|if
condition|(
name|clpContainer
operator|==
literal|null
condition|)
block|{
name|clpContainer
operator|=
operator|new
name|CategoryAssociationsContainer
argument_list|()
expr_stmt|;
name|categoryLists
operator|.
name|put
argument_list|(
name|clp
argument_list|,
name|clpContainer
argument_list|)
expr_stmt|;
block|}
name|clpContainer
operator|.
name|setAssociation
argument_list|(
name|cp
argument_list|,
name|categoryAssociations
operator|.
name|getAssociation
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|categoryLists
return|;
block|}
annotation|@
name|Override
DECL|method|getCategoryListData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|getCategoryListData
parameter_list|(
name|CategoryListParams
name|categoryListParams
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|,
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
name|AssociationsListBuilder
name|associations
init|=
operator|new
name|AssociationsListBuilder
argument_list|(
operator|(
name|CategoryAssociationsContainer
operator|)
name|categories
argument_list|)
decl_stmt|;
return|return
name|associations
operator|.
name|build
argument_list|(
name|ordinals
argument_list|,
name|categories
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDrillDownStream
specifier|protected
name|DrillDownStream
name|getDrillDownStream
parameter_list|(
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
name|categories
parameter_list|)
block|{
return|return
operator|new
name|AssociationsDrillDownStream
argument_list|(
operator|(
name|CategoryAssociationsContainer
operator|)
name|categories
argument_list|,
name|indexingParams
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|drillDownFieldType
specifier|protected
name|FieldType
name|drillDownFieldType
parameter_list|()
block|{
return|return
name|DRILL_DOWN_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|addFields
specifier|public
name|void
name|addFields
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|categories
operator|instanceof
name|CategoryAssociationsContainer
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"categories must be of type "
operator|+
name|CategoryAssociationsContainer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
name|super
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|categories
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
