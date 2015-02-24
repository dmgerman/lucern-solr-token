begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|analysis
operator|.
name|MockAnalyzer
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
name|Document
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
name|Field
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
name|DirectoryReader
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
name|RandomIndexWriter
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
name|Term
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
name|search
operator|.
name|similarities
operator|.
name|DefaultSimilarity
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
name|similarities
operator|.
name|Similarity
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
name|Directory
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
name|MockDirectoryWrapper
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
begin_comment
comment|/** Test BooleanQuery2 against BooleanQuery by overriding the standard query parser.  * This also tests the scoring order of BooleanQuery.  */
end_comment
begin_class
DECL|class|TestBoolean2
specifier|public
class|class
name|TestBoolean2
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|bigSearcher
specifier|private
specifier|static
name|IndexSearcher
name|bigSearcher
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|littleReader
specifier|private
specifier|static
name|IndexReader
name|littleReader
decl_stmt|;
DECL|field|NUM_EXTRA_DOCS
specifier|private
specifier|static
name|int
name|NUM_EXTRA_DOCS
init|=
literal|6000
decl_stmt|;
DECL|field|field
specifier|public
specifier|static
specifier|final
name|String
name|field
init|=
literal|"field"
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|dir2
specifier|private
specifier|static
name|Directory
name|dir2
decl_stmt|;
DECL|field|mulFactor
specifier|private
specifier|static
name|int
name|mulFactor
decl_stmt|;
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
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
name|docFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|field
argument_list|,
name|docFields
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|littleReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|littleReader
argument_list|)
expr_stmt|;
comment|// this is intentionally using the baseline sim, because it compares against bigSearcher (which uses a random one)
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make big index
name|dir2
operator|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
name|TestUtil
operator|.
name|ramCopyOf
argument_list|(
name|directory
argument_list|)
argument_list|)
expr_stmt|;
comment|// First multiply small test index:
name|mulFactor
operator|=
literal|1
expr_stmt|;
name|int
name|docCount
init|=
literal|0
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
literal|"\nTEST: now copy index..."
argument_list|)
expr_stmt|;
block|}
do|do
block|{
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
literal|"\nTEST: cycle..."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Directory
name|copy
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
name|TestUtil
operator|.
name|ramCopyOf
argument_list|(
name|dir2
argument_list|)
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir2
argument_list|)
decl_stmt|;
name|w
operator|.
name|addIndexes
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|docCount
operator|=
name|w
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|mulFactor
operator|*=
literal|2
expr_stmt|;
block|}
do|while
condition|(
name|docCount
operator|<
literal|3000
condition|)
do|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|,
literal|1000
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field2"
argument_list|,
literal|"xxx"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
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
name|NUM_EXTRA_DOCS
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field2"
argument_list|,
literal|"big bad bug"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
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
name|NUM_EXTRA_DOCS
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|w
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|bigSearcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|littleReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|littleReader
operator|=
literal|null
expr_stmt|;
name|dir2
operator|=
literal|null
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
name|bigSearcher
operator|=
literal|null
expr_stmt|;
block|}
DECL|field|docFields
specifier|private
specifier|static
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3"
block|}
decl_stmt|;
DECL|method|queriesTest
specifier|public
name|void
name|queriesTest
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The asserting searcher will sometimes return the bulk scorer and
comment|// sometimes return a default impl around the scorer so that we can
comment|// compare BS1 and BS2
name|TopScoreDocCollector
name|collector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits1
init|=
name|collector
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits2
init|=
name|collector
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|mulFactor
operator|*
name|collector
operator|.
name|totalHits
argument_list|,
name|bigSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkHitsQuery
argument_list|(
name|query
argument_list|,
name|hits1
argument_list|,
name|hits2
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries01
specifier|public
name|void
name|testQueries01
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries02
specifier|public
name|void
name|testQueries02
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
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
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries03
specifier|public
name|void
name|testQueries03
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
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
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
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
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries04
specifier|public
name|void
name|testQueries04
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
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
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries05
specifier|public
name|void
name|testQueries05
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries06
specifier|public
name|void
name|testQueries06
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w5"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|1
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries07
specifier|public
name|void
name|testQueries07
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w5"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries08
specifier|public
name|void
name|testQueries08
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
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
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w5"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|,
literal|1
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries09
specifier|public
name|void
name|testQueries09
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"zz"
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
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueries10
specifier|public
name|void
name|testQueries10
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"xx"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"zz"
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
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|Similarity
name|oldSimilarity
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
try|try
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|overlap
operator|/
operator|(
operator|(
name|float
operator|)
name|maxOverlap
operator|-
literal|1
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|queriesTest
argument_list|(
name|query
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|oldSimilarity
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRandomQueries
specifier|public
name|void
name|testRandomQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|vals
init|=
block|{
literal|"w1"
block|,
literal|"w2"
block|,
literal|"w3"
block|,
literal|"w4"
block|,
literal|"w5"
block|,
literal|"xx"
block|,
literal|"yy"
block|,
literal|"zzz"
block|}
decl_stmt|;
name|int
name|tot
init|=
literal|0
decl_stmt|;
name|BooleanQuery
name|q1
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// increase number of iterations for more complete testing
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|int
name|level
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|q1
operator|=
name|randBoolQuery
argument_list|(
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|level
argument_list|,
name|field
argument_list|,
name|vals
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Can't sort by relevance since floating point numbers may not quite
comment|// match up.
name|Sort
name|sort
init|=
name|Sort
operator|.
name|INDEXORDER
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|q1
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// baseline sim
try|try
block|{
comment|// a little hackish, QueryUtils.check is too costly to do on bigSearcher in this loop.
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|bigSearcher
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// random sim
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|q1
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// restore
block|}
name|TopFieldCollector
name|collector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits1
init|=
name|collector
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits2
init|=
name|collector
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|tot
operator|+=
name|hits2
operator|.
name|length
expr_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|q1
argument_list|,
name|hits1
argument_list|,
name|hits2
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q3
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q3
operator|.
name|add
argument_list|(
name|q1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"b"
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
name|TopDocs
name|hits4
init|=
name|bigSearcher
operator|.
name|search
argument_list|(
name|q3
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mulFactor
operator|*
name|collector
operator|.
name|totalHits
operator|+
name|NUM_EXTRA_DOCS
operator|/
literal|2
argument_list|,
name|hits4
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// For easier debugging
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"failed query: "
operator|+
name|q1
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// System.out.println("Total hits:"+tot);
block|}
comment|// used to set properties or change every BooleanQuery
comment|// generated from randBoolQuery.
DECL|interface|Callback
specifier|public
specifier|static
interface|interface
name|Callback
block|{
DECL|method|postCreate
specifier|public
name|void
name|postCreate
parameter_list|(
name|BooleanQuery
name|q
parameter_list|)
function_decl|;
block|}
comment|// Random rnd is passed in so that the exact same random query may be created
comment|// more than once.
DECL|method|randBoolQuery
specifier|public
specifier|static
name|BooleanQuery
name|randBoolQuery
parameter_list|(
name|Random
name|rnd
parameter_list|,
name|boolean
name|allowMust
parameter_list|,
name|int
name|level
parameter_list|,
name|String
name|field
parameter_list|,
name|String
index|[]
name|vals
parameter_list|,
name|Callback
name|cb
parameter_list|)
block|{
name|BooleanQuery
name|current
init|=
operator|new
name|BooleanQuery
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|()
operator|<
literal|0
argument_list|)
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
name|rnd
operator|.
name|nextInt
argument_list|(
name|vals
operator|.
name|length
argument_list|)
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|int
name|qType
init|=
literal|0
decl_stmt|;
comment|// term query
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|qType
operator|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
decl_stmt|;
if|if
condition|(
name|qType
operator|<
literal|3
condition|)
block|{
name|q
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|vals
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|vals
operator|.
name|length
argument_list|)
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qType
operator|<
literal|4
condition|)
block|{
name|Term
name|t1
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|vals
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|vals
operator|.
name|length
argument_list|)
index|]
argument_list|)
decl_stmt|;
name|Term
name|t2
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|vals
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|vals
operator|.
name|length
argument_list|)
index|]
argument_list|)
decl_stmt|;
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|pq
operator|.
name|setSlop
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// increase possibility of matching
name|q
operator|=
name|pq
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|qType
operator|<
literal|7
condition|)
block|{
name|q
operator|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"w*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|q
operator|=
name|randBoolQuery
argument_list|(
name|rnd
argument_list|,
name|allowMust
argument_list|,
name|level
operator|-
literal|1
argument_list|,
name|field
argument_list|,
name|vals
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
name|int
name|r
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|occur
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|2
condition|)
block|{
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|<
literal|5
condition|)
block|{
if|if
condition|(
name|allowMust
condition|)
block|{
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
expr_stmt|;
block|}
else|else
block|{
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
expr_stmt|;
block|}
block|}
else|else
block|{
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
expr_stmt|;
block|}
name|current
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cb
operator|!=
literal|null
condition|)
name|cb
operator|.
name|postCreate
argument_list|(
name|current
argument_list|)
expr_stmt|;
return|return
name|current
return|;
block|}
block|}
end_class
end_unit
