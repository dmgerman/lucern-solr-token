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
name|util
operator|.
name|mutable
operator|.
name|MutableValue
import|;
end_import
begin_comment
comment|/**  *<code>AbstractDelegationStatsCollector</code> objects wrap other StatsCollectors.  * While they compute their own statistics they pass along all inputs and requests  * to the delegates as well.  */
end_comment
begin_class
DECL|class|AbstractDelegatingStatsCollector
specifier|public
specifier|abstract
class|class
name|AbstractDelegatingStatsCollector
implements|implements
name|StatsCollector
block|{
DECL|field|delegate
specifier|protected
specifier|final
name|StatsCollector
name|delegate
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
DECL|field|value
name|MutableValue
name|value
decl_stmt|;
DECL|field|function
name|FunctionValues
name|function
decl_stmt|;
comment|/**    * @param delegate The delegate computing statistics on the same set of values.    */
DECL|method|AbstractDelegatingStatsCollector
specifier|public
name|AbstractDelegatingStatsCollector
parameter_list|(
name|StatsCollector
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|statsList
operator|=
name|delegate
operator|.
name|getStatsList
argument_list|()
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|value
operator|=
name|getValue
argument_list|()
expr_stmt|;
name|function
operator|=
name|getFunction
argument_list|()
expr_stmt|;
block|}
DECL|method|delegate
specifier|public
name|StatsCollector
name|delegate
parameter_list|()
block|{
return|return
name|delegate
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
DECL|method|getValue
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|getFunction
specifier|public
name|FunctionValues
name|getFunction
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getFunction
argument_list|()
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
name|delegate
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|valueSourceString
specifier|public
name|String
name|valueSourceString
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|valueSourceString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
