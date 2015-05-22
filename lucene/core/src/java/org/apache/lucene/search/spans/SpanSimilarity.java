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
name|similarities
operator|.
name|Similarity
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
name|HashMap
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
begin_comment
comment|/**  * Encapsulates similarity statistics required for SpanScorers  */
end_comment
begin_class
DECL|class|SpanSimilarity
specifier|public
specifier|abstract
class|class
name|SpanSimilarity
block|{
comment|/**    * The field term statistics are taken from    */
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
comment|/**    * Create a new SpanSimilarity    * @param field the similarity field for term statistics    */
DECL|method|SpanSimilarity
specifier|protected
name|SpanSimilarity
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
comment|/**    * Create a SimScorer for this SpanSimilarity's statistics    * @param context the LeafReaderContext to calculate the scorer for    * @return a SimScorer, or null if no scoring is required    * @throws IOException on error    */
DECL|method|simScorer
specifier|public
specifier|abstract
name|Similarity
operator|.
name|SimScorer
name|simScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the field for term statistics    */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/**    * See {@link org.apache.lucene.search.Weight#getValueForNormalization()}    *    * @return the value for normalization    * @throws IOException on error    */
DECL|method|getValueForNormalization
specifier|public
specifier|abstract
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * See {@link org.apache.lucene.search.Weight#normalize(float,float)}    *    * @param queryNorm the query norm    * @param topLevelBoost the top level boost    */
DECL|method|normalize
specifier|public
specifier|abstract
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
function_decl|;
comment|/**    * A SpanSimilarity class that calculates similarity statistics based on the term statistics    * of a set of terms.    */
DECL|class|ScoringSimilarity
specifier|public
specifier|static
class|class
name|ScoringSimilarity
extends|extends
name|SpanSimilarity
block|{
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|stats
specifier|private
specifier|final
name|Similarity
operator|.
name|SimWeight
name|stats
decl_stmt|;
DECL|method|ScoringSimilarity
specifier|private
name|ScoringSimilarity
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
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
name|stats
operator|=
name|similarity
operator|.
name|computeWeight
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|,
name|searcher
operator|.
name|collectionStatistics
argument_list|(
name|field
argument_list|)
argument_list|,
name|termStats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|Similarity
operator|.
name|SimScorer
name|simScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|similarity
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
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
return|return
name|stats
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
name|stats
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
comment|/**    * A SpanSimilarity class that does no scoring    */
DECL|class|NonScoringSimilarity
specifier|public
specifier|static
class|class
name|NonScoringSimilarity
extends|extends
name|SpanSimilarity
block|{
DECL|method|NonScoringSimilarity
specifier|private
name|NonScoringSimilarity
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|Similarity
operator|.
name|SimScorer
name|simScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
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
return|return
literal|0
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
block|{      }
block|}
comment|/**    * Build a SpanSimilarity    * @param query the SpanQuery to be run    * @param searcher the searcher    * @param needsScores whether or not scores are required    * @param stats an array of TermStatistics to use in creating the similarity    * @return a SpanSimilarity, or null if there are no statistics to use    * @throws IOException on error    */
DECL|method|build
specifier|public
specifier|static
name|SpanSimilarity
name|build
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|TermStatistics
modifier|...
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|needsScores
condition|?
operator|new
name|ScoringSimilarity
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|stats
argument_list|)
else|:
operator|new
name|NonScoringSimilarity
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Build a SpanSimilarity    * @param query the SpanQuery to be run    * @param searcher the searcher    * @param needsScores whether or not scores are required    * @param weights a set of {@link org.apache.lucene.search.spans.SpanWeight}s to extract terms from    * @return a SpanSimilarity, or null if there are no statistics to use    * @throws IOException on error    */
DECL|method|build
specifier|public
specifier|static
name|SpanSimilarity
name|build
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|weights
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|build
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|weights
operator|.
name|toArray
argument_list|(
operator|new
name|SpanWeight
index|[
name|weights
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Build a SpanSimilarity    * @param query the SpanQuery to run    * @param searcher the searcher    * @param needsScores whether or not scores are required    * @param weights an array of {@link org.apache.lucene.search.spans.SpanWeight}s to extract terms from    * @return a SpanSimilarity, or null if there are no statistics to use    * @throws IOException on error    */
DECL|method|build
specifier|public
specifier|static
name|SpanSimilarity
name|build
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|SpanWeight
modifier|...
name|weights
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|needsScores
condition|)
return|return
operator|new
name|NonScoringSimilarity
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
return|;
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpanWeight
name|w
range|:
name|weights
control|)
block|{
name|w
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contexts
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|TermStatistics
index|[]
name|stats
init|=
operator|new
name|TermStatistics
index|[
name|contexts
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
name|contexts
operator|.
name|keySet
argument_list|()
control|)
block|{
name|stats
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
name|contexts
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
return|return
operator|new
name|ScoringSimilarity
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
name|stats
argument_list|)
return|;
block|}
block|}
end_class
end_unit