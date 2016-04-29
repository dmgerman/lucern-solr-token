begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
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
name|io
operator|.
name|File
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
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
name|embedded
operator|.
name|JettyConfig
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
name|JettySolrRunner
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
name|CloudSolrClient
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
name|HttpClientUtil
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
name|CoreAdminRequest
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
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
name|SSLTestConfig
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
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
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
name|client
operator|.
name|HttpClient
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
name|client
operator|.
name|methods
operator|.
name|HttpHead
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
name|config
operator|.
name|RegistryBuilder
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
name|config
operator|.
name|Registry
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
name|conn
operator|.
name|socket
operator|.
name|ConnectionSocketFactory
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
name|conn
operator|.
name|socket
operator|.
name|PlainConnectionSocketFactory
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
name|conn
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|conn
operator|.
name|ssl
operator|.
name|SSLConnectionSocketFactory
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
name|Constants
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * Tests various permutations of SSL options with {@link MiniSolrCloudCluster}.  *<b>NOTE: This Test ignores the randomized SSL&amp; clientAuth settings selected by base class</b>,  * instead each method initializes a {@link SSLTestConfig} will specific combinations of settings to test.  *  * @see TestSSLRandomization  */
end_comment
begin_class
DECL|class|TestMiniSolrCloudClusterSSL
specifier|public
class|class
name|TestMiniSolrCloudClusterSSL
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|DEFAULT_SSL_CONTEXT
specifier|private
specifier|static
specifier|final
name|SSLContext
name|DEFAULT_SSL_CONTEXT
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_SSL_CONTEXT
operator|=
name|SSLContext
operator|.
name|getDefault
argument_list|()
expr_stmt|;
assert|assert
literal|null
operator|!=
name|DEFAULT_SSL_CONTEXT
assert|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to initialize 'Default' SSLContext Algorithm, JVM is borked"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|NUM_SERVERS
specifier|public
specifier|static
specifier|final
name|int
name|NUM_SERVERS
init|=
literal|3
decl_stmt|;
DECL|field|CONF_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CONF_NAME
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
comment|// undo the randomization of our super class
name|log
operator|.
name|info
argument_list|(
literal|"NOTE: This Test ignores the randomized SSL& clientAuth settings selected by base class"
argument_list|)
expr_stmt|;
name|HttpClientUtil
operator|.
name|resetHttpClientBuilder
argument_list|()
expr_stmt|;
comment|// also resets SchemaRegistryProvider
name|System
operator|.
name|clearProperty
argument_list|(
name|ZkStateReader
operator|.
name|URL_SCHEME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{
name|HttpClientUtil
operator|.
name|resetHttpClientBuilder
argument_list|()
expr_stmt|;
comment|// also resets SchemaRegistryProvider
name|System
operator|.
name|clearProperty
argument_list|(
name|ZkStateReader
operator|.
name|URL_SCHEME
argument_list|)
expr_stmt|;
name|SSLContext
operator|.
name|setDefault
argument_list|(
name|DEFAULT_SSL_CONTEXT
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoSsl
specifier|public
name|void
name|testNoSsl
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SSLTestConfig
name|sslConfig
init|=
operator|new
name|SSLTestConfig
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HttpClientUtil
operator|.
name|setSchemaRegistryProvider
argument_list|(
name|sslConfig
operator|.
name|buildClientSchemaRegistryProvider
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|URL_SCHEME
argument_list|,
literal|"http"
argument_list|)
expr_stmt|;
name|checkClusterWithNodeReplacement
argument_list|(
name|sslConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoSslButSillyClientAuth
specifier|public
name|void
name|testNoSslButSillyClientAuth
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this combination doesn't really make sense, since ssl==false the clientauth option will be ignored
comment|// but we test it anyway for completeness of sanity checking the behavior of code that looks at those
comment|// options.
specifier|final
name|SSLTestConfig
name|sslConfig
init|=
operator|new
name|SSLTestConfig
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|HttpClientUtil
operator|.
name|setSchemaRegistryProvider
argument_list|(
name|sslConfig
operator|.
name|buildClientSchemaRegistryProvider
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|URL_SCHEME
argument_list|,
literal|"http"
argument_list|)
expr_stmt|;
name|checkClusterWithNodeReplacement
argument_list|(
name|sslConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|testSslAndNoClientAuth
specifier|public
name|void
name|testSslAndNoClientAuth
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SSLTestConfig
name|sslConfig
init|=
operator|new
name|SSLTestConfig
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HttpClientUtil
operator|.
name|setSchemaRegistryProvider
argument_list|(
name|sslConfig
operator|.
name|buildClientSchemaRegistryProvider
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|URL_SCHEME
argument_list|,
literal|"https"
argument_list|)
expr_stmt|;
name|checkClusterWithNodeReplacement
argument_list|(
name|sslConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|testSslAndClientAuth
specifier|public
name|void
name|testSslAndClientAuth
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"SOLR-9039: SSL w/clientAuth does not work on MAC_OS_X"
argument_list|,
name|Constants
operator|.
name|MAC_OS_X
argument_list|)
expr_stmt|;
specifier|final
name|SSLTestConfig
name|sslConfig
init|=
operator|new
name|SSLTestConfig
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|HttpClientUtil
operator|.
name|setSchemaRegistryProvider
argument_list|(
name|sslConfig
operator|.
name|buildClientSchemaRegistryProvider
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|URL_SCHEME
argument_list|,
literal|"https"
argument_list|)
expr_stmt|;
name|checkClusterWithNodeReplacement
argument_list|(
name|sslConfig
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a cluster with the specified sslConfigs, runs {@link #checkClusterWithCollectionCreations},     * then verifies that if we modify the default SSLContext (mimicing<code>javax.net.ssl.*</code>     * sysprops set on JVM startup) and reset to the default HttpClientBuilder, new HttpSolrClient instances     * will still be able to talk to our servers.    *    * @see SSLContext#setDefault    * @see HttpClientUtil#resetHttpClientBuilder    * @see #checkClusterWithCollectionCreations    */
DECL|method|checkClusterWithNodeReplacement
specifier|private
name|void
name|checkClusterWithNodeReplacement
parameter_list|(
name|SSLTestConfig
name|sslConfig
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|JettyConfig
name|config
init|=
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|withSSLConfig
argument_list|(
name|sslConfig
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|MiniSolrCloudCluster
name|cluster
init|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|checkClusterWithCollectionCreations
argument_list|(
name|cluster
argument_list|,
name|sslConfig
argument_list|)
expr_stmt|;
comment|// Change the defaul SSLContext to match our test config, or to match our original system default if
comment|// our test config doesn't use SSL, and reset HttpClientUtil to it's defaults so it picks up our
comment|// SSLContext that way.
name|SSLContext
operator|.
name|setDefault
argument_list|(
name|sslConfig
operator|.
name|isSSLMode
argument_list|()
condition|?
name|sslConfig
operator|.
name|buildClientSSLContext
argument_list|()
else|:
name|DEFAULT_SSL_CONTEXT
argument_list|)
expr_stmt|;
name|HttpClientUtil
operator|.
name|resetHttpClientBuilder
argument_list|()
expr_stmt|;
comment|// recheck that we can communicate with all the jetty instances in our cluster
name|checkClusterJettys
argument_list|(
name|cluster
argument_list|,
name|sslConfig
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * General purpose cluster sanity check...    *<ol>    *<li>Upload a config set</li>    *<li>verifies a collection can be created</li>    *<li>verifies many things that should succeed/fail when communicating with the cluster according to the specified sslConfig</li>    *<li>shutdown a server&amp; startup a new one in it's place</li>    *<li>repeat the verifications of ssl / no-ssl communication</li>    *<li>create a second collection</li>    *</ol>    * @see #CONF_NAME    * @see #NUM_SERVERS    */
DECL|method|checkClusterWithCollectionCreations
specifier|public
specifier|static
name|void
name|checkClusterWithCollectionCreations
parameter_list|(
specifier|final
name|MiniSolrCloudCluster
name|cluster
parameter_list|,
specifier|final
name|SSLTestConfig
name|sslConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|uploadConfigDir
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
argument_list|,
name|CONF_NAME
argument_list|)
expr_stmt|;
name|checkCreateCollection
argument_list|(
name|cluster
argument_list|,
literal|"first_collection"
argument_list|)
expr_stmt|;
name|checkClusterJettys
argument_list|(
name|cluster
argument_list|,
name|sslConfig
argument_list|)
expr_stmt|;
comment|// shut down a server
name|JettySolrRunner
name|stoppedServer
init|=
name|cluster
operator|.
name|stopJettySolrRunner
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stoppedServer
operator|.
name|isStopped
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
operator|-
literal|1
argument_list|,
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// create a new server
name|JettySolrRunner
name|startedServer
init|=
name|cluster
operator|.
name|startJettySolrRunner
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|startedServer
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkClusterJettys
argument_list|(
name|cluster
argument_list|,
name|sslConfig
argument_list|)
expr_stmt|;
name|checkCreateCollection
argument_list|(
name|cluster
argument_list|,
literal|"second_collection"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that we can create a collection that involves one replica per node using the    * CloudSolrClient available for the cluster    */
DECL|method|checkCreateCollection
specifier|private
specifier|static
name|void
name|checkCreateCollection
parameter_list|(
specifier|final
name|MiniSolrCloudCluster
name|cluster
parameter_list|,
specifier|final
name|String
name|collection
parameter_list|)
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|cluster
operator|.
name|createCollection
argument_list|(
name|collection
argument_list|,
comment|/* 1 shard/replica per server */
name|NUM_SERVERS
argument_list|,
literal|1
argument_list|,
name|CONF_NAME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"config"
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CloudSolrClient
name|cloudClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
name|zkStateReader
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|330
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sanity query"
argument_list|,
literal|0
argument_list|,
name|cloudClient
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**     * verify that we can query all of the Jetty instances the specified cluster using the expected    * options (based on the sslConfig), and that we can<b>NOT</b> query the Jetty instances in     * specified cluster in the ways that should fail (based on the sslConfig)    *    * @see #getRandomizedHttpSolrClient    */
DECL|method|checkClusterJettys
specifier|private
specifier|static
name|void
name|checkClusterJettys
parameter_list|(
specifier|final
name|MiniSolrCloudCluster
name|cluster
parameter_list|,
specifier|final
name|SSLTestConfig
name|sslConfig
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|boolean
name|ssl
init|=
name|sslConfig
operator|.
name|isSSLMode
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
init|=
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
specifier|final
name|String
name|baseURL
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// basic base URL sanity checks
name|assertTrue
argument_list|(
literal|"WTF baseURL: "
operator|+
name|baseURL
argument_list|,
literal|null
operator|!=
name|baseURL
operator|&&
literal|10
operator|<
name|baseURL
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http vs https: "
operator|+
name|baseURL
argument_list|,
name|ssl
condition|?
literal|"https"
else|:
literal|"http:"
argument_list|,
name|baseURL
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify solr client success with expected protocol
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getRandomizedHttpSolrClient
argument_list|(
name|baseURL
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
comment|/* all */
literal|null
argument_list|,
name|client
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sanity check the HttpClient used under the hood by our the cluster's CloudSolrClient
comment|// ensure it has the neccessary protocols/credentials for each jetty server
comment|//
comment|// NOTE: we're not responsible for closing the cloud client
specifier|final
name|HttpClient
name|cloudClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getLbClient
argument_list|()
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getRandomizedHttpSolrClient
argument_list|(
name|baseURL
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
comment|/* all */
literal|null
argument_list|,
name|client
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|wrongBaseURL
init|=
name|baseURL
operator|.
name|replaceFirst
argument_list|(
operator|(
name|ssl
condition|?
literal|"https://"
else|:
literal|"http://"
operator|)
argument_list|,
operator|(
name|ssl
condition|?
literal|"http://"
else|:
literal|"https://"
operator|)
argument_list|)
decl_stmt|;
comment|// verify solr client using wrong protocol can't talk to server
name|expectThrows
argument_list|(
name|SolrServerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
init|(
name|HttpSolrClient
name|client
init|=
name|getRandomizedHttpSolrClient
argument_list|(
name|wrongBaseURL
argument_list|)
init|)
block|{
name|CoreAdminRequest
name|req
init|=
operator|new
name|CoreAdminRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setAction
argument_list|(
name|CoreAdminAction
operator|.
name|STATUS
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|sslConfig
operator|.
name|isClientAuthMode
argument_list|()
condition|)
block|{
comment|// verify simple HTTP(S) client can't do HEAD request for URL with wrong protocol
try|try
init|(
name|CloseableHttpClient
name|client
init|=
name|getSslAwareClientWithNoClientCerts
argument_list|()
init|)
block|{
specifier|final
name|String
name|wrongUrl
init|=
name|wrongBaseURL
operator|+
literal|"/admin/cores"
decl_stmt|;
comment|// vastly diff exception details betwen plain http vs https, not worried about details here
name|expectThrows
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|doHeadRequest
argument_list|(
name|client
argument_list|,
name|wrongUrl
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ssl
condition|)
block|{
comment|// verify expected results for a HEAD request to valid URL from HTTP(S) client w/o client certs
try|try
init|(
name|CloseableHttpClient
name|client
init|=
name|getSslAwareClientWithNoClientCerts
argument_list|()
init|)
block|{
specifier|final
name|String
name|url
init|=
name|baseURL
operator|+
literal|"/admin/cores"
decl_stmt|;
if|if
condition|(
name|sslConfig
operator|.
name|isClientAuthMode
argument_list|()
condition|)
block|{
comment|// w/o a valid client cert, SSL connection should fail
name|expectThrows
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|doHeadRequest
argument_list|(
name|client
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Wrong status for head request ("
operator|+
name|url
operator|+
literal|") when clientAuth="
operator|+
name|sslConfig
operator|.
name|isClientAuthMode
argument_list|()
argument_list|,
literal|200
argument_list|,
name|doHeadRequest
argument_list|(
name|client
argument_list|,
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**     * Trivial helper method for doing a HEAD request of the specified URL using the specified client     * and getting the HTTP statusCode from the response    */
DECL|method|doHeadRequest
specifier|private
specifier|static
name|int
name|doHeadRequest
parameter_list|(
specifier|final
name|CloseableHttpClient
name|client
parameter_list|,
specifier|final
name|String
name|url
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|client
operator|.
name|execute
argument_list|(
operator|new
name|HttpHead
argument_list|(
name|url
argument_list|)
argument_list|)
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
return|;
block|}
comment|/**    * Returns a new HttpClient that supports both HTTP and HTTPS (with the default test truststore), but     * has no keystore -- so servers requiring client authentication should fail.    */
DECL|method|getSslAwareClientWithNoClientCerts
specifier|private
specifier|static
name|CloseableHttpClient
name|getSslAwareClientWithNoClientCerts
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: This method explicitly does *NOT* use HttpClientUtil code because that
comment|// will muck with the global static HttpClientBuilder / SchemeRegistryProvider
comment|// and we can't do that and still test the entire purpose of what we are trying to test here.
specifier|final
name|SSLTestConfig
name|clientConfig
init|=
operator|new
name|SSLTestConfig
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|SSLConnectionSocketFactory
name|sslFactory
init|=
name|clientConfig
operator|.
name|buildClientSSLConnectionSocketFactory
argument_list|()
decl_stmt|;
assert|assert
literal|null
operator|!=
name|sslFactory
assert|;
specifier|final
name|Registry
argument_list|<
name|ConnectionSocketFactory
argument_list|>
name|socketFactoryReg
init|=
name|RegistryBuilder
operator|.
expr|<
name|ConnectionSocketFactory
operator|>
name|create
argument_list|()
operator|.
name|register
argument_list|(
literal|"https"
argument_list|,
name|sslFactory
argument_list|)
operator|.
name|register
argument_list|(
literal|"http"
argument_list|,
name|PlainConnectionSocketFactory
operator|.
name|INSTANCE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|HttpClientBuilder
name|builder
init|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setConnectionManager
argument_list|(
operator|new
name|PoolingHttpClientConnectionManager
argument_list|(
name|socketFactoryReg
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**     * Generates an HttpSolrClient, either by using the test framework helper method or by direct     * instantiation (determined randomly)    * @see #getHttpSolrClient    */
DECL|method|getRandomizedHttpSolrClient
specifier|public
specifier|static
name|HttpSolrClient
name|getRandomizedHttpSolrClient
parameter_list|(
name|String
name|url
parameter_list|)
block|{
comment|// NOTE: at the moment, SolrTestCaseJ4 already returns "new HttpSolrClient" most of the time,
comment|// so this method may seem redundent -- but the point here is to sanity check 2 things:
comment|// 1) a direct test that "new HttpSolrClient" works given the current JVM/sysprop defaults
comment|// 2) a sanity check that whatever getHttpSolrClient(String) returns will work regardless of
comment|//    current test configuration.
comment|// ... so we are hopefully future proofing against possible changes to SolrTestCaseJ4.getHttpSolrClient
comment|// that "optimize" the test client construction in a way that would prevent us from finding bugs with
comment|// regular HttpSolrClient instantiation.
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
operator|new
name|HttpSolrClient
argument_list|(
name|url
argument_list|)
return|;
block|}
comment|// else...
return|return
name|getHttpSolrClient
argument_list|(
name|url
argument_list|)
return|;
block|}
block|}
end_class
end_unit
