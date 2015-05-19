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
name|PostingsEnum
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
name|search
operator|.
name|DocIdSetIterator
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
name|Objects
import|;
end_import
begin_comment
comment|/**  * Expert:  * Public for extension only.  * This does not work correctly for terms that indexed at position Integer.MAX_VALUE.  */
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
name|PostingsEnum
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
name|PostingsEnum
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|postings
argument_list|)
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|position
operator|=
operator|-
literal|1
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
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|freq
operator|=
name|postings
operator|.
name|freq
argument_list|()
expr_stmt|;
assert|assert
name|freq
operator|>=
literal|1
assert|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|position
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|doc
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
assert|assert
name|target
operator|>
name|doc
assert|;
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
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|freq
operator|=
name|postings
operator|.
name|freq
argument_list|()
expr_stmt|;
assert|assert
name|freq
operator|>=
literal|1
assert|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
name|position
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|doc
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
DECL|method|nextStartPosition
specifier|public
name|int
name|nextStartPosition
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
assert|assert
name|position
operator|!=
name|NO_MORE_POSITIONS
assert|;
return|return
name|position
operator|=
name|NO_MORE_POSITIONS
return|;
block|}
name|int
name|prevPosition
init|=
name|position
decl_stmt|;
name|position
operator|=
name|postings
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
assert|assert
name|position
operator|>=
name|prevPosition
operator|:
literal|"prevPosition="
operator|+
name|prevPosition
operator|+
literal|"> position="
operator|+
name|position
assert|;
assert|assert
name|position
operator|!=
name|NO_MORE_POSITIONS
assert|;
comment|// int endPosition not possible
name|count
operator|++
expr_stmt|;
name|readPayload
operator|=
literal|false
expr_stmt|;
return|return
name|position
return|;
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
return|return
operator|(
name|position
operator|==
operator|-
literal|1
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
name|position
operator|!=
name|NO_MORE_POSITIONS
operator|)
condition|?
name|position
operator|+
literal|1
else|:
name|NO_MORE_POSITIONS
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
comment|/*   @Override   public Collection<byte[]> getPayload() throws IOException {     final BytesRef payload = postings.getPayload();     readPayload = true;     final byte[] bytes;     if (payload != null) {       bytes = new byte[payload.length];       System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);     } else {       bytes = null;     }     return Collections.singletonList(bytes);   }    @Override   public boolean isPayloadAvailable() throws IOException {     return readPayload == false&& postings.getPayload() != null;   }   */
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|collectLeaf
argument_list|(
name|postings
argument_list|,
name|term
argument_list|)
expr_stmt|;
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
name|NO_MORE_DOCS
operator|)
condition|?
literal|"ENDDOC"
else|:
name|doc
operator|+
literal|" - "
operator|+
operator|(
name|position
operator|==
name|NO_MORE_POSITIONS
condition|?
literal|"ENDPOS"
else|:
name|position
operator|)
operator|)
return|;
block|}
DECL|method|getPostings
specifier|public
name|PostingsEnum
name|getPostings
parameter_list|()
block|{
return|return
name|postings
return|;
block|}
block|}
end_class
end_unit
