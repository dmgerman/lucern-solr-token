begin_unit
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
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
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_class
DECL|class|TestBlendedInfixSuggestions
specifier|public
class|class
name|TestBlendedInfixSuggestions
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|URI
specifier|static
specifier|final
name|String
name|URI
init|=
literal|"/blended_infix_suggest"
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
literal|"solrconfig-phrasesuggest.xml"
argument_list|,
literal|"schema-phrasesuggest.xml"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|""
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_BUILD_ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinearBlenderType
specifier|public
name|void
name|testLinearBlenderType
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_linear"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='top of<b>the</b> lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[1]/long[@name='weight'][.='14']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='payload'][.='lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='<b>the</b> returned']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[2]/long[@name='weight'][.='10']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='payload'][.='ret']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='star wars: episode v -<b>the</b> empire strikes back']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[3]/long[@name='weight'][.='7']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='payload'][.='star']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testReciprocalBlenderType
specifier|public
name|void
name|testReciprocalBlenderType
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='<b>the</b> returned']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/long[@name='weight'][.='10']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='payload'][.='ret']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='top of<b>the</b> lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/long[@name='weight'][.='6']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='payload'][.='lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='star wars: episode v -<b>the</b> empire strikes back']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/long[@name='weight'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='payload'][.='star']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExponentialReciprocalBlenderTypeExponent1
specifier|public
name|void
name|testExponentialReciprocalBlenderTypeExponent1
parameter_list|()
block|{
comment|//exponent=1 will give same output as reciprocal
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_exponential_reciprocal_1"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='<b>the</b> returned']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[1]/long[@name='weight'][.='10']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='payload'][.='ret']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='top of<b>the</b> lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[2]/long[@name='weight'][.='6']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='payload'][.='lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='star wars: episode v -<b>the</b> empire strikes back']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[3]/long[@name='weight'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal_1']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='payload'][.='star']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExponentialReciprocalBlenderType
specifier|public
name|void
name|testExponentialReciprocalBlenderType
parameter_list|()
block|{
comment|// default is exponent=2.0
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_exponential_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='<b>the</b> returned']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/long[@name='weight'][.='10']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='payload'][.='ret']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='top of<b>the</b> lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/long[@name='weight'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='payload'][.='lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='star wars: episode v -<b>the</b> empire strikes back']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/long[@name='weight'][.='0']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_exponential_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='payload'][.='star']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiSuggester
specifier|public
name|void
name|testMultiSuggester
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"10"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_linear"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='top of<b>the</b> lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[1]/long[@name='weight'][.='14']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='payload'][.='lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='<b>the</b> returned']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[2]/long[@name='weight'][.='10']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='payload'][.='ret']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='star wars: episode v -<b>the</b> empire strikes back']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[3]/long[@name='weight'][.='7']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_linear']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='payload'][.='star']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='<b>the</b> returned']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/long[@name='weight'][.='10']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[1]/str[@name='payload'][.='ret']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='top of<b>the</b> lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/long[@name='weight'][.='6']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[2]/str[@name='payload'][.='lake']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='star wars: episode v -<b>the</b> empire strikes back']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/long[@name='weight'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/arr[@name='suggestions']/lst[3]/str[@name='payload'][.='star']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSuggestCount
specifier|public
name|void
name|testSuggestCount
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"1"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/int[@name='numFound'][.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/int[@name='numFound'][.='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"3"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI
argument_list|,
literal|"q"
argument_list|,
literal|"the"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"20"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"blended_infix_suggest_reciprocal"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='blended_infix_suggest_reciprocal']/lst[@name='the']/int[@name='numFound'][.='3']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
