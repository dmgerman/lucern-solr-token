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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Iterator
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
begin_comment
comment|/**  * A priority queue of DocIdSetIterators that orders by current doc ID.  * This specialization is needed over {@link PriorityQueue} because the  * pluggable comparison function makes the rebalancing quite slow.  * @lucene.internal  */
end_comment
begin_class
DECL|class|DisiPriorityQueue
specifier|public
specifier|final
class|class
name|DisiPriorityQueue
parameter_list|<
name|Iter
extends|extends
name|DocIdSetIterator
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
argument_list|>
block|{
DECL|method|leftNode
specifier|static
name|int
name|leftNode
parameter_list|(
name|int
name|node
parameter_list|)
block|{
return|return
operator|(
operator|(
name|node
operator|+
literal|1
operator|)
operator|<<
literal|1
operator|)
operator|-
literal|1
return|;
block|}
DECL|method|rightNode
specifier|static
name|int
name|rightNode
parameter_list|(
name|int
name|leftNode
parameter_list|)
block|{
return|return
name|leftNode
operator|+
literal|1
return|;
block|}
DECL|method|parentNode
specifier|static
name|int
name|parentNode
parameter_list|(
name|int
name|node
parameter_list|)
block|{
return|return
operator|(
operator|(
name|node
operator|+
literal|1
operator|)
operator|>>>
literal|1
operator|)
operator|-
literal|1
return|;
block|}
DECL|field|heap
specifier|private
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
index|[]
name|heap
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|DisiPriorityQueue
specifier|public
name|DisiPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|heap
operator|=
operator|new
name|DisiWrapper
index|[
name|maxSize
index|]
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|top
specifier|public
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|top
parameter_list|()
block|{
return|return
name|heap
index|[
literal|0
index|]
return|;
block|}
comment|/** Get the list of scorers which are on the current doc. */
DECL|method|topList
specifier|public
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|topList
parameter_list|()
block|{
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
index|[]
name|heap
init|=
name|this
operator|.
name|heap
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|this
operator|.
name|size
decl_stmt|;
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|list
init|=
name|heap
index|[
literal|0
index|]
decl_stmt|;
name|list
operator|.
name|next
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|size
operator|>=
literal|3
condition|)
block|{
name|list
operator|=
name|topList
argument_list|(
name|list
argument_list|,
name|heap
argument_list|,
name|size
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|list
operator|=
name|topList
argument_list|(
name|list
argument_list|,
name|heap
argument_list|,
name|size
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|2
operator|&&
name|heap
index|[
literal|1
index|]
operator|.
name|doc
operator|==
name|list
operator|.
name|doc
condition|)
block|{
name|list
operator|=
name|prepend
argument_list|(
name|heap
index|[
literal|1
index|]
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|// prepend w1 (iterator) to w2 (list)
DECL|method|prepend
specifier|private
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|prepend
parameter_list|(
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|w1
parameter_list|,
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|w2
parameter_list|)
block|{
name|w1
operator|.
name|next
operator|=
name|w2
expr_stmt|;
return|return
name|w1
return|;
block|}
DECL|method|topList
specifier|private
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|topList
parameter_list|(
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|list
parameter_list|,
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
index|[]
name|heap
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|i
parameter_list|)
block|{
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|w
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|w
operator|.
name|doc
operator|==
name|list
operator|.
name|doc
condition|)
block|{
name|list
operator|=
name|prepend
argument_list|(
name|w
argument_list|,
name|list
argument_list|)
expr_stmt|;
specifier|final
name|int
name|left
init|=
name|leftNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|int
name|right
init|=
name|left
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|right
operator|<
name|size
condition|)
block|{
name|list
operator|=
name|topList
argument_list|(
name|list
argument_list|,
name|heap
argument_list|,
name|size
argument_list|,
name|left
argument_list|)
expr_stmt|;
name|list
operator|=
name|topList
argument_list|(
name|list
argument_list|,
name|heap
argument_list|,
name|size
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|left
operator|<
name|size
operator|&&
name|heap
index|[
name|left
index|]
operator|.
name|doc
operator|==
name|list
operator|.
name|doc
condition|)
block|{
name|list
operator|=
name|prepend
argument_list|(
name|heap
index|[
name|left
index|]
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
DECL|method|add
specifier|public
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|add
parameter_list|(
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|entry
parameter_list|)
block|{
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
index|[]
name|heap
init|=
name|this
operator|.
name|heap
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|this
operator|.
name|size
decl_stmt|;
name|heap
index|[
name|size
index|]
operator|=
name|entry
expr_stmt|;
name|upHeap
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
operator|+
literal|1
expr_stmt|;
return|return
name|heap
index|[
literal|0
index|]
return|;
block|}
DECL|method|pop
specifier|public
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|pop
parameter_list|()
block|{
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
index|[]
name|heap
init|=
name|this
operator|.
name|heap
decl_stmt|;
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|result
init|=
name|heap
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|int
name|i
init|=
operator|--
name|size
decl_stmt|;
name|heap
index|[
literal|0
index|]
operator|=
name|heap
index|[
name|i
index|]
expr_stmt|;
name|heap
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|downHeap
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|updateTop
specifier|public
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|updateTop
parameter_list|()
block|{
name|downHeap
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|heap
index|[
literal|0
index|]
return|;
block|}
DECL|method|updateTop
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|updateTop
parameter_list|(
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|topReplacement
parameter_list|)
block|{
name|heap
index|[
literal|0
index|]
operator|=
name|topReplacement
expr_stmt|;
return|return
name|updateTop
argument_list|()
return|;
block|}
DECL|method|upHeap
name|void
name|upHeap
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|nodeDoc
init|=
name|node
operator|.
name|doc
decl_stmt|;
name|int
name|j
init|=
name|parentNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
while|while
condition|(
name|j
operator|>=
literal|0
operator|&&
name|nodeDoc
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|doc
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|parentNode
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
DECL|method|downHeap
name|void
name|downHeap
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
name|node
init|=
name|heap
index|[
literal|0
index|]
decl_stmt|;
name|int
name|j
init|=
name|leftNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|j
operator|<
name|size
condition|)
block|{
name|int
name|k
init|=
name|rightNode
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|<
name|size
operator|&&
name|heap
index|[
name|k
index|]
operator|.
name|doc
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|doc
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
if|if
condition|(
name|heap
index|[
name|j
index|]
operator|.
name|doc
operator|<
name|node
operator|.
name|doc
condition|)
block|{
do|do
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|leftNode
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|k
operator|=
name|rightNode
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|<
name|size
operator|&&
name|heap
index|[
name|k
index|]
operator|.
name|doc
operator|<
name|heap
index|[
name|j
index|]
operator|.
name|doc
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
block|}
do|while
condition|(
name|j
operator|<
name|size
operator|&&
name|heap
index|[
name|j
index|]
operator|.
name|doc
operator|<
name|node
operator|.
name|doc
condition|)
do|;
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|DisiWrapper
argument_list|<
name|Iter
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|heap
argument_list|)
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class
end_unit