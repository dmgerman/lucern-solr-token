begin_unit
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocValues
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
name|FieldInvertState
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
name|Norm
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
name|CollectionStatistics
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
name|TermStatistics
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
name|BytesRef
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
name|SmallFloat
import|;
end_import
begin_comment
comment|/**  * A subclass of {@code Similarity} that provides a simplified API for its  * descendants. Subclasses are only required to implement the {@link #score}  * and {@link #toString()} methods. Implementing  * {@link #explain(Explanation, BasicStats, int, float, float)} is optional,  * inasmuch as SimilarityBase already provides a basic explanation of the score  * and the term frequency. However, implementers of a subclass are encouraged to  * include as much detail about the scoring method as possible.  *<p>  * Note: multi-word queries such as phrase queries are scored in a different way  * than Lucene's default ranking algorithm: whereas it "fakes" an IDF value for  * the phrase as a whole (since it does not know it), this class instead scores  * phrases as a summation of the individual term scores.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimilarityBase
specifier|public
specifier|abstract
class|class
name|SimilarityBase
extends|extends
name|Similarity
block|{
comment|/** For {@link #log2(double)}. Precomputed for efficiency reasons. */
DECL|field|LOG_2
specifier|private
specifier|static
specifier|final
name|double
name|LOG_2
init|=
name|Math
operator|.
name|log
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|/** @see #setDiscountOverlaps */
DECL|field|discountOverlaps
specifier|protected
name|boolean
name|discountOverlaps
init|=
literal|true
decl_stmt|;
comment|/** Determines whether overlap tokens (Tokens with    *  0 position increment) are ignored when computing    *  norm.  By default this is true, meaning overlap    *  tokens do not count when computing norms.    *    *  @lucene.experimental    *    *  @see #computeNorm    */
DECL|method|setDiscountOverlaps
specifier|public
name|void
name|setDiscountOverlaps
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|discountOverlaps
operator|=
name|v
expr_stmt|;
block|}
comment|/** @see #setDiscountOverlaps */
DECL|method|getDiscountOverlaps
specifier|public
name|boolean
name|getDiscountOverlaps
parameter_list|()
block|{
return|return
name|discountOverlaps
return|;
block|}
annotation|@
name|Override
DECL|method|computeStats
specifier|public
specifier|final
name|Stats
name|computeStats
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|float
name|queryBoost
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
name|BasicStats
name|stats
index|[]
init|=
operator|new
name|BasicStats
index|[
name|termStats
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
name|termStats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stats
index|[
name|i
index|]
operator|=
name|newStats
argument_list|(
name|queryBoost
argument_list|)
expr_stmt|;
name|fillBasicStats
argument_list|(
name|stats
index|[
name|i
index|]
argument_list|,
name|collectionStats
argument_list|,
name|termStats
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
operator|.
name|length
operator|==
literal|1
condition|?
name|stats
index|[
literal|0
index|]
else|:
operator|new
name|MultiSimilarity
operator|.
name|MultiStats
argument_list|(
name|stats
argument_list|)
return|;
block|}
comment|/** Factory method to return a custom stats object */
DECL|method|newStats
specifier|protected
name|BasicStats
name|newStats
parameter_list|(
name|float
name|queryBoost
parameter_list|)
block|{
return|return
operator|new
name|BasicStats
argument_list|(
name|queryBoost
argument_list|)
return|;
block|}
comment|/** Fills all member fields defined in {@code BasicStats} in {@code stats}.     *  Subclasses can override this method to fill additional stats. */
DECL|method|fillBasicStats
specifier|protected
name|void
name|fillBasicStats
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
parameter_list|)
block|{
assert|assert
name|termStats
operator|.
name|totalTermFreq
argument_list|()
operator|>=
operator|-
literal|1
assert|;
assert|assert
name|termStats
operator|.
name|totalTermFreq
argument_list|()
operator|==
operator|-
literal|1
operator|||
name|termStats
operator|.
name|totalTermFreq
argument_list|()
operator|>=
name|termStats
operator|.
name|docFreq
argument_list|()
assert|;
assert|assert
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
operator|>=
operator|-
literal|1
assert|;
assert|assert
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
operator|==
operator|-
literal|1
operator|||
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
operator|>=
name|termStats
operator|.
name|totalTermFreq
argument_list|()
assert|;
name|int
name|numberOfDocuments
init|=
name|collectionStats
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|docFreq
init|=
name|termStats
operator|.
name|docFreq
argument_list|()
decl_stmt|;
name|long
name|totalTermFreq
init|=
name|termStats
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
comment|// codec does not supply totalTermFreq: substitute docFreq
if|if
condition|(
name|totalTermFreq
operator|==
operator|-
literal|1
condition|)
block|{
name|totalTermFreq
operator|=
name|docFreq
expr_stmt|;
block|}
specifier|final
name|long
name|numberOfFieldTokens
decl_stmt|;
specifier|final
name|float
name|avgFieldLength
decl_stmt|;
name|long
name|sumTotalTermFreq
init|=
name|collectionStats
operator|.
name|sumTotalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|sumTotalTermFreq
operator|<=
literal|0
condition|)
block|{
comment|// field does not exist;
comment|// We have to provide something if codec doesnt supply these measures,
comment|// or if someone omitted frequencies for the field... negative values cause
comment|// NaN/Inf for some scorers.
name|numberOfFieldTokens
operator|=
name|docFreq
expr_stmt|;
name|avgFieldLength
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|numberOfFieldTokens
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|avgFieldLength
operator|=
operator|(
name|float
operator|)
name|numberOfFieldTokens
operator|/
name|numberOfDocuments
expr_stmt|;
block|}
comment|// TODO: add sumDocFreq for field (numberOfFieldPostings)
name|stats
operator|.
name|setNumberOfDocuments
argument_list|(
name|numberOfDocuments
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumberOfFieldTokens
argument_list|(
name|numberOfFieldTokens
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setAvgFieldLength
argument_list|(
name|avgFieldLength
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setDocFreq
argument_list|(
name|docFreq
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setTotalTermFreq
argument_list|(
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
comment|/**    * Scores the document {@code doc}.    *<p>Subclasses must apply their scoring formula in this class.</p>    * @param stats the corpus level statistics.    * @param freq the term frequency.    * @param docLen the document length.    * @return the score.    */
DECL|method|score
specifier|protected
specifier|abstract
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
function_decl|;
comment|/**    * Subclasses should implement this method to explain the score. {@code expl}    * already contains the score, the name of the class and the doc id, as well    * as the term frequency and its explanation; subclasses can add additional    * clauses to explain details of their scoring formulae.    *<p>The default implementation does nothing.</p>    *     * @param expl the explanation to extend with details.    * @param stats the corpus level statistics.    * @param doc the document id.    * @param freq the term frequency.    * @param docLen the document length.    */
DECL|method|explain
specifier|protected
name|void
name|explain
parameter_list|(
name|Explanation
name|expl
parameter_list|,
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{}
comment|/**    * Explains the score. The implementation here provides a basic explanation    * in the format<em>score(name-of-similarity, doc=doc-id,    * freq=term-frequency), computed from:</em>, and    * attaches the score (computed via the {@link #score(BasicStats, float, float)}    * method) and the explanation for the term frequency. Subclasses content with    * this format may add additional details in    * {@link #explain(Explanation, BasicStats, int, float, float)}.    *      * @param stats the corpus level statistics.    * @param doc the document id.    * @param freq the term frequency and its explanation.    * @param docLen the document length.    * @return the explanation.    */
DECL|method|explain
specifier|protected
name|Explanation
name|explain
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|score
argument_list|(
name|stats
argument_list|,
name|freq
operator|.
name|getValue
argument_list|()
argument_list|,
name|docLen
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"score("
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|", doc="
operator|+
name|doc
operator|+
literal|", freq="
operator|+
name|freq
operator|.
name|getValue
argument_list|()
operator|+
literal|"), computed from:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|freq
argument_list|)
expr_stmt|;
name|explain
argument_list|(
name|result
argument_list|,
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
operator|.
name|getValue
argument_list|()
argument_list|,
name|docLen
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|exactDocScorer
specifier|public
name|ExactDocScorer
name|exactDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValues
name|norms
init|=
name|context
operator|.
name|reader
operator|.
name|normValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|instanceof
name|MultiSimilarity
operator|.
name|MultiStats
condition|)
block|{
comment|// a multi term query (e.g. phrase). return the summation,
comment|// scoring almost as if it were boolean query
name|Stats
name|subStats
index|[]
init|=
operator|(
operator|(
name|MultiSimilarity
operator|.
name|MultiStats
operator|)
name|stats
operator|)
operator|.
name|subStats
decl_stmt|;
name|ExactDocScorer
name|subScorers
index|[]
init|=
operator|new
name|ExactDocScorer
index|[
name|subStats
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
name|subScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subScorers
index|[
name|i
index|]
operator|=
operator|new
name|BasicExactDocScorer
argument_list|(
operator|(
name|BasicStats
operator|)
name|subStats
index|[
name|i
index|]
argument_list|,
name|norms
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiSimilarity
operator|.
name|MultiExactDocScorer
argument_list|(
name|subScorers
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BasicExactDocScorer
argument_list|(
operator|(
name|BasicStats
operator|)
name|stats
argument_list|,
name|norms
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|sloppyDocScorer
specifier|public
name|SloppyDocScorer
name|sloppyDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValues
name|norms
init|=
name|context
operator|.
name|reader
operator|.
name|normValues
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|stats
operator|instanceof
name|MultiSimilarity
operator|.
name|MultiStats
condition|)
block|{
comment|// a multi term query (e.g. phrase). return the summation,
comment|// scoring almost as if it were boolean query
name|Stats
name|subStats
index|[]
init|=
operator|(
operator|(
name|MultiSimilarity
operator|.
name|MultiStats
operator|)
name|stats
operator|)
operator|.
name|subStats
decl_stmt|;
name|SloppyDocScorer
name|subScorers
index|[]
init|=
operator|new
name|SloppyDocScorer
index|[
name|subStats
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
name|subScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subScorers
index|[
name|i
index|]
operator|=
operator|new
name|BasicSloppyDocScorer
argument_list|(
operator|(
name|BasicStats
operator|)
name|subStats
index|[
name|i
index|]
argument_list|,
name|norms
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiSimilarity
operator|.
name|MultiSloppyDocScorer
argument_list|(
name|subScorers
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BasicSloppyDocScorer
argument_list|(
operator|(
name|BasicStats
operator|)
name|stats
argument_list|,
name|norms
argument_list|)
return|;
block|}
block|}
comment|/**    * Subclasses must override this method to return the name of the Similarity    * and preferably the values of parameters (if any) as well.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
comment|// ------------------------------ Norm handling ------------------------------
comment|/** Norm -> document length map. */
DECL|field|NORM_TABLE
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|NORM_TABLE
init|=
operator|new
name|float
index|[
literal|256
index|]
decl_stmt|;
static|static
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
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|float
name|floatNorm
init|=
name|SmallFloat
operator|.
name|byte315ToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
decl_stmt|;
name|NORM_TABLE
index|[
name|i
index|]
operator|=
literal|1.0f
operator|/
operator|(
name|floatNorm
operator|*
name|floatNorm
operator|)
expr_stmt|;
block|}
block|}
comment|/** Encodes the document length in the same way as {@link TFIDFSimilarity}. */
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
specifier|final
name|float
name|numTerms
decl_stmt|;
if|if
condition|(
name|discountOverlaps
condition|)
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
expr_stmt|;
else|else
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
operator|/
name|state
operator|.
name|getBoost
argument_list|()
expr_stmt|;
name|norm
operator|.
name|setByte
argument_list|(
name|encodeNormValue
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
argument_list|,
name|numTerms
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Decodes a normalization factor (document length) stored in an index.    * @see #encodeNormValue(float,float)    */
DECL|method|decodeNormValue
specifier|protected
name|float
name|decodeNormValue
parameter_list|(
name|byte
name|norm
parameter_list|)
block|{
return|return
name|NORM_TABLE
index|[
name|norm
operator|&
literal|0xFF
index|]
return|;
comment|//& 0xFF maps negative bytes to positive above 127
block|}
comment|/** Encodes the length to a byte via SmallFloat. */
DECL|method|encodeNormValue
specifier|protected
name|byte
name|encodeNormValue
parameter_list|(
name|float
name|boost
parameter_list|,
name|float
name|length
parameter_list|)
block|{
return|return
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
operator|(
name|boost
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|length
argument_list|)
operator|)
argument_list|)
return|;
block|}
comment|// ----------------------------- Static methods ------------------------------
comment|/** Returns the base two logarithm of {@code x}. */
DECL|method|log2
specifier|public
specifier|static
name|double
name|log2
parameter_list|(
name|double
name|x
parameter_list|)
block|{
comment|// Put this to a 'util' class if we need more of these.
return|return
name|Math
operator|.
name|log
argument_list|(
name|x
argument_list|)
operator|/
name|LOG_2
return|;
block|}
comment|// --------------------------------- Classes ---------------------------------
comment|/** Delegates the {@link #score(int, int)} and    * {@link #explain(int, Explanation)} methods to    * {@link SimilarityBase#score(BasicStats, float, int)} and    * {@link SimilarityBase#explain(BasicStats, int, Explanation, int)},    * respectively.    */
DECL|class|BasicExactDocScorer
specifier|private
class|class
name|BasicExactDocScorer
extends|extends
name|ExactDocScorer
block|{
DECL|field|stats
specifier|private
specifier|final
name|BasicStats
name|stats
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|byte
index|[]
name|norms
decl_stmt|;
DECL|method|BasicExactDocScorer
name|BasicExactDocScorer
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|DocValues
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|byte
index|[]
operator|)
name|norms
operator|.
name|getSource
argument_list|()
operator|.
name|getArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
comment|// We have to supply something in case norms are omitted
return|return
name|SimilarityBase
operator|.
name|this
operator|.
name|score
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|norms
operator|==
literal|null
condition|?
literal|1F
else|:
name|decodeNormValue
argument_list|(
name|norms
index|[
name|doc
index|]
argument_list|)
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
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
return|return
name|SimilarityBase
operator|.
name|this
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
argument_list|,
name|norms
operator|==
literal|null
condition|?
literal|1F
else|:
name|decodeNormValue
argument_list|(
name|norms
index|[
name|doc
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Delegates the {@link #score(int, int)} and    * {@link #explain(int, Explanation)} methods to    * {@link SimilarityBase#score(BasicStats, float, int)} and    * {@link SimilarityBase#explain(BasicStats, int, Explanation, int)},    * respectively.    */
DECL|class|BasicSloppyDocScorer
specifier|private
class|class
name|BasicSloppyDocScorer
extends|extends
name|SloppyDocScorer
block|{
DECL|field|stats
specifier|private
specifier|final
name|BasicStats
name|stats
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|byte
index|[]
name|norms
decl_stmt|;
DECL|method|BasicSloppyDocScorer
name|BasicSloppyDocScorer
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|DocValues
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|byte
index|[]
operator|)
name|norms
operator|.
name|getSource
argument_list|()
operator|.
name|getArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
block|{
comment|// We have to supply something in case norms are omitted
return|return
name|SimilarityBase
operator|.
name|this
operator|.
name|score
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|norms
operator|==
literal|null
condition|?
literal|1F
else|:
name|decodeNormValue
argument_list|(
name|norms
index|[
name|doc
index|]
argument_list|)
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
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
return|return
name|SimilarityBase
operator|.
name|this
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|doc
argument_list|,
name|freq
argument_list|,
name|norms
operator|==
literal|null
condition|?
literal|1F
else|:
name|decodeNormValue
argument_list|(
name|norms
index|[
name|doc
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeSlopFactor
specifier|public
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
operator|/
operator|(
name|distance
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|computePayloadFactor
specifier|public
name|float
name|computePayloadFactor
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1f
return|;
block|}
block|}
block|}
end_class
end_unit
