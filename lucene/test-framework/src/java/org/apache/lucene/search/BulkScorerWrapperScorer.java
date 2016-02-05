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
name|Arrays
import|;
end_import
begin_comment
comment|/**  * A {@link BulkScorer}-backed scorer.  */
end_comment
begin_class
DECL|class|BulkScorerWrapperScorer
specifier|public
class|class
name|BulkScorerWrapperScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|BulkScorer
name|scorer
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|next
specifier|private
name|int
name|next
init|=
literal|0
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|int
index|[]
name|docs
decl_stmt|;
DECL|field|freqs
specifier|private
specifier|final
name|int
index|[]
name|freqs
decl_stmt|;
DECL|field|scores
specifier|private
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|BulkScorerWrapperScorer
specifier|public
name|BulkScorerWrapperScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|BulkScorer
name|scorer
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|docs
operator|=
operator|new
name|int
index|[
name|bufferSize
index|]
expr_stmt|;
name|freqs
operator|=
operator|new
name|int
index|[
name|bufferSize
index|]
expr_stmt|;
name|scores
operator|=
operator|new
name|float
index|[
name|bufferSize
index|]
expr_stmt|;
block|}
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|bufferLength
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|next
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|&&
name|bufferLength
operator|==
literal|0
condition|)
block|{
specifier|final
name|int
name|min
init|=
name|Math
operator|.
name|max
argument_list|(
name|target
argument_list|,
name|next
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|min
operator|+
name|docs
operator|.
name|length
decl_stmt|;
name|next
operator|=
name|scorer
operator|.
name|score
argument_list|(
operator|new
name|LeafCollector
argument_list|()
block|{
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|docs
index|[
name|bufferLength
index|]
operator|=
name|doc
expr_stmt|;
name|freqs
index|[
name|bufferLength
index|]
operator|=
name|scorer
operator|.
name|freq
argument_list|()
expr_stmt|;
name|scores
index|[
name|bufferLength
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|bufferLength
operator|+=
literal|1
expr_stmt|;
block|}
block|}
argument_list|,
literal|null
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
operator|-
literal|1
expr_stmt|;
block|}
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
return|return
name|scores
index|[
name|i
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|freqs
index|[
name|i
index|]
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
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|docID
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|bufferLength
operator|==
literal|0
operator|||
name|docs
index|[
name|bufferLength
operator|-
literal|1
index|]
operator|<
name|target
condition|)
block|{
name|refill
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
name|i
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|docs
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|bufferLength
argument_list|,
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|i
operator|=
operator|-
literal|1
operator|-
name|i
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
name|bufferLength
condition|)
block|{
return|return
name|doc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
return|return
name|doc
operator|=
name|docs
index|[
name|i
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
