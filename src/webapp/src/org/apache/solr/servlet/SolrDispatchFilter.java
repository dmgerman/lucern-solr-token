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
name|File
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
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
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
name|core
operator|.
name|Config
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
name|MultiCore
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
comment|/**  * This filter looks at the incoming URL maps them to handlers defined in solrconfig.xml  */
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrDispatchFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|singlecore
specifier|protected
name|SolrCore
name|singlecore
decl_stmt|;
DECL|field|multicore
specifier|protected
name|MultiCore
name|multicore
decl_stmt|;
DECL|field|parsers
specifier|protected
name|SolrRequestParsers
name|parsers
decl_stmt|;
DECL|field|handleSelect
specifier|protected
name|boolean
name|handleSelect
init|=
literal|false
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
DECL|field|solrConfigFilename
specifier|protected
name|String
name|solrConfigFilename
init|=
literal|null
decl_stmt|;
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
name|boolean
name|abortOnConfigurationError
init|=
literal|true
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
name|solrConfigFilename
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"solrconfig-filename"
argument_list|)
expr_stmt|;
comment|// Find a valid solr core
name|SolrCore
name|core
init|=
literal|null
decl_stmt|;
name|multicore
operator|=
name|MultiCore
operator|.
name|getRegistry
argument_list|()
expr_stmt|;
if|if
condition|(
name|multicore
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Using existing multicore configuration"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|instanceDir
init|=
name|SolrResourceLoader
operator|.
name|locateInstanceDir
argument_list|()
decl_stmt|;
name|File
name|multiconfig
init|=
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
literal|"multicore.xml"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"looking for multicore.xml: "
operator|+
name|multiconfig
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|multiconfig
operator|.
name|exists
argument_list|()
condition|)
block|{
name|multicore
operator|.
name|load
argument_list|(
name|instanceDir
argument_list|,
name|multiconfig
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|multicore
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|singlecore
operator|=
literal|null
expr_stmt|;
name|core
operator|=
name|multicore
operator|.
name|getDefaultCore
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|this
operator|.
name|solrConfigFilename
operator|==
literal|null
condition|)
block|{
name|singlecore
operator|=
operator|new
name|SolrCore
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|SolrConfig
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|singlecore
operator|=
operator|new
name|SolrCore
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|SolrConfig
argument_list|(
name|this
operator|.
name|solrConfigFilename
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|core
operator|=
name|singlecore
expr_stmt|;
block|}
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
comment|// Read global configuration
comment|// Only the first registered core configures the following attributes
name|Config
name|globalConfig
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
decl_stmt|;
name|long
name|uploadLimitKB
init|=
name|globalConfig
operator|.
name|getInt
argument_list|(
literal|"requestDispatcher/requestParsers/@multipartUploadLimitInKB"
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
comment|// 2MB default
name|boolean
name|enableRemoteStreams
init|=
name|globalConfig
operator|.
name|getBool
argument_list|(
literal|"requestDispatcher/requestParsers/@enableRemoteStreaming"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|parsers
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
name|enableRemoteStreams
argument_list|,
name|uploadLimitKB
argument_list|)
expr_stmt|;
comment|// Let this filter take care of /select?xxx format
name|this
operator|.
name|handleSelect
operator|=
name|globalConfig
operator|.
name|getBool
argument_list|(
literal|"requestDispatcher/@handleSelect"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// should it keep going if we hit an error?
name|abortOnConfigurationError
operator|=
name|globalConfig
operator|.
name|getBool
argument_list|(
literal|"abortOnConfigurationError"
argument_list|,
literal|true
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
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
literal|"Could not start SOLR. Check solr/home property"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|add
argument_list|(
name|t
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
comment|// Optionally abort if we found a sever error
if|if
condition|(
name|abortOnConfigurationError
operator|&&
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Severe errors in solr configuration.\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Check your log files for more detailed information on what may be wrong.\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"If you want solr to continue after configuration errors, change: \n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<abortOnConfigurationError>false</abortOnConfigurationError>\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|multicore
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"in multicore.xml\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"in solrconfig.xml\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Throwable
name|t
range|:
name|SolrConfig
operator|.
name|severeErrors
control|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------------------------------------------"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Servlet containers behave slightly differently if you throw an exception during
comment|// initialization.  Resin will display that error for every page, jetty prints it in
comment|// the logs, but continues normally.  (We will see a 404 rather then the real error)
comment|// rather then leave the behavior undefined, lets cache the error and spit it out
comment|// for every request.
name|abortErrorMessage
operator|=
name|sw
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//throw new ServletException( abortErrorMessage );
block|}
name|log
operator|.
name|info
argument_list|(
literal|"SolrDispatchFilter.init() done"
argument_list|)
expr_stmt|;
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|multicore
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|singlecore
operator|!=
literal|null
condition|)
block|{
name|singlecore
operator|.
name|close
argument_list|()
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
name|request
operator|instanceof
name|HttpServletRequest
condition|)
block|{
name|SolrQueryRequest
name|solrReq
init|=
literal|null
decl_stmt|;
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
try|try
block|{
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
comment|// By default use the single core.  If multicore is enabled, look for one.
name|SolrRequestHandler
name|handler
init|=
literal|null
decl_stmt|;
name|SolrCore
name|core
init|=
name|singlecore
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
comment|// Perhaps this is a muli-core admin page?
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|multicore
operator|.
name|getAdminPath
argument_list|()
argument_list|)
condition|)
block|{
name|handler
operator|=
name|multicore
operator|.
name|getMultiCoreHandler
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
name|String
name|corename
init|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|core
operator|=
name|multicore
operator|.
name|getCore
argument_list|(
name|corename
argument_list|)
expr_stmt|;
comment|// invalid core name is ok.  It could fall through to some other request
block|}
block|}
block|}
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
comment|// Only try to parse the handler *if* a valid core exists
comment|// when multi-core is enabled, the path can lead to a null core.
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
block|}
if|if
condition|(
name|handler
operator|==
literal|null
operator|&&
name|handleSelect
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
name|parsers
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
if|if
condition|(
name|qt
operator|!=
literal|null
operator|&&
name|qt
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
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
literal|"Invalid query type.  Do not use /select to access: "
operator|+
name|qt
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|core
operator|=
name|multicore
operator|.
name|getDefaultCore
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|solrReq
operator|==
literal|null
condition|)
block|{
name|solrReq
operator|=
name|parsers
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
name|SolrConfig
name|conf
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|Method
operator|.
name|POST
operator|!=
name|reqMethod
condition|)
block|{
name|HttpCacheHeaderUtil
operator|.
name|setCacheControlHeader
argument_list|(
name|conf
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
comment|// unless we have been explicitly told not to, do cache validation
if|if
condition|(
operator|!
name|conf
operator|.
name|getHttpCachingConfig
argument_list|()
operator|.
name|isNever304
argument_list|()
condition|)
block|{
comment|// if we've confirmed cache validation, return immediately
if|if
condition|(
name|HttpCacheHeaderUtil
operator|.
name|doCacheHeaderValidation
argument_list|(
name|solrReq
argument_list|,
name|req
argument_list|,
name|resp
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|SolrQueryResponse
name|solrRsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
comment|/* even for HEAD requests, we need to execute the handler to            * ensure we don't get an error (and to make sure the correct             * QueryResponseWriter is selectedand we get the correct            * Content-Type)            */
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
return|return;
block|}
comment|// Now write it out
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
name|response
operator|.
name|setContentType
argument_list|(
name|responseWriter
operator|.
name|getContentType
argument_list|(
name|solrReq
argument_list|,
name|solrRsp
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Method
operator|.
name|HEAD
operator|==
name|Method
operator|.
name|getMethod
argument_list|(
name|req
operator|.
name|getMethod
argument_list|()
argument_list|)
condition|)
block|{
comment|// nothing to write out, waited this long just to get ContentType
return|return;
block|}
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
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
return|return;
block|}
comment|// otherwise, let's ensure the core is in the SolrCore request attribute so
comment|// the servlet/jsp can retrieve it
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
name|singlecore
operator|==
literal|null
operator|&&
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
name|logOnce
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
name|warning
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
name|ex
operator|.
name|getMessage
argument_list|()
operator|+
name|trace
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------
comment|//---------------------------------------------------------------------
comment|/**    * Should the filter handle /select even if it is not mapped in solrconfig.xml    *     * This will use consistent error handling for /select?qt=xxx and /update/xml    *     */
DECL|method|isHandleSelect
specifier|public
name|boolean
name|isHandleSelect
parameter_list|()
block|{
return|return
name|handleSelect
return|;
block|}
DECL|method|setHandleSelect
specifier|public
name|void
name|setHandleSelect
parameter_list|(
name|boolean
name|handleSelect
parameter_list|)
block|{
name|this
operator|.
name|handleSelect
operator|=
name|handleSelect
expr_stmt|;
block|}
comment|/**    * set the prefix for all paths.  This is useful if you want to apply the    * filter to something other then *.      *     * For example, if web.xml specifies:    *     *<filter-mapping>    *<filter-name>SolrRequestFilter</filter-name>    *<url-pattern>/xxx/*</url-pattern>    *</filter-mapping>    *     * Make sure to set the PathPrefix to "/xxx" either with this function    * or in web.xml    *     *<init-param>    *<param-name>path-prefix</param-name>    *<param-value>/xxx</param-value>    *</init-param>    *     */
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
