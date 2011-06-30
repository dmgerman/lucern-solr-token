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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_MASK
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
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
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
name|text
operator|.
name|NumberFormat
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
name|analysis
operator|.
name|Analyzer
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
name|document
operator|.
name|Document
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
name|SimilarityProvider
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
name|Directory
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
name|FlushInfo
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
operator|.
name|Context
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
name|BitVector
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
name|ByteBlockPool
operator|.
name|Allocator
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
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
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
begin_class
DECL|class|DocumentsWriterPerThread
specifier|public
class|class
name|DocumentsWriterPerThread
block|{
comment|/**    * The IndexingChain must define the {@link #getChain(DocumentsWriter)} method    * which returns the DocConsumer that the DocumentsWriter calls to process the    * documents.    */
DECL|class|IndexingChain
specifier|abstract
specifier|static
class|class
name|IndexingChain
block|{
DECL|method|getChain
specifier|abstract
name|DocConsumer
name|getChain
parameter_list|(
name|DocumentsWriterPerThread
name|documentsWriterPerThread
parameter_list|)
function_decl|;
block|}
DECL|field|defaultIndexingChain
specifier|static
specifier|final
name|IndexingChain
name|defaultIndexingChain
init|=
operator|new
name|IndexingChain
argument_list|()
block|{
annotation|@
name|Override
name|DocConsumer
name|getChain
parameter_list|(
name|DocumentsWriterPerThread
name|documentsWriterPerThread
parameter_list|)
block|{
comment|/*       This is the current indexing chain:        DocConsumer / DocConsumerPerThread         --> code: DocFieldProcessor / DocFieldProcessorPerThread           --> DocFieldConsumer / DocFieldConsumerPerThread / DocFieldConsumerPerField             --> code: DocFieldConsumers / DocFieldConsumersPerThread / DocFieldConsumersPerField               --> code: DocInverter / DocInverterPerThread / DocInverterPerField                 --> InvertedDocConsumer / InvertedDocConsumerPerThread / InvertedDocConsumerPerField                   --> code: TermsHash / TermsHashPerThread / TermsHashPerField                     --> TermsHashConsumer / TermsHashConsumerPerThread / TermsHashConsumerPerField                       --> code: FreqProxTermsWriter / FreqProxTermsWriterPerThread / FreqProxTermsWriterPerField                       --> code: TermVectorsTermsWriter / TermVectorsTermsWriterPerThread / TermVectorsTermsWriterPerField                 --> InvertedDocEndConsumer / InvertedDocConsumerPerThread / InvertedDocConsumerPerField                   --> code: NormsWriter / NormsWriterPerThread / NormsWriterPerField               --> code: StoredFieldsWriter / StoredFieldsWriterPerThread / StoredFieldsWriterPerField     */
comment|// Build up indexing chain:
specifier|final
name|TermsHashConsumer
name|termVectorsWriter
init|=
operator|new
name|TermVectorsTermsWriter
argument_list|(
name|documentsWriterPerThread
argument_list|)
decl_stmt|;
specifier|final
name|TermsHashConsumer
name|freqProxWriter
init|=
operator|new
name|FreqProxTermsWriter
argument_list|()
decl_stmt|;
specifier|final
name|InvertedDocConsumer
name|termsHash
init|=
operator|new
name|TermsHash
argument_list|(
name|documentsWriterPerThread
argument_list|,
name|freqProxWriter
argument_list|,
literal|true
argument_list|,
operator|new
name|TermsHash
argument_list|(
name|documentsWriterPerThread
argument_list|,
name|termVectorsWriter
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NormsWriter
name|normsWriter
init|=
operator|new
name|NormsWriter
argument_list|()
decl_stmt|;
specifier|final
name|DocInverter
name|docInverter
init|=
operator|new
name|DocInverter
argument_list|(
name|documentsWriterPerThread
operator|.
name|docState
argument_list|,
name|termsHash
argument_list|,
name|normsWriter
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocFieldProcessor
argument_list|(
name|documentsWriterPerThread
argument_list|,
name|docInverter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|DocState
specifier|static
class|class
name|DocState
block|{
DECL|field|docWriter
specifier|final
name|DocumentsWriterPerThread
name|docWriter
decl_stmt|;
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|infoStream
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|similarityProvider
name|SimilarityProvider
name|similarityProvider
decl_stmt|;
DECL|field|docID
name|int
name|docID
decl_stmt|;
DECL|field|doc
name|Document
name|doc
decl_stmt|;
DECL|field|maxTermPrefix
name|String
name|maxTermPrefix
decl_stmt|;
DECL|method|DocState
name|DocState
parameter_list|(
name|DocumentsWriterPerThread
name|docWriter
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
block|}
comment|// Only called by asserts
DECL|method|testPoint
specifier|public
name|boolean
name|testPoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
comment|// don't hold onto doc nor analyzer, in case it is
comment|// largish:
name|doc
operator|=
literal|null
expr_stmt|;
name|analyzer
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|class|FlushedSegment
specifier|static
class|class
name|FlushedSegment
block|{
DECL|field|segmentInfo
specifier|final
name|SegmentInfo
name|segmentInfo
decl_stmt|;
DECL|field|segmentDeletes
specifier|final
name|BufferedDeletes
name|segmentDeletes
decl_stmt|;
DECL|field|deletedDocuments
specifier|final
name|BitVector
name|deletedDocuments
decl_stmt|;
DECL|method|FlushedSegment
specifier|private
name|FlushedSegment
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|BufferedDeletes
name|segmentDeletes
parameter_list|,
name|BitVector
name|deletedDocuments
parameter_list|)
block|{
name|this
operator|.
name|segmentInfo
operator|=
name|segmentInfo
expr_stmt|;
name|this
operator|.
name|segmentDeletes
operator|=
name|segmentDeletes
expr_stmt|;
name|this
operator|.
name|deletedDocuments
operator|=
name|deletedDocuments
expr_stmt|;
block|}
block|}
comment|/** Called if we hit an exception at a bad time (when    *  updating the index files) and must discard all    *  currently buffered docs.  This resets our state,    *  discarding any docs added since last flush. */
DECL|method|abort
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|hasAborted
operator|=
name|aborting
operator|=
literal|true
expr_stmt|;
try|try
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
literal|"docWriter: now abort"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
name|pendingDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deleteSlice
operator|=
name|deleteQueue
operator|.
name|newSlice
argument_list|()
expr_stmt|;
comment|// Reset all postings data
name|doAfterFlush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|aborting
operator|=
literal|false
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
literal|"docWriter: done abort"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|parent
specifier|final
name|DocumentsWriter
name|parent
decl_stmt|;
DECL|field|writer
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|directory
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|docState
specifier|final
name|DocState
name|docState
decl_stmt|;
DECL|field|consumer
specifier|final
name|DocConsumer
name|consumer
decl_stmt|;
DECL|field|bytesUsed
specifier|final
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|field|flushState
name|SegmentWriteState
name|flushState
decl_stmt|;
comment|//Deletes for our still-in-RAM (to be flushed next) segment
DECL|field|pendingDeletes
name|BufferedDeletes
name|pendingDeletes
decl_stmt|;
DECL|field|segment
name|String
name|segment
decl_stmt|;
comment|// Current segment we are working on
DECL|field|aborting
name|boolean
name|aborting
init|=
literal|false
decl_stmt|;
comment|// True if an abort is pending
DECL|field|hasAborted
name|boolean
name|hasAborted
init|=
literal|false
decl_stmt|;
comment|// True if the last exception throws by #updateDocument was aborting
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|numDocsInRAM
specifier|private
name|int
name|numDocsInRAM
decl_stmt|;
DECL|field|flushedDocCount
specifier|private
name|int
name|flushedDocCount
decl_stmt|;
DECL|field|deleteQueue
name|DocumentsWriterDeleteQueue
name|deleteQueue
decl_stmt|;
DECL|field|deleteSlice
name|DeleteSlice
name|deleteSlice
decl_stmt|;
DECL|field|nf
specifier|private
specifier|final
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|field|byteBlockAllocator
specifier|final
name|Allocator
name|byteBlockAllocator
decl_stmt|;
DECL|method|DocumentsWriterPerThread
specifier|public
name|DocumentsWriterPerThread
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|DocumentsWriter
name|parent
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IndexingChain
name|indexingChain
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|parent
operator|.
name|indexWriter
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|parent
operator|.
name|infoStream
expr_stmt|;
name|this
operator|.
name|docState
operator|=
operator|new
name|DocState
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|docState
operator|.
name|similarityProvider
operator|=
name|parent
operator|.
name|indexWriter
operator|.
name|getConfig
argument_list|()
operator|.
name|getSimilarityProvider
argument_list|()
expr_stmt|;
name|bytesUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|byteBlockAllocator
operator|=
operator|new
name|DirectTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
name|consumer
operator|=
name|indexingChain
operator|.
name|getChain
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|pendingDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
DECL|method|DocumentsWriterPerThread
specifier|public
name|DocumentsWriterPerThread
parameter_list|(
name|DocumentsWriterPerThread
name|other
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|this
argument_list|(
name|other
operator|.
name|directory
argument_list|,
name|other
operator|.
name|parent
argument_list|,
name|fieldInfos
argument_list|,
name|other
operator|.
name|parent
operator|.
name|chain
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
name|void
name|initialize
parameter_list|()
block|{
name|deleteQueue
operator|=
name|parent
operator|.
name|deleteQueue
expr_stmt|;
assert|assert
name|numDocsInRAM
operator|==
literal|0
operator|:
literal|"num docs "
operator|+
name|numDocsInRAM
assert|;
name|pendingDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deleteSlice
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setAborting
name|void
name|setAborting
parameter_list|()
block|{
name|aborting
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|checkAndResetHasAborted
name|boolean
name|checkAndResetHasAborted
parameter_list|()
block|{
specifier|final
name|boolean
name|retval
init|=
name|hasAborted
decl_stmt|;
name|hasAborted
operator|=
literal|false
expr_stmt|;
return|return
name|retval
return|;
block|}
DECL|method|updateDocument
specifier|public
name|void
name|updateDocument
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Term
name|delTerm
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|writer
operator|.
name|testPoint
argument_list|(
literal|"DocumentsWriterPerThread addDocument start"
argument_list|)
assert|;
assert|assert
name|deleteQueue
operator|!=
literal|null
assert|;
name|docState
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|docState
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|docState
operator|.
name|docID
operator|=
name|numDocsInRAM
expr_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
comment|// this call is synchronized on IndexWriter.segmentInfos
name|segment
operator|=
name|writer
operator|.
name|newSegmentName
argument_list|()
expr_stmt|;
assert|assert
name|numDocsInRAM
operator|==
literal|0
assert|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
try|try
block|{
name|consumer
operator|.
name|processDocument
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|docState
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|aborting
condition|)
block|{
comment|// mark document as deleted
name|deleteDocID
argument_list|(
name|docState
operator|.
name|docID
argument_list|)
expr_stmt|;
name|numDocsInRAM
operator|++
expr_stmt|;
name|fieldInfos
operator|.
name|revertUncommitted
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|finishDocument
argument_list|()
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
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
name|finishDocument
argument_list|(
name|delTerm
argument_list|)
expr_stmt|;
block|}
DECL|method|updateDocuments
specifier|public
name|int
name|updateDocuments
parameter_list|(
name|Iterable
argument_list|<
name|Document
argument_list|>
name|docs
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Term
name|delTerm
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|writer
operator|.
name|testPoint
argument_list|(
literal|"DocumentsWriterPerThread addDocuments start"
argument_list|)
assert|;
assert|assert
name|deleteQueue
operator|!=
literal|null
assert|;
name|docState
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
if|if
condition|(
name|segment
operator|==
literal|null
condition|)
block|{
comment|// this call is synchronized on IndexWriter.segmentInfos
name|segment
operator|=
name|writer
operator|.
name|newSegmentName
argument_list|()
expr_stmt|;
assert|assert
name|numDocsInRAM
operator|==
literal|0
assert|;
block|}
name|int
name|docCount
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|Document
name|doc
range|:
name|docs
control|)
block|{
name|docState
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|docState
operator|.
name|docID
operator|=
name|numDocsInRAM
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|processDocument
argument_list|(
name|fieldInfos
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
comment|// An exc is being thrown...
if|if
condition|(
operator|!
name|aborting
condition|)
block|{
comment|// One of the documents hit a non-aborting
comment|// exception (eg something happened during
comment|// analysis).  We now go and mark any docs
comment|// from this batch that we had already indexed
comment|// as deleted:
name|int
name|docID
init|=
name|docState
operator|.
name|docID
decl_stmt|;
specifier|final
name|int
name|endDocID
init|=
name|docID
operator|-
name|docCount
decl_stmt|;
while|while
condition|(
name|docID
operator|>
name|endDocID
condition|)
block|{
name|deleteDocID
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|docID
operator|--
expr_stmt|;
block|}
comment|// Incr here because finishDocument will not
comment|// be called (because an exc is being thrown):
name|numDocsInRAM
operator|++
expr_stmt|;
name|fieldInfos
operator|.
name|revertUncommitted
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|finishDocument
argument_list|()
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
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
name|finishDocument
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Apply delTerm only after all indexing has
comment|// succeeded, but apply it only to docs prior to when
comment|// this batch started:
if|if
condition|(
name|delTerm
operator|!=
literal|null
condition|)
block|{
name|deleteQueue
operator|.
name|add
argument_list|(
name|delTerm
argument_list|,
name|deleteSlice
argument_list|)
expr_stmt|;
assert|assert
name|deleteSlice
operator|.
name|isTailItem
argument_list|(
name|delTerm
argument_list|)
operator|:
literal|"expected the delete term as the tail item"
assert|;
name|deleteSlice
operator|.
name|apply
argument_list|(
name|pendingDeletes
argument_list|,
name|numDocsInRAM
operator|-
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|docState
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|docCount
return|;
block|}
DECL|method|finishDocument
specifier|private
name|void
name|finishDocument
parameter_list|(
name|Term
name|delTerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*      * here we actually finish the document in two steps 1. push the delete into      * the queue and update our slice. 2. increment the DWPT private document      * id.      *       * the updated slice we get from 1. holds all the deletes that have occurred      * since we updated the slice the last time.      */
if|if
condition|(
name|deleteSlice
operator|==
literal|null
condition|)
block|{
name|deleteSlice
operator|=
name|deleteQueue
operator|.
name|newSlice
argument_list|()
expr_stmt|;
if|if
condition|(
name|delTerm
operator|!=
literal|null
condition|)
block|{
name|deleteQueue
operator|.
name|add
argument_list|(
name|delTerm
argument_list|,
name|deleteSlice
argument_list|)
expr_stmt|;
name|deleteSlice
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|delTerm
operator|!=
literal|null
condition|)
block|{
name|deleteQueue
operator|.
name|add
argument_list|(
name|delTerm
argument_list|,
name|deleteSlice
argument_list|)
expr_stmt|;
assert|assert
name|deleteSlice
operator|.
name|isTailItem
argument_list|(
name|delTerm
argument_list|)
operator|:
literal|"expected the delete term as the tail item"
assert|;
name|deleteSlice
operator|.
name|apply
argument_list|(
name|pendingDeletes
argument_list|,
name|numDocsInRAM
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|deleteQueue
operator|.
name|updateSlice
argument_list|(
name|deleteSlice
argument_list|)
condition|)
block|{
name|deleteSlice
operator|.
name|apply
argument_list|(
name|pendingDeletes
argument_list|,
name|numDocsInRAM
argument_list|)
expr_stmt|;
block|}
block|}
operator|++
name|numDocsInRAM
expr_stmt|;
block|}
comment|// Buffer a specific docID for deletion.  Currently only
comment|// used when we hit a exception when adding a document
DECL|method|deleteDocID
name|void
name|deleteDocID
parameter_list|(
name|int
name|docIDUpto
parameter_list|)
block|{
name|pendingDeletes
operator|.
name|addDocID
argument_list|(
name|docIDUpto
argument_list|)
expr_stmt|;
comment|// NOTE: we do not trigger flush here.  This is
comment|// potentially a RAM leak, if you have an app that tries
comment|// to add docs but every single doc always hits a
comment|// non-aborting exception.  Allowing a flush here gets
comment|// very messy because we are only invoked when handling
comment|// exceptions so to do this properly, while handling an
comment|// exception we'd have to go off and flush new deletes
comment|// which is risky (likely would hit some other
comment|// confounding exception).
block|}
comment|/**    * Returns the number of delete terms in this {@link DocumentsWriterPerThread}    */
DECL|method|numDeleteTerms
specifier|public
name|int
name|numDeleteTerms
parameter_list|()
block|{
comment|// public for FlushPolicy
return|return
name|pendingDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the number of RAM resident documents in this {@link DocumentsWriterPerThread}    */
DECL|method|getNumDocsInRAM
specifier|public
name|int
name|getNumDocsInRAM
parameter_list|()
block|{
comment|// public for FlushPolicy
return|return
name|numDocsInRAM
return|;
block|}
DECL|method|getCodec
name|SegmentCodecs
name|getCodec
parameter_list|()
block|{
return|return
name|flushState
operator|.
name|segmentCodecs
return|;
block|}
comment|/** Reset after a flush */
DECL|method|doAfterFlush
specifier|private
name|void
name|doAfterFlush
parameter_list|()
throws|throws
name|IOException
block|{
name|segment
operator|=
literal|null
expr_stmt|;
name|consumer
operator|.
name|doAfterFlush
argument_list|()
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
name|parent
operator|.
name|subtractFlushedNumDocs
argument_list|(
name|numDocsInRAM
argument_list|)
expr_stmt|;
name|numDocsInRAM
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Prepares this DWPT for flushing. This method will freeze and return the    * {@link DocumentsWriterDeleteQueue}s global buffer and apply all pending    * deletes to this DWPT.    */
DECL|method|prepareFlush
name|FrozenBufferedDeletes
name|prepareFlush
parameter_list|()
block|{
assert|assert
name|numDocsInRAM
operator|>
literal|0
assert|;
specifier|final
name|FrozenBufferedDeletes
name|globalDeletes
init|=
name|deleteQueue
operator|.
name|freezeGlobalBuffer
argument_list|(
name|deleteSlice
argument_list|)
decl_stmt|;
comment|/* deleteSlice can possibly be null if we have hit non-aborting exceptions during indexing and never succeeded      adding a document. */
if|if
condition|(
name|deleteSlice
operator|!=
literal|null
condition|)
block|{
comment|// apply all deletes before we flush and release the delete slice
name|deleteSlice
operator|.
name|apply
argument_list|(
name|pendingDeletes
argument_list|,
name|numDocsInRAM
argument_list|)
expr_stmt|;
assert|assert
name|deleteSlice
operator|.
name|isEmpty
argument_list|()
assert|;
name|deleteSlice
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|globalDeletes
return|;
block|}
comment|/** Flush all pending docs to a new segment */
DECL|method|flush
name|FlushedSegment
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|numDocsInRAM
operator|>
literal|0
assert|;
assert|assert
name|deleteSlice
operator|==
literal|null
operator|:
literal|"all deletes must be applied in prepareFlush"
assert|;
name|flushState
operator|=
operator|new
name|SegmentWriteState
argument_list|(
name|infoStream
argument_list|,
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|,
name|numDocsInRAM
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getTermIndexInterval
argument_list|()
argument_list|,
name|fieldInfos
operator|.
name|buildSegmentCodecs
argument_list|(
literal|true
argument_list|)
argument_list|,
name|pendingDeletes
argument_list|,
operator|new
name|IOContext
argument_list|(
operator|new
name|FlushInfo
argument_list|(
name|numDocsInRAM
argument_list|,
name|bytesUsed
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|double
name|startMBUsed
init|=
name|parent
operator|.
name|flushControl
operator|.
name|netBytes
argument_list|()
operator|/
literal|1024.
operator|/
literal|1024.
decl_stmt|;
comment|// Apply delete-by-docID now (delete-byDocID only
comment|// happens when an exception is hit processing that
comment|// doc, eg if analyzer has some problem w/ the text):
if|if
condition|(
name|pendingDeletes
operator|.
name|docIDs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|flushState
operator|.
name|deletedDocs
operator|=
operator|new
name|BitVector
argument_list|(
name|numDocsInRAM
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|delDocID
range|:
name|pendingDeletes
operator|.
name|docIDs
control|)
block|{
name|flushState
operator|.
name|deletedDocs
operator|.
name|set
argument_list|(
name|delDocID
argument_list|)
expr_stmt|;
block|}
name|pendingDeletes
operator|.
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|pendingDeletes
operator|.
name|docIDs
operator|.
name|size
argument_list|()
operator|*
name|BufferedDeletes
operator|.
name|BYTES_PER_DEL_DOCID
argument_list|)
expr_stmt|;
name|pendingDeletes
operator|.
name|docIDs
operator|.
name|clear
argument_list|()
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
literal|"flush postings as segment "
operator|+
name|flushState
operator|.
name|segmentName
operator|+
literal|" numDocs="
operator|+
name|numDocsInRAM
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aborting
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
literal|"flush: skip because aborting is set"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|flush
argument_list|(
name|flushState
argument_list|)
expr_stmt|;
name|pendingDeletes
operator|.
name|terms
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|SegmentInfo
name|newSegment
init|=
operator|new
name|SegmentInfo
argument_list|(
name|segment
argument_list|,
name|flushState
operator|.
name|numDocs
argument_list|,
name|directory
argument_list|,
literal|false
argument_list|,
name|flushState
operator|.
name|segmentCodecs
argument_list|,
name|fieldInfos
operator|.
name|asReadOnly
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"new segment has "
operator|+
operator|(
name|flushState
operator|.
name|deletedDocs
operator|==
literal|null
condition|?
literal|0
else|:
name|flushState
operator|.
name|deletedDocs
operator|.
name|count
argument_list|()
operator|)
operator|+
literal|" deleted docs"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"new segment has "
operator|+
operator|(
name|newSegment
operator|.
name|getHasVectors
argument_list|()
condition|?
literal|"vectors"
else|:
literal|"no vectors"
operator|)
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"flushedFiles="
operator|+
name|newSegment
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"flushed codecs="
operator|+
name|newSegment
operator|.
name|getSegmentCodecs
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|flushedDocCount
operator|+=
name|flushState
operator|.
name|numDocs
expr_stmt|;
specifier|final
name|BufferedDeletes
name|segmentDeletes
decl_stmt|;
if|if
condition|(
name|pendingDeletes
operator|.
name|queries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pendingDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|segmentDeletes
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|segmentDeletes
operator|=
name|pendingDeletes
expr_stmt|;
name|pendingDeletes
operator|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
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
specifier|final
name|double
name|newSegmentSizeNoStore
init|=
name|newSegment
operator|.
name|sizeInBytes
argument_list|(
literal|false
argument_list|)
operator|/
literal|1024.
operator|/
literal|1024.
decl_stmt|;
specifier|final
name|double
name|newSegmentSize
init|=
name|newSegment
operator|.
name|sizeInBytes
argument_list|(
literal|true
argument_list|)
operator|/
literal|1024.
operator|/
literal|1024.
decl_stmt|;
name|message
argument_list|(
literal|"flushed: segment="
operator|+
name|newSegment
operator|+
literal|" ramUsed="
operator|+
name|nf
operator|.
name|format
argument_list|(
name|startMBUsed
argument_list|)
operator|+
literal|" MB"
operator|+
literal|" newFlushedSize="
operator|+
name|nf
operator|.
name|format
argument_list|(
name|newSegmentSize
argument_list|)
operator|+
literal|" MB"
operator|+
literal|" ("
operator|+
name|nf
operator|.
name|format
argument_list|(
name|newSegmentSizeNoStore
argument_list|)
operator|+
literal|" MB w/o doc stores)"
operator|+
literal|" docs/MB="
operator|+
name|nf
operator|.
name|format
argument_list|(
name|flushedDocCount
operator|/
name|newSegmentSize
argument_list|)
operator|+
literal|" new/old="
operator|+
name|nf
operator|.
name|format
argument_list|(
literal|100.0
operator|*
name|newSegmentSizeNoStore
operator|/
name|startMBUsed
argument_list|)
operator|+
literal|"%"
argument_list|)
expr_stmt|;
block|}
name|doAfterFlush
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|FlushedSegment
argument_list|(
name|newSegment
argument_list|,
name|segmentDeletes
argument_list|,
name|flushState
operator|.
name|deletedDocs
argument_list|)
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
if|if
condition|(
name|segment
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|parent
operator|.
name|indexWriter
init|)
block|{
name|parent
operator|.
name|indexWriter
operator|.
name|deleter
operator|.
name|refresh
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
block|}
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Get current segment name we are writing. */
DECL|method|getSegment
name|String
name|getSegment
parameter_list|()
block|{
return|return
name|segment
return|;
block|}
DECL|method|bytesUsed
name|long
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
operator|+
name|pendingDeletes
operator|.
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|message
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|writer
operator|.
name|message
argument_list|(
literal|"DWPT: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
comment|/* Initial chunks size of the shared byte[] blocks used to      store postings data */
DECL|field|BYTE_BLOCK_NOT_MASK
specifier|final
specifier|static
name|int
name|BYTE_BLOCK_NOT_MASK
init|=
operator|~
name|BYTE_BLOCK_MASK
decl_stmt|;
comment|/* if you increase this, you must fix field cache impl for    * getTerms/getTermsIndex requires<= 32768 */
DECL|field|MAX_TERM_LENGTH_UTF8
specifier|final
specifier|static
name|int
name|MAX_TERM_LENGTH_UTF8
init|=
name|BYTE_BLOCK_SIZE
operator|-
literal|2
decl_stmt|;
comment|/* Initial chunks size of the shared int[] blocks used to      store postings data */
DECL|field|INT_BLOCK_SHIFT
specifier|final
specifier|static
name|int
name|INT_BLOCK_SHIFT
init|=
literal|13
decl_stmt|;
DECL|field|INT_BLOCK_SIZE
specifier|final
specifier|static
name|int
name|INT_BLOCK_SIZE
init|=
literal|1
operator|<<
name|INT_BLOCK_SHIFT
decl_stmt|;
DECL|field|INT_BLOCK_MASK
specifier|final
specifier|static
name|int
name|INT_BLOCK_MASK
init|=
name|INT_BLOCK_SIZE
operator|-
literal|1
decl_stmt|;
comment|/* Allocate another int[] from the shared pool */
DECL|method|getIntBlock
name|int
index|[]
name|getIntBlock
parameter_list|()
block|{
name|int
index|[]
name|b
init|=
operator|new
name|int
index|[
name|INT_BLOCK_SIZE
index|]
decl_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|INT_BLOCK_SIZE
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
DECL|method|recycleIntBlocks
name|void
name|recycleIntBlocks
parameter_list|(
name|int
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|length
operator|*
operator|(
name|INT_BLOCK_SIZE
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newPerDocWriteState
name|PerDocWriteState
name|newPerDocWriteState
parameter_list|(
name|int
name|codecId
parameter_list|)
block|{
assert|assert
name|segment
operator|!=
literal|null
assert|;
return|return
operator|new
name|PerDocWriteState
argument_list|(
name|infoStream
argument_list|,
name|directory
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|,
name|bytesUsed
argument_list|,
name|codecId
argument_list|)
return|;
block|}
DECL|method|setInfoStream
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
name|docState
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
block|}
end_class
end_unit
