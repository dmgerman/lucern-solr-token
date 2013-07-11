begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|XML
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
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_class
DECL|class|SolrXMLSerializer
specifier|public
class|class
name|SolrXMLSerializer
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrXMLSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INDENT
specifier|private
specifier|final
specifier|static
name|String
name|INDENT
init|=
literal|"  "
decl_stmt|;
comment|/**    * @param w    *          Writer to use    * @throws IOException If there is a low-level I/O error.    */
DECL|method|persist
name|void
name|persist
parameter_list|(
name|Writer
name|w
parameter_list|,
name|SolrXMLDef
name|solrXMLDef
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|write
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"<solr"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rootSolrAttribs
init|=
name|solrXMLDef
operator|.
name|solrAttribs
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|solrAttribKeys
init|=
name|rootSolrAttribs
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|solrAttribKeys
control|)
block|{
name|String
name|value
init|=
name|rootSolrAttribs
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|writeAttribute
argument_list|(
name|w
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
name|Properties
name|containerProperties
init|=
name|solrXMLDef
operator|.
name|containerProperties
decl_stmt|;
if|if
condition|(
name|containerProperties
operator|!=
literal|null
operator|&&
operator|!
name|containerProperties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writeProperties
argument_list|(
name|w
argument_list|,
name|containerProperties
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
block|}
comment|// Output logging section if any
if|if
condition|(
name|solrXMLDef
operator|.
name|loggingAttribs
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|solrXMLDef
operator|.
name|watcherAttribs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
literal|"<logging"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ent
range|:
name|solrXMLDef
operator|.
name|loggingAttribs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeAttribute
argument_list|(
name|w
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrXMLDef
operator|.
name|watcherAttribs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
name|INDENT
operator|+
literal|"<watcher"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ent
range|:
name|solrXMLDef
operator|.
name|watcherAttribs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeAttribute
argument_list|(
name|w
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
literal|"/>\n"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
literal|"</logging>\n"
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
literal|"<cores"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coresAttribs
init|=
name|solrXMLDef
operator|.
name|coresAttribs
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|coreAttribKeys
init|=
name|coresAttribs
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|coreAttribKeys
control|)
block|{
name|String
name|value
init|=
name|coresAttribs
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|writeAttribute
argument_list|(
name|w
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrCoreXMLDef
name|coreDef
range|:
name|solrXMLDef
operator|.
name|coresDefs
control|)
block|{
name|persist
argument_list|(
name|w
argument_list|,
name|coreDef
argument_list|)
expr_stmt|;
block|}
comment|// Shard handler section
if|if
condition|(
name|solrXMLDef
operator|.
name|shardHandlerNode
operator|!=
literal|null
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|nodeToXML
argument_list|(
name|solrXMLDef
operator|.
name|shardHandlerNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
literal|"</cores>\n"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"</solr>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|nodeToXML
specifier|private
name|String
name|nodeToXML
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
try|try
block|{
name|TransformerFactory
name|tfactory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Transformer
name|tx
init|=
name|tfactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|StringWriter
name|buffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|tx
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|tx
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|node
argument_list|)
argument_list|,
operator|new
name|StreamResult
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error transforming XML: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Writes the cores configuration node for a given core. */
DECL|method|persist
specifier|private
name|void
name|persist
parameter_list|(
name|Writer
name|w
parameter_list|,
name|SolrCoreXMLDef
name|coreDef
parameter_list|)
throws|throws
name|IOException
block|{
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
name|INDENT
operator|+
literal|"<core"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|coreDef
operator|.
name|coreAttribs
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|writeAttribute
argument_list|(
name|w
argument_list|,
name|key
argument_list|,
name|coreDef
operator|.
name|coreAttribs
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Properties
name|properties
init|=
name|coreDef
operator|.
name|coreProperties
decl_stmt|;
if|if
condition|(
name|properties
operator|==
literal|null
operator|||
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
name|w
operator|.
name|write
argument_list|(
literal|"/>\n"
argument_list|)
expr_stmt|;
comment|// core
else|else
block|{
name|w
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
name|writeProperties
argument_list|(
name|w
argument_list|,
name|properties
argument_list|,
literal|"      "
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|INDENT
operator|+
name|INDENT
operator|+
literal|"</core>\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeProperties
specifier|private
name|void
name|writeProperties
parameter_list|(
name|Writer
name|w
parameter_list|,
name|Properties
name|props
parameter_list|,
name|String
name|indent
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|props
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|w
operator|.
name|write
argument_list|(
name|indent
operator|+
literal|"<property"
argument_list|)
expr_stmt|;
name|writeAttribute
argument_list|(
name|w
argument_list|,
literal|"name"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|writeAttribute
argument_list|(
name|w
argument_list|,
literal|"value"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"/>\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeAttribute
specifier|private
name|void
name|writeAttribute
parameter_list|(
name|Writer
name|w
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return;
name|w
operator|.
name|write
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
DECL|method|persistFile
name|void
name|persistFile
parameter_list|(
name|File
name|file
parameter_list|,
name|SolrXMLDef
name|solrXMLDef
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Persisting cores config to "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpFile
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// write in temp first
name|tmpFile
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"solr"
argument_list|,
literal|".xml"
argument_list|,
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|java
operator|.
name|io
operator|.
name|FileOutputStream
name|out
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|persist
argument_list|(
name|writer
argument_list|,
name|solrXMLDef
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// rename over origin or copy if this fails
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tmpFile
operator|.
name|renameTo
argument_list|(
name|file
argument_list|)
condition|)
name|tmpFile
operator|=
literal|null
expr_stmt|;
else|else
name|fileCopy
argument_list|(
name|tmpFile
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
name|xnf
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|xnf
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|IOException
name|xio
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|xio
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|tmpFile
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|tmpFile
operator|.
name|delete
argument_list|()
condition|)
name|tmpFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Copies a src file to a dest file: used to circumvent the platform    * discrepancies regarding renaming files.    */
DECL|method|fileCopy
specifier|private
specifier|static
name|void
name|fileCopy
parameter_list|(
name|File
name|src
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|IOException
name|xforward
init|=
literal|null
decl_stmt|;
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
name|FileChannel
name|fcin
init|=
literal|null
decl_stmt|;
name|FileChannel
name|fcout
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|fcin
operator|=
name|fis
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|fcout
operator|=
name|fos
operator|.
name|getChannel
argument_list|()
expr_stmt|;
comment|// do the file copy 32Mb at a time
specifier|final
name|int
name|MB32
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|long
name|size
init|=
name|fcin
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|position
operator|<
name|size
condition|)
block|{
name|position
operator|+=
name|fcin
operator|.
name|transferTo
argument_list|(
name|position
argument_list|,
name|MB32
argument_list|,
name|fcout
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{
name|xforward
operator|=
name|xio
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fis
operator|!=
literal|null
condition|)
try|try
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
try|try
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|fos
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
if|if
condition|(
name|fcin
operator|!=
literal|null
operator|&&
name|fcin
operator|.
name|isOpen
argument_list|()
condition|)
try|try
block|{
name|fcin
operator|.
name|close
argument_list|()
expr_stmt|;
name|fcin
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
if|if
condition|(
name|fcout
operator|!=
literal|null
operator|&&
name|fcout
operator|.
name|isOpen
argument_list|()
condition|)
try|try
block|{
name|fcout
operator|.
name|close
argument_list|()
expr_stmt|;
name|fcout
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|xio
parameter_list|)
block|{}
block|}
if|if
condition|(
name|xforward
operator|!=
literal|null
condition|)
block|{
throw|throw
name|xforward
throw|;
block|}
block|}
DECL|class|SolrXMLDef
specifier|static
specifier|public
class|class
name|SolrXMLDef
block|{
DECL|field|containerProperties
name|Properties
name|containerProperties
decl_stmt|;
DECL|field|solrAttribs
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|solrAttribs
decl_stmt|;
DECL|field|coresAttribs
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coresAttribs
decl_stmt|;
DECL|field|loggingAttribs
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|loggingAttribs
decl_stmt|;
DECL|field|watcherAttribs
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|watcherAttribs
decl_stmt|;
DECL|field|shardHandlerNode
name|Node
name|shardHandlerNode
decl_stmt|;
DECL|field|coresDefs
name|List
argument_list|<
name|SolrCoreXMLDef
argument_list|>
name|coresDefs
decl_stmt|;
block|}
DECL|class|SolrCoreXMLDef
specifier|static
specifier|public
class|class
name|SolrCoreXMLDef
block|{
DECL|field|coreProperties
name|Properties
name|coreProperties
decl_stmt|;
DECL|field|coreAttribs
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coreAttribs
decl_stmt|;
block|}
block|}
end_class
end_unit
