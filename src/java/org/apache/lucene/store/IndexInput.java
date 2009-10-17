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
name|HashMap
import|;
end_import
begin_comment
comment|/** Abstract base class for input from a file in a {@link Directory}.  A  * random-access input stream.  Used for all Lucene index input operations.  * @see Directory  */
end_comment
begin_class
DECL|class|IndexInput
specifier|public
specifier|abstract
class|class
name|IndexInput
implements|implements
name|Cloneable
block|{
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
comment|// used by readString()
DECL|field|chars
specifier|private
name|char
index|[]
name|chars
decl_stmt|;
comment|// used by readModifiedUTF8String()
DECL|field|preUTF8Strings
specifier|private
name|boolean
name|preUTF8Strings
decl_stmt|;
comment|// true if we are reading old (modified UTF8) string format
comment|/** Reads and returns a single byte.    * @see IndexOutput#writeByte(byte)    */
DECL|method|readByte
specifier|public
specifier|abstract
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Reads a specified number of bytes into an array at the specified offset.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @see IndexOutput#writeBytes(byte[],int)    */
DECL|method|readBytes
specifier|public
specifier|abstract
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
function_decl|;
comment|/** Reads a specified number of bytes into an array at the    * specified offset with control over whether the read    * should be buffered (callers who have their own buffer    * should pass in "false" for useBuffer).  Currently only    * {@link BufferedIndexInput} respects this parameter.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @param useBuffer set to false if the caller will handle    * buffering.    * @see IndexOutput#writeBytes(byte[],int)    */
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
parameter_list|,
name|boolean
name|useBuffer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Default to ignoring useBuffer entirely
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/** Reads four bytes and returns an int.    * @see IndexOutput#writeInt(int)    */
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
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
return|;
block|}
comment|/** Reads an int stored in variable-length format.  Reads between one and    * five bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    * @see IndexOutput#writeVInt(int)    */
DECL|method|readVInt
specifier|public
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/** Reads eight bytes and returns a long.    * @see IndexOutput#writeLong(long)    */
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
name|readInt
argument_list|()
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|readInt
argument_list|()
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
comment|/** Reads a long stored in variable-length format.  Reads between one and    * nine bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported. */
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|long
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/** Call this if readString should read characters stored    *  in the old modified UTF8 format (length in java chars    *  and java's modified UTF8 encoding).  This is used for    *  indices written pre-2.4 See LUCENE-510 for details. */
DECL|method|setModifiedUTF8StringsMode
specifier|public
name|void
name|setModifiedUTF8StringsMode
parameter_list|()
block|{
name|preUTF8Strings
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Reads a string.    * @see IndexOutput#writeString(String)    */
DECL|method|readString
specifier|public
name|String
name|readString
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|preUTF8Strings
condition|)
return|return
name|readModifiedUTF8String
argument_list|()
return|;
name|int
name|length
init|=
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|length
operator|>
name|bytes
operator|.
name|length
condition|)
name|bytes
operator|=
operator|new
name|byte
index|[
call|(
name|int
call|)
argument_list|(
name|length
operator|*
literal|1.25
argument_list|)
index|]
expr_stmt|;
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|method|readModifiedUTF8String
specifier|private
name|String
name|readModifiedUTF8String
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|chars
operator|==
literal|null
operator|||
name|length
operator|>
name|chars
operator|.
name|length
condition|)
name|chars
operator|=
operator|new
name|char
index|[
name|length
index|]
expr_stmt|;
name|readChars
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/** Reads Lucene's old "modified UTF-8" encoded    *  characters into an array.    * @param buffer the array to read characters into    * @param start the offset in the array to start storing characters    * @param length the number of characters to read    * @see IndexOutput#writeChars(String,int,int)    * @deprecated -- please use readString or readBytes    *                instead, and construct the string    *                from those utf8 bytes    */
DECL|method|readChars
specifier|public
name|void
name|readChars
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|b
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0xE0
operator|)
operator|!=
literal|0xE0
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert    *     * Similar to {@link #readChars(char[], int, int)} but does not do any conversion operations on the bytes it is reading in.  It still    * has to invoke {@link #readByte()} just as {@link #readChars(char[], int, int)} does, but it does not need a buffer to store anything    * and it does not have to do any of the bitwise operations, since we don't actually care what is in the byte except to determine    * how many more bytes to read    * @param length The number of chars to read    * @deprecated this method operates on old "modified utf8" encoded    *             strings    */
DECL|method|skipChars
specifier|public
name|void
name|skipChars
parameter_list|(
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
comment|//do nothing, we only need one byte
block|}
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0xE0
operator|)
operator|!=
literal|0xE0
condition|)
block|{
name|readByte
argument_list|()
expr_stmt|;
comment|//read an additional byte
block|}
else|else
block|{
comment|//read two additional bytes.
name|readByte
argument_list|()
expr_stmt|;
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Closes the stream to further operations. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current position in this file, where the next read will    * occur.    * @see #seek(long)    */
DECL|method|getFilePointer
specifier|public
specifier|abstract
name|long
name|getFilePointer
parameter_list|()
function_decl|;
comment|/** Sets current position in this file, where the next read will occur.    * @see #getFilePointer()    */
DECL|method|seek
specifier|public
specifier|abstract
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** The number of bytes in the file. */
DECL|method|length
specifier|public
specifier|abstract
name|long
name|length
parameter_list|()
function_decl|;
comment|/** Returns a clone of this stream.    *    *<p>Clones of a stream access the same data, and are positioned at the same    * point as the stream they were cloned from.    *    *<p>Expert: Subclasses must ensure that clones may be positioned at    * different points in the input from each other and from the stream they    * were cloned from.    */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|IndexInput
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|IndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{}
name|clone
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
name|clone
operator|.
name|chars
operator|=
literal|null
expr_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|readStringStringMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readStringStringMap
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|readInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|key
init|=
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|val
init|=
name|readString
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
end_class
end_unit
