begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.servlet.cache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|cache
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|WeakHashMap
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|WeakIdentityMap
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
name|SuppressForbidden
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
name|IndexDeletionPolicyWrapper
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
name|core
operator|.
name|SolrConfig
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
name|SolrConfig
operator|.
name|HttpCachingConfig
operator|.
name|LastModFrom
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
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|SolrIndexSearcher
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
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import
begin_class
DECL|class|HttpCacheHeaderUtil
specifier|public
specifier|final
class|class
name|HttpCacheHeaderUtil
block|{
DECL|method|sendNotModified
specifier|public
specifier|static
name|void
name|sendNotModified
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|)
block|{
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_MODIFIED
argument_list|)
expr_stmt|;
block|}
DECL|method|sendPreconditionFailed
specifier|public
specifier|static
name|void
name|sendPreconditionFailed
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|)
block|{
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_PRECONDITION_FAILED
argument_list|)
expr_stmt|;
block|}
comment|/**    * Weak Ref based cache for keeping track of core specific etagSeed    * and the last computed etag.    *    * @see #calcEtag    */
DECL|field|etagCoreCache
specifier|private
specifier|static
name|WeakIdentityMap
argument_list|<
name|SolrCore
argument_list|,
name|EtagCacheVal
argument_list|>
name|etagCoreCache
init|=
name|WeakIdentityMap
operator|.
name|newConcurrentHashMap
argument_list|()
decl_stmt|;
comment|/** @see #etagCoreCache */
DECL|class|EtagCacheVal
specifier|private
specifier|static
class|class
name|EtagCacheVal
block|{
DECL|field|etagSeed
specifier|private
specifier|final
name|String
name|etagSeed
decl_stmt|;
DECL|field|etagCache
specifier|private
name|String
name|etagCache
init|=
literal|null
decl_stmt|;
DECL|field|indexVersionCache
specifier|private
name|long
name|indexVersionCache
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|EtagCacheVal
specifier|public
name|EtagCacheVal
parameter_list|(
specifier|final
name|String
name|etagSeed
parameter_list|)
block|{
name|this
operator|.
name|etagSeed
operator|=
name|etagSeed
expr_stmt|;
block|}
DECL|method|calcEtag
specifier|public
name|String
name|calcEtag
parameter_list|(
specifier|final
name|long
name|currentIndexVersion
parameter_list|)
block|{
if|if
condition|(
name|currentIndexVersion
operator|!=
name|indexVersionCache
condition|)
block|{
name|indexVersionCache
operator|=
name|currentIndexVersion
expr_stmt|;
try|try
block|{
name|etagCache
operator|=
literal|"\""
operator|+
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|encodeBase64
argument_list|(
operator|(
name|Long
operator|.
name|toHexString
argument_list|(
name|Long
operator|.
name|reverse
argument_list|(
name|indexVersionCache
argument_list|)
argument_list|)
operator|+
name|etagSeed
operator|)
operator|.
name|getBytes
argument_list|(
literal|"US-ASCII"
argument_list|)
argument_list|)
argument_list|,
literal|"US-ASCII"
argument_list|)
operator|+
literal|"\""
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// may not happen
block|}
block|}
return|return
name|etagCache
return|;
block|}
block|}
comment|/**    * Calculates a tag for the ETag header.    *    * @return a tag    */
DECL|method|calcEtag
specifier|public
specifier|static
name|String
name|calcEtag
parameter_list|(
specifier|final
name|SolrQueryRequest
name|solrReq
parameter_list|)
block|{
specifier|final
name|SolrCore
name|core
init|=
name|solrReq
operator|.
name|getCore
argument_list|()
decl_stmt|;
specifier|final
name|long
name|currentIndexVersion
init|=
name|solrReq
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|EtagCacheVal
name|etagCache
init|=
name|etagCoreCache
operator|.
name|get
argument_list|(
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|etagCache
condition|)
block|{
specifier|final
name|String
name|etagSeed
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getHttpCachingConfig
argument_list|()
operator|.
name|getEtagSeed
argument_list|()
decl_stmt|;
name|etagCache
operator|=
operator|new
name|EtagCacheVal
argument_list|(
name|etagSeed
argument_list|)
expr_stmt|;
name|etagCoreCache
operator|.
name|put
argument_list|(
name|core
argument_list|,
name|etagCache
argument_list|)
expr_stmt|;
block|}
return|return
name|etagCache
operator|.
name|calcEtag
argument_list|(
name|currentIndexVersion
argument_list|)
return|;
block|}
comment|/**    * Checks if one of the tags in the list equals the given etag.    *     * @param headerList    *            the ETag header related header elements    * @param etag    *            the ETag to compare with    * @return true if the etag is found in one of the header elements - false    *         otherwise    */
DECL|method|isMatchingEtag
specifier|public
specifier|static
name|boolean
name|isMatchingEtag
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|headerList
parameter_list|,
specifier|final
name|String
name|etag
parameter_list|)
block|{
for|for
control|(
name|String
name|header
range|:
name|headerList
control|)
block|{
specifier|final
name|String
index|[]
name|headerEtags
init|=
name|header
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|headerEtags
control|)
block|{
name|s
operator|=
name|s
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|etag
argument_list|)
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Calculate the appropriate last-modified time for Solr relative the current request.    *     * @return the timestamp to use as a last modified time.    */
DECL|method|calcLastModified
specifier|public
specifier|static
name|long
name|calcLastModified
parameter_list|(
specifier|final
name|SolrQueryRequest
name|solrReq
parameter_list|)
block|{
specifier|final
name|SolrCore
name|core
init|=
name|solrReq
operator|.
name|getCore
argument_list|()
decl_stmt|;
specifier|final
name|SolrIndexSearcher
name|searcher
init|=
name|solrReq
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
specifier|final
name|LastModFrom
name|lastModFrom
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getHttpCachingConfig
argument_list|()
operator|.
name|getLastModFrom
argument_list|()
decl_stmt|;
name|long
name|lastMod
decl_stmt|;
try|try
block|{
comment|// assume default, change if needed (getOpenTime() should be fast)
name|lastMod
operator|=
name|LastModFrom
operator|.
name|DIRLASTMOD
operator|==
name|lastModFrom
condition|?
name|IndexDeletionPolicyWrapper
operator|.
name|getCommitTimestamp
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getIndexCommit
argument_list|()
argument_list|)
else|:
name|searcher
operator|.
name|getOpenTimeStamp
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// we're pretty freaking screwed if this happens
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Get the time where the searcher has been opened
comment|// We get rid of the milliseconds because the HTTP header has only
comment|// second granularity
return|return
name|lastMod
operator|-
operator|(
name|lastMod
operator|%
literal|1000L
operator|)
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis to send out cache control headers externally"
argument_list|)
DECL|method|timeNowForHeader
specifier|private
specifier|static
name|long
name|timeNowForHeader
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
comment|/**    * Set the Cache-Control HTTP header (and Expires if needed)    * based on the SolrConfig.    * @param conf The config of the SolrCore handling this request    * @param resp The servlet response object to modify    * @param method The request method (GET, POST, ...) used by this request    */
DECL|method|setCacheControlHeader
specifier|public
specifier|static
name|void
name|setCacheControlHeader
parameter_list|(
specifier|final
name|SolrConfig
name|conf
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|,
specifier|final
name|Method
name|method
parameter_list|)
block|{
comment|// We do not emit HTTP header for POST and OTHER request types
if|if
condition|(
name|Method
operator|.
name|POST
operator|==
name|method
operator|||
name|Method
operator|.
name|OTHER
operator|==
name|method
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|cc
init|=
name|conf
operator|.
name|getHttpCachingConfig
argument_list|()
operator|.
name|getCacheControlHeader
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|cc
condition|)
block|{
name|resp
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
name|cc
argument_list|)
expr_stmt|;
block|}
name|Long
name|maxAge
init|=
name|conf
operator|.
name|getHttpCachingConfig
argument_list|()
operator|.
name|getMaxAge
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|maxAge
condition|)
block|{
name|resp
operator|.
name|setDateHeader
argument_list|(
literal|"Expires"
argument_list|,
name|timeNowForHeader
argument_list|()
operator|+
operator|(
name|maxAge
operator|*
literal|1000L
operator|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|/**    * Sets HTTP Response cache validator headers appropriately and    * validates the HTTP Request against these using any conditional    * request headers.    *    * If the request contains conditional headers, and those headers    * indicate a match with the current known state of the system, this    * method will return "true" indicating that a 304 Status code can be    * returned, and no further processing is needed.    *    *     * @return true if the request contains conditional headers, and those    *         headers indicate a match with the current known state of the    *         system -- indicating that a 304 Status code can be returned to    *         the client, and no further request processing is needed.      */
DECL|method|doCacheHeaderValidation
specifier|public
specifier|static
name|boolean
name|doCacheHeaderValidation
parameter_list|(
specifier|final
name|SolrQueryRequest
name|solrReq
parameter_list|,
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|Method
name|reqMethod
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|)
block|{
if|if
condition|(
name|Method
operator|.
name|POST
operator|==
name|reqMethod
operator|||
name|Method
operator|.
name|OTHER
operator|==
name|reqMethod
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|long
name|lastMod
init|=
name|HttpCacheHeaderUtil
operator|.
name|calcLastModified
argument_list|(
name|solrReq
argument_list|)
decl_stmt|;
specifier|final
name|String
name|etag
init|=
name|HttpCacheHeaderUtil
operator|.
name|calcEtag
argument_list|(
name|solrReq
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|lastMod
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setHeader
argument_list|(
literal|"ETag"
argument_list|,
name|etag
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkETagValidators
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|reqMethod
argument_list|,
name|etag
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|checkLastModValidators
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|lastMod
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Check for etag related conditional headers and set status     *     * @return true if no request processing is necessary and HTTP response status has been set, false otherwise.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|checkETagValidators
specifier|public
specifier|static
name|boolean
name|checkETagValidators
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|,
specifier|final
name|Method
name|reqMethod
parameter_list|,
specifier|final
name|String
name|etag
parameter_list|)
block|{
comment|// First check If-None-Match because this is the common used header
comment|// element by HTTP clients
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ifNoneMatchList
init|=
name|Collections
operator|.
name|list
argument_list|(
name|req
operator|.
name|getHeaders
argument_list|(
literal|"If-None-Match"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ifNoneMatchList
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|isMatchingEtag
argument_list|(
name|ifNoneMatchList
argument_list|,
name|etag
argument_list|)
condition|)
block|{
if|if
condition|(
name|reqMethod
operator|==
name|Method
operator|.
name|GET
operator|||
name|reqMethod
operator|==
name|Method
operator|.
name|HEAD
condition|)
block|{
name|sendNotModified
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendPreconditionFailed
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|// Check for If-Match headers
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ifMatchList
init|=
name|Collections
operator|.
name|list
argument_list|(
name|req
operator|.
name|getHeaders
argument_list|(
literal|"If-Match"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ifMatchList
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|isMatchingEtag
argument_list|(
name|ifMatchList
argument_list|,
name|etag
argument_list|)
condition|)
block|{
name|sendPreconditionFailed
argument_list|(
name|resp
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Check for modify time related conditional headers and set status     *     * @return true if no request processing is necessary and HTTP response status has been set, false otherwise.    */
DECL|method|checkLastModValidators
specifier|public
specifier|static
name|boolean
name|checkLastModValidators
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|,
specifier|final
name|long
name|lastMod
parameter_list|)
block|{
try|try
block|{
comment|// First check for If-Modified-Since because this is the common
comment|// used header by HTTP clients
specifier|final
name|long
name|modifiedSince
init|=
name|req
operator|.
name|getDateHeader
argument_list|(
literal|"If-Modified-Since"
argument_list|)
decl_stmt|;
if|if
condition|(
name|modifiedSince
operator|!=
operator|-
literal|1L
operator|&&
name|lastMod
operator|<=
name|modifiedSince
condition|)
block|{
comment|// Send a "not-modified"
name|sendNotModified
argument_list|(
name|resp
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|final
name|long
name|unmodifiedSince
init|=
name|req
operator|.
name|getDateHeader
argument_list|(
literal|"If-Unmodified-Since"
argument_list|)
decl_stmt|;
if|if
condition|(
name|unmodifiedSince
operator|!=
operator|-
literal|1L
operator|&&
name|lastMod
operator|>
name|unmodifiedSince
condition|)
block|{
comment|// Send a "precondition failed"
name|sendPreconditionFailed
argument_list|(
name|resp
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// one of our date headers was not formated properly, ignore it
comment|/* NOOP */
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Checks if the downstream request handler wants to avoid HTTP caching of    * the response.    *     * @param solrRsp The Solr response object    * @param resp The HTTP servlet response object    * @param reqMethod The HTTP request type    */
DECL|method|checkHttpCachingVeto
specifier|public
specifier|static
name|void
name|checkHttpCachingVeto
parameter_list|(
specifier|final
name|SolrQueryResponse
name|solrRsp
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|,
specifier|final
name|Method
name|reqMethod
parameter_list|)
block|{
comment|// For POST we do nothing. They never get cached
if|if
condition|(
name|Method
operator|.
name|POST
operator|==
name|reqMethod
operator|||
name|Method
operator|.
name|OTHER
operator|==
name|reqMethod
condition|)
block|{
return|return;
block|}
comment|// If the request handler has not vetoed and there is no
comment|// exception silently return
if|if
condition|(
name|solrRsp
operator|.
name|isHttpCaching
argument_list|()
operator|&&
name|solrRsp
operator|.
name|getException
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Otherwise we tell the caches that we don't want to cache the response
name|resp
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache, no-store"
argument_list|)
expr_stmt|;
comment|// For HTTP/1.0 proxy caches
name|resp
operator|.
name|setHeader
argument_list|(
literal|"Pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
comment|// This sets the expiry date to a date in the past
comment|// As long as no time machines get invented this is safe
name|resp
operator|.
name|setHeader
argument_list|(
literal|"Expires"
argument_list|,
literal|"Sat, 01 Jan 2000 01:00:00 GMT"
argument_list|)
expr_stmt|;
name|long
name|timeNowForHeader
init|=
name|timeNowForHeader
argument_list|()
decl_stmt|;
comment|// We signal "just modified" just in case some broken
comment|// proxy cache does not follow the above headers
name|resp
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|timeNowForHeader
argument_list|)
expr_stmt|;
comment|// We override the ETag with something different
name|resp
operator|.
name|setHeader
argument_list|(
literal|"ETag"
argument_list|,
literal|'"'
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|timeNowForHeader
argument_list|)
operator|+
literal|'"'
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
