begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|ArrayList
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
name|SolrException
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
begin_comment
comment|//We want codecs that support DocValues, and ones supporting blank/empty values.
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Appending"
block|,
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|}
argument_list|)
DECL|class|TestCollapseQParserPlugin
specifier|public
class|class
name|TestCollapseQParserPlugin
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
DECL|method|testStringCollapse
specifier|public
name|void
name|testStringCollapse
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"group_s"
argument_list|)
expr_stmt|;
name|types
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
name|types
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|group
init|=
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|testCollapseQueries
argument_list|(
name|group
argument_list|,
name|hint
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNumericCollapse
specifier|public
name|void
name|testNumericCollapse
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"group_i"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"group_ti_dv"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"group_f"
argument_list|)
expr_stmt|;
name|types
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
name|types
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|group
init|=
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|hint
init|=
literal|""
decl_stmt|;
name|testCollapseQueries
argument_list|(
name|group
argument_list|,
name|hint
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testCollapseQueries
specifier|private
name|void
name|testCollapseQueries
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|hint
parameter_list|,
name|boolean
name|numeric
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
block|,
literal|"test_ti"
block|,
literal|"8"
block|,
literal|"test_tl"
block|,
literal|"50"
block|,
literal|"test_tf"
block|,
literal|"300"
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
comment|//Test collapse by score and following sort by score
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
literal|""
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
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|,
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|// SOLR-5544 test ordering with empty sort param
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
literal|" nullPolicy=expand min=test_tf"
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
literal|"sort"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|// Test value source collapse criteria
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
literal|" nullPolicy=collapse min=field(test_ti)"
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
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|// Test value source collapse criteria with cscore function
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
literal|" nullPolicy=collapse min=cscore()"
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
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|// Test value source collapse criteria with compound cscore function
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
literal|" nullPolicy=collapse min=sum(cscore(),field(test_ti))"
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
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by score with elevation
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
literal|"YYYY"
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
literal|" nullPolicy=collapse"
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
literal|"qf"
argument_list|,
literal|"term_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test SOLR-5773 with score collapse criteria
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
literal|"YYYY"
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
literal|" nullPolicy=collapse"
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
literal|"qf"
argument_list|,
literal|"term_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"1,5"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='3.0']"
argument_list|)
expr_stmt|;
comment|//Test SOLR-5773 with max field collapse criteria
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
literal|"YYYY"
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
literal|" min=test_ti nullPolicy=collapse"
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
literal|"qf"
argument_list|,
literal|"term_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"1,5"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='4.0']"
argument_list|)
expr_stmt|;
comment|//Test SOLR-5773 elevating documents with null group
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
literal|"YYYY"
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
literal|""
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
literal|"qf"
argument_list|,
literal|"term_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"3,4"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min int field and sort
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
literal|" min=test_ti"
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
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
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
literal|" min=test_ti"
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
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
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
literal|" min=test_ti"
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
literal|"sort"
argument_list|,
literal|"test_tl asc,id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
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
literal|" min=test_ti"
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
literal|"sort"
argument_list|,
literal|"score desc,id asc"
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
literal|"field(id)"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by max int field
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
literal|" max=test_ti"
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
literal|"sort"
argument_list|,
literal|"test_ti asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='6.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='2.0']"
argument_list|)
expr_stmt|;
try|try
block|{
comment|//Test collapse by min long field
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
literal|" min=test_tl"
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
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by max long field
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
literal|" max=test_tl"
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
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|numeric
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|//Test collapse by min float field
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
literal|" min=test_tf"
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
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min float field
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
literal|" max=test_tf"
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
literal|"sort"
argument_list|,
literal|"test_ti asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min float field sort by score
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
literal|" max=test_tf"
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
literal|"field(id)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"score, id"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!tag=test}term_s:YYYY"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.field"
argument_list|,
literal|"{!ex=test}term_s"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
comment|//Test nullPolicy expand
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
literal|" max=test_tf nullPolicy=expand"
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
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
comment|//Test nullPolicy collapse
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
literal|" max=test_tf nullPolicy=collapse"
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
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
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
literal|"fq"
argument_list|,
literal|"{!tag=test_ti}id:5"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.field"
argument_list|,
literal|"{!ex=test_ti}test_ti"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.mincount"
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
literal|"*[count(//doc)=1]"
argument_list|,
literal|"*[count(//lst[@name='facet_fields']/lst[@name='test_ti']/int)=2]"
argument_list|)
expr_stmt|;
comment|// SOLR-5230 - ensure CollapsingFieldValueCollector.finish() is called
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
literal|"group"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"group.field"
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
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
comment|// delete the elevated docs, confirm collapsing still works
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"YYYY"
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
literal|"qf"
argument_list|,
literal|"term_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='7.0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingFieldParam
specifier|public
name|void
name|testMissingFieldParam
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"{!collapse}"
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|"It should respond with a bad request when the 'field' param is missing"
argument_list|,
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyCollection
specifier|public
name|void
name|testEmptyCollection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// group_s is docValues=false and group_dv_s is docValues=true
name|String
name|group
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"group_s"
else|:
literal|"group_s_dv"
operator|)
decl_stmt|;
comment|// min-or-max is for CollapsingScoreCollector vs. CollapsingFieldValueCollector
name|String
name|optional_min_or_max
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"min=field(test_ti)"
else|:
literal|"max=field(test_ti)"
operator|)
operator|)
decl_stmt|;
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
literal|" "
operator|+
name|optional_min_or_max
operator|+
literal|"}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
