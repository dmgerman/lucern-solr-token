begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
package|;
end_package
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|PointReader
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
name|store
operator|.
name|BufferedChecksumIndexInput
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
name|ChecksumIndexInput
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
name|BytesRefBuilder
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
name|StringHelper
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
name|bkd
operator|.
name|BKDReader
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|BLOCK_FP
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|BYTES_PER_DIM
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|FIELD_COUNT
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|FIELD_FP
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|FIELD_FP_NAME
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|INDEX_COUNT
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|MAX_LEAF_POINTS
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|MAX_VALUE
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|MIN_VALUE
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|NUM_DIMS
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|SPLIT_COUNT
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|SPLIT_DIM
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
name|simpletext
operator|.
name|SimpleTextPointWriter
operator|.
name|SPLIT_VALUE
import|;
end_import
begin_class
DECL|class|SimpleTextPointReader
class|class
name|SimpleTextPointReader
extends|extends
name|PointReader
block|{
DECL|field|dataIn
specifier|private
specifier|final
name|IndexInput
name|dataIn
decl_stmt|;
DECL|field|readState
specifier|final
name|SegmentReadState
name|readState
decl_stmt|;
DECL|field|readers
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BKDReader
argument_list|>
name|readers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|method|SimpleTextPointReader
specifier|public
name|SimpleTextPointReader
parameter_list|(
name|SegmentReadState
name|readState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Initialize readers now:
comment|// Read index:
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|fieldToFileOffset
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|indexFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|,
name|SimpleTextPointFormat
operator|.
name|POINT_INDEX_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|in
init|=
name|readState
operator|.
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|indexFileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
name|readLine
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|parseInt
argument_list|(
name|FIELD_COUNT
argument_list|)
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
name|readLine
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|stripPrefix
argument_list|(
name|FIELD_FP_NAME
argument_list|)
decl_stmt|;
name|readLine
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|long
name|fp
init|=
name|parseLong
argument_list|(
name|FIELD_FP
argument_list|)
decl_stmt|;
name|fieldToFileOffset
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fp
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|checkFooter
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|readState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|readState
operator|.
name|segmentSuffix
argument_list|,
name|SimpleTextPointFormat
operator|.
name|POINT_EXTENSION
argument_list|)
decl_stmt|;
name|dataIn
operator|=
name|readState
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|ent
range|:
name|fieldToFileOffset
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|readers
operator|.
name|put
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|initReader
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
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
name|success
operator|==
literal|false
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
name|this
operator|.
name|readState
operator|=
name|readState
expr_stmt|;
block|}
DECL|method|initReader
specifier|private
name|BKDReader
name|initReader
parameter_list|(
name|long
name|fp
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NOTE: matches what writeIndex does in SimpleTextPointWriter
name|dataIn
operator|.
name|seek
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|int
name|numDims
init|=
name|parseInt
argument_list|(
name|NUM_DIMS
argument_list|)
decl_stmt|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|int
name|bytesPerDim
init|=
name|parseInt
argument_list|(
name|BYTES_PER_DIM
argument_list|)
decl_stmt|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|int
name|maxPointsInLeafNode
init|=
name|parseInt
argument_list|(
name|MAX_LEAF_POINTS
argument_list|)
decl_stmt|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|parseInt
argument_list|(
name|INDEX_COUNT
argument_list|)
decl_stmt|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|MIN_VALUE
argument_list|)
assert|;
name|BytesRef
name|minValue
init|=
name|SimpleTextUtil
operator|.
name|fromBytesRefString
argument_list|(
name|stripPrefix
argument_list|(
name|MIN_VALUE
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|minValue
operator|.
name|length
operator|==
name|numDims
operator|*
name|bytesPerDim
assert|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|MAX_VALUE
argument_list|)
assert|;
name|BytesRef
name|maxValue
init|=
name|SimpleTextUtil
operator|.
name|fromBytesRefString
argument_list|(
name|stripPrefix
argument_list|(
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|maxValue
operator|.
name|length
operator|==
name|numDims
operator|*
name|bytesPerDim
assert|;
name|long
index|[]
name|leafBlockFPs
init|=
operator|new
name|long
index|[
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|leafBlockFPs
index|[
name|i
index|]
operator|=
name|parseLong
argument_list|(
name|BLOCK_FP
argument_list|)
expr_stmt|;
block|}
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|count
operator|=
name|parseInt
argument_list|(
name|SPLIT_COUNT
argument_list|)
expr_stmt|;
name|byte
index|[]
name|splitPackedValues
init|=
operator|new
name|byte
index|[
name|count
operator|*
operator|(
literal|1
operator|+
name|bytesPerDim
operator|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|splitPackedValues
index|[
operator|(
literal|1
operator|+
name|bytesPerDim
operator|)
operator|*
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|parseInt
argument_list|(
name|SPLIT_DIM
argument_list|)
expr_stmt|;
name|readLine
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|SPLIT_VALUE
argument_list|)
assert|;
name|BytesRef
name|br
init|=
name|SimpleTextUtil
operator|.
name|fromBytesRefString
argument_list|(
name|stripPrefix
argument_list|(
name|SPLIT_VALUE
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|br
operator|.
name|length
operator|==
name|bytesPerDim
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|br
operator|.
name|bytes
argument_list|,
name|br
operator|.
name|offset
argument_list|,
name|splitPackedValues
argument_list|,
operator|(
literal|1
operator|+
name|bytesPerDim
operator|)
operator|*
name|i
operator|+
literal|1
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SimpleTextBKDReader
argument_list|(
name|dataIn
argument_list|,
name|numDims
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|bytesPerDim
argument_list|,
name|leafBlockFPs
argument_list|,
name|splitPackedValues
argument_list|,
name|minValue
operator|.
name|bytes
argument_list|,
name|maxValue
operator|.
name|bytes
argument_list|)
return|;
block|}
DECL|method|readLine
specifier|private
name|void
name|readLine
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|startsWith
specifier|private
name|boolean
name|startsWith
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|prefix
argument_list|)
return|;
block|}
DECL|method|parseInt
specifier|private
name|int
name|parseInt
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
block|{
assert|assert
name|startsWith
argument_list|(
name|prefix
argument_list|)
assert|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|stripPrefix
argument_list|(
name|prefix
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseLong
specifier|private
name|long
name|parseLong
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
block|{
assert|assert
name|startsWith
argument_list|(
name|prefix
argument_list|)
assert|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|stripPrefix
argument_list|(
name|prefix
argument_list|)
argument_list|)
return|;
block|}
DECL|method|stripPrefix
specifier|private
name|String
name|stripPrefix
parameter_list|(
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|prefix
operator|.
name|length
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|prefix
operator|.
name|length
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
DECL|method|getBKDReader
specifier|private
name|BKDReader
name|getBKDReader
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldInfo
name|fieldInfo
init|=
name|readState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldName
operator|+
literal|"\" is unrecognized"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldName
operator|+
literal|"\" did not index points"
argument_list|)
throw|;
block|}
return|return
name|readers
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/** Finds all documents and points matching the provided visitor */
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|BKDReader
name|bkdReader
init|=
name|getBKDReader
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|bkdReader
operator|==
literal|null
condition|)
block|{
comment|// Schema ghost corner case!  This field did index points in the past, but
comment|// now all docs having this field were deleted in this segment:
return|return;
block|}
name|bkdReader
operator|.
name|intersect
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|IndexInput
name|clone
init|=
name|dataIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// checksum is fixed-width encoded with 20 bytes, plus 1 byte for newline (the space is included in SimpleTextUtil.CHECKSUM):
name|long
name|footerStartPos
init|=
name|dataIn
operator|.
name|length
argument_list|()
operator|-
operator|(
name|SimpleTextUtil
operator|.
name|CHECKSUM
operator|.
name|length
operator|+
literal|21
operator|)
decl_stmt|;
name|ChecksumIndexInput
name|input
init|=
operator|new
name|BufferedChecksumIndexInput
argument_list|(
name|clone
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|input
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|>=
name|footerStartPos
condition|)
block|{
comment|// Make sure we landed at precisely the right location:
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|!=
name|footerStartPos
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"SimpleText failure: footer does not start at expected position current="
operator|+
name|input
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" vs expected="
operator|+
name|footerStartPos
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|SimpleTextUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
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
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SimpleTextPointReader(segment="
operator|+
name|readState
operator|.
name|segmentInfo
operator|.
name|name
operator|+
literal|" maxDoc="
operator|+
name|readState
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getMinPackedValue
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|BKDReader
name|bkdReader
init|=
name|getBKDReader
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|bkdReader
operator|==
literal|null
condition|)
block|{
comment|// Schema ghost corner case!  This field did index points in the past, but
comment|// now all docs having this field were deleted in this segment:
return|return
literal|null
return|;
block|}
return|return
name|bkdReader
operator|.
name|getMinPackedValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxPackedValue
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|BKDReader
name|bkdReader
init|=
name|getBKDReader
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|bkdReader
operator|==
literal|null
condition|)
block|{
comment|// Schema ghost corner case!  This field did index points in the past, but
comment|// now all docs having this field were deleted in this segment:
return|return
literal|null
return|;
block|}
return|return
name|bkdReader
operator|.
name|getMaxPackedValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDimensions
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|BKDReader
name|bkdReader
init|=
name|getBKDReader
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|bkdReader
operator|==
literal|null
condition|)
block|{
comment|// Schema ghost corner case!  This field did index points in the past, but
comment|// now all docs having this field were deleted in this segment:
return|return
literal|0
return|;
block|}
return|return
name|bkdReader
operator|.
name|getNumDimensions
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesPerDimension
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|BKDReader
name|bkdReader
init|=
name|getBKDReader
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|bkdReader
operator|==
literal|null
condition|)
block|{
comment|// Schema ghost corner case!  This field did index points in the past, but
comment|// now all docs having this field were deleted in this segment:
return|return
literal|0
return|;
block|}
return|return
name|bkdReader
operator|.
name|getBytesPerDimension
argument_list|()
return|;
block|}
block|}
end_class
end_unit
