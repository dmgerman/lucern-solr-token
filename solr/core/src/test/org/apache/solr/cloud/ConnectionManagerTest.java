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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|common
operator|.
name|cloud
operator|.
name|ConnectionManager
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
name|WatchedEvent
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
name|Watcher
operator|.
name|Event
operator|.
name|EventType
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
name|Watcher
operator|.
name|Event
operator|.
name|KeeperState
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
begin_class
annotation|@
name|Slow
DECL|class|ConnectionManagerTest
specifier|public
class|class
name|ConnectionManagerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|3000
decl_stmt|;
annotation|@
name|Ignore
DECL|method|testConnectionManager
specifier|public
name|void
name|testConnectionManager
parameter_list|()
throws|throws
name|Exception
block|{
comment|// setup a SolrZkClient to do some getBaseUrlForNodeName testing
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|ConnectionManager
name|cm
init|=
name|zkClient
operator|.
name|getConnectionManager
argument_list|()
decl_stmt|;
try|try
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ISEXPIRED:"
operator|+
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|pauseCnxn
argument_list|(
name|TIMEOUT
argument_list|)
expr_stmt|;
name|long
name|sessionId
init|=
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|server
operator|.
name|expire
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cm
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testLikelyExpired
specifier|public
name|void
name|testLikelyExpired
parameter_list|()
throws|throws
name|Exception
block|{
comment|// setup a SolrZkClient to do some getBaseUrlForNodeName testing
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|ConnectionManager
name|cm
init|=
name|zkClient
operator|.
name|getConnectionManager
argument_list|()
decl_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|process
argument_list|(
operator|new
name|WatchedEvent
argument_list|(
name|EventType
operator|.
name|None
argument_list|,
name|KeeperState
operator|.
name|Disconnected
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// disconnect shouldn't immediately set likelyExpired
name|assertFalse
argument_list|(
name|cm
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
comment|// but it should after the timeout
name|Thread
operator|.
name|sleep
argument_list|(
call|(
name|long
call|)
argument_list|(
name|zkClient
operator|.
name|getZkClientTimeout
argument_list|()
operator|*
literal|1.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cm
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
comment|// even if we disconnect immediately again
name|cm
operator|.
name|process
argument_list|(
operator|new
name|WatchedEvent
argument_list|(
name|EventType
operator|.
name|None
argument_list|,
name|KeeperState
operator|.
name|Disconnected
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cm
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
comment|// reconnect -- should no longer be likely expired
name|cm
operator|.
name|process
argument_list|(
operator|new
name|WatchedEvent
argument_list|(
name|EventType
operator|.
name|None
argument_list|,
name|KeeperState
operator|.
name|SyncConnected
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cm
operator|.
name|isLikelyExpired
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cm
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cm
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
