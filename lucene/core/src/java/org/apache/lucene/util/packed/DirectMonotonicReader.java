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
name|io
operator|.
name|IOException
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
name|store
operator|.
name|IndexInput
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
name|store
operator|.
name|RandomAccessInput
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
name|Accountable
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
name|LongValues
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
begin_comment
comment|/**  * Retrieves an instance previously written by {@link DirectMonotonicWriter}.  * @see DirectMonotonicWriter   */
end_comment
begin_class
DECL|class|DirectMonotonicReader
specifier|public
specifier|final
class|class
name|DirectMonotonicReader
block|{
comment|/** An instance that always returns {@code 0}. */
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|LongValues
name|EMPTY
init|=
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|/** In-memory metadata that needs to be kept around for    *  {@link DirectMonotonicReader} to read data from disk. */
DECL|class|Meta
specifier|public
specifier|static
class|class
name|Meta
implements|implements
name|Accountable
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
name|Meta
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|numValues
specifier|final
name|long
name|numValues
decl_stmt|;
DECL|field|blockShift
specifier|final
name|int
name|blockShift
decl_stmt|;
DECL|field|numBlocks
specifier|final
name|int
name|numBlocks
decl_stmt|;
DECL|field|mins
specifier|final
name|long
index|[]
name|mins
decl_stmt|;
DECL|field|avgs
specifier|final
name|float
index|[]
name|avgs
decl_stmt|;
DECL|field|bpvs
specifier|final
name|byte
index|[]
name|bpvs
decl_stmt|;
DECL|field|offsets
specifier|final
name|long
index|[]
name|offsets
decl_stmt|;
DECL|method|Meta
name|Meta
parameter_list|(
name|long
name|numValues
parameter_list|,
name|int
name|blockShift
parameter_list|)
block|{
name|this
operator|.
name|numValues
operator|=
name|numValues
expr_stmt|;
name|this
operator|.
name|blockShift
operator|=
name|blockShift
expr_stmt|;
name|long
name|numBlocks
init|=
name|numValues
operator|>>>
name|blockShift
decl_stmt|;
if|if
condition|(
operator|(
name|numBlocks
operator|<<
name|blockShift
operator|)
operator|<
name|numValues
condition|)
block|{
name|numBlocks
operator|+=
literal|1
expr_stmt|;
block|}
name|this
operator|.
name|numBlocks
operator|=
operator|(
name|int
operator|)
name|numBlocks
expr_stmt|;
name|this
operator|.
name|mins
operator|=
operator|new
name|long
index|[
name|this
operator|.
name|numBlocks
index|]
expr_stmt|;
name|this
operator|.
name|avgs
operator|=
operator|new
name|float
index|[
name|this
operator|.
name|numBlocks
index|]
expr_stmt|;
name|this
operator|.
name|bpvs
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|numBlocks
index|]
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
operator|new
name|long
index|[
name|this
operator|.
name|numBlocks
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
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
name|avgs
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|bpvs
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|offsets
argument_list|)
return|;
block|}
block|}
comment|/** Load metadata from the given {@link IndexInput}.    *  @see DirectMonotonicReader#getInstance(Meta, RandomAccessInput) */
DECL|method|loadMeta
specifier|public
specifier|static
name|Meta
name|loadMeta
parameter_list|(
name|IndexInput
name|metaIn
parameter_list|,
name|long
name|numValues
parameter_list|,
name|int
name|blockShift
parameter_list|)
throws|throws
name|IOException
block|{
name|Meta
name|meta
init|=
operator|new
name|Meta
argument_list|(
name|numValues
argument_list|,
name|blockShift
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
name|meta
operator|.
name|numBlocks
condition|;
operator|++
name|i
control|)
block|{
name|meta
operator|.
name|mins
index|[
name|i
index|]
operator|=
name|metaIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|meta
operator|.
name|avgs
index|[
name|i
index|]
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|metaIn
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|meta
operator|.
name|offsets
index|[
name|i
index|]
operator|=
name|metaIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|meta
operator|.
name|bpvs
index|[
name|i
index|]
operator|=
name|metaIn
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
return|return
name|meta
return|;
block|}
comment|/**    * Retrieves an instance from the specified slice.    */
DECL|method|getInstance
specifier|public
specifier|static
name|LongValues
name|getInstance
parameter_list|(
name|Meta
name|meta
parameter_list|,
name|RandomAccessInput
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LongValues
index|[]
name|readers
init|=
operator|new
name|LongValues
index|[
name|meta
operator|.
name|numBlocks
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
name|meta
operator|.
name|mins
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|meta
operator|.
name|bpvs
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
name|readers
index|[
name|i
index|]
operator|=
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|readers
index|[
name|i
index|]
operator|=
name|DirectReader
operator|.
name|getInstance
argument_list|(
name|data
argument_list|,
name|meta
operator|.
name|bpvs
index|[
name|i
index|]
argument_list|,
name|meta
operator|.
name|offsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|blockShift
init|=
name|meta
operator|.
name|blockShift
decl_stmt|;
specifier|final
name|long
index|[]
name|mins
init|=
name|meta
operator|.
name|mins
decl_stmt|;
specifier|final
name|float
index|[]
name|avgs
init|=
name|meta
operator|.
name|avgs
decl_stmt|;
return|return
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
specifier|final
name|int
name|block
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>>
name|blockShift
argument_list|)
decl_stmt|;
specifier|final
name|long
name|blockIndex
init|=
name|index
operator|&
operator|(
operator|(
literal|1
operator|<<
name|blockShift
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|readers
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|blockIndex
argument_list|)
decl_stmt|;
return|return
name|mins
index|[
name|block
index|]
operator|+
call|(
name|long
call|)
argument_list|(
name|avgs
index|[
name|block
index|]
operator|*
name|blockIndex
argument_list|)
operator|+
name|delta
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
