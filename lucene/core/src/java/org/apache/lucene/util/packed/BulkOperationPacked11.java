begin_unit
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment
begin_class
DECL|class|BulkOperationPacked11
specifier|final
class|class
name|BulkOperationPacked11
extends|extends
name|BulkOperationPacked
block|{
DECL|method|BulkOperationPacked11
specifier|public
name|BulkOperationPacked11
parameter_list|()
block|{
name|super
argument_list|(
literal|11
argument_list|)
expr_stmt|;
assert|assert
name|blockCount
argument_list|()
operator|==
literal|11
assert|;
assert|assert
name|valueCount
argument_list|()
operator|==
literal|64
assert|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|block0
operator|>>>
literal|53
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block0
operator|>>>
literal|42
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block0
operator|>>>
literal|31
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block0
operator|>>>
literal|20
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block0
operator|>>>
literal|9
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block0
operator|&
literal|511L
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|block1
operator|>>>
literal|62
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block1
operator|>>>
literal|51
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block1
operator|>>>
literal|40
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block1
operator|>>>
literal|29
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block1
operator|>>>
literal|18
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block1
operator|>>>
literal|7
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block1
operator|&
literal|127L
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|block2
operator|>>>
literal|60
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block2
operator|>>>
literal|49
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block2
operator|>>>
literal|38
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block2
operator|>>>
literal|27
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block2
operator|>>>
literal|16
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block2
operator|>>>
literal|5
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block2
operator|&
literal|31L
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|block3
operator|>>>
literal|58
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block3
operator|>>>
literal|47
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block3
operator|>>>
literal|36
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block3
operator|>>>
literal|25
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block3
operator|>>>
literal|14
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block3
operator|>>>
literal|3
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block3
operator|&
literal|7L
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block4
operator|>>>
literal|56
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block4
operator|>>>
literal|45
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block4
operator|>>>
literal|34
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block4
operator|>>>
literal|23
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block4
operator|>>>
literal|12
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block4
operator|>>>
literal|1
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block4
operator|&
literal|1L
operator|)
operator|<<
literal|10
operator|)
operator||
operator|(
name|block5
operator|>>>
literal|54
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block5
operator|>>>
literal|43
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block5
operator|>>>
literal|32
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block5
operator|>>>
literal|21
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block5
operator|>>>
literal|10
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block5
operator|&
literal|1023L
operator|)
operator|<<
literal|1
operator|)
operator||
operator|(
name|block6
operator|>>>
literal|63
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block6
operator|>>>
literal|52
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block6
operator|>>>
literal|41
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block6
operator|>>>
literal|30
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block6
operator|>>>
literal|19
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block6
operator|>>>
literal|8
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block6
operator|&
literal|255L
operator|)
operator|<<
literal|3
operator|)
operator||
operator|(
name|block7
operator|>>>
literal|61
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block7
operator|>>>
literal|50
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block7
operator|>>>
literal|39
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block7
operator|>>>
literal|28
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block7
operator|>>>
literal|17
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block7
operator|>>>
literal|6
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block8
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block7
operator|&
literal|63L
operator|)
operator|<<
literal|5
operator|)
operator||
operator|(
name|block8
operator|>>>
literal|59
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block8
operator|>>>
literal|48
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block8
operator|>>>
literal|37
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block8
operator|>>>
literal|26
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block8
operator|>>>
literal|15
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block8
operator|>>>
literal|4
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block9
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block8
operator|&
literal|15L
operator|)
operator|<<
literal|7
operator|)
operator||
operator|(
name|block9
operator|>>>
literal|57
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block9
operator|>>>
literal|46
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block9
operator|>>>
literal|35
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block9
operator|>>>
literal|24
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block9
operator|>>>
literal|13
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block9
operator|>>>
literal|2
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
specifier|final
name|long
name|block10
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|block9
operator|&
literal|3L
operator|)
operator|<<
literal|9
operator|)
operator||
operator|(
name|block10
operator|>>>
literal|55
operator|)
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block10
operator|>>>
literal|44
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block10
operator|>>>
literal|33
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block10
operator|>>>
literal|22
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block10
operator|>>>
literal|11
operator|)
operator|&
literal|2047L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|block10
operator|&
literal|2047L
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
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
literal|8
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|<<
literal|3
operator|)
operator||
operator|(
name|byte1
operator|>>>
literal|5
operator|)
expr_stmt|;
specifier|final
name|int
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte1
operator|&
literal|31
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|byte2
operator|>>>
literal|2
operator|)
expr_stmt|;
specifier|final
name|int
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte2
operator|&
literal|3
operator|)
operator|<<
literal|9
operator|)
operator||
operator|(
name|byte3
operator|<<
literal|1
operator|)
operator||
operator|(
name|byte4
operator|>>>
literal|7
operator|)
expr_stmt|;
specifier|final
name|int
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte4
operator|&
literal|127
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte5
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|int
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte5
operator|&
literal|15
operator|)
operator|<<
literal|7
operator|)
operator||
operator|(
name|byte6
operator|>>>
literal|1
operator|)
expr_stmt|;
specifier|final
name|int
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte8
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte6
operator|&
literal|1
operator|)
operator|<<
literal|10
operator|)
operator||
operator|(
name|byte7
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte8
operator|>>>
literal|6
operator|)
expr_stmt|;
specifier|final
name|int
name|byte9
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte8
operator|&
literal|63
operator|)
operator|<<
literal|5
operator|)
operator||
operator|(
name|byte9
operator|>>>
literal|3
operator|)
expr_stmt|;
specifier|final
name|int
name|byte10
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte9
operator|&
literal|7
operator|)
operator|<<
literal|8
operator|)
operator||
name|byte10
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
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
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block0
operator|>>>
literal|53
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|42
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|31
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|20
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block0
operator|>>>
literal|9
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block0
operator|&
literal|511L
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|block1
operator|>>>
literal|62
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|51
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|40
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|29
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|18
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block1
operator|>>>
literal|7
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block1
operator|&
literal|127L
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|block2
operator|>>>
literal|60
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|49
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|38
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|27
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|16
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block2
operator|>>>
literal|5
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block2
operator|&
literal|31L
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|block3
operator|>>>
literal|58
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|47
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|36
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|25
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|14
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block3
operator|>>>
literal|3
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block3
operator|&
literal|7L
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block4
operator|>>>
literal|56
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|45
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|34
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|23
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|12
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block4
operator|>>>
literal|1
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block4
operator|&
literal|1L
operator|)
operator|<<
literal|10
operator|)
operator||
operator|(
name|block5
operator|>>>
literal|54
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block5
operator|>>>
literal|43
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block5
operator|>>>
literal|32
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block5
operator|>>>
literal|21
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block5
operator|>>>
literal|10
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block5
operator|&
literal|1023L
operator|)
operator|<<
literal|1
operator|)
operator||
operator|(
name|block6
operator|>>>
literal|63
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block6
operator|>>>
literal|52
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block6
operator|>>>
literal|41
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block6
operator|>>>
literal|30
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block6
operator|>>>
literal|19
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block6
operator|>>>
literal|8
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block6
operator|&
literal|255L
operator|)
operator|<<
literal|3
operator|)
operator||
operator|(
name|block7
operator|>>>
literal|61
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block7
operator|>>>
literal|50
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block7
operator|>>>
literal|39
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block7
operator|>>>
literal|28
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block7
operator|>>>
literal|17
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block7
operator|>>>
literal|6
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block8
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block7
operator|&
literal|63L
operator|)
operator|<<
literal|5
operator|)
operator||
operator|(
name|block8
operator|>>>
literal|59
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block8
operator|>>>
literal|48
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block8
operator|>>>
literal|37
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block8
operator|>>>
literal|26
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block8
operator|>>>
literal|15
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block8
operator|>>>
literal|4
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block9
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block8
operator|&
literal|15L
operator|)
operator|<<
literal|7
operator|)
operator||
operator|(
name|block9
operator|>>>
literal|57
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block9
operator|>>>
literal|46
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block9
operator|>>>
literal|35
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block9
operator|>>>
literal|24
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block9
operator|>>>
literal|13
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block9
operator|>>>
literal|2
operator|)
operator|&
literal|2047L
expr_stmt|;
specifier|final
name|long
name|block10
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block9
operator|&
literal|3L
operator|)
operator|<<
literal|9
operator|)
operator||
operator|(
name|block10
operator|>>>
literal|55
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block10
operator|>>>
literal|44
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block10
operator|>>>
literal|33
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block10
operator|>>>
literal|22
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block10
operator|>>>
literal|11
operator|)
operator|&
literal|2047L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block10
operator|&
literal|2047L
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
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
literal|8
operator|*
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|<<
literal|3
operator|)
operator||
operator|(
name|byte1
operator|>>>
literal|5
operator|)
expr_stmt|;
specifier|final
name|long
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte1
operator|&
literal|31
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|byte2
operator|>>>
literal|2
operator|)
expr_stmt|;
specifier|final
name|long
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte2
operator|&
literal|3
operator|)
operator|<<
literal|9
operator|)
operator||
operator|(
name|byte3
operator|<<
literal|1
operator|)
operator||
operator|(
name|byte4
operator|>>>
literal|7
operator|)
expr_stmt|;
specifier|final
name|long
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte4
operator|&
literal|127
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte5
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte5
operator|&
literal|15
operator|)
operator|<<
literal|7
operator|)
operator||
operator|(
name|byte6
operator|>>>
literal|1
operator|)
expr_stmt|;
specifier|final
name|long
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte8
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte6
operator|&
literal|1
operator|)
operator|<<
literal|10
operator|)
operator||
operator|(
name|byte7
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte8
operator|>>>
literal|6
operator|)
expr_stmt|;
specifier|final
name|long
name|byte9
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte8
operator|&
literal|63
operator|)
operator|<<
literal|5
operator|)
operator||
operator|(
name|byte9
operator|>>>
literal|3
operator|)
expr_stmt|;
specifier|final
name|long
name|byte10
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|byte9
operator|&
literal|7
operator|)
operator|<<
literal|8
operator|)
operator||
name|byte10
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
