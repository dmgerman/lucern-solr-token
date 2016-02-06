begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
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
name|tokenattributes
operator|.
name|PositionLengthAttribute
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
name|CharacterUtils
import|;
end_import
begin_comment
comment|/**  * Tokenizes the given token into n-grams of given size(s).  *<p>  * This {@link TokenFilter} create n-grams from the beginning edge of a input token.  *<p><a name="match_version"></a>As of Lucene 4.4, this filter handles correctly  * supplementary characters.  */
end_comment
begin_class
DECL|class|EdgeNGramTokenFilter
specifier|public
specifier|final
class|class
name|EdgeNGramTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|DEFAULT_MAX_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_GRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MIN_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_GRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|charUtils
specifier|private
specifier|final
name|CharacterUtils
name|charUtils
decl_stmt|;
DECL|field|minGram
specifier|private
specifier|final
name|int
name|minGram
decl_stmt|;
DECL|field|maxGram
specifier|private
specifier|final
name|int
name|maxGram
decl_stmt|;
DECL|field|curTermBuffer
specifier|private
name|char
index|[]
name|curTermBuffer
decl_stmt|;
DECL|field|curTermLength
specifier|private
name|int
name|curTermLength
decl_stmt|;
DECL|field|curCodePointCount
specifier|private
name|int
name|curCodePointCount
decl_stmt|;
DECL|field|curGramSize
specifier|private
name|int
name|curGramSize
decl_stmt|;
DECL|field|tokStart
specifier|private
name|int
name|tokStart
decl_stmt|;
DECL|field|tokEnd
specifier|private
name|int
name|tokEnd
decl_stmt|;
comment|// only used if the length changed before this filter
DECL|field|savePosIncr
specifier|private
name|int
name|savePosIncr
decl_stmt|;
DECL|field|savePosLen
specifier|private
name|int
name|savePosLen
decl_stmt|;
DECL|field|termAtt
specifier|private
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
specifier|private
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
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posLenAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLenAtt
init|=
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range    *    * @param input {@link TokenStream} holding the input to be tokenized    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenFilter
specifier|public
name|EdgeNGramTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|minGram
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must be greater than zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minGram
operator|>
name|maxGram
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must not be greater than maxGram"
argument_list|)
throw|;
block|}
name|this
operator|.
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|this
operator|.
name|minGram
operator|=
name|minGram
expr_stmt|;
name|this
operator|.
name|maxGram
operator|=
name|maxGram
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
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|curTermBuffer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|curTermBuffer
operator|=
name|termAtt
operator|.
name|buffer
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
name|curTermLength
operator|=
name|termAtt
operator|.
name|length
argument_list|()
expr_stmt|;
name|curCodePointCount
operator|=
name|charUtils
operator|.
name|codePointCount
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
name|curGramSize
operator|=
name|minGram
expr_stmt|;
name|tokStart
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|tokEnd
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
name|savePosIncr
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|savePosLen
operator|=
name|posLenAtt
operator|.
name|getPositionLength
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|curGramSize
operator|<=
name|maxGram
condition|)
block|{
comment|// if we have hit the end of our n-gram size range, quit
if|if
condition|(
name|curGramSize
operator|<=
name|curCodePointCount
condition|)
block|{
comment|// if the remaining input is too short, we can't generate any n-grams
comment|// grab gramSize chars from front or back
name|clearAttributes
argument_list|()
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tokStart
argument_list|,
name|tokEnd
argument_list|)
expr_stmt|;
comment|// first ngram gets increment, others don't
if|if
condition|(
name|curGramSize
operator|==
name|minGram
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|savePosIncr
argument_list|)
expr_stmt|;
name|savePosIncr
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|posLenAtt
operator|.
name|setPositionLength
argument_list|(
name|savePosLen
argument_list|)
expr_stmt|;
specifier|final
name|int
name|charLength
init|=
name|charUtils
operator|.
name|offsetByCodePoints
argument_list|(
name|curTermBuffer
argument_list|,
literal|0
argument_list|,
name|curTermLength
argument_list|,
literal|0
argument_list|,
name|curGramSize
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|curTermBuffer
argument_list|,
literal|0
argument_list|,
name|charLength
argument_list|)
expr_stmt|;
name|curGramSize
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|curTermBuffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
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
name|curTermBuffer
operator|=
literal|null
expr_stmt|;
name|savePosIncr
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class
end_unit
