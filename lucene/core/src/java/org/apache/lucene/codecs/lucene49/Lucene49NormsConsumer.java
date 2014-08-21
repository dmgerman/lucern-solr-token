begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|Arrays
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
name|Map
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
name|NormsConsumer
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
name|PackedInts
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
name|codecs
operator|.
name|lucene49
operator|.
name|Lucene49NormsFormat
operator|.
name|VERSION_CURRENT
import|;
end_import
begin_comment
comment|/**  * Writer for {@link Lucene49NormsFormat}  */
end_comment
begin_class
DECL|class|Lucene49NormsConsumer
class|class
name|Lucene49NormsConsumer
extends|extends
name|NormsConsumer
block|{
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
DECL|field|CONST_COMPRESSED
specifier|static
specifier|final
name|byte
name|CONST_COMPRESSED
init|=
literal|2
decl_stmt|;
DECL|field|UNCOMPRESSED
specifier|static
specifier|final
name|byte
name|UNCOMPRESSED
init|=
literal|3
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|16384
decl_stmt|;
DECL|field|data
DECL|field|meta
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
DECL|method|Lucene49NormsConsumer
name|Lucene49NormsConsumer
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
parameter_list|)
throws|throws
name|IOException
block|{
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
comment|// we explicitly use only certain bits per value and a specified format, so we statically check this will work
static|static
block|{
assert|assert
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED_SINGLE_BLOCK
operator|.
name|isSupported
argument_list|(
literal|1
argument_list|)
assert|;
assert|assert
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED_SINGLE_BLOCK
operator|.
name|isSupported
argument_list|(
literal|2
argument_list|)
assert|;
assert|assert
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED_SINGLE_BLOCK
operator|.
name|isSupported
argument_list|(
literal|4
argument_list|)
assert|;
block|}
annotation|@
name|Override
DECL|method|addNormsField
specifier|public
name|void
name|addNormsField
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
name|NormMap
name|uniqueValues
init|=
operator|new
name|NormMap
argument_list|()
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
if|if
condition|(
name|nv
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal norms data for field "
operator|+
name|field
operator|.
name|name
operator|+
literal|", got null for value: "
operator|+
name|count
argument_list|)
throw|;
block|}
specifier|final
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
operator|++
name|count
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|!=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal norms data for field "
operator|+
name|field
operator|.
name|name
operator|+
literal|", expected "
operator|+
name|maxDoc
operator|+
literal|" values, got "
operator|+
name|count
argument_list|)
throw|;
block|}
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
operator|&&
name|uniqueValues
operator|.
name|size
operator|==
literal|1
condition|)
block|{
comment|// 0 bpv
name|meta
operator|.
name|writeByte
argument_list|(
name|CONST_COMPRESSED
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
condition|)
block|{
comment|// small number of unique values: this is the typical case:
comment|// we only use bpv=1,2,4,8
name|PackedInts
operator|.
name|Format
name|format
init|=
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED_SINGLE_BLOCK
decl_stmt|;
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
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|==
literal|3
condition|)
block|{
name|bitsPerValue
operator|=
literal|4
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|>
literal|4
condition|)
block|{
name|bitsPerValue
operator|=
literal|8
expr_stmt|;
block|}
if|if
condition|(
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
comment|// uncompressed byte[]
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
name|nv
operator|==
literal|null
condition|?
literal|0
else|:
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
name|data
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|long
index|[]
name|decode
init|=
name|uniqueValues
operator|.
name|getDecodeTable
argument_list|()
decl_stmt|;
comment|// upgrade to power of two sized array
name|int
name|size
init|=
literal|1
operator|<<
name|bitsPerValue
decl_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|size
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
block|}
for|for
control|(
name|int
name|i
init|=
name|decode
operator|.
name|length
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|data
operator|.
name|writeVInt
argument_list|(
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
name|format
argument_list|,
name|maxDoc
argument_list|,
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
name|uniqueValues
operator|.
name|getOrd
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
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|data
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
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|meta
argument_list|)
expr_stmt|;
comment|// write checksum
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// write checksum
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
name|meta
operator|=
name|data
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// specialized deduplication of long->ord for norms: 99.99999% of the time this will be a single-byte range.
DECL|class|NormMap
specifier|static
class|class
name|NormMap
block|{
comment|// we use short: at most we will add 257 values to this map before its rejected as too big above.
DECL|field|singleByteRange
specifier|final
name|short
index|[]
name|singleByteRange
init|=
operator|new
name|short
index|[
literal|256
index|]
decl_stmt|;
DECL|field|other
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Short
argument_list|>
name|other
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Short
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|singleByteRange
argument_list|,
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** adds an item to the mapping. returns true if actually added */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|long
name|l
parameter_list|)
block|{
assert|assert
name|size
operator|<=
literal|256
assert|;
comment|// once we add> 256 values, we nullify the map in addNumericField and don't use this strategy
if|if
condition|(
name|l
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|l
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|l
operator|+
literal|128
argument_list|)
decl_stmt|;
name|short
name|previous
init|=
name|singleByteRange
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|previous
operator|<
literal|0
condition|)
block|{
name|singleByteRange
index|[
name|index
index|]
operator|=
operator|(
name|short
operator|)
name|size
expr_stmt|;
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|other
operator|.
name|containsKey
argument_list|(
name|l
argument_list|)
condition|)
block|{
name|other
operator|.
name|put
argument_list|(
name|l
argument_list|,
operator|(
name|short
operator|)
name|size
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/** gets the ordinal for a previously added item */
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|long
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|l
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|l
operator|+
literal|128
argument_list|)
decl_stmt|;
return|return
name|singleByteRange
index|[
name|index
index|]
return|;
block|}
else|else
block|{
comment|// NPE if something is screwed up
return|return
name|other
operator|.
name|get
argument_list|(
name|l
argument_list|)
return|;
block|}
block|}
comment|/** retrieves the ordinal table for previously added items */
DECL|method|getDecodeTable
specifier|public
name|long
index|[]
name|getDecodeTable
parameter_list|()
block|{
name|long
name|decode
index|[]
init|=
operator|new
name|long
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
name|singleByteRange
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|short
name|s
init|=
name|singleByteRange
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|>=
literal|0
condition|)
block|{
name|decode
index|[
name|s
index|]
operator|=
name|i
operator|-
literal|128
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Short
argument_list|>
name|entry
range|:
name|other
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|decode
index|[
name|entry
operator|.
name|getValue
argument_list|()
index|]
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
return|return
name|decode
return|;
block|}
block|}
block|}
end_class
end_unit
