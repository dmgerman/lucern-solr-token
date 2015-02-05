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
begin_class
DECL|class|TestScoreCachingWrappingScorer
specifier|public
class|class
name|TestScoreCachingWrappingScorer
extends|extends
name|LuceneTestCase
block|{
DECL|class|SimpleScorer
specifier|private
specifier|static
specifier|final
class|class
name|SimpleScorer
extends|extends
name|Scorer
block|{
DECL|field|idx
specifier|private
name|int
name|idx
init|=
literal|0
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SimpleScorer
specifier|public
name|SimpleScorer
parameter_list|(
name|Weight
name|weight
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
DECL|method|score
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
block|{
comment|// advance idx on purpose, so that consecutive calls to score will get
comment|// different results. This is to emulate computation of a score. If
comment|// ScoreCachingWrappingScorer is used, this should not be called more than
comment|// once per document.
return|return
name|idx
operator|==
name|scores
operator|.
name|length
condition|?
name|Float
operator|.
name|NaN
else|:
name|scores
index|[
name|idx
operator|++
index|]
return|;
block|}
DECL|method|freq
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
DECL|method|docID
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|nextDoc
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
operator|++
name|doc
operator|<
name|scores
operator|.
name|length
condition|?
name|doc
else|:
name|NO_MORE_DOCS
return|;
block|}
DECL|method|advance
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
name|doc
operator|=
name|target
expr_stmt|;
return|return
name|doc
operator|<
name|scores
operator|.
name|length
condition|?
name|doc
else|:
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|scores
operator|.
name|length
return|;
block|}
block|}
DECL|class|ScoreCachingCollector
specifier|private
specifier|static
specifier|final
class|class
name|ScoreCachingCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|idx
specifier|private
name|int
name|idx
init|=
literal|0
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|mscores
name|float
index|[]
name|mscores
decl_stmt|;
DECL|method|ScoreCachingCollector
specifier|public
name|ScoreCachingCollector
parameter_list|(
name|int
name|numToCollect
parameter_list|)
block|{
name|mscores
operator|=
operator|new
name|float
index|[
name|numToCollect
index|]
expr_stmt|;
block|}
DECL|method|collect
annotation|@
name|Override
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
comment|// just a sanity check to avoid IOOB.
if|if
condition|(
name|idx
operator|==
name|mscores
operator|.
name|length
condition|)
block|{
return|return;
block|}
comment|// just call score() a couple of times and record the score.
name|mscores
index|[
name|idx
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|mscores
index|[
name|idx
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|mscores
index|[
name|idx
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
operator|++
name|idx
expr_stmt|;
block|}
DECL|method|setScorer
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
operator|new
name|ScoreCachingWrappingScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|field|scores
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[]
block|{
literal|0.7767749f
block|,
literal|1.7839992f
block|,
literal|8.9925785f
block|,
literal|7.9608946f
block|,
literal|0.07948637f
block|,
literal|2.6356435f
block|,
literal|7.4950366f
block|,
literal|7.1490803f
block|,
literal|8.108544f
block|,
literal|4.961808f
block|,
literal|2.2423935f
block|,
literal|7.285586f
block|,
literal|4.6699767f
block|}
decl_stmt|;
DECL|method|testGetScores
specifier|public
name|void
name|testGetScores
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|Weight
name|fake
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"fake"
argument_list|,
literal|"weight"
argument_list|)
argument_list|)
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|Scorer
name|s
init|=
operator|new
name|SimpleScorer
argument_list|(
name|fake
argument_list|)
decl_stmt|;
name|ScoreCachingCollector
name|scc
init|=
operator|new
name|ScoreCachingCollector
argument_list|(
name|scores
operator|.
name|length
argument_list|)
decl_stmt|;
name|scc
operator|.
name|setScorer
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|// We need to iterate on the scorer so that its doc() advances.
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|s
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|scc
operator|.
name|collect
argument_list|(
name|doc
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
name|scores
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|scores
index|[
name|i
index|]
argument_list|,
name|scc
operator|.
name|mscores
index|[
name|i
index|]
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
name|ir
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
block|}
end_class
end_unit
