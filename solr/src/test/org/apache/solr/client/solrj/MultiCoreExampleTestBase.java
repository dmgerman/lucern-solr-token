begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|request
operator|.
name|UpdateRequest
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
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|MultiCoreExampleTestBase
specifier|public
specifier|abstract
class|class
name|MultiCoreExampleTestBase
extends|extends
name|SolrExampleTestBase
block|{
comment|// protected static final CoreContainer cores = new CoreContainer();
DECL|field|cores
specifier|protected
specifier|static
name|CoreContainer
name|cores
decl_stmt|;
DECL|method|getSolrHome
annotation|@
name|Override
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
literal|"../../../example/multicore/"
return|;
block|}
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
name|getSolrHome
argument_list|()
operator|+
literal|"core0/conf/schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
name|getSolrHome
argument_list|()
operator|+
literal|"core0/conf/solrconfig.xml"
return|;
block|}
DECL|method|setUp
annotation|@
name|Override
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
name|cores
operator|=
name|h
operator|.
name|getCoreContainer
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
DECL|method|testMultiCore
specifier|public
name|void
name|testMultiCore
parameter_list|()
throws|throws
name|Exception
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
literal|"yup"
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
comment|// You can't add it to core1
try|try
block|{
name|ignoreException
argument_list|(
literal|"unknown field"
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
block|{}
name|resetExceptionIgnores
argument_list|()
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
literal|"core1"
argument_list|,
literal|"yup"
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
name|ignoreException
argument_list|(
literal|"unknown field"
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
block|{}
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
comment|// test move
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
literal|"core1"
argument_list|,
literal|"corea"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
literal|"corea"
argument_list|,
literal|"coreb"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
literal|"coreb"
argument_list|,
literal|"corec"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
literal|"corec"
argument_list|,
literal|"cored"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|renameCore
argument_list|(
literal|"cored"
argument_list|,
literal|"corefoo"
argument_list|,
name|coreadmin
argument_list|)
expr_stmt|;
try|try
block|{
name|getSolrCore
argument_list|(
literal|"core1"
argument_list|)
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:BBB"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"core1 should be gone"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getSolrCore
argument_list|(
literal|"corefoo"
argument_list|)
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
block|}
end_class
end_unit
