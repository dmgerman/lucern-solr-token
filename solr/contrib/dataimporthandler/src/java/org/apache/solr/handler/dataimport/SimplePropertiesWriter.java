begin_unit
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import
begin_comment
comment|/**  *<p>  *  Writes properties using {@link Properties#store} .  *  The special property "last_index_time" is converted to a formatted date.  *  Users can configure the location, filename, locale and date format to use.  *</p>   */
end_comment
begin_class
DECL|class|SimplePropertiesWriter
specifier|public
class|class
name|SimplePropertiesWriter
extends|extends
name|DIHProperties
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SimplePropertiesWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LAST_INDEX_KEY
specifier|static
specifier|final
name|String
name|LAST_INDEX_KEY
init|=
literal|"last_index_time"
decl_stmt|;
DECL|field|filename
specifier|protected
name|String
name|filename
init|=
literal|null
decl_stmt|;
DECL|field|configDir
specifier|protected
name|String
name|configDir
init|=
literal|null
decl_stmt|;
DECL|field|locale
specifier|protected
name|Locale
name|locale
init|=
literal|null
decl_stmt|;
DECL|field|dateFormat
specifier|protected
name|SimpleDateFormat
name|dateFormat
init|=
literal|null
decl_stmt|;
comment|/**    * The locale to use when writing the properties file.  Default is {@link Locale#ROOT}    */
DECL|field|LOCALE
specifier|public
specifier|static
specifier|final
name|String
name|LOCALE
init|=
literal|"locale"
decl_stmt|;
comment|/**    * The date format to use when writing values for "last_index_time" to the properties file.    * See {@link SimpleDateFormat} for patterns.  Default is yyyy-MM-dd HH:mm:ss .    */
DECL|field|DATE_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FORMAT
init|=
literal|"dateFormat"
decl_stmt|;
comment|/**    * The directory to save the properties file in. Default is the current core's "config" directory.    */
DECL|field|DIRECTORY
specifier|public
specifier|static
specifier|final
name|String
name|DIRECTORY
init|=
literal|"directory"
decl_stmt|;
comment|/**    * The filename to save the properties file to.  Default is this Handler's name from solrconfig.xml.    */
DECL|field|FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"filename"
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|DataImporter
name|dataImporter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|FILENAME
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|filename
operator|=
name|params
operator|.
name|get
argument_list|(
name|FILENAME
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dataImporter
operator|.
name|getHandlerName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|filename
operator|=
name|dataImporter
operator|.
name|getHandlerName
argument_list|()
operator|+
literal|".properties"
expr_stmt|;
block|}
else|else
block|{
name|filename
operator|=
literal|"dataimport.properties"
expr_stmt|;
block|}
name|findDirectory
argument_list|(
name|dataImporter
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|LOCALE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|localeStr
init|=
name|params
operator|.
name|get
argument_list|(
name|LOCALE
argument_list|)
decl_stmt|;
for|for
control|(
name|Locale
name|l
range|:
name|Locale
operator|.
name|getAvailableLocales
argument_list|()
control|)
block|{
if|if
condition|(
name|localeStr
operator|.
name|equals
argument_list|(
name|l
operator|.
name|getDisplayName
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
name|locale
operator|=
name|l
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|locale
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Unsupported locale for PropertWriter: "
operator|+
name|localeStr
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|locale
operator|=
name|Locale
operator|.
name|ROOT
expr_stmt|;
block|}
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|DATE_FORMAT
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|dateFormat
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|DATE_FORMAT
argument_list|)
argument_list|,
name|locale
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dateFormat
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|,
name|locale
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|findDirectory
specifier|protected
name|void
name|findDirectory
parameter_list|(
name|DataImporter
name|dataImporter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|DIRECTORY
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|configDir
operator|=
name|params
operator|.
name|get
argument_list|(
name|DIRECTORY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SolrCore
name|core
init|=
name|dataImporter
operator|.
name|getCore
argument_list|()
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|configDir
operator|=
name|SolrResourceLoader
operator|.
name|locateSolrHome
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|configDir
operator|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPersistFile
specifier|private
name|File
name|getPersistFile
parameter_list|()
block|{
specifier|final
name|File
name|filePath
decl_stmt|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|filename
argument_list|)
operator|.
name|isAbsolute
argument_list|()
operator|||
name|configDir
operator|==
literal|null
condition|)
block|{
name|filePath
operator|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filePath
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|configDir
argument_list|)
argument_list|,
name|filename
argument_list|)
expr_stmt|;
block|}
return|return
name|filePath
return|;
block|}
annotation|@
name|Override
DECL|method|isWritable
specifier|public
name|boolean
name|isWritable
parameter_list|()
block|{
name|File
name|persistFile
init|=
name|getPersistFile
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|persistFile
operator|.
name|exists
argument_list|()
condition|?
name|persistFile
operator|.
name|canWrite
argument_list|()
else|:
name|persistFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|canWrite
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|convertDateToString
specifier|public
name|String
name|convertDateToString
parameter_list|(
name|Date
name|d
parameter_list|)
block|{
return|return
name|dateFormat
operator|.
name|format
argument_list|(
name|d
argument_list|)
return|;
block|}
DECL|method|convertStringToDate
specifier|protected
name|Date
name|convertStringToDate
parameter_list|(
name|String
name|s
parameter_list|)
block|{
try|try
block|{
return|return
name|dateFormat
operator|.
name|parse
argument_list|(
name|s
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Value for "
operator|+
name|LAST_INDEX_KEY
operator|+
literal|" is invalid for date format "
operator|+
name|dateFormat
operator|.
name|toLocalizedPattern
argument_list|()
operator|+
literal|" : "
operator|+
name|s
argument_list|)
throw|;
block|}
block|}
comment|/**    * {@link DocBuilder} sends the date as an Object because     * this class knows how to convert it to a String    */
DECL|method|mapToProperties
specifier|protected
name|Properties
name|mapToProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propObjs
parameter_list|)
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
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
name|Object
argument_list|>
name|entry
range|:
name|propObjs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|val
init|=
literal|null
decl_stmt|;
name|String
name|lastKeyPart
init|=
name|key
decl_stmt|;
name|int
name|lastDotPos
init|=
name|key
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDotPos
operator|!=
operator|-
literal|1
operator|&&
name|key
operator|.
name|length
argument_list|()
operator|>
name|lastDotPos
operator|+
literal|1
condition|)
block|{
name|lastKeyPart
operator|=
name|key
operator|.
name|substring
argument_list|(
name|lastDotPos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LAST_INDEX_KEY
operator|.
name|equals
argument_list|(
name|lastKeyPart
argument_list|)
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Date
condition|)
block|{
name|val
operator|=
name|convertDateToString
argument_list|(
operator|(
name|Date
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|val
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|p
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
comment|/**    * We'll send everything back as Strings as this class has    * already converted them.    */
DECL|method|propertiesToMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propertiesToMap
parameter_list|(
name|Properties
name|p
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|theMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
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
name|p
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Object
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|theMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|theMap
return|;
block|}
annotation|@
name|Override
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propObjs
parameter_list|)
block|{
name|Writer
name|propOutput
init|=
literal|null
decl_stmt|;
name|Properties
name|existingProps
init|=
name|mapToProperties
argument_list|(
name|readIndexerProperties
argument_list|()
argument_list|)
decl_stmt|;
name|Properties
name|newProps
init|=
name|mapToProperties
argument_list|(
name|propObjs
argument_list|)
decl_stmt|;
try|try
block|{
name|existingProps
operator|.
name|putAll
argument_list|(
name|newProps
argument_list|)
expr_stmt|;
name|propOutput
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|getPersistFile
argument_list|()
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|existingProps
operator|.
name|store
argument_list|(
name|propOutput
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Wrote last indexed time to "
operator|+
name|filename
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
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to persist Index Start Time"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|propOutput
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readIndexerProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readIndexerProperties
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStream
name|propInput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|filePath
init|=
name|configDir
decl_stmt|;
if|if
condition|(
name|configDir
operator|!=
literal|null
operator|&&
operator|!
name|configDir
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
block|{
name|filePath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
block|}
name|filePath
operator|+=
name|filename
expr_stmt|;
name|propInput
operator|=
operator|new
name|FileInputStream
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|props
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|propInput
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Read "
operator|+
name|filename
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to read: "
operator|+
name|filename
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|propInput
argument_list|)
expr_stmt|;
block|}
return|return
name|propertiesToMap
argument_list|(
name|props
argument_list|)
return|;
block|}
block|}
end_class
end_unit
