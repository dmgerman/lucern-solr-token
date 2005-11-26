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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** Floating point numbers smaller than 32 bits.  *  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|SmallFloat
specifier|public
class|class
name|SmallFloat
block|{
comment|/** Converts a 32 bit float to an 8 bit float.    *<br>Values less than zero are all mapped to zero.    *<br>Values are truncated (rounded down) to the nearest 8 bit value.    *<br>Values between zero and the smallest representable value    *  are rounded up.    *    * @param f the 32 bit float to be converted to an 8 bit float (byte)    * @param numMantissaBits the number of mantissa bits to use in the byte, with the remainder to be used in the exponent    * @param zeroExp the zero-point in the range of exponent values    * @return the 8 bit float representation    */
DECL|method|floatToByte
specifier|public
specifier|static
name|byte
name|floatToByte
parameter_list|(
name|float
name|f
parameter_list|,
name|int
name|numMantissaBits
parameter_list|,
name|int
name|zeroExp
parameter_list|)
block|{
comment|// Adjustment from a float zero exponent to our zero exponent,
comment|// shifted over to our exponent position.
name|int
name|fzero
init|=
operator|(
literal|63
operator|-
name|zeroExp
operator|)
operator|<<
name|numMantissaBits
decl_stmt|;
name|int
name|bits
init|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|int
name|smallfloat
init|=
name|bits
operator|>>
operator|(
literal|24
operator|-
name|numMantissaBits
operator|)
decl_stmt|;
if|if
condition|(
name|smallfloat
operator|<
name|fzero
condition|)
block|{
return|return
operator|(
name|bits
operator|<=
literal|0
operator|)
condition|?
operator|(
name|byte
operator|)
literal|0
comment|// negative numbers and zero both map to 0 byte
else|:
operator|(
name|byte
operator|)
literal|1
return|;
comment|// underflow is mapped to smallest non-zero number.
block|}
elseif|else
if|if
condition|(
name|smallfloat
operator|>=
name|fzero
operator|+
literal|0x100
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// overflow maps to largest number
block|}
else|else
block|{
return|return
call|(
name|byte
call|)
argument_list|(
name|smallfloat
operator|-
name|fzero
argument_list|)
return|;
block|}
block|}
comment|/** Converts an 8 bit float to a 32 bit float. */
DECL|method|byteToFloat
specifier|public
specifier|static
name|float
name|byteToFloat
parameter_list|(
name|byte
name|b
parameter_list|,
name|int
name|numMantissaBits
parameter_list|,
name|int
name|zeroExp
parameter_list|)
block|{
comment|// on Java1.5& 1.6 JVMs, prebuilding a decoding array and doing a lookup
comment|// is only a little bit faster (anywhere from 0% to 7%)
if|if
condition|(
name|b
operator|==
literal|0
condition|)
return|return
literal|0.0f
return|;
name|int
name|bits
init|=
operator|(
name|b
operator|&
literal|0xff
operator|)
operator|<<
operator|(
literal|24
operator|-
name|numMantissaBits
operator|)
decl_stmt|;
name|bits
operator|+=
operator|(
literal|63
operator|-
name|zeroExp
operator|)
operator|<<
literal|24
expr_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|bits
argument_list|)
return|;
block|}
comment|//
comment|// Some specializations of the generic functions follow.
comment|// The generic functions are just as fast with current (1.5)
comment|// -server JVMs, but still slower with client JVMs.
comment|//
comment|/** floatToByte(b, mantissaBits=3, zeroExponent=15)    *<br>smallest non-zero value = 5.820766E-10    *<br>largest value = 7.5161928E9    *<br>epsilon = 0.125    */
DECL|method|floatToByte315
specifier|public
specifier|static
name|byte
name|floatToByte315
parameter_list|(
name|float
name|f
parameter_list|)
block|{
name|int
name|bits
init|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|int
name|smallfloat
init|=
name|bits
operator|>>
operator|(
literal|24
operator|-
literal|3
operator|)
decl_stmt|;
if|if
condition|(
name|smallfloat
operator|<
operator|(
literal|63
operator|-
literal|15
operator|)
operator|<<
literal|3
condition|)
block|{
return|return
operator|(
name|bits
operator|<=
literal|0
operator|)
condition|?
operator|(
name|byte
operator|)
literal|0
else|:
operator|(
name|byte
operator|)
literal|1
return|;
block|}
if|if
condition|(
name|smallfloat
operator|>=
operator|(
operator|(
literal|63
operator|-
literal|15
operator|)
operator|<<
literal|3
operator|)
operator|+
literal|0x100
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
call|(
name|byte
call|)
argument_list|(
name|smallfloat
operator|-
operator|(
operator|(
literal|63
operator|-
literal|15
operator|)
operator|<<
literal|3
operator|)
argument_list|)
return|;
block|}
comment|/** byteToFloat(b, mantissaBits=3, zeroExponent=15) */
DECL|method|byte315ToFloat
specifier|public
specifier|static
name|float
name|byte315ToFloat
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
comment|// on Java1.5& 1.6 JVMs, prebuilding a decoding array and doing a lookup
comment|// is only a little bit faster (anywhere from 0% to 7%)
if|if
condition|(
name|b
operator|==
literal|0
condition|)
return|return
literal|0.0f
return|;
name|int
name|bits
init|=
operator|(
name|b
operator|&
literal|0xff
operator|)
operator|<<
operator|(
literal|24
operator|-
literal|3
operator|)
decl_stmt|;
name|bits
operator|+=
operator|(
literal|63
operator|-
literal|15
operator|)
operator|<<
literal|24
expr_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|bits
argument_list|)
return|;
block|}
comment|/** floatToByte(b, mantissaBits=5, zeroExponent=2)    *<br>smallest nonzero value = 0.033203125    *<br>largest value = 1984.0    *<br>epsilon = 0.03125    */
DECL|method|floatToByte52
specifier|public
specifier|static
name|byte
name|floatToByte52
parameter_list|(
name|float
name|f
parameter_list|)
block|{
name|int
name|bits
init|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|int
name|smallfloat
init|=
name|bits
operator|>>
operator|(
literal|24
operator|-
literal|5
operator|)
decl_stmt|;
if|if
condition|(
name|smallfloat
operator|<
operator|(
literal|63
operator|-
literal|2
operator|)
operator|<<
literal|5
condition|)
block|{
return|return
operator|(
name|bits
operator|<=
literal|0
operator|)
condition|?
operator|(
name|byte
operator|)
literal|0
else|:
operator|(
name|byte
operator|)
literal|1
return|;
block|}
if|if
condition|(
name|smallfloat
operator|>=
operator|(
operator|(
literal|63
operator|-
literal|2
operator|)
operator|<<
literal|5
operator|)
operator|+
literal|0x100
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
call|(
name|byte
call|)
argument_list|(
name|smallfloat
operator|-
operator|(
operator|(
literal|63
operator|-
literal|2
operator|)
operator|<<
literal|5
operator|)
argument_list|)
return|;
block|}
comment|/** byteToFloat(b, mantissaBits=5, zeroExponent=2) */
DECL|method|byte52ToFloat
specifier|public
specifier|static
name|float
name|byte52ToFloat
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
comment|// on Java1.5& 1.6 JVMs, prebuilding a decoding array and doing a lookup
comment|// is only a little bit faster (anywhere from 0% to 7%)
if|if
condition|(
name|b
operator|==
literal|0
condition|)
return|return
literal|0.0f
return|;
name|int
name|bits
init|=
operator|(
name|b
operator|&
literal|0xff
operator|)
operator|<<
operator|(
literal|24
operator|-
literal|5
operator|)
decl_stmt|;
name|bits
operator|+=
operator|(
literal|63
operator|-
literal|2
operator|)
operator|<<
literal|24
expr_stmt|;
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|bits
argument_list|)
return|;
block|}
block|}
end_class
end_unit
