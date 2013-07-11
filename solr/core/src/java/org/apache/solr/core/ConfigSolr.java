begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|util
operator|.
name|DOMUtil
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
name|PropertiesUtil
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|FileInputStream
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
name|List
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
name|Properties
import|;
end_import
begin_class
DECL|class|ConfigSolr
specifier|public
specifier|abstract
class|class
name|ConfigSolr
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConfigSolr
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOLR_XML_FILE
specifier|public
specifier|final
specifier|static
name|String
name|SOLR_XML_FILE
init|=
literal|"solr.xml"
decl_stmt|;
DECL|method|fromFile
specifier|public
specifier|static
name|ConfigSolr
name|fromFile
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|File
name|configFile
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Loading container configuration from {}"
argument_list|,
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|inputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|configFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{} does not exist, using default configuration"
argument_list|,
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|inputStream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|ConfigSolrXmlOld
operator|.
name|DEF_SOLR_XML
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inputStream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
expr_stmt|;
block|}
return|return
name|fromInputStream
argument_list|(
name|loader
argument_list|,
name|inputStream
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not load SOLR configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fromString
specifier|public
specifier|static
name|ConfigSolr
name|fromString
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|xml
parameter_list|)
block|{
return|return
name|fromInputStream
argument_list|(
name|loader
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fromInputStream
specifier|public
specifier|static
name|ConfigSolr
name|fromInputStream
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|InputStream
name|is
parameter_list|)
block|{
try|try
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|loader
argument_list|,
literal|null
argument_list|,
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//config.substituteProperties();
return|return
name|fromConfig
argument_list|(
name|config
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|fromSolrHome
specifier|public
specifier|static
name|ConfigSolr
name|fromSolrHome
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|solrHome
parameter_list|)
block|{
return|return
name|fromFile
argument_list|(
name|loader
argument_list|,
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
name|SOLR_XML_FILE
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fromConfig
specifier|public
specifier|static
name|ConfigSolr
name|fromConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|boolean
name|oldStyle
init|=
operator|(
name|config
operator|.
name|getNode
argument_list|(
literal|"solr/cores"
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
operator|)
decl_stmt|;
return|return
name|oldStyle
condition|?
operator|new
name|ConfigSolrXmlOld
argument_list|(
name|config
argument_list|)
else|:
operator|new
name|ConfigSolrXml
argument_list|(
name|config
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getShardHandlerFactoryPluginInfo
specifier|public
name|PluginInfo
name|getShardHandlerFactoryPluginInfo
parameter_list|()
block|{
name|Node
name|node
init|=
name|config
operator|.
name|getNode
argument_list|(
name|getShardHandlerFactoryConfigPath
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|(
name|node
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|PluginInfo
argument_list|(
name|node
argument_list|,
literal|"shardHandlerFactory"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getUnsubsititutedShardHandlerFactoryPluginNode
specifier|public
name|Node
name|getUnsubsititutedShardHandlerFactoryPluginNode
parameter_list|()
block|{
return|return
name|config
operator|.
name|getUnsubstitutedNode
argument_list|(
name|getShardHandlerFactoryConfigPath
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getShardHandlerFactoryConfigPath
specifier|protected
specifier|abstract
name|String
name|getShardHandlerFactoryConfigPath
parameter_list|()
function_decl|;
comment|// Ugly for now, but we'll at least be able to centralize all of the differences between 4x and 5x.
DECL|enum|CfgProp
specifier|public
specifier|static
enum|enum
name|CfgProp
block|{
DECL|enum constant|SOLR_ADMINHANDLER
name|SOLR_ADMINHANDLER
block|,
DECL|enum constant|SOLR_CORELOADTHREADS
name|SOLR_CORELOADTHREADS
block|,
DECL|enum constant|SOLR_COREROOTDIRECTORY
name|SOLR_COREROOTDIRECTORY
block|,
DECL|enum constant|SOLR_DISTRIBUPDATECONNTIMEOUT
name|SOLR_DISTRIBUPDATECONNTIMEOUT
block|,
DECL|enum constant|SOLR_DISTRIBUPDATESOTIMEOUT
name|SOLR_DISTRIBUPDATESOTIMEOUT
block|,
DECL|enum constant|SOLR_HOST
name|SOLR_HOST
block|,
DECL|enum constant|SOLR_HOSTCONTEXT
name|SOLR_HOSTCONTEXT
block|,
DECL|enum constant|SOLR_HOSTPORT
name|SOLR_HOSTPORT
block|,
DECL|enum constant|SOLR_LEADERVOTEWAIT
name|SOLR_LEADERVOTEWAIT
block|,
DECL|enum constant|SOLR_LOGGING_CLASS
name|SOLR_LOGGING_CLASS
block|,
DECL|enum constant|SOLR_LOGGING_ENABLED
name|SOLR_LOGGING_ENABLED
block|,
DECL|enum constant|SOLR_LOGGING_WATCHER_SIZE
name|SOLR_LOGGING_WATCHER_SIZE
block|,
DECL|enum constant|SOLR_LOGGING_WATCHER_THRESHOLD
name|SOLR_LOGGING_WATCHER_THRESHOLD
block|,
DECL|enum constant|SOLR_MANAGEMENTPATH
name|SOLR_MANAGEMENTPATH
block|,
DECL|enum constant|SOLR_SHAREDLIB
name|SOLR_SHAREDLIB
block|,
DECL|enum constant|SOLR_SHARESCHEMA
name|SOLR_SHARESCHEMA
block|,
DECL|enum constant|SOLR_TRANSIENTCACHESIZE
name|SOLR_TRANSIENTCACHESIZE
block|,
DECL|enum constant|SOLR_GENERICCORENODENAMES
name|SOLR_GENERICCORENODENAMES
block|,
DECL|enum constant|SOLR_ZKCLIENTTIMEOUT
name|SOLR_ZKCLIENTTIMEOUT
block|,
DECL|enum constant|SOLR_ZKHOST
name|SOLR_ZKHOST
block|,
comment|//TODO: Remove all of these elements for 5.0
DECL|enum constant|SOLR_PERSISTENT
name|SOLR_PERSISTENT
block|,
DECL|enum constant|SOLR_CORES_DEFAULT_CORE_NAME
name|SOLR_CORES_DEFAULT_CORE_NAME
block|,
DECL|enum constant|SOLR_ADMINPATH
name|SOLR_ADMINPATH
block|}
DECL|field|config
specifier|protected
name|Config
name|config
decl_stmt|;
DECL|field|propMap
specifier|protected
name|Map
argument_list|<
name|CfgProp
argument_list|,
name|String
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<
name|CfgProp
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ConfigSolr
specifier|public
name|ConfigSolr
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|// for extension& testing.
DECL|method|ConfigSolr
specifier|protected
name|ConfigSolr
parameter_list|()
block|{    }
DECL|method|getConfig
specifier|public
name|Config
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|int
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|propMap
operator|.
name|get
argument_list|(
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|val
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|val
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|(
name|val
operator|==
literal|null
operator|)
condition|?
name|def
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|boolean
name|defValue
parameter_list|)
block|{
name|String
name|val
init|=
name|propMap
operator|.
name|get
argument_list|(
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|val
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|val
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|(
name|val
operator|==
literal|null
operator|)
condition|?
name|defValue
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|propMap
operator|.
name|get
argument_list|(
name|prop
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|val
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|val
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|(
name|val
operator|==
literal|null
operator|)
condition|?
name|def
else|:
name|val
return|;
block|}
comment|// For saving the original property, ${} syntax and all.
DECL|method|getOrigProp
specifier|public
name|String
name|getOrigProp
parameter_list|(
name|CfgProp
name|prop
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|propMap
operator|.
name|get
argument_list|(
name|prop
argument_list|)
decl_stmt|;
return|return
operator|(
name|val
operator|==
literal|null
operator|)
condition|?
name|def
else|:
name|val
return|;
block|}
DECL|method|getSolrProperties
specifier|public
name|Properties
name|getSolrProperties
parameter_list|(
name|String
name|path
parameter_list|)
block|{
try|try
block|{
return|return
name|readProperties
argument_list|(
operator|(
operator|(
name|NodeList
operator|)
name|config
operator|.
name|evaluate
argument_list|(
name|path
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
operator|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|readProperties
specifier|protected
name|Properties
name|readProperties
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
name|XPath
name|xpath
init|=
name|config
operator|.
name|getXPath
argument_list|()
decl_stmt|;
name|NodeList
name|props
init|=
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"property"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|props
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|prop
init|=
name|props
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|prop
argument_list|,
literal|"name"
argument_list|)
argument_list|,
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|prop
argument_list|,
literal|"value"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
DECL|method|substituteProperties
specifier|public
specifier|abstract
name|void
name|substituteProperties
parameter_list|()
function_decl|;
DECL|method|getAllCoreNames
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getAllCoreNames
parameter_list|()
function_decl|;
DECL|method|getProperty
specifier|public
specifier|abstract
name|String
name|getProperty
parameter_list|(
name|String
name|coreName
parameter_list|,
name|String
name|property
parameter_list|,
name|String
name|defaultVal
parameter_list|)
function_decl|;
DECL|method|readCoreProperties
specifier|public
specifier|abstract
name|Properties
name|readCoreProperties
parameter_list|(
name|String
name|coreName
parameter_list|)
function_decl|;
DECL|method|readCoreAttributes
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readCoreAttributes
parameter_list|(
name|String
name|coreName
parameter_list|)
function_decl|;
block|}
end_class
end_unit
