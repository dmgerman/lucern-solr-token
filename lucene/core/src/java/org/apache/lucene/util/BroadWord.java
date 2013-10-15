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
begin_comment
comment|/**  * Methods and constants inspired by the article  * "Broadword Implementation of Rank/Select Queries" by Sebastiano Vigna, January 30, 2012:  *<ul>  *<li>algorithm 1: {@link #bitCount(long)}, count of set bits in a<code>long</code>  *<li>algorithm 2: {@link #select(long, int)}, selection of a set bit in a<code>long</code>,  *<li>bytewise signed smaller&lt;<sub><small>8</small></sub> operator: {@link #smallerUpTo7_8(long,long)}.  *<li>shortwise signed smaller&lt;<sub><small>16</small></sub> operator: {@link #smallerUpto15_16(long,long)}.  *<li>some of the Lk and Hk constants that are used by the above:  * L8 {@link #L8_L}, H8 {@link #H8_L}, L9 {@link #L9_L}, L16 {@link #L16_L}and H16 {@link #H8_L}.  *</ul>  * @lucene.internal  */
end_comment
begin_class
DECL|class|BroadWord
specifier|public
specifier|final
class|class
name|BroadWord
block|{
comment|// TBD: test smaller8 and smaller16 separately.
DECL|method|BroadWord
specifier|private
name|BroadWord
parameter_list|()
block|{}
comment|// no instance
comment|/** Bit count of a long.    * Only here to compare the implementation with {@link #select(long,int)},    * normally {@link Long#bitCount} is preferable.    * @return The total number of 1 bits in x.    */
DECL|method|bitCount
specifier|static
name|int
name|bitCount
parameter_list|(
name|long
name|x
parameter_list|)
block|{
comment|// Step 0 leaves in each pair of bits the number of ones originally contained in that pair:
name|x
operator|=
name|x
operator|-
operator|(
operator|(
name|x
operator|&
literal|0xAAAAAAAAAAAAAAAAL
operator|)
operator|>>>
literal|1
operator|)
expr_stmt|;
comment|// Step 1, idem for each nibble:
name|x
operator|=
operator|(
name|x
operator|&
literal|0x3333333333333333L
operator|)
operator|+
operator|(
operator|(
name|x
operator|>>>
literal|2
operator|)
operator|&
literal|0x3333333333333333L
operator|)
expr_stmt|;
comment|// Step 2, idem for each byte:
name|x
operator|=
operator|(
name|x
operator|+
operator|(
name|x
operator|>>>
literal|4
operator|)
operator|)
operator|&
literal|0x0F0F0F0F0F0F0F0FL
expr_stmt|;
comment|// Multiply to sum them all into the high byte, and return the high byte:
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|x
operator|*
name|L8_L
operator|)
operator|>>>
literal|56
argument_list|)
return|;
block|}
comment|/** Select a 1-bit from a long.    * @return The index of the r-th 1 bit in x, or if no such bit exists, 72.    */
DECL|method|select
specifier|public
specifier|static
name|int
name|select
parameter_list|(
name|long
name|x
parameter_list|,
name|int
name|r
parameter_list|)
block|{
name|long
name|s
init|=
name|x
operator|-
operator|(
operator|(
name|x
operator|&
literal|0xAAAAAAAAAAAAAAAAL
operator|)
operator|>>>
literal|1
operator|)
decl_stmt|;
comment|// Step 0, pairwise bitsums
comment|// Correct a small mistake in algorithm 2:
comment|// Use s instead of x the second time in right shift 2, compare to Algorithm 1 in rank9 above.
name|s
operator|=
operator|(
name|s
operator|&
literal|0x3333333333333333L
operator|)
operator|+
operator|(
operator|(
name|s
operator|>>>
literal|2
operator|)
operator|&
literal|0x3333333333333333L
operator|)
expr_stmt|;
comment|// Step 1, nibblewise bitsums
name|s
operator|=
operator|(
operator|(
name|s
operator|+
operator|(
name|s
operator|>>>
literal|4
operator|)
operator|)
operator|&
literal|0x0F0F0F0F0F0F0F0FL
operator|)
operator|*
name|L8_L
expr_stmt|;
comment|// Step 2, bytewise bitsums
name|long
name|b
init|=
operator|(
operator|(
name|smallerUpTo7_8
argument_list|(
name|s
argument_list|,
operator|(
name|r
operator|*
name|L8_L
operator|)
argument_list|)
operator|>>>
literal|7
operator|)
operator|*
name|L8_L
operator|)
operator|>>>
literal|53
decl_stmt|;
comment|//& (~7L); // Step 3, side ways addition for byte number times 8
name|long
name|l
init|=
name|r
operator|-
operator|(
operator|(
operator|(
name|s
operator|<<
literal|8
operator|)
operator|>>>
name|b
operator|)
operator|&
literal|0xFFL
operator|)
decl_stmt|;
comment|// Step 4, byte wise rank, subtract the rank with byte at b-8, or zero for b=0;
assert|assert
literal|0L
operator|<=
name|l
operator|:
name|l
assert|;
comment|//assert l< 8 : l; //fails when bit r is not available.
comment|// Select bit l from byte (x>>> b):
name|long
name|spr
init|=
operator|(
operator|(
operator|(
name|x
operator|>>>
name|b
operator|)
operator|&
literal|0xFFL
operator|)
operator|*
name|L8_L
operator|)
operator|&
name|L9_L
decl_stmt|;
comment|// spread the 8 bits of the byte at b over the long at L9 positions
comment|// long spr_bigger8_zero = smaller8(0L, spr); // inlined smaller8 with 0L argument:
comment|// FIXME: replace by biggerequal8_one formula from article page 6, line 9. four operators instead of five here.
name|long
name|spr_bigger8_zero
init|=
operator|(
operator|(
name|H8_L
operator|-
operator|(
name|spr
operator|&
operator|(
operator|~
name|H8_L
operator|)
operator|)
operator|)
operator|^
operator|(
operator|~
name|spr
operator|)
operator|)
operator|&
name|H8_L
decl_stmt|;
name|s
operator|=
operator|(
name|spr_bigger8_zero
operator|>>>
literal|7
operator|)
operator|*
name|L8_L
expr_stmt|;
comment|// Step 5, sideways byte add the 8 bits towards the high byte
name|int
name|res
init|=
call|(
name|int
call|)
argument_list|(
name|b
operator|+
operator|(
operator|(
operator|(
name|smallerUpTo7_8
argument_list|(
name|s
argument_list|,
operator|(
name|l
operator|*
name|L8_L
operator|)
argument_list|)
operator|>>>
literal|7
operator|)
operator|*
name|L8_L
operator|)
operator|>>>
literal|56
operator|)
argument_list|)
decl_stmt|;
comment|// Step 6
return|return
name|res
return|;
block|}
comment|/** A signed bytewise smaller&lt;<sub><small>8</small></sub> operator, for operands 0L<= x, y<=0x7L.    * This uses the following numbers of basic long operations: 1 or, 2 and, 2 xor, 1 minus, 1 not.    * @return A long with bits set in the {@link #H8_L} positions corresponding to each input signed byte pair that compares smaller.    */
DECL|method|smallerUpTo7_8
specifier|public
specifier|static
name|long
name|smallerUpTo7_8
parameter_list|(
name|long
name|x
parameter_list|,
name|long
name|y
parameter_list|)
block|{
comment|// See section 4, page 5, line 14 of the Vigna article:
return|return
operator|(
operator|(
operator|(
name|x
operator||
name|H8_L
operator|)
operator|-
operator|(
name|y
operator|&
operator|(
operator|~
name|H8_L
operator|)
operator|)
operator|)
operator|^
name|x
operator|^
operator|~
name|y
operator|)
operator|&
name|H8_L
return|;
block|}
comment|/** An unsigned bytewise smaller&lt;<sub><small>8</small></sub> operator.    * This uses the following numbers of basic long operations: 3 or, 2 and, 2 xor, 1 minus, 1 not.    * @return A long with bits set in the {@link #H8_L} positions corresponding to each input unsigned byte pair that compares smaller.    */
DECL|method|smalleru_8
specifier|public
specifier|static
name|long
name|smalleru_8
parameter_list|(
name|long
name|x
parameter_list|,
name|long
name|y
parameter_list|)
block|{
comment|// See section 4, 8th line from the bottom of the page 5, of the Vigna article:
return|return
operator|(
operator|(
operator|(
operator|(
name|x
operator||
name|H8_L
operator|)
operator|-
operator|(
name|y
operator|&
operator|~
name|H8_L
operator|)
operator|)
operator||
name|x
operator|^
name|y
operator|)
operator|^
operator|(
name|x
operator||
operator|~
name|y
operator|)
operator|)
operator|&
name|H8_L
return|;
block|}
comment|/** An unsigned bytewise not equals 0 operator.    * This uses the following numbers of basic long operations: 2 or, 1 and, 1 minus.    * @return A long with bits set in the {@link #H8_L} positions corresponding to each unsigned byte that does not equal 0.    */
DECL|method|notEquals0_8
specifier|public
specifier|static
name|long
name|notEquals0_8
parameter_list|(
name|long
name|x
parameter_list|)
block|{
comment|// See section 4, line 6-8 on page 6, of the Vigna article:
return|return
operator|(
operator|(
operator|(
name|x
operator||
name|H8_L
operator|)
operator|-
name|L8_L
operator|)
operator||
name|x
operator|)
operator|&
name|H8_L
return|;
block|}
comment|/** A bytewise smaller&lt;<sub><small>16</small></sub> operator.    * This uses the following numbers of basic long operations: 1 or, 2 and, 2 xor, 1 minus, 1 not.    * @return A long with bits set in the {@link #H16_L} positions corresponding to each input signed short pair that compares smaller.    */
DECL|method|smallerUpto15_16
specifier|public
specifier|static
name|long
name|smallerUpto15_16
parameter_list|(
name|long
name|x
parameter_list|,
name|long
name|y
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
name|x
operator||
name|H16_L
operator|)
operator|-
operator|(
name|y
operator|&
operator|(
operator|~
name|H16_L
operator|)
operator|)
operator|)
operator|^
name|x
operator|^
operator|~
name|y
operator|)
operator|&
name|H16_L
return|;
block|}
comment|/** Lk denotes the constant whose ones are in position 0, k, 2k, . . .    *  These contain the low bit of each group of k bits.    *  The suffix _L indicates the long implementation.    */
DECL|field|L8_L
specifier|public
specifier|final
specifier|static
name|long
name|L8_L
init|=
literal|0x0101010101010101L
decl_stmt|;
DECL|field|L9_L
specifier|public
specifier|final
specifier|static
name|long
name|L9_L
init|=
literal|0x8040201008040201L
decl_stmt|;
DECL|field|L16_L
specifier|public
specifier|final
specifier|static
name|long
name|L16_L
init|=
literal|0x0001000100010001L
decl_stmt|;
comment|/** Hk = Lk<< (k-1) .    *  These contain the high bit of each group of k bits.    *  The suffix _L indicates the long implementation.    */
DECL|field|H8_L
specifier|public
specifier|final
specifier|static
name|long
name|H8_L
init|=
name|L8_L
operator|<<
literal|7
decl_stmt|;
DECL|field|H16_L
specifier|public
specifier|final
specifier|static
name|long
name|H16_L
init|=
name|L16_L
operator|<<
literal|15
decl_stmt|;
comment|/**    * Naive implementation of {@link #select(long,int)}, using {@link Long#numberOfTrailingZeros} repetitively.    * Works relatively fast for low ranks.    * @return The index of the r-th 1 bit in x, or if no such bit exists, 72.    */
DECL|method|selectNaive
specifier|public
specifier|static
name|int
name|selectNaive
parameter_list|(
name|long
name|x
parameter_list|,
name|int
name|r
parameter_list|)
block|{
assert|assert
name|r
operator|>=
literal|1
assert|;
name|int
name|s
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|x
operator|!=
literal|0L
operator|)
operator|&&
operator|(
name|r
operator|>
literal|0
operator|)
condition|)
block|{
name|int
name|ntz
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|x
operator|>>>=
operator|(
name|ntz
operator|+
literal|1
operator|)
expr_stmt|;
name|s
operator|+=
operator|(
name|ntz
operator|+
literal|1
operator|)
expr_stmt|;
name|r
operator|-=
literal|1
expr_stmt|;
block|}
name|int
name|res
init|=
operator|(
name|r
operator|>
literal|0
operator|)
condition|?
literal|72
else|:
name|s
decl_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
