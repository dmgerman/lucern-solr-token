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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|cloud
operator|.
name|DistributedQueue
operator|.
name|QueueEvent
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
begin_class
DECL|class|DistributedQueueTest
specifier|public
class|class
name|DistributedQueueTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|UTF8
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkClient
specifier|protected
name|SolrZkClient
name|zkClient
decl_stmt|;
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
name|setupZk
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDistributedQueue
specifier|public
name|void
name|testDistributedQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dqZNode
init|=
literal|"/distqueue/test"
decl_stmt|;
name|String
name|testData
init|=
literal|"hello world"
decl_stmt|;
name|long
name|timeoutMs
init|=
literal|500L
decl_stmt|;
name|DistributedQueue
name|dq
init|=
operator|new
name|DistributedQueue
argument_list|(
name|zkClient
argument_list|,
name|setupDistributedQueueZNode
argument_list|(
name|dqZNode
argument_list|)
argument_list|)
decl_stmt|;
comment|// basic ops
name|assertTrue
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|testData
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
decl_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|dq
operator|.
name|peek
argument_list|()
argument_list|,
name|UTF8
argument_list|)
argument_list|,
name|testData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|dq
operator|.
name|take
argument_list|()
argument_list|,
name|UTF8
argument_list|)
argument_list|,
name|testData
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|QueueEvent
name|qe
init|=
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|,
name|timeoutMs
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|qe
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|dq
operator|.
name|remove
argument_list|()
argument_list|,
name|UTF8
argument_list|)
argument_list|,
name|testData
argument_list|)
expr_stmt|;
comment|// should block until the background thread makes the offer
operator|(
operator|new
name|QueueChangerThread
argument_list|(
name|dq
argument_list|,
literal|1000
argument_list|)
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|qe
operator|=
name|dq
operator|.
name|peek
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|qe
argument_list|)
expr_stmt|;
name|dq
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// timeout scenario ... background thread won't offer until long after the peek times out
name|QueueChangerThread
name|qct
init|=
operator|new
name|QueueChangerThread
argument_list|(
name|dq
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|qct
operator|.
name|start
argument_list|()
expr_stmt|;
name|qe
operator|=
name|dq
operator|.
name|peek
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|qe
operator|==
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|qct
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{}
block|}
DECL|class|QueueChangerThread
specifier|private
class|class
name|QueueChangerThread
extends|extends
name|Thread
block|{
DECL|field|dq
name|DistributedQueue
name|dq
decl_stmt|;
DECL|field|waitBeforeOfferMs
name|long
name|waitBeforeOfferMs
decl_stmt|;
DECL|method|QueueChangerThread
name|QueueChangerThread
parameter_list|(
name|DistributedQueue
name|dq
parameter_list|,
name|long
name|waitBeforeOfferMs
parameter_list|)
block|{
name|this
operator|.
name|dq
operator|=
name|dq
expr_stmt|;
name|this
operator|.
name|waitBeforeOfferMs
operator|=
name|waitBeforeOfferMs
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitBeforeOfferMs
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|getName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// do nothing
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exc
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|setupDistributedQueueZNode
specifier|protected
name|String
name|setupDistributedQueueZNode
parameter_list|(
name|String
name|znodePath
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/"
argument_list|,
literal|true
argument_list|)
condition|)
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
literal|true
argument_list|)
condition|)
name|zkClient
operator|.
name|clean
argument_list|(
name|znodePath
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|znodePath
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|znodePath
return|;
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
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{}
name|closeZk
argument_list|()
expr_stmt|;
block|}
DECL|method|setupZk
specifier|protected
name|void
name|setupZk
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|,
literal|"8000"
argument_list|)
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
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
name|zkClient
operator|=
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
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|closeZk
specifier|protected
name|void
name|closeZk
parameter_list|()
throws|throws
name|Exception
block|{
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
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
