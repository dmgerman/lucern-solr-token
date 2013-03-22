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
name|Random
import|;
end_import
begin_class
DECL|class|TestArrayUtil
specifier|public
class|class
name|TestArrayUtil
extends|extends
name|LuceneTestCase
block|{
comment|// Ensure ArrayUtil.getNextSize gives linear amortized cost of realloc/copy
DECL|method|testGrowth
specifier|public
name|void
name|testGrowth
parameter_list|()
block|{
name|int
name|currentSize
init|=
literal|0
decl_stmt|;
name|long
name|copyCost
init|=
literal|0
decl_stmt|;
comment|// Make sure ArrayUtil hits Integer.MAX_VALUE, if we insist:
while|while
condition|(
name|currentSize
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|int
name|nextSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|currentSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nextSize
operator|>
name|currentSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentSize
operator|>
literal|0
condition|)
block|{
name|copyCost
operator|+=
name|currentSize
expr_stmt|;
name|double
name|copyCostPerElement
init|=
operator|(
operator|(
name|double
operator|)
name|copyCost
operator|)
operator|/
name|currentSize
decl_stmt|;
name|assertTrue
argument_list|(
literal|"cost "
operator|+
name|copyCostPerElement
argument_list|,
name|copyCostPerElement
operator|<
literal|10.0
argument_list|)
expr_stmt|;
block|}
name|currentSize
operator|=
name|nextSize
expr_stmt|;
block|}
block|}
DECL|method|testMaxSize
specifier|public
name|void
name|testMaxSize
parameter_list|()
block|{
comment|// intentionally pass invalid elemSizes:
for|for
control|(
name|int
name|elemSize
init|=
literal|0
init|;
name|elemSize
operator|<
literal|10
condition|;
name|elemSize
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|elemSize
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
name|elemSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidElementSizes
specifier|public
name|void
name|testInvalidElementSizes
parameter_list|()
block|{
specifier|final
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|minTargetSize
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|int
name|elemSize
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|11
argument_list|)
decl_stmt|;
specifier|final
name|int
name|v
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|minTargetSize
argument_list|,
name|elemSize
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|v
operator|>=
name|minTargetSize
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseInt
specifier|public
name|void
name|testParseInt
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|test
decl_stmt|;
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|""
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"foo"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"0.34"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|test
operator|==
literal|1
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"-10000"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
operator|-
literal|10000
argument_list|,
name|test
operator|==
operator|-
literal|10000
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"1923"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1923
argument_list|,
name|test
operator|==
literal|1923
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"-1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
operator|-
literal|1
argument_list|,
name|test
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"foo 1923 bar"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1923
argument_list|,
name|test
operator|==
literal|1923
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSliceEquals
specifier|public
name|void
name|testSliceEquals
parameter_list|()
block|{
name|String
name|left
init|=
literal|"this is equal"
decl_stmt|;
name|String
name|right
init|=
name|left
decl_stmt|;
name|char
index|[]
name|leftChars
init|=
name|left
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|rightChars
init|=
name|right
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|0
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|1
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|1
argument_list|,
name|rightChars
argument_list|,
literal|2
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|25
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|12
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createRandomArray
specifier|private
name|Integer
index|[]
name|createRandomArray
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
specifier|final
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|Integer
index|[]
name|a
init|=
operator|new
name|Integer
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
operator|+
literal|1
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|a
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
DECL|method|testQuickSort
specifier|public
name|void
name|testQuickSort
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|50
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
name|Integer
index|[]
name|a1
init|=
name|createRandomArray
argument_list|(
literal|2000
argument_list|)
decl_stmt|,
name|a2
init|=
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
name|a1
operator|=
name|createRandomArray
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|a2
operator|=
name|a1
operator|.
name|clone
argument_list|()
expr_stmt|;
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|a1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createSparseRandomArray
specifier|private
name|Integer
index|[]
name|createSparseRandomArray
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
specifier|final
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|Integer
index|[]
name|a
init|=
operator|new
name|Integer
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
operator|+
literal|1
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
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|a
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
comment|// This is a test for LUCENE-3054 (which fails without the merge sort fall back with stack overflow in most cases)
DECL|method|testQuickToMergeSortFallback
specifier|public
name|void
name|testQuickToMergeSortFallback
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|50
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
name|Integer
index|[]
name|a1
init|=
name|createSparseRandomArray
argument_list|(
literal|40000
argument_list|)
decl_stmt|,
name|a2
init|=
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMergeSort
specifier|public
name|void
name|testMergeSort
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|50
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
name|Integer
index|[]
name|a1
init|=
name|createRandomArray
argument_list|(
literal|2000
argument_list|)
decl_stmt|,
name|a2
init|=
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
name|a1
operator|=
name|createRandomArray
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|a2
operator|=
name|a1
operator|.
name|clone
argument_list|()
expr_stmt|;
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|a1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTimSort
specifier|public
name|void
name|testTimSort
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|65
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
name|Integer
index|[]
name|a1
init|=
name|createRandomArray
argument_list|(
literal|2000
argument_list|)
decl_stmt|,
name|a2
init|=
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
name|a1
operator|=
name|createRandomArray
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|a2
operator|=
name|a1
operator|.
name|clone
argument_list|()
expr_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|a1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInsertionSort
specifier|public
name|void
name|testInsertionSort
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|Integer
index|[]
name|a1
init|=
name|createRandomArray
argument_list|(
literal|30
argument_list|)
decl_stmt|,
name|a2
init|=
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|ArrayUtil
operator|.
name|insertionSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
name|a1
operator|=
name|createRandomArray
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|a2
operator|=
name|a1
operator|.
name|clone
argument_list|()
expr_stmt|;
name|ArrayUtil
operator|.
name|insertionSort
argument_list|(
name|a1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|ArrayUtil
operator|.
name|insertionSort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBinarySort
specifier|public
name|void
name|testBinarySort
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|Integer
index|[]
name|a1
init|=
name|createRandomArray
argument_list|(
literal|30
argument_list|)
decl_stmt|,
name|a2
init|=
name|a1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|ArrayUtil
operator|.
name|binarySort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
name|a1
operator|=
name|createRandomArray
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|a2
operator|=
name|a1
operator|.
name|clone
argument_list|()
expr_stmt|;
name|ArrayUtil
operator|.
name|binarySort
argument_list|(
name|a1
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
comment|// reverse back, so we can test that completely backwards sorted array (worst case) is working:
name|ArrayUtil
operator|.
name|binarySort
argument_list|(
name|a1
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|a2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|a2
argument_list|,
name|a1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Item
specifier|static
class|class
name|Item
implements|implements
name|Comparable
argument_list|<
name|Item
argument_list|>
block|{
DECL|field|val
DECL|field|order
specifier|final
name|int
name|val
decl_stmt|,
name|order
decl_stmt|;
DECL|method|Item
name|Item
parameter_list|(
name|int
name|val
parameter_list|,
name|int
name|order
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Item
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|order
operator|-
name|other
operator|.
name|order
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
DECL|method|testMergeSortStability
specifier|public
name|void
name|testMergeSortStability
parameter_list|()
block|{
specifier|final
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
name|Item
index|[]
name|items
init|=
operator|new
name|Item
index|[
literal|100
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// half of the items have value but same order. The value of this items is sorted,
comment|// so they should always be in order after sorting.
comment|// The other half has defined order, but no (-1) value (they should appear after
comment|// all above, when sorted).
specifier|final
name|boolean
name|equal
init|=
name|rnd
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|items
index|[
name|i
index|]
operator|=
operator|new
name|Item
argument_list|(
name|equal
condition|?
operator|(
name|i
operator|+
literal|1
operator|)
else|:
operator|-
literal|1
argument_list|,
name|equal
condition|?
literal|0
else|:
operator|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Before: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|items
argument_list|)
argument_list|)
expr_stmt|;
comment|// if you replace this with ArrayUtil.quickSort(), test should fail:
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|items
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sorted: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|items
argument_list|)
argument_list|)
expr_stmt|;
name|Item
name|last
init|=
name|items
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Item
name|act
init|=
name|items
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|act
operator|.
name|order
operator|==
literal|0
condition|)
block|{
comment|// order of "equal" items should be not mixed up
name|assertTrue
argument_list|(
name|act
operator|.
name|val
operator|>
name|last
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|act
operator|.
name|order
operator|>=
name|last
operator|.
name|order
argument_list|)
expr_stmt|;
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
DECL|method|testTimSortStability
specifier|public
name|void
name|testTimSortStability
parameter_list|()
block|{
specifier|final
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
name|Item
index|[]
name|items
init|=
operator|new
name|Item
index|[
literal|100
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// half of the items have value but same order. The value of this items is sorted,
comment|// so they should always be in order after sorting.
comment|// The other half has defined order, but no (-1) value (they should appear after
comment|// all above, when sorted).
specifier|final
name|boolean
name|equal
init|=
name|rnd
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|items
index|[
name|i
index|]
operator|=
operator|new
name|Item
argument_list|(
name|equal
condition|?
operator|(
name|i
operator|+
literal|1
operator|)
else|:
operator|-
literal|1
argument_list|,
name|equal
condition|?
literal|0
else|:
operator|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Before: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|items
argument_list|)
argument_list|)
expr_stmt|;
comment|// if you replace this with ArrayUtil.quickSort(), test should fail:
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|items
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sorted: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|items
argument_list|)
argument_list|)
expr_stmt|;
name|Item
name|last
init|=
name|items
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Item
name|act
init|=
name|items
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|act
operator|.
name|order
operator|==
literal|0
condition|)
block|{
comment|// order of "equal" items should be not mixed up
name|assertTrue
argument_list|(
name|act
operator|.
name|val
operator|>
name|last
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|act
operator|.
name|order
operator|>=
name|last
operator|.
name|order
argument_list|)
expr_stmt|;
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
comment|// should produce no exceptions
DECL|method|testEmptyArraySort
specifier|public
name|void
name|testEmptyArraySort
parameter_list|()
block|{
name|Integer
index|[]
name|a
init|=
operator|new
name|Integer
index|[
literal|0
index|]
decl_stmt|;
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|insertionSort
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|binarySort
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|quickSort
argument_list|(
name|a
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|mergeSort
argument_list|(
name|a
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|a
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|insertionSort
argument_list|(
name|a
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayUtil
operator|.
name|binarySort
argument_list|(
name|a
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
