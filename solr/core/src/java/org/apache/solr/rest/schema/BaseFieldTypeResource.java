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
name|FieldType
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
name|List
import|;
end_import
begin_comment
comment|/**  * Base class for the FieldType resource classes.  */
end_comment
begin_class
DECL|class|BaseFieldTypeResource
specifier|abstract
class|class
name|BaseFieldTypeResource
extends|extends
name|BaseSolrResource
block|{
DECL|field|showDefaults
specifier|private
name|boolean
name|showDefaults
decl_stmt|;
DECL|method|BaseFieldTypeResource
specifier|protected
name|BaseFieldTypeResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
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
comment|/** Used by subclasses to collect field type properties */
DECL|method|getFieldTypeProperties
specifier|protected
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getFieldTypeProperties
parameter_list|(
name|FieldType
name|fieldType
parameter_list|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|properties
init|=
name|fieldType
operator|.
name|getNamedPropertyValues
argument_list|(
name|showDefaults
argument_list|)
decl_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|FIELDS
argument_list|,
name|getFieldsWithFieldType
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|DYNAMIC_FIELDS
argument_list|,
name|getDynamicFieldsWithFieldType
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
comment|/** Return a list of names of Fields that have the given FieldType */
DECL|method|getFieldsWithFieldType
specifier|protected
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getFieldsWithFieldType
parameter_list|(
name|FieldType
name|fieldType
parameter_list|)
function_decl|;
comment|/** Return a list of names of DynamicFields that have the given FieldType */
DECL|method|getDynamicFieldsWithFieldType
specifier|protected
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getDynamicFieldsWithFieldType
parameter_list|(
name|FieldType
name|fieldType
parameter_list|)
function_decl|;
comment|/**    * Adds one or more new FieldType definitions to the managed schema for the given core.    */
DECL|method|addNewFieldTypes
specifier|protected
name|void
name|addNewFieldTypes
parameter_list|(
name|List
argument_list|<
name|FieldType
argument_list|>
name|newFieldTypes
parameter_list|,
name|ManagedIndexSchema
name|oldSchema
parameter_list|)
block|{
name|IndexSchema
name|newSchema
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|oldSchema
operator|.
name|getSchemaUpdateLock
argument_list|()
init|)
block|{
name|newSchema
operator|=
name|oldSchema
operator|.
name|addFieldTypes
argument_list|(
name|newFieldTypes
argument_list|)
expr_stmt|;
name|getSolrCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ManagedIndexSchema
operator|.
name|SchemaChangedInZkException
name|e
parameter_list|)
block|{
name|oldSchema
operator|=
operator|(
name|ManagedIndexSchema
operator|)
name|getSolrCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
block|}
block|}
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
block|}
end_class
end_unit
