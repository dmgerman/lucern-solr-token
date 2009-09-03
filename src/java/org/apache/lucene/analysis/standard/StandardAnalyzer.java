begin_unit
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
name|*
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
name|Set
import|;
end_import
begin_comment
comment|/**  * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link  * LowerCaseFilter} and {@link StopFilter}, using a list of  * English stop words.  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating StandardAnalyzer:  *<ul>  *<li> As of 2.9, StopFilter preserves position  *        increments by default  *<li> As of 2.9, Tokens incorrectly identified as acronyms  *        are corrected (see<a href="https://issues.apache.org/jira/browse/LUCENE-1068">LUCENE-1608</a>  *</ul>  *  * @version $Id$  */
end_comment
begin_class
DECL|class|StandardAnalyzer
specifier|public
class|class
name|StandardAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|stopSet
specifier|private
name|Set
name|stopSet
decl_stmt|;
comment|/**    * Specifies whether deprecated acronyms should be replaced with HOST type.    * This is false by default to support backward compatibility.    *     * @deprecated this should be removed in the next release (3.0).    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    */
DECL|field|replaceInvalidAcronym
specifier|private
name|boolean
name|replaceInvalidAcronym
init|=
name|defaultReplaceInvalidAcronym
decl_stmt|;
DECL|field|defaultReplaceInvalidAcronym
specifier|private
specifier|static
name|boolean
name|defaultReplaceInvalidAcronym
decl_stmt|;
DECL|field|enableStopPositionIncrements
specifier|private
name|boolean
name|enableStopPositionIncrements
decl_stmt|;
comment|// @deprecated
DECL|field|useDefaultStopPositionIncrements
specifier|private
name|boolean
name|useDefaultStopPositionIncrements
decl_stmt|;
comment|// Default to true (fixed the bug), unless the system prop is set
static|static
block|{
specifier|final
name|String
name|v
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|v
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
name|defaultReplaceInvalidAcronym
operator|=
literal|true
expr_stmt|;
else|else
name|defaultReplaceInvalidAcronym
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    *    * @return true if new instances of StandardTokenizer will    * replace mischaracterized acronyms    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    * @deprecated This will be removed (hardwired to true) in 3.0    */
DECL|method|getDefaultReplaceInvalidAcronym
specifier|public
specifier|static
name|boolean
name|getDefaultReplaceInvalidAcronym
parameter_list|()
block|{
return|return
name|defaultReplaceInvalidAcronym
return|;
block|}
comment|/**    *    * @param replaceInvalidAcronym Set to true to have new    * instances of StandardTokenizer replace mischaracterized    * acronyms by default.  Set to false to preserve the    * previous (before 2.4) buggy behavior.  Alternatively,    * set the system property    * org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym    * to false.    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    * @deprecated This will be removed (hardwired to true) in 3.0    */
DECL|method|setDefaultReplaceInvalidAcronym
specifier|public
specifier|static
name|void
name|setDefaultReplaceInvalidAcronym
parameter_list|(
name|boolean
name|replaceInvalidAcronym
parameter_list|)
block|{
name|defaultReplaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
comment|/** An array containing some common English words that are usually not   useful for searching.    @deprecated Use {@link #STOP_WORDS_SET} instead */
DECL|field|STOP_WORDS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|STOP_WORDS
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
decl_stmt|;
comment|/** An unmodifiable set containing some common English words that are usually not   useful for searching. */
DECL|field|STOP_WORDS_SET
specifier|public
specifier|static
specifier|final
name|Set
comment|/*<String>*/
name|STOP_WORDS_SET
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
comment|/** Builds an analyzer with the default stop words ({@link    * #STOP_WORDS_SET}).    * @deprecated Use {@link #StandardAnalyzer(Version)} instead. */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the default stop words ({@link    * #STOP_WORDS}).    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the given stop words.    * @deprecated Use {@link #StandardAnalyzer(Version, Set)}    * instead */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Set
name|stopWords
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the given stop words.    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopWords stop words */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
name|stopWords
parameter_list|)
block|{
name|stopSet
operator|=
name|stopWords
expr_stmt|;
name|init
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the given stop words.    * @deprecated Use {@link #StandardAnalyzer(Version, Set)} instead */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|String
index|[]
name|stopWords
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopWords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given file.    * @see WordlistLoader#getWordSet(File)    * @deprecated Use {@link #StandardAnalyzer(Version, File)}    * instead    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given file.    * @see WordlistLoader#getWordSet(File)    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopwords File to read stop words from */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
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
name|stopSet
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.    * @see WordlistLoader#getWordSet(Reader)    * @deprecated Use {@link #StandardAnalyzer(Version, Reader)}    * instead    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Reader
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.    * @see WordlistLoader#getWordSet(Reader)    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopwords Reader to read stop words from */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|stopSet
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|boolean
name|replaceInvalidAcronym
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|STOP_WORDS_SET
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
name|useDefaultStopPositionIncrements
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    *  @param stopwords The stopwords to use    * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Reader
name|stopwords
parameter_list|,
name|boolean
name|replaceInvalidAcronym
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
comment|/**    * @param stopwords The stopwords to use    * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|,
name|boolean
name|replaceInvalidAcronym
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
comment|/**    *    * @param stopwords The stopwords to use    * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|,
name|boolean
name|replaceInvalidAcronym
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
comment|/**    * @param stopwords The stopwords to use    * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Set
name|stopwords
parameter_list|,
name|boolean
name|replaceInvalidAcronym
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
DECL|method|init
specifier|private
specifier|final
name|void
name|init
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|setOverridesTokenStreamMethod
argument_list|(
name|StandardAnalyzer
operator|.
name|class
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
name|LUCENE_29
argument_list|)
condition|)
block|{
name|enableStopPositionIncrements
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|useDefaultStopPositionIncrements
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** Constructs a {@link StandardTokenizer} filtered by a {@link   StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}. */
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
name|StandardTokenizer
name|tokenStream
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|,
name|replaceInvalidAcronym
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|useDefaultStopPositionIncrements
condition|)
block|{
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|enableStopPositionIncrements
argument_list|,
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
specifier|static
specifier|final
class|class
name|SavedStreams
block|{
DECL|field|tokenStream
name|StandardTokenizer
name|tokenStream
decl_stmt|;
DECL|field|filteredTokenStream
name|TokenStream
name|filteredTokenStream
decl_stmt|;
block|}
comment|/** Default maximum allowed token length */
DECL|field|DEFAULT_MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
literal|255
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/**    * Set maximum allowed token length.  If a token is seen    * that exceeds this length then it is discarded.  This    * setting only takes effect the next time tokenStream or    * reusableTokenStream is called.    */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * @see #setMaxTokenLength    */
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
comment|/** @deprecated Use {@link #tokenStream} instead */
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
if|if
condition|(
name|overridesTokenStreamMethod
condition|)
block|{
comment|// LUCENE-1678: force fallback to tokenStream() if we
comment|// have been subclassed and that subclass overrides
comment|// tokenStream but not reusableTokenStream
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
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
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|streams
operator|.
name|tokenStream
operator|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|StandardFilter
argument_list|(
name|streams
operator|.
name|tokenStream
argument_list|)
expr_stmt|;
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|streams
operator|.
name|filteredTokenStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|useDefaultStopPositionIncrements
condition|)
block|{
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|StopFilter
argument_list|(
name|streams
operator|.
name|filteredTokenStream
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|filteredTokenStream
operator|=
operator|new
name|StopFilter
argument_list|(
name|enableStopPositionIncrements
argument_list|,
name|streams
operator|.
name|filteredTokenStream
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|streams
operator|.
name|tokenStream
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|streams
operator|.
name|tokenStream
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
name|streams
operator|.
name|tokenStream
operator|.
name|setReplaceInvalidAcronym
argument_list|(
name|replaceInvalidAcronym
argument_list|)
expr_stmt|;
return|return
name|streams
operator|.
name|filteredTokenStream
return|;
block|}
comment|/**    *    * @return true if this Analyzer is replacing mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    * @deprecated This will be removed (hardwired to true) in 3.0    */
DECL|method|isReplaceInvalidAcronym
specifier|public
name|boolean
name|isReplaceInvalidAcronym
parameter_list|()
block|{
return|return
name|replaceInvalidAcronym
return|;
block|}
comment|/**    *    * @param replaceInvalidAcronym Set to true if this Analyzer is replacing mischaracterized acronyms in the StandardTokenizer    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    * @deprecated This will be removed (hardwired to true) in 3.0    */
DECL|method|setReplaceInvalidAcronym
specifier|public
name|void
name|setReplaceInvalidAcronym
parameter_list|(
name|boolean
name|replaceInvalidAcronym
parameter_list|)
block|{
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
block|}
end_class
end_unit
