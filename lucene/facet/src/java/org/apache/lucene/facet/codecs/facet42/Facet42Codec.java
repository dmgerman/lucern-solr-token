begin_unit
begin_package
DECL|package|org.apache.lucene.facet.codecs.facet42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|codecs
operator|.
name|facet42
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|codecs
operator|.
name|DocValuesFormat
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
name|codecs
operator|.
name|lucene42
operator|.
name|Lucene42Codec
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
begin_comment
comment|/**  * Same as {@link Lucene42Codec} except it uses {@link Facet42DocValuesFormat}  * for facet fields (faster-but-more-RAM-consuming doc values).  *   *<p>  *<b>NOTE</b>: this codec does not support facet partitions (see  * {@link FacetIndexingParams#getPartitionSize()}).  *  *<p>  *<b>NOTE</b>: this format cannot handle more than 2 GB  * of facet data in a single segment.  If your usage may hit  * this limit, you can either use Lucene's default  * DocValuesFormat, limit the maximum segment size in your  * MergePolicy, or send us a patch fixing the limitation.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Facet42Codec
specifier|public
class|class
name|Facet42Codec
extends|extends
name|Lucene42Codec
block|{
DECL|field|facetFields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|facetFields
decl_stmt|;
DECL|field|facetsDVFormat
specifier|private
specifier|final
name|DocValuesFormat
name|facetsDVFormat
init|=
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Facet42"
argument_list|)
decl_stmt|;
DECL|field|lucene42DVFormat
specifier|private
specifier|final
name|DocValuesFormat
name|lucene42DVFormat
init|=
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Lucene42"
argument_list|)
decl_stmt|;
comment|// must have that for SPI purposes
comment|/** Default constructor, uses {@link FacetIndexingParams#DEFAULT}. */
DECL|method|Facet42Codec
specifier|public
name|Facet42Codec
parameter_list|()
block|{
name|this
argument_list|(
name|FacetIndexingParams
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes with the given {@link FacetIndexingParams}. Returns the proper    * {@link DocValuesFormat} for the fields that are returned by    * {@link FacetIndexingParams#getAllCategoryListParams()}.    */
DECL|method|Facet42Codec
specifier|public
name|Facet42Codec
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|)
block|{
if|if
condition|(
name|fip
operator|.
name|getPartitionSize
argument_list|()
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this Codec does not support partitions"
argument_list|)
throw|;
block|}
name|this
operator|.
name|facetFields
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|fip
operator|.
name|getAllCategoryListParams
argument_list|()
control|)
block|{
name|facetFields
operator|.
name|add
argument_list|(
name|clp
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDocValuesFormatForField
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|facetFields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
name|facetsDVFormat
return|;
block|}
else|else
block|{
return|return
name|lucene42DVFormat
return|;
block|}
block|}
block|}
end_class
end_unit
