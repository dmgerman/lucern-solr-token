begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|lucene
operator|.
name|document
operator|.
name|FieldType
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
name|index
operator|.
name|IndexOptions
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
name|index
operator|.
name|StorableField
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|EnumFieldSource
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
name|*
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
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
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
name|BytesRef
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
name|BytesRefBuilder
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
name|CharsRef
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
name|CharsRefBuilder
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
name|NumericUtils
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
name|EnumFieldValue
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
name|response
operator|.
name|TextResponseWriter
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
name|QParser
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
name|Document
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|DocumentBuilderFactory
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
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
begin_comment
comment|/***  * Field type for support of string values with custom sort order.  */
end_comment
begin_class
DECL|class|EnumField
specifier|public
class|class
name|EnumField
extends|extends
name|PrimitiveFieldType
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EnumField
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOCALE
specifier|protected
specifier|static
specifier|final
name|Locale
name|LOCALE
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
DECL|field|PARAM_ENUMS_CONFIG
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_ENUMS_CONFIG
init|=
literal|"enumsConfig"
decl_stmt|;
DECL|field|PARAM_ENUM_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_ENUM_NAME
init|=
literal|"enumName"
decl_stmt|;
DECL|field|DEFAULT_VALUE
specifier|protected
specifier|static
specifier|final
name|Integer
name|DEFAULT_VALUE
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|DEFAULT_PRECISION_STEP
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_PRECISION_STEP
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|enumStringToIntMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|enumStringToIntMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|enumIntToStringMap
specifier|protected
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|enumIntToStringMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|enumsConfigFile
specifier|protected
name|String
name|enumsConfigFile
decl_stmt|;
DECL|field|enumName
specifier|protected
name|String
name|enumName
decl_stmt|;
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|enumsConfigFile
operator|=
name|args
operator|.
name|get
argument_list|(
name|PARAM_ENUMS_CONFIG
argument_list|)
expr_stmt|;
if|if
condition|(
name|enumsConfigFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No enums config file was configured."
argument_list|)
throw|;
block|}
name|enumName
operator|=
name|args
operator|.
name|get
argument_list|(
name|PARAM_ENUM_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|enumName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No enum name was configured."
argument_list|)
throw|;
block|}
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|enumsConfigFile
argument_list|)
expr_stmt|;
specifier|final
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Document
name|doc
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|is
argument_list|)
decl_stmt|;
specifier|final
name|XPathFactory
name|xpathFactory
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|XPath
name|xpath
init|=
name|xpathFactory
operator|.
name|newXPath
argument_list|()
decl_stmt|;
specifier|final
name|String
name|xpathStr
init|=
name|String
operator|.
name|format
argument_list|(
name|LOCALE
argument_list|,
literal|"/enumsConfig/enum[@name='%s']"
argument_list|,
name|enumName
argument_list|)
decl_stmt|;
specifier|final
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|xpathStr
argument_list|,
name|doc
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
specifier|final
name|int
name|nodesLength
init|=
name|nodes
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodesLength
operator|==
literal|0
condition|)
block|{
name|String
name|exceptionMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|LOCALE
argument_list|,
literal|"No enum configuration found for enum '%s' in %s."
argument_list|,
name|enumName
argument_list|,
name|enumsConfigFile
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|exceptionMessage
argument_list|)
throw|;
block|}
if|if
condition|(
name|nodesLength
operator|>
literal|1
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isWarnEnabled
argument_list|()
condition|)
name|log
operator|.
name|warn
argument_list|(
literal|"More than one enum configuration found for enum '{}' in {}. The last one was taken."
argument_list|,
name|enumName
argument_list|,
name|enumsConfigFile
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Node
name|enumNode
init|=
name|nodes
operator|.
name|item
argument_list|(
name|nodesLength
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|NodeList
name|valueNodes
init|=
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"value"
argument_list|,
name|enumNode
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valueNodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|valueNode
init|=
name|valueNodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|valueStr
init|=
name|valueNode
operator|.
name|getTextContent
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|valueStr
operator|==
literal|null
operator|)
operator|||
operator|(
name|valueStr
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|LOCALE
argument_list|,
literal|"A value was defined with an no value in enum '%s' in %s."
argument_list|,
name|enumName
argument_list|,
name|enumsConfigFile
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|exceptionMessage
argument_list|)
throw|;
block|}
if|if
condition|(
name|enumStringToIntMap
operator|.
name|containsKey
argument_list|(
name|valueStr
argument_list|)
condition|)
block|{
specifier|final
name|String
name|exceptionMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|LOCALE
argument_list|,
literal|"A duplicated definition was found for value '%s' in enum '%s' in %s."
argument_list|,
name|valueStr
argument_list|,
name|enumName
argument_list|,
name|enumsConfigFile
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|exceptionMessage
argument_list|)
throw|;
block|}
name|enumIntToStringMap
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|valueStr
argument_list|)
expr_stmt|;
name|enumStringToIntMap
operator|.
name|put
argument_list|(
name|valueStr
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
decl||
name|XPathExpressionException
decl||
name|SAXException
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
name|BAD_REQUEST
argument_list|,
literal|"Error parsing enums config."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
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
name|BAD_REQUEST
argument_list|,
literal|"Error while opening enums config."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|enumStringToIntMap
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
operator|||
operator|(
name|enumIntToStringMap
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|String
name|exceptionMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|LOCALE
argument_list|,
literal|"Invalid configuration was defined for enum '%s' in %s."
argument_list|,
name|enumName
argument_list|,
name|enumsConfigFile
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|exceptionMessage
argument_list|)
throw|;
block|}
name|args
operator|.
name|remove
argument_list|(
name|PARAM_ENUMS_CONFIG
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
name|PARAM_ENUM_NAME
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|EnumFieldValue
name|toObject
parameter_list|(
name|StorableField
name|f
parameter_list|)
block|{
name|Integer
name|intValue
init|=
literal|null
decl_stmt|;
name|String
name|stringValue
init|=
literal|null
decl_stmt|;
specifier|final
name|Number
name|val
init|=
name|f
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|intValue
operator|=
name|val
operator|.
name|intValue
argument_list|()
expr_stmt|;
name|stringValue
operator|=
name|intValueToStringValue
argument_list|(
name|intValue
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EnumFieldValue
argument_list|(
name|intValue
argument_list|,
name|stringValue
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
name|field
operator|.
name|checkSortability
argument_list|()
expr_stmt|;
specifier|final
name|Object
name|missingValue
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
name|SortField
name|sf
init|=
operator|new
name|SortField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|top
argument_list|)
decl_stmt|;
name|sf
operator|.
name|setMissingValue
argument_list|(
name|missingValue
argument_list|)
expr_stmt|;
return|return
name|sf
return|;
block|}
annotation|@
name|Override
DECL|method|getUninversionType
specifier|public
name|Type
name|getUninversionType
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
return|return
name|Type
operator|.
name|SORTED_SET_INTEGER
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|INTEGER
return|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
block|{
name|field
operator|.
name|checkFieldCacheSource
argument_list|(
name|qparser
argument_list|)
expr_stmt|;
return|return
operator|new
name|EnumFieldSource
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|enumIntToStringMap
argument_list|,
name|enumStringToIntMap
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|StorableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Number
name|val
init|=
name|f
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|writer
operator|.
name|writeNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|readableValue
init|=
name|intValueToStringValue
argument_list|(
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|readableValue
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|Integer
name|minValue
init|=
name|stringValueToIntValue
argument_list|(
name|min
argument_list|)
decl_stmt|;
name|Integer
name|maxValue
init|=
name|stringValueToIntValue
argument_list|(
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|multiValued
argument_list|()
operator|&&
name|field
operator|.
name|hasDocValues
argument_list|()
operator|&&
operator|!
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
comment|// for the multi-valued dv-case, the default rangeimpl over toInternal is correct
return|return
name|super
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|minValue
operator|.
name|toString
argument_list|()
argument_list|,
name|maxValue
operator|.
name|toString
argument_list|()
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
name|Query
name|query
init|=
literal|null
decl_stmt|;
specifier|final
name|boolean
name|matchOnly
init|=
name|field
operator|.
name|hasDocValues
argument_list|()
operator|&&
operator|!
name|field
operator|.
name|indexed
argument_list|()
decl_stmt|;
if|if
condition|(
name|matchOnly
condition|)
block|{
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|DocValuesRangeFilter
operator|.
name|newIntRange
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|minValue
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|maxValue
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|DEFAULT_PRECISION_STEP
argument_list|,
name|min
operator|==
literal|null
condition|?
literal|null
else|:
name|minValue
argument_list|,
name|max
operator|==
literal|null
condition|?
literal|null
else|:
name|maxValue
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|checkSchemaField
specifier|public
name|void
name|checkSchemaField
parameter_list|(
specifier|final
name|SchemaField
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|hasDocValues
argument_list|()
operator|&&
operator|!
name|field
operator|.
name|multiValued
argument_list|()
operator|&&
operator|!
operator|(
name|field
operator|.
name|isRequired
argument_list|()
operator|||
name|field
operator|.
name|getDefaultValue
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Field "
operator|+
name|this
operator|+
literal|" has single-valued doc values enabled, but has no default value and is not required"
argument_list|)
throw|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|readableToIndexed
specifier|public
name|String
name|readableToIndexed
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
specifier|final
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|readableToIndexed
argument_list|(
name|val
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|readableToIndexed
specifier|public
name|void
name|readableToIndexed
parameter_list|(
name|CharSequence
name|val
parameter_list|,
name|BytesRefBuilder
name|result
parameter_list|)
block|{
specifier|final
name|String
name|s
init|=
name|val
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
return|return;
specifier|final
name|Integer
name|intValue
init|=
name|stringValueToIntValue
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|intValue
argument_list|,
literal|0
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|readableToIndexed
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|StorableField
name|f
parameter_list|)
block|{
specifier|final
name|Number
name|val
init|=
name|f
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|intValueToStringValue
argument_list|(
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|indexedForm
parameter_list|)
block|{
if|if
condition|(
name|indexedForm
operator|==
literal|null
condition|)
return|return
literal|null
return|;
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|indexedForm
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|intValue
init|=
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|bytesRef
argument_list|)
decl_stmt|;
return|return
name|intValueToStringValue
argument_list|(
name|intValue
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|CharsRef
name|indexedToReadable
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|CharsRefBuilder
name|output
parameter_list|)
block|{
specifier|final
name|Integer
name|intValue
init|=
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|input
argument_list|)
decl_stmt|;
specifier|final
name|String
name|stringValue
init|=
name|intValueToStringValue
argument_list|(
name|intValue
argument_list|)
decl_stmt|;
name|output
operator|.
name|grow
argument_list|(
name|stringValue
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|setLength
argument_list|(
name|stringValue
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|stringValue
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|output
operator|.
name|length
argument_list|()
argument_list|,
name|output
operator|.
name|chars
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|output
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|EnumFieldValue
name|toObject
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
specifier|final
name|Integer
name|intValue
init|=
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|String
name|stringValue
init|=
name|intValueToStringValue
argument_list|(
name|intValue
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumFieldValue
argument_list|(
name|intValue
argument_list|,
name|stringValue
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|storedToIndexed
specifier|public
name|String
name|storedToIndexed
parameter_list|(
name|StorableField
name|f
parameter_list|)
block|{
specifier|final
name|Number
name|val
init|=
name|f
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
specifier|final
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|val
operator|.
name|intValue
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|createField
specifier|public
name|StorableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
specifier|final
name|boolean
name|indexed
init|=
name|field
operator|.
name|indexed
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|stored
init|=
name|field
operator|.
name|stored
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|docValues
init|=
name|field
operator|.
name|hasDocValues
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|indexed
operator|&&
operator|!
name|stored
operator|&&
operator|!
name|docValues
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|log
operator|.
name|trace
argument_list|(
literal|"Ignoring unindexed/unstored field: "
operator|+
name|field
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|final
name|Integer
name|intValue
init|=
name|stringValueToIntValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|intValue
operator|==
literal|null
operator|||
name|intValue
operator|.
name|equals
argument_list|(
name|DEFAULT_VALUE
argument_list|)
condition|)
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
literal|"Unknown value for enum field: "
operator|+
name|value
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
name|String
name|intAsString
init|=
name|intValue
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|newType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|newType
operator|.
name|setTokenized
argument_list|(
name|field
operator|.
name|isTokenized
argument_list|()
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setStored
argument_list|(
name|field
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setOmitNorms
argument_list|(
name|field
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setIndexOptions
argument_list|(
name|field
operator|.
name|indexed
argument_list|()
condition|?
name|getIndexOptions
argument_list|(
name|field
argument_list|,
name|intAsString
argument_list|)
else|:
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setStoreTermVectors
argument_list|(
name|field
operator|.
name|storeTermVector
argument_list|()
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|field
operator|.
name|storeTermOffsets
argument_list|()
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|field
operator|.
name|storeTermPositions
argument_list|()
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|NumericType
operator|.
name|INT
argument_list|)
expr_stmt|;
name|newType
operator|.
name|setNumericPrecisionStep
argument_list|(
name|DEFAULT_PRECISION_STEP
argument_list|)
expr_stmt|;
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
name|f
decl_stmt|;
name|f
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|IntField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|intValue
operator|.
name|intValue
argument_list|()
argument_list|,
name|newType
argument_list|)
expr_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/**    * Converting the (internal) integer value (indicating the sort order) to string (displayed) value    * @param intVal integer value    * @return string value    */
DECL|method|intValueToStringValue
specifier|public
name|String
name|intValueToStringValue
parameter_list|(
name|Integer
name|intVal
parameter_list|)
block|{
if|if
condition|(
name|intVal
operator|==
literal|null
condition|)
return|return
literal|null
return|;
specifier|final
name|String
name|enumString
init|=
name|enumIntToStringMap
operator|.
name|get
argument_list|(
name|intVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumString
operator|!=
literal|null
condition|)
return|return
name|enumString
return|;
comment|// can't find matching enum name - return DEFAULT_VALUE.toString()
return|return
name|DEFAULT_VALUE
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Converting the string (displayed) value (internal) to integer value (indicating the sort order)    * @param stringVal string value    * @return integer value    */
DECL|method|stringValueToIntValue
specifier|public
name|Integer
name|stringValueToIntValue
parameter_list|(
name|String
name|stringVal
parameter_list|)
block|{
if|if
condition|(
name|stringVal
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Integer
name|intValue
decl_stmt|;
specifier|final
name|Integer
name|enumInt
init|=
name|enumStringToIntMap
operator|.
name|get
argument_list|(
name|stringVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumInt
operator|!=
literal|null
condition|)
comment|//enum int found for string
return|return
name|enumInt
return|;
comment|//enum int not found for string
name|intValue
operator|=
name|tryParseInt
argument_list|(
name|stringVal
argument_list|)
expr_stmt|;
if|if
condition|(
name|intValue
operator|==
literal|null
condition|)
comment|//not Integer
name|intValue
operator|=
name|DEFAULT_VALUE
expr_stmt|;
specifier|final
name|String
name|enumString
init|=
name|enumIntToStringMap
operator|.
name|get
argument_list|(
name|intValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|enumString
operator|!=
literal|null
condition|)
comment|//has matching string
return|return
name|intValue
return|;
return|return
name|DEFAULT_VALUE
return|;
block|}
DECL|method|tryParseInt
specifier|private
specifier|static
name|Integer
name|tryParseInt
parameter_list|(
name|String
name|valueStr
parameter_list|)
block|{
name|Integer
name|intValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|intValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|valueStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{     }
return|return
name|intValue
return|;
block|}
block|}
end_class
end_unit
