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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|IOUtils
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
name|TestUtil
import|;
end_import
begin_comment
comment|/** Simple tests for NativeFSLockFactory */
end_comment
begin_class
DECL|class|TestNativeFSLockFactory
specifier|public
class|class
name|TestNativeFSLockFactory
extends|extends
name|BaseLockFactoryTestCase
block|{
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|newFSDirectory
argument_list|(
name|path
argument_list|,
name|NativeFSLockFactory
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/** Verify NativeFSLockFactory works correctly if the lock file exists */
DECL|method|testLockFileExists
specifier|public
name|void
name|testLockFileExists
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|Path
name|lockFile
init|=
name|tempDir
operator|.
name|resolve
argument_list|(
literal|"test.lock"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|lockFile
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|tempDir
argument_list|)
decl_stmt|;
name|Lock
name|l
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"test.lock"
argument_list|)
decl_stmt|;
name|l
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** release the lock and test ensureValid fails */
DECL|method|testInvalidateLock
specifier|public
name|void
name|testInvalidateLock
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|NativeFSLockFactory
operator|.
name|NativeFSLock
name|lock
init|=
operator|(
name|NativeFSLockFactory
operator|.
name|NativeFSLock
operator|)
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"test.lock"
argument_list|)
decl_stmt|;
name|lock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|lock
operator|.
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"no exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|expected
parameter_list|)
block|{
comment|// ok
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|lock
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** close the channel and test ensureValid fails */
DECL|method|testInvalidateChannel
specifier|public
name|void
name|testInvalidateChannel
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|NativeFSLockFactory
operator|.
name|NativeFSLock
name|lock
init|=
operator|(
name|NativeFSLockFactory
operator|.
name|NativeFSLock
operator|)
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"test.lock"
argument_list|)
decl_stmt|;
name|lock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|lock
operator|.
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"no exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|expected
parameter_list|)
block|{
comment|// ok
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|lock
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** delete the lockfile and test ensureValid fails */
DECL|method|testDeleteLockFile
specifier|public
name|void
name|testDeleteLockFile
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Directory
name|dir
init|=
name|getDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
init|)
block|{
name|assumeFalse
argument_list|(
literal|"we must be able to delete an open file"
argument_list|,
name|TestUtil
operator|.
name|hasWindowsFS
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|Lock
name|lock
init|=
name|dir
operator|.
name|obtainLock
argument_list|(
literal|"test.lock"
argument_list|)
decl_stmt|;
name|lock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"test.lock"
argument_list|)
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|ensureValid
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"no exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
comment|// ok
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|lock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
