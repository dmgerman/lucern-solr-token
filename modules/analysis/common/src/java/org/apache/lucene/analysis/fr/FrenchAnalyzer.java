begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
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
begin_comment
comment|/**  * {@link Analyzer} for French language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  * A default set of stopwords is used unless an alternative list is specified, but the  * exclusion list is empty by default.  *</p>  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating FrenchAnalyzer:  *<ul>  *<li> As of 3.6, FrenchLightStemFilter is used for less aggressive stemming.  *<li> As of 3.1, Snowball stemming is done with SnowballFilter,   *        LowerCaseFilter is used prior to StopFilter, and ElisionFilter and   *        Snowball stopwords are used by default.  *<li> As of 2.9, StopFilter preserves position  *        increments  *</ul>  *  *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment
begin_class
DECL|class|FrenchAnalyzer
specifier|public
specifier|final
class|class
name|FrenchAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/**    * Extended list of typical French stopwords.    * @deprecated (3.1) remove in Lucene 5.0 (index bw compat)    */
annotation|@
name|Deprecated
DECL|field|FRENCH_STOP_WORDS
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|FRENCH_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"afin"
block|,
literal|"ai"
block|,
literal|"ainsi"
block|,
literal|"aprÃ¨s"
block|,
literal|"attendu"
block|,
literal|"au"
block|,
literal|"aujourd"
block|,
literal|"auquel"
block|,
literal|"aussi"
block|,
literal|"autre"
block|,
literal|"autres"
block|,
literal|"aux"
block|,
literal|"auxquelles"
block|,
literal|"auxquels"
block|,
literal|"avait"
block|,
literal|"avant"
block|,
literal|"avec"
block|,
literal|"avoir"
block|,
literal|"c"
block|,
literal|"car"
block|,
literal|"ce"
block|,
literal|"ceci"
block|,
literal|"cela"
block|,
literal|"celle"
block|,
literal|"celles"
block|,
literal|"celui"
block|,
literal|"cependant"
block|,
literal|"certain"
block|,
literal|"certaine"
block|,
literal|"certaines"
block|,
literal|"certains"
block|,
literal|"ces"
block|,
literal|"cet"
block|,
literal|"cette"
block|,
literal|"ceux"
block|,
literal|"chez"
block|,
literal|"ci"
block|,
literal|"combien"
block|,
literal|"comme"
block|,
literal|"comment"
block|,
literal|"concernant"
block|,
literal|"contre"
block|,
literal|"d"
block|,
literal|"dans"
block|,
literal|"de"
block|,
literal|"debout"
block|,
literal|"dedans"
block|,
literal|"dehors"
block|,
literal|"delÃ "
block|,
literal|"depuis"
block|,
literal|"derriÃ¨re"
block|,
literal|"des"
block|,
literal|"dÃ©sormais"
block|,
literal|"desquelles"
block|,
literal|"desquels"
block|,
literal|"dessous"
block|,
literal|"dessus"
block|,
literal|"devant"
block|,
literal|"devers"
block|,
literal|"devra"
block|,
literal|"divers"
block|,
literal|"diverse"
block|,
literal|"diverses"
block|,
literal|"doit"
block|,
literal|"donc"
block|,
literal|"dont"
block|,
literal|"du"
block|,
literal|"duquel"
block|,
literal|"durant"
block|,
literal|"dÃ¨s"
block|,
literal|"elle"
block|,
literal|"elles"
block|,
literal|"en"
block|,
literal|"entre"
block|,
literal|"environ"
block|,
literal|"est"
block|,
literal|"et"
block|,
literal|"etc"
block|,
literal|"etre"
block|,
literal|"eu"
block|,
literal|"eux"
block|,
literal|"exceptÃ©"
block|,
literal|"hormis"
block|,
literal|"hors"
block|,
literal|"hÃ©las"
block|,
literal|"hui"
block|,
literal|"il"
block|,
literal|"ils"
block|,
literal|"j"
block|,
literal|"je"
block|,
literal|"jusqu"
block|,
literal|"jusque"
block|,
literal|"l"
block|,
literal|"la"
block|,
literal|"laquelle"
block|,
literal|"le"
block|,
literal|"lequel"
block|,
literal|"les"
block|,
literal|"lesquelles"
block|,
literal|"lesquels"
block|,
literal|"leur"
block|,
literal|"leurs"
block|,
literal|"lorsque"
block|,
literal|"lui"
block|,
literal|"lÃ "
block|,
literal|"ma"
block|,
literal|"mais"
block|,
literal|"malgrÃ©"
block|,
literal|"me"
block|,
literal|"merci"
block|,
literal|"mes"
block|,
literal|"mien"
block|,
literal|"mienne"
block|,
literal|"miennes"
block|,
literal|"miens"
block|,
literal|"moi"
block|,
literal|"moins"
block|,
literal|"mon"
block|,
literal|"moyennant"
block|,
literal|"mÃªme"
block|,
literal|"mÃªmes"
block|,
literal|"n"
block|,
literal|"ne"
block|,
literal|"ni"
block|,
literal|"non"
block|,
literal|"nos"
block|,
literal|"notre"
block|,
literal|"nous"
block|,
literal|"nÃ©anmoins"
block|,
literal|"nÃ´tre"
block|,
literal|"nÃ´tres"
block|,
literal|"on"
block|,
literal|"ont"
block|,
literal|"ou"
block|,
literal|"outre"
block|,
literal|"oÃ¹"
block|,
literal|"par"
block|,
literal|"parmi"
block|,
literal|"partant"
block|,
literal|"pas"
block|,
literal|"passÃ©"
block|,
literal|"pendant"
block|,
literal|"plein"
block|,
literal|"plus"
block|,
literal|"plusieurs"
block|,
literal|"pour"
block|,
literal|"pourquoi"
block|,
literal|"proche"
block|,
literal|"prÃ¨s"
block|,
literal|"puisque"
block|,
literal|"qu"
block|,
literal|"quand"
block|,
literal|"que"
block|,
literal|"quel"
block|,
literal|"quelle"
block|,
literal|"quelles"
block|,
literal|"quels"
block|,
literal|"qui"
block|,
literal|"quoi"
block|,
literal|"quoique"
block|,
literal|"revoici"
block|,
literal|"revoilÃ "
block|,
literal|"s"
block|,
literal|"sa"
block|,
literal|"sans"
block|,
literal|"sauf"
block|,
literal|"se"
block|,
literal|"selon"
block|,
literal|"seront"
block|,
literal|"ses"
block|,
literal|"si"
block|,
literal|"sien"
block|,
literal|"sienne"
block|,
literal|"siennes"
block|,
literal|"siens"
block|,
literal|"sinon"
block|,
literal|"soi"
block|,
literal|"soit"
block|,
literal|"son"
block|,
literal|"sont"
block|,
literal|"sous"
block|,
literal|"suivant"
block|,
literal|"sur"
block|,
literal|"ta"
block|,
literal|"te"
block|,
literal|"tes"
block|,
literal|"tien"
block|,
literal|"tienne"
block|,
literal|"tiennes"
block|,
literal|"tiens"
block|,
literal|"toi"
block|,
literal|"ton"
block|,
literal|"tous"
block|,
literal|"tout"
block|,
literal|"toute"
block|,
literal|"toutes"
block|,
literal|"tu"
block|,
literal|"un"
block|,
literal|"une"
block|,
literal|"va"
block|,
literal|"vers"
block|,
literal|"voici"
block|,
literal|"voilÃ "
block|,
literal|"vos"
block|,
literal|"votre"
block|,
literal|"vous"
block|,
literal|"vu"
block|,
literal|"vÃ´tre"
block|,
literal|"vÃ´tres"
block|,
literal|"y"
block|,
literal|"Ã "
block|,
literal|"Ã§a"
block|,
literal|"Ã¨s"
block|,
literal|"Ã©tÃ©"
block|,
literal|"Ãªtre"
block|,
literal|"Ã´"
block|}
decl_stmt|;
comment|/** File containing default French stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"french_stop.txt"
decl_stmt|;
comment|/**    * Contains words that should be indexed but not stemmed.    */
DECL|field|excltable
specifier|private
specifier|final
name|CharArraySet
name|excltable
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
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
comment|/** @deprecated (3.1) remove this in Lucene 5.0, index bw compat */
annotation|@
name|Deprecated
DECL|field|DEFAULT_STOP_SET_30
specifier|static
specifier|final
name|CharArraySet
name|DEFAULT_STOP_SET_30
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
name|FRENCH_STOP_WORDS
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
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
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|SnowballFilter
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
name|Version
operator|.
name|LUCENE_CURRENT
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
comment|/**    * Builds an analyzer with the default stop words ({@link #getDefaultStopSet}).    */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
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
name|DEFAULT_STOP_SET
else|:
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET_30
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
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
comment|/**    * Builds an analyzer with the given stop words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    * @param stemExclutionSet    *          a stemming exclusion set    */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
name|stopwords
parameter_list|,
name|CharArraySet
name|stemExclutionSet
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|excltable
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
name|stemExclutionSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates    * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    * used to tokenize all the text in the provided {@link Reader}.    *     * @return {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}    *         built from a {@link StandardTokenizer} filtered with    *         {@link StandardFilter}, {@link ElisionFilter},    *         {@link LowerCaseFilter}, {@link StopFilter},    *         {@link KeywordMarkerFilter} if a stem exclusion set is    *         provided, and {@link FrenchLightStemFilter}    */
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
name|matchVersion
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|ElisionFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|excltable
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|excltable
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
name|LUCENE_36
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|FrenchLightStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
operator|new
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|FrenchStemmer
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
else|else
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
name|matchVersion
argument_list|,
name|source
argument_list|)
decl_stmt|;
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
if|if
condition|(
operator|!
name|excltable
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|FrenchStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|// Convert to lowercase after stemming!
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
