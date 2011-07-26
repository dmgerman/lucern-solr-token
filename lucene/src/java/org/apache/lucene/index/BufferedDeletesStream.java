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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|atomic
operator|.
name|AtomicLong
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Query
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
name|QueryWrapperFilter
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
name|IOContext
import|;
end_import
begin_comment
comment|/* Tracks the stream of {@link BufferedDeletes}.  * When DocumentsWriterPerThread flushes, its buffered  * deletes are appended to this stream.  We later  * apply these deletes (resolve them to the actual  * docIDs, per segment) when a merge is started  * (only to the to-be-merged segments).  We  * also apply to all segments when NRT reader is pulled,  * commit/close is called, or when too many deletes are  * buffered and must be flushed (by RAM usage or by count).  *  * Each packet is assigned a generation, and each flushed or  * merged segment is also assigned a generation, so we can  * track which BufferedDeletes packets to apply to any given  * segment. */
end_comment
begin_class
DECL|class|BufferedDeletesStream
class|class
name|BufferedDeletesStream
block|{
comment|// TODO: maybe linked list?
DECL|field|deletes
specifier|private
specifier|final
name|List
argument_list|<
name|FrozenBufferedDeletes
argument_list|>
name|deletes
init|=
operator|new
name|ArrayList
argument_list|<
name|FrozenBufferedDeletes
argument_list|>
argument_list|()
decl_stmt|;
comment|// Starts at 1 so that SegmentInfos that have never had
comment|// deletes applied (whose bufferedDelGen defaults to 0)
comment|// will be correct:
DECL|field|nextGen
specifier|private
name|long
name|nextGen
init|=
literal|1
decl_stmt|;
comment|// used only by assert
DECL|field|lastDeleteTerm
specifier|private
name|Term
name|lastDeleteTerm
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|AtomicLong
name|bytesUsed
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numTerms
specifier|private
specifier|final
name|AtomicInteger
name|numTerms
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|messageID
specifier|private
specifier|final
name|int
name|messageID
decl_stmt|;
DECL|method|BufferedDeletesStream
specifier|public
name|BufferedDeletesStream
parameter_list|(
name|int
name|messageID
parameter_list|)
block|{
name|this
operator|.
name|messageID
operator|=
name|messageID
expr_stmt|;
block|}
DECL|method|message
specifier|private
specifier|synchronized
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|println
argument_list|(
literal|"BD "
operator|+
name|messageID
operator|+
literal|" ["
operator|+
operator|new
name|Date
argument_list|()
operator|+
literal|"; "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setInfoStream
specifier|public
specifier|synchronized
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
comment|// Appends a new packet of buffered deletes to the stream,
comment|// setting its generation:
DECL|method|push
specifier|public
specifier|synchronized
name|long
name|push
parameter_list|(
name|FrozenBufferedDeletes
name|packet
parameter_list|)
block|{
comment|/*      * The insert operation must be atomic. If we let threads increment the gen      * and push the packet afterwards we risk that packets are out of order.      * With DWPT this is possible if two or more flushes are racing for pushing      * updates. If the pushed packets get our of order would loose documents      * since deletes are applied to the wrong segments.      */
name|packet
operator|.
name|setDelGen
argument_list|(
name|nextGen
operator|++
argument_list|)
expr_stmt|;
assert|assert
name|packet
operator|.
name|any
argument_list|()
assert|;
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
assert|assert
name|packet
operator|.
name|delGen
argument_list|()
operator|<
name|nextGen
assert|;
assert|assert
name|deletes
operator|.
name|isEmpty
argument_list|()
operator|||
name|deletes
operator|.
name|get
argument_list|(
name|deletes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|delGen
argument_list|()
operator|<
name|packet
operator|.
name|delGen
argument_list|()
operator|:
literal|"Delete packets must be in order"
assert|;
name|deletes
operator|.
name|add
argument_list|(
name|packet
argument_list|)
expr_stmt|;
name|numTerms
operator|.
name|addAndGet
argument_list|(
name|packet
operator|.
name|numTermDeletes
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|packet
operator|.
name|bytesUsed
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"push deletes "
operator|+
name|packet
operator|+
literal|" delGen="
operator|+
name|packet
operator|.
name|delGen
argument_list|()
operator|+
literal|" packetCount="
operator|+
name|deletes
operator|.
name|size
argument_list|()
operator|+
literal|" totBytesUsed="
operator|+
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
return|return
name|packet
operator|.
name|delGen
argument_list|()
return|;
block|}
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|deletes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nextGen
operator|=
literal|1
expr_stmt|;
name|numTerms
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|any
specifier|public
name|boolean
name|any
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|numTerms
specifier|public
name|int
name|numTerms
parameter_list|()
block|{
return|return
name|numTerms
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|bytesUsed
specifier|public
name|long
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
DECL|class|ApplyDeletesResult
specifier|public
specifier|static
class|class
name|ApplyDeletesResult
block|{
comment|// True if any actual deletes took place:
DECL|field|anyDeletes
specifier|public
specifier|final
name|boolean
name|anyDeletes
decl_stmt|;
comment|// Current gen, for the merged segment:
DECL|field|gen
specifier|public
specifier|final
name|long
name|gen
decl_stmt|;
comment|// If non-null, contains segments that are 100% deleted
DECL|field|allDeleted
specifier|public
specifier|final
name|List
argument_list|<
name|SegmentInfo
argument_list|>
name|allDeleted
decl_stmt|;
DECL|method|ApplyDeletesResult
name|ApplyDeletesResult
parameter_list|(
name|boolean
name|anyDeletes
parameter_list|,
name|long
name|gen
parameter_list|,
name|List
argument_list|<
name|SegmentInfo
argument_list|>
name|allDeleted
parameter_list|)
block|{
name|this
operator|.
name|anyDeletes
operator|=
name|anyDeletes
expr_stmt|;
name|this
operator|.
name|gen
operator|=
name|gen
expr_stmt|;
name|this
operator|.
name|allDeleted
operator|=
name|allDeleted
expr_stmt|;
block|}
block|}
comment|// Sorts SegmentInfos from smallest to biggest bufferedDelGen:
DECL|field|sortSegInfoByDelGen
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|SegmentInfo
argument_list|>
name|sortSegInfoByDelGen
init|=
operator|new
name|Comparator
argument_list|<
name|SegmentInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|SegmentInfo
name|si1
parameter_list|,
name|SegmentInfo
name|si2
parameter_list|)
block|{
specifier|final
name|long
name|cmp
init|=
name|si1
operator|.
name|getBufferedDeletesGen
argument_list|()
operator|-
name|si2
operator|.
name|getBufferedDeletesGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
decl_stmt|;
comment|/** Resolves the buffered deleted Term/Query/docIDs, into    *  actual deleted docIDs in the liveDocs BitVector for    *  each SegmentReader. */
DECL|method|applyDeletes
specifier|public
specifier|synchronized
name|ApplyDeletesResult
name|applyDeletes
parameter_list|(
name|IndexWriter
operator|.
name|ReaderPool
name|readerPool
parameter_list|,
name|List
argument_list|<
name|SegmentInfo
argument_list|>
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|infos
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|ApplyDeletesResult
argument_list|(
literal|false
argument_list|,
name|nextGen
operator|++
argument_list|,
literal|null
argument_list|)
return|;
block|}
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
if|if
condition|(
operator|!
name|any
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes: no deletes; skipping"
argument_list|)
expr_stmt|;
return|return
operator|new
name|ApplyDeletesResult
argument_list|(
literal|false
argument_list|,
name|nextGen
operator|++
argument_list|,
literal|null
argument_list|)
return|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes: infos="
operator|+
name|infos
operator|+
literal|" packetCount="
operator|+
name|deletes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|SegmentInfo
argument_list|>
name|infos2
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentInfo
argument_list|>
argument_list|()
decl_stmt|;
name|infos2
operator|.
name|addAll
argument_list|(
name|infos
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|infos2
argument_list|,
name|sortSegInfoByDelGen
argument_list|)
expr_stmt|;
name|BufferedDeletes
name|coalescedDeletes
init|=
literal|null
decl_stmt|;
name|boolean
name|anyNewDeletes
init|=
literal|false
decl_stmt|;
name|int
name|infosIDX
init|=
name|infos2
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|delIDX
init|=
name|deletes
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|SegmentInfo
argument_list|>
name|allDeleted
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|infosIDX
operator|>=
literal|0
condition|)
block|{
comment|//System.out.println("BD: cycle delIDX=" + delIDX + " infoIDX=" + infosIDX);
specifier|final
name|FrozenBufferedDeletes
name|packet
init|=
name|delIDX
operator|>=
literal|0
condition|?
name|deletes
operator|.
name|get
argument_list|(
name|delIDX
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|SegmentInfo
name|info
init|=
name|infos2
operator|.
name|get
argument_list|(
name|infosIDX
argument_list|)
decl_stmt|;
specifier|final
name|long
name|segGen
init|=
name|info
operator|.
name|getBufferedDeletesGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|packet
operator|!=
literal|null
operator|&&
name|segGen
operator|<
name|packet
operator|.
name|delGen
argument_list|()
condition|)
block|{
comment|//System.out.println("  coalesce");
if|if
condition|(
name|coalescedDeletes
operator|==
literal|null
condition|)
block|{
name|coalescedDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|packet
operator|.
name|isSegmentPrivate
condition|)
block|{
comment|/*            * Only coalesce if we are NOT on a segment private del packet: the segment private del packet            * must only applied to segments with the same delGen.  Yet, if a segment is already deleted            * from the SI since it had no more documents remaining after some del packets younger than            * its segPrivate packet (higher delGen) have been applied, the segPrivate packet has not been            * removed.            */
name|coalescedDeletes
operator|.
name|update
argument_list|(
name|packet
argument_list|)
expr_stmt|;
block|}
name|delIDX
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|packet
operator|!=
literal|null
operator|&&
name|segGen
operator|==
name|packet
operator|.
name|delGen
argument_list|()
condition|)
block|{
assert|assert
name|packet
operator|.
name|isSegmentPrivate
operator|:
literal|"Packet and Segments deletegen can only match on a segment private del packet"
assert|;
comment|//System.out.println("  eq");
comment|// Lock order: IW -> BD -> RP
assert|assert
name|readerPool
operator|.
name|infoIsLive
argument_list|(
name|info
argument_list|)
assert|;
specifier|final
name|SegmentReader
name|reader
init|=
name|readerPool
operator|.
name|get
argument_list|(
name|info
argument_list|,
literal|false
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
literal|0
decl_stmt|;
specifier|final
name|boolean
name|segAllDeletes
decl_stmt|;
try|try
block|{
if|if
condition|(
name|coalescedDeletes
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("    del coalesced");
name|delCount
operator|+=
name|applyTermDeletes
argument_list|(
name|coalescedDeletes
operator|.
name|termsIterable
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|delCount
operator|+=
name|applyQueryDeletes
argument_list|(
name|coalescedDeletes
operator|.
name|queriesIterable
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("    del exact");
comment|// Don't delete by Term here; DocumentsWriterPerThread
comment|// already did that on flush:
name|delCount
operator|+=
name|applyQueryDeletes
argument_list|(
name|packet
operator|.
name|queriesIterable
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|segAllDeletes
operator|=
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
expr_stmt|;
block|}
finally|finally
block|{
name|readerPool
operator|.
name|release
argument_list|(
name|reader
argument_list|,
name|IOContext
operator|.
name|Context
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
name|anyNewDeletes
operator||=
name|delCount
operator|>
literal|0
expr_stmt|;
if|if
condition|(
name|segAllDeletes
condition|)
block|{
if|if
condition|(
name|allDeleted
operator|==
literal|null
condition|)
block|{
name|allDeleted
operator|=
operator|new
name|ArrayList
argument_list|<
name|SegmentInfo
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|allDeleted
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"seg="
operator|+
name|info
operator|+
literal|" segGen="
operator|+
name|segGen
operator|+
literal|" segDeletes=["
operator|+
name|packet
operator|+
literal|"]; coalesced deletes=["
operator|+
operator|(
name|coalescedDeletes
operator|==
literal|null
condition|?
literal|"null"
else|:
name|coalescedDeletes
operator|)
operator|+
literal|"] delCount="
operator|+
name|delCount
operator|+
operator|(
name|segAllDeletes
condition|?
literal|" 100% deleted"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|coalescedDeletes
operator|==
literal|null
condition|)
block|{
name|coalescedDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/*          * Since we are on a segment private del packet we must not          * update the coalescedDeletes here! We can simply advance to the           * next packet and seginfo.          */
name|delIDX
operator|--
expr_stmt|;
name|infosIDX
operator|--
expr_stmt|;
name|info
operator|.
name|setBufferedDeletesGen
argument_list|(
name|nextGen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("  gt");
if|if
condition|(
name|coalescedDeletes
operator|!=
literal|null
condition|)
block|{
comment|// Lock order: IW -> BD -> RP
assert|assert
name|readerPool
operator|.
name|infoIsLive
argument_list|(
name|info
argument_list|)
assert|;
name|SegmentReader
name|reader
init|=
name|readerPool
operator|.
name|get
argument_list|(
name|info
argument_list|,
literal|false
argument_list|,
name|IOContext
operator|.
name|READ
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
literal|0
decl_stmt|;
specifier|final
name|boolean
name|segAllDeletes
decl_stmt|;
try|try
block|{
name|delCount
operator|+=
name|applyTermDeletes
argument_list|(
name|coalescedDeletes
operator|.
name|termsIterable
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|delCount
operator|+=
name|applyQueryDeletes
argument_list|(
name|coalescedDeletes
operator|.
name|queriesIterable
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|segAllDeletes
operator|=
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
expr_stmt|;
block|}
finally|finally
block|{
name|readerPool
operator|.
name|release
argument_list|(
name|reader
argument_list|,
name|IOContext
operator|.
name|Context
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
name|anyNewDeletes
operator||=
name|delCount
operator|>
literal|0
expr_stmt|;
if|if
condition|(
name|segAllDeletes
condition|)
block|{
if|if
condition|(
name|allDeleted
operator|==
literal|null
condition|)
block|{
name|allDeleted
operator|=
operator|new
name|ArrayList
argument_list|<
name|SegmentInfo
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|allDeleted
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"seg="
operator|+
name|info
operator|+
literal|" segGen="
operator|+
name|segGen
operator|+
literal|" coalesced deletes=["
operator|+
operator|(
name|coalescedDeletes
operator|==
literal|null
condition|?
literal|"null"
else|:
name|coalescedDeletes
operator|)
operator|+
literal|"] delCount="
operator|+
name|delCount
operator|+
operator|(
name|segAllDeletes
condition|?
literal|" 100% deleted"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
name|info
operator|.
name|setBufferedDeletesGen
argument_list|(
name|nextGen
argument_list|)
expr_stmt|;
name|infosIDX
operator|--
expr_stmt|;
block|}
block|}
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"applyDeletes took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
comment|// assert infos != segmentInfos || !any() : "infos=" + infos + " segmentInfos=" + segmentInfos + " any=" + any;
return|return
operator|new
name|ApplyDeletesResult
argument_list|(
name|anyNewDeletes
argument_list|,
name|nextGen
operator|++
argument_list|,
name|allDeleted
argument_list|)
return|;
block|}
DECL|method|getNextGen
specifier|synchronized
name|long
name|getNextGen
parameter_list|()
block|{
return|return
name|nextGen
operator|++
return|;
block|}
comment|// Lock order IW -> BD
comment|/* Removes any BufferedDeletes that we no longer need to    * store because all segments in the index have had the    * deletes applied. */
DECL|method|prune
specifier|public
specifier|synchronized
name|void
name|prune
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
block|{
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
name|long
name|minGen
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|SegmentInfo
name|info
range|:
name|segmentInfos
control|)
block|{
name|minGen
operator|=
name|Math
operator|.
name|min
argument_list|(
name|info
operator|.
name|getBufferedDeletesGen
argument_list|()
argument_list|,
name|minGen
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"prune sis="
operator|+
name|segmentInfos
operator|+
literal|" minGen="
operator|+
name|minGen
operator|+
literal|" packetCount="
operator|+
name|deletes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|limit
init|=
name|deletes
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|delIDX
init|=
literal|0
init|;
name|delIDX
operator|<
name|limit
condition|;
name|delIDX
operator|++
control|)
block|{
if|if
condition|(
name|deletes
operator|.
name|get
argument_list|(
name|delIDX
argument_list|)
operator|.
name|delGen
argument_list|()
operator|>=
name|minGen
condition|)
block|{
name|prune
argument_list|(
name|delIDX
argument_list|)
expr_stmt|;
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
return|return;
block|}
block|}
comment|// All deletes pruned
name|prune
argument_list|(
name|limit
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|any
argument_list|()
assert|;
assert|assert
name|checkDeleteStats
argument_list|()
assert|;
block|}
DECL|method|prune
specifier|private
specifier|synchronized
name|void
name|prune
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"pruneDeletes: prune "
operator|+
name|count
operator|+
literal|" packets; "
operator|+
operator|(
name|deletes
operator|.
name|size
argument_list|()
operator|-
name|count
operator|)
operator|+
literal|" packets remain"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|delIDX
init|=
literal|0
init|;
name|delIDX
operator|<
name|count
condition|;
name|delIDX
operator|++
control|)
block|{
specifier|final
name|FrozenBufferedDeletes
name|packet
init|=
name|deletes
operator|.
name|get
argument_list|(
name|delIDX
argument_list|)
decl_stmt|;
name|numTerms
operator|.
name|addAndGet
argument_list|(
operator|-
name|packet
operator|.
name|numTermDeletes
argument_list|)
expr_stmt|;
assert|assert
name|numTerms
operator|.
name|get
argument_list|()
operator|>=
literal|0
assert|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|packet
operator|.
name|bytesUsed
argument_list|)
expr_stmt|;
assert|assert
name|bytesUsed
operator|.
name|get
argument_list|()
operator|>=
literal|0
assert|;
block|}
name|deletes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|count
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Delete by Term
DECL|method|applyTermDeletes
specifier|private
specifier|synchronized
name|long
name|applyTermDeletes
parameter_list|(
name|Iterable
argument_list|<
name|Term
argument_list|>
name|termsIter
parameter_list|,
name|SegmentReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|delCount
init|=
literal|0
decl_stmt|;
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// This reader has no postings
return|return
literal|0
return|;
block|}
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|String
name|currentField
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
assert|assert
name|checkDeleteTerm
argument_list|(
literal|null
argument_list|)
assert|;
for|for
control|(
name|Term
name|term
range|:
name|termsIter
control|)
block|{
comment|// Since we visit terms sorted, we gain performance
comment|// by re-using the same TermsEnum and seeking only
comment|// forwards
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|currentField
argument_list|)
condition|)
block|{
assert|assert
name|currentField
operator|==
literal|null
operator|||
name|currentField
operator|.
name|compareTo
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|<
literal|0
assert|;
name|currentField
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|currentField
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
assert|assert
name|checkDeleteTerm
argument_list|(
name|term
argument_list|)
assert|;
comment|// System.out.println("  term=" + term);
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|DocsEnum
name|docsEnum
init|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|docs
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsEnum
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|reader
operator|.
name|deleteDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
comment|// TODO: we could/should change
comment|// reader.deleteDocument to return boolean
comment|// true if it did in fact delete, because here
comment|// we could be deleting an already-deleted doc
comment|// which makes this an upper bound:
name|delCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|delCount
return|;
block|}
DECL|class|QueryAndLimit
specifier|public
specifier|static
class|class
name|QueryAndLimit
block|{
DECL|field|query
specifier|public
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|limit
specifier|public
specifier|final
name|int
name|limit
decl_stmt|;
DECL|method|QueryAndLimit
specifier|public
name|QueryAndLimit
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
block|}
comment|// Delete by query
DECL|method|applyQueryDeletes
specifier|private
specifier|synchronized
name|long
name|applyQueryDeletes
parameter_list|(
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
name|queriesIter
parameter_list|,
name|SegmentReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|delCount
init|=
literal|0
decl_stmt|;
specifier|final
name|AtomicReaderContext
name|readerContext
init|=
operator|(
name|AtomicReaderContext
operator|)
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryAndLimit
name|ent
range|:
name|queriesIter
control|)
block|{
name|Query
name|query
init|=
name|ent
operator|.
name|query
decl_stmt|;
name|int
name|limit
init|=
name|ent
operator|.
name|limit
decl_stmt|;
specifier|final
name|DocIdSet
name|docs
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|query
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docs
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|limit
condition|)
break|break;
name|reader
operator|.
name|deleteDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// TODO: we could/should change
comment|// reader.deleteDocument to return boolean
comment|// true if it did in fact delete, because here
comment|// we could be deleting an already-deleted doc
comment|// which makes this an upper bound:
name|delCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|delCount
return|;
block|}
comment|// used only by assert
DECL|method|checkDeleteTerm
specifier|private
name|boolean
name|checkDeleteTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
assert|assert
name|lastDeleteTerm
operator|==
literal|null
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|lastDeleteTerm
argument_list|)
operator|>
literal|0
operator|:
literal|"lastTerm="
operator|+
name|lastDeleteTerm
operator|+
literal|" vs term="
operator|+
name|term
assert|;
block|}
name|lastDeleteTerm
operator|=
name|term
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// only for assert
DECL|method|checkDeleteStats
specifier|private
name|boolean
name|checkDeleteStats
parameter_list|()
block|{
name|int
name|numTerms2
init|=
literal|0
decl_stmt|;
name|long
name|bytesUsed2
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FrozenBufferedDeletes
name|packet
range|:
name|deletes
control|)
block|{
name|numTerms2
operator|+=
name|packet
operator|.
name|numTermDeletes
expr_stmt|;
name|bytesUsed2
operator|+=
name|packet
operator|.
name|bytesUsed
expr_stmt|;
block|}
assert|assert
name|numTerms2
operator|==
name|numTerms
operator|.
name|get
argument_list|()
operator|:
literal|"numTerms2="
operator|+
name|numTerms2
operator|+
literal|" vs "
operator|+
name|numTerms
operator|.
name|get
argument_list|()
assert|;
assert|assert
name|bytesUsed2
operator|==
name|bytesUsed
operator|.
name|get
argument_list|()
operator|:
literal|"bytesUsed2="
operator|+
name|bytesUsed2
operator|+
literal|" vs "
operator|+
name|bytesUsed
assert|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
