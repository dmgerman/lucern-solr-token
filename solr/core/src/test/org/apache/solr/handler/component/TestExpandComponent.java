begin_unit
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|search
operator|.
name|CollapsingQParserPlugin
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|*
import|;
end_import
begin_class
DECL|class|TestExpandComponent
specifier|public
class|class
name|TestExpandComponent
extends|extends
name|SolrTestCaseJ4
block|{
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
literal|"solrconfig-collapseqparser.xml"
argument_list|,
literal|"schema11.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExpand
specifier|public
name|void
name|testExpand
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
literal|"group_s"
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
literal|"group_s_dv"
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|groups
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|floatAppend
init|=
literal|""
decl_stmt|;
name|String
name|hint
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|" hint="
operator|+
name|CollapsingQParserPlugin
operator|.
name|HINT_TOP_FC
else|:
literal|""
operator|)
decl_stmt|;
name|_testExpand
argument_list|(
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|floatAppend
argument_list|,
name|hint
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNumericExpand
specifier|public
name|void
name|testNumericExpand
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|groups
operator|.
name|add
argument_list|(
literal|"group_i"
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
literal|"group_ti_dv"
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
literal|"group_f"
argument_list|)
expr_stmt|;
name|groups
operator|.
name|add
argument_list|(
literal|"group_tf_dv"
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|groups
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|floatAppend
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|indexOf
argument_list|(
literal|"f"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|floatAppend
operator|=
literal|"."
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|//Append the float
name|floatAppend
operator|=
name|Float
operator|.
name|toString
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|floatAppend
argument_list|)
argument_list|)
expr_stmt|;
comment|//Create a proper float out of the string.
name|floatAppend
operator|=
name|floatAppend
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|//Drop off the leading 0, leaving just the decimal
block|}
name|String
name|hint
init|=
literal|""
decl_stmt|;
name|_testExpand
argument_list|(
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|floatAppend
argument_list|,
name|hint
argument_list|)
expr_stmt|;
block|}
DECL|method|_testExpand
specifier|private
name|void
name|_testExpand
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|floatAppend
parameter_list|,
name|String
name|hint
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|doc
init|=
block|{
literal|"id"
block|,
literal|"1"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
name|group
block|,
literal|"1"
operator|+
name|floatAppend
block|,
literal|"test_ti"
block|,
literal|"5"
block|,
literal|"test_tl"
block|,
literal|"10"
block|,
literal|"test_tf"
block|,
literal|"2000"
block|,
literal|"type_s"
block|,
literal|"parent"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc1
init|=
block|{
literal|"id"
block|,
literal|"2"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
name|group
block|,
literal|"1"
operator|+
name|floatAppend
block|,
literal|"test_ti"
block|,
literal|"50"
block|,
literal|"test_tl"
block|,
literal|"100"
block|,
literal|"test_tf"
block|,
literal|"200"
block|,
literal|"type_s"
block|,
literal|"child"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc1
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc2
init|=
block|{
literal|"id"
block|,
literal|"3"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"test_ti"
block|,
literal|"5000"
block|,
literal|"test_tl"
block|,
literal|"100"
block|,
literal|"test_tf"
block|,
literal|"200"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc2
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc3
init|=
block|{
literal|"id"
block|,
literal|"4"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"test_ti"
block|,
literal|"500"
block|,
literal|"test_tl"
block|,
literal|"1000"
block|,
literal|"test_tf"
block|,
literal|"2000"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc3
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc4
init|=
block|{
literal|"id"
block|,
literal|"5"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
name|group
block|,
literal|"2"
operator|+
name|floatAppend
block|,
literal|"test_ti"
block|,
literal|"4"
block|,
literal|"test_tl"
block|,
literal|"10"
block|,
literal|"test_tf"
block|,
literal|"2000"
block|,
literal|"type_s"
block|,
literal|"parent"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc4
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc5
init|=
block|{
literal|"id"
block|,
literal|"6"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
name|group
block|,
literal|"2"
operator|+
name|floatAppend
block|,
literal|"test_ti"
block|,
literal|"10"
block|,
literal|"test_tl"
block|,
literal|"100"
block|,
literal|"test_tf"
block|,
literal|"200"
block|,
literal|"type_s"
block|,
literal|"child"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc5
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc6
init|=
block|{
literal|"id"
block|,
literal|"7"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
name|group
block|,
literal|"1"
operator|+
name|floatAppend
block|,
literal|"test_ti"
block|,
literal|"1"
block|,
literal|"test_tl"
block|,
literal|"100000"
block|,
literal|"test_tf"
block|,
literal|"2000"
block|,
literal|"type_s"
block|,
literal|"child"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc6
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc7
init|=
block|{
literal|"id"
block|,
literal|"8"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
name|group
block|,
literal|"2"
operator|+
name|floatAppend
block|,
literal|"test_ti"
block|,
literal|"2"
block|,
literal|"test_tl"
block|,
literal|"100000"
block|,
literal|"test_tf"
block|,
literal|"200"
block|,
literal|"type_s"
block|,
literal|"child"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc7
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//First basic test case.
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='6.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='8.0']"
argument_list|)
expr_stmt|;
comment|//Basic test case page 2
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=1]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=1]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='6.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='8.0']"
argument_list|)
expr_stmt|;
comment|//Test expand.sort
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc, sub(1,1) asc"
argument_list|)
expr_stmt|;
comment|//the "sub()" just testing function queries
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='6.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='8.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|//Test with nullPolicy, ExpandComponent should ignore docs with null values in the collapse fields.
comment|//Main result set should include the doc with null value in the collapse field.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|" nullPolicy=collapse}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=3]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='3.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/result/doc[3]/float[@name='id'][.='6.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='8.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|//Test overide expand.q
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"type_s:parent"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.q"
argument_list|,
literal|"type_s:child"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.field"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='8.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test overide expand.fq
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"type_s:parent"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.fq"
argument_list|,
literal|"type_s:child"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.field"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='8.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test overide expand.fq and expand.q
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"type_s:parent"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.q"
argument_list|,
literal|"type_s:child"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.fq"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.field"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='8.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test expand.rows
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc)=1]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc)=1]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='6.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='8.0']"
argument_list|)
expr_stmt|;
comment|//Test no group results
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"test_ti:5"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=1]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=0]"
argument_list|)
expr_stmt|;
comment|//Test zero results
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"test_ti:5532535"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.sort"
argument_list|,
literal|"test_tl desc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand.rows"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=0]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=0]"
argument_list|)
expr_stmt|;
comment|//Test key-only fl
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field="
operator|+
name|group
operator|+
name|hint
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"expand"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(/response/result/doc)=2]"
argument_list|,
literal|"*[count(/response/lst[@name='expanded']/result)=2]"
argument_list|,
literal|"/response/result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"/response/result/doc[2]/float[@name='id'][.='6.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='1"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='7.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"/response/lst[@name='expanded']/result[@name='2"
operator|+
name|floatAppend
operator|+
literal|"']/doc[2]/float[@name='id'][.='8.0']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
