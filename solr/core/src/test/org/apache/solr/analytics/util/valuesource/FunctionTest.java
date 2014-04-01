begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.util.valuesource
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
operator|.
name|valuesource
package|;
end_package
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|AbstractAnalyticsStatsTest
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
name|facet
operator|.
name|AbstractAnalyticsFacetTest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Appending"
block|,
literal|"Asserting"
block|}
argument_list|)
DECL|class|FunctionTest
specifier|public
class|class
name|FunctionTest
extends|extends
name|AbstractAnalyticsStatsTest
block|{
DECL|field|fileName
specifier|static
name|String
name|fileName
init|=
literal|"/analytics/requestFiles/functions.txt"
decl_stmt|;
DECL|field|INT
specifier|static
specifier|public
specifier|final
name|int
name|INT
init|=
literal|71
decl_stmt|;
DECL|field|LONG
specifier|static
specifier|public
specifier|final
name|int
name|LONG
init|=
literal|36
decl_stmt|;
DECL|field|FLOAT
specifier|static
specifier|public
specifier|final
name|int
name|FLOAT
init|=
literal|93
decl_stmt|;
DECL|field|DOUBLE
specifier|static
specifier|public
specifier|final
name|int
name|DOUBLE
init|=
literal|49
decl_stmt|;
DECL|field|DATE
specifier|static
specifier|public
specifier|final
name|int
name|DATE
init|=
literal|12
decl_stmt|;
DECL|field|STRING
specifier|static
specifier|public
specifier|final
name|int
name|STRING
init|=
literal|28
decl_stmt|;
DECL|field|NUM_LOOPS
specifier|static
specifier|public
specifier|final
name|int
name|NUM_LOOPS
init|=
literal|100
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-analytics.xml"
argument_list|)
expr_stmt|;
name|h
operator|.
name|update
argument_list|(
literal|"<delete><query>*:*</query></delete>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUM_LOOPS
condition|;
operator|++
name|j
control|)
block|{
name|int
name|i
init|=
name|j
operator|%
name|INT
operator|+
literal|1
decl_stmt|;
name|long
name|l
init|=
name|j
operator|%
name|LONG
operator|+
literal|1
decl_stmt|;
name|float
name|f
init|=
name|j
operator|%
name|FLOAT
operator|+
literal|1
decl_stmt|;
name|double
name|d
init|=
name|j
operator|%
name|DOUBLE
operator|+
literal|1
decl_stmt|;
name|double
name|d0
init|=
name|j
operator|%
name|DOUBLE
decl_stmt|;
name|String
name|dt
init|=
operator|(
literal|1800
operator|+
name|j
operator|%
name|DATE
operator|)
operator|+
literal|"-06-30T23:59:59Z"
decl_stmt|;
name|String
name|s
init|=
literal|"str"
operator|+
operator|(
name|j
operator|%
name|STRING
operator|)
decl_stmt|;
name|double
name|add_if
init|=
operator|(
name|double
operator|)
name|i
operator|+
name|f
decl_stmt|;
name|double
name|add_ldf
init|=
operator|(
name|double
operator|)
name|l
operator|+
name|d
operator|+
name|f
decl_stmt|;
name|double
name|mult_if
init|=
operator|(
name|double
operator|)
name|i
operator|*
name|f
decl_stmt|;
name|double
name|mult_ldf
init|=
operator|(
name|double
operator|)
name|l
operator|*
name|d
operator|*
name|f
decl_stmt|;
name|double
name|div_if
init|=
operator|(
name|double
operator|)
name|i
operator|/
name|f
decl_stmt|;
name|double
name|div_ld
init|=
operator|(
name|double
operator|)
name|l
operator|/
name|d
decl_stmt|;
name|double
name|pow_if
init|=
name|Math
operator|.
name|pow
argument_list|(
name|i
argument_list|,
name|f
argument_list|)
decl_stmt|;
name|double
name|pow_ld
init|=
name|Math
operator|.
name|pow
argument_list|(
name|l
argument_list|,
name|d
argument_list|)
decl_stmt|;
name|double
name|neg_i
init|=
operator|(
name|double
operator|)
name|i
operator|*
operator|-
literal|1
decl_stmt|;
name|double
name|neg_l
init|=
operator|(
name|double
operator|)
name|l
operator|*
operator|-
literal|1
decl_stmt|;
name|String
name|dm_2y
init|=
operator|(
literal|1802
operator|+
name|j
operator|%
name|DATE
operator|)
operator|+
literal|"-06-30T23:59:59Z"
decl_stmt|;
name|String
name|dm_2m
init|=
operator|(
literal|1800
operator|+
name|j
operator|%
name|DATE
operator|)
operator|+
literal|"-08-30T23:59:59Z"
decl_stmt|;
name|String
name|concat_first
init|=
literal|"this is the first"
operator|+
name|s
decl_stmt|;
name|String
name|concat_second
init|=
literal|"this is the second"
operator|+
name|s
decl_stmt|;
name|String
name|rev
init|=
operator|new
name|StringBuilder
argument_list|(
name|s
argument_list|)
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|AbstractAnalyticsFacetTest
operator|.
name|filter
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
operator|+
name|j
argument_list|,
literal|"int_id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"long_ld"
argument_list|,
literal|""
operator|+
name|l
argument_list|,
literal|"float_fd"
argument_list|,
literal|""
operator|+
name|f
argument_list|,
literal|"double_dd"
argument_list|,
literal|""
operator|+
name|d
argument_list|,
literal|"date_dtd"
argument_list|,
name|dt
argument_list|,
literal|"string_sd"
argument_list|,
name|s
argument_list|,
literal|"add_if_dd"
argument_list|,
literal|""
operator|+
name|add_if
argument_list|,
literal|"add_ldf_dd"
argument_list|,
literal|""
operator|+
name|add_ldf
argument_list|,
literal|"mult_if_dd"
argument_list|,
literal|""
operator|+
name|mult_if
argument_list|,
literal|"mult_ldf_dd"
argument_list|,
literal|""
operator|+
name|mult_ldf
argument_list|,
literal|"div_if_dd"
argument_list|,
literal|""
operator|+
name|div_if
argument_list|,
literal|"div_ld_dd"
argument_list|,
literal|""
operator|+
name|div_ld
argument_list|,
literal|"pow_if_dd"
argument_list|,
literal|""
operator|+
name|pow_if
argument_list|,
literal|"pow_ld_dd"
argument_list|,
literal|""
operator|+
name|pow_ld
argument_list|,
literal|"neg_i_dd"
argument_list|,
literal|""
operator|+
name|neg_i
argument_list|,
literal|"neg_l_dd"
argument_list|,
literal|""
operator|+
name|neg_l
argument_list|,
literal|"const_8_dd"
argument_list|,
literal|"8"
argument_list|,
literal|"const_10_dd"
argument_list|,
literal|"10"
argument_list|,
literal|"dm_2y_dtd"
argument_list|,
name|dm_2y
argument_list|,
literal|"dm_2m_dtd"
argument_list|,
name|dm_2m
argument_list|,
literal|"const_00_dtd"
argument_list|,
literal|"1800-06-30T23:59:59Z"
argument_list|,
literal|"const_04_dtd"
argument_list|,
literal|"1804-06-30T23:59:59Z"
argument_list|,
literal|"const_first_sd"
argument_list|,
literal|"this is the first"
argument_list|,
literal|"const_second_sd"
argument_list|,
literal|"this is the second"
argument_list|,
literal|"concat_first_sd"
argument_list|,
name|concat_first
argument_list|,
literal|"concat_second_sd"
argument_list|,
name|concat_second
argument_list|,
literal|"rev_sd"
argument_list|,
name|rev
argument_list|,
literal|"miss_dd"
argument_list|,
literal|""
operator|+
name|d0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// to have several segments
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|setResponse
argument_list|(
name|h
operator|.
name|query
argument_list|(
name|request
argument_list|(
name|fileToStringArr
argument_list|(
name|FunctionTest
operator|.
name|class
argument_list|,
name|fileName
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addTest
specifier|public
name|void
name|addTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// TODO checfk why asserted 2times
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ar"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
name|calculated
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|multiplyTest
specifier|public
name|void
name|multiplyTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"mr"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|divideTest
specifier|public
name|void
name|divideTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|Double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"dr"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|powerTest
specifier|public
name|void
name|powerTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"pr"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|negateTest
specifier|public
name|void
name|negateTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"nr"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|absoluteValueTest
specifier|public
name|void
name|absoluteValueTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"avr"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|constantNumberTest
specifier|public
name|void
name|constantNumberTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|result
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"cnr"
argument_list|,
literal|"sum"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|calculated
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"cnr"
argument_list|,
literal|"sumc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"cnr"
argument_list|,
literal|"mean"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"cnr"
argument_list|,
literal|"meanc"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|dateMathTest
specifier|public
name|void
name|dateMathTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"median"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|String
name|calculated
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"medianc"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"dmr"
argument_list|,
literal|"maxc"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|constantDateTest
specifier|public
name|void
name|constantDateTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"median"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|String
name|calculated
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"medianc"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cdr"
argument_list|,
literal|"maxc"
argument_list|,
name|VAL_TYPE
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|constantStringTest
specifier|public
name|void
name|constantStringTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"min"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|String
name|calculated
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"minc"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"csr"
argument_list|,
literal|"maxc"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|concatenateTest
specifier|public
name|void
name|concatenateTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"min"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|String
name|calculated
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"minc"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"cr"
argument_list|,
literal|"maxc"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reverseTest
specifier|public
name|void
name|reverseTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|result
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"min"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|String
name|calculated
init|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"minc"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|calculated
operator|=
operator|(
name|String
operator|)
name|getStatResult
argument_list|(
literal|"rr"
argument_list|,
literal|"maxc"
argument_list|,
name|VAL_TYPE
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
name|result
argument_list|,
name|calculated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|missingTest
specifier|public
name|void
name|missingTest
parameter_list|()
throws|throws
name|Exception
block|{
name|double
name|min
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ms"
argument_list|,
literal|"min"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|double
name|max
init|=
operator|(
name|Double
operator|)
name|getStatResult
argument_list|(
literal|"ms"
argument_list|,
literal|"max"
argument_list|,
name|VAL_TYPE
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
literal|48.0d
argument_list|,
name|max
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getRawResponse
argument_list|()
argument_list|,
literal|1.0d
argument_list|,
name|min
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
