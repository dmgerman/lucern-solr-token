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
DECL|class|BulkOperationPacked6
specifier|final
class|class
name|BulkOperationPacked6
extends|extends
name|BulkOperationPacked
block|{
DECL|method|BulkOperationPacked6
specifier|public
name|BulkOperationPacked6
parameter_list|()
block|{
name|super
argument_list|(
literal|6
argument_list|)
expr_stmt|;
assert|assert
name|blockCount
argument_list|()
operator|==
literal|3
assert|;
assert|assert
name|valueCount
argument_list|()
operator|==
literal|32
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
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
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
literal|58
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
literal|52
operator|)
operator|&
literal|63L
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
literal|46
operator|)
operator|&
literal|63L
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
literal|40
operator|)
operator|&
literal|63L
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
literal|34
operator|)
operator|&
literal|63L
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
literal|28
operator|)
operator|&
literal|63L
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
literal|22
operator|)
operator|&
literal|63L
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
literal|16
operator|)
operator|&
literal|63L
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
literal|10
operator|)
operator|&
literal|63L
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
literal|4
operator|)
operator|&
literal|63L
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
literal|15L
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
literal|56
operator|)
operator|&
literal|63L
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
literal|50
operator|)
operator|&
literal|63L
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
literal|44
operator|)
operator|&
literal|63L
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
literal|38
operator|)
operator|&
literal|63L
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
literal|32
operator|)
operator|&
literal|63L
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
literal|26
operator|)
operator|&
literal|63L
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
literal|20
operator|)
operator|&
literal|63L
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
literal|14
operator|)
operator|&
literal|63L
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
literal|8
operator|)
operator|&
literal|63L
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
literal|2
operator|)
operator|&
literal|63L
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
literal|3L
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
literal|54
operator|)
operator|&
literal|63L
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
literal|48
operator|)
operator|&
literal|63L
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
literal|42
operator|)
operator|&
literal|63L
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
literal|36
operator|)
operator|&
literal|63L
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
literal|30
operator|)
operator|&
literal|63L
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
literal|24
operator|)
operator|&
literal|63L
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
literal|18
operator|)
operator|&
literal|63L
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
literal|12
operator|)
operator|&
literal|63L
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
literal|6
operator|)
operator|&
literal|63L
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
name|block2
operator|&
literal|63L
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
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator|>>>
literal|2
expr_stmt|;
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
operator|(
name|byte0
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte1
operator|>>>
literal|4
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
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte2
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|&
literal|63
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|>>>
literal|2
expr_stmt|;
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
name|byte3
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte4
operator|>>>
literal|4
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
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte5
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|&
literal|63
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
name|byte6
operator|>>>
literal|2
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
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte7
operator|>>>
literal|4
operator|)
expr_stmt|;
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
name|byte7
operator|&
literal|15
operator|)
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte8
operator|&
literal|63
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
name|byte9
operator|>>>
literal|2
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
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte10
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|int
name|byte11
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
name|byte10
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte11
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte11
operator|&
literal|63
expr_stmt|;
specifier|final
name|int
name|byte12
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
name|byte12
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|int
name|byte13
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
name|byte12
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte13
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|int
name|byte14
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
name|byte13
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte14
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte14
operator|&
literal|63
expr_stmt|;
specifier|final
name|int
name|byte15
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
name|byte15
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|int
name|byte16
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
name|byte15
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte16
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|int
name|byte17
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
name|byte16
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte17
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte17
operator|&
literal|63
expr_stmt|;
specifier|final
name|int
name|byte18
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
name|byte18
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|int
name|byte19
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
name|byte18
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte19
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|int
name|byte20
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
name|byte19
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte20
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte20
operator|&
literal|63
expr_stmt|;
specifier|final
name|int
name|byte21
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
name|byte21
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|int
name|byte22
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
name|byte21
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte22
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|int
name|byte23
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
name|byte22
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte23
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte23
operator|&
literal|63
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
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
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
literal|58
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
literal|52
operator|)
operator|&
literal|63L
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
literal|46
operator|)
operator|&
literal|63L
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
literal|40
operator|)
operator|&
literal|63L
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
literal|34
operator|)
operator|&
literal|63L
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
literal|28
operator|)
operator|&
literal|63L
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
literal|22
operator|)
operator|&
literal|63L
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
literal|16
operator|)
operator|&
literal|63L
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
literal|10
operator|)
operator|&
literal|63L
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
literal|4
operator|)
operator|&
literal|63L
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
literal|15L
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
literal|56
operator|)
operator|&
literal|63L
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
literal|50
operator|)
operator|&
literal|63L
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
literal|44
operator|)
operator|&
literal|63L
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
literal|38
operator|)
operator|&
literal|63L
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
literal|32
operator|)
operator|&
literal|63L
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
literal|26
operator|)
operator|&
literal|63L
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
literal|20
operator|)
operator|&
literal|63L
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
literal|14
operator|)
operator|&
literal|63L
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
literal|8
operator|)
operator|&
literal|63L
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
literal|2
operator|)
operator|&
literal|63L
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
literal|3L
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
literal|54
operator|)
operator|&
literal|63L
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
literal|48
operator|)
operator|&
literal|63L
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
literal|42
operator|)
operator|&
literal|63L
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
literal|36
operator|)
operator|&
literal|63L
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
literal|30
operator|)
operator|&
literal|63L
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
literal|24
operator|)
operator|&
literal|63L
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
literal|18
operator|)
operator|&
literal|63L
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
literal|12
operator|)
operator|&
literal|63L
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
literal|6
operator|)
operator|&
literal|63L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block2
operator|&
literal|63L
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
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator|>>>
literal|2
expr_stmt|;
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
operator|(
name|byte0
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte1
operator|>>>
literal|4
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
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte2
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte2
operator|&
literal|63
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte3
operator|>>>
literal|2
expr_stmt|;
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
name|byte3
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte4
operator|>>>
literal|4
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
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte5
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte5
operator|&
literal|63
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
name|byte6
operator|>>>
literal|2
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
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte7
operator|>>>
literal|4
operator|)
expr_stmt|;
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
name|byte7
operator|&
literal|15
operator|)
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
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte8
operator|&
literal|63
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
name|byte9
operator|>>>
literal|2
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
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte10
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
name|byte11
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
name|byte10
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte11
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte11
operator|&
literal|63
expr_stmt|;
specifier|final
name|long
name|byte12
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
name|byte12
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|long
name|byte13
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
name|byte12
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte13
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
name|byte14
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
name|byte13
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte14
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte14
operator|&
literal|63
expr_stmt|;
specifier|final
name|long
name|byte15
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
name|byte15
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|long
name|byte16
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
name|byte15
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte16
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
name|byte17
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
name|byte16
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte17
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte17
operator|&
literal|63
expr_stmt|;
specifier|final
name|long
name|byte18
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
name|byte18
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|long
name|byte19
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
name|byte18
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte19
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
name|byte20
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
name|byte19
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte20
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte20
operator|&
literal|63
expr_stmt|;
specifier|final
name|long
name|byte21
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
name|byte21
operator|>>>
literal|2
expr_stmt|;
specifier|final
name|long
name|byte22
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
name|byte21
operator|&
literal|3
operator|)
operator|<<
literal|4
operator|)
operator||
operator|(
name|byte22
operator|>>>
literal|4
operator|)
expr_stmt|;
specifier|final
name|long
name|byte23
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
name|byte22
operator|&
literal|15
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
name|byte23
operator|>>>
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte23
operator|&
literal|63
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
