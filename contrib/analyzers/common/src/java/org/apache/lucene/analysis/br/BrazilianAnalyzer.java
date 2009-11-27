begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.br
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|br
package|;
end_package
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * {@link Analyzer} for Brazilian Portuguese language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (words that will  * not be stemmed, but indexed).  *</p>  *  *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment
begin_class
DECL|class|BrazilianAnalyzer
specifier|public
specifier|final
class|class
name|BrazilianAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * List of typical Brazilian Portuguese stopwords. 	 * @deprecated use {@link #getDefaultStopSet()} instead 	 */
comment|// TODO make this private in 3.1
DECL|field|BRAZILIAN_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|BRAZILIAN_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"ainda"
block|,
literal|"alem"
block|,
literal|"ambas"
block|,
literal|"ambos"
block|,
literal|"antes"
block|,
literal|"ao"
block|,
literal|"aonde"
block|,
literal|"aos"
block|,
literal|"apos"
block|,
literal|"aquele"
block|,
literal|"aqueles"
block|,
literal|"as"
block|,
literal|"assim"
block|,
literal|"com"
block|,
literal|"como"
block|,
literal|"contra"
block|,
literal|"contudo"
block|,
literal|"cuja"
block|,
literal|"cujas"
block|,
literal|"cujo"
block|,
literal|"cujos"
block|,
literal|"da"
block|,
literal|"das"
block|,
literal|"de"
block|,
literal|"dela"
block|,
literal|"dele"
block|,
literal|"deles"
block|,
literal|"demais"
block|,
literal|"depois"
block|,
literal|"desde"
block|,
literal|"desta"
block|,
literal|"deste"
block|,
literal|"dispoe"
block|,
literal|"dispoem"
block|,
literal|"diversa"
block|,
literal|"diversas"
block|,
literal|"diversos"
block|,
literal|"do"
block|,
literal|"dos"
block|,
literal|"durante"
block|,
literal|"e"
block|,
literal|"ela"
block|,
literal|"elas"
block|,
literal|"ele"
block|,
literal|"eles"
block|,
literal|"em"
block|,
literal|"entao"
block|,
literal|"entre"
block|,
literal|"essa"
block|,
literal|"essas"
block|,
literal|"esse"
block|,
literal|"esses"
block|,
literal|"esta"
block|,
literal|"estas"
block|,
literal|"este"
block|,
literal|"estes"
block|,
literal|"ha"
block|,
literal|"isso"
block|,
literal|"isto"
block|,
literal|"logo"
block|,
literal|"mais"
block|,
literal|"mas"
block|,
literal|"mediante"
block|,
literal|"menos"
block|,
literal|"mesma"
block|,
literal|"mesmas"
block|,
literal|"mesmo"
block|,
literal|"mesmos"
block|,
literal|"na"
block|,
literal|"nas"
block|,
literal|"nao"
block|,
literal|"nas"
block|,
literal|"nem"
block|,
literal|"nesse"
block|,
literal|"neste"
block|,
literal|"nos"
block|,
literal|"o"
block|,
literal|"os"
block|,
literal|"ou"
block|,
literal|"outra"
block|,
literal|"outras"
block|,
literal|"outro"
block|,
literal|"outros"
block|,
literal|"pelas"
block|,
literal|"pelas"
block|,
literal|"pelo"
block|,
literal|"pelos"
block|,
literal|"perante"
block|,
literal|"pois"
block|,
literal|"por"
block|,
literal|"porque"
block|,
literal|"portanto"
block|,
literal|"proprio"
block|,
literal|"propios"
block|,
literal|"quais"
block|,
literal|"qual"
block|,
literal|"qualquer"
block|,
literal|"quando"
block|,
literal|"quanto"
block|,
literal|"que"
block|,
literal|"quem"
block|,
literal|"quer"
block|,
literal|"se"
block|,
literal|"seja"
block|,
literal|"sem"
block|,
literal|"sendo"
block|,
literal|"seu"
block|,
literal|"seus"
block|,
literal|"sob"
block|,
literal|"sobre"
block|,
literal|"sua"
block|,
literal|"suas"
block|,
literal|"tal"
block|,
literal|"tambem"
block|,
literal|"teu"
block|,
literal|"teus"
block|,
literal|"toda"
block|,
literal|"todas"
block|,
literal|"todo"
block|,
literal|"todos"
block|,
literal|"tua"
block|,
literal|"tuas"
block|,
literal|"tudo"
block|,
literal|"um"
block|,
literal|"uma"
block|,
literal|"umas"
block|,
literal|"uns"
block|}
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop-words set.    * @return an unmodifiable instance of the default stop-words set.    */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
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
name|DEFAULT_STOP_SET
return|;
block|}
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
DECL|field|DEFAULT_STOP_SET
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_STOP_SET
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|BRAZILIAN_STOP_WORDS
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
block|}
comment|/** 	 * Contains the stopwords used with the {@link StopFilter}. 	 */
DECL|field|stoptable
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stoptable
decl_stmt|;
comment|/** 	 * Contains words that should be indexed but not stemmed. 	 */
comment|// TODO make this private in 3.1
DECL|field|excltable
specifier|private
name|Set
argument_list|<
name|?
argument_list|>
name|excltable
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/** 	 * Builds an analyzer with the default stop words ({@link #BRAZILIAN_STOP_WORDS}). 	 */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
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
name|stoptable
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words and stemming exclusion words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
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
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
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
name|stemExclusionSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 * @deprecated use {@link #BrazilianAnalyzer(Version, Set)} instead 	 */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
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
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.     * @deprecated use {@link #BrazilianAnalyzer(Version, Set)} instead    */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
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
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #BrazilianAnalyzer(Version, Set)} instead    */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
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
comment|/** 	 * Builds an exclusionlist from an array of Strings. 	 * @deprecated use {@link #BrazilianAnalyzer(Version, Set, Set)} instead 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
modifier|...
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopSet
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
comment|/** 	 * Builds an exclusionlist from a {@link Map}. 	 * @deprecated use {@link #BrazilianAnalyzer(Version, Set, Set)} instead 	 */
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
name|excltable
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
comment|/** 	 * Builds an exclusionlist from the words contained in the given file. 	 * @deprecated use {@link #BrazilianAnalyzer(Version, Set, Set)} instead 	 */
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
name|excltable
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
comment|/** 	 * Creates a {@link TokenStream} which tokenizes all the text in the provided {@link Reader}. 	 * 	 * @return  A {@link TokenStream} built from a {@link StandardTokenizer} filtered with 	 * 			{@link LowerCaseFilter}, {@link StandardFilter}, {@link StopFilter}, and  	 *          {@link BrazilianStemFilter}. 	 */
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
specifier|final
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
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
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
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|BrazilianStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|source
name|Tokenizer
name|source
decl_stmt|;
DECL|field|result
name|TokenStream
name|result
decl_stmt|;
block|}
empty_stmt|;
comment|/**      * Returns a (possibly reused) {@link TokenStream} which tokenizes all the text       * in the provided {@link Reader}.      *      * @return  A {@link TokenStream} built from a {@link StandardTokenizer} filtered with      *          {@link LowerCaseFilter}, {@link StandardFilter}, {@link StopFilter}, and       *          {@link BrazilianStemFilter}.      */
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SavedStreams
name|streams
init|=
operator|(
name|SavedStreams
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
name|streams
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|streams
operator|.
name|source
operator|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|streams
operator|.
name|source
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|streams
operator|.
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|BrazilianStemFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|source
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|streams
operator|.
name|result
return|;
block|}
block|}
end_class
end_unit
