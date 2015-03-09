begin_unit
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
name|request
operator|.
name|QueryRequest
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
name|CollectionParams
operator|.
name|CollectionAction
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
name|zookeeper
operator|.
name|KeeperException
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
name|javax
operator|.
name|servlet
operator|.
name|Filter
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
name|LinkedList
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
name|SortedMap
import|;
end_import
begin_class
DECL|class|MiniSolrCloudCluster
specifier|public
class|class
name|MiniSolrCloudCluster
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
name|MiniSolrCloudCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|private
specifier|final
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|jettys
specifier|private
specifier|final
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|final
name|File
name|testDir
decl_stmt|;
DECL|field|solrClient
specifier|private
specifier|final
name|CloudSolrClient
name|solrClient
decl_stmt|;
DECL|field|jettyConfig
specifier|private
specifier|final
name|JettyConfig
name|jettyConfig
decl_stmt|;
comment|/**    * "Mini" SolrCloud cluster to be used for testing    * @param numServers number of Solr servers to start    * @param hostContext context path of Solr servers used by Jetty    * @param baseDir base directory that the mini cluster should be run from    * @param solrXml solr.xml file to be uploaded to ZooKeeper    * @param extraServlets Extra servlets to be started by Jetty    * @param extraRequestFilters extra filters to be started by Jetty    */
DECL|method|MiniSolrCloudCluster
specifier|public
name|MiniSolrCloudCluster
parameter_list|(
name|int
name|numServers
parameter_list|,
name|String
name|hostContext
parameter_list|,
name|File
name|baseDir
parameter_list|,
name|File
name|solrXml
parameter_list|,
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
parameter_list|,
name|SortedMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraRequestFilters
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|numServers
argument_list|,
name|hostContext
argument_list|,
name|baseDir
argument_list|,
name|solrXml
argument_list|,
name|extraServlets
argument_list|,
name|extraRequestFilters
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * "Mini" SolrCloud cluster to be used for testing    * @param numServers number of Solr servers to start    * @param hostContext context path of Solr servers used by Jetty    * @param baseDir base directory that the mini cluster should be run from    * @param solrXml solr.xml file to be uploaded to ZooKeeper    * @param extraServlets Extra servlets to be started by Jetty    * @param extraRequestFilters extra filters to be started by Jetty    * @param sslConfig SSL configuration    */
DECL|method|MiniSolrCloudCluster
specifier|public
name|MiniSolrCloudCluster
parameter_list|(
name|int
name|numServers
parameter_list|,
name|String
name|hostContext
parameter_list|,
name|File
name|baseDir
parameter_list|,
name|File
name|solrXml
parameter_list|,
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
parameter_list|,
name|SortedMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraRequestFilters
parameter_list|,
name|SSLConfig
name|sslConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|numServers
argument_list|,
name|baseDir
argument_list|,
name|solrXml
argument_list|,
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|setContext
argument_list|(
name|hostContext
argument_list|)
operator|.
name|withSSLConfig
argument_list|(
name|sslConfig
argument_list|)
operator|.
name|withFilters
argument_list|(
name|extraRequestFilters
argument_list|)
operator|.
name|withServlets
argument_list|(
name|extraServlets
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MiniSolrCloudCluster
specifier|public
name|MiniSolrCloudCluster
parameter_list|(
name|int
name|numServers
parameter_list|,
name|File
name|baseDir
parameter_list|,
name|File
name|solrXml
parameter_list|,
name|JettyConfig
name|jettyConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|testDir
operator|=
name|baseDir
expr_stmt|;
name|this
operator|.
name|jettyConfig
operator|=
name|jettyConfig
expr_stmt|;
name|String
name|zkDir
init|=
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
try|try
init|(
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|45000
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/solr/solr.xml"
argument_list|,
name|solrXml
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|jettyConfig
operator|.
name|sslConfig
operator|!=
literal|null
operator|&&
name|jettyConfig
operator|.
name|sslConfig
operator|.
name|isSSLMode
argument_list|()
condition|)
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/solr"
operator|+
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
literal|"{'urlScheme':'https'}"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// tell solr to look in zookeeper for solr.xml
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|,
literal|"zookeeper"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|jettys
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numServers
condition|;
operator|++
name|i
control|)
block|{
name|startJettySolrRunner
argument_list|(
name|jettyConfig
argument_list|)
expr_stmt|;
block|}
name|solrClient
operator|=
name|buildSolrClient
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return ZooKeeper server used by the MiniCluster    */
DECL|method|getZkServer
specifier|public
name|ZkTestServer
name|getZkServer
parameter_list|()
block|{
return|return
name|zkServer
return|;
block|}
comment|/**    * @return Unmodifiable list of all the currently started Solr Jettys.    */
DECL|method|getJettySolrRunners
specifier|public
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|getJettySolrRunners
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|jettys
argument_list|)
return|;
block|}
comment|/**    * Start a new Solr instance    *    * @param hostContext context path of Solr servers used by Jetty    * @param extraServlets Extra servlets to be started by Jetty    * @param extraRequestFilters extra filters to be started by Jetty    *    * @return new Solr instance    *    */
DECL|method|startJettySolrRunner
specifier|public
name|JettySolrRunner
name|startJettySolrRunner
parameter_list|(
name|String
name|hostContext
parameter_list|,
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
parameter_list|,
name|SortedMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraRequestFilters
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|startJettySolrRunner
argument_list|(
name|hostContext
argument_list|,
name|extraServlets
argument_list|,
name|extraRequestFilters
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Start a new Solr instance    *    * @param hostContext context path of Solr servers used by Jetty    * @param extraServlets Extra servlets to be started by Jetty    * @param extraRequestFilters extra filters to be started by Jetty    * @param sslConfig SSL configuration    *    * @return new Solr instance    */
DECL|method|startJettySolrRunner
specifier|public
name|JettySolrRunner
name|startJettySolrRunner
parameter_list|(
name|String
name|hostContext
parameter_list|,
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
parameter_list|,
name|SortedMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraRequestFilters
parameter_list|,
name|SSLConfig
name|sslConfig
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|startJettySolrRunner
argument_list|(
name|hostContext
argument_list|,
name|JettyConfig
operator|.
name|builder
argument_list|()
operator|.
name|withServlets
argument_list|(
name|extraServlets
argument_list|)
operator|.
name|withFilters
argument_list|(
name|extraRequestFilters
argument_list|)
operator|.
name|withSSLConfig
argument_list|(
name|sslConfig
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Start a new Solr instance    *    * @param config a JettyConfig for the instance's {@link org.apache.solr.client.solrj.embedded.JettySolrRunner}    *    * @return a JettySolrRunner    */
DECL|method|startJettySolrRunner
specifier|public
name|JettySolrRunner
name|startJettySolrRunner
parameter_list|(
name|JettyConfig
name|config
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|startJettySolrRunner
argument_list|(
name|config
operator|.
name|context
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Start a new Solr instance on a particular servlet context    *    * @param hostContext the context to run on    * @param config a JettyConfig for the instance's {@link org.apache.solr.client.solrj.embedded.JettySolrRunner}    *    * @return a JettySolrRunner    */
DECL|method|startJettySolrRunner
specifier|public
name|JettySolrRunner
name|startJettySolrRunner
parameter_list|(
name|String
name|hostContext
parameter_list|,
name|JettyConfig
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|context
init|=
name|getHostContextSuitableForServletContext
argument_list|(
name|hostContext
argument_list|)
decl_stmt|;
name|JettyConfig
name|newConfig
init|=
name|JettyConfig
operator|.
name|builder
argument_list|(
name|config
argument_list|)
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|newConfig
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|jettys
operator|.
name|add
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
return|return
name|jetty
return|;
block|}
comment|/**    * Start a new Solr instance, using the default config    *    * @return a JettySolrRunner    */
DECL|method|startJettySolrRunner
specifier|public
name|JettySolrRunner
name|startJettySolrRunner
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|startJettySolrRunner
argument_list|(
name|jettyConfig
argument_list|)
return|;
block|}
comment|/**    * Stop a Solr instance    * @param index the index of node in collection returned by {@link #getJettySolrRunners()}    * @return the shut down node    */
DECL|method|stopJettySolrRunner
specifier|public
name|JettySolrRunner
name|stopJettySolrRunner
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|jetty
init|=
name|jettys
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jettys
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|method|uploadConfigDir
specifier|public
name|void
name|uploadConfigDir
parameter_list|(
name|File
name|configDir
parameter_list|,
name|String
name|configName
parameter_list|)
throws|throws
name|IOException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
try|try
init|(
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|,
literal|45000
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|ZkConfigManager
name|manager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|manager
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
operator|.
name|toPath
argument_list|()
argument_list|,
name|configName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createCollection
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|createCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|numShards
parameter_list|,
name|int
name|replicationFactor
parameter_list|,
name|String
name|configName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionProperties
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CollectionAction
operator|.
name|CREATE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"numShards"
argument_list|,
name|numShards
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"replicationFactor"
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection.configName"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionProperties
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
name|property
range|:
name|collectionProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|PROPERTY_PREFIX
operator|+
name|property
operator|.
name|getKey
argument_list|()
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
return|return
name|solrClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
comment|/**    * Shut down the cluster, including all Solr nodes and ZooKeeper    */
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|jettys
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|stopJettySolrRunner
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSolrClient
specifier|public
name|CloudSolrClient
name|getSolrClient
parameter_list|()
block|{
return|return
name|solrClient
return|;
block|}
DECL|method|buildSolrClient
specifier|protected
name|CloudSolrClient
name|buildSolrClient
parameter_list|()
block|{
return|return
operator|new
name|CloudSolrClient
argument_list|(
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getHostContextSuitableForServletContext
specifier|private
specifier|static
name|String
name|getHostContextSuitableForServletContext
parameter_list|(
name|String
name|ctx
parameter_list|)
block|{
if|if
condition|(
name|ctx
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|ctx
argument_list|)
condition|)
name|ctx
operator|=
literal|"/solr"
expr_stmt|;
if|if
condition|(
name|ctx
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|ctx
operator|=
name|ctx
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ctx
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ctx
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|ctx
operator|=
literal|"/"
operator|+
name|ctx
expr_stmt|;
return|return
name|ctx
return|;
block|}
block|}
end_class
end_unit
