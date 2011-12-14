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
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
begin_class
DECL|class|TestSearchForDuplicates
specifier|public
class|class
name|TestSearchForDuplicates
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
name|TestSearchForDuplicates
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|PRIORITY_FIELD
specifier|static
specifier|final
name|String
name|PRIORITY_FIELD
init|=
literal|"priority"
decl_stmt|;
DECL|field|ID_FIELD
specifier|static
specifier|final
name|String
name|ID_FIELD
init|=
literal|"id"
decl_stmt|;
DECL|field|HIGH_PRIORITY
specifier|static
specifier|final
name|String
name|HIGH_PRIORITY
init|=
literal|"high"
decl_stmt|;
DECL|field|MED_PRIORITY
specifier|static
specifier|final
name|String
name|MED_PRIORITY
init|=
literal|"medium"
decl_stmt|;
DECL|field|LOW_PRIORITY
specifier|static
specifier|final
name|String
name|LOW_PRIORITY
init|=
literal|"low"
decl_stmt|;
comment|/** This test compares search results when using and not using compound    *  files.    *    *  TODO: There is rudimentary search result validation as well, but it is    *        simply based on asserting the output observed in the old test case,    *        without really knowing if the output is correct. Someone needs to    *        validate this output and make any changes to the checkHits method.    */
DECL|method|testRun
specifier|public
name|void
name|testRun
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
specifier|final
name|int
name|MAX_DOCS
init|=
name|atLeast
argument_list|(
literal|225
argument_list|)
decl_stmt|;
name|doTest
argument_list|(
name|random
argument_list|,
name|pw
argument_list|,
literal|false
argument_list|,
name|MAX_DOCS
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
name|doTest
argument_list|(
name|random
argument_list|,
name|pw
argument_list|,
literal|true
argument_list|,
name|MAX_DOCS
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
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|Random
name|random
parameter_list|,
name|PrintWriter
name|out
parameter_list|,
name|boolean
name|useCompoundFiles
parameter_list|,
name|int
name|MAX_DOCS
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
specifier|final
name|MergePolicy
name|mp
init|=
name|conf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mp
operator|instanceof
name|LogMergePolicy
condition|)
block|{
operator|(
operator|(
name|LogMergePolicy
operator|)
name|mp
operator|)
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFiles
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now build index MAX_DOCS="
operator|+
name|MAX_DOCS
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|MAX_DOCS
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
name|newField
argument_list|(
name|PRIORITY_FIELD
argument_list|,
name|HIGH_PRIORITY
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|ID_FIELD
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
comment|// try a search without OR
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PRIORITY_FIELD
argument_list|,
name|HIGH_PRIORITY
argument_list|)
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
name|PRIORITY_FIELD
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: search query="
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_SCORE
argument_list|,
operator|new
name|SortField
argument_list|(
name|ID_FIELD
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|MAX_DOCS
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|printHits
argument_list|(
name|out
argument_list|,
name|hits
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|hits
argument_list|,
name|MAX_DOCS
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// try a new search with OR
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|hits
operator|=
literal|null
expr_stmt|;
name|BooleanQuery
name|booleanQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PRIORITY_FIELD
argument_list|,
name|HIGH_PRIORITY
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|PRIORITY_FIELD
argument_list|,
name|MED_PRIORITY
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|booleanQuery
operator|.
name|toString
argument_list|(
name|PRIORITY_FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|booleanQuery
argument_list|,
literal|null
argument_list|,
name|MAX_DOCS
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|printHits
argument_list|(
name|out
argument_list|,
name|hits
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|hits
argument_list|,
name|MAX_DOCS
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|printHits
specifier|private
name|void
name|printHits
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|ScoreDoc
index|[]
name|hits
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
operator|+
literal|" total results\n"
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
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
literal|10
operator|||
operator|(
name|i
operator|>
literal|94
operator|&&
name|i
operator|<
literal|105
operator|)
condition|)
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
name|d
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|ScoreDoc
index|[]
name|hits
parameter_list|,
name|int
name|expectedCount
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|"total results"
argument_list|,
name|expectedCount
argument_list|,
name|hits
operator|.
name|length
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
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
literal|10
operator|||
operator|(
name|i
operator|>
literal|94
operator|&&
name|i
operator|<
literal|105
operator|)
condition|)
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
name|assertEquals
argument_list|(
literal|"check "
operator|+
name|i
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|d
operator|.
name|get
argument_list|(
name|ID_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
