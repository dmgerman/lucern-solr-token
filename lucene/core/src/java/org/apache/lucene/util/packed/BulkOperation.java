begin_unit
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
end_comment
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
begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment
begin_class
DECL|class|BulkOperation
specifier|abstract
class|class
name|BulkOperation
implements|implements
name|PackedInts
operator|.
name|Decoder
implements|,
name|PackedInts
operator|.
name|Encoder
block|{
DECL|field|packedBulkOps
specifier|private
specifier|static
specifier|final
name|BulkOperation
index|[]
name|packedBulkOps
init|=
operator|new
name|BulkOperation
index|[]
block|{
operator|new
name|BulkOperationPacked1
argument_list|()
block|,
operator|new
name|BulkOperationPacked2
argument_list|()
block|,
operator|new
name|BulkOperationPacked3
argument_list|()
block|,
operator|new
name|BulkOperationPacked4
argument_list|()
block|,
operator|new
name|BulkOperationPacked5
argument_list|()
block|,
operator|new
name|BulkOperationPacked6
argument_list|()
block|,
operator|new
name|BulkOperationPacked7
argument_list|()
block|,
operator|new
name|BulkOperationPacked8
argument_list|()
block|,
operator|new
name|BulkOperationPacked9
argument_list|()
block|,
operator|new
name|BulkOperationPacked10
argument_list|()
block|,
operator|new
name|BulkOperationPacked11
argument_list|()
block|,
operator|new
name|BulkOperationPacked12
argument_list|()
block|,
operator|new
name|BulkOperationPacked13
argument_list|()
block|,
operator|new
name|BulkOperationPacked14
argument_list|()
block|,
operator|new
name|BulkOperationPacked15
argument_list|()
block|,
operator|new
name|BulkOperationPacked16
argument_list|()
block|,
operator|new
name|BulkOperationPacked17
argument_list|()
block|,
operator|new
name|BulkOperationPacked18
argument_list|()
block|,
operator|new
name|BulkOperationPacked19
argument_list|()
block|,
operator|new
name|BulkOperationPacked20
argument_list|()
block|,
operator|new
name|BulkOperationPacked21
argument_list|()
block|,
operator|new
name|BulkOperationPacked22
argument_list|()
block|,
operator|new
name|BulkOperationPacked23
argument_list|()
block|,
operator|new
name|BulkOperationPacked24
argument_list|()
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|25
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|26
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|27
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|28
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|29
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|30
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|31
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|32
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|33
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|34
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|35
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|36
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|37
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|38
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|39
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|40
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|41
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|42
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|43
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|44
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|45
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|46
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|47
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|48
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|49
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|50
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|51
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|52
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|53
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|54
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|55
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|56
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|57
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|58
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|59
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|60
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|61
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|62
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|63
argument_list|)
block|,
operator|new
name|BulkOperationPacked
argument_list|(
literal|64
argument_list|)
block|,   }
decl_stmt|;
comment|// NOTE: this is sparse (some entries are null):
DECL|field|packedSingleBlockBulkOps
specifier|private
specifier|static
specifier|final
name|BulkOperation
index|[]
name|packedSingleBlockBulkOps
init|=
operator|new
name|BulkOperation
index|[]
block|{
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|1
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|2
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|3
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|4
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|5
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|6
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|7
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|8
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|9
argument_list|)
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|10
argument_list|)
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|12
argument_list|)
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|16
argument_list|)
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|21
argument_list|)
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock
argument_list|(
literal|32
argument_list|)
block|,   }
decl_stmt|;
DECL|method|of
specifier|public
specifier|static
name|BulkOperation
name|of
parameter_list|(
name|PackedInts
operator|.
name|Format
name|format
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|PACKED
case|:
assert|assert
name|packedBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
operator|!=
literal|null
assert|;
return|return
name|packedBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
return|;
case|case
name|PACKED_SINGLE_BLOCK
case|:
assert|assert
name|packedSingleBlockBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
operator|!=
literal|null
assert|;
return|return
name|packedSingleBlockBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
DECL|method|writeLong
specifier|protected
name|int
name|writeLong
parameter_list|(
name|long
name|block
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
literal|8
condition|;
operator|++
name|j
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|block
operator|>>>
operator|(
literal|64
operator|-
operator|(
name|j
operator|<<
literal|3
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|blocksOffset
return|;
block|}
comment|/**    * For every number of bits per value, there is a minimum number of    * blocks (b) / values (v) you need to write in order to reach the next block    * boundary:    *  - 16 bits per value -&gt; b=2, v=1    *  - 24 bits per value -&gt; b=3, v=1    *  - 50 bits per value -&gt; b=25, v=4    *  - 63 bits per value -&gt; b=63, v=8    *  - ...    *    * A bulk read consists in copying<code>iterations*v</code> values that are    * contained in<code>iterations*b</code> blocks into a<code>long[]</code>    * (higher values of<code>iterations</code> are likely to yield a better    * throughput): this requires n * (b + 8v) bytes of memory.    *    * This method computes<code>iterations</code> as    *<code>ramBudget / (b + 8v)</code> (since a long is 8 bytes).    */
DECL|method|computeIterations
specifier|public
specifier|final
name|int
name|computeIterations
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|ramBudget
parameter_list|)
block|{
specifier|final
name|int
name|iterations
init|=
name|ramBudget
operator|/
operator|(
name|byteBlockCount
argument_list|()
operator|+
literal|8
operator|*
name|byteValueCount
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|iterations
operator|==
literal|0
condition|)
block|{
comment|// at least 1
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|iterations
operator|-
literal|1
operator|)
operator|*
name|byteValueCount
argument_list|()
operator|>=
name|valueCount
condition|)
block|{
comment|// don't allocate for more than the size of the reader
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|valueCount
operator|/
name|byteValueCount
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|iterations
return|;
block|}
block|}
block|}
end_class
end_unit
