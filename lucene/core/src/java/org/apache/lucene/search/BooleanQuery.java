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
name|ArrayList
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
name|Collection
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
name|EnumMap
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
name|Iterator
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxClauseCount must be>= 1"
argument_list|)
throw|;
block|}
name|BooleanQuery
operator|.
name|maxClauseCount
operator|=
name|maxClauseCount
expr_stmt|;
block|}
comment|/** A builder for boolean queries. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|disableCoord
specifier|private
name|boolean
name|disableCoord
decl_stmt|;
DECL|field|minimumNumberShouldMatch
specifier|private
name|int
name|minimumNumberShouldMatch
decl_stmt|;
DECL|field|clauses
specifier|private
specifier|final
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{}
comment|/**      * {@link Similarity#coord(int,int)} may be disabled in scoring, as      * appropriate. For example, this score factor does not make sense for most      * automatically generated queries, like {@link WildcardQuery} and {@link      * FuzzyQuery}.      */
DECL|method|setDisableCoord
specifier|public
name|Builder
name|setDisableCoord
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
return|return
name|this
return|;
block|}
comment|/**      * Specifies a minimum number of the optional BooleanClauses      * which must be satisfied.      *      *<p>      * By default no optional clauses are necessary for a match      * (unless there are no required clauses).  If this method is used,      * then the specified number of clauses is required.      *</p>      *<p>      * Use of this method is totally independent of specifying that      * any specific clauses are required (or prohibited).  This number will      * only be compared against the number of matching optional clauses.      *</p>      *      * @param min the number of optional clauses that must match      */
DECL|method|setMinimumNumberShouldMatch
specifier|public
name|Builder
name|setMinimumNumberShouldMatch
parameter_list|(
name|int
name|min
parameter_list|)
block|{
name|this
operator|.
name|minimumNumberShouldMatch
operator|=
name|min
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a new clause to this {@link Builder}. Note that the order in which      * clauses are added does not have any impact on matching documents or query      * performance.      * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number      */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|BooleanClause
name|clause
parameter_list|)
block|{
name|add
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|clause
operator|.
name|getOccur
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a new clause to this {@link Builder}. Note that the order in which      * clauses are added does not have any impact on matching documents or query      * performance.      * @throws TooManyClauses if the new number of clauses exceeds the maximum clause number      */
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|Query
name|query
parameter_list|,
name|Occur
name|occur
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
block|{
throw|throw
operator|new
name|TooManyClauses
argument_list|()
throw|;
block|}
name|clauses
operator|.
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
return|return
name|this
return|;
block|}
comment|/** Create a new {@link BooleanQuery} based on the parameters that have      *  been set on this builder. */
DECL|method|build
specifier|public
name|BooleanQuery
name|build
parameter_list|()
block|{
return|return
operator|new
name|BooleanQuery
argument_list|(
name|disableCoord
argument_list|,
name|minimumNumberShouldMatch
argument_list|,
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
argument_list|)
return|;
block|}
block|}
DECL|field|disableCoord
specifier|private
specifier|final
name|boolean
name|disableCoord
decl_stmt|;
DECL|field|minimumNumberShouldMatch
specifier|private
specifier|final
name|int
name|minimumNumberShouldMatch
decl_stmt|;
DECL|field|clauses
specifier|private
specifier|final
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
decl_stmt|;
comment|// used for toString() and getClauses()
DECL|field|clauseSets
specifier|private
specifier|final
name|Map
argument_list|<
name|Occur
argument_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
argument_list|>
name|clauseSets
decl_stmt|;
comment|// used for equals/hashcode
DECL|method|BooleanQuery
specifier|private
name|BooleanQuery
parameter_list|(
name|boolean
name|disableCoord
parameter_list|,
name|int
name|minimumNumberShouldMatch
parameter_list|,
name|BooleanClause
index|[]
name|clauses
parameter_list|)
block|{
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
name|this
operator|.
name|minimumNumberShouldMatch
operator|=
name|minimumNumberShouldMatch
expr_stmt|;
name|this
operator|.
name|clauses
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|clauses
argument_list|)
argument_list|)
expr_stmt|;
name|clauseSets
operator|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|Occur
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// duplicates matter for SHOULD and MUST
name|clauseSets
operator|.
name|put
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|,
operator|new
name|Multiset
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|clauseSets
operator|.
name|put
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|,
operator|new
name|Multiset
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
comment|// but not for FILTER and MUST_NOT
name|clauseSets
operator|.
name|put
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|clauseSets
operator|.
name|put
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|clauseSets
operator|.
name|get
argument_list|(
name|clause
operator|.
name|getOccur
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return whether the coord factor is disabled.    */
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
comment|/**    * Gets the minimum number of the optional BooleanClauses    * which must be satisfied.    */
DECL|method|getMinimumNumberShouldMatch
specifier|public
name|int
name|getMinimumNumberShouldMatch
parameter_list|()
block|{
return|return
name|minimumNumberShouldMatch
return|;
block|}
comment|/** Return a list of the clauses of this {@link BooleanQuery}. */
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
comment|/** Return the collection of queries for the given {@link Occur}. */
DECL|method|getClauses
name|Collection
argument_list|<
name|Query
argument_list|>
name|getClauses
parameter_list|(
name|Occur
name|occur
parameter_list|)
block|{
return|return
name|clauseSets
operator|.
name|get
argument_list|(
name|occur
argument_list|)
return|;
block|}
comment|/** Returns an iterator on the clauses in this query. It implements the {@link Iterable} interface to    * make it possible to do:    *<pre class="prettyprint">for (BooleanClause clause : booleanQuery) {}</pre>    */
annotation|@
name|Override
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
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|rewriteNoScoring
specifier|private
name|BooleanQuery
name|rewriteNoScoring
parameter_list|()
block|{
name|BooleanQuery
operator|.
name|Builder
name|newQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// ignore disableCoord, which only matters for scores
name|newQuery
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|MUST
condition|)
block|{
name|newQuery
operator|.
name|add
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newQuery
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newQuery
operator|.
name|build
argument_list|()
return|;
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
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|query
init|=
name|this
decl_stmt|;
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
name|query
operator|=
name|rewriteNoScoring
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|BooleanWeight
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|needsScores
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
comment|// optimize 1-clause queries
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
name|Query
name|query
init|=
name|c
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|minimumNumberShouldMatch
operator|==
literal|1
operator|&&
name|c
operator|.
name|getOccur
argument_list|()
operator|==
name|Occur
operator|.
name|SHOULD
condition|)
block|{
return|return
name|query
return|;
block|}
elseif|else
if|if
condition|(
name|minimumNumberShouldMatch
operator|==
literal|0
condition|)
block|{
switch|switch
condition|(
name|c
operator|.
name|getOccur
argument_list|()
condition|)
block|{
case|case
name|SHOULD
case|:
case|case
name|MUST
case|:
return|return
name|query
return|;
case|case
name|FILTER
case|:
comment|// no scoring clauses, so return a score of 0
return|return
operator|new
name|BoostQuery
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|query
argument_list|)
argument_list|,
literal|0
argument_list|)
return|;
case|case
name|MUST_NOT
case|:
comment|// no positive clauses
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
comment|// recursively rewrite
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDisableCoord
argument_list|(
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|actuallyRewritten
init|=
literal|false
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|this
control|)
block|{
name|Query
name|query
init|=
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Query
name|rewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|query
condition|)
block|{
name|actuallyRewritten
operator|=
literal|true
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|rewritten
argument_list|,
name|clause
operator|.
name|getOccur
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actuallyRewritten
condition|)
block|{
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|// remove duplicate FILTER and MUST_NOT clauses
block|{
name|int
name|clauseCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|queries
range|:
name|clauseSets
operator|.
name|values
argument_list|()
control|)
block|{
name|clauseCount
operator|+=
name|queries
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|clauseCount
operator|!=
name|clauses
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// since clauseSets implicitly deduplicates FILTER and MUST_NOT
comment|// clauses, this means there were duplicates
name|BooleanQuery
operator|.
name|Builder
name|rewritten
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|rewritten
operator|.
name|setDisableCoord
argument_list|(
name|disableCoord
argument_list|)
expr_stmt|;
name|rewritten
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|minimumNumberShouldMatch
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Occur
argument_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
argument_list|>
name|entry
range|:
name|clauseSets
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Occur
name|occur
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|rewritten
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rewritten
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|// remove FILTER clauses that are also MUST clauses
comment|// or that match all documents
if|if
condition|(
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|Set
argument_list|<
name|Query
argument_list|>
name|filters
init|=
operator|new
name|HashSet
argument_list|<
name|Query
argument_list|>
argument_list|(
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|modified
init|=
name|filters
operator|.
name|remove
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
decl_stmt|;
name|modified
operator||=
name|filters
operator|.
name|removeAll
argument_list|(
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|modified
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDisableCoord
argument_list|(
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|getOccur
argument_list|()
operator|!=
name|Occur
operator|.
name|FILTER
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Query
name|filter
range|:
name|filters
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|filter
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|// Rewrite queries whose single scoring clause is a MUST clause on a
comment|// MatchAllDocsQuery to a ConstantScoreQuery
block|{
specifier|final
name|Collection
argument_list|<
name|Query
argument_list|>
name|musts
init|=
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|Query
argument_list|>
name|filters
init|=
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|FILTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|musts
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|filters
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Query
name|must
init|=
name|musts
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|float
name|boost
init|=
literal|1f
decl_stmt|;
if|if
condition|(
name|must
operator|instanceof
name|BoostQuery
condition|)
block|{
name|BoostQuery
name|boostQuery
init|=
operator|(
name|BoostQuery
operator|)
name|must
decl_stmt|;
name|must
operator|=
name|boostQuery
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|boost
operator|=
name|boostQuery
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|must
operator|.
name|getClass
argument_list|()
operator|==
name|MatchAllDocsQuery
operator|.
name|class
condition|)
block|{
comment|// our single scoring clause matches everything: rewrite to a CSQ on the filter
comment|// ignore SHOULD clause for now
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
switch|switch
condition|(
name|clause
operator|.
name|getOccur
argument_list|()
condition|)
block|{
case|case
name|FILTER
case|:
case|case
name|MUST_NOT
case|:
name|builder
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// ignore
break|break;
block|}
block|}
name|Query
name|rewritten
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|rewritten
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|rewritten
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1f
condition|)
block|{
name|rewritten
operator|=
operator|new
name|BoostQuery
argument_list|(
name|rewritten
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
comment|// now add back the SHOULD clauses
name|builder
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|setDisableCoord
argument_list|(
name|isCoordDisabled
argument_list|()
argument_list|)
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|rewritten
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|clauseSets
operator|.
name|get
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|rewritten
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|rewritten
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
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
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
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
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|this
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|c
operator|.
name|getOccur
argument_list|()
operator|.
name|toString
argument_list|()
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
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|i
operator|+=
literal|1
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Compares the specified object with this boolean query for equality.    * Returns true if and only if the provided object<ul>    *<li>is also a {@link BooleanQuery},</li>    *<li>has the same value of {@link #isCoordDisabled()}</li>    *<li>has the same value of {@link #getMinimumNumberShouldMatch()}</li>    *<li>has the same {@link Occur#SHOULD} clauses, regardless of the order</li>    *<li>has the same {@link Occur#MUST} clauses, regardless of the order</li>    *<li>has the same set of {@link Occur#FILTER} clauses, regardless of the    * order and regardless of duplicates</li>    *<li>has the same set of {@link Occur#MUST_NOT} clauses, regardless of    * the order and regardless of duplicates</li></ul>    */
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
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BooleanQuery
name|that
init|=
operator|(
name|BooleanQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|==
name|that
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|&&
name|this
operator|.
name|disableCoord
operator|==
name|that
operator|.
name|disableCoord
operator|&&
name|clauseSets
operator|.
name|equals
argument_list|(
name|that
operator|.
name|clauseSets
argument_list|)
return|;
block|}
DECL|method|computeHashCode
specifier|private
name|int
name|computeHashCode
parameter_list|()
block|{
name|int
name|hashCode
init|=
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|Objects
operator|.
name|hash
argument_list|(
name|disableCoord
argument_list|,
name|minimumNumberShouldMatch
argument_list|,
name|clauseSets
argument_list|)
decl_stmt|;
if|if
condition|(
name|hashCode
operator|==
literal|0
condition|)
block|{
name|hashCode
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
comment|// cached hash code is ok since boolean queries are immutable
DECL|field|hashCode
specifier|private
name|int
name|hashCode
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hashCode
operator|==
literal|0
condition|)
block|{
comment|// no need for synchronization, in the worst case we would just compute the hash several times
name|hashCode
operator|=
name|computeHashCode
argument_list|()
expr_stmt|;
assert|assert
name|hashCode
operator|!=
literal|0
assert|;
block|}
assert|assert
name|hashCode
operator|==
name|computeHashCode
argument_list|()
assert|;
return|return
name|hashCode
return|;
block|}
block|}
end_class
end_unit
