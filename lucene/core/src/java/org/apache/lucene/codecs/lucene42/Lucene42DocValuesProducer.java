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
name|DocValuesProducer
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
name|BinaryDocValues
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
name|CorruptIndexException
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
name|FieldInfos
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
name|NumericDocValues
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
name|SegmentReadState
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
name|SortedDocValues
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
name|BytesRefFSTEnum
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
name|BytesRefFSTEnum
operator|.
name|InputOutput
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
name|Arc
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
name|BytesReader
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
name|BlockPackedReader
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
name|MonotonicBlockPackedReader
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
comment|/**  * Reader for {@link Lucene42DocValuesFormat}  */
end_comment
begin_class
DECL|class|Lucene42DocValuesProducer
class|class
name|Lucene42DocValuesProducer
extends|extends
name|DocValuesProducer
block|{
comment|// metadata maps (just file pointers and minimal stuff)
DECL|field|numerics
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|numerics
decl_stmt|;
DECL|field|binaries
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
name|binaries
decl_stmt|;
DECL|field|fsts
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|FSTEntry
argument_list|>
name|fsts
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|IndexInput
name|data
decl_stmt|;
comment|// ram instances we have already loaded
DECL|field|numericInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericDocValues
argument_list|>
name|numericInstances
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|binaryInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryDocValues
argument_list|>
name|binaryInstances
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|BinaryDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fstInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|FST
argument_list|<
name|Long
argument_list|>
argument_list|>
name|fstInstances
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|FST
argument_list|<
name|Long
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|Lucene42DocValuesProducer
name|Lucene42DocValuesProducer
parameter_list|(
name|SegmentReadState
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
comment|// read in the entries from the metadata file.
name|IndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|metaCodec
argument_list|,
name|Lucene42DocValuesConsumer
operator|.
name|VERSION_START
argument_list|,
name|Lucene42DocValuesConsumer
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|numerics
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
argument_list|()
expr_stmt|;
name|binaries
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
argument_list|()
expr_stmt|;
name|fsts
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|FSTEntry
argument_list|>
argument_list|()
expr_stmt|;
name|readFields
argument_list|(
name|in
argument_list|,
name|state
operator|.
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
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
name|openInput
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
name|checkHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|Lucene42DocValuesConsumer
operator|.
name|VERSION_START
argument_list|,
name|Lucene42DocValuesConsumer
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields
specifier|private
name|void
name|readFields
parameter_list|(
name|IndexInput
name|meta
parameter_list|,
name|FieldInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|fieldNumber
init|=
name|meta
operator|.
name|readVInt
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldNumber
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|fieldType
init|=
name|meta
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
name|Lucene42DocValuesConsumer
operator|.
name|NUMBER
condition|)
block|{
name|NumericEntry
name|entry
init|=
operator|new
name|NumericEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|tableized
operator|=
name|meta
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
expr_stmt|;
name|entry
operator|.
name|packedIntsVersion
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldType
operator|==
name|Lucene42DocValuesConsumer
operator|.
name|BYTES
condition|)
block|{
name|BinaryEntry
name|entry
init|=
operator|new
name|BinaryEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|numBytes
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|minLength
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|maxLength
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|minLength
operator|!=
name|entry
operator|.
name|maxLength
condition|)
block|{
name|entry
operator|.
name|packedIntsVersion
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|blockSize
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
name|binaries
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldType
operator|==
name|Lucene42DocValuesConsumer
operator|.
name|FST
condition|)
block|{
name|FSTEntry
name|entry
init|=
operator|new
name|FSTEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|numOrds
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fsts
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid entry type: "
operator|+
name|fieldType
operator|+
literal|", input="
operator|+
name|meta
argument_list|)
throw|;
block|}
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
specifier|synchronized
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|instance
init|=
name|numericInstances
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
name|loadNumeric
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|numericInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|loadNumeric
specifier|private
name|NumericDocValues
name|loadNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericEntry
name|entry
init|=
name|numerics
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|tableized
condition|)
block|{
name|int
name|size
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
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
name|decode
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|decode
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|bitsPerValue
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
init|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|data
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
name|maxDoc
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|decode
index|[
operator|(
name|int
operator|)
name|reader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
index|]
return|;
block|}
block|}
return|;
block|}
else|else
block|{
specifier|final
name|int
name|blockSize
init|=
name|data
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|BlockPackedReader
name|reader
init|=
operator|new
name|BlockPackedReader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
name|blockSize
argument_list|,
name|maxDoc
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|reader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
specifier|synchronized
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|instance
init|=
name|binaryInstances
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
name|loadBinary
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|binaryInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|loadBinary
specifier|private
name|BinaryDocValues
name|loadBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryEntry
name|entry
init|=
name|binaries
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
assert|assert
name|entry
operator|.
name|numBytes
operator|<
name|Integer
operator|.
name|MAX_VALUE
assert|;
comment|// nocommit
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|entry
operator|.
name|numBytes
index|]
decl_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|minLength
operator|==
name|entry
operator|.
name|maxLength
condition|)
block|{
specifier|final
name|int
name|fixedLength
init|=
name|entry
operator|.
name|minLength
decl_stmt|;
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|result
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|result
operator|.
name|offset
operator|=
name|docID
operator|*
name|fixedLength
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|fixedLength
expr_stmt|;
block|}
block|}
return|;
block|}
else|else
block|{
specifier|final
name|MonotonicBlockPackedReader
name|addresses
init|=
operator|new
name|MonotonicBlockPackedReader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
name|entry
operator|.
name|blockSize
argument_list|,
name|maxDoc
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|long
name|startAddress
init|=
name|docID
operator|==
literal|0
condition|?
literal|0
else|:
name|addresses
operator|.
name|get
argument_list|(
name|docID
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|endAddress
init|=
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|result
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|result
operator|.
name|offset
operator|=
operator|(
name|int
operator|)
name|startAddress
expr_stmt|;
name|result
operator|.
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|endAddress
operator|-
name|startAddress
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FSTEntry
name|entry
init|=
name|fsts
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
name|FST
argument_list|<
name|Long
argument_list|>
name|instance
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|instance
operator|=
name|fstInstances
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
name|instance
operator|=
operator|new
name|FST
argument_list|<
name|Long
argument_list|>
argument_list|(
name|data
argument_list|,
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fstInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|NumericDocValues
name|docToOrd
init|=
name|getNumeric
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
name|instance
decl_stmt|;
comment|// per-thread resources
specifier|final
name|BytesReader
name|in
init|=
name|fst
operator|.
name|getBytesReader
argument_list|()
decl_stmt|;
specifier|final
name|Arc
argument_list|<
name|Long
argument_list|>
name|firstArc
init|=
operator|new
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Arc
argument_list|<
name|Long
argument_list|>
name|scratchArc
init|=
operator|new
name|Arc
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|IntsRef
name|scratchInts
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
specifier|final
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
name|fstEnum
init|=
operator|new
name|BytesRefFSTEnum
argument_list|<
name|Long
argument_list|>
argument_list|(
name|fst
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|docToOrd
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
try|try
block|{
name|in
operator|.
name|setPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|firstArc
argument_list|)
expr_stmt|;
name|Util
operator|.
name|toBytesRef
argument_list|(
name|Util
operator|.
name|getByOutput
argument_list|(
name|fst
argument_list|,
name|ord
argument_list|,
name|in
argument_list|,
name|firstArc
argument_list|,
name|scratchArc
argument_list|,
name|scratchInts
argument_list|)
argument_list|,
name|result
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
block|}
annotation|@
name|Override
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|,
name|BytesRef
name|spare
parameter_list|)
block|{
try|try
block|{
name|InputOutput
argument_list|<
name|Long
argument_list|>
name|o
init|=
name|fstEnum
operator|.
name|seekCeil
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
operator|-
name|getValueCount
argument_list|()
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|o
operator|.
name|input
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|o
operator|.
name|output
operator|.
name|intValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
operator|-
name|o
operator|.
name|output
operator|-
literal|1
return|;
block|}
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
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|entry
operator|.
name|numOrds
return|;
block|}
block|}
return|;
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
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|NumericEntry
specifier|static
class|class
name|NumericEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|tableized
name|boolean
name|tableized
decl_stmt|;
DECL|field|packedIntsVersion
name|int
name|packedIntsVersion
decl_stmt|;
block|}
DECL|class|BinaryEntry
specifier|static
class|class
name|BinaryEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|numBytes
name|long
name|numBytes
decl_stmt|;
DECL|field|minLength
name|int
name|minLength
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
DECL|field|packedIntsVersion
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|blockSize
name|int
name|blockSize
decl_stmt|;
block|}
DECL|class|FSTEntry
specifier|static
class|class
name|FSTEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|numOrds
name|int
name|numOrds
decl_stmt|;
block|}
block|}
end_class
end_unit
