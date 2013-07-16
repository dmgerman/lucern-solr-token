begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|AbstractUpdateRequest
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
name|QueryRequest
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
name|UpdateRequest
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
name|params
operator|.
name|CoreAdminParams
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
name|SolrParams
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
name|CoreContainer
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
name|util
operator|.
name|ExternalPaths
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
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Abstract base class for testing merge indexes command  *  * @since solr 1.4  *  */
end_comment
begin_class
DECL|class|MergeIndexesExampleTestBase
specifier|public
specifier|abstract
class|class
name|MergeIndexesExampleTestBase
extends|extends
name|SolrExampleTestBase
block|{
DECL|field|cores
specifier|protected
name|CoreContainer
name|cores
decl_stmt|;
DECL|field|saveProp
specifier|private
name|String
name|saveProp
decl_stmt|;
DECL|field|dataDir2
specifier|private
name|File
name|dataDir2
decl_stmt|;
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|ExternalPaths
operator|.
name|EXAMPLE_MULTICORE_HOME
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass2
specifier|public
specifier|static
name|void
name|beforeClass2
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|dataDir
operator|==
literal|null
condition|)
block|{
name|createTempDir
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setupCoreContainer
specifier|protected
name|void
name|setupCoreContainer
parameter_list|()
block|{
name|cores
operator|=
operator|new
name|CoreContainer
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
expr_stmt|;
name|cores
operator|.
name|load
argument_list|()
expr_stmt|;
comment|//cores = CoreContainer.createAndLoad(getSolrHome(), new File(TEMP_DIR, "solr.xml"));
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
name|saveProp
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
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
name|setUp
argument_list|()
expr_stmt|;
comment|// setup datadirs
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.core0.data.dir"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|dataDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|dataDir2
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|dataDir2
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.core1.data.dir"
argument_list|,
name|this
operator|.
name|dataDir2
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|setupCoreContainer
argument_list|()
expr_stmt|;
name|SolrCore
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"CORES="
operator|+
name|cores
operator|+
literal|" : "
operator|+
name|cores
operator|.
name|getCoreNames
argument_list|()
argument_list|)
expr_stmt|;
name|cores
operator|.
name|setPersistent
argument_list|(
literal|false
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
name|String
name|skip
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|skip
operator|&&
literal|0
operator|!=
name|skip
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: per solr.test.leavedatadir, dataDir will not be removed: "
operator|+
name|dataDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|recurseDelete
argument_list|(
name|dataDir2
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"!!!! WARNING: best effort to remove "
operator|+
name|dataDir2
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" FAILED !!!!!"
argument_list|)
expr_stmt|;
block|}
block|}
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|saveProp
operator|==
literal|null
condition|)
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|saveProp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSolrServer
specifier|protected
specifier|final
name|SolrServer
name|getSolrServer
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|createNewSolrServer
specifier|protected
specifier|final
name|SolrServer
name|createNewSolrServer
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|getSolrCore0
specifier|protected
specifier|abstract
name|SolrServer
name|getSolrCore0
parameter_list|()
function_decl|;
DECL|method|getSolrCore1
specifier|protected
specifier|abstract
name|SolrServer
name|getSolrCore1
parameter_list|()
function_decl|;
DECL|method|getSolrAdmin
specifier|protected
specifier|abstract
name|SolrServer
name|getSolrAdmin
parameter_list|()
function_decl|;
DECL|method|getSolrCore
specifier|protected
specifier|abstract
name|SolrServer
name|getSolrCore
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|getIndexDirCore1
specifier|protected
specifier|abstract
name|String
name|getIndexDirCore1
parameter_list|()
function_decl|;
DECL|method|setupCores
specifier|private
name|UpdateRequest
name|setupCores
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|setAction
argument_list|(
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|up
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
expr_stmt|;
name|up
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Add something to each core
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"AAA"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"core0"
argument_list|)
expr_stmt|;
comment|// Add to core0
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add to core1
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"BBB"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"core1"
argument_list|)
expr_stmt|;
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|up
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now Make sure AAA is in 0 and BBB in 1
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|QueryRequest
name|r
init|=
operator|new
name|QueryRequest
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|q
operator|.
name|setQuery
argument_list|(
literal|"id:AAA"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:AAA"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getSolrCore1
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:AAA"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore1
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|up
return|;
block|}
DECL|method|testMergeIndexesByDirName
specifier|public
name|void
name|testMergeIndexesByDirName
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequest
name|up
init|=
name|setupCores
argument_list|()
decl_stmt|;
comment|// Now get the index directory of core1 and merge with core0
name|CoreAdminRequest
operator|.
name|mergeIndexes
argument_list|(
literal|"core0"
argument_list|,
operator|new
name|String
index|[]
block|{
name|getIndexDirCore1
argument_list|()
block|}
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|getSolrAdmin
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now commit the merged index
name|up
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// just do commit
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:AAA"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeIndexesByCoreName
specifier|public
name|void
name|testMergeIndexesByCoreName
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequest
name|up
init|=
name|setupCores
argument_list|()
decl_stmt|;
name|CoreAdminRequest
operator|.
name|mergeIndexes
argument_list|(
literal|"core0"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"core1"
block|}
argument_list|,
name|getSolrAdmin
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now commit the merged index
name|up
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// just do commit
name|up
operator|.
name|process
argument_list|(
name|getSolrCore0
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:AAA"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore0
argument_list|()
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeMultipleRequest
specifier|public
name|void
name|testMergeMultipleRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|CoreAdminRequest
operator|.
name|MergeIndexes
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|MergeIndexes
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
literal|"core0"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setIndexDirs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"/path/1"
argument_list|,
literal|"/path/2"
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setSrcCores
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"core1"
argument_list|,
literal|"core2"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|CoreAdminParams
operator|.
name|SRC_CORE
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|CoreAdminParams
operator|.
name|INDEX_DIR
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
