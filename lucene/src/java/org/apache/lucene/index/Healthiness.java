begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|AbstractQueuedSynchronizer
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
name|index
operator|.
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
import|;
end_import
begin_comment
comment|/**  * Controls the health status of a {@link DocumentsWriter} sessions. This class  * used to block incoming indexing threads if flushing significantly slower than  * indexing to ensure the {@link DocumentsWriter}s healthiness. If flushing is  * significantly slower than indexing the net memory used within an  * {@link IndexWriter} session can increase very quickly and easily exceed the  * JVM's available memory.  *<p>  * To prevent OOM Errors and ensure IndexWriter's stability this class blocks  * incoming threads from indexing once 2 x number of available  * {@link ThreadState}s in {@link DocumentsWriterPerThreadPool} is exceeded.  * Once flushing catches up and the number of flushing DWPT is equal or lower  * than the number of active {@link ThreadState}s threads are released and can  * continue indexing.  */
end_comment
begin_class
DECL|class|Healthiness
specifier|final
class|class
name|Healthiness
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|Sync
specifier|private
specifier|static
specifier|final
class|class
name|Sync
extends|extends
name|AbstractQueuedSynchronizer
block|{
DECL|field|hasBlockedThreads
specifier|volatile
name|boolean
name|hasBlockedThreads
init|=
literal|false
decl_stmt|;
comment|// only with assert
DECL|method|Sync
name|Sync
parameter_list|()
block|{
name|setState
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|isHealthy
name|boolean
name|isHealthy
parameter_list|()
block|{
return|return
name|getState
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|trySetStalled
name|boolean
name|trySetStalled
parameter_list|()
block|{
name|int
name|state
init|=
name|getState
argument_list|()
decl_stmt|;
return|return
name|compareAndSetState
argument_list|(
name|state
argument_list|,
name|state
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|tryReset
name|boolean
name|tryReset
parameter_list|()
block|{
specifier|final
name|int
name|oldState
init|=
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldState
operator|==
literal|0
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|compareAndSetState
argument_list|(
name|oldState
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|releaseShared
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|tryAcquireShared
specifier|public
name|int
name|tryAcquireShared
parameter_list|(
name|int
name|acquires
parameter_list|)
block|{
assert|assert
name|maybeSetHasBlocked
argument_list|(
name|getState
argument_list|()
argument_list|)
assert|;
return|return
name|getState
argument_list|()
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
comment|// only used for testing
DECL|method|maybeSetHasBlocked
specifier|private
name|boolean
name|maybeSetHasBlocked
parameter_list|(
name|int
name|state
parameter_list|)
block|{
name|hasBlockedThreads
operator||=
name|getState
argument_list|()
operator|!=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|tryReleaseShared
specifier|public
name|boolean
name|tryReleaseShared
parameter_list|(
name|int
name|newState
parameter_list|)
block|{
return|return
operator|(
name|getState
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
block|}
DECL|field|sync
specifier|private
specifier|final
name|Sync
name|sync
init|=
operator|new
name|Sync
argument_list|()
decl_stmt|;
DECL|field|wasStalled
specifier|volatile
name|boolean
name|wasStalled
init|=
literal|false
decl_stmt|;
comment|// only with asserts
DECL|method|anyStalledThreads
name|boolean
name|anyStalledThreads
parameter_list|()
block|{
return|return
operator|!
name|sync
operator|.
name|isHealthy
argument_list|()
return|;
block|}
comment|/**    * Update the stalled flag status. This method will set the stalled flag to    *<code>true</code> iff the number of flushing    * {@link DocumentsWriterPerThread} is greater than the number of active    * {@link DocumentsWriterPerThread}. Otherwise it will reset the    * {@link Healthiness} to healthy and release all threads waiting on    * {@link #waitIfStalled()}    */
DECL|method|updateStalled
name|void
name|updateStalled
parameter_list|(
name|DocumentsWriterFlushControl
name|flushControl
parameter_list|)
block|{
do|do
block|{
comment|// if we have more flushing DWPT than numActiveDWPT we stall!
while|while
condition|(
name|flushControl
operator|.
name|numActiveDWPT
argument_list|()
operator|<
name|flushControl
operator|.
name|numFlushingDWPT
argument_list|()
condition|)
block|{
if|if
condition|(
name|sync
operator|.
name|trySetStalled
argument_list|()
condition|)
block|{
assert|assert
name|wasStalled
operator|=
literal|true
assert|;
return|return;
block|}
block|}
block|}
do|while
condition|(
operator|!
name|sync
operator|.
name|tryReset
argument_list|()
condition|)
do|;
block|}
DECL|method|waitIfStalled
name|void
name|waitIfStalled
parameter_list|()
block|{
name|sync
operator|.
name|acquireShared
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|hasBlocked
name|boolean
name|hasBlocked
parameter_list|()
block|{
return|return
name|sync
operator|.
name|hasBlockedThreads
return|;
block|}
block|}
end_class
end_unit
