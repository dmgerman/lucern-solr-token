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
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|FileNotFoundException
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
name|OutputStream
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
name|Reader
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
name|Charset
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
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
comment|/**  * Abstract base class that provides most of the functionality needed  * to store arbitrary data for managed resources. Concrete implementations  * need to decide the underlying format that data is stored in, such as JSON.  *   * The underlying storage I/O layer will be determined by the environment  * Solr is running in, e.g. in cloud mode, data will be stored and loaded  * from ZooKeeper.  */
end_comment
begin_class
DECL|class|ManagedResourceStorage
specifier|public
specifier|abstract
class|class
name|ManagedResourceStorage
block|{
comment|/**    * Hides the underlying storage implementation for data being managed    * by a ManagedResource. For instance, a ManagedResource may use JSON as    * the data format and an instance of this class to persist and load     * the JSON bytes to/from some backing store, such as ZooKeeper.    */
DECL|interface|StorageIO
specifier|public
specifier|static
interface|interface
name|StorageIO
block|{
DECL|method|getInfo
name|String
name|getInfo
parameter_list|()
function_decl|;
DECL|method|configure
name|void
name|configure
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
parameter_list|)
throws|throws
name|SolrException
function_decl|;
DECL|method|exists
name|boolean
name|exists
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|openInputStream
name|InputStream
name|openInputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|openOutputStream
name|OutputStream
name|openOutputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|delete
name|boolean
name|delete
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|field|STORAGE_IO_CLASS_INIT_ARG
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_IO_CLASS_INIT_ARG
init|=
literal|"storageIO"
decl_stmt|;
DECL|field|STORAGE_DIR_INIT_ARG
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_DIR_INIT_ARG
init|=
literal|"storageDir"
decl_stmt|;
comment|/**    * Creates a new StorageIO instance for a Solr core, taking into account    * whether the core is running in cloud mode as well as initArgs.     */
DECL|method|newStorageIO
specifier|public
specifier|static
name|StorageIO
name|newStorageIO
parameter_list|(
name|String
name|collection
parameter_list|,
name|SolrResourceLoader
name|resourceLoader
parameter_list|,
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
parameter_list|)
block|{
name|StorageIO
name|storageIO
init|=
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
name|String
name|zkConfigName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourceLoader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|zkClient
operator|=
operator|(
operator|(
name|ZkSolrResourceLoader
operator|)
name|resourceLoader
operator|)
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
expr_stmt|;
try|try
block|{
name|zkConfigName
operator|=
operator|(
operator|(
name|ZkSolrResourceLoader
operator|)
name|resourceLoader
operator|)
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|readConfigName
argument_list|(
name|collection
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
name|error
argument_list|(
literal|"Failed to get config name for collection {} due to: {}"
argument_list|,
name|collection
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zkConfigName
operator|==
literal|null
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
literal|"Could not find config name for collection:"
operator|+
name|collection
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|initArgs
operator|.
name|get
argument_list|(
name|STORAGE_IO_CLASS_INIT_ARG
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|storageIO
operator|=
name|resourceLoader
operator|.
name|newInstance
argument_list|(
name|initArgs
operator|.
name|get
argument_list|(
name|STORAGE_IO_CLASS_INIT_ARG
argument_list|)
argument_list|,
name|StorageIO
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|String
name|znodeBase
init|=
literal|"/configs/"
operator|+
name|zkConfigName
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Setting up ZooKeeper-based storage for the RestManager with znodeBase: "
operator|+
name|znodeBase
argument_list|)
expr_stmt|;
name|storageIO
operator|=
operator|new
name|ManagedResourceStorage
operator|.
name|ZooKeeperStorageIO
argument_list|(
name|zkClient
argument_list|,
name|znodeBase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|storageIO
operator|=
operator|new
name|FileStorageIO
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|storageIO
operator|instanceof
name|FileStorageIO
condition|)
block|{
comment|// using local fs, if storageDir is not set in the solrconfig.xml, assume the configDir for the core
if|if
condition|(
name|initArgs
operator|.
name|get
argument_list|(
name|STORAGE_DIR_INIT_ARG
argument_list|)
operator|==
literal|null
condition|)
block|{
name|initArgs
operator|.
name|add
argument_list|(
name|STORAGE_DIR_INIT_ARG
argument_list|,
name|resourceLoader
operator|.
name|getConfigDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|storageIO
operator|.
name|configure
argument_list|(
name|resourceLoader
argument_list|,
name|initArgs
argument_list|)
expr_stmt|;
return|return
name|storageIO
return|;
block|}
comment|/**    * Local file-based storage implementation.    */
DECL|class|FileStorageIO
specifier|public
specifier|static
class|class
name|FileStorageIO
implements|implements
name|StorageIO
block|{
DECL|field|storageDir
specifier|private
name|String
name|storageDir
decl_stmt|;
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
parameter_list|)
throws|throws
name|SolrException
block|{
name|String
name|storageDirArg
init|=
name|initArgs
operator|.
name|get
argument_list|(
name|STORAGE_DIR_INIT_ARG
argument_list|)
decl_stmt|;
if|if
condition|(
name|storageDirArg
operator|==
literal|null
operator|||
name|storageDirArg
operator|.
name|trim
argument_list|()
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
literal|"Required configuration parameter '"
operator|+
name|STORAGE_DIR_INIT_ARG
operator|+
literal|"' not provided!"
argument_list|)
throw|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|storageDirArg
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|storageDir
operator|=
name|dir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"File-based storage initialized to use dir: "
operator|+
name|storageDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|new
name|File
argument_list|(
name|storageDir
argument_list|,
name|storedResourceId
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|openInputStream
specifier|public
name|InputStream
name|openInputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|storageDir
operator|+
literal|"/"
operator|+
name|storedResourceId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openOutputStream
specifier|public
name|OutputStream
name|openOutputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FileOutputStream
argument_list|(
name|storageDir
operator|+
literal|"/"
operator|+
name|storedResourceId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|boolean
name|delete
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|storedFile
init|=
operator|new
name|File
argument_list|(
name|storageDir
argument_list|,
name|storedResourceId
argument_list|)
decl_stmt|;
return|return
name|storedFile
operator|.
name|isFile
argument_list|()
condition|?
name|storedFile
operator|.
name|delete
argument_list|()
else|:
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getInfo
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
literal|"file:dir="
operator|+
name|storageDir
return|;
block|}
block|}
comment|// end FileStorageIO
comment|/**    * ZooKeeper based storage implementation that uses the SolrZkClient provided    * by the CoreContainer.    */
DECL|class|ZooKeeperStorageIO
specifier|public
specifier|static
class|class
name|ZooKeeperStorageIO
implements|implements
name|StorageIO
block|{
DECL|field|zkClient
specifier|protected
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|znodeBase
specifier|protected
name|String
name|znodeBase
decl_stmt|;
DECL|field|retryOnConnLoss
specifier|protected
name|boolean
name|retryOnConnLoss
init|=
literal|true
decl_stmt|;
DECL|method|ZooKeeperStorageIO
specifier|public
name|ZooKeeperStorageIO
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|znodeBase
parameter_list|)
block|{
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
name|this
operator|.
name|znodeBase
operator|=
name|znodeBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
parameter_list|)
throws|throws
name|SolrException
block|{
comment|// validate connectivity and the configured znode base
try|try
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
name|znodeBase
argument_list|,
name|retryOnConnLoss
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|znodeBase
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
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
literal|"Failed to verify znode at %s due to: %s"
argument_list|,
name|znodeBase
argument_list|,
name|exc
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
name|exc
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
name|errMsg
argument_list|,
name|exc
argument_list|)
throw|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Configured ZooKeeperStorageIO with znodeBase: "
operator|+
name|znodeBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|znodePath
init|=
name|getZnodeForResource
argument_list|(
name|storedResourceId
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
name|retryOnConnLoss
argument_list|)
return|;
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
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read data at "
operator|+
name|znodePath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|openInputStream
specifier|public
name|InputStream
name|openInputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|znodePath
init|=
name|getZnodeForResource
argument_list|(
name|storedResourceId
argument_list|)
decl_stmt|;
name|byte
index|[]
name|znodeData
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
name|retryOnConnLoss
argument_list|)
condition|)
block|{
name|znodeData
operator|=
name|zkClient
operator|.
name|getData
argument_list|(
name|znodePath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
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
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read data at "
operator|+
name|znodePath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|znodeData
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Read {} bytes from znode {}"
argument_list|,
name|znodeData
operator|.
name|length
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|znodeData
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"No data found for znode {}"
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|znodeData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openOutputStream
specifier|public
name|OutputStream
name|openOutputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|znodePath
init|=
name|getZnodeForResource
argument_list|(
name|storedResourceId
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|retryOnConnLoss
init|=
name|this
operator|.
name|retryOnConnLoss
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|byte
index|[]
name|znodeData
init|=
name|toByteArray
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
name|retryOnConnLoss
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|setData
argument_list|(
name|znodePath
argument_list|,
name|znodeData
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Wrote {} bytes to existing znode {}"
argument_list|,
name|znodeData
operator|.
name|length
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|znodePath
argument_list|,
name|znodeData
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Wrote {} bytes to new znode {}"
argument_list|,
name|znodeData
operator|.
name|length
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// have to throw a runtimer here as we're in close,
comment|// which doesn't throw IOException
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|Status
operator|.
name|SERVER_ERROR_INTERNAL
argument_list|,
literal|"Failed to save data to ZooKeeper znode: "
operator|+
name|znodePath
operator|+
literal|" due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
return|return
name|baos
return|;
block|}
comment|/**      * Returns the Znode for the given storedResourceId by combining it      * with the znode base.      */
DECL|method|getZnodeForResource
specifier|protected
name|String
name|getZnodeForResource
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s/%s"
argument_list|,
name|znodeBase
argument_list|,
name|storedResourceId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|boolean
name|delete
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|wasDeleted
init|=
literal|false
decl_stmt|;
specifier|final
name|String
name|znodePath
init|=
name|getZnodeForResource
argument_list|(
name|storedResourceId
argument_list|)
decl_stmt|;
comment|// this might be overkill for a delete operation
try|try
block|{
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
name|retryOnConnLoss
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Attempting to delete znode {}"
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|delete
argument_list|(
name|znodePath
argument_list|,
operator|-
literal|1
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
name|wasDeleted
operator|=
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
name|retryOnConnLoss
argument_list|)
expr_stmt|;
if|if
condition|(
name|wasDeleted
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleted znode {}"
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to delete znode {}"
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Znode {} does not exist; delete operation ignored."
argument_list|,
name|znodePath
argument_list|)
expr_stmt|;
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
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read data at "
operator|+
name|znodePath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|wasDeleted
return|;
block|}
annotation|@
name|Override
DECL|method|getInfo
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
literal|"ZooKeeperStorageIO:path="
operator|+
name|znodeBase
return|;
block|}
block|}
comment|// end ZooKeeperStorageIO
comment|/**    * Memory-backed storage IO; not really intended for storage large amounts    * of data in production, but useful for testing and other transient workloads.    */
DECL|class|InMemoryStorageIO
specifier|public
specifier|static
class|class
name|InMemoryStorageIO
implements|implements
name|StorageIO
block|{
DECL|field|storage
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|storage
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|NamedList
argument_list|<
name|String
argument_list|>
name|initArgs
parameter_list|)
throws|throws
name|SolrException
block|{}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|storage
operator|.
name|containsKey
argument_list|(
name|storedResourceId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openInputStream
specifier|public
name|InputStream
name|openInputStream
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|storedVal
init|=
name|storage
operator|.
name|get
argument_list|(
name|storedResourceId
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedVal
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
name|storedResourceId
argument_list|)
throw|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|storedVal
operator|.
name|bytes
argument_list|,
name|storedVal
operator|.
name|offset
argument_list|,
name|storedVal
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openOutputStream
specifier|public
name|OutputStream
name|openOutputStream
parameter_list|(
specifier|final
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|boas
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|storage
operator|.
name|put
argument_list|(
name|storedResourceId
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
name|boas
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|boolean
name|delete
parameter_list|(
name|String
name|storedResourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|storage
operator|.
name|remove
argument_list|(
name|storedResourceId
argument_list|)
operator|!=
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInfo
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
literal|"InMemoryStorage"
return|;
block|}
block|}
comment|// end InMemoryStorageIO class
comment|/**    * Default storage implementation that uses JSON as the storage format for managed data.    */
DECL|class|JsonStorage
specifier|public
specifier|static
class|class
name|JsonStorage
extends|extends
name|ManagedResourceStorage
block|{
DECL|method|JsonStorage
specifier|public
name|JsonStorage
parameter_list|(
name|StorageIO
name|storageIO
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|super
argument_list|(
name|storageIO
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
comment|/**      * Determines the relative path (from the storage root) for the given resource.      * In this case, it returns a file named with the .json extension.      */
annotation|@
name|Override
DECL|method|getStoredResourceId
specifier|public
name|String
name|getStoredResourceId
parameter_list|(
name|String
name|resourceId
parameter_list|)
block|{
return|return
name|resourceId
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'_'
argument_list|)
operator|+
literal|".json"
return|;
block|}
annotation|@
name|Override
DECL|method|parseText
specifier|protected
name|Object
name|parseText
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|resourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|store
specifier|public
name|void
name|store
parameter_list|(
name|String
name|resourceId
parameter_list|,
name|Object
name|toStore
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|json
init|=
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|toStore
argument_list|)
decl_stmt|;
name|String
name|storedResourceId
init|=
name|getStoredResourceId
argument_list|(
name|resourceId
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|storageIO
operator|.
name|openOutputStream
argument_list|(
name|storedResourceId
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Saved JSON object to path {} using {}"
argument_list|,
name|storedResourceId
argument_list|,
name|storageIO
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// end JsonStorage
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
name|ManagedResourceStorage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UTF_8
specifier|public
specifier|static
specifier|final
name|Charset
name|UTF_8
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
DECL|field|storageIO
specifier|protected
name|StorageIO
name|storageIO
decl_stmt|;
DECL|field|loader
specifier|protected
name|SolrResourceLoader
name|loader
decl_stmt|;
DECL|method|ManagedResourceStorage
specifier|protected
name|ManagedResourceStorage
parameter_list|(
name|StorageIO
name|storageIO
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|storageIO
operator|=
name|storageIO
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
comment|/** Returns the resource loader used by this storage instance */
DECL|method|getResourceLoader
specifier|public
name|SolrResourceLoader
name|getResourceLoader
parameter_list|()
block|{
return|return
name|loader
return|;
block|}
comment|/** Returns the storageIO instance used by this storage instance */
DECL|method|getStorageIO
specifier|public
name|StorageIO
name|getStorageIO
parameter_list|()
block|{
return|return
name|storageIO
return|;
block|}
comment|/**    * Gets the unique identifier for a stored resource, typically based    * on the resourceId and some storage-specific information, such as    * file extension and storage root directory.    */
DECL|method|getStoredResourceId
specifier|public
specifier|abstract
name|String
name|getStoredResourceId
parameter_list|(
name|String
name|resourceId
parameter_list|)
function_decl|;
comment|/**    * Loads a resource from storage; the default implementation makes    * the assumption that the data is stored as UTF-8 encoded text,     * such as JSON. This method should be overridden if that assumption    * is invalid.     */
DECL|method|load
specifier|public
name|Object
name|load
parameter_list|(
name|String
name|resourceId
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|storedResourceId
init|=
name|getStoredResourceId
argument_list|(
name|resourceId
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Reading {} using {}"
argument_list|,
name|storedResourceId
argument_list|,
name|storageIO
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|inputStream
init|=
name|storageIO
operator|.
name|openInputStream
argument_list|(
name|storedResourceId
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputStream
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Object
name|parsed
init|=
literal|null
decl_stmt|;
name|InputStreamReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
name|parsed
operator|=
name|parseText
argument_list|(
name|reader
argument_list|,
name|resourceId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
block|}
name|String
name|objectType
init|=
operator|(
name|parsed
operator|!=
literal|null
operator|)
condition|?
name|parsed
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
else|:
literal|"null"
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Loaded %s at path %s using %s"
argument_list|,
name|objectType
argument_list|,
name|storedResourceId
argument_list|,
name|storageIO
operator|.
name|getInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|parsed
return|;
block|}
comment|/**    * Called by {@link ManagedResourceStorage#load(String)} to convert the    * serialized resource into its in-memory representation.    */
DECL|method|parseText
specifier|protected
name|Object
name|parseText
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|resourceId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op: base classes should override this if they deal with text.
return|return
literal|null
return|;
block|}
comment|/** Persists the given toStore object with the given resourceId. */
DECL|method|store
specifier|public
specifier|abstract
name|void
name|store
parameter_list|(
name|String
name|resourceId
parameter_list|,
name|Object
name|toStore
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Removes the given resourceId's persisted representation. */
DECL|method|delete
specifier|public
name|boolean
name|delete
parameter_list|(
name|String
name|resourceId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|storageIO
operator|.
name|delete
argument_list|(
name|getStoredResourceId
argument_list|(
name|resourceId
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
