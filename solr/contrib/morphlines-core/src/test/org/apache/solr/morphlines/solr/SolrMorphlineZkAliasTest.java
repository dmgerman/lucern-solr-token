begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|BadHdfsThreadsFilter
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
name|kitesdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Fields
import|;
end_import
begin_import
import|import
name|org
operator|.
name|kitesdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
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
name|BadHdfsThreadsFilter
operator|.
name|class
comment|// hdfs currently leaks thread(s)
block|}
argument_list|)
annotation|@
name|Slow
DECL|class|SolrMorphlineZkAliasTest
specifier|public
class|class
name|SolrMorphlineZkAliasTest
extends|extends
name|AbstractSolrMorphlineZkTestBase
block|{
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|createAlias
argument_list|(
literal|"aliascollection"
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|morphline
operator|=
name|parse
argument_list|(
literal|"test-morphlines"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"loadSolrBasic"
argument_list|,
literal|"aliascollection"
argument_list|)
expr_stmt|;
name|Record
name|record
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
name|record
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
literal|"id0-innsbruck"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
literal|"mytext"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"user_screen_name"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"first_name"
argument_list|,
literal|"Nadja"
argument_list|)
expr_stmt|;
comment|// will be sanitized
name|startSession
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collector
operator|.
name|getNumStartEvents
argument_list|()
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyBeginTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|morphline
operator|.
name|process
argument_list|(
name|record
argument_list|)
argument_list|)
expr_stmt|;
name|record
operator|=
operator|new
name|Record
argument_list|()
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
literal|"id1-innsbruck"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
literal|"mytext1"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"user_screen_name"
argument_list|,
literal|"foo1"
argument_list|)
expr_stmt|;
name|record
operator|.
name|put
argument_list|(
literal|"first_name"
argument_list|,
literal|"Nadja1"
argument_list|)
expr_stmt|;
comment|// will be sanitized
name|assertTrue
argument_list|(
name|morphline
operator|.
name|process
argument_list|(
name|record
argument_list|)
argument_list|)
expr_stmt|;
name|Record
name|expected
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
literal|"id0-innsbruck"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
literal|"mytext"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"user_screen_name"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Record
argument_list|>
name|citer
init|=
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|citer
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Record
name|expected2
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
name|expected2
operator|.
name|put
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
literal|"id1-innsbruck"
argument_list|)
expr_stmt|;
name|expected2
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
literal|"mytext1"
argument_list|)
expr_stmt|;
name|expected2
operator|.
name|put
argument_list|(
literal|"user_screen_name"
argument_list|,
literal|"foo1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected2
argument_list|,
name|citer
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|citer
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|100000
argument_list|)
operator|.
name|addSort
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println(rsp);
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|iter
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|getFields
argument_list|()
argument_list|,
name|next
argument_list|(
name|iter
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected2
operator|.
name|getFields
argument_list|()
argument_list|,
name|next
argument_list|(
name|iter
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyRollbackTransaction
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|Notifications
operator|.
name|notifyShutdown
argument_list|(
name|morphline
argument_list|)
expr_stmt|;
name|createAlias
argument_list|(
literal|"aliascollection"
argument_list|,
literal|"collection1,collection2"
argument_list|)
expr_stmt|;
try|try
block|{
name|parse
argument_list|(
literal|"test-morphlines"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"loadSolrBasic"
argument_list|,
literal|"aliascollection"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IAE because update alias maps to multiple collections"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{            }
block|}
DECL|method|createAlias
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
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
return|return
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
end_class
end_unit
