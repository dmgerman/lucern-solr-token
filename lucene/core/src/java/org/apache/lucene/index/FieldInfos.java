begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
begin_comment
comment|/**   * Collection of {@link FieldInfo}s (accessible by number or by name).  *  @lucene.experimental  */
end_comment
begin_class
DECL|class|FieldInfos
specifier|public
specifier|abstract
class|class
name|FieldInfos
implements|implements
name|Cloneable
implements|,
name|Iterable
argument_list|<
name|FieldInfo
argument_list|>
block|{
comment|/**    * Returns a deep clone of this FieldInfos instance.    */
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|abstract
name|FieldInfos
name|clone
parameter_list|()
function_decl|;
comment|/**    * Return the fieldinfo object referenced by the field name    * @return the FieldInfo object or null when the given fieldName    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
specifier|abstract
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/**    * Return the fieldinfo object referenced by the fieldNumber.    * @param fieldNumber    * @return the FieldInfo object or null when the given fieldNumber    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
specifier|abstract
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
function_decl|;
comment|/**    * Returns an iterator over all the fieldinfo objects present,    * ordered by ascending field number    */
comment|// TODO: what happens if in fact a different order is used?
DECL|method|iterator
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|FieldInfo
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/**    * @return number of fields    */
DECL|method|size
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
comment|/** Returns true if any fields have positions */
DECL|method|hasProx
specifier|public
name|boolean
name|hasProx
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
argument_list|()
operator|&&
name|fi
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Returns true if any fields have freqs */
DECL|method|hasFreq
specifier|public
name|boolean
name|hasFreq
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
argument_list|()
operator|&&
name|fi
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @return true if at least one field has any vectors    */
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @return true if at least one field has doc values    */
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @return true if at least one field has any norms    */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
