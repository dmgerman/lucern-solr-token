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
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|SynchronousQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|SolrServer
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
name|impl
operator|.
name|HttpSolrServer
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
name|CoreAdminRequest
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
name|CoreAdminRequest
operator|.
name|Unload
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
name|SolrInputDocument
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
name|ZkCoreNodeProps
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
name|update
operator|.
name|DirectUpdateHandler2
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
name|DefaultSolrThreadFactory
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
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * This test simply does a bunch of basic things in solrcloud mode and asserts things  * work as expected.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|UnloadDistributedZkTest
specifier|public
class|class
name|UnloadDistributedZkTest
extends|extends
name|BasicDistributedZkTest
block|{
annotation|@
name|BeforeClass
DECL|method|beforeThisClass3
specifier|public
specifier|static
name|void
name|beforeThisClass3
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Before
annotation|@
name|Override
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
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|UnloadDistributedZkTest
specifier|public
name|UnloadDistributedZkTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|checkCreatedVsState
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|testCoreUnloadAndLeaders
argument_list|()
expr_stmt|;
comment|// long
name|testUnloadLotsOfCores
argument_list|()
expr_stmt|;
comment|// long
name|testUnloadShardAndCollection
argument_list|()
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testUnloadShardAndCollection
specifier|private
name|void
name|testUnloadShardAndCollection
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create one leader and one replica
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"test_unload_shard_and_collection_1"
argument_list|)
expr_stmt|;
name|String
name|collection
init|=
literal|"test_unload_shard_and_collection"
decl_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|String
name|coreDataDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|collection
operator|+
literal|"1"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|coreDataDir
argument_list|)
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|SolrServer
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|url1
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url1
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|createCmd
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"test_unload_shard_and_collection_2"
argument_list|)
expr_stmt|;
name|collection
operator|=
literal|"test_unload_shard_and_collection"
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|coreDataDir
operator|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|collection
operator|+
literal|"2"
expr_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|coreDataDir
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
comment|// does not mean they are active and up yet :*
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// now unload one of the two
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
literal|"test_unload_shard_and_collection_2"
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
comment|// there should be only one shard
name|int
name|slices
init|=
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlices
argument_list|(
name|collection
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|timeoutAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|45000
decl_stmt|;
while|while
condition|(
name|slices
operator|!=
literal|1
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|timeoutAt
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected to find only one slice in "
operator|+
name|collection
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|slices
operator|=
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlices
argument_list|(
name|collection
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// now unload one of the other
name|unloadCmd
operator|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
literal|"test_unload_shard_and_collection_1"
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
comment|//printLayout();
comment|// the collection should be gone
name|timeoutAt
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|30000
expr_stmt|;
while|while
condition|(
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|hasCollection
argument_list|(
name|collection
argument_list|)
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|timeoutAt
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Still found collection"
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @throws Exception on any problem    */
DECL|method|testCoreUnloadAndLeaders
specifier|private
name|void
name|testCoreUnloadAndLeaders
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a new collection collection
name|SolrServer
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|url1
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url1
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection1"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|String
name|core1DataDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"unloadcollection1"
operator|+
literal|"_1n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core1DataDir
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|slices
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|slices
argument_list|)
expr_stmt|;
name|client
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|String
name|url2
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url2
argument_list|)
expr_stmt|;
name|createCmd
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection2"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|String
name|core2dataDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"unloadcollection1"
operator|+
literal|"_2n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core2dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|slices
operator|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|slices
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ZkCoreNodeProps
name|leaderProps
init|=
name|getLeaderUrlFromZk
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|HttpSolrServer
name|collectionClient
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|collectionClient
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
comment|// lets try and use the solrj client to index and retrieve a couple
comment|// documents
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|6
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc2
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|7
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy3 sat on a walls"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc3
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|8
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy2 sat on a walled"
argument_list|)
decl_stmt|;
name|collectionClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|add
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// create another replica for our collection
name|client
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|String
name|url3
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url3
argument_list|)
expr_stmt|;
name|createCmd
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection3"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|String
name|core3dataDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"unloadcollection"
operator|+
literal|"_3n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core3dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// so that we start with some versions when we reload...
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|false
expr_stmt|;
name|HttpSolrServer
name|addClient
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url3
operator|+
literal|"/unloadcollection3"
argument_list|)
decl_stmt|;
name|addClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// add a few docs
for|for
control|(
name|int
name|x
init|=
literal|20
init|;
name|x
operator|<
literal|100
condition|;
name|x
operator|++
control|)
block|{
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
name|x
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|addClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
block|}
comment|// don't commit so they remain in the tran log
comment|//collectionClient.commit();
comment|// unload the leader
name|collectionClient
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|leaderProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
name|leaderProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|p
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|unloadCmd
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|collectionClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
comment|//    Thread.currentThread().sleep(500);
comment|//    printLayout();
name|int
name|tries
init|=
literal|50
decl_stmt|;
while|while
condition|(
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|zkStateReader
operator|.
name|getLeaderUrl
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|tries
operator|--
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Leader never changed"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ensure there is a leader
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
name|addClient
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url2
operator|+
literal|"/unloadcollection2"
argument_list|)
expr_stmt|;
name|addClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|addClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// add a few docs while the leader is down
for|for
control|(
name|int
name|x
init|=
literal|101
init|;
name|x
operator|<
literal|200
condition|;
name|x
operator|++
control|)
block|{
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
name|x
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|addClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
block|}
comment|// create another replica for our collection
name|client
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|String
name|url4
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url4
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|createCmd
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection4"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|String
name|core4dataDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"unloadcollection"
operator|+
literal|"_4n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core4dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// unload the leader again
name|leaderProps
operator|=
name|getLeaderUrlFromZk
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|collectionClient
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|leaderProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|unloadCmd
operator|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
name|leaderProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|=
operator|(
name|ModifiableSolrParams
operator|)
name|unloadCmd
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|collectionClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
name|tries
operator|=
literal|50
expr_stmt|;
while|while
condition|(
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|zkStateReader
operator|.
name|getLeaderUrl
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|tries
operator|--
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Leader never changed"
argument_list|)
expr_stmt|;
block|}
block|}
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
comment|// set this back
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|true
expr_stmt|;
comment|// bring the downed leader back as replica
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|leaderProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|createCmd
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
name|leaderProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core1DataDir
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url2
operator|+
literal|"/unloadcollection"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|long
name|found1
init|=
name|server
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url3
operator|+
literal|"/unloadcollection"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|q
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|long
name|found3
init|=
name|server
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url4
operator|+
literal|"/unloadcollection"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|q
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|long
name|found4
init|=
name|server
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
comment|// all 3 shards should now have the same number of docs
name|assertEquals
argument_list|(
name|found1
argument_list|,
name|found3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|found3
argument_list|,
name|found4
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnloadLotsOfCores
specifier|private
name|void
name|testUnloadLotsOfCores
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrServer
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|url3
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
specifier|final
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url3
argument_list|)
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"testExecutor"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
comment|// create the cores
name|createCores
argument_list|(
name|server
argument_list|,
name|executor
argument_list|,
literal|"multiunload"
argument_list|,
literal|2
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|120
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|executor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"testExecutor"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|cnt
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|freezeJ
init|=
name|j
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
literal|"multiunload"
operator|+
name|freezeJ
argument_list|)
expr_stmt|;
try|try
block|{
name|server
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|120
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
