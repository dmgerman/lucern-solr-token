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
begin_comment
comment|/**  *<p>Implements {@link LockFactory} using {@link  * Files#createFile}.</p>  *  *<p><b>NOTE:</b> the {@linkplain File#createNewFile() javadocs  * for<code>File.createNewFile()</code>} contain a vague  * yet spooky warning about not using the API for file  * locking.  This warning was added due to<a target="_top"  * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4676183">this  * bug</a>, and in fact the only known problem with using  * this API for locking is that the Lucene write lock may  * not be released when the JVM exits abnormally.</p>   *<p>When this happens, a {@link LockObtainFailedException}  * is hit when trying to create a writer, in which case you  * need to explicitly clear the lock file first.  You can  * either manually remove the file, or use the {@link  * org.apache.lucene.index.IndexWriter#unlock(Directory)}  * API.  But, first be certain that no writer is in fact  * writing to the index otherwise you can easily corrupt  * your index.</p>  *  *<p>Special care needs to be taken if you change the locking  * implementation: First be certain that no writer is in fact  * writing to the index otherwise you can easily corrupt  * your index. Be sure to do the LockFactory change all Lucene  * instances and clean up all leftover lock files before starting  * the new configuration for the first time. Different implementations  * can not work together!</p>  *  *<p>If you suspect that this or any other LockFactory is  * not working properly in your environment, you can easily  * test it by using {@link VerifyingLockFactory}, {@link  * LockVerifyServer} and {@link LockStressTest}.</p>  *   *<p>This is a singleton, you have to use {@link #INSTANCE}.  *  * @see LockFactory  */
end_comment
begin_class
DECL|class|SimpleFSLockFactory
specifier|public
specifier|final
class|class
name|SimpleFSLockFactory
extends|extends
name|FSLockFactory
block|{
comment|/**    * Singleton instance    */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|SimpleFSLockFactory
name|INSTANCE
init|=
operator|new
name|SimpleFSLockFactory
argument_list|()
decl_stmt|;
DECL|method|SimpleFSLockFactory
specifier|private
name|SimpleFSLockFactory
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|makeFSLock
specifier|protected
name|Lock
name|makeFSLock
parameter_list|(
name|FSDirectory
name|dir
parameter_list|,
name|String
name|lockName
parameter_list|)
block|{
return|return
operator|new
name|SimpleFSLock
argument_list|(
name|dir
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|lockName
argument_list|)
return|;
block|}
DECL|class|SimpleFSLock
specifier|static
class|class
name|SimpleFSLock
extends|extends
name|Lock
block|{
DECL|field|lockFile
name|Path
name|lockFile
decl_stmt|;
DECL|field|lockDir
name|Path
name|lockDir
decl_stmt|;
DECL|method|SimpleFSLock
specifier|public
name|SimpleFSLock
parameter_list|(
name|Path
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
name|lockDir
operator|.
name|resolve
argument_list|(
name|lockFileName
argument_list|)
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
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|lockDir
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|lockFile
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// On Windows, on concurrent createNewFile, the 2nd process gets "access denied".
comment|// In that case, the lock was not aquired successfully, so return false.
comment|// We record the failure reason here; the obtain with timeout (usually the
comment|// one calling us) will use this as "root cause" if it fails to get the lock.
name|failureReason
operator|=
name|ioe
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|LockReleaseFailedException
block|{
comment|// TODO: wierd that clearLock() throws the raw IOException...
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|lockFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
throw|throw
operator|new
name|LockReleaseFailedException
argument_list|(
literal|"failed to delete "
operator|+
name|lockFile
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isLocked
specifier|public
name|boolean
name|isLocked
parameter_list|()
block|{
return|return
name|Files
operator|.
name|exists
argument_list|(
name|lockFile
argument_list|)
return|;
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
literal|"SimpleFSLock@"
operator|+
name|lockFile
return|;
block|}
block|}
block|}
end_class
end_unit
