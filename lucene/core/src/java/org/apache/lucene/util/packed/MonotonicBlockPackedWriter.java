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
begin_comment
comment|/**  * A writer for large monotonically increasing sequences of positive longs.  *<p>  * The sequence is divided into fixed-size blocks and for each block, values  * are modeled after a linear function f: x&rarr; A&times; x + B. The block  * encodes deltas from the expected values computed from this function using as  * few bits as possible. Each block has an overhead between 6 and 14 bytes.  *<p>  * Format:  *<ul>  *<li>&lt;BLock&gt;<sup>BlockCount</sup>  *<li>BlockCount:&lceil; ValueCount / BlockSize&rceil;  *<li>Block:&lt;Header, (Ints)&gt;  *<li>Header:&lt;B, A, BitsPerValue&gt;  *<li>B: the B from f: x&rarr; A&times; x + B using a  *     {@link DataOutput#writeVLong(long) variable-length long}  *<li>A: the A from f: x&rarr; A&times; x + B encoded using  *     {@link Float#floatToIntBits(float)} on  *     {@link DataOutput#writeInt(int) 4 bytes}  *<li>BitsPerValue: a {@link DataOutput#writeVInt(int) variable-length int}  *<li>Ints: if BitsPerValue is<tt>0</tt>, then there is nothing to read and  *     all values perfectly match the result of the function. Otherwise, these  *     are the  *<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">zigzag-encoded</a>  *     {@link PackedInts packed} deltas from the expected value (computed from  *     the function) using exaclty BitsPerValue bits per value  *</ul>  * @see MonotonicBlockPackedReader  * @lucene.internal  */
end_comment
begin_class
DECL|class|MonotonicBlockPackedWriter
specifier|public
specifier|final
class|class
name|MonotonicBlockPackedWriter
extends|extends
name|AbstractBlockPackedWriter
block|{
comment|/**    * Sole constructor.    * @param blockSize the number of values of a single block, must be a power of 2    */
DECL|method|MonotonicBlockPackedWriter
specifier|public
name|MonotonicBlockPackedWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|blockSize
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
assert|assert
name|l
operator|>=
literal|0
assert|;
name|super
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
specifier|protected
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|off
operator|>
literal|0
assert|;
comment|// TODO: perform a true linear regression?
specifier|final
name|long
name|min
init|=
name|values
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|float
name|avg
init|=
name|off
operator|==
literal|1
condition|?
literal|0f
else|:
call|(
name|float
call|)
argument_list|(
name|values
index|[
name|off
operator|-
literal|1
index|]
operator|-
name|min
argument_list|)
operator|/
operator|(
name|off
operator|-
literal|1
operator|)
decl_stmt|;
name|long
name|maxZigZagDelta
init|=
literal|0
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
name|off
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|zigZagEncode
argument_list|(
name|values
index|[
name|i
index|]
operator|-
name|min
operator|-
call|(
name|long
call|)
argument_list|(
name|avg
operator|*
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|maxZigZagDelta
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxZigZagDelta
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVLong
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|avg
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxZigZagDelta
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|bitsRequired
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxZigZagDelta
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|bitsRequired
argument_list|)
expr_stmt|;
name|writeValues
argument_list|(
name|bitsRequired
argument_list|)
expr_stmt|;
block|}
name|off
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class
end_unit
