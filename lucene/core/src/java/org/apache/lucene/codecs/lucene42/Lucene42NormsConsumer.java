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
name|MathUtil
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
operator|.
name|FormatAndBits
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
comment|/**  * Writer for {@link Lucene42NormsFormat}  */
end_comment
begin_class
DECL|class|Lucene42NormsConsumer
class|class
name|Lucene42NormsConsumer
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
DECL|field|VERSION_GCD_COMPRESSION
specifier|static
specifier|final
name|int
name|VERSION_GCD_COMPRESSION
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_GCD_COMPRESSION
decl_stmt|;
DECL|field|NUMBER
specifier|static
specifier|final
name|byte
name|NUMBER
init|=
literal|0
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
DECL|field|GCD_COMPRESSED
specifier|static
specifier|final
name|byte
name|GCD_COMPRESSED
init|=
literal|3
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
DECL|method|Lucene42NormsConsumer
name|Lucene42NormsConsumer
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
name|long
name|gcd
init|=
literal|0
decl_stmt|;
comment|// TODO: more efficient?
name|HashSet
argument_list|<
name|Long
argument_list|>
name|uniqueValues
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|true
condition|)
block|{
name|uniqueValues
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
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
assert|assert
name|nv
operator|!=
literal|null
assert|;
specifier|final
name|long
name|v
init|=
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|gcd
operator|!=
literal|1
condition|)
block|{
if|if
condition|(
name|v
argument_list|<
name|Long
operator|.
name|MIN_VALUE
operator|/
literal|2
operator|||
name|v
argument_list|>
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|2
condition|)
block|{
comment|// in that case v - minValue might overflow and make the GCD computation return
comment|// wrong results. Since these extreme values are unlikely, we just discard
comment|// GCD computation for them
name|gcd
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
comment|// minValue needs to be set first
name|gcd
operator|=
name|MathUtil
operator|.
name|gcd
argument_list|(
name|gcd
argument_list|,
name|v
operator|-
name|minValue
argument_list|)
expr_stmt|;
block|}
block|}
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
operator|++
name|count
expr_stmt|;
block|}
assert|assert
name|count
operator|==
name|maxDoc
assert|;
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
argument_list|<>
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
operator|==
literal|null
condition|?
literal|0
else|:
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
elseif|else
if|if
condition|(
name|gcd
operator|!=
literal|0
operator|&&
name|gcd
operator|!=
literal|1
condition|)
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|GCD_COMPRESSED
argument_list|)
expr_stmt|;
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
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeLong
argument_list|(
name|gcd
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
name|long
name|value
init|=
name|nv
operator|==
literal|null
condition|?
literal|0
else|:
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
operator|(
name|value
operator|-
name|minValue
operator|)
operator|/
name|gcd
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
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
operator|==
literal|null
condition|?
literal|0
else|:
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class
end_unit
