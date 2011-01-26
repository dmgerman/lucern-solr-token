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
name|OutputStream
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
name|net
operator|.
name|MalformedURLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|*
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|RequestEntity
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
name|request
operator|.
name|UpdateRequest
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
name|params
operator|.
name|UpdateParams
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
begin_comment
comment|/**  * {@link StreamingUpdateSolrServer} buffers all added documents and writes them  * into open HTTP connections. This class is thread safe.  *   * Although any SolrServer request can be made with this implementation,   * it is only recommended to use the {@link StreamingUpdateSolrServer} with  * /update requests.  The query interface is better suited for   *   * @version $Id: CommonsHttpSolrServer.java 724175 2008-12-07 19:07:11Z ryan $  * @since solr 1.4  */
end_comment
begin_class
DECL|class|StreamingUpdateSolrServer
specifier|public
class|class
name|StreamingUpdateSolrServer
extends|extends
name|CommonsHttpSolrServer
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StreamingUpdateSolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|queue
specifier|final
name|BlockingQueue
argument_list|<
name|UpdateRequest
argument_list|>
name|queue
decl_stmt|;
DECL|field|scheduler
specifier|final
name|ExecutorService
name|scheduler
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
decl_stmt|;
DECL|field|updateUrl
specifier|final
name|String
name|updateUrl
init|=
literal|"/update"
decl_stmt|;
DECL|field|runners
specifier|final
name|Queue
argument_list|<
name|Runner
argument_list|>
name|runners
decl_stmt|;
DECL|field|lock
specifier|volatile
name|CountDownLatch
name|lock
init|=
literal|null
decl_stmt|;
comment|// used to block everything
DECL|field|threadCount
specifier|final
name|int
name|threadCount
decl_stmt|;
comment|/**    * Uses an internal MultiThreadedHttpConnectionManager to manage http connections    *    * @param solrServerUrl The Solr server URL    * @param queueSize     The buffer size before the documents are sent to the server    * @param threadCount   The number of background threads used to empty the queue    * @throws MalformedURLException    */
DECL|method|StreamingUpdateSolrServer
specifier|public
name|StreamingUpdateSolrServer
parameter_list|(
name|String
name|solrServerUrl
parameter_list|,
name|int
name|queueSize
parameter_list|,
name|int
name|threadCount
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
argument_list|(
name|solrServerUrl
argument_list|,
literal|null
argument_list|,
name|queueSize
argument_list|,
name|threadCount
argument_list|)
expr_stmt|;
block|}
comment|/**    * Uses the supplied HttpClient to send documents to the Solr server, the HttpClient should be instantiated using a    * MultiThreadedHttpConnectionManager.    */
DECL|method|StreamingUpdateSolrServer
specifier|public
name|StreamingUpdateSolrServer
parameter_list|(
name|String
name|solrServerUrl
parameter_list|,
name|HttpClient
name|client
parameter_list|,
name|int
name|queueSize
parameter_list|,
name|int
name|threadCount
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|super
argument_list|(
name|solrServerUrl
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|UpdateRequest
argument_list|>
argument_list|(
name|queueSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadCount
operator|=
name|threadCount
expr_stmt|;
name|runners
operator|=
operator|new
name|LinkedList
argument_list|<
name|Runner
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Opens a connection and sends everything...    */
DECL|class|Runner
class|class
name|Runner
implements|implements
name|Runnable
block|{
DECL|field|runnerLock
specifier|final
name|Lock
name|runnerLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|runnerLock
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// info is ok since this should only happen once for each thread
name|log
operator|.
name|info
argument_list|(
literal|"starting runner: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|PostMethod
name|method
init|=
literal|null
decl_stmt|;
try|try
block|{
do|do
block|{
try|try
block|{
name|RequestEntity
name|request
init|=
operator|new
name|RequestEntity
argument_list|()
block|{
comment|// we don't know the length
specifier|public
name|long
name|getContentLength
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|ClientUtils
operator|.
name|TEXT_XML
return|;
block|}
specifier|public
name|boolean
name|isRepeatable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|writeRequest
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|OutputStreamWriter
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"<stream>"
argument_list|)
expr_stmt|;
comment|// can be anything...
name|UpdateRequest
name|req
init|=
name|queue
operator|.
name|poll
argument_list|(
literal|250
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|req
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"sending: {}"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|req
operator|.
name|writeXML
argument_list|(
name|writer
argument_list|)
expr_stmt|;
comment|// check for commit or optimize
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|String
name|fmt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|OPTIMIZE
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|fmt
operator|=
literal|"<optimize waitSearcher=\"%s\" waitFlush=\"%s\" />"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|fmt
operator|=
literal|"<commit waitSearcher=\"%s\" waitFlush=\"%s\" />"
expr_stmt|;
block|}
if|if
condition|(
name|fmt
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|fmt
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
literal|false
argument_list|)
operator|+
literal|""
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_FLUSH
argument_list|,
literal|false
argument_list|)
operator|+
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|req
operator|=
name|queue
operator|.
name|poll
argument_list|(
literal|250
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"</stream>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|method
operator|=
operator|new
name|PostMethod
argument_list|(
name|_baseURL
operator|+
name|updateUrl
argument_list|)
expr_stmt|;
name|method
operator|.
name|setRequestEntity
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|method
operator|.
name|setFollowRedirects
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|method
operator|.
name|addRequestHeader
argument_list|(
literal|"User-Agent"
argument_list|,
name|AGENT
argument_list|)
expr_stmt|;
name|int
name|statusCode
init|=
name|getHttpClient
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
name|handleError
argument_list|(
operator|new
name|Exception
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
comment|// make sure to release the connection
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
name|method
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
block|}
do|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
do|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|handleError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// remove it from the list of running things unless we are the last runner and the queue is full...
comment|// in which case, the next queue.put() would block and there would be no runners to handle it.
synchronized|synchronized
init|(
name|runners
init|)
block|{
if|if
condition|(
name|runners
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|queue
operator|.
name|remainingCapacity
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// keep this runner alive
name|scheduler
operator|.
name|execute
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|runners
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"finished: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|runnerLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
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
if|if
condition|(
operator|!
operator|(
name|request
operator|instanceof
name|UpdateRequest
operator|)
condition|)
block|{
return|return
name|super
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
name|UpdateRequest
name|req
init|=
operator|(
name|UpdateRequest
operator|)
name|request
decl_stmt|;
comment|// this happens for commit...
if|if
condition|(
name|req
operator|.
name|getDocuments
argument_list|()
operator|==
literal|null
operator|||
name|req
operator|.
name|getDocuments
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|blockUntilFinished
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
comment|// check if it is waiting for the searcher
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|UpdateParams
operator|.
name|WAIT_SEARCHER
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"blocking for commit/optimize"
argument_list|)
expr_stmt|;
name|blockUntilFinished
argument_list|()
expr_stmt|;
comment|// empty the queue
return|return
name|super
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
try|try
block|{
name|CountDownLatch
name|tmpLock
init|=
name|lock
decl_stmt|;
if|if
condition|(
name|tmpLock
operator|!=
literal|null
condition|)
block|{
name|tmpLock
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
name|queue
operator|.
name|put
argument_list|(
name|req
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|runners
init|)
block|{
if|if
condition|(
name|runners
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|queue
operator|.
name|remainingCapacity
argument_list|()
operator|<
name|queue
operator|.
name|size
argument_list|()
operator|&&
name|runners
operator|.
name|size
argument_list|()
operator|<
name|threadCount
operator|)
condition|)
block|{
name|Runner
name|r
init|=
operator|new
name|Runner
argument_list|()
decl_stmt|;
name|scheduler
operator|.
name|execute
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|runners
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|// RETURN A DUMMY result
name|NamedList
argument_list|<
name|Object
argument_list|>
name|dummy
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|dummy
operator|.
name|add
argument_list|(
literal|"NOTE"
argument_list|,
literal|"the request is processed in a background stream"
argument_list|)
expr_stmt|;
return|return
name|dummy
return|;
block|}
DECL|method|blockUntilFinished
specifier|public
specifier|synchronized
name|void
name|blockUntilFinished
parameter_list|()
block|{
name|lock
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Wait until no runners are running
for|for
control|(
init|;
condition|;
control|)
block|{
name|Runner
name|runner
decl_stmt|;
synchronized|synchronized
init|(
name|runners
init|)
block|{
name|runner
operator|=
name|runners
operator|.
name|peek
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|runner
operator|==
literal|null
condition|)
break|break;
name|runner
operator|.
name|runnerLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|runner
operator|.
name|runnerLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|lock
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|handleError
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
