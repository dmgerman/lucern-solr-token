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
name|util
operator|.
name|ArrayList
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
begin_comment
comment|/* See the description in BooleanScorer.java, comparing  * BooleanScorer& BooleanScorer2 */
end_comment
begin_comment
comment|/** An alternative to BooleanScorer that also allows a minimum number  * of optional scorers that should match.  *<br>Implements skipTo(), and has no limitations on the numbers of added scorers.  *<br>Uses ConjunctionScorer, DisjunctionScorer, ReqOptScorer and ReqExclScorer.  */
end_comment
begin_class
DECL|class|BooleanScorer2
class|class
name|BooleanScorer2
extends|extends
name|Scorer
block|{
DECL|field|requiredScorers
specifier|private
name|ArrayList
name|requiredScorers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|optionalScorers
specifier|private
name|ArrayList
name|optionalScorers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|prohibitedScorers
specifier|private
name|ArrayList
name|prohibitedScorers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|class|Coordinator
specifier|private
class|class
name|Coordinator
block|{
DECL|field|coordFactors
name|float
index|[]
name|coordFactors
init|=
literal|null
decl_stmt|;
DECL|field|maxCoord
name|int
name|maxCoord
init|=
literal|0
decl_stmt|;
comment|// to be increased for each non prohibited scorer
DECL|field|nrMatchers
name|int
name|nrMatchers
decl_stmt|;
comment|// to be increased by score() of match counting scorers.
DECL|method|init
name|void
name|init
parameter_list|()
block|{
comment|// use after all scorers have been added.
name|coordFactors
operator|=
operator|new
name|float
index|[
name|maxCoord
operator|+
literal|1
index|]
expr_stmt|;
name|Similarity
name|sim
init|=
name|getSimilarity
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
operator|<=
name|maxCoord
condition|;
name|i
operator|++
control|)
block|{
name|coordFactors
index|[
name|i
index|]
operator|=
name|sim
operator|.
name|coord
argument_list|(
name|i
argument_list|,
name|maxCoord
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|coordinator
specifier|private
specifier|final
name|Coordinator
name|coordinator
decl_stmt|;
comment|/** The scorer to which all scoring will be delegated,    * except for computing and using the coordination factor.    */
DECL|field|countingSumScorer
specifier|private
name|Scorer
name|countingSumScorer
init|=
literal|null
decl_stmt|;
comment|/** The number of optionalScorers that need to match (if there are any) */
DECL|field|minNrShouldMatch
specifier|private
specifier|final
name|int
name|minNrShouldMatch
decl_stmt|;
comment|/** Whether it is allowed to return documents out of order.    *  This can accelerate the scoring of disjunction queries.      */
DECL|field|allowDocsOutOfOrder
specifier|private
name|boolean
name|allowDocsOutOfOrder
decl_stmt|;
comment|/** Create a BooleanScorer2.    * @param similarity The similarity to be used.    * @param minNrShouldMatch The minimum number of optional added scorers    *                         that should match during the search.    *                         In case no required scorers are added,    *                         at least one of the optional scorers will have to    *                         match during the search.    * @param allowDocsOutOfOrder Whether it is allowed to return documents out of order.    *                            This can accelerate the scoring of disjunction queries.                             */
DECL|method|BooleanScorer2
specifier|public
name|BooleanScorer2
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|int
name|minNrShouldMatch
parameter_list|,
name|boolean
name|allowDocsOutOfOrder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
if|if
condition|(
name|minNrShouldMatch
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Minimum number of optional scorers should not be negative"
argument_list|)
throw|;
block|}
name|coordinator
operator|=
operator|new
name|Coordinator
argument_list|()
expr_stmt|;
name|this
operator|.
name|minNrShouldMatch
operator|=
name|minNrShouldMatch
expr_stmt|;
name|this
operator|.
name|allowDocsOutOfOrder
operator|=
name|allowDocsOutOfOrder
expr_stmt|;
block|}
comment|/** Create a BooleanScorer2.    *  In no required scorers are added,    *  at least one of the optional scorers will have to match during the search.    * @param similarity The similarity to be used.    * @param minNrShouldMatch The minimum number of optional added scorers    *                         that should match during the search.    *                         In case no required scorers are added,    *                         at least one of the optional scorers will have to    *                         match during the search.    */
DECL|method|BooleanScorer2
specifier|public
name|BooleanScorer2
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|int
name|minNrShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|similarity
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Create a BooleanScorer2.    *  In no required scorers are added,    *  at least one of the optional scorers will have to match during the search.    * @param similarity The similarity to be used.    */
DECL|method|BooleanScorer2
specifier|public
name|BooleanScorer2
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|similarity
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Scorer
name|scorer
parameter_list|,
name|boolean
name|required
parameter_list|,
name|boolean
name|prohibited
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|prohibited
condition|)
block|{
name|coordinator
operator|.
name|maxCoord
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|required
condition|)
block|{
if|if
condition|(
name|prohibited
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"scorer cannot be required and prohibited"
argument_list|)
throw|;
block|}
name|requiredScorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prohibited
condition|)
block|{
name|prohibitedScorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|optionalScorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Initialize the match counting scorer that sums all the    * scores.<p>    * When "counting" is used in a name it means counting the number    * of matching scorers.<br>    * When "sum" is used in a name it means score value summing    * over the matching scorers    */
DECL|method|initCountingSumScorer
specifier|private
name|void
name|initCountingSumScorer
parameter_list|()
throws|throws
name|IOException
block|{
name|coordinator
operator|.
name|init
argument_list|()
expr_stmt|;
name|countingSumScorer
operator|=
name|makeCountingSumScorer
argument_list|()
expr_stmt|;
block|}
comment|/** Count a scorer as a single match. */
DECL|class|SingleMatchScorer
specifier|private
class|class
name|SingleMatchScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|lastScoredDoc
specifier|private
name|int
name|lastScoredDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// Save the score of lastScoredDoc, so that we don't compute it more than
comment|// once in score().
DECL|field|lastDocScore
specifier|private
name|float
name|lastDocScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|method|SingleMatchScorer
name|SingleMatchScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|super
argument_list|(
name|scorer
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|doc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|lastScoredDoc
condition|)
block|{
if|if
condition|(
name|doc
operator|>
name|lastScoredDoc
condition|)
block|{
name|lastDocScore
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|lastScoredDoc
operator|=
name|doc
expr_stmt|;
block|}
name|coordinator
operator|.
name|nrMatchers
operator|++
expr_stmt|;
block|}
return|return
name|lastDocScore
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|docNr
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|skipTo
argument_list|(
name|docNr
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|docNr
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|explain
argument_list|(
name|docNr
argument_list|)
return|;
block|}
block|}
DECL|method|countingDisjunctionSumScorer
specifier|private
name|Scorer
name|countingDisjunctionSumScorer
parameter_list|(
specifier|final
name|List
name|scorers
parameter_list|,
name|int
name|minNrShouldMatch
parameter_list|)
throws|throws
name|IOException
block|{
comment|// each scorer from the list counted as a single matcher
return|return
operator|new
name|DisjunctionSumScorer
argument_list|(
name|scorers
argument_list|,
name|minNrShouldMatch
argument_list|)
block|{
specifier|private
name|int
name|lastScoredDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// Save the score of lastScoredDoc, so that we don't compute it more than
comment|// once in score().
specifier|private
name|float
name|lastDocScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|doc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|lastScoredDoc
condition|)
block|{
if|if
condition|(
name|doc
operator|>
name|lastScoredDoc
condition|)
block|{
name|lastDocScore
operator|=
name|super
operator|.
name|score
argument_list|()
expr_stmt|;
name|lastScoredDoc
operator|=
name|doc
expr_stmt|;
block|}
name|coordinator
operator|.
name|nrMatchers
operator|+=
name|super
operator|.
name|nrMatchers
expr_stmt|;
block|}
return|return
name|lastDocScore
return|;
block|}
block|}
return|;
block|}
DECL|field|defaultSimilarity
specifier|private
specifier|static
specifier|final
name|Similarity
name|defaultSimilarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
DECL|method|countingConjunctionSumScorer
specifier|private
name|Scorer
name|countingConjunctionSumScorer
parameter_list|(
name|List
name|requiredScorers
parameter_list|)
throws|throws
name|IOException
block|{
comment|// each scorer from the list counted as a single matcher
specifier|final
name|int
name|requiredNrMatchers
init|=
name|requiredScorers
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|defaultSimilarity
argument_list|,
name|requiredScorers
argument_list|)
block|{
specifier|private
name|int
name|lastScoredDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// Save the score of lastScoredDoc, so that we don't compute it more than
comment|// once in score().
specifier|private
name|float
name|lastDocScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|doc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|lastScoredDoc
condition|)
block|{
if|if
condition|(
name|doc
operator|>
name|lastScoredDoc
condition|)
block|{
name|lastDocScore
operator|=
name|super
operator|.
name|score
argument_list|()
expr_stmt|;
name|lastScoredDoc
operator|=
name|doc
expr_stmt|;
block|}
name|coordinator
operator|.
name|nrMatchers
operator|+=
name|requiredNrMatchers
expr_stmt|;
block|}
comment|// All scorers match, so defaultSimilarity super.score() always has 1 as
comment|// the coordination factor.
comment|// Therefore the sum of the scores of the requiredScorers
comment|// is used as score.
return|return
name|lastDocScore
return|;
block|}
block|}
return|;
block|}
DECL|method|dualConjunctionSumScorer
specifier|private
name|Scorer
name|dualConjunctionSumScorer
parameter_list|(
name|Scorer
name|req1
parameter_list|,
name|Scorer
name|req2
parameter_list|)
throws|throws
name|IOException
block|{
comment|// non counting.
return|return
operator|new
name|ConjunctionScorer
argument_list|(
name|defaultSimilarity
argument_list|,
operator|new
name|Scorer
index|[]
block|{
name|req1
block|,
name|req2
block|}
argument_list|)
return|;
comment|// All scorers match, so defaultSimilarity always has 1 as
comment|// the coordination factor.
comment|// Therefore the sum of the scores of two scorers
comment|// is used as score.
block|}
comment|/** Returns the scorer to be used for match counting and score summing.    * Uses requiredScorers, optionalScorers and prohibitedScorers.    */
DECL|method|makeCountingSumScorer
specifier|private
name|Scorer
name|makeCountingSumScorer
parameter_list|()
throws|throws
name|IOException
block|{
comment|// each scorer counted as a single matcher
return|return
operator|(
name|requiredScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|makeCountingSumScorerNoReq
argument_list|()
else|:
name|makeCountingSumScorerSomeReq
argument_list|()
return|;
block|}
DECL|method|makeCountingSumScorerNoReq
specifier|private
name|Scorer
name|makeCountingSumScorerNoReq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// No required scorers
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|NonMatchingScorer
argument_list|()
return|;
comment|// no clauses or only prohibited clauses
block|}
else|else
block|{
comment|// No required scorers. At least one optional scorer.
comment|// minNrShouldMatch optional scorers are required, but at least 1
name|int
name|nrOptRequired
init|=
operator|(
name|minNrShouldMatch
operator|<
literal|1
operator|)
condition|?
literal|1
else|:
name|minNrShouldMatch
decl_stmt|;
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|<
name|nrOptRequired
condition|)
block|{
return|return
operator|new
name|NonMatchingScorer
argument_list|()
return|;
comment|// fewer optional clauses than minimum (at least 1) that should match
block|}
else|else
block|{
comment|// optionalScorers.size()>= nrOptRequired, no required scorers
name|Scorer
name|requiredCountingSumScorer
init|=
operator|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|>
name|nrOptRequired
operator|)
condition|?
name|countingDisjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|,
name|nrOptRequired
argument_list|)
else|:
comment|// optionalScorers.size() == nrOptRequired (all optional scorers are required), no required scorers
operator|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
operator|new
name|SingleMatchScorer
argument_list|(
operator|(
name|Scorer
operator|)
name|optionalScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
name|countingConjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|)
decl_stmt|;
return|return
name|addProhibitedScorers
argument_list|(
name|requiredCountingSumScorer
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|makeCountingSumScorerSomeReq
specifier|private
name|Scorer
name|makeCountingSumScorerSomeReq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// At least one required scorer.
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|<
name|minNrShouldMatch
condition|)
block|{
return|return
operator|new
name|NonMatchingScorer
argument_list|()
return|;
comment|// fewer optional clauses than minimum that should match
block|}
elseif|else
if|if
condition|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
name|minNrShouldMatch
condition|)
block|{
comment|// all optional scorers also required.
name|ArrayList
name|allReq
init|=
operator|new
name|ArrayList
argument_list|(
name|requiredScorers
argument_list|)
decl_stmt|;
name|allReq
operator|.
name|addAll
argument_list|(
name|optionalScorers
argument_list|)
expr_stmt|;
return|return
name|addProhibitedScorers
argument_list|(
name|countingConjunctionSumScorer
argument_list|(
name|allReq
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// optionalScorers.size()> minNrShouldMatch, and at least one required scorer
name|Scorer
name|requiredCountingSumScorer
init|=
operator|(
name|requiredScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
operator|new
name|SingleMatchScorer
argument_list|(
operator|(
name|Scorer
operator|)
name|requiredScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
name|countingConjunctionSumScorer
argument_list|(
name|requiredScorers
argument_list|)
decl_stmt|;
if|if
condition|(
name|minNrShouldMatch
operator|>
literal|0
condition|)
block|{
comment|// use a required disjunction scorer over the optional scorers
return|return
name|addProhibitedScorers
argument_list|(
name|dualConjunctionSumScorer
argument_list|(
comment|// non counting
name|requiredCountingSumScorer
argument_list|,
name|countingDisjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|,
name|minNrShouldMatch
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// minNrShouldMatch == 0
return|return
operator|new
name|ReqOptSumScorer
argument_list|(
name|addProhibitedScorers
argument_list|(
name|requiredCountingSumScorer
argument_list|)
argument_list|,
operator|(
operator|(
name|optionalScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
operator|new
name|SingleMatchScorer
argument_list|(
operator|(
name|Scorer
operator|)
name|optionalScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
name|countingDisjunctionSumScorer
argument_list|(
name|optionalScorers
argument_list|,
literal|1
argument_list|)
operator|)
argument_list|)
return|;
comment|// require 1 in combined, optional scorer.
block|}
block|}
block|}
comment|/** Returns the scorer to be used for match counting and score summing.    * Uses the given required scorer and the prohibitedScorers.    * @param requiredCountingSumScorer A required scorer already built.    */
DECL|method|addProhibitedScorers
specifier|private
name|Scorer
name|addProhibitedScorers
parameter_list|(
name|Scorer
name|requiredCountingSumScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|prohibitedScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|requiredCountingSumScorer
comment|// no prohibited
else|:
operator|new
name|ReqExclScorer
argument_list|(
name|requiredCountingSumScorer
argument_list|,
operator|(
operator|(
name|prohibitedScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
operator|(
name|Scorer
operator|)
name|prohibitedScorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
operator|new
name|DisjunctionSumScorer
argument_list|(
name|prohibitedScorers
argument_list|)
operator|)
argument_list|)
return|;
block|}
comment|/** Scores and collects all matching documents.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    *<br>When this method is used the {@link #explain(int)} method should not be used.    * @deprecated use {@link #score(Collector)} instead.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
operator|new
name|HitCollectorWrapper
argument_list|(
name|hc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Scores and collects all matching documents.    * @param collector The collector to which all matching documents are passed through.    *<br>When this method is used the {@link #explain(int)} method should not be used.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|allowDocsOutOfOrder
operator|&&
name|requiredScorers
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|prohibitedScorers
operator|.
name|size
argument_list|()
operator|<
literal|32
condition|)
block|{
comment|// fall back to BooleanScorer, scores documents somewhat out of order
name|BooleanScorer
name|bs
init|=
operator|new
name|BooleanScorer
argument_list|(
name|getSimilarity
argument_list|()
argument_list|,
name|minNrShouldMatch
argument_list|)
decl_stmt|;
name|Iterator
name|si
init|=
name|optionalScorers
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|si
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|bs
operator|.
name|add
argument_list|(
operator|(
name|Scorer
operator|)
name|si
operator|.
name|next
argument_list|()
argument_list|,
literal|false
comment|/* required */
argument_list|,
literal|false
comment|/* prohibited */
argument_list|)
expr_stmt|;
block|}
name|si
operator|=
name|prohibitedScorers
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|si
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|bs
operator|.
name|add
argument_list|(
operator|(
name|Scorer
operator|)
name|si
operator|.
name|next
argument_list|()
argument_list|,
literal|false
comment|/* required */
argument_list|,
literal|true
comment|/* prohibited */
argument_list|)
expr_stmt|;
block|}
name|bs
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|countingSumScorer
operator|==
literal|null
condition|)
block|{
name|initCountingSumScorer
argument_list|()
expr_stmt|;
block|}
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|countingSumScorer
operator|.
name|next
argument_list|()
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|countingSumScorer
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Expert: Collects matching documents in a range.    *<br>Note that {@link #next()} must be called once before this method is    * called for the first time.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    * @param max Do not score documents past this.    * @return true if more matching documents may remain.    * @deprecated use {@link #score(Collector, int)} instead.    */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|score
argument_list|(
operator|new
name|HitCollectorWrapper
argument_list|(
name|hc
argument_list|)
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/** Expert: Collects matching documents in a range.    *<br>Note that {@link #next()} must be called once before this method is    * called for the first time.    * @param collector The collector to which all matching documents are passed through.    * @param max Do not score documents past this.    * @return true if more matching documents may remain.    */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
comment|// null pointer exception when next() was not called before:
name|int
name|docNr
init|=
name|countingSumScorer
operator|.
name|doc
argument_list|()
decl_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|docNr
operator|<
name|max
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|docNr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|countingSumScorer
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|docNr
operator|=
name|countingSumScorer
operator|.
name|doc
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|countingSumScorer
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|countingSumScorer
operator|==
literal|null
condition|)
block|{
name|initCountingSumScorer
argument_list|()
expr_stmt|;
block|}
return|return
name|countingSumScorer
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|coordinator
operator|.
name|nrMatchers
operator|=
literal|0
expr_stmt|;
name|float
name|sum
init|=
name|countingSumScorer
operator|.
name|score
argument_list|()
decl_stmt|;
return|return
name|sum
operator|*
name|coordinator
operator|.
name|coordFactors
index|[
name|coordinator
operator|.
name|nrMatchers
index|]
return|;
block|}
comment|/** Skips to the first match beyond the current whose document number is    * greater than or equal to a given target.    *     *<p>When this method is used the {@link #explain(int)} method should not be used.    *     * @param target The target document number.    * @return true iff there is such a match.    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|countingSumScorer
operator|==
literal|null
condition|)
block|{
name|initCountingSumScorer
argument_list|()
expr_stmt|;
block|}
return|return
name|countingSumScorer
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
return|;
block|}
comment|/** Throws an UnsupportedOperationException.    * TODO: Implement an explanation of the coordination factor.    * @param doc The document number for the explanation.    * @throws UnsupportedOperationException    */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|/* How to explain the coordination factor?     initCountingSumScorer();     return countingSumScorer.explain(doc); // misses coord factor.    */
block|}
block|}
end_class
end_unit
