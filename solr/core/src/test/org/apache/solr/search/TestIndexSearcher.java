begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|IndexReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|ReaderUtil
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|SchemaField
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
name|util
operator|.
name|Map
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
begin_class
DECL|class|TestIndexSearcher
specifier|public
class|class
name|TestIndexSearcher
extends|extends
name|SolrTestCaseJ4
block|{
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
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
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
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|optimize
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|commit
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getStringVal
specifier|private
name|String
name|getStringVal
parameter_list|(
name|SolrQueryRequest
name|sqr
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|sqr
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|ValueSource
name|vs
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|sf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
name|context
init|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|sqr
operator|.
name|getSearcher
argument_list|()
argument_list|)
decl_stmt|;
name|vs
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|sqr
operator|.
name|getSearcher
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReaderContext
name|topReaderContext
init|=
name|sqr
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|topReaderContext
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
name|AtomicReaderContext
name|leaf
init|=
name|leaves
index|[
name|idx
index|]
decl_stmt|;
name|FunctionValues
name|vals
init|=
name|vs
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|leaf
argument_list|)
decl_stmt|;
return|return
name|vals
operator|.
name|strVal
argument_list|(
name|doc
operator|-
name|leaf
operator|.
name|docBase
argument_list|)
return|;
block|}
DECL|method|testReopen
specifier|public
name|void
name|testReopen
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude"
argument_list|,
literal|"v_s1"
argument_list|,
literal|"string1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Yonik"
argument_list|,
literal|"v_s1"
argument_list|,
literal|"string2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr1
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|rCtx1
init|=
name|sr1
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|String
name|sval1
init|=
name|getStringVal
argument_list|(
name|sr1
argument_list|,
literal|"v_s1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"string1"
argument_list|,
name|sval1
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"v_s1"
argument_list|,
literal|"{!literal}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"v_s1"
argument_list|,
literal|"other stuff"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr2
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|rCtx2
init|=
name|sr2
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
comment|// make sure the readers share the first segment
comment|// Didn't work w/ older versions of lucene2.9 going from segment -> multi
name|assertEquals
argument_list|(
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx1
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
argument_list|,
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx2
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"v_f"
argument_list|,
literal|"3.14159"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"v_f"
argument_list|,
literal|"8983"
argument_list|,
literal|"v_s1"
argument_list|,
literal|"string6"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr3
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|rCtx3
init|=
name|sr3
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
comment|// make sure the readers share segments
comment|// assertEquals(r1.getLeafReaders()[0], r3.getLeafReaders()[0]);
name|assertEquals
argument_list|(
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx2
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
argument_list|,
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx3
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx2
argument_list|)
index|[
literal|1
index|]
operator|.
name|reader
argument_list|()
argument_list|,
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx3
argument_list|)
index|[
literal|1
index|]
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|sr1
operator|.
name|close
argument_list|()
expr_stmt|;
name|sr2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// should currently be 1, but this could change depending on future index management
name|int
name|baseRefCount
init|=
name|rCtx3
operator|.
name|reader
argument_list|()
operator|.
name|getRefCount
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|baseRefCount
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr4
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|rCtx4
init|=
name|sr4
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
comment|// force an index change so the registered searcher won't be the one we are testing (and
comment|// then we should be able to test the refCount going all the way to 0
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"v_f"
argument_list|,
literal|"7574"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// test that reader didn't change (according to equals at least... which uses the wrapped reader)
name|assertEquals
argument_list|(
name|rCtx3
operator|.
name|reader
argument_list|()
argument_list|,
name|rCtx4
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|baseRefCount
operator|+
literal|1
argument_list|,
name|rCtx4
operator|.
name|reader
argument_list|()
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|sr3
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|baseRefCount
argument_list|,
name|rCtx4
operator|.
name|reader
argument_list|()
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|sr4
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|baseRefCount
operator|-
literal|1
argument_list|,
name|rCtx4
operator|.
name|reader
argument_list|()
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr5
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|rCtx5
init|=
name|sr5
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr6
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|IndexReaderContext
name|rCtx6
init|=
name|sr6
operator|.
name|getSearcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx6
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// only a single doc left in the first segment
name|assertTrue
argument_list|(
operator|!
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx5
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
operator|.
name|equals
argument_list|(
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|rCtx6
argument_list|)
index|[
literal|0
index|]
operator|.
name|reader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// readers now different
name|sr5
operator|.
name|close
argument_list|()
expr_stmt|;
name|sr6
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
