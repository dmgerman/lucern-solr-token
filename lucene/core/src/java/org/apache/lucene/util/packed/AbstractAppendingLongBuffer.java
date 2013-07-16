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
name|PackedInts
operator|.
name|checkBlockSize
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
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
comment|/** Common functionality shared by {@link AppendingLongBuffer} and {@link MonotonicAppendingLongBuffer}. */
end_comment
begin_class
DECL|class|AbstractAppendingLongBuffer
specifier|abstract
class|class
name|AbstractAppendingLongBuffer
block|{
DECL|field|MIN_PAGE_SIZE
specifier|static
specifier|final
name|int
name|MIN_PAGE_SIZE
init|=
literal|64
decl_stmt|;
comment|// More than 1M doesn't really makes sense with these appending buffers
comment|// since their goal is to try to have small numbers of bits per value
DECL|field|MAX_PAGE_SIZE
specifier|static
specifier|final
name|int
name|MAX_PAGE_SIZE
init|=
literal|1
operator|<<
literal|20
decl_stmt|;
DECL|field|pageShift
DECL|field|pageMask
specifier|final
name|int
name|pageShift
decl_stmt|,
name|pageMask
decl_stmt|;
DECL|field|minValues
name|long
index|[]
name|minValues
decl_stmt|;
DECL|field|deltas
name|PackedInts
operator|.
name|Reader
index|[]
name|deltas
decl_stmt|;
DECL|field|deltasBytes
specifier|private
name|long
name|deltasBytes
decl_stmt|;
DECL|field|valuesOff
name|int
name|valuesOff
decl_stmt|;
DECL|field|pending
name|long
index|[]
name|pending
decl_stmt|;
DECL|field|pendingOff
name|int
name|pendingOff
decl_stmt|;
DECL|method|AbstractAppendingLongBuffer
name|AbstractAppendingLongBuffer
parameter_list|(
name|int
name|initialBlockCount
parameter_list|,
name|int
name|pageSize
parameter_list|)
block|{
name|minValues
operator|=
operator|new
name|long
index|[
name|initialBlockCount
index|]
expr_stmt|;
name|deltas
operator|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
name|initialBlockCount
index|]
expr_stmt|;
name|pending
operator|=
operator|new
name|long
index|[
name|pageSize
index|]
expr_stmt|;
name|pageShift
operator|=
name|checkBlockSize
argument_list|(
name|pageSize
argument_list|,
name|MIN_PAGE_SIZE
argument_list|,
name|MAX_PAGE_SIZE
argument_list|)
expr_stmt|;
name|pageMask
operator|=
name|pageSize
operator|-
literal|1
expr_stmt|;
name|valuesOff
operator|=
literal|0
expr_stmt|;
name|pendingOff
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|pageSize
specifier|final
name|int
name|pageSize
parameter_list|()
block|{
return|return
name|pageMask
operator|+
literal|1
return|;
block|}
comment|/** Get the number of values that have been added to the buffer. */
DECL|method|size
specifier|public
specifier|final
name|long
name|size
parameter_list|()
block|{
name|long
name|size
init|=
name|pendingOff
decl_stmt|;
if|if
condition|(
name|valuesOff
operator|>
literal|0
condition|)
block|{
name|size
operator|+=
name|deltas
index|[
name|valuesOff
operator|-
literal|1
index|]
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|valuesOff
operator|>
literal|1
condition|)
block|{
name|size
operator|+=
call|(
name|long
call|)
argument_list|(
name|valuesOff
operator|-
literal|1
argument_list|)
operator|*
name|pageSize
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/** Append a value to this buffer. */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|long
name|l
parameter_list|)
block|{
if|if
condition|(
name|pending
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This buffer is frozen"
argument_list|)
throw|;
block|}
if|if
condition|(
name|pendingOff
operator|==
name|pending
operator|.
name|length
condition|)
block|{
comment|// check size
if|if
condition|(
name|deltas
operator|.
name|length
operator|==
name|valuesOff
condition|)
block|{
specifier|final
name|int
name|newLength
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|valuesOff
operator|+
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|grow
argument_list|(
name|newLength
argument_list|)
expr_stmt|;
block|}
name|packPendingValues
argument_list|()
expr_stmt|;
name|deltasBytes
operator|+=
name|deltas
index|[
name|valuesOff
index|]
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
operator|++
name|valuesOff
expr_stmt|;
comment|// reset pending buffer
name|pendingOff
operator|=
literal|0
expr_stmt|;
block|}
name|pending
index|[
name|pendingOff
operator|++
index|]
operator|=
name|l
expr_stmt|;
block|}
DECL|method|grow
name|void
name|grow
parameter_list|(
name|int
name|newBlockCount
parameter_list|)
block|{
name|minValues
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|minValues
argument_list|,
name|newBlockCount
argument_list|)
expr_stmt|;
name|deltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|deltas
argument_list|,
name|newBlockCount
argument_list|)
expr_stmt|;
block|}
DECL|method|packPendingValues
specifier|abstract
name|void
name|packPendingValues
parameter_list|()
function_decl|;
comment|/** Get a value from this buffer. */
DECL|method|get
specifier|public
specifier|final
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|""
operator|+
name|index
argument_list|)
throw|;
block|}
specifier|final
name|int
name|block
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>
name|pageShift
argument_list|)
decl_stmt|;
specifier|final
name|int
name|element
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|&
name|pageMask
argument_list|)
decl_stmt|;
return|return
name|get
argument_list|(
name|block
argument_list|,
name|element
argument_list|)
return|;
block|}
DECL|method|get
specifier|abstract
name|long
name|get
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|element
parameter_list|)
function_decl|;
DECL|method|iterator
specifier|abstract
name|Iterator
name|iterator
parameter_list|()
function_decl|;
DECL|class|Iterator
specifier|abstract
class|class
name|Iterator
block|{
DECL|field|currentValues
name|long
index|[]
name|currentValues
decl_stmt|;
DECL|field|vOff
DECL|field|pOff
name|int
name|vOff
decl_stmt|,
name|pOff
decl_stmt|;
DECL|field|currentCount
name|int
name|currentCount
decl_stmt|;
comment|// number of entries of the current page
DECL|method|Iterator
name|Iterator
parameter_list|()
block|{
name|vOff
operator|=
name|pOff
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|valuesOff
operator|==
literal|0
condition|)
block|{
name|currentValues
operator|=
name|pending
expr_stmt|;
name|currentCount
operator|=
name|pendingOff
expr_stmt|;
block|}
else|else
block|{
name|currentValues
operator|=
operator|new
name|long
index|[
name|deltas
index|[
literal|0
index|]
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|fillValues
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|fillValues
specifier|abstract
name|void
name|fillValues
parameter_list|()
function_decl|;
comment|/** Whether or not there are remaining values. */
DECL|method|hasNext
specifier|public
specifier|final
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pOff
operator|<
name|currentCount
return|;
block|}
comment|/** Return the next long in the buffer. */
DECL|method|next
specifier|public
specifier|final
name|long
name|next
parameter_list|()
block|{
assert|assert
name|hasNext
argument_list|()
assert|;
name|long
name|result
init|=
name|currentValues
index|[
name|pOff
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|pOff
operator|==
name|currentCount
condition|)
block|{
name|vOff
operator|+=
literal|1
expr_stmt|;
name|pOff
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|vOff
operator|<=
name|valuesOff
condition|)
block|{
name|fillValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
DECL|method|baseRamBytesUsed
name|long
name|baseRamBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
literal|3
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
comment|// the 3 arrays
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|// the 2 offsets
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|// pageShift, pageMask
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
comment|// deltasBytes
block|}
comment|/**    * Return the number of bytes used by this instance.    */
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// TODO: this is called per-doc-per-norms/dv-field, can we optimize this?
name|long
name|bytesUsed
init|=
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|baseRamBytesUsed
argument_list|()
argument_list|)
operator|+
operator|(
name|pending
operator|!=
literal|null
condition|?
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|pending
argument_list|)
else|:
literal|0L
operator|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|minValues
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|*
name|deltas
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// values
return|return
name|bytesUsed
operator|+
name|deltasBytes
return|;
block|}
comment|/** Pack all pending values in this buffer. Subsequent calls to {@link #add(long)} will fail. */
DECL|method|freeze
specifier|public
name|void
name|freeze
parameter_list|()
block|{
if|if
condition|(
name|pendingOff
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|deltas
operator|.
name|length
operator|==
name|valuesOff
condition|)
block|{
name|grow
argument_list|(
name|valuesOff
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// don't oversize!
block|}
name|packPendingValues
argument_list|()
expr_stmt|;
name|deltasBytes
operator|+=
name|deltas
index|[
name|valuesOff
index|]
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
operator|++
name|valuesOff
expr_stmt|;
name|pendingOff
operator|=
literal|0
expr_stmt|;
block|}
name|pending
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
