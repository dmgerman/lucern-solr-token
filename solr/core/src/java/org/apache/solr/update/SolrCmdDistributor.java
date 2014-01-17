begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
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
name|impl
operator|.
name|HttpSolrServer
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
name|AbstractUpdateRequest
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
name|cloud
operator|.
name|ZkCoreNodeProps
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
name|core
operator|.
name|Diagnostics
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
begin_class
DECL|class|SolrCmdDistributor
specifier|public
class|class
name|SolrCmdDistributor
block|{
DECL|field|MAX_RETRIES_ON_FORWARD
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RETRIES_ON_FORWARD
init|=
literal|25
decl_stmt|;
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrCmdDistributor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|servers
specifier|private
name|StreamingSolrServers
name|servers
decl_stmt|;
DECL|field|retryPause
specifier|private
name|int
name|retryPause
init|=
literal|500
decl_stmt|;
DECL|field|maxRetriesOnForward
specifier|private
name|int
name|maxRetriesOnForward
init|=
name|MAX_RETRIES_ON_FORWARD
decl_stmt|;
DECL|field|allErrors
specifier|private
name|List
argument_list|<
name|Error
argument_list|>
name|allErrors
init|=
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|errors
specifier|private
name|List
argument_list|<
name|Error
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|()
decl_stmt|;
DECL|interface|AbortCheck
specifier|public
specifier|static
interface|interface
name|AbortCheck
block|{
DECL|method|abortCheck
specifier|public
name|boolean
name|abortCheck
parameter_list|()
function_decl|;
block|}
DECL|method|SolrCmdDistributor
specifier|public
name|SolrCmdDistributor
parameter_list|(
name|UpdateShardHandler
name|updateShardHandler
parameter_list|)
block|{
name|servers
operator|=
operator|new
name|StreamingSolrServers
argument_list|(
name|updateShardHandler
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrCmdDistributor
specifier|public
name|SolrCmdDistributor
parameter_list|(
name|StreamingSolrServers
name|servers
parameter_list|,
name|int
name|maxRetriesOnForward
parameter_list|,
name|int
name|retryPause
parameter_list|)
block|{
name|this
operator|.
name|servers
operator|=
name|servers
expr_stmt|;
name|this
operator|.
name|maxRetriesOnForward
operator|=
name|maxRetriesOnForward
expr_stmt|;
name|this
operator|.
name|retryPause
operator|=
name|retryPause
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
block|{
try|try
block|{
name|servers
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|doRetriesIfNeeded
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|servers
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doRetriesIfNeeded
specifier|private
name|void
name|doRetriesIfNeeded
parameter_list|()
block|{
comment|// NOTE: retries will be forwards to a single url
name|List
argument_list|<
name|Error
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|(
name|this
operator|.
name|errors
argument_list|)
decl_stmt|;
name|errors
operator|.
name|addAll
argument_list|(
name|servers
operator|.
name|getErrors
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Error
argument_list|>
name|resubmitList
init|=
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Error
name|err
range|:
name|errors
control|)
block|{
try|try
block|{
name|String
name|oldNodeUrl
init|=
name|err
operator|.
name|req
operator|.
name|node
operator|.
name|getUrl
argument_list|()
decl_stmt|;
comment|// if there is a retry url, we want to retry...
name|boolean
name|isRetry
init|=
name|err
operator|.
name|req
operator|.
name|node
operator|.
name|checkRetry
argument_list|()
decl_stmt|;
name|boolean
name|doRetry
init|=
literal|false
decl_stmt|;
name|int
name|rspCode
init|=
name|err
operator|.
name|statusCode
decl_stmt|;
if|if
condition|(
name|testing_errorHook
operator|!=
literal|null
condition|)
name|Diagnostics
operator|.
name|call
argument_list|(
name|testing_errorHook
argument_list|,
name|err
operator|.
name|e
argument_list|)
expr_stmt|;
comment|// this can happen in certain situations such as shutdown
if|if
condition|(
name|isRetry
condition|)
block|{
if|if
condition|(
name|rspCode
operator|==
literal|404
operator|||
name|rspCode
operator|==
literal|403
operator|||
name|rspCode
operator|==
literal|503
condition|)
block|{
name|doRetry
operator|=
literal|true
expr_stmt|;
block|}
comment|// if its a connect exception, lets try again
if|if
condition|(
name|err
operator|.
name|e
operator|instanceof
name|SolrServerException
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|SolrServerException
operator|)
name|err
operator|.
name|e
operator|)
operator|.
name|getRootCause
argument_list|()
operator|instanceof
name|ConnectException
condition|)
block|{
name|doRetry
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|err
operator|.
name|e
operator|instanceof
name|ConnectException
condition|)
block|{
name|doRetry
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|err
operator|.
name|req
operator|.
name|retries
operator|<
name|maxRetriesOnForward
operator|&&
name|doRetry
condition|)
block|{
name|err
operator|.
name|req
operator|.
name|retries
operator|++
expr_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCmdDistributor
operator|.
name|log
argument_list|,
literal|"forwarding update to "
operator|+
name|oldNodeUrl
operator|+
literal|" failed - retrying ... retries: "
operator|+
name|err
operator|.
name|req
operator|.
name|retries
operator|+
literal|" "
operator|+
name|err
operator|.
name|req
operator|.
name|cmdString
operator|+
literal|" params:"
operator|+
name|err
operator|.
name|req
operator|.
name|uReq
operator|.
name|getParams
argument_list|()
operator|+
literal|" rsp:"
operator|+
name|rspCode
argument_list|,
name|err
operator|.
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|retryPause
argument_list|)
expr_stmt|;
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
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|resubmitList
operator|.
name|add
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allErrors
operator|.
name|add
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|allErrors
operator|.
name|add
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// continue on
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected Error while doing request retries"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|servers
operator|.
name|clearErrors
argument_list|()
expr_stmt|;
name|this
operator|.
name|errors
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Error
name|err
range|:
name|resubmitList
control|)
block|{
name|submit
argument_list|(
name|err
operator|.
name|req
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resubmitList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|servers
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|doRetriesIfNeeded
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|distribDelete
specifier|public
name|void
name|distribDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|distribDelete
argument_list|(
name|cmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|distribDelete
specifier|public
name|void
name|distribDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|UpdateRequest
name|uReq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|uReq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|isDeleteById
argument_list|()
condition|)
block|{
name|uReq
operator|.
name|deleteById
argument_list|(
name|cmd
operator|.
name|getId
argument_list|()
argument_list|,
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|uReq
operator|.
name|deleteByQuery
argument_list|(
name|cmd
operator|.
name|query
argument_list|)
expr_stmt|;
block|}
name|submit
argument_list|(
operator|new
name|Req
argument_list|(
name|cmd
operator|.
name|toString
argument_list|()
argument_list|,
name|node
argument_list|,
name|uReq
argument_list|,
name|sync
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|distribAdd
specifier|public
name|void
name|distribAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|distribAdd
argument_list|(
name|cmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|distribAdd
specifier|public
name|void
name|distribAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|boolean
name|synchronous
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|UpdateRequest
name|uReq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|uReq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|solrDoc
argument_list|,
name|cmd
operator|.
name|commitWithin
argument_list|,
name|cmd
operator|.
name|overwrite
argument_list|)
expr_stmt|;
name|submit
argument_list|(
operator|new
name|Req
argument_list|(
name|cmd
operator|.
name|toString
argument_list|()
argument_list|,
name|node
argument_list|,
name|uReq
argument_list|,
name|synchronous
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|distribCommit
specifier|public
name|void
name|distribCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we need to do any retries before commit...
name|servers
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|doRetriesIfNeeded
argument_list|()
expr_stmt|;
name|UpdateRequest
name|uReq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|uReq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|addCommit
argument_list|(
name|uReq
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Distrib commit to: {} params: {}"
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|submit
argument_list|(
operator|new
name|Req
argument_list|(
name|cmd
operator|.
name|toString
argument_list|()
argument_list|,
name|node
argument_list|,
name|uReq
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addCommit
name|void
name|addCommit
parameter_list|(
name|UpdateRequest
name|ureq
parameter_list|,
name|CommitUpdateCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|==
literal|null
condition|)
return|return;
name|ureq
operator|.
name|setAction
argument_list|(
name|cmd
operator|.
name|optimize
condition|?
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|OPTIMIZE
else|:
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
name|cmd
operator|.
name|waitSearcher
argument_list|,
name|cmd
operator|.
name|maxOptimizeSegments
argument_list|,
name|cmd
operator|.
name|softCommit
argument_list|,
name|cmd
operator|.
name|expungeDeletes
argument_list|)
expr_stmt|;
block|}
DECL|method|submit
specifier|private
name|void
name|submit
parameter_list|(
name|Req
name|req
parameter_list|)
block|{
if|if
condition|(
name|req
operator|.
name|synchronous
condition|)
block|{
name|servers
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|doRetriesIfNeeded
argument_list|()
expr_stmt|;
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|req
operator|.
name|node
operator|.
name|getUrl
argument_list|()
argument_list|,
name|servers
operator|.
name|getHttpClient
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|request
argument_list|(
name|req
operator|.
name|uReq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Failed synchronous update on shard "
operator|+
name|req
operator|.
name|node
operator|+
literal|" update: "
operator|+
name|req
operator|.
name|uReq
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"sending update to "
operator|+
name|req
operator|.
name|node
operator|.
name|getUrl
argument_list|()
operator|+
literal|" retry:"
operator|+
name|req
operator|.
name|retries
operator|+
literal|" "
operator|+
name|req
operator|.
name|cmdString
operator|+
literal|" params:"
operator|+
name|req
operator|.
name|uReq
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|SolrServer
name|solrServer
init|=
name|servers
operator|.
name|getSolrServer
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|solrServer
operator|.
name|request
argument_list|(
name|req
operator|.
name|uReq
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|Error
name|error
init|=
operator|new
name|Error
argument_list|()
decl_stmt|;
name|error
operator|.
name|e
operator|=
name|e
expr_stmt|;
name|error
operator|.
name|req
operator|=
name|req
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
name|error
operator|.
name|statusCode
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
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Req
specifier|public
specifier|static
class|class
name|Req
block|{
DECL|field|node
specifier|public
name|Node
name|node
decl_stmt|;
DECL|field|uReq
specifier|public
name|UpdateRequest
name|uReq
decl_stmt|;
DECL|field|retries
specifier|public
name|int
name|retries
decl_stmt|;
DECL|field|synchronous
specifier|public
name|boolean
name|synchronous
decl_stmt|;
DECL|field|cmdString
specifier|public
name|String
name|cmdString
decl_stmt|;
DECL|method|Req
specifier|public
name|Req
parameter_list|(
name|String
name|cmdString
parameter_list|,
name|Node
name|node
parameter_list|,
name|UpdateRequest
name|uReq
parameter_list|,
name|boolean
name|synchronous
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|uReq
operator|=
name|uReq
expr_stmt|;
name|this
operator|.
name|synchronous
operator|=
name|synchronous
expr_stmt|;
name|this
operator|.
name|cmdString
operator|=
name|cmdString
expr_stmt|;
block|}
block|}
DECL|field|testing_errorHook
specifier|public
specifier|static
name|Diagnostics
operator|.
name|Callable
name|testing_errorHook
decl_stmt|;
comment|// called on error when forwarding request.  Currently data=[this, Request]
DECL|class|Response
specifier|public
specifier|static
class|class
name|Response
block|{
DECL|field|errors
specifier|public
name|List
argument_list|<
name|Error
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|()
decl_stmt|;
block|}
DECL|class|Error
specifier|public
specifier|static
class|class
name|Error
block|{
DECL|field|e
specifier|public
name|Exception
name|e
decl_stmt|;
DECL|field|statusCode
specifier|public
name|int
name|statusCode
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|req
specifier|public
name|Req
name|req
decl_stmt|;
block|}
DECL|class|Node
specifier|public
specifier|static
specifier|abstract
class|class
name|Node
block|{
DECL|method|getUrl
specifier|public
specifier|abstract
name|String
name|getUrl
parameter_list|()
function_decl|;
DECL|method|checkRetry
specifier|public
specifier|abstract
name|boolean
name|checkRetry
parameter_list|()
function_decl|;
DECL|method|getCoreName
specifier|public
specifier|abstract
name|String
name|getCoreName
parameter_list|()
function_decl|;
DECL|method|getBaseUrl
specifier|public
specifier|abstract
name|String
name|getBaseUrl
parameter_list|()
function_decl|;
DECL|method|getNodeProps
specifier|public
specifier|abstract
name|ZkCoreNodeProps
name|getNodeProps
parameter_list|()
function_decl|;
block|}
DECL|class|StdNode
specifier|public
specifier|static
class|class
name|StdNode
extends|extends
name|Node
block|{
DECL|field|nodeProps
specifier|protected
name|ZkCoreNodeProps
name|nodeProps
decl_stmt|;
DECL|method|StdNode
specifier|public
name|StdNode
parameter_list|(
name|ZkCoreNodeProps
name|nodeProps
parameter_list|)
block|{
name|this
operator|.
name|nodeProps
operator|=
name|nodeProps
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrl
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkRetry
specifier|public
name|boolean
name|checkRetry
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getBaseUrl
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
name|nodeProps
operator|.
name|getCoreName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|String
name|baseUrl
init|=
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|String
name|coreName
init|=
name|nodeProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|baseUrl
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|baseUrl
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|coreName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|coreName
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|url
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|url
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|StdNode
name|other
init|=
operator|(
name|StdNode
operator|)
name|obj
decl_stmt|;
name|String
name|baseUrl
init|=
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|String
name|coreName
init|=
name|nodeProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseUrl
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|baseUrl
operator|.
name|equals
argument_list|(
name|other
operator|.
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|coreName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|nodeProps
operator|.
name|getCoreName
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|coreName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|nodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|url
operator|.
name|equals
argument_list|(
name|other
operator|.
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeProps
specifier|public
name|ZkCoreNodeProps
name|getNodeProps
parameter_list|()
block|{
return|return
name|nodeProps
return|;
block|}
block|}
comment|// RetryNodes are used in the case of 'forward to leader' where we want
comment|// to try the latest leader on a fail in the case the leader just went down.
DECL|class|RetryNode
specifier|public
specifier|static
class|class
name|RetryNode
extends|extends
name|StdNode
block|{
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|collection
specifier|private
name|String
name|collection
decl_stmt|;
DECL|field|shardId
specifier|private
name|String
name|shardId
decl_stmt|;
DECL|method|RetryNode
specifier|public
name|RetryNode
parameter_list|(
name|ZkCoreNodeProps
name|nodeProps
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|nodeProps
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkStateReader
operator|=
name|zkStateReader
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkRetry
specifier|public
name|boolean
name|checkRetry
parameter_list|()
block|{
name|ZkCoreNodeProps
name|leaderProps
decl_stmt|;
try|try
block|{
name|leaderProps
operator|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// we retry with same info
name|log
operator|.
name|warn
argument_list|(
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|this
operator|.
name|nodeProps
operator|=
name|leaderProps
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|collection
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|collection
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|shardId
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|shardId
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RetryNode
name|other
init|=
operator|(
name|RetryNode
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|getErrors
specifier|public
name|List
argument_list|<
name|Error
argument_list|>
name|getErrors
parameter_list|()
block|{
return|return
name|allErrors
return|;
block|}
block|}
end_class
end_unit
