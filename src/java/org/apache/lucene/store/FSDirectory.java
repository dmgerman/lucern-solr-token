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
name|FilenameFilter
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
name|RandomAccessFile
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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
name|util
operator|.
name|Constants
import|;
end_import
begin_comment
comment|/**  *<a name="subclasses"/>  * Base class for Directory implementations that store index  * files in the file system.  There are currently three core  * subclasses:  *  *<ul>  *  *<li> {@link SimpleFSDirectory} is a straightforward  *       implementation using java.io.RandomAccessFile.  *       However, it has poor concurrent performance  *       (multiple threads will bottleneck) as it  *       synchronizes when multiple threads read from the  *       same file.  *  *<li> {@link NIOFSDirectory} uses java.nio's  *       FileChannel's positional io when reading to avoid  *       synchronization when reading from the same file.  *       Unfortunately, due to a Windows-only<a  *       href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6265734">Sun  *       JRE bug</a> this is a poor choice for Windows, but  *       on all other platforms this is the preferred  *       choice.  *  *<li> {@link MMapDirectory} uses memory-mapped IO when  *       reading. This is a good choice if you have plenty  *       of virtual memory relative to your index size, eg  *       if you are running on a 64 bit JRE, or you are  *       running on a 32 bit JRE but your index sizes are  *       small enough to fit into the virtual memory space.  *       Java has currently the limitation of not being able to  *       unmap files from user code. The files are unmapped, when GC  *       releases the byte buffers. Due to  *<a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">  *       this bug</a> in Sun's JRE, MMapDirectory's {@link IndexInput#close}  *       is unable to close the underlying OS file handle. Only when  *       GC finally collects the underlying objects, which could be  *       quite some time later, will the file handle be closed.  *       This will consume additional transient disk usage: on Windows,  *       attempts to delete or overwrite the files will result in an  *       exception; on other platforms, which typically have a&quot;delete on  *       last close&quot; semantics, while such operations will succeed, the bytes  *       are still consuming space on disk.  For many applications this  *       limitation is not a problem (e.g. if you have plenty of disk space,  *       and you don't rely on overwriting files on Windows) but it's still  *       an important limitation to be aware of. This class supplies a  *       (possibly dangerous) workaround mentioned in the bug report,  *       which may fail on non-Sun JVMs.  *</ul>  *  * Unfortunately, because of system peculiarities, there is  * no single overall best implementation.  Therefore, we've  * added the {@link #open} method, to allow Lucene to choose  * the best FSDirectory implementation given your  * environment, and the known limitations of each  * implementation.  For users who have no reason to prefer a  * specific implementation, it's best to simply use {@link  * #open}.  For all others, you should instantiate the  * desired implementation directly.  *  *<p>The locking implementation is by default {@link  * NativeFSLockFactory}, but can be changed by  * passing in a custom {@link LockFactory} instance.  *  * @see Directory  */
end_comment
begin_class
DECL|class|FSDirectory
specifier|public
specifier|abstract
class|class
name|FSDirectory
extends|extends
name|Directory
block|{
DECL|field|DIGESTER
specifier|private
specifier|static
name|MessageDigest
name|DIGESTER
decl_stmt|;
static|static
block|{
try|try
block|{
name|DIGESTER
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
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
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// returns the canonical version of the directory, creating it if it doesn't exist.
DECL|method|getCanonicalPath
specifier|private
specifier|static
name|File
name|getCanonicalPath
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|File
argument_list|(
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
return|;
block|}
DECL|field|checked
specifier|private
name|boolean
name|checked
decl_stmt|;
DECL|method|createDir
specifier|final
name|void
name|createDir
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|checked
condition|)
block|{
if|if
condition|(
operator|!
name|directory
operator|.
name|exists
argument_list|()
condition|)
if|if
condition|(
operator|!
name|directory
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
name|directory
argument_list|)
throw|;
name|checked
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** Initializes the directory to create a new file with the given name.    * This method should be used in {@link #createOutput}. */
DECL|method|initOutput
specifier|protected
specifier|final
name|void
name|initOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|createDir
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|delete
argument_list|()
condition|)
comment|// delete existing, if any
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot overwrite: "
operator|+
name|file
argument_list|)
throw|;
block|}
comment|/** The underlying filesystem directory */
DECL|field|directory
specifier|protected
name|File
name|directory
init|=
literal|null
decl_stmt|;
comment|/** Create a new FSDirectory for the named location (ctor for subclasses).    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException    */
DECL|method|FSDirectory
specifier|protected
name|FSDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|path
operator|=
name|getCanonicalPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// new ctors use always NativeFSLockFactory as default:
if|if
condition|(
name|lockFactory
operator|==
literal|null
condition|)
block|{
name|lockFactory
operator|=
operator|new
name|NativeFSLockFactory
argument_list|()
expr_stmt|;
block|}
name|directory
operator|=
name|path
expr_stmt|;
if|if
condition|(
name|directory
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|directory
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchDirectoryException
argument_list|(
literal|"file '"
operator|+
name|directory
operator|+
literal|"' exists but is not a directory"
argument_list|)
throw|;
name|setLockFactory
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
comment|// for filesystem based LockFactory, delete the lockPrefix, if the locks are placed
comment|// in index dir. If no index dir is given, set ourselves
if|if
condition|(
name|lockFactory
operator|instanceof
name|FSLockFactory
condition|)
block|{
specifier|final
name|FSLockFactory
name|lf
init|=
operator|(
name|FSLockFactory
operator|)
name|lockFactory
decl_stmt|;
specifier|final
name|File
name|dir
init|=
name|lf
operator|.
name|getLockDir
argument_list|()
decl_stmt|;
comment|// if the lock factory has no lockDir set, use the this directory as lockDir
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|lf
operator|.
name|setLockDir
argument_list|(
name|this
operator|.
name|directory
argument_list|)
expr_stmt|;
name|lf
operator|.
name|setLockPrefix
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dir
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|directory
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
condition|)
block|{
name|lf
operator|.
name|setLockPrefix
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Creates an FSDirectory instance, trying to pick the    *  best implementation given the current environment.    *  The directory returned uses the {@link NativeFSLockFactory}.    *    *<p>Currently this returns {@link NIOFSDirectory}    *  on non-Windows JREs and {@link SimpleFSDirectory}    *  on Windows.    *    *<p><b>NOTE</b>: this method may suddenly change which    * implementation is returned from release to release, in    * the event that higher performance defaults become    * possible; if the precise implementation is important to    * your application, please instantiate it directly,    * instead. On 64 bit systems, it may also good to    * return {@link MMapDirectory}, but this is disabled    * because of officially missing unmap support in Java.    * For optimal performance you should consider using    * this implementation on 64 bit JVMs.    *    *<p>See<a href="#subclasses">above</a> */
DECL|method|open
specifier|public
specifier|static
name|FSDirectory
name|open
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Just like {@link #open(File)}, but allows you to    *  also specify a custom {@link LockFactory}. */
DECL|method|open
specifier|public
specifier|static
name|FSDirectory
name|open
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* For testing:     MMapDirectory dir=new MMapDirectory(path, lockFactory);     dir.setUseUnmap(true);     return dir;     */
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
return|return
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NIOFSDirectory
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
block|}
comment|/** Lists all files (not subdirectories) in the    *  directory.  This method never returns null (throws    *  {@link IOException} instead).    *    *  @throws NoSuchDirectoryException if the directory    *   does not exist, or does exist but is not a    *   directory.    *  @throws IOException if list() returns null */
DECL|method|listAll
specifier|public
specifier|static
name|String
index|[]
name|listAll
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchDirectoryException
argument_list|(
literal|"directory '"
operator|+
name|dir
operator|+
literal|"' does not exist"
argument_list|)
throw|;
elseif|else
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
throw|throw
operator|new
name|NoSuchDirectoryException
argument_list|(
literal|"file '"
operator|+
name|dir
operator|+
literal|"' exists but is not a directory"
argument_list|)
throw|;
comment|// Exclude subdirs
name|String
index|[]
name|result
init|=
name|dir
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|file
parameter_list|)
block|{
return|return
operator|!
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|file
argument_list|)
operator|.
name|isDirectory
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"directory '"
operator|+
name|dir
operator|+
literal|"' exists and is a directory, but cannot be listed: list() returned null"
argument_list|)
throw|;
return|return
name|result
return|;
block|}
comment|/** Lists all files (not subdirectories) in the    * directory.    * @see #listAll(File) */
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|listAll
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/** Returns true iff a file with the given name exists. */
annotation|@
name|Override
DECL|method|fileExists
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|exists
argument_list|()
return|;
block|}
comment|/** Returns the time the named file was last modified. */
annotation|@
name|Override
DECL|method|fileModified
specifier|public
name|long
name|fileModified
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|lastModified
argument_list|()
return|;
block|}
comment|/** Returns the time the named file was last modified. */
DECL|method|fileModified
specifier|public
specifier|static
name|long
name|fileModified
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|lastModified
argument_list|()
return|;
block|}
comment|/** Set the modified time of an existing file to now. */
annotation|@
name|Override
DECL|method|touchFile
specifier|public
name|void
name|touchFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|file
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the length in bytes of a file in the directory. */
annotation|@
name|Override
DECL|method|fileLength
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
comment|/** Removes an existing file in the directory. */
annotation|@
name|Override
DECL|method|deleteFile
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
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
name|file
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|fullFile
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
name|IOException
name|exc
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|success
operator|&&
name|retryCount
operator|<
literal|5
condition|)
block|{
name|retryCount
operator|++
expr_stmt|;
name|RandomAccessFile
name|file
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|file
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|fullFile
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|exc
operator|==
literal|null
condition|)
name|exc
operator|=
name|ioe
expr_stmt|;
try|try
block|{
comment|// Pause 5 msec
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// In 3.0 we will change this to throw
comment|// InterruptedException instead
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
comment|// Throw original exception
throw|throw
name|exc
throw|;
block|}
comment|// Inherit javadoc
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|openInput
argument_list|(
name|name
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
return|;
block|}
comment|/**    * So we can do some byte-to-hexchar conversion below    */
DECL|field|HEX_DIGITS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEX_DIGITS
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getLockID
specifier|public
name|String
name|getLockID
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
name|dirName
decl_stmt|;
comment|// name to be hashed
try|try
block|{
name|dirName
operator|=
name|directory
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|byte
name|digest
index|[]
decl_stmt|;
synchronized|synchronized
init|(
name|DIGESTER
init|)
block|{
name|digest
operator|=
name|DIGESTER
operator|.
name|digest
argument_list|(
name|dirName
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"lucene-"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|digest
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|digest
index|[
name|i
index|]
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Closes the store to future operations. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|getFile
specifier|public
name|File
name|getFile
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|directory
return|;
block|}
comment|/** For debug output. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"@"
operator|+
name|directory
return|;
block|}
comment|/**    * Default read chunk size.  This is a conditional    * default: on 32bit JVMs, it defaults to 100 MB.  On    * 64bit JVMs, it's<code>Integer.MAX_VALUE</code>.    * @see #setReadChunkSize    */
DECL|field|DEFAULT_READ_CHUNK_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_READ_CHUNK_SIZE
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
literal|100
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// LUCENE-1566
DECL|field|chunkSize
specifier|private
name|int
name|chunkSize
init|=
name|DEFAULT_READ_CHUNK_SIZE
decl_stmt|;
comment|/**    * Sets the maximum number of bytes read at once from the    * underlying file during {@link IndexInput#readBytes}.    * The default value is {@link #DEFAULT_READ_CHUNK_SIZE};    *    *<p> This was introduced due to<a    * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6478546">Sun    * JVM Bug 6478546</a>, which throws an incorrect    * OutOfMemoryError when attempting to read too many bytes    * at once.  It only happens on 32bit JVMs with a large    * maximum heap size.</p>    *    *<p>Changes to this value will not impact any    * already-opened {@link IndexInput}s.  You should call    * this before attempting to open an index on the    * directory.</p>    *    *<p><b>NOTE</b>: This value should be as large as    * possible to reduce any possible performance impact.  If    * you still encounter an incorrect OutOfMemoryError,    * trying lowering the chunk size.</p>    */
DECL|method|setReadChunkSize
specifier|public
specifier|final
name|void
name|setReadChunkSize
parameter_list|(
name|int
name|chunkSize
parameter_list|)
block|{
comment|// LUCENE-1566
if|if
condition|(
name|chunkSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"chunkSize must be positive"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Constants
operator|.
name|JRE_IS_64BIT
condition|)
block|{
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
block|}
block|}
comment|/**    * The maximum number of bytes to read at once from the    * underlying file during {@link IndexInput#readBytes}.    * @see #setReadChunkSize    */
DECL|method|getReadChunkSize
specifier|public
specifier|final
name|int
name|getReadChunkSize
parameter_list|()
block|{
comment|// LUCENE-1566
return|return
name|chunkSize
return|;
block|}
block|}
end_class
end_unit
