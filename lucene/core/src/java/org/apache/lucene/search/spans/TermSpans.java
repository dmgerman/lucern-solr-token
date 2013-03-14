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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DocsAndPositionsEnum
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
name|DocIdSetIterator
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
name|BytesRef
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
name|Collections
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
begin_comment
comment|/**  * Expert:  * Public for extension only  */
end_comment
begin_class
DECL|class|TermSpans
specifier|public
class|class
name|TermSpans
extends|extends
name|Spans
block|{
DECL|field|postings
specifier|protected
specifier|final
name|DocsAndPositionsEnum
name|postings
decl_stmt|;
DECL|field|term
specifier|protected
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|doc
specifier|protected
name|int
name|doc
decl_stmt|;
DECL|field|freq
specifier|protected
name|int
name|freq
decl_stmt|;
DECL|field|count
specifier|protected
name|int
name|count
decl_stmt|;
DECL|field|position
specifier|protected
name|int
name|position
decl_stmt|;
DECL|field|readPayload
specifier|protected
name|boolean
name|readPayload
decl_stmt|;
DECL|method|TermSpans
specifier|public
name|TermSpans
parameter_list|(
name|DocsAndPositionsEnum
name|postings
parameter_list|,
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|postings
operator|=
name|postings
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|// only for EmptyTermSpans (below)
DECL|method|TermSpans
name|TermSpans
parameter_list|()
block|{
name|term
operator|=
literal|null
expr_stmt|;
name|postings
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
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
name|count
operator|==
name|freq
condition|)
block|{
if|if
condition|(
name|postings
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|postings
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
literal|false
return|;
block|}
name|freq
operator|=
name|postings
operator|.
name|freq
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|position
operator|=
name|postings
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|readPayload
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
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
name|doc
operator|=
name|postings
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
literal|false
return|;
block|}
name|freq
operator|=
name|postings
operator|.
name|freq
argument_list|()
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|position
operator|=
name|postings
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|readPayload
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|position
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|postings
operator|.
name|cost
argument_list|()
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|payload
init|=
name|postings
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|readPayload
operator|=
literal|true
expr_stmt|;
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|payload
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readPayload
operator|==
literal|false
operator|&&
name|postings
operator|.
name|getPayload
argument_list|()
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|term
operator|.
name|toString
argument_list|()
operator|+
literal|")@"
operator|+
operator|(
name|doc
operator|==
operator|-
literal|1
condition|?
literal|"START"
else|:
operator|(
name|doc
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|)
condition|?
literal|"END"
else|:
name|doc
operator|+
literal|"-"
operator|+
name|position
operator|)
return|;
block|}
DECL|method|getPostings
specifier|public
name|DocsAndPositionsEnum
name|getPostings
parameter_list|()
block|{
return|return
name|postings
return|;
block|}
DECL|class|EmptyTermSpans
specifier|private
specifier|static
specifier|final
class|class
name|EmptyTermSpans
extends|extends
name|TermSpans
block|{
annotation|@
name|Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|field|EMPTY_TERM_SPANS
specifier|public
specifier|static
specifier|final
name|TermSpans
name|EMPTY_TERM_SPANS
init|=
operator|new
name|EmptyTermSpans
argument_list|()
decl_stmt|;
block|}
end_class
end_unit
