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
name|Set
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|ComplexExplanation
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
name|Explanation
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
name|Weight
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
name|search
operator|.
name|IndexSearcher
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
name|Bits
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
begin_comment
comment|/**  * Query that sets document score as a programmatic function of several (sub) scores:  *<ol>  *<li>the score of its subQuery (any query)</li>  *<li>(optional) the score of its {@link FunctionQuery} (or queries).</li>  *</ol>  * Subclasses can modify the computation by overriding {@link #getCustomScoreProvider}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CustomScoreQuery
specifier|public
class|class
name|CustomScoreQuery
extends|extends
name|Query
block|{
DECL|field|subQuery
specifier|private
name|Query
name|subQuery
decl_stmt|;
DECL|field|scoringQueries
specifier|private
name|Query
index|[]
name|scoringQueries
decl_stmt|;
comment|// never null (empty array if there are no valSrcQueries).
DECL|field|strict
specifier|private
name|boolean
name|strict
init|=
literal|false
decl_stmt|;
comment|// if true, valueSource part of query does not take part in weights normalization.
comment|/**    * Create a CustomScoreQuery over input subQuery.    * @param subQuery the sub query whose scored is being customized. Must not be null.     */
DECL|method|CustomScoreQuery
specifier|public
name|CustomScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|)
block|{
name|this
argument_list|(
name|subQuery
argument_list|,
operator|new
name|FunctionQuery
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a CustomScoreQuery over input subQuery and a {@link org.apache.lucene.queries.function.FunctionQuery}.    * @param subQuery the sub query whose score is being customized. Must not be null.    * @param scoringQuery a value source query whose scores are used in the custom score    * computation.  This parameter is optional - it can be null.    */
DECL|method|CustomScoreQuery
specifier|public
name|CustomScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|FunctionQuery
name|scoringQuery
parameter_list|)
block|{
name|this
argument_list|(
name|subQuery
argument_list|,
name|scoringQuery
operator|!=
literal|null
condition|?
comment|// don't want an array that contains a single null..
operator|new
name|FunctionQuery
index|[]
block|{
name|scoringQuery
block|}
else|:
operator|new
name|FunctionQuery
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a CustomScoreQuery over input subQuery and a {@link org.apache.lucene.queries.function.FunctionQuery}.    * @param subQuery the sub query whose score is being customized. Must not be null.    * @param scoringQueries value source queries whose scores are used in the custom score    * computation.  This parameter is optional - it can be null or even an empty array.    */
DECL|method|CustomScoreQuery
specifier|public
name|CustomScoreQuery
parameter_list|(
name|Query
name|subQuery
parameter_list|,
name|FunctionQuery
modifier|...
name|scoringQueries
parameter_list|)
block|{
name|this
operator|.
name|subQuery
operator|=
name|subQuery
expr_stmt|;
name|this
operator|.
name|scoringQueries
operator|=
name|scoringQueries
operator|!=
literal|null
condition|?
name|scoringQueries
else|:
operator|new
name|Query
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|subQuery
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"<subquery> must not be null!"
argument_list|)
throw|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#rewrite(org.apache.lucene.index.IndexReader) */
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
name|CustomScoreQuery
name|clone
init|=
literal|null
decl_stmt|;
specifier|final
name|Query
name|sq
init|=
name|subQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|sq
operator|!=
name|subQuery
condition|)
block|{
name|clone
operator|=
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|subQuery
operator|=
name|sq
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
name|scoringQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Query
name|v
init|=
name|scoringQueries
index|[
name|i
index|]
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
name|scoringQueries
index|[
name|i
index|]
condition|)
block|{
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|scoringQueries
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
block|}
block|}
return|return
operator|(
name|clone
operator|==
literal|null
operator|)
condition|?
name|this
else|:
name|clone
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#extractTerms(java.util.Set) */
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
name|subQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
for|for
control|(
name|Query
name|scoringQuery
range|:
name|scoringQueries
control|)
block|{
name|scoringQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Query#clone() */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|CustomScoreQuery
name|clone
parameter_list|()
block|{
name|CustomScoreQuery
name|clone
init|=
operator|(
name|CustomScoreQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|subQuery
operator|=
name|subQuery
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|scoringQueries
operator|=
operator|new
name|Query
index|[
name|scoringQueries
operator|.
name|length
index|]
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
name|scoringQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|clone
operator|.
name|scoringQueries
index|[
name|i
index|]
operator|=
name|scoringQueries
index|[
name|i
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
comment|/* (non-Javadoc) @see org.apache.lucene.search.Query#toString(java.lang.String) */
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
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
decl_stmt|;
name|sb
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
for|for
control|(
name|Query
name|scoringQuery
range|:
name|scoringQueries
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|scoringQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|strict
condition|?
literal|" STRICT"
else|:
literal|""
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|+
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
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
name|this
operator|==
name|o
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
name|o
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
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CustomScoreQuery
name|other
init|=
operator|(
name|CustomScoreQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getBoost
argument_list|()
operator|!=
name|other
operator|.
name|getBoost
argument_list|()
operator|||
operator|!
name|this
operator|.
name|subQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|subQuery
argument_list|)
operator|||
name|this
operator|.
name|strict
operator|!=
name|other
operator|.
name|strict
operator|||
name|this
operator|.
name|scoringQueries
operator|.
name|length
operator|!=
name|other
operator|.
name|scoringQueries
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|scoringQueries
argument_list|,
name|other
operator|.
name|scoringQueries
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|subQuery
operator|.
name|hashCode
argument_list|()
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|scoringQueries
argument_list|)
operator|)
operator|^
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
operator|(
name|strict
condition|?
literal|1234
else|:
literal|4321
operator|)
return|;
block|}
comment|/**    * Returns a {@link CustomScoreProvider} that calculates the custom scores    * for the given {@link IndexReader}. The default implementation returns a default    * implementation as specified in the docs of {@link CustomScoreProvider}.    * @since 2.9.2    */
DECL|method|getCustomScoreProvider
specifier|protected
name|CustomScoreProvider
name|getCustomScoreProvider
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CustomScoreProvider
argument_list|(
name|context
argument_list|)
return|;
block|}
comment|//=========================== W E I G H T ============================
DECL|class|CustomWeight
specifier|private
class|class
name|CustomWeight
extends|extends
name|Weight
block|{
DECL|field|subQueryWeight
name|Weight
name|subQueryWeight
decl_stmt|;
DECL|field|valSrcWeights
name|Weight
index|[]
name|valSrcWeights
decl_stmt|;
DECL|field|qStrict
name|boolean
name|qStrict
decl_stmt|;
DECL|field|queryWeight
name|float
name|queryWeight
decl_stmt|;
DECL|method|CustomWeight
specifier|public
name|CustomWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|subQueryWeight
operator|=
name|subQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|valSrcWeights
operator|=
operator|new
name|Weight
index|[
name|scoringQueries
operator|.
name|length
index|]
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
name|scoringQueries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|valSrcWeights
index|[
name|i
index|]
operator|=
name|scoringQueries
index|[
name|i
index|]
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|qStrict
operator|=
name|strict
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#getQuery() */
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|CustomScoreQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
name|subQueryWeight
operator|.
name|getValueForNormalization
argument_list|()
decl_stmt|;
for|for
control|(
name|Weight
name|valSrcWeight
range|:
name|valSrcWeights
control|)
block|{
if|if
condition|(
name|qStrict
condition|)
block|{
name|valSrcWeight
operator|.
name|getValueForNormalization
argument_list|()
expr_stmt|;
comment|// do not include ValueSource part in the query normalization
block|}
else|else
block|{
name|sum
operator|+=
name|valSrcWeight
operator|.
name|getValueForNormalization
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|sum
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Weight#normalize(float) */
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
comment|// note we DONT incorporate our boost, nor pass down any topLevelBoost
comment|// (e.g. from outer BQ), as there is no guarantee that the CustomScoreProvider's
comment|// function obeys the distributive law... it might call sqrt() on the subQuery score
comment|// or some other arbitrary function other than multiplication.
comment|// so, instead boosts are applied directly in score()
name|subQueryWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
for|for
control|(
name|Weight
name|valSrcWeight
range|:
name|valSrcWeights
control|)
block|{
if|if
condition|(
name|qStrict
condition|)
block|{
name|valSrcWeight
operator|.
name|normalize
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// do not normalize the ValueSource part
block|}
else|else
block|{
name|valSrcWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
block|}
block|}
name|queryWeight
operator|=
name|topLevelBoost
operator|*
name|getBoost
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|subQueryScorer
init|=
name|subQueryWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|subQueryScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Scorer
index|[]
name|valSrcScorers
init|=
operator|new
name|Scorer
index|[
name|valSrcWeights
operator|.
name|length
index|]
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
name|valSrcScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcScorers
index|[
name|i
index|]
operator|=
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CustomScorer
argument_list|(
name|CustomScoreQuery
operator|.
name|this
operator|.
name|getCustomScoreProvider
argument_list|(
name|context
argument_list|)
argument_list|,
name|this
argument_list|,
name|queryWeight
argument_list|,
name|subQueryScorer
argument_list|,
name|valSrcScorers
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|explain
init|=
name|doExplain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
return|return
name|explain
operator|==
literal|null
condition|?
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no matching docs"
argument_list|)
else|:
name|explain
return|;
block|}
DECL|method|doExplain
specifier|private
name|Explanation
name|doExplain
parameter_list|(
name|LeafReaderContext
name|info
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|subQueryExpl
init|=
name|subQueryWeight
operator|.
name|explain
argument_list|(
name|info
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subQueryExpl
operator|.
name|isMatch
argument_list|()
condition|)
block|{
return|return
name|subQueryExpl
return|;
block|}
comment|// match
name|Explanation
index|[]
name|valSrcExpls
init|=
operator|new
name|Explanation
index|[
name|valSrcWeights
operator|.
name|length
index|]
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
name|valSrcWeights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|valSrcExpls
index|[
name|i
index|]
operator|=
name|valSrcWeights
index|[
name|i
index|]
operator|.
name|explain
argument_list|(
name|info
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|Explanation
name|customExp
init|=
name|CustomScoreQuery
operator|.
name|this
operator|.
name|getCustomScoreProvider
argument_list|(
name|info
argument_list|)
operator|.
name|customExplain
argument_list|(
name|doc
argument_list|,
name|subQueryExpl
argument_list|,
name|valSrcExpls
argument_list|)
decl_stmt|;
name|float
name|sc
init|=
name|getBoost
argument_list|()
operator|*
name|customExp
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Explanation
name|res
init|=
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|sc
argument_list|,
name|CustomScoreQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|)
decl_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
name|customExp
argument_list|)
expr_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"queryBoost"
argument_list|)
argument_list|)
expr_stmt|;
comment|// actually using the q boost as q weight (== weight value)
return|return
name|res
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
return|return
literal|false
return|;
block|}
block|}
comment|//=========================== S C O R E R ============================
comment|/**    * A scorer that applies a (callback) function on scores of the subQuery.    */
DECL|class|CustomScorer
specifier|private
class|class
name|CustomScorer
extends|extends
name|Scorer
block|{
DECL|field|qWeight
specifier|private
specifier|final
name|float
name|qWeight
decl_stmt|;
DECL|field|subQueryScorer
specifier|private
specifier|final
name|Scorer
name|subQueryScorer
decl_stmt|;
DECL|field|valSrcScorers
specifier|private
specifier|final
name|Scorer
index|[]
name|valSrcScorers
decl_stmt|;
DECL|field|provider
specifier|private
specifier|final
name|CustomScoreProvider
name|provider
decl_stmt|;
DECL|field|vScores
specifier|private
specifier|final
name|float
index|[]
name|vScores
decl_stmt|;
comment|// reused in score() to avoid allocating this array for each doc
comment|// constructor
DECL|method|CustomScorer
specifier|private
name|CustomScorer
parameter_list|(
name|CustomScoreProvider
name|provider
parameter_list|,
name|CustomWeight
name|w
parameter_list|,
name|float
name|qWeight
parameter_list|,
name|Scorer
name|subQueryScorer
parameter_list|,
name|Scorer
index|[]
name|valSrcScorers
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|qWeight
operator|=
name|qWeight
expr_stmt|;
name|this
operator|.
name|subQueryScorer
operator|=
name|subQueryScorer
expr_stmt|;
name|this
operator|.
name|valSrcScorers
operator|=
name|valSrcScorers
expr_stmt|;
name|this
operator|.
name|vScores
operator|=
operator|new
name|float
index|[
name|valSrcScorers
operator|.
name|length
index|]
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
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
name|int
name|doc
init|=
name|subQueryScorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
for|for
control|(
name|Scorer
name|valSrcScorer
range|:
name|valSrcScorers
control|)
block|{
name|valSrcScorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
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
name|subQueryScorer
operator|.
name|docID
argument_list|()
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.Scorer#score() */
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valSrcScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|vScores
index|[
name|i
index|]
operator|=
name|valSrcScorers
index|[
name|i
index|]
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
return|return
name|qWeight
operator|*
name|provider
operator|.
name|customScore
argument_list|(
name|subQueryScorer
operator|.
name|docID
argument_list|()
argument_list|,
name|subQueryScorer
operator|.
name|score
argument_list|()
argument_list|,
name|vScores
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|subQueryScorer
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|subQueryScorer
argument_list|,
literal|"CUSTOM"
argument_list|)
argument_list|)
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
name|int
name|doc
init|=
name|subQueryScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
for|for
control|(
name|Scorer
name|valSrcScorer
range|:
name|valSrcScorers
control|)
block|{
name|valSrcScorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
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
name|subQueryScorer
operator|.
name|cost
argument_list|()
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
name|CustomWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
comment|/**    * Checks if this is strict custom scoring.    * In strict custom scoring, the {@link ValueSource} part does not participate in weight normalization.    * This may be useful when one wants full control over how scores are modified, and does     * not care about normalizing by the {@link ValueSource} part.    * One particular case where this is useful if for testing this query.       *<P>    * Note: only has effect when the {@link ValueSource} part is not null.    */
DECL|method|isStrict
specifier|public
name|boolean
name|isStrict
parameter_list|()
block|{
return|return
name|strict
return|;
block|}
comment|/**    * Set the strict mode of this query.     * @param strict The strict mode to set.    * @see #isStrict()    */
DECL|method|setStrict
specifier|public
name|void
name|setStrict
parameter_list|(
name|boolean
name|strict
parameter_list|)
block|{
name|this
operator|.
name|strict
operator|=
name|strict
expr_stmt|;
block|}
comment|/** The sub-query that CustomScoreQuery wraps, affecting both the score and which documents match. */
DECL|method|getSubQuery
specifier|public
name|Query
name|getSubQuery
parameter_list|()
block|{
return|return
name|subQuery
return|;
block|}
comment|/** The scoring queries that only affect the score of CustomScoreQuery. */
DECL|method|getScoringQueries
specifier|public
name|Query
index|[]
name|getScoringQueries
parameter_list|()
block|{
return|return
name|scoringQueries
return|;
block|}
comment|/**    * A short name of this query, used in {@link #toString(String)}.    */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"custom"
return|;
block|}
block|}
end_class
end_unit
