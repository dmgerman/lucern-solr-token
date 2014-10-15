begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|ContentStream
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
name|InitParams
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
name|SolrRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
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
begin_class
DECL|class|DumpRequestHandler
specifier|public
class|class
name|DumpRequestHandler
extends|extends
name|RequestHandlerBase
block|{
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
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
comment|// Show params
name|rsp
operator|.
name|add
argument_list|(
literal|"params"
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|toNamedList
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|returnParams
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
literal|"param"
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnParams
operator|!=
literal|null
condition|)
block|{
name|NamedList
name|params
init|=
operator|(
name|NamedList
operator|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|returnParam
range|:
name|returnParams
control|)
block|{
name|String
index|[]
name|vals
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|returnParam
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|val
range|:
name|vals
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|returnParam
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
literal|"initArgs"
argument_list|)
argument_list|)
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"initArgs"
argument_list|,
name|initArgs
argument_list|)
expr_stmt|;
comment|// Write the streams...
if|if
condition|(
name|req
operator|.
name|getContentStreams
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ArrayList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Cycle through each stream
for|for
control|(
name|ContentStream
name|content
range|:
name|req
operator|.
name|getContentStreams
argument_list|()
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|stream
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|stream
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|content
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|add
argument_list|(
literal|"sourceInfo"
argument_list|,
name|content
operator|.
name|getSourceInfo
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|content
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|add
argument_list|(
literal|"contentType"
argument_list|,
name|content
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
name|content
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|add
argument_list|(
literal|"stream"
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|streams
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"streams"
argument_list|,
name|streams
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"context"
argument_list|,
name|req
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Dump handler (debug)"
return|;
block|}
annotation|@
name|Override
DECL|method|getSubHandler
specifier|public
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|subPath
parameter_list|)
block|{
if|if
condition|(
name|subpaths
operator|!=
literal|null
operator|&&
name|subpaths
operator|.
name|contains
argument_list|(
name|subPath
argument_list|)
condition|)
return|return
name|this
return|;
return|return
literal|null
return|;
block|}
DECL|field|subpaths
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|subpaths
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|args
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|!=
literal|null
condition|)
name|subpaths
operator|=
name|nl
operator|.
name|getAll
argument_list|(
literal|"subpath"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
