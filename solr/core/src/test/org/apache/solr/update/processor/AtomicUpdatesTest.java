begin_unit
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|AtomicUpdatesTest
specifier|public
class|class
name|AtomicUpdatesTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|h
operator|.
name|update
argument_list|(
literal|"<delete><query>*:*</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemove
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
decl_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"bbb"
block|,
literal|"ccc"
block|,
literal|"ccc"
block|,
literal|"ddd"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"bbb"
block|,
literal|"bbb"
block|,
literal|"ccc"
block|,
literal|"ddd"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"ccc"
block|,
literal|"ddd"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"21"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"bbb"
block|,
literal|"ddd"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '3']"
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|removeList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|removeList
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|removeList
operator|.
name|add
argument_list|(
literal|"ccc"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"remove"
argument_list|,
name|removeList
argument_list|)
argument_list|)
expr_stmt|;
comment|//behavior when hitting Solr through ZK
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"21"
argument_list|)
expr_stmt|;
name|removeList
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|removeList
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|removeList
operator|.
name|add
argument_list|(
literal|"ccc"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"remove"
argument_list|,
name|removeList
argument_list|)
argument_list|)
expr_stmt|;
comment|//behavior when hitting Solr through ZK
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"remove"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
comment|//behavior when hitting Solr directly
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:aaa"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '3']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAdd
specifier|public
name|void
name|testAdd
parameter_list|()
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
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"ccc"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"ccc"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '0']"
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"add"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSet
specifier|public
name|void
name|testSet
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
decl_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"ccc"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"ccc"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:aaa"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:ccc"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"set"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:aaa"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:ccc"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidOperation
specifier|public
name|void
name|testInvalidOperation
parameter_list|()
block|{
name|SolrInputDocument
name|doc
decl_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaa"
block|,
literal|"ccc"
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"cat"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"whatever"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:*"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:aaa"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:bbb"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cat:ccc"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//result[@numFound = '1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit