begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import
begin_comment
comment|/** Function to raise the base "a" to the power "b"  */
end_comment
begin_class
DECL|class|PowFloatFunction
specifier|public
class|class
name|PowFloatFunction
extends|extends
name|DualFloatFunction
block|{
comment|/**    * @param   a  the base.    * @param   b  the exponent.    */
DECL|method|PowFloatFunction
specifier|public
name|PowFloatFunction
parameter_list|(
name|ValueSource
name|a
parameter_list|,
name|ValueSource
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|a
argument_list|,
name|b
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
literal|"pow"
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
name|DocValues
name|aVals
parameter_list|,
name|DocValues
name|bVals
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|pow
argument_list|(
name|aVals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|bVals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
