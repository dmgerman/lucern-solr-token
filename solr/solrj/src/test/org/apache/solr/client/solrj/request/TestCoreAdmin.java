begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
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
operator|.
name|request
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|SolrIgnoredThreadsFilter
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
name|embedded
operator|.
name|AbstractEmbeddedSolrServerTestCase
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
name|embedded
operator|.
name|EmbeddedSolrServer
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
name|response
operator|.
name|CoreAdminResponse
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
name|SolrException
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|SolrIgnoredThreadsFilter
operator|.
name|class
block|}
argument_list|)
DECL|class|TestCoreAdmin
specifier|public
class|class
name|TestCoreAdmin
extends|extends
name|AbstractEmbeddedSolrServerTestCase
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
DECL|field|tempDirProp
specifier|private
specifier|static
name|String
name|tempDirProp
decl_stmt|;
annotation|@
name|Rule
DECL|field|testRule
specifier|public
name|TestRule
name|testRule
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
comment|/*   @Override   protected File getSolrXml() throws Exception {     // This test writes on the directory where the solr.xml is located. Better     // to copy the solr.xml to     // the temporary directory where we store the index     File origSolrXml = new File(SOLR_HOME, SOLR_XML);     File solrXml = new File(tempDir, SOLR_XML);     FileUtils.copyFile(origSolrXml, solrXml);     return solrXml;   }   */
DECL|method|getSolrAdmin
specifier|protected
name|SolrClient
name|getSolrAdmin
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"core0"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testConfigSet
specifier|public
name|void
name|testConfigSet
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrClient
name|client
init|=
name|getSolrAdmin
argument_list|()
decl_stmt|;
name|File
name|testDir
init|=
name|createTempDir
argument_list|(
name|LuceneTestCase
operator|.
name|getTestClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|newCoreInstanceDir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"newcore"
argument_list|)
decl_stmt|;
name|CoreAdminRequest
operator|.
name|Create
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
literal|"corewithconfigset"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setInstanceDir
argument_list|(
name|newCoreInstanceDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setConfigSet
argument_list|(
literal|"configset-2"
argument_list|)
expr_stmt|;
name|CoreAdminResponse
name|response
init|=
name|req
operator|.
name|process
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"corewithconfigset"
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|"corewithconfigset"
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|core
argument_list|,
name|is
argument_list|(
name|notNullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCustomUlogDir
specifier|public
name|void
name|testCustomUlogDir
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|SolrClient
name|client
init|=
name|getSolrAdmin
argument_list|()
init|)
block|{
name|File
name|dataDir
init|=
name|createTempDir
argument_list|(
literal|"data"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|newCoreInstanceDir
init|=
name|createTempDir
argument_list|(
literal|"instance"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|instanceDir
init|=
operator|new
name|File
argument_list|(
name|cores
operator|.
name|getSolrHome
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|instanceDir
argument_list|,
operator|new
name|File
argument_list|(
name|newCoreInstanceDir
argument_list|,
literal|"newcore"
argument_list|)
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|Create
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
literal|"newcore"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setInstanceDir
argument_list|(
name|newCoreInstanceDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"newcore"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setDataDir
argument_list|(
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setUlogDir
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"ulog"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setConfigSet
argument_list|(
literal|"shared"
argument_list|)
expr_stmt|;
comment|// These should be the inverse of defaults.
name|req
operator|.
name|setIsLoadOnStartup
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|req
operator|.
name|setIsTransient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
comment|// Show that the newly-created core has values for load on startup and transient different than defaults due to the
comment|// above.
name|File
name|logDir
decl_stmt|;
try|try
init|(
name|SolrCore
name|coreProveIt
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|"collection1"
argument_list|)
init|;
name|SolrCore
name|core
operator|=
name|cores
operator|.
name|getCore
argument_list|(
literal|"newcore"
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|isTransient
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|coreProveIt
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|isTransient
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|isLoadOnStartup
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|coreProveIt
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|isLoadOnStartup
argument_list|()
argument_list|)
expr_stmt|;
name|logDir
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|getLogDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"ulog"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"tlog"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|logDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testErrorCases
specifier|public
name|void
name|testErrorCases
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
literal|"BADACTION"
argument_list|)
expr_stmt|;
name|String
name|collectionName
init|=
literal|"badactioncollection"
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"name"
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/cores"
argument_list|)
expr_stmt|;
name|boolean
name|gotExp
init|=
literal|false
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|getSolrAdmin
argument_list|()
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|gotExp
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotExp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidCoreNamesAreRejectedWhenCreatingCore
specifier|public
name|void
name|testInvalidCoreNamesAreRejectedWhenCreatingCore
parameter_list|()
block|{
specifier|final
name|Create
name|createRequest
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
try|try
block|{
name|createRequest
operator|.
name|setCoreName
argument_list|(
literal|"invalid$core@name"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid core"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$core@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidCoreNamesAreRejectedWhenRenamingExistingCore
specifier|public
name|void
name|testInvalidCoreNamesAreRejectedWhenRenamingExistingCore
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
literal|"validExistingCoreName"
argument_list|,
literal|"invalid$core@name"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"Invalid core"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"invalid$core@name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exceptionMessage
operator|.
name|contains
argument_list|(
literal|"must consist entirely of periods, underscores, hyphens, and alphanumerics"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|before
specifier|public
specifier|static
name|void
name|before
parameter_list|()
block|{
comment|// wtf?
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
operator|!=
literal|null
condition|)
name|tempDirProp
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{
comment|// wtf?
if|if
condition|(
name|tempDirProp
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"tempDir"
argument_list|,
name|tempDirProp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tempDir"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
