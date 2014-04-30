begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.http
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|http
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
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
name|http
operator|.
name|StatusLine
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|config
operator|.
name|RequestConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|conn
operator|.
name|HttpClientConnectionManager
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|store
operator|.
name|AlreadyClosedException
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
name|util
operator|.
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Base class for Http clients.  *   * @lucene.experimental  * */
end_comment
begin_class
DECL|class|HttpClientBase
specifier|public
specifier|abstract
class|class
name|HttpClientBase
implements|implements
name|Closeable
block|{
comment|/** Default connection timeout for this client, in milliseconds. */
DECL|field|DEFAULT_CONNECTION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CONNECTION_TIMEOUT
init|=
literal|1000
decl_stmt|;
comment|/** Default socket timeout for this client, in milliseconds. */
DECL|field|DEFAULT_SO_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SO_TIMEOUT
init|=
literal|60000
decl_stmt|;
comment|// TODO compression?
comment|/** The URL stting to execute requests against. */
DECL|field|url
specifier|protected
specifier|final
name|String
name|url
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|httpc
specifier|private
specifier|final
name|CloseableHttpClient
name|httpc
decl_stmt|;
DECL|field|defaultConfig
specifier|private
specifier|final
name|RequestConfig
name|defaultConfig
decl_stmt|;
comment|/**    * @param conMgr    *          connection manager to use for this http client.<b>NOTE:</b>The    *          provided {@link HttpClientConnectionManager} will not be    *          {@link HttpClientConnectionManager#shutdown()} by this class.    * @param defaultConfig    *          the default {@link RequestConfig} to set on the client. If    *          {@code null} a default config is created w/ the default connection    *          and socket timeouts.    */
DECL|method|HttpClientBase
specifier|protected
name|HttpClientBase
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|path
parameter_list|,
name|HttpClientConnectionManager
name|conMgr
parameter_list|,
name|RequestConfig
name|defaultConfig
parameter_list|)
block|{
name|url
operator|=
name|normalizedURL
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultConfig
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|defaultConfig
operator|=
name|RequestConfig
operator|.
name|custom
argument_list|()
operator|.
name|setConnectionRequestTimeout
argument_list|(
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
operator|.
name|setSocketTimeout
argument_list|(
name|DEFAULT_SO_TIMEOUT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|defaultConfig
operator|=
name|defaultConfig
expr_stmt|;
block|}
name|httpc
operator|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
operator|.
name|setConnectionManager
argument_list|(
name|conMgr
argument_list|)
operator|.
name|setDefaultRequestConfig
argument_list|(
name|this
operator|.
name|defaultConfig
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/** Throws {@link AlreadyClosedException} if this client is already closed. */
DECL|method|ensureOpen
specifier|protected
specifier|final
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"HttpClient already closed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a URL out of the given parameters, translate an empty/null path to '/'    */
DECL|method|normalizedURL
specifier|private
specifier|static
name|String
name|normalizedURL
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
return|return
literal|"http://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
name|path
return|;
block|}
comment|/**    *<b>Internal:</b> response status after invocation, and in case or error attempt to read the     * exception sent by the server.     */
DECL|method|verifyStatus
specifier|protected
name|void
name|verifyStatus
parameter_list|(
name|HttpResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|StatusLine
name|statusLine
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|statusLine
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
try|try
block|{
name|throwKnownError
argument_list|(
name|response
argument_list|,
name|statusLine
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|throwKnownError
specifier|protected
name|void
name|throwKnownError
parameter_list|(
name|HttpResponse
name|response
parameter_list|,
name|StatusLine
name|statusLine
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|ObjectInputStream
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// the response stream is not an exception - could be an error in servlet.init().
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown error: "
operator|+
name|statusLine
argument_list|,
name|t
argument_list|)
throw|;
block|}
name|Throwable
name|t
decl_stmt|;
try|try
block|{
name|t
operator|=
operator|(
name|Throwable
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to read exception object: "
operator|+
name|statusLine
argument_list|,
name|th
argument_list|)
throw|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    *<b>internal:</b> execute a request and return its result    * The<code>params</code> argument is treated as: name1,value1,name2,value2,...    */
DECL|method|executePOST
specifier|protected
name|HttpResponse
name|executePOST
parameter_list|(
name|String
name|request
parameter_list|,
name|HttpEntity
name|entity
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|HttpPost
name|m
init|=
operator|new
name|HttpPost
argument_list|(
name|queryString
argument_list|(
name|request
argument_list|,
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|m
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|HttpResponse
name|response
init|=
name|httpc
operator|.
name|execute
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|verifyStatus
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    *<b>internal:</b> execute a request and return its result    * The<code>params</code> argument is treated as: name1,value1,name2,value2,...    */
DECL|method|executeGET
specifier|protected
name|HttpResponse
name|executeGET
parameter_list|(
name|String
name|request
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|HttpGet
name|m
init|=
operator|new
name|HttpGet
argument_list|(
name|queryString
argument_list|(
name|request
argument_list|,
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|httpc
operator|.
name|execute
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|verifyStatus
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|queryString
specifier|private
name|String
name|queryString
parameter_list|(
name|String
name|request
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|(
name|url
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|request
argument_list|)
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|query
operator|.
name|append
argument_list|(
name|params
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|query
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|query
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** Internal utility: input stream of the provided response */
DECL|method|responseInputStream
specifier|public
name|InputStream
name|responseInputStream
parameter_list|(
name|HttpResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|responseInputStream
argument_list|(
name|response
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// TODO: can we simplify this Consuming !?!?!?
comment|/**    * Internal utility: input stream of the provided response, which optionally     * consumes the response's resources when the input stream is exhausted.    */
DECL|method|responseInputStream
specifier|public
name|InputStream
name|responseInputStream
parameter_list|(
name|HttpResponse
name|response
parameter_list|,
name|boolean
name|consume
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|in
init|=
name|entity
operator|.
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|consume
condition|)
block|{
return|return
name|in
return|;
block|}
return|return
operator|new
name|InputStream
argument_list|()
block|{
specifier|private
name|boolean
name|consumed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|res
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|consume
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|consume
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|res
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|consume
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|res
init|=
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|consume
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
specifier|private
name|void
name|consume
parameter_list|(
name|int
name|minusOne
parameter_list|)
block|{
if|if
condition|(
operator|!
name|consumed
operator|&&
name|minusOne
operator|==
operator|-
literal|1
condition|)
block|{
try|try
block|{
name|EntityUtils
operator|.
name|consume
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignored on purpose
block|}
name|consumed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Returns true iff this instance was {@link #close() closed}, otherwise    * returns false. Note that if you override {@link #close()}, you must call    * {@code super.close()}, in order for this instance to be properly closed.    */
DECL|method|isClosed
specifier|protected
specifier|final
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/**    * Same as {@link #doAction(HttpResponse, boolean, Callable)} but always do consume at the end.    */
DECL|method|doAction
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|T
name|doAction
parameter_list|(
name|HttpResponse
name|response
parameter_list|,
name|Callable
argument_list|<
name|T
argument_list|>
name|call
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doAction
argument_list|(
name|response
argument_list|,
literal|true
argument_list|,
name|call
argument_list|)
return|;
block|}
comment|/**    * Do a specific action and validate after the action that the status is still OK,     * and if not, attempt to extract the actual server side exception. Optionally    * release the response at exit, depending on<code>consume</code> parameter.    */
DECL|method|doAction
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|T
name|doAction
parameter_list|(
name|HttpResponse
name|response
parameter_list|,
name|boolean
name|consume
parameter_list|,
name|Callable
argument_list|<
name|T
argument_list|>
name|call
parameter_list|)
throws|throws
name|IOException
block|{
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
return|return
name|call
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|th
operator|=
name|t
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|verifyStatus
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|consume
condition|)
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|th
operator|!=
literal|null
assert|;
comment|// extra safety - if we get here, it means the callable failed
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|th
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// silly, if we're here, IOUtils.reThrow always throws an exception
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|httpc
operator|.
name|close
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class
end_unit
