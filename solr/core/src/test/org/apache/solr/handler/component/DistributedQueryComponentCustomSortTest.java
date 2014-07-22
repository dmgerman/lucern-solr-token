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
name|SolrDocumentList
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
begin_comment
comment|/**  * Test for QueryComponent's distributed querying  *  * @see org.apache.solr.handler.component.QueryComponent  */
end_comment
begin_class
DECL|class|DistributedQueryComponentCustomSortTest
specifier|public
class|class
name|DistributedQueryComponentCustomSortTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedQueryComponentCustomSortTest
specifier|public
name|DistributedQueryComponentCustomSortTest
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
literal|"id"
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
literal|"text:a"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
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
literal|1
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"text:a"
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
literal|9
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"text:b"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
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
literal|4
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"text:b"
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
literal|10
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"text:c"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
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
literal|6
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"text:c"
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
literal|8
argument_list|,
literal|6
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
literal|"text:d"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
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
literal|12
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
literal|"text:d"
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
literal|12
argument_list|)
expr_stmt|;
comment|// sanity check function sorting
name|rsp
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:[1 TO 10]"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|,
literal|"sort"
argument_list|,
literal|"abs(sub(5,id)) asc, id desc"
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
literal|5
argument_list|,
literal|6
argument_list|,
literal|4
argument_list|,
literal|7
argument_list|,
literal|3
argument_list|,
literal|8
argument_list|,
literal|2
argument_list|,
literal|9
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Add two more docs with same payload as in doc #4
name|index
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
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
name|index
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
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
comment|// Add three more docs with same payload as in doc #10
name|index
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
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
name|index
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
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
name|index
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
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
name|commit
argument_list|()
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
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc, id desc"
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
literal|15
argument_list|,
literal|14
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|18
argument_list|,
literal|17
argument_list|,
literal|16
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
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc, id asc"
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
literal|16
argument_list|,
literal|17
argument_list|,
literal|18
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
