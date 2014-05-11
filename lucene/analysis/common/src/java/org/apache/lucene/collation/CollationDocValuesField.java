begin_unit
begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|SortedDocValuesField
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
name|FieldCacheRangeFilter
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
comment|/**  * Indexes collation keys as a single-valued {@link SortedDocValuesField}.  *<p>  * This is more efficient that {@link CollationKeyAnalyzer} if the field   * only has one value: no uninversion is necessary to sort on the field,   * locale-sensitive range queries can still work via {@link FieldCacheRangeFilter},   * and the underlying data structures built at index-time are likely more efficient   * and use less memory than FieldCache.  */
end_comment
begin_class
DECL|class|CollationDocValuesField
specifier|public
specifier|final
class|class
name|CollationDocValuesField
extends|extends
name|Field
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|collator
specifier|private
specifier|final
name|Collator
name|collator
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|/**    * Create a new ICUCollationDocValuesField.    *<p>    * NOTE: you should not create a new one for each document, instead    * just make one and reuse it during your indexing process, setting    * the value via {@link #setStringValue(String)}.    * @param name field name    * @param collator Collator for generating collation keys.    */
comment|// TODO: can we make this trap-free? maybe just synchronize on the collator
comment|// instead?
DECL|method|CollationDocValuesField
specifier|public
name|CollationDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SortedDocValuesField
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|collator
operator|=
operator|(
name|Collator
operator|)
name|collator
operator|.
name|clone
argument_list|()
expr_stmt|;
name|fieldsData
operator|=
name|bytes
expr_stmt|;
comment|// so wrong setters cannot be called
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|setStringValue
specifier|public
name|void
name|setStringValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|bytes
operator|.
name|bytes
operator|=
name|collator
operator|.
name|getCollationKey
argument_list|(
name|value
argument_list|)
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|bytes
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|bytes
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
block|}
block|}
end_class
end_unit
