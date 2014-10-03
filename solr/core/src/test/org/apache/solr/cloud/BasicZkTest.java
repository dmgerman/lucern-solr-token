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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|solr
operator|.
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
begin_comment
comment|/**  * This test is not fully functional - the port registered is illegal -   * so you cannot hit this with http - a nice side benifit is that it will  * detect if a node is trying to do an update to itself with http - it shouldn't  * do that.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|BasicZkTest
specifier|public
class|class
name|BasicZkTest
extends|extends
name|AbstractZkTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{    }
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test using ZooKeeper
name|assertTrue
argument_list|(
literal|"Not using ZooKeeper"
argument_list|,
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|isZooKeeperAware
argument_list|()
argument_list|)
expr_stmt|;
comment|// for the really slow/busy computer, we wait to make sure we have a leader before starting
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|ZkController
name|zkController
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
comment|// test that we got the expected config, not just hardcoded defaults
name|assertNotNull
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"mock"
argument_list|)
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test query on empty index"
argument_list|,
name|request
argument_list|(
literal|"qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test escaping of ";"
name|assertU
argument_list|(
literal|"deleting 42 for no reason at all"
argument_list|,
name|delI
argument_list|(
literal|"42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"adding doc#42"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"aa;bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"does commit work?"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"backslash escaping semicolon"
argument_list|,
name|request
argument_list|(
literal|"id:42 AND val_s:aa\\;bb"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"quote escaping semicolon"
argument_list|,
name|request
argument_list|(
literal|"id:42 AND val_s:\"aa;bb\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no escaping semicolon"
argument_list|,
name|request
argument_list|(
literal|"id:42 AND val_s:aa"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test overwrite default of true
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"AAA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"BBB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[.='BBB']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"CCC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"DDD"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[.='DDD']"
argument_list|)
expr_stmt|;
comment|// test deletes
name|String
index|[]
name|adds
init|=
operator|new
name|String
index|[]
block|{
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"105"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"102"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"103"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"overwrite"
argument_list|,
literal|"true"
argument_list|)
block|, }
decl_stmt|;
for|for
control|(
name|String
name|a
range|:
name|adds
control|)
block|{
name|assertU
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|zkPort
init|=
name|zkServer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
comment|// try a reconnect from disconnect
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|,
name|zkPort
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
comment|// ensure zk still thinks node is up
name|assertTrue
argument_list|(
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// test maxint
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"q"
argument_list|,
literal|"id:[100 TO 110]"
argument_list|,
literal|"rows"
argument_list|,
literal|"2147483647"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
comment|// test big limit
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"q"
argument_list|,
literal|"id:[100 TO 111]"
argument_list|,
literal|"rows"
argument_list|,
literal|"1147483647"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"102"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"105"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// SOLR-2651: test that reload still gets config files from zookeeper
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|setData
argument_list|(
literal|"/configs/conf1/solrconfig.xml"
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// we set the solrconfig to nothing, so this reload should fail
try|try
block|{
name|ignoreException
argument_list|(
literal|"solrconfig.xml"
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The reloaded SolrCore did not pick up configs from zookeeper"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to reload core [collection1]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Error loading solr config from solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test stats call
name|NamedList
name|stats
init|=
name|core
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"collection1"
argument_list|,
name|stats
operator|.
name|get
argument_list|(
literal|"coreName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"collection1"
argument_list|,
name|stats
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shard1"
argument_list|,
name|stats
operator|.
name|get
argument_list|(
literal|"shard"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|(
literal|"refCount"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//zkController.getZkClient().printLayoutToStdOut();
block|}
DECL|method|request
specifier|public
name|SolrQueryRequest
name|request
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
block|{
name|LocalSolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
name|q
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
name|add
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
return|return
name|req
return|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{    }
block|}
end_class
end_unit
