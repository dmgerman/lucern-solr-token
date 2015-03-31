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
name|request
operator|.
name|SolrQueryRequest
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
DECL|class|EnumFieldTest
specifier|public
class|class
name|EnumFieldTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|FIELD_NAME
specifier|private
specifier|final
name|String
name|FIELD_NAME
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"severity"
else|:
literal|"severity_dv"
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
literal|"solrconfig-minimal.xml"
argument_list|,
literal|"schema-enums.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnumSchema
specifier|public
name|void
name|testEnumSchema
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
name|getLatestSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|enumField
init|=
name|schema
operator|.
name|getField
argument_list|(
name|FIELD_NAME
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|enumField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnumRangeSearch
specifier|public
name|void
name|testEnumRangeSearch
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
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Not Available"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Not Available"
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
name|FIELD_NAME
argument_list|,
literal|"Not Available"
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
name|FIELD_NAME
argument_list|,
literal|"Not Available"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Not Available"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Low"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Low"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Low"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Low"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Medium"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Medium"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Medium"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"High"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"High"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"14"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Critical"
argument_list|)
argument_list|)
expr_stmt|;
comment|// two docs w/o values
for|for
control|(
name|int
name|i
init|=
literal|20
init|;
name|i
operator|<=
literal|21
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
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//range with the same value
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[\"Not Available\" TO \"Not Available\"]"
argument_list|)
argument_list|,
literal|"//*[@numFound='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[\"Not Available\" TO Critical]"
argument_list|)
argument_list|,
literal|"//*[@numFound='15']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[Low TO High]"
argument_list|)
argument_list|,
literal|"//*[@numFound='9']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[High TO Low]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|//with int values
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[High TO 4]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[3 TO Critical]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[3 TO 4]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
comment|//exclusive
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":{Low TO High]"
argument_list|)
argument_list|,
literal|"//*[@numFound='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[Low TO High}"
argument_list|)
argument_list|,
literal|"//*[@numFound='7']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":{Low TO High}"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
comment|//all docs
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='17']"
argument_list|)
expr_stmt|;
comment|//all docs with values
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='15']"
argument_list|)
expr_stmt|;
comment|//empty docs
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
literal|"-"
operator|+
name|FIELD_NAME
operator|+
literal|":[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBogusEnumSearch
specifier|public
name|void
name|testBogusEnumSearch
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
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Not Available"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Low"
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
name|FIELD_NAME
argument_list|,
literal|"Medium"
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
name|FIELD_NAME
argument_list|,
literal|"High"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Critical"
argument_list|)
argument_list|)
expr_stmt|;
comment|// two docs w/o values
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<=
literal|9
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
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|eoe
init|=
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":bla"
argument_list|)
decl_stmt|;
name|String
name|eoe1
init|=
name|eoe
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":bla"
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
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":7"
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
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":\"-3\""
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBogusEnumIndexing
specifier|public
name|void
name|testBogusEnumIndexing
parameter_list|()
throws|throws
name|Exception
block|{
name|ignoreException
argument_list|(
literal|"Unknown value for enum field: blabla"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"Unknown value for enum field: 10"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"Unknown value for enum field: -4"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertFailedU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"blabla"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFailedU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFailedU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"-4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKnownIntegerEnumIndexing
specifier|public
name|void
name|testKnownIntegerEnumIndexing
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
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"1"
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
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//doc[1]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Low'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnumSort
specifier|public
name|void
name|testEnumSort
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
literal|"0"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Not Available"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Low"
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
name|FIELD_NAME
argument_list|,
literal|"Medium"
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
name|FIELD_NAME
argument_list|,
literal|"High"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"Critical"
argument_list|)
argument_list|)
expr_stmt|;
comment|// two docs w/o values
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<=
literal|9
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
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|FIELD_NAME
operator|+
literal|" desc"
argument_list|)
argument_list|,
literal|"//doc[1]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Critical'"
argument_list|,
literal|"//doc[2]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='High'"
argument_list|,
literal|"//doc[3]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Medium'"
argument_list|,
literal|"//doc[4]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Low'"
argument_list|,
literal|"//doc[5]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Not Available'"
argument_list|)
expr_stmt|;
comment|//sort ascending - empty values will be first
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|FIELD_NAME
operator|+
literal|" asc"
argument_list|)
argument_list|,
literal|"//doc[3]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Not Available'"
argument_list|)
expr_stmt|;
comment|//q for not empty docs
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|""
operator|+
name|FIELD_NAME
argument_list|,
literal|"q"
argument_list|,
name|FIELD_NAME
operator|+
literal|":[* TO *]"
argument_list|,
literal|"sort"
argument_list|,
name|FIELD_NAME
operator|+
literal|" asc"
argument_list|)
argument_list|,
literal|"//doc[1]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Not Available'"
argument_list|,
literal|"//doc[2]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Low'"
argument_list|,
literal|"//doc[3]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Medium'"
argument_list|,
literal|"//doc[4]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='High'"
argument_list|,
literal|"//doc[5]/str[@name='"
operator|+
name|FIELD_NAME
operator|+
literal|"']/text()='Critical'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
