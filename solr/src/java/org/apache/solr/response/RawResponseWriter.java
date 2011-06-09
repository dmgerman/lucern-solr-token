begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|OutputStream
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
name|io
operator|.
name|Writer
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
name|util
operator|.
name|ContentStream
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_comment
comment|/**  * Writes a ContentStream directly to the output.  *  *<p>  * This writer is a special case that extends and alters the  * QueryResponseWriter contract.  If SolrQueryResponse contains a  * ContentStream added with the key {@link #CONTENT}  * then this writer will output that stream exactly as is (with it's  * Content-Type).  if no such ContentStream has been added, then a  * "base" QueryResponseWriter will be used to write the response  * according to the usual contract.  The name of the "base" writer can  * be specified as an initialization param for this writer, or it  * defaults to the "standard" writer.  *</p>  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|RawResponseWriter
specifier|public
class|class
name|RawResponseWriter
implements|implements
name|BinaryQueryResponseWriter
block|{
comment|/**     * The key that should be used to add a ContentStream to the     * SolrQueryResponse if you intend to use this Writer.    */
DECL|field|CONTENT
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT
init|=
literal|"content"
decl_stmt|;
DECL|field|_baseWriter
specifier|private
name|String
name|_baseWriter
init|=
literal|null
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|n
parameter_list|)
block|{
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|Object
name|base
init|=
name|n
operator|.
name|get
argument_list|(
literal|"base"
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|_baseWriter
operator|=
name|base
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Even if this is null, it should be ok
DECL|method|getBaseWriter
specifier|protected
name|QueryResponseWriter
name|getBaseWriter
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
name|_baseWriter
argument_list|)
return|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
name|Object
name|obj
init|=
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|CONTENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
operator|(
name|obj
operator|instanceof
name|ContentStream
operator|)
condition|)
block|{
return|return
operator|(
operator|(
name|ContentStream
operator|)
name|obj
operator|)
operator|.
name|getContentType
argument_list|()
return|;
block|}
return|return
name|getBaseWriter
argument_list|(
name|request
argument_list|)
operator|.
name|getContentType
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|obj
init|=
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|CONTENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
operator|(
name|obj
operator|instanceof
name|ContentStream
operator|)
condition|)
block|{
comment|// copy the contents to the writer...
name|ContentStream
name|content
init|=
operator|(
name|ContentStream
operator|)
name|obj
decl_stmt|;
name|Reader
name|reader
init|=
name|content
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|getBaseWriter
argument_list|(
name|request
argument_list|)
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|obj
init|=
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
name|CONTENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
operator|(
name|obj
operator|instanceof
name|ContentStream
operator|)
condition|)
block|{
comment|// copy the contents to the writer...
name|ContentStream
name|content
init|=
operator|(
name|ContentStream
operator|)
name|obj
decl_stmt|;
name|java
operator|.
name|io
operator|.
name|InputStream
name|in
init|=
name|content
operator|.
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|out
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
block|}
else|else
block|{
comment|//getBaseWriter( request ).write( writer, request, response );
throw|throw
operator|new
name|IOException
argument_list|(
literal|"did not find a CONTENT object"
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
