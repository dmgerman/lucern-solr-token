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
begin_comment
comment|/**  * A memory-resident {@link IndexOutput} implementation.  *   * @version $Id$  */
end_comment
begin_class
DECL|class|RAMOutputStream
specifier|public
class|class
name|RAMOutputStream
extends|extends
name|IndexOutput
block|{
DECL|field|BUFFER_SIZE
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
name|BufferedIndexOutput
operator|.
name|BUFFER_SIZE
decl_stmt|;
DECL|field|file
specifier|private
name|RAMFile
name|file
decl_stmt|;
DECL|field|currentBuffer
specifier|private
name|byte
index|[]
name|currentBuffer
decl_stmt|;
DECL|field|currentBufferIndex
specifier|private
name|int
name|currentBufferIndex
decl_stmt|;
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
decl_stmt|;
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
decl_stmt|;
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
decl_stmt|;
comment|/** Construct an empty output buffer. */
DECL|method|RAMOutputStream
specifier|public
name|RAMOutputStream
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|RAMFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RAMOutputStream
name|RAMOutputStream
parameter_list|(
name|RAMFile
name|f
parameter_list|)
block|{
name|file
operator|=
name|f
expr_stmt|;
comment|// make sure that we switch to the
comment|// first needed buffer lazily
name|currentBufferIndex
operator|=
operator|-
literal|1
expr_stmt|;
name|currentBuffer
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Copy the current contents of this buffer to the named output. */
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|long
name|end
init|=
name|file
operator|.
name|length
decl_stmt|;
name|long
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|buffer
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|int
name|length
init|=
name|BUFFER_SIZE
decl_stmt|;
name|long
name|nextPos
init|=
name|pos
operator|+
name|length
decl_stmt|;
if|if
condition|(
name|nextPos
operator|>
name|end
condition|)
block|{
comment|// at the last buffer
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|-
name|pos
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBytes
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|file
operator|.
name|buffers
operator|.
name|get
argument_list|(
name|buffer
operator|++
argument_list|)
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|pos
operator|=
name|nextPos
expr_stmt|;
block|}
block|}
comment|/** Resets this to an empty buffer. */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
try|try
block|{
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should never happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|file
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
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
comment|// set the file length in case we seek back
comment|// and flush() has not been called yet
name|setFileLength
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|<
name|bufferStart
operator|||
name|pos
operator|>=
name|bufferStart
operator|+
name|bufferLength
condition|)
block|{
name|currentBufferIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|/
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|switchCurrentBuffer
argument_list|()
expr_stmt|;
block|}
name|bufferPosition
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|%
name|BUFFER_SIZE
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
name|file
operator|.
name|length
return|;
block|}
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
operator|==
name|bufferLength
condition|)
block|{
name|currentBufferIndex
operator|++
expr_stmt|;
name|switchCurrentBuffer
argument_list|()
expr_stmt|;
block|}
name|currentBuffer
index|[
name|bufferPosition
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
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
literal|0
condition|)
block|{
if|if
condition|(
name|bufferPosition
operator|==
name|bufferLength
condition|)
block|{
name|currentBufferIndex
operator|++
expr_stmt|;
name|switchCurrentBuffer
argument_list|()
expr_stmt|;
block|}
name|int
name|remainInBuffer
init|=
name|currentBuffer
operator|.
name|length
operator|-
name|bufferPosition
decl_stmt|;
name|int
name|bytesToCopy
init|=
name|len
operator|<
name|remainInBuffer
condition|?
name|len
else|:
name|remainInBuffer
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|currentBuffer
argument_list|,
name|bufferPosition
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|bytesToCopy
expr_stmt|;
name|len
operator|-=
name|bytesToCopy
expr_stmt|;
name|bufferPosition
operator|+=
name|bytesToCopy
expr_stmt|;
block|}
block|}
DECL|method|switchCurrentBuffer
specifier|private
specifier|final
name|void
name|switchCurrentBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentBufferIndex
operator|==
name|file
operator|.
name|buffers
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentBuffer
operator|=
name|file
operator|.
name|addBuffer
argument_list|(
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentBuffer
operator|=
operator|(
name|byte
index|[]
operator|)
name|file
operator|.
name|buffers
operator|.
name|get
argument_list|(
name|currentBufferIndex
argument_list|)
expr_stmt|;
block|}
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferStart
operator|=
name|BUFFER_SIZE
operator|*
name|currentBufferIndex
expr_stmt|;
name|bufferLength
operator|=
name|currentBuffer
operator|.
name|length
expr_stmt|;
block|}
DECL|method|setFileLength
specifier|private
name|void
name|setFileLength
parameter_list|()
block|{
name|long
name|pointer
init|=
name|bufferStart
operator|+
name|bufferPosition
decl_stmt|;
if|if
condition|(
name|pointer
operator|>
name|file
operator|.
name|length
condition|)
block|{
name|file
operator|.
name|setLength
argument_list|(
name|pointer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
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
name|setFileLength
argument_list|()
expr_stmt|;
block|}
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|currentBufferIndex
operator|<
literal|0
condition|?
literal|0
else|:
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
block|}
end_class
end_unit
