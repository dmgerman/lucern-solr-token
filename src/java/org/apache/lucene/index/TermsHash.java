begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|util
operator|.
name|ArrayUtil
import|;
end_import
begin_comment
comment|/** This class implements {@link InvertedDocConsumer}, which  *  is passed each token produced by the analyzer on each  *  field.  It stores these tokens in a hash table, and  *  allocates separate byte streams per token.  Consumers of  *  this class, eg {@link FreqProxTermsWriter} and {@link  *  TermVectorsTermsWriter}, write their own byte streams  *  under each term.  */
end_comment
begin_comment
comment|// TODO: Fix the unchecked collections, I do not understand the whole code here -- Uwe
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TermsHash
specifier|final
class|class
name|TermsHash
extends|extends
name|InvertedDocConsumer
block|{
DECL|field|consumer
specifier|final
name|TermsHashConsumer
name|consumer
decl_stmt|;
DECL|field|nextTermsHash
specifier|final
name|TermsHash
name|nextTermsHash
decl_stmt|;
DECL|field|bytesPerPosting
specifier|final
name|int
name|bytesPerPosting
decl_stmt|;
DECL|field|postingsFreeChunk
specifier|final
name|int
name|postingsFreeChunk
decl_stmt|;
DECL|field|docWriter
specifier|final
name|DocumentsWriter
name|docWriter
decl_stmt|;
DECL|field|primaryTermsHash
specifier|private
name|TermsHash
name|primaryTermsHash
decl_stmt|;
DECL|field|postingsFreeList
specifier|private
name|RawPostingList
index|[]
name|postingsFreeList
init|=
operator|new
name|RawPostingList
index|[
literal|1
index|]
decl_stmt|;
DECL|field|postingsFreeCount
specifier|private
name|int
name|postingsFreeCount
decl_stmt|;
DECL|field|postingsAllocCount
specifier|private
name|int
name|postingsAllocCount
decl_stmt|;
DECL|field|trackAllocations
name|boolean
name|trackAllocations
decl_stmt|;
DECL|method|TermsHash
specifier|public
name|TermsHash
parameter_list|(
specifier|final
name|DocumentsWriter
name|docWriter
parameter_list|,
name|boolean
name|trackAllocations
parameter_list|,
specifier|final
name|TermsHashConsumer
name|consumer
parameter_list|,
specifier|final
name|TermsHash
name|nextTermsHash
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|nextTermsHash
operator|=
name|nextTermsHash
expr_stmt|;
name|this
operator|.
name|trackAllocations
operator|=
name|trackAllocations
expr_stmt|;
comment|// Why + 4*POINTER_NUM_BYTE below?
comment|//   +1: Posting is referenced by postingsFreeList array
comment|//   +3: Posting is referenced by hash, which
comment|//       targets 25-50% fill factor; approximate this
comment|//       as 3X # pointers
name|bytesPerPosting
operator|=
name|consumer
operator|.
name|bytesPerPosting
argument_list|()
operator|+
literal|4
operator|*
name|DocumentsWriter
operator|.
name|POINTER_NUM_BYTE
expr_stmt|;
name|postingsFreeChunk
operator|=
call|(
name|int
call|)
argument_list|(
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
operator|/
name|bytesPerPosting
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addThread
name|InvertedDocConsumerPerThread
name|addThread
parameter_list|(
name|DocInverterPerThread
name|docInverterPerThread
parameter_list|)
block|{
return|return
operator|new
name|TermsHashPerThread
argument_list|(
name|docInverterPerThread
argument_list|,
name|this
argument_list|,
name|nextTermsHash
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|addThread
name|TermsHashPerThread
name|addThread
parameter_list|(
name|DocInverterPerThread
name|docInverterPerThread
parameter_list|,
name|TermsHashPerThread
name|primaryPerThread
parameter_list|)
block|{
return|return
operator|new
name|TermsHashPerThread
argument_list|(
name|docInverterPerThread
argument_list|,
name|this
argument_list|,
name|nextTermsHash
argument_list|,
name|primaryPerThread
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setFieldInfos
name|void
name|setFieldInfos
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|consumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|synchronized
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextTermsHash
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
DECL|method|shrinkFreePostings
name|void
name|shrinkFreePostings
parameter_list|(
name|Map
name|threadsAndFields
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
block|{
assert|assert
name|postingsFreeCount
operator|==
name|postingsAllocCount
operator|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": postingsFreeCount="
operator|+
name|postingsFreeCount
operator|+
literal|" postingsAllocCount="
operator|+
name|postingsAllocCount
operator|+
literal|" consumer="
operator|+
name|consumer
assert|;
specifier|final
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|getShrinkSize
argument_list|(
name|postingsFreeList
operator|.
name|length
argument_list|,
name|postingsAllocCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSize
operator|!=
name|postingsFreeList
operator|.
name|length
condition|)
block|{
name|RawPostingList
index|[]
name|newArray
init|=
operator|new
name|RawPostingList
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|postingsFreeList
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|postingsFreeCount
argument_list|)
expr_stmt|;
name|postingsFreeList
operator|=
name|newArray
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|closeDocStore
specifier|synchronized
name|void
name|closeDocStore
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextTermsHash
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|synchronized
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|InvertedDocConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|InvertedDocConsumerPerField
argument_list|>
argument_list|>
name|threadsAndFields
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|childThreadsAndFields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Map
name|nextThreadsAndFields
decl_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextThreadsAndFields
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
else|else
name|nextThreadsAndFields
operator|=
literal|null
expr_stmt|;
name|Iterator
name|it
init|=
name|threadsAndFields
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|TermsHashPerThread
name|perThread
init|=
operator|(
name|TermsHashPerThread
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Collection
name|fields
init|=
operator|(
name|Collection
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Iterator
name|fieldsIt
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Collection
name|childFields
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Collection
name|nextChildFields
decl_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextChildFields
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
else|else
name|nextChildFields
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|fieldsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TermsHashPerField
name|perField
init|=
operator|(
name|TermsHashPerField
operator|)
name|fieldsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|childFields
operator|.
name|add
argument_list|(
name|perField
operator|.
name|consumer
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextChildFields
operator|.
name|add
argument_list|(
name|perField
operator|.
name|nextPerField
argument_list|)
expr_stmt|;
block|}
name|childThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|consumer
argument_list|,
name|childFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|nextPerThread
argument_list|,
name|nextChildFields
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|flush
argument_list|(
name|childThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|shrinkFreePostings
argument_list|(
name|threadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextTermsHash
operator|.
name|flush
argument_list|(
name|nextThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|freeRAM
specifier|synchronized
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
if|if
condition|(
operator|!
name|trackAllocations
condition|)
return|return
literal|false
return|;
name|boolean
name|any
decl_stmt|;
specifier|final
name|int
name|numToFree
decl_stmt|;
if|if
condition|(
name|postingsFreeCount
operator|>=
name|postingsFreeChunk
condition|)
name|numToFree
operator|=
name|postingsFreeChunk
expr_stmt|;
else|else
name|numToFree
operator|=
name|postingsFreeCount
expr_stmt|;
name|any
operator|=
name|numToFree
operator|>
literal|0
expr_stmt|;
if|if
condition|(
name|any
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|postingsFreeList
argument_list|,
name|postingsFreeCount
operator|-
name|numToFree
argument_list|,
name|postingsFreeCount
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|postingsFreeCount
operator|-=
name|numToFree
expr_stmt|;
name|postingsAllocCount
operator|-=
name|numToFree
expr_stmt|;
name|docWriter
operator|.
name|bytesAllocated
argument_list|(
operator|-
name|numToFree
operator|*
name|bytesPerPosting
argument_list|)
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|any
operator||=
name|nextTermsHash
operator|.
name|freeRAM
argument_list|()
expr_stmt|;
return|return
name|any
return|;
block|}
DECL|method|recyclePostings
specifier|synchronized
specifier|public
name|void
name|recyclePostings
parameter_list|(
specifier|final
name|RawPostingList
index|[]
name|postings
parameter_list|,
specifier|final
name|int
name|numPostings
parameter_list|)
block|{
assert|assert
name|postings
operator|.
name|length
operator|>=
name|numPostings
assert|;
comment|// Move all Postings from this ThreadState back to our
comment|// free list.  We pre-allocated this array while we were
comment|// creating Postings to make sure it's large enough
assert|assert
name|postingsFreeCount
operator|+
name|numPostings
operator|<=
name|postingsFreeList
operator|.
name|length
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|postings
argument_list|,
literal|0
argument_list|,
name|postingsFreeList
argument_list|,
name|postingsFreeCount
argument_list|,
name|numPostings
argument_list|)
expr_stmt|;
name|postingsFreeCount
operator|+=
name|numPostings
expr_stmt|;
block|}
DECL|method|getPostings
specifier|synchronized
specifier|public
name|void
name|getPostings
parameter_list|(
specifier|final
name|RawPostingList
index|[]
name|postings
parameter_list|)
block|{
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"TermsHash.getPostings start"
argument_list|)
assert|;
assert|assert
name|postingsFreeCount
operator|<=
name|postingsFreeList
operator|.
name|length
assert|;
assert|assert
name|postingsFreeCount
operator|<=
name|postingsAllocCount
operator|:
literal|"postingsFreeCount="
operator|+
name|postingsFreeCount
operator|+
literal|" postingsAllocCount="
operator|+
name|postingsAllocCount
assert|;
specifier|final
name|int
name|numToCopy
decl_stmt|;
if|if
condition|(
name|postingsFreeCount
operator|<
name|postings
operator|.
name|length
condition|)
name|numToCopy
operator|=
name|postingsFreeCount
expr_stmt|;
else|else
name|numToCopy
operator|=
name|postings
operator|.
name|length
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|postingsFreeCount
operator|-
name|numToCopy
decl_stmt|;
assert|assert
name|start
operator|>=
literal|0
assert|;
assert|assert
name|start
operator|+
name|numToCopy
operator|<=
name|postingsFreeList
operator|.
name|length
assert|;
assert|assert
name|numToCopy
operator|<=
name|postings
operator|.
name|length
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|postingsFreeList
argument_list|,
name|start
argument_list|,
name|postings
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
comment|// Directly allocate the remainder if any
if|if
condition|(
name|numToCopy
operator|!=
name|postings
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|extra
init|=
name|postings
operator|.
name|length
operator|-
name|numToCopy
decl_stmt|;
specifier|final
name|int
name|newPostingsAllocCount
init|=
name|postingsAllocCount
operator|+
name|extra
decl_stmt|;
name|consumer
operator|.
name|createPostings
argument_list|(
name|postings
argument_list|,
name|numToCopy
argument_list|,
name|extra
argument_list|)
expr_stmt|;
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"TermsHash.getPostings after create"
argument_list|)
assert|;
name|postingsAllocCount
operator|+=
name|extra
expr_stmt|;
if|if
condition|(
name|trackAllocations
condition|)
name|docWriter
operator|.
name|bytesAllocated
argument_list|(
name|extra
operator|*
name|bytesPerPosting
argument_list|)
expr_stmt|;
if|if
condition|(
name|newPostingsAllocCount
operator|>
name|postingsFreeList
operator|.
name|length
condition|)
comment|// Pre-allocate the postingsFreeList so it's large
comment|// enough to hold all postings we've given out
name|postingsFreeList
operator|=
operator|new
name|RawPostingList
index|[
name|ArrayUtil
operator|.
name|getNextSize
argument_list|(
name|newPostingsAllocCount
argument_list|)
index|]
expr_stmt|;
block|}
name|postingsFreeCount
operator|-=
name|numToCopy
expr_stmt|;
if|if
condition|(
name|trackAllocations
condition|)
name|docWriter
operator|.
name|bytesUsed
argument_list|(
name|postings
operator|.
name|length
operator|*
name|bytesPerPosting
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
