begin_unit
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|RejectedExecutionHandler
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
name|SynchronousQueue
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
name|ThreadFactory
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
name|slf4j
operator|.
name|MDC
import|;
end_import
begin_class
DECL|class|ExecutorUtil
specifier|public
class|class
name|ExecutorUtil
block|{
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
name|ExecutorUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// this will interrupt the threads! Lucene and Solr do not like this because it can close channels, so only use
comment|// this if you know what you are doing - you probably want shutdownAndAwaitTermination
DECL|method|shutdownNowAndAwaitTermination
specifier|public
specifier|static
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
comment|// Cancel currently executing tasks  - NOTE: this interrupts!
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
literal|1
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
comment|// Cancel currently executing tasks - NOTE: this interrupts!
block|}
block|}
block|}
DECL|method|shutdownAndAwaitTermination
specifier|public
specifier|static
name|void
name|shutdownAndAwaitTermination
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
literal|1
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
block|}
block|}
comment|/**    * See {@link java.util.concurrent.Executors#newFixedThreadPool(int, ThreadFactory)}    */
DECL|method|newMDCAwareFixedThreadPool
specifier|public
specifier|static
name|ExecutorService
name|newMDCAwareFixedThreadPool
parameter_list|(
name|int
name|nThreads
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|MDCAwareThreadPoolExecutor
argument_list|(
name|nThreads
argument_list|,
name|nThreads
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
comment|/**    * See {@link java.util.concurrent.Executors#newSingleThreadExecutor(ThreadFactory)}    */
DECL|method|newMDCAwareSingleThreadExecutor
specifier|public
specifier|static
name|ExecutorService
name|newMDCAwareSingleThreadExecutor
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|MDCAwareThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
comment|/**    * See {@link java.util.concurrent.Executors#newCachedThreadPool(ThreadFactory)}    */
DECL|method|newMDCAwareCachedThreadPool
specifier|public
specifier|static
name|ExecutorService
name|newMDCAwareCachedThreadPool
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|MDCAwareThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|60L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"class customizes ThreadPoolExecutor so it can be used instead"
argument_list|)
DECL|class|MDCAwareThreadPoolExecutor
specifier|public
specifier|static
class|class
name|MDCAwareThreadPoolExecutor
extends|extends
name|ThreadPoolExecutor
block|{
DECL|field|MAX_THREAD_NAME_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_THREAD_NAME_LEN
init|=
literal|512
decl_stmt|;
DECL|method|MDCAwareThreadPoolExecutor
specifier|public
name|MDCAwareThreadPoolExecutor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|int
name|maximumPoolSize
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|RejectedExecutionHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|corePoolSize
argument_list|,
name|maximumPoolSize
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|,
name|threadFactory
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
DECL|method|MDCAwareThreadPoolExecutor
specifier|public
name|MDCAwareThreadPoolExecutor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|int
name|maximumPoolSize
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
parameter_list|)
block|{
name|super
argument_list|(
name|corePoolSize
argument_list|,
name|maximumPoolSize
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|)
expr_stmt|;
block|}
DECL|method|MDCAwareThreadPoolExecutor
specifier|public
name|MDCAwareThreadPoolExecutor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|int
name|maximumPoolSize
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
name|super
argument_list|(
name|corePoolSize
argument_list|,
name|maximumPoolSize
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|,
name|threadFactory
argument_list|)
expr_stmt|;
block|}
DECL|method|MDCAwareThreadPoolExecutor
specifier|public
name|MDCAwareThreadPoolExecutor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|int
name|maximumPoolSize
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
parameter_list|,
name|RejectedExecutionHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|corePoolSize
argument_list|,
name|maximumPoolSize
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|Runnable
name|command
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|submitterContext
init|=
name|MDC
operator|.
name|getCopyOfContextMap
argument_list|()
decl_stmt|;
name|StringBuilder
name|contextString
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|submitterContext
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|values
init|=
name|submitterContext
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|contextString
operator|.
name|append
argument_list|(
name|value
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextString
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
name|contextString
operator|.
name|setLength
argument_list|(
name|contextString
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|ctxStr
init|=
name|contextString
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|"/"
argument_list|,
literal|"//"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|submitterContextStr
init|=
name|ctxStr
operator|.
name|length
argument_list|()
operator|<=
name|MAX_THREAD_NAME_LEN
condition|?
name|ctxStr
else|:
name|ctxStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|MAX_THREAD_NAME_LEN
argument_list|)
decl_stmt|;
specifier|final
name|Exception
name|submitterStackTrace
init|=
operator|new
name|Exception
argument_list|(
literal|"Submitter stack trace"
argument_list|)
decl_stmt|;
name|super
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|threadContext
init|=
name|MDC
operator|.
name|getCopyOfContextMap
argument_list|()
decl_stmt|;
specifier|final
name|Thread
name|currentThread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
specifier|final
name|String
name|oldName
init|=
name|currentThread
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|submitterContext
operator|!=
literal|null
operator|&&
operator|!
name|submitterContext
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MDC
operator|.
name|setContextMap
argument_list|(
name|submitterContext
argument_list|)
expr_stmt|;
name|currentThread
operator|.
name|setName
argument_list|(
name|oldName
operator|+
literal|"-processing-"
operator|+
name|submitterContextStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MDC
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
throw|throw
name|t
throw|;
block|}
name|log
operator|.
name|error
argument_list|(
literal|"Uncaught exception {} thrown by thread: {}"
argument_list|,
name|t
argument_list|,
name|currentThread
operator|.
name|getName
argument_list|()
argument_list|,
name|submitterStackTrace
argument_list|)
expr_stmt|;
throw|throw
name|t
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|threadContext
operator|!=
literal|null
operator|&&
operator|!
name|threadContext
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MDC
operator|.
name|setContextMap
argument_list|(
name|threadContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MDC
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|currentThread
operator|.
name|setName
argument_list|(
name|oldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
