begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrResponse
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SolrResponseBase
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|Template
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|VelocityContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|app
operator|.
name|VelocityEngine
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|runtime
operator|.
name|RuntimeConstants
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Properties
import|;
end_import
begin_class
DECL|class|VelocityResponseWriter
specifier|public
class|class
name|VelocityResponseWriter
implements|implements
name|QueryResponseWriter
block|{
comment|// TODO: maybe pass this Logger to the template for logging from there?
comment|//  private static final Logger log = LoggerFactory.getLogger(VelocityResponseWriter.class);
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
name|VelocityEngine
name|engine
init|=
name|getEngine
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// TODO: have HTTP headers available for configuring engine
name|Template
name|template
init|=
name|getTemplate
argument_list|(
name|engine
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|VelocityContext
name|context
init|=
operator|new
name|VelocityContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"request"
argument_list|,
name|request
argument_list|)
expr_stmt|;
comment|// Turn the SolrQueryResponse into a SolrResponse.
comment|// QueryResponse has lots of conveniences suitable for a view
comment|// Problem is, which SolrResponse class to use?
comment|// One patch to SOLR-620 solved this by passing in a class name as
comment|// as a parameter and using reflection and Solr's class loader to
comment|// create a new instance.  But for now the implementation simply
comment|// uses QueryResponse, and if it chokes in a known way, fall back
comment|// to bare bones SolrResponseBase.
comment|// TODO: Can this writer know what the handler class is?  With echoHandler=true it can get its string name at least
name|SolrResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|parsedResponse
init|=
name|BinaryResponseWriter
operator|.
name|getParsedResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
try|try
block|{
name|rsp
operator|.
name|setResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
comment|// page only injected if QueryResponse works
name|context
operator|.
name|put
argument_list|(
literal|"page"
argument_list|,
operator|new
name|PageTool
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
comment|// page tool only makes sense for a SearchHandler request... *sigh*
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
comment|// known edge case where QueryResponse's extraction assumes "response" is a SolrDocumentList
comment|// (AnalysisRequestHandler emits a "response")
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrResponseBase
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|setResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Velocity context tools - TODO: make these pluggable
name|context
operator|.
name|put
argument_list|(
literal|"esc"
argument_list|,
operator|new
name|EscapeTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
operator|new
name|ComparisonDateTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"list"
argument_list|,
operator|new
name|ListTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"math"
argument_list|,
operator|new
name|MathTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"number"
argument_list|,
operator|new
name|NumberTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
operator|new
name|SortTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"engine"
argument_list|,
name|engine
argument_list|)
expr_stmt|;
comment|// for $engine.resourceExists(...)
comment|// Mimetype to extension map for detecting file type and show icon
comment|// List of types match the icons in /solr/img/filetypes
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mimeToExt
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"application/x-7z-compressed"
argument_list|,
literal|"7z"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/postscript"
argument_list|,
literal|"ai"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/pgp-signature"
argument_list|,
literal|"asc"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/octet-stream"
argument_list|,
literal|"bin"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-bzip2"
argument_list|,
literal|"bz2"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/x-c"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.ms-htmlhelp"
argument_list|,
literal|"chm"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/java-vm"
argument_list|,
literal|"class"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/css"
argument_list|,
literal|"css"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/csv"
argument_list|,
literal|"csv"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-debian-package"
argument_list|,
literal|"deb"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/msword"
argument_list|,
literal|"doc"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"message/rfc822"
argument_list|,
literal|"eml"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"image/gif"
argument_list|,
literal|"gif"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/winhlp"
argument_list|,
literal|"hlp"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/html"
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/java-archive"
argument_list|,
literal|"jar"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/x-java-source"
argument_list|,
literal|"java"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"image/jpeg"
argument_list|,
literal|"jpeg"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/javascript"
argument_list|,
literal|"js"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.chart"
argument_list|,
literal|"odc"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.formula"
argument_list|,
literal|"odf"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.graphics"
argument_list|,
literal|"odg"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.image"
argument_list|,
literal|"odi"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.presentation"
argument_list|,
literal|"odp"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.spreadsheet"
argument_list|,
literal|"ods"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
literal|"odt"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/pdf"
argument_list|,
literal|"pdf"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/pgp-encrypted"
argument_list|,
literal|"pgp"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"image/png"
argument_list|,
literal|"png"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
literal|"ppt"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"audio/x-pn-realaudio"
argument_list|,
literal|"ram"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-rar-compressed"
argument_list|,
literal|"rar"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.rn-realmedia"
argument_list|,
literal|"rm"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/rtf"
argument_list|,
literal|"rtf"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-shockwave-flash"
argument_list|,
literal|"swf"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.sun.xml.calc"
argument_list|,
literal|"sxc"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.sun.xml.draw"
argument_list|,
literal|"sxd"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.sun.xml.impress"
argument_list|,
literal|"sxi"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.sun.xml.writer"
argument_list|,
literal|"sxw"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-tar"
argument_list|,
literal|"tar"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-tex"
argument_list|,
literal|"tex"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/plain"
argument_list|,
literal|"txt"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"text/x-vcard"
argument_list|,
literal|"vcf"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.visio"
argument_list|,
literal|"vsd"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"audio/x-wav"
argument_list|,
literal|"wav"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"audio/x-ms-wma"
argument_list|,
literal|"wma"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"video/x-ms-wmv"
argument_list|,
literal|"wmv"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
literal|"xls"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/xml"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/x-xpinstall"
argument_list|,
literal|"xpi"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"application/zip"
argument_list|,
literal|"zip"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"mimeToExt"
argument_list|,
name|mimeToExt
argument_list|)
expr_stmt|;
name|String
name|layout_template
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.layout"
argument_list|)
decl_stmt|;
name|String
name|json_wrapper
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.json"
argument_list|)
decl_stmt|;
name|boolean
name|wrap_response
init|=
operator|(
name|layout_template
operator|!=
literal|null
operator|)
operator|||
operator|(
name|json_wrapper
operator|!=
literal|null
operator|)
decl_stmt|;
comment|// create output, optionally wrap it into a json object
if|if
condition|(
name|wrap_response
condition|)
block|{
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|stringWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|layout_template
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"content"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stringWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
try|try
block|{
name|engine
operator|.
name|getTemplate
argument_list|(
name|layout_template
operator|+
literal|".vm"
argument_list|)
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|stringWriter
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
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|json_wrapper
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.json"
argument_list|)
operator|+
literal|"("
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|getJSONWrap
argument_list|(
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// using a layout, but not JSON wrapping
name|writer
operator|.
name|write
argument_list|(
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getEngine
specifier|private
name|VelocityEngine
name|getEngine
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|VelocityEngine
name|engine
init|=
operator|new
name|VelocityEngine
argument_list|()
decl_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"params.resource.loader.instance"
argument_list|,
operator|new
name|SolrParamResourceLoader
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|SolrVelocityResourceLoader
name|resourceLoader
init|=
operator|new
name|SolrVelocityResourceLoader
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"solr.resource.loader.instance"
argument_list|,
name|resourceLoader
argument_list|)
expr_stmt|;
name|File
name|fileResourceLoaderBaseDir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|template_root
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.base_dir"
argument_list|)
decl_stmt|;
name|fileResourceLoaderBaseDir
operator|=
operator|new
name|File
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|,
literal|"velocity"
argument_list|)
expr_stmt|;
if|if
condition|(
name|template_root
operator|!=
literal|null
condition|)
block|{
name|fileResourceLoaderBaseDir
operator|=
operator|new
name|File
argument_list|(
name|template_root
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// no worries... probably in ZooKeeper mode and getConfigDir() isn't available, so we'll just ignore omit
comment|// the file system resource loader
block|}
if|if
condition|(
name|fileResourceLoaderBaseDir
operator|!=
literal|null
condition|)
block|{
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|FILE_RESOURCE_LOADER_PATH
argument_list|,
name|fileResourceLoaderBaseDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|RESOURCE_LOADER
argument_list|,
literal|"params,file,solr"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|engine
operator|.
name|setProperty
argument_list|(
name|RuntimeConstants
operator|.
name|RESOURCE_LOADER
argument_list|,
literal|"params,solr"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: Externalize Velocity properties
name|String
name|propFile
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.properties"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|propFile
operator|==
literal|null
condition|)
name|engine
operator|.
name|init
argument_list|()
expr_stmt|;
else|else
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|resourceLoader
operator|.
name|getResourceStream
argument_list|(
name|propFile
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|engine
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
name|e
argument_list|)
throw|;
block|}
return|return
name|engine
return|;
block|}
DECL|method|getTemplate
specifier|private
name|Template
name|getTemplate
parameter_list|(
name|VelocityEngine
name|engine
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|Template
name|template
decl_stmt|;
name|String
name|template_name
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.template"
argument_list|)
decl_stmt|;
name|String
name|qt
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"qt"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|request
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|template_name
operator|==
literal|null
operator|&&
name|path
operator|!=
literal|null
condition|)
block|{
name|template_name
operator|=
name|path
expr_stmt|;
block|}
comment|// TODO: path is never null, so qt won't get picked up  maybe special case for '/select' to use qt, otherwise use path?
if|if
condition|(
name|template_name
operator|==
literal|null
operator|&&
name|qt
operator|!=
literal|null
condition|)
block|{
name|template_name
operator|=
name|qt
expr_stmt|;
block|}
if|if
condition|(
name|template_name
operator|==
literal|null
condition|)
name|template_name
operator|=
literal|"index"
expr_stmt|;
try|try
block|{
name|template
operator|=
name|engine
operator|.
name|getTemplate
argument_list|(
name|template_name
operator|+
literal|".vm"
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
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|template
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
return|return
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.contentType"
argument_list|,
literal|"text/html;charset=UTF-8"
argument_list|)
return|;
block|}
DECL|method|getJSONWrap
specifier|private
name|String
name|getJSONWrap
parameter_list|(
name|String
name|xmlResult
parameter_list|)
block|{
comment|// TODO: maybe noggit or Solr's JSON utilities can make this cleaner?
comment|// escape the double quotes and backslashes
name|String
name|replace1
init|=
name|xmlResult
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"\\\\\\\\"
argument_list|)
decl_stmt|;
name|replace1
operator|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|"\\\\n"
argument_list|)
expr_stmt|;
name|replace1
operator|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\\r"
argument_list|,
literal|"\\\\r"
argument_list|)
expr_stmt|;
name|String
name|replaced
init|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\\\\\""
argument_list|)
decl_stmt|;
comment|// wrap it in a JSON object
return|return
literal|"{\"result\":\""
operator|+
name|replaced
operator|+
literal|"\"}"
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
block|}
end_class
end_unit
