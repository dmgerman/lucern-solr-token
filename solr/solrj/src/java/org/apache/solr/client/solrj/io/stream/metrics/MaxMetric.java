begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.metrics
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
operator|.
name|stream
operator|.
name|metrics
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
operator|.
name|Tuple
import|;
end_import
begin_class
DECL|class|MaxMetric
specifier|public
class|class
name|MaxMetric
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
DECL|field|MAX
specifier|public
specifier|static
specifier|final
name|String
name|MAX
init|=
literal|"max"
decl_stmt|;
DECL|field|longMax
specifier|private
name|long
name|longMax
init|=
operator|-
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|doubleMax
specifier|private
name|double
name|doubleMax
init|=
operator|-
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|column
specifier|private
name|String
name|column
decl_stmt|;
DECL|method|MaxMetric
specifier|public
name|MaxMetric
parameter_list|(
name|String
name|column
parameter_list|)
block|{
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"max("
operator|+
name|column
operator|+
literal|")"
return|;
block|}
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|longMax
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
return|return
name|doubleMax
return|;
block|}
else|else
block|{
return|return
name|longMax
return|;
block|}
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
name|Object
name|o
init|=
name|tuple
operator|.
name|get
argument_list|(
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Double
condition|)
block|{
name|double
name|d
init|=
operator|(
name|double
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|d
operator|>
name|doubleMax
condition|)
block|{
name|doubleMax
operator|=
name|d
expr_stmt|;
block|}
block|}
else|else
block|{
name|long
name|l
init|=
operator|(
name|long
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|l
operator|>
name|longMax
condition|)
block|{
name|longMax
operator|=
name|l
expr_stmt|;
block|}
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
name|MaxMetric
argument_list|(
name|column
argument_list|)
return|;
block|}
block|}
end_class
end_unit