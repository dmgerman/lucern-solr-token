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
name|TermState
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
name|IndexReader
operator|.
name|ReaderContext
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
name|search
operator|.
name|Explanation
operator|.
name|IDFExplanation
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
name|PerReaderTermState
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
comment|/** A Query that matches documents containing a term.   This may be combined with other terms with a {@link BooleanQuery}.   */
end_comment
begin_class
DECL|class|TermQuery
specifier|public
class|class
name|TermQuery
extends|extends
name|Query
block|{
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|docFreq
specifier|private
name|int
name|docFreq
decl_stmt|;
DECL|field|perReaderTermState
specifier|private
specifier|transient
name|PerReaderTermState
name|perReaderTermState
decl_stmt|;
DECL|class|TermWeight
specifier|private
class|class
name|TermWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|private
specifier|final
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
specifier|final
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
DECL|field|idfExp
specifier|private
specifier|final
name|IDFExplanation
name|idfExp
decl_stmt|;
DECL|field|termStates
specifier|private
specifier|transient
name|PerReaderTermState
name|termStates
decl_stmt|;
DECL|method|TermWeight
specifier|public
name|TermWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|PerReaderTermState
name|termStates
parameter_list|,
name|int
name|docFreq
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|termStates
operator|!=
literal|null
operator|:
literal|"PerReaderTermState must not be null"
assert|;
name|this
operator|.
name|termStates
operator|=
name|termStates
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|docFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|idfExp
operator|=
name|similarity
operator|.
name|idfExplain
argument_list|(
name|term
argument_list|,
name|searcher
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|idfExp
operator|=
name|similarity
operator|.
name|idfExplain
argument_list|(
name|term
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
name|idf
operator|=
name|idfExp
operator|.
name|getIdf
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"weight("
operator|+
name|TermQuery
operator|.
name|this
operator|+
literal|")"
return|;
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
name|TermQuery
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
name|AtomicReaderContext
name|context
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
specifier|final
name|String
name|field
init|=
name|term
operator|.
name|field
argument_list|()
decl_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|context
operator|.
name|reader
decl_stmt|;
assert|assert
name|assertTopReaderContext
argument_list|(
name|termStates
argument_list|,
name|context
argument_list|)
operator|:
literal|"The top-reader used to create Weight is not the same as the current reader's top-reader"
assert|;
specifier|final
name|TermState
name|state
init|=
name|termStates
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// term is not present in that reader
assert|assert
name|termNotInReader
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
operator|:
literal|"no termstate found but term exists in reader"
assert|;
return|return
literal|null
return|;
block|}
specifier|final
name|DocsEnum
name|docs
init|=
name|reader
operator|.
name|termDocsEnum
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
name|field
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|state
argument_list|)
decl_stmt|;
assert|assert
name|docs
operator|!=
literal|null
assert|;
return|return
operator|new
name|TermScorer
argument_list|(
name|this
argument_list|,
name|docs
argument_list|,
name|similarity
argument_list|,
name|context
operator|.
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
DECL|method|termNotInReader
specifier|private
name|boolean
name|termNotInReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// only called from assert
specifier|final
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|terms
operator|==
literal|null
operator|||
name|terms
operator|.
name|docFreq
argument_list|(
name|bytes
argument_list|)
operator|==
literal|0
return|;
block|}
DECL|method|assertTopReaderContext
specifier|private
name|boolean
name|assertTopReaderContext
parameter_list|(
name|PerReaderTermState
name|state
parameter_list|,
name|ReaderContext
name|context
parameter_list|)
block|{
while|while
condition|(
name|context
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|context
operator|=
name|context
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|state
operator|.
name|topReaderContext
operator|==
name|context
return|;
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
name|IndexReader
name|reader
init|=
name|context
operator|.
name|reader
decl_stmt|;
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
name|expl
init|=
operator|new
name|Explanation
argument_list|(
name|idf
argument_list|,
name|idfExp
operator|.
name|explain
argument_list|()
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
name|expl
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
name|expl
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
name|String
name|field
init|=
name|term
operator|.
name|field
argument_list|()
decl_stmt|;
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
name|term
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|"), product of:"
argument_list|)
expr_stmt|;
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|int
name|tf
init|=
literal|0
decl_stmt|;
name|DocsEnum
name|docs
init|=
name|reader
operator|.
name|termDocsEnum
argument_list|(
name|reader
operator|.
name|getDeletedDocs
argument_list|()
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|docs
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDoc
operator|==
name|doc
condition|)
block|{
name|tf
operator|=
name|docs
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|similarity
operator|.
name|tf
argument_list|(
name|tf
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(termFreq("
operator|+
name|term
operator|+
literal|")="
operator|+
name|tf
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tfExplanation
operator|.
name|setValue
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"no matching term"
argument_list|)
expr_stmt|;
block|}
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
name|expl
argument_list|)
expr_stmt|;
name|Explanation
name|fieldNormExpl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
specifier|final
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
name|expl
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
comment|/** Constructs a query for the term<code>t</code>. */
DECL|method|TermQuery
specifier|public
name|TermQuery
parameter_list|(
name|Term
name|t
parameter_list|)
block|{
name|this
argument_list|(
name|t
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: constructs a TermQuery that will use the    *  provided docFreq instead of looking up the docFreq    *  against the searcher. */
DECL|method|TermQuery
specifier|public
name|TermQuery
parameter_list|(
name|Term
name|t
parameter_list|,
name|int
name|docFreq
parameter_list|)
block|{
name|term
operator|=
name|t
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|perReaderTermState
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Expert: constructs a TermQuery that will use the    *  provided docFreq instead of looking up the docFreq    *  against the searcher. */
DECL|method|TermQuery
specifier|public
name|TermQuery
parameter_list|(
name|Term
name|t
parameter_list|,
name|PerReaderTermState
name|states
parameter_list|)
block|{
assert|assert
name|states
operator|!=
literal|null
assert|;
name|term
operator|=
name|t
expr_stmt|;
name|docFreq
operator|=
name|states
operator|.
name|docFreq
argument_list|()
expr_stmt|;
name|perReaderTermState
operator|=
name|states
expr_stmt|;
block|}
comment|/** Returns the term of this query. */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
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
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ReaderContext
name|context
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
specifier|final
name|int
name|weightDocFreq
decl_stmt|;
specifier|final
name|PerReaderTermState
name|termState
decl_stmt|;
if|if
condition|(
name|perReaderTermState
operator|==
literal|null
operator|||
name|perReaderTermState
operator|.
name|topReaderContext
operator|!=
name|context
condition|)
block|{
comment|// make TermQuery single-pass if we don't have a PRTS or if the context differs!
name|termState
operator|=
name|PerReaderTermState
operator|.
name|build
argument_list|(
name|context
argument_list|,
name|term
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// cache term lookups!
comment|// we must not ignore the given docFreq - if set use the given value
name|weightDocFreq
operator|=
name|docFreq
operator|==
operator|-
literal|1
condition|?
name|termState
operator|.
name|docFreq
argument_list|()
else|:
name|docFreq
expr_stmt|;
block|}
else|else
block|{
comment|// PRTS was pre-build for this IS
name|termState
operator|=
name|this
operator|.
name|perReaderTermState
expr_stmt|;
name|weightDocFreq
operator|=
name|docFreq
expr_stmt|;
block|}
return|return
operator|new
name|TermWeight
argument_list|(
name|searcher
argument_list|,
name|termState
argument_list|,
name|weightDocFreq
argument_list|)
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
name|add
argument_list|(
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
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
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
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
name|TermQuery
operator|)
condition|)
return|return
literal|false
return|;
name|TermQuery
name|other
init|=
operator|(
name|TermQuery
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
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
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
name|term
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
