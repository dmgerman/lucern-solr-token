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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestEliasFanoSequence
specifier|public
class|class
name|TestEliasFanoSequence
extends|extends
name|LuceneTestCase
block|{
DECL|method|makeEncoder
specifier|private
specifier|static
name|EliasFanoEncoder
name|makeEncoder
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|long
name|indexInterval
parameter_list|)
block|{
name|long
name|upperBound
init|=
operator|-
literal|1L
decl_stmt|;
for|for
control|(
name|long
name|value
range|:
name|values
control|)
block|{
name|assertTrue
argument_list|(
name|value
operator|>=
name|upperBound
argument_list|)
expr_stmt|;
comment|// test data ok
name|upperBound
operator|=
name|value
expr_stmt|;
block|}
name|EliasFanoEncoder
name|efEncoder
init|=
operator|new
name|EliasFanoEncoder
argument_list|(
name|values
operator|.
name|length
argument_list|,
name|upperBound
argument_list|,
name|indexInterval
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|value
range|:
name|values
control|)
block|{
name|efEncoder
operator|.
name|encodeNext
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|efEncoder
return|;
block|}
DECL|method|tstDecodeAllNext
specifier|private
specifier|static
name|void
name|tstDecodeAllNext
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|EliasFanoDecoder
name|efd
parameter_list|)
block|{
name|efd
operator|.
name|toBeforeSequence
argument_list|()
expr_stmt|;
name|long
name|nextValue
init|=
name|efd
operator|.
name|nextValue
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|expValue
range|:
name|values
control|)
block|{
name|assertFalse
argument_list|(
literal|"nextValue at end too early"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
operator|==
name|nextValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expValue
argument_list|,
name|nextValue
argument_list|)
expr_stmt|;
name|nextValue
operator|=
name|efd
operator|.
name|nextValue
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|nextValue
argument_list|)
expr_stmt|;
block|}
DECL|method|tstDecodeAllPrev
specifier|private
specifier|static
name|void
name|tstDecodeAllPrev
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|EliasFanoDecoder
name|efd
parameter_list|)
block|{
name|efd
operator|.
name|toAfterSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|values
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|long
name|previousValue
init|=
name|efd
operator|.
name|previousValue
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"previousValue at end too early"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
operator|==
name|previousValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|previousValue
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|efd
operator|.
name|previousValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tstDecodeAllAdvanceToExpected
specifier|private
specifier|static
name|void
name|tstDecodeAllAdvanceToExpected
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|EliasFanoDecoder
name|efd
parameter_list|)
block|{
name|efd
operator|.
name|toBeforeSequence
argument_list|()
expr_stmt|;
name|long
name|previousValue
init|=
operator|-
literal|1L
decl_stmt|;
name|long
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|expValue
range|:
name|values
control|)
block|{
if|if
condition|(
name|expValue
operator|>
name|previousValue
condition|)
block|{
name|long
name|advanceValue
init|=
name|efd
operator|.
name|advanceToValue
argument_list|(
name|expValue
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"advanceValue at end too early"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
operator|==
name|advanceValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expValue
argument_list|,
name|advanceValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|index
argument_list|,
name|efd
operator|.
name|currentIndex
argument_list|()
argument_list|)
expr_stmt|;
name|previousValue
operator|=
name|expValue
expr_stmt|;
block|}
name|index
operator|++
expr_stmt|;
block|}
name|long
name|advanceValue
init|=
name|efd
operator|.
name|advanceToValue
argument_list|(
name|previousValue
operator|+
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"at end"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|advanceValue
argument_list|)
expr_stmt|;
block|}
DECL|method|tstDecodeAdvanceToMultiples
specifier|private
specifier|static
name|void
name|tstDecodeAdvanceToMultiples
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|EliasFanoDecoder
name|efd
parameter_list|,
specifier|final
name|long
name|m
parameter_list|)
block|{
comment|// test advancing to multiples of m
assert|assert
name|m
operator|>
literal|0
assert|;
name|long
name|previousValue
init|=
operator|-
literal|1L
decl_stmt|;
name|long
name|index
init|=
literal|0
decl_stmt|;
name|long
name|mm
init|=
name|m
decl_stmt|;
name|efd
operator|.
name|toBeforeSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|expValue
range|:
name|values
control|)
block|{
comment|// mm> previousValue
if|if
condition|(
name|expValue
operator|>=
name|mm
condition|)
block|{
name|long
name|advanceValue
init|=
name|efd
operator|.
name|advanceToValue
argument_list|(
name|mm
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"advanceValue at end too early"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
operator|==
name|advanceValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expValue
argument_list|,
name|advanceValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|index
argument_list|,
name|efd
operator|.
name|currentIndex
argument_list|()
argument_list|)
expr_stmt|;
name|previousValue
operator|=
name|expValue
expr_stmt|;
do|do
block|{
name|mm
operator|+=
name|m
expr_stmt|;
block|}
do|while
condition|(
name|mm
operator|<=
name|previousValue
condition|)
do|;
block|}
name|index
operator|++
expr_stmt|;
block|}
name|long
name|advanceValue
init|=
name|efd
operator|.
name|advanceToValue
argument_list|(
name|mm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|advanceValue
argument_list|)
expr_stmt|;
block|}
DECL|method|tstDecodeBackToMultiples
specifier|private
specifier|static
name|void
name|tstDecodeBackToMultiples
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|EliasFanoDecoder
name|efd
parameter_list|,
specifier|final
name|long
name|m
parameter_list|)
block|{
comment|// test backing to multiples of m
assert|assert
name|m
operator|>
literal|0
assert|;
name|efd
operator|.
name|toAfterSequence
argument_list|()
expr_stmt|;
name|int
name|index
init|=
name|values
operator|.
name|length
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|long
name|advanceValue
init|=
name|efd
operator|.
name|backToValue
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|advanceValue
argument_list|)
expr_stmt|;
return|return;
comment|// empty values, nothing to go back to/from
block|}
name|long
name|expValue
init|=
name|values
index|[
name|index
index|]
decl_stmt|;
name|long
name|previousValue
init|=
name|expValue
operator|+
literal|1
decl_stmt|;
name|long
name|mm
init|=
operator|(
name|expValue
operator|/
name|m
operator|)
operator|*
name|m
decl_stmt|;
while|while
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|expValue
operator|=
name|values
index|[
name|index
index|]
expr_stmt|;
assert|assert
name|mm
operator|<
name|previousValue
assert|;
if|if
condition|(
name|expValue
operator|<=
name|mm
condition|)
block|{
name|long
name|backValue
init|=
name|efd
operator|.
name|backToValue
argument_list|(
name|mm
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"backToValue at end too early"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
operator|==
name|backValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expValue
argument_list|,
name|backValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|index
argument_list|,
name|efd
operator|.
name|currentIndex
argument_list|()
argument_list|)
expr_stmt|;
name|previousValue
operator|=
name|expValue
expr_stmt|;
do|do
block|{
name|mm
operator|-=
name|m
expr_stmt|;
block|}
do|while
condition|(
name|mm
operator|>=
name|previousValue
condition|)
do|;
block|}
name|index
operator|--
expr_stmt|;
block|}
name|long
name|backValue
init|=
name|efd
operator|.
name|backToValue
argument_list|(
name|mm
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|backValue
argument_list|)
expr_stmt|;
block|}
DECL|method|tstEqual
specifier|private
specifier|static
name|void
name|tstEqual
parameter_list|(
name|String
name|mes
parameter_list|,
name|long
index|[]
name|exp
parameter_list|,
name|long
index|[]
name|act
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|mes
operator|+
literal|".length"
argument_list|,
name|exp
operator|.
name|length
argument_list|,
name|act
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exp
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|exp
index|[
name|i
index|]
operator|!=
name|act
index|[
name|i
index|]
condition|)
block|{
name|fail
argument_list|(
name|mes
operator|+
literal|"["
operator|+
name|i
operator|+
literal|"] "
operator|+
name|exp
index|[
name|i
index|]
operator|+
literal|" != "
operator|+
name|act
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|tstDecodeAll
specifier|private
specifier|static
name|void
name|tstDecodeAll
parameter_list|(
name|EliasFanoEncoder
name|efEncoder
parameter_list|,
name|long
index|[]
name|values
parameter_list|)
block|{
name|tstDecodeAllNext
argument_list|(
name|values
argument_list|,
name|efEncoder
operator|.
name|getDecoder
argument_list|()
argument_list|)
expr_stmt|;
name|tstDecodeAllPrev
argument_list|(
name|values
argument_list|,
name|efEncoder
operator|.
name|getDecoder
argument_list|()
argument_list|)
expr_stmt|;
name|tstDecodeAllAdvanceToExpected
argument_list|(
name|values
argument_list|,
name|efEncoder
operator|.
name|getDecoder
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|tstEFS
specifier|private
specifier|static
name|void
name|tstEFS
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|long
index|[]
name|expHighLongs
parameter_list|,
name|long
index|[]
name|expLowLongs
parameter_list|)
block|{
name|EliasFanoEncoder
name|efEncoder
init|=
name|makeEncoder
argument_list|(
name|values
argument_list|,
name|EliasFanoEncoder
operator|.
name|DEFAULT_INDEX_INTERVAL
argument_list|)
decl_stmt|;
name|tstEqual
argument_list|(
literal|"upperBits"
argument_list|,
name|expHighLongs
argument_list|,
name|efEncoder
operator|.
name|getUpperBits
argument_list|()
argument_list|)
expr_stmt|;
name|tstEqual
argument_list|(
literal|"lowerBits"
argument_list|,
name|expLowLongs
argument_list|,
name|efEncoder
operator|.
name|getLowerBits
argument_list|()
argument_list|)
expr_stmt|;
name|tstDecodeAll
argument_list|(
name|efEncoder
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|tstEFS2
specifier|private
specifier|static
name|void
name|tstEFS2
parameter_list|(
name|long
index|[]
name|values
parameter_list|)
block|{
name|EliasFanoEncoder
name|efEncoder
init|=
name|makeEncoder
argument_list|(
name|values
argument_list|,
name|EliasFanoEncoder
operator|.
name|DEFAULT_INDEX_INTERVAL
argument_list|)
decl_stmt|;
name|tstDecodeAll
argument_list|(
name|efEncoder
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|tstEFSadvanceToAndBackToMultiples
specifier|private
specifier|static
name|void
name|tstEFSadvanceToAndBackToMultiples
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|long
name|maxValue
parameter_list|,
name|long
name|minAdvanceMultiple
parameter_list|)
block|{
name|EliasFanoEncoder
name|efEncoder
init|=
name|makeEncoder
argument_list|(
name|values
argument_list|,
name|EliasFanoEncoder
operator|.
name|DEFAULT_INDEX_INTERVAL
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|m
init|=
name|minAdvanceMultiple
init|;
name|m
operator|<=
name|maxValue
condition|;
name|m
operator|+=
literal|1
control|)
block|{
name|tstDecodeAdvanceToMultiples
argument_list|(
name|values
argument_list|,
name|efEncoder
operator|.
name|getDecoder
argument_list|()
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|tstDecodeBackToMultiples
argument_list|(
name|values
argument_list|,
name|efEncoder
operator|.
name|getDecoder
argument_list|()
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|tstEFVI
specifier|private
name|EliasFanoEncoder
name|tstEFVI
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|long
name|indexInterval
parameter_list|,
name|long
index|[]
name|expIndexBits
parameter_list|)
block|{
name|EliasFanoEncoder
name|efEncVI
init|=
name|makeEncoder
argument_list|(
name|values
argument_list|,
name|indexInterval
argument_list|)
decl_stmt|;
name|tstEqual
argument_list|(
literal|"upperZeroBitPositionIndex"
argument_list|,
name|expIndexBits
argument_list|,
name|efEncVI
operator|.
name|getIndexBits
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|efEncVI
return|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneValue1
specifier|public
name|void
name|testOneValue1
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|}
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
literal|0x1L
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{}
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoValues1
specifier|public
name|void
name|testTwoValues1
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
literal|0x3L
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{}
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneValue2
specifier|public
name|void
name|testOneValue2
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|63
block|}
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
literal|2
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{
literal|31
block|}
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneMaxValue
specifier|public
name|void
name|testOneMaxValue
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
literal|2
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|2
block|}
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoMinMaxValues
specifier|public
name|void
name|testTwoMinMaxValues
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
literal|0x11
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{
literal|0xE000000000000000L
block|,
literal|0x03FFFFFFFFFFFFFFL
block|}
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoMaxValues
specifier|public
name|void
name|testTwoMaxValues
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MAX_VALUE
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
literal|0x18
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{
operator|-
literal|1L
block|,
literal|0x03FFFFFFFFFFFFFFL
block|}
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testExample1
specifier|public
name|void
name|testExample1
parameter_list|()
block|{
comment|// Figure 1 from Vigna 2012 paper
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|5
block|,
literal|8
block|,
literal|8
block|,
literal|15
block|,
literal|32
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|parseLong
argument_list|(
literal|"0011000001"
argument_list|,
literal|2
argument_list|)
block|}
decl_stmt|;
comment|// reverse block and bit order
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|parseLong
argument_list|(
literal|"1000001011010"
argument_list|,
literal|2
argument_list|)
block|}
decl_stmt|;
comment|// reverse block and bit order
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashCodeEquals
specifier|public
name|void
name|testHashCodeEquals
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|5
block|,
literal|8
block|,
literal|8
block|,
literal|15
block|,
literal|32
block|}
decl_stmt|;
name|EliasFanoEncoder
name|efEncoder1
init|=
name|makeEncoder
argument_list|(
name|values
argument_list|,
name|EliasFanoEncoder
operator|.
name|DEFAULT_INDEX_INTERVAL
argument_list|)
decl_stmt|;
name|EliasFanoEncoder
name|efEncoder2
init|=
name|makeEncoder
argument_list|(
name|values
argument_list|,
name|EliasFanoEncoder
operator|.
name|DEFAULT_INDEX_INTERVAL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|efEncoder1
argument_list|,
name|efEncoder2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|efEncoder1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|efEncoder2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|EliasFanoEncoder
name|efEncoder3
init|=
name|makeEncoder
argument_list|(
operator|new
name|long
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|,
name|EliasFanoEncoder
operator|.
name|DEFAULT_INDEX_INTERVAL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|efEncoder1
operator|.
name|equals
argument_list|(
name|efEncoder3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|efEncoder3
operator|.
name|equals
argument_list|(
name|efEncoder1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|efEncoder1
operator|.
name|hashCode
argument_list|()
operator|==
name|efEncoder3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// implementation ok for these.
block|}
DECL|method|testMonotoneSequences
specifier|public
name|void
name|testMonotoneSequences
parameter_list|()
block|{
comment|//for (int s = 2; s< 1222; s++) {
for|for
control|(
name|int
name|s
init|=
literal|2
init|;
name|s
operator|<
literal|4422
condition|;
name|s
operator|++
control|)
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|s
index|]
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
name|s
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|(
name|i
operator|/
literal|2
operator|)
expr_stmt|;
comment|// upperbound smaller than number of values, only upper bits encoded
block|}
name|tstEFS2
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStrictMonotoneSequences
specifier|public
name|void
name|testStrictMonotoneSequences
parameter_list|()
block|{
comment|// for (int s = 2; s< 1222; s++) {
for|for
control|(
name|int
name|s
init|=
literal|2
init|;
name|s
operator|<
literal|4422
condition|;
name|s
operator|++
control|)
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|s
index|]
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
name|s
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|i
operator|*
operator|(
operator|(
name|long
operator|)
name|i
operator|-
literal|1
operator|)
operator|/
literal|2
expr_stmt|;
comment|// Add a gap of (s-1) to previous
comment|// s = (s*(s+1) - (s-1)*s)/2
block|}
name|tstEFS2
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testHighBitLongZero
specifier|public
name|void
name|testHighBitLongZero
parameter_list|()
block|{
specifier|final
name|int
name|s
init|=
literal|65
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|s
index|]
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
name|s
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|values
index|[
name|s
operator|-
literal|1
index|]
operator|=
literal|128
expr_stmt|;
name|long
index|[]
name|expHighBits
init|=
operator|new
name|long
index|[]
block|{
operator|-
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|long
index|[]
name|expLowBits
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|tstEFS
argument_list|(
name|values
argument_list|,
name|expHighBits
argument_list|,
name|expLowBits
argument_list|)
expr_stmt|;
block|}
DECL|method|testAdvanceToAndBackToMultiples
specifier|public
name|void
name|testAdvanceToAndBackToMultiples
parameter_list|()
block|{
for|for
control|(
name|int
name|s
init|=
literal|2
init|;
name|s
operator|<
literal|130
condition|;
name|s
operator|++
control|)
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|s
index|]
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
name|s
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|i
operator|*
operator|(
operator|(
name|long
operator|)
name|i
operator|+
literal|1
operator|)
operator|/
literal|2
expr_stmt|;
comment|// Add a gap of s to previous
comment|// s = (s*(s+1) - (s-1)*s)/2
block|}
name|tstEFSadvanceToAndBackToMultiples
argument_list|(
name|values
argument_list|,
name|values
index|[
name|s
operator|-
literal|1
index|]
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|emptyLongs
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|tstEFVI
argument_list|(
name|emptyLongs
argument_list|,
name|indexInterval
argument_list|,
name|emptyLongs
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxContentEmptyIndex
specifier|public
name|void
name|testMaxContentEmptyIndex
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|twoLongs
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|long
index|[]
name|emptyLongs
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
name|tstEFVI
argument_list|(
name|twoLongs
argument_list|,
name|indexInterval
argument_list|,
name|emptyLongs
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinContentNonEmptyIndex
specifier|public
name|void
name|testMinContentNonEmptyIndex
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|twoLongs
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|2
block|}
decl_stmt|;
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|3
block|}
decl_stmt|;
comment|// high bits 1001, index position after zero bit.
name|tstEFVI
argument_list|(
name|twoLongs
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexAdvanceToLast
specifier|public
name|void
name|testIndexAdvanceToLast
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|twoLongs
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|2
block|}
decl_stmt|;
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|3
block|}
decl_stmt|;
comment|// high bits 1001
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|twoLongs
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|efEncVI
operator|.
name|getDecoder
argument_list|()
operator|.
name|advanceToValue
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexAdvanceToAfterLast
specifier|public
name|void
name|testIndexAdvanceToAfterLast
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|twoLongs
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|2
block|}
decl_stmt|;
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|3
block|}
decl_stmt|;
comment|// high bits 1001
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|twoLongs
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|efEncVI
operator|.
name|getDecoder
argument_list|()
operator|.
name|advanceToValue
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexAdvanceToFirst
specifier|public
name|void
name|testIndexAdvanceToFirst
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|twoLongs
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|2
block|}
decl_stmt|;
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|3
block|}
decl_stmt|;
comment|// high bits 1001
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|twoLongs
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|efEncVI
operator|.
name|getDecoder
argument_list|()
operator|.
name|advanceToValue
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoIndexEntries
specifier|public
name|void
name|testTwoIndexEntries
parameter_list|()
block|{
name|long
name|indexInterval
init|=
literal|2
decl_stmt|;
name|long
index|[]
name|twoLongs
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|4
operator|+
literal|8
operator|*
literal|16
block|}
decl_stmt|;
comment|// high bits 0b10101010101
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|twoLongs
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|EliasFanoDecoder
name|efDecVI
init|=
name|efEncVI
operator|.
name|getDecoder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"advance 0"
argument_list|,
literal|0
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"advance 5"
argument_list|,
literal|5
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"advance 6"
argument_list|,
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExample2a
specifier|public
name|void
name|testExample2a
parameter_list|()
block|{
comment|// Figure 2 from Vigna 2012 paper
name|long
name|indexInterval
init|=
literal|4
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|5
block|,
literal|8
block|,
literal|8
block|,
literal|15
block|,
literal|32
block|}
decl_stmt|;
comment|// two low bits, high values 1,2,2,3,8.
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|8
operator|+
literal|12
operator|*
literal|16
block|}
decl_stmt|;
comment|// high bits 0b 0001 0000 0101 1010
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|values
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|EliasFanoDecoder
name|efDecVI
init|=
name|efEncVI
operator|.
name|getDecoder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"advance 22"
argument_list|,
literal|32
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|22
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExample2b
specifier|public
name|void
name|testExample2b
parameter_list|()
block|{
comment|// Figure 2 from Vigna 2012 paper
name|long
name|indexInterval
init|=
literal|4
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|5
block|,
literal|8
block|,
literal|8
block|,
literal|15
block|,
literal|32
block|}
decl_stmt|;
comment|// two low bits, high values 1,2,2,3,8.
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[]
block|{
literal|8
operator|+
literal|12
operator|*
literal|16
block|}
decl_stmt|;
comment|// high bits 0b 0001 0000 0101 1010
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|values
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|EliasFanoDecoder
name|efDecVI
init|=
name|efEncVI
operator|.
name|getDecoder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"initial next"
argument_list|,
literal|5
argument_list|,
name|efDecVI
operator|.
name|nextValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"advance 22"
argument_list|,
literal|32
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|22
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExample2NoIndex1
specifier|public
name|void
name|testExample2NoIndex1
parameter_list|()
block|{
comment|// Figure 2 from Vigna 2012 paper, no index, test broadword selection.
name|long
name|indexInterval
init|=
literal|16
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|5
block|,
literal|8
block|,
literal|8
block|,
literal|15
block|,
literal|32
block|}
decl_stmt|;
comment|// two low bits, high values 1,2,2,3,8.
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
comment|// high bits 0b 0001 0000 0101 1010
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|values
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|EliasFanoDecoder
name|efDecVI
init|=
name|efEncVI
operator|.
name|getDecoder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"advance 22"
argument_list|,
literal|32
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|22
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExample2NoIndex2
specifier|public
name|void
name|testExample2NoIndex2
parameter_list|()
block|{
comment|// Figure 2 from Vigna 2012 paper, no index, test broadword selection.
name|long
name|indexInterval
init|=
literal|16
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|5
block|,
literal|8
block|,
literal|8
block|,
literal|15
block|,
literal|32
block|}
decl_stmt|;
comment|// two low bits, high values 1,2,2,3,8.
name|long
index|[]
name|indexLongs
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
comment|// high bits 0b 0001 0000 0101 1010
name|EliasFanoEncoder
name|efEncVI
init|=
name|tstEFVI
argument_list|(
name|values
argument_list|,
name|indexInterval
argument_list|,
name|indexLongs
argument_list|)
decl_stmt|;
name|EliasFanoDecoder
name|efDecVI
init|=
name|efEncVI
operator|.
name|getDecoder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"initial next"
argument_list|,
literal|5
argument_list|,
name|efDecVI
operator|.
name|nextValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"advance 22"
argument_list|,
literal|32
argument_list|,
name|efDecVI
operator|.
name|advanceToValue
argument_list|(
literal|22
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
