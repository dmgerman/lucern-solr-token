begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|FacetParams
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
name|SolrParams
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
name|request
operator|.
name|SimpleFacets
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryResponse
import|;
end_import
begin_comment
comment|/**  * TODO!  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|FacetComponent
specifier|public
class|class
name|FacetComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"facet"
decl_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|ResponseBuilder
name|builder
init|=
name|SearchHandler
operator|.
name|getResponseBuilder
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setNeedDocSet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Actually run the query    */
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|ResponseBuilder
name|builder
init|=
name|SearchHandler
operator|.
name|getResponseBuilder
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|SimpleFacets
name|f
init|=
operator|new
name|SimpleFacets
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|builder
operator|.
name|getResults
argument_list|()
operator|.
name|docSet
argument_list|,
name|params
argument_list|)
decl_stmt|;
comment|// TODO ???? add this directly to the response?
name|rsp
operator|.
name|add
argument_list|(
literal|"facet_counts"
argument_list|,
name|f
operator|.
name|getFacetCounts
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/////////////////////////////////////////////
comment|///  SolrInfoMBean
comment|////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Handle Faceting"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
