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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|impl
operator|.
name|HttpSolrServer
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import
begin_class
DECL|class|TestModifyConfFiles
specifier|public
class|class
name|TestModifyConfFiles
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|TestModifyConfFiles
specifier|public
name|TestModifyConfFiles
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|which
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|client
init|=
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
name|which
argument_list|)
decl_stmt|;
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
literal|"op"
argument_list|,
literal|"write"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"file"
argument_list|,
literal|"schema.xml"
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
literal|"/admin/file"
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have caught exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Input stream list was null for admin file write operation."
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|remove
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"stream.body"
argument_list|,
literal|"Testing rewrite of schema.xml file."
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have caught exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"No file name specified for write operation."
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|set
argument_list|(
literal|"file"
argument_list|,
literal|"bogus.txt"
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have caught exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can not access: bogus.txt"
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|set
argument_list|(
literal|"file"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|String
name|contents
init|=
operator|new
name|String
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/configs/conf1/schema.xml"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|//String schema = getFileContentFromZooKeeper("schema.xml");
name|assertTrue
argument_list|(
literal|"Schema contents should have changed!"
argument_list|,
literal|"Testing rewrite of schema.xml file."
operator|.
name|equals
argument_list|(
name|contents
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a velocity/whatever node. Put a bit of data in it. See if you can change it.
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/configs/conf1/velocity/test.vm"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"stream.body"
argument_list|,
literal|"Some bogus stuff for a test."
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"file"
argument_list|,
literal|"velocity/test.vm"
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|contents
operator|=
operator|new
name|String
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/configs/conf1/velocity/test.vm"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have found new content in a velocity/test.vm."
argument_list|,
name|contents
operator|.
name|indexOf
argument_list|(
literal|"Some bogus stuff for a test."
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
