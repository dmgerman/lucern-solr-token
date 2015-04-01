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
comment|/** A Spans that is formed from the ordered subspans of a SpanNearQuery  * where the subspans do not overlap and have a maximum slop between them,  * and that does not need to collect payloads.  * To also collect payloads, see {@link NearSpansPayloadOrdered}.  *<p>  * The formed spans only contains minimum slop matches.<br>  * The matching slop is computed from the distance(s) between  * the non overlapping matching Spans.<br>  * Successive matches are always formed from the successive Spans  * of the SpanNearQuery.  *<p>  * The formed spans may contain overlaps when the slop is at least 1.  * For example, when querying using  *<pre>t1 t2 t3</pre>  * with slop at least 1, the fragment:  *<pre>t1 t2 t1 t3 t2 t3</pre>  * matches twice:  *<pre>t1 t2 .. t3</pre>  *<pre>      t1 .. t2 t3</pre>  *  *  * Expert:  * Only public for subclassing.  Most implementations should not need this class  */
end_comment
begin_class
DECL|class|NearSpansOrdered
specifier|public
class|class
name|NearSpansOrdered
extends|extends
name|NearSpans
block|{
DECL|field|matchDoc
specifier|protected
name|int
name|matchDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|matchStart
specifier|protected
name|int
name|matchStart
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|matchEnd
specifier|protected
name|int
name|matchEnd
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|NearSpansOrdered
specifier|public
name|NearSpansOrdered
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
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
comment|// -1 startPosition/endPosition also at doc -1
block|}
comment|/** Advances the subSpans to just after an ordered match with a minimum slop    * that is smaller than the slop allowed by the SpanNearQuery.    * @return true iff there is such a match.    */
annotation|@
name|Override
DECL|method|toMatchDoc
name|int
name|toMatchDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|subSpansToFirstStartPosition
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|stretchToOrder
argument_list|()
condition|)
block|{
if|if
condition|(
name|conjunction
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
name|subSpansToFirstStartPosition
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|shrinkToAfterShortestMatch
argument_list|()
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
name|conjunction
operator|.
name|docID
argument_list|()
return|;
block|}
comment|// not a match, after shortest ordered spans, not at beginning of doc.
if|if
condition|(
name|oneExhaustedInCurrentDoc
condition|)
block|{
if|if
condition|(
name|conjunction
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
name|subSpansToFirstStartPosition
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|twoPhaseCurrentDocMatches
name|boolean
name|twoPhaseCurrentDocMatches
parameter_list|()
throws|throws
name|IOException
block|{
name|subSpansToFirstStartPosition
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|stretchToOrder
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|shrinkToAfterShortestMatch
argument_list|()
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// not a match, after shortest ordered spans
if|if
condition|(
name|oneExhaustedInCurrentDoc
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
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
name|atFirstInCurrentDoc
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|false
expr_stmt|;
return|return
name|matchStart
return|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|oneExhaustedInCurrentDoc
condition|)
block|{
name|matchStart
operator|=
name|NO_MORE_POSITIONS
expr_stmt|;
name|matchEnd
operator|=
name|NO_MORE_POSITIONS
expr_stmt|;
return|return
name|NO_MORE_POSITIONS
return|;
block|}
if|if
condition|(
operator|!
name|stretchToOrder
argument_list|()
condition|)
block|{
name|matchStart
operator|=
name|NO_MORE_POSITIONS
expr_stmt|;
name|matchEnd
operator|=
name|NO_MORE_POSITIONS
expr_stmt|;
return|return
name|NO_MORE_POSITIONS
return|;
block|}
if|if
condition|(
name|shrinkToAfterShortestMatch
argument_list|()
condition|)
block|{
comment|// may also leave oneExhaustedInCurrentDoc
return|return
name|matchStart
return|;
block|}
comment|// after shortest ordered spans, or oneExhaustedInCurrentDoc
block|}
block|}
DECL|method|subSpansToFirstStartPosition
specifier|private
name|void
name|subSpansToFirstStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|Spans
name|spans
range|:
name|subSpans
control|)
block|{
assert|assert
name|spans
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
operator|:
literal|"spans="
operator|+
name|spans
assert|;
name|spans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
assert|assert
name|spans
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
block|}
name|oneExhaustedInCurrentDoc
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Order the subSpans within the same document by using nextStartPosition on all subSpans    * after the first as little as necessary.    * Return true when the subSpans could be ordered in this way,    * otherwise at least one is exhausted in the current doc.    */
DECL|method|stretchToOrder
specifier|private
name|boolean
name|stretchToOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|Spans
name|prevSpans
init|=
name|subSpans
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|prevSpans
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
operator|:
literal|"prevSpans no start position "
operator|+
name|prevSpans
assert|;
assert|assert
name|prevSpans
operator|.
name|endPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|subSpans
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Spans
name|spans
init|=
name|subSpans
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
assert|assert
name|spans
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
assert|assert
name|spans
operator|.
name|endPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
while|while
condition|(
name|prevSpans
operator|.
name|endPosition
argument_list|()
operator|>
name|spans
operator|.
name|startPosition
argument_list|()
condition|)
block|{
comment|// while overlapping spans
if|if
condition|(
name|spans
operator|.
name|nextStartPosition
argument_list|()
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|prevSpans
operator|=
name|spans
expr_stmt|;
block|}
return|return
literal|true
return|;
comment|// all subSpans ordered and non overlapping
block|}
comment|/** The subSpans are ordered in the same doc, so there is a possible match.    * Compute the slop while making the match as short as possible by using nextStartPosition    * on all subSpans, except the last one, in reverse order.    */
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
operator|.
name|get
argument_list|(
name|subSpans
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
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
name|int
name|lastEnd
init|=
name|matchEnd
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|subSpans
operator|.
name|size
argument_list|()
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
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
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
name|lastEnd
operator|=
name|prevEnd
expr_stmt|;
block|}
name|boolean
name|match
init|=
name|matchSlop
operator|<=
name|allowedSlop
decl_stmt|;
return|return
name|match
return|;
comment|// ordered and allowed slop
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
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|matchStart
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
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|matchEnd
return|;
block|}
comment|/** Throws an UnsupportedOperationException */
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Use NearSpansPayloadOrdered instead"
argument_list|)
throw|;
block|}
comment|/** Throws an UnsupportedOperationException */
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Use NearSpansPayloadOrdered instead"
argument_list|)
throw|;
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
literal|"NearSpansOrdered("
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
