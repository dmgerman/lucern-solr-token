begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport.config
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
operator|.
name|config
package|;
end_package
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
name|HashSet
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
name|Set
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
name|dataimport
operator|.
name|DataImportHandlerException
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
name|dataimport
operator|.
name|DataImporter
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_class
DECL|class|Entity
specifier|public
class|class
name|Entity
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|pk
specifier|private
specifier|final
name|String
name|pk
decl_stmt|;
DECL|field|pkMappingFromSchema
specifier|private
specifier|final
name|String
name|pkMappingFromSchema
decl_stmt|;
DECL|field|dataSourceName
specifier|private
specifier|final
name|String
name|dataSourceName
decl_stmt|;
DECL|field|processorName
specifier|private
specifier|final
name|String
name|processorName
decl_stmt|;
DECL|field|parentEntity
specifier|private
specifier|final
name|Entity
name|parentEntity
decl_stmt|;
DECL|field|docRoot
specifier|private
specifier|final
name|boolean
name|docRoot
decl_stmt|;
DECL|field|child
specifier|private
specifier|final
name|boolean
name|child
decl_stmt|;
DECL|field|children
specifier|private
specifier|final
name|List
argument_list|<
name|Entity
argument_list|>
name|children
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|EntityField
argument_list|>
name|fields
decl_stmt|;
DECL|field|colNameVsField
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|EntityField
argument_list|>
argument_list|>
name|colNameVsField
decl_stmt|;
DECL|field|allAttributes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allAttributes
decl_stmt|;
DECL|field|allFieldAttributes
specifier|private
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|allFieldAttributes
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|DIHConfiguration
name|config
decl_stmt|;
DECL|method|Entity
specifier|public
name|Entity
parameter_list|(
name|boolean
name|docRootFound
parameter_list|,
name|Element
name|element
parameter_list|,
name|DataImporter
name|di
parameter_list|,
name|DIHConfiguration
name|config
parameter_list|,
name|Entity
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parentEntity
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|String
name|modName
init|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|ConfigNameConstants
operator|.
name|NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|modName
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
literal|"Entity must have a name."
argument_list|)
throw|;
block|}
if|if
condition|(
name|modName
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Entity name must not have period (.): '"
operator|+
name|modName
argument_list|)
throw|;
block|}
if|if
condition|(
name|ConfigNameConstants
operator|.
name|RESERVED_WORDS
operator|.
name|contains
argument_list|(
name|modName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Entity name : '"
operator|+
name|modName
operator|+
literal|"' is a reserved keyword. Reserved words are: "
operator|+
name|ConfigNameConstants
operator|.
name|RESERVED_WORDS
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|modName
expr_stmt|;
name|this
operator|.
name|pk
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
literal|"pk"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|processorName
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|ConfigNameConstants
operator|.
name|PROCESSOR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataSourceName
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|DataImporter
operator|.
name|DATA_SRC
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|rawDocRootValue
init|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|ConfigNameConstants
operator|.
name|ROOT_ENTITY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|docRootFound
operator|&&
operator|!
literal|"false"
operator|.
name|equals
argument_list|(
name|rawDocRootValue
argument_list|)
condition|)
block|{
comment|// if in this chain no document root is found()
name|docRoot
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|docRoot
operator|=
literal|false
expr_stmt|;
block|}
name|String
name|childValue
init|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|ConfigNameConstants
operator|.
name|CHILD
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|child
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|childValue
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|modAttributes
init|=
name|ConfigParseUtil
operator|.
name|getAllAttributes
argument_list|(
name|element
argument_list|)
decl_stmt|;
name|modAttributes
operator|.
name|put
argument_list|(
name|ConfigNameConstants
operator|.
name|DATA_SRC
argument_list|,
name|this
operator|.
name|dataSourceName
argument_list|)
expr_stmt|;
name|this
operator|.
name|allAttributes
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|modAttributes
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Element
argument_list|>
name|n
init|=
name|ConfigParseUtil
operator|.
name|getChildNodes
argument_list|(
name|element
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|EntityField
argument_list|>
name|modFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|n
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|EntityField
argument_list|>
argument_list|>
name|modColNameVsField
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|modAllFieldAttributes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Element
name|elem
range|:
name|n
control|)
block|{
name|EntityField
operator|.
name|Builder
name|fieldBuilder
init|=
operator|new
name|EntityField
operator|.
name|Builder
argument_list|(
name|elem
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getSchema
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fieldBuilder
operator|.
name|getNameOrColumn
argument_list|()
operator|!=
literal|null
operator|&&
name|fieldBuilder
operator|.
name|getNameOrColumn
argument_list|()
operator|.
name|contains
argument_list|(
literal|"${"
argument_list|)
condition|)
block|{
name|fieldBuilder
operator|.
name|dynamicName
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|SchemaField
name|schemaField
init|=
name|config
operator|.
name|getSchemaField
argument_list|(
name|fieldBuilder
operator|.
name|getNameOrColumn
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaField
operator|!=
literal|null
condition|)
block|{
name|fieldBuilder
operator|.
name|name
operator|=
name|schemaField
operator|.
name|getName
argument_list|()
expr_stmt|;
name|fieldBuilder
operator|.
name|multiValued
operator|=
name|schemaField
operator|.
name|multiValued
argument_list|()
expr_stmt|;
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
name|DataImporter
operator|.
name|MULTI_VALUED
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|schemaField
operator|.
name|multiValued
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
name|DataImporter
operator|.
name|TYPE
argument_list|,
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
literal|"indexed"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|schemaField
operator|.
name|indexed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
literal|"stored"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|schemaField
operator|.
name|stored
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
literal|"defaultValue"
argument_list|,
name|schemaField
operator|.
name|getDefaultValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldBuilder
operator|.
name|toWrite
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
name|Set
argument_list|<
name|EntityField
argument_list|>
name|fieldSet
init|=
name|modColNameVsField
operator|.
name|get
argument_list|(
name|fieldBuilder
operator|.
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldSet
operator|==
literal|null
condition|)
block|{
name|fieldSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|modColNameVsField
operator|.
name|put
argument_list|(
name|fieldBuilder
operator|.
name|column
argument_list|,
name|fieldSet
argument_list|)
expr_stmt|;
block|}
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
literal|"boost"
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|fieldBuilder
operator|.
name|boost
argument_list|)
argument_list|)
expr_stmt|;
name|fieldBuilder
operator|.
name|allAttributes
operator|.
name|put
argument_list|(
literal|"toWrite"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|fieldBuilder
operator|.
name|toWrite
argument_list|)
argument_list|)
expr_stmt|;
name|modAllFieldAttributes
operator|.
name|add
argument_list|(
name|fieldBuilder
operator|.
name|allAttributes
argument_list|)
expr_stmt|;
name|fieldBuilder
operator|.
name|entity
operator|=
name|this
expr_stmt|;
name|EntityField
name|field
init|=
operator|new
name|EntityField
argument_list|(
name|fieldBuilder
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|modFields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|EntityField
argument_list|>
argument_list|>
name|modColNameVsField1
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
name|String
argument_list|,
name|Set
argument_list|<
name|EntityField
argument_list|>
argument_list|>
name|entry
range|:
name|modColNameVsField
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|modColNameVsField1
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|colNameVsField
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|modColNameVsField1
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|modFields
argument_list|)
expr_stmt|;
name|this
operator|.
name|allFieldAttributes
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|modAllFieldAttributes
argument_list|)
expr_stmt|;
name|String
name|modPkMappingFromSchema
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|getSchema
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SchemaField
name|uniqueKey
init|=
name|config
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|uniqueKey
operator|!=
literal|null
condition|)
block|{
name|modPkMappingFromSchema
operator|=
name|uniqueKey
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|// if no fields are mentioned . solr uniqueKey is same as dih 'pk'
for|for
control|(
name|EntityField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|modPkMappingFromSchema
argument_list|)
condition|)
block|{
name|modPkMappingFromSchema
operator|=
name|field
operator|.
name|getColumn
argument_list|()
expr_stmt|;
comment|// get the corresponding column mapping for the solr uniqueKey
comment|// But if there are multiple columns mapping to the solr uniqueKey,
comment|// it will fail
comment|// so , in one off cases we may need pk
break|break;
block|}
block|}
block|}
block|}
name|pkMappingFromSchema
operator|=
name|modPkMappingFromSchema
expr_stmt|;
name|n
operator|=
name|ConfigParseUtil
operator|.
name|getChildNodes
argument_list|(
name|element
argument_list|,
literal|"entity"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Entity
argument_list|>
name|modEntities
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Element
name|elem
range|:
name|n
control|)
block|{
name|modEntities
operator|.
name|add
argument_list|(
operator|new
name|Entity
argument_list|(
operator|(
name|docRootFound
operator|||
name|this
operator|.
name|docRoot
operator|)
argument_list|,
name|elem
argument_list|,
name|di
argument_list|,
name|config
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|children
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|modEntities
argument_list|)
expr_stmt|;
block|}
DECL|method|getPk
specifier|public
name|String
name|getPk
parameter_list|()
block|{
return|return
name|pk
operator|==
literal|null
condition|?
name|pkMappingFromSchema
else|:
name|pk
return|;
block|}
DECL|method|getSchemaPk
specifier|public
name|String
name|getSchemaPk
parameter_list|()
block|{
return|return
name|pkMappingFromSchema
operator|!=
literal|null
condition|?
name|pkMappingFromSchema
else|:
name|pk
return|;
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
DECL|method|getPkMappingFromSchema
specifier|public
name|String
name|getPkMappingFromSchema
parameter_list|()
block|{
return|return
name|pkMappingFromSchema
return|;
block|}
DECL|method|getDataSourceName
specifier|public
name|String
name|getDataSourceName
parameter_list|()
block|{
return|return
name|dataSourceName
return|;
block|}
DECL|method|getProcessorName
specifier|public
name|String
name|getProcessorName
parameter_list|()
block|{
return|return
name|processorName
return|;
block|}
DECL|method|getParentEntity
specifier|public
name|Entity
name|getParentEntity
parameter_list|()
block|{
return|return
name|parentEntity
return|;
block|}
DECL|method|isDocRoot
specifier|public
name|boolean
name|isDocRoot
parameter_list|()
block|{
return|return
name|docRoot
return|;
block|}
DECL|method|getChildren
specifier|public
name|List
argument_list|<
name|Entity
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
DECL|method|getFields
specifier|public
name|List
argument_list|<
name|EntityField
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
DECL|method|getColNameVsField
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|EntityField
argument_list|>
argument_list|>
name|getColNameVsField
parameter_list|()
block|{
return|return
name|colNameVsField
return|;
block|}
DECL|method|getAllAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllAttributes
parameter_list|()
block|{
return|return
name|allAttributes
return|;
block|}
DECL|method|getAllFieldsList
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getAllFieldsList
parameter_list|()
block|{
return|return
name|allFieldAttributes
return|;
block|}
DECL|method|isChild
specifier|public
name|boolean
name|isChild
parameter_list|()
block|{
return|return
name|child
return|;
block|}
block|}
end_class
end_unit
