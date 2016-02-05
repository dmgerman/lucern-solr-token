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
begin_class
DECL|class|TestSmallFloat
specifier|public
class|class
name|TestSmallFloat
extends|extends
name|LuceneTestCase
block|{
comment|// original lucene byteToFloat
DECL|method|orig_byteToFloat
specifier|static
name|float
name|orig_byteToFloat
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
comment|// zero is a special case
return|return
literal|0.0f
return|;
name|int
name|mantissa
init|=
name|b
operator|&
literal|7
decl_stmt|;
name|int
name|exponent
init|=
operator|(
name|b
operator|>>
literal|3
operator|)
operator|&
literal|31
decl_stmt|;
name|int
name|bits
init|=
operator|(
operator|(
name|exponent
operator|+
operator|(
literal|63
operator|-
literal|15
operator|)
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
name|mantissa
operator|<<
literal|21
operator|)
decl_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|bits
argument_list|)
return|;
block|}
comment|// original lucene floatToByte (since lucene 1.3)
DECL|method|orig_floatToByte_v13
specifier|static
name|byte
name|orig_floatToByte_v13
parameter_list|(
name|float
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|<
literal|0.0f
condition|)
comment|// round negatives up to zero
name|f
operator|=
literal|0.0f
expr_stmt|;
if|if
condition|(
name|f
operator|==
literal|0.0f
condition|)
comment|// zero is a special case
return|return
literal|0
return|;
name|int
name|bits
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|// parse float into parts
name|int
name|mantissa
init|=
operator|(
name|bits
operator|&
literal|0xffffff
operator|)
operator|>>
literal|21
decl_stmt|;
name|int
name|exponent
init|=
operator|(
operator|(
operator|(
name|bits
operator|>>
literal|24
operator|)
operator|&
literal|0x7f
operator|)
operator|-
literal|63
operator|)
operator|+
literal|15
decl_stmt|;
if|if
condition|(
name|exponent
operator|>
literal|31
condition|)
block|{
comment|// overflow: use max value
name|exponent
operator|=
literal|31
expr_stmt|;
name|mantissa
operator|=
literal|7
expr_stmt|;
block|}
if|if
condition|(
name|exponent
operator|<
literal|0
condition|)
block|{
comment|// underflow: use min value
name|exponent
operator|=
literal|0
expr_stmt|;
name|mantissa
operator|=
literal|1
expr_stmt|;
block|}
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
name|exponent
operator|<<
literal|3
operator|)
operator||
name|mantissa
argument_list|)
return|;
comment|// pack into a byte
block|}
comment|// This is the original lucene floatToBytes (from v1.3)
comment|// except with the underflow detection bug fixed for values like 5.8123817E-10f
DECL|method|orig_floatToByte
specifier|static
name|byte
name|orig_floatToByte
parameter_list|(
name|float
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|<
literal|0.0f
condition|)
comment|// round negatives up to zero
name|f
operator|=
literal|0.0f
expr_stmt|;
if|if
condition|(
name|f
operator|==
literal|0.0f
condition|)
comment|// zero is a special case
return|return
literal|0
return|;
name|int
name|bits
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|// parse float into parts
name|int
name|mantissa
init|=
operator|(
name|bits
operator|&
literal|0xffffff
operator|)
operator|>>
literal|21
decl_stmt|;
name|int
name|exponent
init|=
operator|(
operator|(
operator|(
name|bits
operator|>>
literal|24
operator|)
operator|&
literal|0x7f
operator|)
operator|-
literal|63
operator|)
operator|+
literal|15
decl_stmt|;
if|if
condition|(
name|exponent
operator|>
literal|31
condition|)
block|{
comment|// overflow: use max value
name|exponent
operator|=
literal|31
expr_stmt|;
name|mantissa
operator|=
literal|7
expr_stmt|;
block|}
if|if
condition|(
name|exponent
operator|<
literal|0
operator|||
name|exponent
operator|==
literal|0
operator|&&
name|mantissa
operator|==
literal|0
condition|)
block|{
comment|// underflow: use min value
name|exponent
operator|=
literal|0
expr_stmt|;
name|mantissa
operator|=
literal|1
expr_stmt|;
block|}
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
name|exponent
operator|<<
literal|3
operator|)
operator||
name|mantissa
argument_list|)
return|;
comment|// pack into a byte
block|}
DECL|method|testByteToFloat
specifier|public
name|void
name|testByteToFloat
parameter_list|()
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
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|float
name|f1
init|=
name|orig_byteToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
decl_stmt|;
name|float
name|f2
init|=
name|SmallFloat
operator|.
name|byteToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
literal|3
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|float
name|f3
init|=
name|SmallFloat
operator|.
name|byte315ToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|f2
argument_list|,
name|f3
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|float
name|f4
init|=
name|SmallFloat
operator|.
name|byteToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|float
name|f5
init|=
name|SmallFloat
operator|.
name|byte52ToFloat
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|f4
argument_list|,
name|f5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFloatToByte
specifier|public
name|void
name|testFloatToByte
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|orig_floatToByte_v13
argument_list|(
literal|5.8123817E
operator|-
literal|10f
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the old bug (see LUCENE-2937)
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|orig_floatToByte
argument_list|(
literal|5.8123817E
operator|-
literal|10f
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify it's fixed in this test code
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
literal|5.8123817E
operator|-
literal|10f
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify it's fixed
comment|// test some constants
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|// underflow rounds up to smallest positive
name|assertEquals
argument_list|(
literal|255
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|Float
operator|.
name|MAX_VALUE
argument_list|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
comment|// overflow rounds down to largest positive
name|assertEquals
argument_list|(
literal|255
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
comment|// all negatives map to 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
operator|-
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
operator|-
name|Float
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
argument_list|)
expr_stmt|;
comment|// up iterations for more exhaustive test after changing something
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100000
argument_list|)
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|float
name|f
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|f
argument_list|)
condition|)
continue|continue;
comment|// skip NaN
name|byte
name|b1
init|=
name|orig_floatToByte
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|byte
name|b2
init|=
name|SmallFloat
operator|.
name|floatToByte
argument_list|(
name|f
argument_list|,
literal|3
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|byte
name|b3
init|=
name|SmallFloat
operator|.
name|floatToByte315
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b2
argument_list|,
name|b3
argument_list|)
expr_stmt|;
name|byte
name|b4
init|=
name|SmallFloat
operator|.
name|floatToByte
argument_list|(
name|f
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|byte
name|b5
init|=
name|SmallFloat
operator|.
name|floatToByte52
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|b4
argument_list|,
name|b5
argument_list|)
expr_stmt|;
block|}
block|}
comment|/***   // Do an exhaustive test of all possible floating point values   // for the 315 float against the original norm encoding in Similarity.   // Takes 75 seconds on my Pentium4 3GHz, with Java5 -server   public void testAllFloats() {     for(int i = Integer.MIN_VALUE;;i++) {       float f = Float.intBitsToFloat(i);       if (f==f) { // skip non-numbers         byte b1 = orig_floatToByte(f);         byte b2 = SmallFloat.floatToByte315(f);         if (b1!=b2 || b2==0&& f>0) {           fail("Failed floatToByte315 for float " + f + " source bits="+Integer.toHexString(i) + " float raw bits=" + Integer.toHexString(Float.floatToRawIntBits(i)));         }       }       if (i==Integer.MAX_VALUE) break;     }   }   ***/
block|}
end_class
end_unit
