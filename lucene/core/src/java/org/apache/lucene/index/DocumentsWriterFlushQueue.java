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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|DocumentsWriterPerThread
operator|.
name|FlushedSegment
import|;
end_import
begin_comment
comment|/**  * @lucene.internal   */
end_comment
begin_class
DECL|class|DocumentsWriterFlushQueue
class|class
name|DocumentsWriterFlushQueue
block|{
DECL|field|queue
specifier|private
specifier|final
name|Queue
argument_list|<
name|FlushTicket
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<
name|FlushTicket
argument_list|>
argument_list|()
decl_stmt|;
comment|// we track tickets separately since count must be present even before the ticket is
comment|// constructed ie. queue.size would not reflect it.
DECL|field|ticketCount
specifier|private
specifier|final
name|AtomicInteger
name|ticketCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|purgeLock
specifier|private
specifier|final
name|ReentrantLock
name|purgeLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|method|addDeletes
name|void
name|addDeletes
parameter_list|(
name|DocumentsWriterDeleteQueue
name|deleteQueue
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|incTickets
argument_list|()
expr_stmt|;
comment|// first inc the ticket count - freeze opens
comment|// a window for #anyChanges to fail
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|queue
operator|.
name|add
argument_list|(
operator|new
name|GlobalDeletesTicket
argument_list|(
name|deleteQueue
operator|.
name|freezeGlobalBuffer
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|decTickets
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|incTickets
specifier|private
name|void
name|incTickets
parameter_list|()
block|{
name|int
name|numTickets
init|=
name|ticketCount
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
assert|assert
name|numTickets
operator|>
literal|0
assert|;
block|}
DECL|method|decTickets
specifier|private
name|void
name|decTickets
parameter_list|()
block|{
name|int
name|numTickets
init|=
name|ticketCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
assert|assert
name|numTickets
operator|>=
literal|0
assert|;
block|}
DECL|method|addFlushTicket
specifier|synchronized
name|SegmentFlushTicket
name|addFlushTicket
parameter_list|(
name|DocumentsWriterPerThread
name|dwpt
parameter_list|)
block|{
comment|// Each flush is assigned a ticket in the order they acquire the ticketQueue
comment|// lock
name|incTickets
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// prepare flush freezes the global deletes - do in synced block!
specifier|final
name|SegmentFlushTicket
name|ticket
init|=
operator|new
name|SegmentFlushTicket
argument_list|(
name|dwpt
operator|.
name|prepareFlush
argument_list|()
argument_list|)
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|ticket
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ticket
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|decTickets
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|addSegment
specifier|synchronized
name|void
name|addSegment
parameter_list|(
name|SegmentFlushTicket
name|ticket
parameter_list|,
name|FlushedSegment
name|segment
parameter_list|)
block|{
comment|// the actual flush is done asynchronously and once done the FlushedSegment
comment|// is passed to the flush ticket
name|ticket
operator|.
name|setSegment
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
DECL|method|markTicketFailed
specifier|synchronized
name|void
name|markTicketFailed
parameter_list|(
name|SegmentFlushTicket
name|ticket
parameter_list|)
block|{
comment|// to free the queue we mark tickets as failed just to clean up the queue.
name|ticket
operator|.
name|setFailed
argument_list|()
expr_stmt|;
block|}
DECL|method|hasTickets
name|boolean
name|hasTickets
parameter_list|()
block|{
assert|assert
name|ticketCount
operator|.
name|get
argument_list|()
operator|>=
literal|0
operator|:
literal|"ticketCount should be>= 0 but was: "
operator|+
name|ticketCount
operator|.
name|get
argument_list|()
assert|;
return|return
name|ticketCount
operator|.
name|get
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|innerPurge
specifier|private
name|int
name|innerPurge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|purgeLock
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
name|int
name|numPurged
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|FlushTicket
name|head
decl_stmt|;
specifier|final
name|boolean
name|canPublish
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|head
operator|=
name|queue
operator|.
name|peek
argument_list|()
expr_stmt|;
name|canPublish
operator|=
name|head
operator|!=
literal|null
operator|&&
name|head
operator|.
name|canPublish
argument_list|()
expr_stmt|;
comment|// do this synced
block|}
if|if
condition|(
name|canPublish
condition|)
block|{
name|numPurged
operator|++
expr_stmt|;
try|try
block|{
comment|/*            * if we block on publish -> lock IW -> lock BufferedDeletes we don't block            * concurrent segment flushes just because they want to append to the queue.            * the downside is that we need to force a purge on fullFlush since ther could            * be a ticket still in the queue.             */
name|head
operator|.
name|publish
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// finally remove the published ticket from the queue
specifier|final
name|FlushTicket
name|poll
init|=
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
name|ticketCount
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
assert|assert
name|poll
operator|==
name|head
assert|;
block|}
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|numPurged
return|;
block|}
DECL|method|forcePurge
name|int
name|forcePurge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
assert|assert
operator|!
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
name|purgeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|innerPurge
argument_list|(
name|writer
argument_list|)
return|;
block|}
finally|finally
block|{
name|purgeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|tryPurge
name|int
name|tryPurge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
assert|assert
operator|!
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
if|if
condition|(
name|purgeLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
try|try
block|{
return|return
name|innerPurge
argument_list|(
name|writer
argument_list|)
return|;
block|}
finally|finally
block|{
name|purgeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|getTicketCount
specifier|public
name|int
name|getTicketCount
parameter_list|()
block|{
return|return
name|ticketCount
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|clear
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ticketCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|class|FlushTicket
specifier|static
specifier|abstract
class|class
name|FlushTicket
block|{
DECL|field|frozenUpdates
specifier|protected
name|FrozenBufferedUpdates
name|frozenUpdates
decl_stmt|;
DECL|field|published
specifier|protected
name|boolean
name|published
init|=
literal|false
decl_stmt|;
DECL|method|FlushTicket
specifier|protected
name|FlushTicket
parameter_list|(
name|FrozenBufferedUpdates
name|frozenUpdates
parameter_list|)
block|{
assert|assert
name|frozenUpdates
operator|!=
literal|null
assert|;
name|this
operator|.
name|frozenUpdates
operator|=
name|frozenUpdates
expr_stmt|;
block|}
DECL|method|publish
specifier|protected
specifier|abstract
name|void
name|publish
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|canPublish
specifier|protected
specifier|abstract
name|boolean
name|canPublish
parameter_list|()
function_decl|;
comment|/**      * Publishes the flushed segment, segment private deletes (if any) and its      * associated global delete (if present) to IndexWriter.  The actual      * publishing operation is synced on IW -> BDS so that the {@link SegmentInfo}'s      * delete generation is always GlobalPacket_deleteGeneration + 1      */
DECL|method|publishFlushedSegment
specifier|protected
specifier|final
name|void
name|publishFlushedSegment
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|FlushedSegment
name|newSegment
parameter_list|,
name|FrozenBufferedUpdates
name|globalPacket
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|newSegment
operator|!=
literal|null
assert|;
assert|assert
name|newSegment
operator|.
name|segmentInfo
operator|!=
literal|null
assert|;
specifier|final
name|FrozenBufferedUpdates
name|segmentUpdates
init|=
name|newSegment
operator|.
name|segmentUpdates
decl_stmt|;
comment|//System.out.println("FLUSH: " + newSegment.segmentInfo.info.name);
if|if
condition|(
name|indexWriter
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"DW"
argument_list|)
condition|)
block|{
name|indexWriter
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"DW"
argument_list|,
literal|"publishFlushedSegment seg-private updates="
operator|+
name|segmentUpdates
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|segmentUpdates
operator|!=
literal|null
operator|&&
name|indexWriter
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"DW"
argument_list|)
condition|)
block|{
name|indexWriter
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"DW"
argument_list|,
literal|"flush: push buffered seg private updates: "
operator|+
name|segmentUpdates
argument_list|)
expr_stmt|;
block|}
comment|// now publish!
name|indexWriter
operator|.
name|publishFlushedSegment
argument_list|(
name|newSegment
operator|.
name|segmentInfo
argument_list|,
name|segmentUpdates
argument_list|,
name|globalPacket
argument_list|)
expr_stmt|;
block|}
DECL|method|finishFlush
specifier|protected
specifier|final
name|void
name|finishFlush
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|FlushedSegment
name|newSegment
parameter_list|,
name|FrozenBufferedUpdates
name|bufferedUpdates
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Finish the flushed segment and publish it to IndexWriter
if|if
condition|(
name|newSegment
operator|==
literal|null
condition|)
block|{
assert|assert
name|bufferedUpdates
operator|!=
literal|null
assert|;
if|if
condition|(
name|bufferedUpdates
operator|!=
literal|null
operator|&&
name|bufferedUpdates
operator|.
name|any
argument_list|()
condition|)
block|{
name|indexWriter
operator|.
name|publishFrozenUpdates
argument_list|(
name|bufferedUpdates
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"DW"
argument_list|)
condition|)
block|{
name|indexWriter
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"DW"
argument_list|,
literal|"flush: push buffered updates: "
operator|+
name|bufferedUpdates
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|publishFlushedSegment
argument_list|(
name|indexWriter
argument_list|,
name|newSegment
argument_list|,
name|bufferedUpdates
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|GlobalDeletesTicket
specifier|static
specifier|final
class|class
name|GlobalDeletesTicket
extends|extends
name|FlushTicket
block|{
DECL|method|GlobalDeletesTicket
specifier|protected
name|GlobalDeletesTicket
parameter_list|(
name|FrozenBufferedUpdates
name|frozenUpdates
parameter_list|)
block|{
name|super
argument_list|(
name|frozenUpdates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publish
specifier|protected
name|void
name|publish
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|published
operator|:
literal|"ticket was already publised - can not publish twice"
assert|;
name|published
operator|=
literal|true
expr_stmt|;
comment|// its a global ticket - no segment to publish
name|finishFlush
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|,
name|frozenUpdates
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canPublish
specifier|protected
name|boolean
name|canPublish
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|class|SegmentFlushTicket
specifier|static
specifier|final
class|class
name|SegmentFlushTicket
extends|extends
name|FlushTicket
block|{
DECL|field|segment
specifier|private
name|FlushedSegment
name|segment
decl_stmt|;
DECL|field|failed
specifier|private
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
DECL|method|SegmentFlushTicket
specifier|protected
name|SegmentFlushTicket
parameter_list|(
name|FrozenBufferedUpdates
name|frozenDeletes
parameter_list|)
block|{
name|super
argument_list|(
name|frozenDeletes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publish
specifier|protected
name|void
name|publish
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|published
operator|:
literal|"ticket was already publised - can not publish twice"
assert|;
name|published
operator|=
literal|true
expr_stmt|;
name|finishFlush
argument_list|(
name|writer
argument_list|,
name|segment
argument_list|,
name|frozenUpdates
argument_list|)
expr_stmt|;
block|}
DECL|method|setSegment
specifier|protected
name|void
name|setSegment
parameter_list|(
name|FlushedSegment
name|segment
parameter_list|)
block|{
assert|assert
operator|!
name|failed
assert|;
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
block|}
DECL|method|setFailed
specifier|protected
name|void
name|setFailed
parameter_list|()
block|{
assert|assert
name|segment
operator|==
literal|null
assert|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canPublish
specifier|protected
name|boolean
name|canPublish
parameter_list|()
block|{
return|return
name|segment
operator|!=
literal|null
operator|||
name|failed
return|;
block|}
block|}
block|}
end_class
end_unit
