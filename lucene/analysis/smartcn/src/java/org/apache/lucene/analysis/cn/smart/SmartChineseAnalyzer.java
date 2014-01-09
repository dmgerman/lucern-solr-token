begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
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
name|Analyzer
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
name|en
operator|.
name|PorterStemFilter
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
name|analysis
operator|.
name|util
operator|.
name|WordlistLoader
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
name|cn
operator|.
name|smart
operator|.
name|SentenceTokenizer
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
name|cn
operator|.
name|smart
operator|.
name|WordTokenFilter
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
name|core
operator|.
name|StopFilter
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
name|IOUtils
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
comment|/**  *<p>  * SmartChineseAnalyzer is an analyzer for Chinese or mixed Chinese-English text.  * The analyzer uses probabilistic knowledge to find the optimal word segmentation for Simplified Chinese text.  * The text is first broken into sentences, then each sentence is segmented into words.  *</p>  *<p>  * Segmentation is based upon the<a href="http://en.wikipedia.org/wiki/Hidden_Markov_Model">Hidden Markov Model</a>.   * A large training corpus was used to calculate Chinese word frequency probability.  *</p>  *<p>  * This analyzer requires a dictionary to provide statistical data.   * SmartChineseAnalyzer has an included dictionary out-of-box.  *</p>  *<p>  * The included dictionary data is from<a href="http://www.ictclas.org">ICTCLAS1.0</a>.  * Thanks to ICTCLAS for their hard work, and for contributing the data under the Apache 2 License!  *</p>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SmartChineseAnalyzer
specifier|public
specifier|final
class|class
name|SmartChineseAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopWords
specifier|private
specifier|final
name|CharArraySet
name|stopWords
decl_stmt|;
DECL|field|DEFAULT_STOPWORD_FILE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"stopwords.txt"
decl_stmt|;
DECL|field|STOPWORD_FILE_COMMENT
specifier|private
specifier|static
specifier|final
name|String
name|STOPWORD_FILE_COMMENT
init|=
literal|"//"
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop-words set.    * @return an unmodifiable instance of the default stop-words set.    */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
name|CharArraySet
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
return|;
block|}
comment|/**    * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer class     * accesses the static final set the first time.;    */
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
DECL|field|DEFAULT_STOP_SET
specifier|static
specifier|final
name|CharArraySet
name|DEFAULT_STOP_SET
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_STOP_SET
operator|=
name|loadDefaultStopWordSet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// default set should always be present as it is part of the
comment|// distribution (JAR)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to load default stopword set"
argument_list|)
throw|;
block|}
block|}
DECL|method|loadDefaultStopWordSet
specifier|static
name|CharArraySet
name|loadDefaultStopWordSet
parameter_list|()
throws|throws
name|IOException
block|{
comment|// make sure it is unmodifiable as we expose it in the outer class
return|return
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|SmartChineseAnalyzer
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|,
name|STOPWORD_FILE_COMMENT
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Create a new SmartChineseAnalyzer, using the default stopword list.    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Create a new SmartChineseAnalyzer, optionally using the default stopword list.    *</p>    *<p>    * The included default stopword list is simply a list of punctuation.    * If you do not use this list, punctuation will not be removed from the text!    *</p>    *     * @param useDefaultStopWords true to use the default stopword list.    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|boolean
name|useDefaultStopWords
parameter_list|)
block|{
name|stopWords
operator|=
name|useDefaultStopWords
condition|?
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
else|:
name|CharArraySet
operator|.
name|EMPTY_SET
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    *<p>    * Create a new SmartChineseAnalyzer, using the provided {@link Set} of stopwords.    *</p>    *<p>    * Note: the set should include punctuation, unless you want to index punctuation!    *</p>    * @param stopWords {@link Set} of stopwords to use.    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|stopWords
operator|=
name|stopWords
operator|==
literal|null
condition|?
name|CharArraySet
operator|.
name|EMPTY_SET
else|:
name|stopWords
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|SentenceTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|WordTokenFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
comment|// result = new LowerCaseFilter(result);
comment|// LowerCaseFilter is not needed, as SegTokenFilter lowercases Basic Latin text.
comment|// The porter stemming is too strict, this is not a bug, this is a feature:)
name|result
operator|=
operator|new
name|PorterStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stopWords
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|result
argument_list|)
return|;
block|}
block|}
end_class
end_unit
