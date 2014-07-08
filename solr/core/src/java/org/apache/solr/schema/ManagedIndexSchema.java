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
name|commons
operator|.
name|io
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
name|cloud
operator|.
name|ZkController
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
name|SolrException
operator|.
name|ErrorCode
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
name|cloud
operator|.
name|SolrZkClient
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
name|Config
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
name|SolrConfig
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|util
operator|.
name|Arrays
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
name|Map
import|;
end_import
begin_comment
comment|/** Solr-managed schema - non-user-editable, but can be mutable via internal and external REST API requests. */
end_comment
begin_class
DECL|class|ManagedIndexSchema
specifier|public
specifier|final
class|class
name|ManagedIndexSchema
extends|extends
name|IndexSchema
block|{
DECL|field|isMutable
specifier|private
name|boolean
name|isMutable
init|=
literal|false
decl_stmt|;
DECL|method|isMutable
annotation|@
name|Override
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
name|isMutable
return|;
block|}
DECL|field|managedSchemaResourceName
specifier|final
name|String
name|managedSchemaResourceName
decl_stmt|;
DECL|field|schemaZkVersion
name|int
name|schemaZkVersion
decl_stmt|;
DECL|field|schemaUpdateLock
specifier|final
name|Object
name|schemaUpdateLock
decl_stmt|;
comment|/**    * Constructs a schema using the specified resource name and stream.    *    * @see org.apache.solr.core.SolrResourceLoader#openSchema    *      By default, this follows the normal config path directory searching rules.    * @see org.apache.solr.core.SolrResourceLoader#openResource    */
DECL|method|ManagedIndexSchema
name|ManagedIndexSchema
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|String
name|name
parameter_list|,
name|InputSource
name|is
parameter_list|,
name|boolean
name|isMutable
parameter_list|,
name|String
name|managedSchemaResourceName
parameter_list|,
name|int
name|schemaZkVersion
parameter_list|,
name|Object
name|schemaUpdateLock
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|super
argument_list|(
name|solrConfig
argument_list|,
name|name
argument_list|,
name|is
argument_list|)
expr_stmt|;
name|this
operator|.
name|isMutable
operator|=
name|isMutable
expr_stmt|;
name|this
operator|.
name|managedSchemaResourceName
operator|=
name|managedSchemaResourceName
expr_stmt|;
name|this
operator|.
name|schemaZkVersion
operator|=
name|schemaZkVersion
expr_stmt|;
name|this
operator|.
name|schemaUpdateLock
operator|=
name|schemaUpdateLock
expr_stmt|;
block|}
comment|/** Persist the schema to local storage or to ZooKeeper */
DECL|method|persistManagedSchema
name|boolean
name|persistManagedSchema
parameter_list|(
name|boolean
name|createOnly
parameter_list|)
block|{
if|if
condition|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
return|return
name|persistManagedSchemaToZooKeeper
argument_list|(
name|createOnly
argument_list|)
return|;
block|}
comment|// Persist locally
name|File
name|managedSchemaFile
init|=
operator|new
name|File
argument_list|(
name|loader
operator|.
name|getConfigDir
argument_list|()
argument_list|,
name|managedSchemaResourceName
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|parentDir
init|=
name|managedSchemaFile
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parentDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|parentDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Can't create managed schema directory "
operator|+
name|parentDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
specifier|final
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|managedSchemaFile
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Upgraded to managed schema at "
operator|+
name|managedSchemaFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Error persisting managed schema "
operator|+
name|managedSchemaFile
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|writer
argument_list|)
expr_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|sync
argument_list|(
name|managedSchemaFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Error syncing the managed schema file "
operator|+
name|managedSchemaFile
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Persists the managed schema to ZooKeeper using optimistic concurrency.    *<p/>    * If createOnly is true, success is when the schema is created or if it previously existed.    *<p/>    * If createOnly is false, success is when the schema is persisted - this will only happen    * if schemaZkVersion matches the version in ZooKeeper.    *     * @return true on success     */
DECL|method|persistManagedSchemaToZooKeeper
name|boolean
name|persistManagedSchemaToZooKeeper
parameter_list|(
name|boolean
name|createOnly
parameter_list|)
block|{
specifier|final
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
decl_stmt|;
specifier|final
name|ZkController
name|zkController
init|=
name|zkLoader
operator|.
name|getZkController
argument_list|()
decl_stmt|;
specifier|final
name|SolrZkClient
name|zkClient
init|=
name|zkController
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
specifier|final
name|String
name|managedSchemaPath
init|=
name|zkLoader
operator|.
name|getCollectionZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|managedSchemaResourceName
decl_stmt|;
name|boolean
name|success
init|=
literal|true
decl_stmt|;
name|boolean
name|schemaChangedInZk
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// Persist the managed schema
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|persist
argument_list|(
name|writer
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|writer
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
if|if
condition|(
name|createOnly
condition|)
block|{
try|try
block|{
name|zkClient
operator|.
name|create
argument_list|(
name|managedSchemaPath
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|schemaZkVersion
operator|=
literal|0
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created and persisted managed schema znode at "
operator|+
name|managedSchemaPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|e
parameter_list|)
block|{
comment|// This is okay - do nothing and fall through
name|log
operator|.
name|info
argument_list|(
literal|"Managed schema znode at "
operator|+
name|managedSchemaPath
operator|+
literal|" already exists - no need to create it"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
comment|// Assumption: the path exists
name|Stat
name|stat
init|=
name|zkClient
operator|.
name|setData
argument_list|(
name|managedSchemaPath
argument_list|,
name|data
argument_list|,
name|schemaZkVersion
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|schemaZkVersion
operator|=
name|stat
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Persisted managed schema at "
operator|+
name|managedSchemaPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|e
parameter_list|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|schemaChangedInZk
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// Restore the interrupted status
block|}
specifier|final
name|String
name|msg
init|=
literal|"Error persisting managed schema at "
operator|+
name|managedSchemaPath
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|schemaChangedInZk
condition|)
block|{
name|String
name|msg
init|=
literal|"Failed to persist managed schema at "
operator|+
name|managedSchemaPath
operator|+
literal|" - version mismatch"
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SchemaChangedInZkException
argument_list|(
name|ErrorCode
operator|.
name|CONFLICT
argument_list|,
name|msg
operator|+
literal|", retry."
argument_list|)
throw|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|ManagedIndexSchema
name|addField
parameter_list|(
name|SchemaField
name|newField
parameter_list|)
block|{
return|return
name|addFields
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|newField
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|ManagedIndexSchema
name|addField
parameter_list|(
name|SchemaField
name|newField
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|copyFieldNames
parameter_list|)
block|{
return|return
name|addFields
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|newField
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
argument_list|,
name|copyFieldNames
argument_list|)
argument_list|)
return|;
block|}
DECL|class|FieldExistsException
specifier|public
class|class
name|FieldExistsException
extends|extends
name|SolrException
block|{
DECL|method|FieldExistsException
specifier|public
name|FieldExistsException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SchemaChangedInZkException
specifier|public
class|class
name|SchemaChangedInZkException
extends|extends
name|SolrException
block|{
DECL|method|SchemaChangedInZkException
specifier|public
name|SchemaChangedInZkException
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addFields
specifier|public
name|ManagedIndexSchema
name|addFields
parameter_list|(
name|Collection
argument_list|<
name|SchemaField
argument_list|>
name|newFields
parameter_list|)
block|{
return|return
name|addFields
argument_list|(
name|newFields
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
operator|>
name|emptyMap
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addFields
specifier|public
name|ManagedIndexSchema
name|addFields
parameter_list|(
name|Collection
argument_list|<
name|SchemaField
argument_list|>
name|newFields
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|copyFieldNames
parameter_list|)
block|{
name|ManagedIndexSchema
name|newSchema
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isMutable
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|copyFieldNames
operator|==
literal|null
condition|)
block|{
name|copyFieldNames
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|newSchema
operator|=
name|shallowCopy
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|SchemaField
name|newField
range|:
name|newFields
control|)
block|{
if|if
condition|(
literal|null
operator|!=
name|newSchema
operator|.
name|getFieldOrNull
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Field '"
operator|+
name|newField
operator|.
name|getName
argument_list|()
operator|+
literal|"' already exists."
decl_stmt|;
throw|throw
operator|new
name|FieldExistsException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|)
throw|;
block|}
name|newSchema
operator|.
name|fields
operator|.
name|put
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
argument_list|,
name|newField
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|newField
operator|.
name|getDefaultValue
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
operator|+
literal|" contains default value: "
operator|+
name|newField
operator|.
name|getDefaultValue
argument_list|()
argument_list|)
expr_stmt|;
name|newSchema
operator|.
name|fieldsWithDefaultValue
operator|.
name|add
argument_list|(
name|newField
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newField
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"{} is required in this schema"
argument_list|,
name|newField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|newSchema
operator|.
name|requiredFields
operator|.
name|add
argument_list|(
name|newField
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|copyFields
init|=
name|copyFieldNames
operator|.
name|get
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|copyFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|copyField
range|:
name|copyFields
control|)
block|{
name|newSchema
operator|.
name|registerCopyField
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
argument_list|,
name|copyField
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Run the callbacks on SchemaAware now that everything else is done
for|for
control|(
name|SchemaAware
name|aware
range|:
name|newSchema
operator|.
name|schemaAware
control|)
block|{
name|aware
operator|.
name|inform
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
block|}
name|newSchema
operator|.
name|refreshAnalyzers
argument_list|()
expr_stmt|;
name|success
operator|=
name|newSchema
operator|.
name|persistManagedSchema
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't just create - update it if it already exists
if|if
condition|(
name|success
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Added field(s): {}"
argument_list|,
name|newFields
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to add field(s): {}"
argument_list|,
name|newFields
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"This ManagedIndexSchema is not mutable."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|)
throw|;
block|}
return|return
name|newSchema
return|;
block|}
annotation|@
name|Override
DECL|method|addCopyFields
specifier|public
name|ManagedIndexSchema
name|addCopyFields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|copyFields
parameter_list|)
block|{
name|ManagedIndexSchema
name|newSchema
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isMutable
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|newSchema
operator|=
name|shallowCopy
argument_list|(
literal|true
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
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|copyFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//Key is the name of the field, values are the destinations
for|for
control|(
name|String
name|destination
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|newSchema
operator|.
name|registerCopyField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO: move this common stuff out to shared methods
comment|// Run the callbacks on SchemaAware now that everything else is done
for|for
control|(
name|SchemaAware
name|aware
range|:
name|newSchema
operator|.
name|schemaAware
control|)
block|{
name|aware
operator|.
name|inform
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
block|}
name|newSchema
operator|.
name|refreshAnalyzers
argument_list|()
expr_stmt|;
name|success
operator|=
name|newSchema
operator|.
name|persistManagedSchema
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't just create - update it if it already exists
if|if
condition|(
name|success
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Added copy fields for {} sources"
argument_list|,
name|copyFields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to add copy fields for {} sources"
argument_list|,
name|copyFields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newSchema
return|;
block|}
annotation|@
name|Override
DECL|method|newField
specifier|public
name|SchemaField
name|newField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|fieldType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|options
parameter_list|)
block|{
name|SchemaField
name|sf
decl_stmt|;
if|if
condition|(
name|isMutable
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|-
literal|1
operator|!=
name|fieldName
operator|.
name|indexOf
argument_list|(
literal|'*'
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Can't add dynamic field '"
operator|+
name|fieldName
operator|+
literal|"'."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|)
throw|;
block|}
name|SchemaField
name|existingFieldWithTheSameName
init|=
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|existingFieldWithTheSameName
condition|)
block|{
name|String
name|msg
init|=
literal|"Field '"
operator|+
name|fieldName
operator|+
literal|"' already exists."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|)
throw|;
block|}
name|FieldType
name|type
init|=
name|getFieldTypeByName
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|type
condition|)
block|{
name|String
name|msg
init|=
literal|"Field '"
operator|+
name|fieldName
operator|+
literal|"': Field type '"
operator|+
name|fieldType
operator|+
literal|"' not found."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|)
throw|;
block|}
name|sf
operator|=
name|SchemaField
operator|.
name|create
argument_list|(
name|fieldName
argument_list|,
name|type
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
name|e
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"This ManagedIndexSchema is not mutable."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|msg
argument_list|)
throw|;
block|}
return|return
name|sf
return|;
block|}
comment|/**     * Called from ZkIndexSchemaReader to merge the fields from the serialized managed schema    * on ZooKeeper with the local managed schema.    *     * @param inputSource The serialized content of the managed schema from ZooKeeper    * @param schemaZkVersion The ZK version of the managed schema on ZooKeeper    * @return The new merged schema    */
DECL|method|reloadFields
name|ManagedIndexSchema
name|reloadFields
parameter_list|(
name|InputSource
name|inputSource
parameter_list|,
name|int
name|schemaZkVersion
parameter_list|)
block|{
name|ManagedIndexSchema
name|newSchema
decl_stmt|;
try|try
block|{
name|newSchema
operator|=
name|shallowCopy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Config
name|schemaConf
init|=
operator|new
name|Config
argument_list|(
name|loader
argument_list|,
name|SCHEMA
argument_list|,
name|inputSource
argument_list|,
name|SLASH
operator|+
name|SCHEMA
operator|+
name|SLASH
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
name|schemaConf
operator|.
name|getDocument
argument_list|()
decl_stmt|;
specifier|final
name|XPath
name|xpath
init|=
name|schemaConf
operator|.
name|getXPath
argument_list|()
decl_stmt|;
name|newSchema
operator|.
name|loadFields
argument_list|(
name|document
argument_list|,
name|xpath
argument_list|)
expr_stmt|;
comment|// let's completely rebuild the copy fields from the schema in ZK.
comment|// create new copyField-related objects so we don't affect the
comment|// old schema
name|newSchema
operator|.
name|copyFieldsMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|newSchema
operator|.
name|dynamicCopyFields
operator|=
literal|null
expr_stmt|;
name|newSchema
operator|.
name|copyFieldTargetCounts
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|newSchema
operator|.
name|loadCopyFields
argument_list|(
name|document
argument_list|,
name|xpath
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|uniqueKeyField
condition|)
block|{
name|newSchema
operator|.
name|requiredFields
operator|.
name|add
argument_list|(
name|uniqueKeyField
argument_list|)
expr_stmt|;
block|}
comment|//Run the callbacks on SchemaAware now that everything else is done
for|for
control|(
name|SchemaAware
name|aware
range|:
name|newSchema
operator|.
name|schemaAware
control|)
block|{
name|aware
operator|.
name|inform
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
block|}
name|newSchema
operator|.
name|refreshAnalyzers
argument_list|()
expr_stmt|;
name|newSchema
operator|.
name|schemaZkVersion
operator|=
name|schemaZkVersion
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
name|e
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Schema Parsing Failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|newSchema
return|;
block|}
DECL|method|ManagedIndexSchema
specifier|private
name|ManagedIndexSchema
parameter_list|(
specifier|final
name|SolrConfig
name|solrConfig
parameter_list|,
specifier|final
name|SolrResourceLoader
name|loader
parameter_list|,
name|boolean
name|isMutable
parameter_list|,
name|String
name|managedSchemaResourceName
parameter_list|,
name|int
name|schemaZkVersion
parameter_list|,
name|Object
name|schemaUpdateLock
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|super
argument_list|(
name|solrConfig
argument_list|,
name|loader
argument_list|)
expr_stmt|;
name|this
operator|.
name|isMutable
operator|=
name|isMutable
expr_stmt|;
name|this
operator|.
name|managedSchemaResourceName
operator|=
name|managedSchemaResourceName
expr_stmt|;
name|this
operator|.
name|schemaZkVersion
operator|=
name|schemaZkVersion
expr_stmt|;
name|this
operator|.
name|schemaUpdateLock
operator|=
name|schemaUpdateLock
expr_stmt|;
block|}
comment|/**    * Makes a shallow copy of this schema.    *     * Not copied: analyzers     *     * @param includeFieldDataStructures if true, fields, fieldsWithDefaultValue, and requiredFields    *                                   are copied; otherwise, they are not.    * @return A shallow copy of this schema    */
DECL|method|shallowCopy
specifier|private
name|ManagedIndexSchema
name|shallowCopy
parameter_list|(
name|boolean
name|includeFieldDataStructures
parameter_list|)
block|{
name|ManagedIndexSchema
name|newSchema
init|=
literal|null
decl_stmt|;
try|try
block|{
name|newSchema
operator|=
operator|new
name|ManagedIndexSchema
argument_list|(
name|solrConfig
argument_list|,
name|loader
argument_list|,
name|isMutable
argument_list|,
name|managedSchemaResourceName
argument_list|,
name|schemaZkVersion
argument_list|,
name|getSchemaUpdateLock
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Error instantiating ManagedIndexSchema"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
assert|assert
name|newSchema
operator|!=
literal|null
assert|;
name|newSchema
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|newSchema
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|newSchema
operator|.
name|defaultSearchFieldName
operator|=
name|defaultSearchFieldName
expr_stmt|;
name|newSchema
operator|.
name|queryParserDefaultOperator
operator|=
name|queryParserDefaultOperator
expr_stmt|;
name|newSchema
operator|.
name|isExplicitQueryParserDefaultOperator
operator|=
name|isExplicitQueryParserDefaultOperator
expr_stmt|;
name|newSchema
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
name|newSchema
operator|.
name|similarityFactory
operator|=
name|similarityFactory
expr_stmt|;
name|newSchema
operator|.
name|isExplicitSimilarity
operator|=
name|isExplicitSimilarity
expr_stmt|;
name|newSchema
operator|.
name|uniqueKeyField
operator|=
name|uniqueKeyField
expr_stmt|;
name|newSchema
operator|.
name|uniqueKeyFieldName
operator|=
name|uniqueKeyFieldName
expr_stmt|;
name|newSchema
operator|.
name|uniqueKeyFieldType
operator|=
name|uniqueKeyFieldType
expr_stmt|;
comment|// After the schema is persisted, resourceName is the same as managedSchemaResourceName
name|newSchema
operator|.
name|resourceName
operator|=
name|managedSchemaResourceName
expr_stmt|;
if|if
condition|(
name|includeFieldDataStructures
condition|)
block|{
comment|// These need new collections, since addFields() can add members to them
name|newSchema
operator|.
name|fields
operator|.
name|putAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|newSchema
operator|.
name|fieldsWithDefaultValue
operator|.
name|addAll
argument_list|(
name|fieldsWithDefaultValue
argument_list|)
expr_stmt|;
name|newSchema
operator|.
name|requiredFields
operator|.
name|addAll
argument_list|(
name|requiredFields
argument_list|)
expr_stmt|;
block|}
comment|// These don't need new collections - addFields() won't add members to them
name|newSchema
operator|.
name|fieldTypes
operator|=
name|fieldTypes
expr_stmt|;
name|newSchema
operator|.
name|dynamicFields
operator|=
name|dynamicFields
expr_stmt|;
name|newSchema
operator|.
name|dynamicCopyFields
operator|=
name|dynamicCopyFields
expr_stmt|;
name|newSchema
operator|.
name|copyFieldsMap
operator|=
name|copyFieldsMap
expr_stmt|;
name|newSchema
operator|.
name|copyFieldTargetCounts
operator|=
name|copyFieldTargetCounts
expr_stmt|;
name|newSchema
operator|.
name|schemaAware
operator|=
name|schemaAware
expr_stmt|;
return|return
name|newSchema
return|;
block|}
annotation|@
name|Override
DECL|method|getSchemaUpdateLock
specifier|public
name|Object
name|getSchemaUpdateLock
parameter_list|()
block|{
return|return
name|schemaUpdateLock
return|;
block|}
block|}
end_class
end_unit
