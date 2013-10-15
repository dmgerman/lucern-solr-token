begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BroadWord
import|;
end_import
begin_comment
comment|// bit selection in long
end_comment
begin_comment
comment|/** A decoder for an {@link EliasFanoEncoder}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|EliasFanoDecoder
specifier|public
class|class
name|EliasFanoDecoder
block|{
DECL|field|LOG2_LONG_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|LOG2_LONG_SIZE
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|Long
operator|.
name|SIZE
argument_list|)
decl_stmt|;
DECL|field|efEncoder
specifier|private
specifier|final
name|EliasFanoEncoder
name|efEncoder
decl_stmt|;
DECL|field|numEncoded
specifier|private
specifier|final
name|long
name|numEncoded
decl_stmt|;
DECL|field|efIndex
specifier|private
name|long
name|efIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|// the decoding index.
DECL|field|setBitForIndex
specifier|private
name|long
name|setBitForIndex
init|=
operator|-
literal|1
decl_stmt|;
comment|// the index of the high bit at the decoding index.
DECL|field|NO_MORE_VALUES
specifier|public
specifier|final
specifier|static
name|long
name|NO_MORE_VALUES
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|numIndexEntries
specifier|private
specifier|final
name|long
name|numIndexEntries
decl_stmt|;
DECL|field|indexMask
specifier|private
specifier|final
name|long
name|indexMask
decl_stmt|;
comment|/** Construct a decoder for a given {@link EliasFanoEncoder}.    * The decoding index is set to just before the first encoded value.    */
DECL|method|EliasFanoDecoder
specifier|public
name|EliasFanoDecoder
parameter_list|(
name|EliasFanoEncoder
name|efEncoder
parameter_list|)
block|{
name|this
operator|.
name|efEncoder
operator|=
name|efEncoder
expr_stmt|;
name|this
operator|.
name|numEncoded
operator|=
name|efEncoder
operator|.
name|numEncoded
expr_stmt|;
comment|// not final in EliasFanoEncoder
name|this
operator|.
name|numIndexEntries
operator|=
name|efEncoder
operator|.
name|currentEntryIndex
expr_stmt|;
comment|// not final in EliasFanoEncoder
name|this
operator|.
name|indexMask
operator|=
operator|(
literal|1L
operator|<<
name|efEncoder
operator|.
name|nIndexEntryBits
operator|)
operator|-
literal|1
expr_stmt|;
block|}
comment|/** @return The Elias-Fano encoder that is decoded. */
DECL|method|getEliasFanoEncoder
specifier|public
name|EliasFanoEncoder
name|getEliasFanoEncoder
parameter_list|()
block|{
return|return
name|efEncoder
return|;
block|}
comment|/** The number of values encoded by the encoder.    * @return The number of values encoded by the encoder.    */
DECL|method|numEncoded
specifier|public
name|long
name|numEncoded
parameter_list|()
block|{
return|return
name|numEncoded
return|;
block|}
comment|/** The current decoding index.    * The first value encoded by {@link EliasFanoEncoder#encodeNext} has index 0.    * Only valid directly after    * {@link #nextValue}, {@link #advanceToValue},    * {@link #previousValue}, or {@link #backToValue}    * returned another value than {@link #NO_MORE_VALUES},    * or {@link #advanceToIndex} returned true.    * @return The decoding index of the last decoded value, or as last set by {@link #advanceToIndex}.    */
DECL|method|currentIndex
specifier|public
name|long
name|currentIndex
parameter_list|()
block|{
if|if
condition|(
name|efIndex
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"index before sequence"
argument_list|)
throw|;
block|}
if|if
condition|(
name|efIndex
operator|>=
name|numEncoded
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"index after sequence"
argument_list|)
throw|;
block|}
return|return
name|efIndex
return|;
block|}
comment|/** The value at the current decoding index.    * Only valid when {@link #currentIndex} would return a valid result.    *<br>This is only intended for use after {@link #advanceToIndex} returned true.    * @return The value encoded at {@link #currentIndex}.    */
DECL|method|currentValue
specifier|public
name|long
name|currentValue
parameter_list|()
block|{
return|return
name|combineHighLowValues
argument_list|(
name|currentHighValue
argument_list|()
argument_list|,
name|currentLowValue
argument_list|()
argument_list|)
return|;
block|}
comment|/**  @return The high value for the current decoding index. */
DECL|method|currentHighValue
specifier|private
name|long
name|currentHighValue
parameter_list|()
block|{
return|return
name|setBitForIndex
operator|-
name|efIndex
return|;
comment|// sequence of unary gaps
block|}
comment|/** See also {@link EliasFanoEncoder#packValue} */
DECL|method|unPackValue
specifier|private
specifier|static
name|long
name|unPackValue
parameter_list|(
name|long
index|[]
name|longArray
parameter_list|,
name|int
name|numBits
parameter_list|,
name|long
name|packIndex
parameter_list|,
name|long
name|bitsMask
parameter_list|)
block|{
if|if
condition|(
name|numBits
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|bitPos
init|=
name|packIndex
operator|*
name|numBits
decl_stmt|;
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|bitPos
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
decl_stmt|;
name|int
name|bitPosAtIndex
init|=
call|(
name|int
call|)
argument_list|(
name|bitPos
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
name|long
name|value
init|=
name|longArray
index|[
name|index
index|]
operator|>>>
name|bitPosAtIndex
decl_stmt|;
if|if
condition|(
operator|(
name|bitPosAtIndex
operator|+
name|numBits
operator|)
operator|>
name|Long
operator|.
name|SIZE
condition|)
block|{
name|value
operator||=
operator|(
name|longArray
index|[
name|index
operator|+
literal|1
index|]
operator|<<
operator|(
name|Long
operator|.
name|SIZE
operator|-
name|bitPosAtIndex
operator|)
operator|)
expr_stmt|;
block|}
name|value
operator|&=
name|bitsMask
expr_stmt|;
return|return
name|value
return|;
block|}
comment|/**  @return The low value for the current decoding index. */
DECL|method|currentLowValue
specifier|private
name|long
name|currentLowValue
parameter_list|()
block|{
assert|assert
operator|(
operator|(
name|efIndex
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|efIndex
operator|<
name|numEncoded
operator|)
operator|)
operator|:
literal|"efIndex "
operator|+
name|efIndex
assert|;
return|return
name|unPackValue
argument_list|(
name|efEncoder
operator|.
name|lowerLongs
argument_list|,
name|efEncoder
operator|.
name|numLowBits
argument_list|,
name|efIndex
argument_list|,
name|efEncoder
operator|.
name|lowerBitsMask
argument_list|)
return|;
block|}
comment|/**  @return The given highValue shifted left by the number of low bits from by the EliasFanoSequence,    *           logically OR-ed with the given lowValue.    */
DECL|method|combineHighLowValues
specifier|private
name|long
name|combineHighLowValues
parameter_list|(
name|long
name|highValue
parameter_list|,
name|long
name|lowValue
parameter_list|)
block|{
return|return
operator|(
name|highValue
operator|<<
name|efEncoder
operator|.
name|numLowBits
operator|)
operator||
name|lowValue
return|;
block|}
DECL|field|curHighLong
specifier|private
name|long
name|curHighLong
decl_stmt|;
comment|/* The implementation of forward decoding and backward decoding is done by the following method pairs.    *    * toBeforeSequence - toAfterSequence    * getCurrentRightShift - getCurrentLeftShift    * toAfterCurrentHighBit - toBeforeCurrentHighBit    * toNextHighLong - toPreviousHighLong    * nextHighValue - previousHighValue    * nextValue - previousValue    * advanceToValue - backToValue    *    */
comment|/* Forward decoding section */
comment|/** Set the decoding index to just before the first encoded value.    */
DECL|method|toBeforeSequence
specifier|public
name|void
name|toBeforeSequence
parameter_list|()
block|{
name|efIndex
operator|=
operator|-
literal|1
expr_stmt|;
name|setBitForIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/** @return the number of bits in a long after (setBitForIndex modulo Long.SIZE) */
DECL|method|getCurrentRightShift
specifier|private
name|int
name|getCurrentRightShift
parameter_list|()
block|{
name|int
name|s
init|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
return|return
name|s
return|;
block|}
comment|/** Increment efIndex and setBitForIndex and    * shift curHighLong so that it does not contain the high bits before setBitForIndex.    * @return true iff efIndex still smaller than numEncoded.    */
DECL|method|toAfterCurrentHighBit
specifier|private
name|boolean
name|toAfterCurrentHighBit
parameter_list|()
block|{
name|efIndex
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|efIndex
operator|>=
name|numEncoded
condition|)
block|{
return|return
literal|false
return|;
block|}
name|setBitForIndex
operator|+=
literal|1
expr_stmt|;
name|int
name|highIndex
init|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
decl_stmt|;
name|curHighLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
operator|>>>
name|getCurrentRightShift
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** The current high long has been determined to not contain the set bit that is needed.    *  Increment setBitForIndex to the next high long and set curHighLong accordingly.    */
DECL|method|toNextHighLong
specifier|private
name|void
name|toNextHighLong
parameter_list|()
block|{
name|setBitForIndex
operator|+=
name|Long
operator|.
name|SIZE
operator|-
operator|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
operator|)
expr_stmt|;
comment|//assert getCurrentRightShift() == 0;
name|int
name|highIndex
init|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
decl_stmt|;
name|curHighLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
block|}
comment|/** setBitForIndex and efIndex have just been incremented, scan to the next high set bit    *  by incrementing setBitForIndex, and by setting curHighLong accordingly.    */
DECL|method|toNextHighValue
specifier|private
name|void
name|toNextHighValue
parameter_list|()
block|{
while|while
condition|(
name|curHighLong
operator|==
literal|0L
condition|)
block|{
name|toNextHighLong
argument_list|()
expr_stmt|;
comment|// inlining and unrolling would simplify somewhat
block|}
name|setBitForIndex
operator|+=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|curHighLong
argument_list|)
expr_stmt|;
block|}
comment|/** setBitForIndex and efIndex have just been incremented, scan to the next high set bit    *  by incrementing setBitForIndex, and by setting curHighLong accordingly.    *  @return the next encoded high value.    */
DECL|method|nextHighValue
specifier|private
name|long
name|nextHighValue
parameter_list|()
block|{
name|toNextHighValue
argument_list|()
expr_stmt|;
return|return
name|currentHighValue
argument_list|()
return|;
block|}
comment|/** If another value is available after the current decoding index, return this value and    * and increase the decoding index by 1. Otherwise return {@link #NO_MORE_VALUES}.    */
DECL|method|nextValue
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
if|if
condition|(
operator|!
name|toAfterCurrentHighBit
argument_list|()
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|long
name|highValue
init|=
name|nextHighValue
argument_list|()
decl_stmt|;
return|return
name|combineHighLowValues
argument_list|(
name|highValue
argument_list|,
name|currentLowValue
argument_list|()
argument_list|)
return|;
block|}
comment|/** Advance the decoding index to a given index.    * and return<code>true</code> iff it is available.    *<br>See also {@link #currentValue}.    *<br>The current implementation does not use the index on the upper bit zero bit positions.    *<br>Note: there is currently no implementation of<code>backToIndex</code>.    */
DECL|method|advanceToIndex
specifier|public
name|boolean
name|advanceToIndex
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>
name|efIndex
assert|;
if|if
condition|(
name|index
operator|>=
name|numEncoded
condition|)
block|{
name|efIndex
operator|=
name|numEncoded
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|toAfterCurrentHighBit
argument_list|()
condition|)
block|{
assert|assert
literal|false
assert|;
block|}
comment|/* CHECKME: Add a (binary) search in the upperZeroBitPositions here. */
name|int
name|curSetBits
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|curHighLong
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|efIndex
operator|+
name|curSetBits
operator|)
operator|<
name|index
condition|)
block|{
comment|// curHighLong has not enough set bits to reach index
name|efIndex
operator|+=
name|curSetBits
expr_stmt|;
name|toNextHighLong
argument_list|()
expr_stmt|;
name|curSetBits
operator|=
name|Long
operator|.
name|bitCount
argument_list|(
name|curHighLong
argument_list|)
expr_stmt|;
block|}
comment|// curHighLong has enough set bits to reach index
while|while
condition|(
name|efIndex
operator|<
name|index
condition|)
block|{
comment|/* CHECKME: Instead of the linear search here, use (forward) broadword selection from        * "Broadword Implementation of Rank/Select Queries", Sebastiano Vigna, January 30, 2012.        */
if|if
condition|(
operator|!
name|toAfterCurrentHighBit
argument_list|()
condition|)
block|{
assert|assert
literal|false
assert|;
block|}
name|toNextHighValue
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Given a target value, advance the decoding index to the first bigger or equal value    * and return it if it is available. Otherwise return {@link #NO_MORE_VALUES}.    *<br>The current implementation uses the index on the upper zero bit positions.    */
DECL|method|advanceToValue
specifier|public
name|long
name|advanceToValue
parameter_list|(
name|long
name|target
parameter_list|)
block|{
name|efIndex
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|efIndex
operator|>=
name|numEncoded
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|setBitForIndex
operator|+=
literal|1
expr_stmt|;
comment|// the high bit at setBitForIndex belongs to the unary code for efIndex
name|int
name|highIndex
init|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
decl_stmt|;
name|long
name|upperLong
init|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
decl_stmt|;
name|curHighLong
operator|=
name|upperLong
operator|>>>
operator|(
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
operator|)
expr_stmt|;
comment|// may contain the unary 1 bit for efIndex
comment|// determine index entry to advance to
name|long
name|highTarget
init|=
name|target
operator|>>>
name|efEncoder
operator|.
name|numLowBits
decl_stmt|;
name|long
name|indexEntryIndex
init|=
operator|(
name|highTarget
operator|/
name|efEncoder
operator|.
name|indexInterval
operator|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|indexEntryIndex
operator|>=
literal|0
condition|)
block|{
comment|// not before first index entry
if|if
condition|(
name|indexEntryIndex
operator|>=
name|numIndexEntries
condition|)
block|{
name|indexEntryIndex
operator|=
name|numIndexEntries
operator|-
literal|1
expr_stmt|;
comment|// no further than last index entry
block|}
name|long
name|indexHighValue
init|=
operator|(
name|indexEntryIndex
operator|+
literal|1
operator|)
operator|*
name|efEncoder
operator|.
name|indexInterval
decl_stmt|;
assert|assert
name|indexHighValue
operator|<=
name|highTarget
assert|;
if|if
condition|(
name|indexHighValue
operator|>
operator|(
name|setBitForIndex
operator|-
name|efIndex
operator|)
condition|)
block|{
comment|// advance to just after zero bit position of index entry.
name|setBitForIndex
operator|=
name|unPackValue
argument_list|(
name|efEncoder
operator|.
name|upperZeroBitPositionIndex
argument_list|,
name|efEncoder
operator|.
name|nIndexEntryBits
argument_list|,
name|indexEntryIndex
argument_list|,
name|indexMask
argument_list|)
expr_stmt|;
name|efIndex
operator|=
name|setBitForIndex
operator|-
name|indexHighValue
expr_stmt|;
comment|// the high bit at setBitForIndex belongs to the unary code for efIndex
name|highIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
expr_stmt|;
name|upperLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
name|curHighLong
operator|=
name|upperLong
operator|>>>
operator|(
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
operator|)
expr_stmt|;
comment|// may contain the unary 1 bit for efIndex
block|}
assert|assert
name|efIndex
operator|<
name|numEncoded
assert|;
comment|// there is a high value to be found.
block|}
name|int
name|curSetBits
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|curHighLong
argument_list|)
decl_stmt|;
comment|// shifted right.
name|int
name|curClearBits
init|=
name|Long
operator|.
name|SIZE
operator|-
name|curSetBits
operator|-
operator|(
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
operator|)
decl_stmt|;
comment|// subtract right shift, may be more than encoded
while|while
condition|(
operator|(
operator|(
name|setBitForIndex
operator|-
name|efIndex
operator|)
operator|+
name|curClearBits
operator|)
operator|<
name|highTarget
condition|)
block|{
comment|// curHighLong has not enough clear bits to reach highTarget
name|efIndex
operator|+=
name|curSetBits
expr_stmt|;
if|if
condition|(
name|efIndex
operator|>=
name|numEncoded
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|setBitForIndex
operator|+=
name|Long
operator|.
name|SIZE
operator|-
operator|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
operator|)
expr_stmt|;
comment|// highIndex = (int)(setBitForIndex>>> LOG2_LONG_SIZE);
assert|assert
operator|(
name|highIndex
operator|+
literal|1
operator|)
operator|==
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
assert|;
name|highIndex
operator|+=
literal|1
expr_stmt|;
name|upperLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
name|curHighLong
operator|=
name|upperLong
expr_stmt|;
name|curSetBits
operator|=
name|Long
operator|.
name|bitCount
argument_list|(
name|curHighLong
argument_list|)
expr_stmt|;
name|curClearBits
operator|=
name|Long
operator|.
name|SIZE
operator|-
name|curSetBits
expr_stmt|;
block|}
comment|// curHighLong has enough clear bits to reach highTarget, and may not have enough set bits.
while|while
condition|(
name|curHighLong
operator|==
literal|0L
condition|)
block|{
name|setBitForIndex
operator|+=
name|Long
operator|.
name|SIZE
operator|-
operator|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
operator|)
expr_stmt|;
assert|assert
operator|(
name|highIndex
operator|+
literal|1
operator|)
operator|==
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
assert|;
name|highIndex
operator|+=
literal|1
expr_stmt|;
name|upperLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
name|curHighLong
operator|=
name|upperLong
expr_stmt|;
block|}
comment|// curHighLong has enough clear bits to reach highTarget, has at least 1 set bit, and may not have enough set bits.
name|int
name|rank
init|=
call|(
name|int
call|)
argument_list|(
name|highTarget
operator|-
operator|(
name|setBitForIndex
operator|-
name|efIndex
operator|)
argument_list|)
decl_stmt|;
comment|// the rank of the zero bit for highValue.
assert|assert
operator|(
name|rank
operator|<=
name|Long
operator|.
name|SIZE
operator|)
operator|:
operator|(
literal|"rank "
operator|+
name|rank
operator|)
assert|;
if|if
condition|(
name|rank
operator|>=
literal|1
condition|)
block|{
name|long
name|invCurHighLong
init|=
operator|~
name|curHighLong
decl_stmt|;
name|int
name|clearBitForValue
init|=
operator|(
name|rank
operator|<=
literal|8
operator|)
condition|?
name|BroadWord
operator|.
name|selectNaive
argument_list|(
name|invCurHighLong
argument_list|,
name|rank
argument_list|)
else|:
name|BroadWord
operator|.
name|select
argument_list|(
name|invCurHighLong
argument_list|,
name|rank
argument_list|)
decl_stmt|;
assert|assert
name|clearBitForValue
operator|<=
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
assert|;
name|setBitForIndex
operator|+=
name|clearBitForValue
operator|+
literal|1
expr_stmt|;
comment|// the high bit just before setBitForIndex is zero
name|int
name|oneBitsBeforeClearBit
init|=
name|clearBitForValue
operator|-
name|rank
operator|+
literal|1
decl_stmt|;
name|efIndex
operator|+=
name|oneBitsBeforeClearBit
expr_stmt|;
comment|// the high bit at setBitForIndex and belongs to the unary code for efIndex
if|if
condition|(
name|efIndex
operator|>=
name|numEncoded
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
if|if
condition|(
operator|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
operator|)
operator|==
literal|0L
condition|)
block|{
comment|// exhausted curHighLong
assert|assert
operator|(
name|highIndex
operator|+
literal|1
operator|)
operator|==
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
assert|;
name|highIndex
operator|+=
literal|1
expr_stmt|;
name|upperLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
name|curHighLong
operator|=
name|upperLong
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|highIndex
operator|==
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
assert|;
name|curHighLong
operator|=
name|upperLong
operator|>>>
operator|(
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
operator|)
expr_stmt|;
block|}
comment|// curHighLong has enough clear bits to reach highTarget, and may not have enough set bits.
while|while
condition|(
name|curHighLong
operator|==
literal|0L
condition|)
block|{
name|setBitForIndex
operator|+=
name|Long
operator|.
name|SIZE
operator|-
operator|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
operator|)
expr_stmt|;
assert|assert
operator|(
name|highIndex
operator|+
literal|1
operator|)
operator|==
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
assert|;
name|highIndex
operator|+=
literal|1
expr_stmt|;
name|upperLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
name|curHighLong
operator|=
name|upperLong
expr_stmt|;
block|}
block|}
name|setBitForIndex
operator|+=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|curHighLong
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|setBitForIndex
operator|-
name|efIndex
operator|)
operator|>=
name|highTarget
assert|;
comment|// highTarget reached
comment|// Linear search also with low values
name|long
name|currentValue
init|=
name|combineHighLowValues
argument_list|(
operator|(
name|setBitForIndex
operator|-
name|efIndex
operator|)
argument_list|,
name|currentLowValue
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|currentValue
operator|<
name|target
condition|)
block|{
name|currentValue
operator|=
name|nextValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentValue
operator|==
name|NO_MORE_VALUES
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
block|}
return|return
name|currentValue
return|;
block|}
comment|/* Backward decoding section */
comment|/** Set the decoding index to just after the last encoded value.    */
DECL|method|toAfterSequence
specifier|public
name|void
name|toAfterSequence
parameter_list|()
block|{
name|efIndex
operator|=
name|numEncoded
expr_stmt|;
comment|// just after last index
name|setBitForIndex
operator|=
operator|(
name|efEncoder
operator|.
name|lastEncoded
operator|>>>
name|efEncoder
operator|.
name|numLowBits
operator|)
operator|+
name|numEncoded
expr_stmt|;
block|}
comment|/** @return the number of bits in a long before (setBitForIndex modulo Long.SIZE) */
DECL|method|getCurrentLeftShift
specifier|private
name|int
name|getCurrentLeftShift
parameter_list|()
block|{
name|int
name|s
init|=
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|-
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
return|return
name|s
return|;
block|}
comment|/** Decrement efindex and setBitForIndex and    * shift curHighLong so that it does not contain the high bits after setBitForIndex.    * @return true iff efindex still>= 0    */
DECL|method|toBeforeCurrentHighBit
specifier|private
name|boolean
name|toBeforeCurrentHighBit
parameter_list|()
block|{
name|efIndex
operator|-=
literal|1
expr_stmt|;
if|if
condition|(
name|efIndex
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|setBitForIndex
operator|-=
literal|1
expr_stmt|;
name|int
name|highIndex
init|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
decl_stmt|;
name|curHighLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
operator|<<
name|getCurrentLeftShift
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** The current high long has been determined to not contain the set bit that is needed.    *  Decrement setBitForIndex to the previous high long and set curHighLong accordingly.    */
DECL|method|toPreviousHighLong
specifier|private
name|void
name|toPreviousHighLong
parameter_list|()
block|{
name|setBitForIndex
operator|-=
operator|(
name|setBitForIndex
operator|&
operator|(
name|Long
operator|.
name|SIZE
operator|-
literal|1
operator|)
operator|)
operator|+
literal|1
expr_stmt|;
comment|//assert getCurrentLeftShift() == 0;
name|int
name|highIndex
init|=
call|(
name|int
call|)
argument_list|(
name|setBitForIndex
operator|>>>
name|LOG2_LONG_SIZE
argument_list|)
decl_stmt|;
name|curHighLong
operator|=
name|efEncoder
operator|.
name|upperLongs
index|[
name|highIndex
index|]
expr_stmt|;
block|}
comment|/** setBitForIndex and efIndex have just been decremented, scan to the previous high set bit    *  by decrementing setBitForIndex and by setting curHighLong accordingly.    *  @return the previous encoded high value.    */
DECL|method|previousHighValue
specifier|private
name|long
name|previousHighValue
parameter_list|()
block|{
while|while
condition|(
name|curHighLong
operator|==
literal|0L
condition|)
block|{
name|toPreviousHighLong
argument_list|()
expr_stmt|;
comment|// inlining and unrolling would simplify somewhat
block|}
name|setBitForIndex
operator|-=
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|curHighLong
argument_list|)
expr_stmt|;
return|return
name|currentHighValue
argument_list|()
return|;
block|}
comment|/** If another value is available before the current decoding index, return this value    * and decrease the decoding index by 1. Otherwise return {@link #NO_MORE_VALUES}.    */
DECL|method|previousValue
specifier|public
name|long
name|previousValue
parameter_list|()
block|{
if|if
condition|(
operator|!
name|toBeforeCurrentHighBit
argument_list|()
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|long
name|highValue
init|=
name|previousHighValue
argument_list|()
decl_stmt|;
return|return
name|combineHighLowValues
argument_list|(
name|highValue
argument_list|,
name|currentLowValue
argument_list|()
argument_list|)
return|;
block|}
comment|/** setBitForIndex and efIndex have just been decremented, scan backward to the high set bit    *  of at most a given high value    *  by decrementing setBitForIndex and by setting curHighLong accordingly.    *<br>The current implementation does not use the index on the upper zero bit positions.    *  @return the largest encoded high value that is at most the given one.    */
DECL|method|backToHighValue
specifier|private
name|long
name|backToHighValue
parameter_list|(
name|long
name|highTarget
parameter_list|)
block|{
comment|/* CHECKME: Add using the index as in advanceToHighValue */
name|int
name|curSetBits
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|curHighLong
argument_list|)
decl_stmt|;
comment|// is shifted by getCurrentLeftShift()
name|int
name|curClearBits
init|=
name|Long
operator|.
name|SIZE
operator|-
name|curSetBits
operator|-
name|getCurrentLeftShift
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|currentHighValue
argument_list|()
operator|-
name|curClearBits
operator|)
operator|>
name|highTarget
condition|)
block|{
comment|// curHighLong has not enough clear bits to reach highTarget
name|efIndex
operator|-=
name|curSetBits
expr_stmt|;
if|if
condition|(
name|efIndex
operator|<
literal|0
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|toPreviousHighLong
argument_list|()
expr_stmt|;
comment|//assert getCurrentLeftShift() == 0;
name|curSetBits
operator|=
name|Long
operator|.
name|bitCount
argument_list|(
name|curHighLong
argument_list|)
expr_stmt|;
name|curClearBits
operator|=
name|Long
operator|.
name|SIZE
operator|-
name|curSetBits
expr_stmt|;
block|}
comment|// curHighLong has enough clear bits to reach highTarget, but may not have enough set bits.
name|long
name|highValue
init|=
name|previousHighValue
argument_list|()
decl_stmt|;
while|while
condition|(
name|highValue
operator|>
name|highTarget
condition|)
block|{
comment|/* CHECKME: See at advanceToHighValue on using broadword bit selection. */
if|if
condition|(
operator|!
name|toBeforeCurrentHighBit
argument_list|()
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|highValue
operator|=
name|previousHighValue
argument_list|()
expr_stmt|;
block|}
return|return
name|highValue
return|;
block|}
comment|/** Given a target value, go back to the first smaller or equal value    * and return it if it is available. Otherwise return {@link #NO_MORE_VALUES}.    *<br>The current implementation does not use the index on the upper zero bit positions.    */
DECL|method|backToValue
specifier|public
name|long
name|backToValue
parameter_list|(
name|long
name|target
parameter_list|)
block|{
if|if
condition|(
operator|!
name|toBeforeCurrentHighBit
argument_list|()
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
name|long
name|highTarget
init|=
name|target
operator|>>>
name|efEncoder
operator|.
name|numLowBits
decl_stmt|;
name|long
name|highValue
init|=
name|backToHighValue
argument_list|(
name|highTarget
argument_list|)
decl_stmt|;
if|if
condition|(
name|highValue
operator|==
name|NO_MORE_VALUES
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
comment|// Linear search with low values:
name|long
name|currentValue
init|=
name|combineHighLowValues
argument_list|(
name|highValue
argument_list|,
name|currentLowValue
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|currentValue
operator|>
name|target
condition|)
block|{
name|currentValue
operator|=
name|previousValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentValue
operator|==
name|NO_MORE_VALUES
condition|)
block|{
return|return
name|NO_MORE_VALUES
return|;
block|}
block|}
return|return
name|currentValue
return|;
block|}
block|}
end_class
end_unit
