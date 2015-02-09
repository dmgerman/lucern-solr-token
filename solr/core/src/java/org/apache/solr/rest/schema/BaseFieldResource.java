begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
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
name|solr
operator|.
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|CoreDescriptor
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
name|rest
operator|.
name|BaseSolrResource
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
name|solr
operator|.
name|schema
operator|.
name|ManagedIndexSchema
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
name|SchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
comment|/**  * Base class for Schema Field and DynamicField requests.  */
end_comment
begin_class
DECL|class|BaseFieldResource
specifier|abstract
class|class
name|BaseFieldResource
extends|extends
name|BaseSolrResource
block|{
DECL|field|INCLUDE_DYNAMIC_PARAM
specifier|protected
specifier|static
specifier|final
name|String
name|INCLUDE_DYNAMIC_PARAM
init|=
literal|"includeDynamic"
decl_stmt|;
DECL|field|DYNAMIC_BASE
specifier|private
specifier|static
specifier|final
name|String
name|DYNAMIC_BASE
init|=
literal|"dynamicBase"
decl_stmt|;
DECL|field|requestedFields
specifier|private
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
name|requestedFields
decl_stmt|;
DECL|field|showDefaults
specifier|private
name|boolean
name|showDefaults
decl_stmt|;
DECL|method|getRequestedFields
specifier|protected
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
name|getRequestedFields
parameter_list|()
block|{
return|return
name|requestedFields
return|;
block|}
DECL|method|BaseFieldResource
specifier|protected
name|BaseFieldResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Pulls the "fl" param from the request and splits it to get the    * requested list of fields.  The (Dynamic)FieldCollectionResource classes    * will then restrict the fields sent back in the response to those    * on this list.  The (Dynamic)FieldResource classes ignore this list,     * since the (dynamic) field is specified in the URL path, rather than    * in a query parameter.    *<p>    * Also pulls the "showDefaults" param from the request, for use by all    * subclasses to include default values from the associated field type    * in the response.  By default this param is off.    */
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
if|if
condition|(
name|isExisting
argument_list|()
condition|)
block|{
name|String
name|flParam
init|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|flParam
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|flParam
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"[,\\s]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|requestedFields
operator|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|field
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|requestedFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|showDefaults
operator|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|SHOW_DEFAULTS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Get the properties for a given field.    *    * @param field not required to exist in the schema    */
DECL|method|getFieldProperties
specifier|protected
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getFieldProperties
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|field
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|properties
init|=
name|field
operator|.
name|getNamedPropertyValues
argument_list|(
name|showDefaults
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|dynamicBase
init|=
name|getSchema
argument_list|()
operator|.
name|getDynamicPattern
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add dynamicBase property if it's different from the field name.
if|if
condition|(
operator|!
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|dynamicBase
argument_list|)
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|DYNAMIC_BASE
argument_list|,
name|dynamicBase
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|field
operator|==
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|UNIQUE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
comment|/**    * When running in cloud mode, waits for a schema update to be    * applied by all active replicas of the current collection.    */
DECL|method|waitForSchemaUpdateToPropagate
specifier|protected
name|void
name|waitForSchemaUpdateToPropagate
parameter_list|(
name|IndexSchema
name|newSchema
parameter_list|)
block|{
comment|// If using ZooKeeper and the client application has requested an update timeout, then block until all
comment|// active replicas for this collection process the updated schema
if|if
condition|(
name|getUpdateTimeoutSecs
argument_list|()
operator|>
literal|0
operator|&&
name|newSchema
operator|!=
literal|null
operator|&&
name|newSchema
operator|.
name|getResourceLoader
argument_list|()
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|CoreDescriptor
name|cd
init|=
name|getSolrCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|String
name|collection
init|=
name|cd
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|newSchema
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|ManagedIndexSchema
operator|.
name|waitForSchemaZkVersionAgreement
argument_list|(
name|collection
argument_list|,
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
argument_list|,
operator|(
operator|(
name|ManagedIndexSchema
operator|)
name|newSchema
operator|)
operator|.
name|getSchemaZkVersion
argument_list|()
argument_list|,
name|zkLoader
operator|.
name|getZkController
argument_list|()
argument_list|,
name|getUpdateTimeoutSecs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// protected access on this class triggers a bug in javadoc generation caught by
comment|// documentation-link: "BROKEN LINK" reported in javadoc for classes using
comment|// NewFieldArguments because the link target file is BaseFieldResource.NewFieldArguments,
comment|// but the actual file is BaseFieldResource$NewFieldArguments.
DECL|class|NewFieldArguments
specifier|static
class|class
name|NewFieldArguments
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|map
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
DECL|method|NewFieldArguments
name|NewFieldArguments
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|map
return|;
block|}
block|}
block|}
end_class
end_unit
