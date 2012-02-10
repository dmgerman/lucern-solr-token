begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import
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
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|common
operator|.
name|cloud
operator|.
name|CloudState
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
name|cloud
operator|.
name|Slice
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|util
operator|.
name|FastWriter
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
name|*
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
name|*
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
name|BinaryQueryResponseWriter
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
name|servlet
operator|.
name|cache
operator|.
name|HttpCacheHeaderUtil
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
name|servlet
operator|.
name|cache
operator|.
name|Method
import|;
end_import
begin_comment
comment|/**  * This filter looks at the incoming URL maps them to handlers defined in solrconfig.xml  *  * @since solr 1.2  */
end_comment
begin_class
DECL|class|SolrDispatchFilter
specifier|public
class|class
name|SolrDispatchFilter
implements|implements
name|Filter
block|{
DECL|field|log
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrDispatchFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cores
specifier|protected
specifier|volatile
name|CoreContainer
name|cores
decl_stmt|;
DECL|field|pathPrefix
specifier|protected
name|String
name|pathPrefix
init|=
literal|null
decl_stmt|;
comment|// strip this from the beginning of a path
DECL|field|abortErrorMessage
specifier|protected
name|String
name|abortErrorMessage
init|=
literal|null
decl_stmt|;
DECL|field|parsers
specifier|protected
specifier|final
name|Map
argument_list|<
name|SolrConfig
argument_list|,
name|SolrRequestParsers
argument_list|>
name|parsers
init|=
operator|new
name|WeakHashMap
argument_list|<
name|SolrConfig
argument_list|,
name|SolrRequestParsers
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|adminRequestParser
specifier|protected
specifier|final
name|SolrRequestParsers
name|adminRequestParser
decl_stmt|;
DECL|field|UTF8
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|method|SolrDispatchFilter
specifier|public
name|SolrDispatchFilter
parameter_list|()
block|{
try|try
block|{
name|adminRequestParser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
operator|new
name|Config
argument_list|(
literal|null
argument_list|,
literal|"solr"
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"<root/>"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//unlikely
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init()"
argument_list|)
expr_stmt|;
name|CoreContainer
operator|.
name|Initializer
name|init
init|=
name|createInitializer
argument_list|()
decl_stmt|;
try|try
block|{
comment|// web.xml configuration
name|this
operator|.
name|pathPrefix
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"path-prefix"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cores
operator|=
name|init
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"user.dir="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// catch this so our filter still works
name|log
operator|.
name|error
argument_list|(
literal|"Could not start Solr. Check solr/home property and the logs"
argument_list|)
expr_stmt|;
name|SolrCore
operator|.
name|log
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init() done"
argument_list|)
expr_stmt|;
block|}
DECL|method|getCores
specifier|public
name|CoreContainer
name|getCores
parameter_list|()
block|{
return|return
name|cores
return|;
block|}
comment|/** Method to override to change how CoreContainer initialization is performed. */
DECL|method|createInitializer
specifier|protected
name|CoreContainer
operator|.
name|Initializer
name|createInitializer
parameter_list|()
block|{
return|return
operator|new
name|CoreContainer
operator|.
name|Initializer
argument_list|()
return|;
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cores
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doFilter
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
if|if
condition|(
name|abortErrorMessage
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|HttpServletResponse
operator|)
name|response
operator|)
operator|.
name|sendError
argument_list|(
literal|500
argument_list|,
name|abortErrorMessage
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|this
operator|.
name|cores
operator|==
literal|null
condition|)
block|{
operator|(
operator|(
name|HttpServletResponse
operator|)
name|response
operator|)
operator|.
name|sendError
argument_list|(
literal|403
argument_list|,
literal|"Server is shutting down"
argument_list|)
expr_stmt|;
return|return;
block|}
name|CoreContainer
name|cores
init|=
name|this
operator|.
name|cores
decl_stmt|;
if|if
condition|(
name|request
operator|instanceof
name|HttpServletRequest
condition|)
block|{
name|HttpServletRequest
name|req
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|HttpServletResponse
name|resp
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
decl_stmt|;
name|SolrRequestHandler
name|handler
init|=
literal|null
decl_stmt|;
name|SolrQueryRequest
name|solrReq
init|=
literal|null
decl_stmt|;
name|SolrCore
name|core
init|=
literal|null
decl_stmt|;
name|String
name|corename
init|=
literal|""
decl_stmt|;
try|try
block|{
comment|// put the core container in request attribute
name|req
operator|.
name|setAttribute
argument_list|(
literal|"org.apache.solr.CoreContainer"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|req
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getPathInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// this lets you handle /update/commit when /update is a servlet
name|path
operator|+=
name|req
operator|.
name|getPathInfo
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pathPrefix
operator|!=
literal|null
operator|&&
name|path
operator|.
name|startsWith
argument_list|(
name|pathPrefix
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|pathPrefix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check for management path
name|String
name|alternate
init|=
name|cores
operator|.
name|getManagementPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|alternate
operator|!=
literal|null
operator|&&
name|path
operator|.
name|startsWith
argument_list|(
name|alternate
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|alternate
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// unused feature ?
name|int
name|idx
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|// save the portion after the ':' for a 'handler' path parameter
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
comment|// Check for the core admin page
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|cores
operator|.
name|getAdminPath
argument_list|()
argument_list|)
condition|)
block|{
name|handler
operator|=
name|cores
operator|.
name|getMultiCoreHandler
argument_list|()
expr_stmt|;
name|solrReq
operator|=
name|adminRequestParser
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|path
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|handleAdminRequest
argument_list|(
name|req
argument_list|,
name|response
argument_list|,
name|handler
argument_list|,
name|solrReq
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
comment|//otherwise, we should find a core from the path
name|idx
operator|=
name|path
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|>
literal|1
condition|)
block|{
comment|// try to get the corename as a request parameter first
name|corename
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
name|corename
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|cores
operator|.
name|isZooKeeperAware
argument_list|()
operator|&&
name|corename
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cores
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|core
operator|==
literal|null
operator|&&
name|cores
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
comment|// we couldn't find the core - lets make sure a collection was not specified instead
name|core
operator|=
name|getCoreByCollection
argument_list|(
name|cores
argument_list|,
name|core
argument_list|,
name|corename
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
comment|// we found a core, update the path
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
comment|// TODO: if we couldn't find it locally, look on other nodes
block|}
comment|// With a valid core...
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
specifier|final
name|SolrConfig
name|config
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
decl_stmt|;
comment|// get or create/cache the parser for the core
name|SolrRequestParsers
name|parser
init|=
literal|null
decl_stmt|;
name|parser
operator|=
name|parsers
operator|.
name|get
argument_list|(
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
name|parser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|parsers
operator|.
name|put
argument_list|(
name|config
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
comment|// Determine the handler from the url path if not set
comment|// (we might already have selected the cores handler)
if|if
condition|(
name|handler
operator|==
literal|null
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// don't match "" or "/" as valid path
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// no handler yet but allowed to handle select; let's check
if|if
condition|(
name|handler
operator|==
literal|null
operator|&&
name|parser
operator|.
name|isHandleSelect
argument_list|()
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
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|solrReq
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|core
argument_list|,
name|path
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|String
name|qt
init|=
name|solrReq
operator|.
name|getParams
argument_list|()
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
block|}
comment|// With a valid handler and a valid core...
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
comment|// if not a /select, create the request
if|if
condition|(
name|solrReq
operator|==
literal|null
condition|)
block|{
name|solrReq
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|core
argument_list|,
name|path
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Method
name|reqMethod
init|=
name|Method
operator|.
name|getMethod
argument_list|(
name|req
operator|.
name|getMethod
argument_list|()
argument_list|)
decl_stmt|;
name|HttpCacheHeaderUtil
operator|.
name|setCacheControlHeader
argument_list|(
name|config
argument_list|,
name|resp
argument_list|,
name|reqMethod
argument_list|)
expr_stmt|;
comment|// unless we have been explicitly told not to, do cache validation
comment|// if we fail cache validation, execute the query
if|if
condition|(
name|config
operator|.
name|getHttpCachingConfig
argument_list|()
operator|.
name|isNever304
argument_list|()
operator|||
operator|!
name|HttpCacheHeaderUtil
operator|.
name|doCacheHeaderValidation
argument_list|(
name|solrReq
argument_list|,
name|req
argument_list|,
name|reqMethod
argument_list|,
name|resp
argument_list|)
condition|)
block|{
name|SolrQueryResponse
name|solrRsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
comment|/* even for HEAD requests, we need to execute the handler to                  * ensure we don't get an error (and to make sure the correct                  * QueryResponseWriter is selected and we get the correct                  * Content-Type)                  */
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|execute
argument_list|(
name|req
argument_list|,
name|handler
argument_list|,
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
expr_stmt|;
name|HttpCacheHeaderUtil
operator|.
name|checkHttpCachingVeto
argument_list|(
name|solrRsp
argument_list|,
name|resp
argument_list|,
name|reqMethod
argument_list|)
expr_stmt|;
comment|// add info to http headers
comment|//TODO: See SOLR-232 and SOLR-267.
comment|/*try {                   NamedList solrRspHeader = solrRsp.getResponseHeader();                  for (int i=0; i<solrRspHeader.size(); i++) {                    ((javax.servlet.http.HttpServletResponse) response).addHeader(("Solr-" + solrRspHeader.getName(i)), String.valueOf(solrRspHeader.getVal(i)));                  }                 } catch (ClassCastException cce) {                   log.log(Level.WARNING, "exception adding response header log information", cce);                 }*/
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|solrReq
argument_list|)
decl_stmt|;
name|writeResponse
argument_list|(
name|solrRsp
argument_list|,
name|response
argument_list|,
name|responseWriter
argument_list|,
name|solrReq
argument_list|,
name|reqMethod
argument_list|)
expr_stmt|;
block|}
return|return;
comment|// we are done with a valid handler
block|}
comment|// otherwise (we have a core), let's ensure the core is in the SolrCore request attribute so
comment|// a servlet/jsp can retrieve it
else|else
block|{
name|req
operator|.
name|setAttribute
argument_list|(
literal|"org.apache.solr.SolrCore"
argument_list|,
name|core
argument_list|)
expr_stmt|;
comment|// Modify the request so each core gets its own /admin
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/admin"
argument_list|)
condition|)
block|{
name|req
operator|.
name|getRequestDispatcher
argument_list|(
name|pathPrefix
operator|==
literal|null
condition|?
name|path
else|:
name|pathPrefix
operator|+
name|path
argument_list|)
operator|.
name|forward
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"no handler or core retrieved for "
operator|+
name|path
operator|+
literal|", follow through..."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|sendError
argument_list|(
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
if|if
condition|(
name|solrReq
operator|!=
literal|null
condition|)
block|{
name|solrReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|core
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
comment|// Otherwise let the webapp handle the request
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreByCollection
specifier|private
name|SolrCore
name|getCoreByCollection
parameter_list|(
name|CoreContainer
name|cores
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|String
name|corename
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|String
name|collection
init|=
name|corename
decl_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|CloudState
name|cloudState
init|=
name|zkStateReader
operator|.
name|getCloudState
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|cloudState
operator|.
name|getSlices
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// look for a core on this node
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|entries
init|=
name|slices
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|done
label|:
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
comment|// first see if we have the leader
name|ZkNodeProps
name|leaderProps
init|=
name|cloudState
operator|.
name|getLeader
argument_list|(
name|collection
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|core
operator|=
name|checkProps
argument_list|(
name|cores
argument_list|,
name|core
argument_list|,
name|path
argument_list|,
name|leaderProps
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
break|break
name|done
break|;
block|}
comment|// check everyone then
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|shards
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getShards
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
argument_list|>
name|shardEntries
init|=
name|shards
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|shardEntry
range|:
name|shardEntries
control|)
block|{
name|ZkNodeProps
name|zkProps
init|=
name|shardEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|core
operator|=
name|checkProps
argument_list|(
name|cores
argument_list|,
name|core
argument_list|,
name|path
argument_list|,
name|zkProps
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
break|break
name|done
break|;
block|}
block|}
block|}
return|return
name|core
return|;
block|}
DECL|method|checkProps
specifier|private
name|SolrCore
name|checkProps
parameter_list|(
name|CoreContainer
name|cores
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|String
name|path
parameter_list|,
name|ZkNodeProps
name|zkProps
parameter_list|)
block|{
name|String
name|corename
decl_stmt|;
if|if
condition|(
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
name|zkProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|corename
operator|=
name|zkProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
expr_stmt|;
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
name|corename
argument_list|)
expr_stmt|;
block|}
return|return
name|core
return|;
block|}
DECL|method|handleAdminRequest
specifier|private
name|void
name|handleAdminRequest
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|SolrRequestHandler
name|handler
parameter_list|,
name|SolrQueryRequest
name|solrReq
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|solrResp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|responseHeader
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|solrResp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
name|responseHeader
argument_list|)
expr_stmt|;
name|NamedList
name|toLog
init|=
name|solrResp
operator|.
name|getToLog
argument_list|()
decl_stmt|;
name|toLog
operator|.
name|add
argument_list|(
literal|"webapp"
argument_list|,
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|)
expr_stmt|;
name|toLog
operator|.
name|add
argument_list|(
literal|"path"
argument_list|,
name|solrReq
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|toLog
operator|.
name|add
argument_list|(
literal|"params"
argument_list|,
literal|"{"
operator|+
name|solrReq
operator|.
name|getParamString
argument_list|()
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|solrReq
argument_list|,
name|solrResp
argument_list|)
expr_stmt|;
name|SolrCore
operator|.
name|setResponseHeaderValues
argument_list|(
name|handler
argument_list|,
name|solrReq
argument_list|,
name|solrResp
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|toLog
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|toLog
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
name|toLog
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|val
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|QueryResponseWriter
name|respWriter
init|=
name|SolrCore
operator|.
name|DEFAULT_RESPONSE_WRITERS
operator|.
name|get
argument_list|(
name|solrReq
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|respWriter
operator|==
literal|null
condition|)
name|respWriter
operator|=
name|SolrCore
operator|.
name|DEFAULT_RESPONSE_WRITERS
operator|.
name|get
argument_list|(
literal|"standard"
argument_list|)
expr_stmt|;
name|writeResponse
argument_list|(
name|solrResp
argument_list|,
name|response
argument_list|,
name|respWriter
argument_list|,
name|solrReq
argument_list|,
name|Method
operator|.
name|getMethod
argument_list|(
name|req
operator|.
name|getMethod
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeResponse
specifier|private
name|void
name|writeResponse
parameter_list|(
name|SolrQueryResponse
name|solrRsp
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|QueryResponseWriter
name|responseWriter
parameter_list|,
name|SolrQueryRequest
name|solrReq
parameter_list|,
name|Method
name|reqMethod
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|solrRsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sendError
argument_list|(
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|,
name|solrRsp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Now write it out
specifier|final
name|String
name|ct
init|=
name|responseWriter
operator|.
name|getContentType
argument_list|(
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
decl_stmt|;
comment|// don't call setContentType on null
if|if
condition|(
literal|null
operator|!=
name|ct
condition|)
name|response
operator|.
name|setContentType
argument_list|(
name|ct
argument_list|)
expr_stmt|;
if|if
condition|(
name|Method
operator|.
name|HEAD
operator|!=
name|reqMethod
condition|)
block|{
if|if
condition|(
name|responseWriter
operator|instanceof
name|BinaryQueryResponseWriter
condition|)
block|{
name|BinaryQueryResponseWriter
name|binWriter
init|=
operator|(
name|BinaryQueryResponseWriter
operator|)
name|responseWriter
decl_stmt|;
name|binWriter
operator|.
name|write
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|charset
init|=
name|ContentStreamBase
operator|.
name|getCharsetFromContentType
argument_list|(
name|ct
argument_list|)
decl_stmt|;
name|Writer
name|out
init|=
operator|(
name|charset
operator|==
literal|null
operator|||
name|charset
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"UTF-8"
argument_list|)
operator|)
condition|?
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|UTF8
argument_list|)
else|:
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|out
operator|=
operator|new
name|FastWriter
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
comment|//else http HEAD request, nothing to write out, waited this long just to get ContentType
block|}
block|}
DECL|method|execute
specifier|protected
name|void
name|execute
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|SolrRequestHandler
name|handler
parameter_list|,
name|SolrQueryRequest
name|sreq
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
comment|// a custom filter could add more stuff to the request before passing it on.
comment|// for example: sreq.getContext().put( "HttpServletRequest", req );
comment|// used for logging query stats in SolrCore.execute()
name|sreq
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
literal|"webapp"
argument_list|,
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|getCore
argument_list|()
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|sreq
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|sendError
specifier|protected
name|void
name|sendError
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|,
name|Throwable
name|ex
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|code
init|=
literal|500
decl_stmt|;
name|String
name|trace
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|ex
operator|instanceof
name|SolrException
condition|)
block|{
name|code
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|ex
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
name|String
name|msg
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Throwable
name|th
init|=
name|ex
init|;
name|th
operator|!=
literal|null
condition|;
name|th
operator|=
name|th
operator|.
name|getCause
argument_list|()
control|)
block|{
name|msg
operator|=
name|th
operator|.
name|getMessage
argument_list|()
expr_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
break|break;
block|}
comment|// For any regular code, don't include the stack trace
if|if
condition|(
name|code
operator|==
literal|500
operator|||
name|code
operator|<
literal|100
condition|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
name|trace
operator|=
literal|"\n\n"
operator|+
name|sw
operator|.
name|toString
argument_list|()
expr_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|null
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|// non standard codes have undefined results with various servers
if|if
condition|(
name|code
operator|<
literal|100
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"invalid return code: "
operator|+
name|code
argument_list|)
expr_stmt|;
name|code
operator|=
literal|500
expr_stmt|;
block|}
block|}
name|res
operator|.
name|sendError
argument_list|(
name|code
argument_list|,
name|msg
operator|+
name|trace
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------
comment|//---------------------------------------------------------------------
comment|/**    * Set the prefix for all paths.  This is useful if you want to apply the    * filter to something other then /*, perhaps because you are merging this    * filter into a larger web application.    *    * For example, if web.xml specifies:    *    *<filter-mapping>    *<filter-name>SolrRequestFilter</filter-name>    *<url-pattern>/xxx/*</url-pattern>    *</filter-mapping>    *    * Make sure to set the PathPrefix to "/xxx" either with this function    * or in web.xml.    *    *<init-param>    *<param-name>path-prefix</param-name>    *<param-value>/xxx</param-value>    *</init-param>    *    */
DECL|method|setPathPrefix
specifier|public
name|void
name|setPathPrefix
parameter_list|(
name|String
name|pathPrefix
parameter_list|)
block|{
name|this
operator|.
name|pathPrefix
operator|=
name|pathPrefix
expr_stmt|;
block|}
DECL|method|getPathPrefix
specifier|public
name|String
name|getPathPrefix
parameter_list|()
block|{
return|return
name|pathPrefix
return|;
block|}
block|}
end_class
end_unit
