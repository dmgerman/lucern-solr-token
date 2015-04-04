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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
import|;
end_import
begin_comment
comment|/** A {@link NearSpansOrdered} that allows collecting payloads.  * Expert:  * Only public for subclassing.  Most implementations should not need this class  */
end_comment
begin_class
DECL|class|NearSpansPayloadOrdered
specifier|public
class|class
name|NearSpansPayloadOrdered
extends|extends
name|NearSpansOrdered
block|{
DECL|field|matchPayload
specifier|private
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|matchPayload
decl_stmt|;
DECL|field|possibleMatchPayloads
specifier|private
name|Set
argument_list|<
name|byte
index|[]
argument_list|>
name|possibleMatchPayloads
decl_stmt|;
DECL|method|NearSpansPayloadOrdered
specifier|public
name|NearSpansPayloadOrdered
parameter_list|(
name|SpanNearQuery
name|query
parameter_list|,
name|List
argument_list|<
name|Spans
argument_list|>
name|subSpans
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|subSpans
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchPayload
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|possibleMatchPayloads
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/** The subSpans are ordered in the same doc, so there is a possible match.    * Compute the slop while making the match as short as possible by using nextStartPosition    * on all subSpans, except the last one, in reverse order.    * Also collect the payloads.    */
DECL|method|shrinkToAfterShortestMatch
specifier|protected
name|boolean
name|shrinkToAfterShortestMatch
parameter_list|()
throws|throws
name|IOException
block|{
name|Spans
name|lastSubSpans
init|=
name|subSpans
index|[
name|subSpans
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|matchStart
operator|=
name|lastSubSpans
operator|.
name|startPosition
argument_list|()
expr_stmt|;
name|matchEnd
operator|=
name|lastSubSpans
operator|.
name|endPosition
argument_list|()
expr_stmt|;
name|matchPayload
operator|.
name|clear
argument_list|()
expr_stmt|;
name|possibleMatchPayloads
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastSubSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|possibleMatchPayloads
operator|.
name|addAll
argument_list|(
name|lastSubSpans
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|possiblePayload
init|=
literal|null
decl_stmt|;
name|int
name|matchSlop
init|=
literal|0
decl_stmt|;
name|int
name|lastStart
init|=
name|matchStart
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|subSpans
operator|.
name|length
operator|-
literal|2
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Spans
name|prevSpans
init|=
name|subSpans
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|prevSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payload
init|=
name|prevSpans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|possiblePayload
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|payload
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|possiblePayload
operator|.
name|addAll
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
name|int
name|prevStart
init|=
name|prevSpans
operator|.
name|startPosition
argument_list|()
decl_stmt|;
name|int
name|prevEnd
init|=
name|prevSpans
operator|.
name|endPosition
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// prevSpans nextStartPosition until after (lastStart, lastEnd)
if|if
condition|(
name|prevSpans
operator|.
name|nextStartPosition
argument_list|()
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
name|oneExhaustedInCurrentDoc
operator|=
literal|true
expr_stmt|;
break|break;
comment|// Check remaining subSpans for match.
block|}
name|int
name|ppStart
init|=
name|prevSpans
operator|.
name|startPosition
argument_list|()
decl_stmt|;
name|int
name|ppEnd
init|=
name|prevSpans
operator|.
name|endPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|ppEnd
operator|>
name|lastStart
condition|)
block|{
comment|// if overlapping spans
break|break;
comment|// Check remaining subSpans.
block|}
comment|// prevSpans still before (lastStart, lastEnd)
name|prevStart
operator|=
name|ppStart
expr_stmt|;
name|prevEnd
operator|=
name|ppEnd
expr_stmt|;
if|if
condition|(
name|prevSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|payload
init|=
name|prevSpans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|possiblePayload
operator|==
literal|null
condition|)
block|{
name|possiblePayload
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|payload
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|possiblePayload
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|possiblePayload
operator|.
name|addAll
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|possiblePayload
operator|!=
literal|null
condition|)
block|{
name|possibleMatchPayloads
operator|.
name|addAll
argument_list|(
name|possiblePayload
argument_list|)
expr_stmt|;
block|}
assert|assert
name|prevStart
operator|<=
name|matchStart
assert|;
if|if
condition|(
name|matchStart
operator|>
name|prevEnd
condition|)
block|{
comment|// Only non overlapping spans add to slop.
name|matchSlop
operator|+=
operator|(
name|matchStart
operator|-
name|prevEnd
operator|)
expr_stmt|;
block|}
comment|/* Do not break on (matchSlop> allowedSlop) here to make sure        * that on return the first subSpans has nextStartPosition called.        */
name|matchStart
operator|=
name|prevStart
expr_stmt|;
name|lastStart
operator|=
name|prevStart
expr_stmt|;
block|}
name|boolean
name|match
init|=
name|matchSlop
operator|<=
name|allowedSlop
decl_stmt|;
if|if
condition|(
name|match
operator|&&
name|possibleMatchPayloads
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|matchPayload
operator|.
name|addAll
argument_list|(
name|possibleMatchPayloads
argument_list|)
expr_stmt|;
block|}
return|return
name|match
return|;
comment|// ordered and allowed slop
block|}
comment|// TODO: Remove warning after API has been finalized
comment|// TODO: Would be nice to be able to lazy load payloads
comment|/** Return payloads when available. */
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
return|return
name|matchPayload
return|;
block|}
comment|/** Indicates whether payloads are available */
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
operator|!
name|matchPayload
operator|.
name|isEmpty
argument_list|()
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
literal|"NearSpansPayloadOrdered("
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|")@"
operator|+
name|docID
argument_list|()
operator|+
literal|": "
operator|+
name|startPosition
argument_list|()
operator|+
literal|" - "
operator|+
name|endPosition
argument_list|()
return|;
block|}
block|}
end_class
end_unit