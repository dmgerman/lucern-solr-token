begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_comment
comment|/** Single threaded buffered InputStream  *  Internal Solr use only, subject to change.  */
end_comment
begin_class
DECL|class|FastInputStream
specifier|public
class|class
name|FastInputStream
extends|extends
name|InputStream
implements|implements
name|DataInput
block|{
DECL|field|in
specifier|private
specifier|final
name|InputStream
name|in
decl_stmt|;
DECL|field|buf
specifier|private
specifier|final
name|byte
index|[]
name|buf
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
DECL|method|FastInputStream
specifier|public
name|FastInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
comment|// use default BUFSIZE of BufferedOutputStream so if we wrap that
comment|// it won't cause double buffering.
name|this
argument_list|(
name|in
argument_list|,
operator|new
name|byte
index|[
literal|8192
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|FastInputStream
specifier|public
name|FastInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|byte
index|[]
name|tempBuffer
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|buf
operator|=
name|tempBuffer
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
DECL|method|wrap
specifier|public
specifier|static
name|FastInputStream
name|wrap
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
return|return
operator|(
name|in
operator|instanceof
name|FastInputStream
operator|)
condition|?
operator|(
name|FastInputStream
operator|)
name|in
else|:
operator|new
name|FastInputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
return|return
operator|-
literal|1
return|;
block|}
return|return
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
return|;
block|}
DECL|method|readUnsignedByte
specifier|public
name|int
name|readUnsignedByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
return|return
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
return|;
block|}
DECL|method|readWrappedStream
specifier|public
name|int
name|readWrappedStream
parameter_list|(
name|byte
index|[]
name|target
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
return|return
name|in
operator|.
name|read
argument_list|(
name|target
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|refill
specifier|public
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
comment|// this will set end to -1 at EOF
name|end
operator|=
name|readWrappedStream
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|available
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|end
operator|-
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|r
init|=
literal|0
decl_stmt|;
comment|// number of bytes read
comment|// first read from our buffer;
if|if
condition|(
name|end
operator|-
name|pos
operator|>
literal|0
condition|)
block|{
name|r
operator|=
name|Math
operator|.
name|min
argument_list|(
name|end
operator|-
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|r
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|==
name|len
condition|)
return|return
name|r
return|;
comment|// amount left to read is>= buffer size
if|if
condition|(
name|len
operator|-
name|r
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
name|int
name|ret
init|=
name|readWrappedStream
argument_list|(
name|b
argument_list|,
name|off
operator|+
name|r
argument_list|,
name|len
operator|-
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
operator|-
literal|1
condition|)
return|return
name|r
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|r
return|;
name|r
operator|+=
name|ret
expr_stmt|;
return|return
name|r
return|;
block|}
name|refill
argument_list|()
expr_stmt|;
comment|// first read from our buffer;
if|if
condition|(
name|end
operator|-
name|pos
operator|>
literal|0
condition|)
block|{
name|int
name|toRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|end
operator|-
name|pos
argument_list|,
name|len
operator|-
name|r
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
operator|+
name|r
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|toRead
expr_stmt|;
name|r
operator|+=
name|toRead
expr_stmt|;
return|return
name|r
return|;
block|}
return|return
name|r
operator|>
literal|0
condition|?
name|r
else|:
operator|-
literal|1
return|;
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readFully
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
name|b
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|readFully
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|readFully
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
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
name|int
name|ret
init|=
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|off
operator|+=
name|ret
expr_stmt|;
name|len
operator|-=
name|ret
expr_stmt|;
block|}
block|}
DECL|method|skipBytes
specifier|public
name|int
name|skipBytes
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|end
operator|-
name|pos
operator|>=
name|n
condition|)
block|{
name|pos
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
if|if
condition|(
name|end
operator|-
name|pos
operator|<
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|r
init|=
name|end
operator|-
name|pos
decl_stmt|;
name|pos
operator|=
name|end
expr_stmt|;
while|while
condition|(
name|r
operator|<
name|n
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|end
operator|-
name|pos
operator|<=
literal|0
condition|)
return|return
name|r
return|;
name|int
name|toRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|end
operator|-
name|pos
argument_list|,
name|n
operator|-
name|r
argument_list|)
decl_stmt|;
name|r
operator|+=
name|toRead
expr_stmt|;
name|pos
operator|+=
name|toRead
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|readBoolean
specifier|public
name|boolean
name|readBoolean
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readByte
argument_list|()
operator|==
literal|1
return|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
block|{
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
return|return
name|buf
index|[
name|pos
operator|++
index|]
return|;
block|}
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
call|(
name|short
call|)
argument_list|(
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|8
operator|)
operator||
name|readUnsignedByte
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readUnsignedShort
specifier|public
name|int
name|readUnsignedShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|8
operator|)
operator||
name|readUnsignedByte
argument_list|()
return|;
block|}
DECL|method|readChar
specifier|public
name|char
name|readChar
parameter_list|()
throws|throws
name|IOException
block|{
return|return
call|(
name|char
call|)
argument_list|(
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|8
operator|)
operator||
name|readUnsignedByte
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|24
operator|)
operator||
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|16
operator|)
operator||
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|8
operator|)
operator||
name|readUnsignedByte
argument_list|()
operator|)
return|;
block|}
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|readUnsignedByte
argument_list|()
operator|)
operator|<<
literal|56
operator|)
operator||
operator|(
operator|(
operator|(
name|long
operator|)
name|readUnsignedByte
argument_list|()
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
operator|(
operator|(
name|long
operator|)
name|readUnsignedByte
argument_list|()
operator|)
operator|<<
literal|40
operator|)
operator||
operator|(
operator|(
operator|(
name|long
operator|)
name|readUnsignedByte
argument_list|()
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
operator|(
operator|(
name|long
operator|)
name|readUnsignedByte
argument_list|()
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|16
operator|)
operator||
operator|(
name|readUnsignedByte
argument_list|()
operator|<<
literal|8
operator|)
operator||
operator|(
name|readUnsignedByte
argument_list|()
operator|)
return|;
block|}
DECL|method|readFloat
specifier|public
name|float
name|readFloat
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|readInt
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readDouble
specifier|public
name|double
name|readDouble
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|readLong
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readLine
specifier|public
name|String
name|readLine
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
name|this
argument_list|)
operator|.
name|readLine
argument_list|()
return|;
block|}
DECL|method|readUTF
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
name|this
argument_list|)
operator|.
name|readUTF
argument_list|()
return|;
block|}
block|}
end_class
end_unit
