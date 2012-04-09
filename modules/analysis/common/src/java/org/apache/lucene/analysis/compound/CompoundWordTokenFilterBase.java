begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|LinkedList
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
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
name|AttributeSource
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Base class for decomposition token filters.  *<p>  * You must specify the required {@link Version} compatibility when creating  * CompoundWordTokenFilterBase:  *<ul>  *<li>As of 3.1, CompoundWordTokenFilterBase correctly handles Unicode 4.0  * supplementary characters in strings and char arrays provided as compound word  * dictionaries.  *</ul>  */
end_comment
begin_class
DECL|class|CompoundWordTokenFilterBase
specifier|public
specifier|abstract
class|class
name|CompoundWordTokenFilterBase
extends|extends
name|TokenFilter
block|{
comment|/**    * The default for minimal word length that gets decomposed    */
DECL|field|DEFAULT_MIN_WORD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_WORD_SIZE
init|=
literal|5
decl_stmt|;
comment|/**    * The default for minimal length of subwords that get propagated to the output of this filter    */
DECL|field|DEFAULT_MIN_SUBWORD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_SUBWORD_SIZE
init|=
literal|2
decl_stmt|;
comment|/**    * The default for maximal length of subwords that get propagated to the output of this filter    */
DECL|field|DEFAULT_MAX_SUBWORD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_SUBWORD_SIZE
init|=
literal|15
decl_stmt|;
DECL|field|dictionary
specifier|protected
specifier|final
name|CharArraySet
name|dictionary
decl_stmt|;
DECL|field|tokens
specifier|protected
specifier|final
name|LinkedList
argument_list|<
name|CompoundToken
argument_list|>
name|tokens
decl_stmt|;
DECL|field|minWordSize
specifier|protected
specifier|final
name|int
name|minWordSize
decl_stmt|;
DECL|field|minSubwordSize
specifier|protected
specifier|final
name|int
name|minSubwordSize
decl_stmt|;
DECL|field|maxSubwordSize
specifier|protected
specifier|final
name|int
name|maxSubwordSize
decl_stmt|;
DECL|field|onlyLongestMatch
specifier|protected
specifier|final
name|boolean
name|onlyLongestMatch
decl_stmt|;
DECL|field|termAtt
specifier|protected
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|protected
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|current
specifier|private
name|AttributeSource
operator|.
name|State
name|current
decl_stmt|;
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
name|onlyLongestMatch
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|,
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundWordTokenFilterBase
specifier|protected
name|CompoundWordTokenFilterBase
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|CharArraySet
name|dictionary
parameter_list|,
name|int
name|minWordSize
parameter_list|,
name|int
name|minSubwordSize
parameter_list|,
name|int
name|maxSubwordSize
parameter_list|,
name|boolean
name|onlyLongestMatch
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokens
operator|=
operator|new
name|LinkedList
argument_list|<
name|CompoundToken
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|minWordSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minWordSize cannot be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minWordSize
operator|=
name|minWordSize
expr_stmt|;
if|if
condition|(
name|minSubwordSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minSubwordSize cannot be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minSubwordSize
operator|=
name|minSubwordSize
expr_stmt|;
if|if
condition|(
name|maxSubwordSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxSubwordSize cannot be negative"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxSubwordSize
operator|=
name|maxSubwordSize
expr_stmt|;
name|this
operator|.
name|onlyLongestMatch
operator|=
name|onlyLongestMatch
expr_stmt|;
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
assert|assert
name|current
operator|!=
literal|null
assert|;
name|CompoundToken
name|token
init|=
name|tokens
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|restoreState
argument_list|(
name|current
argument_list|)
expr_stmt|;
comment|// keep all other attributes untouched
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|token
operator|.
name|txt
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|,
name|token
operator|.
name|endOffset
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|current
operator|=
literal|null
expr_stmt|;
comment|// not really needed, but for safety
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|// Only words longer than minWordSize get processed
if|if
condition|(
name|termAtt
operator|.
name|length
argument_list|()
operator|>=
name|this
operator|.
name|minWordSize
condition|)
block|{
name|decompose
argument_list|()
expr_stmt|;
comment|// only capture the state if we really need it for producing new tokens
if|if
condition|(
operator|!
name|tokens
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|current
operator|=
name|captureState
argument_list|()
expr_stmt|;
block|}
block|}
comment|// return original token:
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Decomposes the current {@link #termAtt} and places {@link CompoundToken} instances in the {@link #tokens} list.    * The original token may not be placed in the list, as it is automatically passed through this filter.    */
DECL|method|decompose
specifier|protected
specifier|abstract
name|void
name|decompose
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tokens
operator|.
name|clear
argument_list|()
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Helper class to hold decompounded token information    */
DECL|class|CompoundToken
specifier|protected
class|class
name|CompoundToken
block|{
DECL|field|txt
specifier|public
specifier|final
name|CharSequence
name|txt
decl_stmt|;
DECL|field|startOffset
DECL|field|endOffset
specifier|public
specifier|final
name|int
name|startOffset
decl_stmt|,
name|endOffset
decl_stmt|;
comment|/** Construct the compound token based on a slice of the current {@link CompoundWordTokenFilterBase#termAtt}. */
DECL|method|CompoundToken
specifier|public
name|CompoundToken
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|txt
operator|=
name|CompoundWordTokenFilterBase
operator|.
name|this
operator|.
name|termAtt
operator|.
name|subSequence
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|)
expr_stmt|;
comment|// offsets of the original word
name|int
name|startOff
init|=
name|CompoundWordTokenFilterBase
operator|.
name|this
operator|.
name|offsetAtt
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOff
init|=
name|CompoundWordTokenFilterBase
operator|.
name|this
operator|.
name|offsetAtt
operator|.
name|endOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|endOff
operator|-
name|startOff
operator|!=
name|CompoundWordTokenFilterBase
operator|.
name|this
operator|.
name|termAtt
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// if length by start + end offsets doesn't match the term text then assume
comment|// this is a synonym and don't adjust the offsets.
name|this
operator|.
name|startOffset
operator|=
name|startOff
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOff
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|newStart
init|=
name|startOff
operator|+
name|offset
decl_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|newStart
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|newStart
operator|+
name|length
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
