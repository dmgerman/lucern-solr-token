begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Used by MockDirectoryWrapper to wrap another factory  * and track open locks.  */
end_comment
begin_class
DECL|class|MockLockFactoryWrapper
specifier|public
class|class
name|MockLockFactoryWrapper
extends|extends
name|LockFactory
block|{
DECL|field|dir
name|MockDirectoryWrapper
name|dir
decl_stmt|;
DECL|field|delegate
name|LockFactory
name|delegate
decl_stmt|;
DECL|method|MockLockFactoryWrapper
specifier|public
name|MockLockFactoryWrapper
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|,
name|LockFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLockPrefix
specifier|public
name|void
name|setLockPrefix
parameter_list|(
name|String
name|lockPrefix
parameter_list|)
block|{
name|delegate
operator|.
name|setLockPrefix
argument_list|(
name|lockPrefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLockPrefix
specifier|public
name|String
name|getLockPrefix
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getLockPrefix
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|makeLock
specifier|public
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
return|return
operator|new
name|MockLock
argument_list|(
name|delegate
operator|.
name|makeLock
argument_list|(
name|lockName
argument_list|)
argument_list|,
name|lockName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clearLock
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|clearLock
argument_list|(
name|lockName
argument_list|)
expr_stmt|;
name|dir
operator|.
name|openLocks
operator|.
name|remove
argument_list|(
name|lockName
argument_list|)
expr_stmt|;
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
literal|"MockLockFactoryWrapper("
operator|+
name|delegate
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|class|MockLock
specifier|private
class|class
name|MockLock
extends|extends
name|Lock
block|{
DECL|field|delegateLock
specifier|private
name|Lock
name|delegateLock
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|MockLock
name|MockLock
parameter_list|(
name|Lock
name|delegate
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|delegateLock
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|delegateLock
operator|.
name|obtain
argument_list|()
condition|)
block|{
name|dir
operator|.
name|openLocks
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
name|delegateLock
operator|.
name|release
argument_list|()
expr_stmt|;
name|dir
operator|.
name|openLocks
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateLock
operator|.
name|isLocked
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
