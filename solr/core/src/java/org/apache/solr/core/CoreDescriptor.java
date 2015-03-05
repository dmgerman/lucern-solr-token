begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ImmutableList
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
name|ImmutableMap
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
name|lang
operator|.
name|StringUtils
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
name|cloud
operator|.
name|CloudDescriptor
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
name|util
operator|.
name|PropertiesUtil
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
name|IOException
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Properties
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import
begin_comment
comment|/**  * A Solr core descriptor  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|CoreDescriptor
specifier|public
class|class
name|CoreDescriptor
block|{
comment|// Properties file name constants
DECL|field|CORE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CORE_NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|CORE_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|CORE_CONFIG
init|=
literal|"config"
decl_stmt|;
DECL|field|CORE_INSTDIR
specifier|public
specifier|static
specifier|final
name|String
name|CORE_INSTDIR
init|=
literal|"instanceDir"
decl_stmt|;
DECL|field|CORE_ABS_INSTDIR
specifier|public
specifier|static
specifier|final
name|String
name|CORE_ABS_INSTDIR
init|=
literal|"absoluteInstDir"
decl_stmt|;
DECL|field|CORE_DATADIR
specifier|public
specifier|static
specifier|final
name|String
name|CORE_DATADIR
init|=
literal|"dataDir"
decl_stmt|;
DECL|field|CORE_ULOGDIR
specifier|public
specifier|static
specifier|final
name|String
name|CORE_ULOGDIR
init|=
literal|"ulogDir"
decl_stmt|;
DECL|field|CORE_SCHEMA
specifier|public
specifier|static
specifier|final
name|String
name|CORE_SCHEMA
init|=
literal|"schema"
decl_stmt|;
DECL|field|CORE_SHARD
specifier|public
specifier|static
specifier|final
name|String
name|CORE_SHARD
init|=
literal|"shard"
decl_stmt|;
DECL|field|CORE_COLLECTION
specifier|public
specifier|static
specifier|final
name|String
name|CORE_COLLECTION
init|=
literal|"collection"
decl_stmt|;
DECL|field|CORE_ROLES
specifier|public
specifier|static
specifier|final
name|String
name|CORE_ROLES
init|=
literal|"roles"
decl_stmt|;
DECL|field|CORE_PROPERTIES
specifier|public
specifier|static
specifier|final
name|String
name|CORE_PROPERTIES
init|=
literal|"properties"
decl_stmt|;
DECL|field|CORE_LOADONSTARTUP
specifier|public
specifier|static
specifier|final
name|String
name|CORE_LOADONSTARTUP
init|=
literal|"loadOnStartup"
decl_stmt|;
DECL|field|CORE_TRANSIENT
specifier|public
specifier|static
specifier|final
name|String
name|CORE_TRANSIENT
init|=
literal|"transient"
decl_stmt|;
DECL|field|CORE_NODE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CORE_NODE_NAME
init|=
literal|"coreNodeName"
decl_stmt|;
DECL|field|CORE_CONFIGSET
specifier|public
specifier|static
specifier|final
name|String
name|CORE_CONFIGSET
init|=
literal|"configSet"
decl_stmt|;
DECL|field|SOLR_CORE_PROP_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_CORE_PROP_PREFIX
init|=
literal|"solr.core."
decl_stmt|;
DECL|field|DEFAULT_EXTERNAL_PROPERTIES_FILE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_EXTERNAL_PROPERTIES_FILE
init|=
literal|"conf"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrcore.properties"
decl_stmt|;
comment|/**    * Get the standard properties in persistable form    * @return the standard core properties in persistable form    */
DECL|method|getPersistableStandardProperties
specifier|public
name|Properties
name|getPersistableStandardProperties
parameter_list|()
block|{
return|return
name|originalCoreProperties
return|;
block|}
comment|/**    * Get user-defined core properties in persistable form    * @return user-defined core properties in persistable form    */
DECL|method|getPersistableUserProperties
specifier|public
name|Properties
name|getPersistableUserProperties
parameter_list|()
block|{
return|return
name|originalExtraProperties
return|;
block|}
DECL|field|defaultProperties
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|defaultProperties
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|CORE_CONFIG
argument_list|,
literal|"solrconfig.xml"
argument_list|,
name|CORE_SCHEMA
argument_list|,
literal|"schema.xml"
argument_list|,
name|CORE_DATADIR
argument_list|,
literal|"data"
operator|+
name|File
operator|.
name|separator
argument_list|,
name|CORE_TRANSIENT
argument_list|,
literal|"false"
argument_list|,
name|CORE_LOADONSTARTUP
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
DECL|field|requiredProperties
specifier|private
specifier|static
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|requiredProperties
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CORE_NAME
argument_list|,
name|CORE_INSTDIR
argument_list|,
name|CORE_ABS_INSTDIR
argument_list|)
decl_stmt|;
DECL|field|standardPropNames
specifier|public
specifier|static
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|standardPropNames
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|CORE_NAME
argument_list|,
name|CORE_CONFIG
argument_list|,
name|CORE_INSTDIR
argument_list|,
name|CORE_DATADIR
argument_list|,
name|CORE_ULOGDIR
argument_list|,
name|CORE_SCHEMA
argument_list|,
name|CORE_PROPERTIES
argument_list|,
name|CORE_LOADONSTARTUP
argument_list|,
name|CORE_TRANSIENT
argument_list|,
name|CORE_CONFIGSET
argument_list|,
comment|// cloud props
name|CORE_SHARD
argument_list|,
name|CORE_COLLECTION
argument_list|,
name|CORE_ROLES
argument_list|,
name|CORE_NODE_NAME
argument_list|,
name|CloudDescriptor
operator|.
name|NUM_SHARDS
argument_list|)
decl_stmt|;
DECL|field|coreContainer
specifier|private
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|cloudDesc
specifier|private
specifier|final
name|CloudDescriptor
name|cloudDesc
decl_stmt|;
comment|/** The original standard core properties, before substitution */
DECL|field|originalCoreProperties
specifier|protected
specifier|final
name|Properties
name|originalCoreProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/** The original extra core properties, before substitution */
DECL|field|originalExtraProperties
specifier|protected
specifier|final
name|Properties
name|originalExtraProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/** The properties for this core, as available through getProperty() */
DECL|field|coreProperties
specifier|protected
specifier|final
name|Properties
name|coreProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/** The properties for this core, substitutable by resource loaders */
DECL|field|substitutableProperties
specifier|protected
specifier|final
name|Properties
name|substitutableProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/**    * Create a new CoreDescriptor.    * @param container       the CoreDescriptor's container    * @param name            the CoreDescriptor's name    * @param instanceDir     a String containing the instanceDir    * @param coreProps       a Properties object of the properties for this core    */
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|container
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|,
name|Properties
name|coreProps
parameter_list|)
block|{
name|this
argument_list|(
name|container
argument_list|,
name|name
argument_list|,
name|instanceDir
argument_list|,
name|coreProps
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|container
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|,
name|String
modifier|...
name|properties
parameter_list|)
block|{
name|this
argument_list|(
name|container
argument_list|,
name|name
argument_list|,
name|instanceDir
argument_list|,
name|toProperties
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toProperties
specifier|private
specifier|static
name|Properties
name|toProperties
parameter_list|(
name|String
modifier|...
name|properties
parameter_list|)
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
assert|assert
name|properties
operator|.
name|length
operator|%
literal|2
operator|==
literal|0
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|properties
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|properties
index|[
name|i
index|]
argument_list|,
name|properties
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
comment|/**    * Create a new CoreDescriptor.    * @param container       the CoreDescriptor's container    * @param name            the CoreDescriptor's name    * @param instanceDir     a String containing the instanceDir    * @param coreProps       a Properties object of the properties for this core    * @param params          additional params    */
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|container
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|,
name|Properties
name|coreProps
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|container
expr_stmt|;
name|originalCoreProperties
operator|.
name|setProperty
argument_list|(
name|CORE_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|originalCoreProperties
operator|.
name|setProperty
argument_list|(
name|CORE_INSTDIR
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
name|Properties
name|containerProperties
init|=
name|container
operator|.
name|getContainerProperties
argument_list|()
decl_stmt|;
name|name
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|checkPropertyIsNotEmpty
argument_list|(
name|name
argument_list|,
name|CORE_NAME
argument_list|)
argument_list|,
name|containerProperties
argument_list|)
expr_stmt|;
name|instanceDir
operator|=
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|checkPropertyIsNotEmpty
argument_list|(
name|instanceDir
argument_list|,
name|CORE_INSTDIR
argument_list|)
argument_list|,
name|containerProperties
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|putAll
argument_list|(
name|defaultProperties
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_INSTDIR
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_ABS_INSTDIR
argument_list|,
name|convertToAbsolute
argument_list|(
name|instanceDir
argument_list|,
name|container
operator|.
name|getCoreRootDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|propname
range|:
name|coreProps
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|String
name|propvalue
init|=
name|coreProps
operator|.
name|getProperty
argument_list|(
name|propname
argument_list|)
decl_stmt|;
if|if
condition|(
name|isUserDefinedProperty
argument_list|(
name|propname
argument_list|)
condition|)
name|originalExtraProperties
operator|.
name|put
argument_list|(
name|propname
argument_list|,
name|propvalue
argument_list|)
expr_stmt|;
else|else
name|originalCoreProperties
operator|.
name|put
argument_list|(
name|propname
argument_list|,
name|propvalue
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|requiredProperties
operator|.
name|contains
argument_list|(
name|propname
argument_list|)
condition|)
comment|// Required props are already dealt with
name|coreProperties
operator|.
name|setProperty
argument_list|(
name|propname
argument_list|,
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|propvalue
argument_list|,
name|containerProperties
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|loadExtraProperties
argument_list|()
expr_stmt|;
name|buildSubstitutableProperties
argument_list|()
expr_stmt|;
comment|// TODO maybe make this a CloudCoreDescriptor subclass?
if|if
condition|(
name|container
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
block|{
name|cloudDesc
operator|=
operator|new
name|CloudDescriptor
argument_list|(
name|name
argument_list|,
name|coreProperties
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|cloudDesc
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|cloudDesc
operator|=
literal|null
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CORE DESCRIPTOR: "
operator|+
name|coreProperties
argument_list|)
expr_stmt|;
block|}
comment|/**    * Load properties specified in an external properties file.    *    * The file to load can be specified in a {@code properties} property on    * the original Properties object used to create this CoreDescriptor.  If    * this has not been set, then we look for {@code conf/solrcore.properties}    * underneath the instance dir.    *    * File paths are taken as read from the core's instance directory    * if they are not absolute.    */
DECL|method|loadExtraProperties
specifier|protected
name|void
name|loadExtraProperties
parameter_list|()
block|{
name|String
name|filename
init|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_PROPERTIES
argument_list|,
name|DEFAULT_EXTERNAL_PROPERTIES_FILE
argument_list|)
decl_stmt|;
name|File
name|propertiesFile
init|=
name|resolvePaths
argument_list|(
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertiesFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|propertiesFile
argument_list|)
expr_stmt|;
name|Properties
name|externalProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|externalProps
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|putAll
argument_list|(
name|externalProps
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Could not load properties from %s: %s:"
argument_list|,
name|propertiesFile
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
name|e
operator|.
name|toString
argument_list|()
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
name|SERVER_ERROR
argument_list|,
name|message
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create the properties object used by resource loaders, etc, for property    * substitution.  The default solr properties are prefixed with 'solr.core.', so,    * e.g., 'name' becomes 'solr.core.name'    */
DECL|method|buildSubstitutableProperties
specifier|protected
name|void
name|buildSubstitutableProperties
parameter_list|()
block|{
for|for
control|(
name|String
name|propName
range|:
name|coreProperties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|String
name|propValue
init|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|propName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isUserDefinedProperty
argument_list|(
name|propName
argument_list|)
condition|)
name|propName
operator|=
name|SOLR_CORE_PROP_PREFIX
operator|+
name|propName
expr_stmt|;
name|substitutableProperties
operator|.
name|setProperty
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|resolvePaths
specifier|protected
name|File
name|resolvePaths
parameter_list|(
name|String
name|filepath
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|filepath
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isAbsolute
argument_list|()
condition|)
return|return
name|file
return|;
return|return
operator|new
name|File
argument_list|(
name|getInstanceDir
argument_list|()
argument_list|,
name|filepath
argument_list|)
return|;
block|}
comment|/**    * Is this property a Solr-standard property, or is it an extra property    * defined per-core by the user?    * @param propName the Property name    * @return {@code true} if this property is user-defined    */
DECL|method|isUserDefinedProperty
specifier|protected
specifier|static
name|boolean
name|isUserDefinedProperty
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
return|return
operator|!
name|standardPropNames
operator|.
name|contains
argument_list|(
name|propName
argument_list|)
return|;
block|}
DECL|method|checkPropertyIsNotEmpty
specifier|public
specifier|static
name|String
name|checkPropertyIsNotEmpty
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot create core with empty %s value"
argument_list|,
name|propName
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
name|SERVER_ERROR
argument_list|,
name|message
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
comment|/**    * Create a new CoreDescriptor with a given name and instancedir    * @param container     the CoreDescriptor's container    * @param name          the CoreDescriptor's name    * @param instanceDir   the CoreDescriptor's instancedir    */
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|container
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|)
block|{
name|this
argument_list|(
name|container
argument_list|,
name|name
argument_list|,
name|instanceDir
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new CoreDescriptor using the properties of an existing one    * @param coreName the new CoreDescriptor's name    * @param other    the CoreDescriptor to copy    */
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|String
name|coreName
parameter_list|,
name|CoreDescriptor
name|other
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|other
operator|.
name|coreContainer
expr_stmt|;
name|this
operator|.
name|cloudDesc
operator|=
name|other
operator|.
name|cloudDesc
expr_stmt|;
name|this
operator|.
name|originalExtraProperties
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|originalExtraProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|originalCoreProperties
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|originalCoreProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|coreProperties
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|coreProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|substitutableProperties
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|substitutableProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|coreProperties
operator|.
name|setProperty
argument_list|(
name|CORE_NAME
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|this
operator|.
name|originalCoreProperties
operator|.
name|setProperty
argument_list|(
name|CORE_NAME
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|this
operator|.
name|substitutableProperties
operator|.
name|setProperty
argument_list|(
name|SOLR_CORE_PROP_PREFIX
operator|+
name|CORE_NAME
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
block|}
DECL|method|getPropertiesName
specifier|public
name|String
name|getPropertiesName
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_PROPERTIES
argument_list|)
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_DATADIR
argument_list|)
return|;
block|}
DECL|method|usingDefaultDataDir
specifier|public
name|boolean
name|usingDefaultDataDir
parameter_list|()
block|{
return|return
name|defaultProperties
operator|.
name|get
argument_list|(
name|CORE_DATADIR
argument_list|)
operator|.
name|equals
argument_list|(
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_DATADIR
argument_list|)
argument_list|)
return|;
block|}
comment|/**@return the core instance directory. */
DECL|method|getRawInstanceDir
specifier|public
name|String
name|getRawInstanceDir
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_INSTDIR
argument_list|)
return|;
block|}
DECL|method|convertToAbsolute
specifier|private
specifier|static
name|String
name|convertToAbsolute
parameter_list|(
name|String
name|instDir
parameter_list|,
name|String
name|solrHome
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|instDir
argument_list|)
expr_stmt|;
return|return
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|solrHome
argument_list|)
operator|.
name|resolve
argument_list|(
name|instDir
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    *    * @return the core instance directory, prepended with solr_home if not an absolute path.    */
DECL|method|getInstanceDir
specifier|public
name|String
name|getInstanceDir
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_ABS_INSTDIR
argument_list|)
return|;
block|}
comment|/**@return the core configuration resource name. */
DECL|method|getConfigName
specifier|public
name|String
name|getConfigName
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_CONFIG
argument_list|)
return|;
block|}
comment|/**@return the core schema resource name. */
DECL|method|getSchemaName
specifier|public
name|String
name|getSchemaName
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_SCHEMA
argument_list|)
return|;
block|}
comment|/**@return the initial core name */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_NAME
argument_list|)
return|;
block|}
DECL|method|getCollectionName
specifier|public
name|String
name|getCollectionName
parameter_list|()
block|{
return|return
name|cloudDesc
operator|==
literal|null
condition|?
literal|null
else|:
name|cloudDesc
operator|.
name|getCollectionName
argument_list|()
return|;
block|}
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|coreContainer
return|;
block|}
DECL|method|getCloudDescriptor
specifier|public
name|CloudDescriptor
name|getCloudDescriptor
parameter_list|()
block|{
return|return
name|cloudDesc
return|;
block|}
DECL|method|isLoadOnStartup
specifier|public
name|boolean
name|isLoadOnStartup
parameter_list|()
block|{
name|String
name|tmp
init|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_LOADONSTARTUP
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|tmp
argument_list|)
return|;
block|}
DECL|method|isTransient
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
name|String
name|tmp
init|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_TRANSIENT
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
return|return
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|tmp
argument_list|)
return|;
block|}
DECL|method|getUlogDir
specifier|public
name|String
name|getUlogDir
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_ULOGDIR
argument_list|)
return|;
block|}
comment|/**    * Returns a specific property defined on this CoreDescriptor    * @param prop    - value to read from the properties structure.    * @param defVal  - return if no property found.    * @return associated string. May be null.    */
DECL|method|getCoreProperty
specifier|public
name|String
name|getCoreProperty
parameter_list|(
name|String
name|prop
parameter_list|,
name|String
name|defVal
parameter_list|)
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|,
name|defVal
argument_list|)
return|;
block|}
comment|/**    * Returns all substitutable properties defined on this CoreDescriptor    * @return all substitutable properties defined on this CoreDescriptor    */
DECL|method|getSubstitutableProperties
specifier|public
name|Properties
name|getSubstitutableProperties
parameter_list|()
block|{
return|return
name|substitutableProperties
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
literal|"CoreDescriptor[name="
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|";instanceDir="
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getConfigSet
specifier|public
name|String
name|getConfigSet
parameter_list|()
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_CONFIGSET
argument_list|)
return|;
block|}
block|}
end_class
end_unit
