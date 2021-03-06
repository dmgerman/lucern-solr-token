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
begin_comment
comment|/**  * A meter metric which measures mean throughput and one-, five-, and fifteen-minute  * exponentially-weighted moving average throughputs.  *  * @see<a href="http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average">EMA</a>  */
end_comment
begin_class
DECL|class|Meter
specifier|public
class|class
name|Meter
block|{
DECL|field|TICK_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|TICK_INTERVAL
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toNanos
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|m1Rate
specifier|private
specifier|final
name|EWMA
name|m1Rate
init|=
name|EWMA
operator|.
name|oneMinuteEWMA
argument_list|()
decl_stmt|;
DECL|field|m5Rate
specifier|private
specifier|final
name|EWMA
name|m5Rate
init|=
name|EWMA
operator|.
name|fiveMinuteEWMA
argument_list|()
decl_stmt|;
DECL|field|m15Rate
specifier|private
specifier|final
name|EWMA
name|m15Rate
init|=
name|EWMA
operator|.
name|fifteenMinuteEWMA
argument_list|()
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|lastTick
specifier|private
specifier|final
name|AtomicLong
name|lastTick
decl_stmt|;
DECL|field|rateUnit
specifier|private
specifier|final
name|TimeUnit
name|rateUnit
decl_stmt|;
DECL|field|eventType
specifier|private
specifier|final
name|String
name|eventType
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
comment|/**    * Creates a new {@link Meter}.    *    * @param eventType  the plural name of the event the meter is measuring (e.g., {@code    *                   "requests"})    * @param rateUnit   the rate unit of the new meter    * @param clock      the clock to use for the meter ticks    */
DECL|method|Meter
name|Meter
parameter_list|(
name|String
name|eventType
parameter_list|,
name|TimeUnit
name|rateUnit
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|rateUnit
operator|=
name|rateUnit
expr_stmt|;
name|this
operator|.
name|eventType
operator|=
name|eventType
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|this
operator|.
name|clock
operator|.
name|getTick
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastTick
operator|=
operator|new
name|AtomicLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getRateUnit
specifier|public
name|TimeUnit
name|getRateUnit
parameter_list|()
block|{
return|return
name|rateUnit
return|;
block|}
DECL|method|getEventType
specifier|public
name|String
name|getEventType
parameter_list|()
block|{
return|return
name|eventType
return|;
block|}
comment|/**    * Updates the moving averages.    */
DECL|method|tick
name|void
name|tick
parameter_list|()
block|{
name|m1Rate
operator|.
name|tick
argument_list|()
expr_stmt|;
name|m5Rate
operator|.
name|tick
argument_list|()
expr_stmt|;
name|m15Rate
operator|.
name|tick
argument_list|()
expr_stmt|;
block|}
comment|/**    * Mark the occurrence of an event.    */
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|()
block|{
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Mark the occurrence of a given number of events.    *    * @param n the number of events    */
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|tickIfNecessary
argument_list|()
expr_stmt|;
name|count
operator|.
name|addAndGet
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|m1Rate
operator|.
name|update
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|m5Rate
operator|.
name|update
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|m15Rate
operator|.
name|update
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
DECL|method|tickIfNecessary
specifier|private
name|void
name|tickIfNecessary
parameter_list|()
block|{
specifier|final
name|long
name|oldTick
init|=
name|lastTick
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|long
name|newTick
init|=
name|clock
operator|.
name|getTick
argument_list|()
decl_stmt|;
specifier|final
name|long
name|age
init|=
name|newTick
operator|-
name|oldTick
decl_stmt|;
if|if
condition|(
name|age
operator|>
name|TICK_INTERVAL
operator|&&
name|lastTick
operator|.
name|compareAndSet
argument_list|(
name|oldTick
argument_list|,
name|newTick
argument_list|)
condition|)
block|{
specifier|final
name|long
name|requiredTicks
init|=
name|age
operator|/
name|TICK_INTERVAL
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requiredTicks
condition|;
name|i
operator|++
control|)
block|{
name|tick
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getFifteenMinuteRate
specifier|public
name|double
name|getFifteenMinuteRate
parameter_list|()
block|{
name|tickIfNecessary
argument_list|()
expr_stmt|;
return|return
name|m15Rate
operator|.
name|getRate
argument_list|(
name|rateUnit
argument_list|)
return|;
block|}
DECL|method|getFiveMinuteRate
specifier|public
name|double
name|getFiveMinuteRate
parameter_list|()
block|{
name|tickIfNecessary
argument_list|()
expr_stmt|;
return|return
name|m5Rate
operator|.
name|getRate
argument_list|(
name|rateUnit
argument_list|)
return|;
block|}
DECL|method|getMeanRate
specifier|public
name|double
name|getMeanRate
parameter_list|()
block|{
if|if
condition|(
name|getCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0.0
return|;
block|}
else|else
block|{
specifier|final
name|long
name|elapsed
init|=
operator|(
name|clock
operator|.
name|getTick
argument_list|()
operator|-
name|startTime
operator|)
decl_stmt|;
return|return
name|convertNsRate
argument_list|(
name|getCount
argument_list|()
operator|/
operator|(
name|double
operator|)
name|elapsed
argument_list|)
return|;
block|}
block|}
DECL|method|getOneMinuteRate
specifier|public
name|double
name|getOneMinuteRate
parameter_list|()
block|{
name|tickIfNecessary
argument_list|()
expr_stmt|;
return|return
name|m1Rate
operator|.
name|getRate
argument_list|(
name|rateUnit
argument_list|)
return|;
block|}
DECL|method|convertNsRate
specifier|private
name|double
name|convertNsRate
parameter_list|(
name|double
name|ratePerNs
parameter_list|)
block|{
return|return
name|ratePerNs
operator|*
operator|(
name|double
operator|)
name|rateUnit
operator|.
name|toNanos
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
end_class
end_unit
