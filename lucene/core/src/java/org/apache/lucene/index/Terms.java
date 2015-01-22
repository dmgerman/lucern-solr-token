begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRefBuilder
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
name|automaton
operator|.
name|CompiledAutomaton
import|;
end_import
begin_comment
comment|/**  * Access to the terms in a specific field.  See {@link Fields}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Terms
specifier|public
specifier|abstract
class|class
name|Terms
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|Terms
specifier|protected
name|Terms
parameter_list|()
block|{   }
comment|/** Returns an iterator that will step through all    *  terms. This method will not return null.  If you have    *  a previous TermsEnum, for example from a different    *  field, you can pass it for possible reuse if the    *  implementation can do so. */
DECL|method|iterator
specifier|public
specifier|abstract
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a TermsEnum that iterates over all terms that    *  are accepted by the provided {@link    *  CompiledAutomaton}.  If the<code>startTerm</code> is    *  provided then the returned enum will only accept terms    *  {@code> startTerm}, but you still must call    *  next() first to get to the first term.  Note that the    *  provided<code>startTerm</code> must be accepted by    *  the automaton.    *    *<p><b>NOTE</b>: the returned TermsEnum cannot    * seek</p>. */
DECL|method|intersect
specifier|public
name|TermsEnum
name|intersect
parameter_list|(
name|CompiledAutomaton
name|compiled
parameter_list|,
specifier|final
name|BytesRef
name|startTerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: could we factor out a common interface b/w
comment|// CompiledAutomaton and FST?  Then we could pass FST there too,
comment|// and likely speed up resolving terms to deleted docs ... but
comment|// AutomatonTermsEnum makes this tricky because of its on-the-fly cycle
comment|// detection
comment|// TODO: eventually we could support seekCeil/Exact on
comment|// the returned enum, instead of only being able to seek
comment|// at the start
if|if
condition|(
name|compiled
operator|.
name|type
operator|!=
name|CompiledAutomaton
operator|.
name|AUTOMATON_TYPE
operator|.
name|NORMAL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"please use CompiledAutomaton.getTermsEnum instead"
argument_list|)
throw|;
block|}
if|if
condition|(
name|startTerm
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|AutomatonTermsEnum
argument_list|(
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|compiled
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|AutomatonTermsEnum
argument_list|(
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|compiled
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|term
operator|=
name|startTerm
expr_stmt|;
block|}
return|return
name|super
operator|.
name|nextSeekTerm
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Returns the number of terms for this field, or -1 if this     *  measure isn't stored by the codec. Note that, just like     *  other term measures, this measure does not take deleted     *  documents into account. */
DECL|method|size
specifier|public
specifier|abstract
name|long
name|size
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the sum of {@link TermsEnum#totalTermFreq} for    *  all terms in this field, or -1 if this measure isn't    *  stored by the codec (or if this fields omits term freq    *  and positions).  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getSumTotalTermFreq
specifier|public
specifier|abstract
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the sum of {@link TermsEnum#docFreq()} for    *  all terms in this field, or -1 if this measure isn't    *  stored by the codec.  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getSumDocFreq
specifier|public
specifier|abstract
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of documents that have at least one    *  term for this field, or -1 if this measure isn't    *  stored by the codec.  Note that, just like other term    *  measures, this measure does not take deleted documents    *  into account. */
DECL|method|getDocCount
specifier|public
specifier|abstract
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns true if documents in this field store    *  per-document term frequency ({@link DocsEnum#freq}). */
DECL|method|hasFreqs
specifier|public
specifier|abstract
name|boolean
name|hasFreqs
parameter_list|()
function_decl|;
comment|/** Returns true if documents in this field store offsets. */
DECL|method|hasOffsets
specifier|public
specifier|abstract
name|boolean
name|hasOffsets
parameter_list|()
function_decl|;
comment|/** Returns true if documents in this field store positions. */
DECL|method|hasPositions
specifier|public
specifier|abstract
name|boolean
name|hasPositions
parameter_list|()
function_decl|;
comment|/** Returns true if documents in this field store payloads. */
DECL|method|hasPayloads
specifier|public
specifier|abstract
name|boolean
name|hasPayloads
parameter_list|()
function_decl|;
comment|/** Zero-length array of {@link Terms}. */
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|Terms
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Terms
index|[
literal|0
index|]
decl_stmt|;
comment|/** Returns the smallest term (in lexicographic order) in the field.     *  Note that, just like other term measures, this measure does not     *  take deleted documents into account.  This returns    *  null when there are no terms. */
DECL|method|getMin
specifier|public
name|BytesRef
name|getMin
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|iterator
argument_list|(
literal|null
argument_list|)
operator|.
name|next
argument_list|()
return|;
block|}
comment|/** Returns the largest term (in lexicographic order) in the field.     *  Note that, just like other term measures, this measure does not     *  take deleted documents into account.  This returns    *  null when there are no terms. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|getMax
specifier|public
name|BytesRef
name|getMax
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
init|=
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
comment|// empty: only possible from a FilteredTermsEnum...
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|>=
literal|0
condition|)
block|{
comment|// try to seek-by-ord
try|try
block|{
name|TermsEnum
name|iterator
init|=
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|iterator
operator|.
name|seekExact
argument_list|(
name|size
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|iterator
operator|.
name|term
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
comment|// otherwise: binary search
name|TermsEnum
name|iterator
init|=
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|v
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
comment|// empty: only possible from a FilteredTermsEnum...
return|return
name|v
return|;
block|}
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|scratch
operator|.
name|append
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|// Iterates over digits:
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|low
init|=
literal|0
decl_stmt|;
name|int
name|high
init|=
literal|256
decl_stmt|;
comment|// Binary search current digit to find the highest
comment|// digit before END:
while|while
condition|(
name|low
operator|!=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|scratch
operator|.
name|setByteAt
argument_list|(
name|scratch
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
operator|(
name|byte
operator|)
name|mid
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|seekCeil
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|)
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
comment|// Scratch was too high
if|if
condition|(
name|mid
operator|==
literal|0
condition|)
block|{
name|scratch
operator|.
name|setLength
argument_list|(
name|scratch
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|scratch
operator|.
name|get
argument_list|()
return|;
block|}
name|high
operator|=
name|mid
expr_stmt|;
block|}
else|else
block|{
comment|// Scratch was too low; there is at least one term
comment|// still after it:
if|if
condition|(
name|low
operator|==
name|mid
condition|)
block|{
break|break;
block|}
name|low
operator|=
name|mid
expr_stmt|;
block|}
block|}
comment|// Recurse to next digit:
name|scratch
operator|.
name|setLength
argument_list|(
name|scratch
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|grow
argument_list|(
name|scratch
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Expert: returns additional information about this Terms instance    * for debugging purposes.    */
DECL|method|getStats
specifier|public
name|Object
name|getStats
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"impl="
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",size="
operator|+
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",docCount="
operator|+
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",sumTotalTermFreq="
operator|+
name|getSumTotalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",sumDocFreq="
operator|+
name|getSumDocFreq
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
