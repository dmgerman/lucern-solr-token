begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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
name|io
operator|.
name|StringWriter
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
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|store
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
name|document
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
name|analysis
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
name|index
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
name|search
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
name|queryParser
operator|.
name|*
import|;
end_import
begin_comment
comment|/** JUnit adaptation of an older test case SearchTest. */
end_comment
begin_class
DECL|class|TestSearch
specifier|public
class|class
name|TestSearch
extends|extends
name|LuceneTestCase
block|{
comment|/** Main for running test case by itself. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|TestSearch
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** This test performs a number of searches. It also compares output      *  of searches using multi-file index segments with single-file      *  index segments.      *      *  TODO: someone should check that the results of the searches are      *        still correct by adding assert statements. Right now, the test      *        passes if the results are the same between multi-file and      *        single-file formats, even if the results are wrong.      */
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|doTestSearch
argument_list|(
name|pw
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|multiFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|//System.out.println(multiFileOutput);
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestSearch
argument_list|(
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|singleFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|multiFileOutput
argument_list|,
name|singleFileOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestSearch
specifier|private
name|void
name|doTestSearch
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|conf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setUseCompoundDocStore
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|docs
init|=
block|{
literal|"a b c d e"
block|,
literal|"a b c d e a b c d e"
block|,
literal|"a b c d e f g h i j"
block|,
literal|"a c e"
block|,
literal|"e c a"
block|,
literal|"a c e a c e"
block|,
literal|"a c e a b c"
block|}
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|docs
index|[
name|j
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
index|[]
name|queries
init|=
block|{
literal|"a b"
block|,
literal|"\"a b\""
block|,
literal|"\"a b c\""
block|,
literal|"a c"
block|,
literal|"\"a c\""
block|,
literal|"\"a c e\""
block|,       }
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
literal|null
decl_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|parser
operator|.
name|setPhraseSlop
argument_list|(
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|queries
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|queries
index|[
name|j
index|]
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|query
operator|.
name|toString
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
comment|//DateFilter filter =
comment|//  new DateFilter("modified", Time(1997,0,1), Time(1998,0,1));
comment|//DateFilter filter = DateFilter.Before("modified", Time(1997,00,01));
comment|//System.out.println(filter);
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
operator|+
literal|" total results"
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
name|hits
operator|.
name|length
operator|&&
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" "
operator|+
name|hits
index|[
name|i
index|]
operator|.
name|score
comment|// 			   + " " + DateField.stringToDate(d.get("modified"))
operator|+
literal|" "
operator|+
name|d
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|Time
specifier|static
name|long
name|Time
parameter_list|(
name|int
name|year
parameter_list|,
name|int
name|month
parameter_list|,
name|int
name|day
parameter_list|)
block|{
name|GregorianCalendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|clear
argument_list|()
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|)
expr_stmt|;
return|return
name|calendar
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
end_class
end_unit
