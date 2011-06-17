begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|*
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|URLDataSource
operator|.
name|*
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
name|Properties
import|;
end_import
begin_comment
comment|/**  *<p> A data source implementation which can be used to read binary streams using HTTP.</p><p/><p> Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a> for more  * details.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.5  */
end_comment
begin_class
DECL|class|BinURLDataSource
specifier|public
class|class
name|BinURLDataSource
extends|extends
name|DataSource
argument_list|<
name|InputStream
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BinURLDataSource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|baseUrl
specifier|private
name|String
name|baseUrl
decl_stmt|;
DECL|field|connectionTimeout
specifier|private
name|int
name|connectionTimeout
init|=
name|CONNECTION_TIMEOUT
decl_stmt|;
DECL|field|readTimeout
specifier|private
name|int
name|readTimeout
init|=
name|READ_TIMEOUT
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|initProps
specifier|private
name|Properties
name|initProps
decl_stmt|;
DECL|method|BinURLDataSource
specifier|public
name|BinURLDataSource
parameter_list|()
block|{ }
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|initProps
operator|=
name|initProps
expr_stmt|;
name|baseUrl
operator|=
name|getInitPropWithReplacements
argument_list|(
name|BASE_URL
argument_list|)
expr_stmt|;
name|String
name|cTimeout
init|=
name|getInitPropWithReplacements
argument_list|(
name|CONNECTION_TIMEOUT_FIELD_NAME
argument_list|)
decl_stmt|;
name|String
name|rTimeout
init|=
name|getInitPropWithReplacements
argument_list|(
name|READ_TIMEOUT_FIELD_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|cTimeout
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connectionTimeout
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cTimeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid connection timeout: "
operator|+
name|cTimeout
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rTimeout
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|readTimeout
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|rTimeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid read timeout: "
operator|+
name|rTimeout
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getData
specifier|public
name|InputStream
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|URL
name|url
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|URIMETHOD
operator|.
name|matcher
argument_list|(
name|query
argument_list|)
operator|.
name|find
argument_list|()
condition|)
name|url
operator|=
operator|new
name|URL
argument_list|(
name|query
argument_list|)
expr_stmt|;
else|else
name|url
operator|=
operator|new
name|URL
argument_list|(
name|baseUrl
operator|+
name|query
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Accessing URL: "
operator|+
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|URLConnection
name|conn
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setConnectTimeout
argument_list|(
name|connectionTimeout
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setReadTimeout
argument_list|(
name|readTimeout
argument_list|)
expr_stmt|;
return|return
name|conn
operator|.
name|getInputStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception thrown while getting data"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Exception in invoking url "
operator|+
name|url
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|//unreachable
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{ }
DECL|method|getInitPropWithReplacements
specifier|private
name|String
name|getInitPropWithReplacements
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
specifier|final
name|String
name|expr
init|=
name|initProps
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|context
operator|.
name|replaceTokens
argument_list|(
name|expr
argument_list|)
return|;
block|}
block|}
end_class
end_unit
