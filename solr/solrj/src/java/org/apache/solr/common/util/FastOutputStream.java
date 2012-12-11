begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** Single threaded buffered OutputStream  *  Internal Solr use only, subject to change.  */
end_comment
begin_class
DECL|class|FastOutputStream
specifier|public
class|class
name|FastOutputStream
extends|extends
name|OutputStream
implements|implements
name|DataOutput
block|{
DECL|field|out
specifier|protected
specifier|final
name|OutputStream
name|out
decl_stmt|;
DECL|field|buf
specifier|protected
name|byte
index|[]
name|buf
decl_stmt|;
DECL|field|written
specifier|protected
name|long
name|written
decl_stmt|;
comment|// how many bytes written to the underlying stream
DECL|field|pos
specifier|protected
name|int
name|pos
decl_stmt|;
DECL|method|FastOutputStream
specifier|public
name|FastOutputStream
parameter_list|(
name|OutputStream
name|w
parameter_list|)
block|{
comment|// use default BUFSIZE of BufferedOutputStream so if we wrap that
comment|// it won't cause double buffering.
name|this
argument_list|(
name|w
argument_list|,
operator|new
name|byte
index|[
literal|8192
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|FastOutputStream
specifier|public
name|FastOutputStream
parameter_list|(
name|OutputStream
name|sink
parameter_list|,
name|byte
index|[]
name|tempBuffer
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|sink
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
block|}
DECL|method|wrap
specifier|public
specifier|static
name|FastOutputStream
name|wrap
parameter_list|(
name|OutputStream
name|sink
parameter_list|)
block|{
return|return
operator|(
name|sink
operator|instanceof
name|FastOutputStream
operator|)
condition|?
operator|(
name|FastOutputStream
operator|)
name|sink
else|:
operator|new
name|FastOutputStream
argument_list|(
name|sink
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
operator|(
name|byte
operator|)
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|write
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
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|buf
operator|.
name|length
condition|)
block|{
name|written
operator|+=
name|pos
expr_stmt|;
name|flush
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
name|buf
index|[
name|pos
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|arr
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
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|space
init|=
name|buf
operator|.
name|length
operator|-
name|pos
decl_stmt|;
if|if
condition|(
name|len
operator|<=
name|space
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|pos
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|len
operator|>
name|buf
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
comment|// flush
name|written
operator|+=
name|pos
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
comment|// don't buffer, just write to sink
name|flush
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|written
operator|+=
name|len
expr_stmt|;
return|return;
block|}
comment|// buffer is too big to fit in the free space, but
comment|// not big enough to warrant writing on its own.
comment|// write whatever we can fit, then flush and iterate.
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
name|off
argument_list|,
name|buf
argument_list|,
name|pos
argument_list|,
name|space
argument_list|)
expr_stmt|;
name|written
operator|+=
name|buf
operator|.
name|length
expr_stmt|;
comment|// important to do this first, since buf.length can change after a flush!
name|flush
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
name|off
operator|+=
name|space
expr_stmt|;
name|len
operator|-=
name|space
expr_stmt|;
block|}
block|}
comment|/** reserve at least len bytes at the end of the buffer.    * Invalid if len> buffer.length    */
DECL|method|reserve
specifier|public
name|void
name|reserve
parameter_list|(
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|len
operator|>
operator|(
name|buf
operator|.
name|length
operator|-
name|pos
operator|)
condition|)
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
comment|////////////////// DataOutput methods ///////////////////
annotation|@
name|Override
DECL|method|writeBoolean
specifier|public
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|v
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
name|void
name|writeByte
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
operator|(
name|byte
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeShort
specifier|public
name|void
name|writeShort
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|write
argument_list|(
operator|(
name|byte
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeChar
specifier|public
name|void
name|writeChar
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeShort
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|reserve
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|4
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|reserve
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|56
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|48
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|40
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|32
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|buf
index|[
name|pos
operator|+
literal|7
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|8
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
comment|// non-optimized version, but this shouldn't be used anyway
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
name|write
argument_list|(
operator|(
name|byte
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeChars
specifier|public
name|void
name|writeChars
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
comment|// non-optimized version
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
name|writeChar
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeUTF
specifier|public
name|void
name|writeUTF
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
comment|// non-optimized version, but this shouldn't be used anyway
name|DataOutputStream
name|daos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|daos
operator|.
name|writeUTF
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|flush
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
name|flushBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Only flushes the buffer of the FastOutputStream, not that of the    * underlying stream.    */
DECL|method|flushBuffer
specifier|public
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|written
operator|+=
name|pos
expr_stmt|;
name|flush
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** All writes to the sink will go through this method */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|byte
index|[]
name|buf
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
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|written
operator|+
name|pos
return|;
block|}
comment|/** Returns the number of bytes actually written to the underlying OutputStream, not including    * anything currently buffered by this class itself.    */
DECL|method|written
specifier|public
name|long
name|written
parameter_list|()
block|{
return|return
name|written
return|;
block|}
comment|/** Resets the count returned by written() */
DECL|method|setWritten
specifier|public
name|void
name|setWritten
parameter_list|(
name|long
name|written
parameter_list|)
block|{
name|this
operator|.
name|written
operator|=
name|written
expr_stmt|;
block|}
block|}
end_class
end_unit
