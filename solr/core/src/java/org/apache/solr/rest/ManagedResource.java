begin_unit
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
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
name|FileNotFoundException
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|LinkedHashMap
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|rest
operator|.
name|ManagedResourceStorage
operator|.
name|StorageIO
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|Status
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|Representation
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
begin_comment
comment|/**  * Supports Solr components that have external data that   * needs to be managed using the REST API.  */
end_comment
begin_class
DECL|class|ManagedResource
specifier|public
specifier|abstract
class|class
name|ManagedResource
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Marker interface to indicate a ManagedResource implementation class also supports    * managing child resources at path: /&lt;resource&gt;/{child}    */
DECL|interface|ChildResourceSupport
specifier|public
specifier|static
interface|interface
name|ChildResourceSupport
block|{}
DECL|field|INIT_ARGS_JSON_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|INIT_ARGS_JSON_FIELD
init|=
literal|"initArgs"
decl_stmt|;
DECL|field|MANAGED_JSON_LIST_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|MANAGED_JSON_LIST_FIELD
init|=
literal|"managedList"
decl_stmt|;
DECL|field|MANAGED_JSON_MAP_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|MANAGED_JSON_MAP_FIELD
init|=
literal|"managedMap"
decl_stmt|;
DECL|field|INITIALIZED_ON_JSON_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|INITIALIZED_ON_JSON_FIELD
init|=
literal|"initializedOn"
decl_stmt|;
DECL|field|UPDATED_SINCE_INIT_JSON_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|UPDATED_SINCE_INIT_JSON_FIELD
init|=
literal|"updatedSinceInit"
decl_stmt|;
DECL|field|resourceId
specifier|private
specifier|final
name|String
name|resourceId
decl_stmt|;
DECL|field|solrResourceLoader
specifier|protected
specifier|final
name|SolrResourceLoader
name|solrResourceLoader
decl_stmt|;
DECL|field|storage
specifier|protected
specifier|final
name|ManagedResourceStorage
name|storage
decl_stmt|;
DECL|field|managedInitArgs
specifier|protected
name|NamedList
argument_list|<
name|Object
argument_list|>
name|managedInitArgs
decl_stmt|;
DECL|field|initializedOn
specifier|protected
name|Date
name|initializedOn
decl_stmt|;
DECL|field|lastUpdateSinceInitialization
specifier|protected
name|Date
name|lastUpdateSinceInitialization
decl_stmt|;
comment|/**    * Initializes this managed resource, including setting up JSON-based storage using    * the provided storageIO implementation, such as ZK.    */
DECL|method|ManagedResource
specifier|protected
name|ManagedResource
parameter_list|(
name|String
name|resourceId
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|StorageIO
name|storageIO
parameter_list|)
throws|throws
name|SolrException
block|{
name|this
operator|.
name|resourceId
operator|=
name|resourceId
expr_stmt|;
name|this
operator|.
name|solrResourceLoader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|storage
operator|=
name|createStorage
argument_list|(
name|storageIO
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called once during core initialization to get the managed    * data loaded from storage and notify observers.    */
DECL|method|loadManagedDataAndNotify
specifier|public
name|void
name|loadManagedDataAndNotify
parameter_list|(
name|List
argument_list|<
name|ManagedResourceObserver
argument_list|>
name|observers
parameter_list|)
throws|throws
name|SolrException
block|{
comment|// load managed data from storage
name|reloadFromStorage
argument_list|()
expr_stmt|;
comment|// important!!! only affect the Solr component once during core initialization
comment|// also, as most analysis components will alter the initArgs it is processes them
comment|// we need to clone the managed initArgs
name|notifyObserversDuringInit
argument_list|(
name|managedInitArgs
argument_list|,
name|observers
argument_list|)
expr_stmt|;
comment|// some basic date tracking around when the data was initialized and updated
name|initializedOn
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|lastUpdateSinceInitialization
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Notifies all registered observers that the ManagedResource is initialized.    * This event only occurs once when the core is loaded. Thus, you need to    * reload the core to get updates applied to the analysis components that    * depend on the ManagedResource data.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|notifyObserversDuringInit
specifier|protected
name|void
name|notifyObserversDuringInit
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|args
parameter_list|,
name|List
argument_list|<
name|ManagedResourceObserver
argument_list|>
name|observers
parameter_list|)
throws|throws
name|SolrException
block|{
if|if
condition|(
name|observers
operator|==
literal|null
operator|||
name|observers
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
for|for
control|(
name|ManagedResourceObserver
name|observer
range|:
name|observers
control|)
block|{
comment|// clone the args for each observer as some components
comment|// remove args as they process them, e.g. AbstractAnalysisFactory
name|NamedList
argument_list|<
name|?
argument_list|>
name|clonedArgs
init|=
name|args
operator|.
name|clone
argument_list|()
decl_stmt|;
name|observer
operator|.
name|onManagedResourceInitialized
argument_list|(
name|clonedArgs
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Notified {} observers of {}"
argument_list|,
name|observers
operator|.
name|size
argument_list|()
argument_list|,
name|getResourceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Potential extension point allowing concrete implementations to supply their own storage    * implementation. The default implementation uses JSON as the storage format and delegates    * the loading and saving of JSON bytes to the supplied StorageIO class.     */
DECL|method|createStorage
specifier|protected
name|ManagedResourceStorage
name|createStorage
parameter_list|(
name|StorageIO
name|storageIO
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
throws|throws
name|SolrException
block|{
return|return
operator|new
name|ManagedResourceStorage
operator|.
name|JsonStorage
argument_list|(
name|storageIO
argument_list|,
name|loader
argument_list|)
return|;
block|}
comment|/**    * Returns the resource loader used by this resource.    */
DECL|method|getResourceLoader
specifier|public
name|SolrResourceLoader
name|getResourceLoader
parameter_list|()
block|{
return|return
name|solrResourceLoader
return|;
block|}
comment|/**    * Gets the resource ID for this managed resource.    */
DECL|method|getResourceId
specifier|public
name|String
name|getResourceId
parameter_list|()
block|{
return|return
name|resourceId
return|;
block|}
comment|/**    * Gets the ServerResource class to register this endpoint with the Rest API router;    * in most cases, the default RestManager.ManagedEndpoint class is sufficient but    * ManagedResource implementations can override this method if a different ServerResource    * class is needed.     */
DECL|method|getServerResourceClass
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|BaseSolrResource
argument_list|>
name|getServerResourceClass
parameter_list|()
block|{
return|return
name|RestManager
operator|.
name|ManagedEndpoint
operator|.
name|class
return|;
block|}
comment|/**    * Called from {@link #doPut(BaseSolrResource,Representation,Object)}    * to update this resource's init args using the given updatedArgs    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|updateInitArgs
specifier|protected
name|boolean
name|updateInitArgs
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|updatedArgs
parameter_list|)
block|{
if|if
condition|(
name|updatedArgs
operator|==
literal|null
operator|||
name|updatedArgs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|madeChanges
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|managedInitArgs
operator|.
name|equals
argument_list|(
name|updatedArgs
argument_list|)
condition|)
block|{
name|managedInitArgs
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|updatedArgs
operator|.
name|clone
argument_list|()
expr_stmt|;
name|madeChanges
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|madeChanges
return|;
block|}
comment|/**    * Invoked when this object determines it needs to reload the stored data.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|reloadFromStorage
specifier|protected
specifier|synchronized
name|void
name|reloadFromStorage
parameter_list|()
throws|throws
name|SolrException
block|{
name|String
name|resourceId
init|=
name|getResourceId
argument_list|()
decl_stmt|;
name|Object
name|data
init|=
literal|null
decl_stmt|;
try|try
block|{
name|data
operator|=
name|storage
operator|.
name|load
argument_list|(
name|resourceId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnf
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No stored data found for {}"
argument_list|,
name|resourceId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioExc
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
literal|"Failed to load stored data for "
operator|+
name|resourceId
operator|+
literal|" due to: "
operator|+
name|ioExc
argument_list|,
name|ioExc
argument_list|)
throw|;
block|}
name|Object
name|managedData
init|=
name|processStoredData
argument_list|(
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|managedInitArgs
operator|==
literal|null
condition|)
name|managedInitArgs
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|onManagedDataLoadedFromStorage
argument_list|(
name|managedInitArgs
argument_list|,
name|managedData
argument_list|)
expr_stmt|;
block|}
comment|/**    * Processes the stored data.    */
DECL|method|processStoredData
specifier|protected
name|Object
name|processStoredData
parameter_list|(
name|Object
name|data
parameter_list|)
throws|throws
name|SolrException
block|{
name|Object
name|managedData
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|data
operator|instanceof
name|Map
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Stored data for "
operator|+
name|resourceId
operator|+
literal|" is not a valid JSON object!"
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|initArgsMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|jsonMap
operator|.
name|get
argument_list|(
name|INIT_ARGS_JSON_FIELD
argument_list|)
decl_stmt|;
name|managedInitArgs
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|initArgsMap
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Loaded initArgs {} for {}"
argument_list|,
name|managedInitArgs
argument_list|,
name|resourceId
argument_list|)
expr_stmt|;
if|if
condition|(
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|MANAGED_JSON_LIST_FIELD
argument_list|)
condition|)
block|{
name|Object
name|jsonList
init|=
name|jsonMap
operator|.
name|get
argument_list|(
name|MANAGED_JSON_LIST_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|jsonList
operator|instanceof
name|List
operator|)
condition|)
block|{
name|String
name|errMsg
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected JSON array as value for %s but client sent a %s instead!"
argument_list|,
name|MANAGED_JSON_LIST_FIELD
argument_list|,
name|jsonList
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|errMsg
argument_list|)
throw|;
block|}
name|managedData
operator|=
name|jsonList
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|MANAGED_JSON_MAP_FIELD
argument_list|)
condition|)
block|{
name|Object
name|jsonObj
init|=
name|jsonMap
operator|.
name|get
argument_list|(
name|MANAGED_JSON_MAP_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|jsonObj
operator|instanceof
name|Map
operator|)
condition|)
block|{
name|String
name|errMsg
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected JSON map as value for %s but client sent a %s instead!"
argument_list|,
name|MANAGED_JSON_MAP_FIELD
argument_list|,
name|jsonObj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|errMsg
argument_list|)
throw|;
block|}
name|managedData
operator|=
name|jsonObj
expr_stmt|;
block|}
block|}
return|return
name|managedData
return|;
block|}
comment|/**    * Method called after data has been loaded from storage to give the concrete    * implementation a chance to post-process the data.    */
DECL|method|onManagedDataLoadedFromStorage
specifier|protected
specifier|abstract
name|void
name|onManagedDataLoadedFromStorage
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|managedInitArgs
parameter_list|,
name|Object
name|managedData
parameter_list|)
throws|throws
name|SolrException
function_decl|;
comment|/**    * Persists managed data to the configured storage IO as a JSON object.     */
DECL|method|storeManagedData
specifier|public
specifier|synchronized
name|void
name|storeManagedData
parameter_list|(
name|Object
name|managedData
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toStore
init|=
name|buildMapToStore
argument_list|(
name|managedData
argument_list|)
decl_stmt|;
name|String
name|resourceId
init|=
name|getResourceId
argument_list|()
decl_stmt|;
try|try
block|{
name|storage
operator|.
name|store
argument_list|(
name|resourceId
argument_list|,
name|toStore
argument_list|)
expr_stmt|;
comment|// keep track that the managed data has been updated
name|lastUpdateSinceInitialization
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|storeErr
parameter_list|)
block|{
comment|// store failed, so try to reset the state of this object by reloading
comment|// from storage and then failing the store request, but only do that
comment|// if we've successfully initialized before
if|if
condition|(
name|initializedOn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reloadFromStorage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|reloadExc
parameter_list|)
block|{
comment|// note: the data we're managing now remains in a dubious state
comment|// however the text analysis component remains unaffected
comment|// (at least until core reload)
name|log
operator|.
name|error
argument_list|(
literal|"Failed to load data from storage due to: "
operator|+
name|reloadExc
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|errMsg
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Failed to store data for %s due to: %s"
argument_list|,
name|resourceId
argument_list|,
name|storeErr
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|errMsg
argument_list|,
name|storeErr
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceException
argument_list|(
name|Status
operator|.
name|SERVER_ERROR_INTERNAL
argument_list|,
name|errMsg
argument_list|,
name|storeErr
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns this resource's initialization timestamp.    */
DECL|method|getInitializedOn
specifier|public
name|String
name|getInitializedOn
parameter_list|()
block|{
if|if
condition|(
name|initializedOn
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|StringBuilder
name|dateBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|DateUtil
operator|.
name|formatDate
argument_list|(
name|initializedOn
argument_list|,
literal|null
argument_list|,
name|dateBuf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore
block|}
return|return
name|dateBuf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns the timestamp of the most recent update,    * or null if this resource has not been updated since initialization.    */
DECL|method|getUpdatedSinceInitialization
specifier|public
name|String
name|getUpdatedSinceInitialization
parameter_list|()
block|{
name|String
name|dateStr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|lastUpdateSinceInitialization
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|dateBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|DateUtil
operator|.
name|formatDate
argument_list|(
name|lastUpdateSinceInitialization
argument_list|,
literal|null
argument_list|,
name|dateBuf
argument_list|)
expr_stmt|;
name|dateStr
operator|=
name|dateBuf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore here
block|}
block|}
return|return
name|dateStr
return|;
block|}
comment|/**    * Returns true if this resource has been changed since initialization.    */
DECL|method|hasChangesSinceInitialization
specifier|public
name|boolean
name|hasChangesSinceInitialization
parameter_list|()
block|{
return|return
operator|(
name|lastUpdateSinceInitialization
operator|!=
literal|null
operator|)
return|;
block|}
comment|/**    * Builds the JSON object to be stored, containing initArgs and managed data fields.     */
DECL|method|buildMapToStore
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|buildMapToStore
parameter_list|(
name|Object
name|managedData
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toStore
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
literal|4
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|toStore
operator|.
name|put
argument_list|(
name|INIT_ARGS_JSON_FIELD
argument_list|,
name|convertNamedListToMap
argument_list|(
name|managedInitArgs
argument_list|)
argument_list|)
expr_stmt|;
comment|// report important dates when data was init'd / updated
name|String
name|initializedOnStr
init|=
name|getInitializedOn
argument_list|()
decl_stmt|;
if|if
condition|(
name|initializedOnStr
operator|!=
literal|null
condition|)
block|{
name|toStore
operator|.
name|put
argument_list|(
name|INITIALIZED_ON_JSON_FIELD
argument_list|,
name|initializedOnStr
argument_list|)
expr_stmt|;
block|}
comment|// if the managed data has been updated since initialization (ie. it's dirty)
comment|// return that in the response as well ... which gives a good hint that the
comment|// client needs to re-load the collection / core to apply the updates
if|if
condition|(
name|hasChangesSinceInitialization
argument_list|()
condition|)
block|{
name|toStore
operator|.
name|put
argument_list|(
name|UPDATED_SINCE_INIT_JSON_FIELD
argument_list|,
name|getUpdatedSinceInitialization
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|managedData
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|managedData
operator|instanceof
name|List
operator|||
name|managedData
operator|instanceof
name|Set
condition|)
block|{
name|toStore
operator|.
name|put
argument_list|(
name|MANAGED_JSON_LIST_FIELD
argument_list|,
name|managedData
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|managedData
operator|instanceof
name|Map
condition|)
block|{
name|toStore
operator|.
name|put
argument_list|(
name|MANAGED_JSON_MAP_FIELD
argument_list|,
name|managedData
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid managed data type "
operator|+
name|managedData
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"! Only List, Set, or Map objects are supported by this ManagedResource!"
argument_list|)
throw|;
block|}
block|}
return|return
name|toStore
return|;
block|}
comment|/**    * Converts a NamedList&lt;?&gt; into an ordered Map for returning as JSON.    */
DECL|method|convertNamedListToMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertNamedListToMap
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|args
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|argsMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
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
name|?
argument_list|>
name|entry
range|:
name|args
control|)
block|{
name|argsMap
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
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|argsMap
return|;
block|}
comment|/**    * Just calls {@link #doPut(BaseSolrResource,Representation,Object)};    * override to change the behavior of POST handling.    */
DECL|method|doPost
specifier|public
name|void
name|doPost
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|Representation
name|entity
parameter_list|,
name|Object
name|json
parameter_list|)
block|{
name|doPut
argument_list|(
name|endpoint
argument_list|,
name|entity
argument_list|,
name|json
argument_list|)
expr_stmt|;
block|}
comment|/**    * Applies changes to initArgs or managed data.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doPut
specifier|public
specifier|synchronized
name|void
name|doPut
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|Representation
name|entity
parameter_list|,
name|Object
name|json
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Processing update to {}: {} is a "
operator|+
name|json
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|getResourceId
argument_list|()
argument_list|,
name|json
argument_list|)
expr_stmt|;
name|boolean
name|updatedInitArgs
init|=
literal|false
decl_stmt|;
name|Object
name|managedData
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|json
operator|instanceof
name|Map
condition|)
block|{
comment|// hmmmm ... not sure how flexible we want to be here?
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|json
decl_stmt|;
if|if
condition|(
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|INIT_ARGS_JSON_FIELD
argument_list|)
operator|||
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|MANAGED_JSON_LIST_FIELD
argument_list|)
operator|||
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|MANAGED_JSON_MAP_FIELD
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|initArgsMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|jsonMap
operator|.
name|get
argument_list|(
name|INIT_ARGS_JSON_FIELD
argument_list|)
decl_stmt|;
name|updatedInitArgs
operator|=
name|updateInitArgs
argument_list|(
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|initArgsMap
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|MANAGED_JSON_LIST_FIELD
argument_list|)
condition|)
block|{
name|managedData
operator|=
name|jsonMap
operator|.
name|get
argument_list|(
name|MANAGED_JSON_LIST_FIELD
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jsonMap
operator|.
name|containsKey
argument_list|(
name|MANAGED_JSON_MAP_FIELD
argument_list|)
condition|)
block|{
name|managedData
operator|=
name|jsonMap
operator|.
name|get
argument_list|(
name|MANAGED_JSON_MAP_FIELD
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|managedData
operator|=
name|jsonMap
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|json
operator|instanceof
name|List
condition|)
block|{
name|managedData
operator|=
name|json
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|Status
operator|.
name|CLIENT_ERROR_BAD_REQUEST
argument_list|,
literal|"Unsupported update format "
operator|+
name|json
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|Object
name|updated
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|managedData
operator|!=
literal|null
condition|)
block|{
name|updated
operator|=
name|applyUpdatesToManagedData
argument_list|(
name|managedData
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|updatedInitArgs
operator|||
name|updated
operator|!=
literal|null
condition|)
block|{
name|storeManagedData
argument_list|(
name|updated
argument_list|)
expr_stmt|;
block|}
comment|// PUT just returns success status code with an empty body
block|}
comment|/**    * Called by the RestManager framework after this resource has been deleted    * to allow this resource to close and clean-up any resources used by this.    *     * @throws IOException if an error occurs in the underlying storage when    * trying to delete    */
DECL|method|onResourceDeleted
specifier|public
name|void
name|onResourceDeleted
parameter_list|()
throws|throws
name|IOException
block|{
name|storage
operator|.
name|delete
argument_list|(
name|resourceId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called during PUT/POST processing to apply updates to the managed data passed from the client.    */
DECL|method|applyUpdatesToManagedData
specifier|protected
specifier|abstract
name|Object
name|applyUpdatesToManagedData
parameter_list|(
name|Object
name|updates
parameter_list|)
function_decl|;
comment|/**    * Called by {@link RestManager.ManagedEndpoint#delete()}    * to delete a named part (the given childId) of the    * resource at the given endpoint    */
DECL|method|doDeleteChild
specifier|public
specifier|abstract
name|void
name|doDeleteChild
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
function_decl|;
comment|/**    * Called by {@link RestManager.ManagedEndpoint#get()}    * to retrieve a named part (the given childId) of the    * resource at the given endpoint    */
DECL|method|doGet
specifier|public
specifier|abstract
name|void
name|doGet
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
function_decl|;
block|}
end_class
end_unit
