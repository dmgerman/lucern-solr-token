begin_unit
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|commons
operator|.
name|io
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|SolrPluginUtils
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
name|SolrCoreAware
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cybozu
operator|.
name|labs
operator|.
name|langdetect
operator|.
name|DetectorFactory
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cybozu
operator|.
name|labs
operator|.
name|langdetect
operator|.
name|LangDetectException
import|;
end_import
begin_comment
comment|/**  * Identifies the language of a set of input fields using   * http://code.google.com/p/language-detection  *<p/>  * The UpdateProcessorChain config entry can take a number of parameters  * which may also be passed as HTTP parameters on the update request  * and override the defaults. Here is the simplest processor config possible:  *   *<pre class="prettyprint">  *&lt;processor class=&quot;org.apache.solr.update.processor.LangDetectLanguageIdentifierUpdateProcessorFactory&quot;&gt;  *&lt;str name=&quot;langid.fl&quot;&gt;title,text&lt;/str&gt;  *&lt;str name=&quot;langid.langField&quot;&gt;language_s&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  * See<a href="http://wiki.apache.org/solr/LanguageDetection">http://wiki.apache.org/solr/LanguageDetection</a>  * @since 3.5  */
end_comment
begin_class
DECL|class|LangDetectLanguageIdentifierUpdateProcessorFactory
specifier|public
class|class
name|LangDetectLanguageIdentifierUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
implements|,
name|LangIdParams
block|{
DECL|field|defaults
specifier|protected
name|SolrParams
name|defaults
decl_stmt|;
DECL|field|appends
specifier|protected
name|SolrParams
name|appends
decl_stmt|;
DECL|field|invariants
specifier|protected
name|SolrParams
name|invariants
decl_stmt|;
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{   }
comment|/**    * The UpdateRequestProcessor may be initialized in solrconfig.xml similarly    * to a RequestHandler, with defaults, appends and invariants.    * @param args a NamedList with the configuration parameters     */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
try|try
block|{
name|loadData
argument_list|()
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
literal|"Couldn't load profile data, will return empty languages always!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
decl_stmt|;
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"defaults"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"appends"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|appends
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"invariants"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|invariants
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
comment|// Process defaults, appends and invariants if we got a request
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
block|{
name|SolrPluginUtils
operator|.
name|setDefaults
argument_list|(
name|req
argument_list|,
name|defaults
argument_list|,
name|appends
argument_list|,
name|invariants
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LangDetectLanguageIdentifierUpdateProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|next
argument_list|)
return|;
block|}
comment|// DetectorFactory is totally global, so we only want to do this once... ever!!!
DECL|field|loaded
specifier|static
name|boolean
name|loaded
decl_stmt|;
comment|// profiles we will load from classpath
DECL|field|languages
specifier|static
specifier|final
name|String
name|languages
index|[]
init|=
block|{
literal|"af"
block|,
literal|"ar"
block|,
literal|"bg"
block|,
literal|"bn"
block|,
literal|"cs"
block|,
literal|"da"
block|,
literal|"de"
block|,
literal|"el"
block|,
literal|"en"
block|,
literal|"es"
block|,
literal|"et"
block|,
literal|"fa"
block|,
literal|"fi"
block|,
literal|"fr"
block|,
literal|"gu"
block|,
literal|"he"
block|,
literal|"hi"
block|,
literal|"hr"
block|,
literal|"hu"
block|,
literal|"id"
block|,
literal|"it"
block|,
literal|"ja"
block|,
literal|"kn"
block|,
literal|"ko"
block|,
literal|"lt"
block|,
literal|"lv"
block|,
literal|"mk"
block|,
literal|"ml"
block|,
literal|"mr"
block|,
literal|"ne"
block|,
literal|"nl"
block|,
literal|"no"
block|,
literal|"pa"
block|,
literal|"pl"
block|,
literal|"pt"
block|,
literal|"ro"
block|,
literal|"ru"
block|,
literal|"sk"
block|,
literal|"sl"
block|,
literal|"so"
block|,
literal|"sq"
block|,
literal|"sv"
block|,
literal|"sw"
block|,
literal|"ta"
block|,
literal|"te"
block|,
literal|"th"
block|,
literal|"tl"
block|,
literal|"tr"
block|,
literal|"uk"
block|,
literal|"ur"
block|,
literal|"vi"
block|,
literal|"zh-cn"
block|,
literal|"zh-tw"
block|}
decl_stmt|;
DECL|method|loadData
specifier|public
specifier|static
specifier|synchronized
name|void
name|loadData
parameter_list|()
throws|throws
name|IOException
throws|,
name|LangDetectException
block|{
if|if
condition|(
name|loaded
condition|)
return|return;
name|loaded
operator|=
literal|true
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|profileData
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|language
range|:
name|languages
control|)
block|{
name|InputStream
name|stream
init|=
name|LangDetectLanguageIdentifierUpdateProcessor
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"langdetect-profiles/"
operator|+
name|language
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
name|profileData
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|IOUtils
operator|.
name|toCharArray
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|DetectorFactory
operator|.
name|loadProfile
argument_list|(
name|profileData
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
