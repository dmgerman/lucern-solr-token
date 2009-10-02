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
comment|/** * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|OpenBitSet
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_class
DECL|class|TestNumericUtils
specifier|public
class|class
name|TestNumericUtils
extends|extends
name|LuceneTestCase
block|{
DECL|method|testLongConversionAndOrdering
specifier|public
name|void
name|testLongConversionAndOrdering
parameter_list|()
throws|throws
name|Exception
block|{
comment|// generate a series of encoded longs, each numerical one bigger than the one before
name|String
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|long
name|l
init|=
operator|-
literal|100000L
init|;
name|l
operator|<
literal|100000L
condition|;
name|l
operator|++
control|)
block|{
name|String
name|act
init|=
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|l
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// test if smaller
name|assertTrue
argument_list|(
literal|"actual bigger than last"
argument_list|,
name|last
operator|.
name|compareTo
argument_list|(
name|act
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// test is back and forward conversion works
name|assertEquals
argument_list|(
literal|"forward and back conversion should generate same long"
argument_list|,
name|l
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|act
argument_list|)
argument_list|)
expr_stmt|;
comment|// next step
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
DECL|method|testIntConversionAndOrdering
specifier|public
name|void
name|testIntConversionAndOrdering
parameter_list|()
throws|throws
name|Exception
block|{
comment|// generate a series of encoded ints, each numerical one bigger than the one before
name|String
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|-
literal|100000
init|;
name|i
operator|<
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|act
init|=
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// test if smaller
name|assertTrue
argument_list|(
literal|"actual bigger than last"
argument_list|,
name|last
operator|.
name|compareTo
argument_list|(
name|act
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// test is back and forward conversion works
name|assertEquals
argument_list|(
literal|"forward and back conversion should generate same int"
argument_list|,
name|i
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|act
argument_list|)
argument_list|)
expr_stmt|;
comment|// next step
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
DECL|method|testLongSpecialValues
specifier|public
name|void
name|testLongSpecialValues
parameter_list|()
throws|throws
name|Exception
block|{
name|long
index|[]
name|vals
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MIN_VALUE
block|,
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|1
block|,
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|2
block|,
operator|-
literal|5003400000000L
block|,
operator|-
literal|4000L
block|,
operator|-
literal|3000L
block|,
operator|-
literal|2000L
block|,
operator|-
literal|1000L
block|,
operator|-
literal|1L
block|,
literal|0L
block|,
literal|1L
block|,
literal|10L
block|,
literal|300L
block|,
literal|50006789999999999L
block|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|2
block|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|String
index|[]
name|prefixVals
init|=
operator|new
name|String
index|[
name|vals
operator|.
name|length
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|prefixVals
index|[
name|i
index|]
operator|=
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// check forward and back conversion
name|assertEquals
argument_list|(
literal|"forward and back conversion should generate same long"
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|prefixVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// test if decoding values as int fails correctly
try|try
block|{
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|prefixVals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"decoding a prefix coded long value as int should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// worked
block|}
block|}
comment|// check sort order (prefixVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|prefixVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"check sort order"
argument_list|,
name|prefixVals
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|compareTo
argument_list|(
name|prefixVals
index|[
name|i
index|]
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// check the prefix encoding, lower precision should have the difference to original value equal to the lower removed bits
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|64
condition|;
name|j
operator|++
control|)
block|{
name|long
name|prefixVal
init|=
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|mask
init|=
operator|(
literal|1L
operator|<<
name|j
operator|)
operator|-
literal|1L
decl_stmt|;
name|assertEquals
argument_list|(
literal|"difference between prefix val and original value for "
operator|+
name|vals
index|[
name|i
index|]
operator|+
literal|" with shift="
operator|+
name|j
argument_list|,
name|vals
index|[
name|i
index|]
operator|&
name|mask
argument_list|,
name|vals
index|[
name|i
index|]
operator|-
name|prefixVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testIntSpecialValues
specifier|public
name|void
name|testIntSpecialValues
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|vals
init|=
operator|new
name|int
index|[]
block|{
name|Integer
operator|.
name|MIN_VALUE
block|,
name|Integer
operator|.
name|MIN_VALUE
operator|+
literal|1
block|,
name|Integer
operator|.
name|MIN_VALUE
operator|+
literal|2
block|,
operator|-
literal|64765767
block|,
operator|-
literal|4000
block|,
operator|-
literal|3000
block|,
operator|-
literal|2000
block|,
operator|-
literal|1000
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|10
block|,
literal|300
block|,
literal|765878989
block|,
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|2
block|,
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
block|,
name|Integer
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|String
index|[]
name|prefixVals
init|=
operator|new
name|String
index|[
name|vals
operator|.
name|length
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|prefixVals
index|[
name|i
index|]
operator|=
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// check forward and back conversion
name|assertEquals
argument_list|(
literal|"forward and back conversion should generate same int"
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|,
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|prefixVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// test if decoding values as long fails correctly
try|try
block|{
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|prefixVals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"decoding a prefix coded int value as long should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// worked
block|}
block|}
comment|// check sort order (prefixVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|prefixVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"check sort order"
argument_list|,
name|prefixVals
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|compareTo
argument_list|(
name|prefixVals
index|[
name|i
index|]
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// check the prefix encoding, lower precision should have the difference to original value equal to the lower removed bits
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|32
condition|;
name|j
operator|++
control|)
block|{
name|int
name|prefixVal
init|=
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|mask
init|=
operator|(
literal|1
operator|<<
name|j
operator|)
operator|-
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
literal|"difference between prefix val and original value for "
operator|+
name|vals
index|[
name|i
index|]
operator|+
literal|" with shift="
operator|+
name|j
argument_list|,
name|vals
index|[
name|i
index|]
operator|&
name|mask
argument_list|,
name|vals
index|[
name|i
index|]
operator|-
name|prefixVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testDoubles
specifier|public
name|void
name|testDoubles
parameter_list|()
throws|throws
name|Exception
block|{
name|double
index|[]
name|vals
init|=
operator|new
name|double
index|[]
block|{
name|Double
operator|.
name|NEGATIVE_INFINITY
block|,
operator|-
literal|2.3E25
block|,
operator|-
literal|1.0E15
block|,
operator|-
literal|1.0
block|,
operator|-
literal|1.0E
operator|-
literal|1
block|,
operator|-
literal|1.0E
operator|-
literal|2
block|,
operator|-
literal|0.0
block|,
operator|+
literal|0.0
block|,
literal|1.0E
operator|-
literal|2
block|,
literal|1.0E
operator|-
literal|1
block|,
literal|1.0
block|,
literal|1.0E15
block|,
literal|2.3E25
block|,
name|Double
operator|.
name|POSITIVE_INFINITY
block|}
decl_stmt|;
name|long
index|[]
name|longVals
init|=
operator|new
name|long
index|[
name|vals
operator|.
name|length
index|]
decl_stmt|;
comment|// check forward and back conversion
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|longVals
index|[
name|i
index|]
operator|=
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"forward and back conversion should generate same double"
argument_list|,
name|Double
operator|.
name|compare
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|longVals
index|[
name|i
index|]
argument_list|)
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// check sort order (prefixVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|longVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"check sort order"
argument_list|,
name|longVals
index|[
name|i
operator|-
literal|1
index|]
operator|<
name|longVals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFloats
specifier|public
name|void
name|testFloats
parameter_list|()
throws|throws
name|Exception
block|{
name|float
index|[]
name|vals
init|=
operator|new
name|float
index|[]
block|{
name|Float
operator|.
name|NEGATIVE_INFINITY
block|,
operator|-
literal|2.3E25f
block|,
operator|-
literal|1.0E15f
block|,
operator|-
literal|1.0f
block|,
operator|-
literal|1.0E
operator|-
literal|1f
block|,
operator|-
literal|1.0E
operator|-
literal|2f
block|,
operator|-
literal|0.0f
block|,
operator|+
literal|0.0f
block|,
literal|1.0E
operator|-
literal|2f
block|,
literal|1.0E
operator|-
literal|1f
block|,
literal|1.0f
block|,
literal|1.0E15f
block|,
literal|2.3E25f
block|,
name|Float
operator|.
name|POSITIVE_INFINITY
block|}
decl_stmt|;
name|int
index|[]
name|intVals
init|=
operator|new
name|int
index|[
name|vals
operator|.
name|length
index|]
decl_stmt|;
comment|// check forward and back conversion
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|intVals
index|[
name|i
index|]
operator|=
name|NumericUtils
operator|.
name|floatToSortableInt
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"forward and back conversion should generate same double"
argument_list|,
name|Float
operator|.
name|compare
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|intVals
index|[
name|i
index|]
argument_list|)
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// check sort order (prefixVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|intVals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"check sort order"
argument_list|,
name|intVals
index|[
name|i
operator|-
literal|1
index|]
operator|<
name|intVals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// INFO: Tests for trieCodeLong()/trieCodeInt() not needed because implicitely tested by range filter tests
comment|/** Note: The neededBounds iterator must be unsigned (easier understanding what's happening) */
DECL|method|assertLongRangeSplit
specifier|protected
name|void
name|assertLongRangeSplit
parameter_list|(
specifier|final
name|long
name|lower
parameter_list|,
specifier|final
name|long
name|upper
parameter_list|,
name|int
name|precisionStep
parameter_list|,
specifier|final
name|boolean
name|useBitSet
parameter_list|,
specifier|final
name|Iterator
name|neededBounds
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|OpenBitSet
name|bits
init|=
name|useBitSet
condition|?
operator|new
name|OpenBitSet
argument_list|(
name|upper
operator|-
name|lower
operator|+
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
name|NumericUtils
operator|.
name|splitLongRange
argument_list|(
operator|new
name|NumericUtils
operator|.
name|LongRangeBuilder
argument_list|()
block|{
comment|//@Override
specifier|public
name|void
name|addRange
parameter_list|(
name|long
name|min
parameter_list|,
name|long
name|max
parameter_list|,
name|int
name|shift
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"min, max should be inside bounds"
argument_list|,
name|min
operator|>=
name|lower
operator|&&
name|min
operator|<=
name|upper
operator|&&
name|max
operator|>=
name|lower
operator|&&
name|max
operator|<=
name|upper
argument_list|)
expr_stmt|;
if|if
condition|(
name|useBitSet
condition|)
for|for
control|(
name|long
name|l
init|=
name|min
init|;
name|l
operator|<=
name|max
condition|;
name|l
operator|++
control|)
block|{
name|assertFalse
argument_list|(
literal|"ranges should not overlap"
argument_list|,
name|bits
operator|.
name|getAndSet
argument_list|(
name|l
operator|-
name|lower
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// make unsigned longs for easier display and understanding
name|min
operator|^=
literal|0x8000000000000000L
expr_stmt|;
name|max
operator|^=
literal|0x8000000000000000L
expr_stmt|;
comment|//System.out.println("Long.valueOf(0x"+Long.toHexString(min>>>shift)+"L),Long.valueOf(0x"+Long.toHexString(max>>>shift)+"L),");
name|assertEquals
argument_list|(
literal|"inner min bound"
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|neededBounds
operator|.
name|next
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|min
operator|>>>
name|shift
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"inner max bound"
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|neededBounds
operator|.
name|next
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|max
operator|>>>
name|shift
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|precisionStep
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
expr_stmt|;
if|if
condition|(
name|useBitSet
condition|)
block|{
comment|// after flipping all bits in the range, the cardinality should be zero
name|bits
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|upper
operator|-
name|lower
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The sub-range concenated should match the whole range"
argument_list|,
name|bits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSplitLongRange
specifier|public
name|void
name|testSplitLongRange
parameter_list|()
throws|throws
name|Exception
block|{
comment|// a hard-coded "standard" range
name|assertLongRangeSplit
argument_list|(
operator|-
literal|5000L
argument_list|,
literal|9500L
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffec78L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffec7fL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x8000000000002510L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000251cL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffec8L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffecfL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000250L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000250L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffedL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffefL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x80000000000020L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x80000000000024L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7ffffffffffffL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x8000000000001L
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with no range splitting
name|assertLongRangeSplit
argument_list|(
operator|-
literal|5000L
argument_list|,
literal|9500L
argument_list|,
literal|64
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x7fffffffffffec78L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000251cL
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// this tests optimized range splitting, if one of the inner bounds
comment|// is also the bound of the next lower precision, it should be used completely
name|assertLongRangeSplit
argument_list|(
literal|0L
argument_list|,
literal|1024L
operator|+
literal|63L
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000040L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000043L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x80000000000000L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x80000000000003L
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the full long range should only consist of a lowest precision range; no bitset testing here, as too much memory needed :-)
name|assertLongRangeSplit
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x00L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0xffL
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with precisionStep=4
name|assertLongRangeSplit
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x0L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0xfL
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with precisionStep=2
name|assertLongRangeSplit
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x0L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x3L
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with precisionStep=1
name|assertLongRangeSplit
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x0L
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x1L
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// a inverse range should produce no sub-ranges
name|assertLongRangeSplit
argument_list|(
literal|9500L
argument_list|,
operator|-
literal|5000L
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// a 0-length range should reproduce the range itsself
name|assertLongRangeSplit
argument_list|(
literal|9500L
argument_list|,
literal|9500L
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Long
index|[]
block|{
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000251cL
argument_list|)
block|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0x800000000000251cL
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Note: The neededBounds iterator must be unsigned (easier understanding what's happening) */
DECL|method|assertIntRangeSplit
specifier|protected
name|void
name|assertIntRangeSplit
parameter_list|(
specifier|final
name|int
name|lower
parameter_list|,
specifier|final
name|int
name|upper
parameter_list|,
name|int
name|precisionStep
parameter_list|,
specifier|final
name|boolean
name|useBitSet
parameter_list|,
specifier|final
name|Iterator
name|neededBounds
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|OpenBitSet
name|bits
init|=
name|useBitSet
condition|?
operator|new
name|OpenBitSet
argument_list|(
name|upper
operator|-
name|lower
operator|+
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
name|NumericUtils
operator|.
name|splitIntRange
argument_list|(
operator|new
name|NumericUtils
operator|.
name|IntRangeBuilder
argument_list|()
block|{
comment|//@Override
specifier|public
name|void
name|addRange
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|shift
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"min, max should be inside bounds"
argument_list|,
name|min
operator|>=
name|lower
operator|&&
name|min
operator|<=
name|upper
operator|&&
name|max
operator|>=
name|lower
operator|&&
name|max
operator|<=
name|upper
argument_list|)
expr_stmt|;
if|if
condition|(
name|useBitSet
condition|)
for|for
control|(
name|int
name|i
init|=
name|min
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
literal|"ranges should not overlap"
argument_list|,
name|bits
operator|.
name|getAndSet
argument_list|(
name|i
operator|-
name|lower
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// make unsigned ints for easier display and understanding
name|min
operator|^=
literal|0x80000000
expr_stmt|;
name|max
operator|^=
literal|0x80000000
expr_stmt|;
comment|//System.out.println("Integer.valueOf(0x"+Integer.toHexString(min>>>shift)+"),Integer.valueOf(0x"+Integer.toHexString(max>>>shift)+"),");
name|assertEquals
argument_list|(
literal|"inner min bound"
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|neededBounds
operator|.
name|next
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|min
operator|>>>
name|shift
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"inner max bound"
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|neededBounds
operator|.
name|next
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|max
operator|>>>
name|shift
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|precisionStep
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|)
expr_stmt|;
if|if
condition|(
name|useBitSet
condition|)
block|{
comment|// after flipping all bits in the range, the cardinality should be zero
name|bits
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|upper
operator|-
name|lower
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The sub-range concenated should match the whole range"
argument_list|,
name|bits
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSplitIntRange
specifier|public
name|void
name|testSplitIntRange
parameter_list|()
throws|throws
name|Exception
block|{
comment|// a hard-coded "standard" range
name|assertIntRangeSplit
argument_list|(
operator|-
literal|5000
argument_list|,
literal|9500
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffec78
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffec7f
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x80002510
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000251c
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffec8
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffecf
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000250
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000250
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffed
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffef
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x800020
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x800024
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7ffff
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x80001
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with no range splitting
name|assertIntRangeSplit
argument_list|(
operator|-
literal|5000
argument_list|,
literal|9500
argument_list|,
literal|32
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x7fffec78
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000251c
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// this tests optimized range splitting, if one of the inner bounds
comment|// is also the bound of the next lower precision, it should be used completely
name|assertIntRangeSplit
argument_list|(
literal|0
argument_list|,
literal|1024
operator|+
literal|63
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000040
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000043
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x800000
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x800003
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the full int range should only consist of a lowest precision range; no bitset testing here, as too much memory needed :-)
name|assertIntRangeSplit
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x00
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0xff
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with precisionStep=4
name|assertIntRangeSplit
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x0
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0xf
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with precisionStep=2
name|assertIntRangeSplit
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x0
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x3
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// the same with precisionStep=1
name|assertIntRangeSplit
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x0
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x1
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// a inverse range should produce no sub-ranges
name|assertIntRangeSplit
argument_list|(
literal|9500
argument_list|,
operator|-
literal|5000
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
comment|// a 0-length range should reproduce the range itsself
name|assertIntRangeSplit
argument_list|(
literal|9500
argument_list|,
literal|9500
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000251c
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0x8000251c
argument_list|)
block|}
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
