begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.lucene40.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|values
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
name|List
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
name|MergeState
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|BytesReaderBase
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|BytesSortedSourceBase
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|DerefBytesWriterBase
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|SortedBytesMergeUtils
operator|.
name|MergeContext
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|SortedBytesMergeUtils
operator|.
name|SortedSourceSlice
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
name|values
operator|.
name|IndexDocValues
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
name|values
operator|.
name|ValueType
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
name|values
operator|.
name|IndexDocValues
operator|.
name|SortedSource
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
name|Counter
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
name|IOUtils
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
import|;
end_import
begin_comment
comment|// Stores variable-length byte[] by deref, ie when two docs
end_comment
begin_comment
comment|// have the same value, they store only 1 byte[] and both
end_comment
begin_comment
comment|// docs reference that single source
end_comment
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|VarSortedBytesImpl
specifier|final
class|class
name|VarSortedBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"VarDerefBytes"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|class|Writer
specifier|final
specifier|static
class|class
name|Writer
extends|extends
name|DerefBytesWriterBase
block|{
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|IndexDocValues
index|[]
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|MergeContext
name|ctx
init|=
name|SortedBytesMergeUtils
operator|.
name|init
argument_list|(
name|ValueType
operator|.
name|BYTES_VAR_SORTED
argument_list|,
name|docValues
argument_list|,
name|comp
argument_list|,
name|mergeState
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SortedSourceSlice
argument_list|>
name|slices
init|=
name|SortedBytesMergeUtils
operator|.
name|buildSlices
argument_list|(
name|mergeState
argument_list|,
name|docValues
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|offsets
operator|=
operator|new
name|long
index|[
literal|1
index|]
expr_stmt|;
specifier|final
name|int
name|maxOrd
init|=
name|SortedBytesMergeUtils
operator|.
name|mergeRecords
argument_list|(
name|ctx
argument_list|,
name|datOut
argument_list|,
name|slices
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|offsets
init|=
name|ctx
operator|.
name|offsets
decl_stmt|;
name|maxBytes
operator|=
name|offsets
index|[
name|maxOrd
operator|-
literal|1
index|]
expr_stmt|;
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
name|idxOut
operator|.
name|writeLong
argument_list|(
name|maxBytes
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|offsetWriter
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|maxOrd
operator|+
literal|1
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxBytes
argument_list|)
argument_list|)
decl_stmt|;
name|offsetWriter
operator|.
name|add
argument_list|(
literal|0
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
name|maxOrd
condition|;
name|i
operator|++
control|)
block|{
name|offsetWriter
operator|.
name|add
argument_list|(
name|offsets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|offsetWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|ordsWriter
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|ctx
operator|.
name|docToEntry
operator|.
name|length
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxOrd
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|SortedSourceSlice
name|slice
range|:
name|slices
control|)
block|{
name|slice
operator|.
name|writeOrds
argument_list|(
name|ordsWriter
argument_list|)
expr_stmt|;
block|}
name|ordsWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|releaseResources
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|getIndexOut
argument_list|()
argument_list|,
name|getDataOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|getIndexOut
argument_list|()
argument_list|,
name|getDataOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|checkSize
specifier|protected
name|void
name|checkSize
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
comment|// allow var bytes sizes
block|}
comment|// Important that we get docCount, in case there were
comment|// some last docs that we didn't see
annotation|@
name|Override
DECL|method|finishInternal
specifier|public
name|void
name|finishInternal
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|fillDefault
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
name|long
name|offset
init|=
literal|0
decl_stmt|;
specifier|final
name|int
index|[]
name|index
init|=
operator|new
name|int
index|[
name|count
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|sortedEntries
init|=
name|hash
operator|.
name|sort
argument_list|(
name|comp
argument_list|)
decl_stmt|;
comment|// total bytes of data
name|idxOut
operator|.
name|writeLong
argument_list|(
name|maxBytes
argument_list|)
expr_stmt|;
name|PackedInts
operator|.
name|Writer
name|offsetWriter
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|count
operator|+
literal|1
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxBytes
argument_list|)
argument_list|)
decl_stmt|;
comment|// first dump bytes data, recording index& write offset as
comment|// we go
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
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
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|e
init|=
name|sortedEntries
index|[
name|i
index|]
decl_stmt|;
name|offsetWriter
operator|.
name|add
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|index
index|[
name|e
index|]
operator|=
name|i
expr_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|hash
operator|.
name|get
argument_list|(
name|e
argument_list|,
name|spare
argument_list|)
decl_stmt|;
comment|// TODO: we could prefix code...
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
comment|// write sentinel
name|offsetWriter
operator|.
name|add
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|offsetWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|// write index
name|writeIndex
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|count
argument_list|,
name|index
argument_list|,
name|docToEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|BytesReaderBase
block|{
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
decl_stmt|;
DECL|method|Reader
name|Reader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|ValueType
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
operator|.
name|IndexDocValues
operator|.
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|VarSortedSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DirectSortedSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|comparator
argument_list|,
name|type
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|VarSortedSource
specifier|private
specifier|static
specifier|final
class|class
name|VarSortedSource
extends|extends
name|BytesSortedSourceBase
block|{
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|VarSortedSource
name|VarSortedSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|,
name|comp
argument_list|,
name|idxIn
operator|.
name|readLong
argument_list|()
argument_list|,
name|ValueType
operator|.
name|BYTES_VAR_SORTED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|valueCount
operator|=
name|ordToOffsetIndex
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
comment|// the last value here is just a dummy value to get the length of the last value
name|closeIndexInput
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
specifier|final
name|long
name|offset
init|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
specifier|final
name|long
name|nextOffset
init|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
literal|1
operator|+
name|ord
argument_list|)
decl_stmt|;
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
name|offset
argument_list|,
call|(
name|int
call|)
argument_list|(
name|nextOffset
operator|-
name|offset
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
block|}
DECL|class|DirectSortedSource
specifier|private
specifier|static
specifier|final
class|class
name|DirectSortedSource
extends|extends
name|SortedSource
block|{
DECL|field|docToOrdIndex
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|docToOrdIndex
decl_stmt|;
DECL|field|ordToOffsetIndex
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|ordToOffsetIndex
decl_stmt|;
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|basePointer
specifier|private
specifier|final
name|long
name|basePointer
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|DirectSortedSource
name|DirectSortedSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|,
name|ValueType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|type
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
name|idxIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|ordToOffsetIndex
operator|=
name|PackedInts
operator|.
name|getDirectReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|valueCount
operator|=
name|ordToOffsetIndex
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
comment|// the last value here is just a dummy value to get the length of the last value
comment|// advance this iterator to the end and clone the stream once it points to the docToOrdIndex header
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
name|docToOrdIndex
operator|=
name|PackedInts
operator|.
name|getDirectReader
argument_list|(
operator|(
name|IndexInput
operator|)
name|idxIn
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
comment|// read the ords in to prevent too many random disk seeks
name|basePointer
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|docToOrdIndex
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasPackedDocToOrd
specifier|public
name|boolean
name|hasPackedDocToOrd
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getDocToOrd
specifier|public
name|PackedInts
operator|.
name|Reader
name|getDocToOrd
parameter_list|()
block|{
return|return
name|docToOrdIndex
return|;
block|}
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
try|try
block|{
specifier|final
name|long
name|offset
init|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
comment|// 1+ord is safe because we write a sentinel at the end
specifier|final
name|long
name|nextOffset
init|=
name|ordToOffsetIndex
operator|.
name|get
argument_list|(
literal|1
operator|+
name|ord
argument_list|)
decl_stmt|;
name|datIn
operator|.
name|seek
argument_list|(
name|basePointer
operator|+
name|offset
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|nextOffset
operator|-
name|offset
argument_list|)
decl_stmt|;
name|bytesRef
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
block|}
block|}
end_class
end_unit
