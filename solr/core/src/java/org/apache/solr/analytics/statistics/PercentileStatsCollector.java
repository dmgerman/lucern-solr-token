begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.statistics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|statistics
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|analytics
operator|.
name|util
operator|.
name|PercentileCalculator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import
begin_comment
comment|/**  *<code>PercentileStatsCollector</code> computes a given list of percentiles.  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|PercentileStatsCollector
specifier|public
class|class
name|PercentileStatsCollector
extends|extends
name|AbstractDelegatingStatsCollector
block|{
DECL|field|values
specifier|public
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Comparable
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|PERCENTILE_PATTERN
specifier|public
specifier|static
specifier|final
name|Pattern
name|PERCENTILE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"perc(?:entile)?_(\\d+)"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|percentiles
specifier|protected
specifier|final
name|double
index|[]
name|percentiles
decl_stmt|;
DECL|field|percentileNames
specifier|protected
specifier|final
name|String
index|[]
name|percentileNames
decl_stmt|;
DECL|field|results
specifier|protected
name|Comparable
index|[]
name|results
decl_stmt|;
DECL|method|PercentileStatsCollector
specifier|public
name|PercentileStatsCollector
parameter_list|(
name|StatsCollector
name|delegate
parameter_list|,
name|double
index|[]
name|percentiles
parameter_list|,
name|String
index|[]
name|percentileNames
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|percentiles
operator|=
name|percentiles
expr_stmt|;
name|this
operator|.
name|percentileNames
operator|=
name|percentileNames
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStat
specifier|public
name|Comparable
name|getStat
parameter_list|(
name|String
name|stat
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|percentiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
name|percentileNames
index|[
name|i
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
name|results
operator|!=
literal|null
condition|)
block|{
return|return
name|results
index|[
name|i
index|]
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
return|return
name|delegate
operator|.
name|getStat
argument_list|(
name|stat
argument_list|)
return|;
block|}
DECL|method|compute
specifier|public
name|void
name|compute
parameter_list|()
block|{
name|delegate
operator|.
name|compute
argument_list|()
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|results
operator|=
name|Iterables
operator|.
name|toArray
argument_list|(
name|getPercentiles
argument_list|()
argument_list|,
name|Comparable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|results
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|getPercentiles
specifier|protected
name|List
argument_list|<
name|Comparable
argument_list|>
name|getPercentiles
parameter_list|()
block|{
return|return
name|PercentileCalculator
operator|.
name|getPercentiles
argument_list|(
name|values
argument_list|,
name|percentiles
argument_list|)
return|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|exists
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
operator|(
name|Comparable
operator|)
name|value
operator|.
name|toObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
