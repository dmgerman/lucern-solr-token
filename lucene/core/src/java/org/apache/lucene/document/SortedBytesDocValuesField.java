begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|DocValues
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
comment|/**  *<p>  * This class provides a {@link Field} that enables storing  * of a per-document {@link BytesRef} value, indexed for  * sorting.  Here's an example usage:  *   *<pre>  *   document.add(new SortedBytesDocValuesField(name, new BytesRef("hello")));  *</pre>  *   *<p>  * If you also need to store the value, you should add a  * separate {@link StoredField} instance.  *   * @see DocValues for further information  * */
end_comment
begin_class
DECL|class|SortedBytesDocValuesField
specifier|public
class|class
name|SortedBytesDocValuesField
extends|extends
name|StoredField
block|{
comment|// TODO: ideally indexer figures out var vs fixed on its own!?
DECL|field|TYPE_FIXED_LEN
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_FIXED_LEN
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_FIXED_LEN
operator|.
name|setDocValueType
argument_list|(
name|DocValues
operator|.
name|Type
operator|.
name|BYTES_FIXED_SORTED
argument_list|)
expr_stmt|;
name|TYPE_FIXED_LEN
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|TYPE_VAR_LEN
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_VAR_LEN
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_VAR_LEN
operator|.
name|setDocValueType
argument_list|(
name|DocValues
operator|.
name|Type
operator|.
name|BYTES_VAR_SORTED
argument_list|)
expr_stmt|;
name|TYPE_VAR_LEN
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|method|SortedBytesDocValuesField
specifier|public
name|SortedBytesDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|bytes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|SortedBytesDocValuesField
specifier|public
name|SortedBytesDocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|,
name|boolean
name|isFixedLength
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|isFixedLength
condition|?
name|TYPE_FIXED_LEN
else|:
name|TYPE_VAR_LEN
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|bytes
expr_stmt|;
block|}
block|}
end_class
end_unit
