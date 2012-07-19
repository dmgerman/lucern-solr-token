begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Collector
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Scorer
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
name|ArrayUtil
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
name|OpenBitSet
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link Collector} which stores all docIDs and their scores in a  * {@link ScoredDocIDs} instance. If scoring is not enabled, then the default  * score as set in {@link #setDefaultScore(float)} (or  * {@link ScoredDocIDsIterator#DEFAULT_SCORE}) will be set for all documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|ScoredDocIdCollector
specifier|public
specifier|abstract
class|class
name|ScoredDocIdCollector
extends|extends
name|Collector
block|{
DECL|class|NonScoringDocIdCollector
specifier|private
specifier|static
specifier|final
class|class
name|NonScoringDocIdCollector
extends|extends
name|ScoredDocIdCollector
block|{
DECL|field|defaultScore
name|float
name|defaultScore
init|=
name|ScoredDocIDsIterator
operator|.
name|DEFAULT_SCORE
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"synthetic-access"
argument_list|)
DECL|method|NonScoringDocIdCollector
specifier|public
name|NonScoringDocIdCollector
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|super
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
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
block|{
name|docIds
operator|.
name|fastSet
argument_list|(
name|docBase
operator|+
name|doc
argument_list|)
expr_stmt|;
operator|++
name|numDocIds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDefaultScore
specifier|public
name|float
name|getDefaultScore
parameter_list|()
block|{
return|return
name|defaultScore
return|;
block|}
annotation|@
name|Override
DECL|method|scoredDocIdsIterator
specifier|public
name|ScoredDocIDsIterator
name|scoredDocIdsIterator
parameter_list|()
block|{
return|return
operator|new
name|ScoredDocIDsIterator
argument_list|()
block|{
specifier|private
name|DocIdSetIterator
name|docIdsIter
init|=
name|docIds
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|int
name|nextDoc
decl_stmt|;
specifier|public
name|int
name|getDocID
parameter_list|()
block|{
return|return
name|nextDoc
return|;
block|}
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|defaultScore
return|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
block|{
try|try
block|{
name|nextDoc
operator|=
name|docIdsIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
return|return
name|nextDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// This should not happen as we're iterating over an OpenBitSet. For
comment|// completeness, terminate iteration
name|nextDoc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|setDefaultScore
specifier|public
name|void
name|setDefaultScore
parameter_list|(
name|float
name|defaultScore
parameter_list|)
block|{
name|this
operator|.
name|defaultScore
operator|=
name|defaultScore
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
block|{}
block|}
DECL|class|ScoringDocIdCollector
specifier|private
specifier|static
specifier|final
class|class
name|ScoringDocIdCollector
extends|extends
name|ScoredDocIdCollector
block|{
DECL|field|scores
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"synthetic-access"
argument_list|)
DECL|method|ScoringDocIdCollector
specifier|public
name|ScoringDocIdCollector
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|super
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|scores
operator|=
operator|new
name|float
index|[
name|maxDoc
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
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
block|{
name|docIds
operator|.
name|fastSet
argument_list|(
name|docBase
operator|+
name|doc
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|this
operator|.
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
if|if
condition|(
name|numDocIds
operator|>=
name|scores
operator|.
name|length
condition|)
block|{
name|float
index|[]
name|newScores
init|=
operator|new
name|float
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numDocIds
operator|+
literal|1
argument_list|,
literal|4
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scores
argument_list|,
literal|0
argument_list|,
name|newScores
argument_list|,
literal|0
argument_list|,
name|numDocIds
argument_list|)
expr_stmt|;
name|scores
operator|=
name|newScores
expr_stmt|;
block|}
name|scores
index|[
name|numDocIds
index|]
operator|=
name|score
expr_stmt|;
operator|++
name|numDocIds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scoredDocIdsIterator
specifier|public
name|ScoredDocIDsIterator
name|scoredDocIdsIterator
parameter_list|()
block|{
return|return
operator|new
name|ScoredDocIDsIterator
argument_list|()
block|{
specifier|private
name|DocIdSetIterator
name|docIdsIter
init|=
name|docIds
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|int
name|nextDoc
decl_stmt|;
specifier|private
name|int
name|scoresIdx
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|int
name|getDocID
parameter_list|()
block|{
return|return
name|nextDoc
return|;
block|}
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|scores
index|[
name|scoresIdx
index|]
return|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
block|{
try|try
block|{
name|nextDoc
operator|=
name|docIdsIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
literal|false
return|;
block|}
operator|++
name|scoresIdx
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// This should not happen as we're iterating over an OpenBitSet. For
comment|// completeness, terminate iteration
name|nextDoc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultScore
specifier|public
name|float
name|getDefaultScore
parameter_list|()
block|{
return|return
name|ScoredDocIDsIterator
operator|.
name|DEFAULT_SCORE
return|;
block|}
annotation|@
name|Override
DECL|method|setDefaultScore
specifier|public
name|void
name|setDefaultScore
parameter_list|(
name|float
name|defaultScore
parameter_list|)
block|{}
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
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
block|}
DECL|field|numDocIds
specifier|protected
name|int
name|numDocIds
decl_stmt|;
DECL|field|docBase
specifier|protected
name|int
name|docBase
decl_stmt|;
DECL|field|docIds
specifier|protected
specifier|final
name|OpenBitSet
name|docIds
decl_stmt|;
comment|/**    * Creates a new {@link ScoredDocIdCollector} with the given parameters.    *     * @param maxDoc the number of documents that are expected to be collected.    *        Note that if more documents are collected, unexpected exceptions may    *        be thrown. Usually you should pass {@link IndexReader#maxDoc()} of    *        the same IndexReader with which the search is executed.    * @param enableScoring if scoring is enabled, a score will be computed for    *        every matching document, which might be expensive. Therefore if you    *        do not require scoring, it is better to set it to<i>false</i>.    */
DECL|method|create
specifier|public
specifier|static
name|ScoredDocIdCollector
name|create
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|boolean
name|enableScoring
parameter_list|)
block|{
return|return
name|enableScoring
condition|?
operator|new
name|ScoringDocIdCollector
argument_list|(
name|maxDoc
argument_list|)
else|:
operator|new
name|NonScoringDocIdCollector
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
DECL|method|ScoredDocIdCollector
specifier|private
name|ScoredDocIdCollector
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|numDocIds
operator|=
literal|0
expr_stmt|;
name|docIds
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the default score used when scoring is disabled. */
DECL|method|getDefaultScore
specifier|public
specifier|abstract
name|float
name|getDefaultScore
parameter_list|()
function_decl|;
comment|/** Set the default score. Only applicable if scoring is disabled. */
DECL|method|setDefaultScore
specifier|public
specifier|abstract
name|void
name|setDefaultScore
parameter_list|(
name|float
name|defaultScore
parameter_list|)
function_decl|;
DECL|method|scoredDocIdsIterator
specifier|public
specifier|abstract
name|ScoredDocIDsIterator
name|scoredDocIdsIterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getScoredDocIDs
specifier|public
name|ScoredDocIDs
name|getScoredDocIDs
parameter_list|()
block|{
return|return
operator|new
name|ScoredDocIDs
argument_list|()
block|{
specifier|public
name|ScoredDocIDsIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scoredDocIdsIterator
argument_list|()
return|;
block|}
specifier|public
name|DocIdSet
name|getDocIDs
parameter_list|()
block|{
return|return
name|docIds
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|numDocIds
return|;
block|}
block|}
return|;
block|}
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
block|{
name|this
operator|.
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
block|}
end_class
end_unit
