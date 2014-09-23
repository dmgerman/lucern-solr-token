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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|LeafReaderContext
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
name|Counter
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * The {@link TimeLimitingCollector} is used to timeout search requests that  * take longer than the maximum allowed search time limit. After this time is  * exceeded, the search thread is stopped by throwing a  * {@link TimeExceededException}.  */
end_comment
begin_class
DECL|class|TimeLimitingCollector
specifier|public
class|class
name|TimeLimitingCollector
implements|implements
name|Collector
block|{
comment|/** Thrown when elapsed search time exceeds allowed search time. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|TimeExceededException
specifier|public
specifier|static
class|class
name|TimeExceededException
extends|extends
name|RuntimeException
block|{
DECL|field|timeAllowed
specifier|private
name|long
name|timeAllowed
decl_stmt|;
DECL|field|timeElapsed
specifier|private
name|long
name|timeElapsed
decl_stmt|;
DECL|field|lastDocCollected
specifier|private
name|int
name|lastDocCollected
decl_stmt|;
DECL|method|TimeExceededException
specifier|private
name|TimeExceededException
parameter_list|(
name|long
name|timeAllowed
parameter_list|,
name|long
name|timeElapsed
parameter_list|,
name|int
name|lastDocCollected
parameter_list|)
block|{
name|super
argument_list|(
literal|"Elapsed time: "
operator|+
name|timeElapsed
operator|+
literal|"Exceeded allowed search time: "
operator|+
name|timeAllowed
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeAllowed
operator|=
name|timeAllowed
expr_stmt|;
name|this
operator|.
name|timeElapsed
operator|=
name|timeElapsed
expr_stmt|;
name|this
operator|.
name|lastDocCollected
operator|=
name|lastDocCollected
expr_stmt|;
block|}
comment|/** Returns allowed time (milliseconds). */
DECL|method|getTimeAllowed
specifier|public
name|long
name|getTimeAllowed
parameter_list|()
block|{
return|return
name|timeAllowed
return|;
block|}
comment|/** Returns elapsed time (milliseconds). */
DECL|method|getTimeElapsed
specifier|public
name|long
name|getTimeElapsed
parameter_list|()
block|{
return|return
name|timeElapsed
return|;
block|}
comment|/** Returns last doc (absolute doc id) that was collected when the search time exceeded. */
DECL|method|getLastDocCollected
specifier|public
name|int
name|getLastDocCollected
parameter_list|()
block|{
return|return
name|lastDocCollected
return|;
block|}
block|}
DECL|field|t0
specifier|private
name|long
name|t0
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|timeout
specifier|private
name|long
name|timeout
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|collector
specifier|private
name|Collector
name|collector
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Counter
name|clock
decl_stmt|;
DECL|field|ticksAllowed
specifier|private
specifier|final
name|long
name|ticksAllowed
decl_stmt|;
DECL|field|greedy
specifier|private
name|boolean
name|greedy
init|=
literal|false
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
comment|/**    * Create a TimeLimitedCollector wrapper over another {@link Collector} with a specified timeout.    * @param collector the wrapped {@link Collector}    * @param clock the timer clock    * @param ticksAllowed max time allowed for collecting    * hits after which {@link TimeExceededException} is thrown    */
DECL|method|TimeLimitingCollector
specifier|public
name|TimeLimitingCollector
parameter_list|(
specifier|final
name|Collector
name|collector
parameter_list|,
name|Counter
name|clock
parameter_list|,
specifier|final
name|long
name|ticksAllowed
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|ticksAllowed
operator|=
name|ticksAllowed
expr_stmt|;
block|}
comment|/**    * Sets the baseline for this collector. By default the collectors baseline is     * initialized once the first reader is passed to the collector.     * To include operations executed in prior to the actual document collection    * set the baseline through this method in your prelude.    *<p>    * Example usage:    *<pre class="prettyprint">    *   Counter clock = ...;    *   long baseline = clock.get();    *   // ... prepare search    *   TimeLimitingCollector collector = new TimeLimitingCollector(c, clock, numTicks);    *   collector.setBaseline(baseline);    *   indexSearcher.search(query, collector);    *</pre>    *</p>    * @see #setBaseline()     */
DECL|method|setBaseline
specifier|public
name|void
name|setBaseline
parameter_list|(
name|long
name|clockTime
parameter_list|)
block|{
name|t0
operator|=
name|clockTime
expr_stmt|;
name|timeout
operator|=
name|t0
operator|+
name|ticksAllowed
expr_stmt|;
block|}
comment|/**    * Syntactic sugar for {@link #setBaseline(long)} using {@link Counter#get()}    * on the clock passed to the constructor.    */
DECL|method|setBaseline
specifier|public
name|void
name|setBaseline
parameter_list|()
block|{
name|setBaseline
argument_list|(
name|clock
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if this time limited collector is greedy in collecting the last hit.    * A non greedy collector, upon a timeout, would throw a {@link TimeExceededException}     * without allowing the wrapped collector to collect current doc. A greedy one would     * first allow the wrapped hit collector to collect current doc and only then     * throw a {@link TimeExceededException}.    * @see #setGreedy(boolean)    */
DECL|method|isGreedy
specifier|public
name|boolean
name|isGreedy
parameter_list|()
block|{
return|return
name|greedy
return|;
block|}
comment|/**    * Sets whether this time limited collector is greedy.    * @param greedy true to make this time limited greedy    * @see #isGreedy()    */
DECL|method|setGreedy
specifier|public
name|void
name|setGreedy
parameter_list|(
name|boolean
name|greedy
parameter_list|)
block|{
name|this
operator|.
name|greedy
operator|=
name|greedy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
if|if
condition|(
name|Long
operator|.
name|MIN_VALUE
operator|==
name|t0
condition|)
block|{
name|setBaseline
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|collector
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|time
init|=
name|clock
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeout
operator|<
name|time
condition|)
block|{
if|if
condition|(
name|greedy
condition|)
block|{
comment|//System.out.println(this+"  greedy: before failing, collecting doc: "+(docBase + doc)+"  "+(time-t0));
name|in
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println(this+"  failing on:  "+(docBase + doc)+"  "+(time-t0));
throw|throw
operator|new
name|TimeExceededException
argument_list|(
name|timeout
operator|-
name|t0
argument_list|,
name|time
operator|-
name|t0
argument_list|,
name|docBase
operator|+
name|doc
argument_list|)
throw|;
block|}
comment|//System.out.println(this+"  collecting: "+(docBase + doc)+"  "+(time-t0));
name|in
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**    * This is so the same timer can be used with a multi-phase search process such as grouping.     * We don't want to create a new TimeLimitingCollector for each phase because that would     * reset the timer for each phase.  Once time is up subsequent phases need to timeout quickly.    *    * @param collector The actual collector performing search functionality    */
DECL|method|setCollector
specifier|public
name|void
name|setCollector
parameter_list|(
name|Collector
name|collector
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
block|}
comment|/**    * Returns the global TimerThreads {@link Counter}    *<p>    * Invoking this creates may create a new instance of {@link TimerThread} iff    * the global {@link TimerThread} has never been accessed before. The thread    * returned from this method is started on creation and will be alive unless    * you stop the {@link TimerThread} via {@link TimerThread#stopTimer()}.    *</p>    * @return the global TimerThreads {@link Counter}    * @lucene.experimental    */
DECL|method|getGlobalCounter
specifier|public
specifier|static
name|Counter
name|getGlobalCounter
parameter_list|()
block|{
return|return
name|TimerThreadHolder
operator|.
name|THREAD
operator|.
name|counter
return|;
block|}
comment|/**    * Returns the global {@link TimerThread}.    *<p>    * Invoking this creates may create a new instance of {@link TimerThread} iff    * the global {@link TimerThread} has never been accessed before. The thread    * returned from this method is started on creation and will be alive unless    * you stop the {@link TimerThread} via {@link TimerThread#stopTimer()}.    *</p>    *     * @return the global {@link TimerThread}    * @lucene.experimental    */
DECL|method|getGlobalTimerThread
specifier|public
specifier|static
name|TimerThread
name|getGlobalTimerThread
parameter_list|()
block|{
return|return
name|TimerThreadHolder
operator|.
name|THREAD
return|;
block|}
DECL|class|TimerThreadHolder
specifier|private
specifier|static
specifier|final
class|class
name|TimerThreadHolder
block|{
DECL|field|THREAD
specifier|static
specifier|final
name|TimerThread
name|THREAD
decl_stmt|;
static|static
block|{
name|THREAD
operator|=
operator|new
name|TimerThread
argument_list|(
name|Counter
operator|.
name|newCounter
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|THREAD
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Thread used to timeout search requests.    * Can be stopped completely with {@link TimerThread#stopTimer()}    * @lucene.experimental    */
DECL|class|TimerThread
specifier|public
specifier|static
specifier|final
class|class
name|TimerThread
extends|extends
name|Thread
block|{
DECL|field|THREAD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|THREAD_NAME
init|=
literal|"TimeLimitedCollector timer thread"
decl_stmt|;
DECL|field|DEFAULT_RESOLUTION
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RESOLUTION
init|=
literal|20
decl_stmt|;
comment|// NOTE: we can avoid explicit synchronization here for several reasons:
comment|// * updates to volatile long variables are atomic
comment|// * only single thread modifies this value
comment|// * use of volatile keyword ensures that it does not reside in
comment|//   a register, but in main memory (so that changes are visible to
comment|//   other threads).
comment|// * visibility of changes does not need to be instantaneous, we can
comment|//   afford losing a tick or two.
comment|//
comment|// See section 17 of the Java Language Specification for details.
DECL|field|time
specifier|private
specifier|volatile
name|long
name|time
init|=
literal|0
decl_stmt|;
DECL|field|stop
specifier|private
specifier|volatile
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
DECL|field|resolution
specifier|private
specifier|volatile
name|long
name|resolution
decl_stmt|;
DECL|field|counter
specifier|final
name|Counter
name|counter
decl_stmt|;
DECL|method|TimerThread
specifier|public
name|TimerThread
parameter_list|(
name|long
name|resolution
parameter_list|,
name|Counter
name|counter
parameter_list|)
block|{
name|super
argument_list|(
name|THREAD_NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|resolution
operator|=
name|resolution
expr_stmt|;
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
name|this
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|TimerThread
specifier|public
name|TimerThread
parameter_list|(
name|Counter
name|counter
parameter_list|)
block|{
name|this
argument_list|(
name|DEFAULT_RESOLUTION
argument_list|,
name|counter
argument_list|)
expr_stmt|;
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
operator|!
name|stop
condition|)
block|{
comment|// TODO: Use System.nanoTime() when Lucene moves to Java SE 5.
name|counter
operator|.
name|addAndGet
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|resolution
argument_list|)
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
block|}
comment|/**      * Get the timer value in milliseconds.      */
DECL|method|getMilliseconds
specifier|public
name|long
name|getMilliseconds
parameter_list|()
block|{
return|return
name|time
return|;
block|}
comment|/**      * Stops the timer thread       */
DECL|method|stopTimer
specifier|public
name|void
name|stopTimer
parameter_list|()
block|{
name|stop
operator|=
literal|true
expr_stmt|;
block|}
comment|/**       * Return the timer resolution.      * @see #setResolution(long)      */
DECL|method|getResolution
specifier|public
name|long
name|getResolution
parameter_list|()
block|{
return|return
name|resolution
return|;
block|}
comment|/**      * Set the timer resolution.      * The default timer resolution is 20 milliseconds.       * This means that a search required to take no longer than       * 800 milliseconds may be stopped after 780 to 820 milliseconds.      *<br>Note that:       *<ul>      *<li>Finer (smaller) resolution is more accurate but less efficient.</li>      *<li>Setting resolution to less than 5 milliseconds will be silently modified to 5 milliseconds.</li>      *<li>Setting resolution smaller than current resolution might take effect only after current       * resolution. (Assume current resolution of 20 milliseconds is modified to 5 milliseconds,       * then it can take up to 20 milliseconds for the change to have effect.</li>      *</ul>            */
DECL|method|setResolution
specifier|public
name|void
name|setResolution
parameter_list|(
name|long
name|resolution
parameter_list|)
block|{
name|this
operator|.
name|resolution
operator|=
name|Math
operator|.
name|max
argument_list|(
name|resolution
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// 5 milliseconds is about the minimum reasonable time for a Object.wait(long) call.
block|}
block|}
block|}
end_class
end_unit
