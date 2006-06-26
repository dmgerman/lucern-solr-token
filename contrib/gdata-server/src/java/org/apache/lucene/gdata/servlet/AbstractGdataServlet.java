begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
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
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
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
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|gdata
operator|.
name|servlet
operator|.
name|handler
operator|.
name|RequestHandlerFactory
import|;
end_import
begin_comment
comment|/**   *    * Provides an abstract class to be subclassed to create an GDATA servlet   * suitable for a GDATA serverside implementation.   *    * @see javax.servlet.http.HttpServlet   *    * @author Simon Willnauer   *    */
end_comment
begin_class
DECL|class|AbstractGdataServlet
specifier|public
specifier|abstract
class|class
name|AbstractGdataServlet
extends|extends
name|HttpServlet
block|{
DECL|field|METHOD_HEADER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_HEADER_NAME
init|=
literal|"x-http-method-override"
decl_stmt|;
DECL|field|METHOD_DELETE
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_DELETE
init|=
literal|"DELETE"
decl_stmt|;
DECL|field|METHOD_GET
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_GET
init|=
literal|"GET"
decl_stmt|;
DECL|field|METHOD_POST
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_POST
init|=
literal|"POST"
decl_stmt|;
DECL|field|METHOD_PUT
specifier|private
specifier|static
specifier|final
name|String
name|METHOD_PUT
init|=
literal|"PUT"
decl_stmt|;
DECL|field|HANDLER_FACTORY
specifier|protected
specifier|static
name|RequestHandlerFactory
name|HANDLER_FACTORY
init|=
literal|null
decl_stmt|;
comment|/**       * This overwrites the protected<code>service</code> method to dispatch       * the request to the correponding<code>do</code> method. There is       * ususaly no need for overwriting this method. The GData protool and the       * Google GData API uses the<code>x-http-method-override</code> header to       * get through firewalls. The http method will be overritten by the       *<code>x-http-method-override</code> and dispatched to the       *<code>do</code><i>Xxx</i> methods defined in this class. This method       * is an GDATA-specific version of the {@link javax.servlet.Servlet#service}       * method.       *        * @see HttpServlet#service(javax.servlet.http.HttpServletRequest,       *      javax.servlet.http.HttpServletResponse)       */
annotation|@
name|Override
DECL|method|service
specifier|protected
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
if|if
condition|(
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
operator|==
literal|null
condition|)
block|{
name|super
operator|.
name|service
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
return|return;
block|}
name|overrideMethod
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
DECL|method|overrideMethod
specifier|private
name|void
name|overrideMethod
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
specifier|final
name|String
name|method
init|=
name|arg0
operator|.
name|getMethod
argument_list|()
decl_stmt|;
specifier|final
name|String
name|overrideHeaderMethod
init|=
name|arg0
operator|.
name|getHeader
argument_list|(
name|METHOD_HEADER_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|overrideHeaderMethod
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|super
operator|.
name|service
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// These methodes are use by GDATA Client APIs
if|if
condition|(
name|overrideHeaderMethod
operator|.
name|equals
argument_list|(
name|METHOD_DELETE
argument_list|)
condition|)
block|{
name|doDelete
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|overrideHeaderMethod
operator|.
name|equals
argument_list|(
name|METHOD_GET
argument_list|)
condition|)
block|{
name|doGet
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|overrideHeaderMethod
operator|.
name|equals
argument_list|(
name|METHOD_POST
argument_list|)
condition|)
block|{
name|doPost
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|overrideHeaderMethod
operator|.
name|equals
argument_list|(
name|METHOD_PUT
argument_list|)
condition|)
block|{
name|doPut
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if another method has been overwritten follow the HttpServlet
comment|// implementation
name|super
operator|.
name|service
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *       * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)      */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|arg0
parameter_list|)
throws|throws
name|ServletException
block|{
name|HANDLER_FACTORY
operator|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|lookup
argument_list|(
name|RequestHandlerFactory
operator|.
name|class
argument_list|,
name|ComponentType
operator|.
name|REQUESTHANDLERFACTORY
argument_list|)
expr_stmt|;
if|if
condition|(
name|HANDLER_FACTORY
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"service not available"
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
