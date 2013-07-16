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
begin_class
DECL|class|TestBroadWord
specifier|public
class|class
name|TestBroadWord
extends|extends
name|LuceneTestCase
block|{
DECL|method|tstRank
specifier|private
name|void
name|tstRank
parameter_list|(
name|long
name|x
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"rank9("
operator|+
name|x
operator|+
literal|")"
argument_list|,
name|Long
operator|.
name|bitCount
argument_list|(
name|x
argument_list|)
argument_list|,
name|BroadWord
operator|.
name|rank9
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRank1
specifier|public
name|void
name|testRank1
parameter_list|()
block|{
name|tstRank
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|tstRank
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|tstRank
argument_list|(
literal|3L
argument_list|)
expr_stmt|;
name|tstRank
argument_list|(
literal|0x100L
argument_list|)
expr_stmt|;
name|tstRank
argument_list|(
literal|0x300L
argument_list|)
expr_stmt|;
name|tstRank
argument_list|(
literal|0x8000000000000001L
argument_list|)
expr_stmt|;
block|}
DECL|method|tstSelect
specifier|private
name|void
name|tstSelect
parameter_list|(
name|long
name|x
parameter_list|,
name|int
name|r
parameter_list|,
name|int
name|exp
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"selectNaive("
operator|+
name|x
operator|+
literal|","
operator|+
name|r
operator|+
literal|")"
argument_list|,
name|exp
argument_list|,
name|BroadWord
operator|.
name|selectNaive
argument_list|(
name|x
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"select9("
operator|+
name|x
operator|+
literal|","
operator|+
name|r
operator|+
literal|")"
argument_list|,
name|exp
argument_list|,
name|BroadWord
operator|.
name|select9
argument_list|(
name|x
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSelectFromZero
specifier|public
name|void
name|testSelectFromZero
parameter_list|()
block|{
name|tstSelect
argument_list|(
literal|0L
argument_list|,
literal|1
argument_list|,
literal|72
argument_list|)
expr_stmt|;
block|}
DECL|method|testSelectSingleBit
specifier|public
name|void
name|testSelectSingleBit
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
literal|64
condition|;
name|i
operator|++
control|)
block|{
name|tstSelect
argument_list|(
operator|(
literal|1L
operator|<<
name|i
operator|)
argument_list|,
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSelectTwoBits
specifier|public
name|void
name|testSelectTwoBits
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
literal|64
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
name|i
operator|+
literal|1
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
name|x
init|=
operator|(
literal|1L
operator|<<
name|i
operator|)
operator||
operator|(
literal|1L
operator|<<
name|j
operator|)
decl_stmt|;
comment|//System.out.println(getName() + " i: " + i + " j: " + j);
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|2
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|3
argument_list|,
literal|72
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSelectThreeBits
specifier|public
name|void
name|testSelectThreeBits
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
literal|64
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
name|i
operator|+
literal|1
init|;
name|j
operator|<
literal|64
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|k
init|=
name|j
operator|+
literal|1
init|;
name|k
operator|<
literal|64
condition|;
name|k
operator|++
control|)
block|{
name|long
name|x
init|=
operator|(
literal|1L
operator|<<
name|i
operator|)
operator||
operator|(
literal|1L
operator|<<
name|j
operator|)
operator||
operator|(
literal|1L
operator|<<
name|k
operator|)
decl_stmt|;
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|2
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|3
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|tstSelect
argument_list|(
name|x
argument_list|,
literal|4
argument_list|,
literal|72
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testSelectAllBits
specifier|public
name|void
name|testSelectAllBits
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
literal|64
condition|;
name|i
operator|++
control|)
block|{
name|tstSelect
argument_list|(
literal|0xFFFFFFFFFFFFFFFFL
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPerfSelectAllBitsBroad
specifier|public
name|void
name|testPerfSelectAllBitsBroad
parameter_list|()
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
literal|100000
condition|;
name|j
operator|++
control|)
block|{
comment|// 1000000 for real perf test
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|64
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|BroadWord
operator|.
name|select9
argument_list|(
literal|0xFFFFFFFFFFFFFFFFL
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testPerfSelectAllBitsNaive
specifier|public
name|void
name|testPerfSelectAllBitsNaive
parameter_list|()
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
literal|10000
condition|;
name|j
operator|++
control|)
block|{
comment|// real perftest: 1000000
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|64
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|BroadWord
operator|.
name|selectNaive
argument_list|(
literal|0xFFFFFFFFFFFFFFFFL
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSmalleru_87_01
specifier|public
name|void
name|testSmalleru_87_01
parameter_list|()
block|{
comment|// 0<= arguments< 2 ** (k-1), k=8, see paper
for|for
control|(
name|long
name|i
init|=
literal|0x0L
init|;
name|i
operator|<=
literal|0x7FL
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|long
name|j
init|=
literal|0x0L
init|;
name|i
operator|<=
literal|0x7FL
condition|;
name|i
operator|++
control|)
block|{
name|long
name|ii
init|=
name|i
operator|*
name|BroadWord
operator|.
name|L8_L
decl_stmt|;
name|long
name|jj
init|=
name|j
operator|*
name|BroadWord
operator|.
name|L8_L
decl_stmt|;
name|assertEquals
argument_list|(
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|ii
argument_list|)
operator|+
literal|"< "
operator|+
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|jj
argument_list|)
argument_list|,
name|ToStringUtils
operator|.
name|longHex
argument_list|(
operator|(
name|i
operator|<
name|j
operator|)
condition|?
operator|(
literal|0x80L
operator|*
name|BroadWord
operator|.
name|L8_L
operator|)
else|:
literal|0x0L
argument_list|)
argument_list|,
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|BroadWord
operator|.
name|smallerUpTo7_8
argument_list|(
name|ii
argument_list|,
name|jj
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSmalleru_8_01
specifier|public
name|void
name|testSmalleru_8_01
parameter_list|()
block|{
comment|// 0<= arguments< 2 ** k, k=8, see paper
for|for
control|(
name|long
name|i
init|=
literal|0x0L
init|;
name|i
operator|<=
literal|0xFFL
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|long
name|j
init|=
literal|0x0L
init|;
name|i
operator|<=
literal|0xFFL
condition|;
name|i
operator|++
control|)
block|{
name|long
name|ii
init|=
name|i
operator|*
name|BroadWord
operator|.
name|L8_L
decl_stmt|;
name|long
name|jj
init|=
name|j
operator|*
name|BroadWord
operator|.
name|L8_L
decl_stmt|;
name|assertEquals
argument_list|(
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|ii
argument_list|)
operator|+
literal|"< "
operator|+
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|jj
argument_list|)
argument_list|,
name|ToStringUtils
operator|.
name|longHex
argument_list|(
operator|(
name|i
operator|<
name|j
operator|)
condition|?
operator|(
literal|0x80L
operator|*
name|BroadWord
operator|.
name|L8_L
operator|)
else|:
literal|0x0L
argument_list|)
argument_list|,
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|BroadWord
operator|.
name|smalleru_8
argument_list|(
name|ii
argument_list|,
name|jj
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testNotEquals0_8
specifier|public
name|void
name|testNotEquals0_8
parameter_list|()
block|{
comment|// 0<= arguments< 2 ** k, k=8, see paper
for|for
control|(
name|long
name|i
init|=
literal|0x0L
init|;
name|i
operator|<=
literal|0xFFL
condition|;
name|i
operator|++
control|)
block|{
name|long
name|ii
init|=
name|i
operator|*
name|BroadWord
operator|.
name|L8_L
decl_stmt|;
name|assertEquals
argument_list|(
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|ii
argument_list|)
operator|+
literal|"<> 0"
argument_list|,
name|ToStringUtils
operator|.
name|longHex
argument_list|(
operator|(
name|i
operator|!=
literal|0L
operator|)
condition|?
operator|(
literal|0x80L
operator|*
name|BroadWord
operator|.
name|L8_L
operator|)
else|:
literal|0x0L
argument_list|)
argument_list|,
name|ToStringUtils
operator|.
name|longHex
argument_list|(
name|BroadWord
operator|.
name|notEquals0_8
argument_list|(
name|ii
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
