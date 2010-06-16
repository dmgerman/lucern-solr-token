begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|Field
operator|.
name|Index
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|CommonParams
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
name|MapSolrParams
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|search
operator|.
name|SolrIndexReader
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
begin_comment
comment|/**  *   *  */
end_comment
begin_class
DECL|class|DirectUpdateHandlerTest
specifier|public
class|class
name|DirectUpdateHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema12.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|testRequireUniqueKey
specifier|public
name|void
name|testRequireUniqueKey
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|overwriteCommitted
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|overwritePending
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
comment|// Add a valid document
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"AAA"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"subject"
argument_list|,
literal|"xxxxx"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
comment|// Add a document with multiple ids
name|cmd
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
comment|// reset the id for this add
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"AAA"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"BBB"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"subject"
argument_list|,
literal|"xxxxx"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"added a document with multiple ids"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// expected
comment|// Add a document without an id
name|cmd
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
comment|// reset the id for this add
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"subject"
argument_list|,
literal|"xxxxx"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"added a document without an ids"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// expected
block|}
DECL|method|testUncommit
specifier|public
name|void
name|testUncommit
parameter_list|()
throws|throws
name|Exception
block|{
name|addSimpleDoc
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
comment|// search - not committed - "A" should not be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"A\" should not be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddCommit
specifier|public
name|void
name|testAddCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|addSimpleDoc
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
comment|// commit "A"
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|cmtCmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
comment|// search - "A" should be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"A\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteCommit
specifier|public
name|void
name|testDeleteCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|addSimpleDoc
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|addSimpleDoc
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
comment|// commit "A", "B"
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|cmtCmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
comment|// search - "A","B" should be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A OR id:B"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"A\" and \"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// delete "B"
name|deleteSimpleDoc
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
comment|// search - "A","B" should be found.
name|assertQ
argument_list|(
literal|"\"A\" and \"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// commit
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
comment|// search - "B" should not be found.
name|assertQ
argument_list|(
literal|"\"B\" should not be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddRollback
specifier|public
name|void
name|testAddRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|addSimpleDoc
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
comment|// commit "A"
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|updater
operator|instanceof
name|DirectUpdateHandler2
argument_list|)
expr_stmt|;
name|DirectUpdateHandler2
name|duh2
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|updater
decl_stmt|;
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|cmtCmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|addSimpleDoc
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
comment|// rollback "B"
name|RollbackUpdateCommand
name|rbkCmd
init|=
operator|new
name|RollbackUpdateCommand
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|rollback
argument_list|(
name|rbkCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// search - "B" should not be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A OR id:B"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"B\" should not be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|)
expr_stmt|;
comment|// Add a doc after the rollback to make sure we can continue to add/delete documents
comment|// after a rollback as normal
name|addSimpleDoc
argument_list|(
literal|"ZZZ"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"\"ZZZ\" must be found."
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:ZZZ"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='ZZZ']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteRollback
specifier|public
name|void
name|testDeleteRollback
parameter_list|()
throws|throws
name|Exception
block|{
name|addSimpleDoc
argument_list|(
literal|"A"
argument_list|)
expr_stmt|;
name|addSimpleDoc
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
comment|// commit "A", "B"
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|updater
operator|instanceof
name|DirectUpdateHandler2
argument_list|)
expr_stmt|;
name|DirectUpdateHandler2
name|duh2
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|updater
decl_stmt|;
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|cmtCmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// search - "A","B" should be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A OR id:B"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"A\" and \"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// delete "B"
name|deleteSimpleDoc
argument_list|(
literal|"B"
argument_list|)
expr_stmt|;
comment|// search - "A","B" should be found.
name|assertQ
argument_list|(
literal|"\"A\" and \"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// rollback "B"
name|RollbackUpdateCommand
name|rbkCmd
init|=
operator|new
name|RollbackUpdateCommand
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|deleteByIdCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|rollback
argument_list|(
name|rbkCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|deleteByIdCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// search - "B" should be found.
name|assertQ
argument_list|(
literal|"\"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// Add a doc after the rollback to make sure we can continue to add/delete documents
comment|// after a rollback as normal
name|addSimpleDoc
argument_list|(
literal|"ZZZ"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"\"ZZZ\" must be found."
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:ZZZ"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='ZZZ']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExpungeDeletes
specifier|public
name|void
name|testExpungeDeletes
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|SolrIndexReader
name|r
init|=
name|sr
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
operator|>
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// should have deletions
name|assertTrue
argument_list|(
name|r
operator|.
name|getLeafReaders
argument_list|()
operator|.
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// more than 1 segment
name|sr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"expungeDeletes"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|sr
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|r
operator|=
name|sr
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// no deletions
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// no dups
name|assertTrue
argument_list|(
name|r
operator|.
name|getLeafReaders
argument_list|()
operator|.
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// still more than 1 segment
name|sr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addSimpleDoc
specifier|private
name|void
name|addSimpleDoc
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|overwriteCommitted
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|overwritePending
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
comment|// Add a document
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteSimpleDoc
specifier|private
name|void
name|deleteSimpleDoc
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
comment|// Delete the document
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|cmd
operator|.
name|fromCommitted
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|fromPending
operator|=
literal|true
expr_stmt|;
name|updater
operator|.
name|delete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
