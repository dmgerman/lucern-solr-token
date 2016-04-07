begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Constants
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
name|BaseDistributedSearchTestCase
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
name|impl
operator|.
name|BinaryResponseParser
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
name|HttpSolrClient
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
name|SolrDocument
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
name|CommonParams
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|DistributedQueryElevationComponentTest
specifier|public
class|class
name|DistributedQueryElevationComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|betterNotBeJ9
specifier|public
specifier|static
name|void
name|betterNotBeJ9
parameter_list|()
block|{
name|assumeFalse
argument_list|(
literal|"FIXME: SOLR-5791: This test fails under IBM J9"
argument_list|,
name|Constants
operator|.
name|JAVA_VENDOR
operator|.
name|startsWith
argument_list|(
literal|"IBM"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|DistributedQueryElevationComponentTest
specifier|public
name|DistributedQueryElevationComponentTest
parameter_list|()
block|{
name|stress
operator|=
literal|0
expr_stmt|;
comment|// TODO: a better way to do this?
name|configString
operator|=
literal|"solrconfig-elevate.xml"
expr_stmt|;
name|schemaString
operator|=
literal|"schema11.xml"
expr_stmt|;
block|}
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
literal|"elevate.data.file"
argument_list|,
literal|"elevate.xml"
argument_list|)
expr_stmt|;
name|File
name|parent
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"elevate.data.file"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|3
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"int_i"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"XXXX XXXX"
argument_list|,
literal|"field_t"
argument_list|,
literal|"anything"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"int_i"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"YYYY YYYY"
argument_list|,
literal|"plow_t"
argument_list|,
literal|"rake"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"int_i"
argument_list|,
literal|"3"
argument_list|,
literal|"text"
argument_list|,
literal|"ZZZZ ZZZZ"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"int_i"
argument_list|,
literal|"4"
argument_list|,
literal|"text"
argument_list|,
literal|"XXXX XXXX"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"int_i"
argument_list|,
literal|"5"
argument_list|,
literal|"text"
argument_list|,
literal|"ZZZZ ZZZZ ZZZZ"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"int_i"
argument_list|,
literal|"6"
argument_list|,
literal|"text"
argument_list|,
literal|"ZZZZ"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"int_i"
argument_list|,
literal|"7"
argument_list|,
literal|"text"
argument_list|,
literal|"solr"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"explain"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards.qt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"qt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id, score, [elevated]"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"ZZZZ"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*, [elevated]"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"sort"
argument_list|,
literal|"int_i desc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"solr"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*, [elevated]"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"sort"
argument_list|,
literal|"int_i asc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"ZZZZ"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"*, [elevated]"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
comment|// See SOLR-4854 for background on following test code
comment|// Uses XML response format by default
name|QueryResponse
name|response
init|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"XXXX"
argument_list|,
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"rows"
argument_list|,
literal|"500"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id, [elevated]"
argument_list|,
literal|"enableElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|,
literal|"elevateIds"
argument_list|,
literal|"6"
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|SolrDocument
name|document
init|=
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6.0f
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"[elevated]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Force javabin format
specifier|final
name|String
name|clientUrl
init|=
operator|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|HttpSolrClient
name|client
init|=
name|getHttpSolrClient
argument_list|(
name|clientUrl
argument_list|)
decl_stmt|;
name|client
operator|.
name|setParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"XXXX"
argument_list|)
operator|.
name|setParam
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
operator|.
name|setParam
argument_list|(
literal|"shards.qt"
argument_list|,
literal|"/elevate"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|500
argument_list|)
operator|.
name|setFields
argument_list|(
literal|"id,[elevated]"
argument_list|)
operator|.
name|setParam
argument_list|(
literal|"enableElevation"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|setParam
argument_list|(
literal|"forceElevation"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|setParam
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"6"
argument_list|,
literal|"wt"
argument_list|,
literal|"javabin"
argument_list|)
operator|.
name|setSort
argument_list|(
literal|"id"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
decl_stmt|;
name|setDistributedParams
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|document
operator|=
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6.0f
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"[elevated]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
