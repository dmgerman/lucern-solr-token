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
name|*
import|;
end_import
begin_comment
comment|/** Scorer for conjunctions, sets of queries, all of which are required. */
end_comment
begin_class
DECL|class|ConjunctionScorer
specifier|final
class|class
name|ConjunctionScorer
extends|extends
name|Scorer
block|{
DECL|field|scorers
specifier|private
name|LinkedList
name|scorers
init|=
operator|new
name|LinkedList
argument_list|()
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
DECL|field|coord
specifier|private
name|float
name|coord
decl_stmt|;
DECL|method|ConjunctionScorer
specifier|public
name|ConjunctionScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|final
name|void
name|add
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|scorers
operator|.
name|addLast
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
DECL|method|first
specifier|private
name|Scorer
name|first
parameter_list|()
block|{
return|return
operator|(
name|Scorer
operator|)
name|scorers
operator|.
name|getFirst
argument_list|()
return|;
block|}
DECL|method|last
specifier|private
name|Scorer
name|last
parameter_list|()
block|{
return|return
operator|(
name|Scorer
operator|)
name|scorers
operator|.
name|getLast
argument_list|()
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|first
argument_list|()
operator|.
name|doc
argument_list|()
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
argument_list|()
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
operator|&&
name|first
argument_list|()
operator|.
name|doc
argument_list|()
operator|<
name|last
argument_list|()
operator|.
name|doc
argument_list|()
condition|)
block|{
comment|// find doc w/ all clauses
name|more
operator|=
name|first
argument_list|()
operator|.
name|skipTo
argument_list|(
name|last
argument_list|()
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
comment|// skip first upto last
name|scorers
operator|.
name|addLast
argument_list|(
name|scorers
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
comment|// move first to last
block|}
return|return
name|more
return|;
comment|// found a doc with all clauses
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
name|Iterator
name|i
init|=
name|scorers
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|more
operator|&&
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|more
operator|=
operator|(
operator|(
name|Scorer
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
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
name|sortScorers
argument_list|()
expr_stmt|;
comment|// re-sort scorers
return|return
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
name|score
init|=
literal|0.0f
decl_stmt|;
comment|// sum scores
name|Iterator
name|i
init|=
name|scorers
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
name|score
operator|+=
operator|(
operator|(
name|Scorer
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|score
argument_list|()
expr_stmt|;
name|score
operator|*=
name|coord
expr_stmt|;
return|return
name|score
return|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|more
operator|=
name|scorers
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
comment|// compute coord factor
name|coord
operator|=
name|getSimilarity
argument_list|()
operator|.
name|coord
argument_list|(
name|scorers
operator|.
name|size
argument_list|()
argument_list|,
name|scorers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// move each scorer to its first entry
name|Iterator
name|i
init|=
name|scorers
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|more
operator|&&
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|more
operator|=
operator|(
operator|(
name|Scorer
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|more
condition|)
name|sortScorers
argument_list|()
expr_stmt|;
comment|// initial sort of list
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|sortScorers
specifier|private
name|void
name|sortScorers
parameter_list|()
throws|throws
name|IOException
block|{
comment|// move scorers to an array
name|Scorer
index|[]
name|array
init|=
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
decl_stmt|;
name|scorers
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// empty the list
comment|// note that this comparator is not consistent with equals!
name|Arrays
operator|.
name|sort
argument_list|(
name|array
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
name|doc
argument_list|()
operator|-
operator|(
operator|(
name|Scorer
operator|)
name|o2
operator|)
operator|.
name|doc
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
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
name|doc
argument_list|()
operator|==
operator|(
operator|(
name|Scorer
operator|)
name|o2
operator|)
operator|.
name|doc
argument_list|()
return|;
block|}
block|}
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scorers
operator|.
name|addLast
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// re-build list, now sorted
block|}
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class
end_unit
