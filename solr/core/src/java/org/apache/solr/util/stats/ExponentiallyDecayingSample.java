begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/*  * Forked from https://github.com/codahale/metrics  */
end_comment
begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package
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
name|Random
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
name|ConcurrentSkipListMap
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
name|atomic
operator|.
name|AtomicLong
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
name|ReentrantReadWriteLock
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|exp
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
import|;
end_import
begin_comment
comment|/**  * An exponentially-decaying random sample of {@code long}s. Uses Cormode et al's forward-decaying  * priority reservoir sampling method to produce a statistically representative sample,  * exponentially biased towards newer entries.  *  * See<a href="http://www.research.att.com/people/Cormode_Graham/library/publications/CormodeShkapenyukSrivastavaXu09.pdf">  *      Cormode et al. Forward Decay: A Practical Time Decay Model for Streaming Systems. ICDE '09: Proceedings of the 2009 IEEE International Conference on Data Engineering (2009)</a>  */
end_comment
begin_class
DECL|class|ExponentiallyDecayingSample
specifier|public
class|class
name|ExponentiallyDecayingSample
implements|implements
name|Sample
block|{
DECL|field|RESCALE_THRESHOLD
specifier|private
specifier|static
specifier|final
name|long
name|RESCALE_THRESHOLD
init|=
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toNanos
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|ConcurrentSkipListMap
argument_list|<
name|Double
argument_list|,
name|Long
argument_list|>
name|values
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|lock
decl_stmt|;
DECL|field|alpha
specifier|private
specifier|final
name|double
name|alpha
decl_stmt|;
DECL|field|reservoirSize
specifier|private
specifier|final
name|int
name|reservoirSize
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|volatile
name|long
name|startTime
decl_stmt|;
DECL|field|nextScaleTime
specifier|private
specifier|final
name|AtomicLong
name|nextScaleTime
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
comment|// TODO: Maybe replace this with a Mersenne Twister?
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@link ExponentiallyDecayingSample}.    *    * @param reservoirSize the number of samples to keep in the sampling reservoir    * @param alpha         the exponential decay factor; the higher this is, the more biased the    *                      sample will be towards newer values    */
DECL|method|ExponentiallyDecayingSample
specifier|public
name|ExponentiallyDecayingSample
parameter_list|(
name|int
name|reservoirSize
parameter_list|,
name|double
name|alpha
parameter_list|)
block|{
name|this
argument_list|(
name|reservoirSize
argument_list|,
name|alpha
argument_list|,
name|Clock
operator|.
name|defaultClock
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link ExponentiallyDecayingSample}.    *    * @param reservoirSize the number of samples to keep in the sampling reservoir    * @param alpha         the exponential decay factor; the higher this is, the more biased the    *                      sample will be towards newer values    */
DECL|method|ExponentiallyDecayingSample
specifier|public
name|ExponentiallyDecayingSample
parameter_list|(
name|int
name|reservoirSize
parameter_list|,
name|double
name|alpha
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
operator|new
name|ConcurrentSkipListMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|alpha
operator|=
name|alpha
expr_stmt|;
name|this
operator|.
name|reservoirSize
operator|=
name|reservoirSize
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|lockForRescale
argument_list|()
expr_stmt|;
try|try
block|{
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|count
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|currentTimeInSeconds
argument_list|()
expr_stmt|;
name|nextScaleTime
operator|.
name|set
argument_list|(
name|clock
operator|.
name|getTick
argument_list|()
operator|+
name|RESCALE_THRESHOLD
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|unlockForRescale
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|min
argument_list|(
name|reservoirSize
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|update
argument_list|(
name|value
argument_list|,
name|currentTimeInSeconds
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds an old value with a fixed timestamp to the sample.    *    * @param value     the value to be added    * @param timestamp the epoch timestamp of {@code value} in seconds    */
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|long
name|value
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|rescaleIfNeeded
argument_list|()
expr_stmt|;
name|lockForRegularUsage
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|double
name|priority
init|=
name|weight
argument_list|(
name|timestamp
operator|-
name|startTime
argument_list|)
operator|/
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
specifier|final
name|long
name|newCount
init|=
name|count
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|newCount
operator|<=
name|reservoirSize
condition|)
block|{
name|values
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Double
name|first
init|=
name|values
operator|.
name|firstKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|<
name|priority
condition|)
block|{
if|if
condition|(
name|values
operator|.
name|putIfAbsent
argument_list|(
name|priority
argument_list|,
name|value
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// ensure we always remove an item
while|while
condition|(
name|values
operator|.
name|remove
argument_list|(
name|first
argument_list|)
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|values
operator|.
name|firstKey
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|unlockForRegularUsage
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|rescaleIfNeeded
specifier|private
name|void
name|rescaleIfNeeded
parameter_list|()
block|{
specifier|final
name|long
name|now
init|=
name|clock
operator|.
name|getTick
argument_list|()
decl_stmt|;
specifier|final
name|long
name|next
init|=
name|nextScaleTime
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>=
name|next
condition|)
block|{
name|rescale
argument_list|(
name|now
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSnapshot
specifier|public
name|Snapshot
name|getSnapshot
parameter_list|()
block|{
name|lockForRegularUsage
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|new
name|Snapshot
argument_list|(
name|values
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|unlockForRegularUsage
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|currentTimeInSeconds
specifier|private
name|long
name|currentTimeInSeconds
parameter_list|()
block|{
return|return
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
DECL|method|weight
specifier|private
name|double
name|weight
parameter_list|(
name|long
name|t
parameter_list|)
block|{
return|return
name|exp
argument_list|(
name|alpha
operator|*
name|t
argument_list|)
return|;
block|}
comment|/* "A common feature of the above techniquesâindeed, the key technique that    * allows us to track the decayed weights efficientlyâis that they maintain    * counts and other quantities based on g(ti â L), and only scale by g(t â L)    * at query time. But while g(ti âL)/g(tâL) is guaranteed to lie between zero    * and one, the intermediate values of g(ti â L) could become very large. For    * polynomial functions, these values should not grow too large, and should be    * effectively represented in practice by floating point values without loss of    * precision. For exponential functions, these values could grow quite large as    * new values of (ti â L) become large, and potentially exceed the capacity of    * common floating point types. However, since the values stored by the    * algorithms are linear combinations of g values (scaled sums), they can be    * rescaled relative to a new landmark. That is, by the analysis of exponential    * decay in Section III-A, the choice of L does not affect the final result. We    * can therefore multiply each value based on L by a factor of exp(âÎ±(Lâ² â L)),    * and obtain the correct value as if we had instead computed relative to a new    * landmark Lâ² (and then use this new Lâ² at query time). This can be done with    * a linear pass over whatever data structure is being used."    */
DECL|method|rescale
specifier|private
name|void
name|rescale
parameter_list|(
name|long
name|now
parameter_list|,
name|long
name|next
parameter_list|)
block|{
if|if
condition|(
name|nextScaleTime
operator|.
name|compareAndSet
argument_list|(
name|next
argument_list|,
name|now
operator|+
name|RESCALE_THRESHOLD
argument_list|)
condition|)
block|{
name|lockForRescale
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|long
name|oldStartTime
init|=
name|startTime
decl_stmt|;
name|this
operator|.
name|startTime
operator|=
name|currentTimeInSeconds
argument_list|()
expr_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Double
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|values
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Double
name|key
range|:
name|keys
control|)
block|{
specifier|final
name|Long
name|value
init|=
name|values
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
name|key
operator|*
name|exp
argument_list|(
operator|-
name|alpha
operator|*
operator|(
name|startTime
operator|-
name|oldStartTime
operator|)
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// make sure the counter is in sync with the number of stored samples.
name|count
operator|.
name|set
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|unlockForRescale
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|unlockForRescale
specifier|private
name|void
name|unlockForRescale
parameter_list|()
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
DECL|method|lockForRescale
specifier|private
name|void
name|lockForRescale
parameter_list|()
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
DECL|method|lockForRegularUsage
specifier|private
name|void
name|lockForRegularUsage
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
DECL|method|unlockForRegularUsage
specifier|private
name|void
name|unlockForRegularUsage
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
