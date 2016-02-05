begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|carrot2
operator|.
name|core
operator|.
name|LanguageCode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|analysis
operator|.
name|ExtendedWhitespaceTokenizer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|analysis
operator|.
name|ITokenizer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|ITokenizerFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|util
operator|.
name|MutableCharArray
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|ExceptionUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|ReflectionUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * An implementation of Carrot2's {@link ITokenizerFactory} based on Lucene's  * Smart Chinese tokenizer. If Smart Chinese tokenizer is not available in  * classpath at runtime, the default Carrot2's tokenizer is used. Should the  * Lucene APIs need to change, the changes can be made in this class.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|LuceneCarrot2TokenizerFactory
specifier|public
class|class
name|LuceneCarrot2TokenizerFactory
implements|implements
name|ITokenizerFactory
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getTokenizer
specifier|public
name|ITokenizer
name|getTokenizer
parameter_list|(
name|LanguageCode
name|language
parameter_list|)
block|{
switch|switch
condition|(
name|language
condition|)
block|{
case|case
name|CHINESE_SIMPLIFIED
case|:
return|return
name|ChineseTokenizerFactory
operator|.
name|createTokenizer
argument_list|()
return|;
comment|/*        * We use our own analyzer for Arabic. Lucene's version has special        * support for Nonspacing-Mark characters (see        * http://www.fileformat.info/info/unicode/category/Mn/index.htm), but we        * have them included as letters in the parser.        */
case|case
name|ARABIC
case|:
comment|// Intentional fall-through.
default|default:
return|return
operator|new
name|ExtendedWhitespaceTokenizer
argument_list|()
return|;
block|}
block|}
comment|/**    * Creates tokenizers that adapt Lucene's Smart Chinese Tokenizer to Carrot2's    * {@link ITokenizer}. If Smart Chinese is not available in the classpath, the    * factory will fall back to the default white space tokenizer.    */
DECL|class|ChineseTokenizerFactory
specifier|private
specifier|static
specifier|final
class|class
name|ChineseTokenizerFactory
block|{
static|static
block|{
try|try
block|{
name|ReflectionUtils
operator|.
name|classForName
argument_list|(
literal|"org.apache.lucene.analysis.cn.smart.WordTokenFilter"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ReflectionUtils
operator|.
name|classForName
argument_list|(
literal|"org.apache.lucene.analysis.cn.smart.SentenceTokenizer"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not instantiate Smart Chinese Analyzer, clustering quality "
operator|+
literal|"of Chinese content may be degraded. For best quality clusters, "
operator|+
literal|"make sure Lucene's Smart Chinese Analyzer JAR is in the classpath"
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|e
throw|;
block|}
block|}
block|}
DECL|method|createTokenizer
specifier|static
name|ITokenizer
name|createTokenizer
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|ChineseTokenizer
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
throw|throw
operator|(
name|OutOfMemoryError
operator|)
name|e
throw|;
block|}
return|return
operator|new
name|ExtendedWhitespaceTokenizer
argument_list|()
return|;
block|}
block|}
DECL|class|ChineseTokenizer
specifier|private
specifier|final
specifier|static
class|class
name|ChineseTokenizer
implements|implements
name|ITokenizer
block|{
DECL|field|numeric
specifier|private
specifier|final
specifier|static
name|Pattern
name|numeric
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?"
argument_list|)
decl_stmt|;
DECL|field|sentenceTokenizer
specifier|private
name|Tokenizer
name|sentenceTokenizer
decl_stmt|;
DECL|field|wordTokenFilter
specifier|private
name|TokenStream
name|wordTokenFilter
decl_stmt|;
DECL|field|term
specifier|private
name|CharTermAttribute
name|term
init|=
literal|null
decl_stmt|;
DECL|field|tempCharSequence
specifier|private
specifier|final
name|MutableCharArray
name|tempCharSequence
decl_stmt|;
DECL|field|tokenFilterClass
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|tokenFilterClass
decl_stmt|;
DECL|method|ChineseTokenizer
specifier|private
name|ChineseTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|tempCharSequence
operator|=
operator|new
name|MutableCharArray
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// As Smart Chinese is not available during compile time,
comment|// we need to resort to reflection.
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|tokenizerClass
init|=
name|ReflectionUtils
operator|.
name|classForName
argument_list|(
literal|"org.apache.lucene.analysis.cn.smart.SentenceTokenizer"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|this
operator|.
name|sentenceTokenizer
operator|=
operator|(
name|Tokenizer
operator|)
name|tokenizerClass
operator|.
name|getConstructor
argument_list|(
name|Reader
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
operator|(
name|Reader
operator|)
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|tokenFilterClass
operator|=
name|ReflectionUtils
operator|.
name|classForName
argument_list|(
literal|"org.apache.lucene.analysis.cn.smart.WordTokenFilter"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextToken
specifier|public
name|short
name|nextToken
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|hasNextToken
init|=
name|wordTokenFilter
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasNextToken
condition|)
block|{
name|short
name|flags
init|=
literal|0
decl_stmt|;
specifier|final
name|char
index|[]
name|image
init|=
name|term
operator|.
name|buffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|term
operator|.
name|length
argument_list|()
decl_stmt|;
name|tempCharSequence
operator|.
name|reset
argument_list|(
name|image
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|==
literal|1
operator|&&
name|image
index|[
literal|0
index|]
operator|==
literal|','
condition|)
block|{
comment|// ChineseTokenizer seems to convert all punctuation to ','
comment|// characters
name|flags
operator|=
name|ITokenizer
operator|.
name|TT_PUNCTUATION
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numeric
operator|.
name|matcher
argument_list|(
name|tempCharSequence
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|flags
operator|=
name|ITokenizer
operator|.
name|TT_NUMERIC
expr_stmt|;
block|}
else|else
block|{
name|flags
operator|=
name|ITokenizer
operator|.
name|TT_TERM
expr_stmt|;
block|}
return|return
name|flags
return|;
block|}
return|return
name|ITokenizer
operator|.
name|TT_EOF
return|;
block|}
annotation|@
name|Override
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|MutableCharArray
name|array
parameter_list|)
block|{
name|array
operator|.
name|reset
argument_list|(
name|term
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|term
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
try|try
block|{
name|sentenceTokenizer
operator|.
name|setReader
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|wordTokenFilter
operator|=
operator|(
name|TokenStream
operator|)
name|tokenFilterClass
operator|.
name|getConstructor
argument_list|(
name|TokenStream
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|sentenceTokenizer
argument_list|)
expr_stmt|;
name|term
operator|=
name|wordTokenFilter
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionUtils
operator|.
name|wrapAsRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
