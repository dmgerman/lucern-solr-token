begin_unit
begin_comment
comment|/*  * Licensed under the Apache License,   * Version 2.0 (the "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software distributed under the License   * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.commongrams
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|commongrams
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Version
import|;
end_import
begin_comment
comment|/*  * TODO: Consider implementing https://issues.apache.org/jira/browse/LUCENE-1688 changes to stop list and associated constructors   */
end_comment
begin_comment
comment|/**  * Construct bigrams for frequently occurring terms while indexing. Single terms  * are still indexed too, with bigrams overlaid. This is achieved through the  * use of {@link PositionIncrementAttribute#setPositionIncrement(int)}. Bigrams have a type  * of {@link #GRAM_TYPE} Example:  *<ul>  *<li>input:"the quick brown fox"</li>  *<li>output:|"the","the-quick"|"brown"|"fox"|</li>  *<li>"the-quick" has a position increment of 0 so it is in the same position  * as "the" "the-quick" has a term.type() of "gram"</li>  *   *</ul>  */
end_comment
begin_comment
comment|/*  * Constructors and makeCommonSet based on similar code in StopFilter  */
end_comment
begin_class
DECL|class|CommonGramsFilter
specifier|public
specifier|final
class|class
name|CommonGramsFilter
extends|extends
name|TokenFilter
block|{
DECL|field|GRAM_TYPE
specifier|static
specifier|final
name|String
name|GRAM_TYPE
init|=
literal|"gram"
decl_stmt|;
DECL|field|SEPARATOR
specifier|private
specifier|static
specifier|final
name|char
name|SEPARATOR
init|=
literal|'_'
decl_stmt|;
DECL|field|commonWords
specifier|private
specifier|final
name|CharArraySet
name|commonWords
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttribute
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAttribute
specifier|private
specifier|final
name|TypeAttribute
name|typeAttribute
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAttribute
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
name|lastStartOffset
decl_stmt|;
DECL|field|lastWasCommon
specifier|private
name|boolean
name|lastWasCommon
decl_stmt|;
DECL|field|savedState
specifier|private
name|State
name|savedState
decl_stmt|;
comment|/**    * Construct a token stream filtering the given input using a Set of common    * words to create bigrams. Outputs both unigrams with position increment and    * bigrams with position increment 0 type=gram where one or both of the words    * in a potential bigram are in the set of common words .    *     * @param input TokenStream input in filter chain    * @param commonWords The set of common words.    */
DECL|method|CommonGramsFilter
specifier|public
name|CommonGramsFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|commonWords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|,
name|commonWords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a token stream filtering the given input using a Set of common    * words to create bigrams, case-sensitive if ignoreCase is false (unless Set    * is CharArraySet). If<code>commonWords</code> is an instance of    * {@link CharArraySet} (true if<code>makeCommonSet()</code> was used to    * construct the set) it will be directly used and<code>ignoreCase</code>    * will be ignored since<code>CharArraySet</code> directly controls case    * sensitivity.    *<p/>    * If<code>commonWords</code> is not an instance of {@link CharArraySet}, a    * new CharArraySet will be constructed and<code>ignoreCase</code> will be    * used to specify the case sensitivity of that set.    *     * @param input TokenStream input in filter chain.    * @param commonWords The set of common words.    * @param ignoreCase -Ignore case when constructing bigrams for common words.    */
DECL|method|CommonGramsFilter
specifier|public
name|CommonGramsFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|commonWords
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|commonWords
operator|instanceof
name|CharArraySet
condition|)
block|{
name|this
operator|.
name|commonWords
operator|=
operator|(
name|CharArraySet
operator|)
name|commonWords
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|commonWords
operator|=
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|commonWords
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|this
operator|.
name|commonWords
operator|.
name|addAll
argument_list|(
name|commonWords
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Inserts bigrams for common words into a token stream. For each input token,    * output the token. If the token and/or the following token are in the list    * of common words also output a bigram with position increment 0 and    * type="gram"    *    * TODO:Consider adding an option to not emit unigram stopwords    * as in CDL XTF BigramStopFilter, CommonGramsQueryFilter would need to be    * changed to work with this.    *    * TODO: Consider optimizing for the case of three    * commongrams i.e "man of the year" normally produces 3 bigrams: "man-of",    * "of-the", "the-year" but with proper management of positions we could    * eliminate the middle bigram "of-the"and save a disk seek and a whole set of    * position lookups.    */
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
comment|// get the next piece of input
if|if
condition|(
name|savedState
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|savedState
argument_list|)
expr_stmt|;
name|savedState
operator|=
literal|null
expr_stmt|;
name|saveTermBuffer
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
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
comment|/* We build n-grams before and after stopwords.       * When valid, the buffer always contains at least the separator.      * If its empty, there is nothing before this stopword.      */
if|if
condition|(
name|lastWasCommon
operator|||
operator|(
name|isCommon
argument_list|()
operator|&&
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|savedState
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|gramToken
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|saveTermBuffer
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|lastWasCommon
operator|=
literal|false
expr_stmt|;
name|savedState
operator|=
literal|null
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// ================================================= Helper Methods ================================================
comment|/**    * Determines if the current token is a common term    *    * @return {@code true} if the current token is a common term, {@code false} otherwise    */
DECL|method|isCommon
specifier|private
name|boolean
name|isCommon
parameter_list|()
block|{
return|return
name|commonWords
operator|!=
literal|null
operator|&&
name|commonWords
operator|.
name|contains
argument_list|(
name|termAttribute
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAttribute
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Saves this information to form the left part of a gram    */
DECL|method|saveTermBuffer
specifier|private
name|void
name|saveTermBuffer
parameter_list|()
block|{
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|termAttribute
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAttribute
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|SEPARATOR
argument_list|)
expr_stmt|;
name|lastStartOffset
operator|=
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|lastWasCommon
operator|=
name|isCommon
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a compound token.    */
DECL|method|gramToken
specifier|private
name|void
name|gramToken
parameter_list|()
block|{
name|buffer
operator|.
name|append
argument_list|(
name|termAttribute
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAttribute
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|endOffset
init|=
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|int
name|length
init|=
name|buffer
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|termText
index|[]
init|=
name|termAttribute
operator|.
name|buffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|>
name|termText
operator|.
name|length
condition|)
block|{
name|termText
operator|=
name|termAttribute
operator|.
name|resizeBuffer
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|length
argument_list|,
name|termText
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termAttribute
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|lastStartOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
name|typeAttribute
operator|.
name|setType
argument_list|(
name|GRAM_TYPE
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
