begin_unit
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
begin_class
DECL|class|SolrTestCaseJ4Test
specifier|public
class|class
name|SolrTestCaseJ4Test
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|String
name|tmpSolrHome
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
comment|// Create a temporary directory that holds a core NOT named "collection1". Use the smallest configuration sets
comment|// we can so we don't copy that much junk around.
name|tmpSolrHome
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|File
name|subHome
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
literal|"core0"
argument_list|)
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to make subdirectory "
argument_list|,
name|subHome
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|top
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|top
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
literal|"core0"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
literal|"core1"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/solr-multicore.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-minimal.xml"
argument_list|,
literal|"schema-tiny.xml"
argument_list|,
name|tmpSolrHome
argument_list|,
literal|"core1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|AfterClass
specifier|public
specifier|static
name|void
name|AfterClass
parameter_list|()
throws|throws
name|Exception
block|{    }
annotation|@
name|Test
DECL|method|testCorrectCore
specifier|public
name|void
name|testCorrectCore
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"should be core1"
argument_list|,
literal|"core1"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
