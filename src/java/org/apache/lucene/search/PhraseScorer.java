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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
import|;
end_import
begin_class
DECL|class|PhraseScorer
specifier|abstract
class|class
name|PhraseScorer
extends|extends
name|Scorer
block|{
DECL|field|weight
specifier|private
name|Weight
name|weight
decl_stmt|;
DECL|field|norms
specifier|protected
name|byte
index|[]
name|norms
decl_stmt|;
DECL|field|value
specifier|protected
name|float
name|value
decl_stmt|;
DECL|field|firstTime
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
DECL|field|more
specifier|private
name|boolean
name|more
init|=
literal|true
decl_stmt|;
DECL|field|pq
specifier|protected
name|PhraseQueue
name|pq
decl_stmt|;
DECL|field|first
DECL|field|last
specifier|protected
name|PhrasePositions
name|first
decl_stmt|,
name|last
decl_stmt|;
DECL|field|freq
specifier|private
name|float
name|freq
decl_stmt|;
DECL|method|PhraseScorer
name|PhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|int
index|[]
name|positions
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|weight
operator|.
name|getValue
argument_list|()
expr_stmt|;
comment|// convert tps to a list
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PhrasePositions
name|pp
init|=
operator|new
name|PhrasePositions
argument_list|(
name|tps
index|[
name|i
index|]
argument_list|,
name|positions
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// add next to end of list
name|last
operator|.
name|next
operator|=
name|pp
expr_stmt|;
block|}
else|else
name|first
operator|=
name|pp
expr_stmt|;
name|last
operator|=
name|pp
expr_stmt|;
block|}
name|pq
operator|=
operator|new
name|PhraseQueue
argument_list|(
name|tps
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// construct empty pq
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|first
operator|.
name|doc
return|;
block|}
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
name|init
argument_list|()
expr_stmt|;
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|more
condition|)
block|{
name|more
operator|=
name|last
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// trigger further scanning
block|}
return|return
name|doNext
argument_list|()
return|;
block|}
comment|// next without initial increment
DECL|method|doNext
specifier|private
name|boolean
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|more
condition|)
block|{
while|while
condition|(
name|more
operator|&&
name|first
operator|.
name|doc
operator|<
name|last
operator|.
name|doc
condition|)
block|{
comment|// find doc w/ all the terms
name|more
operator|=
name|first
operator|.
name|skipTo
argument_list|(
name|last
operator|.
name|doc
argument_list|)
expr_stmt|;
comment|// skip first upto last
name|firstToLast
argument_list|()
expr_stmt|;
comment|// and move it to the end
block|}
if|if
condition|(
name|more
condition|)
block|{
comment|// found a doc with all of the terms
name|freq
operator|=
name|phraseFreq
argument_list|()
expr_stmt|;
comment|// check for phrase
if|if
condition|(
name|freq
operator|==
literal|0.0f
condition|)
comment|// no match
name|more
operator|=
name|last
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// trigger further scanning
else|else
return|return
literal|true
return|;
comment|// found a match
block|}
block|}
return|return
literal|false
return|;
comment|// no more matches
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("scoring " + first.doc);
name|float
name|raw
init|=
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|freq
argument_list|)
operator|*
name|value
decl_stmt|;
comment|// raw score
return|return
name|raw
operator|*
name|Similarity
operator|.
name|decodeNorm
argument_list|(
name|norms
index|[
name|first
operator|.
name|doc
index|]
argument_list|)
return|;
comment|// normalize
block|}
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
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|more
operator|&&
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|more
operator|=
name|pp
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|more
condition|)
name|sort
argument_list|()
expr_stmt|;
comment|// re-sort
return|return
name|doNext
argument_list|()
return|;
block|}
DECL|method|phraseFreq
specifier|protected
specifier|abstract
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|more
operator|&&
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
name|more
operator|=
name|pp
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|more
condition|)
name|sort
argument_list|()
expr_stmt|;
block|}
DECL|method|sort
specifier|private
name|void
name|sort
parameter_list|()
block|{
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
name|pq
operator|.
name|put
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|pqToList
argument_list|()
expr_stmt|;
block|}
DECL|method|pqToList
specifier|protected
specifier|final
name|void
name|pqToList
parameter_list|()
block|{
name|last
operator|=
name|first
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|pq
operator|.
name|top
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PhrasePositions
name|pp
init|=
operator|(
name|PhrasePositions
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// add next to end of list
name|last
operator|.
name|next
operator|=
name|pp
expr_stmt|;
block|}
else|else
name|first
operator|=
name|pp
expr_stmt|;
name|last
operator|=
name|pp
expr_stmt|;
name|pp
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|firstToLast
specifier|protected
specifier|final
name|void
name|firstToLast
parameter_list|()
block|{
name|last
operator|.
name|next
operator|=
name|first
expr_stmt|;
comment|// move first to end of list
name|last
operator|=
name|first
expr_stmt|;
name|first
operator|=
name|first
operator|.
name|next
expr_stmt|;
name|last
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
argument_list|()
operator|&&
name|doc
argument_list|()
operator|<
name|doc
condition|)
block|{}
name|float
name|phraseFreq
init|=
operator|(
name|doc
argument_list|()
operator|==
name|doc
operator|)
condition|?
name|freq
else|:
literal|0.0f
decl_stmt|;
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|getSimilarity
argument_list|()
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
return|return
name|tfExplanation
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"scorer("
operator|+
name|weight
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
