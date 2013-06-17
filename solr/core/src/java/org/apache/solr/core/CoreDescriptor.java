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
name|io
operator|.
name|File
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
name|core
operator|.
name|ConfigSolr
operator|.
name|CfgProp
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
comment|// should probably be removed after 4x
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
DECL|field|standardPropNames
specifier|static
specifier|final
name|String
index|[]
name|standardPropNames
init|=
block|{
name|CORE_NAME
block|,
name|CORE_CONFIG
block|,
name|CORE_INSTDIR
block|,
name|CORE_DATADIR
block|,
name|CORE_ULOGDIR
block|,
name|CORE_SCHEMA
block|,
name|CORE_SHARD
block|,
name|CORE_COLLECTION
block|,
name|CORE_ROLES
block|,
name|CORE_PROPERTIES
block|,
name|CORE_LOADONSTARTUP
block|,
name|CORE_TRANSIENT
block|}
decl_stmt|;
comment|// As part of moving away from solr.xml (see SOLR-4196), it's _much_ easier to keep these as properties than set
comment|// them individually.
DECL|field|coreProperties
specifier|private
name|Properties
name|coreProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|//TODO: 5.0 remove this, this is solely a hack for persistence.
DECL|field|createdProperties
specifier|private
name|Properties
name|createdProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
DECL|field|loadedImplicit
specifier|private
name|boolean
name|loadedImplicit
init|=
literal|false
decl_stmt|;
DECL|field|coreContainer
specifier|private
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|cloudDesc
specifier|private
name|CloudDescriptor
name|cloudDesc
decl_stmt|;
DECL|method|CoreDescriptor
specifier|private
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|cont
parameter_list|)
block|{
comment|// Just a place to put initialization since it's a pain to add to the descriptor in every c'tor.
name|this
operator|.
name|coreContainer
operator|=
name|cont
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_LOADONSTARTUP
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_TRANSIENT
argument_list|,
literal|"false"
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
parameter_list|)
block|{
name|this
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|doInit
argument_list|(
name|name
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
block|}
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreDescriptor
name|descr
parameter_list|)
block|{
name|this
argument_list|(
name|descr
operator|.
name|coreContainer
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_INSTDIR
argument_list|,
name|descr
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_CONFIG
argument_list|,
name|descr
operator|.
name|getConfigName
argument_list|()
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_SCHEMA
argument_list|,
name|descr
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_NAME
argument_list|,
name|descr
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_DATADIR
argument_list|,
name|descr
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * CoreDescriptor - create a core descriptor given default properties from a core.properties file. This will be    * used in the "solr.xml-less (See SOLR-4196) world where there are no&lt;core&gt;&lt;/core&gt; tags at all, thus  much    * of the initialization that used to be done when reading solr.xml needs to be done here instead, particularly    * setting any defaults (e.g. schema.xml, directories, whatever).    *    * @param container - the CoreContainer that holds all the information about our cores, loaded, lazy etc.    * @param propsIn - A properties structure "core.properties" found while walking the file tree to discover cores.    *                  Any properties set in this param will overwrite the any defaults.    */
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|container
parameter_list|,
name|Properties
name|propsIn
parameter_list|)
block|{
name|this
argument_list|(
name|container
argument_list|)
expr_stmt|;
comment|// Set some default, normalize a directory or two
name|doInit
argument_list|(
name|propsIn
operator|.
name|getProperty
argument_list|(
name|CORE_NAME
argument_list|)
argument_list|,
name|propsIn
operator|.
name|getProperty
argument_list|(
name|CORE_INSTDIR
argument_list|)
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|putAll
argument_list|(
name|propsIn
argument_list|)
expr_stmt|;
block|}
DECL|method|doInit
specifier|private
name|void
name|doInit
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Core needs a name"
argument_list|)
throw|;
block|}
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|coreContainer
operator|!=
literal|null
operator|&&
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|cloudDesc
operator|=
operator|new
name|CloudDescriptor
argument_list|()
expr_stmt|;
comment|// cloud collection defaults to core name
name|cloudDesc
operator|.
name|setCollectionName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|instanceDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Missing required \'instanceDir\'"
argument_list|)
throw|;
block|}
name|instanceDir
operator|=
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|instanceDir
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
name|CORE_CONFIG
argument_list|,
name|getDefaultConfigName
argument_list|()
argument_list|)
expr_stmt|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_SCHEMA
argument_list|,
name|getDefaultSchemaName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initImplicitProperties
specifier|public
name|Properties
name|initImplicitProperties
parameter_list|()
block|{
name|Properties
name|implicitProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|coreContainer
operator|!=
literal|null
operator|&&
name|coreContainer
operator|.
name|getContainerProperties
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|implicitProperties
operator|.
name|putAll
argument_list|(
name|coreContainer
operator|.
name|getContainerProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.name"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.instanceDir"
argument_list|,
name|getInstanceDir
argument_list|()
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.dataDir"
argument_list|,
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.configName"
argument_list|,
name|getConfigName
argument_list|()
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.schemaName"
argument_list|,
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|implicitProperties
return|;
block|}
comment|/**@return the default config name. */
DECL|method|getDefaultConfigName
specifier|public
name|String
name|getDefaultConfigName
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
comment|/**@return the default schema name. */
DECL|method|getDefaultSchemaName
specifier|public
name|String
name|getDefaultSchemaName
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
comment|/**@return the default data directory. */
DECL|method|getDefaultDataDir
specifier|public
name|String
name|getDefaultDataDir
parameter_list|()
block|{
return|return
literal|"data"
operator|+
name|File
operator|.
name|separator
return|;
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
DECL|method|setPropertiesName
specifier|public
name|void
name|setPropertiesName
parameter_list|(
name|String
name|propertiesName
parameter_list|)
block|{
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_PROPERTIES
argument_list|,
name|propertiesName
argument_list|)
expr_stmt|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
name|String
name|dataDir
init|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_DATADIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataDir
operator|==
literal|null
condition|)
name|dataDir
operator|=
name|getDefaultDataDir
argument_list|()
expr_stmt|;
return|return
name|dataDir
return|;
block|}
DECL|method|setDataDir
specifier|public
name|void
name|setDataDir
parameter_list|(
name|String
name|s
parameter_list|)
block|{
comment|// normalize zero length to null.
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|coreProperties
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_DATADIR
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|usingDefaultDataDir
specifier|public
name|boolean
name|usingDefaultDataDir
parameter_list|()
block|{
comment|// DO NOT use the getDataDir method here since it'll assign something regardless.
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_DATADIR
argument_list|)
operator|==
literal|null
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
comment|/**    *    * @return the core instance directory, prepended with solr_home if not an absolute path.    */
DECL|method|getInstanceDir
specifier|public
name|String
name|getInstanceDir
parameter_list|()
block|{
name|String
name|instDir
init|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|CORE_INSTDIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|instDir
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
operator|new
name|File
argument_list|(
name|instDir
argument_list|)
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
return|return
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|instDir
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|coreContainer
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|coreContainer
operator|.
name|cfg
operator|!=
literal|null
condition|)
block|{
name|String
name|coreRootDir
init|=
name|coreContainer
operator|.
name|cfg
operator|.
name|get
argument_list|(
name|CfgProp
operator|.
name|SOLR_COREROOTDIRECTORY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|coreRootDir
operator|!=
literal|null
condition|)
block|{
return|return
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|coreRootDir
operator|+
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|instDir
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|coreContainer
operator|.
name|getSolrHome
argument_list|()
operator|+
name|SolrResourceLoader
operator|.
name|normalizeDir
argument_list|(
name|instDir
argument_list|)
argument_list|)
return|;
block|}
comment|/**Sets the core configuration resource name. */
DECL|method|setConfigName
specifier|public
name|void
name|setConfigName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name can not be null or empty"
argument_list|)
throw|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_CONFIG
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
comment|/**Sets the core schema resource name. */
DECL|method|setSchemaName
specifier|public
name|void
name|setSchemaName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name can not be null or empty"
argument_list|)
throw|;
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_SCHEMA
argument_list|,
name|name
argument_list|)
expr_stmt|;
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
DECL|method|getCoreProperties
name|Properties
name|getCoreProperties
parameter_list|()
block|{
return|return
name|coreProperties
return|;
block|}
comment|/**    * Set this core's properties. Please note that some implicit values will be added to the    * Properties instance passed into this method. This means that the Properties instance    * sent to this method will have different (less) key/value pairs than the Properties    * instance returned by #getCoreProperties method.    *    * Under any circumstance, the properties passed in will override any already present.Merge    */
DECL|method|setCoreProperties
specifier|public
name|void
name|setCoreProperties
parameter_list|(
name|Properties
name|coreProperties
parameter_list|)
block|{
if|if
condition|(
operator|!
name|loadedImplicit
condition|)
block|{
name|loadedImplicit
operator|=
literal|true
expr_stmt|;
name|Properties
name|p
init|=
name|initImplicitProperties
argument_list|()
decl_stmt|;
name|this
operator|.
name|coreProperties
operator|.
name|putAll
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// The caller presumably wants whatever properties passed in to override the current core props, so just add them.
if|if
condition|(
name|coreProperties
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|coreProperties
operator|.
name|putAll
argument_list|(
name|coreProperties
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addCreatedProperty
specifier|public
name|void
name|addCreatedProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|createdProperties
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getCreatedProperties
specifier|public
specifier|final
name|Properties
name|getCreatedProperties
parameter_list|()
block|{
return|return
name|createdProperties
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
DECL|method|setCloudDescriptor
specifier|public
name|void
name|setCloudDescriptor
parameter_list|(
name|CloudDescriptor
name|cloudDesc
parameter_list|)
block|{
name|this
operator|.
name|cloudDesc
operator|=
name|cloudDesc
expr_stmt|;
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
DECL|method|setLoadOnStartup
specifier|public
name|void
name|setLoadOnStartup
parameter_list|(
name|boolean
name|loadOnStartup
parameter_list|)
block|{
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_LOADONSTARTUP
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|loadOnStartup
argument_list|)
argument_list|)
expr_stmt|;
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
operator|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|tmp
argument_list|)
operator|)
return|;
block|}
DECL|method|setTransient
specifier|public
name|void
name|setTransient
parameter_list|(
name|boolean
name|isTransient
parameter_list|)
block|{
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_TRANSIENT
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|isTransient
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|setUlogDir
specifier|public
name|void
name|setUlogDir
parameter_list|(
name|String
name|ulogDir
parameter_list|)
block|{
name|coreProperties
operator|.
name|put
argument_list|(
name|CORE_ULOGDIR
argument_list|,
name|ulogDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads a property defined in the core.properties file that's replacing solr.xml (if present).    * @param prop    - value to read from the properties structure.    * @param defVal  - return if no property found.    * @return associated string. May be null.    */
DECL|method|getProperty
specifier|public
name|String
name|getProperty
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
comment|/**    * gReads a property defined in the core.properties file that's replacing solr.xml (if present).    * @param prop  value to read from the properties structure.    * @return associated string. May be null.    */
DECL|method|getProperty
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|prop
parameter_list|)
block|{
return|return
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|prop
argument_list|)
return|;
block|}
comment|/**    * This will eventually replace _all_ of the setters. Puts a value in the "new" (obsoleting solr.xml JIRAs) properties    * structures.    *    * Will replace any currently-existing property with the key "prop".    *    * @param prop - property name    * @param val  - property value    */
DECL|method|putProperty
specifier|public
name|void
name|putProperty
parameter_list|(
name|String
name|prop
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|coreProperties
operator|.
name|put
argument_list|(
name|prop
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
