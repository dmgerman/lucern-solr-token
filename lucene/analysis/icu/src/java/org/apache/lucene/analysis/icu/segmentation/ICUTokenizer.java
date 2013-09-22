begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
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
name|icu
operator|.
name|tokenattributes
operator|.
name|ScriptAttribute
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
name|TypeAttribute
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UCharacter
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_comment
comment|/**  * Breaks text into words according to UAX #29: Unicode Text Segmentation  * (http://www.unicode.org/reports/tr29/)  *<p>  * Words are broken across script boundaries, then segmented according to  * the BreakIterator and typing provided by the {@link ICUTokenizerConfig}  *</p>  * @see ICUTokenizerConfig  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ICUTokenizer
specifier|public
specifier|final
class|class
name|ICUTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|IOBUFFER
specifier|private
specifier|static
specifier|final
name|int
name|IOBUFFER
init|=
literal|4096
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|char
name|buffer
index|[]
init|=
operator|new
name|char
index|[
name|IOBUFFER
index|]
decl_stmt|;
comment|/** true length of text in the buffer */
DECL|field|length
specifier|private
name|int
name|length
init|=
literal|0
decl_stmt|;
comment|/** length in buffer that can be evaluated safely, up to a safe end point */
DECL|field|usableLength
specifier|private
name|int
name|usableLength
init|=
literal|0
decl_stmt|;
comment|/** accumulated offset of previous buffers for this reader, for offsetAtt */
DECL|field|offset
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|;
DECL|field|breaker
specifier|private
specifier|final
name|CompositeBreakIterator
name|breaker
decl_stmt|;
comment|/* tokenizes a char[] of text */
DECL|field|config
specifier|private
specifier|final
name|ICUTokenizerConfig
name|config
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
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scriptAtt
specifier|private
specifier|final
name|ScriptAttribute
name|scriptAtt
init|=
name|addAttribute
argument_list|(
name|ScriptAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Construct a new ICUTokenizer that breaks text into words from the given    * Reader.    *<p>    * The default script-specific handling is used.    *<p>    * The default attribute factory is used.    *     * @param input Reader containing text to tokenize.    * @see DefaultICUTokenizerConfig    */
DECL|method|ICUTokenizer
specifier|public
name|ICUTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
operator|new
name|DefaultICUTokenizerConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a new ICUTokenizer that breaks text into words from the given    * Reader, using a tailored BreakIterator configuration.    *<p>    * The default attribute factory is used.    *    * @param input Reader containing text to tokenize.    * @param config Tailored BreakIterator configuration     */
DECL|method|ICUTokenizer
specifier|public
name|ICUTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|ICUTokenizerConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|input
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a new ICUTokenizer that breaks text into words from the given    * Reader, using a tailored BreakIterator configuration.    *    * @param factory AttributeFactory to use    * @param input Reader containing text to tokenize.    * @param config Tailored BreakIterator configuration     */
DECL|method|ICUTokenizer
specifier|public
name|ICUTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|ICUTokenizerConfig
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|breaker
operator|=
operator|new
name|CompositeBreakIterator
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
name|refill
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|incrementTokenBuffer
argument_list|()
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|length
operator|<=
literal|0
condition|)
comment|// no more bytes to read;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
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
name|breaker
operator|.
name|setText
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|length
operator|=
name|usableLength
operator|=
name|offset
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
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
specifier|final
name|int
name|finalOffset
init|=
operator|(
name|length
operator|<
literal|0
operator|)
condition|?
name|offset
else|:
name|offset
operator|+
name|length
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|finalOffset
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|finalOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * This tokenizes text based upon the longest matching rule, and because of     * this, isn't friendly to a Reader.    *     * Text is read from the input stream in 4kB chunks. Within a 4kB chunk of    * text, the last unambiguous break point is found (in this implementation:    * white space character) Any remaining characters represent possible partial    * words, so are appended to the front of the next chunk.    *     * There is the possibility that there are no unambiguous break points within    * an entire 4kB chunk of text (binary data). So there is a maximum word limit    * of 4kB since it will not try to grow the buffer in this case.    */
comment|/**    * Returns the last unambiguous break position in the text.    *     * @return position of character, or -1 if one does not exist    */
DECL|method|findSafeEnd
specifier|private
name|int
name|findSafeEnd
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
if|if
condition|(
name|UCharacter
operator|.
name|isWhitespace
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
condition|)
return|return
name|i
operator|+
literal|1
return|;
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Refill the buffer, accumulating the offset and setting usableLength to the    * last unambiguous break position    *     * @throws IOException If there is a low-level I/O error.    */
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
name|offset
operator|+=
name|usableLength
expr_stmt|;
name|int
name|leftover
init|=
name|length
operator|-
name|usableLength
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|usableLength
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|leftover
argument_list|)
expr_stmt|;
name|int
name|requested
init|=
name|buffer
operator|.
name|length
operator|-
name|leftover
decl_stmt|;
name|int
name|returned
init|=
name|read
argument_list|(
name|input
argument_list|,
name|buffer
argument_list|,
name|leftover
argument_list|,
name|requested
argument_list|)
decl_stmt|;
name|length
operator|=
name|returned
operator|+
name|leftover
expr_stmt|;
if|if
condition|(
name|returned
operator|<
name|requested
condition|)
comment|/* reader has been emptied, process the rest */
name|usableLength
operator|=
name|length
expr_stmt|;
else|else
block|{
comment|/* still more data to be read, find a safe-stopping place */
name|usableLength
operator|=
name|findSafeEnd
argument_list|()
expr_stmt|;
if|if
condition|(
name|usableLength
operator|<
literal|0
condition|)
name|usableLength
operator|=
name|length
expr_stmt|;
comment|/*                                 * more than IOBUFFER of text without space,                                 * gonna possibly truncate tokens                                 */
block|}
name|breaker
operator|.
name|setText
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|usableLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// TODO: refactor to a shared readFully somewhere
comment|// (NGramTokenizer does this too):
comment|/** commons-io's readFully, but without bugs if offset != 0 */
DECL|method|read
specifier|private
specifier|static
name|int
name|read
parameter_list|(
name|Reader
name|input
parameter_list|,
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|length
operator|>=
literal|0
operator|:
literal|"length must not be negative: "
operator|+
name|length
assert|;
name|int
name|remaining
init|=
name|length
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|int
name|location
init|=
name|length
operator|-
name|remaining
decl_stmt|;
name|int
name|count
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
operator|+
name|location
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|count
condition|)
block|{
comment|// EOF
break|break;
block|}
name|remaining
operator|-=
name|count
expr_stmt|;
block|}
return|return
name|length
operator|-
name|remaining
return|;
block|}
comment|/*    * return true if there is a token from the buffer, or null if it is    * exhausted.    */
DECL|method|incrementTokenBuffer
specifier|private
name|boolean
name|incrementTokenBuffer
parameter_list|()
block|{
name|int
name|start
init|=
name|breaker
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|==
name|BreakIterator
operator|.
name|DONE
condition|)
return|return
literal|false
return|;
comment|// BreakIterator exhausted
comment|// find the next set of boundaries, skipping over non-tokens (rule status 0)
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|start
operator|!=
name|BreakIterator
operator|.
name|DONE
operator|&&
name|breaker
operator|.
name|getRuleStatus
argument_list|()
operator|==
literal|0
condition|)
block|{
name|start
operator|=
name|end
expr_stmt|;
name|end
operator|=
name|breaker
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|start
operator|==
name|BreakIterator
operator|.
name|DONE
condition|)
return|return
literal|false
return|;
comment|// BreakIterator exhausted
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|buffer
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|offset
operator|+
name|start
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|offset
operator|+
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|config
operator|.
name|getType
argument_list|(
name|breaker
operator|.
name|getScriptCode
argument_list|()
argument_list|,
name|breaker
operator|.
name|getRuleStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|scriptAtt
operator|.
name|setCode
argument_list|(
name|breaker
operator|.
name|getScriptCode
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
