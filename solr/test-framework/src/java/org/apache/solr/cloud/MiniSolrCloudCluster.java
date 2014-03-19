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
name|zookeeper
operator|.
name|CreateMode
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
name|ZooDefs
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
name|SortedMap
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
name|io
operator|.
name|Files
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
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|jettys
specifier|private
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
decl_stmt|;
DECL|field|testDir
specifier|private
name|File
name|testDir
decl_stmt|;
comment|/**    * "Mini" SolrCloud cluster to be used for testing    * @param numServers number of Solr servers to start    * @param hostContext context path of Solr servers used by Jetty    * @param solrXml solr.xml file to be uploaded to ZooKeeper    * @param extraServlets Extra servlets to be started by Jetty    * @param extraRequestFilters extra filters to be started by Jetty    */
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
argument_list|,
name|String
argument_list|>
name|extraRequestFilters
parameter_list|)
throws|throws
name|Exception
block|{
name|testDir
operator|=
name|Files
operator|.
name|createTempDir
argument_list|()
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
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
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
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/solr"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|solrXml
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
literal|"/solr/solr.xml"
argument_list|,
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
argument_list|,
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
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
argument_list|<
name|JettySolrRunner
argument_list|>
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
name|hostContext
argument_list|,
name|extraServlets
argument_list|,
name|extraRequestFilters
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Start a new Solr instance    * @param hostContext context path of Solr servers used by Jetty    * @param extraServlets Extra servlets to be started by Jetty    * @param extraRequestFilters extra filters to be started by Jetty    * @return new Solr instance    */
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
argument_list|,
name|String
argument_list|>
name|extraRequestFilters
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
name|context
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|extraServlets
argument_list|,
literal|null
argument_list|,
name|extraRequestFilters
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
comment|/**    * Shut down the cluster, including all Solr nodes and ZooKeeper    */
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
empty_stmt|;
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
