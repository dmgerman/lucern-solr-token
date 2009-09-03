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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
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
name|RandomAccessFile
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
name|Random
import|;
end_import
begin_comment
comment|/**  *<p>Implements {@link LockFactory} using native OS file  * locks.  Note that because this LockFactory relies on  * java.nio.* APIs for locking, any problems with those APIs  * will cause locking to fail.  Specifically, on certain NFS  * environments the java.nio.* locks will fail (the lock can  * incorrectly be double acquired) whereas {@link  * SimpleFSLockFactory} worked perfectly in those same  * environments.  For NFS based access to an index, it's  * recommended that you try {@link SimpleFSLockFactory}  * first and work around the one limitation that a lock file  * could be left when the JVM exits abnormally.</p>  *  *<p>The primary benefit of {@link NativeFSLockFactory} is  * that lock files will be properly removed (by the OS) if  * the JVM has an abnormal exit.</p>  *   *<p>Note that, unlike {@link SimpleFSLockFactory}, the existence of  * leftover lock files in the filesystem on exiting the JVM  * is fine because the OS will free the locks held against  * these files even though the files still remain.</p>  *  *<p>If you suspect that this or any other LockFactory is  * not working properly in your environment, you can easily  * test it by using {@link VerifyingLockFactory}, {@link  * LockVerifyServer} and {@link LockStressTest}.</p>  *  * @see LockFactory  */
end_comment
begin_class
DECL|class|NativeFSLockFactory
specifier|public
class|class
name|NativeFSLockFactory
extends|extends
name|LockFactory
block|{
comment|/**    * Directory specified by<code>org.apache.lucene.lockDir</code>    * system property.  If that is not set, then<code>java.io.tmpdir</code>    * system property is used.    */
DECL|field|lockDir
specifier|private
name|File
name|lockDir
decl_stmt|;
comment|// Simple test to verify locking system is "working".  On
comment|// NFS, if it's misconfigured, you can hit long (35
comment|// second) timeouts which cause Lock.obtain to take far
comment|// too long (it assumes the obtain() call takes zero
comment|// time).  Since it's a configuration problem, we test up
comment|// front once on creating the LockFactory:
DECL|method|acquireTestLock
specifier|private
name|void
name|acquireTestLock
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|randomLockName
init|=
literal|"lucene-"
operator|+
name|Long
operator|.
name|toString
argument_list|(
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
operator|+
literal|"-test.lock"
decl_stmt|;
name|Lock
name|l
init|=
name|makeLock
argument_list|(
name|randomLockName
argument_list|)
decl_stmt|;
try|try
block|{
name|l
operator|.
name|obtain
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|IOException
name|e2
init|=
operator|new
name|IOException
argument_list|(
literal|"Failed to acquire random test lock; please verify filesystem for lock directory '"
operator|+
name|lockDir
operator|+
literal|"' supports locking"
argument_list|)
decl_stmt|;
name|e2
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
name|l
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a NativeFSLockFactory instance, with null (unset)    * lock directory.  This is package-private and is only    * used by FSDirectory when creating this LockFactory via    * the System property    * org.apache.lucene.store.FSDirectoryLockFactoryClass.    */
DECL|method|NativeFSLockFactory
name|NativeFSLockFactory
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|(
name|File
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a NativeFSLockFactory instance, storing lock    * files into the specified lockDirName:    *    * @param lockDirName where lock files are created.    */
DECL|method|NativeFSLockFactory
specifier|public
name|NativeFSLockFactory
parameter_list|(
name|String
name|lockDirName
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|File
argument_list|(
name|lockDirName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a NativeFSLockFactory instance, storing lock    * files into the specified lockDir:    *     * @param lockDir where lock files are created.    */
DECL|method|NativeFSLockFactory
specifier|public
name|NativeFSLockFactory
parameter_list|(
name|File
name|lockDir
parameter_list|)
throws|throws
name|IOException
block|{
name|setLockDir
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the lock directory.  This is package-private and is    * only used externally by FSDirectory when creating this    * LockFactory via the System property    * org.apache.lucene.store.FSDirectoryLockFactoryClass.    */
DECL|method|setLockDir
name|void
name|setLockDir
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
if|if
condition|(
name|lockDir
operator|!=
literal|null
condition|)
block|{
comment|// Ensure that lockDir exists and is a directory.
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
name|acquireTestLock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|makeLock
specifier|public
specifier|synchronized
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
block|{
if|if
condition|(
name|lockPrefix
operator|!=
literal|null
condition|)
name|lockName
operator|=
name|lockPrefix
operator|+
literal|"-n-"
operator|+
name|lockName
expr_stmt|;
return|return
operator|new
name|NativeFSLock
argument_list|(
name|lockDir
argument_list|,
name|lockName
argument_list|)
return|;
block|}
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
comment|// Note that this isn't strictly required anymore
comment|// because the existence of these files does not mean
comment|// they are locked, but, still do this in case people
comment|// really want to see the files go away:
if|if
condition|(
name|lockDir
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|lockPrefix
operator|!=
literal|null
condition|)
block|{
name|lockName
operator|=
name|lockPrefix
operator|+
literal|"-n-"
operator|+
name|lockName
expr_stmt|;
block|}
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|lockDir
argument_list|,
name|lockName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|lockFile
operator|.
name|delete
argument_list|()
condition|)
block|{
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
DECL|class|NativeFSLock
class|class
name|NativeFSLock
extends|extends
name|Lock
block|{
DECL|field|f
specifier|private
name|RandomAccessFile
name|f
decl_stmt|;
DECL|field|channel
specifier|private
name|FileChannel
name|channel
decl_stmt|;
DECL|field|lock
specifier|private
name|FileLock
name|lock
decl_stmt|;
DECL|field|path
specifier|private
name|File
name|path
decl_stmt|;
DECL|field|lockDir
specifier|private
name|File
name|lockDir
decl_stmt|;
comment|/*    * The javadocs for FileChannel state that you should have    * a single instance of a FileChannel (per JVM) for all    * locking against a given file.  To ensure this, we have    * a single (static) HashSet that contains the file paths    * of all currently locked locks.  This protects against    * possible cases where different Directory instances in    * one JVM (each with their own NativeFSLockFactory    * instance) have set the same lock dir and lock prefix.    */
DECL|field|LOCK_HELD
specifier|private
specifier|static
name|HashSet
name|LOCK_HELD
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
DECL|method|NativeFSLock
specifier|public
name|NativeFSLock
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
name|path
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
specifier|synchronized
name|boolean
name|obtain
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isLocked
argument_list|()
condition|)
block|{
comment|// Our instance is already locked:
return|return
literal|false
return|;
block|}
comment|// Ensure that lockDir exists and is a directory.
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
name|String
name|canonicalPath
init|=
name|path
operator|.
name|getCanonicalPath
argument_list|()
decl_stmt|;
name|boolean
name|markedHeld
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// Make sure nobody else in-process has this lock held
comment|// already, and, mark it held if not:
synchronized|synchronized
init|(
name|LOCK_HELD
init|)
block|{
if|if
condition|(
name|LOCK_HELD
operator|.
name|contains
argument_list|(
name|canonicalPath
argument_list|)
condition|)
block|{
comment|// Someone else in this JVM already has the lock:
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// This "reserves" the fact that we are the one
comment|// thread trying to obtain this lock, so we own
comment|// the only instance of a channel against this
comment|// file:
name|LOCK_HELD
operator|.
name|add
argument_list|(
name|canonicalPath
argument_list|)
expr_stmt|;
name|markedHeld
operator|=
literal|true
expr_stmt|;
block|}
block|}
try|try
block|{
name|f
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|path
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// On Windows, we can get intermittent "Access
comment|// Denied" here.  So, we treat this as failure to
comment|// acquire the lock, but, store the reason in case
comment|// there is in fact a real error case.
name|failureReason
operator|=
name|e
expr_stmt|;
name|f
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|channel
operator|=
name|f
operator|.
name|getChannel
argument_list|()
expr_stmt|;
try|try
block|{
name|lock
operator|=
name|channel
operator|.
name|tryLock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// At least on OS X, we will sometimes get an
comment|// intermittent "Permission Denied" IOException,
comment|// which seems to simply mean "you failed to get
comment|// the lock".  But other IOExceptions could be
comment|// "permanent" (eg, locking is not supported via
comment|// the filesystem).  So, we record the failure
comment|// reason here; the timeout obtain (usually the
comment|// one calling us) will use this as "root cause"
comment|// if it fails to get the lock.
name|failureReason
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|channel
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|f
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|markedHeld
operator|&&
operator|!
name|isLocked
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|LOCK_HELD
init|)
block|{
if|if
condition|(
name|LOCK_HELD
operator|.
name|contains
argument_list|(
name|canonicalPath
argument_list|)
condition|)
block|{
name|LOCK_HELD
operator|.
name|remove
argument_list|(
name|canonicalPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|isLocked
argument_list|()
return|;
block|}
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isLocked
argument_list|()
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|channel
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|f
operator|=
literal|null
expr_stmt|;
synchronized|synchronized
init|(
name|LOCK_HELD
init|)
block|{
name|LOCK_HELD
operator|.
name|remove
argument_list|(
name|path
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|path
operator|.
name|delete
argument_list|()
condition|)
throw|throw
operator|new
name|LockReleaseFailedException
argument_list|(
literal|"failed to delete "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
DECL|method|isLocked
specifier|public
specifier|synchronized
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|lock
operator|!=
literal|null
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NativeFSLock@"
operator|+
name|path
return|;
block|}
block|}
end_class
end_unit
