begin_unit
begin_package
DECL|package|org.apache.lucene.rangetree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|rangetree
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
name|Closeable
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
name|HashMap
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
begin_class
DECL|class|RangeTreeDocValuesConsumer
class|class
name|RangeTreeDocValuesConsumer
extends|extends
name|DocValuesConsumer
implements|implements
name|Closeable
block|{
DECL|field|delegate
specifier|final
name|DocValuesConsumer
name|delegate
decl_stmt|;
DECL|field|maxPointsInLeafNode
specifier|final
name|int
name|maxPointsInLeafNode
decl_stmt|;
DECL|field|maxPointsSortInHeap
specifier|final
name|int
name|maxPointsSortInHeap
decl_stmt|;
DECL|field|out
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|fieldIndexFPs
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|fieldIndexFPs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|state
specifier|final
name|SegmentWriteState
name|state
decl_stmt|;
DECL|method|RangeTreeDocValuesConsumer
specifier|public
name|RangeTreeDocValuesConsumer
parameter_list|(
name|DocValuesConsumer
name|delegate
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|,
name|int
name|maxPointsInLeafNode
parameter_list|,
name|int
name|maxPointsSortInHeap
parameter_list|)
throws|throws
name|IOException
block|{
name|RangeTreeWriter
operator|.
name|verifyParams
argument_list|(
name|maxPointsInLeafNode
argument_list|,
name|maxPointsSortInHeap
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|maxPointsInLeafNode
operator|=
name|maxPointsInLeafNode
expr_stmt|;
name|this
operator|.
name|maxPointsSortInHeap
operator|=
name|maxPointsSortInHeap
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|String
name|datFileName
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
name|RangeTreeDocValuesFormat
operator|.
name|DATA_EXTENSION
argument_list|)
decl_stmt|;
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|datFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|out
argument_list|,
name|RangeTreeDocValuesFormat
operator|.
name|DATA_CODEC_NAME
argument_list|,
name|RangeTreeDocValuesFormat
operator|.
name|DATA_VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
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
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|out
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
name|delegate
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|delegate
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|metaFileName
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
name|RangeTreeDocValuesFormat
operator|.
name|META_EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|metaOut
init|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|metaFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|metaOut
argument_list|,
name|RangeTreeDocValuesFormat
operator|.
name|META_CODEC_NAME
argument_list|,
name|RangeTreeDocValuesFormat
operator|.
name|META_VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|metaOut
operator|.
name|writeVInt
argument_list|(
name|fieldIndexFPs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|ent
range|:
name|fieldIndexFPs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|metaOut
operator|.
name|writeVInt
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|metaOut
operator|.
name|writeVLong
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|metaOut
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
name|metaOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|metaOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addSortedNumericField
specifier|public
name|void
name|addSortedNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToValueCount
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
name|delegate
operator|.
name|addSortedNumericField
argument_list|(
name|field
argument_list|,
name|docToValueCount
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|RangeTreeWriter
name|writer
init|=
operator|new
name|RangeTreeWriter
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|maxPointsSortInHeap
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Number
argument_list|>
name|valueIt
init|=
name|values
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Number
argument_list|>
name|valueCountIt
init|=
name|docToValueCount
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|//System.out.println("\nSNF: field=" + field.name);
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
condition|;
name|docID
operator|++
control|)
block|{
assert|assert
name|valueCountIt
operator|.
name|hasNext
argument_list|()
assert|;
name|int
name|count
init|=
name|valueCountIt
operator|.
name|next
argument_list|()
operator|.
name|intValue
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
assert|assert
name|valueIt
operator|.
name|hasNext
argument_list|()
assert|;
name|writer
operator|.
name|add
argument_list|(
name|valueIt
operator|.
name|next
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|indexStartFP
init|=
name|writer
operator|.
name|finish
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|fieldIndexFPs
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|indexStartFP
argument_list|)
expr_stmt|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"use either SortedNumericDocValuesField or SortedSetDocValuesField"
argument_list|)
throw|;
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
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"use either SortedNumericDocValuesField or SortedSetDocValuesField"
argument_list|)
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"use either SortedNumericDocValuesField or SortedSetDocValuesField"
argument_list|)
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
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrdCount
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|ords
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|addSortedSetField
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
name|docToOrdCount
argument_list|,
name|ords
argument_list|)
expr_stmt|;
name|RangeTreeWriter
name|writer
init|=
operator|new
name|RangeTreeWriter
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|maxPointsSortInHeap
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Number
argument_list|>
name|docToOrdCountIt
init|=
name|docToOrdCount
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Number
argument_list|>
name|ordsIt
init|=
name|ords
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|//System.out.println("\nSSF: field=" + field.name);
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
condition|;
name|docID
operator|++
control|)
block|{
assert|assert
name|docToOrdCountIt
operator|.
name|hasNext
argument_list|()
assert|;
name|int
name|count
init|=
name|docToOrdCountIt
operator|.
name|next
argument_list|()
operator|.
name|intValue
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
assert|assert
name|ordsIt
operator|.
name|hasNext
argument_list|()
assert|;
name|long
name|ord
init|=
name|ordsIt
operator|.
name|next
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
name|ord
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|indexStartFP
init|=
name|writer
operator|.
name|finish
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|fieldIndexFPs
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|indexStartFP
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
