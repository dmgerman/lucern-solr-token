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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_class
DECL|class|TestLongBitSet
specifier|public
class|class
name|TestLongBitSet
extends|extends
name|LuceneTestCase
block|{
DECL|method|doGet
name|void
name|doGet
parameter_list|(
name|java
operator|.
name|util
operator|.
name|BitSet
name|a
parameter_list|,
name|LongBitSet
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|max
init|=
name|b
operator|.
name|length
argument_list|()
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
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"mismatch: BitSet=["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doNextSetBit
name|void
name|doNextSetBit
parameter_list|(
name|java
operator|.
name|util
operator|.
name|BitSet
name|a
parameter_list|,
name|LongBitSet
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|bb
init|=
operator|-
literal|1
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bb
operator|=
name|bb
operator|<
name|b
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|?
name|b
operator|.
name|nextSetBit
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
else|:
operator|-
literal|1
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doPrevSetBit
name|void
name|doPrevSetBit
parameter_list|(
name|java
operator|.
name|util
operator|.
name|BitSet
name|a
parameter_list|,
name|LongBitSet
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|aa
init|=
name|a
operator|.
name|size
argument_list|()
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|long
name|bb
init|=
name|aa
decl_stmt|;
do|do
block|{
comment|// aa = a.prevSetBit(aa-1);
name|aa
operator|--
expr_stmt|;
while|while
condition|(
operator|(
name|aa
operator|>=
literal|0
operator|)
operator|&&
operator|(
operator|!
name|a
operator|.
name|get
argument_list|(
name|aa
argument_list|)
operator|)
condition|)
block|{
name|aa
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|bb
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bb
operator|>
name|b
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|bb
operator|=
name|b
operator|.
name|prevSetBit
argument_list|(
name|b
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bb
operator|<
literal|1
condition|)
block|{
name|bb
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|bb
operator|=
name|bb
operator|>=
literal|1
condition|?
name|b
operator|.
name|prevSetBit
argument_list|(
name|bb
operator|-
literal|1
argument_list|)
else|:
operator|-
literal|1
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doRandomSets
name|void
name|doRandomSets
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|int
name|iter
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|java
operator|.
name|util
operator|.
name|BitSet
name|a0
init|=
literal|null
decl_stmt|;
name|LongBitSet
name|b0
init|=
literal|null
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|sz
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|a
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|BitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|LongBitSet
name|b
init|=
operator|new
name|LongBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
comment|// test the various ways of setting bits
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
name|int
name|nOper
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nOper
condition|;
name|j
operator|++
control|)
block|{
name|int
name|idx
decl_stmt|;
name|idx
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|clear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|clear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|flip
argument_list|(
name|idx
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
name|b
operator|.
name|flip
argument_list|(
name|idx
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
name|idx
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|flip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|flip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|boolean
name|val2
init|=
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|boolean
name|val
init|=
name|b
operator|.
name|getAndSet
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|val2
operator|==
name|val
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|val
condition|)
name|b
operator|.
name|clear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|==
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test that the various ways of accessing the bits are equivalent
name|doGet
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
comment|// test ranges, including possible extension
name|int
name|fromIndex
decl_stmt|,
name|toIndex
decl_stmt|;
name|fromIndex
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
operator|/
literal|2
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
operator|-
name|fromIndex
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|aa
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|aa
operator|.
name|flip
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|LongBitSet
name|bb
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|bb
operator|.
name|flip
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|fromIndex
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
operator|/
literal|2
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
operator|-
name|fromIndex
argument_list|)
expr_stmt|;
name|aa
operator|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
expr_stmt|;
name|aa
operator|.
name|clear
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|bb
operator|=
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|bb
operator|.
name|clear
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doNextSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from clear() or nextSetBit
name|doPrevSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
name|fromIndex
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
operator|/
literal|2
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sz
operator|-
name|fromIndex
argument_list|)
expr_stmt|;
name|aa
operator|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
expr_stmt|;
name|aa
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|bb
operator|=
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|bb
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doNextSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from set() or nextSetBit
name|doPrevSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
if|if
condition|(
name|b0
operator|!=
literal|null
operator|&&
name|b0
operator|.
name|length
argument_list|()
operator|<=
name|b
operator|.
name|length
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|a_and
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_and
operator|.
name|and
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|a_or
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_or
operator|.
name|or
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|a_xor
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_xor
operator|.
name|xor
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|a_andn
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_andn
operator|.
name|andNot
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|LongBitSet
name|b_and
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|b_and
argument_list|)
expr_stmt|;
name|b_and
operator|.
name|and
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|LongBitSet
name|b_or
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_or
operator|.
name|or
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|LongBitSet
name|b_xor
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_xor
operator|.
name|xor
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|LongBitSet
name|b_andn
init|=
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_andn
operator|.
name|andNot
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a0
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b0
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_or
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_and
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_or
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_xor
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_xor
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_andn
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|a0
operator|=
name|a
expr_stmt|;
name|b0
operator|=
name|b
expr_stmt|;
block|}
block|}
comment|// large enough to flush obvious bugs, small enough to run in<.5 sec as part of a
comment|// larger testsuite.
DECL|method|testSmall
specifier|public
name|void
name|testSmall
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|TEST_NIGHTLY
condition|?
name|atLeast
argument_list|(
literal|1000
argument_list|)
else|:
literal|100
decl_stmt|;
name|doRandomSets
argument_list|(
name|atLeast
argument_list|(
literal|1200
argument_list|)
argument_list|,
name|iters
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doRandomSets
argument_list|(
name|atLeast
argument_list|(
literal|1200
argument_list|)
argument_list|,
name|iters
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// uncomment to run a bigger test (~2 minutes).
comment|/*   public void testBig() {     doRandomSets(2000,200000, 1);     doRandomSets(2000,200000, 2);   }   */
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
comment|// This test can't handle numBits==0:
specifier|final
name|int
name|numBits
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2000
argument_list|)
operator|+
literal|1
decl_stmt|;
name|LongBitSet
name|b1
init|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|LongBitSet
name|b2
init|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|idx
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|b1
operator|.
name|get
argument_list|(
name|idx
argument_list|)
condition|)
block|{
name|b1
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b2
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// try different type of object
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testHashCodeEquals
specifier|public
name|void
name|testHashCodeEquals
parameter_list|()
block|{
comment|// This test can't handle numBits==0:
specifier|final
name|int
name|numBits
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2000
argument_list|)
operator|+
literal|1
decl_stmt|;
name|LongBitSet
name|b1
init|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|LongBitSet
name|b2
init|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
operator|*
name|RANDOM_MULTIPLIER
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|idx
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|b1
operator|.
name|get
argument_list|(
name|idx
argument_list|)
condition|)
block|{
name|b1
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|hashCode
argument_list|()
operator|==
name|b2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|b2
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|b2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSmallBitSets
specifier|public
name|void
name|testSmallBitSets
parameter_list|()
block|{
comment|// Make sure size 0-10 bit sets are OK:
for|for
control|(
name|int
name|numBits
init|=
literal|0
init|;
name|numBits
operator|<
literal|10
condition|;
name|numBits
operator|++
control|)
block|{
name|LongBitSet
name|b1
init|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|LongBitSet
name|b2
init|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|b2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b1
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|numBits
operator|>
literal|0
condition|)
block|{
name|b1
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|b1
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|b1
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b1
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|makeLongBitSet
specifier|private
name|LongBitSet
name|makeLongBitSet
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
name|LongBitSet
name|bs
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|int
name|bits2words
init|=
name|LongBitSet
operator|.
name|bits2words
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|long
index|[]
name|words
init|=
operator|new
name|long
index|[
name|bits2words
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
index|]
decl_stmt|;
name|bs
operator|=
operator|new
name|LongBitSet
argument_list|(
name|words
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bs
operator|=
operator|new
name|LongBitSet
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|e
range|:
name|a
control|)
block|{
name|bs
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|makeBitSet
specifier|private
name|java
operator|.
name|util
operator|.
name|BitSet
name|makeBitSet
parameter_list|(
name|int
index|[]
name|a
parameter_list|)
block|{
name|java
operator|.
name|util
operator|.
name|BitSet
name|bs
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|e
range|:
name|a
control|)
block|{
name|bs
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|checkPrevSetBitArray
specifier|private
name|void
name|checkPrevSetBitArray
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
name|LongBitSet
name|obs
init|=
name|makeLongBitSet
argument_list|(
name|a
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|bs
init|=
name|makeBitSet
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|doPrevSetBit
argument_list|(
name|bs
argument_list|,
name|obs
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrevSetBit
specifier|public
name|void
name|testPrevSetBit
parameter_list|()
block|{
name|checkPrevSetBitArray
argument_list|(
operator|new
name|int
index|[]
block|{}
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkPrevSetBitArray
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkPrevSetBitArray
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNextSetBitArray
specifier|private
name|void
name|checkNextSetBitArray
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
name|LongBitSet
name|obs
init|=
name|makeLongBitSet
argument_list|(
name|a
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|BitSet
name|bs
init|=
name|makeBitSet
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|doNextSetBit
argument_list|(
name|bs
argument_list|,
name|obs
argument_list|)
expr_stmt|;
block|}
DECL|method|testNextBitSet
specifier|public
name|void
name|testNextBitSet
parameter_list|()
block|{
name|int
index|[]
name|setBits
init|=
operator|new
name|int
index|[
literal|0
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
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
name|setBits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|setBits
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|setBits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|checkNextSetBitArray
argument_list|(
name|setBits
argument_list|,
name|setBits
operator|.
name|length
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|checkNextSetBitArray
argument_list|(
operator|new
name|int
index|[
literal|0
index|]
argument_list|,
name|setBits
operator|.
name|length
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnsureCapacity
specifier|public
name|void
name|testEnsureCapacity
parameter_list|()
block|{
name|LongBitSet
name|bits
init|=
operator|new
name|LongBitSet
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|LongBitSet
name|newBits
init|=
name|LongBitSet
operator|.
name|ensureCapacity
argument_list|(
name|bits
argument_list|,
literal|8
argument_list|)
decl_stmt|;
comment|// grow within the word
name|assertTrue
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|newBits
operator|.
name|clear
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// we align to 64-bits, so even though it shouldn't have, it re-allocated a long[1]
name|assertTrue
argument_list|(
name|bits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|newBits
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|newBits
operator|=
name|LongBitSet
operator|.
name|ensureCapacity
argument_list|(
name|newBits
argument_list|,
name|newBits
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// reuse
name|assertTrue
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|newBits
operator|=
name|LongBitSet
operator|.
name|ensureCapacity
argument_list|(
name|bits
argument_list|,
literal|72
argument_list|)
expr_stmt|;
comment|// grow beyond one word
name|assertTrue
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|newBits
operator|.
name|clear
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// we grew the long[], so it's not shared
name|assertTrue
argument_list|(
name|bits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newBits
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nightly
DECL|method|testHugeCapacity
specifier|public
name|void
name|testHugeCapacity
parameter_list|()
block|{
name|long
name|moreThanMaxInt
init|=
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|5
decl_stmt|;
name|LongBitSet
name|bits
init|=
operator|new
name|LongBitSet
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|bits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|LongBitSet
name|hugeBits
init|=
name|LongBitSet
operator|.
name|ensureCapacity
argument_list|(
name|bits
argument_list|,
name|moreThanMaxInt
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hugeBits
operator|.
name|length
argument_list|()
operator|>=
name|moreThanMaxInt
argument_list|)
expr_stmt|;
block|}
DECL|method|testBits2Words
specifier|public
name|void
name|testBits2Words
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// ...
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|64
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|65
argument_list|)
argument_list|)
expr_stmt|;
comment|// ...
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|128
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|129
argument_list|)
argument_list|)
expr_stmt|;
comment|// ...
name|assertEquals
argument_list|(
literal|1
operator|<<
operator|(
literal|31
operator|-
literal|6
operator|)
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// == 1L<< 31
name|assertEquals
argument_list|(
operator|(
literal|1
operator|<<
operator|(
literal|31
operator|-
literal|6
operator|)
operator|)
operator|+
literal|1
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// == (1L<< 31) + 1
comment|// ...
name|assertEquals
argument_list|(
literal|1
operator|<<
operator|(
literal|32
operator|-
literal|6
operator|)
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
literal|1L
operator|<<
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|1
operator|<<
operator|(
literal|32
operator|-
literal|6
operator|)
operator|)
operator|+
literal|1
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
operator|(
literal|1L
operator|<<
literal|32
operator|)
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// ...
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|LongBitSet
operator|.
name|bits2words
argument_list|(
operator|(
literal|1L
operator|<<
literal|37
operator|)
operator|-
literal|64
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
