begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
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
name|handler
operator|.
name|PingRequestHandler
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
name|LocalSolrQueryRequest
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
name|search
operator|.
name|CacheConfig
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
name|SolrIndexConfig
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
name|search
operator|.
name|BooleanQuery
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|StringTokenizer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
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
begin_comment
comment|/**  * Provides a static reference to a Config object modeling the main  * configuration data for a a Solr instance -- typically found in  * "solrconfig.xml".  *  * @version $Id$  */
end_comment
begin_class
DECL|class|SolrConfig
specifier|public
class|class
name|SolrConfig
extends|extends
name|Config
block|{
DECL|field|DEFAULT_CONF_FILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CONF_FILE
init|=
literal|"solrconfig.xml"
decl_stmt|;
comment|// Compatibility feature for single-core (pre-solr{215,350} patch); should go away at solr-2.0
annotation|@
name|Deprecated
DECL|field|config
specifier|public
specifier|static
name|SolrConfig
name|config
init|=
literal|null
decl_stmt|;
comment|/**    * Singleton keeping track of configuration errors    */
DECL|field|severeErrors
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|Throwable
argument_list|>
name|severeErrors
init|=
operator|new
name|HashSet
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Creates a default instance from the solrconfig.xml. */
DECL|method|SolrConfig
specifier|public
name|SolrConfig
parameter_list|()
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
operator|(
name|SolrResourceLoader
operator|)
literal|null
argument_list|,
name|DEFAULT_CONF_FILE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a configuration instance from a configuration name.    * A default resource loader will be created (@see SolrResourceLoader)    *@param name the configuration name used by the loader    */
DECL|method|SolrConfig
specifier|public
name|SolrConfig
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
operator|(
name|SolrResourceLoader
operator|)
literal|null
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a configuration instance from a configuration name and stream.    * A default resource loader will be created (@see SolrResourceLoader).    * If the stream is null, the resource loader will open the configuration stream.    * If the stream is not null, no attempt to load the resource will occur (the name is not used).    *@param name the configuration name    *@param is the configuration stream    */
DECL|method|SolrConfig
specifier|public
name|SolrConfig
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
operator|(
name|SolrResourceLoader
operator|)
literal|null
argument_list|,
name|name
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a configuration instance from an instance directory, configuration name and stream.    *@param instanceDir the directory used to create the resource loader    *@param name the configuration name used by the loader if the stream is null    *@param is the configuration stream     */
DECL|method|SolrConfig
specifier|public
name|SolrConfig
parameter_list|(
name|String
name|instanceDir
parameter_list|,
name|String
name|name
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|this
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|instanceDir
argument_list|)
argument_list|,
name|name
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a configuration instance from a resource loader, a configuration name and a stream.    * If the stream is null, the resource loader will open the configuration stream.    * If the stream is not null, no attempt to load the resource will occur (the name is not used).    *@param loader the resource loader    *@param name the configuration name    *@param is the configuration stream    */
DECL|method|SolrConfig
name|SolrConfig
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|super
argument_list|(
name|loader
argument_list|,
name|name
argument_list|,
name|is
argument_list|,
literal|"/config/"
argument_list|)
expr_stmt|;
name|defaultIndexConfig
operator|=
operator|new
name|SolrIndexConfig
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mainIndexConfig
operator|=
operator|new
name|SolrIndexConfig
argument_list|(
name|this
argument_list|,
literal|"mainIndex"
argument_list|,
name|defaultIndexConfig
argument_list|)
expr_stmt|;
name|booleanQueryMaxClauseCount
operator|=
name|getInt
argument_list|(
literal|"query/maxBooleanClauses"
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|)
expr_stmt|;
name|filtOptEnabled
operator|=
name|getBool
argument_list|(
literal|"query/boolTofilterOptimizer/@enabled"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|filtOptCacheSize
operator|=
name|getInt
argument_list|(
literal|"query/boolTofilterOptimizer/@cacheSize"
argument_list|,
literal|32
argument_list|)
expr_stmt|;
name|filtOptThreshold
operator|=
name|getFloat
argument_list|(
literal|"query/boolTofilterOptimizer/@threshold"
argument_list|,
literal|.05f
argument_list|)
expr_stmt|;
name|useFilterForSortedQuery
operator|=
name|getBool
argument_list|(
literal|"query/useFilterForSortedQuery"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|queryResultWindowSize
operator|=
name|getInt
argument_list|(
literal|"query/queryResultWindowSize"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResultMaxDocsCached
operator|=
name|getInt
argument_list|(
literal|"query/queryResultMaxDocsCached"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|enableLazyFieldLoading
operator|=
name|getBool
argument_list|(
literal|"query/enableLazyFieldLoading"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|filterCacheConfig
operator|=
name|CacheConfig
operator|.
name|getConfig
argument_list|(
name|this
argument_list|,
literal|"query/filterCache"
argument_list|)
expr_stmt|;
name|queryResultCacheConfig
operator|=
name|CacheConfig
operator|.
name|getConfig
argument_list|(
name|this
argument_list|,
literal|"query/queryResultCache"
argument_list|)
expr_stmt|;
name|documentCacheConfig
operator|=
name|CacheConfig
operator|.
name|getConfig
argument_list|(
name|this
argument_list|,
literal|"query/documentCache"
argument_list|)
expr_stmt|;
name|userCacheConfigs
operator|=
name|CacheConfig
operator|.
name|getMultipleConfigs
argument_list|(
name|this
argument_list|,
literal|"query/cache"
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
operator|.
name|initRegenerators
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|hashSetInverseLoadFactor
operator|=
literal|1.0f
operator|/
name|getFloat
argument_list|(
literal|"//HashDocSet/@loadFactor"
argument_list|,
literal|0.75f
argument_list|)
expr_stmt|;
name|hashDocSetMaxSize
operator|=
name|getInt
argument_list|(
literal|"//HashDocSet/@maxSize"
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|pingQueryParams
operator|=
name|readPingQueryParams
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|httpCachingConfig
operator|=
operator|new
name|HttpCachingConfig
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Config
operator|.
name|log
operator|.
name|info
argument_list|(
literal|"Loaded SolrConfig: "
operator|+
name|name
argument_list|)
expr_stmt|;
comment|// TODO -- at solr 2.0. this should go away
name|config
operator|=
name|this
expr_stmt|;
block|}
comment|/* The set of materialized parameters: */
DECL|field|booleanQueryMaxClauseCount
specifier|public
specifier|final
name|int
name|booleanQueryMaxClauseCount
decl_stmt|;
comment|// SolrIndexSearcher - nutch optimizer
DECL|field|filtOptEnabled
specifier|public
specifier|final
name|boolean
name|filtOptEnabled
decl_stmt|;
DECL|field|filtOptCacheSize
specifier|public
specifier|final
name|int
name|filtOptCacheSize
decl_stmt|;
DECL|field|filtOptThreshold
specifier|public
specifier|final
name|float
name|filtOptThreshold
decl_stmt|;
comment|// SolrIndexSearcher - caches configurations
DECL|field|filterCacheConfig
specifier|public
specifier|final
name|CacheConfig
name|filterCacheConfig
decl_stmt|;
DECL|field|queryResultCacheConfig
specifier|public
specifier|final
name|CacheConfig
name|queryResultCacheConfig
decl_stmt|;
DECL|field|documentCacheConfig
specifier|public
specifier|final
name|CacheConfig
name|documentCacheConfig
decl_stmt|;
DECL|field|userCacheConfigs
specifier|public
specifier|final
name|CacheConfig
index|[]
name|userCacheConfigs
decl_stmt|;
comment|// SolrIndexSearcher - more...
DECL|field|useFilterForSortedQuery
specifier|public
specifier|final
name|boolean
name|useFilterForSortedQuery
decl_stmt|;
DECL|field|queryResultWindowSize
specifier|public
specifier|final
name|int
name|queryResultWindowSize
decl_stmt|;
DECL|field|queryResultMaxDocsCached
specifier|public
specifier|final
name|int
name|queryResultMaxDocsCached
decl_stmt|;
DECL|field|enableLazyFieldLoading
specifier|public
specifier|final
name|boolean
name|enableLazyFieldLoading
decl_stmt|;
comment|// DocSet
DECL|field|hashSetInverseLoadFactor
specifier|public
specifier|final
name|float
name|hashSetInverseLoadFactor
decl_stmt|;
DECL|field|hashDocSetMaxSize
specifier|public
specifier|final
name|int
name|hashDocSetMaxSize
decl_stmt|;
comment|// default& main index configurations
DECL|field|defaultIndexConfig
specifier|public
specifier|final
name|SolrIndexConfig
name|defaultIndexConfig
decl_stmt|;
DECL|field|mainIndexConfig
specifier|public
specifier|final
name|SolrIndexConfig
name|mainIndexConfig
decl_stmt|;
DECL|field|httpCachingConfig
specifier|private
specifier|final
name|HttpCachingConfig
name|httpCachingConfig
decl_stmt|;
DECL|method|getHttpCachingConfig
specifier|public
name|HttpCachingConfig
name|getHttpCachingConfig
parameter_list|()
block|{
return|return
name|httpCachingConfig
return|;
block|}
comment|// ping query request parameters
annotation|@
name|Deprecated
DECL|field|pingQueryParams
specifier|private
specifier|final
name|NamedList
name|pingQueryParams
decl_stmt|;
DECL|method|readPingQueryParams
specifier|static
specifier|private
name|NamedList
name|readPingQueryParams
parameter_list|(
name|SolrConfig
name|config
parameter_list|)
block|{
comment|// TODO: check for nested tags and parse as a named list instead
name|String
name|urlSnippet
init|=
name|config
operator|.
name|get
argument_list|(
literal|"admin/pingQuery"
argument_list|,
literal|""
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|StringTokenizer
name|qtokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|urlSnippet
argument_list|,
literal|"&"
argument_list|)
decl_stmt|;
name|String
name|tok
decl_stmt|;
name|NamedList
name|params
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
while|while
condition|(
name|qtokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|tok
operator|=
name|qtokens
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
index|[]
name|split
init|=
name|tok
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|split
index|[
literal|0
index|]
argument_list|,
name|split
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
comment|/**    * Returns a Request object based on the admin/pingQuery section    * of the Solr config file.    *     * @deprecated use {@link PingRequestHandler} instead     */
annotation|@
name|Deprecated
DECL|method|getPingQueryRequest
specifier|public
name|SolrQueryRequest
name|getPingQueryRequest
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
return|return
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|pingQueryParams
argument_list|)
return|;
block|}
DECL|class|HttpCachingConfig
specifier|public
specifier|static
class|class
name|HttpCachingConfig
block|{
comment|/** config xpath prefix for getting HTTP Caching options */
DECL|field|CACHE_PRE
specifier|private
specifier|final
specifier|static
name|String
name|CACHE_PRE
init|=
literal|"requestDispatcher/httpCaching/"
decl_stmt|;
comment|/** For extracting Expires "ttl" from<cacheControl> config */
DECL|field|MAX_AGE
specifier|private
specifier|final
specifier|static
name|Pattern
name|MAX_AGE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\bmax-age=(\\d+)"
argument_list|)
decl_stmt|;
DECL|enum|LastModFrom
specifier|public
specifier|static
enum|enum
name|LastModFrom
block|{
DECL|enum constant|OPENTIME
DECL|enum constant|DIRLASTMOD
DECL|enum constant|BOGUS
name|OPENTIME
block|,
name|DIRLASTMOD
block|,
name|BOGUS
block|;
comment|/** Input must not be null */
DECL|method|parse
specifier|public
specifier|static
name|LastModFrom
name|parse
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
try|try
block|{
return|return
name|valueOf
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|log
argument_list|(
name|Level
operator|.
name|WARNING
argument_list|,
literal|"Unrecognized value for lastModFrom: "
operator|+
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|BOGUS
return|;
block|}
block|}
block|}
DECL|field|never304
specifier|private
specifier|final
name|boolean
name|never304
decl_stmt|;
DECL|field|etagSeed
specifier|private
specifier|final
name|String
name|etagSeed
decl_stmt|;
DECL|field|cacheControlHeader
specifier|private
specifier|final
name|String
name|cacheControlHeader
decl_stmt|;
DECL|field|maxAge
specifier|private
specifier|final
name|Long
name|maxAge
decl_stmt|;
DECL|field|lastModFrom
specifier|private
specifier|final
name|LastModFrom
name|lastModFrom
decl_stmt|;
DECL|method|HttpCachingConfig
specifier|private
name|HttpCachingConfig
parameter_list|(
name|SolrConfig
name|conf
parameter_list|)
block|{
name|never304
operator|=
name|conf
operator|.
name|getBool
argument_list|(
name|CACHE_PRE
operator|+
literal|"@never304"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|etagSeed
operator|=
name|conf
operator|.
name|get
argument_list|(
name|CACHE_PRE
operator|+
literal|"@etagSeed"
argument_list|,
literal|"Solr"
argument_list|)
expr_stmt|;
name|lastModFrom
operator|=
name|LastModFrom
operator|.
name|parse
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|CACHE_PRE
operator|+
literal|"@lastModFrom"
argument_list|,
literal|"openTime"
argument_list|)
argument_list|)
expr_stmt|;
name|cacheControlHeader
operator|=
name|conf
operator|.
name|get
argument_list|(
name|CACHE_PRE
operator|+
literal|"cacheControl"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Long
name|tmp
init|=
literal|null
decl_stmt|;
comment|// maxAge
if|if
condition|(
literal|null
operator|!=
name|cacheControlHeader
condition|)
block|{
try|try
block|{
specifier|final
name|Matcher
name|ttlMatcher
init|=
name|MAX_AGE
operator|.
name|matcher
argument_list|(
name|cacheControlHeader
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ttlStr
init|=
name|ttlMatcher
operator|.
name|find
argument_list|()
condition|?
name|ttlMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
name|tmp
operator|=
operator|(
literal|null
operator|!=
name|ttlStr
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|ttlStr
argument_list|)
operator|)
condition|?
name|Long
operator|.
name|valueOf
argument_list|(
name|ttlStr
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|log
argument_list|(
name|Level
operator|.
name|WARNING
argument_list|,
literal|"Ignoring exception while attempting to "
operator|+
literal|"extract max-age from cacheControl config: "
operator|+
name|cacheControlHeader
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|maxAge
operator|=
name|tmp
expr_stmt|;
block|}
DECL|method|isNever304
specifier|public
name|boolean
name|isNever304
parameter_list|()
block|{
return|return
name|never304
return|;
block|}
DECL|method|getEtagSeed
specifier|public
name|String
name|getEtagSeed
parameter_list|()
block|{
return|return
name|etagSeed
return|;
block|}
comment|/** null if no Cache-Control header */
DECL|method|getCacheControlHeader
specifier|public
name|String
name|getCacheControlHeader
parameter_list|()
block|{
return|return
name|cacheControlHeader
return|;
block|}
comment|/** null if no max age limitation */
DECL|method|getMaxAge
specifier|public
name|Long
name|getMaxAge
parameter_list|()
block|{
return|return
name|maxAge
return|;
block|}
DECL|method|getLastModFrom
specifier|public
name|LastModFrom
name|getLastModFrom
parameter_list|()
block|{
return|return
name|lastModFrom
return|;
block|}
block|}
block|}
end_class
end_unit
