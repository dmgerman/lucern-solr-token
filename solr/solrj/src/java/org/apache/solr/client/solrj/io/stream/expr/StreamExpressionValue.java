begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.expr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**   * Basic string stream expression  */
end_comment
begin_class
DECL|class|StreamExpressionValue
specifier|public
class|class
name|StreamExpressionValue
implements|implements
name|StreamExpressionParameter
block|{
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|method|StreamExpressionValue
specifier|public
name|StreamExpressionValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|withValue
specifier|public
name|StreamExpressionValue
name|withValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|!=
name|StreamExpressionValue
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|StreamExpressionValue
name|check
init|=
operator|(
name|StreamExpressionValue
operator|)
name|other
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|value
operator|&&
literal|null
operator|==
name|check
operator|.
name|value
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
literal|null
operator|==
name|this
operator|.
name|value
operator|||
literal|null
operator|==
name|check
operator|.
name|value
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|value
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
block|}
end_class
end_unit
