begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Set
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
name|params
operator|.
name|FacetParams
operator|.
name|FacetRangeInclude
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
name|params
operator|.
name|FacetParams
operator|.
name|FacetRangeOther
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
name|Lists
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
name|Sets
import|;
end_import
begin_interface
DECL|interface|AnalyticsParams
specifier|public
interface|interface
name|AnalyticsParams
block|{
comment|// Full length Analytics Params
DECL|field|ANALYTICS
specifier|public
specifier|static
specifier|final
name|String
name|ANALYTICS
init|=
literal|"olap"
decl_stmt|;
DECL|field|REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|REQUEST
init|=
literal|"o|olap"
decl_stmt|;
DECL|field|EXPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|EXPRESSION
init|=
literal|"s|stat|statistic"
decl_stmt|;
DECL|field|HIDDEN_EXPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|HIDDEN_EXPRESSION
init|=
literal|"hs|hiddenstat|hiddenstatistic"
decl_stmt|;
DECL|field|FIELD_FACET
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_FACET
init|=
literal|"ff|fieldfacet"
decl_stmt|;
DECL|field|LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|LIMIT
init|=
literal|"l|limit"
decl_stmt|;
DECL|field|OFFSET
specifier|public
specifier|static
specifier|final
name|String
name|OFFSET
init|=
literal|"off|offset"
decl_stmt|;
DECL|field|HIDDEN
specifier|public
specifier|static
specifier|final
name|String
name|HIDDEN
init|=
literal|"h|hidden"
decl_stmt|;
DECL|field|SHOW_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|SHOW_MISSING
init|=
literal|"sm|showmissing"
decl_stmt|;
DECL|field|SORT_STATISTIC
specifier|public
specifier|static
specifier|final
name|String
name|SORT_STATISTIC
init|=
literal|"ss|sortstat|sortstatistic"
decl_stmt|;
DECL|field|SORT_DIRECTION
specifier|public
specifier|static
specifier|final
name|String
name|SORT_DIRECTION
init|=
literal|"sd|sortdirection"
decl_stmt|;
DECL|field|RANGE_FACET
specifier|public
specifier|static
specifier|final
name|String
name|RANGE_FACET
init|=
literal|"rf|rangefacet"
decl_stmt|;
DECL|field|START
specifier|public
specifier|static
specifier|final
name|String
name|START
init|=
literal|"st|start"
decl_stmt|;
DECL|field|END
specifier|public
specifier|static
specifier|final
name|String
name|END
init|=
literal|"e|end"
decl_stmt|;
DECL|field|GAP
specifier|public
specifier|static
specifier|final
name|String
name|GAP
init|=
literal|"g|gap"
decl_stmt|;
DECL|field|HARDEND
specifier|public
specifier|static
specifier|final
name|String
name|HARDEND
init|=
literal|"he|hardend"
decl_stmt|;
DECL|field|INCLUDE_BOUNDARY
specifier|public
specifier|static
specifier|final
name|String
name|INCLUDE_BOUNDARY
init|=
literal|"ib|includebound"
decl_stmt|;
DECL|field|OTHER_RANGE
specifier|public
specifier|static
specifier|final
name|String
name|OTHER_RANGE
init|=
literal|"or|otherrange"
decl_stmt|;
DECL|field|QUERY_FACET
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_FACET
init|=
literal|"qf|queryfacet"
decl_stmt|;
DECL|field|DEPENDENCY
specifier|public
specifier|static
specifier|final
name|String
name|DEPENDENCY
init|=
literal|"d|dependecy"
decl_stmt|;
DECL|field|QUERY
specifier|public
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"q|query"
decl_stmt|;
comment|//Defaults
DECL|field|DEFAULT_ABBREVIATE_PREFIX
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ABBREVIATE_PREFIX
init|=
literal|true
decl_stmt|;
DECL|field|DEFAULT_SORT_DIRECTION
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SORT_DIRECTION
init|=
literal|"ascending"
decl_stmt|;
DECL|field|DEFAULT_LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LIMIT
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DEFAULT_HIDDEN
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_HIDDEN
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_HARDEND
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_HARDEND
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_SHOW_MISSING
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_SHOW_MISSING
init|=
literal|false
decl_stmt|;
DECL|field|DEFAULT_INCLUDE
specifier|public
specifier|static
specifier|final
name|FacetRangeInclude
name|DEFAULT_INCLUDE
init|=
name|FacetRangeInclude
operator|.
name|LOWER
decl_stmt|;
DECL|field|DEFAULT_OTHER
specifier|public
specifier|static
specifier|final
name|FacetRangeOther
name|DEFAULT_OTHER
init|=
name|FacetRangeOther
operator|.
name|NONE
decl_stmt|;
comment|// Statistic Function Names (Cannot share names with ValueSource& Expression Functions)
DECL|field|STAT_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|STAT_COUNT
init|=
literal|"count"
decl_stmt|;
DECL|field|STAT_MISSING
specifier|public
specifier|static
specifier|final
name|String
name|STAT_MISSING
init|=
literal|"missing"
decl_stmt|;
DECL|field|STAT_SUM
specifier|public
specifier|static
specifier|final
name|String
name|STAT_SUM
init|=
literal|"sum"
decl_stmt|;
DECL|field|STAT_SUM_OF_SQUARES
specifier|public
specifier|static
specifier|final
name|String
name|STAT_SUM_OF_SQUARES
init|=
literal|"sumofsquares"
decl_stmt|;
DECL|field|STAT_STANDARD_DEVIATION
specifier|public
specifier|static
specifier|final
name|String
name|STAT_STANDARD_DEVIATION
init|=
literal|"stddev"
decl_stmt|;
DECL|field|STAT_MEAN
specifier|public
specifier|static
specifier|final
name|String
name|STAT_MEAN
init|=
literal|"mean"
decl_stmt|;
DECL|field|STAT_UNIQUE
specifier|public
specifier|static
specifier|final
name|String
name|STAT_UNIQUE
init|=
literal|"unique"
decl_stmt|;
DECL|field|STAT_MEDIAN
specifier|public
specifier|static
specifier|final
name|String
name|STAT_MEDIAN
init|=
literal|"median"
decl_stmt|;
DECL|field|STAT_PERCENTILE
specifier|public
specifier|static
specifier|final
name|String
name|STAT_PERCENTILE
init|=
literal|"percentile"
decl_stmt|;
DECL|field|STAT_MIN
specifier|public
specifier|static
specifier|final
name|String
name|STAT_MIN
init|=
literal|"min"
decl_stmt|;
DECL|field|STAT_MAX
specifier|public
specifier|static
specifier|final
name|String
name|STAT_MAX
init|=
literal|"max"
decl_stmt|;
DECL|field|ALL_STAT_LIST
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ALL_STAT_LIST
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|STAT_COUNT
argument_list|,
name|STAT_MISSING
argument_list|,
name|STAT_SUM
argument_list|,
name|STAT_SUM_OF_SQUARES
argument_list|,
name|STAT_STANDARD_DEVIATION
argument_list|,
name|STAT_MEAN
argument_list|,
name|STAT_UNIQUE
argument_list|,
name|STAT_MEDIAN
argument_list|,
name|STAT_PERCENTILE
argument_list|,
name|STAT_MIN
argument_list|,
name|STAT_MAX
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|ALL_STAT_SET
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ALL_STAT_SET
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|Sets
operator|.
name|newLinkedHashSet
argument_list|(
name|ALL_STAT_LIST
argument_list|)
argument_list|)
decl_stmt|;
comment|// ValueSource& Expression Function Names (Cannot share names with Statistic Functions)
comment|// No specific type
DECL|field|FILTER
specifier|final
specifier|static
name|String
name|FILTER
init|=
literal|"filter"
decl_stmt|;
DECL|field|RESULT
specifier|final
specifier|static
name|String
name|RESULT
init|=
literal|"result"
decl_stmt|;
DECL|field|QUERY_RESULT
specifier|final
specifier|static
name|String
name|QUERY_RESULT
init|=
literal|"qresult"
decl_stmt|;
comment|// Numbers
DECL|field|CONSTANT_NUMBER
specifier|final
specifier|static
name|String
name|CONSTANT_NUMBER
init|=
literal|"const_num"
decl_stmt|;
DECL|field|NEGATE
specifier|final
specifier|static
name|String
name|NEGATE
init|=
literal|"neg"
decl_stmt|;
DECL|field|ABSOLUTE_VALUE
specifier|final
specifier|static
name|String
name|ABSOLUTE_VALUE
init|=
literal|"abs"
decl_stmt|;
DECL|field|LOG
specifier|final
specifier|static
name|String
name|LOG
init|=
literal|"log"
decl_stmt|;
DECL|field|ADD
specifier|final
specifier|static
name|String
name|ADD
init|=
literal|"add"
decl_stmt|;
DECL|field|MULTIPLY
specifier|final
specifier|static
name|String
name|MULTIPLY
init|=
literal|"mult"
decl_stmt|;
DECL|field|DIVIDE
specifier|final
specifier|static
name|String
name|DIVIDE
init|=
literal|"div"
decl_stmt|;
DECL|field|POWER
specifier|final
specifier|static
name|String
name|POWER
init|=
literal|"pow"
decl_stmt|;
DECL|field|NUMERIC_OPERATION_SET
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|NUMERIC_OPERATION_SET
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|Sets
operator|.
name|newLinkedHashSet
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|CONSTANT_NUMBER
argument_list|,
name|NEGATE
argument_list|,
name|ABSOLUTE_VALUE
argument_list|,
name|LOG
argument_list|,
name|ADD
argument_list|,
name|MULTIPLY
argument_list|,
name|DIVIDE
argument_list|,
name|POWER
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Dates
DECL|field|CONSTANT_DATE
specifier|final
specifier|static
name|String
name|CONSTANT_DATE
init|=
literal|"const_date"
decl_stmt|;
DECL|field|DATE_MATH
specifier|final
specifier|static
name|String
name|DATE_MATH
init|=
literal|"date_math"
decl_stmt|;
DECL|field|DATE_OPERATION_SET
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|DATE_OPERATION_SET
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|Sets
operator|.
name|newLinkedHashSet
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|CONSTANT_DATE
argument_list|,
name|DATE_MATH
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//Strings
DECL|field|CONSTANT_STRING
specifier|final
specifier|static
name|String
name|CONSTANT_STRING
init|=
literal|"const_str"
decl_stmt|;
DECL|field|REVERSE
specifier|final
specifier|static
name|String
name|REVERSE
init|=
literal|"rev"
decl_stmt|;
DECL|field|CONCATENATE
specifier|final
specifier|static
name|String
name|CONCATENATE
init|=
literal|"concat"
decl_stmt|;
DECL|field|STRING_OPERATION_SET
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|STRING_OPERATION_SET
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|Sets
operator|.
name|newLinkedHashSet
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|CONSTANT_STRING
argument_list|,
name|REVERSE
argument_list|,
name|CONCATENATE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Field Source Wrappers
block|}
end_interface
end_unit
