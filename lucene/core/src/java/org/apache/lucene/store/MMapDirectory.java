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
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|FileChannel
operator|.
name|MapMode
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
name|security
operator|.
name|AccessController
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedActionException
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|ByteBufferIndexInput
operator|.
name|BufferCleaner
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
comment|/** File-based {@link Directory} implementation that uses  *  mmap for reading, and {@link  *  FSDirectory.FSIndexOutput} for writing.  *  *<p><b>NOTE</b>: memory mapping uses up a portion of the  * virtual memory address space in your process equal to the  * size of the file being mapped.  Before using this class,  * be sure your have plenty of virtual address space, e.g. by  * using a 64 bit JRE, or a 32 bit JRE with indexes that are  * guaranteed to fit within the address space.  * On 32 bit platforms also consult {@link #MMapDirectory(File, LockFactory, int)}  * if you have problems with mmap failing because of fragmented  * address space. If you get an OutOfMemoryException, it is recommended  * to reduce the chunk size, until it works.  *  *<p>Due to<a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">  * this bug</a> in Sun's JRE, MMapDirectory's {@link IndexInput#close}  * is unable to close the underlying OS file handle.  Only when GC  * finally collects the underlying objects, which could be quite  * some time later, will the file handle be closed.  *  *<p>This will consume additional transient disk usage: on Windows,  * attempts to delete or overwrite the files will result in an  * exception; on other platforms, which typically have a&quot;delete on  * last close&quot; semantics, while such operations will succeed, the bytes  * are still consuming space on disk.  For many applications this  * limitation is not a problem (e.g. if you have plenty of disk space,  * and you don't rely on overwriting files on Windows) but it's still  * an important limitation to be aware of.  *  *<p>This class supplies the workaround mentioned in the bug report  * (see {@link #setUseUnmap}), which may fail on  * non-Sun JVMs. It forcefully unmaps the buffer on close by using  * an undocumented internal cleanup functionality.  * {@link #UNMAP_SUPPORTED} is<code>true</code>, if the workaround  * can be enabled (with no guarantees).  *<p>  *<b>NOTE:</b> Accessing this class either directly or  * indirectly from a thread while it's interrupted can close the  * underlying channel immediately if at the same time the thread is  * blocked on IO. The channel will remain closed and subsequent access  * to {@link MMapDirectory} will throw a {@link ClosedChannelException}.   *</p>  * @see<a href="http://blog.thetaphi.de/2012/07/use-lucenes-mmapdirectory-on-64bit.html">Blog post about MMapDirectory</a>  */
end_comment
begin_class
DECL|class|MMapDirectory
specifier|public
class|class
name|MMapDirectory
extends|extends
name|FSDirectory
block|{
DECL|field|useUnmapHack
specifier|private
name|boolean
name|useUnmapHack
init|=
name|UNMAP_SUPPORTED
decl_stmt|;
comment|/**     * Default max chunk size.    * @see #MMapDirectory(File, LockFactory, int)    */
DECL|field|DEFAULT_MAX_BUFF
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_BUFF
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
operator|(
literal|1
operator|<<
literal|30
operator|)
else|:
operator|(
literal|1
operator|<<
literal|28
operator|)
decl_stmt|;
DECL|field|chunkSizePower
specifier|final
name|int
name|chunkSizePower
decl_stmt|;
comment|/** Create a new MMapDirectory for the named location.    *    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException if there is a low-level I/O error    */
DECL|method|MMapDirectory
specifier|public
name|MMapDirectory
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
name|this
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|,
name|DEFAULT_MAX_BUFF
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new MMapDirectory for the named location and {@link NativeFSLockFactory}.    *    * @param path the path of the directory    * @throws IOException if there is a low-level I/O error    */
DECL|method|MMapDirectory
specifier|public
name|MMapDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new MMapDirectory for the named location, specifying the     * maximum chunk size used for memory mapping.    *     * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @param maxChunkSize maximum chunk size (default is 1 GiBytes for    * 64 bit JVMs and 256 MiBytes for 32 bit JVMs) used for memory mapping.    *<p>    * Especially on 32 bit platform, the address space can be very fragmented,    * so large index files cannot be mapped. Using a lower chunk size makes     * the directory implementation a little bit slower (as the correct chunk     * may be resolved on lots of seeks) but the chance is higher that mmap     * does not fail. On 64 bit Java platforms, this parameter should always     * be {@code 1<< 30}, as the address space is big enough.    *<p>    *<b>Please note:</b> The chunk size is always rounded down to a power of 2.    * @throws IOException if there is a low-level I/O error    */
DECL|method|MMapDirectory
specifier|public
name|MMapDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|,
name|int
name|maxChunkSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxChunkSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Maximum chunk size for mmap must be>0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|chunkSizePower
operator|=
literal|31
operator|-
name|Integer
operator|.
name|numberOfLeadingZeros
argument_list|(
name|maxChunkSize
argument_list|)
expr_stmt|;
assert|assert
name|this
operator|.
name|chunkSizePower
operator|>=
literal|0
operator|&&
name|this
operator|.
name|chunkSizePower
operator|<=
literal|30
assert|;
block|}
comment|/**    *<code>true</code>, if this platform supports unmapping mmapped files.    */
DECL|field|UNMAP_SUPPORTED
specifier|public
specifier|static
specifier|final
name|boolean
name|UNMAP_SUPPORTED
decl_stmt|;
static|static
block|{
name|boolean
name|v
decl_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Cleaner"
argument_list|)
expr_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"java.nio.DirectByteBuffer"
argument_list|)
operator|.
name|getMethod
argument_list|(
literal|"cleaner"
argument_list|)
expr_stmt|;
name|v
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|v
operator|=
literal|false
expr_stmt|;
block|}
name|UNMAP_SUPPORTED
operator|=
name|v
expr_stmt|;
block|}
comment|/**    * This method enables the workaround for unmapping the buffers    * from address space after closing {@link IndexInput}, that is    * mentioned in the bug report. This hack may fail on non-Sun JVMs.    * It forcefully unmaps the buffer on close by using    * an undocumented internal cleanup functionality.    *<p><b>NOTE:</b> Enabling this is completely unsupported    * by Java and may lead to JVM crashes if<code>IndexInput</code>    * is closed while another thread is still accessing it (SIGSEGV).    * @throws IllegalArgumentException if {@link #UNMAP_SUPPORTED}    * is<code>false</code> and the workaround cannot be enabled.    */
DECL|method|setUseUnmap
specifier|public
name|void
name|setUseUnmap
parameter_list|(
specifier|final
name|boolean
name|useUnmapHack
parameter_list|)
block|{
if|if
condition|(
name|useUnmapHack
operator|&&
operator|!
name|UNMAP_SUPPORTED
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unmap hack not supported on this platform!"
argument_list|)
throw|;
name|this
operator|.
name|useUnmapHack
operator|=
name|useUnmapHack
expr_stmt|;
block|}
comment|/**    * Returns<code>true</code>, if the unmap workaround is enabled.    * @see #setUseUnmap    */
DECL|method|getUseUnmap
specifier|public
name|boolean
name|getUseUnmap
parameter_list|()
block|{
return|return
name|useUnmapHack
return|;
block|}
comment|/**    * Returns the current mmap chunk size.    * @see #MMapDirectory(File, LockFactory, int)    */
DECL|method|getMaxChunkSize
specifier|public
specifier|final
name|int
name|getMaxChunkSize
parameter_list|()
block|{
return|return
literal|1
operator|<<
name|chunkSizePower
return|;
block|}
comment|/** Creates an IndexInput for the file with the given name. */
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
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
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
try|try
init|(
name|FileChannel
name|c
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|)
init|)
block|{
specifier|final
name|String
name|resourceDescription
init|=
literal|"MMapIndexInput(path=\""
operator|+
name|file
operator|.
name|toString
argument_list|()
operator|+
literal|"\")"
decl_stmt|;
specifier|final
name|boolean
name|useUnmap
init|=
name|getUseUnmap
argument_list|()
decl_stmt|;
return|return
name|ByteBufferIndexInput
operator|.
name|newInstance
argument_list|(
name|resourceDescription
argument_list|,
name|map
argument_list|(
name|resourceDescription
argument_list|,
name|c
argument_list|,
literal|0
argument_list|,
name|c
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|c
operator|.
name|size
argument_list|()
argument_list|,
name|chunkSizePower
argument_list|,
name|useUnmap
condition|?
name|CLEANER
else|:
literal|null
argument_list|,
name|useUnmap
argument_list|)
return|;
block|}
block|}
comment|/** Maps a file into a set of buffers */
DECL|method|map
specifier|final
name|ByteBuffer
index|[]
name|map
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|FileChannel
name|fc
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|length
operator|>>>
name|chunkSizePower
operator|)
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"RandomAccessFile too big for chunk size: "
operator|+
name|resourceDescription
argument_list|)
throw|;
specifier|final
name|long
name|chunkSize
init|=
literal|1L
operator|<<
name|chunkSizePower
decl_stmt|;
comment|// we always allocate one more buffer, the last one may be a 0 byte one
specifier|final
name|int
name|nrBuffers
init|=
call|(
name|int
call|)
argument_list|(
name|length
operator|>>>
name|chunkSizePower
argument_list|)
operator|+
literal|1
decl_stmt|;
name|ByteBuffer
name|buffers
index|[]
init|=
operator|new
name|ByteBuffer
index|[
name|nrBuffers
index|]
decl_stmt|;
name|long
name|bufferStart
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|bufNr
init|=
literal|0
init|;
name|bufNr
operator|<
name|nrBuffers
condition|;
name|bufNr
operator|++
control|)
block|{
name|int
name|bufSize
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|length
operator|>
operator|(
name|bufferStart
operator|+
name|chunkSize
operator|)
operator|)
condition|?
name|chunkSize
else|:
operator|(
name|length
operator|-
name|bufferStart
operator|)
argument_list|)
decl_stmt|;
try|try
block|{
name|buffers
index|[
name|bufNr
index|]
operator|=
name|fc
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
name|offset
operator|+
name|bufferStart
argument_list|,
name|bufSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
name|convertMapFailedIOException
argument_list|(
name|ioe
argument_list|,
name|resourceDescription
argument_list|,
name|bufSize
argument_list|)
throw|;
block|}
name|bufferStart
operator|+=
name|bufSize
expr_stmt|;
block|}
return|return
name|buffers
return|;
block|}
DECL|method|convertMapFailedIOException
specifier|private
name|IOException
name|convertMapFailedIOException
parameter_list|(
name|IOException
name|ioe
parameter_list|,
name|String
name|resourceDescription
parameter_list|,
name|int
name|bufSize
parameter_list|)
block|{
specifier|final
name|String
name|originalMessage
decl_stmt|;
specifier|final
name|Throwable
name|originalCause
decl_stmt|;
if|if
condition|(
name|ioe
operator|.
name|getCause
argument_list|()
operator|instanceof
name|OutOfMemoryError
condition|)
block|{
comment|// nested OOM confuses users, because its "incorrect", just print a plain message:
name|originalMessage
operator|=
literal|"Map failed"
expr_stmt|;
name|originalCause
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|originalMessage
operator|=
name|ioe
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|originalCause
operator|=
name|ioe
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|moreInfo
decl_stmt|;
if|if
condition|(
operator|!
name|Constants
operator|.
name|JRE_IS_64BIT
condition|)
block|{
name|moreInfo
operator|=
literal|"MMapDirectory should only be used on 64bit platforms, because the address space on 32bit operating systems is too small. "
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|moreInfo
operator|=
literal|"Windows is unfortunately very limited on virtual address space. If your index size is several hundred Gigabytes, consider changing to Linux. "
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Constants
operator|.
name|LINUX
condition|)
block|{
name|moreInfo
operator|=
literal|"Please review 'ulimit -v', 'ulimit -m' (both should return 'unlimited'), and 'sysctl vm.max_map_count'. "
expr_stmt|;
block|}
else|else
block|{
name|moreInfo
operator|=
literal|"Please review 'ulimit -v', 'ulimit -m' (both should return 'unlimited'). "
expr_stmt|;
block|}
specifier|final
name|IOException
name|newIoe
init|=
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%s: %s [this may be caused by lack of enough unfragmented virtual address space "
operator|+
literal|"or too restrictive virtual memory limits enforced by the operating system, "
operator|+
literal|"preventing us to map a chunk of %d bytes. %sMore information: "
operator|+
literal|"http://blog.thetaphi.de/2012/07/use-lucenes-mmapdirectory-on-64bit.html]"
argument_list|,
name|originalMessage
argument_list|,
name|resourceDescription
argument_list|,
name|bufSize
argument_list|,
name|moreInfo
argument_list|)
argument_list|,
name|originalCause
argument_list|)
decl_stmt|;
name|newIoe
operator|.
name|setStackTrace
argument_list|(
name|ioe
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newIoe
return|;
block|}
DECL|field|CLEANER
specifier|private
specifier|static
specifier|final
name|BufferCleaner
name|CLEANER
init|=
operator|new
name|BufferCleaner
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|freeBuffer
parameter_list|(
specifier|final
name|ByteBufferIndexInput
name|parent
parameter_list|,
specifier|final
name|ByteBuffer
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Method
name|getCleanerMethod
init|=
name|buffer
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"cleaner"
argument_list|)
decl_stmt|;
name|getCleanerMethod
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Object
name|cleaner
init|=
name|getCleanerMethod
operator|.
name|invoke
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|cleaner
operator|!=
literal|null
condition|)
block|{
name|cleaner
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"clean"
argument_list|)
operator|.
name|invoke
argument_list|(
name|cleaner
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedActionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to unmap the mapped buffer: "
operator|+
name|parent
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
