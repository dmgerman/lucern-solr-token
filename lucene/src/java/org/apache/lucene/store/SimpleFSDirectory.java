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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
operator|.
name|Context
import|;
end_import
begin_comment
comment|/** A straightforward implementation of {@link FSDirectory}  *  using java.io.RandomAccessFile.  However, this class has  *  poor concurrent performance (multiple threads will  *  bottleneck) as it synchronizes when multiple threads  *  read from the same file.  It's usually better to use  *  {@link NIOFSDirectory} or {@link MMapDirectory} instead. */
end_comment
begin_class
DECL|class|SimpleFSDirectory
specifier|public
class|class
name|SimpleFSDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new SimpleFSDirectory for the named location.    *    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException    */
DECL|method|SimpleFSDirectory
specifier|public
name|SimpleFSDirectory
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
comment|/** Create a new SimpleFSDirectory for the named location and {@link NativeFSLockFactory}.    *    * @param path the path of the directory    * @throws IOException    */
DECL|method|SimpleFSDirectory
specifier|public
name|SimpleFSDirectory
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
return|return
operator|new
name|SimpleFSIndexInput
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|name
argument_list|)
argument_list|,
name|context
argument_list|,
name|getReadChunkSize
argument_list|()
argument_list|)
return|;
block|}
DECL|class|SimpleFSIndexInput
specifier|protected
specifier|static
class|class
name|SimpleFSIndexInput
extends|extends
name|BufferedIndexInput
block|{
DECL|class|Descriptor
specifier|protected
specifier|static
class|class
name|Descriptor
extends|extends
name|RandomAccessFile
block|{
comment|// remember if the file is open, so that we don't try to close it
comment|// more than once
DECL|field|isOpen
specifier|protected
specifier|volatile
name|boolean
name|isOpen
decl_stmt|;
DECL|field|position
name|long
name|position
decl_stmt|;
DECL|field|length
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|Descriptor
specifier|public
name|Descriptor
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|file
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|isOpen
operator|=
literal|true
expr_stmt|;
name|length
operator|=
name|length
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|isOpen
condition|)
block|{
name|isOpen
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|file
specifier|protected
specifier|final
name|Descriptor
name|file
decl_stmt|;
DECL|field|isClone
name|boolean
name|isClone
decl_stmt|;
comment|//  LUCENE-1566 - maximum read length on a 32bit JVM to prevent incorrect OOM
DECL|field|chunkSize
specifier|protected
specifier|final
name|int
name|chunkSize
decl_stmt|;
DECL|method|SimpleFSIndexInput
specifier|public
name|SimpleFSIndexInput
parameter_list|(
name|File
name|path
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|int
name|chunkSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|Descriptor
argument_list|(
name|path
argument_list|,
literal|"r"
argument_list|)
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
block|}
comment|/** IndexInput methods */
annotation|@
name|Override
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
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
synchronized|synchronized
init|(
name|file
init|)
block|{
name|long
name|position
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|position
operator|!=
name|file
operator|.
name|position
condition|)
block|{
name|file
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|file
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
name|int
name|total
init|=
literal|0
decl_stmt|;
try|try
block|{
do|do
block|{
specifier|final
name|int
name|readLength
decl_stmt|;
if|if
condition|(
name|total
operator|+
name|chunkSize
operator|>
name|len
condition|)
block|{
name|readLength
operator|=
name|len
operator|-
name|total
expr_stmt|;
block|}
else|else
block|{
comment|// LUCENE-1566 - work around JVM Bug by breaking very large reads into chunks
name|readLength
operator|=
name|chunkSize
expr_stmt|;
block|}
specifier|final
name|int
name|i
init|=
name|file
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|offset
operator|+
name|total
argument_list|,
name|readLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
block|}
name|file
operator|.
name|position
operator|+=
name|i
expr_stmt|;
name|total
operator|+=
name|i
expr_stmt|;
block|}
do|while
condition|(
name|total
operator|<
name|len
condition|)
do|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
comment|// propagate OOM up and add a hint for 32bit VM Users hitting the bug
comment|// with a large chunk size in the fast path.
specifier|final
name|OutOfMemoryError
name|outOfMemoryError
init|=
operator|new
name|OutOfMemoryError
argument_list|(
literal|"OutOfMemoryError likely caused by the Sun VM Bug described in "
operator|+
literal|"https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize "
operator|+
literal|"with a value smaller than the current chunk size ("
operator|+
name|chunkSize
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|outOfMemoryError
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|outOfMemoryError
throw|;
block|}
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
name|IOException
block|{
comment|// only close the file if this is not a clone
if|if
condition|(
operator|!
name|isClone
condition|)
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|position
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|SimpleFSIndexInput
name|clone
init|=
operator|(
name|SimpleFSIndexInput
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
return|return
name|clone
return|;
block|}
comment|/** Method used for testing. Returns true if the underlying      *  file descriptor is valid.      */
DECL|method|isFDValid
name|boolean
name|isFDValid
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|file
operator|.
name|getFD
argument_list|()
operator|.
name|valid
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copyBytes
specifier|public
name|void
name|copyBytes
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|numBytes
operator|-=
name|flushBuffer
argument_list|(
name|out
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
comment|// If out is FSIndexOutput, the copy will be optimized
name|out
operator|.
name|copyBytes
argument_list|(
name|this
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
