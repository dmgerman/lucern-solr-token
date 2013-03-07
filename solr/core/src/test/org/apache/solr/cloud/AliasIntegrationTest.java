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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
operator|.
name|Slow
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
name|SolrServerException
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
name|JettySolrRunner
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
name|CollectionParams
operator|.
name|CollectionAction
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
begin_comment
comment|/**  * Test sync phase that occurs when Leader goes down and a new Leader is  * elected.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|AliasIntegrationTest
specifier|public
class|class
name|AliasIntegrationTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{        }
annotation|@
name|Before
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|AliasIntegrationTest
specifier|public
name|AliasIntegrationTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|shardCount
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|3
else|:
literal|4
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
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
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
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"collection2"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|numShardsNumReplicaList
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|numShardsNumReplicaList
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|numShardsNumReplicaList
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkForCollection
argument_list|(
literal|"collection2"
argument_list|,
name|numShardsNumReplicaList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"collection2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|6
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc2
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|7
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy3 sat on a walls"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc3
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|8
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy2 sat on a walled"
argument_list|)
decl_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrInputDocument
name|doc6
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|9
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc7
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|10
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy3 sat on a walls"
argument_list|)
decl_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
literal|"collection2"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc6
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc7
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// create alias
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
comment|// search for alias
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|QueryResponse
name|res
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// search for alias with random non cloud client
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jetty
init|=
name|jettys
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://127.0.0.1:"
operator|+
name|port
operator|+
name|context
operator|+
literal|"/testalias"
argument_list|)
decl_stmt|;
name|res
operator|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// now without collections param
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|jetty
operator|=
name|jettys
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://127.0.0.1:"
operator|+
name|port
operator|+
name|context
operator|+
literal|"/testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// create alias, collection2 first because it's not on every node
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2,collection1"
argument_list|)
expr_stmt|;
comment|// search for alias with random non cloud client
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|jetty
operator|=
name|jettys
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://127.0.0.1:"
operator|+
name|port
operator|+
name|context
operator|+
literal|"/testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// now without collections param
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|jetty
operator|=
name|jettys
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://127.0.0.1:"
operator|+
name|port
operator|+
name|context
operator|+
literal|"/testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// update alias
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2"
argument_list|)
expr_stmt|;
comment|//checkForAlias("testalias", "collection2");
comment|// search for alias
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// set alias to two collections
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection1,collection2"
argument_list|)
expr_stmt|;
comment|//checkForAlias("testalias", "collection1,collection2");
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// try a std client
comment|// search 1 and 2, but have no collections param
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|HttpSolrServer
name|client
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"/testalias"
argument_list|)
decl_stmt|;
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2"
argument_list|)
expr_stmt|;
comment|// a second alias
name|createAlias
argument_list|(
literal|"testalias2"
argument_list|,
literal|"collection2"
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"/testalias"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc8
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|11
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy4 sat on a walls"
argument_list|)
decl_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc8
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|res
operator|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|createAlias
argument_list|(
literal|"testalias"
argument_list|,
literal|"collection2,collection1"
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"testalias"
argument_list|)
expr_stmt|;
name|res
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|deleteAlias
argument_list|(
literal|"testalias"
argument_list|)
expr_stmt|;
name|deleteAlias
argument_list|(
literal|"testalias2"
argument_list|)
expr_stmt|;
name|boolean
name|sawException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|res
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|sawException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|sawException
argument_list|)
expr_stmt|;
block|}
DECL|method|createAlias
specifier|private
name|void
name|createAlias
parameter_list|(
name|String
name|alias
parameter_list|,
name|String
name|collections
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
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
literal|"collections"
argument_list|,
name|collections
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"name"
argument_list|,
name|alias
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionAction
operator|.
name|CREATEALIAS
operator|.
name|toString
argument_list|()
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
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
name|createNewSolrServer
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
block|}
DECL|method|deleteAlias
specifier|private
name|void
name|deleteAlias
parameter_list|(
name|String
name|alias
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
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
literal|"name"
argument_list|,
name|alias
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionAction
operator|.
name|DELETEALIAS
operator|.
name|toString
argument_list|()
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
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
name|createNewSolrServer
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
block|}
DECL|method|indexDoc
specifier|protected
name|void
name|indexDoc
parameter_list|(
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|skipServers
parameter_list|,
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
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
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|UpdateRequest
name|ureq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|skip
range|:
name|skipServers
control|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"test.distrib.skip.servers"
argument_list|,
name|skip
operator|.
name|url
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
name|ureq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
