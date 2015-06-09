begin_unit
begin_comment
comment|/**  * Copyright (c) 2015 Renaud Delbru. All Rights Reserved.  */
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
name|junit
operator|.
name|Ignore
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
begin_class
annotation|@
name|Ignore
annotation|@
name|Slow
DECL|class|CdcrReplicationHandlerTest
specifier|public
class|class
name|CdcrReplicationHandlerTest
extends|extends
name|BaseCdcrDistributedZkTest
block|{
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
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|4
argument_list|)
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|doTestFullReplication
argument_list|()
expr_stmt|;
name|this
operator|.
name|doTestPartialReplication
argument_list|()
expr_stmt|;
name|this
operator|.
name|doTestPartialReplicationWithTruncatedTlog
argument_list|()
expr_stmt|;
name|this
operator|.
name|doTestPartialReplicationAfterPeerSync
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed from the start. The replication    * strategy should fetch all the missing tlog files from the leader.    */
DECL|method|doTestFullReplication
specifier|public
name|void
name|doTestFullReplication
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
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|getNumDocs
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
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
name|assertUpdateLogs
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed before receiving all the documents. The replication    * strategy should fetch all the missing tlog files from the leader.    */
DECL|method|doTestPartialReplication
specifier|public
name|void
name|doTestPartialReplication
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|clearSourceCollection
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
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|getNumDocs
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
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
name|assertUpdateLogs
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave is killed before receiving a commit. This creates a truncated tlog    * file on the slave node. The replication strategy should detect this truncated file, and fetch the    * non-truncated file from the leader.    */
DECL|method|doTestPartialReplicationWithTruncatedTlog
specifier|public
name|void
name|doTestPartialReplicationWithTruncatedTlog
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|clearSourceCollection
argument_list|()
expr_stmt|;
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
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|getNumDocs
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
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
name|assertUpdateLogs
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the scenario where the slave first recovered with a PeerSync strategy, then with a Replication strategy.    * The PeerSync strategy will generate a single tlog file for all the missing updates on the slave node.    * If a Replication strategy occurs at a later stage, it should remove this tlog file generated by PeerSync    * and fetch the corresponding tlog files from the leader.    */
DECL|method|doTestPartialReplicationAfterPeerSync
specifier|public
name|void
name|doTestPartialReplicationAfterPeerSync
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|clearSourceCollection
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
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|getNumDocs
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
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
name|assertUpdateLogs
argument_list|(
name|SOURCE_COLLECTION
argument_list|,
literal|15
argument_list|)
expr_stmt|;
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
comment|/**    * Asserts that the transaction logs between the leader and slave    */
annotation|@
name|Override
DECL|method|assertUpdateLogs
specifier|protected
name|void
name|assertUpdateLogs
parameter_list|(
name|String
name|collection
parameter_list|,
name|int
name|maxNumberOfTLogs
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
name|maxNumberOfTLogs
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
name|maxNumberOfTLogs
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