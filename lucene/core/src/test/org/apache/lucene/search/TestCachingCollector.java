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
name|util
operator|.
name|LuceneTestCase
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
DECL|class|TestCachingCollector
specifier|public
class|class
name|TestCachingCollector
extends|extends
name|LuceneTestCase
block|{
DECL|field|ONE_BYTE
specifier|private
specifier|static
specifier|final
name|double
name|ONE_BYTE
init|=
literal|1.0
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
decl_stmt|;
comment|// 1 byte out of MB
DECL|class|MockScorer
specifier|private
specifier|static
class|class
name|MockScorer
extends|extends
name|Scorer
block|{
DECL|method|MockScorer
specifier|private
name|MockScorer
parameter_list|()
block|{
name|super
argument_list|(
operator|(
name|Weight
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|class|NoOpCollector
specifier|private
specifier|static
class|class
name|NoOpCollector
extends|extends
name|Collector
block|{
DECL|field|acceptDocsOutOfOrder
specifier|private
specifier|final
name|boolean
name|acceptDocsOutOfOrder
decl_stmt|;
DECL|method|NoOpCollector
specifier|public
name|NoOpCollector
parameter_list|(
name|boolean
name|acceptDocsOutOfOrder
parameter_list|)
block|{
name|this
operator|.
name|acceptDocsOutOfOrder
operator|=
name|acceptDocsOutOfOrder
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|acceptDocsOutOfOrder
return|;
block|}
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|boolean
name|cacheScores
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
name|CachingCollector
name|cc
init|=
name|CachingCollector
operator|.
name|create
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|,
name|cacheScores
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|cc
operator|.
name|setScorer
argument_list|(
operator|new
name|MockScorer
argument_list|()
argument_list|)
expr_stmt|;
comment|// collect 1000 docs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|cc
operator|.
name|collect
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// now replay them
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|Collector
argument_list|()
block|{
name|int
name|prevDocID
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|prevDocID
operator|+
literal|1
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|prevDocID
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalStateOnReplay
specifier|public
name|void
name|testIllegalStateOnReplay
parameter_list|()
throws|throws
name|Exception
block|{
name|CachingCollector
name|cc
init|=
name|CachingCollector
operator|.
name|create
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|50
operator|*
name|ONE_BYTE
argument_list|)
decl_stmt|;
name|cc
operator|.
name|setScorer
argument_list|(
operator|new
name|MockScorer
argument_list|()
argument_list|)
expr_stmt|;
comment|// collect 130 docs, this should be enough for triggering cache abort.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|130
condition|;
name|i
operator|++
control|)
block|{
name|cc
operator|.
name|collect
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"CachingCollector should not be cached due to low memory limit"
argument_list|,
name|cc
operator|.
name|isCached
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"replay should fail if CachingCollector is not cached"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testIllegalCollectorOnReplay
specifier|public
name|void
name|testIllegalCollectorOnReplay
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that the Collector passed to replay() has an out-of-order mode that
comment|// is valid with the Collector passed to the ctor
comment|// 'src' Collector does not support out-of-order
name|CachingCollector
name|cc
init|=
name|CachingCollector
operator|.
name|create
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|50
operator|*
name|ONE_BYTE
argument_list|)
decl_stmt|;
name|cc
operator|.
name|setScorer
argument_list|(
operator|new
name|MockScorer
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
name|cc
operator|.
name|collect
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// this call should not fail
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// this call should not fail
comment|// 'src' Collector supports out-of-order
name|cc
operator|=
name|CachingCollector
operator|.
name|create
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|50
operator|*
name|ONE_BYTE
argument_list|)
expr_stmt|;
name|cc
operator|.
name|setScorer
argument_list|(
operator|new
name|MockScorer
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
name|cc
operator|.
name|collect
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// this call should not fail
try|try
block|{
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// this call should fail
name|fail
argument_list|(
literal|"should have failed if an in-order Collector was given to replay(), "
operator|+
literal|"while CachingCollector was initialized with out-of-order collection"
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
block|}
DECL|method|testCachedArraysAllocation
specifier|public
name|void
name|testCachedArraysAllocation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests the cached arrays allocation -- if the 'nextLength' was too high,
comment|// caching would terminate even if a smaller length would suffice.
comment|// set RAM limit enough for 150 docs + random(10000)
name|int
name|numDocs
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
operator|+
literal|150
decl_stmt|;
for|for
control|(
name|boolean
name|cacheScores
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
name|int
name|bytesPerDoc
init|=
name|cacheScores
condition|?
literal|8
else|:
literal|4
decl_stmt|;
name|CachingCollector
name|cc
init|=
name|CachingCollector
operator|.
name|create
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|false
argument_list|)
argument_list|,
name|cacheScores
argument_list|,
name|bytesPerDoc
operator|*
name|ONE_BYTE
operator|*
name|numDocs
argument_list|)
decl_stmt|;
name|cc
operator|.
name|setScorer
argument_list|(
operator|new
name|MockScorer
argument_list|()
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
name|numDocs
condition|;
name|i
operator|++
control|)
name|cc
operator|.
name|collect
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cc
operator|.
name|isCached
argument_list|()
argument_list|)
expr_stmt|;
comment|// The 151's document should terminate caching
name|cc
operator|.
name|collect
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cc
operator|.
name|isCached
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoWrappedCollector
specifier|public
name|void
name|testNoWrappedCollector
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|boolean
name|cacheScores
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
comment|// create w/ null wrapped collector, and test that the methods work
name|CachingCollector
name|cc
init|=
name|CachingCollector
operator|.
name|create
argument_list|(
literal|true
argument_list|,
name|cacheScores
argument_list|,
literal|50
operator|*
name|ONE_BYTE
argument_list|)
decl_stmt|;
name|cc
operator|.
name|setNextReader
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|cc
operator|.
name|setScorer
argument_list|(
operator|new
name|MockScorer
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cc
operator|.
name|isCached
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|replay
argument_list|(
operator|new
name|NoOpCollector
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
