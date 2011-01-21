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
name|IndexReader
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
name|util
operator|.
name|ToStringUtils
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
comment|/** A Query that matches documents matching boolean combinations of other   * queries, e.g. {@link TermQuery}s, {@link PhraseQuery}s or other   * BooleanQuerys.   */
end_comment
begin_class
DECL|class|BooleanQuery
specifier|public
class|class
name|BooleanQuery
extends|extends
name|Query
implements|implements
name|Iterable
argument_list|<
name|BooleanClause
argument_list|>
block|{
DECL|field|maxClauseCount
specifier|private
specifier|static
name|int
name|maxClauseCount
init|=
literal|1024
decl_stmt|;
comment|/** Thrown when an attempt is made to add more than {@link    * #getMaxClauseCount()} clauses. This typically happens if    * a PrefixQuery, FuzzyQuery, WildcardQuery, or TermRangeQuery     * is expanded to many terms during search.     */
DECL|class|TooManyClauses
specifier|public
specifier|static
class|class
name|TooManyClauses
extends|extends
name|RuntimeException
block|{
DECL|method|TooManyClauses
specifier|public
name|TooManyClauses
parameter_list|()
block|{
name|super
argument_list|(
literal|"maxClauseCount is set to "
operator|+
name|maxClauseCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Return the maximum number of clauses permitted, 1024 by default.    * Attempts to add more than the permitted number of clauses cause {@link    * TooManyClauses} to be thrown.    * @see #setMaxClauseCount(int)    */
DECL|method|getMaxClauseCount
specifier|public
specifier|static
name|int
name|getMaxClauseCount
parameter_list|()
block|{
return|return
name|maxClauseCount
return|;
block|}
comment|/**     * Set the maximum number of clauses permitted per BooleanQuery.    * Default value is 1024.    */
DECL|method|setMaxClauseCount
specifier|public
specifier|static
name|void
name|setMaxClauseCount
parameter_list|(
name|int
name|maxClauseCount
parameter_list|)
block|{
if|if
condition|(
name|maxClauseCount
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxClauseCount must be>= 1"
argument_list|)
throw|;
name|BooleanQuery
operator|.
name|maxClauseCount
operator|=
name|maxClauseCount
expr_stmt|;
block|}
DECL|field|clauses
specifier|private
name|ArrayList
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<
name|BooleanClause
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|disableCoord
specifier|private
specifier|final
name|boolean
name|disableCoord
decl_stmt|;
comment|/** Constructs an empty boolean query. */
DECL|method|BooleanQuery
specifier|public
name|BooleanQuery
parameter_list|()
block|{
name|disableCoord
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Constructs an empty boolean query.    *    * {@link Similarity#coord(int,int)} may be disabled in scoring, as    * appropriate. For example, this score factor does not make sense for most    * automatically generated queries, like {@link WildcardQuery} and {@link    * FuzzyQuery}.    *    * @param disableCoord disables {@link Similarity#coord(int,int)} in scoring.    */
DECL|method|BooleanQuery
specifier|public
name|BooleanQuery
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
block|{
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
block|}
comment|/** Returns true iff {@link Similarity#coord(int,int)} is disabled in    * scoring for this query instance.    * @see #BooleanQuery(boolean)    */
DECL|method|isCoordDisabled
specifier|public
name|boolean
name|isCoordDisabled
parameter_list|()
block|{
return|return
name|disableCoord
return|;
block|}
comment|/**    * Specifies a minimum number of the optional BooleanClauses    * which must be satisfied.    *    *<p>    * By default no optional clauses are necessary for a match    * (unless there are no required clauses).  If this method is used,    * then the specified number of clauses is required.    *</p>    *<p>    * Use of this method is totally independent of specifying that    * any specific clauses are required (or prohibited).  This number will    * only be compared against the number of matching optional clauses.    *</p>    *    * @param min the number of optional clauses that must match    */
DECL|method|setMinimumNumberShouldMatch
specifier|public
name|void
name|setMinimumNumberShouldMatch
parameter_list|(
name|int
name|min
parameter_list|)
block|{
name|this
operator|.
name|minNrShouldMatch
operator|=
name|min
expr_stmt|;
block|}
DECL|field|minNrShouldMatch
specifier|protected
name|int
name|minNrShouldMatch
init|=
literal|0
decl_stmt|;
comment|/**    * Gets the minimum number of the optional BooleanClauses    * which must be satisfied.    */
DECL|method|getMinimumNumberShouldMatch
specifier|public
name|int
name|getMinimumNumberShouldMatch
parameter_list|()
block|{
return|return
name|minNrShouldMatch
return|;
block|}
comment|/** Adds a clause to a boolean query.    *    * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number    * @see #getMaxClauseCount()    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Query
name|query
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|occur
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Adds a clause to a boolean query.    * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number    * @see #getMaxClauseCount()    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|BooleanClause
name|clause
parameter_list|)
block|{
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|>=
name|maxClauseCount
condition|)
throw|throw
operator|new
name|TooManyClauses
argument_list|()
throw|;
name|clauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the set of clauses in this query. */
DECL|method|getClauses
specifier|public
name|BooleanClause
index|[]
name|getClauses
parameter_list|()
block|{
return|return
name|clauses
operator|.
name|toArray
argument_list|(
operator|new
name|BooleanClause
index|[
name|clauses
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Returns the list of clauses in this query. */
DECL|method|clauses
specifier|public
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
parameter_list|()
block|{
return|return
name|clauses
return|;
block|}
comment|/** Returns an iterator on the clauses in this query. It implements the {@link Iterable} interface to    * make it possible to do:    *<pre>for (BooleanClause clause : booleanQuery) {}</pre>    */
DECL|method|iterator
specifier|public
specifier|final
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|clauses
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Expert: the Weight for BooleanQuery, used to    * normalize, score and explain these queries.    *    *<p>NOTE: this API and implementation is subject to    * change suddenly in the next release.</p>    */
DECL|class|BooleanWeight
specifier|protected
class|class
name|BooleanWeight
extends|extends
name|Weight
block|{
comment|/** The Similarity implementation. */
DECL|field|similarity
specifier|protected
name|Similarity
name|similarity
decl_stmt|;
DECL|field|weights
specifier|protected
name|ArrayList
argument_list|<
name|Weight
argument_list|>
name|weights
decl_stmt|;
DECL|field|maxCoord
specifier|protected
name|int
name|maxCoord
decl_stmt|;
comment|// num optional + num required
DECL|field|disableCoord
specifier|private
specifier|final
name|boolean
name|disableCoord
decl_stmt|;
DECL|method|BooleanWeight
specifier|public
name|BooleanWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
name|weights
operator|=
operator|new
name|ArrayList
argument_list|<
name|Weight
argument_list|>
argument_list|(
name|clauses
operator|.
name|size
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
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|weights
operator|.
name|add
argument_list|(
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
name|maxCoord
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|BooleanQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
literal|0.0f
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
name|weights
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// call sumOfSquaredWeights for all clauses in case of side effects
name|float
name|s
init|=
name|weights
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|sumOfSquaredWeights
argument_list|()
decl_stmt|;
comment|// sum sub weights
if|if
condition|(
operator|!
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isProhibited
argument_list|()
condition|)
comment|// only add to sum for non-prohibited clauses
name|sum
operator|+=
name|s
expr_stmt|;
block|}
name|sum
operator|*=
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
expr_stmt|;
comment|// boost each sub-weight
return|return
name|sum
return|;
block|}
DECL|method|coord
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
name|similarity
operator|.
name|coord
argument_list|(
name|overlap
argument_list|,
name|maxOverlap
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
name|norm
operator|*=
name|getBoost
argument_list|()
expr_stmt|;
comment|// incorporate boost
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
comment|// normalize all clauses, (even if prohibited in case of side affects)
name|w
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|minShouldMatch
init|=
name|BooleanQuery
operator|.
name|this
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
decl_stmt|;
name|ComplexExplanation
name|sumExpl
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|sumExpl
operator|.
name|setDescription
argument_list|(
literal|"sum of:"
argument_list|)
expr_stmt|;
name|int
name|coord
init|=
literal|0
decl_stmt|;
name|float
name|sum
init|=
literal|0.0f
decl_stmt|;
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
name|int
name|shouldMatchCount
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Weight
argument_list|>
name|wIter
init|=
name|weights
operator|.
name|iterator
argument_list|()
init|;
name|wIter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Weight
name|w
init|=
name|wIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|BooleanClause
name|c
init|=
name|cIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|ScorerContext
operator|.
name|def
argument_list|()
operator|.
name|scoreDocsInOrder
argument_list|(
literal|true
argument_list|)
operator|.
name|topScorer
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
name|Explanation
name|r
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no match on required clause ("
operator|+
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|sumExpl
operator|.
name|addDetail
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
name|Explanation
name|e
init|=
name|w
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|isMatch
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|sumExpl
operator|.
name|addDetail
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|e
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|coord
operator|++
expr_stmt|;
block|}
else|else
block|{
name|Explanation
name|r
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"match on prohibited clause ("
operator|+
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|r
operator|.
name|addDetail
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|addDetail
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|SHOULD
condition|)
name|shouldMatchCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|Explanation
name|r
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no match on required clause ("
operator|+
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|r
operator|.
name|addDetail
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|addDetail
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|fail
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fail
condition|)
block|{
name|sumExpl
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|setValue
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|setDescription
argument_list|(
literal|"Failure to meet condition(s) of required/prohibited clause(s)"
argument_list|)
expr_stmt|;
return|return
name|sumExpl
return|;
block|}
elseif|else
if|if
condition|(
name|shouldMatchCount
operator|<
name|minShouldMatch
condition|)
block|{
name|sumExpl
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|setValue
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|setDescription
argument_list|(
literal|"Failure to match minimum number "
operator|+
literal|"of optional clauses: "
operator|+
name|minShouldMatch
argument_list|)
expr_stmt|;
return|return
name|sumExpl
return|;
block|}
name|sumExpl
operator|.
name|setMatch
argument_list|(
literal|0
operator|<
name|coord
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|sumExpl
operator|.
name|setValue
argument_list|(
name|sum
argument_list|)
expr_stmt|;
specifier|final
name|float
name|coordFactor
init|=
name|disableCoord
condition|?
literal|1.0f
else|:
name|coord
argument_list|(
name|coord
argument_list|,
name|maxCoord
argument_list|)
decl_stmt|;
if|if
condition|(
name|coordFactor
operator|==
literal|1.0f
condition|)
block|{
return|return
name|sumExpl
return|;
comment|// eliminate wrapper
block|}
else|else
block|{
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|(
name|sumExpl
operator|.
name|isMatch
argument_list|()
argument_list|,
name|sum
operator|*
name|coordFactor
argument_list|,
literal|"product of:"
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|sumExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|coordFactor
argument_list|,
literal|"coord("
operator|+
name|coord
operator|+
literal|"/"
operator|+
name|maxCoord
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|ScorerContext
name|scorerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Scorer
argument_list|>
name|required
init|=
operator|new
name|ArrayList
argument_list|<
name|Scorer
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|prohibited
init|=
operator|new
name|ArrayList
argument_list|<
name|Scorer
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|optional
init|=
operator|new
name|ArrayList
argument_list|<
name|Scorer
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|BooleanClause
argument_list|>
name|cIter
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
name|BooleanClause
name|c
init|=
name|cIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|ScorerContext
operator|.
name|def
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|required
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|prohibited
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|optional
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Check if we can return a BooleanScorer
if|if
condition|(
operator|!
name|scorerContext
operator|.
name|scoreDocsInOrder
operator|&&
name|scorerContext
operator|.
name|topScorer
operator|&&
name|required
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|prohibited
operator|.
name|size
argument_list|()
operator|<
literal|32
condition|)
block|{
return|return
operator|new
name|BooleanScorer
argument_list|(
name|this
argument_list|,
name|disableCoord
argument_list|,
name|minNrShouldMatch
argument_list|,
name|optional
argument_list|,
name|prohibited
argument_list|,
name|maxCoord
argument_list|)
return|;
block|}
if|if
condition|(
name|required
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|optional
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// no required and optional clauses.
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|optional
operator|.
name|size
argument_list|()
operator|<
name|minNrShouldMatch
condition|)
block|{
comment|// either>1 req scorer, or there are 0 req scorers and at least 1
comment|// optional scorer. Therefore if there are not enough optional scorers
comment|// no documents will be matched by the query
return|return
literal|null
return|;
block|}
comment|// Return a BooleanScorer2
return|return
operator|new
name|BooleanScorer2
argument_list|(
name|this
argument_list|,
name|disableCoord
argument_list|,
name|minNrShouldMatch
argument_list|,
name|required
argument_list|,
name|prohibited
argument_list|,
name|optional
argument_list|,
name|maxCoord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
name|int
name|numProhibited
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
comment|// BS2 (in-order) will be used by scorer()
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
operator|++
name|numProhibited
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numProhibited
operator|>
literal|32
condition|)
block|{
comment|// cannot use BS
return|return
literal|false
return|;
block|}
comment|// scorer() will return an out-of-order scorer if requested.
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BooleanWeight
argument_list|(
name|searcher
argument_list|,
name|disableCoord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|minNrShouldMatch
operator|==
literal|0
operator|&&
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize 1-clause queries
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
comment|// just return clause
name|Query
name|query
init|=
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// rewrite first
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
comment|// incorporate boost
if|if
condition|(
name|query
operator|==
name|c
operator|.
name|getQuery
argument_list|()
condition|)
comment|// if rewrite was no-op
name|query
operator|=
operator|(
name|Query
operator|)
name|query
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// then clone before boost
name|query
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
operator|*
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
block|}
name|BooleanQuery
name|clone
init|=
literal|null
decl_stmt|;
comment|// recursively rewrite
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
name|c
operator|.
name|getQuery
argument_list|()
condition|)
block|{
comment|// clause rewrote: must clone
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
operator|(
name|BooleanQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|clauses
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|new
name|BooleanClause
argument_list|(
name|query
argument_list|,
name|c
operator|.
name|getOccur
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|clause
operator|.
name|getQuery
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|BooleanQuery
name|clone
init|=
operator|(
name|BooleanQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|clauses
operator|=
operator|(
name|ArrayList
argument_list|<
name|BooleanClause
argument_list|>
operator|)
name|this
operator|.
name|clauses
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|needParens
init|=
operator|(
name|getBoost
argument_list|()
operator|!=
literal|1.0
operator|)
operator|||
operator|(
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
operator|)
decl_stmt|;
if|if
condition|(
name|needParens
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"("
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
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|c
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|"+"
argument_list|)
expr_stmt|;
name|Query
name|subQuery
init|=
name|c
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|subQuery
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|subQuery
operator|instanceof
name|BooleanQuery
condition|)
block|{
comment|// wrap sub-bools in parens
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|subQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|subQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|!=
name|clauses
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|needParens
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'~'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|BooleanQuery
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanQuery
name|other
init|=
operator|(
name|BooleanQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
name|this
operator|.
name|clauses
operator|.
name|equals
argument_list|(
name|other
operator|.
name|clauses
argument_list|)
operator|&&
name|this
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|==
name|other
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|&&
name|this
operator|.
name|disableCoord
operator|==
name|other
operator|.
name|disableCoord
return|;
block|}
comment|/** Returns a hash code value for this object.*/
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|clauses
operator|.
name|hashCode
argument_list|()
operator|+
name|getMinimumNumberShouldMatch
argument_list|()
operator|+
operator|(
name|disableCoord
condition|?
literal|17
else|:
literal|0
operator|)
return|;
block|}
block|}
end_class
end_unit
