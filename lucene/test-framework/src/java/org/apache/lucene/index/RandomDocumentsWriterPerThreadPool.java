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
name|Random
import|;
end_import
begin_comment
comment|/**  *   * A {@link DocumentsWriterPerThreadPool} that selects thread states at random.  *   * @lucene.internal  * @lucene.experimental  */
end_comment
begin_class
DECL|class|RandomDocumentsWriterPerThreadPool
specifier|public
class|class
name|RandomDocumentsWriterPerThreadPool
extends|extends
name|DocumentsWriterPerThreadPool
block|{
DECL|field|states
specifier|private
specifier|final
name|ThreadState
index|[]
name|states
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|maxRetry
specifier|private
specifier|final
name|int
name|maxRetry
decl_stmt|;
DECL|method|RandomDocumentsWriterPerThreadPool
specifier|public
name|RandomDocumentsWriterPerThreadPool
parameter_list|(
name|int
name|maxNumPerThreads
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|maxNumPerThreads
argument_list|)
expr_stmt|;
assert|assert
name|getMaxThreadStates
argument_list|()
operator|>=
literal|1
assert|;
name|states
operator|=
operator|new
name|ThreadState
index|[
name|maxNumPerThreads
index|]
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxRetry
operator|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAndLock
name|ThreadState
name|getAndLock
parameter_list|(
name|Thread
name|requestingThread
parameter_list|,
name|DocumentsWriter
name|documentsWriter
parameter_list|)
block|{
name|ThreadState
name|threadState
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getActiveThreadState
argument_list|()
operator|==
literal|0
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|getActiveThreadState
argument_list|()
operator|==
literal|0
condition|)
block|{
name|threadState
operator|=
name|states
index|[
literal|0
index|]
operator|=
name|newThreadState
argument_list|()
expr_stmt|;
return|return
name|threadState
return|;
block|}
block|}
block|}
assert|assert
name|getActiveThreadState
argument_list|()
operator|>
literal|0
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxRetry
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ord
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|getActiveThreadState
argument_list|()
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|threadState
operator|=
name|states
index|[
name|ord
index|]
expr_stmt|;
assert|assert
name|threadState
operator|!=
literal|null
assert|;
block|}
if|if
condition|(
name|threadState
operator|.
name|tryLock
argument_list|()
condition|)
block|{
return|return
name|threadState
return|;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
comment|/*      * only try to create a new threadstate if we can not lock the randomly      * selected state. this is important since some tests rely on a single      * threadstate in the single threaded case. Eventually it would be nice if      * we would not have this limitation but for now we just make sure we only      * allocate one threadstate if indexing is single threaded      */
synchronized|synchronized
init|(
name|this
init|)
block|{
name|ThreadState
name|newThreadState
init|=
name|newThreadState
argument_list|()
decl_stmt|;
if|if
condition|(
name|newThreadState
operator|!=
literal|null
condition|)
block|{
comment|// did we get a new state?
name|threadState
operator|=
name|states
index|[
name|getActiveThreadState
argument_list|()
operator|-
literal|1
index|]
operator|=
name|newThreadState
expr_stmt|;
assert|assert
name|threadState
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
return|return
name|threadState
return|;
block|}
comment|// if no new state is available lock the random one
block|}
assert|assert
name|threadState
operator|!=
literal|null
assert|;
name|threadState
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|threadState
return|;
block|}
block|}
end_class
end_unit
