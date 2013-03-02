begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|StorableField
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
name|SolrTestCaseJ4
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
name|core
operator|.
name|SolrCore
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
name|Ignore
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|Random
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
name|java
operator|.
name|util
operator|.
name|Currency
import|;
end_import
begin_comment
comment|/**  * Tests currency field type.  * @see #field  */
end_comment
begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Abstract base class with test methods"
argument_list|)
DECL|class|AbstractCurrencyFieldTest
specifier|public
specifier|abstract
class|class
name|AbstractCurrencyFieldTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|/**    * "Assumes" that the specified list of currency codes are    * supported in this JVM    */
DECL|method|assumeCurrencySupport
specifier|public
specifier|static
name|void
name|assumeCurrencySupport
parameter_list|(
name|String
modifier|...
name|codes
parameter_list|)
block|{
try|try
block|{
comment|// each JDK might have a diff list of supported currencies,
comment|// these are the ones needed for this test to work.
for|for
control|(
name|String
name|code
range|:
name|codes
control|)
block|{
name|Currency
name|obj
init|=
name|Currency
operator|.
name|getInstance
argument_list|(
name|code
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|code
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
name|assumeCurrencySupport
argument_list|(
literal|"USD"
argument_list|,
literal|"EUR"
argument_list|,
literal|"MXN"
argument_list|,
literal|"GBP"
argument_list|,
literal|"JPY"
argument_list|,
literal|"NOK"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** The field name to use in all tests */
DECL|method|field
specifier|public
specifier|abstract
name|String
name|field
parameter_list|()
function_decl|;
annotation|@
name|Test
DECL|method|testCurrencySchema
specifier|public
name|void
name|testCurrencySchema
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|amount
init|=
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|amount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|amount
operator|.
name|isPolyField
argument_list|()
argument_list|)
expr_stmt|;
name|SchemaField
index|[]
name|dynFields
init|=
name|schema
operator|.
name|getDynamicFieldPrototypes
argument_list|()
decl_stmt|;
name|boolean
name|seenCurrency
init|=
literal|false
decl_stmt|;
name|boolean
name|seenAmount
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SchemaField
name|dynField
range|:
name|dynFields
control|)
block|{
if|if
condition|(
name|dynField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
name|CurrencyField
operator|.
name|FIELD_SUFFIX_CURRENCY
argument_list|)
condition|)
block|{
name|seenCurrency
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|dynField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"*"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
name|CurrencyField
operator|.
name|FIELD_SUFFIX_AMOUNT_RAW
argument_list|)
condition|)
block|{
name|seenAmount
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Didn't find the expected currency code dynamic field"
argument_list|,
name|seenCurrency
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Didn't find the expected value dynamic field"
argument_list|,
name|seenAmount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCurrencyFieldType
specifier|public
name|void
name|testCurrencyFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|amount
init|=
name|schema
operator|.
name|getField
argument_list|(
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|amount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
argument_list|()
operator|+
literal|" is not a poly field"
argument_list|,
name|amount
operator|.
name|isPolyField
argument_list|()
argument_list|)
expr_stmt|;
name|FieldType
name|tmp
init|=
name|amount
operator|.
name|getType
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|tmp
operator|instanceof
name|CurrencyField
argument_list|)
expr_stmt|;
name|String
name|currencyValue
init|=
literal|"1.50,EUR"
decl_stmt|;
name|List
argument_list|<
name|StorableField
argument_list|>
name|fields
init|=
name|amount
operator|.
name|createFields
argument_list|(
name|currencyValue
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// First field is currency code, second is value, third is stored.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|hasValue
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|readerValue
argument_list|()
operator|!=
literal|null
operator|||
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|!=
literal|null
operator|||
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Doesn't have a value: "
operator|+
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|hasValue
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"string"
argument_list|)
operator|.
name|toExternal
argument_list|(
name|fields
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|"1.50,EUR"
argument_list|)
expr_stmt|;
comment|// A few tests on the provider directly
name|ExchangeRateProvider
name|p
init|=
operator|(
operator|(
name|CurrencyField
operator|)
name|tmp
operator|)
operator|.
name|getProvider
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|availableCurrencies
init|=
name|p
operator|.
name|listAvailableCurrencies
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|availableCurrencies
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|p
operator|.
name|reload
argument_list|()
operator|==
literal|true
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getExchangeRate
argument_list|(
literal|"USD"
argument_list|,
literal|"EUR"
argument_list|)
operator|==
literal|2.5
operator|)
assert|;
block|}
annotation|@
name|Test
DECL|method|testMockExchangeRateProvider
specifier|public
name|void
name|testMockExchangeRateProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|amount
init|=
name|schema
operator|.
name|getField
argument_list|(
literal|"mock_amount"
argument_list|)
decl_stmt|;
comment|// A few tests on the provider directly
name|ExchangeRateProvider
name|p
init|=
operator|(
operator|(
name|CurrencyField
operator|)
name|amount
operator|.
name|getType
argument_list|()
operator|)
operator|.
name|getProvider
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|availableCurrencies
init|=
name|p
operator|.
name|listAvailableCurrencies
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|availableCurrencies
operator|.
name|size
argument_list|()
operator|==
literal|3
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|reload
argument_list|()
operator|==
literal|true
operator|)
assert|;
assert|assert
operator|(
name|p
operator|.
name|getExchangeRate
argument_list|(
literal|"USD"
argument_list|,
literal|"EUR"
argument_list|)
operator|==
literal|0.8
operator|)
assert|;
block|}
annotation|@
name|Test
DECL|method|testCurrencyRangeSearch
specifier|public
name|void
name|testCurrencyRangeSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
specifier|final
name|int
name|emptyDocs
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
decl_stmt|;
comment|// times 2
specifier|final
name|int
name|negDocs
init|=
name|atLeast
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"0,USD"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 0
comment|// lots of docs w/o values
for|for
control|(
name|int
name|i
init|=
literal|100
init|;
name|i
operator|<=
literal|100
operator|+
name|emptyDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// docs with values in ranges we'll query
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|field
argument_list|()
argument_list|,
name|i
operator|+
literal|",USD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// more docs w/o values
for|for
control|(
name|int
name|i
init|=
literal|500
init|;
name|i
operator|<=
literal|500
operator|+
name|emptyDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// some negative values
for|for
control|(
name|int
name|i
init|=
operator|-
literal|100
init|;
name|i
operator|>
operator|-
literal|100
operator|-
name|negDocs
condition|;
name|i
operator|--
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|field
argument_list|()
argument_list|,
name|i
operator|+
literal|",USD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"40"
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"0,USD"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 0
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[2.00,USD TO 5.00,USD]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[0.50,USD TO 1.00,USD]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[24.00,USD TO 25.00,USD]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// "GBP" currency code is 1/2 of a USD dollar, for testing.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[0.50,GBP TO 1.00,GBP]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
comment|// "EUR" currency code is 2.5X of a USD dollar, for testing.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[24.00,EUR TO 25.00,EUR]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// Slight asymmetric rate should work.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[24.99,EUR TO 25.01,EUR]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// Open ended ranges without currency
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
operator|(
literal|2
operator|+
literal|10
operator|+
name|negDocs
operator|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
comment|// Open ended ranges with currency
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[*,EUR TO *,EUR]"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
operator|(
literal|2
operator|+
literal|10
operator|+
name|negDocs
operator|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
comment|// Open ended start range without currency
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[* TO 5,USD]"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
operator|(
literal|2
operator|+
literal|5
operator|+
name|negDocs
operator|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
comment|// Open ended start range with currency (currency for the * won't matter)
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[*,USD TO 5,USD]"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
operator|(
literal|2
operator|+
literal|5
operator|+
name|negDocs
operator|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
comment|// Open ended end range
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[3 TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='8']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBogusCurrency
specifier|public
name|void
name|testBogusCurrency
parameter_list|()
throws|throws
name|Exception
block|{
name|ignoreException
argument_list|(
literal|"HOSS"
argument_list|)
expr_stmt|;
comment|// bogus currency
name|assertQEx
argument_list|(
literal|"Expected exception for invalid currency"
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":[3,HOSS TO *]"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCurrencyPointQuery
specifier|public
name|void
name|testCurrencyPointQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|1
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"10.00,USD"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|2
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"15.00,MXN"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":10.00,USD"
argument_list|)
argument_list|,
literal|"//int[@name='id']='1'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":9.99,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":10.01,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":15.00,MXN"
argument_list|)
argument_list|,
literal|"//int[@name='id']='2'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":7.50,USD"
argument_list|)
argument_list|,
literal|"//int[@name='id']='2'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":7.49,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":7.51,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
DECL|method|testPerformance
specifier|public
name|void
name|testPerformance
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|initDocs
init|=
literal|200000
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|initDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|field
argument_list|()
argument_list|,
operator|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1.00
operator|)
operator|+
literal|",USD"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lower
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1.00
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":["
operator|+
name|lower
operator|+
literal|",USD TO "
operator|+
operator|(
name|lower
operator|+
literal|10.00
operator|)
operator|+
literal|",USD]"
argument_list|)
argument_list|,
literal|"//*"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":["
operator|+
name|lower
operator|+
literal|",EUR TO "
operator|+
operator|(
name|lower
operator|+
literal|10.00
operator|)
operator|+
literal|",EUR]"
argument_list|)
argument_list|,
literal|"//*"
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
literal|3
condition|;
name|j
operator|++
control|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lower
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1.00
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":["
operator|+
name|lower
operator|+
literal|",USD TO "
operator|+
operator|(
name|lower
operator|+
operator|(
literal|9.99
operator|-
operator|(
name|j
operator|*
literal|0.01
operator|)
operator|)
operator|)
operator|+
literal|",USD]"
argument_list|)
argument_list|,
literal|"//*"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---"
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
literal|3
condition|;
name|j
operator|++
control|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|double
name|lower
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1.00
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|field
argument_list|()
operator|+
literal|":["
operator|+
name|lower
operator|+
literal|",EUR TO "
operator|+
operator|(
name|lower
operator|+
operator|(
literal|9.99
operator|-
operator|(
name|j
operator|*
literal|0.01
operator|)
operator|)
operator|)
operator|+
literal|",EUR]"
argument_list|)
argument_list|,
literal|"//*"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCurrencySort
specifier|public
name|void
name|testCurrencySort
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|1
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"10.00,USD"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|2
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"15.00,EUR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|3
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"7.00,EUR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|4
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"6.00,GBP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
literal|5
argument_list|,
name|field
argument_list|()
argument_list|,
literal|"2.00,GBP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|field
argument_list|()
operator|+
literal|" desc"
argument_list|,
literal|"limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//int[@name='id']='4'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|field
argument_list|()
operator|+
literal|" asc"
argument_list|,
literal|"limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//int[@name='id']='3'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMockFieldType
specifier|public
name|void
name|testMockFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"mock_amount"
argument_list|,
literal|"1.00,USD"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"mock_amount"
argument_list|,
literal|"1.00,EUR"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"mock_amount"
argument_list|,
literal|"1.00,NOK"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"mock_amount:5.0,NOK"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id']='1'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"mock_amount:1.2,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id']='2'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"mock_amount:0.2,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id']='3'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"mock_amount:99,USD"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
