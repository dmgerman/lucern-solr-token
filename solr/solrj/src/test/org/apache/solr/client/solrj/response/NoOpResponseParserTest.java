begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|SolrJettyTestBase
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
name|client
operator|.
name|solrj
operator|.
name|ResponseParser
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|NoOpResponseParser
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|XMLResponseParser
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|SolrDocument
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
name|SolrInputDocument
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
name|NamedList
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
name|SolrResourceLoader
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
comment|/**  * A test for parsing Solr response from query by NoOpResponseParser.  * @see org.apache.solr.client.solrj.impl.NoOpResponseParser  * @see<a href="https://issues.apache.org/jira/browse/SOLR-5530">SOLR-5530</a>  */
end_comment
begin_class
DECL|class|NoOpResponseParserTest
specifier|public
class|class
name|NoOpResponseParserTest
extends|extends
name|SolrJettyTestBase
block|{
DECL|method|getResponse
specifier|private
specifier|static
name|InputStream
name|getResponse
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SolrResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
literal|"solrj/sampleDateFacetResponse.xml"
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|createJetty
argument_list|(
name|legacyExampleCollection1SolrHome
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|doBefore
specifier|public
name|void
name|doBefore
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|//add document and commit, and ensure it's there
name|SolrClient
name|client
init|=
name|getSolrClient
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/**    * Parse response from query using NoOpResponseParser.    */
annotation|@
name|Test
DECL|method|testQueryParse
specifier|public
name|void
name|testQueryParse
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|createNewSolrClient
argument_list|()
init|)
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"id:1234"
argument_list|)
decl_stmt|;
name|QueryRequest
name|req
init|=
operator|new
name|QueryRequest
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|client
operator|.
name|setParser
argument_list|(
operator|new
name|NoOpResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resp
init|=
name|client
operator|.
name|request
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|responseString
init|=
operator|(
name|String
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
name|assertResponse
argument_list|(
name|responseString
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertResponse
specifier|private
name|void
name|assertResponse
parameter_list|(
name|String
name|responseString
parameter_list|)
throws|throws
name|IOException
block|{
name|ResponseParser
name|xmlResponseParser
init|=
operator|new
name|XMLResponseParser
argument_list|()
decl_stmt|;
name|NamedList
name|expectedResponse
init|=
name|xmlResponseParser
operator|.
name|processResponse
argument_list|(
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|responseString
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SolrDocument
argument_list|>
name|documentList
init|=
operator|(
name|List
argument_list|<
name|SolrDocument
argument_list|>
operator|)
name|expectedResponse
operator|.
name|getAll
argument_list|(
literal|"response"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|documentList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SolrDocument
name|solrDocument
init|=
name|documentList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1234"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parse response from java.io.Reader.    */
annotation|@
name|Test
DECL|method|testReaderResponse
specifier|public
name|void
name|testReaderResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|NoOpResponseParser
name|parser
init|=
operator|new
name|NoOpResponseParser
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|getResponse
argument_list|()
init|)
block|{
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|Reader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|parser
operator|.
name|processResponse
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expectedResponse
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|getResponse
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedResponse
argument_list|,
name|response
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Parse response from java.io.InputStream.    */
annotation|@
name|Test
DECL|method|testInputStreamResponse
specifier|public
name|void
name|testInputStreamResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|NoOpResponseParser
name|parser
init|=
operator|new
name|NoOpResponseParser
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|getResponse
argument_list|()
init|)
block|{
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|parser
operator|.
name|processResponse
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|expectedResponse
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|getResponse
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedResponse
argument_list|,
name|response
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
