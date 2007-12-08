begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.byTask
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
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
name|FileReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|benchmark
operator|.
name|byTask
operator|.
name|Benchmark
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|DocData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|NoMoreDataException
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|ReutersDocMaker
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
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
operator|.
name|CountingSearchTestTask
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
name|benchmark
operator|.
name|byTask
operator|.
name|stats
operator|.
name|TaskStats
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
name|index
operator|.
name|IndexWriter
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
name|TermEnum
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
name|TermDocs
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * Test very simply that perf tasks - simple algorithms - are doing what they should.  */
end_comment
begin_class
DECL|class|TestPerfTasksLogic
specifier|public
class|class
name|TestPerfTasksLogic
extends|extends
name|TestCase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
DECL|field|NEW_LINE
specifier|static
specifier|final
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|// properties in effect in all tests here
DECL|field|propLines
specifier|static
specifier|final
name|String
name|propLines
index|[]
init|=
block|{
literal|"directory=RAMDirectory"
block|,
literal|"print.props=false"
block|,   }
decl_stmt|;
comment|/**    * @param name test name    */
DECL|method|TestPerfTasksLogic
specifier|public
name|TestPerfTasksLogic
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test index creation logic    */
DECL|method|testIndexAndSearchTasks
specifier|public
name|void
name|testIndexAndSearchTasks
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 1. alg definition (required in every "logic" test)
name|String
name|algLines
index|[]
init|=
block|{
literal|"ResetSystemErase"
block|,
literal|"CreateIndex"
block|,
literal|"{ AddDoc } : 1000"
block|,
literal|"Optimize"
block|,
literal|"CloseIndex"
block|,
literal|"OpenReader"
block|,
literal|"{ CountingSearchTest } : 200"
block|,
literal|"CloseReader"
block|,
literal|"[ CountingSearchTest> : 70"
block|,
literal|"[ CountingSearchTest> : 9"
block|,     }
decl_stmt|;
comment|// 2. we test this value later
name|CountingSearchTestTask
operator|.
name|numSearches
operator|=
literal|0
expr_stmt|;
comment|// 3. execute the algorithm  (required in every "logic" test)
name|Benchmark
name|benchmark
init|=
name|execBenchmark
argument_list|(
name|algLines
argument_list|)
decl_stmt|;
comment|// 4. test specific checks after the benchmark run completed.
name|assertEquals
argument_list|(
literal|"TestSearchTask was supposed to be called!"
argument_list|,
literal|279
argument_list|,
name|CountingSearchTestTask
operator|.
name|numSearches
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Index does not exist?...!"
argument_list|,
name|IndexReader
operator|.
name|indexExists
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// now we should be able to open the index for write.
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1000 docs were added to the index, this is what we expect to find!"
argument_list|,
literal|1000
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test Exhasting Doc Maker logic    */
DECL|method|testExhaustDocMaker
specifier|public
name|void
name|testExhaustDocMaker
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 1. alg definition (required in every "logic" test)
name|String
name|algLines
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"doc.maker=org.apache.lucene.benchmark.byTask.feeds.SimpleDocMaker"
block|,
literal|"doc.add.log.step=1"
block|,
literal|"doc.term.vector=false"
block|,
literal|"doc.maker.forever=false"
block|,
literal|"directory=RAMDirectory"
block|,
literal|"doc.stored=false"
block|,
literal|"doc.tokenized=false"
block|,
literal|"# ----- alg "
block|,
literal|"CreateIndex"
block|,
literal|"{ AddDoc } : * "
block|,
literal|"Optimize"
block|,
literal|"CloseIndex"
block|,
literal|"OpenReader"
block|,
literal|"{ CountingSearchTest } : 100"
block|,
literal|"CloseReader"
block|,
literal|"[ CountingSearchTest> : 30"
block|,
literal|"[ CountingSearchTest> : 9"
block|,     }
decl_stmt|;
comment|// 2. we test this value later
name|CountingSearchTestTask
operator|.
name|numSearches
operator|=
literal|0
expr_stmt|;
comment|// 3. execute the algorithm  (required in every "logic" test)
name|Benchmark
name|benchmark
init|=
name|execBenchmark
argument_list|(
name|algLines
argument_list|)
decl_stmt|;
comment|// 4. test specific checks after the benchmark run completed.
name|assertEquals
argument_list|(
literal|"TestSearchTask was supposed to be called!"
argument_list|,
literal|139
argument_list|,
name|CountingSearchTestTask
operator|.
name|numSearches
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Index does not exist?...!"
argument_list|,
name|IndexReader
operator|.
name|indexExists
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// now we should be able to open the index for write.
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1 docs were added to the index, this is what we expect to find!"
argument_list|,
literal|1
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test Parallel Doc Maker logic (for LUCENE-940)    */
DECL|method|testParallelDocMaker
specifier|public
name|void
name|testParallelDocMaker
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 1. alg definition (required in every "logic" test)
name|String
name|algLines
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"doc.maker="
operator|+
name|Reuters20DocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"doc.add.log.step=3"
block|,
literal|"doc.term.vector=false"
block|,
literal|"doc.maker.forever=false"
block|,
literal|"directory=FSDirectory"
block|,
literal|"doc.stored=false"
block|,
literal|"doc.tokenized=false"
block|,
literal|"# ----- alg "
block|,
literal|"CreateIndex"
block|,
literal|"[ { AddDoc } : * ] : 4 "
block|,
literal|"CloseIndex"
block|,     }
decl_stmt|;
comment|// 2. execute the algorithm  (required in every "logic" test)
name|Benchmark
name|benchmark
init|=
name|execBenchmark
argument_list|(
name|algLines
argument_list|)
decl_stmt|;
comment|// 3. test number of docs in the index
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|ndocsExpected
init|=
literal|20
decl_stmt|;
comment|// Reuters20DocMaker exhausts after 20 docs.
name|assertEquals
argument_list|(
literal|"wrong number of docs in the index!"
argument_list|,
name|ndocsExpected
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test WriteLineDoc and LineDocMaker.    */
DECL|method|testLineDocFile
specifier|public
name|void
name|testLineDocFile
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|lineFile
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"test.reuters.lines.txt"
argument_list|)
decl_stmt|;
comment|// We will call WriteLineDocs this many times
specifier|final
name|int
name|NUM_TRY_DOCS
init|=
literal|500
decl_stmt|;
comment|// Creates a line file with first 500 docs from reuters
name|String
name|algLines1
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"doc.maker=org.apache.lucene.benchmark.byTask.feeds.ReutersDocMaker"
block|,
literal|"doc.maker.forever=false"
block|,
literal|"line.file.out="
operator|+
name|lineFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
block|,
literal|"# ----- alg "
block|,
literal|"{WriteLineDoc()}:"
operator|+
name|NUM_TRY_DOCS
block|,     }
decl_stmt|;
comment|// Run algo
name|Benchmark
name|benchmark
init|=
name|execBenchmark
argument_list|(
name|algLines1
argument_list|)
decl_stmt|;
comment|// Verify we got somewhere between 1-500 lines (some
comment|// Reuters docs have no body, which WriteLineDoc task
comment|// skips).
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|lineFile
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numLines
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|readLine
argument_list|()
operator|!=
literal|null
condition|)
name|numLines
operator|++
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"did not see the right number of docs; should be> 0 and<= "
operator|+
name|NUM_TRY_DOCS
operator|+
literal|" but was "
operator|+
name|numLines
argument_list|,
name|numLines
operator|>
literal|0
operator|&&
name|numLines
operator|<=
name|NUM_TRY_DOCS
argument_list|)
expr_stmt|;
comment|// Index the line docs
name|String
name|algLines2
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"analyzer=org.apache.lucene.analysis.SimpleAnalyzer"
block|,
literal|"doc.maker=org.apache.lucene.benchmark.byTask.feeds.LineDocMaker"
block|,
literal|"docs.file="
operator|+
name|lineFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
block|,
literal|"doc.maker.forever=false"
block|,
literal|"autocommit=false"
block|,
literal|"ram.flush.mb=4"
block|,
literal|"# ----- alg "
block|,
literal|"ResetSystemErase"
block|,
literal|"CreateIndex"
block|,
literal|"{AddDoc}: *"
block|,
literal|"CloseIndex"
block|,     }
decl_stmt|;
comment|// Run algo
name|benchmark
operator|=
name|execBenchmark
argument_list|(
name|algLines2
argument_list|)
expr_stmt|;
comment|// now we should be able to open the index for write.
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numLines
operator|+
literal|" lines were were created but "
operator|+
name|ir
operator|.
name|numDocs
argument_list|()
operator|+
literal|" docs are in the index"
argument_list|,
name|numLines
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|lineFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test ReadTokensTask    */
DECL|method|testReadTokens
specifier|public
name|void
name|testReadTokens
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We will call ReadTokens on this many docs
specifier|final
name|int
name|NUM_DOCS
init|=
literal|100
decl_stmt|;
comment|// Read tokens from first NUM_DOCS docs from Reuters and
comment|// then build index from the same docs
name|String
name|algLines1
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"analyzer=org.apache.lucene.analysis.WhitespaceAnalyzer"
block|,
literal|"doc.maker=org.apache.lucene.benchmark.byTask.feeds.ReutersDocMaker"
block|,
literal|"# ----- alg "
block|,
literal|"{ReadTokens}: "
operator|+
name|NUM_DOCS
block|,
literal|"ResetSystemErase"
block|,
literal|"CreateIndex"
block|,
literal|"{AddDoc}: "
operator|+
name|NUM_DOCS
block|,
literal|"CloseIndex"
block|,     }
decl_stmt|;
comment|// Run algo
name|Benchmark
name|benchmark
init|=
name|execBenchmark
argument_list|(
name|algLines1
argument_list|)
decl_stmt|;
name|List
name|stats
init|=
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
operator|.
name|taskStats
argument_list|()
decl_stmt|;
comment|// Count how many tokens all ReadTokens saw
name|int
name|totalTokenCount1
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|stats
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TaskStats
name|stat
init|=
operator|(
name|TaskStats
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|getTask
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ReadTokens"
argument_list|)
condition|)
block|{
name|totalTokenCount1
operator|+=
name|stat
operator|.
name|getCount
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Separately count how many tokens are actually in the index:
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|TermEnum
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|int
name|totalTokenCount2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
name|termDocs
operator|.
name|seek
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
name|totalTokenCount2
operator|+=
name|termDocs
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Make sure they are the same
name|assertEquals
argument_list|(
name|totalTokenCount1
argument_list|,
name|totalTokenCount2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that " {[AddDoc(4000)]: 4} : * " works corrcetly (for LUCENE-941)    */
DECL|method|testParallelExhausted
specifier|public
name|void
name|testParallelExhausted
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 1. alg definition (required in every "logic" test)
name|String
name|algLines
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"doc.maker="
operator|+
name|Reuters20DocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"doc.add.log.step=3"
block|,
literal|"doc.term.vector=false"
block|,
literal|"doc.maker.forever=false"
block|,
literal|"directory=RAMDirectory"
block|,
literal|"doc.stored=false"
block|,
literal|"doc.tokenized=false"
block|,
literal|"debug.level=1"
block|,
literal|"# ----- alg "
block|,
literal|"CreateIndex"
block|,
literal|"{ [ AddDoc]: 4} : * "
block|,
literal|"ResetInputs "
block|,
literal|"{ [ AddDoc]: 4} : * "
block|,
literal|"CloseIndex"
block|,     }
decl_stmt|;
comment|// 2. execute the algorithm  (required in every "logic" test)
name|Benchmark
name|benchmark
init|=
name|execBenchmark
argument_list|(
name|algLines
argument_list|)
decl_stmt|;
comment|// 3. test number of docs in the index
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|benchmark
operator|.
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|ndocsExpected
init|=
literal|2
operator|*
literal|20
decl_stmt|;
comment|// Reuters20DocMaker exhausts after 20 docs.
name|assertEquals
argument_list|(
literal|"wrong number of docs in the index!"
argument_list|,
name|ndocsExpected
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// create the benchmark and execute it.
DECL|method|execBenchmark
specifier|public
specifier|static
name|Benchmark
name|execBenchmark
parameter_list|(
name|String
index|[]
name|algLines
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|algText
init|=
name|algLinesToText
argument_list|(
name|algLines
argument_list|)
decl_stmt|;
name|logTstLogic
argument_list|(
name|algText
argument_list|)
expr_stmt|;
name|Benchmark
name|benchmark
init|=
operator|new
name|Benchmark
argument_list|(
operator|new
name|StringReader
argument_list|(
name|algText
argument_list|)
argument_list|)
decl_stmt|;
name|benchmark
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|benchmark
return|;
block|}
comment|// catenate alg lines to make the alg text
DECL|method|algLinesToText
specifier|private
specifier|static
name|String
name|algLinesToText
parameter_list|(
name|String
index|[]
name|algLines
parameter_list|)
block|{
name|String
name|indent
init|=
literal|"  "
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
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
name|propLines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|indent
argument_list|)
operator|.
name|append
argument_list|(
name|propLines
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|algLines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|indent
argument_list|)
operator|.
name|append
argument_list|(
name|algLines
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|logTstLogic
specifier|private
specifier|static
name|void
name|logTstLogic
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
if|if
condition|(
operator|!
name|DEBUG
condition|)
return|return;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test logic of:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|txt
argument_list|)
expr_stmt|;
block|}
comment|/** use reuters and the exhaust mechanism, but to be faster, add 20 docs only... */
DECL|class|Reuters20DocMaker
specifier|public
specifier|static
class|class
name|Reuters20DocMaker
extends|extends
name|ReutersDocMaker
block|{
DECL|field|nDocs
specifier|private
name|int
name|nDocs
init|=
literal|0
decl_stmt|;
DECL|method|getNextDocData
specifier|protected
specifier|synchronized
name|DocData
name|getNextDocData
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|nDocs
operator|>=
literal|20
operator|&&
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
name|nDocs
operator|++
expr_stmt|;
return|return
name|super
operator|.
name|getNextDocData
argument_list|()
return|;
block|}
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|nDocs
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
