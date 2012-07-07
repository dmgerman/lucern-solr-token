begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|net
operator|.
name|MalformedURLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|SolrInputDocument
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
name|update
operator|.
name|AddUpdateCommand
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
comment|/**  * Update processor which examines a URL and outputs to various other fields  * characteristics of that URL, including length, number of path levels, whether  * it is a top level URL (levels==0), whether it looks like a landing/index page,  * a canonical representation of the URL (e.g. stripping index.html), the domain  * and path parts of the URL etc.  *<p>  * This processor is intended used in connection with processing web resources,  * and helping to produce values which may be used for boosting or filtering later.  */
end_comment
begin_class
DECL|class|URLClassifyProcessor
specifier|public
class|class
name|URLClassifyProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|INPUT_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|INPUT_FIELD_PARAM
init|=
literal|"inputField"
decl_stmt|;
DECL|field|OUTPUT_LENGTH_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_LENGTH_FIELD_PARAM
init|=
literal|"lengthOutputField"
decl_stmt|;
DECL|field|OUTPUT_LEVELS_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_LEVELS_FIELD_PARAM
init|=
literal|"levelsOutputField"
decl_stmt|;
DECL|field|OUTPUT_TOPLEVEL_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_TOPLEVEL_FIELD_PARAM
init|=
literal|"toplevelOutputField"
decl_stmt|;
DECL|field|OUTPUT_LANDINGPAGE_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_LANDINGPAGE_FIELD_PARAM
init|=
literal|"landingpageOutputField"
decl_stmt|;
DECL|field|OUTPUT_DOMAIN_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_DOMAIN_FIELD_PARAM
init|=
literal|"domainOutputField"
decl_stmt|;
DECL|field|OUTPUT_CANONICALURL_FIELD_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_CANONICALURL_FIELD_PARAM
init|=
literal|"canonicalUrlOutputField"
decl_stmt|;
DECL|field|DEFAULT_URL_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_URL_FIELDNAME
init|=
literal|"url"
decl_stmt|;
DECL|field|DEFAULT_LENGTH_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_LENGTH_FIELDNAME
init|=
literal|"url_length"
decl_stmt|;
DECL|field|DEFAULT_LEVELS_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_LEVELS_FIELDNAME
init|=
literal|"url_levels"
decl_stmt|;
DECL|field|DEFAULT_TOPLEVEL_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TOPLEVEL_FIELDNAME
init|=
literal|"url_toplevel"
decl_stmt|;
DECL|field|DEFAULT_LANDINGPAGE_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_LANDINGPAGE_FIELDNAME
init|=
literal|"url_landingpage"
decl_stmt|;
DECL|field|log
specifier|private
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|URLClassifyProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
DECL|field|urlFieldname
specifier|private
name|String
name|urlFieldname
init|=
name|DEFAULT_URL_FIELDNAME
decl_stmt|;
DECL|field|lengthFieldname
specifier|private
name|String
name|lengthFieldname
init|=
name|DEFAULT_LENGTH_FIELDNAME
decl_stmt|;
DECL|field|levelsFieldname
specifier|private
name|String
name|levelsFieldname
init|=
name|DEFAULT_LEVELS_FIELDNAME
decl_stmt|;
DECL|field|toplevelpageFieldname
specifier|private
name|String
name|toplevelpageFieldname
init|=
name|DEFAULT_TOPLEVEL_FIELDNAME
decl_stmt|;
DECL|field|landingpageFieldname
specifier|private
name|String
name|landingpageFieldname
init|=
name|DEFAULT_LANDINGPAGE_FIELDNAME
decl_stmt|;
DECL|field|domainFieldname
specifier|private
name|String
name|domainFieldname
init|=
literal|null
decl_stmt|;
DECL|field|canonicalUrlFieldname
specifier|private
name|String
name|canonicalUrlFieldname
init|=
literal|null
decl_stmt|;
DECL|field|landingPageSuffixes
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|landingPageSuffixes
init|=
block|{
literal|"/"
block|,
literal|"index.html"
block|,
literal|"index.htm"
block|,
literal|"index.phtml"
block|,
literal|"index.shtml"
block|,
literal|"index.xml"
block|,
literal|"index.php"
block|,
literal|"index.asp"
block|,
literal|"index.aspx"
block|,
literal|"welcome.html"
block|,
literal|"welcome.htm"
block|,
literal|"welcome.phtml"
block|,
literal|"welcome.shtml"
block|,
literal|"welcome.xml"
block|,
literal|"welcome.php"
block|,
literal|"welcome.asp"
block|,
literal|"welcome.aspx"
block|}
decl_stmt|;
DECL|method|URLClassifyProcessor
specifier|public
name|URLClassifyProcessor
parameter_list|(
name|SolrParams
name|parameters
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|,
name|UpdateRequestProcessor
name|nextProcessor
parameter_list|)
block|{
name|super
argument_list|(
name|nextProcessor
argument_list|)
expr_stmt|;
name|this
operator|.
name|initParameters
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
block|}
DECL|method|initParameters
specifier|private
name|void
name|initParameters
parameter_list|(
name|SolrParams
name|parameters
parameter_list|)
block|{
if|if
condition|(
name|parameters
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|setEnabled
argument_list|(
name|parameters
operator|.
name|getBool
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|urlFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|INPUT_FIELD_PARAM
argument_list|,
name|DEFAULT_URL_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|lengthFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|OUTPUT_LENGTH_FIELD_PARAM
argument_list|,
name|DEFAULT_LENGTH_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|levelsFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|OUTPUT_LEVELS_FIELD_PARAM
argument_list|,
name|DEFAULT_LEVELS_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|toplevelpageFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|OUTPUT_TOPLEVEL_FIELD_PARAM
argument_list|,
name|DEFAULT_TOPLEVEL_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|landingpageFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|OUTPUT_LANDINGPAGE_FIELD_PARAM
argument_list|,
name|DEFAULT_LANDINGPAGE_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|domainFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|OUTPUT_DOMAIN_FIELD_PARAM
argument_list|)
expr_stmt|;
name|this
operator|.
name|canonicalUrlFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|OUTPUT_CANONICALURL_FIELD_PARAM
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isEnabled
argument_list|()
condition|)
block|{
name|SolrInputDocument
name|document
init|=
name|command
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|document
operator|.
name|containsKey
argument_list|(
name|urlFieldname
argument_list|)
condition|)
block|{
name|String
name|url
init|=
operator|(
name|String
operator|)
name|document
operator|.
name|getFieldValue
argument_list|(
name|urlFieldname
argument_list|)
decl_stmt|;
try|try
block|{
name|URL
name|normalizedURL
init|=
name|getNormalizedURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|document
operator|.
name|setField
argument_list|(
name|lengthFieldname
argument_list|,
name|length
argument_list|(
name|normalizedURL
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|setField
argument_list|(
name|levelsFieldname
argument_list|,
name|levels
argument_list|(
name|normalizedURL
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|setField
argument_list|(
name|toplevelpageFieldname
argument_list|,
name|isTopLevelPage
argument_list|(
name|normalizedURL
argument_list|)
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|document
operator|.
name|setField
argument_list|(
name|landingpageFieldname
argument_list|,
name|isLandingPage
argument_list|(
name|normalizedURL
argument_list|)
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|domainFieldname
operator|!=
literal|null
condition|)
block|{
name|document
operator|.
name|setField
argument_list|(
name|domainFieldname
argument_list|,
name|normalizedURL
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|canonicalUrlFieldname
operator|!=
literal|null
condition|)
block|{
name|document
operator|.
name|setField
argument_list|(
name|canonicalUrlFieldname
argument_list|,
name|getCanonicalUrl
argument_list|(
name|normalizedURL
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|debug
argument_list|(
name|document
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"cannot get the normalized url for \""
operator|+
name|url
operator|+
literal|"\" due to "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"cannot get the normalized url for \""
operator|+
name|url
operator|+
literal|"\" due to "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets a canonical form of the URL for use as main URL    * @param url The input url    * @return The URL object representing the canonical URL    */
DECL|method|getCanonicalUrl
specifier|public
name|URL
name|getCanonicalUrl
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
comment|// NOTE: Do we want to make sure this URL is normalized? (Christian thinks we should)
name|String
name|urlString
init|=
name|url
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|lps
init|=
name|landingPageSuffix
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
operator|new
name|URL
argument_list|(
name|urlString
operator|.
name|replaceFirst
argument_list|(
literal|"/"
operator|+
name|lps
operator|+
literal|"$"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|url
return|;
block|}
comment|/**    * Calculates the length of the URL in characters    * @param url The input URL    * @return the length of the URL    */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
return|return
name|url
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Calculates the number of path levels in the given URL    * @param url The input URL    * @return the number of levels, where a top-level URL is 0    */
DECL|method|levels
specifier|public
name|int
name|levels
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
comment|// Remove any trailing slashes for the purpose of level counting
name|String
name|path
init|=
name|getPathWithoutSuffix
argument_list|(
name|url
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"/+$"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|int
name|levels
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|path
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'/'
condition|)
block|{
name|levels
operator|++
expr_stmt|;
block|}
block|}
return|return
name|levels
return|;
block|}
comment|/**    * Calculates whether a URL is a top level page    * @param url The input URL    * @return true if page is a top level page    */
DECL|method|isTopLevelPage
specifier|public
name|boolean
name|isTopLevelPage
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
comment|// Remove any trailing slashes for the purpose of level counting
name|String
name|path
init|=
name|getPathWithoutSuffix
argument_list|(
name|url
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"/+$"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|url
operator|.
name|getQuery
argument_list|()
operator|==
literal|null
return|;
block|}
comment|/**    * Calculates whether the URL is a landing page or not    * @param url The input URL    * @return true if URL represents a landing page (index page)    */
DECL|method|isLandingPage
specifier|public
name|boolean
name|isLandingPage
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
if|if
condition|(
name|url
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|landingPageSuffix
argument_list|(
name|url
argument_list|)
operator|!=
literal|""
return|;
block|}
block|}
DECL|method|getNormalizedURL
specifier|public
name|URL
name|getNormalizedURL
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
return|return
operator|new
name|URI
argument_list|(
name|url
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toURL
argument_list|()
return|;
block|}
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|setEnabled
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|landingPageSuffix
specifier|private
name|String
name|landingPageSuffix
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|String
name|path
init|=
name|url
operator|.
name|getPath
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|suffix
range|:
name|landingPageSuffixes
control|)
block|{
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
return|return
name|suffix
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
DECL|method|getPathWithoutSuffix
specifier|private
name|String
name|getPathWithoutSuffix
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
return|return
name|url
operator|.
name|getPath
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|replaceFirst
argument_list|(
name|landingPageSuffix
argument_list|(
name|url
argument_list|)
operator|+
literal|"$"
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
end_class
end_unit
