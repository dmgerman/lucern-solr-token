begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
begin_comment
comment|/** 2/11/2009 - Moved out of StatsComponent to allow open access to UnInvertedField  * StatsValues is a utility to accumulate statistics on a set of values  *  *</p>  * @see org.apache.solr.handler.component.StatsComponent  * */
end_comment
begin_class
DECL|class|StatsValues
specifier|public
class|class
name|StatsValues
block|{
DECL|field|FACETS
specifier|private
specifier|static
specifier|final
name|String
name|FACETS
init|=
literal|"facets"
decl_stmt|;
DECL|field|min
name|double
name|min
decl_stmt|;
DECL|field|max
name|double
name|max
decl_stmt|;
DECL|field|sum
name|double
name|sum
decl_stmt|;
DECL|field|sumOfSquares
name|double
name|sumOfSquares
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|missing
name|long
name|missing
decl_stmt|;
comment|// facetField   facetValue
DECL|field|facets
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|>
name|facets
decl_stmt|;
DECL|method|StatsValues
specifier|public
name|StatsValues
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|accumulate
specifier|public
name|void
name|accumulate
parameter_list|(
name|NamedList
name|stv
parameter_list|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
operator|(
name|Double
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"min"
argument_list|)
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
operator|(
name|Double
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"max"
argument_list|)
argument_list|)
expr_stmt|;
name|sum
operator|+=
operator|(
name|Double
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"sum"
argument_list|)
expr_stmt|;
name|count
operator|+=
operator|(
name|Long
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"count"
argument_list|)
expr_stmt|;
name|missing
operator|+=
operator|(
name|Long
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"missing"
argument_list|)
expr_stmt|;
name|sumOfSquares
operator|+=
operator|(
name|Double
operator|)
name|stv
operator|.
name|get
argument_list|(
literal|"sumOfSquares"
argument_list|)
expr_stmt|;
name|NamedList
name|f
init|=
operator|(
name|NamedList
operator|)
name|stv
operator|.
name|get
argument_list|(
name|FACETS
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|facets
operator|==
literal|null
condition|)
block|{
name|facets
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|f
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
name|f
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NamedList
name|vals
init|=
operator|(
name|NamedList
operator|)
name|f
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|addTo
init|=
name|facets
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|addTo
operator|==
literal|null
condition|)
block|{
name|addTo
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|()
expr_stmt|;
name|facets
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|addTo
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|vals
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|val
init|=
name|f
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|StatsValues
name|vvals
init|=
name|addTo
operator|.
name|get
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|vvals
operator|==
literal|null
condition|)
block|{
name|vvals
operator|=
operator|new
name|StatsValues
argument_list|()
expr_stmt|;
name|addTo
operator|.
name|put
argument_list|(
name|val
argument_list|,
name|vvals
argument_list|)
expr_stmt|;
block|}
name|vvals
operator|.
name|accumulate
argument_list|(
operator|(
name|NamedList
operator|)
name|f
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|accumulate
specifier|public
name|void
name|accumulate
parameter_list|(
name|double
name|v
parameter_list|)
block|{
name|sumOfSquares
operator|+=
operator|(
name|v
operator|*
name|v
operator|)
expr_stmt|;
comment|// for std deviation
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|v
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
DECL|method|accumulate
specifier|public
name|void
name|accumulate
parameter_list|(
name|double
name|v
parameter_list|,
name|int
name|c
parameter_list|)
block|{
name|sumOfSquares
operator|+=
operator|(
name|v
operator|*
name|v
operator|*
name|c
operator|)
expr_stmt|;
comment|// for std deviation
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|v
operator|*
name|c
expr_stmt|;
name|count
operator|+=
name|c
expr_stmt|;
block|}
DECL|method|addMissing
specifier|public
name|void
name|addMissing
parameter_list|(
name|int
name|c
parameter_list|)
block|{
name|missing
operator|+=
name|c
expr_stmt|;
block|}
DECL|method|getAverage
specifier|public
name|double
name|getAverage
parameter_list|()
block|{
return|return
name|sum
operator|/
name|count
return|;
block|}
DECL|method|getStandardDeviation
specifier|public
name|double
name|getStandardDeviation
parameter_list|()
block|{
if|if
condition|(
name|count
operator|<=
literal|1.0D
condition|)
return|return
literal|0.0D
return|;
return|return
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
operator|(
name|count
operator|*
name|sumOfSquares
operator|)
operator|-
operator|(
name|sum
operator|*
name|sum
operator|)
operator|)
operator|/
operator|(
name|count
operator|*
operator|(
name|count
operator|-
literal|1.0D
operator|)
operator|)
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
name|count
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|min
operator|=
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
name|max
operator|=
operator|-
literal|1.0
operator|*
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
name|sum
operator|=
name|count
operator|=
name|missing
operator|=
literal|0
expr_stmt|;
name|sumOfSquares
operator|=
literal|0
expr_stmt|;
name|facets
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getStatsValues
specifier|public
name|NamedList
argument_list|<
name|?
argument_list|>
name|getStatsValues
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"min"
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"max"
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"sum"
argument_list|,
name|sum
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"missing"
argument_list|,
name|missing
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"sumOfSquares"
argument_list|,
name|sumOfSquares
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"mean"
argument_list|,
name|getAverage
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
literal|"stddev"
argument_list|,
name|getStandardDeviation
argument_list|()
argument_list|)
expr_stmt|;
comment|// add the facet stats
if|if
condition|(
name|facets
operator|!=
literal|null
operator|&&
name|facets
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
name|nl
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
argument_list|>
name|entry
range|:
name|facets
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
name|nl2
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|NamedList
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|nl2
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|e2
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|nl2
operator|.
name|add
argument_list|(
name|e2
operator|.
name|getKey
argument_list|()
argument_list|,
name|e2
operator|.
name|getValue
argument_list|()
operator|.
name|getStatsValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|res
operator|.
name|add
argument_list|(
name|FACETS
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
