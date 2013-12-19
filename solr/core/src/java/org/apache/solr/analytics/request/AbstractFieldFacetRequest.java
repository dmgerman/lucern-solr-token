begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|request
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import
begin_comment
comment|/**  * An abstract request for a facet over a single field, such as a field or range facet.  */
end_comment
begin_class
DECL|class|AbstractFieldFacetRequest
specifier|public
specifier|abstract
class|class
name|AbstractFieldFacetRequest
implements|implements
name|FacetRequest
block|{
DECL|field|field
specifier|protected
name|SchemaField
name|field
init|=
literal|null
decl_stmt|;
DECL|method|AbstractFieldFacetRequest
specifier|public
name|AbstractFieldFacetRequest
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|SchemaField
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|field
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class
end_unit
