begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import
begin_comment
comment|/**  *<code>MinFloatFunction</code> returns the min of it's components.  */
end_comment
begin_class
DECL|class|MinFloatFunction
specifier|public
class|class
name|MinFloatFunction
extends|extends
name|MultiFloatFunction
block|{
DECL|method|MinFloatFunction
specifier|public
name|MinFloatFunction
parameter_list|(
name|ValueSource
index|[]
name|sources
parameter_list|)
block|{
name|super
argument_list|(
name|sources
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"min"
return|;
block|}
annotation|@
name|Override
DECL|method|func
specifier|protected
name|float
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
index|[]
name|valsArr
parameter_list|)
block|{
if|if
condition|(
name|valsArr
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|0.0f
return|;
name|float
name|val
init|=
name|Float
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
for|for
control|(
name|FunctionValues
name|vals
range|:
name|valsArr
control|)
block|{
name|val
operator|=
name|Math
operator|.
name|min
argument_list|(
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|val
return|;
block|}
block|}
end_class
end_unit
