begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.http
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|http
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
name|DataOutputStream
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
name|ObjectOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Locale
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
name|ServletException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletOutputStream
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
name|http
operator|.
name|HttpStatus
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
name|replicator
operator|.
name|Replicator
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
name|replicator
operator|.
name|SessionToken
import|;
end_import
begin_comment
comment|/**  * A server-side service for handling replication requests. The service assumes  * requests are sent in the format  *<code>/&lt;context&gt;/&lt;shard&gt;/&lt;action&gt;</code> where  *<ul>  *<li>{@code context} is the servlet context, e.g. {@link #REPLICATION_CONTEXT}  *<li>{@code shard} is the ID of the shard, e.g. "s1"  *<li>{@code action} is one of {@link ReplicationAction} values  *</ul>  * For example, to check whether there are revision updates for shard "s1" you  * should send the request:<code>http://host:port/replicate/s1/update</code>.  *<p>  * This service is written like a servlet, and  * {@link #perform(HttpServletRequest, HttpServletResponse)} takes servlet  * request and response accordingly, so it is quite easy to embed in your  * application's servlet.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|ReplicationService
specifier|public
class|class
name|ReplicationService
block|{
comment|/** Actions supported by the {@link ReplicationService}. */
DECL|enum|ReplicationAction
specifier|public
enum|enum
name|ReplicationAction
block|{
DECL|enum constant|OBTAIN
DECL|enum constant|RELEASE
DECL|enum constant|UPDATE
name|OBTAIN
block|,
name|RELEASE
block|,
name|UPDATE
block|}
comment|/** The context path for the servlet. */
DECL|field|REPLICATION_CONTEXT
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATION_CONTEXT
init|=
literal|"/replicate"
decl_stmt|;
comment|/** Request parameter name for providing the revision version. */
DECL|field|REPLICATE_VERSION_PARAM
specifier|public
specifier|final
specifier|static
name|String
name|REPLICATE_VERSION_PARAM
init|=
literal|"version"
decl_stmt|;
comment|/** Request parameter name for providing a session ID. */
DECL|field|REPLICATE_SESSION_ID_PARAM
specifier|public
specifier|final
specifier|static
name|String
name|REPLICATE_SESSION_ID_PARAM
init|=
literal|"sessionid"
decl_stmt|;
comment|/** Request parameter name for providing the file's source. */
DECL|field|REPLICATE_SOURCE_PARAM
specifier|public
specifier|final
specifier|static
name|String
name|REPLICATE_SOURCE_PARAM
init|=
literal|"source"
decl_stmt|;
comment|/** Request parameter name for providing the file's name. */
DECL|field|REPLICATE_FILENAME_PARAM
specifier|public
specifier|final
specifier|static
name|String
name|REPLICATE_FILENAME_PARAM
init|=
literal|"filename"
decl_stmt|;
DECL|field|SHARD_IDX
DECL|field|ACTION_IDX
specifier|private
specifier|static
specifier|final
name|int
name|SHARD_IDX
init|=
literal|0
decl_stmt|,
name|ACTION_IDX
init|=
literal|1
decl_stmt|;
DECL|field|replicators
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Replicator
argument_list|>
name|replicators
decl_stmt|;
DECL|method|ReplicationService
specifier|public
name|ReplicationService
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Replicator
argument_list|>
name|replicators
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|replicators
operator|=
name|replicators
expr_stmt|;
block|}
comment|/**    * Returns the path elements that were given in the servlet request, excluding    * the servlet's action context.    */
DECL|method|getPathElements
specifier|private
name|String
index|[]
name|getPathElements
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|path
init|=
name|req
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
name|String
name|pathInfo
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|!=
literal|null
condition|)
block|{
name|path
operator|+=
name|pathInfo
expr_stmt|;
block|}
name|int
name|actionLen
init|=
name|REPLICATION_CONTEXT
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|startIdx
init|=
name|actionLen
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
name|actionLen
operator|&&
name|path
operator|.
name|charAt
argument_list|(
name|actionLen
argument_list|)
operator|==
literal|'/'
condition|)
block|{
operator|++
name|startIdx
expr_stmt|;
block|}
comment|// split the string on '/' and remove any empty elements. This is better
comment|// than using String.split() since the latter may return empty elements in
comment|// the array
name|StringTokenizer
name|stok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|path
operator|.
name|substring
argument_list|(
name|startIdx
argument_list|)
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|elements
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|stok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
name|stok
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|elements
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|extractRequestParam
specifier|private
specifier|static
name|String
name|extractRequestParam
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|String
name|paramName
parameter_list|)
throws|throws
name|ServletException
block|{
name|String
name|param
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|paramName
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Missing mandatory parameter: "
operator|+
name|paramName
argument_list|)
throw|;
block|}
return|return
name|param
return|;
block|}
DECL|method|copy
specifier|private
specifier|static
name|void
name|copy
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|16384
index|]
decl_stmt|;
name|int
name|numRead
decl_stmt|;
while|while
condition|(
operator|(
name|numRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|numRead
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Executes the replication task. */
DECL|method|perform
specifier|public
name|void
name|perform
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|String
index|[]
name|pathElements
init|=
name|getPathElements
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathElements
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"invalid path, must contain shard ID and action, e.g. */s1/update"
argument_list|)
throw|;
block|}
specifier|final
name|ReplicationAction
name|action
decl_stmt|;
try|try
block|{
name|action
operator|=
name|ReplicationAction
operator|.
name|valueOf
argument_list|(
name|pathElements
index|[
name|ACTION_IDX
index|]
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Unsupported action provided: "
operator|+
name|pathElements
index|[
name|ACTION_IDX
index|]
argument_list|)
throw|;
block|}
specifier|final
name|Replicator
name|replicator
init|=
name|replicators
operator|.
name|get
argument_list|(
name|pathElements
index|[
name|SHARD_IDX
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"unrecognized shard ID "
operator|+
name|pathElements
index|[
name|SHARD_IDX
index|]
argument_list|)
throw|;
block|}
name|ServletOutputStream
name|resOut
init|=
name|resp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|OBTAIN
case|:
specifier|final
name|String
name|sessionID
init|=
name|extractRequestParam
argument_list|(
name|req
argument_list|,
name|REPLICATE_SESSION_ID_PARAM
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fileName
init|=
name|extractRequestParam
argument_list|(
name|req
argument_list|,
name|REPLICATE_FILENAME_PARAM
argument_list|)
decl_stmt|;
specifier|final
name|String
name|source
init|=
name|extractRequestParam
argument_list|(
name|req
argument_list|,
name|REPLICATE_SOURCE_PARAM
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|replicator
operator|.
name|obtainFile
argument_list|(
name|sessionID
argument_list|,
name|source
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
try|try
block|{
name|copy
argument_list|(
name|in
argument_list|,
name|resOut
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|RELEASE
case|:
name|replicator
operator|.
name|release
argument_list|(
name|extractRequestParam
argument_list|(
name|req
argument_list|,
name|REPLICATE_SESSION_ID_PARAM
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|UPDATE
case|:
name|String
name|currVersion
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|REPLICATE_VERSION_PARAM
argument_list|)
decl_stmt|;
name|SessionToken
name|token
init|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
name|currVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|resOut
operator|.
name|write
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// marker for null token
block|}
else|else
block|{
name|resOut
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// marker for null token
name|token
operator|.
name|serialize
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|resOut
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpStatus
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
comment|// propagate the failure
try|try
block|{
comment|/*          * Note: it is assumed that "identified exceptions" are thrown before          * anything was written to the stream.          */
name|ObjectOutputStream
name|oos
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|resOut
argument_list|)
decl_stmt|;
name|oos
operator|.
name|writeObject
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|oos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not serialize"
argument_list|,
name|e2
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|resp
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
