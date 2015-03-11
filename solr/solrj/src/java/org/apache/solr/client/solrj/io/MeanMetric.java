begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_class
DECL|class|MeanMetric
specifier|public
class|class
name|MeanMetric
implements|implements
name|Metric
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|SUM
specifier|public
specifier|static
specifier|final
name|String
name|SUM
init|=
literal|"sum"
decl_stmt|;
DECL|field|COUNT
specifier|public
specifier|static
specifier|final
name|String
name|COUNT
init|=
literal|"count"
decl_stmt|;
DECL|field|MEAN
specifier|public
specifier|static
specifier|final
name|String
name|MEAN
init|=
literal|"mean"
decl_stmt|;
DECL|field|column
specifier|private
name|String
name|column
decl_stmt|;
DECL|field|isDouble
specifier|private
name|boolean
name|isDouble
decl_stmt|;
DECL|field|doubleSum
specifier|private
name|double
name|doubleSum
decl_stmt|;
DECL|field|longSum
specifier|private
name|long
name|longSum
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|method|MeanMetric
specifier|public
name|MeanMetric
parameter_list|(
name|String
name|column
parameter_list|,
name|boolean
name|isDouble
parameter_list|)
block|{
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
name|this
operator|.
name|isDouble
operator|=
name|isDouble
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"mean:"
operator|+
name|column
return|;
block|}
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
block|{
operator|++
name|count
expr_stmt|;
if|if
condition|(
name|isDouble
condition|)
block|{
name|Double
name|d
init|=
operator|(
name|Double
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|column
argument_list|)
decl_stmt|;
name|doubleSum
operator|+=
name|d
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Long
name|l
init|=
operator|(
name|Long
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|column
argument_list|)
decl_stmt|;
name|longSum
operator|+=
name|l
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newInstance
specifier|public
name|Metric
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MeanMetric
argument_list|(
name|column
argument_list|,
name|isDouble
argument_list|)
return|;
block|}
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
block|{
name|double
name|dcount
init|=
operator|(
name|double
operator|)
name|count
decl_stmt|;
if|if
condition|(
name|isDouble
condition|)
block|{
name|double
name|ave
init|=
name|doubleSum
operator|/
name|dcount
decl_stmt|;
return|return
name|ave
return|;
block|}
else|else
block|{
name|double
name|ave
init|=
name|longSum
operator|/
name|dcount
decl_stmt|;
return|return
name|ave
return|;
block|}
block|}
DECL|method|metricValues
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|metricValues
parameter_list|()
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|double
name|dcount
init|=
operator|(
name|double
operator|)
name|count
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|COUNT
argument_list|,
name|dcount
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDouble
condition|)
block|{
name|double
name|ave
init|=
name|doubleSum
operator|/
name|dcount
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|MEAN
argument_list|,
name|ave
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|SUM
argument_list|,
name|doubleSum
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|double
name|ave
init|=
name|longSum
operator|/
name|dcount
decl_stmt|;
name|doubleSum
operator|=
operator|(
name|double
operator|)
name|longSum
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|MEAN
argument_list|,
name|ave
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|SUM
argument_list|,
name|doubleSum
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|metricValues
parameter_list|)
block|{
name|double
name|dcount
init|=
name|metricValues
operator|.
name|get
argument_list|(
name|COUNT
argument_list|)
decl_stmt|;
name|count
operator|+=
operator|(
name|long
operator|)
name|dcount
expr_stmt|;
if|if
condition|(
name|isDouble
condition|)
block|{
name|double
name|dsum
init|=
name|metricValues
operator|.
name|get
argument_list|(
name|SUM
argument_list|)
decl_stmt|;
name|doubleSum
operator|+=
name|dsum
expr_stmt|;
block|}
else|else
block|{
name|double
name|dsum
init|=
name|metricValues
operator|.
name|get
argument_list|(
name|SUM
argument_list|)
decl_stmt|;
name|longSum
operator|+=
operator|(
name|long
operator|)
name|dsum
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit