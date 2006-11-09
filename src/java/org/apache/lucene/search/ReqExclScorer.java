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
comment|/** A Scorer for queries with a required subscorer and an excluding (prohibited) subscorer.  *<br>  * This<code>Scorer</code> implements {@link Scorer#skipTo(int)},  * and it uses the skipTo() on the given scorers.  */
end_comment
begin_class
DECL|class|ReqExclScorer
specifier|public
class|class
name|ReqExclScorer
extends|extends
name|Scorer
block|{
DECL|field|reqScorer
DECL|field|exclScorer
specifier|private
name|Scorer
name|reqScorer
decl_stmt|,
name|exclScorer
decl_stmt|;
comment|/** Construct a<code>ReqExclScorer</code>.    * @param reqScorer The scorer that must match, except where    * @param exclScorer indicates exclusion.    */
DECL|method|ReqExclScorer
specifier|public
name|ReqExclScorer
parameter_list|(
name|Scorer
name|reqScorer
parameter_list|,
name|Scorer
name|exclScorer
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
name|exclScorer
operator|=
name|exclScorer
expr_stmt|;
block|}
DECL|field|firstTime
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
if|if
condition|(
operator|!
name|exclScorer
operator|.
name|next
argument_list|()
condition|)
block|{
name|exclScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted at start
block|}
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|reqScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|reqScorer
operator|.
name|next
argument_list|()
condition|)
block|{
name|reqScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted, nothing left
return|return
literal|false
return|;
block|}
if|if
condition|(
name|exclScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
comment|// reqScorer.next() already returned true
block|}
return|return
name|toNonExcluded
argument_list|()
return|;
block|}
comment|/** Advance to non excluded doc.    *<br>On entry:    *<ul>    *<li>reqScorer != null,    *<li>exclScorer != null,    *<li>reqScorer was advanced once via next() or skipTo()    *      and reqScorer.doc() may still be excluded.    *</ul>    * Advances reqScorer a non excluded required doc, if any.    * @return true iff there is a non excluded required doc.    */
DECL|method|toNonExcluded
specifier|private
name|boolean
name|toNonExcluded
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|exclDoc
init|=
name|exclScorer
operator|.
name|doc
argument_list|()
decl_stmt|;
do|do
block|{
name|int
name|reqDoc
init|=
name|reqScorer
operator|.
name|doc
argument_list|()
decl_stmt|;
comment|// may be excluded
if|if
condition|(
name|reqDoc
operator|<
name|exclDoc
condition|)
block|{
return|return
literal|true
return|;
comment|// reqScorer advanced to before exclScorer, ie. not excluded
block|}
elseif|else
if|if
condition|(
name|reqDoc
operator|>
name|exclDoc
condition|)
block|{
if|if
condition|(
operator|!
name|exclScorer
operator|.
name|skipTo
argument_list|(
name|reqDoc
argument_list|)
condition|)
block|{
name|exclScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted, no more exclusions
return|return
literal|true
return|;
block|}
name|exclDoc
operator|=
name|exclScorer
operator|.
name|doc
argument_list|()
expr_stmt|;
if|if
condition|(
name|exclDoc
operator|>
name|reqDoc
condition|)
block|{
return|return
literal|true
return|;
comment|// not excluded
block|}
block|}
block|}
do|while
condition|(
name|reqScorer
operator|.
name|next
argument_list|()
condition|)
do|;
name|reqScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted, nothing left
return|return
literal|false
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|reqScorer
operator|.
name|doc
argument_list|()
return|;
comment|// reqScorer may be null when next() or skipTo() already return false
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #next()} is called the first time.    * @return The score of the required scorer.    */
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reqScorer
operator|.
name|score
argument_list|()
return|;
comment|// reqScorer may be null when next() or skipTo() already return false
block|}
comment|/** Skips to the first match beyond the current whose document number is    * greater than or equal to a given target.    *<br>When this method is used the {@link #explain(int)} method should not be used.    * @param target The target document number.    * @return true iff there is such a match.    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
if|if
condition|(
operator|!
name|exclScorer
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|exclScorer
operator|=
literal|null
expr_stmt|;
comment|// exhausted
block|}
block|}
if|if
condition|(
name|reqScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|exclScorer
operator|==
literal|null
condition|)
block|{
return|return
name|reqScorer
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|reqScorer
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|reqScorer
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|toNonExcluded
argument_list|()
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|res
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
if|if
condition|(
name|exclScorer
operator|.
name|skipTo
argument_list|(
name|doc
argument_list|)
operator|&&
operator|(
name|exclScorer
operator|.
name|doc
argument_list|()
operator|==
name|doc
operator|)
condition|)
block|{
name|res
operator|.
name|setDescription
argument_list|(
literal|"excluded"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|setDescription
argument_list|(
literal|"not excluded"
argument_list|)
expr_stmt|;
name|res
operator|.
name|addDetail
argument_list|(
name|reqScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
