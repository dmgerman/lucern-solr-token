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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
operator|.
name|Histogram
operator|.
name|SampleType
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
name|TimeUnit
import|;
end_import
begin_comment
comment|/**  * A timer metric which aggregates timing durations and provides duration statistics, plus  * throughput statistics via {@link Meter}.  */
end_comment
begin_class
DECL|class|Timer
specifier|public
class|class
name|Timer
block|{
DECL|field|durationUnit
DECL|field|rateUnit
specifier|private
specifier|final
name|TimeUnit
name|durationUnit
decl_stmt|,
name|rateUnit
decl_stmt|;
DECL|field|meter
specifier|private
specifier|final
name|Meter
name|meter
decl_stmt|;
DECL|field|histogram
specifier|private
specifier|final
name|Histogram
name|histogram
init|=
operator|new
name|Histogram
argument_list|(
name|SampleType
operator|.
name|BIASED
argument_list|)
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|method|Timer
specifier|public
name|Timer
parameter_list|()
block|{
name|this
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|Clock
operator|.
name|defaultClock
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link Timer}.    *    * @param durationUnit the scale unit for this timer's duration metrics    * @param rateUnit     the scale unit for this timer's rate metrics    * @param clock        the clock used to calculate duration    */
DECL|method|Timer
specifier|public
name|Timer
parameter_list|(
name|TimeUnit
name|durationUnit
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
name|durationUnit
operator|=
name|durationUnit
expr_stmt|;
name|this
operator|.
name|rateUnit
operator|=
name|rateUnit
expr_stmt|;
name|this
operator|.
name|meter
operator|=
operator|new
name|Meter
argument_list|(
literal|"calls"
argument_list|,
name|rateUnit
argument_list|,
name|clock
argument_list|)
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
comment|/**    * Returns the timer's duration scale unit.    *    * @return the timer's duration scale unit    */
DECL|method|getDurationUnit
specifier|public
name|TimeUnit
name|getDurationUnit
parameter_list|()
block|{
return|return
name|durationUnit
return|;
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
comment|/**    * Clears all recorded durations.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|histogram
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds a recorded duration.    *    * @param duration the length of the duration    * @param unit     the scale unit of {@code duration}    */
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
name|update
argument_list|(
name|unit
operator|.
name|toNanos
argument_list|(
name|duration
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Times and records the duration of event.    *    * @param event a {@link Callable} whose {@link Callable#call()} method implements a process    *              whose duration should be timed    * @param<T>   the type of the value returned by {@code event}    * @return the value returned by {@code event}    * @throws Exception if {@code event} throws an {@link Exception}    */
DECL|method|time
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|time
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|event
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|startTime
init|=
name|clock
operator|.
name|getTick
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|event
operator|.
name|call
argument_list|()
return|;
block|}
finally|finally
block|{
name|update
argument_list|(
name|clock
operator|.
name|getTick
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a timing {@link TimerContext}, which measures an elapsed time in nanoseconds.    *    * @return a new {@link TimerContext}    */
DECL|method|time
specifier|public
name|TimerContext
name|time
parameter_list|()
block|{
return|return
operator|new
name|TimerContext
argument_list|(
name|this
argument_list|,
name|clock
argument_list|)
return|;
block|}
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|histogram
operator|.
name|getCount
argument_list|()
return|;
block|}
DECL|method|getFifteenMinuteRate
specifier|public
name|double
name|getFifteenMinuteRate
parameter_list|()
block|{
return|return
name|meter
operator|.
name|getFifteenMinuteRate
argument_list|()
return|;
block|}
DECL|method|getFiveMinuteRate
specifier|public
name|double
name|getFiveMinuteRate
parameter_list|()
block|{
return|return
name|meter
operator|.
name|getFiveMinuteRate
argument_list|()
return|;
block|}
DECL|method|getMeanRate
specifier|public
name|double
name|getMeanRate
parameter_list|()
block|{
return|return
name|meter
operator|.
name|getMeanRate
argument_list|()
return|;
block|}
DECL|method|getOneMinuteRate
specifier|public
name|double
name|getOneMinuteRate
parameter_list|()
block|{
return|return
name|meter
operator|.
name|getOneMinuteRate
argument_list|()
return|;
block|}
comment|/**    * Returns the longest recorded duration.    *    * @return the longest recorded duration    */
DECL|method|getMax
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|convertFromNS
argument_list|(
name|histogram
operator|.
name|getMax
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the shortest recorded duration.    *    * @return the shortest recorded duration    */
DECL|method|getMin
specifier|public
name|double
name|getMin
parameter_list|()
block|{
return|return
name|convertFromNS
argument_list|(
name|histogram
operator|.
name|getMin
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the arithmetic mean of all recorded durations.    *    * @return the arithmetic mean of all recorded durations    */
DECL|method|getMean
specifier|public
name|double
name|getMean
parameter_list|()
block|{
return|return
name|convertFromNS
argument_list|(
name|histogram
operator|.
name|getMean
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the standard deviation of all recorded durations.    *    * @return the standard deviation of all recorded durations    */
DECL|method|getStdDev
specifier|public
name|double
name|getStdDev
parameter_list|()
block|{
return|return
name|convertFromNS
argument_list|(
name|histogram
operator|.
name|getStdDev
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the sum of all recorded durations.    *    * @return the sum of all recorded durations    */
DECL|method|getSum
specifier|public
name|double
name|getSum
parameter_list|()
block|{
return|return
name|convertFromNS
argument_list|(
name|histogram
operator|.
name|getSum
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getSnapshot
specifier|public
name|Snapshot
name|getSnapshot
parameter_list|()
block|{
specifier|final
name|double
index|[]
name|values
init|=
name|histogram
operator|.
name|getSnapshot
argument_list|()
operator|.
name|getValues
argument_list|()
decl_stmt|;
specifier|final
name|double
index|[]
name|converted
init|=
operator|new
name|double
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|converted
index|[
name|i
index|]
operator|=
name|convertFromNS
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Snapshot
argument_list|(
name|converted
argument_list|)
return|;
block|}
DECL|method|getEventType
specifier|public
name|String
name|getEventType
parameter_list|()
block|{
return|return
name|meter
operator|.
name|getEventType
argument_list|()
return|;
block|}
DECL|method|update
specifier|private
name|void
name|update
parameter_list|(
name|long
name|duration
parameter_list|)
block|{
if|if
condition|(
name|duration
operator|>=
literal|0
condition|)
block|{
name|histogram
operator|.
name|update
argument_list|(
name|duration
argument_list|)
expr_stmt|;
name|meter
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|convertFromNS
specifier|private
name|double
name|convertFromNS
parameter_list|(
name|double
name|ns
parameter_list|)
block|{
return|return
name|ns
operator|/
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|1
argument_list|,
name|durationUnit
argument_list|)
return|;
block|}
block|}
end_class
end_unit
