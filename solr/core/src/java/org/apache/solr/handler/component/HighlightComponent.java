begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|search
operator|.
name|Query
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
name|core
operator|.
name|PluginInfo
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
name|core
operator|.
name|SolrCore
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
name|DefaultSolrHighlighter
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
name|PostingsSolrHighlighter
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
name|search
operator|.
name|QParser
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
name|search
operator|.
name|QParserPlugin
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
name|search
operator|.
name|QueryParsing
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
name|search
operator|.
name|SyntaxError
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
name|util
operator|.
name|SolrPluginUtils
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
name|util
operator|.
name|plugin
operator|.
name|PluginInfoInitialized
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * TODO!  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|HighlightComponent
specifier|public
class|class
name|HighlightComponent
extends|extends
name|SearchComponent
implements|implements
name|PluginInfoInitialized
implements|,
name|SolrCoreAware
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
DECL|field|info
specifier|private
name|PluginInfo
name|info
init|=
name|PluginInfo
operator|.
name|EMPTY_INFO
decl_stmt|;
DECL|field|highlighter
specifier|private
name|SolrHighlighter
name|highlighter
decl_stmt|;
DECL|method|getHighlighter
specifier|public
specifier|static
name|SolrHighlighter
name|getHighlighter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|HighlightComponent
name|hl
init|=
operator|(
name|HighlightComponent
operator|)
name|core
operator|.
name|getSearchComponents
argument_list|()
operator|.
name|get
argument_list|(
name|HighlightComponent
operator|.
name|COMPONENT_NAME
argument_list|)
decl_stmt|;
return|return
name|hl
operator|==
literal|null
condition|?
literal|null
else|:
name|hl
operator|.
name|getHighlighter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
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
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
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
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|doHighlights
condition|)
block|{
name|String
name|hlq
init|=
name|params
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|Q
argument_list|)
decl_stmt|;
name|String
name|hlparser
init|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|QPARSER
argument_list|)
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|DEFTYPE
argument_list|,
name|QParserPlugin
operator|.
name|DEFAULT_QTYPE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|hlq
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|hlq
argument_list|,
name|hlparser
argument_list|,
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
name|rb
operator|.
name|setHighlightQuery
argument_list|(
name|parser
operator|.
name|getHighlightQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
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
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|children
init|=
name|info
operator|.
name|getChildren
argument_list|(
literal|"highlighting"
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|PluginInfo
name|pluginInfo
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getPluginInfo
argument_list|(
name|SolrHighlighter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//TODO deprecated configuration remove later
if|if
condition|(
name|pluginInfo
operator|!=
literal|null
condition|)
block|{
name|highlighter
operator|=
name|core
operator|.
name|createInitInstance
argument_list|(
name|pluginInfo
argument_list|,
name|SolrHighlighter
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|DefaultSolrHighlighter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DefaultSolrHighlighter
name|defHighlighter
init|=
operator|new
name|DefaultSolrHighlighter
argument_list|(
name|core
argument_list|)
decl_stmt|;
name|defHighlighter
operator|.
name|init
argument_list|(
name|PluginInfo
operator|.
name|EMPTY_INFO
argument_list|)
expr_stmt|;
name|highlighter
operator|=
name|defHighlighter
expr_stmt|;
block|}
block|}
else|else
block|{
name|highlighter
operator|=
name|core
operator|.
name|createInitInstance
argument_list|(
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|SolrHighlighter
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|DefaultSolrHighlighter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|rb
operator|.
name|doHighlights
condition|)
block|{
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
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
name|Query
name|highlightQuery
init|=
name|rb
operator|.
name|getHighlightQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|highlightQuery
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
name|highlightQuery
operator|=
name|rb
operator|.
name|getQparser
argument_list|()
operator|.
name|getHighlightQuery
argument_list|()
expr_stmt|;
name|rb
operator|.
name|setHighlightQuery
argument_list|(
name|highlightQuery
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
name|highlightQuery
operator|=
name|rb
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|rb
operator|.
name|setHighlightQuery
argument_list|(
name|highlightQuery
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|highlightQuery
operator|!=
literal|null
condition|)
block|{
name|boolean
name|rewrite
init|=
operator|(
name|highlighter
operator|instanceof
name|PostingsSolrHighlighter
operator|==
literal|false
operator|)
operator|&&
operator|!
operator|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|USE_PHRASE_HIGHLIGHTER
argument_list|,
literal|"true"
argument_list|)
argument_list|)
operator|&&
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|HIGHLIGHT_MULTI_TERM
argument_list|,
literal|"true"
argument_list|)
argument_list|)
operator|)
decl_stmt|;
name|highlightQuery
operator|=
name|rewrite
condition|?
name|highlightQuery
operator|.
name|rewrite
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
else|:
name|highlightQuery
expr_stmt|;
block|}
comment|// No highlighting if there is no query -- consider q.alt="*:*
if|if
condition|(
name|highlightQuery
operator|!=
literal|null
condition|)
block|{
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
name|highlightQuery
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
block|}
annotation|@
name|Override
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
index|[]
name|arr
init|=
operator|new
name|NamedList
operator|.
name|NamedListEntry
index|[
name|rb
operator|.
name|resultIds
operator|.
name|size
argument_list|()
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
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// can't expect the highlight content if there was an exception for this request
comment|// this should only happen when using shards.tolerant=true
continue|continue;
block|}
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
comment|// sdoc maybe null
if|if
condition|(
name|sdoc
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
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
index|]
operator|=
operator|new
name|NamedList
operator|.
name|NamedListEntry
argument_list|<>
argument_list|(
name|id
argument_list|,
name|hl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
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
name|SolrPluginUtils
operator|.
name|removeNulls
argument_list|(
name|arr
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getHighlighter
specifier|public
name|SolrHighlighter
name|getHighlighter
parameter_list|()
block|{
return|return
name|highlighter
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
