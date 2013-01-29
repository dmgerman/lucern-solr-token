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
DECL|class|BulkOperationPacked2
specifier|final
class|class
name|BulkOperationPacked2
extends|extends
name|BulkOperationPacked
block|{
DECL|method|BulkOperationPacked2
specifier|public
name|BulkOperationPacked2
parameter_list|()
block|{
name|super
argument_list|(
literal|2
argument_list|)
expr_stmt|;
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
name|block
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|62
init|;
name|shift
operator|>=
literal|0
condition|;
name|shift
operator|-=
literal|2
control|)
block|{
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
name|block
operator|>>>
name|shift
operator|)
operator|&
literal|3
argument_list|)
expr_stmt|;
block|}
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
name|j
init|=
literal|0
init|;
name|j
operator|<
name|iterations
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|byte
name|block
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
name|block
operator|>>>
literal|6
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|2
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|&
literal|3
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
name|block
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|62
init|;
name|shift
operator|>=
literal|0
condition|;
name|shift
operator|-=
literal|2
control|)
block|{
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
name|shift
operator|)
operator|&
literal|3
expr_stmt|;
block|}
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
name|j
init|=
literal|0
init|;
name|j
operator|<
name|iterations
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|byte
name|block
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
name|block
operator|>>>
literal|6
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|4
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|2
operator|)
operator|&
literal|3
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|&
literal|3
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
