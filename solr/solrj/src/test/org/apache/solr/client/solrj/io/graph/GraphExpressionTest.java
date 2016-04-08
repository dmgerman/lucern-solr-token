begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.io.graph
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
name|io
operator|.
name|graph
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|io
operator|.
name|SolrClientCache
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
name|io
operator|.
name|Tuple
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
name|io
operator|.
name|stream
operator|.
name|*
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|cloud
operator|.
name|AbstractZkTestCase
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
comment|/**  *  All base tests will be done with CloudSolrStream. Under the covers CloudSolrStream uses SolrStream so  *  SolrStream will get fully exercised through these tests.  *  **/
end_comment
begin_class
annotation|@
name|Slow
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|}
argument_list|)
DECL|class|GraphExpressionTest
specifier|public
class|class
name|GraphExpressionTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|SOLR_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
name|getFile
argument_list|(
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
static|static
block|{
name|schemaString
operator|=
literal|"schema-streaming.xml"
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{    }
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-streaming.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
DECL|method|SOLR_HOME
specifier|public
specifier|static
name|String
name|SOLR_HOME
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
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
comment|// we expect this time of exception as shards go up and down...
comment|//ignoreException(".*");
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
DECL|method|GraphExpressionTest
specifier|public
name|GraphExpressionTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAll
specifier|public
name|void
name|testAll
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
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
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|testShortestPathStream
argument_list|()
expr_stmt|;
block|}
DECL|method|testShortestPathStream
specifier|private
name|void
name|testShortestPathStream
parameter_list|()
throws|throws
name|Exception
block|{
name|indexr
argument_list|(
name|id
argument_list|,
literal|"0"
argument_list|,
literal|"from_s"
argument_list|,
literal|"jim"
argument_list|,
literal|"to_s"
argument_list|,
literal|"mike"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"from_s"
argument_list|,
literal|"jim"
argument_list|,
literal|"to_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"from_s"
argument_list|,
literal|"jim"
argument_list|,
literal|"to_s"
argument_list|,
literal|"stan"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"from_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"to_s"
argument_list|,
literal|"stan"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"from_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"to_s"
argument_list|,
literal|"bill"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"from_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"to_s"
argument_list|,
literal|"mike"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
literal|"from_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"to_s"
argument_list|,
literal|"alex"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
literal|"from_s"
argument_list|,
literal|"alex"
argument_list|,
literal|"to_s"
argument_list|,
literal|"steve"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"from_s"
argument_list|,
literal|"stan"
argument_list|,
literal|"to_s"
argument_list|,
literal|"alice"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"from_s"
argument_list|,
literal|"stan"
argument_list|,
literal|"to_s"
argument_list|,
literal|"mary"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"from_s"
argument_list|,
literal|"stan"
argument_list|,
literal|"to_s"
argument_list|,
literal|"dave"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"from_s"
argument_list|,
literal|"mary"
argument_list|,
literal|"to_s"
argument_list|,
literal|"mike"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"11"
argument_list|,
literal|"from_s"
argument_list|,
literal|"mary"
argument_list|,
literal|"to_s"
argument_list|,
literal|"max"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"from_s"
argument_list|,
literal|"mary"
argument_list|,
literal|"to_s"
argument_list|,
literal|"jim"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"from_s"
argument_list|,
literal|"mary"
argument_list|,
literal|"to_s"
argument_list|,
literal|"steve"
argument_list|,
literal|"predicate_s"
argument_list|,
literal|"knows"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
literal|null
decl_stmt|;
name|ShortestPathStream
name|stream
init|=
literal|null
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|SolrClientCache
name|cache
init|=
operator|new
name|SolrClientCache
argument_list|()
decl_stmt|;
name|context
operator|.
name|setSolrClientCache
argument_list|(
name|cache
argument_list|)
expr_stmt|;
name|StreamFactory
name|factory
init|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withCollectionZkHost
argument_list|(
literal|"collection1"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"shortestPath"
argument_list|,
name|ShortestPathStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
name|params
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"fq"
argument_list|,
literal|"predicate_s:knows"
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|(
name|ShortestPathStream
operator|)
name|factory
operator|.
name|constructStream
argument_list|(
literal|"shortestPath(collection1, "
operator|+
literal|"from=\"jim\", "
operator|+
literal|"to=\"steve\","
operator|+
literal|"edge=\"from_s=to_s\","
operator|+
literal|"fq=\"predicate_s:knows\","
operator|+
literal|"threads=\"3\","
operator|+
literal|"partitionSize=\"3\","
operator|+
literal|"maxDepth=\"6\")"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|paths
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|tuples
operator|=
name|getTuples
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tuples
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|tuple
operator|.
name|getStrings
argument_list|(
literal|"path"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
literal|"[jim, dave, alex, steve]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
literal|"[jim, stan, mary, steve]"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test with batch size of 1
name|params
operator|.
name|put
argument_list|(
literal|"fq"
argument_list|,
literal|"predicate_s:knows"
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|(
name|ShortestPathStream
operator|)
name|factory
operator|.
name|constructStream
argument_list|(
literal|"shortestPath(collection1, "
operator|+
literal|"from=\"jim\", "
operator|+
literal|"to=\"steve\","
operator|+
literal|"edge=\"from_s=to_s\","
operator|+
literal|"fq=\"predicate_s:knows\","
operator|+
literal|"threads=\"3\","
operator|+
literal|"partitionSize=\"1\","
operator|+
literal|"maxDepth=\"6\")"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|paths
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|tuples
operator|=
name|getTuples
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tuples
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|tuple
operator|.
name|getStrings
argument_list|(
literal|"path"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
literal|"[jim, dave, alex, steve]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
literal|"[jim, stan, mary, steve]"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test with bad predicate
name|stream
operator|=
operator|(
name|ShortestPathStream
operator|)
name|factory
operator|.
name|constructStream
argument_list|(
literal|"shortestPath(collection1, "
operator|+
literal|"from=\"jim\", "
operator|+
literal|"to=\"steve\","
operator|+
literal|"edge=\"from_s=to_s\","
operator|+
literal|"fq=\"predicate_s:crap\","
operator|+
literal|"threads=\"3\","
operator|+
literal|"partitionSize=\"3\","
operator|+
literal|"maxDepth=\"6\")"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|paths
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|tuples
operator|=
name|getTuples
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tuples
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|//Test with depth 2
name|stream
operator|=
operator|(
name|ShortestPathStream
operator|)
name|factory
operator|.
name|constructStream
argument_list|(
literal|"shortestPath(collection1, "
operator|+
literal|"from=\"jim\", "
operator|+
literal|"to=\"steve\","
operator|+
literal|"edge=\"from_s=to_s\","
operator|+
literal|"fq=\"predicate_s:knows\","
operator|+
literal|"threads=\"3\","
operator|+
literal|"partitionSize=\"3\","
operator|+
literal|"maxDepth=\"2\")"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|tuples
operator|=
name|getTuples
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tuples
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|//Take out alex
name|params
operator|.
name|put
argument_list|(
literal|"fq"
argument_list|,
literal|"predicate_s:knows NOT to_s:alex"
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|(
name|ShortestPathStream
operator|)
name|factory
operator|.
name|constructStream
argument_list|(
literal|"shortestPath(collection1, "
operator|+
literal|"from=\"jim\", "
operator|+
literal|"to=\"steve\","
operator|+
literal|"edge=\"from_s=to_s\","
operator|+
literal|"fq=\" predicate_s:knows NOT to_s:alex\","
operator|+
literal|"threads=\"3\","
operator|+
literal|"partitionSize=\"3\","
operator|+
literal|"maxDepth=\"6\")"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|paths
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|tuples
operator|=
name|getTuples
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tuples
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|tuple
operator|.
name|getStrings
argument_list|(
literal|"path"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|paths
operator|.
name|contains
argument_list|(
literal|"[jim, stan, mary, steve]"
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|getTuples
specifier|protected
name|List
argument_list|<
name|Tuple
argument_list|>
name|getTuples
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|)
throws|throws
name|IOException
block|{
name|tupleStream
operator|.
name|open
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
init|=
operator|new
name|ArrayList
argument_list|<
name|Tuple
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
name|t
init|=
name|tupleStream
operator|.
name|read
argument_list|()
init|;
operator|!
name|t
operator|.
name|EOF
condition|;
name|t
operator|=
name|tupleStream
operator|.
name|read
argument_list|()
control|)
block|{
name|tuples
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|tuples
return|;
block|}
DECL|method|assertOrder
specifier|protected
name|boolean
name|assertOrder
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|assertOrderOf
argument_list|(
name|tuples
argument_list|,
literal|"id"
argument_list|,
name|ids
argument_list|)
return|;
block|}
DECL|method|assertOrderOf
specifier|protected
name|boolean
name|assertOrderOf
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Tuple
name|t
init|=
name|tuples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Long
name|tip
init|=
operator|(
name|Long
operator|)
name|t
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tip
operator|.
name|intValue
argument_list|()
operator|!=
name|val
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|tip
operator|.
name|intValue
argument_list|()
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertMapOrder
specifier|protected
name|boolean
name|assertMapOrder
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Tuple
name|t
init|=
name|tuples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|>
name|tip
init|=
name|t
operator|.
name|getMaps
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
name|int
name|id
init|=
operator|(
name|int
operator|)
name|tip
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|val
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|id
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertFields
specifier|protected
name|boolean
name|assertFields
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|tuple
operator|.
name|fields
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected field '%s' not found"
argument_list|,
name|field
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertNotFields
specifier|protected
name|boolean
name|assertNotFields
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|tuple
operator|.
name|fields
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Unexpected field '%s' found"
argument_list|,
name|field
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertGroupOrder
specifier|protected
name|boolean
name|assertGroupOrder
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|?
argument_list|>
name|group
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|tuple
operator|.
name|get
argument_list|(
literal|"tuples"
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|t
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|group
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Long
name|tip
init|=
operator|(
name|Long
operator|)
name|t
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tip
operator|.
name|intValue
argument_list|()
operator|!=
name|val
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|tip
operator|.
name|intValue
argument_list|()
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertLong
specifier|public
name|boolean
name|assertLong
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|long
name|l
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|lv
init|=
operator|(
name|long
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lv
operator|!=
name|l
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Longs not equal:"
operator|+
name|l
operator|+
literal|" : "
operator|+
name|lv
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertString
specifier|public
name|boolean
name|assertString
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|actual
init|=
operator|(
name|String
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
literal|null
operator|==
name|expected
operator|&&
literal|null
operator|!=
name|actual
operator|)
operator|||
operator|(
literal|null
operator|!=
name|expected
operator|&&
literal|null
operator|==
name|actual
operator|)
operator|||
operator|(
literal|null
operator|!=
name|expected
operator|&&
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Longs not equal:"
operator|+
name|expected
operator|+
literal|" : "
operator|+
name|actual
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertMaps
specifier|protected
name|boolean
name|assertMaps
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|>
name|maps
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|maps
operator|.
name|size
argument_list|()
operator|!=
name|ids
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Expected id count != actual map count:"
operator|+
name|ids
operator|.
name|length
operator|+
literal|":"
operator|+
name|maps
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Map
name|t
init|=
name|maps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Long
name|tip
init|=
operator|(
name|Long
operator|)
name|t
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tip
operator|.
name|intValue
argument_list|()
operator|!=
name|val
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|tip
operator|.
name|intValue
argument_list|()
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertList
specifier|private
name|boolean
name|assertList
parameter_list|(
name|List
name|list
parameter_list|,
name|Object
modifier|...
name|vals
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|!=
name|vals
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Lists are not the same size:"
operator|+
name|list
operator|.
name|size
argument_list|()
operator|+
literal|" : "
operator|+
name|vals
operator|.
name|length
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|a
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|b
init|=
name|vals
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"List items not equals:"
operator|+
name|a
operator|+
literal|" : "
operator|+
name|b
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
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
name|getDoc
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit