begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
name|embedded
package|;
end_package
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
name|SolrServer
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
operator|.
name|ACTION
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
name|core
operator|.
name|SolrXMLCoresLocator
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
name|TestHarness
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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestSolrProperties
specifier|public
class|class
name|TestSolrProperties
extends|extends
name|AbstractEmbeddedSolrServerTestCase
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
name|TestSolrProperties
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_XML
init|=
literal|"solr.xml"
decl_stmt|;
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
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
annotation|@
name|Override
DECL|method|getSolrXml
specifier|protected
name|File
name|getSolrXml
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|,
name|SOLR_XML
argument_list|)
return|;
block|}
DECL|method|getSolrAdmin
specifier|protected
name|SolrServer
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
DECL|method|getRenamedSolrAdmin
specifier|protected
name|SolrServer
name|getRenamedSolrAdmin
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|cores
argument_list|,
literal|"renamed_core"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testProperties
specifier|public
name|void
name|testProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrXMLCoresLocator
operator|.
name|NonPersistingLocator
name|locator
init|=
operator|(
name|SolrXMLCoresLocator
operator|.
name|NonPersistingLocator
operator|)
name|cores
operator|.
name|getCoresLocator
argument_list|()
decl_stmt|;
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
literal|"core0"
argument_list|,
literal|"yup stopfra stopfrb stopena stopenb"
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
name|SolrTestCaseJ4
operator|.
name|ignoreException
argument_list|(
literal|"unknown field"
argument_list|)
expr_stmt|;
comment|// You can't add it to core1
try|try
block|{
name|up
operator|.
name|process
argument_list|(
name|getSolrCore1
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Can't add core0 field to core1!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{     }
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
literal|"core1"
argument_list|,
literal|"yup stopfra stopfrb stopena stopenb"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
literal|"core0"
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
comment|// You can't add it to core1
try|try
block|{
name|SolrTestCaseJ4
operator|.
name|ignoreException
argument_list|(
literal|"core0"
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
name|fail
argument_list|(
literal|"Can't add core1 field to core0!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{     }
name|SolrTestCaseJ4
operator|.
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
comment|// now Make sure AAA is in 0 and BBB in 1
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
comment|// Now test Changing the default core
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
comment|// Now test reloading it should have a newer open time
name|String
name|name
init|=
literal|"core0"
decl_stmt|;
name|SolrServer
name|coreadmin
init|=
name|getSolrAdmin
argument_list|()
decl_stmt|;
name|CoreAdminResponse
name|mcr
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|name
argument_list|,
name|coreadmin
argument_list|)
decl_stmt|;
name|long
name|before
init|=
name|mcr
operator|.
name|getStartTime
argument_list|(
name|name
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|CoreAdminRequest
operator|.
name|reloadCore
argument_list|(
name|name
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|mcr
operator|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|name
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|long
name|after
init|=
name|mcr
operator|.
name|getStartTime
argument_list|(
name|name
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"should have more recent time: "
operator|+
name|after
operator|+
literal|","
operator|+
name|before
argument_list|,
name|after
operator|>
name|before
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|locator
operator|.
name|xml
argument_list|,
literal|"/solr/cores[@defaultCoreName='core0']"
argument_list|,
literal|"/solr/cores[@host='127.0.0.1']"
argument_list|,
literal|"/solr/cores[@hostPort='${hostPort:8983}']"
argument_list|,
literal|"/solr/cores[@zkClientTimeout='8000']"
argument_list|,
literal|"/solr/cores[@hostContext='${hostContext:solr}']"
argument_list|,
literal|"/solr/cores[@genericCoreNodeNames='${genericCoreNodeNames:true}']"
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
name|name
argument_list|,
literal|"renamed_core"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|locator
operator|.
name|xml
argument_list|,
literal|"/solr/cores/core[@name='renamed_core']"
argument_list|,
literal|"/solr/cores/core[@instanceDir='${theInstanceDir:./}']"
argument_list|,
literal|"/solr/cores/core[@collection='${collection:acollection}']"
argument_list|)
expr_stmt|;
name|coreadmin
operator|=
name|getRenamedSolrAdmin
argument_list|()
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"data3"
argument_list|)
decl_stmt|;
name|File
name|tlogDir
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"tlog3"
argument_list|)
decl_stmt|;
name|CoreAdminRequest
operator|.
name|createCore
argument_list|(
literal|"newCore"
argument_list|,
name|SOLR_HOME
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|coreadmin
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|tlogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|validateXPath
argument_list|(
name|locator
operator|.
name|xml
argument_list|,
literal|"/solr/cores/core[@name='collection1' and @instanceDir='.']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
