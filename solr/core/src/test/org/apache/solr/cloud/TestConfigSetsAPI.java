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
name|InputStreamReader
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
name|Collection
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
name|Iterator
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|FileUtils
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
name|request
operator|.
name|ConfigSetAdminRequest
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
name|ConfigSetAdminRequest
operator|.
name|Create
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
name|ConfigSetAdminRequest
operator|.
name|Delete
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
name|ConfigSetAdminRequest
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|ConfigSetAdminResponse
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
name|SolrZkClient
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
name|ZkConfigManager
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
name|ConfigSetParams
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
name|ConfigSetParams
operator|.
name|ConfigSetAction
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
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|Utils
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
name|ConfigSetProperties
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
name|Assert
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
name|Test
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
name|cloud
operator|.
name|OverseerConfigSetMessageHandler
operator|.
name|BASE_CONFIGSET
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
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
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
name|core
operator|.
name|ConfigSetProperties
operator|.
name|DEFAULT_FILENAME
import|;
end_import
begin_comment
comment|/**  * Simple ConfigSets API tests on user errors and simple success cases.  */
end_comment
begin_class
DECL|class|TestConfigSetsAPI
specifier|public
class|class
name|TestConfigSetsAPI
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCluster
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|solrCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|1
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|solrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateErrors
specifier|public
name|void
name|testCreateErrors
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|baseUrl
init|=
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
literal|"configSet"
argument_list|)
expr_stmt|;
comment|// no action
name|CreateNoErrorChecking
name|createNoAction
init|=
operator|new
name|CreateNoErrorChecking
argument_list|()
decl_stmt|;
name|createNoAction
operator|.
name|setAction
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|createNoAction
argument_list|,
literal|"action"
argument_list|)
expr_stmt|;
comment|// no ConfigSet name
name|CreateNoErrorChecking
name|create
init|=
operator|new
name|CreateNoErrorChecking
argument_list|()
decl_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|create
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// no base ConfigSet name
name|create
operator|.
name|setConfigSetName
argument_list|(
literal|"configSetName"
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|create
argument_list|,
name|BASE_CONFIGSET
argument_list|)
expr_stmt|;
comment|// ConfigSet already exists
name|Create
name|alreadyExists
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|alreadyExists
operator|.
name|setConfigSetName
argument_list|(
literal|"configSet"
argument_list|)
operator|.
name|setBaseConfigSetName
argument_list|(
literal|"baseConfigSet"
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|alreadyExists
argument_list|,
literal|"ConfigSet already exists"
argument_list|)
expr_stmt|;
comment|// Base ConfigSet does not exist
name|Create
name|baseConfigNoExists
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|baseConfigNoExists
operator|.
name|setConfigSetName
argument_list|(
literal|"newConfigSet"
argument_list|)
operator|.
name|setBaseConfigSetName
argument_list|(
literal|"baseConfigSet"
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|baseConfigNoExists
argument_list|,
literal|"Base ConfigSet does not exist"
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreate
specifier|public
name|void
name|testCreate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// no old, no new
name|verifyCreate
argument_list|(
literal|"baseConfigSet1"
argument_list|,
literal|"configSet1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// no old, new
name|verifyCreate
argument_list|(
literal|"baseConfigSet2"
argument_list|,
literal|"configSet2"
argument_list|,
literal|null
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|(
literal|"immutable"
argument_list|,
literal|"true"
argument_list|,
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// old, no new
name|verifyCreate
argument_list|(
literal|"baseConfigSet3"
argument_list|,
literal|"configSet3"
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|(
literal|"immutable"
argument_list|,
literal|"false"
argument_list|,
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// old, new
name|verifyCreate
argument_list|(
literal|"baseConfigSet4"
argument_list|,
literal|"configSet4"
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|(
literal|"immutable"
argument_list|,
literal|"true"
argument_list|,
literal|"onlyOld"
argument_list|,
literal|"onlyOldValue"
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|(
literal|"immutable"
argument_list|,
literal|"false"
argument_list|,
literal|"onlyNew"
argument_list|,
literal|"onlyNewValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setupBaseConfigSet
specifier|private
name|void
name|setupBaseConfigSet
parameter_list|(
name|String
name|baseConfigSetName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|oldProps
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
specifier|final
name|File
name|tmpConfigDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|tmpConfigDir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|configDir
argument_list|,
name|tmpConfigDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldProps
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|tmpConfigDir
argument_list|,
name|ConfigSetProperties
operator|.
name|DEFAULT_FILENAME
argument_list|)
argument_list|,
name|getConfigSetProps
argument_list|(
name|oldProps
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|tmpConfigDir
argument_list|,
name|baseConfigSetName
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyCreate
specifier|private
name|void
name|verifyCreate
parameter_list|(
name|String
name|baseConfigSetName
parameter_list|,
name|String
name|configSetName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|oldProps
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|newProps
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|baseUrl
init|=
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
name|setupBaseConfigSet
argument_list|(
name|baseConfigSetName
argument_list|,
name|oldProps
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|solrCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|configManager
operator|.
name|configExists
argument_list|(
name|configSetName
argument_list|)
argument_list|)
expr_stmt|;
name|Create
name|create
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|create
operator|.
name|setBaseConfigSetName
argument_list|(
name|baseConfigSetName
argument_list|)
operator|.
name|setConfigSetName
argument_list|(
name|configSetName
argument_list|)
expr_stmt|;
if|if
condition|(
name|newProps
operator|!=
literal|null
condition|)
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|putAll
argument_list|(
name|newProps
argument_list|)
expr_stmt|;
name|create
operator|.
name|setNewConfigSetProperties
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|ConfigSetAdminResponse
name|response
init|=
name|create
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configManager
operator|.
name|configExists
argument_list|(
name|configSetName
argument_list|)
argument_list|)
expr_stmt|;
name|verifyProperties
argument_list|(
name|configSetName
argument_list|,
name|oldProps
argument_list|,
name|newProps
argument_list|,
name|zkClient
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getConfigSetPropertiesFromZk
specifier|private
name|NamedList
name|getConfigSetPropertiesFromZk
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|oldPropsData
init|=
literal|null
decl_stmt|;
try|try
block|{
name|oldPropsData
operator|=
name|zkClient
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// okay, properties just don't exist
block|}
if|if
condition|(
name|oldPropsData
operator|!=
literal|null
condition|)
block|{
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|oldPropsData
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|ConfigSetProperties
operator|.
name|readFromInputStream
argument_list|(
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|verifyProperties
specifier|private
name|void
name|verifyProperties
parameter_list|(
name|String
name|configSetName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|oldProps
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|newProps
parameter_list|,
name|SolrZkClient
name|zkClient
parameter_list|)
throws|throws
name|Exception
block|{
name|NamedList
name|properties
init|=
name|getConfigSetPropertiesFromZk
argument_list|(
name|zkClient
argument_list|,
name|ZkConfigManager
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|configSetName
operator|+
literal|"/"
operator|+
name|DEFAULT_FILENAME
argument_list|)
decl_stmt|;
comment|// let's check without merging the maps, since that's what the MessageHandler does
comment|// (since we'd probably repeat any bug in the MessageHandler here)
if|if
condition|(
name|oldProps
operator|==
literal|null
operator|&&
name|newProps
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertNotNull
argument_list|(
name|properties
argument_list|)
expr_stmt|;
comment|// check all oldProps are in props
if|if
condition|(
name|oldProps
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|oldProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertNotNull
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check all newProps are in props
if|if
condition|(
name|newProps
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|newProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertNotNull
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check the value in properties are correct
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|it
init|=
name|properties
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|newValue
init|=
name|newProps
operator|!=
literal|null
condition|?
name|newProps
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|String
name|oldValue
init|=
name|oldProps
operator|!=
literal|null
condition|?
name|oldProps
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|newValue
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|newValue
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oldValue
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|oldValue
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// not in either
assert|assert
operator|(
literal|false
operator|)
assert|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testDeleteErrors
specifier|public
name|void
name|testDeleteErrors
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|baseUrl
init|=
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
specifier|final
name|File
name|tmpConfigDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|tmpConfigDir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
comment|// Ensure ConfigSet is immutable
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|configDir
argument_list|,
name|tmpConfigDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|tmpConfigDir
argument_list|,
literal|"configsetprops.json"
argument_list|)
argument_list|,
name|getConfigSetProps
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|(
literal|"immutable"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|tmpConfigDir
argument_list|,
literal|"configSet"
argument_list|)
expr_stmt|;
comment|// no ConfigSet name
name|DeleteNoErrorChecking
name|delete
init|=
operator|new
name|DeleteNoErrorChecking
argument_list|()
decl_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|delete
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|// ConfigSet doesn't exist
name|delete
operator|.
name|setConfigSetName
argument_list|(
literal|"configSetBogus"
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|delete
argument_list|,
literal|"ConfigSet does not exist"
argument_list|)
expr_stmt|;
comment|// ConfigSet is immutable
name|delete
operator|.
name|setConfigSetName
argument_list|(
literal|"configSet"
argument_list|)
expr_stmt|;
name|verifyException
argument_list|(
name|solrClient
argument_list|,
name|delete
argument_list|,
literal|"Requested delete of immutable ConfigSet"
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyException
specifier|private
name|void
name|verifyException
parameter_list|(
name|SolrClient
name|solrClient
parameter_list|,
name|ConfigSetAdminRequest
name|request
parameter_list|,
name|String
name|errorContains
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected exception message to contain: "
operator|+
name|errorContains
operator|+
literal|" got: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|errorContains
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDelete
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|baseUrl
init|=
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
specifier|final
name|String
name|configSet
init|=
literal|"configSet"
decl_stmt|;
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
name|configSet
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|solrCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|configManager
operator|.
name|configExists
argument_list|(
name|configSet
argument_list|)
argument_list|)
expr_stmt|;
name|Delete
name|delete
init|=
operator|new
name|Delete
argument_list|()
decl_stmt|;
name|delete
operator|.
name|setConfigSetName
argument_list|(
name|configSet
argument_list|)
expr_stmt|;
name|ConfigSetAdminResponse
name|response
init|=
name|delete
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|configManager
operator|.
name|configExists
argument_list|(
name|configSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testList
specifier|public
name|void
name|testList
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|baseUrl
init|=
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|solrCluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
comment|// test empty
name|List
name|list
init|=
operator|new
name|List
argument_list|()
decl_stmt|;
name|ConfigSetAdminResponse
operator|.
name|List
name|response
init|=
name|list
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|actualConfigSets
init|=
name|response
operator|.
name|getConfigSets
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualConfigSets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// test multiple
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|configSets
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
literal|5
condition|;
operator|++
name|i
control|)
block|{
name|String
name|configSet
init|=
literal|"configSet"
operator|+
name|i
decl_stmt|;
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
name|configSet
argument_list|)
expr_stmt|;
name|configSets
operator|.
name|add
argument_list|(
name|configSet
argument_list|)
expr_stmt|;
block|}
name|response
operator|=
name|list
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
name|actualConfigSets
operator|=
name|response
operator|.
name|getConfigSets
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|configSets
operator|.
name|size
argument_list|()
argument_list|,
name|actualConfigSets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|configSets
operator|.
name|containsAll
argument_list|(
name|actualConfigSets
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getConfigSetProps
specifier|private
name|StringBuilder
name|getConfigSetProps
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
operator|new
name|String
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|map
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
DECL|class|CreateNoErrorChecking
specifier|public
specifier|static
class|class
name|CreateNoErrorChecking
extends|extends
name|ConfigSetAdminRequest
operator|.
name|Create
block|{
DECL|method|setAction
specifier|public
name|ConfigSetAdminRequest
name|setAction
parameter_list|(
name|ConfigSetAction
name|action
parameter_list|)
block|{
return|return
name|super
operator|.
name|setAction
argument_list|(
name|action
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
name|ConfigSetParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|configSetName
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
name|NAME
argument_list|,
name|configSetName
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseConfigSetName
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
literal|"baseConfigSet"
argument_list|,
name|baseConfigSetName
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
DECL|class|DeleteNoErrorChecking
specifier|public
specifier|static
class|class
name|DeleteNoErrorChecking
extends|extends
name|ConfigSetAdminRequest
operator|.
name|Delete
block|{
DECL|method|setAction
specifier|public
name|ConfigSetAdminRequest
name|setAction
parameter_list|(
name|ConfigSetAction
name|action
parameter_list|)
block|{
return|return
name|super
operator|.
name|setAction
argument_list|(
name|action
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
name|ConfigSetParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|configSetName
operator|!=
literal|null
condition|)
name|params
operator|.
name|set
argument_list|(
name|NAME
argument_list|,
name|configSetName
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
block|}
end_class
end_unit
