begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
name|impl
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
name|http
operator|.
name|client
operator|.
name|config
operator|.
name|RequestConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
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
name|SolrRequest
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
name|embedded
operator|.
name|JettyConfig
import|;
end_import
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
DECL|class|ExternalHttpClientTest
specifier|public
class|class
name|ExternalHttpClientTest
extends|extends
name|SolrJettyTestBase
block|{
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
name|JettyConfig
name|jettyConfig
init|=
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|withServlet
argument_list|(
operator|new
name|ServletHolder
argument_list|(
name|BasicHttpSolrClientTest
operator|.
name|SlowServlet
operator|.
name|class
argument_list|)
argument_list|,
literal|"/slow/*"
argument_list|)
operator|.
name|withSSLConfig
argument_list|(
name|sslConfig
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|createJetty
argument_list|(
name|legacyExampleCollection1SolrHome
argument_list|()
argument_list|,
name|jettyConfig
argument_list|)
expr_stmt|;
block|}
comment|/**    * The internal client created by HttpSolrClient is a SystemDefaultHttpClient    * which takes care of merging request level params (such as timeout) with the    * configured defaults.    *    * However, if an external HttpClient is passed to HttpSolrClient,    * the logic in InternalHttpClient.executeMethod replaces the configured defaults    * by request level params if they exist. That is why we must test a setting such    * as timeout with an external client to assert that the defaults are indeed being    * used    *    * See SOLR-6245 for more details    */
annotation|@
name|Test
DECL|method|testTimeoutWithExternalClient
specifier|public
name|void
name|testTimeoutWithExternalClient
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpClientBuilder
name|builder
init|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
decl_stmt|;
name|RequestConfig
name|config
init|=
name|RequestConfig
operator|.
name|custom
argument_list|()
operator|.
name|setSocketTimeout
argument_list|(
literal|2000
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDefaultRequestConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|builder
operator|.
name|build
argument_list|()
init|;
name|HttpSolrClient
name|solrClient
operator|=
operator|new
name|HttpSolrClient
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/slow/foo"
argument_list|,
name|httpClient
argument_list|)
init|)
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
try|try
block|{
name|solrClient
operator|.
name|query
argument_list|(
name|q
argument_list|,
name|SolrRequest
operator|.
name|METHOD
operator|.
name|GET
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No exception thrown."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Timeout"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
