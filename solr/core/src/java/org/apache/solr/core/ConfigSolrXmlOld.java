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
name|HashSet
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
name|Locale
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
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|NamedNodeMap
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
name|SAXException
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|ConfigSolrXmlOld
specifier|public
class|class
name|ConfigSolrXmlOld
extends|extends
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
name|ConfigSolrXmlOld
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|coreNodes
specifier|private
name|NodeList
name|coreNodes
init|=
literal|null
decl_stmt|;
DECL|method|ConfigSolrXmlOld
specifier|public
name|ConfigSolrXmlOld
parameter_list|(
name|Config
name|config
parameter_list|,
name|CoreContainer
name|container
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|checkForIllegalConfig
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|fillPropMap
argument_list|()
expr_stmt|;
name|initCoreList
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
DECL|method|checkForIllegalConfig
specifier|private
name|void
name|checkForIllegalConfig
parameter_list|(
name|CoreContainer
name|container
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do sanity checks - we don't want to find new style
comment|// config
name|failIfFound
argument_list|(
literal|"solr/str[@name='adminHandler']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/int[@name='coreLoadThreads']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/str[@name='coreRootDirectory']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='distribUpdateConnTimeout']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='distribUpdateSoTimeout']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/str[@name='host']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/str[@name='hostContext']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='hostPort']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='leaderVoteWait']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='genericCoreNodeNames']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/str[@name='managementPath']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/str[@name='sharedLib']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/str[@name='shareSchema']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/int[@name='transientCacheSize']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='zkClientTimeout']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/solrcloud/int[@name='zkHost']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/str[@name='class']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/str[@name='enabled']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/watcher/int[@name='size']"
argument_list|)
expr_stmt|;
name|failIfFound
argument_list|(
literal|"solr/logging/watcher/int[@name='threshold']"
argument_list|)
expr_stmt|;
block|}
DECL|method|failIfFound
specifier|private
name|void
name|failIfFound
parameter_list|(
name|String
name|xPath
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|getVal
argument_list|(
name|xPath
argument_list|,
literal|false
argument_list|)
operator|!=
literal|null
condition|)
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
literal|"Should not have found "
operator|+
name|xPath
operator|+
literal|" solr.xml may be a mix of old and new style formats."
argument_list|)
throw|;
block|}
block|}
DECL|method|fillPropMap
specifier|private
name|void
name|fillPropMap
parameter_list|()
block|{
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_CORELOADTHREADS
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/@coreLoadThreads"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHAREDLIB
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/@sharedLib"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKHOST
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/@zkHost"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_CLASS
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/logging/@class"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_ENABLED
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/logging/@enabled"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_SIZE
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/logging/watcher/@size"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_THRESHOLD
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/logging/watcher/@threshold"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ADMINHANDLER
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@adminHandler"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATECONNTIMEOUT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@distribUpdateConnTimeout"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATESOTIMEOUT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@distribUpdateSoTimeout"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOST
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@host"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOSTCONTEXT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@hostContext"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOSTPORT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@hostPort"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_LEADERVOTEWAIT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@leaderVoteWait"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_GENERICCORENODENAMES
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@genericCoreNodeNames"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_MANAGEMENTPATH
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@managementPath"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARESCHEMA
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@shareSchema"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_TRANSIENTCACHESIZE
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@transientCacheSize"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKCLIENTTIMEOUT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@zkClientTimeout"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_CLASS
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/shardHandlerFactory/@class"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_NAME
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/shardHandlerFactory/@name"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_CONNTIMEOUT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/shardHandlerFactory/int[@name='connTimeout']"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_SHARDHANDLERFACTORY_SOCKETTIMEOUT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/shardHandlerFactory/int[@name='socketTimeout']"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// These have no counterpart in 5.0, asking, for any of these in Solr 5.0
comment|// will result in an error being
comment|// thrown.
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_CORES_DEFAULT_CORE_NAME
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@defaultCoreName"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_PERSISTENT
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/@persistent"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|CfgProp
operator|.
name|SOLR_ADMINPATH
argument_list|,
name|config
operator|.
name|getVal
argument_list|(
literal|"solr/cores/@adminPath"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initCoreList
specifier|private
name|void
name|initCoreList
parameter_list|(
name|CoreContainer
name|container
parameter_list|)
throws|throws
name|IOException
block|{
name|coreNodes
operator|=
operator|(
name|NodeList
operator|)
name|config
operator|.
name|evaluate
argument_list|(
literal|"solr/cores/core"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
expr_stmt|;
comment|// Check a couple of error conditions
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// for duplicate names
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dirs
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
comment|// for duplicate
comment|// data dirs.
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|coreNodes
operator|.
name|getLength
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|Node
name|node
init|=
name|coreNodes
operator|.
name|item
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|dataDir
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
name|dataDir
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|dataDir
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"More than one core defined for core named %s"
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|instDir
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_INSTDIR
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|instDir
operator|!=
literal|null
condition|)
name|instDir
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|instDir
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataDir
operator|!=
literal|null
condition|)
block|{
name|String
name|absData
init|=
literal|null
decl_stmt|;
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|absData
operator|=
name|dataFile
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|instDir
operator|!=
literal|null
condition|)
block|{
name|File
name|instFile
init|=
operator|new
name|File
argument_list|(
name|instDir
argument_list|)
decl_stmt|;
name|absData
operator|=
operator|new
name|File
argument_list|(
name|instFile
argument_list|,
name|dataDir
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|absData
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|dirs
operator|.
name|containsKey
argument_list|(
name|absData
argument_list|)
condition|)
block|{
name|dirs
operator|.
name|put
argument_list|(
name|absData
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"More than one core points to data dir %s. They are in %s and %s"
argument_list|,
name|absData
argument_list|,
name|dirs
operator|.
name|get
argument_list|(
name|absData
argument_list|)
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|readCoreAttributes
specifier|public
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
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
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
synchronized|synchronized
init|(
name|coreNodes
init|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|coreNodes
operator|.
name|getLength
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|Node
name|node
init|=
name|coreNodes
operator|.
name|item
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|coreName
operator|.
name|equals
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
literal|null
argument_list|)
argument_list|)
condition|)
block|{
name|NamedNodeMap
name|attributes
init|=
name|node
operator|.
name|getAttributes
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|attribute
init|=
name|attributes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|attribute
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|CoreDescriptor
operator|.
name|CORE_DATADIR
operator|.
name|equals
argument_list|(
name|attribute
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|||
name|CoreDescriptor
operator|.
name|CORE_INSTDIR
operator|.
name|equals
argument_list|(
name|attribute
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|val
operator|.
name|indexOf
argument_list|(
literal|'$'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|val
operator|=
operator|(
name|val
operator|!=
literal|null
operator|&&
operator|!
name|val
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|)
condition|?
name|val
operator|+
literal|'/'
else|:
name|val
expr_stmt|;
block|}
block|}
name|attrs
operator|.
name|put
argument_list|(
name|attribute
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|attrs
return|;
block|}
block|}
block|}
return|return
name|attrs
return|;
block|}
annotation|@
name|Override
DECL|method|getAllCoreNames
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllCoreNames
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|coreNodes
init|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|coreNodes
operator|.
name|getLength
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|Node
name|node
init|=
name|coreNodes
operator|.
name|item
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
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
block|{
synchronized|synchronized
init|(
name|coreNodes
init|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|coreNodes
operator|.
name|getLength
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|Node
name|node
init|=
name|coreNodes
operator|.
name|item
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|coreName
operator|.
name|equals
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
literal|null
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|property
argument_list|,
name|defaultVal
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|defaultVal
return|;
block|}
annotation|@
name|Override
DECL|method|readCoreProperties
specifier|public
name|Properties
name|readCoreProperties
parameter_list|(
name|String
name|coreName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|coreNodes
init|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|coreNodes
operator|.
name|getLength
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|Node
name|node
init|=
name|coreNodes
operator|.
name|item
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|coreName
operator|.
name|equals
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_NAME
argument_list|,
literal|null
argument_list|)
argument_list|)
condition|)
block|{
try|try
block|{
return|return
name|readProperties
argument_list|(
name|node
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|field|DEF_SOLR_XML
specifier|public
specifier|static
specifier|final
name|String
name|DEF_SOLR_XML
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr persistent=\"false\">\n"
operator|+
literal|"<cores adminPath=\"/admin/cores\" defaultCoreName=\""
operator|+
name|CoreContainer
operator|.
name|DEFAULT_DEFAULT_CORE_NAME
operator|+
literal|"\""
operator|+
literal|" host=\"${host:}\" hostPort=\"${hostPort:}\" hostContext=\"${hostContext:}\" zkClientTimeout=\"${zkClientTimeout:15000}\""
operator|+
literal|">\n"
operator|+
literal|"<core name=\""
operator|+
name|CoreContainer
operator|.
name|DEFAULT_DEFAULT_CORE_NAME
operator|+
literal|"\" shard=\"${shard:}\" collection=\"${collection:}\" instanceDir=\"collection1\" />\n"
operator|+
literal|"</cores>\n"
operator|+
literal|"</solr>"
decl_stmt|;
annotation|@
name|Override
DECL|method|substituteProperties
specifier|public
name|void
name|substituteProperties
parameter_list|()
block|{
name|config
operator|.
name|substituteProperties
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
