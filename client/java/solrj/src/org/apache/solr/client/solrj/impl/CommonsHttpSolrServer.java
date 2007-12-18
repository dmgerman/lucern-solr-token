begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
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
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|net
operator|.
name|MalformedURLException
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|httpclient
operator|.
name|HttpClient
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
name|httpclient
operator|.
name|HttpException
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
name|httpclient
operator|.
name|HttpMethod
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
name|httpclient
operator|.
name|HttpMethodBase
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
name|httpclient
operator|.
name|HttpStatus
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
name|httpclient
operator|.
name|MultiThreadedHttpConnectionManager
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
name|httpclient
operator|.
name|methods
operator|.
name|GetMethod
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
name|httpclient
operator|.
name|methods
operator|.
name|InputStreamRequestEntity
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
name|httpclient
operator|.
name|methods
operator|.
name|PostMethod
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|MultipartRequestEntity
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|Part
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
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|PartBase
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
name|client
operator|.
name|solrj
operator|.
name|ResponseParser
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|util
operator|.
name|ClientUtils
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
name|DefaultSolrParams
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
name|ModifiableSolrParams
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
name|NamedList
import|;
end_import
begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|CommonsHttpSolrServer
specifier|public
class|class
name|CommonsHttpSolrServer
extends|extends
name|BaseSolrServer
block|{
DECL|field|AGENT
specifier|public
specifier|static
specifier|final
name|String
name|AGENT
init|=
literal|"Solr["
operator|+
name|CommonsHttpSolrServer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"] 1.0"
decl_stmt|;
comment|/**    * The URL of the Solr server.    */
DECL|field|_baseURL
specifier|protected
name|String
name|_baseURL
decl_stmt|;
DECL|field|_invariantParams
specifier|protected
name|ModifiableSolrParams
name|_invariantParams
decl_stmt|;
DECL|field|_processor
specifier|protected
name|ResponseParser
name|_processor
decl_stmt|;
DECL|field|_connectionManager
name|MultiThreadedHttpConnectionManager
name|_connectionManager
init|=
operator|new
name|MultiThreadedHttpConnectionManager
argument_list|()
decl_stmt|;
comment|/**      * @param solrServerUrl The URL of the Solr server.  For     * example, "<code>http://localhost:8983/solr/</code>"    * if you are using the standard distribution Solr webapp     * on your local machine.    */
DECL|method|CommonsHttpSolrServer
specifier|public
name|CommonsHttpSolrServer
parameter_list|(
name|String
name|solrServerUrl
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
argument_list|(
operator|new
name|URL
argument_list|(
name|solrServerUrl
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param baseURL The URL of the Solr server.  For     * example, "<code>http://localhost:8983/solr/</code>"    * if you are using the standard distribution Solr webapp     * on your local machine.    */
DECL|method|CommonsHttpSolrServer
specifier|public
name|CommonsHttpSolrServer
parameter_list|(
name|URL
name|baseURL
parameter_list|)
block|{
name|this
operator|.
name|_baseURL
operator|=
name|baseURL
operator|.
name|toExternalForm
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|_baseURL
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|this
operator|.
name|_baseURL
operator|=
name|this
operator|.
name|_baseURL
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|this
operator|.
name|_baseURL
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// increase the default connections
name|this
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|32
argument_list|)
expr_stmt|;
comment|// 2
name|this
operator|.
name|setMaxTotalConnections
argument_list|(
literal|128
argument_list|)
expr_stmt|;
comment|// 20
comment|// by default use the XML one
name|_processor
operator|=
operator|new
name|XMLResponseParser
argument_list|()
expr_stmt|;
comment|// TODO -- expose these so that people can add things like 'u'& 'p'
name|_invariantParams
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|_invariantParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
name|_processor
operator|.
name|getWriterType
argument_list|()
argument_list|)
expr_stmt|;
name|_invariantParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------------------
comment|//------------------------------------------------------------------------
DECL|method|request
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
specifier|final
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
comment|// TODO -- need to set the WRITER TYPE!!!
comment|// params.set( SolrParams.WT, wt );
name|HttpMethod
name|method
init|=
literal|null
decl_stmt|;
name|SolrParams
name|params
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|request
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|request
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
operator|||
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/select"
expr_stmt|;
block|}
comment|// modify the path for multicore access
if|if
condition|(
name|request
operator|.
name|getCore
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
literal|"/@"
operator|+
name|request
operator|.
name|getCore
argument_list|()
operator|+
name|path
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|_invariantParams
operator|!=
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|DefaultSolrParams
argument_list|(
name|_invariantParams
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|GET
operator|==
name|request
operator|.
name|getMethod
argument_list|()
condition|)
block|{
if|if
condition|(
name|streams
operator|!=
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
literal|"GET can't send streams!"
argument_list|)
throw|;
block|}
name|method
operator|=
operator|new
name|GetMethod
argument_list|(
name|_baseURL
operator|+
name|path
operator|+
name|ClientUtils
operator|.
name|toQueryString
argument_list|(
name|params
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
operator|==
name|request
operator|.
name|getMethod
argument_list|()
condition|)
block|{
name|String
name|url
init|=
name|_baseURL
operator|+
name|path
decl_stmt|;
name|boolean
name|isMultipart
init|=
operator|(
name|streams
operator|!=
literal|null
operator|&&
name|streams
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
operator|||
name|isMultipart
condition|)
block|{
comment|// Without streams, just post the parameters
name|PostMethod
name|post
init|=
operator|new
name|PostMethod
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|p
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
index|[]
name|vals
init|=
name|params
operator|.
name|getParams
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
operator|&&
name|vals
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|v
range|:
name|vals
control|)
block|{
name|post
operator|.
name|addParameter
argument_list|(
name|p
argument_list|,
operator|(
name|v
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|v
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|post
operator|.
name|addParameter
argument_list|(
name|p
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|post
operator|.
name|getParams
argument_list|()
operator|.
name|setContentCharset
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isMultipart
condition|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Part
index|[]
name|parts
init|=
operator|new
name|Part
index|[
name|streams
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|ContentStream
name|content
range|:
name|streams
control|)
block|{
specifier|final
name|ContentStream
name|c
init|=
name|content
decl_stmt|;
name|String
name|charSet
init|=
literal|null
decl_stmt|;
name|String
name|transferEncoding
init|=
literal|null
decl_stmt|;
name|parts
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|PartBase
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|c
operator|.
name|getContentType
argument_list|()
argument_list|,
name|charSet
argument_list|,
name|transferEncoding
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|long
name|lengthOfData
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|c
operator|.
name|getSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|sendData
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|c
operator|.
name|getReader
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
comment|// Set the multi-part request
name|post
operator|.
name|setRequestEntity
argument_list|(
operator|new
name|MultipartRequestEntity
argument_list|(
name|parts
argument_list|,
name|post
operator|.
name|getParams
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|method
operator|=
name|post
expr_stmt|;
block|}
name|method
operator|=
name|post
expr_stmt|;
block|}
comment|// It is has one stream, it is the post body, put the params in the URL
else|else
block|{
name|String
name|pstr
init|=
name|ClientUtils
operator|.
name|toQueryString
argument_list|(
name|params
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|PostMethod
name|post
init|=
operator|new
name|PostMethod
argument_list|(
name|url
operator|+
name|pstr
argument_list|)
decl_stmt|;
comment|// Single stream as body
comment|// Using a loop just to get the first one
for|for
control|(
name|ContentStream
name|content
range|:
name|streams
control|)
block|{
name|post
operator|.
name|setRequestEntity
argument_list|(
operator|new
name|InputStreamRequestEntity
argument_list|(
name|content
operator|.
name|getStream
argument_list|()
argument_list|,
name|content
operator|.
name|getContentType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|method
operator|=
name|post
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"Unsupported method: "
operator|+
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"error reading streams"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|method
operator|.
name|addRequestHeader
argument_list|(
literal|"User-Agent"
argument_list|,
name|AGENT
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Execute the method.
comment|//System.out.println( "EXECUTE:"+method.getURI() );
name|int
name|statusCode
init|=
name|getHttpConnection
argument_list|()
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
if|if
condition|(
name|statusCode
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|method
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getReasonPhrase
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|method
operator|.
name|getStatusText
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"request: "
operator|+
name|method
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|statusCode
argument_list|,
name|java
operator|.
name|net
operator|.
name|URLDecoder
operator|.
name|decode
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
throw|;
block|}
comment|// Read the contents
name|String
name|charset
init|=
literal|"UTF-8"
decl_stmt|;
if|if
condition|(
name|method
operator|instanceof
name|HttpMethodBase
condition|)
block|{
name|charset
operator|=
operator|(
operator|(
name|HttpMethodBase
operator|)
name|method
operator|)
operator|.
name|getResponseCharSet
argument_list|()
expr_stmt|;
block|}
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|method
operator|.
name|getResponseBodyAsStream
argument_list|()
argument_list|,
name|charset
argument_list|)
decl_stmt|;
return|return
name|_processor
operator|.
name|processResponse
argument_list|(
name|reader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|HttpException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|method
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------------------------
comment|//-------------------------------------------------------------------
comment|/**    * Parameters are added to ever request regardless.  This may be a place to add     * something like an authentication token.    */
DECL|method|getInvariantParams
specifier|public
name|ModifiableSolrParams
name|getInvariantParams
parameter_list|()
block|{
return|return
name|_invariantParams
return|;
block|}
DECL|method|getBaseURL
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
name|_baseURL
return|;
block|}
DECL|method|setBaseURL
specifier|public
name|void
name|setBaseURL
parameter_list|(
name|String
name|baseURL
parameter_list|)
block|{
name|this
operator|.
name|_baseURL
operator|=
name|baseURL
expr_stmt|;
block|}
DECL|method|getProcessor
specifier|public
name|ResponseParser
name|getProcessor
parameter_list|()
block|{
return|return
name|_processor
return|;
block|}
DECL|method|setProcessor
specifier|public
name|void
name|setProcessor
parameter_list|(
name|ResponseParser
name|processor
parameter_list|)
block|{
name|_processor
operator|=
name|processor
expr_stmt|;
block|}
DECL|method|getHttpConnection
specifier|protected
name|HttpClient
name|getHttpConnection
parameter_list|()
block|{
return|return
operator|new
name|HttpClient
argument_list|(
name|_connectionManager
argument_list|)
return|;
block|}
DECL|method|getConnectionManager
specifier|public
name|MultiThreadedHttpConnectionManager
name|getConnectionManager
parameter_list|()
block|{
return|return
name|_connectionManager
return|;
block|}
comment|/** set connectionTimeout on the underlying MultiThreadedHttpConnectionManager */
DECL|method|setConnectionTimeout
specifier|public
name|void
name|setConnectionTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|_connectionManager
operator|.
name|getParams
argument_list|()
operator|.
name|setConnectionTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/** set maxConnectionsPerHost on the underlying MultiThreadedHttpConnectionManager */
DECL|method|setDefaultMaxConnectionsPerHost
specifier|public
name|void
name|setDefaultMaxConnectionsPerHost
parameter_list|(
name|int
name|connections
parameter_list|)
block|{
name|_connectionManager
operator|.
name|getParams
argument_list|()
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
name|connections
argument_list|)
expr_stmt|;
block|}
comment|/** set maxTotalConnection on the underlying MultiThreadedHttpConnectionManager */
DECL|method|setMaxTotalConnections
specifier|public
name|void
name|setMaxTotalConnections
parameter_list|(
name|int
name|connections
parameter_list|)
block|{
name|_connectionManager
operator|.
name|getParams
argument_list|()
operator|.
name|setMaxTotalConnections
argument_list|(
name|connections
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
