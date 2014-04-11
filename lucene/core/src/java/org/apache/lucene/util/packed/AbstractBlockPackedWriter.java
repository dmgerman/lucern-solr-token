begin_unit
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
operator|.
name|PackedInts
operator|.
name|checkBlockSize
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
name|util
operator|.
name|Arrays
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
name|DataOutput
import|;
end_import
begin_class
DECL|class|AbstractBlockPackedWriter
specifier|abstract
class|class
name|AbstractBlockPackedWriter
block|{
DECL|field|MIN_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|MIN_BLOCK_SIZE
init|=
literal|64
decl_stmt|;
DECL|field|MAX_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|MAX_BLOCK_SIZE
init|=
literal|1
operator|<<
operator|(
literal|30
operator|-
literal|3
operator|)
decl_stmt|;
DECL|field|MIN_VALUE_EQUALS_0
specifier|static
specifier|final
name|int
name|MIN_VALUE_EQUALS_0
init|=
literal|1
operator|<<
literal|0
decl_stmt|;
DECL|field|BPV_SHIFT
specifier|static
specifier|final
name|int
name|BPV_SHIFT
init|=
literal|1
decl_stmt|;
comment|// same as DataOutput.writeVLong but accepts negative values
DECL|method|writeVLong
specifier|static
name|void
name|writeVLong
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|k
init|=
literal|0
decl_stmt|;
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
operator|&&
name|k
operator|++
operator|<
literal|8
condition|)
block|{
name|out
operator|.
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
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
DECL|field|out
specifier|protected
name|DataOutput
name|out
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|long
index|[]
name|values
decl_stmt|;
DECL|field|blocks
specifier|protected
name|byte
index|[]
name|blocks
decl_stmt|;
DECL|field|off
specifier|protected
name|int
name|off
decl_stmt|;
DECL|field|ord
specifier|protected
name|long
name|ord
decl_stmt|;
DECL|field|finished
specifier|protected
name|boolean
name|finished
decl_stmt|;
comment|/**    * Sole constructor.    * @param blockSize the number of values of a single block, must be a multiple of<tt>64</tt>    */
DECL|method|AbstractBlockPackedWriter
specifier|public
name|AbstractBlockPackedWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|checkBlockSize
argument_list|(
name|blockSize
argument_list|,
name|MIN_BLOCK_SIZE
argument_list|,
name|MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[
name|blockSize
index|]
expr_stmt|;
block|}
comment|/** Reset this writer to wrap<code>out</code>. The block size remains unchanged. */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|DataOutput
name|out
parameter_list|)
block|{
assert|assert
name|out
operator|!=
literal|null
assert|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|off
operator|=
literal|0
expr_stmt|;
name|ord
operator|=
literal|0L
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|checkNotFinished
specifier|private
name|void
name|checkNotFinished
parameter_list|()
block|{
if|if
condition|(
name|finished
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already finished"
argument_list|)
throw|;
block|}
block|}
comment|/** Append a new long. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotFinished
argument_list|()
expr_stmt|;
if|if
condition|(
name|off
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|off
operator|++
index|]
operator|=
name|l
expr_stmt|;
operator|++
name|ord
expr_stmt|;
block|}
comment|// For testing only
DECL|method|addBlockOfZeros
name|void
name|addBlockOfZeros
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotFinished
argument_list|()
expr_stmt|;
if|if
condition|(
name|off
operator|!=
literal|0
operator|&&
name|off
operator|!=
name|values
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|""
operator|+
name|off
argument_list|)
throw|;
block|}
if|if
condition|(
name|off
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|off
operator|=
name|values
operator|.
name|length
expr_stmt|;
name|ord
operator|+=
name|values
operator|.
name|length
expr_stmt|;
block|}
comment|/** Flush all buffered data to disk. This instance is not usable anymore    *  after this method has been called until {@link #reset(DataOutput)} has    *  been called. */
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotFinished
argument_list|()
expr_stmt|;
if|if
condition|(
name|off
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Return the number of values which have been added. */
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
name|ord
return|;
block|}
DECL|method|flush
specifier|protected
specifier|abstract
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|writeValues
specifier|protected
specifier|final
name|void
name|writeValues
parameter_list|(
name|int
name|bitsRequired
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|PackedInts
operator|.
name|Encoder
name|encoder
init|=
name|PackedInts
operator|.
name|getEncoder
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|values
operator|.
name|length
operator|/
name|encoder
operator|.
name|byteValueCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|blockSize
init|=
name|encoder
operator|.
name|byteBlockCount
argument_list|()
operator|*
name|iterations
decl_stmt|;
if|if
condition|(
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|<
name|blockSize
condition|)
block|{
name|blocks
operator|=
operator|new
name|byte
index|[
name|blockSize
index|]
expr_stmt|;
block|}
if|if
condition|(
name|off
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
name|off
argument_list|,
name|values
operator|.
name|length
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|blocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
specifier|final
name|int
name|blockCount
init|=
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|off
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|blocks
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
