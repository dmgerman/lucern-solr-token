begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|HashMap
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
name|MapSolrParams
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
name|ContentStreamBase
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
name|CoreContainer
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
name|CoreDescriptor
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
name|SolrConfig
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
name|core
operator|.
name|SolrResourceLoader
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
name|request
operator|.
name|SolrRequestInfo
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
name|QueryResponseWriter
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_comment
comment|/**  * DirectSolrConnection provides an interface to Solr that is similar to  * the the HTTP interface, but does not require an HTTP connection.  *   * This class is designed to be as simple as possible and allow for more flexibility  * in how you interface to Solr.  *   *  * @since solr 1.2  */
end_comment
begin_class
DECL|class|DirectSolrConnection
specifier|public
class|class
name|DirectSolrConnection
block|{
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|parser
specifier|protected
specifier|final
name|SolrRequestParsers
name|parser
decl_stmt|;
comment|/**    * Initialize using an explicit SolrCore    */
DECL|method|DirectSolrConnection
specifier|public
name|DirectSolrConnection
parameter_list|(
name|SolrCore
name|c
parameter_list|)
block|{
name|core
operator|=
name|c
expr_stmt|;
name|parser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
name|c
operator|.
name|getSolrConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * For example:    *     * String json = solr.request( "/select?qt=dismax&wt=json&q=...", null );    * String xml = solr.request( "/update", "&lt;add&gt;&lt;doc&gt;&lt;field ..." );    */
DECL|method|request
specifier|public
name|String
name|request
parameter_list|(
name|String
name|pathAndParams
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
name|SolrParams
name|params
init|=
literal|null
decl_stmt|;
name|int
name|idx
init|=
name|pathAndParams
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|path
operator|=
name|pathAndParams
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|params
operator|=
name|SolrRequestParsers
operator|.
name|parseQueryString
argument_list|(
name|pathAndParams
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|pathAndParams
expr_stmt|;
name|params
operator|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|request
argument_list|(
name|path
argument_list|,
name|params
argument_list|,
name|body
argument_list|)
return|;
block|}
DECL|method|request
specifier|public
name|String
name|request
parameter_list|(
name|String
name|path
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Extract the handler from the path or params
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
if|if
condition|(
literal|"/select"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
literal|"/select/"
operator|.
name|equalsIgnoreCase
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|qt
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|qt
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
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
literal|"unknown handler: "
operator|+
name|qt
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
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
literal|"unknown handler: "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
name|request
argument_list|(
name|handler
argument_list|,
name|params
argument_list|,
name|body
argument_list|)
return|;
block|}
DECL|method|request
specifier|public
name|String
name|request
parameter_list|(
name|SolrRequestHandler
name|handler
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make a stream for the 'body' content
name|List
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
operator|&&
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|body
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
name|req
operator|=
name|parser
operator|.
name|buildRequestFrom
argument_list|(
name|core
argument_list|,
name|params
argument_list|,
name|streams
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
comment|// Now write it out
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|SolrRequestInfo
operator|.
name|clearRequestInfo
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Use this method to close the underlying SolrCore.    *     * @since solr 1.3    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
