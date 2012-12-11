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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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
name|index
operator|.
name|DocumentsWriterDeleteQueue
operator|.
name|DeleteSlice
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
name|search
operator|.
name|TermQuery
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
name|BytesRef
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
name|LuceneTestCase
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
name|ThreadInterruptedException
import|;
end_import
begin_comment
comment|/**  * Unit test for {@link DocumentsWriterDeleteQueue}  */
end_comment
begin_class
DECL|class|TestDocumentsWriterDeleteQueue
specifier|public
class|class
name|TestDocumentsWriterDeleteQueue
extends|extends
name|LuceneTestCase
block|{
DECL|method|testUpdateDelteSlices
specifier|public
name|void
name|testUpdateDelteSlices
parameter_list|()
block|{
name|DocumentsWriterDeleteQueue
name|queue
init|=
operator|new
name|DocumentsWriterDeleteQueue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
literal|200
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|Integer
index|[]
name|ids
init|=
operator|new
name|Integer
index|[
name|size
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
name|ids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ids
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
name|DeleteSlice
name|slice1
init|=
name|queue
operator|.
name|newSlice
argument_list|()
decl_stmt|;
name|DeleteSlice
name|slice2
init|=
name|queue
operator|.
name|newSlice
argument_list|()
decl_stmt|;
name|BufferedDeletes
name|bd1
init|=
operator|new
name|BufferedDeletes
argument_list|()
decl_stmt|;
name|BufferedDeletes
name|bd2
init|=
operator|new
name|BufferedDeletes
argument_list|()
decl_stmt|;
name|int
name|last1
init|=
literal|0
decl_stmt|;
name|int
name|last2
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|uniqueValues
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
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
name|ids
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Integer
name|i
init|=
name|ids
index|[
name|j
index|]
decl_stmt|;
comment|// create an array here since we compare identity below against tailItem
name|Term
index|[]
name|term
init|=
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|i
operator|.
name|toString
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|uniqueValues
operator|.
name|add
argument_list|(
name|term
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addDelete
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|0
operator|||
name|j
operator|==
name|ids
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|queue
operator|.
name|updateSlice
argument_list|(
name|slice1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slice1
operator|.
name|isTailItem
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|slice1
operator|.
name|apply
argument_list|(
name|bd1
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|assertAllBetween
argument_list|(
name|last1
argument_list|,
name|j
argument_list|,
name|bd1
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|last1
operator|=
name|j
operator|+
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|5
operator|||
name|j
operator|==
name|ids
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|queue
operator|.
name|updateSlice
argument_list|(
name|slice2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slice2
operator|.
name|isTailItem
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|slice2
operator|.
name|apply
argument_list|(
name|bd2
argument_list|,
name|j
argument_list|)
expr_stmt|;
name|assertAllBetween
argument_list|(
name|last2
argument_list|,
name|j
argument_list|,
name|bd2
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|last2
operator|=
name|j
operator|+
literal|1
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|j
operator|+
literal|1
argument_list|,
name|queue
operator|.
name|numGlobalTermDeletes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|uniqueValues
argument_list|,
name|bd1
operator|.
name|terms
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueValues
argument_list|,
name|bd2
operator|.
name|terms
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|frozenSet
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|queue
operator|.
name|freezeGlobalBuffer
argument_list|(
literal|null
argument_list|)
operator|.
name|termsIterable
argument_list|()
control|)
block|{
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|bytesRef
operator|.
name|copyBytes
argument_list|(
name|t
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|frozenSet
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|t
operator|.
name|field
argument_list|,
name|bytesRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|uniqueValues
argument_list|,
name|frozenSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"num deletes must be 0 after freeze"
argument_list|,
literal|0
argument_list|,
name|queue
operator|.
name|numGlobalTermDeletes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAllBetween
specifier|private
name|void
name|assertAllBetween
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BufferedDeletes
name|deletes
parameter_list|,
name|Integer
index|[]
name|ids
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<=
name|end
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|end
argument_list|)
argument_list|,
name|deletes
operator|.
name|terms
operator|.
name|get
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|ids
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testClear
specifier|public
name|void
name|testClear
parameter_list|()
block|{
name|DocumentsWriterDeleteQueue
name|queue
init|=
operator|new
name|DocumentsWriterDeleteQueue
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
literal|200
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|int
name|termsSinceFreeze
init|=
literal|0
decl_stmt|;
name|int
name|queriesSinceFreeze
init|=
literal|0
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|queue
operator|.
name|addDelete
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|queriesSinceFreeze
operator|++
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|addDelete
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|termsSinceFreeze
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|queue
operator|.
name|tryApplyGlobalSlice
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testAnyChanges
specifier|public
name|void
name|testAnyChanges
parameter_list|()
block|{
name|DocumentsWriterDeleteQueue
name|queue
init|=
operator|new
name|DocumentsWriterDeleteQueue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
literal|200
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|int
name|termsSinceFreeze
init|=
literal|0
decl_stmt|;
name|int
name|queriesSinceFreeze
init|=
literal|0
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|queue
operator|.
name|addDelete
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|queriesSinceFreeze
operator|++
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|addDelete
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|termsSinceFreeze
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
condition|)
block|{
name|FrozenBufferedDeletes
name|freezeGlobalBuffer
init|=
name|queue
operator|.
name|freezeGlobalBuffer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|termsSinceFreeze
argument_list|,
name|freezeGlobalBuffer
operator|.
name|termCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|queriesSinceFreeze
argument_list|,
name|freezeGlobalBuffer
operator|.
name|queries
operator|.
name|length
argument_list|)
expr_stmt|;
name|queriesSinceFreeze
operator|=
literal|0
expr_stmt|;
name|termsSinceFreeze
operator|=
literal|0
expr_stmt|;
name|assertFalse
argument_list|(
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testPartiallyAppliedGlobalSlice
specifier|public
name|void
name|testPartiallyAppliedGlobalSlice
parameter_list|()
throws|throws
name|SecurityException
throws|,
name|NoSuchFieldException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|InterruptedException
block|{
specifier|final
name|DocumentsWriterDeleteQueue
name|queue
init|=
operator|new
name|DocumentsWriterDeleteQueue
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|DocumentsWriterDeleteQueue
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"globalBufferLock"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ReentrantLock
name|lock
init|=
operator|(
name|ReentrantLock
operator|)
name|field
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|queue
operator|.
name|addDelete
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"changes in del queue but not in slice yet"
argument_list|,
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
name|queue
operator|.
name|tryApplyGlobalSlice
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"changes in global buffer"
argument_list|,
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
name|FrozenBufferedDeletes
name|freezeGlobalBuffer
init|=
name|queue
operator|.
name|freezeGlobalBuffer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|freezeGlobalBuffer
operator|.
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freezeGlobalBuffer
operator|.
name|termCount
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"all changes applied"
argument_list|,
name|queue
operator|.
name|anyChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStressDeleteQueue
specifier|public
name|void
name|testStressDeleteQueue
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|DocumentsWriterDeleteQueue
name|queue
init|=
operator|new
name|DocumentsWriterDeleteQueue
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|uniqueValues
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
literal|10000
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|Integer
index|[]
name|ids
init|=
operator|new
name|Integer
index|[
name|size
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
name|ids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ids
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|uniqueValues
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|ids
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AtomicInteger
name|index
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numThreads
init|=
literal|2
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|UpdateThread
index|[]
name|threads
init|=
operator|new
name|UpdateThread
index|[
name|numThreads
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|UpdateThread
argument_list|(
name|queue
argument_list|,
name|index
argument_list|,
name|ids
argument_list|,
name|latch
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|UpdateThread
name|updateThread
range|:
name|threads
control|)
block|{
name|DeleteSlice
name|slice
init|=
name|updateThread
operator|.
name|slice
decl_stmt|;
name|queue
operator|.
name|updateSlice
argument_list|(
name|slice
argument_list|)
expr_stmt|;
name|BufferedDeletes
name|deletes
init|=
name|updateThread
operator|.
name|deletes
decl_stmt|;
name|slice
operator|.
name|apply
argument_list|(
name|deletes
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueValues
argument_list|,
name|deletes
operator|.
name|terms
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|queue
operator|.
name|tryApplyGlobalSlice
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|Term
argument_list|>
name|frozenSet
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|queue
operator|.
name|freezeGlobalBuffer
argument_list|(
literal|null
argument_list|)
operator|.
name|termsIterable
argument_list|()
control|)
block|{
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|bytesRef
operator|.
name|copyBytes
argument_list|(
name|t
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|frozenSet
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|t
operator|.
name|field
argument_list|,
name|bytesRef
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"num deletes must be 0 after freeze"
argument_list|,
literal|0
argument_list|,
name|queue
operator|.
name|numGlobalTermDeletes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
argument_list|,
name|frozenSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|uniqueValues
argument_list|,
name|frozenSet
argument_list|)
expr_stmt|;
block|}
DECL|class|UpdateThread
specifier|private
specifier|static
class|class
name|UpdateThread
extends|extends
name|Thread
block|{
DECL|field|queue
specifier|final
name|DocumentsWriterDeleteQueue
name|queue
decl_stmt|;
DECL|field|index
specifier|final
name|AtomicInteger
name|index
decl_stmt|;
DECL|field|ids
specifier|final
name|Integer
index|[]
name|ids
decl_stmt|;
DECL|field|slice
specifier|final
name|DeleteSlice
name|slice
decl_stmt|;
DECL|field|deletes
specifier|final
name|BufferedDeletes
name|deletes
decl_stmt|;
DECL|field|latch
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|method|UpdateThread
specifier|protected
name|UpdateThread
parameter_list|(
name|DocumentsWriterDeleteQueue
name|queue
parameter_list|,
name|AtomicInteger
name|index
parameter_list|,
name|Integer
index|[]
name|ids
parameter_list|,
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|ids
operator|=
name|ids
expr_stmt|;
name|this
operator|.
name|slice
operator|=
name|queue
operator|.
name|newSlice
argument_list|()
expr_stmt|;
name|deletes
operator|=
operator|new
name|BufferedDeletes
argument_list|()
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|i
operator|=
name|index
operator|.
name|getAndIncrement
argument_list|()
operator|)
operator|<
name|ids
operator|.
name|length
condition|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|ids
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|term
argument_list|,
name|slice
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slice
operator|.
name|isTailItem
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|slice
operator|.
name|apply
argument_list|(
name|deletes
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
