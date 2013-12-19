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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|AtomicReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
operator|.
name|ValueFiller
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|mutable
operator|.
name|MutableValue
import|;
end_import
begin_comment
comment|/**  *<code>MinMaxStatsCollector</code> computes the min, max, number of values and number of missing values.  */
end_comment
begin_class
DECL|class|MinMaxStatsCollector
specifier|public
class|class
name|MinMaxStatsCollector
implements|implements
name|StatsCollector
block|{
DECL|field|missingCount
specifier|protected
name|long
name|missingCount
init|=
literal|0
decl_stmt|;
DECL|field|valueCount
specifier|protected
name|long
name|valueCount
init|=
literal|0
decl_stmt|;
DECL|field|max
specifier|protected
name|MutableValue
name|max
decl_stmt|;
DECL|field|min
specifier|protected
name|MutableValue
name|min
decl_stmt|;
DECL|field|value
specifier|protected
name|MutableValue
name|value
decl_stmt|;
DECL|field|statsList
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|statsList
decl_stmt|;
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|function
specifier|protected
name|FunctionValues
name|function
decl_stmt|;
DECL|field|valueFiller
specifier|protected
name|ValueFiller
name|valueFiller
decl_stmt|;
DECL|method|MinMaxStatsCollector
specifier|public
name|MinMaxStatsCollector
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|statsList
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|statsList
operator|=
name|statsList
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|function
operator|=
name|source
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|valueFiller
operator|=
name|function
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|value
operator|=
name|valueFiller
operator|.
name|getValue
argument_list|()
expr_stmt|;
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
name|valueFiller
operator|.
name|fillValue
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
name|valueCount
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|max
operator|==
literal|null
condition|)
name|max
operator|=
name|value
operator|.
name|duplicate
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
operator|!
name|max
operator|.
name|exists
operator|||
name|value
operator|.
name|compareTo
argument_list|(
name|max
argument_list|)
operator|>
literal|0
condition|)
name|max
operator|.
name|copy
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|==
literal|null
condition|)
name|min
operator|=
name|value
operator|.
name|duplicate
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
operator|!
name|min
operator|.
name|exists
operator|||
name|value
operator|.
name|compareTo
argument_list|(
name|min
argument_list|)
operator|<
literal|0
condition|)
name|min
operator|.
name|copy
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missingCount
operator|+=
literal|1
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"<min=%s max=%s c=%d m=%d>"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|valueCount
argument_list|,
name|missingCount
argument_list|)
return|;
block|}
DECL|method|getStat
specifier|public
name|Comparable
name|getStat
parameter_list|(
name|String
name|stat
parameter_list|)
block|{
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"min"
argument_list|)
operator|&&
name|min
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|Comparable
operator|)
name|min
operator|.
name|toObject
argument_list|()
return|;
block|}
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"max"
argument_list|)
operator|&&
name|min
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|Comparable
operator|)
name|max
operator|.
name|toObject
argument_list|()
return|;
block|}
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"count"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"missing"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|missingCount
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getStatsList
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getStatsList
parameter_list|()
block|{
return|return
name|statsList
return|;
block|}
annotation|@
name|Override
DECL|method|compute
specifier|public
name|void
name|compute
parameter_list|()
block|{  }
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|getFunction
specifier|public
name|FunctionValues
name|getFunction
parameter_list|()
block|{
return|return
name|function
return|;
block|}
DECL|method|valueSourceString
specifier|public
name|String
name|valueSourceString
parameter_list|()
block|{
return|return
name|source
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|statString
specifier|public
name|String
name|statString
parameter_list|(
name|String
name|stat
parameter_list|)
block|{
return|return
name|stat
operator|+
literal|"("
operator|+
name|valueSourceString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
