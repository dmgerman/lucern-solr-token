begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|Field
operator|.
name|Store
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
name|SortedDocValuesField
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
name|StringField
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
name|LeafReaderContext
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
name|MultiReader
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|IOUtils
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
name|NamedThreadFactory
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
name|Test
import|;
end_import
begin_class
DECL|class|TestIndexSearcher
specifier|public
class|class
name|TestIndexSearcher
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
literal|100
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"field2"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|2
operator|==
literal|0
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"field2"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|2
operator|==
literal|0
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
name|reader
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
comment|// should not throw exception
DECL|method|testHugeN
specifier|public
name|void
name|testHugeN
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorService
name|service
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|4
argument_list|,
literal|4
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|NamedThreadFactory
argument_list|(
literal|"TestIndexSearcher"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searchers
index|[]
init|=
operator|new
name|IndexSearcher
index|[]
block|{
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
block|,
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|,
name|service
argument_list|)
block|}
decl_stmt|;
name|Query
name|queries
index|[]
init|=
operator|new
name|Query
index|[]
block|{
operator|new
name|MatchAllDocsQuery
argument_list|()
block|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|Sort
name|sorts
index|[]
init|=
operator|new
name|Sort
index|[]
block|{
literal|null
block|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"field2"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|ScoreDoc
name|afters
index|[]
init|=
operator|new
name|ScoreDoc
index|[]
block|{
literal|null
block|,
operator|new
name|FieldDoc
argument_list|(
literal|0
argument_list|,
literal|0f
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|BytesRef
argument_list|(
literal|"boo!"
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|IndexSearcher
name|searcher
range|:
name|searchers
control|)
block|{
for|for
control|(
name|ScoreDoc
name|after
range|:
name|afters
control|)
block|{
for|for
control|(
name|Query
name|query
range|:
name|queries
control|)
block|{
for|for
control|(
name|Sort
name|sort
range|:
name|sorts
control|)
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
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
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|searchAfter
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|sort
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|TestUtil
operator|.
name|shutdownExecutorService
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSearchAfterPassedMaxDoc
specifier|public
name|void
name|testSearchAfterPassedMaxDoc
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-5128: ensure we get a meaningful message if searchAfter exceeds maxDoc
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
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|r
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
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
try|try
block|{
name|s
operator|.
name|searchAfter
argument_list|(
operator|new
name|ScoreDoc
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|0.54f
argument_list|)
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit IllegalArgumentException when searchAfter exceeds maxDoc"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCount
specifier|public
name|void
name|testCount
parameter_list|()
throws|throws
name|IOException
block|{
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
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"delete"
argument_list|,
literal|"yes"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|boolean
name|delete
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
if|if
condition|(
name|delete
condition|)
block|{
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"delete"
argument_list|,
literal|"yes"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Test multiple queries, some of them are optimized by IndexSearcher.count()
for|for
control|(
name|Query
name|query
range|:
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|MatchNoDocsQuery
argument_list|()
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|searcher
operator|.
name|count
argument_list|(
name|query
argument_list|)
argument_list|,
name|searcher
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
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|w
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
DECL|method|testGetQueryCache
specifier|public
name|void
name|testGetQueryCache
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
operator|new
name|MultiReader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IndexSearcher
operator|.
name|getDefaultQueryCache
argument_list|()
argument_list|,
name|searcher
operator|.
name|getQueryCache
argument_list|()
argument_list|)
expr_stmt|;
name|QueryCache
name|dummyCache
init|=
operator|new
name|QueryCache
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Weight
name|doCache
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|QueryCachingPolicy
name|policy
parameter_list|)
block|{
return|return
name|weight
return|;
block|}
block|}
decl_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
name|dummyCache
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dummyCache
argument_list|,
name|searcher
operator|.
name|getQueryCache
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
operator|.
name|setDefaultQueryCache
argument_list|(
name|dummyCache
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
operator|new
name|MultiReader
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dummyCache
argument_list|,
name|searcher
operator|.
name|getQueryCache
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|searcher
operator|.
name|getQueryCache
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
operator|.
name|setDefaultQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
operator|new
name|MultiReader
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|searcher
operator|.
name|getQueryCache
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetQueryCachingPolicy
specifier|public
name|void
name|testGetQueryCachingPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
operator|new
name|MultiReader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|IndexSearcher
operator|.
name|getDefaultQueryCachingPolicy
argument_list|()
argument_list|,
name|searcher
operator|.
name|getQueryCachingPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|QueryCachingPolicy
name|dummyPolicy
init|=
operator|new
name|QueryCachingPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldCache
parameter_list|(
name|Query
name|query
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onUse
parameter_list|(
name|Query
name|query
parameter_list|)
block|{}
block|}
decl_stmt|;
name|searcher
operator|.
name|setQueryCachingPolicy
argument_list|(
name|dummyPolicy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dummyPolicy
argument_list|,
name|searcher
operator|.
name|getQueryCachingPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcher
operator|.
name|setDefaultQueryCachingPolicy
argument_list|(
name|dummyPolicy
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
operator|new
name|MultiReader
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dummyPolicy
argument_list|,
name|searcher
operator|.
name|getQueryCachingPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
