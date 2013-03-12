begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
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
comment|/** A grammar-based tokenizer constructed with JFlex  *  *<p> This should be a good tokenizer for most European-language documents:  *  *<ul>  *<li>Splits words at punctuation characters, removing punctuation. However, a   *     dot that's not followed by whitespace is considered part of a token.  *<li>Splits words at hyphens, unless there's a number in the token, in which case  *     the whole token is interpreted as a product number and is not split.  *<li>Recognizes email addresses and internet hostnames as one token.  *</ul>  *  *<p>Many applications have specific tokenizer needs.  If this tokenizer does  * not suit your application, please consider copying this source code  * directory to your project and maintaining your own grammar-based tokenizer.  *  * ClassicTokenizer was named StandardTokenizer in Lucene versions prior to 3.1.  * As of 3.1, {@link StandardTokenizer} implements Unicode text segmentation,  * as specified by UAX#29.  */
end_comment
begin_class
DECL|class|ClassicTokenizer
specifier|public
specifier|final
class|class
name|ClassicTokenizer
extends|extends
name|Tokenizer
block|{
comment|/** A private instance of the JFlex-constructed scanner */
DECL|field|scanner
specifier|private
name|StandardTokenizerInterface
name|scanner
decl_stmt|;
DECL|field|ALPHANUM
specifier|public
specifier|static
specifier|final
name|int
name|ALPHANUM
init|=
literal|0
decl_stmt|;
DECL|field|APOSTROPHE
specifier|public
specifier|static
specifier|final
name|int
name|APOSTROPHE
init|=
literal|1
decl_stmt|;
DECL|field|ACRONYM
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM
init|=
literal|2
decl_stmt|;
DECL|field|COMPANY
specifier|public
specifier|static
specifier|final
name|int
name|COMPANY
init|=
literal|3
decl_stmt|;
DECL|field|EMAIL
specifier|public
specifier|static
specifier|final
name|int
name|EMAIL
init|=
literal|4
decl_stmt|;
DECL|field|HOST
specifier|public
specifier|static
specifier|final
name|int
name|HOST
init|=
literal|5
decl_stmt|;
DECL|field|NUM
specifier|public
specifier|static
specifier|final
name|int
name|NUM
init|=
literal|6
decl_stmt|;
DECL|field|CJ
specifier|public
specifier|static
specifier|final
name|int
name|CJ
init|=
literal|7
decl_stmt|;
DECL|field|ACRONYM_DEP
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM_DEP
init|=
literal|8
decl_stmt|;
comment|/** String token types that correspond to token type int constants */
DECL|field|TOKEN_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TOKEN_TYPES
init|=
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<APOSTROPHE>"
block|,
literal|"<ACRONYM>"
block|,
literal|"<COMPANY>"
block|,
literal|"<EMAIL>"
block|,
literal|"<HOST>"
block|,
literal|"<NUM>"
block|,
literal|"<CJ>"
block|,
literal|"<ACRONYM_DEP>"
block|}
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/** Set the max allowed token length.  Any token longer    *  than this is skipped. */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** @see #setMaxTokenLength */
DECL|method|getMaxTokenLength
specifier|public
name|int
name|getMaxTokenLength
parameter_list|()
block|{
return|return
name|maxTokenLength
return|;
block|}
comment|/**    * Creates a new instance of the {@link ClassicTokenizer}.  Attaches    * the<code>input</code> to the newly created JFlex scanner.    *    * @param input The input reader    *    * See http://issues.apache.org/jira/browse/LUCENE-1068    */
DECL|method|ClassicTokenizer
specifier|public
name|ClassicTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new ClassicTokenizer with a given {@link org.apache.lucene.util.AttributeSource.AttributeFactory}     */
DECL|method|ClassicTokenizer
specifier|public
name|ClassicTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
operator|.
name|scanner
operator|=
operator|new
name|ClassicTokenizerImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// best effort NPE if you dont call reset
block|}
comment|// this tokenizer generates three attributes:
comment|// term offset, positionIncrement and type
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
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.analysis.TokenStream#next()    */
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
name|int
name|posIncr
init|=
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|tokenType
init|=
name|scanner
operator|.
name|getNextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenType
operator|==
name|StandardTokenizerInterface
operator|.
name|YYEOF
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|scanner
operator|.
name|yylength
argument_list|()
operator|<=
name|maxTokenLength
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|getText
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|start
operator|+
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenType
operator|==
name|ClassicTokenizer
operator|.
name|ACRONYM_DEP
condition|)
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|ClassicTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|ClassicTokenizer
operator|.
name|HOST
index|]
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|termAtt
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove extra '.'
block|}
else|else
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|ClassicTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|tokenType
index|]
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
comment|// When we skip a too-long term, we still increment the
comment|// position increment
name|posIncr
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
name|int
name|finalOffset
init|=
name|correctOffset
argument_list|(
name|scanner
operator|.
name|yychar
argument_list|()
operator|+
name|scanner
operator|.
name|yylength
argument_list|()
argument_list|)
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
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
name|scanner
operator|.
name|yyreset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
