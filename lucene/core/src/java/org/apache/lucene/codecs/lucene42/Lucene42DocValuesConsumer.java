begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene42
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
name|HashSet
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
name|NoSuchElementException
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
name|codecs
operator|.
name|CodecUtil
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|FieldInfo
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
name|IndexFileNames
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
name|SegmentWriteState
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
name|ByteArrayDataOutput
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
name|IntsRef
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|FST
operator|.
name|INPUT_TYPE
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
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
name|BlockPackedWriter
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
name|MonotonicBlockPackedWriter
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
operator|.
name|FormatAndBits
import|;
end_import
begin_comment
comment|/**  * Writer for {@link Lucene42DocValuesFormat}  */
end_comment
begin_class
DECL|class|Lucene42DocValuesConsumer
class|class
name|Lucene42DocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
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
DECL|field|NUMBER
specifier|static
specifier|final
name|byte
name|NUMBER
init|=
literal|0
decl_stmt|;
DECL|field|BYTES
specifier|static
specifier|final
name|byte
name|BYTES
init|=
literal|1
decl_stmt|;
DECL|field|FST
specifier|static
specifier|final
name|byte
name|FST
init|=
literal|2
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|DELTA_COMPRESSED
specifier|static
specifier|final
name|byte
name|DELTA_COMPRESSED
init|=
literal|0
decl_stmt|;
DECL|field|TABLE_COMPRESSED
specifier|static
specifier|final
name|byte
name|TABLE_COMPRESSED
init|=
literal|1
decl_stmt|;
DECL|field|UNCOMPRESSED
specifier|static
specifier|final
name|byte
name|UNCOMPRESSED
init|=
literal|2
decl_stmt|;
DECL|field|data
DECL|field|meta
specifier|final
name|IndexOutput
name|data
decl_stmt|,
name|meta
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|acceptableOverheadRatio
specifier|final
name|float
name|acceptableOverheadRatio
decl_stmt|;
DECL|method|Lucene42DocValuesConsumer
name|Lucene42DocValuesConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|dataCodec
parameter_list|,
name|String
name|dataExtension
parameter_list|,
name|String
name|metaCodec
parameter_list|,
name|String
name|metaExtension
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|acceptableOverheadRatio
operator|=
name|acceptableOverheadRatio
expr_stmt|;
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|String
name|dataName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|dataExtension
argument_list|)
decl_stmt|;
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|dataName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|String
name|metaName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|metaExtension
argument_list|)
decl_stmt|;
name|meta
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|meta
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_CURRENT
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addNumericField
specifier|public
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|NUMBER
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
comment|// TODO: more efficient?
name|HashSet
argument_list|<
name|Long
argument_list|>
name|uniqueValues
init|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|long
name|v
init|=
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|uniqueValues
operator|.
name|add
argument_list|(
name|v
argument_list|)
condition|)
block|{
if|if
condition|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|>
literal|256
condition|)
block|{
name|uniqueValues
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
condition|)
block|{
comment|// small number of unique values
specifier|final
name|int
name|bitsPerValue
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|FormatAndBits
name|formatAndBits
init|=
name|PackedInts
operator|.
name|fastestFormatAndBits
argument_list|(
name|maxDoc
argument_list|,
name|bitsPerValue
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
if|if
condition|(
name|formatAndBits
operator|.
name|bitsPerValue
operator|==
literal|8
operator|&&
name|minValue
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|UNCOMPRESSED
argument_list|)
expr_stmt|;
comment|// uncompressed
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|data
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|nv
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|TABLE_COMPRESSED
argument_list|)
expr_stmt|;
comment|// table-compressed
name|Long
index|[]
name|decode
init|=
name|uniqueValues
operator|.
name|toArray
argument_list|(
operator|new
name|Long
index|[
name|uniqueValues
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|encode
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|decode
operator|.
name|length
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
name|decode
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|.
name|writeLong
argument_list|(
name|decode
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|encode
operator|.
name|put
argument_list|(
name|decode
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|meta
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|formatAndBits
operator|.
name|format
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|writer
init|=
name|PackedInts
operator|.
name|getWriterNoHeader
argument_list|(
name|data
argument_list|,
name|formatAndBits
operator|.
name|format
argument_list|,
name|maxDoc
argument_list|,
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|,
name|PackedInts
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|encode
operator|.
name|get
argument_list|(
name|nv
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|DELTA_COMPRESSED
argument_list|)
expr_stmt|;
comment|// delta-compressed
name|meta
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
specifier|final
name|BlockPackedWriter
name|writer
init|=
operator|new
name|BlockPackedWriter
argument_list|(
name|data
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|nv
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
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
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// write EOF marker
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addBinaryField
specifier|public
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write the byte[] data
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|BYTES
argument_list|)
expr_stmt|;
name|int
name|minLength
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|maxLength
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|final
name|long
name|startFP
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|minLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLength
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeBytes
argument_list|(
name|v
operator|.
name|bytes
argument_list|,
name|v
operator|.
name|offset
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|meta
operator|.
name|writeLong
argument_list|(
name|startFP
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|startFP
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|minLength
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|maxLength
argument_list|)
expr_stmt|;
comment|// if minLength == maxLength, its a fixed-length byte[], we are done (the addresses are implicit)
comment|// otherwise, we need to record the length fields...
if|if
condition|(
name|minLength
operator|!=
name|maxLength
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
specifier|final
name|MonotonicBlockPackedWriter
name|writer
init|=
operator|new
name|MonotonicBlockPackedWriter
argument_list|(
name|data
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|long
name|addr
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|addr
operator|+=
name|v
operator|.
name|length
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeFST
specifier|private
name|void
name|writeFST
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|FST
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|PositiveIntOutputs
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Builder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|long
name|ord
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|v
argument_list|,
name|scratch
argument_list|)
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|ord
operator|++
expr_stmt|;
block|}
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|fst
operator|.
name|save
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVLong
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addSortedField
specifier|public
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write the ordinals as numerics
name|addNumericField
argument_list|(
name|field
argument_list|,
name|docToOrd
argument_list|)
expr_stmt|;
comment|// write the values as FST
name|writeFST
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|// note: this might not be the most efficient... but its fairly simple
annotation|@
name|Override
DECL|method|addSortedSetField
specifier|public
name|void
name|addSortedSetField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrdCount
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|ords
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write the ordinals as a binary field
name|addBinaryField
argument_list|(
name|field
argument_list|,
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|SortedSetIterator
argument_list|(
name|docToOrdCount
operator|.
name|iterator
argument_list|()
argument_list|,
name|ords
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// write the values as FST
name|writeFST
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|// per-document vint-encoded byte[]
DECL|class|SortedSetIterator
specifier|static
class|class
name|SortedSetIterator
implements|implements
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|buffer
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|10
index|]
decl_stmt|;
DECL|field|out
name|ByteArrayDataOutput
name|out
init|=
operator|new
name|ByteArrayDataOutput
argument_list|()
decl_stmt|;
DECL|field|ref
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|counts
specifier|final
name|Iterator
argument_list|<
name|Number
argument_list|>
name|counts
decl_stmt|;
DECL|field|ords
specifier|final
name|Iterator
argument_list|<
name|Number
argument_list|>
name|ords
decl_stmt|;
DECL|method|SortedSetIterator
name|SortedSetIterator
parameter_list|(
name|Iterator
argument_list|<
name|Number
argument_list|>
name|counts
parameter_list|,
name|Iterator
argument_list|<
name|Number
argument_list|>
name|ords
parameter_list|)
block|{
name|this
operator|.
name|counts
operator|=
name|counts
expr_stmt|;
name|this
operator|.
name|ords
operator|=
name|ords
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|counts
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|int
name|count
init|=
name|counts
operator|.
name|next
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|int
name|maxSize
init|=
name|count
operator|*
literal|9
decl_stmt|;
comment|// worst case
if|if
condition|(
name|maxSize
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|encodeValues
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
name|ref
operator|.
name|bytes
operator|=
name|buffer
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|out
operator|.
name|getPosition
argument_list|()
expr_stmt|;
return|return
name|ref
return|;
block|}
comment|// encodes count values to buffer
DECL|method|encodeValues
specifier|private
name|void
name|encodeValues
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|long
name|lastOrd
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|long
name|ord
init|=
name|ords
operator|.
name|next
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|ord
operator|-
name|lastOrd
argument_list|)
expr_stmt|;
name|lastOrd
operator|=
name|ord
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class
end_unit
