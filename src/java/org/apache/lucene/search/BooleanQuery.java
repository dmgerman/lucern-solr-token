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
comment|/**  * Copyright 2004-2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
block|{
comment|/**     */
DECL|field|maxClauseCount
specifier|private
specifier|static
name|int
name|maxClauseCount
init|=
literal|1024
decl_stmt|;
comment|/** Thrown when an attempt is made to add more than {@link    * #getMaxClauseCount()} clauses. This typically happens if    * a PrefixQuery, FuzzyQuery, WildcardQuery, or RangeQuery     * is expanded to many terms during search.     */
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
block|{}
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
literal|"maxClauseCount is set to "
operator|+
name|maxClauseCount
return|;
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
comment|/** Set the maximum number of clauses permitted per BooleanQuery.    * Default value is 1024.    *<p>TermQuery clauses are generated from for example prefix queries and    * fuzzy queries. Each TermQuery needs some buffer space during search,    * so this parameter indirectly controls the maximum buffer requirements for    * query search.    *<p>When this parameter becomes a bottleneck for a Query one can use a    * Filter. For example instead of a {@link RangeQuery} one can use a    * {@link RangeFilter}.    *<p>Normally the buffers are allocated by the JVM. When using for example    * {@link org.apache.lucene.store.MMapDirectory} the buffering is left to    * the operating system.    */
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
name|Vector
name|clauses
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|disableCoord
specifier|private
name|boolean
name|disableCoord
decl_stmt|;
comment|/** Constructs an empty boolean query. */
DECL|method|BooleanQuery
specifier|public
name|BooleanQuery
parameter_list|()
block|{}
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
comment|// Implement coord disabling.
comment|// Inherit javadoc.
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
block|{
name|Similarity
name|result
init|=
name|super
operator|.
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableCoord
condition|)
block|{
comment|// disable coord as requested
name|result
operator|=
operator|new
name|SimilarityDelegator
argument_list|(
name|result
argument_list|)
block|{
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
literal|1.0f
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Specifies a minimum number of the optional BooleanClauses    * which must be satisifed.    *    *<p>    * By default no optional clauses are neccessary for a match    * (unless there are no required clauses).  If this method is used,    * then the specified numebr of clauses is required.    *</p>    *<p>    * Use of this method is totally independant of specifying that    * any specific clauses are required (or prohibited).  This number will    * only be compared against the number of matching optional clauses.    *</p>    *<p>    * EXPERT NOTE: Using this method will force the use of BooleanWeight2,    * regardless of wether setUseScorer14(true) has been called.    *</p>    *    * @param min the number of optional clauses that must match    * @see #setUseScorer14    */
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
comment|/**    * Gets the minimum number of the optional BooleanClauses    * which must be satisifed.    */
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
name|addElement
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
operator|(
name|BooleanClause
index|[]
operator|)
name|clauses
operator|.
name|toArray
argument_list|(
operator|new
name|BooleanClause
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|class|BooleanWeight
specifier|private
class|class
name|BooleanWeight
implements|implements
name|Weight
block|{
DECL|field|similarity
specifier|protected
name|Similarity
name|similarity
decl_stmt|;
DECL|field|weights
specifier|protected
name|Vector
name|weights
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|method|BooleanWeight
specifier|public
name|BooleanWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|similarity
operator|=
name|getSimilarity
argument_list|(
name|searcher
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
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
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
block|}
block|}
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// call sumOfSquaredWeights for all clauses in case of side effects
name|float
name|s
init|=
name|w
operator|.
name|sumOfSquaredWeights
argument_list|()
decl_stmt|;
comment|// sum sub weights
if|if
condition|(
operator|!
name|c
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
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
comment|/** @return A good old 1.4 Scorer */
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First see if the (faster) ConjunctionScorer will work.  This can be
comment|// used when all clauses are required.  Also, at this point a
comment|// BooleanScorer cannot be embedded in a ConjunctionScorer, as the hits
comment|// from a BooleanScorer are not always sorted by document number (sigh)
comment|// and hence BooleanScorer cannot implement skipTo() correctly, which is
comment|// required by ConjunctionScorer.
name|boolean
name|allRequired
init|=
literal|true
decl_stmt|;
name|boolean
name|noneBoolean
init|=
literal|true
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isRequired
argument_list|()
condition|)
name|allRequired
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|BooleanQuery
condition|)
name|noneBoolean
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|allRequired
operator|&&
name|noneBoolean
condition|)
block|{
comment|// ConjunctionScorer is okay
name|ConjunctionScorer
name|result
init|=
operator|new
name|ConjunctionScorer
argument_list|(
name|similarity
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
name|weights
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|result
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// Use good-old BooleanScorer instead.
name|BooleanScorer
name|result
init|=
operator|new
name|BooleanScorer
argument_list|(
name|similarity
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
name|weights
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
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|!=
literal|null
condition|)
name|result
operator|.
name|add
argument_list|(
name|subScorer
argument_list|,
name|c
operator|.
name|isRequired
argument_list|()
argument_list|,
name|c
operator|.
name|isProhibited
argument_list|()
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
return|return
literal|null
return|;
block|}
return|return
name|result
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
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
name|int
name|maxCoord
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
name|BooleanClause
name|c
init|=
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Explanation
name|e
init|=
name|w
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
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
name|maxCoord
operator|++
expr_stmt|;
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
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
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
name|float
name|coordFactor
init|=
name|similarity
operator|.
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
comment|// coord is no-op
return|return
name|sumExpl
return|;
comment|// eliminate wrapper
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
block|}
DECL|class|BooleanWeight2
specifier|private
class|class
name|BooleanWeight2
extends|extends
name|BooleanWeight
block|{
comment|/* Merge into BooleanWeight in case the 1.4 BooleanScorer is dropped */
DECL|method|BooleanWeight2
specifier|public
name|BooleanWeight2
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|/** @return An alternative Scorer that uses and provides skipTo(),      *          and scores documents in document number order.      */
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanScorer2
name|result
init|=
operator|new
name|BooleanScorer2
argument_list|(
name|similarity
argument_list|,
name|minNrShouldMatch
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
name|weights
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
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
operator|(
name|Weight
operator|)
name|weights
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|!=
literal|null
condition|)
name|result
operator|.
name|add
argument_list|(
name|subScorer
argument_list|,
name|c
operator|.
name|isRequired
argument_list|()
argument_list|,
name|c
operator|.
name|isProhibited
argument_list|()
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
return|return
literal|null
return|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/** Indicates whether to use good old 1.4 BooleanScorer. */
DECL|field|useScorer14
specifier|private
specifier|static
name|boolean
name|useScorer14
init|=
literal|false
decl_stmt|;
DECL|method|setUseScorer14
specifier|public
specifier|static
name|void
name|setUseScorer14
parameter_list|(
name|boolean
name|use14
parameter_list|)
block|{
name|useScorer14
operator|=
name|use14
expr_stmt|;
block|}
DECL|method|getUseScorer14
specifier|public
specifier|static
name|boolean
name|getUseScorer14
parameter_list|()
block|{
return|return
name|useScorer14
return|;
block|}
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|0
operator|<
name|minNrShouldMatch
condition|)
block|{
comment|// :TODO: should we throw an exception if getUseScorer14 ?
return|return
operator|new
name|BooleanWeight2
argument_list|(
name|searcher
argument_list|)
return|;
block|}
return|return
name|getUseScorer14
argument_list|()
condition|?
operator|(
name|Weight
operator|)
operator|new
name|BooleanWeight
argument_list|(
name|searcher
argument_list|)
else|:
operator|(
name|Weight
operator|)
operator|new
name|BooleanWeight2
argument_list|(
name|searcher
argument_list|)
return|;
block|}
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
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
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
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
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
name|setElementAt
argument_list|(
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
argument_list|,
name|i
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
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
name|terms
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|clauses
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|BooleanClause
name|clause
init|=
operator|(
name|BooleanClause
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|Vector
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
operator|(
name|BooleanClause
operator|)
name|clauses
operator|.
name|elementAt
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
name|c
operator|.
name|getQuery
argument_list|()
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
name|buffer
operator|.
name|append
argument_list|(
name|c
operator|.
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
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
return|;
block|}
comment|/** Returns a hash code value for this object.*/
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
return|;
block|}
block|}
end_class
end_unit
