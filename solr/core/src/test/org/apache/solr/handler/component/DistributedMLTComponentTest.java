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
name|BaseDistributedSearchTestCase
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
comment|/**  * Test for distributed MoreLikeThisComponent's   *  * @since solr 4.1  *  * @see org.apache.solr.handler.component.MoreLikeThisComponent  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|DistributedMLTComponentTest
specifier|public
class|class
name|DistributedMLTComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|requestHandlerName
specifier|private
name|String
name|requestHandlerName
decl_stmt|;
DECL|method|DistributedMLTComponentTest
specifier|public
name|DistributedMLTComponentTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|2
expr_stmt|;
name|stress
operator|=
literal|0
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
throws|throws
name|Exception
block|{    }
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
name|requestHandlerName
operator|=
literal|"mltrh"
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|"lowerfilt"
argument_list|,
literal|"toyota"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"chevrolet"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"suzuki"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ford"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ferrari"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"jaguar"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"mclaren moon or the moon and moon"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"sonata"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick red fox jumped over the lazy big and large brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The fat red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The slim red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped moon over the lazy brown dogs moon. Of course moon"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The hose red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The court red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"22"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"23"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"24"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The file red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"25"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"rod fix"
argument_list|)
expr_stmt|;
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
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// we care only about the mlt results
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
comment|// currently distrib mlt is sorting by score (even though it's not really comparable across shards)
comment|// so it may not match the sort of single shard mlt
name|handle
operator|.
name|put
argument_list|(
literal|"17"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"match_none"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:sonata"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:moon"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"24"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"23"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"22"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"21"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"20"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"19"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"18"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"17"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"16"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"15"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"14"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"13"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:fox"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|)
expr_stmt|;
comment|//query("q", "*:*", "mlt", "true", "mlt.fl", "lowerfilt", "qt", requestHandlerName, "shards.qt", requestHandlerName);
block|}
block|}
end_class
end_unit
