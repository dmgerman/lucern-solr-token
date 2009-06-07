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
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/** Scorer for conjunctions, sets of queries, all of which are required. */
end_comment
begin_class
DECL|class|ConjunctionScorer
class|class
name|ConjunctionScorer
extends|extends
name|Scorer
block|{
DECL|field|scorers
specifier|private
specifier|final
name|Scorer
index|[]
name|scorers
decl_stmt|;
DECL|field|coord
specifier|private
specifier|final
name|float
name|coord
decl_stmt|;
DECL|field|lastDoc
specifier|private
name|int
name|lastDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|ConjunctionScorer
specifier|public
name|ConjunctionScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|Collection
name|scorers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|similarity
argument_list|,
operator|(
name|Scorer
index|[]
operator|)
name|scorers
operator|.
name|toArray
argument_list|(
operator|new
name|Scorer
index|[
name|scorers
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ConjunctionScorer
specifier|public
name|ConjunctionScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|Scorer
index|[]
name|scorers
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorers
operator|=
name|scorers
expr_stmt|;
name|coord
operator|=
name|similarity
operator|.
name|coord
argument_list|(
name|scorers
operator|.
name|length
argument_list|,
name|scorers
operator|.
name|length
argument_list|)
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
name|scorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|scorers
index|[
name|i
index|]
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
comment|// If even one of the sub-scorers does not have any documents, this
comment|// scorer should not attempt to do any more work.
name|lastDoc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return;
block|}
block|}
comment|// Sort the array the first time...
comment|// We don't need to sort the array in any future calls because we know
comment|// it will already start off sorted (all scorers on same doc).
comment|// note that this comparator is not consistent with equals!
name|Arrays
operator|.
name|sort
argument_list|(
name|scorers
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
comment|// sort the array
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Scorer
operator|)
name|o1
operator|)
operator|.
name|docID
argument_list|()
operator|-
operator|(
operator|(
name|Scorer
operator|)
name|o2
operator|)
operator|.
name|docID
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// NOTE: doNext() must be called before the re-sorting of the array later on.
comment|// The reason is this: assume there are 5 scorers, whose first docs are 1,
comment|// 2, 3, 5, 5 respectively. Sorting (above) leaves the array as is. Calling
comment|// doNext() here advances all the first scorers to 5 (or a larger doc ID
comment|// they all agree on).
comment|// However, if we re-sort before doNext() is called, the order will be 5, 3,
comment|// 2, 1, 5 and then doNext() will stop immediately, since the first scorer's
comment|// docs equals the last one. So the invariant that after calling doNext()
comment|// all scorers are on the same doc ID is broken.
if|if
condition|(
name|doNext
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
comment|// The scorers did not agree on any document.
name|lastDoc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return;
block|}
comment|// If first-time skip distance is any predictor of
comment|// scorer sparseness, then we should always try to skip first on
comment|// those scorers.
comment|// Keep last scorer in it's last place (it will be the first
comment|// to be skipped on), but reverse all of the others so that
comment|// they will be skipped on in order of original high skip.
name|int
name|end
init|=
name|scorers
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|max
init|=
name|end
operator|>>
literal|1
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|Scorer
name|tmp
init|=
name|scorers
index|[
name|i
index|]
decl_stmt|;
name|int
name|idx
init|=
name|end
operator|-
name|i
operator|-
literal|1
decl_stmt|;
name|scorers
index|[
name|i
index|]
operator|=
name|scorers
index|[
name|idx
index|]
expr_stmt|;
name|scorers
index|[
name|idx
index|]
operator|=
name|tmp
expr_stmt|;
block|}
block|}
DECL|method|doNext
specifier|private
name|int
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|first
init|=
literal|0
decl_stmt|;
name|int
name|doc
init|=
name|scorers
index|[
name|scorers
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|docID
argument_list|()
decl_stmt|;
name|Scorer
name|firstScorer
decl_stmt|;
while|while
condition|(
operator|(
name|firstScorer
operator|=
name|scorers
index|[
name|first
index|]
operator|)
operator|.
name|docID
argument_list|()
operator|<
name|doc
condition|)
block|{
name|doc
operator|=
name|firstScorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|first
operator|=
name|first
operator|==
name|scorers
operator|.
name|length
operator|-
literal|1
condition|?
literal|0
else|:
name|first
operator|+
literal|1
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
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
if|if
condition|(
name|lastDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|lastDoc
return|;
block|}
elseif|else
if|if
condition|(
name|scorers
index|[
operator|(
name|scorers
operator|.
name|length
operator|-
literal|1
operator|)
index|]
operator|.
name|docID
argument_list|()
operator|<
name|target
condition|)
block|{
name|scorers
index|[
operator|(
name|scorers
operator|.
name|length
operator|-
literal|1
operator|)
index|]
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
return|return
name|lastDoc
operator|=
name|doNext
argument_list|()
return|;
block|}
comment|/** @deprecated use {@link #docID()} instead. */
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|lastDoc
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|lastDoc
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** @deprecated use {@link #nextDoc()} instead. */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
return|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|lastDoc
return|;
block|}
elseif|else
if|if
condition|(
name|lastDoc
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|lastDoc
operator|=
name|scorers
index|[
name|scorers
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|docID
argument_list|()
return|;
block|}
name|scorers
index|[
operator|(
name|scorers
operator|.
name|length
operator|-
literal|1
operator|)
index|]
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
return|return
name|lastDoc
operator|=
name|doNext
argument_list|()
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|sum
init|=
literal|0.0f
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
name|scorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|scorers
index|[
name|i
index|]
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
return|return
name|sum
operator|*
name|coord
return|;
block|}
comment|/** @deprecated use {@link #advance(int)} instead. */
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
return|return
name|advance
argument_list|(
name|target
argument_list|)
operator|!=
name|NO_MORE_DOCS
return|;
block|}
block|}
end_class
end_unit
