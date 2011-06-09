begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|MultiMapSolrParams
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|ServletSolrParams
specifier|public
class|class
name|ServletSolrParams
extends|extends
name|MultiMapSolrParams
block|{
DECL|method|ServletSolrParams
specifier|public
name|ServletSolrParams
parameter_list|(
name|ServletRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|req
operator|.
name|getParameterMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|s
init|=
name|arr
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|// screen out blank parameters
return|return
name|s
return|;
block|}
block|}
end_class
end_unit
