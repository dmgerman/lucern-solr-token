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
name|IndexWriter
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|search
operator|.
name|Scorer
operator|.
name|ChildScorer
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
name|grouping
operator|.
name|GroupDocs
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
name|grouping
operator|.
name|TopGroups
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
begin_comment
comment|/** Collects parent document hits for a Query containing one more more  *  BlockJoinQuery clauses, sorted by the  *  specified parent Sort.  Note that this cannot perform  *  arbitrary joins; rather, it requires that all joined  *  documents are indexed as a doc block (using {@link  *  IndexWriter#addDocuments} or {@link  *  IndexWriter#updateDocuments}).  Ie, the join is computed  *  at index time.  *  *<p>The parent Sort must only use  *  fields from the parent documents; sorting by field in  *  the child documents is not supported.</p>  *  *<p>You should only use this  *  collector if one or more of the clauses in the query is  *  a {@link ToParentBlockJoinQuery}.  This collector will find those query  *  clauses and record the matching child documents for the  *  top scoring parent documents.</p>  *  *<p>Multiple joins (star join) and nested joins and a mix  *  of the two are allowed, as long as in all cases the  *  documents corresponding to a single row of each joined  *  parent table were indexed as a doc block.</p>  *  *<p>For the simple star join you can retrieve the  *  {@link TopGroups} instance containing each {@link ToParentBlockJoinQuery}'s  *  matching child documents for the top parent groups,  *  using {@link #getTopGroups}.  Ie,  *  a single query, which will contain two or more  *  {@link ToParentBlockJoinQuery}'s as clauses representing the star join,  *  can then retrieve two or more {@link TopGroups} instances.</p>  *  *<p>For nested joins, the query will run correctly (ie,  *  match the right parent and child documents), however,  *  because TopGroups is currently unable to support nesting  *  (each group is not able to hold another TopGroups), you  *  are only able to retrieve the TopGroups of the first  *  join.  The TopGroups of the nested joins will not be  *  correct.  *  *  See {@link org.apache.lucene.search.join} for a code  *  sample.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ToParentBlockJoinCollector
specifier|public
class|class
name|ToParentBlockJoinCollector
extends|extends
name|Collector
block|{
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
decl_stmt|;
comment|// Maps each BlockJoinQuery instance to its "slot" in
comment|// joinScorers and in OneGroup's cached doc/scores/count:
DECL|field|joinQueryID
specifier|private
specifier|final
name|Map
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|joinQueryID
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numParentHits
specifier|private
specifier|final
name|int
name|numParentHits
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|FieldValueHitQueue
argument_list|<
name|OneGroup
argument_list|>
name|queue
decl_stmt|;
DECL|field|comparators
specifier|private
specifier|final
name|FieldComparator
index|[]
name|comparators
decl_stmt|;
DECL|field|reverseMul
specifier|private
specifier|final
name|int
index|[]
name|reverseMul
decl_stmt|;
DECL|field|compEnd
specifier|private
specifier|final
name|int
name|compEnd
decl_stmt|;
DECL|field|trackMaxScore
specifier|private
specifier|final
name|boolean
name|trackMaxScore
decl_stmt|;
DECL|field|trackScores
specifier|private
specifier|final
name|boolean
name|trackScores
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|joinScorers
specifier|private
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
index|[]
name|joinScorers
init|=
operator|new
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
index|[
literal|0
index|]
decl_stmt|;
DECL|field|currentReaderContext
specifier|private
name|AtomicReaderContext
name|currentReaderContext
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|queueFull
specifier|private
name|boolean
name|queueFull
decl_stmt|;
DECL|field|bottom
specifier|private
name|OneGroup
name|bottom
decl_stmt|;
DECL|field|totalHitCount
specifier|private
name|int
name|totalHitCount
decl_stmt|;
DECL|field|maxScore
specifier|private
name|float
name|maxScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
comment|/**  Creates a ToParentBlockJoinCollector.  The provided sort must    *  not be null.  If you pass true trackScores, all    *  ToParentBlockQuery instances must not use    *  ScoreMode.None. */
DECL|method|ToParentBlockJoinCollector
specifier|public
name|ToParentBlockJoinCollector
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|int
name|numParentHits
parameter_list|,
name|boolean
name|trackScores
parameter_list|,
name|boolean
name|trackMaxScore
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: allow null sort to be specialized to relevance
comment|// only collector
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|trackMaxScore
operator|=
name|trackMaxScore
expr_stmt|;
if|if
condition|(
name|trackMaxScore
condition|)
block|{
name|maxScore
operator|=
name|Float
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
name|this
operator|.
name|trackScores
operator|=
name|trackScores
expr_stmt|;
name|this
operator|.
name|numParentHits
operator|=
name|numParentHits
expr_stmt|;
name|queue
operator|=
name|FieldValueHitQueue
operator|.
name|create
argument_list|(
name|sort
operator|.
name|getSort
argument_list|()
argument_list|,
name|numParentHits
argument_list|)
expr_stmt|;
name|comparators
operator|=
name|queue
operator|.
name|getComparators
argument_list|()
expr_stmt|;
name|reverseMul
operator|=
name|queue
operator|.
name|getReverseMul
argument_list|()
expr_stmt|;
name|compEnd
operator|=
name|comparators
operator|.
name|length
operator|-
literal|1
expr_stmt|;
block|}
DECL|class|OneGroup
specifier|private
specifier|static
specifier|final
class|class
name|OneGroup
extends|extends
name|FieldValueHitQueue
operator|.
name|Entry
block|{
DECL|method|OneGroup
specifier|public
name|OneGroup
parameter_list|(
name|int
name|comparatorSlot
parameter_list|,
name|int
name|parentDoc
parameter_list|,
name|float
name|parentScore
parameter_list|,
name|int
name|numJoins
parameter_list|,
name|boolean
name|doScores
parameter_list|)
block|{
name|super
argument_list|(
name|comparatorSlot
argument_list|,
name|parentDoc
argument_list|,
name|parentScore
argument_list|)
expr_stmt|;
name|docs
operator|=
operator|new
name|int
index|[
name|numJoins
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|joinID
init|=
literal|0
init|;
name|joinID
operator|<
name|numJoins
condition|;
name|joinID
operator|++
control|)
block|{
name|docs
index|[
name|joinID
index|]
operator|=
operator|new
name|int
index|[
literal|5
index|]
expr_stmt|;
block|}
if|if
condition|(
name|doScores
condition|)
block|{
name|scores
operator|=
operator|new
name|float
index|[
name|numJoins
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|joinID
init|=
literal|0
init|;
name|joinID
operator|<
name|numJoins
condition|;
name|joinID
operator|++
control|)
block|{
name|scores
index|[
name|joinID
index|]
operator|=
operator|new
name|float
index|[
literal|5
index|]
expr_stmt|;
block|}
block|}
name|counts
operator|=
operator|new
name|int
index|[
name|numJoins
index|]
expr_stmt|;
block|}
DECL|field|readerContext
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|docs
name|int
index|[]
index|[]
name|docs
decl_stmt|;
DECL|field|scores
name|float
index|[]
index|[]
name|scores
decl_stmt|;
DECL|field|counts
name|int
index|[]
name|counts
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|parentDoc
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("C parentDoc=" + parentDoc);
name|totalHitCount
operator|++
expr_stmt|;
name|float
name|score
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
if|if
condition|(
name|trackMaxScore
condition|)
block|{
name|score
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxScore
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we could sweep all joinScorers here and
comment|// aggregate total child hit count, so we can fill this
comment|// in getTopGroups (we wire it to 0 now)
if|if
condition|(
name|queueFull
condition|)
block|{
comment|//System.out.println("  queueFull");
comment|// Fastmatch: return if this hit is not competitive
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|c
init|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compareBottom
argument_list|(
name|parentDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
comment|// Definitely not competitive.
comment|//System.out.println("    skip");
return|return;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
comment|// Definitely competitive.
break|break;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|compEnd
condition|)
block|{
comment|// Here c=0. If we're at the last comparator, this doc is not
comment|// competitive, since docs are visited in doc Id order, which means
comment|// this doc cannot compete with any other document in the queue.
comment|//System.out.println("    skip");
return|return;
block|}
block|}
comment|//System.out.println("    competes!  doc=" + (docBase + parentDoc));
comment|// This hit is competitive - replace bottom element in queue& adjustTop
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
name|bottom
operator|.
name|slot
argument_list|,
name|parentDoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|trackMaxScore
operator|&&
name|trackScores
condition|)
block|{
name|score
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
name|bottom
operator|.
name|doc
operator|=
name|docBase
operator|+
name|parentDoc
expr_stmt|;
name|bottom
operator|.
name|readerContext
operator|=
name|currentReaderContext
expr_stmt|;
name|bottom
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|copyGroups
argument_list|(
name|bottom
argument_list|)
expr_stmt|;
name|bottom
operator|=
name|queue
operator|.
name|updateTop
argument_list|()
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
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
name|bottom
operator|.
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Startup transient: queue is not yet full:
specifier|final
name|int
name|comparatorSlot
init|=
name|totalHitCount
operator|-
literal|1
decl_stmt|;
comment|// Copy hit into queue
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
name|comparatorSlot
argument_list|,
name|parentDoc
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("  startup: new OG doc=" +
comment|//(docBase+parentDoc));
if|if
condition|(
operator|!
name|trackMaxScore
operator|&&
name|trackScores
condition|)
block|{
name|score
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
specifier|final
name|OneGroup
name|og
init|=
operator|new
name|OneGroup
argument_list|(
name|comparatorSlot
argument_list|,
name|docBase
operator|+
name|parentDoc
argument_list|,
name|score
argument_list|,
name|joinScorers
operator|.
name|length
argument_list|,
name|trackScores
argument_list|)
decl_stmt|;
name|og
operator|.
name|readerContext
operator|=
name|currentReaderContext
expr_stmt|;
name|copyGroups
argument_list|(
name|og
argument_list|)
expr_stmt|;
name|bottom
operator|=
name|queue
operator|.
name|add
argument_list|(
name|og
argument_list|)
expr_stmt|;
name|queueFull
operator|=
name|totalHitCount
operator|==
name|numParentHits
expr_stmt|;
if|if
condition|(
name|queueFull
condition|)
block|{
comment|// End of startup transient: queue just filled up:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
name|bottom
operator|.
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Pulls out child doc and scores for all join queries:
DECL|method|copyGroups
specifier|private
name|void
name|copyGroups
parameter_list|(
name|OneGroup
name|og
parameter_list|)
block|{
comment|// While rare, it's possible top arrays could be too
comment|// short if join query had null scorer on first
comment|// segment(s) but then became non-null on later segments
specifier|final
name|int
name|numSubScorers
init|=
name|joinScorers
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|og
operator|.
name|docs
operator|.
name|length
operator|<
name|numSubScorers
condition|)
block|{
comment|// While rare, this could happen if join query had
comment|// null scorer on first segment(s) but then became
comment|// non-null on later segments
name|og
operator|.
name|docs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|og
operator|.
name|docs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|og
operator|.
name|counts
operator|.
name|length
operator|<
name|numSubScorers
condition|)
block|{
name|og
operator|.
name|counts
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|og
operator|.
name|counts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|trackScores
operator|&&
name|og
operator|.
name|scores
operator|.
name|length
operator|<
name|numSubScorers
condition|)
block|{
name|og
operator|.
name|scores
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|og
operator|.
name|scores
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("copyGroups parentDoc=" + og.doc);
for|for
control|(
name|int
name|scorerIDX
init|=
literal|0
init|;
name|scorerIDX
operator|<
name|numSubScorers
condition|;
name|scorerIDX
operator|++
control|)
block|{
specifier|final
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
name|joinScorer
init|=
name|joinScorers
index|[
name|scorerIDX
index|]
decl_stmt|;
comment|//System.out.println("  scorer=" + joinScorer);
if|if
condition|(
name|joinScorer
operator|!=
literal|null
condition|)
block|{
name|og
operator|.
name|counts
index|[
name|scorerIDX
index|]
operator|=
name|joinScorer
operator|.
name|getChildCount
argument_list|()
expr_stmt|;
comment|//System.out.println("    count=" + og.counts[scorerIDX]);
name|og
operator|.
name|docs
index|[
name|scorerIDX
index|]
operator|=
name|joinScorer
operator|.
name|swapChildDocs
argument_list|(
name|og
operator|.
name|docs
index|[
name|scorerIDX
index|]
argument_list|)
expr_stmt|;
comment|/*         for(int idx=0;idx<og.counts[scorerIDX];idx++) {           System.out.println("    docs[" + idx + "]=" + og.docs[scorerIDX][idx]);         }         */
if|if
condition|(
name|trackScores
condition|)
block|{
name|og
operator|.
name|scores
index|[
name|scorerIDX
index|]
operator|=
name|joinScorer
operator|.
name|swapChildScores
argument_list|(
name|og
operator|.
name|scores
index|[
name|scorerIDX
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|currentReaderContext
operator|=
name|context
expr_stmt|;
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|comparators
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
name|queue
operator|.
name|setComparator
argument_list|(
name|compIDX
argument_list|,
name|comparators
index|[
name|compIDX
index|]
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|enroll
specifier|private
name|void
name|enroll
parameter_list|(
name|ToParentBlockJoinQuery
name|query
parameter_list|,
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
name|scorer
parameter_list|)
block|{
specifier|final
name|Integer
name|slot
init|=
name|joinQueryID
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|==
literal|null
condition|)
block|{
name|joinQueryID
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|joinScorers
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//System.out.println("found JQ: " + query + " slot=" + joinScorers.length);
specifier|final
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
index|[]
name|newArray
init|=
operator|new
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
index|[
literal|1
operator|+
name|joinScorers
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|joinScorers
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|joinScorers
operator|.
name|length
argument_list|)
expr_stmt|;
name|joinScorers
operator|=
name|newArray
expr_stmt|;
name|joinScorers
index|[
name|joinScorers
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|scorer
expr_stmt|;
block|}
else|else
block|{
name|joinScorers
index|[
name|slot
index|]
operator|=
name|scorer
expr_stmt|;
block|}
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
block|{
comment|//System.out.println("C.setScorer scorer=" + scorer);
comment|// Since we invoke .score(), and the comparators likely
comment|// do as well, cache it so it's only "really" computed
comment|// once:
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
for|for
control|(
name|int
name|compIDX
init|=
literal|0
init|;
name|compIDX
operator|<
name|comparators
operator|.
name|length
condition|;
name|compIDX
operator|++
control|)
block|{
name|comparators
index|[
name|compIDX
index|]
operator|.
name|setScorer
argument_list|(
name|this
operator|.
name|scorer
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|fill
argument_list|(
name|joinScorers
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Queue
argument_list|<
name|Scorer
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|Scorer
argument_list|>
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|scorer
operator|=
name|queue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|scorer
operator|instanceof
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
condition|)
block|{
name|enroll
argument_list|(
operator|(
name|ToParentBlockJoinQuery
operator|)
name|scorer
operator|.
name|getWeight
argument_list|()
operator|.
name|getQuery
argument_list|()
argument_list|,
operator|(
name|ToParentBlockJoinQuery
operator|.
name|BlockJoinScorer
operator|)
name|scorer
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildScorer
name|sub
range|:
name|scorer
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|sub
operator|.
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|FakeScorer
specifier|private
specifier|final
specifier|static
class|class
name|FakeScorer
extends|extends
name|Scorer
block|{
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|method|FakeScorer
specifier|public
name|FakeScorer
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
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
literal|1
return|;
comment|// TODO: does anything else make sense?... duplicate of grouping's FakeScorer btw?
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
name|doc
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
literal|1
return|;
block|}
block|}
DECL|field|sortedGroups
specifier|private
name|OneGroup
index|[]
name|sortedGroups
decl_stmt|;
DECL|method|sortQueue
specifier|private
name|void
name|sortQueue
parameter_list|()
block|{
name|sortedGroups
operator|=
operator|new
name|OneGroup
index|[
name|queue
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|downTo
init|=
name|queue
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|downTo
operator|>=
literal|0
condition|;
name|downTo
operator|--
control|)
block|{
name|sortedGroups
index|[
name|downTo
index|]
operator|=
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the TopGroups for the specified    *  BlockJoinQuery. The groupValue of each GroupDocs will    *  be the parent docID for that group.    *  The number of documents within each group is calculated as minimum of<code>maxDocsPerGroup</code>    *  and number of matched child documents for that group.    *  Returns null if no groups matched.    *    * @param query Search query    * @param withinGroupSort Sort criteria within groups    * @param offset Parent docs offset    * @param maxDocsPerGroup Upper bound of documents per group number    * @param withinGroupOffset Offset within each group of child docs    * @param fillSortFields Specifies whether to add sort fields or not    * @return TopGroups for specified query    * @throws IOException if there is a low-level I/O error    */
DECL|method|getTopGroups
specifier|public
name|TopGroups
argument_list|<
name|Integer
argument_list|>
name|getTopGroups
parameter_list|(
name|ToParentBlockJoinQuery
name|query
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|int
name|withinGroupOffset
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Integer
name|_slot
init|=
name|joinQueryID
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|_slot
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|totalHitCount
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the Query did not contain the provided BlockJoinQuery"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|sortedGroups
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|offset
operator|>=
name|queue
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|sortQueue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|offset
operator|>
name|sortedGroups
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|accumulateGroups
argument_list|(
name|_slot
argument_list|,
name|offset
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|withinGroupOffset
argument_list|,
name|withinGroupSort
argument_list|,
name|fillSortFields
argument_list|)
return|;
block|}
comment|/**    *  Accumulates groups for the BlockJoinQuery specified by its slot.    *    * @param slot Search query's slot    * @param offset Parent docs offset    * @param maxDocsPerGroup Upper bound of documents per group number    * @param withinGroupOffset Offset within each group of child docs    * @param withinGroupSort Sort criteria within groups    * @param fillSortFields Specifies whether to add sort fields or not    * @return TopGroups for the query specified by slot    * @throws IOException if there is a low-level I/O error    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|accumulateGroups
specifier|private
name|TopGroups
argument_list|<
name|Integer
argument_list|>
name|accumulateGroups
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|int
name|withinGroupOffset
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|GroupDocs
argument_list|<
name|Integer
argument_list|>
index|[]
name|groups
init|=
operator|new
name|GroupDocs
index|[
name|sortedGroups
operator|.
name|length
operator|-
name|offset
index|]
decl_stmt|;
specifier|final
name|FakeScorer
name|fakeScorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
name|int
name|totalGroupedHitCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|groupIDX
init|=
name|offset
init|;
name|groupIDX
operator|<
name|sortedGroups
operator|.
name|length
condition|;
name|groupIDX
operator|++
control|)
block|{
specifier|final
name|OneGroup
name|og
init|=
name|sortedGroups
index|[
name|groupIDX
index|]
decl_stmt|;
specifier|final
name|int
name|numChildDocs
init|=
name|og
operator|.
name|counts
index|[
name|slot
index|]
decl_stmt|;
comment|// Number of documents in group should be bounded to prevent redundant memory allocation
specifier|final
name|int
name|numDocsInGroup
init|=
name|Math
operator|.
name|min
argument_list|(
name|numChildDocs
argument_list|,
name|maxDocsPerGroup
argument_list|)
decl_stmt|;
comment|// At this point we hold all docs w/ in each group,
comment|// unsorted; we now sort them:
specifier|final
name|TopDocsCollector
argument_list|<
name|?
argument_list|>
name|collector
decl_stmt|;
if|if
condition|(
name|withinGroupSort
operator|==
literal|null
condition|)
block|{
comment|// Sort by score
if|if
condition|(
operator|!
name|trackScores
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot sort by relevance within group: trackScores=false"
argument_list|)
throw|;
block|}
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numDocsInGroup
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Sort by fields
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|withinGroupSort
argument_list|,
name|numDocsInGroup
argument_list|,
name|fillSortFields
argument_list|,
name|trackScores
argument_list|,
name|trackMaxScore
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|setScorer
argument_list|(
name|fakeScorer
argument_list|)
expr_stmt|;
name|collector
operator|.
name|setNextReader
argument_list|(
name|og
operator|.
name|readerContext
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|docIDX
init|=
literal|0
init|;
name|docIDX
operator|<
name|numChildDocs
condition|;
name|docIDX
operator|++
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|og
operator|.
name|docs
index|[
name|slot
index|]
index|[
name|docIDX
index|]
decl_stmt|;
name|fakeScorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|trackScores
condition|)
block|{
name|fakeScorer
operator|.
name|score
operator|=
name|og
operator|.
name|scores
index|[
name|slot
index|]
index|[
name|docIDX
index|]
expr_stmt|;
block|}
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|totalGroupedHitCount
operator|+=
name|numChildDocs
expr_stmt|;
specifier|final
name|Object
index|[]
name|groupSortValues
decl_stmt|;
if|if
condition|(
name|fillSortFields
condition|)
block|{
name|groupSortValues
operator|=
operator|new
name|Object
index|[
name|comparators
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|sortFieldIDX
init|=
literal|0
init|;
name|sortFieldIDX
operator|<
name|comparators
operator|.
name|length
condition|;
name|sortFieldIDX
operator|++
control|)
block|{
name|groupSortValues
index|[
name|sortFieldIDX
index|]
operator|=
name|comparators
index|[
name|sortFieldIDX
index|]
operator|.
name|value
argument_list|(
name|og
operator|.
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|groupSortValues
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|TopDocs
name|topDocs
init|=
name|collector
operator|.
name|topDocs
argument_list|(
name|withinGroupOffset
argument_list|,
name|numDocsInGroup
argument_list|)
decl_stmt|;
name|groups
index|[
name|groupIDX
operator|-
name|offset
index|]
operator|=
operator|new
name|GroupDocs
argument_list|<>
argument_list|(
name|og
operator|.
name|score
argument_list|,
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|,
name|numChildDocs
argument_list|,
name|topDocs
operator|.
name|scoreDocs
argument_list|,
name|og
operator|.
name|doc
argument_list|,
name|groupSortValues
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TopGroups
argument_list|<>
argument_list|(
operator|new
name|TopGroups
argument_list|<>
argument_list|(
name|sort
operator|.
name|getSort
argument_list|()
argument_list|,
name|withinGroupSort
operator|==
literal|null
condition|?
literal|null
else|:
name|withinGroupSort
operator|.
name|getSort
argument_list|()
argument_list|,
literal|0
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|groups
argument_list|,
name|maxScore
argument_list|)
argument_list|,
name|totalHitCount
argument_list|)
return|;
block|}
comment|/** Returns the TopGroups for the specified BlockJoinQuery.    *  The groupValue of each GroupDocs will be the parent docID for that group.    *  The number of documents within each group    *  equals to the total number of matched child documents for that group.    *  Returns null if no groups matched.    *    * @param query Search query    * @param withinGroupSort Sort criteria within groups    * @param offset Parent docs offset    * @param withinGroupOffset Offset within each group of child docs    * @param fillSortFields Specifies whether to add sort fields or not    * @return TopGroups for specified query    * @throws IOException if there is a low-level I/O error    */
DECL|method|getTopGroupsWithAllChildDocs
specifier|public
name|TopGroups
argument_list|<
name|Integer
argument_list|>
name|getTopGroupsWithAllChildDocs
parameter_list|(
name|ToParentBlockJoinQuery
name|query
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|withinGroupOffset
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTopGroups
argument_list|(
name|query
argument_list|,
name|withinGroupSort
argument_list|,
name|offset
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|withinGroupOffset
argument_list|,
name|fillSortFields
argument_list|)
return|;
block|}
comment|/**    * Returns the highest score across all collected parent hits, as long as    *<code>trackMaxScores=true</code> was passed    * {@link #ToParentBlockJoinCollector(Sort, int, boolean, boolean) on    * construction}. Else, this returns<code>Float.NaN</code>    */
DECL|method|getMaxScore
specifier|public
name|float
name|getMaxScore
parameter_list|()
block|{
return|return
name|maxScore
return|;
block|}
block|}
end_class
end_unit
