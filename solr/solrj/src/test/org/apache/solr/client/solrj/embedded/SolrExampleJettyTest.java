begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
name|embedded
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
name|client
operator|.
name|solrj
operator|.
name|SolrExampleTests
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
name|HttpSolrServer
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
name|ExternalPaths
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
name|util
operator|.
name|Map
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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/**  * TODO? perhaps use:  *  http://docs.codehaus.org/display/JETTY/ServletTester  * rather then open a real connection?  *   */
end_comment
begin_class
DECL|class|SolrExampleJettyTest
specifier|public
class|class
name|SolrExampleJettyTest
extends|extends
name|SolrExampleTests
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrExampleJettyTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|manageSslProps
specifier|private
specifier|static
name|boolean
name|manageSslProps
init|=
literal|true
decl_stmt|;
DECL|field|TEST_KEYSTORE
specifier|private
specifier|static
specifier|final
name|File
name|TEST_KEYSTORE
init|=
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SOURCE_HOME
argument_list|,
literal|"example/etc/solrtest.keystore"
argument_list|)
decl_stmt|;
DECL|field|SSL_PROPS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|SSL_PROPS
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
static|static
block|{
name|SSL_PROPS
operator|.
name|put
argument_list|(
literal|"tests.jettySsl"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|SSL_PROPS
operator|.
name|put
argument_list|(
literal|"tests.jettySsl.clientAuth"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|SSL_PROPS
operator|.
name|put
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|TEST_KEYSTORE
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|SSL_PROPS
operator|.
name|put
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
literal|"secret"
argument_list|)
expr_stmt|;
name|SSL_PROPS
operator|.
name|put
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|TEST_KEYSTORE
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|SSL_PROPS
operator|.
name|put
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
literal|"secret"
argument_list|)
expr_stmt|;
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
comment|// // //
comment|// :TODO: SOLR-4394 promote SSL up to SolrJettyTestBase?
comment|// consume the same amount of random no matter what
specifier|final
name|boolean
name|trySsl
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trySslClientAuth
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// only randomize SSL if none of the SSL_PROPS are already set
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|sysprops
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|SSL_PROPS
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|sysprops
operator|.
name|containsKey
argument_list|(
name|prop
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"System property explicitly set, so skipping randomized ssl properties: "
operator|+
name|prop
argument_list|)
expr_stmt|;
name|manageSslProps
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"test keystore does not exist, can't be used for randomized "
operator|+
literal|"ssl testing: "
operator|+
name|TEST_KEYSTORE
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|TEST_KEYSTORE
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|manageSslProps
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Randomized ssl ({}) and clientAuth ({})"
argument_list|,
name|trySsl
argument_list|,
name|trySslClientAuth
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|SSL_PROPS
operator|.
name|keySet
argument_list|()
control|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|prop
argument_list|,
name|SSL_PROPS
operator|.
name|get
argument_list|(
name|prop
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now explicitly re-set the two random values
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.jettySsl"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|trySsl
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.jettySsl.clientAuth"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|trySslClientAuth
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// // //
name|createJetty
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterTest
specifier|public
specifier|static
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|manageSslProps
condition|)
block|{
for|for
control|(
name|String
name|prop
range|:
name|SSL_PROPS
operator|.
name|keySet
argument_list|()
control|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBadSetup
specifier|public
name|void
name|testBadSetup
parameter_list|()
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
literal|"http://127.0.0.1/?core=xxx"
decl_stmt|;
name|HttpSolrServer
name|s
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"CommonsHttpSolrServer should not allow a path with a parameter: "
operator|+
name|s
operator|.
name|getBaseURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit
