begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|search
operator|.
name|DocIdSet
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
name|search
operator|.
name|DocIdSetIterator
import|;
end_import
begin_comment
comment|/**  * A bit set that only stores longs that have at least one bit which is set.  * The way it works is that the space of bits is divided into blocks of  * 4096 bits, which is 64 longs. Then for each block, we have:<ul>  *<li>a long[] which stores the non-zero longs for that block</li>  *<li>a long so that bit<tt>i</tt> being set means that the<code>i-th</code>  *     long of the block is non-null, and its offset in the array of longs is  *     the number of one bits on the right of the<code>i-th</code> bit.</li></ul>  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|SparseFixedBitSet
specifier|public
class|class
name|SparseFixedBitSet
extends|extends
name|DocIdSet
implements|implements
name|Bits
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|SparseFixedBitSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SINGLE_ELEMENT_ARRAY_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|SINGLE_ELEMENT_ARRAY_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
operator|new
name|long
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
DECL|method|blockCount
specifier|private
specifier|static
name|int
name|blockCount
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|int
name|blockCount
init|=
name|length
operator|>>>
literal|12
decl_stmt|;
if|if
condition|(
operator|(
name|blockCount
operator|<<
literal|12
operator|)
operator|<
name|length
condition|)
block|{
operator|++
name|blockCount
expr_stmt|;
block|}
assert|assert
operator|(
name|blockCount
operator|<<
literal|12
operator|)
operator|>=
name|length
assert|;
return|return
name|blockCount
return|;
block|}
DECL|field|indices
specifier|final
name|long
index|[]
name|indices
decl_stmt|;
DECL|field|bits
specifier|final
name|long
index|[]
index|[]
name|bits
decl_stmt|;
DECL|field|length
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|nonZeroLongCount
name|int
name|nonZeroLongCount
decl_stmt|;
DECL|field|ramBytesUsed
name|long
name|ramBytesUsed
decl_stmt|;
comment|/** Create a {@link SparseFixedBitSet} that can contain bits between    *<code>0</code> included and<code>length</code> excluded. */
DECL|method|SparseFixedBitSet
specifier|public
name|SparseFixedBitSet
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length needs to be>= 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
specifier|final
name|int
name|blockCount
init|=
name|blockCount
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|indices
operator|=
operator|new
name|long
index|[
name|blockCount
index|]
expr_stmt|;
name|bits
operator|=
operator|new
name|long
index|[
name|blockCount
index|]
index|[]
expr_stmt|;
name|ramBytesUsed
operator|=
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|indices
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|Bits
name|bits
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
DECL|method|consistent
specifier|private
name|boolean
name|consistent
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|length
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|",length="
operator|+
name|length
assert|;
return|return
literal|true
return|;
block|}
comment|/**    * Compute the cardinality of this set.    * NOTE: this operation runs in linear time.    */
DECL|method|cardinality
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
name|int
name|cardinality
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
index|[]
name|bitArray
range|:
name|bits
control|)
block|{
if|if
condition|(
name|bitArray
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|long
name|bits
range|:
name|bitArray
control|)
block|{
name|cardinality
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|cardinality
return|;
block|}
comment|/**    * Return an approximation of the cardinality of this set, assuming that bits    * are uniformly distributed. This operation runs in constant time.    */
DECL|method|approximateCardinality
specifier|public
name|int
name|approximateCardinality
parameter_list|()
block|{
comment|// this is basically the linear counting algorithm
specifier|final
name|int
name|totalLongs
init|=
operator|(
name|length
operator|+
literal|63
operator|)
operator|>>>
literal|6
decl_stmt|;
comment|// total number of longs in the space
assert|assert
name|totalLongs
operator|>=
name|nonZeroLongCount
assert|;
specifier|final
name|int
name|zeroLongs
init|=
name|totalLongs
operator|-
name|nonZeroLongCount
decl_stmt|;
comment|// number of longs that are zeros
comment|// No need to guard against division by zero, it will return +Infinity and things will work as expected
specifier|final
name|long
name|estimate
init|=
name|Math
operator|.
name|round
argument_list|(
name|totalLongs
operator|*
name|Math
operator|.
name|log
argument_list|(
operator|(
name|double
operator|)
name|totalLongs
operator|/
name|zeroLongs
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|estimate
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|consistent
argument_list|(
name|i
argument_list|)
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
comment|// first check the index, if the i64-th bit is not set, then i is not set
comment|// note: this relies on the fact that shifts are mod 64 in java
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// if it is set, then we count the number of bits that are set on the right
comment|// of i64, and that gives us the index of the long that stores the bits we
comment|// are interested in
specifier|final
name|long
name|bits
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
index|[
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
index|]
decl_stmt|;
return|return
operator|(
name|bits
operator|&
operator|(
literal|1L
operator|<<
name|i
operator|)
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|oversize
specifier|private
specifier|static
name|int
name|oversize
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|int
name|newSize
init|=
name|s
operator|+
operator|(
name|s
operator|>>>
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|newSize
operator|>
literal|50
condition|)
block|{
name|newSize
operator|=
literal|64
expr_stmt|;
block|}
return|return
name|newSize
return|;
block|}
comment|/**    * Set the bit at index<tt>i</tt>.    */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|consistent
argument_list|(
name|i
argument_list|)
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// in that case the sub 64-bits block we are interested in already exists,
comment|// we just need to set a bit in an existing long: the number of ones on
comment|// the right of i64 gives us the index of the long we need to update
name|bits
index|[
name|i4096
index|]
index|[
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
index|]
operator||=
literal|1L
operator|<<
name|i
expr_stmt|;
comment|// shifts are mod 64 in java
block|}
elseif|else
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
comment|// if the index is 0, it means that we just found a block of 4096 bits
comment|// that has no bit that is set yet. So let's initialize a new block:
name|insertBlock
argument_list|(
name|i4096
argument_list|,
name|i64
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// in that case we found a block of 4096 bits that has some values, but
comment|// the sub-block of 64 bits that we are interested in has no value yet,
comment|// so we need to insert a new long
name|insertLong
argument_list|(
name|i4096
argument_list|,
name|i64
argument_list|,
name|i
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insertBlock
specifier|private
name|void
name|insertBlock
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|i64
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|indices
index|[
name|i4096
index|]
operator|=
literal|1L
operator|<<
name|i64
expr_stmt|;
comment|// shifts are mod 64 in java
assert|assert
name|bits
index|[
name|i4096
index|]
operator|==
literal|null
assert|;
name|bits
index|[
name|i4096
index|]
operator|=
operator|new
name|long
index|[]
block|{
literal|1L
operator|<<
name|i
block|}
expr_stmt|;
comment|// shifts are mod 64 in java
operator|++
name|nonZeroLongCount
expr_stmt|;
name|ramBytesUsed
operator|+=
name|SINGLE_ELEMENT_ARRAY_BYTES_USED
expr_stmt|;
block|}
DECL|method|insertLong
specifier|private
name|void
name|insertLong
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|i64
parameter_list|,
name|int
name|i
parameter_list|,
name|long
name|index
parameter_list|)
block|{
name|indices
index|[
name|i4096
index|]
operator||=
literal|1L
operator|<<
name|i64
expr_stmt|;
comment|// shifts are mod 64 in java
comment|// we count the number of bits that are set on the right of i64
comment|// this gives us the index at which to perform the insertion
specifier|final
name|int
name|o
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|bitArray
init|=
name|bits
index|[
name|i4096
index|]
decl_stmt|;
if|if
condition|(
name|bitArray
index|[
name|bitArray
operator|.
name|length
operator|-
literal|1
index|]
operator|==
literal|0
condition|)
block|{
comment|// since we only store non-zero longs, if the last value is 0, it means
comment|// that we alreay have extra space, make use of it
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
name|o
argument_list|,
name|bitArray
argument_list|,
name|o
operator|+
literal|1
argument_list|,
name|bitArray
operator|.
name|length
operator|-
name|o
operator|-
literal|1
argument_list|)
expr_stmt|;
name|bitArray
index|[
name|o
index|]
operator|=
literal|1L
operator|<<
name|i
expr_stmt|;
block|}
else|else
block|{
comment|// we don't have extra space so we need to resize to insert the new long
specifier|final
name|int
name|newSize
init|=
name|oversize
argument_list|(
name|bitArray
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|newBitArray
init|=
operator|new
name|long
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
literal|0
argument_list|,
name|newBitArray
argument_list|,
literal|0
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|newBitArray
index|[
name|o
index|]
operator|=
literal|1L
operator|<<
name|i
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
name|o
argument_list|,
name|newBitArray
argument_list|,
name|o
operator|+
literal|1
argument_list|,
name|bitArray
operator|.
name|length
operator|-
name|o
argument_list|)
expr_stmt|;
name|bits
index|[
name|i4096
index|]
operator|=
name|newBitArray
expr_stmt|;
name|ramBytesUsed
operator|+=
operator|(
name|newSize
operator|-
name|bitArray
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
expr_stmt|;
block|}
operator|++
name|nonZeroLongCount
expr_stmt|;
block|}
comment|/**    * Add the documents contained in the provided {@link DocIdSetIterator} to    * this bit set.    */
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Iterator
argument_list|()
return|;
block|}
DECL|class|Iterator
class|class
name|Iterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|cost
specifier|private
name|int
name|cost
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|/** Return the first document that occurs on or after the provided block index. */
DECL|method|firstDoc
specifier|private
name|int
name|firstDoc
parameter_list|(
name|int
name|i4096
parameter_list|)
block|{
name|long
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i4096
operator|<
name|indices
operator|.
name|length
condition|)
block|{
name|index
operator|=
name|indices
index|[
name|i4096
index|]
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|i64
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|doc
operator|=
operator|(
name|i4096
operator|<<
literal|12
operator|)
operator||
operator|(
name|i64
operator|<<
literal|6
operator|)
operator||
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
index|[
name|i4096
index|]
index|[
literal|0
index|]
argument_list|)
return|;
block|}
name|i4096
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|i4096
init|=
name|target
operator|>>>
literal|12
decl_stmt|;
if|if
condition|(
name|i4096
operator|>=
name|indices
operator|.
name|length
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
name|int
name|i64
init|=
name|target
operator|>>>
literal|6
decl_stmt|;
name|long
name|indexBits
init|=
name|index
operator|>>>
name|i64
decl_stmt|;
if|if
condition|(
name|indexBits
operator|==
literal|0
condition|)
block|{
comment|// if the index is zero, it means that there is no value in the
comment|// current block, so return the first document of the next block
comment|// or
comment|// if neither the i64-th bit or any other bit on its left is set then
comment|// it means that there are no more documents in this block, go to the
comment|// next one
return|return
name|firstDoc
argument_list|(
name|i4096
operator|+
literal|1
argument_list|)
return|;
block|}
else|else
block|{
comment|// We know we still have some 64-bits blocks that have bits set, let's
comment|// advance to the next one by skipping trailing zeros of the index
name|int
name|i1
init|=
name|target
operator|&
literal|0x3F
decl_stmt|;
name|int
name|trailingZeros
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|indexBits
argument_list|)
decl_stmt|;
if|if
condition|(
name|trailingZeros
operator|!=
literal|0
condition|)
block|{
comment|// no bits in the current long, go to the next one
name|i64
operator|+=
name|trailingZeros
expr_stmt|;
name|i1
operator|=
literal|0
expr_stmt|;
block|}
comment|// So now we are on a sub 64-bits block that has values
assert|assert
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|!=
literal|0
assert|;
comment|// we count the number of ones on the left of i64 to figure out the
comment|// index of the long that contains the bits we are interested in
name|int
name|longIndex
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
comment|// shifts are mod 64 in java
specifier|final
name|long
index|[]
name|longArray
init|=
name|bits
index|[
name|i4096
index|]
decl_stmt|;
assert|assert
name|longArray
index|[
name|longIndex
index|]
operator|!=
literal|0
assert|;
name|long
name|bits
init|=
name|longArray
index|[
name|longIndex
index|]
operator|>>>
name|i1
decl_stmt|;
comment|// shifts are mod 64 in java
if|if
condition|(
name|bits
operator|!=
literal|0L
condition|)
block|{
comment|// hurray, we found some non-zero bits, this gives us the next document:
name|i1
operator|+=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
argument_list|)
expr_stmt|;
return|return
name|doc
operator|=
operator|(
name|i4096
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|i64
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
name|i1
return|;
block|}
comment|// otherwise it means that although we were on a sub-64 block that contains
comment|// documents, all documents of this sub-block have already been consumed
comment|// so two cases:
name|indexBits
operator|=
name|index
operator|>>>
name|i64
operator|>>>
literal|1
expr_stmt|;
comment|// we don't shift by (i64+1) otherwise we might shift by a multiple of 64 which is a no-op
if|if
condition|(
name|indexBits
operator|==
literal|0
condition|)
block|{
comment|// Case 1: this was the last long of the block of 4096 bits, then go
comment|// to the next block
return|return
name|firstDoc
argument_list|(
name|i4096
operator|+
literal|1
argument_list|)
return|;
block|}
comment|// Case 2: go to the next sub 64-bits block in the current block of 4096 bits
comment|// by skipping trailing zeros of the index
name|trailingZeros
operator|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|indexBits
argument_list|)
expr_stmt|;
name|i64
operator|+=
literal|1
operator|+
name|trailingZeros
expr_stmt|;
name|bits
operator|=
name|longArray
index|[
name|longIndex
operator|+
literal|1
index|]
expr_stmt|;
assert|assert
name|bits
operator|!=
literal|0
assert|;
name|i1
operator|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
argument_list|)
expr_stmt|;
return|return
name|doc
operator|=
operator|(
name|i4096
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|i64
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
name|i1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
comment|// although constant-time, approximateCardinality is a bit expensive so
comment|// we cache it to avoid performance traps eg. when sorting iterators by
comment|// cost
if|if
condition|(
name|cost
operator|<
literal|0
condition|)
block|{
name|cost
operator|=
name|approximateCardinality
argument_list|()
expr_stmt|;
block|}
assert|assert
name|cost
operator|>=
literal|0
assert|;
return|return
name|cost
return|;
block|}
block|}
block|}
end_class
end_unit
