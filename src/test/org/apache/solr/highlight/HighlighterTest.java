begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|core
operator|.
name|SolrCore
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
name|*
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
name|util
operator|.
name|*
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
name|schema
operator|.
name|*
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
begin_comment
comment|/**  * Tests some basic functionality of Solr while demonstrating good  * Best Practices for using AbstractSolrTestCase  */
end_comment
begin_class
DECL|class|HighlighterTest
specifier|public
class|class
name|HighlighterTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
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
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testConfig
specifier|public
name|void
name|testConfig
parameter_list|()
block|{
name|SolrHighlighter
name|highlighter
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
operator|.
name|getHighlighter
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"highlighter"
argument_list|)
expr_stmt|;
comment|// Make sure we loaded the one formatter
name|SolrFormatter
name|fmt1
init|=
name|highlighter
operator|.
name|formatters
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|SolrFormatter
name|fmt2
init|=
name|highlighter
operator|.
name|formatters
operator|.
name|get
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|fmt1
argument_list|,
name|fmt2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fmt1
operator|instanceof
name|HtmlFormatter
argument_list|)
expr_stmt|;
comment|// Make sure we loaded the one formatter
name|SolrFragmenter
name|gap
init|=
name|highlighter
operator|.
name|fragmenters
operator|.
name|get
argument_list|(
literal|"gap"
argument_list|)
decl_stmt|;
name|SolrFragmenter
name|regex
init|=
name|highlighter
operator|.
name|fragmenters
operator|.
name|get
argument_list|(
literal|"regex"
argument_list|)
decl_stmt|;
name|SolrFragmenter
name|frag
init|=
name|highlighter
operator|.
name|fragmenters
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|gap
argument_list|,
name|frag
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|gap
operator|instanceof
name|GapFragmenter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|regex
operator|instanceof
name|RegexFragmenter
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermVecHighlight
specifier|public
name|void
name|testTermVecHighlight
parameter_list|()
block|{
comment|// do summarization using term vectors
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"tv_text"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.snippets"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"tv_text"
argument_list|,
literal|"a long days night this should be a piece of text which is is is is is is is is is is is is is is is is is is is is is is is is isis is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is sufficiently lengthly to produce multiple fragments which are not concatenated at all--we want two disjoint long fragments."
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"tv_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str[.='a<em>long</em> days night this should be a piece of text which']"
argument_list|,
literal|"//arr[@name='tv_text']/str[.='<em>long</em> fragments.']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisMaxHighlight
specifier|public
name|void
name|testDisMaxHighlight
parameter_list|()
block|{
comment|// same test run through dismax handler
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"tv_text"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"qf"
argument_list|,
literal|"tv_text"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"dismax"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"tv_text"
argument_list|,
literal|"a long day's night"
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiValueAnalysisHighlight
specifier|public
name|void
name|testMultiValueAnalysisHighlight
parameter_list|()
block|{
comment|// do summarization using re-analysis of the field
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"textgap"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"df"
argument_list|,
literal|"textgap"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"textgap"
argument_list|,
literal|"first entry hasnt queryword"
argument_list|,
literal|"textgap"
argument_list|,
literal|"second entry has queryword long"
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='textgap']/str"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultFieldHighlight
specifier|public
name|void
name|testDefaultFieldHighlight
parameter_list|()
block|{
comment|// do summarization using re-analysis of the field
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"df"
argument_list|,
literal|"t_text"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"t_text"
argument_list|,
literal|"a long day's night"
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text']/str"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHighlightDisabled
specifier|public
name|void
name|testHighlightDisabled
parameter_list|()
block|{
comment|// ensure highlighting can be explicitly disabled
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"t_text"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"t_text"
argument_list|,
literal|"a long day's night"
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"t_text:long"
argument_list|)
argument_list|,
literal|"not(//lst[@name='highlighting'])"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoFieldHighlight
specifier|public
name|void
name|testTwoFieldHighlight
parameter_list|()
block|{
comment|// do summarization using re-analysis of the field
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"t_text tv_text"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"t_text"
argument_list|,
literal|"a long day's night"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"tv_text"
argument_list|,
literal|"a long night's day"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"t_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text']/str"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldMatch
specifier|public
name|void
name|testFieldMatch
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"t_text1"
argument_list|,
literal|"random words for highlighting tests"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"t_text2"
argument_list|,
literal|"more random words for second field"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"t_text1 t_text2"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
comment|// default should highlight both random and words in both fields
name|assertQ
argument_list|(
literal|"Test Default"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"t_text1:random OR t_text2:words"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text1']/str[.='<em>random</em><em>words</em> for highlighting tests']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text2']/str[.='more<em>random</em><em>words</em> for second field']"
argument_list|)
expr_stmt|;
comment|// requireFieldMatch=true - highlighting should only occur if term matched in that field
name|args
operator|.
name|put
argument_list|(
literal|"hl.requireFieldMatch"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|sumLRF
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Test RequireFieldMatch"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"t_text1:random OR t_text2:words"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text1']/str[.='<em>random</em> words for highlighting tests']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text2']/str[.='more random<em>words</em> for second field']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomSimpleFormatterHighlight
specifier|public
name|void
name|testCustomSimpleFormatterHighlight
parameter_list|()
block|{
comment|// do summarization using a custom formatter
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"t_text"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.simple.pre"
argument_list|,
literal|"<B>"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.simple.post"
argument_list|,
literal|"</B>"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"t_text"
argument_list|,
literal|"a long days night"
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"t_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text']/str[.='a<B>long</B> days night']"
argument_list|)
expr_stmt|;
comment|// test a per-field override
name|args
operator|.
name|put
argument_list|(
literal|"f.t_text.hl.simple.pre"
argument_list|,
literal|"<I>"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"f.t_text.hl.simple.post"
argument_list|,
literal|"</I>"
argument_list|)
expr_stmt|;
name|sumLRF
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"t_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='t_text']/str[.='a<I>long</I> days night']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongFragment
specifier|public
name|void
name|testLongFragment
parameter_list|()
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"tv_text"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|text
init|=
literal|"junit: [mkdir] Created dir: /home/klaas/worio/backend/trunk/build-src/solr-nightly/build/test-results [junit] Running org.apache.solr.BasicFunctionalityTest [junit] Tests run: 7, Failures: 0, Errors: 0, Time elapsed: 5.36 sec [junit] Running org.apache.solr.ConvertedLegacyTest [junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 8.268 sec [junit] Running org.apache.solr.DisMaxRequestHandlerTest [junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 1.56 sec [junit] Running org.apache.solr.HighlighterTest [junit] Tests run: 7, Failures: 0, Errors: 0, Time elapsed: 4.979 sec [junit] Running org.apache.solr.OutputWriterTest [junit] Tests run: 2, Failures: 0, Errors: 0, Time elapsed: 0.797 sec [junit] Running org.apache.solr.SampleTest [junit] Tests run: 2, Failures: 0, Errors: 0, Time elapsed: 1.021 sec [junit] Running org.apache.solr.analysis.TestBufferedTokenStream [junit] Tests run: 2, Failures: 0, Errors: 0, Time elapsed: 0.05 sec [junit] Running org.apache.solr.analysis.TestRemoveDuplicatesTokenFilter [junit] Tests run: 3, Failures: 0, Errors: 0, Time elapsed: 0.054 sec [junit] Running org.apache.solr.analysis.TestSynonymFilter [junit] Tests run: 6, Failures: 0, Errors: 0, Time elapsed: 0.081 sec [junit] Running org.apache.solr.analysis.TestWordDelimiterFilter [junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 1.714 sec [junit] Running org.apache.solr.search.TestDocSet [junit] Tests run: 1, Failures: 0, Errors: 0, Time elapsed: 0.788 sec [junit] Running org.apache.solr.util.SolrPluginUtilsTest [junit] Tests run: 5, Failures: 0, Errors: 0, Time elapsed: 3.519 sec [junit] Running org.apache.solr.util.TestOpenBitSet [junit] Tests run: 2, Failures: 0, Errors: 0, Time elapsed: 0.533 sec"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"tv_text"
argument_list|,
name|text
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"tv_text:dir"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str"
argument_list|)
expr_stmt|;
block|}
DECL|method|testVariableFragsize
specifier|public
name|void
name|testVariableFragsize
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"tv_text"
argument_list|,
literal|"a long days night this should be a piece of text which is is is is is is is is is is is is is is is is is is is is is is is is isis is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is sufficiently lengthly to produce multiple fragments which are not concatenated at all"
argument_list|,
literal|"id"
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
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
comment|// default length
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"tv_text"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"tv_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str[.='a<em>long</em> days night this should be a piece of text which']"
argument_list|)
expr_stmt|;
comment|// 25
name|args
operator|.
name|put
argument_list|(
literal|"hl.fragsize"
argument_list|,
literal|"25"
argument_list|)
expr_stmt|;
name|sumLRF
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"tv_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str[.='a<em>long</em> days night']"
argument_list|)
expr_stmt|;
comment|// 0 - NullFragmenter
name|args
operator|.
name|put
argument_list|(
literal|"hl.fragsize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|sumLRF
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"tv_text:long"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str[.='a<em>long</em> days night this should be a piece of text which is is is is is is is is is is is is is is is is is is is is is is is is isis is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is is sufficiently lengthly to produce multiple fragments which are not concatenated at all']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
