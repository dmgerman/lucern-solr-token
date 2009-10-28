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
name|HashMap
import|;
end_import
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
name|Iterator
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
name|HashSet
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
comment|/** This is just a "splitter" class: it lets you wrap two  *  DocFieldConsumer instances as a single consumer. */
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
DECL|class|DocFieldConsumers
specifier|final
class|class
name|DocFieldConsumers
extends|extends
name|DocFieldConsumer
block|{
DECL|field|one
specifier|final
name|DocFieldConsumer
name|one
decl_stmt|;
DECL|field|two
specifier|final
name|DocFieldConsumer
name|two
decl_stmt|;
DECL|method|DocFieldConsumers
specifier|public
name|DocFieldConsumers
parameter_list|(
name|DocFieldConsumer
name|one
parameter_list|,
name|DocFieldConsumer
name|two
parameter_list|)
block|{
name|this
operator|.
name|one
operator|=
name|one
expr_stmt|;
name|this
operator|.
name|two
operator|=
name|two
expr_stmt|;
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
name|super
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|one
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|two
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|DocFieldConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|DocFieldConsumerPerField
argument_list|>
argument_list|>
name|threadsAndFields
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
name|oneThreadsAndFields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Map
name|twoThreadsAndFields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
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
name|DocFieldConsumersPerThread
name|perThread
init|=
operator|(
name|DocFieldConsumersPerThread
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
name|oneFields
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|Collection
name|twoFields
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocFieldConsumersPerField
name|perField
init|=
operator|(
name|DocFieldConsumersPerField
operator|)
name|fieldsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|oneFields
operator|.
name|add
argument_list|(
name|perField
operator|.
name|one
argument_list|)
expr_stmt|;
name|twoFields
operator|.
name|add
argument_list|(
name|perField
operator|.
name|two
argument_list|)
expr_stmt|;
block|}
name|oneThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|one
argument_list|,
name|oneFields
argument_list|)
expr_stmt|;
name|twoThreadsAndFields
operator|.
name|put
argument_list|(
name|perThread
operator|.
name|two
argument_list|,
name|twoFields
argument_list|)
expr_stmt|;
block|}
name|one
operator|.
name|flush
argument_list|(
name|oneThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|two
operator|.
name|flush
argument_list|(
name|twoThreadsAndFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|closeDocStore
specifier|public
name|void
name|closeDocStore
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|one
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|two
operator|.
name|closeDocStore
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|one
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|two
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
name|boolean
name|any
init|=
name|one
operator|.
name|freeRAM
argument_list|()
decl_stmt|;
name|any
operator||=
name|two
operator|.
name|freeRAM
argument_list|()
expr_stmt|;
return|return
name|any
return|;
block|}
annotation|@
name|Override
DECL|method|addThread
specifier|public
name|DocFieldConsumerPerThread
name|addThread
parameter_list|(
name|DocFieldProcessorPerThread
name|docFieldProcessorPerThread
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocFieldConsumersPerThread
argument_list|(
name|docFieldProcessorPerThread
argument_list|,
name|this
argument_list|,
name|one
operator|.
name|addThread
argument_list|(
name|docFieldProcessorPerThread
argument_list|)
argument_list|,
name|two
operator|.
name|addThread
argument_list|(
name|docFieldProcessorPerThread
argument_list|)
argument_list|)
return|;
block|}
DECL|field|docFreeList
name|PerDoc
index|[]
name|docFreeList
init|=
operator|new
name|PerDoc
index|[
literal|1
index|]
decl_stmt|;
DECL|field|freeCount
name|int
name|freeCount
decl_stmt|;
DECL|field|allocCount
name|int
name|allocCount
decl_stmt|;
DECL|method|getPerDoc
specifier|synchronized
name|PerDoc
name|getPerDoc
parameter_list|()
block|{
if|if
condition|(
name|freeCount
operator|==
literal|0
condition|)
block|{
name|allocCount
operator|++
expr_stmt|;
if|if
condition|(
name|allocCount
operator|>
name|docFreeList
operator|.
name|length
condition|)
block|{
comment|// Grow our free list up front to make sure we have
comment|// enough space to recycle all outstanding PerDoc
comment|// instances
assert|assert
name|allocCount
operator|==
literal|1
operator|+
name|docFreeList
operator|.
name|length
assert|;
name|docFreeList
operator|=
operator|new
name|PerDoc
index|[
name|ArrayUtil
operator|.
name|getNextSize
argument_list|(
name|allocCount
argument_list|)
index|]
expr_stmt|;
block|}
return|return
operator|new
name|PerDoc
argument_list|()
return|;
block|}
else|else
return|return
name|docFreeList
index|[
operator|--
name|freeCount
index|]
return|;
block|}
DECL|method|freePerDoc
specifier|synchronized
name|void
name|freePerDoc
parameter_list|(
name|PerDoc
name|perDoc
parameter_list|)
block|{
assert|assert
name|freeCount
operator|<
name|docFreeList
operator|.
name|length
assert|;
name|docFreeList
index|[
name|freeCount
operator|++
index|]
operator|=
name|perDoc
expr_stmt|;
block|}
DECL|class|PerDoc
class|class
name|PerDoc
extends|extends
name|DocumentsWriter
operator|.
name|DocWriter
block|{
DECL|field|one
name|DocumentsWriter
operator|.
name|DocWriter
name|one
decl_stmt|;
DECL|field|two
name|DocumentsWriter
operator|.
name|DocWriter
name|two
decl_stmt|;
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|one
operator|.
name|sizeInBytes
argument_list|()
operator|+
name|two
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
try|try
block|{
name|one
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|two
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|freePerDoc
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
try|try
block|{
name|one
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|two
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|freePerDoc
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
