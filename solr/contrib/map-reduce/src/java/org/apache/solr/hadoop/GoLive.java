begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
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
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Callable
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
name|CompletionService
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
name|ExecutionException
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
name|ExecutorCompletionService
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
name|Future
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
name|ThreadPoolExecutor
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
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
name|CloudSolrServer
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
name|CoreAdminRequest
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
name|hadoop
operator|.
name|MapReduceIndexerTool
operator|.
name|Options
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
comment|/**  * The optional (parallel) GoLive phase merges the output shards of the previous  * phase into a set of live customer facing Solr servers, typically a SolrCloud.  */
end_comment
begin_class
DECL|class|GoLive
class|class
name|GoLive
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GoLive
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO: handle clusters with replicas
DECL|method|goLive
specifier|public
name|boolean
name|goLive
parameter_list|(
name|Options
name|options
parameter_list|,
name|FileStatus
index|[]
name|outDirs
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Live merging of output shards into Solr cluster..."
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|int
name|concurrentMerges
init|=
name|options
operator|.
name|goLiveThreads
decl_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|concurrentMerges
argument_list|,
name|concurrentMerges
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|CompletionService
argument_list|<
name|Request
argument_list|>
name|completionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<
name|Request
argument_list|>
argument_list|(
name|executor
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Future
argument_list|<
name|Request
argument_list|>
argument_list|>
name|pending
init|=
operator|new
name|HashSet
argument_list|<
name|Future
argument_list|<
name|Request
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|cnt
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|FileStatus
name|dir
range|:
name|outDirs
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing: "
operator|+
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|options
operator|.
name|shardUrls
operator|.
name|get
argument_list|(
name|cnt
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|url
range|:
name|urls
control|)
block|{
name|String
name|baseUrl
init|=
name|url
decl_stmt|;
if|if
condition|(
name|baseUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|baseUrl
operator|=
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseUrl
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|lastPathIndex
init|=
name|baseUrl
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastPathIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Found unexpected shardurl, live merge failed: "
operator|+
name|baseUrl
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|final
name|String
name|name
init|=
name|baseUrl
operator|.
name|substring
argument_list|(
name|lastPathIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|baseUrl
operator|=
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastPathIndex
argument_list|)
expr_stmt|;
specifier|final
name|String
name|mergeUrl
init|=
name|baseUrl
decl_stmt|;
name|Callable
argument_list|<
name|Request
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|Request
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Request
name|call
parameter_list|()
block|{
name|Request
name|req
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Live merge "
operator|+
name|dir
operator|.
name|getPath
argument_list|()
operator|+
literal|" into "
operator|+
name|mergeUrl
argument_list|)
expr_stmt|;
specifier|final
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|mergeUrl
argument_list|)
decl_stmt|;
try|try
block|{
name|CoreAdminRequest
operator|.
name|MergeIndexes
name|mergeRequest
init|=
operator|new
name|CoreAdminRequest
operator|.
name|MergeIndexes
argument_list|()
decl_stmt|;
name|mergeRequest
operator|.
name|setCoreName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|mergeRequest
operator|.
name|setIndexDirs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|dir
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/data/index"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|mergeRequest
operator|.
name|process
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|req
operator|.
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|req
operator|.
name|e
operator|=
name|e
expr_stmt|;
return|return
name|req
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|req
operator|.
name|e
operator|=
name|e
expr_stmt|;
return|return
name|req
return|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return
name|req
return|;
block|}
block|}
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|completionService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
name|pending
operator|!=
literal|null
operator|&&
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Future
argument_list|<
name|Request
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|==
literal|null
condition|)
break|break;
name|pending
operator|.
name|remove
argument_list|(
name|future
argument_list|)
expr_stmt|;
try|try
block|{
name|Request
name|req
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|req
operator|.
name|success
condition|)
block|{
comment|// failed
name|LOG
operator|.
name|error
argument_list|(
literal|"A live merge command failed"
argument_list|,
name|req
operator|.
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error sending live merge command"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Live merge process interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|cnt
operator|=
operator|-
literal|1
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Committing live merge..."
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|zkHost
operator|!=
literal|null
condition|)
block|{
name|CloudSolrServer
name|server
init|=
operator|new
name|CloudSolrServer
argument_list|(
name|options
operator|.
name|zkHost
argument_list|)
decl_stmt|;
name|server
operator|.
name|setDefaultCollection
argument_list|(
name|options
operator|.
name|collection
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|urls
range|:
name|options
operator|.
name|shardUrls
control|)
block|{
for|for
control|(
name|String
name|url
range|:
name|urls
control|)
block|{
comment|// TODO: we should do these concurrently
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done committing live merge"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error sending commits to live Solr cluster"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|shutdownNowAndAwaitTermination
argument_list|(
name|executor
argument_list|)
expr_stmt|;
name|float
name|secs
init|=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|)
operator|/
call|(
name|float
call|)
argument_list|(
literal|10
operator|^
literal|9
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Live merging of index shards into Solr cluster took "
operator|+
name|secs
operator|+
literal|" secs"
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Live merging completed successfully"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Live merging failed"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if an output dir does not exist, we should fail and do no merge?
block|}
DECL|method|shutdownNowAndAwaitTermination
specifier|private
name|void
name|shutdownNowAndAwaitTermination
parameter_list|(
name|ExecutorService
name|pool
parameter_list|)
block|{
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Disable new tasks from being submitted
name|pool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// Cancel currently executing tasks
name|boolean
name|shutdown
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|shutdown
condition|)
block|{
try|try
block|{
comment|// Wait a while for existing tasks to terminate
name|shutdown
operator|=
name|pool
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Preserve interrupt status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|shutdown
condition|)
block|{
name|pool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// Cancel currently executing tasks
block|}
block|}
block|}
DECL|class|Request
specifier|private
specifier|static
specifier|final
class|class
name|Request
block|{
DECL|field|e
name|Exception
name|e
decl_stmt|;
DECL|field|success
name|boolean
name|success
init|=
literal|false
decl_stmt|;
block|}
block|}
end_class
end_unit
