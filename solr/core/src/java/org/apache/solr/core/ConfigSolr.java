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
name|java
operator|.
name|util
operator|.
name|Properties
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
name|cloud
operator|.
name|CloudConfigSetService
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
name|cloud
operator|.
name|ZkController
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
name|logging
operator|.
name|LogWatcherConfig
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
if|if
condition|(
operator|!
name|configFile
operator|.
name|exists
argument_list|()
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
literal|"solr.xml does not exist in "
operator|+
name|configFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" cannot start Solr"
argument_list|)
throw|;
block|}
try|try
init|(
name|InputStream
name|inputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
init|)
block|{
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
name|SolrException
name|exc
parameter_list|)
block|{
throw|throw
name|exc
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
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
name|exc
argument_list|)
throw|;
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
name|StandardCharsets
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
name|byte
index|[]
name|buf
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|String
name|originalXml
init|=
operator|new
name|String
argument_list|(
name|buf
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
try|try
init|(
name|ByteArrayInputStream
name|dup
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|)
init|)
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
name|dup
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|fromConfig
argument_list|(
name|config
argument_list|,
name|originalXml
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|exc
parameter_list|)
block|{
throw|throw
name|exc
throw|;
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
parameter_list|,
name|String
name|originalXml
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
argument_list|,
name|originalXml
argument_list|)
else|:
operator|new
name|ConfigSolrXml
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|getCoresLocator
specifier|public
specifier|abstract
name|CoresLocator
name|getCoresLocator
parameter_list|()
function_decl|;
comment|/**    * The directory against which relative core instance dirs are resolved.  If none is    * specified in the config, uses solr home.    *    * @return core root directory    */
DECL|method|getCoreRootDirectory
specifier|public
name|String
name|getCoreRootDirectory
parameter_list|()
block|{
return|return
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_COREROOTDIRECTORY
argument_list|,
name|config
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
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
DECL|method|getShardHandlerFactoryConfigPath
specifier|protected
specifier|abstract
name|String
name|getShardHandlerFactoryConfigPath
parameter_list|()
function_decl|;
DECL|method|getZkHost
specifier|public
name|String
name|getZkHost
parameter_list|()
block|{
name|String
name|sysZkHost
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkHost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sysZkHost
operator|!=
literal|null
condition|)
return|return
name|sysZkHost
return|;
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKHOST
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getZkClientTimeout
specifier|public
name|int
name|getZkClientTimeout
parameter_list|()
block|{
name|String
name|sysProp
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sysProp
operator|!=
literal|null
condition|)
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|sysProp
argument_list|)
return|;
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKCLIENTTIMEOUT
argument_list|,
name|DEFAULT_ZK_CLIENT_TIMEOUT
argument_list|)
return|;
block|}
DECL|field|DEFAULT_ZK_CLIENT_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_ZK_CLIENT_TIMEOUT
init|=
literal|15000
decl_stmt|;
DECL|field|DEFAULT_LEADER_VOTE_WAIT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_LEADER_VOTE_WAIT
init|=
literal|180000
decl_stmt|;
comment|// 3 minutes
DECL|field|DEFAULT_LEADER_CONFLICT_RESOLVE_WAIT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_LEADER_CONFLICT_RESOLVE_WAIT
init|=
literal|180000
decl_stmt|;
DECL|field|DEFAULT_CORE_LOAD_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CORE_LOAD_THREADS
init|=
literal|3
decl_stmt|;
comment|// TODO: tune defaults
DECL|field|DEFAULT_AUTO_REPLICA_FAILOVER_WAIT_AFTER_EXPIRATION
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_AUTO_REPLICA_FAILOVER_WAIT_AFTER_EXPIRATION
init|=
literal|30000
decl_stmt|;
DECL|field|DEFAULT_AUTO_REPLICA_FAILOVER_WORKLOOP_DELAY
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_AUTO_REPLICA_FAILOVER_WORKLOOP_DELAY
init|=
literal|10000
decl_stmt|;
DECL|field|DEFAULT_AUTO_REPLICA_FAILOVER_BAD_NODE_EXPIRATION
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_AUTO_REPLICA_FAILOVER_BAD_NODE_EXPIRATION
init|=
literal|60000
decl_stmt|;
DECL|field|DEFAULT_CORE_ADMIN_PATH
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_CORE_ADMIN_PATH
init|=
literal|"/admin/cores"
decl_stmt|;
DECL|method|getZkHostPort
specifier|public
name|String
name|getZkHostPort
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOSTPORT
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getZkHostContext
specifier|public
name|String
name|getZkHostContext
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOSTCONTEXT
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getHost
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_HOST
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getLeaderVoteWait
specifier|public
name|int
name|getLeaderVoteWait
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_LEADERVOTEWAIT
argument_list|,
name|DEFAULT_LEADER_VOTE_WAIT
argument_list|)
return|;
block|}
DECL|method|getLeaderConflictResolveWait
specifier|public
name|int
name|getLeaderConflictResolveWait
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_LEADERCONFLICTRESOLVEWAIT
argument_list|,
name|DEFAULT_LEADER_CONFLICT_RESOLVE_WAIT
argument_list|)
return|;
block|}
DECL|method|getAutoReplicaFailoverWaitAfterExpiration
specifier|public
name|int
name|getAutoReplicaFailoverWaitAfterExpiration
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_AUTOREPLICAFAILOVERWAITAFTEREXPIRATION
argument_list|,
name|DEFAULT_AUTO_REPLICA_FAILOVER_WAIT_AFTER_EXPIRATION
argument_list|)
return|;
block|}
DECL|method|getAutoReplicaFailoverWorkLoopDelay
specifier|public
name|int
name|getAutoReplicaFailoverWorkLoopDelay
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_AUTOREPLICAFAILOVERWORKLOOPDELAY
argument_list|,
name|DEFAULT_AUTO_REPLICA_FAILOVER_WORKLOOP_DELAY
argument_list|)
return|;
block|}
DECL|method|getAutoReplicaFailoverBadNodeExpiration
specifier|public
name|int
name|getAutoReplicaFailoverBadNodeExpiration
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_AUTOREPLICAFAILOVERBADNODEEXPIRATION
argument_list|,
name|DEFAULT_AUTO_REPLICA_FAILOVER_BAD_NODE_EXPIRATION
argument_list|)
return|;
block|}
DECL|method|getGenericCoreNodeNames
specifier|public
name|boolean
name|getGenericCoreNodeNames
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_GENERICCORENODENAMES
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getDistributedConnectionTimeout
specifier|public
name|int
name|getDistributedConnectionTimeout
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATECONNTIMEOUT
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|getDistributedSocketTimeout
specifier|public
name|int
name|getDistributedSocketTimeout
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATESOTIMEOUT
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|getMaxUpdateConnections
specifier|public
name|int
name|getMaxUpdateConnections
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_MAXUPDATECONNECTIONS
argument_list|,
literal|10000
argument_list|)
return|;
block|}
DECL|method|getMaxUpdateConnectionsPerHost
specifier|public
name|int
name|getMaxUpdateConnectionsPerHost
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_MAXUPDATECONNECTIONSPERHOST
argument_list|,
literal|100
argument_list|)
return|;
block|}
DECL|method|getCoreLoadThreadCount
specifier|public
name|int
name|getCoreLoadThreadCount
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_CORELOADTHREADS
argument_list|,
name|DEFAULT_CORE_LOAD_THREADS
argument_list|)
return|;
block|}
DECL|method|getSharedLibDirectory
specifier|public
name|String
name|getSharedLibDirectory
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_SHAREDLIB
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getDefaultCoreName
specifier|public
name|String
name|getDefaultCoreName
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_CORES_DEFAULT_CORE_NAME
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|isPersistent
specifier|public
specifier|abstract
name|boolean
name|isPersistent
parameter_list|()
function_decl|;
DECL|method|getAdminPath
specifier|public
name|String
name|getAdminPath
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_ADMINPATH
argument_list|,
name|DEFAULT_CORE_ADMIN_PATH
argument_list|)
return|;
block|}
DECL|method|getCoreAdminHandlerClass
specifier|public
name|String
name|getCoreAdminHandlerClass
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_ADMINHANDLER
argument_list|,
literal|"org.apache.solr.handler.admin.CoreAdminHandler"
argument_list|)
return|;
block|}
DECL|method|getZkCredentialProviderClass
specifier|public
name|String
name|getZkCredentialProviderClass
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKCREDENTIALPROVIDER
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getZkACLProviderClass
specifier|public
name|String
name|getZkACLProviderClass
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_ZKACLPROVIDER
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getCollectionsHandlerClass
specifier|public
name|String
name|getCollectionsHandlerClass
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_COLLECTIONSHANDLER
argument_list|,
literal|"org.apache.solr.handler.admin.CollectionsHandler"
argument_list|)
return|;
block|}
DECL|method|getInfoHandlerClass
specifier|public
name|String
name|getInfoHandlerClass
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_INFOHANDLER
argument_list|,
literal|"org.apache.solr.handler.admin.InfoHandler"
argument_list|)
return|;
block|}
DECL|method|hasSchemaCache
specifier|public
name|boolean
name|hasSchemaCache
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_SHARESCHEMA
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getManagementPath
specifier|public
name|String
name|getManagementPath
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_MANAGEMENTPATH
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getConfigSetBaseDirectory
specifier|public
name|String
name|getConfigSetBaseDirectory
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_CONFIGSETBASEDIR
argument_list|,
literal|"configsets"
argument_list|)
return|;
block|}
DECL|method|getLogWatcherConfig
specifier|public
name|LogWatcherConfig
name|getLogWatcherConfig
parameter_list|()
block|{
name|String
name|loggingClass
init|=
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_CLASS
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|loggingWatcherThreshold
init|=
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_THRESHOLD
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogWatcherConfig
argument_list|(
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_ENABLED
argument_list|,
literal|true
argument_list|)
argument_list|,
name|loggingClass
argument_list|,
name|loggingWatcherThreshold
argument_list|,
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_SIZE
argument_list|,
literal|50
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTransientCacheSize
specifier|public
name|int
name|getTransientCacheSize
parameter_list|()
block|{
return|return
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_TRANSIENTCACHESIZE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|createCoreConfigService
specifier|public
name|ConfigSetService
name|createCoreConfigService
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|ZkController
name|zkController
parameter_list|)
block|{
if|if
condition|(
name|getZkHost
argument_list|()
operator|!=
literal|null
operator|||
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkRun"
argument_list|)
operator|!=
literal|null
condition|)
return|return
operator|new
name|CloudConfigSetService
argument_list|(
name|loader
argument_list|,
name|zkController
argument_list|)
return|;
if|if
condition|(
name|hasSchemaCache
argument_list|()
condition|)
return|return
operator|new
name|ConfigSetService
operator|.
name|SchemaCaching
argument_list|(
name|loader
argument_list|,
name|getConfigSetBaseDirectory
argument_list|()
argument_list|)
return|;
return|return
operator|new
name|ConfigSetService
operator|.
name|Default
argument_list|(
name|loader
argument_list|,
name|getConfigSetBaseDirectory
argument_list|()
argument_list|)
return|;
block|}
comment|// Ugly for now, but we'll at least be able to centralize all of the differences between 4x and 5x.
DECL|enum|CfgProp
specifier|protected
specifier|static
enum|enum
name|CfgProp
block|{
DECL|enum constant|SOLR_ADMINHANDLER
name|SOLR_ADMINHANDLER
block|,
DECL|enum constant|SOLR_COLLECTIONSHANDLER
name|SOLR_COLLECTIONSHANDLER
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
DECL|enum constant|SOLR_MAXUPDATECONNECTIONS
name|SOLR_MAXUPDATECONNECTIONS
block|,
DECL|enum constant|SOLR_MAXUPDATECONNECTIONSPERHOST
name|SOLR_MAXUPDATECONNECTIONSPERHOST
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
DECL|enum constant|SOLR_INFOHANDLER
name|SOLR_INFOHANDLER
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
DECL|enum constant|SOLR_LEADERCONFLICTRESOLVEWAIT
name|SOLR_LEADERCONFLICTRESOLVEWAIT
block|,
DECL|enum constant|SOLR_CONFIGSETBASEDIR
name|SOLR_CONFIGSETBASEDIR
block|,
DECL|enum constant|SOLR_AUTOREPLICAFAILOVERWAITAFTEREXPIRATION
name|SOLR_AUTOREPLICAFAILOVERWAITAFTEREXPIRATION
block|,
DECL|enum constant|SOLR_AUTOREPLICAFAILOVERWORKLOOPDELAY
name|SOLR_AUTOREPLICAFAILOVERWORKLOOPDELAY
block|,
DECL|enum constant|SOLR_AUTOREPLICAFAILOVERBADNODEEXPIRATION
name|SOLR_AUTOREPLICAFAILOVERBADNODEEXPIRATION
block|,
DECL|enum constant|SOLR_ZKCREDENTIALPROVIDER
name|SOLR_ZKCREDENTIALPROVIDER
block|,
DECL|enum constant|SOLR_ZKACLPROVIDER
name|SOLR_ZKACLPROVIDER
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
name|Object
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<>
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
name|config
operator|.
name|substituteProperties
argument_list|()
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|get
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|CfgProp
name|key
parameter_list|,
name|T
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|propMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|&&
name|propMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
name|defaultValue
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
name|Exception
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
block|}
end_class
end_unit
