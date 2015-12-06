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
name|Nightly
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
name|Test
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**  * This class is testing the cdcr extension to the {@link org.apache.solr.handler.ReplicationHandler} and  * {@link org.apache.solr.handler.IndexFetcher}.  */
end_comment
begin_class
annotation|@
name|Nightly
DECL|class|CdcrReplicationHandlerTest
specifier|public
class|class
name|CdcrReplicationHandlerTest
extends|extends
name|BaseCdcrDistributedZkTest
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|createTargetCollection
operator|=
literal|false
expr_stmt|;
comment|// we do not need the target cluster
name|shardCount
operator|=
literal|1
expr_stmt|;
comment|// we need only one shard
comment|// we need a persistent directory, otherwise the UpdateHandler will erase existing tlog files after restarting a node
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed from the start. The replication    * strategy should fetch all the missing tlog files from the leader.    */
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|testFullReplication
specifier|public
name|void
name|testFullReplication
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|slaves
init|=
name|this
operator|.
name|getShardToSlaveJetty
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|jetty
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
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|10
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
name|assertNumDocs
argument_list|(
literal|100
argument_list|,
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// Restart the slave node to trigger Replication strategy
name|this
operator|.
name|restartServer
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|assertUpdateLogsEquals
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed before receiving all the documents. The replication    * strategy should fetch all the missing tlog files from the leader.    */
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|testPartialReplication
specifier|public
name|void
name|testPartialReplication
parameter_list|()
throws|throws
name|Exception
block|{
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
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|20
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|20
operator|)
operator|+
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|slaves
init|=
name|this
operator|.
name|getShardToSlaveJetty
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|jetty
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|20
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|20
operator|)
operator|+
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
name|assertNumDocs
argument_list|(
literal|200
argument_list|,
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// Restart the slave node to trigger Replication strategy
name|this
operator|.
name|restartServer
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// at this stage, the slave should have replicated the 5 missing tlog files
name|this
operator|.
name|assertUpdateLogsEquals
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed before receiving a commit. This creates a truncated tlog    * file on the slave node. The replication strategy should detect this truncated file, and fetch the    * non-truncated file from the leader.    */
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|testPartialReplicationWithTruncatedTlog
specifier|public
name|void
name|testPartialReplicationWithTruncatedTlog
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|slaves
init|=
name|this
operator|.
name|getShardToSlaveJetty
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
try|try
block|{
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
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|20
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|20
operator|)
operator|+
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|client
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stop the slave in the middle of a batch to create a truncated tlog on the slave
if|if
condition|(
name|j
operator|==
literal|45
condition|)
block|{
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|jetty
argument_list|)
expr_stmt|;
block|}
block|}
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertNumDocs
argument_list|(
literal|200
argument_list|,
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// Restart the slave node to trigger Replication recovery
name|this
operator|.
name|restartServer
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// at this stage, the slave should have replicated the 5 missing tlog files
name|this
operator|.
name|assertUpdateLogsEquals
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave first recovered with a PeerSync strategy, then with a Replication strategy.    * The PeerSync strategy will generate a single tlog file for all the missing updates on the slave node.    * If a Replication strategy occurs at a later stage, it should remove this tlog file generated by PeerSync    * and fetch the corresponding tlog files from the leader.    */
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|testPartialReplicationAfterPeerSync
specifier|public
name|void
name|testPartialReplicationAfterPeerSync
parameter_list|()
throws|throws
name|Exception
block|{
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
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|10
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|slaves
init|=
name|this
operator|.
name|getShardToSlaveJetty
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|jetty
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|10
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
name|assertNumDocs
argument_list|(
literal|100
argument_list|,
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// Restart the slave node to trigger PeerSync recovery
comment|// (the update windows between leader and slave is small enough)
name|this
operator|.
name|restartServer
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|jetty
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|*
literal|20
init|;
name|j
operator|<
operator|(
name|i
operator|*
literal|20
operator|)
operator|+
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
comment|// restart the slave node to trigger Replication recovery
name|this
operator|.
name|restartServer
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// at this stage, the slave should have replicated the 5 missing tlog files
name|this
operator|.
name|assertUpdateLogsEquals
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|15
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed while the leader is still receiving updates.    * The slave should buffer updates while in recovery, then replay them at the end of the recovery.    * If updates were properly buffered and replayed, then the slave should have the same number of documents    * than the leader. This checks if cdcr tlog replication interferes with buffered updates - SOLR-8263.    */
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|testReplicationWithBufferedUpdates
specifier|public
name|void
name|testReplicationWithBufferedUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|slaves
init|=
name|this
operator|.
name|getShardToSlaveJetty
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
name|AtomicInteger
name|numDocs
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|(
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"cdcr-test-update-scheduler"
argument_list|)
argument_list|)
decl_stmt|;
name|executor
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|UpdateThread
argument_list|(
name|numDocs
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// Restart the slave node to trigger Replication strategy
name|this
operator|.
name|restartServer
argument_list|(
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// shutdown the update thread and wait for its completion
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|500
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// check that we have the expected number of documents in the cluster
name|assertNumDocs
argument_list|(
name|numDocs
operator|.
name|get
argument_list|()
argument_list|,
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// check that we have the expected number of documents on the slave
name|assertNumDocs
argument_list|(
name|numDocs
operator|.
name|get
argument_list|()
argument_list|,
name|slaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNumDocs
specifier|private
name|void
name|assertNumDocs
parameter_list|(
name|int
name|expectedNumDocs
parameter_list|,
name|CloudJettyRunner
name|jetty
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|SolrServerException
block|{
name|SolrClient
name|client
init|=
name|createNewSolrServer
argument_list|(
name|jetty
operator|.
name|url
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|cnt
init|=
literal|30
decl_stmt|;
comment|// timeout after 15 seconds
name|AssertionError
name|lastAssertionError
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|cnt
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|assertEquals
argument_list|(
name|expectedNumDocs
argument_list|,
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
name|lastAssertionError
operator|=
name|e
expr_stmt|;
name|cnt
operator|--
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Timeout while trying to assert number of documents @ "
operator|+
name|jetty
operator|.
name|url
argument_list|,
name|lastAssertionError
argument_list|)
throw|;
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|UpdateThread
specifier|private
class|class
name|UpdateThread
implements|implements
name|Runnable
block|{
DECL|field|numDocs
specifier|private
name|AtomicInteger
name|numDocs
decl_stmt|;
DECL|method|UpdateThread
specifier|private
name|UpdateThread
parameter_list|(
name|AtomicInteger
name|numDocs
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|numDocs
operator|.
name|get
argument_list|()
init|;
name|j
operator|<
operator|(
name|numDocs
operator|.
name|get
argument_list|()
operator|+
literal|10
operator|)
condition|;
name|j
operator|++
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
name|id
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|index
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|numDocs
operator|.
name|getAndAdd
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sent batch of {} updates - numDocs:{}"
argument_list|,
name|docs
operator|.
name|size
argument_list|()
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
DECL|method|getShardToSlaveJetty
specifier|private
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|getShardToSlaveJetty
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
name|collection
argument_list|)
operator|.
name|get
argument_list|(
name|shard
argument_list|)
argument_list|)
decl_stmt|;
name|CloudJettyRunner
name|leader
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
name|collection
argument_list|)
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
name|jetties
operator|.
name|remove
argument_list|(
name|leader
argument_list|)
expr_stmt|;
return|return
name|jetties
return|;
block|}
comment|/**    * Asserts that the update logs are in sync between the leader and slave. The leader and the slaves    * must have identical tlog files.    */
DECL|method|assertUpdateLogsEquals
specifier|protected
name|void
name|assertUpdateLogsEquals
parameter_list|(
name|String
name|collection
parameter_list|,
name|int
name|numberOfTLogs
parameter_list|)
throws|throws
name|Exception
block|{
name|CollectionInfo
name|info
init|=
name|collectInfo
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CollectionInfo
operator|.
name|CoreInfo
argument_list|>
argument_list|>
name|shardToCoresMap
init|=
name|info
operator|.
name|getShardToCoresMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|shard
range|:
name|shardToCoresMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|leaderFilesMeta
init|=
name|this
operator|.
name|getFilesMeta
argument_list|(
name|info
operator|.
name|getLeader
argument_list|(
name|shard
argument_list|)
operator|.
name|ulogDir
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|slaveFilesMeta
init|=
name|this
operator|.
name|getFilesMeta
argument_list|(
name|info
operator|.
name|getReplicas
argument_list|(
name|shard
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|ulogDir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect number of tlog files on the leader"
argument_list|,
name|numberOfTLogs
argument_list|,
name|leaderFilesMeta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect number of tlog files on the slave"
argument_list|,
name|numberOfTLogs
argument_list|,
name|slaveFilesMeta
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Long
name|leaderFileVersion
range|:
name|leaderFilesMeta
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"Slave is missing a tlog for version "
operator|+
name|leaderFileVersion
argument_list|,
name|slaveFilesMeta
operator|.
name|containsKey
argument_list|(
name|leaderFileVersion
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Slave's tlog file size differs for version "
operator|+
name|leaderFileVersion
argument_list|,
name|leaderFilesMeta
operator|.
name|get
argument_list|(
name|leaderFileVersion
argument_list|)
argument_list|,
name|slaveFilesMeta
operator|.
name|get
argument_list|(
name|leaderFileVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFilesMeta
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|getFilesMeta
parameter_list|(
name|String
name|dir
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Path to tlog "
operator|+
name|dir
operator|+
literal|" does not exists or it's not a directory."
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|filesMeta
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|tlogFile
range|:
name|file
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|filesMeta
operator|.
name|put
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|tlogFile
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|tlogFile
operator|.
name|getName
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|tlogFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|filesMeta
return|;
block|}
block|}
end_class
end_unit
