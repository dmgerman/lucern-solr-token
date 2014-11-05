begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
DECL|package|org.apache.lucene.util
comment|// from org.apache.solr.util rev 555343
end_comment
begin_comment
comment|/**  A variety of high efficiency bit twiddling routines.  * @lucene.internal  */
end_comment
begin_class
DECL|class|BitUtil
specifier|public
specifier|final
class|class
name|BitUtil
block|{
DECL|field|BYTE_COUNTS
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|BYTE_COUNTS
init|=
block|{
comment|// table of bits/byte
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|,
literal|4
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|3
block|,
literal|4
block|,
literal|4
block|,
literal|5
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|4
block|,
literal|5
block|,
literal|5
block|,
literal|6
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|5
block|,
literal|6
block|,
literal|6
block|,
literal|7
block|,
literal|6
block|,
literal|7
block|,
literal|7
block|,
literal|8
block|}
decl_stmt|;
comment|// The General Idea: instead of having an array per byte that has
comment|// the offsets of the next set bit, that array could be
comment|// packed inside a 32 bit integer (8 4 bit numbers).  That
comment|// should be faster than accessing an array for each index, and
comment|// the total array size is kept smaller (256*sizeof(int))=1K
comment|/***** the python code that generated bitlist   def bits2int(val):   arr=0   for shift in range(8,0,-1):     if val& 0x80:       arr = (arr<< 4) | shift     val = val<< 1   return arr    def int_table():     tbl = [ hex(bits2int(val)).strip('L') for val in range(256) ]     return ','.join(tbl)   ******/
DECL|field|BIT_LISTS
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BIT_LISTS
init|=
block|{
literal|0x0
block|,
literal|0x1
block|,
literal|0x2
block|,
literal|0x21
block|,
literal|0x3
block|,
literal|0x31
block|,
literal|0x32
block|,
literal|0x321
block|,
literal|0x4
block|,
literal|0x41
block|,
literal|0x42
block|,
literal|0x421
block|,
literal|0x43
block|,
literal|0x431
block|,
literal|0x432
block|,
literal|0x4321
block|,
literal|0x5
block|,
literal|0x51
block|,
literal|0x52
block|,
literal|0x521
block|,
literal|0x53
block|,
literal|0x531
block|,
literal|0x532
block|,
literal|0x5321
block|,
literal|0x54
block|,
literal|0x541
block|,
literal|0x542
block|,
literal|0x5421
block|,
literal|0x543
block|,
literal|0x5431
block|,
literal|0x5432
block|,
literal|0x54321
block|,
literal|0x6
block|,
literal|0x61
block|,
literal|0x62
block|,
literal|0x621
block|,
literal|0x63
block|,
literal|0x631
block|,
literal|0x632
block|,
literal|0x6321
block|,
literal|0x64
block|,
literal|0x641
block|,
literal|0x642
block|,
literal|0x6421
block|,
literal|0x643
block|,
literal|0x6431
block|,
literal|0x6432
block|,
literal|0x64321
block|,
literal|0x65
block|,
literal|0x651
block|,
literal|0x652
block|,
literal|0x6521
block|,
literal|0x653
block|,
literal|0x6531
block|,
literal|0x6532
block|,
literal|0x65321
block|,
literal|0x654
block|,
literal|0x6541
block|,
literal|0x6542
block|,
literal|0x65421
block|,
literal|0x6543
block|,
literal|0x65431
block|,
literal|0x65432
block|,
literal|0x654321
block|,
literal|0x7
block|,
literal|0x71
block|,
literal|0x72
block|,
literal|0x721
block|,
literal|0x73
block|,
literal|0x731
block|,
literal|0x732
block|,
literal|0x7321
block|,
literal|0x74
block|,
literal|0x741
block|,
literal|0x742
block|,
literal|0x7421
block|,
literal|0x743
block|,
literal|0x7431
block|,
literal|0x7432
block|,
literal|0x74321
block|,
literal|0x75
block|,
literal|0x751
block|,
literal|0x752
block|,
literal|0x7521
block|,
literal|0x753
block|,
literal|0x7531
block|,
literal|0x7532
block|,
literal|0x75321
block|,
literal|0x754
block|,
literal|0x7541
block|,
literal|0x7542
block|,
literal|0x75421
block|,
literal|0x7543
block|,
literal|0x75431
block|,
literal|0x75432
block|,
literal|0x754321
block|,
literal|0x76
block|,
literal|0x761
block|,
literal|0x762
block|,
literal|0x7621
block|,
literal|0x763
block|,
literal|0x7631
block|,
literal|0x7632
block|,
literal|0x76321
block|,
literal|0x764
block|,
literal|0x7641
block|,
literal|0x7642
block|,
literal|0x76421
block|,
literal|0x7643
block|,
literal|0x76431
block|,
literal|0x76432
block|,
literal|0x764321
block|,
literal|0x765
block|,
literal|0x7651
block|,
literal|0x7652
block|,
literal|0x76521
block|,
literal|0x7653
block|,
literal|0x76531
block|,
literal|0x76532
block|,
literal|0x765321
block|,
literal|0x7654
block|,
literal|0x76541
block|,
literal|0x76542
block|,
literal|0x765421
block|,
literal|0x76543
block|,
literal|0x765431
block|,
literal|0x765432
block|,
literal|0x7654321
block|,
literal|0x8
block|,
literal|0x81
block|,
literal|0x82
block|,
literal|0x821
block|,
literal|0x83
block|,
literal|0x831
block|,
literal|0x832
block|,
literal|0x8321
block|,
literal|0x84
block|,
literal|0x841
block|,
literal|0x842
block|,
literal|0x8421
block|,
literal|0x843
block|,
literal|0x8431
block|,
literal|0x8432
block|,
literal|0x84321
block|,
literal|0x85
block|,
literal|0x851
block|,
literal|0x852
block|,
literal|0x8521
block|,
literal|0x853
block|,
literal|0x8531
block|,
literal|0x8532
block|,
literal|0x85321
block|,
literal|0x854
block|,
literal|0x8541
block|,
literal|0x8542
block|,
literal|0x85421
block|,
literal|0x8543
block|,
literal|0x85431
block|,
literal|0x85432
block|,
literal|0x854321
block|,
literal|0x86
block|,
literal|0x861
block|,
literal|0x862
block|,
literal|0x8621
block|,
literal|0x863
block|,
literal|0x8631
block|,
literal|0x8632
block|,
literal|0x86321
block|,
literal|0x864
block|,
literal|0x8641
block|,
literal|0x8642
block|,
literal|0x86421
block|,
literal|0x8643
block|,
literal|0x86431
block|,
literal|0x86432
block|,
literal|0x864321
block|,
literal|0x865
block|,
literal|0x8651
block|,
literal|0x8652
block|,
literal|0x86521
block|,
literal|0x8653
block|,
literal|0x86531
block|,
literal|0x86532
block|,
literal|0x865321
block|,
literal|0x8654
block|,
literal|0x86541
block|,
literal|0x86542
block|,
literal|0x865421
block|,
literal|0x86543
block|,
literal|0x865431
block|,
literal|0x865432
block|,
literal|0x8654321
block|,
literal|0x87
block|,
literal|0x871
block|,
literal|0x872
block|,
literal|0x8721
block|,
literal|0x873
block|,
literal|0x8731
block|,
literal|0x8732
block|,
literal|0x87321
block|,
literal|0x874
block|,
literal|0x8741
block|,
literal|0x8742
block|,
literal|0x87421
block|,
literal|0x8743
block|,
literal|0x87431
block|,
literal|0x87432
block|,
literal|0x874321
block|,
literal|0x875
block|,
literal|0x8751
block|,
literal|0x8752
block|,
literal|0x87521
block|,
literal|0x8753
block|,
literal|0x87531
block|,
literal|0x87532
block|,
literal|0x875321
block|,
literal|0x8754
block|,
literal|0x87541
block|,
literal|0x87542
block|,
literal|0x875421
block|,
literal|0x87543
block|,
literal|0x875431
block|,
literal|0x875432
block|,
literal|0x8754321
block|,
literal|0x876
block|,
literal|0x8761
block|,
literal|0x8762
block|,
literal|0x87621
block|,
literal|0x8763
block|,
literal|0x87631
block|,
literal|0x87632
block|,
literal|0x876321
block|,
literal|0x8764
block|,
literal|0x87641
block|,
literal|0x87642
block|,
literal|0x876421
block|,
literal|0x87643
block|,
literal|0x876431
block|,
literal|0x876432
block|,
literal|0x8764321
block|,
literal|0x8765
block|,
literal|0x87651
block|,
literal|0x87652
block|,
literal|0x876521
block|,
literal|0x87653
block|,
literal|0x876531
block|,
literal|0x876532
block|,
literal|0x8765321
block|,
literal|0x87654
block|,
literal|0x876541
block|,
literal|0x876542
block|,
literal|0x8765421
block|,
literal|0x876543
block|,
literal|0x8765431
block|,
literal|0x8765432
block|,
literal|0x87654321
block|}
decl_stmt|;
DECL|method|BitUtil
specifier|private
name|BitUtil
parameter_list|()
block|{}
comment|// no instance
comment|/** Return the number of bits sets in b. */
DECL|method|bitCount
specifier|public
specifier|static
name|int
name|bitCount
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
return|return
name|BYTE_COUNTS
index|[
name|b
operator|&
literal|0xFF
index|]
return|;
block|}
comment|/** Return the list of bits which are set in b encoded as followed:    *<code>(i>>> (4 * n))& 0x0F</code> is the offset of the n-th set bit of    * the given byte plus one, or 0 if there are n or less bits set in the given    * byte. For example<code>bitList(12)</code> returns 0x43:<ul>    *<li><code>0x43& 0x0F</code> is 3, meaning the the first bit set is at offset 3-1 = 2,</li>    *<li><code>(0x43>>> 4)& 0x0F</code> is 4, meaning there is a second bit set at offset 4-1=3,</li>    *<li><code>(0x43>>> 8)& 0x0F</code> is 0, meaning there is no more bit set in this byte.</li>    *</ul>*/
DECL|method|bitList
specifier|public
specifier|static
name|int
name|bitList
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
return|return
name|BIT_LISTS
index|[
name|b
operator|&
literal|0xFF
index|]
return|;
block|}
comment|// The pop methods used to rely on bit-manipulation tricks for speed but it
comment|// turns out that it is faster to use the Long.bitCount method (which is an
comment|// intrinsic since Java 6u18) in a naive loop, see LUCENE-2221
comment|/** Returns the number of set bits in an array of longs. */
DECL|method|pop_array
specifier|public
specifier|static
name|long
name|pop_array
parameter_list|(
name|long
index|[]
name|arr
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of the two sets after an intersection.    *  Neither array is modified. */
DECL|method|pop_intersect
specifier|public
specifier|static
name|long
name|pop_intersect
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator|&
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of the union of two sets.     *  Neither array is modified. */
DECL|method|pop_union
specifier|public
specifier|static
name|long
name|pop_union
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator||
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of A& ~B.    *  Neither array is modified. */
DECL|method|pop_andnot
specifier|public
specifier|static
name|long
name|pop_andnot
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator|&
operator|~
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of A ^ B     * Neither array is modified. */
DECL|method|pop_xor
specifier|public
specifier|static
name|long
name|pop_xor
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator|^
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** returns the next highest power of two, or the current value if it's already a power of two or zero*/
DECL|method|nextHighestPowerOfTwo
specifier|public
specifier|static
name|int
name|nextHighestPowerOfTwo
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|16
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
comment|/** returns the next highest power of two, or the current value if it's already a power of two or zero*/
DECL|method|nextHighestPowerOfTwo
specifier|public
specifier|static
name|long
name|nextHighestPowerOfTwo
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|16
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|32
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
comment|/** Same as {@link #zigZagEncode(long)} but on integers. */
DECL|method|zigZagEncode
specifier|public
specifier|static
name|int
name|zigZagEncode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|i
operator|>>
literal|31
operator|)
operator|^
operator|(
name|i
operator|<<
literal|1
operator|)
return|;
block|}
comment|/**     *<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">Zig-zag</a>     * encode the provided long. Assuming the input is a signed long whose     * absolute value can be stored on<tt>n</tt> bits, the returned value will     * be an unsigned long that can be stored on<tt>n+1</tt> bits.     */
DECL|method|zigZagEncode
specifier|public
specifier|static
name|long
name|zigZagEncode
parameter_list|(
name|long
name|l
parameter_list|)
block|{
return|return
operator|(
name|l
operator|>>
literal|63
operator|)
operator|^
operator|(
name|l
operator|<<
literal|1
operator|)
return|;
block|}
comment|/** Decode an int previously encoded with {@link #zigZagEncode(int)}. */
DECL|method|zigZagDecode
specifier|public
specifier|static
name|int
name|zigZagDecode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
operator|(
name|i
operator|>>>
literal|1
operator|)
operator|^
operator|-
operator|(
name|i
operator|&
literal|1
operator|)
operator|)
return|;
block|}
comment|/** Decode a long previously encoded with {@link #zigZagEncode(long)}. */
DECL|method|zigZagDecode
specifier|public
specifier|static
name|long
name|zigZagDecode
parameter_list|(
name|long
name|l
parameter_list|)
block|{
return|return
operator|(
operator|(
name|l
operator|>>>
literal|1
operator|)
operator|^
operator|-
operator|(
name|l
operator|&
literal|1
operator|)
operator|)
return|;
block|}
comment|/** Select a 1-bit from a long. See also LUCENE-6040.    * @return The index of the r-th 1 bit in x. This bit must exist.    */
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
comment|// pairwise bitsums
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
comment|// nibblewise bitsums
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
comment|// bytewise bitsums, cumulative
name|int
name|b
init|=
operator|(
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
operator|(
name|s
operator|+
name|psOverflow
index|[
name|r
operator|-
literal|1
index|]
operator|)
operator|&
operator|(
name|L8_L
operator|<<
literal|7
operator|)
argument_list|)
operator|>>
literal|3
operator|)
operator|<<
literal|3
decl_stmt|;
comment|// bit position of byte with r-th 1 bit.
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
comment|// bit rank in byte at b
comment|// Select bit l from byte (x>>> b):
name|int
name|selectIndex
init|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|x
operator|>>>
name|b
operator|)
operator|&
literal|0xFFL
operator|)
operator||
operator|(
operator|(
name|l
operator|-
literal|1
operator|)
operator|<<
literal|8
operator|)
argument_list|)
decl_stmt|;
name|int
name|res
init|=
name|b
operator|+
name|select256
index|[
name|selectIndex
index|]
decl_stmt|;
return|return
name|res
return|;
block|}
DECL|field|L8_L
specifier|private
specifier|final
specifier|static
name|long
name|L8_L
init|=
literal|0x0101010101010101L
decl_stmt|;
DECL|field|psOverflow
specifier|private
specifier|static
specifier|final
name|long
index|[]
name|psOverflow
init|=
operator|new
name|long
index|[
literal|64
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|s
init|=
literal|1
init|;
name|s
operator|<=
literal|64
condition|;
name|s
operator|++
control|)
block|{
name|psOverflow
index|[
name|s
operator|-
literal|1
index|]
operator|=
operator|(
literal|128
operator|-
name|s
operator|)
operator|*
name|L8_L
expr_stmt|;
block|}
block|}
DECL|field|select256
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|select256
init|=
operator|new
name|byte
index|[
literal|8
operator|*
literal|256
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|int
name|b
init|=
literal|0
init|;
name|b
operator|<=
literal|0xFF
condition|;
name|b
operator|++
control|)
block|{
for|for
control|(
name|int
name|s
init|=
literal|1
init|;
name|s
operator|<=
literal|8
condition|;
name|s
operator|++
control|)
block|{
name|int
name|byteIndex
init|=
name|b
operator||
operator|(
operator|(
name|s
operator|-
literal|1
operator|)
operator|<<
literal|8
operator|)
decl_stmt|;
name|int
name|bitIndex
init|=
name|selectNaive
argument_list|(
name|b
argument_list|,
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitIndex
operator|<
literal|0
condition|)
block|{
name|bitIndex
operator|=
literal|127
expr_stmt|;
comment|// positive as byte
block|}
assert|assert
name|bitIndex
operator|>=
literal|0
assert|;
assert|assert
operator|(
operator|(
name|byte
operator|)
name|bitIndex
operator|)
operator|>=
literal|0
assert|;
comment|// non negative as byte, no need to mask the sign
name|select256
index|[
name|byteIndex
index|]
operator|=
operator|(
name|byte
operator|)
name|bitIndex
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Naive implementation of {@link #select(long,int)}, using {@link Long#numberOfTrailingZeros} repetitively.    * Works relatively fast for low ranks.    * @return The index of the r-th 1 bit in x, or -1 if no such bit exists.    */
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
operator|-
literal|1
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
