begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|hunspell
operator|.
name|HunspellDictionary
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
name|hunspell
operator|.
name|HunspellStemFilter
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
name|ResourceLoader
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
name|ResourceLoaderAware
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
name|TokenFilterFactory
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
begin_comment
comment|/**  * TokenFilterFactory that creates instances of {@link org.apache.lucene.analysis.hunspell.HunspellStemFilter}.  * Example config for British English including a custom dictionary, case insensitive matching:  *<pre class="prettyprint">  *&lt;filter class=&quot;solr.HunspellStemFilterFactory&quot;  *    dictionary=&quot;en_GB.dic,my_custom.dic&quot;  *    affix=&quot;en_GB.aff&quot;  *    ignoreCase=&quot;true&quot; /&gt;</pre>  * Both parameters dictionary and affix are mandatory.  *<br/>  * The parameter ignoreCase (true/false) controls whether matching is case sensitive or not. Default false.  *<br/>  * The parameter strictAffixParsing (true/false) controls whether the affix parsing is strict or not. Default true.  * If strict an error while reading an affix rule causes a ParseException, otherwise is ignored.  *<br/>  * Dictionaries for many languages are available through the OpenOffice project.  *   * See<a href="http://wiki.apache.org/solr/Hunspell">http://wiki.apache.org/solr/Hunspell</a>  */
end_comment
begin_class
DECL|class|HunspellStemFilterFactory
specifier|public
class|class
name|HunspellStemFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|PARAM_DICTIONARY
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_DICTIONARY
init|=
literal|"dictionary"
decl_stmt|;
DECL|field|PARAM_AFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_AFFIX
init|=
literal|"affix"
decl_stmt|;
DECL|field|PARAM_IGNORE_CASE
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_IGNORE_CASE
init|=
literal|"ignoreCase"
decl_stmt|;
DECL|field|PARAM_STRICT_AFFIX_PARSING
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_STRICT_AFFIX_PARSING
init|=
literal|"strictAffixParsing"
decl_stmt|;
DECL|field|dictionaryArg
specifier|private
specifier|final
name|String
name|dictionaryArg
decl_stmt|;
DECL|field|affixFile
specifier|private
specifier|final
name|String
name|affixFile
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|strictAffixParsing
specifier|private
specifier|final
name|boolean
name|strictAffixParsing
decl_stmt|;
DECL|field|dictionary
specifier|private
name|HunspellDictionary
name|dictionary
decl_stmt|;
comment|/** Creates a new HunspellStemFilterFactory */
DECL|method|HunspellStemFilterFactory
specifier|public
name|HunspellStemFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|dictionaryArg
operator|=
name|args
operator|.
name|remove
argument_list|(
name|PARAM_DICTIONARY
argument_list|)
expr_stmt|;
if|if
condition|(
name|dictionaryArg
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Parameter "
operator|+
name|PARAM_DICTIONARY
operator|+
literal|" is mandatory."
argument_list|)
throw|;
block|}
name|affixFile
operator|=
name|args
operator|.
name|remove
argument_list|(
name|PARAM_AFFIX
argument_list|)
expr_stmt|;
name|ignoreCase
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
name|PARAM_IGNORE_CASE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|strictAffixParsing
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
name|PARAM_STRICT_AFFIX_PARSING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
comment|/**    * Loads the hunspell dictionary and affix files defined in the configuration    *      * @param loader ResourceLoader used to load the files    */
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dictionaryFiles
index|[]
init|=
name|dictionaryArg
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|InputStream
name|affix
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|InputStream
argument_list|>
name|dictionaries
init|=
operator|new
name|ArrayList
argument_list|<
name|InputStream
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|dictionaries
operator|=
operator|new
name|ArrayList
argument_list|<
name|InputStream
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dictionaryFiles
control|)
block|{
name|dictionaries
operator|.
name|add
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|affix
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|affixFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|dictionary
operator|=
operator|new
name|HunspellDictionary
argument_list|(
name|affix
argument_list|,
name|dictionaries
argument_list|,
name|luceneMatchVersion
argument_list|,
name|ignoreCase
argument_list|,
name|strictAffixParsing
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to load hunspell data! [dictionary="
operator|+
name|dictionaryArg
operator|+
literal|",affix="
operator|+
name|affixFile
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|affix
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dictionaries
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates an instance of {@link org.apache.lucene.analysis.hunspell.HunspellStemFilter} that will filter the given    * TokenStream    *    * @param tokenStream TokenStream that will be filtered    * @return HunspellStemFilter that filters the TokenStream     */
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
return|return
operator|new
name|HunspellStemFilter
argument_list|(
name|tokenStream
argument_list|,
name|dictionary
argument_list|)
return|;
block|}
block|}
end_class
end_unit
