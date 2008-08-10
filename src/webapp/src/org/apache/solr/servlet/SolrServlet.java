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
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|http
operator|.
name|HttpServlet
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
begin_comment
comment|/**  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|SolrServlet
specifier|public
class|class
name|SolrServlet
extends|extends
name|HttpServlet
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
name|SolrServlet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|hasMulticore
specifier|private
name|boolean
name|hasMulticore
init|=
literal|false
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrServlet.init()"
argument_list|)
expr_stmt|;
comment|// Check if the "multicore.xml" file exists -- if so, this is an invalid servlet
comment|// (even if there is only one core...)
name|String
name|instanceDir
init|=
name|SolrResourceLoader
operator|.
name|locateInstanceDir
argument_list|()
decl_stmt|;
name|File
name|fconf
init|=
operator|new
name|File
argument_list|(
name|instanceDir
argument_list|,
literal|"multicore.xml"
argument_list|)
decl_stmt|;
name|hasMulticore
operator|=
name|fconf
operator|.
name|exists
argument_list|()
expr_stmt|;
comment|// we deliberately do not initialize a SolrCore because of SOLR-597
comment|// https://issues.apache.org/jira/browse/SOLR-597
name|log
operator|.
name|info
argument_list|(
literal|"SolrServlet.init() done"
argument_list|)
expr_stmt|;
block|}
DECL|method|doPost
specifier|public
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
if|if
condition|(
name|hasMulticore
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
literal|"Missing solr core name in path"
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
name|SolrServletRequest
name|solrReq
init|=
operator|new
name|SolrServletRequest
argument_list|(
name|core
argument_list|,
name|request
argument_list|)
decl_stmt|;
empty_stmt|;
name|SolrQueryResponse
name|solrRsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
try|try
block|{
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|solrReq
operator|.
name|getQueryType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warning
argument_list|(
literal|"Unknown Request Handler '"
operator|+
name|solrReq
operator|.
name|getQueryType
argument_list|()
operator|+
literal|"' :"
operator|+
name|solrReq
argument_list|)
expr_stmt|;
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
literal|"Unknown Request Handler '"
operator|+
name|solrReq
operator|.
name|getQueryType
argument_list|()
operator|+
literal|"'"
argument_list|,
literal|true
argument_list|)
throw|;
block|}
name|core
operator|.
name|execute
argument_list|(
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
operator|==
literal|null
condition|)
block|{
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
block|}
else|else
block|{
name|Exception
name|e
init|=
name|solrRsp
operator|.
name|getException
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
literal|500
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
name|rc
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
name|sendErr
argument_list|(
name|rc
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|logged
condition|)
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|sendErr
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|sendErr
argument_list|(
literal|500
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// This releases the IndexReader associated with the request
name|solrReq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sendErr
specifier|final
name|void
name|sendErr
parameter_list|(
name|int
name|rc
parameter_list|,
name|String
name|msg
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
block|{
try|try
block|{
comment|// hmmm, what if this was already set to text/xml?
try|try
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|QueryResponseWriter
operator|.
name|CONTENT_TYPE_TEXT_UTF8
argument_list|)
expr_stmt|;
comment|// response.setCharacterEncoding("UTF-8");
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
try|try
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
