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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|core
operator|.
name|SolrConfig
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
name|AbstractSolrTestCase
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
import|;
end_import
begin_class
DECL|class|ZkSolrClientTest
specifier|public
class|class
name|ZkSolrClientTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
DECL|method|testConnect
specifier|public
name|void
name|testConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
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
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
literal|null
decl_stmt|;
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
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
literal|100
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|testMakeRootNode
specifier|public
name|void
name|testMakeRootNode
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
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
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
literal|null
decl_stmt|;
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
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
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|testReconnect
specifier|public
name|void
name|testReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
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
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
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
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|String
name|shardsPath
init|=
literal|"/collections/collection1/shards"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|shardsPath
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection1"
argument_list|)
expr_stmt|;
name|int
name|zkServerPort
init|=
name|server
operator|.
name|getPort
argument_list|()
decl_stmt|;
comment|// this tests disconnect state
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|80
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection2"
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|fail
argument_list|(
literal|"Server should be down here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{        }
comment|// bring server back up
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|,
name|zkServerPort
argument_list|)
expr_stmt|;
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// TODO: can we do better?
comment|// wait for reconnect
name|Thread
operator|.
name|sleep
argument_list|(
literal|600
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection3"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// try again in a bit
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection3"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/collections/collection3"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/collections/collection1"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate session expiration
comment|// one option
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
comment|// another option
comment|//zkClient.getSolrZooKeeper().getConnection().disconnect();
comment|// this tests expired state
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// pause for reconnect
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection4"
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|SessionExpiredException
name|e
parameter_list|)
block|{          }
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{          }
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
operator|*
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Node does not exist, but it should"
argument_list|,
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/collections/collection4"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testWatchChildren
specifier|public
name|void
name|testWatchChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
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
literal|"zookeeper/server1/data"
decl_stmt|;
specifier|final
name|AtomicInteger
name|cnt
init|=
operator|new
name|AtomicInteger
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
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|400
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
specifier|final
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
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections"
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getChildren
argument_list|(
literal|"/collections"
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"children changed"
argument_list|)
expr_stmt|;
block|}
name|cnt
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// remake watch
try|try
block|{
name|zkClient
operator|.
name|getChildren
argument_list|(
literal|"/collections"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
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
name|InterruptedException
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
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/collection99/shards"
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection99/config=collection1"
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"collections/collection99/config=collection3"
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/collection97/shards"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
comment|// pause for the watches to fire
name|Thread
operator|.
name|sleep
argument_list|(
literal|700
argument_list|)
expr_stmt|;
if|if
condition|(
name|cnt
operator|.
name|intValue
argument_list|()
operator|<
literal|2
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
comment|// wait a bit more
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cnt
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|null
return|;
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
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
