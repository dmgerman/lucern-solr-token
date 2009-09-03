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
name|BufferUnderflowException
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
name|util
operator|.
name|Constants
import|;
end_import
begin_comment
comment|/** File-based {@link Directory} implementation that uses  *  mmap for reading, and {@link  *  SimpleFSDirectory.SimpleFSIndexOutput} for writing.  *  *<p><b>NOTE</b>: memory mapping uses up a portion of the  * virtual memory address space in your process equal to the  * size of the file being mapped.  Before using this class,  * be sure your have plenty of virtual address space, e.g. by  * using a 64 bit JRE, or a 32 bit JRE with indexes that are  * guaranteed to fit within the address space.  * On 32 bit platforms also consult {@link #setMaxChunkSize}  * if you have problems with mmap failing because of fragmented  * address space. If you get an OutOfMemoryException, it is recommended  * to reduce the chunk size, until it works.  *  *<p>Due to<a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038">  * this bug</a> in Sun's JRE, MMapDirectory's {@link IndexInput#close}  * is unable to close the underlying OS file handle.  Only when GC  * finally collects the underlying objects, which could be quite  * some time later, will the file handle be closed.  *  *<p>This will consume additional transient disk usage: on Windows,  * attempts to delete or overwrite the files will result in an  * exception; on other platforms, which typically have a&quot;delete on  * last close&quot; semantics, while such operations will succeed, the bytes  * are still consuming space on disk.  For many applications this  * limitation is not a problem (e.g. if you have plenty of disk space,  * and you don't rely on overwriting files on Windows) but it's still  * an important limitation to be aware of.  *  *<p>This class supplies the workaround mentioned in the bug report  * (disabled by default, see {@link #setUseUnmap}), which may fail on  * non-Sun JVMs. It forcefully unmaps the buffer on close by using  * an undocumented internal cleanup functionality.  * {@link #UNMAP_SUPPORTED} is<code>true</code>, if the workaround  * can be enabled (with no guarantees).  */
end_comment
begin_class
DECL|class|MMapDirectory
specifier|public
class|class
name|MMapDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new MMapDirectory for the named location.    *    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException    */
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
name|super
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new MMapDirectory for the named location and {@link NativeFSLockFactory}.    *    * @param path the path of the directory    * @throws IOException    */
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
name|super
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// back compatibility so FSDirectory can instantiate via reflection
comment|/** @deprecated */
DECL|method|MMapDirectory
name|MMapDirectory
parameter_list|()
block|{}
DECL|field|NO_PARAM_TYPES
specifier|static
specifier|final
name|Class
index|[]
name|NO_PARAM_TYPES
init|=
operator|new
name|Class
index|[
literal|0
index|]
decl_stmt|;
DECL|field|NO_PARAMS
specifier|static
specifier|final
name|Object
index|[]
name|NO_PARAMS
init|=
operator|new
name|Object
index|[
literal|0
index|]
decl_stmt|;
DECL|field|useUnmapHack
specifier|private
name|boolean
name|useUnmapHack
init|=
literal|false
decl_stmt|;
DECL|field|maxBBuf
specifier|private
name|int
name|maxBBuf
init|=
name|Constants
operator|.
name|JRE_IS_64BIT
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
operator|(
literal|256
operator|*
literal|1024
operator|*
literal|1024
operator|)
decl_stmt|;
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
argument_list|,
name|NO_PARAM_TYPES
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
comment|/**    * Try to unmap the buffer, this method silently fails if no support    * for that in the JVM. On Windows, this leads to the fact,    * that mmapped files cannot be modified or deleted.    */
DECL|method|cleanMapping
specifier|final
name|void
name|cleanMapping
parameter_list|(
specifier|final
name|ByteBuffer
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|useUnmapHack
condition|)
block|{
try|try
block|{
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|()
block|{
specifier|public
name|Object
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
argument_list|,
name|NO_PARAM_TYPES
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
argument_list|,
name|NO_PARAMS
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
argument_list|,
name|NO_PARAM_TYPES
argument_list|)
operator|.
name|invoke
argument_list|(
name|cleaner
argument_list|,
name|NO_PARAMS
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
specifier|final
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
literal|"unable to unmap the mapped buffer"
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|initCause
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
comment|/**    * Sets the maximum chunk size (default is {@link Integer#MAX_VALUE} for    * 64 bit JVMs and 256 MiBytes for 32 bit JVMs) used for memory mapping.    * Especially on 32 bit platform, the address space can be very fragmented,    * so large index files cannot be mapped.    * Using a lower chunk size makes the directory implementation a little    * bit slower (as the correct chunk must be resolved on each seek)    * but the chance is higher that mmap does not fail. On 64 bit    * Java platforms, this parameter should always be {@link Integer#MAX_VALUE},    * as the address space is big enough.    */
DECL|method|setMaxChunkSize
specifier|public
name|void
name|setMaxChunkSize
parameter_list|(
specifier|final
name|int
name|maxBBuf
parameter_list|)
block|{
if|if
condition|(
name|maxBBuf
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Maximum chunk size for mmap must be>0"
argument_list|)
throw|;
name|this
operator|.
name|maxBBuf
operator|=
name|maxBBuf
expr_stmt|;
block|}
comment|/**    * Returns the current mmap chunk size.    * @see #setMaxChunkSize    */
DECL|method|getMaxChunkSize
specifier|public
name|int
name|getMaxChunkSize
parameter_list|()
block|{
return|return
name|maxBBuf
return|;
block|}
DECL|class|MMapIndexInput
specifier|private
class|class
name|MMapIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|buffer
specifier|private
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|isClone
specifier|private
name|boolean
name|isClone
init|=
literal|false
decl_stmt|;
DECL|method|MMapIndexInput
specifier|private
name|MMapIndexInput
parameter_list|(
name|RandomAccessFile
name|raf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|length
operator|=
name|raf
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|raf
operator|.
name|getChannel
argument_list|()
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|buffer
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
block|}
block|}
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|buffer
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
block|}
block|}
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|position
argument_list|()
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|position
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|MMapIndexInput
name|clone
init|=
operator|(
name|MMapIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
name|clone
operator|.
name|buffer
operator|=
name|buffer
operator|.
name|duplicate
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isClone
operator|||
name|buffer
operator|==
literal|null
condition|)
return|return;
comment|// unmap the buffer (if enabled) and at least unset it for GC
try|try
block|{
name|cleanMapping
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|// Because Java's ByteBuffer uses an int to address the
comment|// values, it's necessary to access a file>
comment|// Integer.MAX_VALUE in size using multiple byte buffers.
DECL|class|MultiMMapIndexInput
specifier|private
class|class
name|MultiMMapIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|buffers
specifier|private
name|ByteBuffer
index|[]
name|buffers
decl_stmt|;
DECL|field|bufSizes
specifier|private
name|int
index|[]
name|bufSizes
decl_stmt|;
comment|// keep here, ByteBuffer.size() method is optional
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|curBufIndex
specifier|private
name|int
name|curBufIndex
decl_stmt|;
DECL|field|maxBufSize
specifier|private
specifier|final
name|int
name|maxBufSize
decl_stmt|;
DECL|field|curBuf
specifier|private
name|ByteBuffer
name|curBuf
decl_stmt|;
comment|// redundant for speed: buffers[curBufIndex]
DECL|field|curAvail
specifier|private
name|int
name|curAvail
decl_stmt|;
comment|// redundant for speed: (bufSizes[curBufIndex] - curBuf.position())
DECL|field|isClone
specifier|private
name|boolean
name|isClone
init|=
literal|false
decl_stmt|;
DECL|method|MultiMMapIndexInput
specifier|public
name|MultiMMapIndexInput
parameter_list|(
name|RandomAccessFile
name|raf
parameter_list|,
name|int
name|maxBufSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|length
operator|=
name|raf
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxBufSize
operator|=
name|maxBufSize
expr_stmt|;
if|if
condition|(
name|maxBufSize
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Non positive maxBufSize: "
operator|+
name|maxBufSize
argument_list|)
throw|;
if|if
condition|(
operator|(
name|length
operator|/
name|maxBufSize
operator|)
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"RandomAccessFile too big for maximum buffer size: "
operator|+
name|raf
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
name|int
name|nrBuffers
init|=
call|(
name|int
call|)
argument_list|(
name|length
operator|/
name|maxBufSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|(
name|long
operator|)
name|nrBuffers
operator|*
name|maxBufSize
operator|)
operator|<
name|length
condition|)
name|nrBuffers
operator|++
expr_stmt|;
name|this
operator|.
name|buffers
operator|=
operator|new
name|ByteBuffer
index|[
name|nrBuffers
index|]
expr_stmt|;
name|this
operator|.
name|bufSizes
operator|=
operator|new
name|int
index|[
name|nrBuffers
index|]
expr_stmt|;
name|long
name|bufferStart
init|=
literal|0
decl_stmt|;
name|FileChannel
name|rafc
init|=
name|raf
operator|.
name|getChannel
argument_list|()
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
operator|(
name|length
operator|>
operator|(
name|bufferStart
operator|+
name|maxBufSize
operator|)
operator|)
condition|?
name|maxBufSize
else|:
call|(
name|int
call|)
argument_list|(
name|length
operator|-
name|bufferStart
argument_list|)
decl_stmt|;
name|this
operator|.
name|buffers
index|[
name|bufNr
index|]
operator|=
name|rafc
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
name|bufferStart
argument_list|,
name|bufSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufSizes
index|[
name|bufNr
index|]
operator|=
name|bufSize
expr_stmt|;
name|bufferStart
operator|+=
name|bufSize
expr_stmt|;
block|}
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Performance might be improved by reading ahead into an array of
comment|// e.g. 128 bytes and readByte() from there.
if|if
condition|(
name|curAvail
operator|==
literal|0
condition|)
block|{
name|curBufIndex
operator|++
expr_stmt|;
if|if
condition|(
name|curBufIndex
operator|>=
name|buffers
operator|.
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
name|curBuf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|bufSizes
index|[
name|curBufIndex
index|]
expr_stmt|;
block|}
name|curAvail
operator|--
expr_stmt|;
return|return
name|curBuf
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|len
operator|>
name|curAvail
condition|)
block|{
name|curBuf
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|curAvail
argument_list|)
expr_stmt|;
name|len
operator|-=
name|curAvail
expr_stmt|;
name|offset
operator|+=
name|curAvail
expr_stmt|;
name|curBufIndex
operator|++
expr_stmt|;
if|if
condition|(
name|curBufIndex
operator|>=
name|buffers
operator|.
name|length
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
name|curBuf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|bufSizes
index|[
name|curBufIndex
index|]
expr_stmt|;
block|}
name|curBuf
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|curAvail
operator|-=
name|len
expr_stmt|;
block|}
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
operator|(
operator|(
name|long
operator|)
name|curBufIndex
operator|*
name|maxBufSize
operator|)
operator|+
name|curBuf
operator|.
name|position
argument_list|()
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|curBufIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|/
name|maxBufSize
argument_list|)
expr_stmt|;
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
name|int
name|bufOffset
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
operator|(
operator|(
name|long
operator|)
name|curBufIndex
operator|*
name|maxBufSize
operator|)
argument_list|)
decl_stmt|;
name|curBuf
operator|.
name|position
argument_list|(
name|bufOffset
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|bufSizes
index|[
name|curBufIndex
index|]
operator|-
name|bufOffset
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|MultiMMapIndexInput
name|clone
init|=
operator|(
name|MultiMMapIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
name|clone
operator|.
name|buffers
operator|=
operator|new
name|ByteBuffer
index|[
name|buffers
operator|.
name|length
index|]
expr_stmt|;
comment|// No need to clone bufSizes.
comment|// Since most clones will use only one buffer, duplicate() could also be
comment|// done lazy in clones, e.g. when adapting curBuf.
for|for
control|(
name|int
name|bufNr
init|=
literal|0
init|;
name|bufNr
operator|<
name|buffers
operator|.
name|length
condition|;
name|bufNr
operator|++
control|)
block|{
name|clone
operator|.
name|buffers
index|[
name|bufNr
index|]
operator|=
name|buffers
index|[
name|bufNr
index|]
operator|.
name|duplicate
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|clone
operator|.
name|seek
argument_list|(
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|RuntimeException
name|newException
init|=
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
decl_stmt|;
name|newException
operator|.
name|initCause
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
throw|throw
name|newException
throw|;
block|}
empty_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isClone
operator|||
name|buffers
operator|==
literal|null
condition|)
return|return;
try|try
block|{
for|for
control|(
name|int
name|bufNr
init|=
literal|0
init|;
name|bufNr
operator|<
name|buffers
operator|.
name|length
condition|;
name|bufNr
operator|++
control|)
block|{
comment|// unmap the buffer (if enabled) and at least unset it for GC
try|try
block|{
name|cleanMapping
argument_list|(
name|buffers
index|[
name|bufNr
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|buffers
index|[
name|bufNr
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|buffers
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/** Creates an IndexInput for the file with the given name. */
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|getFile
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|f
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|raf
operator|.
name|length
argument_list|()
operator|<=
operator|(
name|long
operator|)
name|maxBBuf
operator|)
condition|?
operator|(
name|IndexInput
operator|)
operator|new
name|MMapIndexInput
argument_list|(
name|raf
argument_list|)
else|:
operator|(
name|IndexInput
operator|)
operator|new
name|MultiMMapIndexInput
argument_list|(
name|raf
argument_list|,
name|maxBBuf
argument_list|)
return|;
block|}
finally|finally
block|{
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Creates an IndexOutput for the file with the given name. */
DECL|method|createOutput
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|initOutput
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimpleFSDirectory
operator|.
name|SimpleFSIndexOutput
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
