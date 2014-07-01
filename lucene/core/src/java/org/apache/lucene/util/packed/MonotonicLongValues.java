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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
operator|.
name|MonotonicBlockPackedReader
operator|.
name|expected
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
name|RamUsageEstimator
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
name|packed
operator|.
name|DeltaPackedLongValues
operator|.
name|Builder
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
name|packed
operator|.
name|PackedInts
operator|.
name|Reader
import|;
end_import
begin_class
DECL|class|MonotonicLongValues
class|class
name|MonotonicLongValues
extends|extends
name|DeltaPackedLongValues
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|MonotonicLongValues
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|averages
specifier|final
name|float
index|[]
name|averages
decl_stmt|;
DECL|method|MonotonicLongValues
name|MonotonicLongValues
parameter_list|(
name|int
name|pageShift
parameter_list|,
name|int
name|pageMask
parameter_list|,
name|Reader
index|[]
name|values
parameter_list|,
name|long
index|[]
name|mins
parameter_list|,
name|float
index|[]
name|averages
parameter_list|,
name|long
name|size
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|)
block|{
name|super
argument_list|(
name|pageShift
argument_list|,
name|pageMask
argument_list|,
name|values
argument_list|,
name|mins
argument_list|,
name|size
argument_list|,
name|ramBytesUsed
argument_list|)
expr_stmt|;
assert|assert
name|values
operator|.
name|length
operator|==
name|averages
operator|.
name|length
assert|;
name|this
operator|.
name|averages
operator|=
name|averages
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
name|long
name|get
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|element
parameter_list|)
block|{
return|return
name|expected
argument_list|(
name|mins
index|[
name|block
index|]
argument_list|,
name|averages
index|[
name|block
index|]
argument_list|,
name|element
argument_list|)
operator|+
name|values
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|element
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|decodeBlock
name|int
name|decodeBlock
parameter_list|(
name|int
name|block
parameter_list|,
name|long
index|[]
name|dest
parameter_list|)
block|{
specifier|final
name|int
name|count
init|=
name|super
operator|.
name|decodeBlock
argument_list|(
name|block
argument_list|,
name|dest
argument_list|)
decl_stmt|;
specifier|final
name|float
name|average
init|=
name|averages
index|[
name|block
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|dest
index|[
name|i
index|]
operator|+=
name|expected
argument_list|(
literal|0
argument_list|,
name|average
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|class|Builder
specifier|static
class|class
name|Builder
extends|extends
name|DeltaPackedLongValues
operator|.
name|Builder
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|Builder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|averages
name|float
index|[]
name|averages
decl_stmt|;
DECL|method|Builder
name|Builder
parameter_list|(
name|int
name|pageSize
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|super
argument_list|(
name|pageSize
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|averages
operator|=
operator|new
name|float
index|[
name|values
operator|.
name|length
index|]
expr_stmt|;
name|ramBytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|averages
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|baseRamBytesUsed
name|long
name|baseRamBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|MonotonicLongValues
name|build
parameter_list|()
block|{
name|finish
argument_list|()
expr_stmt|;
name|pending
operator|=
literal|null
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|values
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|values
argument_list|,
name|valuesOff
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|mins
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|mins
argument_list|,
name|valuesOff
argument_list|)
decl_stmt|;
specifier|final
name|float
index|[]
name|averages
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|averages
argument_list|,
name|valuesOff
argument_list|)
decl_stmt|;
specifier|final
name|long
name|ramBytesUsed
init|=
name|MonotonicLongValues
operator|.
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|mins
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|averages
argument_list|)
decl_stmt|;
return|return
operator|new
name|MonotonicLongValues
argument_list|(
name|pageShift
argument_list|,
name|pageMask
argument_list|,
name|values
argument_list|,
name|mins
argument_list|,
name|averages
argument_list|,
name|size
argument_list|,
name|ramBytesUsed
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|pack
name|void
name|pack
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|numValues
parameter_list|,
name|int
name|block
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
specifier|final
name|float
name|average
init|=
name|numValues
operator|==
literal|1
condition|?
literal|0
else|:
call|(
name|float
call|)
argument_list|(
name|values
index|[
name|numValues
operator|-
literal|1
index|]
operator|-
name|values
index|[
literal|0
index|]
argument_list|)
operator|/
operator|(
name|numValues
operator|-
literal|1
operator|)
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
name|numValues
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|-=
name|expected
argument_list|(
literal|0
argument_list|,
name|average
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|pack
argument_list|(
name|values
argument_list|,
name|numValues
argument_list|,
name|block
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|averages
index|[
name|block
index|]
operator|=
name|average
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|grow
name|void
name|grow
parameter_list|(
name|int
name|newBlockCount
parameter_list|)
block|{
name|super
operator|.
name|grow
argument_list|(
name|newBlockCount
argument_list|)
expr_stmt|;
name|ramBytesUsed
operator|-=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|averages
argument_list|)
expr_stmt|;
name|averages
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|averages
argument_list|,
name|newBlockCount
argument_list|)
expr_stmt|;
name|ramBytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|averages
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit