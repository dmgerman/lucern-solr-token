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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/** Base implementation class for buffered {@link IndexOutput}. */
end_comment
begin_class
DECL|class|BufferedIndexOutput
specifier|public
specifier|abstract
class|class
name|BufferedIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|BUFFER_SIZE
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
decl_stmt|;
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
init|=
literal|0
decl_stmt|;
comment|// position in file of buffer
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
init|=
literal|0
decl_stmt|;
comment|// position in buffer
comment|/** Writes a single byte.    * @see IndexInput#readByte()    */
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bufferPosition
operator|>=
name|BUFFER_SIZE
condition|)
name|flush
argument_list|()
expr_stmt|;
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
comment|/** Writes an array of bytes.    * @param b the bytes to write    * @param length the number of bytes to write    * @see IndexInput#readBytes(byte[],int,int)    */
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesLeft
init|=
name|BUFFER_SIZE
operator|-
name|bufferPosition
decl_stmt|;
comment|// is there enough space in the buffer?
if|if
condition|(
name|bytesLeft
operator|>=
name|length
condition|)
block|{
comment|// we add the data to the end of the buffer
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bufferPosition
operator|+=
name|length
expr_stmt|;
comment|// if the buffer is full, flush it
if|if
condition|(
name|BUFFER_SIZE
operator|-
name|bufferPosition
operator|==
literal|0
condition|)
name|flush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// is data larger then buffer?
if|if
condition|(
name|length
operator|>
name|BUFFER_SIZE
condition|)
block|{
comment|// we flush the buffer
if|if
condition|(
name|bufferPosition
operator|>
literal|0
condition|)
name|flush
argument_list|()
expr_stmt|;
comment|// and write data at once
name|flushBuffer
argument_list|(
name|b
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bufferStart
operator|+=
name|length
expr_stmt|;
block|}
else|else
block|{
comment|// we fill/flush the buffer (until the input is written)
name|int
name|pos
init|=
literal|0
decl_stmt|;
comment|// position in the input data
name|int
name|pieceLength
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|length
condition|)
block|{
name|pieceLength
operator|=
operator|(
name|length
operator|-
name|pos
operator|<
name|bytesLeft
operator|)
condition|?
name|length
operator|-
name|pos
else|:
name|bytesLeft
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|pos
argument_list|,
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|pieceLength
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|pieceLength
expr_stmt|;
name|bufferPosition
operator|+=
name|pieceLength
expr_stmt|;
comment|// if the buffer is full, flush it
name|bytesLeft
operator|=
name|BUFFER_SIZE
operator|-
name|bufferPosition
expr_stmt|;
if|if
condition|(
name|bytesLeft
operator|==
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
name|bytesLeft
operator|=
name|BUFFER_SIZE
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/** Forces any buffered output to be written. */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBuffer
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|)
expr_stmt|;
name|bufferStart
operator|+=
name|bufferPosition
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Expert: implements buffer write.  Writes bytes at the current position in    * the output.    * @param b the bytes to write    * @param len the number of bytes to write    */
DECL|method|flushBuffer
specifier|protected
specifier|abstract
name|void
name|flushBuffer
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Closes this stream to further operations. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the current position in this file, where the next write will    * occur.    * @see #seek(long)    */
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
comment|/** Sets current position in this file, where the next write will occur.    * @see #getFilePointer()    */
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
name|flush
argument_list|()
expr_stmt|;
name|bufferStart
operator|=
name|pos
expr_stmt|;
block|}
comment|/** The number of bytes in the file. */
DECL|method|length
specifier|public
specifier|abstract
name|long
name|length
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
