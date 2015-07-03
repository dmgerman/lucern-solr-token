begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import
begin_class
annotation|@
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
DECL|class|TestSortingResponseWriter
specifier|public
class|class
name|TestSortingResponseWriter
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"export.test"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-sortingresponse.xml"
argument_list|,
literal|"schema-sortingresponse.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"2.1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"1"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"hello world"
argument_list|,
literal|"longdv"
argument_list|,
literal|"323223232323"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"2344.345"
argument_list|,
literal|"intdv_m"
argument_list|,
literal|"100"
argument_list|,
literal|"intdv_m"
argument_list|,
literal|"250"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"123.321"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"345.123"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"3444.222"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"23232.2"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"43434343434"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"343332"
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"manchester \"city\""
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"liverpool"
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"Everton"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"2.1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"7"
argument_list|,
literal|"longdv"
argument_list|,
literal|"323223232323"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"2344.345"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"123.321"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"345.123"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"3444.222"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"23232.2"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"43434343434"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"343332"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
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
literal|"floatdv"
argument_list|,
literal|"2.1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"2"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"hello world"
argument_list|,
literal|"longdv"
argument_list|,
literal|"323223232323"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"2344.344"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
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
literal|"floatdv"
argument_list|,
literal|"2.1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"3"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"chello world"
argument_list|,
literal|"longdv"
argument_list|,
literal|"323223232323"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"2344.346"
argument_list|,
literal|"intdv_m"
argument_list|,
literal|"100"
argument_list|,
literal|"intdv_m"
argument_list|,
literal|"250"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"123.321"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"345.123"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"3444.222"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"23232.2"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"43434343434"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"343332"
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"manchester \"city\""
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"liverpool"
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"everton"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"2.1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"10000000"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"chello \"world\""
argument_list|,
literal|"longdv"
argument_list|,
literal|"323223232323"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"2344.346"
argument_list|,
literal|"intdv_m"
argument_list|,
literal|"100"
argument_list|,
literal|"intdv_m"
argument_list|,
literal|"250"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"123.321"
argument_list|,
literal|"floatdv_m"
argument_list|,
literal|"345.123"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"3444.222"
argument_list|,
literal|"doubledv_m"
argument_list|,
literal|"23232.2"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"43434343434"
argument_list|,
literal|"longdv_m"
argument_list|,
literal|"343332"
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"manchester \"city\""
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"liverpool"
argument_list|,
literal|"stringdv_m"
argument_list|,
literal|"everton"
argument_list|)
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
DECL|method|testSortingOutput
specifier|public
name|void
name|testSortingOutput
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Test single value DocValue output
name|String
name|s
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"floatdv,intdv,stringdv,longdv,doubledv"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv asc"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":1, \"docs\":[{\"floatdv\":2.1,\"intdv\":1,\"stringdv\":\"hello world\",\"longdv\":323223232323,\"doubledv\":2344.345}]}}"
argument_list|)
expr_stmt|;
comment|//Test null value string:
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:7"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"floatdv,intdv,stringdv,longdv,doubledv"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":1, \"docs\":[{\"floatdv\":2.1,\"intdv\":7,\"stringdv\":\"\",\"longdv\":323223232323,\"doubledv\":2344.345}]}}"
argument_list|)
expr_stmt|;
comment|//Test multiValue docValues output
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv_m,floatdv_m,doubledv_m,longdv_m,stringdv_m"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":1, \"docs\":[{\"intdv_m\":[100,250],\"floatdv_m\":[123.321,345.123],\"doubledv_m\":[3444.222,23232.2],\"longdv_m\":[343332,43434343434],\"stringdv_m\":[\"Everton\",\"liverpool\",\"manchester \\\"city\\\"\"]}]}}"
argument_list|)
expr_stmt|;
comment|//Test multiValues docValues output with nulls
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:7"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv_m,floatdv_m,doubledv_m,longdv_m,stringdv_m"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":1, \"docs\":[{\"intdv_m\":[],\"floatdv_m\":[123.321,345.123],\"doubledv_m\":[3444.222,23232.2],\"longdv_m\":[343332,43434343434],\"stringdv_m\":[]}]}}"
argument_list|)
expr_stmt|;
comment|//Test single sort param is working
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":2, \"docs\":[{\"intdv\":2},{\"intdv\":1}]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":2, \"docs\":[{\"intdv\":1},{\"intdv\":2}]}}"
argument_list|)
expr_stmt|;
comment|// Test sort on String will null value. Null value should sort last on desc and first on asc.
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 7)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"stringdv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":2, \"docs\":[{\"intdv\":1},{\"intdv\":7}]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 7)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"stringdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":2, \"docs\":[{\"intdv\":7},{\"intdv\":1}]}}"
argument_list|)
expr_stmt|;
comment|//Test multi-sort params
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"floatdv asc,intdv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":2, \"docs\":[{\"intdv\":2},{\"intdv\":1}]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"floatdv desc,intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":2, \"docs\":[{\"intdv\":1},{\"intdv\":2}]}}"
argument_list|)
expr_stmt|;
comment|//Test three sort fields
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2 3)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"floatdv asc,stringdv asc,intdv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":3, \"docs\":[{\"intdv\":3},{\"intdv\":2},{\"intdv\":1}]}}"
argument_list|)
expr_stmt|;
comment|//Test three sort fields
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2 3)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"floatdv asc,stringdv desc,intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":3, \"docs\":[{\"intdv\":1},{\"intdv\":2},{\"intdv\":3}]}}"
argument_list|)
expr_stmt|;
comment|//Test four sort fields
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2 3)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"floatdv asc,floatdv desc,floatdv asc,intdv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":3, \"docs\":[{\"intdv\":3},{\"intdv\":2},{\"intdv\":1}]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:(1 2 3)"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"doubledv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":3, \"docs\":[{\"intdv\":3},{\"intdv\":1},{\"intdv\":2}]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"intdv:[2 TO 1000]"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"doubledv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":3, \"docs\":[{\"intdv\":3},{\"intdv\":7},{\"intdv\":2}]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"stringdv:blah"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"intdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"doubledv desc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":0, \"docs\":[]}}"
argument_list|)
expr_stmt|;
name|s
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:8"
argument_list|,
literal|"qt"
argument_list|,
literal|"/export"
argument_list|,
literal|"fl"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"sort"
argument_list|,
literal|"intdv asc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
literal|"{\"responseHeader\": {\"status\": 0}, \"response\":{\"numFound\":1, \"docs\":[{\"stringdv\":\"chello \\\"world\\\"\"}]}}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
