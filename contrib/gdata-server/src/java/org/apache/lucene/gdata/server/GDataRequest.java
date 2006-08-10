begin_unit
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.server
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|StringTokenizer
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
name|Cookie
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchema
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
name|gdata
operator|.
name|search
operator|.
name|query
operator|.
name|QueryTranslator
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
name|gdata
operator|.
name|server
operator|.
name|authentication
operator|.
name|AuthenticationController
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|GDataServerRegistry
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ProvidedService
import|;
end_import
begin_comment
comment|/**  * The GDataRequest Class wraps the incoming HttpServletRequest. Needed  * information coming with the HttpServletRequest can be accessed directly. It  * represents an abstraction on the plain HttpServletRequest. Every GData  * specific data coming from the client will be available and can be accessed  * via the GDataRequest.  *<p>  * GDataRequest instances will be passed to any action requested by the client.  * This class also holds the logic to retrieve important information like  * response format, the requested feed instance and query parameters.  *   *</p>  *   * @author Simon Willnauer  *   */
end_comment
begin_comment
comment|/* this class might be extracted as an interface in later development */
end_comment
begin_class
DECL|class|GDataRequest
specifier|public
class|class
name|GDataRequest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|GDataRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESPONSE_FORMAT_PARAMETER
specifier|private
specifier|static
specifier|final
name|String
name|RESPONSE_FORMAT_PARAMETER
init|=
literal|"alt"
decl_stmt|;
DECL|field|RESPONSE_FORMAT_PARAMETER_RSS
specifier|private
specifier|static
specifier|final
name|String
name|RESPONSE_FORMAT_PARAMETER_RSS
init|=
literal|"rss"
decl_stmt|;
DECL|field|DEFAULT_ITEMS_PER_PAGE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_ITEMS_PER_PAGE
init|=
literal|25
decl_stmt|;
DECL|field|DEFAULT_START_INDEX
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_START_INDEX
init|=
literal|1
decl_stmt|;
DECL|field|START_INDEX_NEXT_PAGE_PARAMETER
specifier|private
specifier|static
specifier|final
name|String
name|START_INDEX_NEXT_PAGE_PARAMETER
init|=
literal|"start-index"
decl_stmt|;
DECL|field|ITEMS_PER_PAGE_PARAMETER
specifier|private
specifier|static
specifier|final
name|String
name|ITEMS_PER_PAGE_PARAMETER
init|=
literal|"max-results"
decl_stmt|;
DECL|field|contextPath
specifier|private
name|String
name|contextPath
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|RESPONSE_FORMAT_PARAMETER_ATOM
specifier|private
specifier|static
specifier|final
name|String
name|RESPONSE_FORMAT_PARAMETER_ATOM
init|=
literal|"atom"
decl_stmt|;
DECL|field|HTTP_HEADER_IF_MODIFIED_SINCE
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_HEADER_IF_MODIFIED_SINCE
init|=
literal|"If-Modified-Since"
decl_stmt|;
DECL|field|HTTP_HEADER_AUTH
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_HEADER_AUTH
init|=
literal|"Authorization"
decl_stmt|;
DECL|field|CATEGORY_QUERY_INDICATOR
specifier|private
specifier|static
specifier|final
name|Object
name|CATEGORY_QUERY_INDICATOR
init|=
literal|"-"
decl_stmt|;
comment|// Atom is the default response format
DECL|field|responseFormat
specifier|private
name|OutputFormat
name|responseFormat
init|=
name|OutputFormat
operator|.
name|ATOM
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|HttpServletRequest
name|request
decl_stmt|;
DECL|field|feedId
specifier|private
name|String
name|feedId
init|=
literal|null
decl_stmt|;
DECL|field|entryId
specifier|private
name|String
name|entryId
init|=
literal|null
decl_stmt|;
DECL|field|service
specifier|private
name|String
name|service
init|=
literal|null
decl_stmt|;
DECL|field|configurator
specifier|private
name|ProvidedService
name|configurator
init|=
literal|null
decl_stmt|;
DECL|field|isSearchRequest
specifier|private
name|boolean
name|isSearchRequest
init|=
literal|false
decl_stmt|;
DECL|field|entryVersion
specifier|private
name|String
name|entryVersion
init|=
literal|null
decl_stmt|;
DECL|field|type
specifier|private
name|GDataRequestType
name|type
decl_stmt|;
DECL|field|categoryQuery
specifier|private
name|String
name|categoryQuery
decl_stmt|;
DECL|field|translatedSearchQuery
specifier|private
name|String
name|translatedSearchQuery
decl_stmt|;
DECL|field|isFeedRequest
specifier|private
name|boolean
name|isFeedRequest
init|=
literal|false
decl_stmt|;
comment|/**      * Creates a new FeedRequest      *       * @param requst -      *            the incoming HttpServletReqeust      * @param type -      *            the request type      *       */
DECL|method|GDataRequest
specifier|public
name|GDataRequest
parameter_list|(
specifier|final
name|HttpServletRequest
name|requst
parameter_list|,
specifier|final
name|GDataRequestType
name|type
parameter_list|)
block|{
if|if
condition|(
name|requst
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"request must not be null "
argument_list|)
throw|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"request type must not be null "
argument_list|)
throw|;
name|this
operator|.
name|request
operator|=
name|requst
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Initialize the GDataRequest. This will initialize all needed values /      * attributes in this request.      *       * @throws GDataRequestException      */
DECL|method|initializeRequest
specifier|public
name|void
name|initializeRequest
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|generateIdentificationProperties
argument_list|()
expr_stmt|;
name|setOutputFormat
argument_list|()
expr_stmt|;
try|try
block|{
comment|/*              * ExtensionProfile and the type is used for building the Entry /              * Feed Instances from an input stream or reader              *               */
name|this
operator|.
name|configurator
operator|=
name|GDataServerRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|getProvidedService
argument_list|(
name|this
operator|.
name|service
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|configurator
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GDataRequestException
argument_list|(
literal|"no Provided Service found for service id: "
operator|+
name|this
operator|.
name|service
argument_list|,
name|GDataResponse
operator|.
name|NOT_FOUND
argument_list|)
throw|;
name|applyRequestParameter
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|translatedSearchQuery
operator|!=
literal|null
condition|)
name|this
operator|.
name|isSearchRequest
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataRequestException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GDataRequestException
argument_list|(
literal|"failed to initialize GDataRequest -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|,
name|GDataResponse
operator|.
name|SERVER_ERROR
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applyRequestParameter
specifier|private
name|void
name|applyRequestParameter
parameter_list|()
throws|throws
name|GDataRequestException
block|{
name|IndexSchema
name|schema
init|=
name|this
operator|.
name|configurator
operator|.
name|getIndexSchema
argument_list|()
decl_stmt|;
try|try
block|{
name|this
operator|.
name|translatedSearchQuery
operator|=
name|QueryTranslator
operator|.
name|translateHttpSearchRequest
argument_list|(
name|schema
argument_list|,
name|this
operator|.
name|request
operator|.
name|getParameterMap
argument_list|()
argument_list|,
name|this
operator|.
name|categoryQuery
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
name|GDataRequestException
argument_list|(
literal|"Can not translate user query to search query"
argument_list|,
name|e
argument_list|,
name|GDataResponse
operator|.
name|BAD_REQUEST
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return - the id of the requested feed      */
DECL|method|getFeedId
specifier|public
name|String
name|getFeedId
parameter_list|()
block|{
return|return
name|this
operator|.
name|feedId
return|;
block|}
comment|/**      * @return - the entry id of the requested Entry if specified, otherwise      *<code>null</code>      */
DECL|method|getEntryId
specifier|public
name|String
name|getEntryId
parameter_list|()
block|{
return|return
name|this
operator|.
name|entryId
return|;
block|}
comment|/**      * @return the version Id of the requested Entry if specified, otherwise      *<code>null</code>      */
DECL|method|getEntryVersion
specifier|public
name|String
name|getEntryVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|entryVersion
return|;
block|}
comment|/**      * A Reader instance to read form the client input stream      *       * @return - the HttpServletRequest {@link Reader}      * @throws IOException -      *             if an I/O Exception occurs      */
DECL|method|getReader
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|request
operator|.
name|getReader
argument_list|()
return|;
block|}
comment|/**      * Returns the {@link HttpServletRequest} parameter map containing all      *<i>GET</i> request parameters.      *       * @return the parameter map      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getQueryParameter
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|getQueryParameter
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
operator|.
name|getParameterMap
argument_list|()
return|;
block|}
comment|/**      * The {@link HttpServletRequest} request parameter names      *       * @return parameter names enumeration      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getQueryParameterNames
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getQueryParameterNames
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
operator|.
name|getParameterNames
argument_list|()
return|;
block|}
comment|/**      * Either<i>Atom</i> or<i>RSS</i>      *       * @return - The output format requested by the client      */
DECL|method|getRequestedResponseFormat
specifier|public
name|OutputFormat
name|getRequestedResponseFormat
parameter_list|()
block|{
return|return
name|this
operator|.
name|responseFormat
return|;
block|}
DECL|method|generateIdentificationProperties
specifier|private
name|void
name|generateIdentificationProperties
parameter_list|()
throws|throws
name|GDataRequestException
block|{
comment|/* generate all needed data to identify the requested feed/entry */
name|String
name|pathInfo
init|=
name|this
operator|.
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|.
name|length
argument_list|()
operator|<=
literal|1
condition|)
throw|throw
operator|new
name|GDataRequestException
argument_list|(
literal|"No feed or entry specified for this request"
argument_list|,
name|GDataResponse
operator|.
name|BAD_REQUEST
argument_list|)
throw|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|pathInfo
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|this
operator|.
name|service
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
throw|throw
operator|new
name|GDataRequestException
argument_list|(
literal|"Can not find feed id in requested path "
operator|+
name|pathInfo
argument_list|,
name|GDataResponse
operator|.
name|BAD_REQUEST
argument_list|)
throw|;
name|this
operator|.
name|feedId
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|appendix
init|=
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|?
name|tokenizer
operator|.
name|nextToken
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|appendix
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|isFeedRequest
operator|=
literal|true
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|appendix
operator|.
name|equals
argument_list|(
name|CATEGORY_QUERY_INDICATOR
argument_list|)
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
name|builder
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|tokenizer
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|categoryQuery
operator|=
name|builder
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|entryId
operator|=
name|appendix
expr_stmt|;
name|this
operator|.
name|entryVersion
operator|=
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|?
name|tokenizer
operator|.
name|nextToken
argument_list|()
else|:
literal|""
expr_stmt|;
block|}
name|this
operator|.
name|isFeedRequest
operator|=
operator|(
name|this
operator|.
name|type
operator|==
name|GDataRequestType
operator|.
name|GET
operator|&&
operator|(
name|this
operator|.
name|entryId
operator|==
literal|null
operator|||
name|this
operator|.
name|entryId
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|this
operator|.
name|entryId
operator|.
name|equals
argument_list|(
literal|'/'
argument_list|)
operator|)
operator|)
operator|)
expr_stmt|;
block|}
DECL|method|setOutputFormat
specifier|private
name|void
name|setOutputFormat
parameter_list|()
block|{
name|String
name|formatParameter
init|=
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|RESPONSE_FORMAT_PARAMETER
argument_list|)
decl_stmt|;
if|if
condition|(
name|formatParameter
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|formatParameter
operator|.
name|equalsIgnoreCase
argument_list|(
name|RESPONSE_FORMAT_PARAMETER_RSS
argument_list|)
condition|)
name|this
operator|.
name|responseFormat
operator|=
name|OutputFormat
operator|.
name|RSS
expr_stmt|;
block|}
comment|/**      * @return - the number of returned items per page      */
DECL|method|getItemsPerPage
specifier|public
name|int
name|getItemsPerPage
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|ITEMS_PER_PAGE_PARAMETER
argument_list|)
operator|==
literal|null
condition|)
return|return
name|DEFAULT_ITEMS_PER_PAGE
return|;
name|int
name|retval
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|retval
operator|=
operator|new
name|Integer
argument_list|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|ITEMS_PER_PAGE_PARAMETER
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Items per page could not be parsed - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
operator|<
literal|0
condition|?
name|DEFAULT_ITEMS_PER_PAGE
else|:
name|retval
return|;
block|}
comment|/**      * Start index represents the number of the first entry of the query -      * result. The order depends on the query. Is the query a search query the      * this value will be assigned to the score in a common feed query the value      * will be assigned to the update time of the entries.      *       * @return - the requested start index      */
DECL|method|getStartIndex
specifier|public
name|int
name|getStartIndex
parameter_list|()
block|{
name|String
name|startIndex
init|=
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|START_INDEX_NEXT_PAGE_PARAMETER
argument_list|)
decl_stmt|;
if|if
condition|(
name|startIndex
operator|==
literal|null
condition|)
return|return
name|DEFAULT_START_INDEX
return|;
name|int
name|retval
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|retval
operator|=
operator|new
name|Integer
argument_list|(
name|startIndex
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Start-index could not be parsed - not an integer - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
operator|<
literal|0
condition|?
name|DEFAULT_START_INDEX
else|:
name|retval
return|;
block|}
comment|/**      * The self id is the feeds<i>href</i> pointing to the requested resource      *       * @return - the self id      */
DECL|method|getSelfId
specifier|public
name|String
name|getSelfId
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|buildRequestIDString
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**        * The previous id is the feeds<i>href</i> pointing to the previous result of the requested resource      *       * @return - the self id      */
DECL|method|getPreviousId
specifier|public
name|String
name|getPreviousId
parameter_list|()
block|{
name|int
name|startIndex
init|=
name|getStartIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|startIndex
operator|==
name|DEFAULT_START_INDEX
condition|)
return|return
literal|null
return|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|buildRequestIDString
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|startIndex
operator|=
name|startIndex
operator|-
name|getItemsPerPage
argument_list|()
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getPreparedQueryString
argument_list|(
name|startIndex
operator|<
literal|1
condition|?
name|DEFAULT_START_INDEX
else|:
name|startIndex
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getPreparedQueryString
specifier|private
name|String
name|getPreparedQueryString
parameter_list|(
name|int
name|startIndex
parameter_list|)
block|{
name|String
name|queryString
init|=
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
name|String
name|startIndexValue
init|=
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|START_INDEX_NEXT_PAGE_PARAMETER
argument_list|)
decl_stmt|;
name|String
name|maxResultsValue
init|=
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|ITEMS_PER_PAGE_PARAMETER
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"?"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxResultsValue
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|ITEMS_PER_PAGE_PARAMETER
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|DEFAULT_ITEMS_PER_PAGE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|startIndexValue
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|START_INDEX_NEXT_PAGE_PARAMETER
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|startIndex
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|queryString
operator|.
name|replaceAll
argument_list|(
literal|"start-index=[\\d]*"
argument_list|,
name|START_INDEX_NEXT_PAGE_PARAMETER
operator|+
literal|"="
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|startIndex
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * The<i>href</i> id of the next page of the requested resource.      *       * @return the id of the next page      */
DECL|method|getNextId
specifier|public
name|String
name|getNextId
parameter_list|()
block|{
name|int
name|startIndex
init|=
name|getStartIndex
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|buildRequestIDString
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|startIndex
operator|=
name|startIndex
operator|+
name|getItemsPerPage
argument_list|()
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getPreparedQueryString
argument_list|(
name|startIndex
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|buildRequestIDString
specifier|private
name|String
name|buildRequestIDString
parameter_list|(
name|boolean
name|endingSlash
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"http://"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
literal|"Host"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|this
operator|.
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|endingSlash
operator|&&
name|builder
operator|.
name|charAt
argument_list|(
name|builder
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
condition|)
name|builder
operator|.
name|setLength
argument_list|(
name|builder
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|endingSlash
operator|&&
name|builder
operator|.
name|charAt
argument_list|(
name|builder
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'/'
condition|)
name|builder
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * This will return the current query string including all parameters.      * Additionally the<code>max-resul</code> parameter will be added if not      * specified.      *<p>      *<code>max-resul</code> indicates the number of results returned to the      * client. The default value is 25.      *</p>      *       * @return - the query string including all parameters      */
DECL|method|getQueryString
specifier|public
name|String
name|getQueryString
parameter_list|()
block|{
name|String
name|retVal
init|=
name|this
operator|.
name|request
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|request
operator|.
name|getParameter
argument_list|(
name|ITEMS_PER_PAGE_PARAMETER
argument_list|)
operator|!=
literal|null
condition|)
return|return
name|retVal
return|;
name|String
name|tempString
init|=
operator|(
name|retVal
operator|==
literal|null
condition|?
name|ITEMS_PER_PAGE_PARAMETER
operator|+
literal|"="
operator|+
name|DEFAULT_ITEMS_PER_PAGE
else|:
literal|"&"
operator|+
name|ITEMS_PER_PAGE_PARAMETER
operator|+
literal|"="
operator|+
name|DEFAULT_ITEMS_PER_PAGE
operator|)
decl_stmt|;
return|return
name|retVal
operator|==
literal|null
condition|?
name|tempString
else|:
name|retVal
operator|+
name|tempString
return|;
block|}
comment|/**      * This enum represents the OutputFormat of the GDATA Server      *       * @author Simon Willnauer      *       */
DECL|enum|OutputFormat
specifier|public
specifier|static
enum|enum
name|OutputFormat
block|{
comment|/**          * Output format ATOM. ATOM is the default response format.          */
DECL|enum constant|ATOM
name|ATOM
block|,
comment|/**          * Output format RSS          */
DECL|enum constant|RSS
name|RSS
block|}
comment|/**      * Returns the requested path including the domain name and the requested      * resource<i>http://www.apache.org/path/resource/</i>      *       * @return the context path      */
DECL|method|getContextPath
specifier|public
name|String
name|getContextPath
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|contextPath
operator|==
literal|null
condition|)
name|this
operator|.
name|contextPath
operator|=
name|buildRequestIDString
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|contextPath
return|;
block|}
comment|/**      * Indicates the request type      *       * @author Simon Willnauer      *       */
DECL|enum|GDataRequestType
specifier|public
enum|enum
name|GDataRequestType
block|{
comment|/**          * Type FeedRequest          */
DECL|enum constant|GET
name|GET
block|,
comment|/**          * Type UpdateRequest          */
DECL|enum constant|UPDATE
name|UPDATE
block|,
comment|/**          * Type DeleteRequest          */
DECL|enum constant|DELETE
name|DELETE
block|,
comment|/**          * Type InsertRequest          */
DECL|enum constant|INSERT
name|INSERT
block|}
comment|/**      * {@link GDataRequestType}      *       * @return the current request type      */
DECL|method|getType
specifier|public
name|GDataRequestType
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
comment|/**      * If the request is a {@link GDataRequestType#GET} request and there is no      * entry id specified, the requested resource is a feed.      *       * @return -<code>true</code> if an only if the requested resource is a      *         feed      */
DECL|method|isFeedRequested
specifier|public
name|boolean
name|isFeedRequested
parameter_list|()
block|{
return|return
name|this
operator|.
name|isFeedRequest
return|;
block|}
comment|/**      * * If the request is a {@link GDataRequestType#GET} request and there is      * an entry id specified, the requested resource is an entry.      *       * @return -<code>true</code> if an only if the requested resource is an      *         entry      */
DECL|method|isEntryRequested
specifier|public
name|boolean
name|isEntryRequested
parameter_list|()
block|{
return|return
operator|!
name|this
operator|.
name|isFeedRequested
argument_list|()
return|;
block|}
comment|/**      * @return -<code>true</code> if an only if the user request is a search request, otherwise<code>false</code>      */
DECL|method|isSearchRequested
specifier|public
name|boolean
name|isSearchRequested
parameter_list|()
block|{
return|return
name|this
operator|.
name|isSearchRequest
return|;
block|}
comment|/**      * @return the configuration for this request      */
DECL|method|getConfigurator
specifier|public
name|ProvidedService
name|getConfigurator
parameter_list|()
block|{
return|return
name|this
operator|.
name|configurator
return|;
block|}
comment|/**      * @return - Returns the Internet Protocol (IP) address of the client or      *         last proxy that sent the request.      */
DECL|method|getRemoteAddress
specifier|public
name|String
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
operator|.
name|getRemoteAddr
argument_list|()
return|;
block|}
comment|/**      * @return - the value for the send auth token. The auth token will be send      *         as a request<tt>Authentication</tt> header.      */
DECL|method|getAuthToken
specifier|public
name|String
name|getAuthToken
parameter_list|()
block|{
name|String
name|token
init|=
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
name|HTTP_HEADER_AUTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|token
operator|=
name|token
operator|.
name|substring
argument_list|(
name|token
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
comment|/**      * @return - Returns an array containing all of the Cookie objects the      *         client sent with underlying HttpServletRequest.      */
DECL|method|getCookies
specifier|public
name|Cookie
index|[]
name|getCookies
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
operator|.
name|getCookies
argument_list|()
return|;
block|}
comment|/**      * @return - the cookie set instead of the authentication token or      *<code>null</code> if no auth cookie is set      */
DECL|method|getAuthCookie
specifier|public
name|Cookie
name|getAuthCookie
parameter_list|()
block|{
name|Cookie
index|[]
name|cookies
init|=
name|this
operator|.
name|request
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|cookies
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cookies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|cookies
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|AuthenticationController
operator|.
name|TOKEN_KEY
argument_list|)
condition|)
return|return
name|cookies
index|[
name|i
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @return - the date string of the<tt>If-Modified-Since</tt> HTTP      *         request header, or null if header is not set      */
DECL|method|getModifiedSince
specifier|public
name|String
name|getModifiedSince
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
operator|.
name|getHeader
argument_list|(
name|HTTP_HEADER_IF_MODIFIED_SINCE
argument_list|)
return|;
block|}
comment|/**      * @return - the underlying HttpServletRequest      */
DECL|method|getHttpServletRequest
specifier|public
name|HttpServletRequest
name|getHttpServletRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
return|;
block|}
DECL|method|getTranslatedQuery
specifier|protected
name|String
name|getTranslatedQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|translatedSearchQuery
return|;
block|}
block|}
end_class
end_unit
