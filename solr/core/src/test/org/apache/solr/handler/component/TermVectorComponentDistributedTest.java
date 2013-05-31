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
name|common
operator|.
name|params
operator|.
name|TermVectorParams
import|;
end_import
begin_class
DECL|class|TermVectorComponentDistributedTest
specifier|public
class|class
name|TermVectorComponentDistributedTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
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
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"[docid]"
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
comment|// not a cloud test, but may use updateLog
comment|// SOLR-3720: TODO: TVC doesn't "merge" df and idf .. should it?
name|handle
operator|.
name|put
argument_list|(
literal|"df"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"tf-idf"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"This is a title and another title"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_notv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_postv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"This is a document"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"another document"
argument_list|)
expr_stmt|;
comment|//bunch of docs that are variants on blue
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blud"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"boue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"glue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blee"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blah"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|String
name|tv
init|=
literal|"tvrh"
decl_stmt|;
for|for
control|(
name|String
name|q
range|:
operator|new
name|String
index|[]
block|{
literal|"id:0"
block|,
literal|"id:7"
block|,
literal|"id:[3 TO 6]"
block|,
literal|"*:*"
block|}
control|)
block|{
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// tv.fl diff from fl
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"tv.fl"
argument_list|,
literal|"test_basictv,test_offtv"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// multi-valued tv.fl
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"tv.fl"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"tv.fl"
argument_list|,
literal|"test_offtv"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// re-use fl glob
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// re-use fl, ignore things we can't handle
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"score,test_basictv,[docid],test_postv,val:sum(3,4)"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// re-use (multi-valued) fl, ignore things we can't handle
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"fl"
argument_list|,
literal|"score,test_basictv"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],test_postv,val:sum(3,4)"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// test some other options
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|ALL
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// per field stuff
name|query
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"shards.qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
name|q
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|FIELDS
argument_list|,
literal|"test_basictv,test_notv,test_postv,test_offtv,test_posofftv"
argument_list|,
literal|"f.test_posofftv."
operator|+
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_offtv."
operator|+
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
