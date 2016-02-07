begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
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
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
begin_comment
comment|/** Runs CopyJob(s) in background thread; each ReplicaNode has an instance of this  *  running.  At a given there could be one NRT copy job running, and multiple  *  pre-warm merged segments jobs. */
end_comment
begin_class
DECL|class|Jobs
class|class
name|Jobs
extends|extends
name|Thread
implements|implements
name|Closeable
block|{
DECL|field|queue
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|CopyJob
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|node
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
DECL|method|Jobs
specifier|public
name|Jobs
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
DECL|field|finish
specifier|private
name|boolean
name|finish
decl_stmt|;
comment|/** Returns null if we are closing, else, returns the top job or waits for one to arrive if the queue is empty. */
DECL|method|getNextJob
specifier|private
specifier|synchronized
name|SimpleCopyJob
name|getNextJob
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|finish
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
operator|(
name|SimpleCopyJob
operator|)
name|queue
operator|.
name|poll
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|SimpleCopyJob
name|topJob
init|=
name|getNextJob
argument_list|()
decl_stmt|;
if|if
condition|(
name|topJob
operator|==
literal|null
condition|)
block|{
assert|assert
name|finish
assert|;
break|break;
block|}
name|this
operator|.
name|setName
argument_list|(
literal|"jobs o"
operator|+
name|topJob
operator|.
name|ord
argument_list|)
expr_stmt|;
assert|assert
name|topJob
operator|!=
literal|null
assert|;
name|boolean
name|result
decl_stmt|;
try|try
block|{
name|result
operator|=
name|topJob
operator|.
name|visit
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
operator|(
name|t
operator|instanceof
name|AlreadyClosedException
operator|)
operator|==
literal|false
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"exception during job.visit job="
operator|+
name|topJob
operator|+
literal|"; now cancel"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|message
argument_list|(
literal|"AlreadyClosedException during job.visit job="
operator|+
name|topJob
operator|+
literal|"; now cancel"
argument_list|)
expr_stmt|;
block|}
name|topJob
operator|.
name|cancel
argument_list|(
literal|"unexpected exception in visit"
argument_list|,
name|t
argument_list|)
expr_stmt|;
try|try
block|{
name|topJob
operator|.
name|onceDone
operator|.
name|run
argument_list|(
name|topJob
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t2
parameter_list|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"ignore exception calling OnceDone: "
operator|+
name|t2
argument_list|)
expr_stmt|;
name|t2
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|result
operator|==
literal|false
condition|)
block|{
comment|// Job isn't done yet; put it back:
synchronized|synchronized
init|(
name|this
init|)
block|{
name|queue
operator|.
name|offer
argument_list|(
name|topJob
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Job finished, now notify caller:
try|try
block|{
name|topJob
operator|.
name|onceDone
operator|.
name|run
argument_list|(
name|topJob
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"ignore exception calling OnceDone: "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|node
operator|.
name|message
argument_list|(
literal|"top: jobs now exit run thread"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Gracefully cancel any jobs we didn't finish:
while|while
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|SimpleCopyJob
name|job
init|=
operator|(
name|SimpleCopyJob
operator|)
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
name|node
operator|.
name|message
argument_list|(
literal|"top: Jobs: now cancel job="
operator|+
name|job
argument_list|)
expr_stmt|;
name|job
operator|.
name|cancel
argument_list|(
literal|"jobs closing"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|job
operator|.
name|onceDone
operator|.
name|run
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"ignore exception calling OnceDone: "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|launch
specifier|public
specifier|synchronized
name|void
name|launch
parameter_list|(
name|CopyJob
name|job
parameter_list|)
block|{
if|if
condition|(
name|finish
operator|==
literal|false
condition|)
block|{
name|queue
operator|.
name|offer
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"closed"
argument_list|)
throw|;
block|}
block|}
comment|/** Cancels any existing jobs that are copying the same file names as this one */
DECL|method|cancelConflictingJobs
specifier|public
specifier|synchronized
name|void
name|cancelConflictingJobs
parameter_list|(
name|CopyJob
name|newJob
parameter_list|)
block|{
for|for
control|(
name|CopyJob
name|job
range|:
name|queue
control|)
block|{
if|if
condition|(
name|job
operator|.
name|conflicts
argument_list|(
name|newJob
argument_list|)
condition|)
block|{
name|node
operator|.
name|message
argument_list|(
literal|"top: now cancel existing conflicting job="
operator|+
name|job
operator|+
literal|" due to newJob="
operator|+
name|newJob
argument_list|)
expr_stmt|;
name|job
operator|.
name|cancel
argument_list|(
literal|"conflicts with new job"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|finish
operator|=
literal|true
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
try|try
block|{
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
