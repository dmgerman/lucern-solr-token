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
name|SolrJettyTestBase
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
operator|.
name|SuppressSSL
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
name|beans
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|BinaryRequestWriter
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
name|RequestWriter
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
name|QueryResponse
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Iterator
import|;
end_import
begin_comment
comment|/**  * Test for SOLR-1038  *  * @since solr 1.4  *  */
end_comment
begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|TestBatchUpdate
specifier|public
class|class
name|TestBatchUpdate
extends|extends
name|SolrJettyTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|createJetty
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|field|numdocs
specifier|static
specifier|final
name|int
name|numdocs
init|=
literal|1000
decl_stmt|;
annotation|@
name|Test
DECL|method|testWithXml
specifier|public
name|void
name|testWithXml
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrServer
name|httpSolrServer
init|=
operator|(
name|HttpSolrServer
operator|)
name|getSolrServer
argument_list|()
decl_stmt|;
name|httpSolrServer
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|RequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|httpSolrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|doIt
argument_list|(
name|httpSolrServer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithBinary
specifier|public
name|void
name|testWithBinary
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrServer
name|httpSolrServer
init|=
operator|(
name|HttpSolrServer
operator|)
name|getSolrServer
argument_list|()
decl_stmt|;
name|httpSolrServer
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|httpSolrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
name|doIt
argument_list|(
name|httpSolrServer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithBinaryBean
specifier|public
name|void
name|testWithBinaryBean
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrServer
name|httpSolrServer
init|=
operator|(
name|HttpSolrServer
operator|)
name|getSolrServer
argument_list|()
decl_stmt|;
name|httpSolrServer
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|httpSolrServer
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// delete everything!
specifier|final
name|int
index|[]
name|counter
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|counter
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
name|httpSolrServer
operator|.
name|addBeans
argument_list|(
operator|new
name|Iterator
argument_list|<
name|Bean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|counter
index|[
literal|0
index|]
operator|<
name|numdocs
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bean
name|next
parameter_list|()
block|{
name|Bean
name|bean
init|=
operator|new
name|Bean
argument_list|()
decl_stmt|;
name|bean
operator|.
name|id
operator|=
literal|""
operator|+
operator|(
operator|++
name|counter
index|[
literal|0
index|]
operator|)
expr_stmt|;
name|bean
operator|.
name|cat
operator|=
literal|"foocat"
expr_stmt|;
return|return
name|bean
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//do nothing
block|}
block|}
argument_list|)
expr_stmt|;
name|httpSolrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|httpSolrServer
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numdocs
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|Bean
specifier|public
specifier|static
class|class
name|Bean
block|{
annotation|@
name|Field
DECL|field|id
name|String
name|id
decl_stmt|;
annotation|@
name|Field
DECL|field|cat
name|String
name|cat
decl_stmt|;
block|}
DECL|method|doIt
specifier|private
name|void
name|doIt
parameter_list|(
name|HttpSolrServer
name|httpSolrServer
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
specifier|final
name|int
index|[]
name|counter
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|counter
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
name|httpSolrServer
operator|.
name|add
argument_list|(
operator|new
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|counter
index|[
literal|0
index|]
operator|<
name|numdocs
return|;
block|}
annotation|@
name|Override
specifier|public
name|SolrInputDocument
name|next
parameter_list|()
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
operator|(
operator|++
name|counter
index|[
literal|0
index|]
operator|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"cat"
argument_list|,
literal|"foocat"
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//do nothing
block|}
block|}
argument_list|)
expr_stmt|;
name|httpSolrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|httpSolrServer
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numdocs
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
