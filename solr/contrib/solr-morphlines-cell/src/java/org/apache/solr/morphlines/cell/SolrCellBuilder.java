begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.cell
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|cell
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
name|InputStream
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
name|Collection
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
name|HashMap
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
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|SolrInputField
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
name|MultiMapSolrParams
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
name|common
operator|.
name|util
operator|.
name|DateUtil
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
name|extraction
operator|.
name|ExtractingParams
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
name|extraction
operator|.
name|SolrContentHandler
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
name|extraction
operator|.
name|SolrContentHandlerFactory
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
name|morphlines
operator|.
name|solr
operator|.
name|SolrLocator
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
name|schema
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
name|tika
operator|.
name|exception
operator|.
name|TikaException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|io
operator|.
name|TikaInputStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|Parser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|TeeContentHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|MatchingContentHandler
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|xpath
operator|.
name|XPathParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
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
name|ContentHandler
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
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Command
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|CommandBuilder
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineCompilationException
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineRuntimeException
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Fields
import|;
end_import
begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|stdio
operator|.
name|AbstractParser
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ListMultimap
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closeables
import|;
end_import
begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import
begin_comment
comment|/**  * Command that pipes the first attachment of a record into one of the given Tika parsers, then maps  * the Tika output back to a record using SolrCell.  *<p>  * The Tika parser is chosen from the configurable list of parsers, depending on the MIME type  * specified in the input record. Typically, this requires an upstream DetectMimeTypeBuilder  * in a prior command.  */
end_comment
begin_class
DECL|class|SolrCellBuilder
specifier|public
specifier|final
class|class
name|SolrCellBuilder
implements|implements
name|CommandBuilder
block|{
annotation|@
name|Override
DECL|method|getNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"solrCell"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Command
name|build
parameter_list|(
name|Config
name|config
parameter_list|,
name|Command
name|parent
parameter_list|,
name|Command
name|child
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|SolrCell
argument_list|(
name|config
argument_list|,
name|parent
argument_list|,
name|child
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|SolrCell
specifier|private
specifier|static
specifier|final
class|class
name|SolrCell
extends|extends
name|AbstractParser
block|{
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|dateFormats
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|dateFormats
decl_stmt|;
DECL|field|xpathExpr
specifier|private
specifier|final
name|String
name|xpathExpr
decl_stmt|;
DECL|field|parsers
specifier|private
specifier|final
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|solrContentHandlerFactory
specifier|private
specifier|final
name|SolrContentHandlerFactory
name|solrContentHandlerFactory
decl_stmt|;
DECL|field|solrParams
specifier|private
specifier|final
name|SolrParams
name|solrParams
decl_stmt|;
DECL|field|mediaTypeToParserMap
specifier|private
specifier|final
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|mediaTypeToParserMap
decl_stmt|;
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|XPathParser
name|PARSER
init|=
operator|new
name|XPathParser
argument_list|(
literal|"xhtml"
argument_list|,
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|)
decl_stmt|;
DECL|field|ADDITIONAL_SUPPORTED_MIME_TYPES
specifier|public
specifier|static
specifier|final
name|String
name|ADDITIONAL_SUPPORTED_MIME_TYPES
init|=
literal|"additionalSupportedMimeTypes"
decl_stmt|;
DECL|method|SolrCell
specifier|public
name|SolrCell
parameter_list|(
name|Config
name|config
parameter_list|,
name|Command
name|parent
parameter_list|,
name|Command
name|child
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|,
name|parent
argument_list|,
name|child
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|Config
name|solrLocatorConfig
init|=
name|getConfigs
argument_list|()
operator|.
name|getConfig
argument_list|(
name|config
argument_list|,
literal|"solrLocator"
argument_list|)
decl_stmt|;
name|SolrLocator
name|locator
init|=
operator|new
name|SolrLocator
argument_list|(
name|solrLocatorConfig
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"solrLocator: {}"
argument_list|,
name|locator
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|locator
operator|.
name|getIndexSchema
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|schema
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Solr schema: \n{}"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|join
argument_list|(
operator|new
name|TreeMap
argument_list|(
name|schema
operator|.
name|getFields
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cellParams
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|uprefix
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
name|ExtractingParams
operator|.
name|UNKNOWN_FIELD_PREFIX
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|uprefix
operator|!=
literal|null
condition|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|UNKNOWN_FIELD_PREFIX
argument_list|,
name|uprefix
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|capture
range|:
name|getConfigs
argument_list|()
operator|.
name|getStringList
argument_list|(
name|config
argument_list|,
name|ExtractingParams
operator|.
name|CAPTURE_ELEMENTS
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
control|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|CAPTURE_ELEMENTS
argument_list|,
name|capture
argument_list|)
expr_stmt|;
block|}
name|Config
name|fmapConfig
init|=
name|getConfigs
argument_list|()
operator|.
name|getConfig
argument_list|(
name|config
argument_list|,
literal|"fmap"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fmapConfig
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|fmapConfig
operator|.
name|root
argument_list|()
operator|.
name|unwrapped
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|MAP_PREFIX
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|captureAttributes
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
name|ExtractingParams
operator|.
name|CAPTURE_ATTRIBUTES
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|captureAttributes
operator|!=
literal|null
condition|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|CAPTURE_ATTRIBUTES
argument_list|,
name|captureAttributes
argument_list|)
expr_stmt|;
block|}
name|String
name|lowerNames
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
name|ExtractingParams
operator|.
name|LOWERNAMES
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|lowerNames
operator|!=
literal|null
condition|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|LOWERNAMES
argument_list|,
name|lowerNames
argument_list|)
expr_stmt|;
block|}
name|String
name|defaultField
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
name|ExtractingParams
operator|.
name|DEFAULT_FIELD
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultField
operator|!=
literal|null
condition|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|DEFAULT_FIELD
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
block|}
name|xpathExpr
operator|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
name|ExtractingParams
operator|.
name|XPATH_EXPRESSION
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|xpathExpr
operator|!=
literal|null
condition|)
block|{
name|cellParams
operator|.
name|put
argument_list|(
name|ExtractingParams
operator|.
name|XPATH_EXPRESSION
argument_list|,
name|xpathExpr
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dateFormats
operator|=
name|getConfigs
argument_list|()
operator|.
name|getStringList
argument_list|(
name|config
argument_list|,
literal|"dateFormats"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|DateUtil
operator|.
name|DEFAULT_DATE_FORMATS
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|handlerStr
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"solrContentHandlerFactory"
argument_list|,
name|TrimSolrContentHandlerFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|SolrContentHandlerFactory
argument_list|>
name|factoryClass
decl_stmt|;
try|try
block|{
name|factoryClass
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|SolrContentHandlerFactory
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|handlerStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Could not find class "
operator|+
name|handlerStr
operator|+
literal|" to use for "
operator|+
literal|"solrContentHandlerFactory"
argument_list|,
name|config
argument_list|,
name|cnfe
argument_list|)
throw|;
block|}
name|this
operator|.
name|solrContentHandlerFactory
operator|=
name|getSolrContentHandlerFactory
argument_list|(
name|factoryClass
argument_list|,
name|dateFormats
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|mediaTypeToParserMap
operator|=
operator|new
name|HashMap
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
argument_list|()
expr_stmt|;
comment|//MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes(); // FIXME getMediaTypeRegistry.normalize()
name|List
argument_list|<
name|?
extends|extends
name|Config
argument_list|>
name|parserConfigs
init|=
name|getConfigs
argument_list|()
operator|.
name|getConfigList
argument_list|(
name|config
argument_list|,
literal|"parsers"
argument_list|)
decl_stmt|;
for|for
control|(
name|Config
name|parserConfig
range|:
name|parserConfigs
control|)
block|{
name|String
name|parserClassName
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|parserConfig
argument_list|,
literal|"parser"
argument_list|)
decl_stmt|;
name|Object
name|obj
decl_stmt|;
try|try
block|{
name|obj
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|parserClassName
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Cannot instantiate Tika parser: "
operator|+
name|parserClassName
argument_list|,
name|config
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Parser
operator|)
condition|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Tika parser "
operator|+
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" must be an instance of class "
operator|+
name|Parser
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|config
argument_list|)
throw|;
block|}
name|Parser
name|parser
init|=
operator|(
name|Parser
operator|)
name|obj
decl_stmt|;
name|this
operator|.
name|parsers
operator|.
name|add
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|mediaTypes
init|=
name|getConfigs
argument_list|()
operator|.
name|getStringList
argument_list|(
name|parserConfig
argument_list|,
name|SUPPORTED_MIME_TYPES
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|mediaTypeStr
range|:
name|mediaTypes
control|)
block|{
name|MediaType
name|mediaType
init|=
name|parseMediaType
argument_list|(
name|mediaTypeStr
argument_list|)
decl_stmt|;
name|addSupportedMimeType
argument_list|(
name|mediaTypeStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|mediaTypeToParserMap
operator|.
name|put
argument_list|(
name|mediaType
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|parserConfig
operator|.
name|hasPath
argument_list|(
name|SUPPORTED_MIME_TYPES
argument_list|)
condition|)
block|{
for|for
control|(
name|MediaType
name|mediaType
range|:
name|parser
operator|.
name|getSupportedTypes
argument_list|(
operator|new
name|ParseContext
argument_list|()
argument_list|)
control|)
block|{
name|mediaType
operator|=
name|mediaType
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
name|addSupportedMimeType
argument_list|(
name|mediaType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|mediaTypeToParserMap
operator|.
name|put
argument_list|(
name|mediaType
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|extras
init|=
name|getConfigs
argument_list|()
operator|.
name|getStringList
argument_list|(
name|parserConfig
argument_list|,
name|ADDITIONAL_SUPPORTED_MIME_TYPES
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|mediaTypeStr
range|:
name|extras
control|)
block|{
name|MediaType
name|mediaType
init|=
name|parseMediaType
argument_list|(
name|mediaTypeStr
argument_list|)
decl_stmt|;
name|addSupportedMimeType
argument_list|(
name|mediaTypeStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|mediaTypeToParserMap
operator|.
name|put
argument_list|(
name|mediaType
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//LOG.info("mediaTypeToParserMap="+mediaTypeToParserMap);
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|tmp
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|cellParams
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|tmp
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|solrParams
operator|=
operator|new
name|MultiMapSolrParams
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|validateArguments
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doProcess
specifier|protected
name|boolean
name|doProcess
parameter_list|(
name|Record
name|record
parameter_list|,
name|InputStream
name|inputStream
parameter_list|)
block|{
name|Parser
name|parser
init|=
name|detectParser
argument_list|(
name|record
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
comment|// necessary for gzipped files or tar files, etc! copied from TikaCLI
name|parseContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|entries
argument_list|()
control|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SolrContentHandler
name|handler
init|=
name|solrContentHandlerFactory
operator|.
name|createSolrContentHandler
argument_list|(
name|metadata
argument_list|,
name|solrParams
argument_list|,
name|schema
argument_list|)
decl_stmt|;
try|try
block|{
name|inputStream
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
name|ContentHandler
name|parsingHandler
init|=
name|handler
decl_stmt|;
name|StringWriter
name|debugWriter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|debugWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|ContentHandler
name|serializer
init|=
operator|new
name|XMLSerializer
argument_list|(
name|debugWriter
argument_list|,
operator|new
name|OutputFormat
argument_list|(
literal|"XML"
argument_list|,
literal|"UTF-8"
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|parsingHandler
operator|=
operator|new
name|TeeContentHandler
argument_list|(
name|parsingHandler
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
block|}
comment|// String xpathExpr = "/xhtml:html/xhtml:body/xhtml:div/descendant:node()";
if|if
condition|(
name|xpathExpr
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|xpathExpr
argument_list|)
decl_stmt|;
name|parsingHandler
operator|=
operator|new
name|MatchingContentHandler
argument_list|(
name|parsingHandler
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|inputStream
argument_list|,
name|parsingHandler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
literal|"Cannot parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
literal|"Cannot parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
literal|"Cannot parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"debug XML doc: {}"
argument_list|,
name|debugWriter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|inputStream
operator|!=
literal|null
condition|)
block|{
name|Closeables
operator|.
name|closeQuietly
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
block|}
name|SolrInputDocument
name|doc
init|=
name|handler
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"solr doc: {}"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|Record
name|outputRecord
init|=
name|toRecord
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|getChild
argument_list|()
operator|.
name|process
argument_list|(
name|outputRecord
argument_list|)
return|;
block|}
DECL|method|detectParser
specifier|private
name|Parser
name|detectParser
parameter_list|(
name|Record
name|record
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hasAtLeastOneMimeType
argument_list|(
name|record
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|mediaTypeStr
init|=
operator|(
name|String
operator|)
name|record
operator|.
name|getFirstValue
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_MIME_TYPE
argument_list|)
decl_stmt|;
comment|//ExtractingParams.STREAM_TYPE);
assert|assert
name|mediaTypeStr
operator|!=
literal|null
assert|;
name|MediaType
name|mediaType
init|=
name|parseMediaType
argument_list|(
name|mediaTypeStr
argument_list|)
operator|.
name|getBaseType
argument_list|()
decl_stmt|;
name|Parser
name|parser
init|=
name|mediaTypeToParserMap
operator|.
name|get
argument_list|(
name|mediaType
argument_list|)
decl_stmt|;
comment|// fast path
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
return|return
name|parser
return|;
block|}
comment|// wildcard matching
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|entry
range|:
name|mediaTypeToParserMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|isMediaTypeMatch
argument_list|(
name|mediaType
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No supported MIME type parser found for "
operator|+
name|Fields
operator|.
name|ATTACHMENT_MIME_TYPE
operator|+
literal|"="
operator|+
name|mediaTypeStr
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|hasAtLeastOneMimeType
specifier|private
name|boolean
name|hasAtLeastOneMimeType
parameter_list|(
name|Record
name|record
parameter_list|)
block|{
if|if
condition|(
operator|!
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Fields
operator|.
name|ATTACHMENT_MIME_TYPE
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Command failed because of missing MIME type for record: {}"
argument_list|,
name|record
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|parseMediaType
specifier|private
name|MediaType
name|parseMediaType
parameter_list|(
name|String
name|mediaTypeStr
parameter_list|)
block|{
name|MediaType
name|mediaType
init|=
name|MediaType
operator|.
name|parse
argument_list|(
name|mediaTypeStr
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|mediaType
operator|.
name|getBaseType
argument_list|()
return|;
block|}
empty_stmt|;
comment|/** Returns true if mediaType falls withing the given range (pattern), false otherwise */
DECL|method|isMediaTypeMatch
specifier|private
name|boolean
name|isMediaTypeMatch
parameter_list|(
name|MediaType
name|mediaType
parameter_list|,
name|MediaType
name|rangePattern
parameter_list|)
block|{
name|String
name|WILDCARD
init|=
literal|"*"
decl_stmt|;
name|String
name|rangePatternType
init|=
name|rangePattern
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|rangePatternSubtype
init|=
name|rangePattern
operator|.
name|getSubtype
argument_list|()
decl_stmt|;
return|return
operator|(
name|rangePatternType
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|||
name|rangePatternType
operator|.
name|equals
argument_list|(
name|mediaType
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|&&
operator|(
name|rangePatternSubtype
operator|.
name|equals
argument_list|(
name|WILDCARD
argument_list|)
operator|||
name|rangePatternSubtype
operator|.
name|equals
argument_list|(
name|mediaType
operator|.
name|getSubtype
argument_list|()
argument_list|)
operator|)
return|;
block|}
DECL|method|getSolrContentHandlerFactory
specifier|private
specifier|static
name|SolrContentHandlerFactory
name|getSolrContentHandlerFactory
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|SolrContentHandlerFactory
argument_list|>
name|factoryClass
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
try|try
block|{
return|return
name|factoryClass
operator|.
name|getConstructor
argument_list|(
name|Collection
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|dateFormats
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
throw|throw
operator|new
name|MorphlineCompilationException
argument_list|(
literal|"Unable to find valid constructor of type "
operator|+
name|factoryClass
operator|.
name|getName
argument_list|()
operator|+
literal|" for creating SolrContentHandler"
argument_list|,
name|config
argument_list|,
name|nsme
argument_list|)
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
name|MorphlineCompilationException
argument_list|(
literal|"Unexpected exception when trying to create SolrContentHandlerFactory of type "
operator|+
name|factoryClass
operator|.
name|getName
argument_list|()
argument_list|,
name|config
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|toRecord
specifier|private
name|Record
name|toRecord
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|Record
name|record
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
name|entry
range|:
name|doc
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|record
operator|.
name|getFields
argument_list|()
operator|.
name|putAll
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|record
return|;
block|}
block|}
block|}
end_class
end_unit
