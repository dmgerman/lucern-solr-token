begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_comment
comment|/**  * A numeric field that can contain 32-bit signed two's complement integer values.  *  *<ul>  *<li>Min Value Allowed: -2147483648</li>  *<li>Max Value Allowed: 2147483647</li>  *</ul>  *   * @see Integer  */
end_comment
begin_class
DECL|class|TrieIntField
specifier|public
class|class
name|TrieIntField
extends|extends
name|TrieField
implements|implements
name|IntValueFieldType
block|{
block|{
name|type
operator|=
name|TrieTypes
operator|.
name|INTEGER
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toNativeType
specifier|public
name|Object
name|toNativeType
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|val
operator|instanceof
name|Number
condition|)
return|return
operator|(
operator|(
name|Number
operator|)
name|val
operator|)
operator|.
name|intValue
argument_list|()
return|;
try|try
block|{
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|Float
name|v
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|intValue
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|toNativeType
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
end_unit
