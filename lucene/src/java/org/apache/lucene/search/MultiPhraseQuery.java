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
name|DocsEnum
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
name|MultiFields
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
name|DocsAndPositionsEnum
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
name|util
operator|.
name|PriorityQueue
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
begin_comment
comment|/**  * MultiPhraseQuery is a generalized version of PhraseQuery, with an added  * method {@link #add(Term[])}.  * To use this class, to search for the phrase "Microsoft app*" first use  * add(Term) on the term "Microsoft", then find all terms that have "app" as  * prefix using IndexReader.terms(Term), and use MultiPhraseQuery.add(Term[]  * terms) to add them to the query.  *  * @version 1.0  */
end_comment
begin_class
DECL|class|MultiPhraseQuery
specifier|public
class|class
name|MultiPhraseQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|termArrays
specifier|private
name|ArrayList
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
index|[]
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|positions
specifier|private
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|positions
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
literal|0
decl_stmt|;
comment|/** Sets the phrase slop for this query.    * @see PhraseQuery#setSlop(int)    */
DECL|method|setSlop
specifier|public
name|void
name|setSlop
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|slop
operator|=
name|s
expr_stmt|;
block|}
comment|/** Sets the phrase slop for this query.    * @see PhraseQuery#getSlop()    */
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/** Add a single term at the next position in the phrase.    * @see PhraseQuery#add(Term)    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|Term
index|[]
block|{
name|term
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Add multiple terms at the next position in the phrase.  Any of the terms    * may match.    *    * @see PhraseQuery#add(Term)    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
block|{
name|int
name|position
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|positions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|position
operator|=
name|positions
operator|.
name|get
argument_list|(
name|positions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|intValue
argument_list|()
operator|+
literal|1
expr_stmt|;
name|add
argument_list|(
name|terms
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allows to specify the relative position of terms within the phrase.    *     * @see PhraseQuery#add(Term, int)    * @param terms    * @param position    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|,
name|int
name|position
parameter_list|)
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|field
operator|=
name|terms
index|[
literal|0
index|]
operator|.
name|field
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All phrase terms must be in the same field ("
operator|+
name|field
operator|+
literal|"): "
operator|+
name|terms
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
name|termArrays
operator|.
name|add
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|positions
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a List of the terms in the multiphrase.    * Do not modify the List or its contents.    */
DECL|method|getTermArrays
specifier|public
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|getTermArrays
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|termArrays
argument_list|)
return|;
block|}
comment|/**    * Returns the relative positions of terms in this phrase.    */
DECL|method|getPositions
specifier|public
name|int
index|[]
name|getPositions
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|positions
operator|.
name|size
argument_list|()
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
name|positions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|result
index|[
name|i
index|]
operator|=
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
name|result
return|;
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
specifier|final
name|Term
index|[]
name|arr
range|:
name|termArrays
control|)
block|{
for|for
control|(
specifier|final
name|Term
name|term
range|:
name|arr
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|MultiPhraseWeight
specifier|private
class|class
name|MultiPhraseWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
DECL|field|value
specifier|private
name|float
name|value
decl_stmt|;
DECL|field|idf
specifier|private
name|float
name|idf
decl_stmt|;
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
decl_stmt|;
DECL|method|MultiPhraseWeight
specifier|public
name|MultiPhraseWeight
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
comment|// compute idf
specifier|final
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Term
index|[]
name|terms
range|:
name|termArrays
control|)
block|{
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|idf
operator|+=
name|this
operator|.
name|similarity
operator|.
name|idf
argument_list|(
name|searcher
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
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
name|MultiPhraseQuery
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
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
block|{
name|queryWeight
operator|=
name|idf
operator|*
name|getBoost
argument_list|()
expr_stmt|;
comment|// compute query weight
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
comment|// square it
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|queryNorm
expr_stmt|;
name|queryWeight
operator|*=
name|queryNorm
expr_stmt|;
comment|// normalize query weight
name|value
operator|=
name|queryWeight
operator|*
name|idf
expr_stmt|;
comment|// idf for document
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// optimize zero-term case
return|return
literal|null
return|;
name|DocsAndPositionsEnum
index|[]
name|postings
init|=
operator|new
name|DocsAndPositionsEnum
index|[
name|termArrays
operator|.
name|size
argument_list|()
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
name|postings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Term
index|[]
name|terms
init|=
name|termArrays
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|DocsAndPositionsEnum
name|postingsEnum
decl_stmt|;
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|postingsEnum
operator|=
operator|new
name|UnionDocsAndPositionsEnum
argument_list|(
name|reader
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|postingsEnum
operator|=
name|reader
operator|.
name|termPositionsEnum
argument_list|(
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|terms
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|terms
index|[
literal|0
index|]
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|postingsEnum
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|postings
index|[
name|i
index|]
operator|=
name|postingsEnum
expr_stmt|;
block|}
if|if
condition|(
name|slop
operator|==
literal|0
condition|)
return|return
operator|new
name|ExactPhraseScorer
argument_list|(
name|this
argument_list|,
name|postings
argument_list|,
name|getPositions
argument_list|()
argument_list|,
name|similarity
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|new
name|SloppyPhraseScorer
argument_list|(
name|this
argument_list|,
name|postings
argument_list|,
name|getPositions
argument_list|()
argument_list|,
name|similarity
argument_list|,
name|slop
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|field
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
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|idfExpl
init|=
operator|new
name|Explanation
argument_list|(
name|idf
argument_list|,
literal|"idf("
operator|+
name|getQuery
argument_list|()
operator|+
literal|")"
argument_list|)
decl_stmt|;
comment|// explain query weight
name|Explanation
name|queryExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|queryExpl
operator|.
name|setDescription
argument_list|(
literal|"queryWeight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|boostExpl
init|=
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|boostExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|queryNormExpl
init|=
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
decl_stmt|;
name|queryExpl
operator|.
name|addDetail
argument_list|(
name|queryNormExpl
argument_list|)
expr_stmt|;
name|queryExpl
operator|.
name|setValue
argument_list|(
name|boostExpl
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|queryNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|queryExpl
argument_list|)
expr_stmt|;
comment|// explain field weight
name|ComplexExplanation
name|fieldExpl
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|fieldExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldWeight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|PhraseScorer
name|scorer
init|=
operator|(
name|PhraseScorer
operator|)
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"no matching docs"
argument_list|)
return|;
block|}
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|int
name|d
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|float
name|phraseFreq
init|=
operator|(
name|d
operator|==
name|doc
operator|)
condition|?
name|scorer
operator|.
name|currentFreq
argument_list|()
else|:
literal|0.0f
decl_stmt|;
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|similarity
operator|.
name|tf
argument_list|(
name|phraseFreq
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(phraseFreq="
operator|+
name|phraseFreq
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|tfExplanation
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|idfExpl
argument_list|)
expr_stmt|;
name|Explanation
name|fieldNormExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fieldNorms
init|=
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|float
name|fieldNorm
init|=
name|fieldNorms
operator|!=
literal|null
condition|?
name|similarity
operator|.
name|decodeNormValue
argument_list|(
name|fieldNorms
index|[
name|doc
index|]
argument_list|)
else|:
literal|1.0f
decl_stmt|;
name|fieldNormExpl
operator|.
name|setValue
argument_list|(
name|fieldNorm
argument_list|)
expr_stmt|;
name|fieldNormExpl
operator|.
name|setDescription
argument_list|(
literal|"fieldNorm(field="
operator|+
name|field
operator|+
literal|", doc="
operator|+
name|doc
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|addDetail
argument_list|(
name|fieldNormExpl
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|tfExplanation
operator|.
name|isMatch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fieldExpl
operator|.
name|setValue
argument_list|(
name|tfExplanation
operator|.
name|getValue
argument_list|()
operator|*
name|idfExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldNormExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|fieldExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|fieldExpl
operator|.
name|getMatch
argument_list|()
argument_list|)
expr_stmt|;
comment|// combine them
name|result
operator|.
name|setValue
argument_list|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|*
name|fieldExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryExpl
operator|.
name|getValue
argument_list|()
operator|==
literal|1.0f
condition|)
return|return
name|fieldExpl
return|;
return|return
name|result
return|;
block|}
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
block|{
if|if
condition|(
name|termArrays
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// optimize one-term case
name|Term
index|[]
name|terms
init|=
name|termArrays
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|BooleanQuery
name|boq
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|boq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|boq
return|;
block|}
else|else
block|{
return|return
name|this
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
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiPhraseWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|(
name|String
name|f
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Term
index|[]
argument_list|>
name|i
init|=
name|termArrays
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
index|[]
name|terms
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|terms
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|terms
index|[
name|j
index|]
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|<
name|terms
operator|.
name|length
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
name|terms
index|[
literal|0
index|]
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|slop
operator|!=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"~"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
block|}
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
return|return
name|buffer
operator|.
name|toString
argument_list|()
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
operator|!
operator|(
name|o
operator|instanceof
name|MultiPhraseQuery
operator|)
condition|)
return|return
literal|false
return|;
name|MultiPhraseQuery
name|other
init|=
operator|(
name|MultiPhraseQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|this
operator|.
name|slop
operator|==
name|other
operator|.
name|slop
operator|&&
name|termArraysEquals
argument_list|(
name|this
operator|.
name|termArrays
argument_list|,
name|other
operator|.
name|termArrays
argument_list|)
operator|&&
name|this
operator|.
name|positions
operator|.
name|equals
argument_list|(
name|other
operator|.
name|positions
argument_list|)
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
name|slop
operator|^
name|termArraysHashCode
argument_list|()
operator|^
name|positions
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x4AC65113
return|;
block|}
comment|// Breakout calculation of the termArrays hashcode
DECL|method|termArraysHashCode
specifier|private
name|int
name|termArraysHashCode
parameter_list|()
block|{
name|int
name|hashCode
init|=
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|Term
index|[]
name|termArray
range|:
name|termArrays
control|)
block|{
name|hashCode
operator|=
literal|31
operator|*
name|hashCode
operator|+
operator|(
name|termArray
operator|==
literal|null
condition|?
literal|0
else|:
name|Arrays
operator|.
name|hashCode
argument_list|(
name|termArray
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|hashCode
return|;
block|}
comment|// Breakout calculation of the termArrays equals
DECL|method|termArraysEquals
specifier|private
name|boolean
name|termArraysEquals
parameter_list|(
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays1
parameter_list|,
name|List
argument_list|<
name|Term
index|[]
argument_list|>
name|termArrays2
parameter_list|)
block|{
if|if
condition|(
name|termArrays1
operator|.
name|size
argument_list|()
operator|!=
name|termArrays2
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ListIterator
argument_list|<
name|Term
index|[]
argument_list|>
name|iterator1
init|=
name|termArrays1
operator|.
name|listIterator
argument_list|()
decl_stmt|;
name|ListIterator
argument_list|<
name|Term
index|[]
argument_list|>
name|iterator2
init|=
name|termArrays2
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Term
index|[]
name|termArray1
init|=
name|iterator1
operator|.
name|next
argument_list|()
decl_stmt|;
name|Term
index|[]
name|termArray2
init|=
name|iterator2
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|termArray1
operator|==
literal|null
condition|?
name|termArray2
operator|==
literal|null
else|:
name|Arrays
operator|.
name|equals
argument_list|(
name|termArray1
argument_list|,
name|termArray2
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * Takes the logical union of multiple DocsEnum iterators.  */
end_comment
begin_comment
comment|// TODO: if ever we allow subclassing of the *PhraseScorer
end_comment
begin_class
DECL|class|UnionDocsAndPositionsEnum
class|class
name|UnionDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|class|DocsQueue
specifier|private
specifier|static
specifier|final
class|class
name|DocsQueue
extends|extends
name|PriorityQueue
argument_list|<
name|DocsAndPositionsEnum
argument_list|>
block|{
DECL|method|DocsQueue
name|DocsQueue
parameter_list|(
name|List
argument_list|<
name|DocsAndPositionsEnum
argument_list|>
name|docsEnums
parameter_list|)
throws|throws
name|IOException
block|{
name|initialize
argument_list|(
name|docsEnums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|DocsAndPositionsEnum
argument_list|>
name|i
init|=
name|docsEnums
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocsAndPositionsEnum
name|postings
init|=
operator|(
name|DocsAndPositionsEnum
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|postings
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsAndPositionsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|add
argument_list|(
name|postings
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|peek
specifier|final
specifier|public
name|DocsEnum
name|peek
parameter_list|()
block|{
return|return
name|top
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|public
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|DocsAndPositionsEnum
name|a
parameter_list|,
name|DocsAndPositionsEnum
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|docID
argument_list|()
operator|<
name|b
operator|.
name|docID
argument_list|()
return|;
block|}
block|}
DECL|class|IntQueue
specifier|private
specifier|static
specifier|final
class|class
name|IntQueue
block|{
DECL|field|_arraySize
specifier|private
name|int
name|_arraySize
init|=
literal|16
decl_stmt|;
DECL|field|_index
specifier|private
name|int
name|_index
init|=
literal|0
decl_stmt|;
DECL|field|_lastIndex
specifier|private
name|int
name|_lastIndex
init|=
literal|0
decl_stmt|;
DECL|field|_array
specifier|private
name|int
index|[]
name|_array
init|=
operator|new
name|int
index|[
name|_arraySize
index|]
decl_stmt|;
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|_lastIndex
operator|==
name|_arraySize
condition|)
name|growArray
argument_list|()
expr_stmt|;
name|_array
index|[
name|_lastIndex
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
DECL|method|next
specifier|final
name|int
name|next
parameter_list|()
block|{
return|return
name|_array
index|[
name|_index
operator|++
index|]
return|;
block|}
DECL|method|sort
specifier|final
name|void
name|sort
parameter_list|()
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|_array
argument_list|,
name|_index
argument_list|,
name|_lastIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|final
name|void
name|clear
parameter_list|()
block|{
name|_index
operator|=
literal|0
expr_stmt|;
name|_lastIndex
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|size
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
operator|(
name|_lastIndex
operator|-
name|_index
operator|)
return|;
block|}
DECL|method|growArray
specifier|private
name|void
name|growArray
parameter_list|()
block|{
name|int
index|[]
name|newArray
init|=
operator|new
name|int
index|[
name|_arraySize
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|_array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|_arraySize
argument_list|)
expr_stmt|;
name|_array
operator|=
name|newArray
expr_stmt|;
name|_arraySize
operator|*=
literal|2
expr_stmt|;
block|}
block|}
DECL|field|_doc
specifier|private
name|int
name|_doc
decl_stmt|;
DECL|field|_freq
specifier|private
name|int
name|_freq
decl_stmt|;
DECL|field|_queue
specifier|private
name|DocsQueue
name|_queue
decl_stmt|;
DECL|field|_posList
specifier|private
name|IntQueue
name|_posList
decl_stmt|;
DECL|method|UnionDocsAndPositionsEnum
specifier|public
name|UnionDocsAndPositionsEnum
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DocsAndPositionsEnum
argument_list|>
name|docsEnums
init|=
operator|new
name|LinkedList
argument_list|<
name|DocsAndPositionsEnum
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|delDocs
init|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|indexReader
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DocsAndPositionsEnum
name|postings
init|=
name|indexReader
operator|.
name|termPositionsEnum
argument_list|(
name|delDocs
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|terms
index|[
name|i
index|]
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|postings
operator|!=
literal|null
condition|)
block|{
name|docsEnums
operator|.
name|add
argument_list|(
name|postings
argument_list|)
expr_stmt|;
block|}
block|}
name|_queue
operator|=
operator|new
name|DocsQueue
argument_list|(
name|docsEnums
argument_list|)
expr_stmt|;
name|_posList
operator|=
operator|new
name|IntQueue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|_queue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
comment|// TODO: move this init into positions(): if the search
comment|// doesn't need the positions for this doc then don't
comment|// waste CPU merging them:
name|_posList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|_doc
operator|=
name|_queue
operator|.
name|top
argument_list|()
operator|.
name|docID
argument_list|()
expr_stmt|;
comment|// merge sort all positions together
name|DocsAndPositionsEnum
name|postings
decl_stmt|;
do|do
block|{
name|postings
operator|=
name|_queue
operator|.
name|top
argument_list|()
expr_stmt|;
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freq
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
operator|<
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|_posList
operator|.
name|add
argument_list|(
name|postings
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|postings
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|_queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|_queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|_queue
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|_queue
operator|.
name|top
argument_list|()
operator|.
name|docID
argument_list|()
operator|==
name|_doc
condition|)
do|;
name|_posList
operator|.
name|sort
argument_list|()
expr_stmt|;
name|_freq
operator|=
name|_posList
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|_doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
block|{
return|return
name|_posList
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
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
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
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
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|_queue
operator|.
name|top
argument_list|()
operator|!=
literal|null
operator|&&
name|target
operator|>
name|_queue
operator|.
name|top
argument_list|()
operator|.
name|docID
argument_list|()
condition|)
block|{
name|DocsAndPositionsEnum
name|postings
init|=
name|_queue
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|postings
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|_queue
operator|.
name|add
argument_list|(
name|postings
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
block|{
return|return
name|_freq
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|_doc
return|;
block|}
block|}
end_class
end_unit
