begin_unit
begin_package
DECL|package|org.apache.solr.uima.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|processor
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|MultiMapSolrParams
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
name|SolrParams
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
name|UpdateParams
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
name|ContentStream
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
name|ContentStreamBase
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
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|XmlUpdateRequestHandler
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
name|SolrQueryRequestBase
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
name|response
operator|.
name|SolrQueryResponse
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
name|uima
operator|.
name|processor
operator|.
name|SolrUIMAConfiguration
operator|.
name|MapField
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
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
comment|/**  * TestCase for {@link UIMAUpdateRequestProcessor}  *   *  */
end_comment
begin_class
DECL|class|UIMAUpdateRequestProcessorTest
specifier|public
class|class
name|UIMAUpdateRequestProcessorTest
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"solr-uima"
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
DECL|method|testProcessorConfiguration
specifier|public
name|void
name|testProcessorConfiguration
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"uima"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|UIMAUpdateRequestProcessorFactory
name|factory
init|=
operator|(
name|UIMAUpdateRequestProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|processor
operator|instanceof
name|UIMAUpdateRequestProcessor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiMap
specifier|public
name|void
name|testMultiMap
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"uima-multi-map"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|UIMAUpdateRequestProcessorFactory
name|factory
init|=
operator|(
name|UIMAUpdateRequestProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|processor
operator|instanceof
name|UIMAUpdateRequestProcessor
argument_list|)
expr_stmt|;
name|SolrUIMAConfiguration
name|conf
init|=
operator|(
operator|(
name|UIMAUpdateRequestProcessor
operator|)
name|processor
operator|)
operator|.
name|solrUIMAConfiguration
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|map
init|=
name|conf
operator|.
name|getTypesFeaturesFieldsMapping
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
name|subMap
init|=
name|map
operator|.
name|get
argument_list|(
literal|"a-type-which-can-have-multiple-features"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|subMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|subMap
operator|.
name|get
argument_list|(
literal|"A"
argument_list|)
operator|.
name|getFieldName
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|subMap
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
operator|.
name|getFieldName
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessing
specifier|public
name|void
name|testProcessing
parameter_list|()
throws|throws
name|Exception
block|{
name|addDoc
argument_list|(
literal|"uima"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2312312321312"
argument_list|,
literal|"text"
argument_list|,
literal|"SpellCheckComponent got improvement related to recent Lucene changes. \n  "
operator|+
literal|"Add support for specifying Spelling SuggestWord Comparator to Lucene spell "
operator|+
literal|"checkers for SpellCheckComponent. Issue SOLR-2053 is already fixed, patch is"
operator|+
literal|" attached if you need it, but it is also committed to trunk and 3_x branch."
operator|+
literal|" Last Lucene European Conference has been held in Prague."
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
literal|"sentence:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"sentiment:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"OTHER_sm:Prague"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoUpdates
specifier|public
name|void
name|testTwoUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|addDoc
argument_list|(
literal|"uima"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"The Apache Software Foundation is happy to announce "
operator|+
literal|"BarCampApache Sydney, Australia, the first ASF-backed event in the Southern "
operator|+
literal|"Hemisphere!"
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
literal|"sentence:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"uima"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"Taking place 11th December 2010 at the University "
operator|+
literal|"of Sydney's Darlington Centre, the BarCampApache \"unconference\" will be"
operator|+
literal|" attendee-driven, facilitated by members of the Apache community and will "
operator|+
literal|"focus on the Apache..."
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
literal|"sentence:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"sentiment:positive"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"ORGANIZATION_sm:Apache"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorHandling
specifier|public
name|void
name|testErrorHandling
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|addDoc
argument_list|(
literal|"uima-not-ignoreErrors"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2312312321312"
argument_list|,
literal|"text"
argument_list|,
literal|"SpellCheckComponent got improvement related to recent Lucene changes. \n  "
operator|+
literal|"Add support for specifying Spelling SuggestWord Comparator to Lucene spell "
operator|+
literal|"checkers for SpellCheckComponent. Issue SOLR-2053 is already fixed, patch is"
operator|+
literal|" attached if you need it, but it is also committed to trunk and 3_x branch."
operator|+
literal|" Last Lucene European Conference has been held in Prague."
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception shouldn't be ignored"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|expected
parameter_list|)
block|{}
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
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"uima-ignoreErrors"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2312312321312"
argument_list|,
literal|"text"
argument_list|,
literal|"SpellCheckComponent got improvement related to recent Lucene changes. \n  "
operator|+
literal|"Add support for specifying Spelling SuggestWord Comparator to Lucene spell "
operator|+
literal|"checkers for SpellCheckComponent. Issue SOLR-2053 is already fixed, patch is"
operator|+
literal|" attached if you need it, but it is also committed to trunk and 3_x branch."
operator|+
literal|" Last Lucene European Conference has been held in Prague."
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
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
try|try
block|{
name|addDoc
argument_list|(
literal|"uima-not-ignoreErrors"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2312312321312"
argument_list|,
literal|"text"
argument_list|,
literal|"SpellCheckComponent got improvement related to recent Lucene changes."
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception shouldn't be ignored"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// SOLR-2579
name|fail
argument_list|(
literal|"exception shouldn't be raised"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|expected
parameter_list|)
block|{}
try|try
block|{
name|addDoc
argument_list|(
literal|"uima-ignoreErrors"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2312312321312"
argument_list|,
literal|"text"
argument_list|,
literal|"SpellCheckComponent got improvement related to recent Lucene changes."
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// SOLR-2579
name|fail
argument_list|(
literal|"exception shouldn't be raised"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|chain
parameter_list|,
name|String
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|,
operator|new
name|String
index|[]
block|{
name|chain
block|}
argument_list|)
expr_stmt|;
name|MultiMapSolrParams
name|mmparams
init|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
operator|(
name|SolrParams
operator|)
name|mmparams
argument_list|)
block|{     }
decl_stmt|;
name|XmlUpdateRequestHandler
name|handler
init|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
