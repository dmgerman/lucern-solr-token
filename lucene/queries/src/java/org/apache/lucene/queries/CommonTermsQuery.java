begin_unit
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|Fields
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
name|index
operator|.
name|TermContext
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
name|Terms
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
name|TermsEnum
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
name|BooleanQuery
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
name|Query
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
name|TermQuery
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
name|Set
import|;
end_import
begin_comment
comment|/**  * A query that executes high-frequency terms in a optional sub-query to prevent  * slow queries due to "common" terms like stopwords. This query  * builds 2 queries off the {@link #add(Term) added} terms: low-frequency  * terms are added to a required boolean clause and high-frequency terms are  * added to an optional boolean clause. The optional clause is only executed if  * the required "low-frequency" clause matches. Scores produced by this query  * will be slightly different than plain {@link BooleanQuery} scorer mainly due to  * differences in the {@link Similarity#coord(int,int) number of leaf queries}  * in the required boolean clause. In most cases, high-frequency terms are  * unlikely to significantly contribute to the document score unless at least  * one of the low-frequency terms are matched.  This query can improve  * query execution times significantly if applicable.  *<p>  * {@link CommonTermsQuery} has several advantages over stopword filtering at  * index or query time since a term can be "classified" based on the actual  * document frequency in the index and can prevent slow queries even across  * domains without specialized stopword files.  *</p>  *<p>  *<b>Note:</b> if the query only contains high-frequency terms the query is  * rewritten into a plain conjunction query ie. all high-frequency terms need to  * match in order to match a document.  *</p>  */
end_comment
begin_class
DECL|class|CommonTermsQuery
specifier|public
class|class
name|CommonTermsQuery
extends|extends
name|Query
block|{
comment|/*    * TODO maybe it would make sense to abstract this even further and allow to    * rewrite to dismax rather than boolean. Yet, this can already be subclassed    * to do so.    */
DECL|field|terms
specifier|protected
specifier|final
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|disableCoord
specifier|protected
specifier|final
name|boolean
name|disableCoord
decl_stmt|;
DECL|field|maxTermFrequency
specifier|protected
specifier|final
name|float
name|maxTermFrequency
decl_stmt|;
DECL|field|lowFreqOccur
specifier|protected
specifier|final
name|Occur
name|lowFreqOccur
decl_stmt|;
DECL|field|highFreqOccur
specifier|protected
specifier|final
name|Occur
name|highFreqOccur
decl_stmt|;
DECL|field|lowFreqBoost
specifier|protected
name|float
name|lowFreqBoost
init|=
literal|1.0f
decl_stmt|;
DECL|field|highFreqBoost
specifier|protected
name|float
name|highFreqBoost
init|=
literal|1.0f
decl_stmt|;
DECL|field|lowFreqMinNrShouldMatch
specifier|protected
name|float
name|lowFreqMinNrShouldMatch
init|=
literal|0
decl_stmt|;
DECL|field|highFreqMinNrShouldMatch
specifier|protected
name|float
name|highFreqMinNrShouldMatch
init|=
literal|0
decl_stmt|;
comment|/**    * Creates a new {@link CommonTermsQuery}    *     * @param highFreqOccur    *          {@link Occur} used for high frequency terms    * @param lowFreqOccur    *          {@link Occur} used for low frequency terms    * @param maxTermFrequency    *          a value in [0..1) (or absolute number>=1) representing the    *          maximum threshold of a terms document frequency to be considered a    *          low frequency term.    * @throws IllegalArgumentException    *           if {@link Occur#MUST_NOT} is pass as lowFreqOccur or    *           highFreqOccur    */
DECL|method|CommonTermsQuery
specifier|public
name|CommonTermsQuery
parameter_list|(
name|Occur
name|highFreqOccur
parameter_list|,
name|Occur
name|lowFreqOccur
parameter_list|,
name|float
name|maxTermFrequency
parameter_list|)
block|{
name|this
argument_list|(
name|highFreqOccur
argument_list|,
name|lowFreqOccur
argument_list|,
name|maxTermFrequency
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link CommonTermsQuery}    *     * @param highFreqOccur    *          {@link Occur} used for high frequency terms    * @param lowFreqOccur    *          {@link Occur} used for low frequency terms    * @param maxTermFrequency    *          a value in [0..1) (or absolute number>=1) representing the    *          maximum threshold of a terms document frequency to be considered a    *          low frequency term.    * @param disableCoord    *          disables {@link Similarity#coord(int,int)} in scoring for the low    *          / high frequency sub-queries    * @throws IllegalArgumentException    *           if {@link Occur#MUST_NOT} is pass as lowFreqOccur or    *           highFreqOccur    */
DECL|method|CommonTermsQuery
specifier|public
name|CommonTermsQuery
parameter_list|(
name|Occur
name|highFreqOccur
parameter_list|,
name|Occur
name|lowFreqOccur
parameter_list|,
name|float
name|maxTermFrequency
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
block|{
if|if
condition|(
name|highFreqOccur
operator|==
name|Occur
operator|.
name|MUST_NOT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"highFreqOccur should be MUST or SHOULD but was MUST_NOT"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowFreqOccur
operator|==
name|Occur
operator|.
name|MUST_NOT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowFreqOccur should be MUST or SHOULD but was MUST_NOT"
argument_list|)
throw|;
block|}
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
name|this
operator|.
name|highFreqOccur
operator|=
name|highFreqOccur
expr_stmt|;
name|this
operator|.
name|lowFreqOccur
operator|=
name|lowFreqOccur
expr_stmt|;
name|this
operator|.
name|maxTermFrequency
operator|=
name|maxTermFrequency
expr_stmt|;
block|}
comment|/**    * Adds a term to the {@link CommonTermsQuery}    *     * @param term    *          the term to add    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Term must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|terms
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|BooleanQuery
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|Query
name|tq
init|=
name|newTermQuery
argument_list|(
name|this
operator|.
name|terms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|tq
return|;
block|}
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|TermContext
index|[]
name|contextArray
init|=
operator|new
name|TermContext
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|Term
index|[]
name|queryTerms
init|=
name|this
operator|.
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|collectTermContext
argument_list|(
name|reader
argument_list|,
name|leaves
argument_list|,
name|contextArray
argument_list|,
name|queryTerms
argument_list|)
expr_stmt|;
return|return
name|buildQuery
argument_list|(
name|maxDoc
argument_list|,
name|contextArray
argument_list|,
name|queryTerms
argument_list|)
return|;
block|}
DECL|method|calcLowFreqMinimumNumberShouldMatch
specifier|protected
name|int
name|calcLowFreqMinimumNumberShouldMatch
parameter_list|(
name|int
name|numOptional
parameter_list|)
block|{
return|return
name|minNrShouldMatch
argument_list|(
name|lowFreqMinNrShouldMatch
argument_list|,
name|numOptional
argument_list|)
return|;
block|}
DECL|method|calcHighFreqMinimumNumberShouldMatch
specifier|protected
name|int
name|calcHighFreqMinimumNumberShouldMatch
parameter_list|(
name|int
name|numOptional
parameter_list|)
block|{
return|return
name|minNrShouldMatch
argument_list|(
name|highFreqMinNrShouldMatch
argument_list|,
name|numOptional
argument_list|)
return|;
block|}
DECL|method|minNrShouldMatch
specifier|private
specifier|final
name|int
name|minNrShouldMatch
parameter_list|(
name|float
name|minNrShouldMatch
parameter_list|,
name|int
name|numOptional
parameter_list|)
block|{
if|if
condition|(
name|minNrShouldMatch
operator|>=
literal|1.0f
operator|||
name|minNrShouldMatch
operator|==
literal|0.0f
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|minNrShouldMatch
return|;
block|}
return|return
name|Math
operator|.
name|round
argument_list|(
name|minNrShouldMatch
operator|*
name|numOptional
argument_list|)
return|;
block|}
DECL|method|buildQuery
specifier|protected
name|Query
name|buildQuery
parameter_list|(
specifier|final
name|int
name|maxDoc
parameter_list|,
specifier|final
name|TermContext
index|[]
name|contextArray
parameter_list|,
specifier|final
name|Term
index|[]
name|queryTerms
parameter_list|)
block|{
name|BooleanQuery
name|lowFreq
init|=
operator|new
name|BooleanQuery
argument_list|(
name|disableCoord
argument_list|)
decl_stmt|;
name|BooleanQuery
name|highFreq
init|=
operator|new
name|BooleanQuery
argument_list|(
name|disableCoord
argument_list|)
decl_stmt|;
name|highFreq
operator|.
name|setBoost
argument_list|(
name|highFreqBoost
argument_list|)
expr_stmt|;
name|lowFreq
operator|.
name|setBoost
argument_list|(
name|lowFreqBoost
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
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
name|queryTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermContext
name|termContext
init|=
name|contextArray
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|termContext
operator|==
literal|null
condition|)
block|{
name|lowFreq
operator|.
name|add
argument_list|(
name|newTermQuery
argument_list|(
name|queryTerms
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|)
argument_list|,
name|lowFreqOccur
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|(
name|maxTermFrequency
operator|>=
literal|1f
operator|&&
name|termContext
operator|.
name|docFreq
argument_list|()
operator|>
name|maxTermFrequency
operator|)
operator|||
operator|(
name|termContext
operator|.
name|docFreq
argument_list|()
operator|>
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|maxTermFrequency
operator|*
operator|(
name|float
operator|)
name|maxDoc
argument_list|)
operator|)
condition|)
block|{
name|highFreq
operator|.
name|add
argument_list|(
name|newTermQuery
argument_list|(
name|queryTerms
index|[
name|i
index|]
argument_list|,
name|termContext
argument_list|)
argument_list|,
name|highFreqOccur
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lowFreq
operator|.
name|add
argument_list|(
name|newTermQuery
argument_list|(
name|queryTerms
index|[
name|i
index|]
argument_list|,
name|termContext
argument_list|)
argument_list|,
name|lowFreqOccur
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|int
name|numLowFreqClauses
init|=
name|lowFreq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numHighFreqClauses
init|=
name|highFreq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowFreqOccur
operator|==
name|Occur
operator|.
name|SHOULD
operator|&&
name|numLowFreqClauses
operator|>
literal|0
condition|)
block|{
name|int
name|minMustMatch
init|=
name|calcLowFreqMinimumNumberShouldMatch
argument_list|(
name|numLowFreqClauses
argument_list|)
decl_stmt|;
name|lowFreq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|minMustMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|highFreqOccur
operator|==
name|Occur
operator|.
name|SHOULD
operator|&&
name|numHighFreqClauses
operator|>
literal|0
condition|)
block|{
name|int
name|minMustMatch
init|=
name|calcHighFreqMinimumNumberShouldMatch
argument_list|(
name|numHighFreqClauses
argument_list|)
decl_stmt|;
name|highFreq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|minMustMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lowFreq
operator|.
name|clauses
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|/*        * if lowFreq is empty we rewrite the high freq terms in a conjunction to        * prevent slow queries.        */
if|if
condition|(
name|highFreq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|==
literal|0
operator|&&
name|highFreqOccur
operator|!=
name|Occur
operator|.
name|MUST
condition|)
block|{
for|for
control|(
name|BooleanClause
name|booleanClause
range|:
name|highFreq
control|)
block|{
name|booleanClause
operator|.
name|setOccur
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
block|}
name|highFreq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|highFreq
return|;
block|}
elseif|else
if|if
condition|(
name|highFreq
operator|.
name|clauses
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// only do low freq terms - we don't have high freq terms
name|lowFreq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lowFreq
return|;
block|}
else|else
block|{
name|query
operator|.
name|add
argument_list|(
name|highFreq
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|lowFreq
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
DECL|method|collectTermContext
specifier|public
name|void
name|collectTermContext
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|,
name|TermContext
index|[]
name|contextArray
parameter_list|,
name|Term
index|[]
name|queryTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|context
range|:
name|leaves
control|)
block|{
specifier|final
name|Fields
name|fields
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// reader has no fields
continue|continue;
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
name|queryTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|term
init|=
name|queryTerms
index|[
name|i
index|]
decl_stmt|;
name|TermContext
name|termContext
init|=
name|contextArray
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// field does not exist
continue|continue;
block|}
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
if|if
condition|(
name|termsEnum
operator|==
name|TermsEnum
operator|.
name|EMPTY
condition|)
continue|continue;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|termContext
operator|==
literal|null
condition|)
block|{
name|contextArray
index|[
name|i
index|]
operator|=
operator|new
name|TermContext
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|termState
argument_list|()
argument_list|,
name|context
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termContext
operator|.
name|register
argument_list|(
name|termsEnum
operator|.
name|termState
argument_list|()
argument_list|,
name|context
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Returns true iff {@link Similarity#coord(int,int)} is disabled in scoring    * for the high and low frequency query instance. The top level query will    * always disable coords.    */
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
comment|/**    * Specifies a minimum number of the low frequent optional BooleanClauses which must be    * satisfied in order to produce a match on the low frequency terms query    * part. This method accepts a float value in the range [0..1) as a fraction    * of the actual query terms in the low frequent clause or a number    *<tt>&gt;=1</tt> as an absolut number of clauses that need to match.    *     *<p>    * By default no optional clauses are necessary for a match (unless there are    * no required clauses). If this method is used, then the specified number of    * clauses is required.    *</p>    *     * @param min    *          the number of optional clauses that must match    */
DECL|method|setLowFreqMinimumNumberShouldMatch
specifier|public
name|void
name|setLowFreqMinimumNumberShouldMatch
parameter_list|(
name|float
name|min
parameter_list|)
block|{
name|this
operator|.
name|lowFreqMinNrShouldMatch
operator|=
name|min
expr_stmt|;
block|}
comment|/**    * Gets the minimum number of the optional low frequent BooleanClauses which must be    * satisfied.    */
DECL|method|getLowFreqMinimumNumberShouldMatch
specifier|public
name|float
name|getLowFreqMinimumNumberShouldMatch
parameter_list|()
block|{
return|return
name|lowFreqMinNrShouldMatch
return|;
block|}
comment|/**    * Specifies a minimum number of the high frequent optional BooleanClauses which must be    * satisfied in order to produce a match on the low frequency terms query    * part. This method accepts a float value in the range [0..1) as a fraction    * of the actual query terms in the low frequent clause or a number    *<tt>&gt;=1</tt> as an absolut number of clauses that need to match.    *     *<p>    * By default no optional clauses are necessary for a match (unless there are    * no required clauses). If this method is used, then the specified number of    * clauses is required.    *</p>    *     * @param min    *          the number of optional clauses that must match    */
DECL|method|setHighFreqMinimumNumberShouldMatch
specifier|public
name|void
name|setHighFreqMinimumNumberShouldMatch
parameter_list|(
name|float
name|min
parameter_list|)
block|{
name|this
operator|.
name|highFreqMinNrShouldMatch
operator|=
name|min
expr_stmt|;
block|}
comment|/**    * Gets the minimum number of the optional high frequent BooleanClauses which must be    * satisfied.    */
DECL|method|getHighFreqMinimumNumberShouldMatch
specifier|public
name|float
name|getHighFreqMinimumNumberShouldMatch
parameter_list|()
block|{
return|return
name|highFreqMinNrShouldMatch
return|;
block|}
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
name|terms
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|terms
argument_list|)
expr_stmt|;
block|}
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
name|getLowFreqMinimumNumberShouldMatch
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
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|t
init|=
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|newTermQuery
argument_list|(
name|t
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|terms
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
literal|", "
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
name|getLowFreqMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
operator|||
name|getHighFreqMinimumNumberShouldMatch
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
literal|"("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getLowFreqMinimumNumberShouldMatch
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getHighFreqMinimumNumberShouldMatch
argument_list|()
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
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|disableCoord
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|highFreqBoost
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|highFreqOccur
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|highFreqOccur
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|lowFreqBoost
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|lowFreqOccur
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|lowFreqOccur
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|maxTermFrequency
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|lowFreqMinNrShouldMatch
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|highFreqMinNrShouldMatch
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|terms
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|terms
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|CommonTermsQuery
name|other
init|=
operator|(
name|CommonTermsQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|disableCoord
operator|!=
name|other
operator|.
name|disableCoord
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|highFreqBoost
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|highFreqBoost
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|highFreqOccur
operator|!=
name|other
operator|.
name|highFreqOccur
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|lowFreqBoost
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|lowFreqBoost
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lowFreqOccur
operator|!=
name|other
operator|.
name|lowFreqOccur
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|maxTermFrequency
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|maxTermFrequency
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|lowFreqMinNrShouldMatch
operator|!=
name|other
operator|.
name|lowFreqMinNrShouldMatch
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|highFreqMinNrShouldMatch
operator|!=
name|other
operator|.
name|highFreqMinNrShouldMatch
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|terms
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|terms
operator|.
name|equals
argument_list|(
name|other
operator|.
name|terms
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/**    * Builds a new TermQuery instance.    *<p>This is intended for subclasses that wish to customize the generated queries.</p>    * @param term term    * @param context the TermContext to be used to create the low level term query. Can be<code>null</code>.    * @return new TermQuery instance    */
DECL|method|newTermQuery
specifier|protected
name|Query
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|TermContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|==
literal|null
condition|?
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
else|:
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
end_class
end_unit
