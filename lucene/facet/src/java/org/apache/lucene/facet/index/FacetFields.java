begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|BinaryDocValuesField
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
DECL|class|FacetFields
specifier|public
class|class
name|FacetFields
block|{
comment|// The drill-down field is added with a TokenStream, hence why it's based on
comment|// TextField type. However in practice, it is added just like StringField.
comment|// Therefore we set its IndexOptions to DOCS_ONLY.
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
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|DRILL_DOWN_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DRILL_DOWN_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|taxonomyWriter
specifier|protected
specifier|final
name|TaxonomyWriter
name|taxonomyWriter
decl_stmt|;
DECL|field|indexingParams
specifier|protected
specifier|final
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
comment|/**    * Constructs a new instance with the {@link FacetIndexingParams#DEFAULT    * default} facet indexing params.    *     * @param taxonomyWriter    *          used to resolve given categories to ordinals    */
DECL|method|FacetFields
specifier|public
name|FacetFields
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
block|{
name|this
argument_list|(
name|taxonomyWriter
argument_list|,
name|FacetIndexingParams
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new instance with the given facet indexing params.    *     * @param taxonomyWriter    *          used to resolve given categories to ordinals    * @param params    *          determines under which fields the categories should be indexed    */
DECL|method|FacetFields
specifier|public
name|FacetFields
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|,
name|FacetIndexingParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|taxonomyWriter
operator|=
name|taxonomyWriter
expr_stmt|;
name|this
operator|.
name|indexingParams
operator|=
name|params
expr_stmt|;
block|}
comment|/**    * Creates a mapping between a {@link CategoryListParams} and all    * {@link FacetLabel categories} that are associated with it.    */
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
if|if
condition|(
name|indexingParams
operator|.
name|getAllCategoryListParams
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
argument_list|,
name|categories
argument_list|)
return|;
block|}
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
name|List
argument_list|<
name|FacetLabel
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|FacetLabel
argument_list|>
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
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetLabel
argument_list|>
argument_list|()
expr_stmt|;
name|categoryLists
operator|.
name|put
argument_list|(
name|clp
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
return|return
name|categoryLists
return|;
block|}
comment|/**    * Returns the category list data, as a mapping from key to {@link BytesRef}    * which includes the encoded data. Every ordinal in {@code ordinals}    * corrspond to a {@link FacetLabel} returned from {@code categories}.    */
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
comment|/* needed for AssociationsFacetFields */
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CountingListBuilder
argument_list|(
name|categoryListParams
argument_list|,
name|indexingParams
argument_list|,
name|taxonomyWriter
argument_list|)
operator|.
name|build
argument_list|(
name|ordinals
argument_list|,
name|categories
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link DrillDownStream} for writing the categories drill-down    * terms.    */
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
name|DrillDownStream
argument_list|(
name|categories
argument_list|,
name|indexingParams
argument_list|)
return|;
block|}
comment|/**    * Returns the {@link FieldType} with which the drill-down terms should be    * indexed. The default is {@link IndexOptions#DOCS_ONLY}.    */
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
comment|/**    * Add the counting list data to the document under the given field. Note that    * the field is determined by the {@link CategoryListParams}.    */
DECL|method|addCountingListData
specifier|protected
name|void
name|addCountingListData
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|categoriesData
parameter_list|,
name|String
name|field
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|entry
range|:
name|categoriesData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
name|field
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Adds the needed facet fields to the document. */
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
name|categories
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"categories should not be null"
argument_list|)
throw|;
block|}
comment|// TODO: add reuse capabilities to this class, per CLP objects:
comment|// - drill-down field
comment|// - counting list field
comment|// - DrillDownStream
comment|// - CountingListStream
specifier|final
name|Map
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
name|createCategoryListMapping
argument_list|(
name|categories
argument_list|)
decl_stmt|;
comment|// for each CLP we add a different field for drill-down terms as well as for
comment|// counting list data.
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
comment|// should be enough for most common applications
for|for
control|(
name|Entry
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|FacetLabel
argument_list|>
argument_list|>
name|e
range|:
name|categoryLists
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|CategoryListParams
name|clp
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|field
init|=
name|clp
operator|.
name|field
decl_stmt|;
comment|// build category list data
name|ordinals
operator|.
name|length
operator|=
literal|0
expr_stmt|;
comment|// reset
name|int
name|maxNumOrds
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetLabel
name|cp
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|int
name|ordinal
init|=
name|taxonomyWriter
operator|.
name|addCategory
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|maxNumOrds
operator|+=
name|cp
operator|.
name|length
expr_stmt|;
comment|// ordinal and potentially all parents
if|if
condition|(
name|ordinals
operator|.
name|ints
operator|.
name|length
operator|<
name|maxNumOrds
condition|)
block|{
name|ordinals
operator|.
name|grow
argument_list|(
name|maxNumOrds
argument_list|)
expr_stmt|;
block|}
name|ordinals
operator|.
name|ints
index|[
name|ordinals
operator|.
name|length
operator|++
index|]
operator|=
name|ordinal
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|categoriesData
init|=
name|getCategoryListData
argument_list|(
name|clp
argument_list|,
name|ordinals
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// add the counting list data
name|addCountingListData
argument_list|(
name|doc
argument_list|,
name|categoriesData
argument_list|,
name|field
argument_list|)
expr_stmt|;
comment|// add the drill-down field
name|DrillDownStream
name|drillDownStream
init|=
name|getDrillDownStream
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Field
name|drillDown
init|=
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|drillDownStream
argument_list|,
name|drillDownFieldType
argument_list|()
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|drillDown
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
