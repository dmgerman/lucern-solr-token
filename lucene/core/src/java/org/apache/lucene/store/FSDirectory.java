begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|FilterOutputStream
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
name|channels
operator|.
name|ClosedChannelException
import|;
end_import
begin_comment
comment|// javadoc @link
end_comment
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
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
name|FileAlreadyExistsException
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
name|NoSuchFileException
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
name|OpenOption
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
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
name|StandardOpenOption
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashSet
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|IndexFileNames
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
begin_comment
comment|/**  * Base class for Directory implementations that store index  * files in the file system.    *<a name="subclasses"></a>  * There are currently three core  * subclasses:  *  *<ul>  *  *<li>{@link SimpleFSDirectory} is a straightforward  *       implementation using Files.newByteChannel.  *       However, it has poor concurrent performance  *       (multiple threads will bottleneck) as it  *       synchronizes when multiple threads read from the  *       same file.  *  *<li>{@link NIOFSDirectory} uses java.nio's  *       FileChannel's positional io when reading to avoid  *       synchronization when reading from the same file.  *       Unfortunately, due to a Windows-only<a  *       href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6265734">Sun  *       JRE bug</a> this is a poor choice for Windows, but  *       on all other platforms this is the preferred  *       choice. Applications using {@link Thread#interrupt()} or  *       {@link Future#cancel(boolean)} should use  *       {@code RAFDirectory} instead. See {@link NIOFSDirectory} java doc  *       for details.  *          *<li>{@link MMapDirectory} uses memory-mapped IO when  *       reading. This is a good choice if you have plenty  *       of virtual memory relative to your index size, eg  *       if you are running on a 64 bit JRE, or you are  *       running on a 32 bit JRE but your index sizes are  *       small enough to fit into the virtual memory space.  *       Java has currently the limitation of not being able to  *       unmap files from user code. The files are unmapped, when GC  *       releases the byte buffers. Due to  *<a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">  *       this bug</a> in Sun's JRE, MMapDirectory's {@link IndexInput#close}  *       is unable to close the underlying OS file handle. Only when  *       GC finally collects the underlying objects, which could be  *       quite some time later, will the file handle be closed.  *       This will consume additional transient disk usage: on Windows,  *       attempts to delete or overwrite the files will result in an  *       exception; on other platforms, which typically have a&quot;delete on  *       last close&quot; semantics, while such operations will succeed, the bytes  *       are still consuming space on disk.  For many applications this  *       limitation is not a problem (e.g. if you have plenty of disk space,  *       and you don't rely on overwriting files on Windows) but it's still  *       an important limitation to be aware of. This class supplies a  *       (possibly dangerous) workaround mentioned in the bug report,  *       which may fail on non-Sun JVMs.  *</ul>  *  *<p>Unfortunately, because of system peculiarities, there is  * no single overall best implementation.  Therefore, we've  * added the {@link #open} method, to allow Lucene to choose  * the best FSDirectory implementation given your  * environment, and the known limitations of each  * implementation.  For users who have no reason to prefer a  * specific implementation, it's best to simply use {@link  * #open}.  For all others, you should instantiate the  * desired implementation directly.  *  *<p><b>NOTE:</b> Accessing one of the above subclasses either directly or  * indirectly from a thread while it's interrupted can close the  * underlying channel immediately if at the same time the thread is  * blocked on IO. The channel will remain closed and subsequent access  * to the index will throw a {@link ClosedChannelException}.  * Applications using {@link Thread#interrupt()} or  * {@link Future#cancel(boolean)} should use the slower legacy  * {@code RAFDirectory} from the {@code misc} Lucene module instead.  *  *<p>The locking implementation is by default {@link  * NativeFSLockFactory}, but can be changed by  * passing in a custom {@link LockFactory} instance.  *  * @see Directory  */
end_comment
begin_class
DECL|class|FSDirectory
specifier|public
specifier|abstract
class|class
name|FSDirectory
extends|extends
name|BaseDirectory
block|{
DECL|field|directory
specifier|protected
specifier|final
name|Path
name|directory
decl_stmt|;
comment|// The underlying filesystem directory
comment|/** Maps files that we are trying to delete (or we tried already but failed)    *  before attempting to delete that key. */
DECL|field|pendingDeletes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|pendingDeletes
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|opsSinceLastDelete
specifier|private
specifier|final
name|AtomicInteger
name|opsSinceLastDelete
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/** Used to generate temp file names in {@link #createTempOutput}. */
DECL|field|nextTempFileCounter
specifier|private
specifier|final
name|AtomicLong
name|nextTempFileCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/** Create a new FSDirectory for the named location (ctor for subclasses).    * The directory is created at the named location if it does not yet exist.    *     *<p>{@code FSDirectory} resolves the given Path to a canonical /    * real path to ensure it can correctly lock the index directory and no other process    * can interfere with changing possible symlinks to the index directory inbetween.    * If you want to use symlinks and change them dynamically, close all    * {@code IndexWriters} and create a new {@code FSDirecory} instance.    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException if there is a low-level I/O error    */
DECL|method|FSDirectory
specifier|protected
name|FSDirectory
parameter_list|(
name|Path
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|lockFactory
argument_list|)
expr_stmt|;
comment|// If only read access is permitted, createDirectories fails even if the directory already exists.
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// create directory, if it doesn't exist
block|}
name|directory
operator|=
name|path
operator|.
name|toRealPath
argument_list|()
expr_stmt|;
block|}
comment|/** Creates an FSDirectory instance, trying to pick the    *  best implementation given the current environment.    *  The directory returned uses the {@link NativeFSLockFactory}.    *  The directory is created at the named location if it does not yet exist.    *     *<p>{@code FSDirectory} resolves the given Path when calling this method to a canonical /    * real path to ensure it can correctly lock the index directory and no other process    * can interfere with changing possible symlinks to the index directory inbetween.    * If you want to use symlinks and change them dynamically, close all    * {@code IndexWriters} and create a new {@code FSDirecory} instance.    *    *<p>Currently this returns {@link MMapDirectory} for Linux, MacOSX, Solaris,    *  and Windows 64-bit JREs, {@link NIOFSDirectory} for other    *  non-Windows JREs, and {@link SimpleFSDirectory} for other    *  JREs on Windows. It is highly recommended that you consult the    *  implementation's documentation for your platform before    *  using this method.    *    *<p><b>NOTE</b>: this method may suddenly change which    * implementation is returned from release to release, in    * the event that higher performance defaults become    * possible; if the precise implementation is important to    * your application, please instantiate it directly,    * instead. For optimal performance you should consider using    * {@link MMapDirectory} on 64 bit JVMs.    *    *<p>See<a href="#subclasses">above</a> */
DECL|method|open
specifier|public
specifier|static
name|FSDirectory
name|open
parameter_list|(
name|Path
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
name|FSLockFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
comment|/** Just like {@link #open(Path)}, but allows you to    *  also specify a custom {@link LockFactory}. */
DECL|method|open
specifier|public
specifier|static
name|FSDirectory
name|open
parameter_list|(
name|Path
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
operator|&&
name|MMapDirectory
operator|.
name|UNMAP_SUPPORTED
condition|)
block|{
return|return
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
return|;
block|}
elseif|else
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
comment|/** Lists all files (including subdirectories) in the directory.    *    *  @throws IOException if there was an I/O error during listing */
DECL|method|listAll
specifier|public
specifier|static
name|String
index|[]
name|listAll
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|listAll
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|listAll
specifier|private
specifier|static
name|String
index|[]
name|listAll
parameter_list|(
name|Path
name|dir
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|skipNames
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|dir
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|path
range|:
name|stream
control|)
block|{
name|String
name|name
init|=
name|path
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|skipNames
operator|!=
literal|null
operator|&&
name|skipNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|==
literal|false
condition|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
index|[]
name|array
init|=
name|entries
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// Directory.listAll javadocs state that we sort the results here, so we don't let filesystem
comment|// specifics leak out of this abstraction:
name|Arrays
operator|.
name|sort
argument_list|(
name|array
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
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
argument_list|,
name|pendingDeletes
argument_list|)
return|;
block|}
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
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|pendingDeletes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchFileException
argument_list|(
literal|"file \""
operator|+
name|name
operator|+
literal|"\" is pending delete"
argument_list|)
throw|;
block|}
return|return
name|Files
operator|.
name|size
argument_list|(
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// If this file was pending delete, we are now bringing it back to life:
name|pendingDeletes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|maybeDeletePendingFiles
argument_list|()
expr_stmt|;
return|return
operator|new
name|FSIndexOutput
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createTempOutput
specifier|public
name|IndexOutput
name|createTempOutput
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|maybeDeletePendingFiles
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|String
name|name
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|prefix
argument_list|,
name|suffix
operator|+
literal|"_"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|nextTempFileCounter
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pendingDeletes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
return|return
operator|new
name|FSIndexOutput
argument_list|(
name|name
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileAlreadyExistsException
name|faee
parameter_list|)
block|{
comment|// Retry with next incremented name
block|}
block|}
block|}
DECL|method|ensureCanRead
specifier|protected
name|void
name|ensureCanRead
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pendingDeletes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchFileException
argument_list|(
literal|"file \""
operator|+
name|name
operator|+
literal|"\" is pending delete and cannot be opened for read"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|fsync
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|maybeDeletePendingFiles
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renameFile
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|pendingDeletes
operator|.
name|contains
argument_list|(
name|source
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchFileException
argument_list|(
literal|"file \""
operator|+
name|source
operator|+
literal|"\" is pending delete and cannot be moved"
argument_list|)
throw|;
block|}
name|pendingDeletes
operator|.
name|remove
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|Files
operator|.
name|move
argument_list|(
name|directory
operator|.
name|resolve
argument_list|(
name|source
argument_list|)
argument_list|,
name|directory
operator|.
name|resolve
argument_list|(
name|dest
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
comment|// TODO: should we move directory fsync to a separate 'syncMetadata' method?
comment|// for example, to improve listCommits(), IndexFileDeleter could also call that after deleting segments_Ns
name|IOUtils
operator|.
name|fsync
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|maybeDeletePendingFiles
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|deletePendingFiles
argument_list|()
expr_stmt|;
block|}
comment|/** @return the underlying filesystem directory */
DECL|method|getDirectory
specifier|public
name|Path
name|getDirectory
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|directory
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"@"
operator|+
name|directory
operator|+
literal|" lockFactory="
operator|+
name|lockFactory
return|;
block|}
DECL|method|fsync
specifier|protected
name|void
name|fsync
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|fsync
argument_list|(
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|pendingDeletes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchFileException
argument_list|(
literal|"file \""
operator|+
name|name
operator|+
literal|"\" is already pending delete"
argument_list|)
throw|;
block|}
name|privateDeleteFile
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|maybeDeletePendingFiles
argument_list|()
expr_stmt|;
block|}
comment|/** Tries to delete any pending deleted files, and returns true if    *  there are still files that could not be deleted. */
DECL|method|checkPendingDeletions
specifier|public
name|boolean
name|checkPendingDeletions
parameter_list|()
throws|throws
name|IOException
block|{
name|deletePendingFiles
argument_list|()
expr_stmt|;
return|return
name|pendingDeletes
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
return|;
block|}
comment|/** Try to delete any pending files that we had previously tried to delete but failed    *  because we are on Windows and the files were still held open. */
DECL|method|deletePendingFiles
specifier|public
specifier|synchronized
name|void
name|deletePendingFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pendingDeletes
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// TODO: we could fix IndexInputs from FSDirectory subclasses to call this when they are closed?
comment|// Clone the set since we mutate it in privateDeleteFile:
for|for
control|(
name|String
name|name
range|:
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|pendingDeletes
argument_list|)
control|)
block|{
name|privateDeleteFile
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|maybeDeletePendingFiles
specifier|private
name|void
name|maybeDeletePendingFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pendingDeletes
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// This is a silly heuristic to try to avoid O(N^2), where N = number of files pending deletion, behaviour on Windows:
name|int
name|count
init|=
name|opsSinceLastDelete
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|pendingDeletes
operator|.
name|size
argument_list|()
condition|)
block|{
name|opsSinceLastDelete
operator|.
name|addAndGet
argument_list|(
operator|-
name|count
argument_list|)
expr_stmt|;
name|deletePendingFiles
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|privateDeleteFile
specifier|private
name|void
name|privateDeleteFile
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isPendingDelete
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|pendingDeletes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFileException
decl||
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// We were asked to delete a non-existent file:
name|pendingDeletes
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPendingDelete
operator|&&
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
comment|// TODO: can we remove this OS-specific hacky logic?  If windows deleteFile is buggy, we should instead contain this workaround in
comment|// a WindowsFSDirectory ...
comment|// LUCENE-6684: we suppress this check for Windows, since a file could be in a confusing "pending delete" state, failing the first
comment|// delete attempt with access denied and then apparently falsely failing here when we try ot delete it again, with NSFE/FNFE
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// On windows, a file delete can fail because there's still an open
comment|// file handle against it.  We record this in pendingDeletes and
comment|// try again later.
comment|// TODO: this is hacky/lenient (we don't know which IOException this is), and
comment|// it should only happen on filesystems that can do this, so really we should
comment|// move this logic to WindowsDirectory or something
comment|// TODO: can/should we do if (Constants.WINDOWS) here, else throw the exc?
comment|// but what about a Linux box with a CIFS mount?
name|pendingDeletes
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FSIndexOutput
specifier|final
class|class
name|FSIndexOutput
extends|extends
name|OutputStreamIndexOutput
block|{
comment|/**      * The maximum chunk size is 8192 bytes, because file channel mallocs      * a native buffer outside of stack if the write buffer size is larger.      */
DECL|field|CHUNK_SIZE
specifier|static
specifier|final
name|int
name|CHUNK_SIZE
init|=
literal|8192
decl_stmt|;
DECL|method|FSIndexOutput
specifier|public
name|FSIndexOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|name
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|TRUNCATE_EXISTING
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
DECL|method|FSIndexOutput
name|FSIndexOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|OpenOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"FSIndexOutput(path=\""
operator|+
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
operator|+
literal|"\")"
argument_list|,
name|name
argument_list|,
operator|new
name|FilterOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
argument_list|,
name|options
argument_list|)
argument_list|)
block|{
comment|// This implementation ensures, that we never write more than CHUNK_SIZE bytes:
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|length
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|CHUNK_SIZE
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|length
operator|-=
name|chunk
expr_stmt|;
name|offset
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|CHUNK_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
