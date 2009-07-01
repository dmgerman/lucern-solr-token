begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|HashSet
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
name|WordSegmenter
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
name|WordTokenizer
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
name|AnalyzerProfile
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
begin_comment
comment|/**  *<p>  * SmartChineseAnalyzer is an analyzer for Chinese or mixed Chinese-English text.  * The analyzer uses probabilistic knowledge to find the optimal word segmentation for Simplified Chinese text.  * The text is first broken into sentences, then each sentence is segmented into words.  *</p>  *<p>  * Segmentation is based upon the<a href="http://en.wikipedia.org/wiki/Hidden_Markov_Model">Hidden Markov Model</a>.   * A large training corpus was used to calculate Chinese word frequency probability.  *</p>  *<p>  * This analyzer requires a dictionary to provide statistical data.   * To specify the location of the dictionary data, refer to {@link AnalyzerProfile}  *</p>  *<p>  * The included dictionary data is from<a href="http://www.ictclas.org">ICTCLAS1.0</a>.  * Thanks to ICTCLAS for their hard work, and for contributing the data under the Apache 2 License!  *</p>  */
end_comment
begin_class
DECL|class|SmartChineseAnalyzer
specifier|public
class|class
name|SmartChineseAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopWords
specifier|private
name|Set
name|stopWords
init|=
literal|null
decl_stmt|;
DECL|field|wordSegment
specifier|private
name|WordSegmenter
name|wordSegment
decl_stmt|;
comment|/**    * Create a new SmartChineseAnalyzer, using the default stopword list.    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Create a new SmartChineseAnalyzer, optionally using the default stopword list.    *</p>    *<p>    * The included default stopword list is simply a list of punctuation.    * If you do not use this list, punctuation will not be removed from the text!    *</p>    *     * @param useDefaultStopWords true to use the default stopword list.    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|boolean
name|useDefaultStopWords
parameter_list|)
block|{
if|if
condition|(
name|useDefaultStopWords
condition|)
block|{
name|stopWords
operator|=
name|loadStopWords
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"stopwords.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|wordSegment
operator|=
operator|new
name|WordSegmenter
argument_list|()
expr_stmt|;
block|}
comment|/**    *<p>    * Create a new SmartChineseAnalyzer, using the provided {@link Set} of stopwords.    *</p>    *<p>    * Note: the set should include punctuation, unless you want to index punctuation!    *</p>    * @param stopWords {@link Set} of stopwords to use.    * @see SmartChineseAnalyzer#loadStopWords(InputStream)    */
DECL|method|SmartChineseAnalyzer
specifier|public
name|SmartChineseAnalyzer
parameter_list|(
name|Set
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
name|wordSegment
operator|=
operator|new
name|WordSegmenter
argument_list|()
expr_stmt|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|SentenceTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|WordTokenizer
argument_list|(
name|result
argument_list|,
name|wordSegment
argument_list|)
expr_stmt|;
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
name|stopWords
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Utility function to return a {@link Set} of stopwords from a UTF-8 encoded {@link InputStream}.    * The comment "//" can be used in the stopword list.    *     * @param input {@link InputStream} of UTF-8 encoded stopwords    * @return {@link Set} of stopwords.    */
DECL|method|loadStopWords
specifier|public
specifier|static
name|Set
name|loadStopWords
parameter_list|(
name|InputStream
name|input
parameter_list|)
block|{
comment|/*      * Note: WordListLoader is not used here because this method allows for inline "//" comments.      * WordListLoader will only filter out these comments if they are on a separate line.      */
name|String
name|line
decl_stmt|;
name|Set
name|stopWords
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
try|try
block|{
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|input
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|"//"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|line
operator|.
name|indexOf
argument_list|(
literal|"//"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
name|stopWords
operator|.
name|add
argument_list|(
name|line
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: cannot open stop words list!"
argument_list|)
expr_stmt|;
block|}
return|return
name|stopWords
return|;
block|}
block|}
end_class
end_unit
