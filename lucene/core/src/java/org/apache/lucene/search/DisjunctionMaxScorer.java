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
name|List
import|;
end_import
begin_comment
comment|/**  * The Scorer for DisjunctionMaxQuery.  The union of all documents generated by the the subquery scorers  * is generated in document number order.  The score for each document is the maximum of the scores computed  * by the subquery scorers that generate that document, plus tieBreakerMultiplier times the sum of the scores  * for the other subqueries that generate the document.  */
end_comment
begin_class
DECL|class|DisjunctionMaxScorer
specifier|final
class|class
name|DisjunctionMaxScorer
extends|extends
name|DisjunctionScorer
block|{
comment|/* Multiplier applied to non-maximum-scoring subqueries for a document as they are summed into the result. */
DECL|field|tieBreakerMultiplier
specifier|private
specifier|final
name|float
name|tieBreakerMultiplier
decl_stmt|;
comment|/**    * Creates a new instance of DisjunctionMaxScorer    *     * @param weight    *          The Weight to be used.    * @param tieBreakerMultiplier    *          Multiplier applied to non-maximum-scoring subqueries for a    *          document as they are summed into the result.    * @param subScorers    *          The sub scorers this Scorer should iterate on    */
DECL|method|DisjunctionMaxScorer
name|DisjunctionMaxScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|float
name|tieBreakerMultiplier
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|subScorers
argument_list|,
name|needsScores
argument_list|)
expr_stmt|;
name|this
operator|.
name|tieBreakerMultiplier
operator|=
name|tieBreakerMultiplier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|protected
name|float
name|score
parameter_list|(
name|DisiWrapper
name|topList
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|scoreSum
init|=
literal|0
decl_stmt|;
name|float
name|scoreMax
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DisiWrapper
name|w
init|=
name|topList
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|next
control|)
block|{
specifier|final
name|float
name|subScore
init|=
name|w
operator|.
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|scoreSum
operator|+=
name|subScore
expr_stmt|;
if|if
condition|(
name|subScore
operator|>
name|scoreMax
condition|)
block|{
name|scoreMax
operator|=
name|subScore
expr_stmt|;
block|}
block|}
return|return
name|scoreMax
operator|+
operator|(
name|scoreSum
operator|-
name|scoreMax
operator|)
operator|*
name|tieBreakerMultiplier
return|;
block|}
block|}
end_class
end_unit
