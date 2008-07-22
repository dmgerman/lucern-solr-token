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
name|HighlightParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrHighlighter
name|highlighter
init|=
name|rb
operator|.
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getHighlighter
argument_list|()
decl_stmt|;
name|rb
operator|.
name|doHighlights
operator|=
name|highlighter
operator|.
name|isHighlightingEnabled
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|doHighlights
condition|)
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
name|rb
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|defaultHighlightFields
operator|=
name|rb
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
name|rb
operator|.
name|getHighlightQuery
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|rb
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|rb
operator|.
name|setHighlightQuery
argument_list|(
name|rb
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
name|rb
operator|.
name|setHighlightQuery
argument_list|(
name|rb
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
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|,
name|rb
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
name|rb
operator|.
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
DECL|method|modifyRequest
specifier|public
name|void
name|modifyRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SearchComponent
name|who
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doHighlights
condition|)
return|return;
comment|// Turn on highlighting only only when retrieving fields
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
operator|)
operator|!=
literal|0
condition|)
block|{
name|sreq
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_GET_HIGHLIGHTS
expr_stmt|;
comment|// should already be true...
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handleResponses
specifier|public
name|void
name|handleResponses
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
if|if
condition|(
name|rb
operator|.
name|doHighlights
operator|&&
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
name|NamedList
name|hlResult
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|Object
index|[]
name|arr
init|=
operator|new
name|Object
index|[
name|rb
operator|.
name|resultIds
operator|.
name|size
argument_list|()
operator|*
literal|2
index|]
decl_stmt|;
comment|// TODO: make a generic routine to do automatic merging of id keyed data
for|for
control|(
name|ShardRequest
name|sreq
range|:
name|rb
operator|.
name|finished
control|)
block|{
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_HIGHLIGHTS
operator|)
operator|==
literal|0
condition|)
continue|continue;
for|for
control|(
name|ShardResponse
name|srsp
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|NamedList
name|hl
init|=
operator|(
name|NamedList
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"highlighting"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|hl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ShardDoc
name|sdoc
init|=
name|rb
operator|.
name|resultIds
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|sdoc
operator|.
name|positionInResponse
decl_stmt|;
name|arr
index|[
name|idx
operator|<<
literal|1
index|]
operator|=
name|id
expr_stmt|;
name|arr
index|[
operator|(
name|idx
operator|<<
literal|1
operator|)
operator|+
literal|1
index|]
operator|=
name|hl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// remove nulls in case not all docs were able to be retrieved
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"highlighting"
argument_list|,
name|removeNulls
argument_list|(
operator|new
name|SimpleOrderedMap
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|arr
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeNulls
specifier|static
name|NamedList
name|removeNulls
parameter_list|(
name|NamedList
name|nl
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|NamedList
name|newList
init|=
name|nl
operator|instanceof
name|SimpleOrderedMap
condition|?
operator|new
name|SimpleOrderedMap
argument_list|()
else|:
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nl
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|n
init|=
name|nl
operator|.
name|getName
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|nl
operator|.
name|getVal
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newList
return|;
block|}
block|}
return|return
name|nl
return|;
block|}
comment|////////////////////////////////////////////
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
