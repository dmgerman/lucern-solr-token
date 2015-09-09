begin_unit
begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
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
name|SolrDocumentList
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
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestDefaultStatsCache
specifier|public
class|class
name|TestDefaultStatsCache
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|docId
specifier|private
name|int
name|docId
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|LocalStatsCache
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.statsCache"
argument_list|)
expr_stmt|;
block|}
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
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clients
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|shard
init|=
name|i
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|i
condition|;
name|j
operator|++
control|)
block|{
name|index_specific
argument_list|(
name|i
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
literal|"a_t"
argument_list|,
literal|"one two three"
argument_list|,
literal|"shard_i"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
name|commit
argument_list|()
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
name|dfQuery
argument_list|(
literal|"q"
argument_list|,
literal|"a_t:one"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|// add another document
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clients
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|shard
init|=
name|i
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|i
condition|;
name|j
operator|++
control|)
block|{
name|index_specific
argument_list|(
name|i
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
literal|"a_t"
argument_list|,
literal|"one two three four five"
argument_list|,
literal|"shard_i"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
name|commit
argument_list|()
expr_stmt|;
name|dfQuery
argument_list|(
literal|"q"
argument_list|,
literal|"a_t:one a_t:four"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
block|}
comment|// in this case, as the number of shards increases, per-shard scores begin to
comment|// diverge due to the different docFreq-s per shard.
DECL|method|checkResponse
specifier|protected
name|void
name|checkResponse
parameter_list|(
name|QueryResponse
name|controlRsp
parameter_list|,
name|QueryResponse
name|shardRsp
parameter_list|)
block|{
name|SolrDocumentList
name|shardList
init|=
name|shardRsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|controlList
init|=
name|controlRsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|controlList
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|shardList
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|Float
name|shardScore
init|=
operator|(
name|Float
operator|)
name|shardList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
name|Float
name|controlScore
init|=
operator|(
name|Float
operator|)
name|controlList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
if|if
condition|(
name|clients
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// only one shard
name|assertEquals
argument_list|(
name|controlScore
argument_list|,
name|shardScore
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dfQuery
specifier|protected
name|void
name|dfQuery
parameter_list|(
name|Object
modifier|...
name|q
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|q
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|q
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|q
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|QueryResponse
name|controlRsp
init|=
name|controlClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
decl_stmt|;
comment|// query a random server
name|params
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|shards
argument_list|)
expr_stmt|;
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
name|SolrClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|which
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|checkResponse
argument_list|(
name|controlRsp
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
