begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Locale
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
name|Set
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
name|BlockingQueue
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
name|CountDownLatch
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
name|ExecutorService
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
name|Executors
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
name|LinkedBlockingQueue
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
name|TimeUnit
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
name|client
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
name|entity
operator|.
name|ContentProducer
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
name|entity
operator|.
name|EntityTemplate
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
name|SolrServer
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
name|RequestWriter
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
name|SolrException
operator|.
name|ErrorCode
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SolrjNamedThreadFactory
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
comment|/**  * ConcurrentUpdateSolrServer buffers all added documents and writes  * them into open HTTP connections. This class is thread safe.  *   * Params from {@link UpdateRequest} are converted to http request  * parameters. When params change between UpdateRequests a new HTTP  * request is started.  *   * Although any SolrServer request can be made with this implementation, it is  * only recommended to use ConcurrentUpdateSolrServer with /update  * requests. The class {@link HttpSolrServer} is better suited for the  * query interface.  */
end_comment
begin_class
DECL|class|ConcurrentUpdateSolrServer
specifier|public
class|class
name|ConcurrentUpdateSolrServer
extends|extends
name|SolrServer
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
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
name|ConcurrentUpdateSolrServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|server
specifier|private
name|HttpSolrServer
name|server
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
DECL|field|shutdownExecutor
name|boolean
name|shutdownExecutor
init|=
literal|false
decl_stmt|;
DECL|field|pollQueueTime
name|int
name|pollQueueTime
init|=
literal|250
decl_stmt|;
DECL|field|streamDeletes
specifier|private
specifier|final
name|boolean
name|streamDeletes
decl_stmt|;
comment|/**    * Uses an internally managed HttpClient instance.    *     * @param solrServerUrl    *          The Solr server URL    * @param queueSize    *          The buffer size before the documents are sent to the server    * @param threadCount    *          The number of background threads used to empty the queue    */
DECL|method|ConcurrentUpdateSolrServer
specifier|public
name|ConcurrentUpdateSolrServer
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
name|shutdownExecutor
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|ConcurrentUpdateSolrServer
specifier|public
name|ConcurrentUpdateSolrServer
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
block|{
name|this
argument_list|(
name|solrServerUrl
argument_list|,
name|client
argument_list|,
name|queueSize
argument_list|,
name|threadCount
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"concurrentUpdateScheduler"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|shutdownExecutor
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Uses the supplied HttpClient to send documents to the Solr server.    */
DECL|method|ConcurrentUpdateSolrServer
specifier|public
name|ConcurrentUpdateSolrServer
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
parameter_list|,
name|ExecutorService
name|es
parameter_list|)
block|{
name|this
argument_list|(
name|solrServerUrl
argument_list|,
name|client
argument_list|,
name|queueSize
argument_list|,
name|threadCount
argument_list|,
name|es
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Uses the supplied HttpClient to send documents to the Solr server.    */
DECL|method|ConcurrentUpdateSolrServer
specifier|public
name|ConcurrentUpdateSolrServer
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
parameter_list|,
name|ExecutorService
name|es
parameter_list|,
name|boolean
name|streamDeletes
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|solrServerUrl
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|setFollowRedirects
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|queue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
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
argument_list|<>
argument_list|()
expr_stmt|;
name|scheduler
operator|=
name|es
expr_stmt|;
name|this
operator|.
name|streamDeletes
operator|=
name|streamDeletes
expr_stmt|;
block|}
DECL|method|getQueryParams
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getQueryParams
parameter_list|()
block|{
return|return
name|this
operator|.
name|server
operator|.
name|getQueryParams
argument_list|()
return|;
block|}
comment|/**    * Expert Method.    * @param queryParams set of param keys to only send via the query string    */
DECL|method|setQueryParams
specifier|public
name|void
name|setQueryParams
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
parameter_list|)
block|{
name|this
operator|.
name|server
operator|.
name|setQueryParams
argument_list|(
name|queryParams
argument_list|)
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
annotation|@
name|Override
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
name|log
operator|.
name|debug
argument_list|(
literal|"starting runner: {}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|HttpPost
name|method
init|=
literal|null
decl_stmt|;
name|HttpResponse
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|UpdateRequest
name|updateRequest
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
if|if
condition|(
name|updateRequest
operator|==
literal|null
condition|)
break|break;
name|String
name|contentType
init|=
name|server
operator|.
name|requestWriter
operator|.
name|getUpdateContentType
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isXml
init|=
name|ClientUtils
operator|.
name|TEXT_XML
operator|.
name|equals
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
specifier|final
name|ModifiableSolrParams
name|origParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|updateRequest
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|EntityTemplate
name|template
init|=
operator|new
name|EntityTemplate
argument_list|(
operator|new
name|ContentProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|isXml
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"<stream>"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
comment|// can be anything
block|}
name|UpdateRequest
name|req
init|=
name|updateRequest
decl_stmt|;
while|while
condition|(
name|req
operator|!=
literal|null
condition|)
block|{
name|SolrParams
name|currentParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|origParams
operator|.
name|toNamedList
argument_list|()
operator|.
name|equals
argument_list|(
name|currentParams
operator|.
name|toNamedList
argument_list|()
argument_list|)
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// params are different, push back to queue
break|break;
block|}
name|server
operator|.
name|requestWriter
operator|.
name|write
argument_list|(
name|req
argument_list|,
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|isXml
condition|)
block|{
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
literal|"<optimize waitSearcher=\"%s\" />"
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
literal|"<commit waitSearcher=\"%s\" />"
expr_stmt|;
block|}
if|if
condition|(
name|fmt
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|content
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
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
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|out
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
name|pollQueueTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isXml
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"</stream>"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|// The parser 'wt=' and 'version=' params are used instead of the
comment|// original params
name|ModifiableSolrParams
name|requestParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|origParams
argument_list|)
decl_stmt|;
name|requestParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
name|server
operator|.
name|parser
operator|.
name|getWriterType
argument_list|()
argument_list|)
expr_stmt|;
name|requestParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
name|server
operator|.
name|parser
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|method
operator|=
operator|new
name|HttpPost
argument_list|(
name|server
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/update"
operator|+
name|ClientUtils
operator|.
name|toQueryString
argument_list|(
name|requestParams
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|method
operator|.
name|setEntity
argument_list|(
name|template
argument_list|)
expr_stmt|;
name|method
operator|.
name|addHeader
argument_list|(
literal|"User-Agent"
argument_list|,
name|HttpSolrServer
operator|.
name|AGENT
argument_list|)
expr_stmt|;
name|method
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
name|contentType
argument_list|)
expr_stmt|;
name|response
operator|=
name|server
operator|.
name|getHttpClient
argument_list|()
operator|.
name|execute
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|int
name|statusCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
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
name|response
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
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"request: "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|handleError
argument_list|(
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|statusCode
argument_list|)
argument_list|,
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
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
throw|throw
operator|(
name|OutOfMemoryError
operator|)
name|e
throw|;
block|}
name|handleError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
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
name|debug
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
name|server
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
name|streamDeletes
condition|)
block|{
if|if
condition|(
operator|(
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
operator|)
operator|&&
operator|(
name|req
operator|.
name|getDeleteById
argument_list|()
operator|==
literal|null
operator|||
name|req
operator|.
name|getDeleteById
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|req
operator|.
name|getDeleteByIdMap
argument_list|()
operator|==
literal|null
operator|||
name|req
operator|.
name|getDeleteByIdMap
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|req
operator|.
name|getDeleteQuery
argument_list|()
operator|==
literal|null
condition|)
block|{
name|blockUntilFinished
argument_list|()
expr_stmt|;
return|return
name|server
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
operator|(
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
operator|)
condition|)
block|{
name|blockUntilFinished
argument_list|()
expr_stmt|;
return|return
name|server
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
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
name|server
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
name|boolean
name|success
init|=
name|queue
operator|.
name|offer
argument_list|(
name|req
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
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
comment|// queue
comment|// is
comment|// half
comment|// full
comment|// and
comment|// we
comment|// can
comment|// add
comment|// more
comment|// runners
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
comment|// We need more runners, so start a new one.
name|Runner
name|r
init|=
operator|new
name|Runner
argument_list|()
decl_stmt|;
name|runners
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|execute
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// break out of the retry loop if we added the element to the queue
comment|// successfully, *and*
comment|// while we are still holding the runners lock to prevent race
comment|// conditions.
if|if
condition|(
name|success
condition|)
break|break;
block|}
block|}
comment|// Retry to add to the queue w/o the runners lock held (else we risk
comment|// temporary deadlock)
comment|// This retry could also fail because
comment|// 1) existing runners were not able to take off any new elements in the
comment|// queue
comment|// 2) the queue was filled back up since our last try
comment|// If we succeed, the queue may have been completely emptied, and all
comment|// runners stopped.
comment|// In all cases, we should loop back to the top to see if we need to
comment|// start more runners.
comment|//
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|success
operator|=
name|queue
operator|.
name|offer
argument_list|(
name|req
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
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
argument_list|<>
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
operator|(
name|runner
operator|==
literal|null
operator|&&
name|queue
operator|.
name|isEmpty
argument_list|()
operator|)
operator|||
name|scheduler
operator|.
name|isTerminated
argument_list|()
condition|)
break|break;
if|if
condition|(
name|runner
operator|!=
literal|null
condition|)
block|{
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
elseif|else
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// failsafe - should not be necessary, but a good
comment|// precaution to ensure blockUntilFinished guarantees
comment|// all updates are emptied from the queue regardless of
comment|// any bugs around starting or retaining runners
name|Runner
name|r
init|=
operator|new
name|Runner
argument_list|()
decl_stmt|;
name|runners
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|execute
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Override
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|shutdownExecutor
condition|)
block|{
name|scheduler
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|scheduler
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|scheduler
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
name|log
operator|.
name|error
argument_list|(
literal|"ExecutorService did not terminate"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|setConnectionTimeout
specifier|public
name|void
name|setConnectionTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|HttpClientUtil
operator|.
name|setConnectionTimeout
argument_list|(
name|server
operator|.
name|getHttpClient
argument_list|()
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**    * set soTimeout (read timeout) on the underlying HttpConnectionManager. This is desirable for queries, but probably    * not for indexing.    */
DECL|method|setSoTimeout
specifier|public
name|void
name|setSoTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|HttpClientUtil
operator|.
name|setSoTimeout
argument_list|(
name|server
operator|.
name|getHttpClient
argument_list|()
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdownNow
specifier|public
name|void
name|shutdownNow
parameter_list|()
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|shutdownExecutor
condition|)
block|{
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// Cancel currently executing tasks
try|try
block|{
if|if
condition|(
operator|!
name|scheduler
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
name|log
operator|.
name|error
argument_list|(
literal|"ExecutorService did not terminate"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|setParser
specifier|public
name|void
name|setParser
parameter_list|(
name|ResponseParser
name|responseParser
parameter_list|)
block|{
name|server
operator|.
name|setParser
argument_list|(
name|responseParser
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param pollQueueTime time for an open connection to wait for updates when    * the queue is empty.     */
DECL|method|setPollQueueTime
specifier|public
name|void
name|setPollQueueTime
parameter_list|(
name|int
name|pollQueueTime
parameter_list|)
block|{
name|this
operator|.
name|pollQueueTime
operator|=
name|pollQueueTime
expr_stmt|;
block|}
DECL|method|setRequestWriter
specifier|public
name|void
name|setRequestWriter
parameter_list|(
name|RequestWriter
name|requestWriter
parameter_list|)
block|{
name|server
operator|.
name|setRequestWriter
argument_list|(
name|requestWriter
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
