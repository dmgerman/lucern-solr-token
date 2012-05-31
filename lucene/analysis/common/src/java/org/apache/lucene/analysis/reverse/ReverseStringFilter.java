begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.reverse
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|reverse
package|;
end_package
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
name|util
operator|.
name|Version
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
begin_comment
comment|/**  * Reverse token string, for example "country" => "yrtnuoc".  *<p>  * If<code>marker</code> is supplied, then tokens will be also prepended by  * that character. For example, with a marker of&#x5C;u0001, "country" =>  * "&#x5C;u0001yrtnuoc". This is useful when implementing efficient leading  * wildcards search.  */
end_comment
begin_class
DECL|class|ReverseStringFilter
specifier|public
specifier|final
class|class
name|ReverseStringFilter
extends|extends
name|TokenFilter
block|{
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
DECL|field|marker
specifier|private
specifier|final
name|char
name|marker
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
DECL|field|NOMARKER
specifier|private
specifier|static
specifier|final
name|char
name|NOMARKER
init|=
literal|'\uFFFF'
decl_stmt|;
comment|/**    * Example marker character: U+0001 (START OF HEADING)     */
DECL|field|START_OF_HEADING_MARKER
specifier|public
specifier|static
specifier|final
name|char
name|START_OF_HEADING_MARKER
init|=
literal|'\u0001'
decl_stmt|;
comment|/**    * Example marker character: U+001F (INFORMATION SEPARATOR ONE)    */
DECL|field|INFORMATION_SEPARATOR_MARKER
specifier|public
specifier|static
specifier|final
name|char
name|INFORMATION_SEPARATOR_MARKER
init|=
literal|'\u001F'
decl_stmt|;
comment|/**    * Example marker character: U+EC00 (PRIVATE USE AREA: EC00)     */
DECL|field|PUA_EC00_MARKER
specifier|public
specifier|static
specifier|final
name|char
name|PUA_EC00_MARKER
init|=
literal|'\uEC00'
decl_stmt|;
comment|/**    * Example marker character: U+200F (RIGHT-TO-LEFT MARK)    */
DECL|field|RTL_DIRECTION_MARKER
specifier|public
specifier|static
specifier|final
name|char
name|RTL_DIRECTION_MARKER
init|=
literal|'\u200F'
decl_stmt|;
comment|/**    * Create a new ReverseStringFilter that reverses all tokens in the     * supplied {@link TokenStream}.    *<p>    * The reversed tokens will not be marked.     *</p>    *     * @param matchVersion Lucene compatibility version    * @param in {@link TokenStream} to filter    */
DECL|method|ReverseStringFilter
specifier|public
name|ReverseStringFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|in
argument_list|,
name|NOMARKER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new ReverseStringFilter that reverses and marks all tokens in the    * supplied {@link TokenStream}.    *<p>    * The reversed tokens will be prepended (marked) by the<code>marker</code>    * character.    *</p>    *     * @param matchVersion compatibility version    * @param in {@link TokenStream} to filter    * @param marker A character used to mark reversed tokens    */
DECL|method|ReverseStringFilter
specifier|public
name|ReverseStringFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|in
parameter_list|,
name|char
name|marker
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
name|this
operator|.
name|marker
operator|=
name|marker
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
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|len
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|marker
operator|!=
name|NOMARKER
condition|)
block|{
name|len
operator|++
expr_stmt|;
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|buffer
argument_list|()
index|[
name|len
operator|-
literal|1
index|]
operator|=
name|marker
expr_stmt|;
block|}
name|reverse
argument_list|(
name|matchVersion
argument_list|,
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|len
argument_list|)
expr_stmt|;
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
comment|/**    * Reverses the given input string    *     * @param matchVersion compatibility version    * @param input the string to reverse    * @return the given input string in reversed order    */
DECL|method|reverse
specifier|public
specifier|static
name|String
name|reverse
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|String
name|input
parameter_list|)
block|{
specifier|final
name|char
index|[]
name|charInput
init|=
name|input
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|reverse
argument_list|(
name|matchVersion
argument_list|,
name|charInput
argument_list|,
literal|0
argument_list|,
name|charInput
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|charInput
argument_list|)
return|;
block|}
comment|/**    * Reverses the given input buffer in-place    * @param matchVersion compatibility version    * @param buffer the input char array to reverse    */
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|char
index|[]
name|buffer
parameter_list|)
block|{
name|reverse
argument_list|(
name|matchVersion
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Partially reverses the given input buffer in-place from offset 0    * up to the given length.    * @param matchVersion compatibility version    * @param buffer the input char array to reverse    * @param len the length in the buffer up to where the    *        buffer should be reversed    */
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|char
index|[]
name|buffer
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
name|reverse
argument_list|(
name|matchVersion
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**    * Partially reverses the given input buffer in-place from the given offset    * up to the given length.    * @param matchVersion compatibility version    * @param buffer the input char array to reverse    * @param start the offset from where to reverse the buffer    * @param len the length in the buffer up to where the    *        buffer should be reversed    */
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|char
index|[]
name|buffer
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
comment|/* modified version of Apache Harmony AbstractStringBuilder reverse0() */
if|if
condition|(
name|len
operator|<
literal|2
condition|)
return|return;
name|int
name|end
init|=
operator|(
name|start
operator|+
name|len
operator|)
operator|-
literal|1
decl_stmt|;
name|char
name|frontHigh
init|=
name|buffer
index|[
name|start
index|]
decl_stmt|;
name|char
name|endLow
init|=
name|buffer
index|[
name|end
index|]
decl_stmt|;
name|boolean
name|allowFrontSur
init|=
literal|true
decl_stmt|,
name|allowEndSur
init|=
literal|true
decl_stmt|;
specifier|final
name|int
name|mid
init|=
name|start
operator|+
operator|(
name|len
operator|>>
literal|1
operator|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|mid
condition|;
operator|++
name|i
operator|,
operator|--
name|end
control|)
block|{
specifier|final
name|char
name|frontLow
init|=
name|buffer
index|[
name|i
operator|+
literal|1
index|]
decl_stmt|;
specifier|final
name|char
name|endHigh
init|=
name|buffer
index|[
name|end
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|boolean
name|surAtFront
init|=
name|allowFrontSur
operator|&&
name|Character
operator|.
name|isSurrogatePair
argument_list|(
name|frontHigh
argument_list|,
name|frontLow
argument_list|)
decl_stmt|;
if|if
condition|(
name|surAtFront
operator|&&
operator|(
name|len
operator|<
literal|3
operator|)
condition|)
block|{
comment|// nothing to do since surAtFront is allowed and 1 char left
return|return;
block|}
specifier|final
name|boolean
name|surAtEnd
init|=
name|allowEndSur
operator|&&
name|Character
operator|.
name|isSurrogatePair
argument_list|(
name|endHigh
argument_list|,
name|endLow
argument_list|)
decl_stmt|;
name|allowFrontSur
operator|=
name|allowEndSur
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|surAtFront
operator|==
name|surAtEnd
condition|)
block|{
if|if
condition|(
name|surAtFront
condition|)
block|{
comment|// both surrogates
name|buffer
index|[
name|end
index|]
operator|=
name|frontLow
expr_stmt|;
name|buffer
index|[
operator|--
name|end
index|]
operator|=
name|frontHigh
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endHigh
expr_stmt|;
name|buffer
index|[
operator|++
name|i
index|]
operator|=
name|endLow
expr_stmt|;
name|frontHigh
operator|=
name|buffer
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|endLow
operator|=
name|buffer
index|[
name|end
operator|-
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// neither surrogates
name|buffer
index|[
name|end
index|]
operator|=
name|frontHigh
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endLow
expr_stmt|;
name|frontHigh
operator|=
name|frontLow
expr_stmt|;
name|endLow
operator|=
name|endHigh
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|surAtFront
condition|)
block|{
comment|// surrogate only at the front
name|buffer
index|[
name|end
index|]
operator|=
name|frontLow
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endLow
expr_stmt|;
name|endLow
operator|=
name|endHigh
expr_stmt|;
name|allowFrontSur
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// surrogate only at the end
name|buffer
index|[
name|end
index|]
operator|=
name|frontHigh
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endHigh
expr_stmt|;
name|frontHigh
operator|=
name|frontLow
expr_stmt|;
name|allowEndSur
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|(
name|len
operator|&
literal|0x01
operator|)
operator|==
literal|1
operator|&&
operator|!
operator|(
name|allowFrontSur
operator|&&
name|allowEndSur
operator|)
condition|)
block|{
comment|// only if odd length
name|buffer
index|[
name|end
index|]
operator|=
name|allowFrontSur
condition|?
name|endLow
else|:
name|frontHigh
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
