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
name|File
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
begin_comment
comment|/**  * Implements {@link LockFactory} using {@link File#createNewFile()}.  This is  * currently the default LockFactory used for {@link FSDirectory} if no  * LockFactory instance is otherwise provided.  *  * Note that there are known problems with this locking implementation on NFS.  *  * @see LockFactory  */
end_comment
begin_class
DECL|class|SimpleFSLockFactory
specifier|public
class|class
name|SimpleFSLockFactory
extends|extends
name|LockFactory
block|{
comment|/**    * Directory specified by<code>org.apache.lucene.lockDir</code>    * system property.  If that is not set, then<code>java.io.tmpdir</code>    * system property is used.    */
DECL|field|LOCK_DIR
specifier|public
specifier|static
specifier|final
name|String
name|LOCK_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"org.apache.lucene.lockDir"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|lockDir
specifier|private
name|File
name|lockDir
decl_stmt|;
comment|/**    * Instantiate using default LOCK_DIR:<code>org.apache.lucene.lockDir</code>    * system property, or (if that is null) then<code>java.io.tmpdir</code>.    */
DECL|method|SimpleFSLockFactory
specifier|public
name|SimpleFSLockFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|lockDir
operator|=
operator|new
name|File
argument_list|(
name|LOCK_DIR
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiate using the provided directory (as a File instance).    * @param lockDir where lock files should be created.    */
DECL|method|SimpleFSLockFactory
specifier|public
name|SimpleFSLockFactory
parameter_list|(
name|File
name|lockDir
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiate using the provided directory name (String).    * @param lockDirName where lock files should be created.    */
DECL|method|SimpleFSLockFactory
specifier|public
name|SimpleFSLockFactory
parameter_list|(
name|String
name|lockDirName
parameter_list|)
throws|throws
name|IOException
block|{
name|lockDir
operator|=
operator|new
name|File
argument_list|(
name|lockDirName
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|File
name|lockDir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|lockDir
operator|=
name|lockDir
expr_stmt|;
block|}
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
name|SimpleFSLock
argument_list|(
name|lockDir
argument_list|,
name|lockPrefix
operator|+
literal|"-"
operator|+
name|lockName
argument_list|)
return|;
block|}
DECL|method|clearAllLocks
specifier|public
name|void
name|clearAllLocks
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
index|[]
name|files
init|=
name|lockDir
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read lock directory "
operator|+
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
name|String
name|prefix
init|=
name|lockPrefix
operator|+
literal|"-"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
continue|continue;
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|lockDir
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lockFile
operator|.
name|delete
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot delete "
operator|+
name|lockFile
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
begin_empty_stmt
empty_stmt|;
end_empty_stmt
begin_class
DECL|class|SimpleFSLock
class|class
name|SimpleFSLock
extends|extends
name|Lock
block|{
DECL|field|lockFile
name|File
name|lockFile
decl_stmt|;
DECL|field|lockDir
name|File
name|lockDir
decl_stmt|;
DECL|method|SimpleFSLock
specifier|public
name|SimpleFSLock
parameter_list|(
name|File
name|lockDir
parameter_list|,
name|String
name|lockFileName
parameter_list|)
block|{
name|this
operator|.
name|lockDir
operator|=
name|lockDir
expr_stmt|;
name|lockFile
operator|=
operator|new
name|File
argument_list|(
name|lockDir
argument_list|,
name|lockFileName
argument_list|)
expr_stmt|;
block|}
DECL|method|obtain
specifier|public
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Ensure that lockDir exists and is a directory:
if|if
condition|(
operator|!
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|lockDir
operator|.
name|mkdirs
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create directory: "
operator|+
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|lockDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Found regular file where directory expected: "
operator|+
name|lockDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|lockFile
operator|.
name|createNewFile
argument_list|()
return|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
name|lockFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|lockFile
operator|.
name|exists
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SimpleFSLock@"
operator|+
name|lockFile
return|;
block|}
block|}
end_class
end_unit
