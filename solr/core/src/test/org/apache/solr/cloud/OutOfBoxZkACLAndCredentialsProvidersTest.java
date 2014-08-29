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
name|List
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
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
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
name|data
operator|.
name|Stat
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|OutOfBoxZkACLAndCredentialsProvidersTest
specifier|public
class|class
name|OutOfBoxZkACLAndCredentialsProvidersTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractZkTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DATA_ENCODING
specifier|private
specifier|static
specifier|final
name|Charset
name|DATA_ENCODING
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
DECL|field|zkDir
specifier|protected
name|String
name|zkDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
block|}
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
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|createTempDir
argument_list|()
expr_stmt|;
name|zkDir
operator|=
name|createTempDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"ZooKeeper dataDir:"
operator|+
name|zkDir
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
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
name|zkClient
operator|.
name|close
argument_list|()
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
name|zkClient
operator|.
name|create
argument_list|(
literal|"/protectedCreateNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/protectedMakePathNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
literal|"/unprotectedCreateNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/unprotectedMakePathNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_END "
operator|+
name|getTestName
argument_list|()
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
name|zkServer
operator|.
name|shutdown
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
DECL|method|testOutOfBoxSolrZkClient
specifier|public
name|void
name|testOutOfBoxSolrZkClient
parameter_list|()
throws|throws
name|Exception
block|{
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
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOpenACLUnsafeAllover
specifier|public
name|void
name|testOpenACLUnsafeAllover
parameter_list|()
throws|throws
name|Exception
block|{
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
argument_list|)
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|verifiedList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertOpenACLUnsafeAllover
argument_list|(
name|zkClient
argument_list|,
literal|"/"
argument_list|,
name|verifiedList
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|verifiedList
operator|.
name|contains
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|verifiedList
operator|.
name|contains
argument_list|(
literal|"/solr/unprotectedCreateNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|verifiedList
operator|.
name|contains
argument_list|(
literal|"/solr/unprotectedMakePathNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|verifiedList
operator|.
name|contains
argument_list|(
literal|"/solr/protectedMakePathNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|verifiedList
operator|.
name|contains
argument_list|(
literal|"/solr/protectedCreateNode"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertOpenACLUnsafeAllover
specifier|protected
name|void
name|assertOpenACLUnsafeAllover
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|verifiedList
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ACL
argument_list|>
name|acls
init|=
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getACL
argument_list|(
name|path
argument_list|,
operator|new
name|Stat
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Verifying "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Path "
operator|+
name|path
operator|+
literal|" does not have OPEN_ACL_UNSAFE"
argument_list|,
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|acls
argument_list|)
expr_stmt|;
name|verifiedList
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|children
control|)
block|{
name|assertOpenACLUnsafeAllover
argument_list|(
name|zkClient
argument_list|,
name|path
operator|+
operator|(
operator|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|)
condition|?
literal|""
else|:
literal|"/"
operator|)
operator|+
name|child
argument_list|,
name|verifiedList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
