begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ThreadInterruptedException
import|;
end_import
begin_comment
comment|/**  * Utility class that runs a reopen thread to periodically  * reopen the NRT searchers in the provided {@link  * NRTManager}.  *  *<p> Typical usage looks like this:  *  *<pre>  *   ... open your own writer ...  *   *   NRTManager manager = new NRTManager(writer);  *  *   // Refreshes searcher every 5 seconds when nobody is waiting, and up to 100 msec delay  *   // when somebody is waiting:  *   NRTManagerReopenThread reopenThread = new NRTManagerReopenThread(manager, 5.0, 0.1);  *   reopenThread.setName("NRT Reopen Thread");  *   reopenThread.setPriority(Math.min(Thread.currentThread().getPriority()+2, Thread.MAX_PRIORITY));  *   reopenThread.setDaemon(true);  *   reopenThread.start();  *</pre>  *  * Then, for each incoming query, do this:  *  *<pre>  *   // For each incoming query:  *   IndexSearcher searcher = manager.get();  *   try {  *     // Use searcher to search...  *   } finally {  *     manager.release(searcher);  *   }  *</pre>  *  * You should make changes using the<code>NRTManager</code>; if you later need to obtain  * a searcher reflecting those changes:  *  *<pre>  *   // ... or updateDocument, deleteDocuments, etc:  *   long gen = manager.addDocument(...);  *     *   // Returned searcher is guaranteed to reflect the just added document  *   IndexSearcher searcher = manager.get(gen);  *   try {  *     // Use searcher to search...  *   } finally {  *     manager.release(searcher);  *   }  *</pre>  *  *  * When you are done be sure to close both the manager and the reopen thrad:  *<pre>   *   reopenThread.close();         *   manager.close();  *</pre>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|NRTManagerReopenThread
specifier|public
class|class
name|NRTManagerReopenThread
extends|extends
name|Thread
implements|implements
name|NRTManager
operator|.
name|WaitingListener
implements|,
name|Closeable
block|{
DECL|field|manager
specifier|private
specifier|final
name|NRTManager
name|manager
decl_stmt|;
DECL|field|targetMaxStaleNS
specifier|private
specifier|final
name|long
name|targetMaxStaleNS
decl_stmt|;
DECL|field|targetMinStaleNS
specifier|private
specifier|final
name|long
name|targetMinStaleNS
decl_stmt|;
DECL|field|finish
specifier|private
name|boolean
name|finish
decl_stmt|;
DECL|field|waitingGen
specifier|private
name|long
name|waitingGen
decl_stmt|;
comment|/**    * Create NRTManagerReopenThread, to periodically reopen the NRT searcher.    *    * @param targetMaxStaleSec Maximum time until a new    *        reader must be opened; this sets the upper bound    *        on how slowly reopens may occur    *    * @param targetMinStaleSec Mininum time until a new    *        reader can be opened; this sets the lower bound    *        on how quickly reopens may occur, when a caller    *        is waiting for a specific indexing change to    *        become visible.    */
DECL|method|NRTManagerReopenThread
specifier|public
name|NRTManagerReopenThread
parameter_list|(
name|NRTManager
name|manager
parameter_list|,
name|double
name|targetMaxStaleSec
parameter_list|,
name|double
name|targetMinStaleSec
parameter_list|)
block|{
if|if
condition|(
name|targetMaxStaleSec
operator|<
name|targetMinStaleSec
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"targetMaxScaleSec (= "
operator|+
name|targetMaxStaleSec
operator|+
literal|")< targetMinStaleSec (="
operator|+
name|targetMinStaleSec
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|targetMaxStaleNS
operator|=
call|(
name|long
call|)
argument_list|(
literal|1000000000
operator|*
name|targetMaxStaleSec
argument_list|)
expr_stmt|;
name|this
operator|.
name|targetMinStaleNS
operator|=
call|(
name|long
call|)
argument_list|(
literal|1000000000
operator|*
name|targetMinStaleSec
argument_list|)
expr_stmt|;
name|manager
operator|.
name|addWaitingListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
comment|//System.out.println("NRT: set finish");
name|manager
operator|.
name|removeWaitingListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
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
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
DECL|method|waiting
specifier|public
specifier|synchronized
name|void
name|waiting
parameter_list|(
name|long
name|targetGen
parameter_list|)
block|{
name|waitingGen
operator|=
name|Math
operator|.
name|max
argument_list|(
name|waitingGen
argument_list|,
name|targetGen
argument_list|)
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
comment|//System.out.println(Thread.currentThread().getName() + ": force wakeup waitingGen=" + waitingGen + " applyDeletes=" + applyDeletes);
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// TODO: maybe use private thread ticktock timer, in
comment|// case clock shift messes up nanoTime?
name|long
name|lastReopenStartNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
comment|//System.out.println("reopen: start");
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|hasWaiting
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// TODO: try to guestimate how long reopen might
comment|// take based on past data?
while|while
condition|(
operator|!
name|finish
condition|)
block|{
comment|//System.out.println("reopen: cycle");
comment|// True if we have someone waiting for reopen'd searcher:
name|hasWaiting
operator|=
name|waitingGen
operator|>
name|manager
operator|.
name|getCurrentSearchingGen
argument_list|()
expr_stmt|;
specifier|final
name|long
name|nextReopenStartNS
init|=
name|lastReopenStartNS
operator|+
operator|(
name|hasWaiting
condition|?
name|targetMinStaleNS
else|:
name|targetMaxStaleNS
operator|)
decl_stmt|;
specifier|final
name|long
name|sleepNS
init|=
name|nextReopenStartNS
operator|-
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|sleepNS
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("reopen: sleep " + (sleepNS/1000000.0) + " ms (hasWaiting=" + hasWaiting + ")");
try|try
block|{
name|wait
argument_list|(
name|sleepNS
operator|/
literal|1000000
argument_list|,
call|(
name|int
call|)
argument_list|(
name|sleepNS
operator|%
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
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
comment|//System.out.println("NRT: set finish on interrupt");
name|finish
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|finish
condition|)
block|{
comment|//System.out.println("reopen: finish");
return|return;
block|}
comment|//System.out.println("reopen: start hasWaiting=" + hasWaiting);
block|}
name|lastReopenStartNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
try|try
block|{
comment|//final long t0 = System.nanoTime();
name|manager
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
comment|//System.out.println("reopen took " + ((System.nanoTime()-t0)/1000000.0) + " msec");
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": IOE");
comment|//ioe.printStackTrace();
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|//System.out.println("REOPEN EXC");
comment|//t.printStackTrace(System.out);
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
