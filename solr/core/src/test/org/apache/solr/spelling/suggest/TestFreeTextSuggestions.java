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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SpellingParams
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
DECL|class|TestFreeTextSuggestions
specifier|public
class|class
name|TestFreeTextSuggestions
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|URI
specifier|static
specifier|final
name|String
name|URI
init|=
literal|"/free_text_suggest"
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
DECL|method|test
specifier|public
name|void
name|test
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
literal|"foo b"
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
literal|"free_text_suggest"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo b']/int[@name='numFound'][.='1']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo b']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='foo bar']"
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
literal|"foo "
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
literal|"free_text_suggest"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo ']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo ']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='foo bar']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo ']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='foo bee']"
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
literal|"foo"
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
literal|"free_text_suggest"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo']/int[@name='numFound'][.='1']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='foo']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='foo']"
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
literal|"b"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_COUNT
argument_list|,
literal|"5"
argument_list|,
name|SuggesterParams
operator|.
name|SUGGEST_DICT
argument_list|,
literal|"free_text_suggest"
argument_list|)
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='b']/int[@name='numFound'][.='5']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='b']/arr[@name='suggestions']/lst[1]/str[@name='term'][.='bar']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='b']/arr[@name='suggestions']/lst[2]/str[@name='term'][.='baz']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='b']/arr[@name='suggestions']/lst[3]/str[@name='term'][.='bee']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='b']/arr[@name='suggestions']/lst[4]/str[@name='term'][.='blah']"
argument_list|,
literal|"//lst[@name='suggest']/lst[@name='free_text_suggest']/lst[@name='b']/arr[@name='suggestions']/lst[5]/str[@name='term'][.='boo']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit