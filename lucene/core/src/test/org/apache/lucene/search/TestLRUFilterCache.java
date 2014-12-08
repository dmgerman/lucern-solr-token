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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|atomic
operator|.
name|AtomicBoolean
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
name|atomic
operator|.
name|AtomicReference
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import
begin_class
DECL|class|TestLRUFilterCache
specifier|public
class|class
name|TestLRUFilterCache
extends|extends
name|LuceneTestCase
block|{
DECL|field|NEVER_CACHE
specifier|private
specifier|static
specifier|final
name|FilterCachingPolicy
name|NEVER_CACHE
init|=
operator|new
name|FilterCachingPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCache
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldCache
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|,
name|DocIdSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
DECL|method|testConcurrency
specifier|public
name|void
name|testConcurrency
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|LRUFilterCache
name|filterCache
init|=
operator|new
name|LRUFilterCache
argument_list|(
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|,
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
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
name|SearcherManager
name|mgr
init|=
operator|new
name|SearcherManager
argument_list|(
name|w
operator|.
name|w
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
operator|new
name|SearcherFactory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|indexing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|error
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|3
index|]
decl_stmt|;
name|threads
index|[
literal|0
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|StringField
name|f
init|=
operator|new
name|StringField
argument_list|(
literal|"color"
argument_list|,
literal|""
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|indexing
operator|.
name|get
argument_list|()
operator|&&
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|f
operator|.
name|setStringValue
argument_list|(
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"blue"
block|,
literal|"red"
block|,
literal|"yellow"
block|}
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|63
operator|)
operator|==
literal|0
condition|)
block|{
name|mgr
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|filterCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
specifier|final
name|String
name|color
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"blue"
block|,
literal|"red"
block|,
literal|"yellow"
block|}
argument_list|)
decl_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"color"
argument_list|,
name|color
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|error
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|t
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|indexing
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|indexing
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|mgr
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|value
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"blue"
block|,
literal|"red"
block|,
literal|"yellow"
block|,
literal|"green"
block|}
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|f
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"color"
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|cached
init|=
name|filterCache
operator|.
name|doCache
argument_list|(
name|f
argument_list|,
name|MAYBE_CACHE_POLICY
argument_list|)
decl_stmt|;
name|TotalHitCountCollector
name|collector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|cached
argument_list|)
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|TotalHitCountCollector
name|collector2
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|f
argument_list|)
argument_list|,
name|collector2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|collector
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|collector2
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|error
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|error
operator|.
name|get
argument_list|()
throw|;
block|}
name|filterCache
operator|.
name|assertConsistent
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|filterCache
operator|.
name|assertConsistent
argument_list|()
expr_stmt|;
block|}
DECL|method|testLRUEviction
specifier|public
name|void
name|testLRUEviction
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|StringField
name|f
init|=
operator|new
name|StringField
argument_list|(
literal|"color"
argument_list|,
literal|"blue"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
literal|"red"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
literal|"green"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|DirectoryReader
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
specifier|final
name|LRUFilterCache
name|filterCache
init|=
operator|new
name|LRUFilterCache
argument_list|(
literal|2
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|blue
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"color"
argument_list|,
literal|"blue"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|red
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"color"
argument_list|,
literal|"red"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|green
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"color"
argument_list|,
literal|"green"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
comment|// the filter is not cached on any segment: no changes
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|green
argument_list|,
name|NEVER_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|red
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|red
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|green
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|red
argument_list|,
name|green
argument_list|)
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|red
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|red
argument_list|,
name|green
argument_list|)
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|blue
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|red
argument_list|,
name|blue
argument_list|)
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|blue
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|red
argument_list|,
name|blue
argument_list|)
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|green
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|green
argument_list|,
name|blue
argument_list|)
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterCache
operator|.
name|doCache
argument_list|(
name|red
argument_list|,
name|NEVER_CACHE
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|green
argument_list|,
name|blue
argument_list|)
argument_list|)
argument_list|,
name|filterCache
operator|.
name|cachedFilters
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
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
block|}
end_class
end_unit