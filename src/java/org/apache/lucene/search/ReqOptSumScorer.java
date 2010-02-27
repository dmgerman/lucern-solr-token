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
begin_comment
comment|/** A Scorer for queries with a required part and an optional part.  * Delays skipTo() on the optional part until a score() is needed.  *<br>  * This<code>Scorer</code> implements {@link Scorer#skipTo(int)}.  */
end_comment
begin_class
DECL|class|ReqOptSumScorer
class|class
name|ReqOptSumScorer
extends|extends
name|Scorer
block|{
comment|/** The scorers passed from the constructor.    * These are set to null as soon as their next() or skipTo() returns false.    */
DECL|field|reqScorer
specifier|private
name|Scorer
name|reqScorer
decl_stmt|;
DECL|field|optScorer
specifier|private
name|Scorer
name|optScorer
decl_stmt|;
comment|/** Construct a<code>ReqOptScorer</code>.    * @param reqScorer The required scorer. This must match.    * @param optScorer The optional scorer. This is used for scoring only.    */
DECL|method|ReqOptSumScorer
specifier|public
name|ReqOptSumScorer
parameter_list|(
name|Scorer
name|reqScorer
parameter_list|,
name|Scorer
name|optScorer
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// No similarity used.
name|this
operator|.
name|reqScorer
operator|=
name|reqScorer
expr_stmt|;
name|this
operator|.
name|optScorer
operator|=
name|optScorer
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
return|return
name|reqScorer
operator|.
name|nextDoc
argument_list|()
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
return|return
name|reqScorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
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
name|reqScorer
operator|.
name|docID
argument_list|()
return|;
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #nextDoc()} is called the first time.    * @return The score of the required scorer, eventually increased by the score    * of the optional scorer when it also matches the current document.    */
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
name|int
name|curDoc
init|=
name|reqScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
name|float
name|reqScore
init|=
name|reqScorer
operator|.
name|score
argument_list|()
decl_stmt|;
if|if
condition|(
name|optScorer
operator|==
literal|null
condition|)
block|{
return|return
name|reqScore
return|;
block|}
name|int
name|optScorerDoc
init|=
name|optScorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|optScorerDoc
operator|<
name|curDoc
operator|&&
operator|(
name|optScorerDoc
operator|=
name|optScorer
operator|.
name|advance
argument_list|(
name|curDoc
argument_list|)
operator|)
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|optScorer
operator|=
literal|null
expr_stmt|;
return|return
name|reqScore
return|;
block|}
return|return
name|optScorerDoc
operator|==
name|curDoc
condition|?
name|reqScore
operator|+
name|optScorer
operator|.
name|score
argument_list|()
else|:
name|reqScore
return|;
block|}
block|}
end_class
end_unit
