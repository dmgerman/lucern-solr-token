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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|util
operator|.
name|UnicodeUtil
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
name|Collections
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
name|ArrayList
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
name|Iterator
import|;
end_import
begin_class
DECL|class|FreqProxTermsWriter
specifier|final
class|class
name|FreqProxTermsWriter
extends|extends
name|TermsHashConsumer
block|{
DECL|method|FreqProxTermsWriter
name|FreqProxTermsWriter
parameter_list|()
block|{
name|streamCount
operator|=
literal|2
expr_stmt|;
block|}
DECL|method|addThread
specifier|public
name|TermsHashConsumerPerThread
name|addThread
parameter_list|(
name|TermsHashPerThread
name|perThread
parameter_list|)
block|{
return|return
operator|new
name|FreqProxTermsWriterPerThread
argument_list|(
name|perThread
argument_list|)
return|;
block|}
DECL|method|createPostings
name|void
name|createPostings
parameter_list|(
name|RawPostingList
index|[]
name|postings
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|count
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|postings
index|[
name|i
index|]
operator|=
operator|new
name|PostingList
argument_list|()
expr_stmt|;
block|}
DECL|method|compareText
specifier|private
specifier|static
name|int
name|compareText
parameter_list|(
specifier|final
name|char
index|[]
name|text1
parameter_list|,
name|int
name|pos1
parameter_list|,
specifier|final
name|char
index|[]
name|text2
parameter_list|,
name|int
name|pos2
parameter_list|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|char
name|c1
init|=
name|text1
index|[
name|pos1
operator|++
index|]
decl_stmt|;
specifier|final
name|char
name|c2
init|=
name|text2
index|[
name|pos2
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|c1
operator|!=
name|c2
condition|)
block|{
if|if
condition|(
literal|0xffff
operator|==
name|c2
condition|)
return|return
literal|1
return|;
elseif|else
if|if
condition|(
literal|0xffff
operator|==
name|c1
condition|)
return|return
operator|-
literal|1
return|;
else|else
return|return
name|c1
operator|-
name|c2
return|;
block|}
elseif|else
if|if
condition|(
literal|0xffff
operator|==
name|c1
condition|)
return|return
literal|0
return|;
block|}
block|}
DECL|method|closeDocStore
name|void
name|closeDocStore
parameter_list|(
name|DocumentsWriter
operator|.
name|FlushState
name|state
parameter_list|)
block|{}
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{}
comment|// TODO: would be nice to factor out morme of this, eg the
comment|// FreqProxFieldMergeState, and code to visit all Fields
comment|// under the same FieldInfo together, up into TermsHash*.
comment|// Other writers would presumably share alot of this...
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Map
name|threadsAndFields
parameter_list|,
specifier|final
name|DocumentsWriter
operator|.
name|FlushState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Gather all FieldData's that have postings, across all
comment|// ThreadStates
name|List
name|allFields
init|=
operator|new
name|ArrayList
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
while|while
condition|(
name|fieldsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FreqProxTermsWriterPerField
name|perField
init|=
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|fieldsIt
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|perField
operator|.
name|termsHashPerField
operator|.
name|numPostings
operator|>
literal|0
condition|)
name|allFields
operator|.
name|add
argument_list|(
name|perField
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Sort by field name
name|Collections
operator|.
name|sort
argument_list|(
name|allFields
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numAllFields
init|=
name|allFields
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|TermInfosWriter
name|termsOut
init|=
operator|new
name|TermInfosWriter
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentName
argument_list|,
name|fieldInfos
argument_list|,
name|state
operator|.
name|docWriter
operator|.
name|writer
operator|.
name|getTermIndexInterval
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|freqOut
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|FREQ_EXTENSION
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexOutput
name|proxOut
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|PROX_EXTENSION
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DefaultSkipListWriter
name|skipListWriter
init|=
operator|new
name|DefaultSkipListWriter
argument_list|(
name|termsOut
operator|.
name|skipInterval
argument_list|,
name|termsOut
operator|.
name|maxSkipLevels
argument_list|,
name|state
operator|.
name|numDocsInRAM
argument_list|,
name|freqOut
argument_list|,
name|proxOut
argument_list|)
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|numAllFields
condition|)
block|{
specifier|final
name|FieldInfo
name|fieldInfo
init|=
operator|(
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|allFields
operator|.
name|get
argument_list|(
name|start
argument_list|)
operator|)
operator|.
name|fieldInfo
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
name|fieldInfo
operator|.
name|name
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|end
operator|<
name|numAllFields
operator|&&
operator|(
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|allFields
operator|.
name|get
argument_list|(
name|end
argument_list|)
operator|)
operator|.
name|fieldInfo
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
name|end
operator|++
expr_stmt|;
name|FreqProxTermsWriterPerField
index|[]
name|fields
init|=
operator|new
name|FreqProxTermsWriterPerField
index|[
name|end
operator|-
name|start
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
operator|-
name|start
index|]
operator|=
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|allFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// Aggregate the storePayload as seen by the same
comment|// field across multiple threads
name|fieldInfo
operator|.
name|storePayloads
operator||=
name|fields
index|[
name|i
operator|-
name|start
index|]
operator|.
name|hasPayloads
expr_stmt|;
block|}
comment|// If this field has postings then add them to the
comment|// segment
name|appendPostings
argument_list|(
name|state
argument_list|,
name|fields
argument_list|,
name|termsOut
argument_list|,
name|freqOut
argument_list|,
name|proxOut
argument_list|,
name|skipListWriter
argument_list|)
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermsHashPerField
name|perField
init|=
name|fields
index|[
name|i
index|]
operator|.
name|termsHashPerField
decl_stmt|;
name|int
name|numPostings
init|=
name|perField
operator|.
name|numPostings
decl_stmt|;
name|perField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perField
operator|.
name|shrinkHash
argument_list|(
name|numPostings
argument_list|)
expr_stmt|;
block|}
name|start
operator|=
name|end
expr_stmt|;
block|}
name|it
operator|=
name|threadsAndFields
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
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
name|FreqProxTermsWriterPerThread
name|perThread
init|=
operator|(
name|FreqProxTermsWriterPerThread
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|reset
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|freqOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|proxOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|termsOut
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Record all files we have flushed
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|FIELD_INFOS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|FREQ_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|PROX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|TERMS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|segmentFileName
argument_list|(
name|IndexFileNames
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|copyByteBuffer
specifier|final
name|byte
index|[]
name|copyByteBuffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
comment|/** Copy numBytes from srcIn to destIn */
DECL|method|copyBytes
name|void
name|copyBytes
parameter_list|(
name|IndexInput
name|srcIn
parameter_list|,
name|IndexOutput
name|destIn
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: we could do this more efficiently (save a copy)
comment|// because it's always from a ByteSliceReader ->
comment|// IndexOutput
while|while
condition|(
name|numBytes
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|chunk
decl_stmt|;
if|if
condition|(
name|numBytes
operator|>
literal|4096
condition|)
name|chunk
operator|=
literal|4096
expr_stmt|;
else|else
name|chunk
operator|=
operator|(
name|int
operator|)
name|numBytes
expr_stmt|;
name|srcIn
operator|.
name|readBytes
argument_list|(
name|copyByteBuffer
argument_list|,
literal|0
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|destIn
operator|.
name|writeBytes
argument_list|(
name|copyByteBuffer
argument_list|,
literal|0
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|numBytes
operator|-=
name|chunk
expr_stmt|;
block|}
block|}
comment|/* Walk through all unique text tokens (Posting    * instances) found in this field and serialize them    * into a single RAM segment. */
DECL|method|appendPostings
name|void
name|appendPostings
parameter_list|(
specifier|final
name|DocumentsWriter
operator|.
name|FlushState
name|flushState
parameter_list|,
name|FreqProxTermsWriterPerField
index|[]
name|fields
parameter_list|,
name|TermInfosWriter
name|termsOut
parameter_list|,
name|IndexOutput
name|freqOut
parameter_list|,
name|IndexOutput
name|proxOut
parameter_list|,
name|DefaultSkipListWriter
name|skipListWriter
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|int
name|fieldNumber
init|=
name|fields
index|[
literal|0
index|]
operator|.
name|fieldInfo
operator|.
name|number
decl_stmt|;
name|int
name|numFields
init|=
name|fields
operator|.
name|length
decl_stmt|;
specifier|final
name|FreqProxFieldMergeState
index|[]
name|mergeStates
init|=
operator|new
name|FreqProxFieldMergeState
index|[
name|numFields
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
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|FreqProxFieldMergeState
name|fms
init|=
name|mergeStates
index|[
name|i
index|]
operator|=
operator|new
name|FreqProxFieldMergeState
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
decl_stmt|;
assert|assert
name|fms
operator|.
name|field
operator|.
name|fieldInfo
operator|==
name|fields
index|[
literal|0
index|]
operator|.
name|fieldInfo
assert|;
comment|// Should always be true
name|boolean
name|result
init|=
name|fms
operator|.
name|nextTerm
argument_list|()
decl_stmt|;
assert|assert
name|result
assert|;
block|}
specifier|final
name|int
name|skipInterval
init|=
name|termsOut
operator|.
name|skipInterval
decl_stmt|;
specifier|final
name|boolean
name|currentFieldStorePayloads
init|=
name|fields
index|[
literal|0
index|]
operator|.
name|fieldInfo
operator|.
name|storePayloads
decl_stmt|;
name|FreqProxFieldMergeState
index|[]
name|termStates
init|=
operator|new
name|FreqProxFieldMergeState
index|[
name|numFields
index|]
decl_stmt|;
while|while
condition|(
name|numFields
operator|>
literal|0
condition|)
block|{
comment|// Get the next term to merge
name|termStates
index|[
literal|0
index|]
operator|=
name|mergeStates
index|[
literal|0
index|]
expr_stmt|;
name|int
name|numToMerge
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
index|[]
name|text
init|=
name|mergeStates
index|[
name|i
index|]
operator|.
name|text
decl_stmt|;
specifier|final
name|int
name|textOffset
init|=
name|mergeStates
index|[
name|i
index|]
operator|.
name|textOffset
decl_stmt|;
specifier|final
name|int
name|cmp
init|=
name|compareText
argument_list|(
name|text
argument_list|,
name|textOffset
argument_list|,
name|termStates
index|[
literal|0
index|]
operator|.
name|text
argument_list|,
name|termStates
index|[
literal|0
index|]
operator|.
name|textOffset
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|termStates
index|[
literal|0
index|]
operator|=
name|mergeStates
index|[
name|i
index|]
expr_stmt|;
name|numToMerge
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
name|termStates
index|[
name|numToMerge
operator|++
index|]
operator|=
name|mergeStates
index|[
name|i
index|]
expr_stmt|;
block|}
name|int
name|df
init|=
literal|0
decl_stmt|;
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
specifier|final
name|char
index|[]
name|text
init|=
name|termStates
index|[
literal|0
index|]
operator|.
name|text
decl_stmt|;
specifier|final
name|int
name|start
init|=
name|termStates
index|[
literal|0
index|]
operator|.
name|textOffset
decl_stmt|;
name|long
name|freqPointer
init|=
name|freqOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|proxPointer
init|=
name|proxOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|skipListWriter
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
comment|// Now termStates has numToMerge FieldMergeStates
comment|// which all share the same term.  Now we must
comment|// interleave the docID streams.
while|while
condition|(
name|numToMerge
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
operator|++
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
name|skipListWriter
operator|.
name|setSkipData
argument_list|(
name|lastDoc
argument_list|,
name|currentFieldStorePayloads
argument_list|,
name|lastPayloadLength
argument_list|)
expr_stmt|;
name|skipListWriter
operator|.
name|bufferSkip
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
name|FreqProxFieldMergeState
name|minState
init|=
name|termStates
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numToMerge
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|termStates
index|[
name|i
index|]
operator|.
name|docID
operator|<
name|minState
operator|.
name|docID
condition|)
name|minState
operator|=
name|termStates
index|[
name|i
index|]
expr_stmt|;
specifier|final
name|int
name|doc
init|=
name|minState
operator|.
name|docID
decl_stmt|;
specifier|final
name|int
name|termDocFreq
init|=
name|minState
operator|.
name|termFreq
decl_stmt|;
assert|assert
name|doc
operator|<
name|flushState
operator|.
name|numDocsInRAM
assert|;
assert|assert
name|doc
operator|>
name|lastDoc
operator|||
name|df
operator|==
literal|1
assert|;
specifier|final
name|int
name|newDocCode
init|=
operator|(
name|doc
operator|-
name|lastDoc
operator|)
operator|<<
literal|1
decl_stmt|;
name|lastDoc
operator|=
name|doc
expr_stmt|;
specifier|final
name|ByteSliceReader
name|prox
init|=
name|minState
operator|.
name|prox
decl_stmt|;
comment|// Carefully copy over the prox + payload info,
comment|// changing the format to match Lucene's segment
comment|// format.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|termDocFreq
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
name|prox
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentFieldStorePayloads
condition|)
block|{
specifier|final
name|int
name|payloadLength
decl_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// This position has a payload
name|payloadLength
operator|=
name|prox
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
else|else
name|payloadLength
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|code
operator||
literal|1
argument_list|)
expr_stmt|;
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
block|}
else|else
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|code
operator|&
operator|(
operator|~
literal|1
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
name|copyBytes
argument_list|(
name|prox
argument_list|,
name|proxOut
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
literal|0
operator|==
operator|(
name|code
operator|&
literal|1
operator|)
assert|;
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|code
operator|>>
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|1
operator|==
name|termDocFreq
condition|)
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|newDocCode
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|newDocCode
argument_list|)
expr_stmt|;
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|minState
operator|.
name|nextDoc
argument_list|()
condition|)
block|{
comment|// Remove from termStates
name|int
name|upto
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
name|numToMerge
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|termStates
index|[
name|i
index|]
operator|!=
name|minState
condition|)
name|termStates
index|[
name|upto
operator|++
index|]
operator|=
name|termStates
index|[
name|i
index|]
expr_stmt|;
name|numToMerge
operator|--
expr_stmt|;
assert|assert
name|upto
operator|==
name|numToMerge
assert|;
comment|// Advance this state to the next term
if|if
condition|(
operator|!
name|minState
operator|.
name|nextTerm
argument_list|()
condition|)
block|{
comment|// OK, no more terms, so remove from mergeStates
comment|// as well
name|upto
operator|=
literal|0
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
name|numFields
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|mergeStates
index|[
name|i
index|]
operator|!=
name|minState
condition|)
name|mergeStates
index|[
name|upto
operator|++
index|]
operator|=
name|mergeStates
index|[
name|i
index|]
expr_stmt|;
name|numFields
operator|--
expr_stmt|;
assert|assert
name|upto
operator|==
name|numFields
assert|;
block|}
block|}
block|}
assert|assert
name|df
operator|>
literal|0
assert|;
comment|// Done merging this term
name|long
name|skipPointer
init|=
name|skipListWriter
operator|.
name|writeSkip
argument_list|(
name|freqOut
argument_list|)
decl_stmt|;
comment|// Write term
name|termInfo
operator|.
name|set
argument_list|(
name|df
argument_list|,
name|freqPointer
argument_list|,
name|proxPointer
argument_list|,
call|(
name|int
call|)
argument_list|(
name|skipPointer
operator|-
name|freqPointer
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: we could do this incrementally
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|termsUTF8
argument_list|)
expr_stmt|;
comment|// TODO: we could save O(n) re-scan of the term by
comment|// computing the shared prefix with the last term
comment|// while during the UTF8 encoding
name|termsOut
operator|.
name|add
argument_list|(
name|fieldNumber
argument_list|,
name|termsUTF8
operator|.
name|result
argument_list|,
name|termsUTF8
operator|.
name|length
argument_list|,
name|termInfo
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|termInfo
specifier|private
specifier|final
name|TermInfo
name|termInfo
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
comment|// minimize consing
DECL|field|termsUTF8
specifier|final
name|UnicodeUtil
operator|.
name|UTF8Result
name|termsUTF8
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
decl_stmt|;
DECL|method|files
name|void
name|files
parameter_list|(
name|Collection
name|files
parameter_list|)
block|{}
DECL|class|PostingList
specifier|static
specifier|final
class|class
name|PostingList
extends|extends
name|RawPostingList
block|{
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
comment|// # times this term occurs in the current doc
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
comment|// Last docID where this term occurred
DECL|field|lastDocCode
name|int
name|lastDocCode
decl_stmt|;
comment|// Code for prior doc
DECL|field|lastPosition
name|int
name|lastPosition
decl_stmt|;
comment|// Last position where this term occurred
block|}
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|RawPostingList
operator|.
name|BYTES_SIZE
operator|+
literal|4
operator|*
name|DocumentsWriter
operator|.
name|INT_NUM_BYTE
return|;
block|}
block|}
end_class
end_unit
