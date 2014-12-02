begin_unit
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
name|params
operator|.
name|ShardParams
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
name|SimpleOrderedMap
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
begin_comment
comment|/**  * Test for QueryComponent's distributed querying optimization.  * If the "fl" param is just "id" or just "id,score", all document data to return is already fetched by STAGE_EXECUTE_QUERY.  * The second STAGE_GET_FIELDS query is completely unnecessary.  * Eliminating that 2nd HTTP request can make a big difference in overall performance.  *  * @see QueryComponent  */
end_comment
begin_class
DECL|class|DistributedQueryComponentOptimizationTest
specifier|public
class|class
name|DistributedQueryComponentOptimizationTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedQueryComponentOptimizationTest
specifier|public
name|DistributedQueryComponentOptimizationTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|3
expr_stmt|;
name|stress
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUpBeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema-custom-field.xml"
argument_list|)
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
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"21"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x12
block|,
literal|0x62
block|,
literal|0x15
block|}
argument_list|)
argument_list|,
comment|//  2
comment|// quick check to prove "*" dynamicField hasn't been broken by somebody mucking with schema
literal|"asdfasdf_field_should_match_catchall_dynamic_field_adsfasdf"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"22"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x25
block|,
literal|0x21
block|,
literal|0x16
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  5
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"23"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x35
block|,
literal|0x32
block|,
literal|0x58
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  8
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"24"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x25
block|,
literal|0x21
block|,
literal|0x15
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  4
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"25"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x35
block|,
literal|0x35
block|,
literal|0x10
block|,
literal|0x00
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  9
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"text"
argument_list|,
literal|"c"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"26"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x1a
block|,
literal|0x2b
block|,
literal|0x3c
block|,
literal|0x00
block|,
literal|0x00
block|,
literal|0x03
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  3
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"text"
argument_list|,
literal|"c"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"27"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x00
block|,
literal|0x3c
block|,
literal|0x73
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  1
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"text"
argument_list|,
literal|"c"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"28"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x59
block|,
literal|0x2d
block|,
literal|0x4d
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// 11
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"29"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x39
block|,
literal|0x79
block|,
literal|0x7a
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// 10
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"30"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x31
block|,
literal|0x39
block|,
literal|0x7c
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  6
name|index
argument_list|(
name|id
argument_list|,
literal|"11"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"31"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xaf
block|,
operator|(
name|byte
operator|)
literal|0x9c
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// 13
name|index
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"32"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x34
block|,
operator|(
name|byte
operator|)
literal|0xdd
block|,
literal|0x4d
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|//  7
name|index
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"test_sS"
argument_list|,
literal|"33"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x11
block|,
literal|0x33
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// 12
name|commit
argument_list|()
expr_stmt|;
name|QueryResponse
name|rsp
decl_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,test_sS,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
name|id
argument_list|,
literal|7
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|,
literal|8
argument_list|,
literal|13
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
literal|"test_sS"
argument_list|,
literal|"27"
argument_list|,
literal|"21"
argument_list|,
literal|"26"
argument_list|,
literal|"24"
argument_list|,
literal|"22"
argument_list|,
literal|"30"
argument_list|,
literal|"32"
argument_list|,
literal|"23"
argument_list|,
literal|"25"
argument_list|,
literal|"29"
argument_list|,
literal|"28"
argument_list|,
literal|"33"
argument_list|,
literal|"31"
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
name|id
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|12
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|// works with just fl=id as well
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
name|id
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|12
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
name|id
argument_list|,
literal|7
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|,
literal|8
argument_list|,
literal|13
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,test_sS,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
name|id
argument_list|,
literal|7
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|,
literal|8
argument_list|,
literal|13
argument_list|,
literal|11
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
literal|"test_sS"
argument_list|,
literal|"27"
argument_list|,
literal|"21"
argument_list|,
literal|"26"
argument_list|,
literal|"24"
argument_list|,
literal|"22"
argument_list|,
literal|"30"
argument_list|,
literal|"32"
argument_list|,
literal|"23"
argument_list|,
literal|"25"
argument_list|,
literal|"29"
argument_list|,
literal|"28"
argument_list|,
literal|"33"
argument_list|,
literal|"31"
argument_list|)
expr_stmt|;
name|QueryResponse
name|nonDistribRsp
init|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,test_sS,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
decl_stmt|;
name|compareResponses
argument_list|(
name|rsp
argument_list|,
name|nonDistribRsp
argument_list|)
expr_stmt|;
comment|// make sure distrib and distrib.singlePass return the same thing
name|nonDistribRsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|compareResponses
argument_list|(
name|rsp
argument_list|,
name|nonDistribRsp
argument_list|)
expr_stmt|;
comment|// make sure distrib and distrib.singlePass return the same thing
comment|// verify that the optimization actually works
name|verifySinglePass
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
comment|// id only is optimized by default
name|verifySinglePass
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
comment|// id,score only is optimized by default
name|verifySinglePass
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// SOLR-6545, wild card field list
name|index
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"cat_a_sS"
argument_list|,
literal|"1"
argument_list|,
literal|"dynamic"
argument_list|,
literal|"2"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x11
block|,
literal|0x33
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|nonDistribRsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:19"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*a_sS"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:19"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*a_sS"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|nonDistribRsp
operator|.
name|getResults
argument_list|()
argument_list|,
literal|"id"
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
literal|"id"
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|nonDistribRsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:19"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,dynamic,cat*"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:19"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,dynamic,cat*"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|nonDistribRsp
operator|.
name|getResults
argument_list|()
argument_list|,
literal|"id"
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|assertFieldValues
argument_list|(
name|rsp
operator|.
name|getResults
argument_list|()
argument_list|,
literal|"id"
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|verifySinglePass
argument_list|(
literal|"q"
argument_list|,
literal|"id:19"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*a_sS"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifySinglePass
argument_list|(
literal|"q"
argument_list|,
literal|"id:19"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,dynamic,cat*"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// see SOLR-6795, distrib.singlePass=true would return score even when not asked for
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
name|handle
operator|.
name|put
argument_list|(
literal|"_version_"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// we don't to compare maxScore because most distributed requests return it anyway (just because they have score already)
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"{!func}id"
argument_list|,
name|ShardParams
operator|.
name|DISTRIB_SINGLE_PASS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// fix for a bug where not all fields are returned if using multiple fl parameters, see SOLR-6796
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"fl"
argument_list|,
literal|"dynamic"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
name|ShardParams
operator|.
name|DISTRIB_SINGLE_PASS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySinglePass
specifier|private
name|void
name|verifySinglePass
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
throws|throws
name|SolrServerException
block|{
name|QueryResponse
name|rsp
decl_stmt|;
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
name|params
operator|.
name|add
argument_list|(
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
literal|"track"
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|queryServer
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|debugMap
init|=
name|rsp
operator|.
name|getDebugMap
argument_list|()
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|track
init|=
operator|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
operator|)
name|debugMap
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|track
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"A single pass request should not have a GET_FIELDS phase"
argument_list|,
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
