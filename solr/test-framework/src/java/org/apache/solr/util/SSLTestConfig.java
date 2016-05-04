begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
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
name|security
operator|.
name|KeyManagementException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStoreException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandomSpi
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|UnrecoverableKeyException
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
name|conn
operator|.
name|scheme
operator|.
name|Scheme
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
name|scheme
operator|.
name|SchemeRegistry
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
name|SSLConnectionSocketFactory
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
name|SSLContexts
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
name|SSLContextBuilder
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
name|TrustSelfSignedStrategy
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
name|SSLConfig
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
name|impl
operator|.
name|HttpClientUtil
operator|.
name|SchemaRegistryProvider
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
name|SolrHttpClientBuilder
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
name|util
operator|.
name|resource
operator|.
name|Resource
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
name|util
operator|.
name|security
operator|.
name|CertificateUtils
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
name|util
operator|.
name|ssl
operator|.
name|SslContextFactory
import|;
end_import
begin_class
DECL|class|SSLTestConfig
specifier|public
class|class
name|SSLTestConfig
extends|extends
name|SSLConfig
block|{
DECL|field|TEST_KEYSTORE
specifier|public
specifier|static
name|File
name|TEST_KEYSTORE
init|=
name|ExternalPaths
operator|.
name|SERVER_HOME
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SERVER_HOME
argument_list|,
literal|"../etc/test/solrtest.keystore"
argument_list|)
decl_stmt|;
DECL|field|TEST_KEYSTORE_PATH
specifier|private
specifier|static
name|String
name|TEST_KEYSTORE_PATH
init|=
name|TEST_KEYSTORE
operator|!=
literal|null
operator|&&
name|TEST_KEYSTORE
operator|.
name|exists
argument_list|()
condition|?
name|TEST_KEYSTORE
operator|.
name|getAbsolutePath
argument_list|()
else|:
literal|null
decl_stmt|;
DECL|field|TEST_KEYSTORE_PASSWORD
specifier|private
specifier|static
name|String
name|TEST_KEYSTORE_PASSWORD
init|=
literal|"secret"
decl_stmt|;
DECL|method|SSLTestConfig
specifier|public
name|SSLTestConfig
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|SSLTestConfig
specifier|public
name|SSLTestConfig
parameter_list|(
name|boolean
name|useSSL
parameter_list|,
name|boolean
name|clientAuth
parameter_list|)
block|{
name|this
argument_list|(
name|useSSL
argument_list|,
name|clientAuth
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
block|}
DECL|method|SSLTestConfig
specifier|public
name|SSLTestConfig
parameter_list|(
name|boolean
name|useSSL
parameter_list|,
name|boolean
name|clientAuth
parameter_list|,
name|String
name|keyStore
parameter_list|,
name|String
name|keyStorePassword
parameter_list|,
name|String
name|trustStore
parameter_list|,
name|String
name|trustStorePassword
parameter_list|)
block|{
name|super
argument_list|(
name|useSSL
argument_list|,
name|clientAuth
argument_list|,
name|keyStore
argument_list|,
name|keyStorePassword
argument_list|,
name|trustStore
argument_list|,
name|trustStorePassword
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link SchemaRegistryProvider} for HTTP<b>clients</b> to use when communicating with servers     * which have been configured based on the settings of this object.  When {@link #isSSLMode} is true, this     *<code>SchemaRegistryProvider</code> will<i>only</i> support HTTPS (no HTTP scheme) using the     * appropriate certs.  When {@link #isSSLMode} is false,<i>only</i> HTTP (no HTTPS scheme) will be     * supported.    */
DECL|method|buildClientSchemaRegistryProvider
specifier|public
name|SchemaRegistryProvider
name|buildClientSchemaRegistryProvider
parameter_list|()
block|{
if|if
condition|(
name|isSSLMode
argument_list|()
condition|)
block|{
name|SSLConnectionSocketFactory
name|sslConnectionFactory
init|=
name|buildClientSSLConnectionSocketFactory
argument_list|()
decl_stmt|;
assert|assert
literal|null
operator|!=
name|sslConnectionFactory
assert|;
return|return
operator|new
name|SSLSchemaRegistryProvider
argument_list|(
name|sslConnectionFactory
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|HTTP_ONLY_SCHEMA_PROVIDER
return|;
block|}
block|}
comment|/**    * Builds a new SSLContext for HTTP<b>clients</b> to use when communicating with servers which have     * been configured based on the settings of this object.      *    * NOTE: Uses a completely insecure {@link SecureRandom} instance to prevent tests from blocking     * due to lack of entropy, also explicitly allows the use of self-signed     * certificates (since that's what is almost always used during testing).    */
DECL|method|buildClientSSLContext
specifier|public
name|SSLContext
name|buildClientSSLContext
parameter_list|()
throws|throws
name|KeyManagementException
throws|,
name|UnrecoverableKeyException
throws|,
name|NoSuchAlgorithmException
throws|,
name|KeyStoreException
block|{
assert|assert
name|isSSLMode
argument_list|()
assert|;
name|SSLContextBuilder
name|builder
init|=
name|SSLContexts
operator|.
name|custom
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setSecureRandom
argument_list|(
name|NullSecureRandom
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
comment|// NOTE: KeyStore& TrustStore are swapped because they are from configured from server perspective...
comment|// we are a client - our keystore contains the keys the server trusts, and vice versa
name|builder
operator|.
name|loadTrustMaterial
argument_list|(
name|buildKeyStore
argument_list|(
name|getKeyStore
argument_list|()
argument_list|,
name|getKeyStorePassword
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TrustSelfSignedStrategy
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
if|if
condition|(
name|isClientAuthMode
argument_list|()
condition|)
block|{
name|builder
operator|.
name|loadKeyMaterial
argument_list|(
name|buildKeyStore
argument_list|(
name|getTrustStore
argument_list|()
argument_list|,
name|getTrustStorePassword
argument_list|()
argument_list|)
argument_list|,
name|getTrustStorePassword
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Builds a new SSLContext for jetty servers which have been configured based on the settings of     * this object.    *    * NOTE: Uses a completely insecure {@link SecureRandom} instance to prevent tests from blocking     * due to lack of entropy, also explicitly allows the use of self-signed     * certificates (since that's what is almost always used during testing).    * almost always used during testing).     */
DECL|method|buildServerSSLContext
specifier|public
name|SSLContext
name|buildServerSSLContext
parameter_list|()
throws|throws
name|KeyManagementException
throws|,
name|UnrecoverableKeyException
throws|,
name|NoSuchAlgorithmException
throws|,
name|KeyStoreException
block|{
assert|assert
name|isSSLMode
argument_list|()
assert|;
name|SSLContextBuilder
name|builder
init|=
name|SSLContexts
operator|.
name|custom
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setSecureRandom
argument_list|(
name|NullSecureRandom
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|loadKeyMaterial
argument_list|(
name|buildKeyStore
argument_list|(
name|getKeyStore
argument_list|()
argument_list|,
name|getKeyStorePassword
argument_list|()
argument_list|)
argument_list|,
name|getKeyStorePassword
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isClientAuthMode
argument_list|()
condition|)
block|{
name|builder
operator|.
name|loadTrustMaterial
argument_list|(
name|buildKeyStore
argument_list|(
name|getTrustStore
argument_list|()
argument_list|,
name|getTrustStorePassword
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TrustSelfSignedStrategy
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns an SslContextFactory using {@link #buildServerSSLContext} if SSL should be used, else returns null.    */
annotation|@
name|Override
DECL|method|createContextFactory
specifier|public
name|SslContextFactory
name|createContextFactory
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSSLMode
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// else...
name|SslContextFactory
name|factory
init|=
operator|new
name|SslContextFactory
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|factory
operator|.
name|setSslContext
argument_list|(
name|buildServerSSLContext
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"ssl context init failure: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|factory
operator|.
name|setNeedClientAuth
argument_list|(
name|isClientAuthMode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
comment|/**    * Constructs a KeyStore using the specified filename and password    */
DECL|method|buildKeyStore
specifier|protected
specifier|static
name|KeyStore
name|buildKeyStore
parameter_list|(
name|String
name|keyStoreLocation
parameter_list|,
name|String
name|password
parameter_list|)
block|{
try|try
block|{
return|return
name|CertificateUtils
operator|.
name|getKeyStore
argument_list|(
name|Resource
operator|.
name|newResource
argument_list|(
name|keyStoreLocation
argument_list|)
argument_list|,
literal|"JKS"
argument_list|,
literal|null
argument_list|,
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to build KeyStore from file: "
operator|+
name|keyStoreLocation
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**     * Constructs a new SSLConnectionSocketFactory for HTTP<b>clients</b> to use when communicating     * with servers which have been configured based on the settings of this object. Will return null    * unless {@link #isSSLMode} is true.    */
DECL|method|buildClientSSLConnectionSocketFactory
specifier|public
name|SSLConnectionSocketFactory
name|buildClientSSLConnectionSocketFactory
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSSLMode
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SSLConnectionSocketFactory
name|sslConnectionFactory
decl_stmt|;
try|try
block|{
name|boolean
name|sslCheckPeerName
init|=
name|toBooleanDefaultIfNull
argument_list|(
name|toBooleanObject
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|HttpClientUtil
operator|.
name|SYS_PROP_CHECK_PEER_NAME
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SSLContext
name|sslContext
init|=
name|buildClientSSLContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|sslCheckPeerName
operator|==
literal|false
condition|)
block|{
name|sslConnectionFactory
operator|=
operator|new
name|SSLConnectionSocketFactory
argument_list|(
name|sslContext
argument_list|,
name|SSLSocketFactory
operator|.
name|ALLOW_ALL_HOSTNAME_VERIFIER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sslConnectionFactory
operator|=
operator|new
name|SSLConnectionSocketFactory
argument_list|(
name|sslContext
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeyManagementException
decl||
name|UnrecoverableKeyException
decl||
name|NoSuchAlgorithmException
decl||
name|KeyStoreException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to setup https scheme for HTTPClient to test SSL."
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|sslConnectionFactory
return|;
block|}
comment|/** A SchemaRegistryProvider that only knows about SSL using a specified SSLConnectionSocketFactory */
DECL|class|SSLSchemaRegistryProvider
specifier|private
specifier|static
class|class
name|SSLSchemaRegistryProvider
extends|extends
name|SchemaRegistryProvider
block|{
DECL|field|sslConnectionFactory
specifier|private
specifier|final
name|SSLConnectionSocketFactory
name|sslConnectionFactory
decl_stmt|;
DECL|method|SSLSchemaRegistryProvider
specifier|public
name|SSLSchemaRegistryProvider
parameter_list|(
name|SSLConnectionSocketFactory
name|sslConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|sslConnectionFactory
operator|=
name|sslConnectionFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSchemaRegistry
specifier|public
name|Registry
argument_list|<
name|ConnectionSocketFactory
argument_list|>
name|getSchemaRegistry
parameter_list|()
block|{
return|return
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
name|sslConnectionFactory
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|/** A SchemaRegistryProvider that only knows about HTTP */
DECL|field|HTTP_ONLY_SCHEMA_PROVIDER
specifier|private
specifier|static
specifier|final
name|SchemaRegistryProvider
name|HTTP_ONLY_SCHEMA_PROVIDER
init|=
operator|new
name|SchemaRegistryProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Registry
argument_list|<
name|ConnectionSocketFactory
argument_list|>
name|getSchemaRegistry
parameter_list|()
block|{
return|return
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
literal|"http"
argument_list|,
name|PlainConnectionSocketFactory
operator|.
name|getSocketFactory
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|toBooleanDefaultIfNull
specifier|public
specifier|static
name|boolean
name|toBooleanDefaultIfNull
parameter_list|(
name|Boolean
name|bool
parameter_list|,
name|boolean
name|valueIfNull
parameter_list|)
block|{
if|if
condition|(
name|bool
operator|==
literal|null
condition|)
block|{
return|return
name|valueIfNull
return|;
block|}
return|return
name|bool
operator|.
name|booleanValue
argument_list|()
condition|?
literal|true
else|:
literal|false
return|;
block|}
DECL|method|toBooleanObject
specifier|public
specifier|static
name|Boolean
name|toBooleanObject
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|str
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
elseif|else
if|if
condition|(
literal|"false"
operator|.
name|equalsIgnoreCase
argument_list|(
name|str
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
comment|// no match
return|return
literal|null
return|;
block|}
comment|/**    * @deprecated this method has very little practical use, in most cases you'll want to use     * {@link SSLContext#setDefault} with {@link #buildClientSSLContext} instead.    */
annotation|@
name|Deprecated
DECL|method|setSSLSystemProperties
specifier|public
specifier|static
name|void
name|setSSLSystemProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated this method has very little practical use, in most cases you'll want to use     * {@link SSLContext#setDefault} with {@link #buildClientSSLContext} instead.    */
annotation|@
name|Deprecated
DECL|method|clearSSLSystemProperties
specifier|public
specifier|static
name|void
name|clearSSLSystemProperties
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|)
expr_stmt|;
block|}
comment|/**    * A mocked up instance of SecureRandom that always does the minimal amount of work to generate     * "random" numbers.  This is to prevent blocking issues that arise in platform default     * SecureRandom instances due to too many instances / not enough random entropy.      * Tests do not need secure SSL.    */
DECL|class|NullSecureRandom
specifier|private
specifier|static
class|class
name|NullSecureRandom
extends|extends
name|SecureRandom
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|SecureRandom
name|INSTANCE
init|=
operator|new
name|NullSecureRandom
argument_list|()
decl_stmt|;
comment|/** SPI Used to init all instances */
DECL|field|NULL_SPI
specifier|private
specifier|static
specifier|final
name|SecureRandomSpi
name|NULL_SPI
init|=
operator|new
name|SecureRandomSpi
argument_list|()
block|{
comment|/** NOOP: returns new uninitialized byte[] */
specifier|public
name|byte
index|[]
name|engineGenerateSeed
parameter_list|(
name|int
name|numBytes
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[
name|numBytes
index|]
return|;
block|}
comment|/** NOOP */
specifier|public
name|void
name|engineNextBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
comment|/* NOOP */
block|}
comment|/** NOOP */
specifier|public
name|void
name|engineSetSeed
parameter_list|(
name|byte
index|[]
name|seed
parameter_list|)
block|{
comment|/* NOOP */
block|}
block|}
decl_stmt|;
DECL|method|NullSecureRandom
specifier|private
name|NullSecureRandom
parameter_list|()
block|{
name|super
argument_list|(
name|NULL_SPI
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** NOOP: returns new uninitialized byte[] */
DECL|method|generateSeed
specifier|public
name|byte
index|[]
name|generateSeed
parameter_list|(
name|int
name|numBytes
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[
name|numBytes
index|]
return|;
block|}
comment|/** NOOP */
DECL|method|nextBytes
specifier|synchronized
specifier|public
name|void
name|nextBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
comment|/* NOOP */
block|}
comment|/** NOOP */
DECL|method|setSeed
specifier|synchronized
specifier|public
name|void
name|setSeed
parameter_list|(
name|byte
index|[]
name|seed
parameter_list|)
block|{
comment|/* NOOP */
block|}
comment|/** NOOP */
DECL|method|setSeed
specifier|synchronized
specifier|public
name|void
name|setSeed
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
comment|/* NOOP */
block|}
block|}
block|}
end_class
end_unit
