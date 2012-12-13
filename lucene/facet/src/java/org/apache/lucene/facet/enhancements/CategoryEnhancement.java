begin_unit
begin_package
DECL|package|org.apache.lucene.facet.enhancements
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
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
name|analysis
operator|.
name|TokenStream
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
name|enhancements
operator|.
name|params
operator|.
name|EnhancementsIndexingParams
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
name|attributes
operator|.
name|CategoryAttribute
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
name|attributes
operator|.
name|CategoryProperty
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
name|streaming
operator|.
name|CategoryListTokenizer
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
name|streaming
operator|.
name|CategoryParentsStream
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * This interface allows easy addition of enhanced category features. Usually, a  * {@link CategoryEnhancement} will correspond to a {@link CategoryProperty}.  *<p>  * A category enhancement can contribute to the index in two possible ways:  *<ol>  *<li>To each category with data relevant to the enhancement, add this data to  * the category's token payload, through  * {@link #getCategoryTokenBytes(CategoryAttribute)}. This data will be read  * during search using {@link #extractCategoryTokenData(byte[], int, int)}.</li>  *<li>To each document which contains categories with data relevant to the  * enhancement, add a {@link CategoryListTokenizer} through  * {@link #getCategoryListTokenizer(TokenStream, EnhancementsIndexingParams, TaxonomyWriter)}  * . The {@link CategoryListTokenizer} should add a single token which includes  * all the enhancement relevant data from the categories. The category list  * token's text is defined by {@link #getCategoryListTermText()}.</li>  *</ol>  *   * @lucene.experimental  */
end_comment
begin_interface
DECL|interface|CategoryEnhancement
specifier|public
interface|interface
name|CategoryEnhancement
block|{
comment|/**    * Get the bytes to be added to the category token payload for this    * enhancement.    *<p>    *<b>NOTE</b>: The returned array is copied, it is recommended to allocate a    * new one each time.    *<p>    * The bytes generated by this method are the input of    * {@link #extractCategoryTokenData(byte[], int, int)}.    *     * @param categoryAttribute    *          The attribute of the category.    * @return The bytes to be added to the category token payload for this    *         enhancement.    */
DECL|method|getCategoryTokenBytes
name|byte
index|[]
name|getCategoryTokenBytes
parameter_list|(
name|CategoryAttribute
name|categoryAttribute
parameter_list|)
function_decl|;
comment|/**    * Get the data of this enhancement from a category token payload.    *<p>    * The input bytes for this method are generated in    * {@link #getCategoryTokenBytes(CategoryAttribute)}.    *     * @param buffer    *          The payload buffer.    * @param offset    *          The offset of this enhancement's data in the buffer.    * @param length    *          The length of this enhancement's data (bytes).    * @return An Object containing the data.    */
DECL|method|extractCategoryTokenData
name|Object
name|extractCategoryTokenData
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**    * Declarative method to indicate whether this enhancement generates separate    * category list.    *     * @return {@code true} if generates category list, else {@code false}.    */
DECL|method|generatesCategoryList
name|boolean
name|generatesCategoryList
parameter_list|()
function_decl|;
comment|/**    * Returns the text of this enhancement's category list term.    *     * @return The text of this enhancement's category list term.    */
DECL|method|getCategoryListTermText
name|String
name|getCategoryListTermText
parameter_list|()
function_decl|;
comment|/**    * Get the {@link CategoryListTokenizer} which generates the category list for    * this enhancement. If {@link #generatesCategoryList()} returns {@code false}    * this method will not be called.    *     * @param tokenizer    *          The input stream containing categories.    * @param indexingParams    *          The indexing params to use.    * @param taxonomyWriter    *          The taxonomy to add categories and get their ordinals.    * @return A {@link CategoryListTokenizer} generating the category list for    *         this enhancement, with {@code tokenizer} as it's input.    */
DECL|method|getCategoryListTokenizer
name|CategoryListTokenizer
name|getCategoryListTokenizer
parameter_list|(
name|TokenStream
name|tokenizer
parameter_list|,
name|EnhancementsIndexingParams
name|indexingParams
parameter_list|,
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
function_decl|;
comment|/**    * Get a {@link CategoryProperty} class to be retained when creating    * {@link CategoryParentsStream}.    *     * @return the {@link CategoryProperty} class to be retained when creating    *         {@link CategoryParentsStream}, or {@code null} if there is no such    *         property.    */
DECL|method|getRetainableProperty
name|CategoryProperty
name|getRetainableProperty
parameter_list|()
function_decl|;
comment|/**    * Category enhancements must override {@link Object#equals(Object)}, as it is    * used in    * {@link EnhancementsPayloadIterator#getCategoryData(CategoryEnhancement)}.    */
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
function_decl|;
block|}
end_interface
end_unit
