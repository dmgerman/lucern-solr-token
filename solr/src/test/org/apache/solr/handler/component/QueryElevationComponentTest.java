begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FileOutputStream
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
name|io
operator|.
name|PrintWriter
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
name|Map
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
name|IndexReader
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
name|BytesRef
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
name|params
operator|.
name|CommonParams
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
name|MapSolrParams
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
name|QueryElevationParams
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
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|component
operator|.
name|QueryElevationComponent
operator|.
name|ElevationObj
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
name|LocalSolrQueryRequest
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import
begin_class
DECL|class|QueryElevationComponentTest
specifier|public
class|class
name|QueryElevationComponentTest
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
literal|"solrconfig-elevate.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
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
name|clearIndex
argument_list|()
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
DECL|method|testInterface
specifier|public
name|void
name|testInterface
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|QueryElevationComponent
operator|.
name|FIELD_TYPE
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|QueryElevationComponent
operator|.
name|CONFIG_FILE
argument_list|,
literal|"elevate.xml"
argument_list|)
expr_stmt|;
name|QueryElevationComponent
name|comp
init|=
operator|new
name|QueryElevationComponent
argument_list|()
decl_stmt|;
name|comp
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|comp
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|core
operator|.
name|getSearcher
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ElevationObj
argument_list|>
name|map
init|=
name|comp
operator|.
name|getElevationMap
argument_list|(
name|reader
argument_list|,
name|core
argument_list|)
decl_stmt|;
comment|// Make sure the boosts loaded properly
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"XXXX"
argument_list|)
operator|.
name|priority
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"YYYY"
argument_list|)
operator|.
name|priority
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"ZZZZ"
argument_list|)
operator|.
name|priority
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"xxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"yyyy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"zzzz"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now test the same thing with a lowercase filter: 'lowerfilt'
name|args
operator|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|QueryElevationComponent
operator|.
name|FIELD_TYPE
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|QueryElevationComponent
operator|.
name|CONFIG_FILE
argument_list|,
literal|"elevate.xml"
argument_list|)
expr_stmt|;
name|comp
operator|=
operator|new
name|QueryElevationComponent
argument_list|()
expr_stmt|;
name|comp
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|comp
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|map
operator|=
name|comp
operator|.
name|getElevationMap
argument_list|(
name|reader
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"XXXX"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"YYYY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"ZZZZ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"xxxx"
argument_list|)
operator|.
name|priority
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"yyyy"
argument_list|)
operator|.
name|priority
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"zzzz"
argument_list|)
operator|.
name|priority
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xxxx"
argument_list|,
name|comp
operator|.
name|getAnalyzedQuery
argument_list|(
literal|"XXXX"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xxxxyyyy"
argument_list|,
name|comp
operator|.
name|getAnalyzedQuery
argument_list|(
literal|"XXXX YYYY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Make sure QEC handles null queries"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|,
literal|"q.alt"
argument_list|,
literal|"*:*"
argument_list|,
literal|"defType"
argument_list|,
literal|"dismax"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSorting
specifier|public
name|void
name|testSorting
parameter_list|()
throws|throws
name|IOException
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"a"
argument_list|,
literal|"title"
argument_list|,
literal|"ipod"
argument_list|,
literal|"str_s"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"b"
argument_list|,
literal|"title"
argument_list|,
literal|"ipod ipod"
argument_list|,
literal|"str_s"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"c"
argument_list|,
literal|"title"
argument_list|,
literal|"ipod ipod ipod"
argument_list|,
literal|"str_s"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"x"
argument_list|,
literal|"title"
argument_list|,
literal|"boosted"
argument_list|,
literal|"str_s"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"y"
argument_list|,
literal|"title"
argument_list|,
literal|"boosted boosted"
argument_list|,
literal|"str_s"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"z"
argument_list|,
literal|"title"
argument_list|,
literal|"boosted boosted boosted"
argument_list|,
literal|"str_s"
argument_list|,
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|query
init|=
literal|"title:ipod"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id,score"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//args.put( CommonParams.FL, "id,title,score" );
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|QueryElevationComponent
name|booster
init|=
operator|(
name|QueryElevationComponent
operator|)
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getSearchComponent
argument_list|(
literal|"elevate"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Make sure standard sort works as expected"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='a']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='c']"
argument_list|)
expr_stmt|;
comment|// Explicitly set what gets boosted
name|booster
operator|.
name|elevationCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|booster
operator|.
name|setTopQueryResults
argument_list|(
name|reader
argument_list|,
name|query
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"x"
block|,
literal|"y"
block|,
literal|"z"
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"All six should make it"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='6']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='x']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='y']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='z']"
argument_list|,
literal|"//result/doc[4]/str[@name='id'][.='a']"
argument_list|,
literal|"//result/doc[5]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[6]/str[@name='id'][.='c']"
argument_list|)
expr_stmt|;
name|booster
operator|.
name|elevationCache
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// now switch the order:
name|booster
operator|.
name|setTopQueryResults
argument_list|(
name|reader
argument_list|,
name|query
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"x"
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"All four should make it"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='a']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='x']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[4]/str[@name='id'][.='c']"
argument_list|)
expr_stmt|;
comment|// Test reverse sort
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|,
literal|"score asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"All four should make it"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[4]/str[@name='id'][.='a']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='x']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='c']"
argument_list|)
expr_stmt|;
comment|// Try normal sort by 'id'
comment|// default 'forceBoost' should be false
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|booster
operator|.
name|forceElevation
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|,
literal|"str_s asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='a']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='c']"
argument_list|,
literal|"//result/doc[4]/str[@name='id'][.='x']"
argument_list|)
expr_stmt|;
name|booster
operator|.
name|forceElevation
operator|=
literal|true
expr_stmt|;
name|assertQ
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='a']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='x']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[4]/str[@name='id'][.='c']"
argument_list|)
expr_stmt|;
comment|//Test exclusive (not to be confused with exclusion)
name|args
operator|.
name|put
argument_list|(
name|QueryElevationParams
operator|.
name|EXCLUSIVE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|booster
operator|.
name|setTopQueryResults
argument_list|(
name|reader
argument_list|,
name|query
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"x"
block|,
literal|"a"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='x']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='a']"
argument_list|)
expr_stmt|;
comment|// Test exclusion
name|booster
operator|.
name|elevationCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|QueryElevationParams
operator|.
name|EXCLUSIVE
argument_list|)
expr_stmt|;
name|booster
operator|.
name|setTopQueryResults
argument_list|(
name|reader
argument_list|,
name|query
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"x"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='x']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='b']"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.='c']"
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// write a test file to boost some docs
DECL|method|writeFile
specifier|private
name|void
name|writeFile
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|query
parameter_list|,
name|String
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<elevate>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<query text=\""
operator|+
name|query
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<doc id=\""
operator|+
name|id
operator|+
literal|"\"/>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"</query>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</elevate>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"OUT:"
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testElevationReloading
specifier|public
name|void
name|testElevationReloading
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testfile
init|=
literal|"data-elevation.xml"
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDataDir
argument_list|()
argument_list|,
name|testfile
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|f
argument_list|,
literal|"aaa"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|QueryElevationComponent
name|comp
init|=
operator|(
name|QueryElevationComponent
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSearchComponent
argument_list|(
literal|"elevate"
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|QueryElevationComponent
operator|.
name|CONFIG_FILE
argument_list|,
name|testfile
argument_list|)
expr_stmt|;
name|comp
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|comp
operator|.
name|inform
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ElevationObj
argument_list|>
name|map
init|=
name|comp
operator|.
name|getElevationMap
argument_list|(
name|reader
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
operator|.
name|priority
operator|.
name|containsKey
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"A"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now change the file
name|writeFile
argument_list|(
name|f
argument_list|,
literal|"bbb"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10000"
argument_list|)
argument_list|)
expr_stmt|;
comment|// will get same reader if no index change
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|=
name|req
argument_list|()
expr_stmt|;
name|reader
operator|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|map
operator|=
name|comp
operator|.
name|getElevationMap
argument_list|(
name|reader
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"bbb"
argument_list|)
operator|.
name|priority
operator|.
name|containsKey
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"B"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
