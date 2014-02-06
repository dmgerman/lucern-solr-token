begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_comment
comment|/**  * Parameters used across many handlers  */
end_comment
begin_interface
DECL|interface|CommonParams
specifier|public
interface|interface
name|CommonParams
block|{
comment|/**     * Override for the concept of "NOW" to be used throughout this request,     * expressed as milliseconds since epoch.  This is primarily used in     * distributed search to ensure consistent time values are used across     * multiple sub-requests.    */
DECL|field|NOW
specifier|public
specifier|static
specifier|final
name|String
name|NOW
init|=
literal|"NOW"
decl_stmt|;
comment|/**     * Specifies the TimeZone used by the client for the purposes of     * any DateMath rounding that may take place when executing the request    */
DECL|field|TZ
specifier|public
specifier|static
specifier|final
name|String
name|TZ
init|=
literal|"TZ"
decl_stmt|;
comment|/** the Request Handler (formerly known as the Query Type) - which Request Handler should handle the request */
DECL|field|QT
specifier|public
specifier|static
specifier|final
name|String
name|QT
init|=
literal|"qt"
decl_stmt|;
comment|/** the response writer type - the format of the response */
DECL|field|WT
specifier|public
specifier|static
specifier|final
name|String
name|WT
init|=
literal|"wt"
decl_stmt|;
comment|/** query string */
DECL|field|Q
specifier|public
specifier|static
specifier|final
name|String
name|Q
init|=
literal|"q"
decl_stmt|;
comment|/** sort order */
DECL|field|SORT
specifier|public
specifier|static
specifier|final
name|String
name|SORT
init|=
literal|"sort"
decl_stmt|;
comment|/** Lucene query string(s) for filtering the results without affecting scoring */
DECL|field|FQ
specifier|public
specifier|static
specifier|final
name|String
name|FQ
init|=
literal|"fq"
decl_stmt|;
comment|/** zero based offset of matching documents to retrieve */
DECL|field|START
specifier|public
specifier|static
specifier|final
name|String
name|START
init|=
literal|"start"
decl_stmt|;
comment|/** number of documents to return starting at "start" */
DECL|field|ROWS
specifier|public
specifier|static
specifier|final
name|String
name|ROWS
init|=
literal|"rows"
decl_stmt|;
comment|// SOLR-4228 start
comment|/** handler value for SolrPing */
DECL|field|PING_HANDLER
specifier|public
specifier|static
specifier|final
name|String
name|PING_HANDLER
init|=
literal|"/admin/ping"
decl_stmt|;
comment|/** "action" parameter for SolrPing */
DECL|field|ACTION
specifier|public
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"action"
decl_stmt|;
comment|/** "disable" value for SolrPing action */
DECL|field|DISABLE
specifier|public
specifier|static
specifier|final
name|String
name|DISABLE
init|=
literal|"disable"
decl_stmt|;
comment|/** "enable" value for SolrPing action */
DECL|field|ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE
init|=
literal|"enable"
decl_stmt|;
comment|/** "ping" value for SolrPing action */
DECL|field|PING
specifier|public
specifier|static
specifier|final
name|String
name|PING
init|=
literal|"ping"
decl_stmt|;
comment|// SOLR-4228 end
comment|/** stylesheet to apply to XML results */
DECL|field|XSL
specifier|public
specifier|static
specifier|final
name|String
name|XSL
init|=
literal|"xsl"
decl_stmt|;
comment|/** version parameter to check request-response compatibility */
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
comment|/** query and init param for field list */
DECL|field|FL
specifier|public
specifier|static
specifier|final
name|String
name|FL
init|=
literal|"fl"
decl_stmt|;
comment|/** default query field */
DECL|field|DF
specifier|public
specifier|static
specifier|final
name|String
name|DF
init|=
literal|"df"
decl_stmt|;
comment|/** Transformer param -- used with XSLT */
DECL|field|TR
specifier|public
specifier|static
specifier|final
name|String
name|TR
init|=
literal|"tr"
decl_stmt|;
comment|/** whether to include debug data for all components pieces, including doing explains*/
DECL|field|DEBUG_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|DEBUG_QUERY
init|=
literal|"debugQuery"
decl_stmt|;
comment|/**    * Whether to provide debug info for specific items.    *    * @see #DEBUG_QUERY    */
DECL|field|DEBUG
specifier|public
specifier|static
specifier|final
name|String
name|DEBUG
init|=
literal|"debug"
decl_stmt|;
comment|/**    * {@link #DEBUG} value indicating an interest in debug output related to timing    */
DECL|field|TIMING
specifier|public
specifier|static
specifier|final
name|String
name|TIMING
init|=
literal|"timing"
decl_stmt|;
comment|/**    * {@link #DEBUG} value indicating an interest in debug output related to the results (explains)    */
DECL|field|RESULTS
specifier|public
specifier|static
specifier|final
name|String
name|RESULTS
init|=
literal|"results"
decl_stmt|;
comment|/**    * {@link #DEBUG} value indicating an interest in debug output related to the Query (parsing, etc.)    */
DECL|field|QUERY
specifier|public
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"query"
decl_stmt|;
comment|/**    * {@link #DEBUG} value indicating an interest in debug output related to the distributed tracking    */
DECL|field|TRACK
specifier|public
specifier|static
specifier|final
name|String
name|TRACK
init|=
literal|"track"
decl_stmt|;
comment|/**     * boolean indicating whether score explanations should structured (true),     * or plain text (false)    */
DECL|field|EXPLAIN_STRUCT
specifier|public
specifier|static
specifier|final
name|String
name|EXPLAIN_STRUCT
init|=
literal|"debug.explain.structured"
decl_stmt|;
comment|/** another query to explain against */
DECL|field|EXPLAIN_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|EXPLAIN_OTHER
init|=
literal|"explainOther"
decl_stmt|;
comment|/** If the content stream should come from a URL (using URLConnection) */
DECL|field|STREAM_URL
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_URL
init|=
literal|"stream.url"
decl_stmt|;
comment|/** If the content stream should come from a File (using FileReader) */
DECL|field|STREAM_FILE
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_FILE
init|=
literal|"stream.file"
decl_stmt|;
comment|/** If the content stream should come directly from a field */
DECL|field|STREAM_BODY
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_BODY
init|=
literal|"stream.body"
decl_stmt|;
comment|/**     * Explicitly set the content type for the input stream    * If multiple streams are specified, the explicit contentType    * will be used for all of them.      */
DECL|field|STREAM_CONTENTTYPE
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_CONTENTTYPE
init|=
literal|"stream.contentType"
decl_stmt|;
comment|/**    * Timeout value in milliseconds.  If not set, or the value is<= 0, there is no timeout.    */
DECL|field|TIME_ALLOWED
specifier|public
specifier|static
specifier|final
name|String
name|TIME_ALLOWED
init|=
literal|"timeAllowed"
decl_stmt|;
comment|/** 'true' if the header should include the handler name */
DECL|field|HEADER_ECHO_HANDLER
specifier|public
specifier|static
specifier|final
name|String
name|HEADER_ECHO_HANDLER
init|=
literal|"echoHandler"
decl_stmt|;
comment|/** include the parameters in the header **/
DECL|field|HEADER_ECHO_PARAMS
specifier|public
specifier|static
specifier|final
name|String
name|HEADER_ECHO_PARAMS
init|=
literal|"echoParams"
decl_stmt|;
comment|/** include header in the response */
DECL|field|OMIT_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|OMIT_HEADER
init|=
literal|"omitHeader"
decl_stmt|;
comment|/** valid values for:<code>echoParams</code> */
DECL|enum|EchoParamStyle
specifier|public
enum|enum
name|EchoParamStyle
block|{
DECL|enum constant|EXPLICIT
name|EXPLICIT
block|,
DECL|enum constant|ALL
name|ALL
block|,
DECL|enum constant|NONE
name|NONE
block|;
DECL|method|get
specifier|public
specifier|static
name|EchoParamStyle
name|get
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"EXPLICIT"
argument_list|)
condition|)
block|{
return|return
name|EXPLICIT
return|;
block|}
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
return|return
name|ALL
return|;
block|}
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"NONE"
argument_list|)
condition|)
block|{
comment|// the same as nothing...
return|return
name|NONE
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
empty_stmt|;
comment|/** which parameters to log (if not supplied all parameters will be logged) **/
DECL|field|LOG_PARAMS_LIST
specifier|public
specifier|static
specifier|final
name|String
name|LOG_PARAMS_LIST
init|=
literal|"logParamsList"
decl_stmt|;
DECL|field|EXCLUDE
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDE
init|=
literal|"ex"
decl_stmt|;
DECL|field|TAG
specifier|public
specifier|static
specifier|final
name|String
name|TAG
init|=
literal|"tag"
decl_stmt|;
DECL|field|TERMS
specifier|public
specifier|static
specifier|final
name|String
name|TERMS
init|=
literal|"terms"
decl_stmt|;
DECL|field|OUTPUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_KEY
init|=
literal|"key"
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"f"
decl_stmt|;
DECL|field|VALUE
specifier|public
specifier|static
specifier|final
name|String
name|VALUE
init|=
literal|"v"
decl_stmt|;
DECL|field|THREADS
specifier|public
specifier|static
specifier|final
name|String
name|THREADS
init|=
literal|"threads"
decl_stmt|;
DECL|field|TRUE
specifier|public
specifier|static
specifier|final
name|String
name|TRUE
init|=
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|FALSE
specifier|public
specifier|static
specifier|final
name|String
name|FALSE
init|=
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/** Used as a local parameter on queries.  cache=false means don't check any query or filter caches.    * cache=true is the default.    */
DECL|field|CACHE
specifier|public
specifier|static
specifier|final
name|String
name|CACHE
init|=
literal|"cache"
decl_stmt|;
comment|/** Used as a local param on filter queries in conjunction with cache=false.  Filters are checked in order, from    * smallest cost to largest. If cost>=100 and the query implements PostFilter, then that interface will be used to do post query filtering.    */
DECL|field|COST
specifier|public
specifier|static
specifier|final
name|String
name|COST
init|=
literal|"cost"
decl_stmt|;
comment|/**    * Request ID parameter added to the request when using debug=track    */
DECL|field|REQUEST_ID
specifier|public
specifier|static
specifier|final
name|String
name|REQUEST_ID
init|=
literal|"rid"
decl_stmt|;
comment|/**    * Request Purpose parameter added to each internal shard request when using debug=track    */
DECL|field|REQUEST_PURPOSE
specifier|public
specifier|static
specifier|final
name|String
name|REQUEST_PURPOSE
init|=
literal|"requestPurpose"
decl_stmt|;
block|}
end_interface
end_unit
