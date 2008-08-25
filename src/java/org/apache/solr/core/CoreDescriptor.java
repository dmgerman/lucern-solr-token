begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * A Solr core descriptor  *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|CoreDescriptor
specifier|public
class|class
name|CoreDescriptor
implements|implements
name|Cloneable
block|{
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|instanceDir
specifier|protected
name|String
name|instanceDir
decl_stmt|;
DECL|field|configName
specifier|protected
name|String
name|configName
decl_stmt|;
DECL|field|schemaName
specifier|protected
name|String
name|schemaName
decl_stmt|;
DECL|field|coreContainer
specifier|private
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|coreProperties
specifier|private
name|Properties
name|coreProperties
decl_stmt|;
DECL|method|CoreDescriptor
specifier|public
name|CoreDescriptor
parameter_list|(
name|CoreContainer
name|coreContainer
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|instanceDir
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
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
if|if
condition|(
operator|!
name|instanceDir
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|instanceDir
operator|=
name|instanceDir
operator|+
literal|"/"
expr_stmt|;
name|this
operator|.
name|instanceDir
operator|=
name|instanceDir
expr_stmt|;
name|this
operator|.
name|configName
operator|=
name|getDefaultConfigName
argument_list|()
expr_stmt|;
name|this
operator|.
name|schemaName
operator|=
name|getDefaultSchemaName
argument_list|()
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
operator|.
name|instanceDir
operator|=
name|descr
operator|.
name|instanceDir
expr_stmt|;
name|this
operator|.
name|configName
operator|=
name|descr
operator|.
name|configName
expr_stmt|;
name|this
operator|.
name|schemaName
operator|=
name|descr
operator|.
name|schemaName
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|descr
operator|.
name|name
expr_stmt|;
name|coreContainer
operator|=
name|descr
operator|.
name|coreContainer
expr_stmt|;
block|}
DECL|method|initImplicitProperties
specifier|private
name|Properties
name|initImplicitProperties
parameter_list|()
block|{
name|Properties
name|implicitProperties
init|=
operator|new
name|Properties
argument_list|(
name|coreContainer
operator|.
name|getContainerProperties
argument_list|()
argument_list|)
decl_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.instanceDir"
argument_list|,
name|instanceDir
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.configName"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|implicitProperties
operator|.
name|setProperty
argument_list|(
literal|"solr.core.schemaName"
argument_list|,
name|schemaName
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
name|this
operator|.
name|instanceDir
operator|+
literal|"data/"
return|;
block|}
comment|/**@return the core instance directory. */
DECL|method|getInstanceDir
specifier|public
name|String
name|getInstanceDir
parameter_list|()
block|{
return|return
name|instanceDir
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
name|this
operator|.
name|configName
operator|=
name|name
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
name|this
operator|.
name|configName
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
name|this
operator|.
name|schemaName
operator|=
name|name
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
name|this
operator|.
name|schemaName
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
name|this
operator|.
name|name
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
comment|/**    * Set this core's properties. Please note that some implicit values will be added to the    * Properties instance passed into this method. This means that the Properties instance    * set to this method will have different (less) key/value pairs than the Properties    * instance returned by #getCoreProperties method.    *     * @param coreProperties    */
DECL|method|setCoreProperties
name|void
name|setCoreProperties
parameter_list|(
name|Properties
name|coreProperties
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|coreProperties
operator|==
literal|null
condition|)
block|{
name|Properties
name|p
init|=
name|initImplicitProperties
argument_list|()
decl_stmt|;
name|this
operator|.
name|coreProperties
operator|=
operator|new
name|Properties
argument_list|(
name|p
argument_list|)
expr_stmt|;
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
end_class
end_unit
