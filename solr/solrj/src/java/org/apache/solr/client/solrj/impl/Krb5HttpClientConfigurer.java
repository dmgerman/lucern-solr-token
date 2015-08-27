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
name|security
operator|.
name|Principal
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|HttpEntity
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
name|HttpEntityEnclosingRequest
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
name|HttpException
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
name|HttpRequest
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
name|HttpRequestInterceptor
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
name|auth
operator|.
name|AuthScope
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
name|auth
operator|.
name|Credentials
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
name|auth
operator|.
name|AuthSchemeRegistry
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
name|auth
operator|.
name|SPNegoSchemeFactory
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
name|DefaultHttpClient
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
name|protocol
operator|.
name|HttpContext
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
name|config
operator|.
name|AuthSchemes
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|BufferedHttpEntity
import|;
end_import
begin_comment
comment|/**  * Kerberos-enabled HttpClientConfigurer  */
end_comment
begin_class
DECL|class|Krb5HttpClientConfigurer
specifier|public
class|class
name|Krb5HttpClientConfigurer
extends|extends
name|HttpClientConfigurer
block|{
DECL|field|LOGIN_CONFIG_PROP
specifier|public
specifier|static
specifier|final
name|String
name|LOGIN_CONFIG_PROP
init|=
literal|"java.security.auth.login.config"
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Krb5HttpClientConfigurer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jaasConfig
specifier|private
specifier|static
specifier|final
name|Configuration
name|jaasConfig
init|=
operator|new
name|SolrJaasConfiguration
argument_list|()
decl_stmt|;
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|SolrParams
name|config
parameter_list|)
block|{
name|super
operator|.
name|configure
argument_list|(
name|httpClient
argument_list|,
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|LOGIN_CONFIG_PROP
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|configValue
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|LOGIN_CONFIG_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|configValue
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Setting up SPNego auth with config: "
operator|+
name|configValue
argument_list|)
expr_stmt|;
specifier|final
name|String
name|useSubjectCredsProp
init|=
literal|"javax.security.auth.useSubjectCredsOnly"
decl_stmt|;
name|String
name|useSubjectCredsVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|useSubjectCredsProp
argument_list|)
decl_stmt|;
comment|// "javax.security.auth.useSubjectCredsOnly" should be false so that the underlying
comment|// authentication mechanism can load the credentials from the JAAS configuration.
if|if
condition|(
name|useSubjectCredsVal
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|useSubjectCredsProp
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|useSubjectCredsVal
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
comment|// Don't overwrite the prop value if it's already been written to something else,
comment|// but log because it is likely the Credentials won't be loaded correctly.
name|logger
operator|.
name|warn
argument_list|(
literal|"System Property: "
operator|+
name|useSubjectCredsProp
operator|+
literal|" set to: "
operator|+
name|useSubjectCredsVal
operator|+
literal|" not false.  SPNego authentication may not be successful."
argument_list|)
expr_stmt|;
block|}
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|jaasConfig
argument_list|)
expr_stmt|;
comment|//Enable only SPNEGO authentication scheme.
name|AuthSchemeRegistry
name|registry
init|=
operator|new
name|AuthSchemeRegistry
argument_list|()
decl_stmt|;
name|registry
operator|.
name|register
argument_list|(
name|AuthSchemes
operator|.
name|SPNEGO
argument_list|,
operator|new
name|SPNegoSchemeFactory
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|setAuthSchemes
argument_list|(
name|registry
argument_list|)
expr_stmt|;
comment|// Get the credentials from the JAAS configuration rather than here
name|Credentials
name|useJaasCreds
init|=
operator|new
name|Credentials
argument_list|()
block|{
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|httpClient
operator|.
name|getCredentialsProvider
argument_list|()
operator|.
name|setCredentials
argument_list|(
name|AuthScope
operator|.
name|ANY
argument_list|,
name|useJaasCreds
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|addRequestInterceptor
argument_list|(
name|bufferedEntityInterceptor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpClient
operator|.
name|getCredentialsProvider
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Set a buffered entity based request interceptor
DECL|field|bufferedEntityInterceptor
specifier|private
name|HttpRequestInterceptor
name|bufferedEntityInterceptor
init|=
operator|new
name|HttpRequestInterceptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpContext
name|context
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
if|if
condition|(
name|request
operator|instanceof
name|HttpEntityEnclosingRequest
condition|)
block|{
name|HttpEntityEnclosingRequest
name|enclosingRequest
init|=
operator|(
operator|(
name|HttpEntityEnclosingRequest
operator|)
name|request
operator|)
decl_stmt|;
name|HttpEntity
name|requestEntity
init|=
name|enclosingRequest
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|enclosingRequest
operator|.
name|setEntity
argument_list|(
operator|new
name|BufferedHttpEntity
argument_list|(
name|requestEntity
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
DECL|class|SolrJaasConfiguration
specifier|private
specifier|static
class|class
name|SolrJaasConfiguration
extends|extends
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
block|{
DECL|field|baseConfig
specifier|private
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
name|baseConfig
decl_stmt|;
comment|// the com.sun.security.jgss appNames
DECL|field|initiateAppNames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|initiateAppNames
init|=
operator|new
name|HashSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"com.sun.security.jgss.krb5.initiate"
argument_list|,
literal|"com.sun.security.jgss.initiate"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|SolrJaasConfiguration
specifier|public
name|SolrJaasConfiguration
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|baseConfig
operator|=
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
name|this
operator|.
name|baseConfig
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAppConfigurationEntry
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|appName
parameter_list|)
block|{
if|if
condition|(
name|baseConfig
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Login prop: "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
name|LOGIN_CONFIG_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|clientAppName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.kerberos.jaas.appname"
argument_list|,
literal|"Client"
argument_list|)
decl_stmt|;
if|if
condition|(
name|initiateAppNames
operator|.
name|contains
argument_list|(
name|appName
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Using AppConfigurationEntry for appName '"
operator|+
name|clientAppName
operator|+
literal|"' instead of: "
operator|+
name|appName
argument_list|)
expr_stmt|;
return|return
name|baseConfig
operator|.
name|getAppConfigurationEntry
argument_list|(
name|clientAppName
argument_list|)
return|;
block|}
return|return
name|baseConfig
operator|.
name|getAppConfigurationEntry
argument_list|(
name|appName
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
