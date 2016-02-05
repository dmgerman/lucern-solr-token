begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.icu
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Replaceable
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
name|Transliterator
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
name|UTF16
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
name|UnicodeSet
import|;
end_import
begin_comment
comment|/**  * A {@link TokenFilter} that transforms text with ICU.  *<p>  * ICU provides text-transformation functionality via its Transliteration API.  * Although script conversion is its most common use, a Transliterator can  * actually perform a more general class of tasks. In fact, Transliterator  * defines a very general API which specifies only that a segment of the input  * text is replaced by new text. The particulars of this conversion are  * determined entirely by subclasses of Transliterator.  *</p>  *<p>  * Some useful transformations for search are built-in:  *<ul>  *<li>Conversion from Traditional to Simplified Chinese characters  *<li>Conversion from Hiragana to Katakana  *<li>Conversion from Fullwidth to Halfwidth forms.  *<li>Script conversions, for example Serbian Cyrillic to Latin  *</ul>  *<p>  * Example usage:<blockquote>stream = new ICUTransformFilter(stream,  * Transliterator.getInstance("Traditional-Simplified"));</blockquote>  *<br>  * For more details, see the<a  * href="http://userguide.icu-project.org/transforms/general">ICU User  * Guide</a>.  */
end_comment
begin_class
DECL|class|ICUTransformFilter
specifier|public
specifier|final
class|class
name|ICUTransformFilter
extends|extends
name|TokenFilter
block|{
comment|// Transliterator to transform the text
DECL|field|transform
specifier|private
specifier|final
name|Transliterator
name|transform
decl_stmt|;
comment|// Reusable position object
DECL|field|position
specifier|private
specifier|final
name|Transliterator
operator|.
name|Position
name|position
init|=
operator|new
name|Transliterator
operator|.
name|Position
argument_list|()
decl_stmt|;
comment|// term attribute, will be updated with transformed text.
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
comment|// Wraps a termAttribute around the replaceable interface.
DECL|field|replaceableAttribute
specifier|private
specifier|final
name|ReplaceableTermAttribute
name|replaceableAttribute
init|=
operator|new
name|ReplaceableTermAttribute
argument_list|()
decl_stmt|;
comment|/**    * Create a new ICUTransformFilter that transforms text on the given stream.    *     * @param input {@link TokenStream} to filter.    * @param transform Transliterator to transform the text.    */
DECL|method|ICUTransformFilter
specifier|public
name|ICUTransformFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Transliterator
name|transform
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|transform
operator|=
name|transform
expr_stmt|;
comment|/*       * This is cheating, but speeds things up a lot.      * If we wanted to use pkg-private APIs we could probably do better.      */
if|if
condition|(
name|transform
operator|.
name|getFilter
argument_list|()
operator|==
literal|null
operator|&&
name|transform
operator|instanceof
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedTransliterator
condition|)
block|{
specifier|final
name|UnicodeSet
name|sourceSet
init|=
name|transform
operator|.
name|getSourceSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|sourceSet
operator|!=
literal|null
operator|&&
operator|!
name|sourceSet
operator|.
name|isEmpty
argument_list|()
condition|)
name|transform
operator|.
name|setFilter
argument_list|(
name|sourceSet
argument_list|)
expr_stmt|;
block|}
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
comment|/*      * Wrap around replaceable. clear the positions, and transliterate.      */
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|replaceableAttribute
operator|.
name|setText
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
name|position
operator|.
name|start
operator|=
literal|0
expr_stmt|;
name|position
operator|.
name|limit
operator|=
name|length
expr_stmt|;
name|position
operator|.
name|contextStart
operator|=
literal|0
expr_stmt|;
name|position
operator|.
name|contextLimit
operator|=
name|length
expr_stmt|;
name|transform
operator|.
name|filteredTransliterate
argument_list|(
name|replaceableAttribute
argument_list|,
name|position
argument_list|,
literal|false
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
comment|/**    * Wrap a {@link CharTermAttribute} with the Replaceable API.    */
DECL|class|ReplaceableTermAttribute
specifier|final
class|class
name|ReplaceableTermAttribute
implements|implements
name|Replaceable
block|{
DECL|field|buffer
specifier|private
name|char
name|buffer
index|[]
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|token
specifier|private
name|CharTermAttribute
name|token
decl_stmt|;
DECL|method|setText
name|void
name|setText
parameter_list|(
specifier|final
name|CharTermAttribute
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|token
operator|.
name|buffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|token
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|char32At
specifier|public
name|int
name|char32At
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|UTF16
operator|.
name|charAt
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|buffer
index|[
name|pos
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|dest
parameter_list|)
block|{
name|char
name|text
index|[]
init|=
operator|new
name|char
index|[
name|limit
operator|-
name|start
index|]
decl_stmt|;
name|getChars
argument_list|(
name|start
argument_list|,
name|limit
argument_list|,
name|text
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|replace
argument_list|(
name|dest
argument_list|,
name|dest
argument_list|,
name|text
argument_list|,
literal|0
argument_list|,
name|limit
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChars
specifier|public
name|void
name|getChars
parameter_list|(
name|int
name|srcStart
parameter_list|,
name|int
name|srcLimit
parameter_list|,
name|char
index|[]
name|dst
parameter_list|,
name|int
name|dstStart
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|srcStart
argument_list|,
name|dst
argument_list|,
name|dstStart
argument_list|,
name|srcLimit
operator|-
name|srcStart
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasMetaData
specifier|public
name|boolean
name|hasMetaData
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|replace
specifier|public
name|void
name|replace
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|String
name|text
parameter_list|)
block|{
specifier|final
name|int
name|charsLen
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|newLength
init|=
name|shiftForReplace
argument_list|(
name|start
argument_list|,
name|limit
argument_list|,
name|charsLen
argument_list|)
decl_stmt|;
comment|// insert the replacement text
name|text
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|charsLen
argument_list|,
name|buffer
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|token
operator|.
name|setLength
argument_list|(
name|length
operator|=
name|newLength
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replace
specifier|public
name|void
name|replace
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|char
index|[]
name|text
parameter_list|,
name|int
name|charsStart
parameter_list|,
name|int
name|charsLen
parameter_list|)
block|{
comment|// shift text if necessary for the replacement
specifier|final
name|int
name|newLength
init|=
name|shiftForReplace
argument_list|(
name|start
argument_list|,
name|limit
argument_list|,
name|charsLen
argument_list|)
decl_stmt|;
comment|// insert the replacement text
name|System
operator|.
name|arraycopy
argument_list|(
name|text
argument_list|,
name|charsStart
argument_list|,
name|buffer
argument_list|,
name|start
argument_list|,
name|charsLen
argument_list|)
expr_stmt|;
name|token
operator|.
name|setLength
argument_list|(
name|length
operator|=
name|newLength
argument_list|)
expr_stmt|;
block|}
comment|/** shift text (if necessary) for a replacement operation */
DECL|method|shiftForReplace
specifier|private
name|int
name|shiftForReplace
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|charsLen
parameter_list|)
block|{
specifier|final
name|int
name|replacementLength
init|=
name|limit
operator|-
name|start
decl_stmt|;
specifier|final
name|int
name|newLength
init|=
name|length
operator|-
name|replacementLength
operator|+
name|charsLen
decl_stmt|;
comment|// resize if necessary
if|if
condition|(
name|newLength
operator|>
name|length
condition|)
name|buffer
operator|=
name|token
operator|.
name|resizeBuffer
argument_list|(
name|newLength
argument_list|)
expr_stmt|;
comment|// if the substring being replaced is longer or shorter than the
comment|// replacement, need to shift things around
if|if
condition|(
name|replacementLength
operator|!=
name|charsLen
operator|&&
name|limit
operator|<
name|length
condition|)
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|limit
argument_list|,
name|buffer
argument_list|,
name|start
operator|+
name|charsLen
argument_list|,
name|length
operator|-
name|limit
argument_list|)
expr_stmt|;
return|return
name|newLength
return|;
block|}
block|}
block|}
end_class
end_unit
