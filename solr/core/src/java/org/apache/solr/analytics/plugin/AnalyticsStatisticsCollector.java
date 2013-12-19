begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.plugin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|plugin
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
name|atomic
operator|.
name|AtomicLong
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|util
operator|.
name|stats
operator|.
name|Snapshot
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
name|util
operator|.
name|stats
operator|.
name|Timer
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
name|util
operator|.
name|stats
operator|.
name|TimerContext
import|;
end_import
begin_class
DECL|class|AnalyticsStatisticsCollector
specifier|public
class|class
name|AnalyticsStatisticsCollector
block|{
DECL|field|numRequests
specifier|private
specifier|final
name|AtomicLong
name|numRequests
decl_stmt|;
DECL|field|numAnalyticsRequests
specifier|private
specifier|final
name|AtomicLong
name|numAnalyticsRequests
decl_stmt|;
DECL|field|numStatsRequests
specifier|private
specifier|final
name|AtomicLong
name|numStatsRequests
decl_stmt|;
DECL|field|numCollectedStats
specifier|private
specifier|final
name|AtomicLong
name|numCollectedStats
decl_stmt|;
DECL|field|numFieldFacets
specifier|private
specifier|final
name|AtomicLong
name|numFieldFacets
decl_stmt|;
DECL|field|numRangeFacets
specifier|private
specifier|final
name|AtomicLong
name|numRangeFacets
decl_stmt|;
DECL|field|numQueryFacets
specifier|private
specifier|final
name|AtomicLong
name|numQueryFacets
decl_stmt|;
DECL|field|numQueries
specifier|private
specifier|final
name|AtomicLong
name|numQueries
decl_stmt|;
DECL|field|requestTimes
specifier|private
specifier|final
name|Timer
name|requestTimes
decl_stmt|;
DECL|field|currentTimer
specifier|public
name|TimerContext
name|currentTimer
decl_stmt|;
DECL|method|AnalyticsStatisticsCollector
specifier|public
name|AnalyticsStatisticsCollector
parameter_list|()
block|{
name|numRequests
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numAnalyticsRequests
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numStatsRequests
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numCollectedStats
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numFieldFacets
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numRangeFacets
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numQueryFacets
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|numQueries
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|requestTimes
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
block|}
DECL|method|startRequest
specifier|public
name|void
name|startRequest
parameter_list|()
block|{
name|numRequests
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|currentTimer
operator|=
name|requestTimes
operator|.
name|time
argument_list|()
expr_stmt|;
block|}
DECL|method|addRequests
specifier|public
name|void
name|addRequests
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numAnalyticsRequests
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|addStatsRequests
specifier|public
name|void
name|addStatsRequests
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numStatsRequests
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|addStatsCollected
specifier|public
name|void
name|addStatsCollected
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numCollectedStats
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|addFieldFacets
specifier|public
name|void
name|addFieldFacets
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numFieldFacets
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|addRangeFacets
specifier|public
name|void
name|addRangeFacets
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numRangeFacets
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|addQueryFacets
specifier|public
name|void
name|addQueryFacets
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numQueryFacets
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|addQueries
specifier|public
name|void
name|addQueries
parameter_list|(
name|long
name|num
parameter_list|)
block|{
name|numQueries
operator|.
name|addAndGet
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
DECL|method|endRequest
specifier|public
name|void
name|endRequest
parameter_list|()
block|{
name|currentTimer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getStatistics
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Snapshot
name|snapshot
init|=
name|requestTimes
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|numRequests
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"analyticsRequests"
argument_list|,
name|numAnalyticsRequests
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"statsRequests"
argument_list|,
name|numStatsRequests
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"statsCollected"
argument_list|,
name|numCollectedStats
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"fieldFacets"
argument_list|,
name|numFieldFacets
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"rangeFacets"
argument_list|,
name|numRangeFacets
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"queryFacets"
argument_list|,
name|numQueryFacets
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"queriesInQueryFacets"
argument_list|,
name|numQueries
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"totalTime"
argument_list|,
name|requestTimes
operator|.
name|getSum
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"avgRequestsPerSecond"
argument_list|,
name|requestTimes
operator|.
name|getMeanRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"5minRateReqsPerSecond"
argument_list|,
name|requestTimes
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"15minRateReqsPerSecond"
argument_list|,
name|requestTimes
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"avgTimePerRequest"
argument_list|,
name|requestTimes
operator|.
name|getMean
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"medianRequestTime"
argument_list|,
name|snapshot
operator|.
name|getMedian
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"75thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get75thPercentile
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"95thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get95thPercentile
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"99thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get99thPercentile
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"999thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get999thPercentile
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class
end_unit
