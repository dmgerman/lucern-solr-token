begin_unit
begin_package
DECL|package|org.apache.solr.store.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|hdfs
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
name|store
operator|.
name|Lock
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
name|cloud
operator|.
name|hdfs
operator|.
name|HdfsTestUtil
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
name|BadHdfsThreadsFilter
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
name|AfterClass
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
import|;
end_import
begin_class
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|BadHdfsThreadsFilter
operator|.
name|class
comment|// hdfs currently leaks thread(s)
block|}
argument_list|)
DECL|class|HdfsLockFactoryTest
specifier|public
class|class
name|HdfsLockFactoryTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
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
name|Exception
block|{
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
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
block|}
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|URI
name|uri
init|=
name|dfsCluster
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|Path
name|lockPath
init|=
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/basedir/lock"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|HdfsDirectory
name|dir
init|=
operator|new
name|HdfsDirectory
argument_list|(
name|lockPath
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Lock
name|lock
init|=
name|dir
operator|.
name|makeLock
argument_list|(
literal|"testlock"
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|lock
operator|.
name|obtain
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"We could not get the lock when it should be available"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|success
operator|=
name|lock
operator|.
name|obtain
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"We got the lock but it should be unavailble"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|lock
operator|.
name|close
argument_list|()
expr_stmt|;
name|success
operator|=
name|lock
operator|.
name|obtain
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"We could not get the lock when it should be available"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|success
operator|=
name|lock
operator|.
name|obtain
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"We got the lock but it should be unavailble"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
