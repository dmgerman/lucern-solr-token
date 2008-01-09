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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|CommonParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|highlight
operator|.
name|SolrHighlighter
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
begin_comment
comment|/**  * TODO!  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|HighlightComponent
specifier|public
class|class
name|HighlightComponent
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
literal|"highlight"
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
block|{        }
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
name|SolrHighlighter
name|highlighter
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getHighlighter
argument_list|()
decl_stmt|;
if|if
condition|(
name|highlighter
operator|.
name|isHighlightingEnabled
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
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
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
index|[]
name|defaultHighlightFields
decl_stmt|;
comment|//TODO: get from builder by default?
if|if
condition|(
name|builder
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|defaultHighlightFields
operator|=
name|builder
operator|.
name|getQparser
argument_list|()
operator|.
name|getDefaultHighlightFields
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|defaultHighlightFields
operator|=
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|builder
operator|.
name|getHighlightQuery
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|builder
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|builder
operator|.
name|setHighlightQuery
argument_list|(
name|builder
operator|.
name|getQparser
argument_list|()
operator|.
name|getHighlightQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|setHighlightQuery
argument_list|(
name|builder
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|NamedList
name|sumData
init|=
name|highlighter
operator|.
name|doHighlighting
argument_list|(
name|builder
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|,
name|builder
operator|.
name|getHighlightQuery
argument_list|()
operator|.
name|rewrite
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|,
name|req
argument_list|,
name|defaultHighlightFields
argument_list|)
decl_stmt|;
if|if
condition|(
name|sumData
operator|!=
literal|null
condition|)
block|{
comment|// TODO ???? add this directly to the response?
name|rsp
operator|.
name|add
argument_list|(
literal|"highlighting"
argument_list|,
name|sumData
argument_list|)
expr_stmt|;
block|}
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
literal|"Highlighting"
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
