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
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitUtil
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
name|BytesRef
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
name|UnicodeUtil
import|;
end_import
begin_comment
comment|/**  * Abstract base class for performing write operations of Lucene's low-level  * data types.    *<p>{@code DataOutput} may only be used from one thread, because it is not  * thread safe (it keeps internal state like file position).  */
end_comment
begin_class
DECL|class|DataOutput
specifier|public
specifier|abstract
class|class
name|DataOutput
block|{
comment|/** Writes a single byte.    *<p>    * The most primitive data type is an eight-bit byte. Files are     * accessed as sequences of bytes. All other data types are defined     * as sequences of bytes, so file formats are byte-order independent.    *     * @see IndexInput#readByte()    */
DECL|method|writeByte
specifier|public
specifier|abstract
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Writes an array of bytes.    * @param b the bytes to write    * @param length the number of bytes to write    * @see DataInput#readBytes(byte[],int,int)    */
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
name|writeBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Writes an array of bytes.    * @param b the bytes to write    * @param offset the offset in the byte array    * @param length the number of bytes to write    * @see DataInput#readBytes(byte[],int,int)    */
DECL|method|writeBytes
specifier|public
specifier|abstract
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
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Writes an int as four bytes.    *<p>    * 32-bit unsigned integer written as four bytes, high-order bytes first.    *     * @see DataInput#readInt()    */
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|24
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes a short as two bytes.    * @see DataInput#readShort()    */
DECL|method|writeShort
specifier|public
name|void
name|writeShort
parameter_list|(
name|short
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes an int in a variable-length format.  Writes between one and    * five bytes.  Smaller values take fewer bytes.  Negative numbers are    * supported, but should be avoided.    *<p>VByte is a variable-length format for positive integers is defined where the    * high-order bit of each byte indicates whether more bytes remain to be read. The    * low-order seven bits are appended as increasingly more significant bits in the    * resulting integer value. Thus values from zero to 127 may be stored in a single    * byte, values from 128 to 16,383 may be stored in two bytes, and so on.</p>    *<p>VByte Encoding Example</p>    *<table cellspacing="0" cellpadding="2" border="0" summary="variable length encoding examples">    *<col width="64*">    *<col width="64*">    *<col width="64*">    *<col width="64*">    *<tr valign="top">    *<th align="left" width="25%">Value</th>    *<th align="left" width="25%">Byte 1</th>    *<th align="left" width="25%">Byte 2</th>    *<th align="left" width="25%">Byte 3</th>    *</tr>    *<tr valign="bottom">    *<td width="25%">0</td>    *<td width="25%"><kbd>00000000</kbd></td>    *<td width="25%"></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">1</td>    *<td width="25%"><kbd>00000001</kbd></td>    *<td width="25%"></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">2</td>    *<td width="25%"><kbd>00000010</kbd></td>    *<td width="25%"></td>    *<td width="25%"></td>    *</tr>    *<tr>    *<td valign="top" width="25%">...</td>    *<td valign="bottom" width="25%"></td>    *<td valign="bottom" width="25%"></td>    *<td valign="bottom" width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">127</td>    *<td width="25%"><kbd>01111111</kbd></td>    *<td width="25%"></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">128</td>    *<td width="25%"><kbd>10000000</kbd></td>    *<td width="25%"><kbd>00000001</kbd></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">129</td>    *<td width="25%"><kbd>10000001</kbd></td>    *<td width="25%"><kbd>00000001</kbd></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">130</td>    *<td width="25%"><kbd>10000010</kbd></td>    *<td width="25%"><kbd>00000001</kbd></td>    *<td width="25%"></td>    *</tr>    *<tr>    *<td valign="top" width="25%">...</td>    *<td width="25%"></td>    *<td width="25%"></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">16,383</td>    *<td width="25%"><kbd>11111111</kbd></td>    *<td width="25%"><kbd>01111111</kbd></td>    *<td width="25%"></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">16,384</td>    *<td width="25%"><kbd>10000000</kbd></td>    *<td width="25%"><kbd>10000000</kbd></td>    *<td width="25%"><kbd>00000001</kbd></td>    *</tr>    *<tr valign="bottom">    *<td width="25%">16,385</td>    *<td width="25%"><kbd>10000001</kbd></td>    *<td width="25%"><kbd>10000000</kbd></td>    *<td width="25%"><kbd>00000001</kbd></td>    *</tr>    *<tr>    *<td valign="top" width="25%">...</td>    *<td valign="bottom" width="25%"></td>    *<td valign="bottom" width="25%"></td>    *<td valign="bottom" width="25%"></td>    *</tr>    *</table>    *<p>This provides compression while still being efficient to decode.</p>    *     * @param i Smaller values take fewer bytes.  Negative numbers are    * supported, but should be avoided.    * @throws IOException If there is an I/O error writing to the underlying medium.    * @see DataInput#readVInt()    */
DECL|method|writeVInt
specifier|public
specifier|final
name|void
name|writeVInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7F
operator|)
operator|!=
literal|0
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7F
operator|)
operator||
literal|0x80
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write a {@link BitUtil#zigZagEncode(int) zig-zag}-encoded    * {@link #writeVInt(int) variable-length} integer. This is typically useful    * to write small signed ints and is equivalent to calling    *<code>writeVInt(BitUtil.zigZagEncode(i))</code>.    * @see DataInput#readZInt()    */
DECL|method|writeZInt
specifier|public
specifier|final
name|void
name|writeZInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|BitUtil
operator|.
name|zigZagEncode
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Writes a long as eight bytes.    *<p>    * 64-bit unsigned integer written as eight bytes, high-order bytes first.    *     * @see DataInput#readLong()    */
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|i
operator|>>
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes an long in a variable-length format.  Writes between one and nine    * bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    *<p>    * The format is described further in {@link DataOutput#writeVInt(int)}.    * @see DataInput#readVLong()    */
DECL|method|writeVLong
specifier|public
specifier|final
name|void
name|writeVLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|i
operator|>=
literal|0L
assert|;
name|writeNegativeVLong
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// write a pontentially negative vLong
DECL|method|writeNegativeVLong
specifier|private
name|void
name|writeNegativeVLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7FL
operator|)
operator|!=
literal|0L
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7FL
operator|)
operator||
literal|0x80L
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write a {@link BitUtil#zigZagEncode(long) zig-zag}-encoded    * {@link #writeVLong(long) variable-length} long. Writes between one and ten    * bytes. This is typically useful to write small signed ints.    * @see DataInput#readZLong()    */
DECL|method|writeZLong
specifier|public
specifier|final
name|void
name|writeZLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeNegativeVLong
argument_list|(
name|BitUtil
operator|.
name|zigZagEncode
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Writes a string.    *<p>    * Writes strings as UTF-8 encoded bytes. First the length, in bytes, is    * written as a {@link #writeVInt VInt}, followed by the bytes.    *     * @see DataInput#readString()    */
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|utf8Result
init|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|writeVInt
argument_list|(
name|utf8Result
operator|.
name|length
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
name|utf8Result
operator|.
name|bytes
argument_list|,
name|utf8Result
operator|.
name|offset
argument_list|,
name|utf8Result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|field|COPY_BUFFER_SIZE
specifier|private
specifier|static
name|int
name|COPY_BUFFER_SIZE
init|=
literal|16384
decl_stmt|;
DECL|field|copyBuffer
specifier|private
name|byte
index|[]
name|copyBuffer
decl_stmt|;
comment|/** Copy numBytes bytes from input to ourself. */
DECL|method|copyBytes
specifier|public
name|void
name|copyBytes
parameter_list|(
name|DataInput
name|input
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|numBytes
operator|>=
literal|0
operator|:
literal|"numBytes="
operator|+
name|numBytes
assert|;
name|long
name|left
init|=
name|numBytes
decl_stmt|;
if|if
condition|(
name|copyBuffer
operator|==
literal|null
condition|)
name|copyBuffer
operator|=
operator|new
name|byte
index|[
name|COPY_BUFFER_SIZE
index|]
expr_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|toCopy
decl_stmt|;
if|if
condition|(
name|left
operator|>
name|COPY_BUFFER_SIZE
condition|)
name|toCopy
operator|=
name|COPY_BUFFER_SIZE
expr_stmt|;
else|else
name|toCopy
operator|=
operator|(
name|int
operator|)
name|left
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|copyBuffer
argument_list|,
literal|0
argument_list|,
name|toCopy
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
name|copyBuffer
argument_list|,
literal|0
argument_list|,
name|toCopy
argument_list|)
expr_stmt|;
name|left
operator|-=
name|toCopy
expr_stmt|;
block|}
block|}
comment|/**    * Writes a String map.    *<p>    * First the size is written as an {@link #writeInt(int) Int32},    * followed by each key-value pair written as two consecutive     * {@link #writeString(String) String}s.    *     * @param map Input map. May be null (equivalent to an empty map)    */
DECL|method|writeStringStringMap
specifier|public
name|void
name|writeStringStringMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeInt
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Writes a String set.    *<p>    * First the size is written as an {@link #writeInt(int) Int32},    * followed by each value written as a    * {@link #writeString(String) String}.    *     * @param set Input set. May be null (equivalent to an empty set)    */
DECL|method|writeStringSet
specifier|public
name|void
name|writeStringSet
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeInt
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|set
control|)
block|{
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
