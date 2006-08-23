begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|DefaultSolrParams
specifier|public
class|class
name|DefaultSolrParams
extends|extends
name|SolrParams
block|{
DECL|field|params
specifier|protected
specifier|final
name|SolrParams
name|params
decl_stmt|;
DECL|field|defaults
specifier|protected
specifier|final
name|SolrParams
name|defaults
decl_stmt|;
DECL|method|DefaultSolrParams
specifier|public
name|DefaultSolrParams
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|SolrParams
name|defaults
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|defaults
operator|=
name|defaults
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|val
else|:
name|defaults
operator|.
name|get
argument_list|(
name|param
argument_list|)
return|;
block|}
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
index|[]
name|vals
init|=
name|params
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|vals
operator|!=
literal|null
condition|?
name|vals
else|:
name|defaults
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{params("
operator|+
name|params
operator|+
literal|"),defaults("
operator|+
name|defaults
operator|+
literal|")}"
return|;
block|}
block|}
end_class
end_unit
