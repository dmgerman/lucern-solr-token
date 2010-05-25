begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package
begin_comment
comment|// This file is encoded in UTF-8
end_comment
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Reader
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|core
operator|.
name|LowerCaseFilter
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
name|analysis
operator|.
name|miscellaneous
operator|.
name|KeywordMarkerFilter
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
name|snowball
operator|.
name|SnowballFilter
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
name|standard
operator|.
name|StandardAnalyzer
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
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
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
name|StopwordAnalyzerBase
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
name|util
operator|.
name|Version
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|German2Stemmer
import|;
end_import
begin_comment
comment|/**  * {@link Analyzer} for German language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  * A default set of stopwords is used unless an alternative list is specified, but the  * exclusion list is empty by default.  *</p>  *   *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating GermanAnalyzer:  *<ul>  *<li> As of 3.1, Snowball stemming is done with SnowballFilter, and   *        Snowball stopwords are used by default.  *<li> As of 2.9, StopFilter preserves position  *        increments  *</ul>  *   *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment
begin_class
DECL|class|GermanAnalyzer
specifier|public
specifier|final
class|class
name|GermanAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/**    * List of typical german stopwords.    * @deprecated use {@link #getDefaultStopSet()} instead    */
comment|//TODO make this private in 3.1, remove in 4.0
annotation|@
name|Deprecated
DECL|field|GERMAN_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|GERMAN_STOP_WORDS
init|=
block|{
literal|"einer"
block|,
literal|"eine"
block|,
literal|"eines"
block|,
literal|"einem"
block|,
literal|"einen"
block|,
literal|"der"
block|,
literal|"die"
block|,
literal|"das"
block|,
literal|"dass"
block|,
literal|"daÃ"
block|,
literal|"du"
block|,
literal|"er"
block|,
literal|"sie"
block|,
literal|"es"
block|,
literal|"was"
block|,
literal|"wer"
block|,
literal|"wie"
block|,
literal|"wir"
block|,
literal|"und"
block|,
literal|"oder"
block|,
literal|"ohne"
block|,
literal|"mit"
block|,
literal|"am"
block|,
literal|"im"
block|,
literal|"in"
block|,
literal|"aus"
block|,
literal|"auf"
block|,
literal|"ist"
block|,
literal|"sein"
block|,
literal|"war"
block|,
literal|"wird"
block|,
literal|"ihr"
block|,
literal|"ihre"
block|,
literal|"ihres"
block|,
literal|"als"
block|,
literal|"fÃ¼r"
block|,
literal|"von"
block|,
literal|"mit"
block|,
literal|"dich"
block|,
literal|"dir"
block|,
literal|"mich"
block|,
literal|"mir"
block|,
literal|"mein"
block|,
literal|"sein"
block|,
literal|"kein"
block|,
literal|"durch"
block|,
literal|"wegen"
block|,
literal|"wird"
block|}
decl_stmt|;
comment|/** File containing default German stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"german_stop.txt"
decl_stmt|;
comment|/**    * Returns a set of default German-stopwords     * @return a set of default German-stopwords     */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_SET
return|;
block|}
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
comment|/** @deprecated remove in Lucene 4.0 */
annotation|@
name|Deprecated
DECL|field|DEFAULT_SET_30
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_SET_30
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|GERMAN_STOP_WORDS
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_SET
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_SET
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_SET
operator|=
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|SnowballFilter
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|)
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
block|}
comment|/**    * Contains the stopwords used with the {@link StopFilter}.    */
comment|/**    * Contains words that should be indexed but not stemmed.    */
comment|// TODO make this final in 3.1
DECL|field|exclusionSet
specifier|private
name|Set
argument_list|<
name|?
argument_list|>
name|exclusionSet
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words:    * {@link #getDefaultStopSet()}.    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|?
name|DefaultSetHolder
operator|.
name|DEFAULT_SET
else|:
name|DefaultSetHolder
operator|.
name|DEFAULT_SET_30
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words     *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    * @param stemExclusionSet    *          a stemming exclusion set    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionSet
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|exclusionSet
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|stemExclusionSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #GermanAnalyzer(Version, Set)}    */
annotation|@
name|Deprecated
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #GermanAnalyzer(Version, Set)}    */
annotation|@
name|Deprecated
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #GermanAnalyzer(Version, Set)}    */
annotation|@
name|Deprecated
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an exclusionlist from an array of Strings.    * @deprecated use {@link #GermanAnalyzer(Version, Set, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
index|[]
name|exclusionlist
parameter_list|)
block|{
name|exclusionSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|exclusionlist
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from a {@link Map}    * @deprecated use {@link #GermanAnalyzer(Version, Set, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|exclusionlist
parameter_list|)
block|{
name|exclusionSet
operator|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|(
name|exclusionlist
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from the words contained in the given file.    * @deprecated use {@link #GermanAnalyzer(Version, Set, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
throws|throws
name|IOException
block|{
name|exclusionSet
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Creates    * {@link org.apache.lucene.analysis.util.ReusableAnalyzerBase.TokenStreamComponents}    * used to tokenize all the text in the provided {@link Reader}.    *     * @return {@link org.apache.lucene.analysis.util.ReusableAnalyzerBase.TokenStreamComponents}    *         built from a {@link StandardTokenizer} filtered with    *         {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}    *         , {@link KeywordMarkerFilter} if a stem exclusion set is    *         provided, and {@link SnowballFilter}    */
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|exclusionSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|)
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
operator|new
name|German2Stemmer
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|result
operator|=
operator|new
name|GermanStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
return|;
block|}
block|}
end_class
end_unit
