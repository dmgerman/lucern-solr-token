begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|InputStream
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
name|solr
operator|.
name|common
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
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|ResourceLoaderAware
import|;
end_import
begin_comment
comment|/**  * TokenFilterFactory that creates instances of {@link org.apache.lucene.analysis.hunspell.HunspellStemFilter}.  * Example config for British English including a custom dictionary:  *<pre class="prettyprint">  *&lt;filter class=&quot;solr.HunspellStemFilterFactory&quot;  *    dictionary=&quot;en_GB.dic,my_custom.dic&quot;  *    affix=&quot;en_GB.aff&quot;/&gt;</pre>  * Dictionaries for many languages are available through the OpenOffice project  *<p>See:<a href="http://wiki.services.openoffice.org/wiki/Dictionaries">OpenOffice Dictionaries</a>  */
end_comment
begin_class
DECL|class|HunspellStemFilterFactory
specifier|public
class|class
name|HunspellStemFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|dictionary
specifier|private
name|HunspellDictionary
name|dictionary
decl_stmt|;
comment|/**    * Loads the hunspell dictionary and affix files defined in the configuration    *      * @param loader ResourceLoader used to load the files    */
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|String
name|dictionaryFiles
index|[]
init|=
name|args
operator|.
name|get
argument_list|(
literal|"dictionary"
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
name|affixFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|"affix"
argument_list|)
decl_stmt|;
try|try
block|{
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
name|this
operator|.
name|dictionary
operator|=
operator|new
name|HunspellDictionary
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|affixFile
argument_list|)
argument_list|,
name|dictionaries
argument_list|,
name|luceneMatchVersion
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
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to load hunspell data! [dictionary="
operator|+
name|args
operator|.
name|get
argument_list|(
literal|"dictionary"
argument_list|)
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
block|}
comment|/**    * Creates an instance of {@link org.apache.lucene.analysis.hunspell.HunspellStemFilter} that will filter the given    * TokenStream    *    * @param tokenStream TokenStream that will be filtered    * @return HunspellStemFilter that filters the TokenStream     */
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
