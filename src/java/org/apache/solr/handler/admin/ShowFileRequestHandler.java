begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Set
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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|SolrResourceLoader
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
name|RequestHandlerBase
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
name|RequestHandlerUtils
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
name|RawResponseWriter
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
name|request
operator|.
name|SolrQueryResponse
import|;
end_import
begin_comment
comment|/**  * This handler uses the RawResponseWriter to give client access to  * files inside ${solr.home}/conf  *   * If you want to selectively restrict access some configuration files, you can list  * these files in the {@link #HIDDEN} invariants.  For example to hide   * synonyms.txt and anotherfile.txt, you would register:  *   *<pre>  *&lt;requestHandler name="/admin/file" class="org.apache.solr.handler.admin.ShowFileRequestHandler"&gt;  *&lt;lst name="defaults"&gt;  *&lt;str name="echoParams"&gt;explicit&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="invariants"&gt;  *&lt;str name="hidden"&gt;synonyms.txt&lt;/str&gt;   *&lt;str name="hidden"&gt;anotherfile.txt&lt;/str&gt;   *&lt;/lst&gt;  *&lt;/requestHandler&gt;  *</pre>  *   * The ShowFileRequestHandler uses the {@link RawResponseWriter} (wt=raw) to return  * file contents.  If you need to use a different writer, you will need to change   * the registered invarient param for wt.  *   * If you want to override the contentType header returned for a given file, you can  * set it directly using: {@link #USE_CONTENT_TYPE}.  For example, to get a plain text   * version of schema.xml, try:  *<pre>  *   http://localhost:8983/solr/admin/file?file=schema.xml&contentType=text/plain  *</pre>  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|ShowFileRequestHandler
specifier|public
class|class
name|ShowFileRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|HIDDEN
specifier|public
specifier|static
specifier|final
name|String
name|HIDDEN
init|=
literal|"hidden"
decl_stmt|;
DECL|field|USE_CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|USE_CONTENT_TYPE
init|=
literal|"contentType"
decl_stmt|;
DECL|field|hiddenFiles
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenFiles
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
name|ShowFileRequestHandler
name|instance
decl_stmt|;
DECL|method|ShowFileRequestHandler
specifier|public
name|ShowFileRequestHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|instance
operator|=
name|this
expr_stmt|;
comment|// used so that getFileContents can access hiddenFiles
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// by default, use wt=raw
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|invariants
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
operator|==
literal|null
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"raw"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|invariants
operator|=
name|params
expr_stmt|;
comment|// Build a list of hidden files
name|hiddenFiles
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|invariants
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|hidden
init|=
name|invariants
operator|.
name|getParams
argument_list|(
name|HIDDEN
argument_list|)
decl_stmt|;
if|if
condition|(
name|hidden
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|hidden
control|)
block|{
name|hiddenFiles
operator|.
name|add
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|adminFile
init|=
literal|null
decl_stmt|;
specifier|final
name|SolrResourceLoader
name|loader
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|File
name|configdir
init|=
operator|new
name|File
argument_list|(
name|loader
operator|.
name|getConfigDir
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fname
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fname
operator|==
literal|null
condition|)
block|{
name|adminFile
operator|=
name|configdir
expr_stmt|;
block|}
else|else
block|{
name|fname
operator|=
name|fname
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|// normalize slashes
if|if
condition|(
name|hiddenFiles
operator|.
name|contains
argument_list|(
name|fname
operator|.
name|toUpperCase
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Can not access: "
operator|+
name|fname
argument_list|)
throw|;
block|}
if|if
condition|(
name|fname
operator|.
name|indexOf
argument_list|(
literal|".."
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|FORBIDDEN
argument_list|,
literal|"Invalid path: "
operator|+
name|fname
argument_list|)
throw|;
block|}
name|adminFile
operator|=
operator|new
name|File
argument_list|(
name|configdir
argument_list|,
name|fname
argument_list|)
expr_stmt|;
block|}
comment|// Make sure the file exists, is readable and is not a hidden file
if|if
condition|(
operator|!
name|adminFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can not find: "
operator|+
name|adminFile
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|adminFile
operator|.
name|canRead
argument_list|()
operator|||
name|adminFile
operator|.
name|isHidden
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can not show: "
operator|+
name|adminFile
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|adminFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// Add a warning
name|RequestHandlerUtils
operator|.
name|addExperimentalFormatWarning
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
comment|// Show a directory listing
if|if
condition|(
name|adminFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|int
name|basePath
init|=
name|configdir
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
name|NamedList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|files
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|adminFile
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|substring
argument_list|(
name|basePath
argument_list|)
decl_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
comment|// normalize slashes
if|if
condition|(
name|hiddenFiles
operator|.
name|contains
argument_list|(
name|path
operator|.
name|toUpperCase
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
comment|// don't show 'hidden' files
block|}
if|if
condition|(
name|f
operator|.
name|isHidden
argument_list|()
operator|||
name|f
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
continue|continue;
comment|// skip hidden system files...
block|}
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|fileInfo
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|files
operator|.
name|add
argument_list|(
name|path
argument_list|,
name|fileInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|fileInfo
operator|.
name|add
argument_list|(
literal|"directory"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO? content type
name|fileInfo
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|f
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fileInfo
operator|.
name|add
argument_list|(
literal|"modified"
argument_list|,
operator|new
name|Date
argument_list|(
name|f
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"files"
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if they want the file as text
specifier|final
name|String
name|contentType
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|USE_CONTENT_TYPE
argument_list|)
decl_stmt|;
specifier|final
name|File
name|file
init|=
name|adminFile
decl_stmt|;
comment|//final URLConnection conn = adminFile.toURI().toURL().openConnection();
name|ContentStream
name|stream
init|=
operator|new
name|ContentStream
argument_list|()
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|file
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|Long
name|getSize
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
specifier|public
name|String
name|getSourceInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
return|return
name|contentType
return|;
block|}
return|return
literal|null
return|;
comment|//conn.getContentType();
block|}
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|loader
operator|.
name|openResource
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
comment|//conn.getInputStream();
block|}
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This is a utility function that lets you get the contents of an admin file    *     * It is only used so that we can get rid of "/admin/get-file.jsp" and include    * "admin-extra.html" in "/admin/index.html" using jsp scriptlets    */
annotation|@
name|Deprecated
DECL|method|getFileContents
specifier|public
specifier|static
name|String
name|getFileContents
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|instance
operator|!=
literal|null
operator|&&
name|instance
operator|.
name|hiddenFiles
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|instance
operator|.
name|hiddenFiles
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
comment|// ignore it...
block|}
block|}
try|try
block|{
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
name|InputStream
name|input
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|IOUtils
operator|.
name|toString
argument_list|(
name|input
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// ignore it
return|return
literal|""
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Admin Get File -- view config files directly"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
end_unit
