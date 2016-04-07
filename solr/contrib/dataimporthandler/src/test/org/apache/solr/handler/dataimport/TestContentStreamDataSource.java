begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DirectXmlRequest
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
name|QueryResponse
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
name|SolrDocument
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
name|SolrDocumentList
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
name|UpdateParams
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|Properties
import|;
end_import
begin_comment
comment|/**  * Test for ContentStreamDataSource  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestContentStreamDataSource
specifier|public
class|class
name|TestContentStreamDataSource
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|CONF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CONF_DIR
init|=
literal|"dih/solr/collection1/conf/"
decl_stmt|;
DECL|field|ROOT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_DIR
init|=
literal|"dih/solr/"
decl_stmt|;
DECL|field|instance
name|SolrInstance
name|instance
init|=
literal|null
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
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
name|instance
operator|=
operator|new
name|SolrInstance
argument_list|(
literal|"inst"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|jetty
operator|=
name|createJetty
argument_list|(
name|instance
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
name|jetty
operator|.
name|stop
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
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectXmlRequest
name|req
init|=
operator|new
name|DirectXmlRequest
argument_list|(
literal|"/dataimport"
argument_list|,
name|xml
argument_list|)
decl_stmt|;
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
literal|"command"
argument_list|,
literal|"full-import"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"clean"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|buildUrl
argument_list|(
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
literal|"/solr/collection1"
argument_list|)
argument_list|)
init|)
block|{
name|solrClient
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|qparams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|qparams
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|QueryResponse
name|qres
init|=
name|solrClient
operator|.
name|query
argument_list|(
name|qparams
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|results
init|=
name|qres
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello C1"
argument_list|,
operator|(
operator|(
name|List
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"desc"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCommitWithin
specifier|public
name|void
name|testCommitWithin
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectXmlRequest
name|req
init|=
operator|new
name|DirectXmlRequest
argument_list|(
literal|"/dataimport"
argument_list|,
name|xml
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
name|params
argument_list|(
literal|"command"
argument_list|,
literal|"full-import"
argument_list|,
literal|"clean"
argument_list|,
literal|"false"
argument_list|,
name|UpdateParams
operator|.
name|COMMIT
argument_list|,
literal|"false"
argument_list|,
name|UpdateParams
operator|.
name|COMMIT_WITHIN
argument_list|,
literal|"1000"
argument_list|)
decl_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|solrServer
init|=
name|getHttpSolrClient
argument_list|(
name|buildUrl
argument_list|(
name|jetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
literal|"/solr/collection1"
argument_list|)
argument_list|)
init|)
block|{
name|solrServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|queryAll
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|qres
init|=
name|solrServer
operator|.
name|query
argument_list|(
name|queryAll
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|results
init|=
name|qres
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|results
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|qres
operator|=
name|solrServer
operator|.
name|query
argument_list|(
name|queryAll
argument_list|)
expr_stmt|;
name|results
operator|=
name|qres
operator|.
name|getResults
argument_list|()
expr_stmt|;
if|if
condition|(
literal|2
operator|==
name|results
operator|.
name|getNumFound
argument_list|()
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
name|fail
argument_list|(
literal|"Commit should have occured but it did not"
argument_list|)
expr_stmt|;
block|}
DECL|class|SolrInstance
specifier|private
class|class
name|SolrInstance
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|port
name|Integer
name|port
decl_stmt|;
DECL|field|homeDir
name|File
name|homeDir
decl_stmt|;
DECL|field|confDir
name|File
name|confDir
decl_stmt|;
DECL|field|dataDir
name|File
name|dataDir
decl_stmt|;
comment|/**      * if masterPort is null, this instance is a master -- otherwise this instance is a slave, and assumes the master is      * on localhost at the specified port.      */
DECL|method|SolrInstance
specifier|public
name|SolrInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|Integer
name|port
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
DECL|method|getHomeDir
specifier|public
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|homeDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|+
literal|"dataimport-schema.xml"
return|;
block|}
DECL|method|getConfDir
specifier|public
name|String
name|getConfDir
parameter_list|()
block|{
return|return
name|confDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
name|CONF_DIR
operator|+
literal|"contentstream-solrconfig.xml"
return|;
block|}
DECL|method|getSolrXmlFile
specifier|public
name|String
name|getSolrXmlFile
parameter_list|()
block|{
return|return
name|ROOT_DIR
operator|+
literal|"solr.xml"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|homeDir
operator|=
name|createTempDir
argument_list|(
literal|"inst"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
literal|"/collection1"
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
literal|"/collection1"
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|getSolrXmlFile
argument_list|()
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"data-config.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
name|CONF_DIR
operator|+
literal|"dataconfig-contentstream.xml"
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|homeDir
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1/core.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createJetty
specifier|private
name|JettySolrRunner
name|createJetty
parameter_list|(
name|SolrInstance
name|instance
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|nodeProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|nodeProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|instance
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
argument_list|,
name|nodeProperties
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|field|xml
specifier|static
name|String
name|xml
init|=
literal|"<root>\n"
operator|+
literal|"<b>\n"
operator|+
literal|"<id>1</id>\n"
operator|+
literal|"<c>Hello C1</c>\n"
operator|+
literal|"</b>\n"
operator|+
literal|"<b>\n"
operator|+
literal|"<id>2</id>\n"
operator|+
literal|"<c>Hello C2</c>\n"
operator|+
literal|"</b>\n"
operator|+
literal|"</root>"
decl_stmt|;
block|}
end_class
end_unit
