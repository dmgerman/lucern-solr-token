begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|PostingsEnum
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
operator|.
name|SimScorer
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
name|Map
import|;
end_import
begin_comment
comment|/**  * Expert-only.  Public for use by other weight implementations  */
end_comment
begin_class
DECL|class|SpanWeight
specifier|public
specifier|abstract
class|class
name|SpanWeight
extends|extends
name|Weight
block|{
comment|/**    * Enumeration defining what postings information should be retrieved from the    * index for a given Spans    */
DECL|enum|Postings
specifier|public
enum|enum
name|Postings
block|{
DECL|enum constant|POSITIONS
name|POSITIONS
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRequiredPostings
parameter_list|()
block|{
return|return
name|PostingsEnum
operator|.
name|POSITIONS
return|;
block|}
block|}
block|,
DECL|enum constant|PAYLOADS
name|PAYLOADS
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRequiredPostings
parameter_list|()
block|{
return|return
name|PostingsEnum
operator|.
name|PAYLOADS
return|;
block|}
block|}
block|,
DECL|enum constant|OFFSETS
name|OFFSETS
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRequiredPostings
parameter_list|()
block|{
return|return
name|PostingsEnum
operator|.
name|PAYLOADS
operator||
name|PostingsEnum
operator|.
name|OFFSETS
return|;
block|}
block|}
block|;
DECL|method|getRequiredPostings
specifier|public
specifier|abstract
name|int
name|getRequiredPostings
parameter_list|()
function_decl|;
DECL|method|atLeast
specifier|public
name|Postings
name|atLeast
parameter_list|(
name|Postings
name|postings
parameter_list|)
block|{
if|if
condition|(
name|postings
operator|.
name|compareTo
argument_list|(
name|this
argument_list|)
operator|>
literal|0
condition|)
return|return
name|postings
return|;
return|return
name|this
return|;
block|}
block|}
DECL|field|similarity
specifier|protected
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|simWeight
specifier|protected
specifier|final
name|Similarity
operator|.
name|SimWeight
name|simWeight
decl_stmt|;
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
comment|/**    * Create a new SpanWeight    * @param query the parent query    * @param searcher the IndexSearcher to query against    * @param termContexts a map of terms to termcontexts for use in building the similarity.  May    *                     be null if scores are not required    * @throws IOException on error    */
DECL|method|SpanWeight
specifier|public
name|SpanWeight
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|query
operator|.
name|getField
argument_list|()
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|(
name|termContexts
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|simWeight
operator|=
name|buildSimWeight
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|termContexts
argument_list|)
expr_stmt|;
block|}
DECL|method|buildSimWeight
specifier|private
name|Similarity
operator|.
name|SimWeight
name|buildSimWeight
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termContexts
operator|==
literal|null
operator|||
name|termContexts
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|query
operator|.
name|getField
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|TermStatistics
index|[]
name|termStats
init|=
operator|new
name|TermStatistics
index|[
name|termContexts
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|termContexts
operator|.
name|keySet
argument_list|()
control|)
block|{
name|termStats
index|[
name|i
index|]
operator|=
name|searcher
operator|.
name|termStatistics
argument_list|(
name|term
argument_list|,
name|termContexts
operator|.
name|get
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|CollectionStatistics
name|collectionStats
init|=
name|searcher
operator|.
name|collectionStatistics
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
operator|.
name|computeWeight
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|,
name|collectionStats
argument_list|,
name|termStats
argument_list|)
return|;
block|}
comment|/**    * Collect all TermContexts used by this Weight    * @param contexts a map to add the TermContexts to    */
DECL|method|extractTermContexts
specifier|public
specifier|abstract
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
function_decl|;
comment|/**    * Expert: Return a Spans object iterating over matches from this Weight    * @param ctx a LeafReaderContext for this Spans    * @return a Spans    * @throws IOException on error    */
DECL|method|getSpans
specifier|public
specifier|abstract
name|Spans
name|getSpans
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|Postings
name|requiredPostings
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
return|return
name|simWeight
operator|==
literal|null
condition|?
literal|1.0f
else|:
name|simWeight
operator|.
name|getValueForNormalization
argument_list|()
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
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
if|if
condition|(
name|simWeight
operator|!=
literal|null
condition|)
block|{
name|simWeight
operator|.
name|normalize
argument_list|(
name|queryNorm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|hasPositions
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" was indexed without position data; cannot run SpanQuery (query="
operator|+
name|parentQuery
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|Spans
name|spans
init|=
name|getSpans
argument_list|(
name|context
argument_list|,
name|Postings
operator|.
name|POSITIONS
argument_list|)
decl_stmt|;
name|Similarity
operator|.
name|SimScorer
name|simScorer
init|=
name|simWeight
operator|==
literal|null
condition|?
literal|null
else|:
name|similarity
operator|.
name|simScorer
argument_list|(
name|simWeight
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
operator|(
name|spans
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|SpanScorer
argument_list|(
name|spans
argument_list|,
name|this
argument_list|,
name|simScorer
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
name|SpanScorer
name|scorer
init|=
operator|(
name|SpanScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|scorer
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
name|float
name|freq
init|=
name|scorer
operator|.
name|sloppyFreq
argument_list|()
decl_stmt|;
name|SimScorer
name|docScorer
init|=
name|similarity
operator|.
name|simScorer
argument_list|(
name|simWeight
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Explanation
name|freqExplanation
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|freq
argument_list|,
literal|"phraseFreq="
operator|+
name|freq
argument_list|)
decl_stmt|;
name|Explanation
name|scoreExplanation
init|=
name|docScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freqExplanation
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scoreExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|") ["
operator|+
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"], result of:"
argument_list|,
name|scoreExplanation
argument_list|)
return|;
block|}
block|}
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"no matching term"
argument_list|)
return|;
block|}
block|}
end_class
end_unit
