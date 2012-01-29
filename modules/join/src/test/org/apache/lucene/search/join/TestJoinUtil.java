begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|analysis
operator|.
name|MockTokenizer
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
name|document
operator|.
name|TextField
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
name|index
operator|.
name|AtomicIndexReader
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
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
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
name|_TestUtil
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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestJoinUtil
specifier|public
class|class
name|TestJoinUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|idField
init|=
literal|"id"
decl_stmt|;
specifier|final
name|String
name|toField
init|=
literal|"productId"
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
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
comment|// 0
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
operator|new
name|Field
argument_list|(
literal|"description"
argument_list|,
literal|"random text"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"name1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idField
argument_list|,
literal|"1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 1
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
operator|new
name|Field
argument_list|(
literal|"price"
argument_list|,
literal|"10.0"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idField
argument_list|,
literal|"2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|toField
argument_list|,
literal|"1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 2
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
operator|new
name|Field
argument_list|(
literal|"price"
argument_list|,
literal|"20.0"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idField
argument_list|,
literal|"3"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|toField
argument_list|,
literal|"1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 3
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
operator|new
name|Field
argument_list|(
literal|"description"
argument_list|,
literal|"more random text"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"name2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idField
argument_list|,
literal|"4"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// 4
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
operator|new
name|Field
argument_list|(
literal|"price"
argument_list|,
literal|"10.0"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idField
argument_list|,
literal|"5"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|toField
argument_list|,
literal|"4"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 5
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
operator|new
name|Field
argument_list|(
literal|"price"
argument_list|,
literal|"20.0"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idField
argument_list|,
literal|"6"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|toField
argument_list|,
literal|"4"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Search for product
name|Query
name|joinQuery
init|=
name|JoinUtil
operator|.
name|createJoinQuery
argument_list|(
name|idField
argument_list|,
literal|false
argument_list|,
name|toField
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"name2"
argument_list|)
argument_list|)
argument_list|,
name|indexSearcher
argument_list|)
decl_stmt|;
name|TopDocs
name|result
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|joinQuery
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|result
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|result
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|joinQuery
operator|=
name|JoinUtil
operator|.
name|createJoinQuery
argument_list|(
name|idField
argument_list|,
literal|false
argument_list|,
name|toField
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"name1"
argument_list|)
argument_list|)
argument_list|,
name|indexSearcher
argument_list|)
expr_stmt|;
name|result
operator|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|joinQuery
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
comment|// Search for offer
name|joinQuery
operator|=
name|JoinUtil
operator|.
name|createJoinQuery
argument_list|(
name|toField
argument_list|,
literal|false
argument_list|,
name|idField
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
argument_list|,
name|indexSearcher
argument_list|)
expr_stmt|;
name|result
operator|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|joinQuery
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleValueRandomJoin
specifier|public
name|void
name|testSingleValueRandomJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxIndexIter
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|6
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|int
name|maxSearchIter
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|13
argument_list|,
literal|26
argument_list|)
decl_stmt|;
name|executeRandomJoin
argument_list|(
literal|false
argument_list|,
name|maxIndexIter
argument_list|,
name|maxSearchIter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// This test really takes more time, that is why the number of iterations are smaller.
DECL|method|testMultiValueRandomJoin
specifier|public
name|void
name|testMultiValueRandomJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|maxIndexIter
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|3
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|int
name|maxSearchIter
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|6
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|executeRandomJoin
argument_list|(
literal|true
argument_list|,
name|maxIndexIter
argument_list|,
name|maxSearchIter
argument_list|)
expr_stmt|;
block|}
DECL|method|executeRandomJoin
specifier|private
name|void
name|executeRandomJoin
parameter_list|(
name|boolean
name|multipleValuesPerDocument
parameter_list|,
name|int
name|maxIndexIter
parameter_list|,
name|int
name|maxSearchIter
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|indexIter
init|=
literal|1
init|;
name|indexIter
operator|<=
name|maxIndexIter
condition|;
name|indexIter
operator|++
control|)
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
literal|"indexIter="
operator|+
name|indexIter
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
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
name|int
name|numberOfDocumentsToIndex
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|87
argument_list|,
literal|764
argument_list|)
decl_stmt|;
name|IndexIterationContext
name|context
init|=
name|createContext
argument_list|(
name|numberOfDocumentsToIndex
argument_list|,
name|w
argument_list|,
name|multipleValuesPerDocument
argument_list|)
decl_stmt|;
name|IndexReader
name|topLevelReader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|searchIter
init|=
literal|1
init|;
name|searchIter
operator|<=
name|maxSearchIter
condition|;
name|searchIter
operator|++
control|)
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
literal|"searchIter="
operator|+
name|searchIter
argument_list|)
expr_stmt|;
block|}
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|topLevelReader
argument_list|)
decl_stmt|;
name|int
name|r
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|context
operator|.
name|randomUniqueValues
operator|.
name|length
argument_list|)
decl_stmt|;
name|boolean
name|from
init|=
name|context
operator|.
name|randomFrom
index|[
name|r
index|]
decl_stmt|;
name|String
name|randomValue
init|=
name|context
operator|.
name|randomUniqueValues
index|[
name|r
index|]
decl_stmt|;
name|FixedBitSet
name|expectedResult
init|=
name|createExpectedResult
argument_list|(
name|randomValue
argument_list|,
name|from
argument_list|,
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Query
name|actualQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"value"
argument_list|,
name|randomValue
argument_list|)
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
literal|"actualQuery="
operator|+
name|actualQuery
argument_list|)
expr_stmt|;
block|}
name|Query
name|joinQuery
decl_stmt|;
if|if
condition|(
name|from
condition|)
block|{
name|joinQuery
operator|=
name|JoinUtil
operator|.
name|createJoinQuery
argument_list|(
literal|"from"
argument_list|,
name|multipleValuesPerDocument
argument_list|,
literal|"to"
argument_list|,
name|actualQuery
argument_list|,
name|indexSearcher
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|joinQuery
operator|=
name|JoinUtil
operator|.
name|createJoinQuery
argument_list|(
literal|"to"
argument_list|,
name|multipleValuesPerDocument
argument_list|,
literal|"from"
argument_list|,
name|actualQuery
argument_list|,
name|indexSearcher
argument_list|)
expr_stmt|;
block|}
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
literal|"joinQuery="
operator|+
name|joinQuery
argument_list|)
expr_stmt|;
block|}
comment|// Need to know all documents that have matches. TopDocs doesn't give me that and then I'd be also testing TopDocsCollector...
specifier|final
name|FixedBitSet
name|actualResult
init|=
operator|new
name|FixedBitSet
argument_list|(
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|joinQuery
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
name|int
name|docBase
decl_stmt|;
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|actualResult
operator|.
name|set
argument_list|(
name|doc
operator|+
name|docBase
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{           }
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
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
literal|"expected cardinality:"
operator|+
name|expectedResult
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iterator
init|=
name|expectedResult
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Expected doc[%d] with id value %s"
argument_list|,
name|doc
argument_list|,
name|indexSearcher
operator|.
name|doc
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"actual cardinality:"
operator|+
name|actualResult
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|actualResult
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Actual doc[%d] with id value %s"
argument_list|,
name|doc
argument_list|,
name|indexSearcher
operator|.
name|doc
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|actualResult
argument_list|)
expr_stmt|;
block|}
name|topLevelReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createContext
specifier|private
name|IndexIterationContext
name|createContext
parameter_list|(
name|int
name|nDocs
parameter_list|,
name|RandomIndexWriter
name|writer
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createContext
argument_list|(
name|nDocs
argument_list|,
name|writer
argument_list|,
name|writer
argument_list|,
name|multipleValuesPerDocument
argument_list|)
return|;
block|}
DECL|method|createContext
specifier|private
name|IndexIterationContext
name|createContext
parameter_list|(
name|int
name|nDocs
parameter_list|,
name|RandomIndexWriter
name|fromWriter
parameter_list|,
name|RandomIndexWriter
name|toWriter
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexIterationContext
name|context
init|=
operator|new
name|IndexIterationContext
argument_list|()
decl_stmt|;
name|int
name|numRandomValues
init|=
name|nDocs
operator|/
literal|2
decl_stmt|;
name|context
operator|.
name|randomUniqueValues
operator|=
operator|new
name|String
index|[
name|numRandomValues
index|]
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|trackSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|context
operator|.
name|randomFrom
operator|=
operator|new
name|boolean
index|[
name|numRandomValues
index|]
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
name|numRandomValues
condition|;
name|i
operator|++
control|)
block|{
name|String
name|uniqueRandomValue
decl_stmt|;
do|do
block|{
name|uniqueRandomValue
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
expr_stmt|;
comment|//        uniqueRandomValue = _TestUtil.randomSimpleString(random);
block|}
do|while
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|uniqueRandomValue
argument_list|)
operator|||
name|trackSet
operator|.
name|contains
argument_list|(
name|uniqueRandomValue
argument_list|)
condition|)
do|;
comment|// Generate unique values and empty strings aren't allowed.
name|trackSet
operator|.
name|add
argument_list|(
name|uniqueRandomValue
argument_list|)
expr_stmt|;
name|context
operator|.
name|randomFrom
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|context
operator|.
name|randomUniqueValues
index|[
name|i
index|]
operator|=
name|uniqueRandomValue
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|randomI
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|context
operator|.
name|randomUniqueValues
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|context
operator|.
name|randomUniqueValues
index|[
name|randomI
index|]
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|random
argument_list|,
literal|"id"
argument_list|,
name|id
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|random
argument_list|,
literal|"value"
argument_list|,
name|value
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|from
init|=
name|context
operator|.
name|randomFrom
index|[
name|randomI
index|]
decl_stmt|;
name|int
name|numberOfLinkValues
init|=
name|multipleValuesPerDocument
condition|?
literal|2
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
else|:
literal|1
decl_stmt|;
name|RandomDoc
name|doc
init|=
operator|new
name|RandomDoc
argument_list|(
name|id
argument_list|,
name|numberOfLinkValues
argument_list|,
name|value
argument_list|)
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
name|numberOfLinkValues
condition|;
name|j
operator|++
control|)
block|{
name|String
name|linkValue
init|=
name|context
operator|.
name|randomUniqueValues
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|context
operator|.
name|randomUniqueValues
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|doc
operator|.
name|linkValues
operator|.
name|add
argument_list|(
name|linkValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|from
condition|)
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|fromDocuments
operator|.
name|containsKey
argument_list|(
name|linkValue
argument_list|)
condition|)
block|{
name|context
operator|.
name|fromDocuments
operator|.
name|put
argument_list|(
name|linkValue
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RandomDoc
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|context
operator|.
name|randomValueFromDocs
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|context
operator|.
name|randomValueFromDocs
operator|.
name|put
argument_list|(
name|value
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RandomDoc
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|fromDocuments
operator|.
name|get
argument_list|(
name|linkValue
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|context
operator|.
name|randomValueFromDocs
operator|.
name|get
argument_list|(
name|value
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|random
argument_list|,
literal|"from"
argument_list|,
name|linkValue
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|toDocuments
operator|.
name|containsKey
argument_list|(
name|linkValue
argument_list|)
condition|)
block|{
name|context
operator|.
name|toDocuments
operator|.
name|put
argument_list|(
name|linkValue
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RandomDoc
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|context
operator|.
name|randomValueToDocs
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|context
operator|.
name|randomValueToDocs
operator|.
name|put
argument_list|(
name|value
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RandomDoc
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|toDocuments
operator|.
name|get
argument_list|(
name|linkValue
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|context
operator|.
name|randomValueToDocs
operator|.
name|get
argument_list|(
name|value
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|random
argument_list|,
literal|"to"
argument_list|,
name|linkValue
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RandomIndexWriter
name|w
decl_stmt|;
if|if
condition|(
name|from
condition|)
block|{
name|w
operator|=
name|fromWriter
expr_stmt|;
block|}
else|else
block|{
name|w
operator|=
name|toWriter
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|4
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
literal|"Added document["
operator|+
name|i
operator|+
literal|"]: "
operator|+
name|document
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|context
return|;
block|}
DECL|method|createExpectedResult
specifier|private
name|FixedBitSet
name|createExpectedResult
parameter_list|(
name|String
name|queryValue
parameter_list|,
name|boolean
name|from
parameter_list|,
name|IndexReader
name|topLevelReader
parameter_list|,
name|IndexIterationContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
name|randomValueDocs
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
name|linkValueDocuments
decl_stmt|;
if|if
condition|(
name|from
condition|)
block|{
name|randomValueDocs
operator|=
name|context
operator|.
name|randomValueFromDocs
expr_stmt|;
name|linkValueDocuments
operator|=
name|context
operator|.
name|toDocuments
expr_stmt|;
block|}
else|else
block|{
name|randomValueDocs
operator|=
name|context
operator|.
name|randomValueToDocs
expr_stmt|;
name|linkValueDocuments
operator|=
name|context
operator|.
name|fromDocuments
expr_stmt|;
block|}
name|FixedBitSet
name|expectedResult
init|=
operator|new
name|FixedBitSet
argument_list|(
name|topLevelReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RandomDoc
argument_list|>
name|matchingDocs
init|=
name|randomValueDocs
operator|.
name|get
argument_list|(
name|queryValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingDocs
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|FixedBitSet
argument_list|(
name|topLevelReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
for|for
control|(
name|RandomDoc
name|matchingDoc
range|:
name|matchingDocs
control|)
block|{
for|for
control|(
name|String
name|linkValue
range|:
name|matchingDoc
operator|.
name|linkValues
control|)
block|{
name|List
argument_list|<
name|RandomDoc
argument_list|>
name|otherMatchingDocs
init|=
name|linkValueDocuments
operator|.
name|get
argument_list|(
name|linkValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherMatchingDocs
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|RandomDoc
name|otherSideDoc
range|:
name|otherMatchingDocs
control|)
block|{
name|DocsEnum
name|docsEnum
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|topLevelReader
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|topLevelReader
argument_list|)
argument_list|,
literal|"id"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|otherSideDoc
operator|.
name|id
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
assert|assert
name|docsEnum
operator|!=
literal|null
assert|;
name|int
name|doc
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|expectedResult
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|expectedResult
return|;
block|}
DECL|class|IndexIterationContext
specifier|private
specifier|static
class|class
name|IndexIterationContext
block|{
DECL|field|randomUniqueValues
name|String
index|[]
name|randomUniqueValues
decl_stmt|;
DECL|field|randomFrom
name|boolean
index|[]
name|randomFrom
decl_stmt|;
DECL|field|fromDocuments
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
name|fromDocuments
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|toDocuments
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
name|toDocuments
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|randomValueFromDocs
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
name|randomValueFromDocs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|randomValueToDocs
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
name|randomValueToDocs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RandomDoc
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
block|}
DECL|class|RandomDoc
specifier|private
specifier|static
class|class
name|RandomDoc
block|{
DECL|field|id
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|linkValues
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|linkValues
decl_stmt|;
DECL|field|value
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|RandomDoc
specifier|private
name|RandomDoc
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|numberOfLinkValues
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|linkValues
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|numberOfLinkValues
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
