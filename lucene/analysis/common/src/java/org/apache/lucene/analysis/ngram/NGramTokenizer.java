begin_unit
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
name|io
operator|.
name|Reader
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
name|Tokenizer
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
comment|/**  * Tokenizes the input into n-grams of the given size(s).  *<p>On the contrary to {@link NGramTokenFilter}, this class sets offsets so  * that characters between startOffset and endOffset in the original stream are  * the same as the term chars.  *<p>For example, "abcde" would be tokenized as (minGram=2, maxGram=3):  *<table>  *<tr><th>Term</th><td>ab</td><td>abc</td><td>bc</td><td>bcd</td><td>cd</td><td>cde</td><td>de</td></tr>  *<tr><th>Position increment</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td></tr>  *<tr><th>Position length</th><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td></tr>  *<tr><th>Offsets</th><td>[0,2[</td><td>[0,3[</td><td>[1,3[</td><td>[1,4[</td><td>[2,4[</td><td>[2,5[</td><td>[3,5[</td></tr>  *</table>  *<a name="version"/>  *<p>This tokenizer changed a lot in Lucene 4.4 in order to:<ul>  *<li>tokenize in a streaming fashion to support streams which are larger  * than 1024 chars (limit of the previous version),  *<li>count grams based on unicode code points instead of java chars (and  * never split in the middle of surrogate pairs),  *<li>give the ability to {@link #isTokenChar(int) pre-tokenize} the stream  * before computing n-grams.</ul>  *<p>Additionally, this class doesn't trim trailing whitespaces and emits  * tokens in a different order, tokens are now emitted by increasing start  * offsets while they used to be emitted by increasing lengths (which prevented  * from supporting large input streams).  *<p>Although<b style="color:red">highly</b> discouraged, it is still possible  * to use the old behavior through {@link Lucene43NGramTokenizer}.  */
end_comment
begin_comment
comment|// non-final to allow for overriding isTokenChar, but all other methods should be final
end_comment
begin_class
DECL|class|NGramTokenizer
specifier|public
class|class
name|NGramTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_MIN_NGRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_NGRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MAX_NGRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_NGRAM_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|charUtils
specifier|private
name|CharacterUtils
name|charUtils
decl_stmt|;
DECL|field|charBuffer
specifier|private
name|CharacterUtils
operator|.
name|CharacterBuffer
name|charBuffer
decl_stmt|;
DECL|field|buffer
specifier|private
name|int
index|[]
name|buffer
decl_stmt|;
comment|// like charBuffer, but converted to code points
DECL|field|bufferStart
DECL|field|bufferEnd
specifier|private
name|int
name|bufferStart
decl_stmt|,
name|bufferEnd
decl_stmt|;
comment|// remaining slice in buffer
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
DECL|field|gramSize
specifier|private
name|int
name|gramSize
decl_stmt|;
DECL|field|minGram
DECL|field|maxGram
specifier|private
name|int
name|minGram
decl_stmt|,
name|maxGram
decl_stmt|;
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
decl_stmt|;
DECL|field|lastCheckedChar
specifier|private
name|int
name|lastCheckedChar
decl_stmt|;
comment|// last offset in the buffer that we checked
DECL|field|lastNonTokenChar
specifier|private
name|int
name|lastNonTokenChar
decl_stmt|;
comment|// last offset that we found to not be a token char
DECL|field|edgesOnly
specifier|private
name|boolean
name|edgesOnly
decl_stmt|;
comment|// leading edges n-grams only
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
DECL|method|NGramTokenizer
name|NGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|,
name|boolean
name|edgesOnly
parameter_list|)
block|{
name|init
argument_list|(
name|version
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|edgesOnly
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates NGramTokenizer with given min and max n-grams.    * @param version the lucene compatibility<a href="#version">version</a>    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|NGramTokenizer
specifier|public
name|NGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|NGramTokenizer
name|NGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|,
name|boolean
name|edgesOnly
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|version
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|,
name|edgesOnly
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates NGramTokenizer with given min and max n-grams.    * @param version the lucene compatibility<a href="#version">version</a>    * @param factory {@link org.apache.lucene.util.AttributeSource.AttributeFactory} to use    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|NGramTokenizer
specifier|public
name|NGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
name|factory
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates NGramTokenizer with default min and max n-grams.    * @param version the lucene compatibility<a href="#version">version</a>    */
DECL|method|NGramTokenizer
specifier|public
name|NGramTokenizer
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|,
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Version
name|version
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|,
name|boolean
name|edgesOnly
parameter_list|)
block|{
if|if
condition|(
operator|!
name|edgesOnly
operator|&&
operator|!
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_4
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This class only works with Lucene 4.4+. To emulate the old (broken) behavior of NGramTokenizer, use Lucene43NGramTokenizer"
argument_list|)
throw|;
block|}
name|charUtils
operator|=
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_4
argument_list|)
condition|?
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|version
argument_list|)
else|:
name|CharacterUtils
operator|.
name|getJava4Instance
argument_list|()
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
name|this
operator|.
name|edgesOnly
operator|=
name|edgesOnly
expr_stmt|;
name|charBuffer
operator|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
literal|2
operator|*
name|maxGram
operator|+
literal|1024
argument_list|)
expr_stmt|;
comment|// 2 * maxGram in case all code points require 2 chars and + 1024 for buffering to not keep polling the Reader
name|buffer
operator|=
operator|new
name|int
index|[
name|charBuffer
operator|.
name|getBuffer
argument_list|()
operator|.
name|length
index|]
expr_stmt|;
comment|// Make the term att large enough
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
literal|2
operator|*
name|maxGram
argument_list|)
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
name|clearAttributes
argument_list|()
expr_stmt|;
comment|// termination of this loop is guaranteed by the fact that every iteration
comment|// either advances the buffer (calls consumes()) or increases gramSize
while|while
condition|(
literal|true
condition|)
block|{
comment|// compact
if|if
condition|(
name|bufferStart
operator|>=
name|bufferEnd
operator|-
name|maxGram
operator|-
literal|1
operator|&&
operator|!
name|exhausted
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferStart
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferEnd
operator|-
name|bufferStart
argument_list|)
expr_stmt|;
name|bufferEnd
operator|-=
name|bufferStart
expr_stmt|;
name|lastCheckedChar
operator|-=
name|bufferStart
expr_stmt|;
name|lastNonTokenChar
operator|-=
name|bufferStart
expr_stmt|;
name|bufferStart
operator|=
literal|0
expr_stmt|;
comment|// fill in remaining space
name|exhausted
operator|=
operator|!
name|charUtils
operator|.
name|fill
argument_list|(
name|charBuffer
argument_list|,
name|input
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|bufferEnd
argument_list|)
expr_stmt|;
comment|// convert to code points
name|bufferEnd
operator|+=
name|charUtils
operator|.
name|toCodePoints
argument_list|(
name|charBuffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|charBuffer
operator|.
name|getLength
argument_list|()
argument_list|,
name|buffer
argument_list|,
name|bufferEnd
argument_list|)
expr_stmt|;
block|}
comment|// should we go to the next offset?
if|if
condition|(
name|gramSize
operator|>
name|maxGram
operator|||
operator|(
name|bufferStart
operator|+
name|gramSize
operator|)
operator|>
name|bufferEnd
condition|)
block|{
if|if
condition|(
name|bufferStart
operator|+
literal|1
operator|+
name|minGram
operator|>
name|bufferEnd
condition|)
block|{
assert|assert
name|exhausted
assert|;
return|return
literal|false
return|;
block|}
name|consume
argument_list|()
expr_stmt|;
name|gramSize
operator|=
name|minGram
expr_stmt|;
block|}
name|updateLastNonTokenChar
argument_list|()
expr_stmt|;
comment|// retry if the token to be emitted was going to not only contain token chars
specifier|final
name|boolean
name|termContainsNonTokenChar
init|=
name|lastNonTokenChar
operator|>=
name|bufferStart
operator|&&
name|lastNonTokenChar
operator|<
operator|(
name|bufferStart
operator|+
name|gramSize
operator|)
decl_stmt|;
specifier|final
name|boolean
name|isEdgeAndPreviousCharIsTokenChar
init|=
name|edgesOnly
operator|&&
name|lastNonTokenChar
operator|!=
name|bufferStart
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|termContainsNonTokenChar
operator|||
name|isEdgeAndPreviousCharIsTokenChar
condition|)
block|{
name|consume
argument_list|()
expr_stmt|;
name|gramSize
operator|=
name|minGram
expr_stmt|;
continue|continue;
block|}
specifier|final
name|int
name|length
init|=
name|charUtils
operator|.
name|toChars
argument_list|(
name|buffer
argument_list|,
name|bufferStart
argument_list|,
name|gramSize
argument_list|,
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|posLenAtt
operator|.
name|setPositionLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|offset
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|offset
operator|+
name|length
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|gramSize
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|updateLastNonTokenChar
specifier|private
name|void
name|updateLastNonTokenChar
parameter_list|()
block|{
specifier|final
name|int
name|termEnd
init|=
name|bufferStart
operator|+
name|gramSize
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|termEnd
operator|>
name|lastCheckedChar
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|termEnd
init|;
name|i
operator|>
name|lastCheckedChar
condition|;
operator|--
name|i
control|)
block|{
if|if
condition|(
operator|!
name|isTokenChar
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|lastNonTokenChar
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|lastCheckedChar
operator|=
name|termEnd
expr_stmt|;
block|}
block|}
comment|/** Consume one code point. */
DECL|method|consume
specifier|private
name|void
name|consume
parameter_list|()
block|{
name|offset
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|buffer
index|[
name|bufferStart
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Only collect characters which satisfy this condition. */
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|chr
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
assert|assert
name|bufferStart
operator|<=
name|bufferEnd
assert|;
name|int
name|endOffset
init|=
name|offset
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bufferStart
init|;
name|i
operator|<
name|bufferEnd
condition|;
operator|++
name|i
control|)
block|{
name|endOffset
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|endOffset
operator|=
name|correctOffset
argument_list|(
name|endOffset
argument_list|)
expr_stmt|;
comment|// set final offset
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|endOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
specifier|final
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
name|bufferStart
operator|=
name|bufferEnd
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|lastNonTokenChar
operator|=
name|lastCheckedChar
operator|=
name|bufferStart
operator|-
literal|1
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|gramSize
operator|=
name|minGram
expr_stmt|;
name|exhausted
operator|=
literal|false
expr_stmt|;
name|charBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
