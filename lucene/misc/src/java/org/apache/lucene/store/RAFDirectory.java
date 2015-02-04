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
name|EOFException
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_comment
comment|/** A straightforward implementation of {@link FSDirectory}  *  using java.io.RandomAccessFile.  However, this class has  *  poor concurrent performance (multiple threads will  *  bottleneck) as it synchronizes when multiple threads  *  read from the same file.  It's usually better to use  *  {@link NIOFSDirectory} or {@link MMapDirectory} instead.   *<p>  *  NOTE: Because this uses RandomAccessFile, it will generally  *  not work with non-default filesystem providers. It is only  *  provided for applications that relied on the fact that   *  RandomAccessFile's IO was not interruptible.  */
end_comment
begin_class
DECL|class|RAFDirectory
specifier|public
class|class
name|RAFDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new RAFDirectory for the named location.    *  The directory is created at the named location if it does not yet exist.    *    * @param path the path of the directory    * @param lockFactory the lock factory to use    * @throws IOException if there is a low-level I/O error    */
DECL|method|RAFDirectory
specifier|public
name|RAFDirectory
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
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
name|path
operator|.
name|toFile
argument_list|()
expr_stmt|;
comment|// throw exception if we can't get a File
block|}
comment|/** Create a new SimpleFSDirectory for the named location and {@link FSLockFactory#getDefault()}.    *  The directory is created at the named location if it does not yet exist.    *    * @param path the path of the directory    * @throws IOException if there is a low-level I/O error    */
DECL|method|RAFDirectory
specifier|public
name|RAFDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
name|FSLockFactory
operator|.
name|getDefault
argument_list|()
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
specifier|final
name|File
name|path
init|=
name|directory
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|path
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
return|return
operator|new
name|RAFIndexInput
argument_list|(
literal|"SimpleFSIndexInput(path=\""
operator|+
name|path
operator|.
name|getPath
argument_list|()
operator|+
literal|"\")"
argument_list|,
name|raf
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**    * Reads bytes with {@link RandomAccessFile#seek(long)} followed by    * {@link RandomAccessFile#read(byte[], int, int)}.      */
DECL|class|RAFIndexInput
specifier|static
specifier|final
class|class
name|RAFIndexInput
extends|extends
name|BufferedIndexInput
block|{
comment|/**      * The maximum chunk size is 8192 bytes, because {@link RandomAccessFile} mallocs      * a native buffer outside of stack if the read buffer size is larger.      */
DECL|field|CHUNK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|CHUNK_SIZE
init|=
literal|8192
decl_stmt|;
comment|/** the file channel we will read from */
DECL|field|file
specifier|protected
specifier|final
name|RandomAccessFile
name|file
decl_stmt|;
comment|/** is this instance a clone and hence does not own the file to close it */
DECL|field|isClone
name|boolean
name|isClone
init|=
literal|false
decl_stmt|;
comment|/** start offset: non-zero in the slice case */
DECL|field|off
specifier|protected
specifier|final
name|long
name|off
decl_stmt|;
comment|/** end offset (start+length) */
DECL|field|end
specifier|protected
specifier|final
name|long
name|end
decl_stmt|;
DECL|method|RAFIndexInput
specifier|public
name|RAFIndexInput
parameter_list|(
name|String
name|resourceDesc
parameter_list|,
name|RandomAccessFile
name|file
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|resourceDesc
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|off
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
DECL|method|RAFIndexInput
specifier|public
name|RAFIndexInput
parameter_list|(
name|String
name|resourceDesc
parameter_list|,
name|RandomAccessFile
name|file
parameter_list|,
name|long
name|off
parameter_list|,
name|long
name|length
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|resourceDesc
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|off
operator|+
name|length
expr_stmt|;
name|this
operator|.
name|isClone
operator|=
literal|true
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
operator|!
name|isClone
condition|)
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|RAFIndexInput
name|clone
parameter_list|()
block|{
name|RAFIndexInput
name|clone
init|=
operator|(
name|RAFIndexInput
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
annotation|@
name|Override
DECL|method|slice
specifier|public
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
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
name|offset
operator|<
literal|0
operator|||
name|length
argument_list|<
literal|0
operator|||
name|offset
operator|+
name|length
argument_list|>
name|this
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"slice() "
operator|+
name|sliceDescription
operator|+
literal|" out of bounds: "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
operator|new
name|RAFIndexInput
argument_list|(
name|sliceDescription
argument_list|,
name|file
argument_list|,
name|off
operator|+
name|offset
argument_list|,
name|length
argument_list|,
name|getBufferSize
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
specifier|final
name|long
name|length
parameter_list|()
block|{
return|return
name|end
operator|-
name|off
return|;
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
name|off
operator|+
name|getFilePointer
argument_list|()
decl_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|position
operator|+
name|len
operator|>
name|end
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
try|try
block|{
while|while
condition|(
name|total
operator|<
name|len
condition|)
block|{
specifier|final
name|int
name|toRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|CHUNK_SIZE
argument_list|,
name|len
operator|-
name|total
argument_list|)
decl_stmt|;
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
name|toRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
comment|// be defensive here, even though we checked before hand, something could have changed
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
operator|+
literal|" off: "
operator|+
name|offset
operator|+
literal|" len: "
operator|+
name|len
operator|+
literal|" total: "
operator|+
name|total
operator|+
literal|" chunkLen: "
operator|+
name|toRead
operator|+
literal|" end: "
operator|+
name|end
argument_list|)
throw|;
block|}
assert|assert
name|i
operator|>
literal|0
operator|:
literal|"RandomAccessFile.read with non zero-length toRead must always read at least one byte"
assert|;
name|total
operator|+=
name|i
expr_stmt|;
block|}
assert|assert
name|total
operator|==
name|len
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|+
literal|": "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
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
block|}
block|}
end_class
end_unit
