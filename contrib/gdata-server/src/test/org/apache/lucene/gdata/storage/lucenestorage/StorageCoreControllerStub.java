begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
package|;
end_package
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
name|security
operator|.
name|NoSuchAlgorithmException
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|Component
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
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
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
name|gdata
operator|.
name|storage
operator|.
name|IDGenerator
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
name|gdata
operator|.
name|storage
operator|.
name|Storage
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
name|gdata
operator|.
name|storage
operator|.
name|StorageException
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
name|gdata
operator|.
name|utils
operator|.
name|ReferenceCounter
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
name|index
operator|.
name|IndexModifier
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
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment
begin_class
annotation|@
name|Component
argument_list|(
name|componentType
operator|=
name|ComponentType
operator|.
name|STORAGECONTROLLER
argument_list|)
DECL|class|StorageCoreControllerStub
specifier|public
class|class
name|StorageCoreControllerStub
extends|extends
name|StorageCoreController
block|{
DECL|field|idGenerator
specifier|private
specifier|final
name|IDGenerator
name|idGenerator
decl_stmt|;
DECL|method|StorageCoreControllerStub
specifier|public
name|StorageCoreControllerStub
parameter_list|()
throws|throws
name|IOException
throws|,
name|StorageException
block|{
try|try
block|{
name|this
operator|.
name|idGenerator
operator|=
operator|new
name|IDGenerator
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#createIndexModifier()      */
annotation|@
name|Override
DECL|method|createIndexModifier
specifier|protected
name|IndexModifier
name|createIndexModifier
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#forceWrite()      */
annotation|@
name|Override
DECL|method|forceWrite
specifier|public
name|void
name|forceWrite
parameter_list|()
throws|throws
name|IOException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getDirectory()      */
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getBufferSize()      */
annotation|@
name|Override
DECL|method|getBufferSize
specifier|public
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getStorageModifier()      */
annotation|@
name|Override
DECL|method|getStorageModifier
specifier|protected
name|StorageModifier
name|getStorageModifier
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|StorageModifierStub
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getPersistFactor()      */
annotation|@
name|Override
DECL|method|getPersistFactor
specifier|public
name|int
name|getPersistFactor
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getStorageQuery()      */
annotation|@
name|Override
DECL|method|getStorageQuery
specifier|protected
name|ReferenceCounter
argument_list|<
name|StorageQuery
argument_list|>
name|getStorageQuery
parameter_list|()
block|{
name|ReferenceCounter
argument_list|<
name|StorageQuery
argument_list|>
name|retVal
init|=
operator|new
name|ReferenceCounter
argument_list|<
name|StorageQuery
argument_list|>
argument_list|(
operator|new
name|StorageQueryStub
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|close
parameter_list|()
block|{
comment|//
block|}
block|}
decl_stmt|;
name|retVal
operator|.
name|increamentReference
argument_list|()
expr_stmt|;
name|retVal
operator|.
name|increamentReference
argument_list|()
expr_stmt|;
return|return
name|retVal
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#registerNewStorageQuery()      */
annotation|@
name|Override
DECL|method|registerNewStorageQuery
specifier|protected
name|void
name|registerNewStorageQuery
parameter_list|()
throws|throws
name|IOException
block|{                      }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#releaseId()      */
annotation|@
name|Override
DECL|method|releaseId
specifier|public
specifier|synchronized
name|String
name|releaseId
parameter_list|()
throws|throws
name|StorageException
block|{
try|try
block|{
return|return
name|this
operator|.
name|idGenerator
operator|.
name|getUID
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#releaseNewStorageBuffer()      */
annotation|@
name|Override
DECL|method|releaseNewStorageBuffer
specifier|protected
name|StorageBuffer
name|releaseNewStorageBuffer
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#setBufferSize(int)      */
annotation|@
name|Override
DECL|method|setBufferSize
specifier|public
name|void
name|setBufferSize
parameter_list|(
name|int
name|storageBufferSize
parameter_list|)
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#setPersistFactor(int)      */
annotation|@
name|Override
DECL|method|setPersistFactor
specifier|public
name|void
name|setPersistFactor
parameter_list|(
name|int
name|storagePersistFactor
parameter_list|)
block|{                      }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#destroy()      */
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|this
operator|.
name|idGenerator
operator|.
name|stopIDGenerator
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#getStorage()      */
annotation|@
name|Override
DECL|method|getStorage
specifier|public
name|Storage
name|getStorage
parameter_list|()
throws|throws
name|StorageException
block|{
return|return
operator|new
name|StorageImplementation
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageCoreController#initialize()      */
annotation|@
name|Override
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{
comment|//        this.setStorageDir(new RAMDirectory());
comment|//        super.initialize();
block|}
block|}
end_class
end_unit
