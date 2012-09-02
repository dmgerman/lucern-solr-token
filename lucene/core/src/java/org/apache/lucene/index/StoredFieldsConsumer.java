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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|Codec
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
name|StoredFieldsWriter
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/** This is a DocFieldConsumer that writes stored fields. */
end_comment
begin_class
DECL|class|StoredFieldsConsumer
specifier|final
class|class
name|StoredFieldsConsumer
block|{
DECL|field|fieldsWriter
name|StoredFieldsWriter
name|fieldsWriter
decl_stmt|;
DECL|field|docWriter
specifier|final
name|DocumentsWriterPerThread
name|docWriter
decl_stmt|;
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|freeCount
name|int
name|freeCount
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|codec
specifier|final
name|Codec
name|codec
decl_stmt|;
DECL|method|StoredFieldsConsumer
specifier|public
name|StoredFieldsConsumer
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
name|this
operator|.
name|docState
operator|=
name|docWriter
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|docWriter
operator|.
name|codec
expr_stmt|;
block|}
DECL|field|numStoredFields
specifier|private
name|int
name|numStoredFields
decl_stmt|;
DECL|field|storedFields
specifier|private
name|StorableField
index|[]
name|storedFields
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfo
index|[]
name|fieldInfos
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|numStoredFields
operator|=
literal|0
expr_stmt|;
name|storedFields
operator|=
operator|new
name|StorableField
index|[
literal|1
index|]
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfo
index|[
literal|1
index|]
expr_stmt|;
block|}
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numDocs
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|numDocs
operator|>
literal|0
condition|)
block|{
comment|// It's possible that all documents seen in this segment
comment|// hit non-aborting exceptions, in which case we will
comment|// not have yet init'd the FieldsWriter:
name|initFieldsWriter
argument_list|(
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|fill
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldsWriter
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fieldsWriter
operator|.
name|finish
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fieldsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|fieldsWriter
operator|=
literal|null
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
DECL|method|initFieldsWriter
specifier|private
specifier|synchronized
name|void
name|initFieldsWriter
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldsWriter
operator|==
literal|null
condition|)
block|{
name|fieldsWriter
operator|=
name|codec
operator|.
name|storedFieldsFormat
argument_list|()
operator|.
name|fieldsWriter
argument_list|(
name|docWriter
operator|.
name|directory
argument_list|,
name|docWriter
operator|.
name|getSegmentInfo
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|field|allocCount
name|int
name|allocCount
decl_stmt|;
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldsWriter
operator|!=
literal|null
condition|)
block|{
name|fieldsWriter
operator|.
name|abort
argument_list|()
expr_stmt|;
name|fieldsWriter
operator|=
literal|null
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** Fills in any hole in the docIDs */
DECL|method|fill
name|void
name|fill
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We must "catch up" for all docs before us
comment|// that had no stored fields:
while|while
condition|(
name|lastDocID
operator|<
name|docID
condition|)
block|{
name|fieldsWriter
operator|.
name|startDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|lastDocID
operator|++
expr_stmt|;
block|}
block|}
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"StoredFieldsWriter.finishDocument start"
argument_list|)
assert|;
name|initFieldsWriter
argument_list|(
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|fill
argument_list|(
name|docState
operator|.
name|docID
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsWriter
operator|!=
literal|null
operator|&&
name|numStoredFields
operator|>
literal|0
condition|)
block|{
name|fieldsWriter
operator|.
name|startDocument
argument_list|(
name|numStoredFields
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
name|numStoredFields
condition|;
name|i
operator|++
control|)
block|{
name|fieldsWriter
operator|.
name|writeField
argument_list|(
name|fieldInfos
index|[
name|i
index|]
argument_list|,
name|storedFields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|++
expr_stmt|;
block|}
name|reset
argument_list|()
expr_stmt|;
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"StoredFieldsWriter.finishDocument end"
argument_list|)
assert|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|StorableField
name|field
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
if|if
condition|(
name|numStoredFields
operator|==
name|storedFields
operator|.
name|length
condition|)
block|{
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numStoredFields
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
name|StorableField
index|[]
name|newArray
init|=
operator|new
name|StorableField
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|storedFields
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|numStoredFields
argument_list|)
expr_stmt|;
name|storedFields
operator|=
name|newArray
expr_stmt|;
name|FieldInfo
index|[]
name|newInfoArray
init|=
operator|new
name|FieldInfo
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|fieldInfos
argument_list|,
literal|0
argument_list|,
name|newInfoArray
argument_list|,
literal|0
argument_list|,
name|numStoredFields
argument_list|)
expr_stmt|;
name|fieldInfos
operator|=
name|newInfoArray
expr_stmt|;
block|}
name|storedFields
index|[
name|numStoredFields
index|]
operator|=
name|field
expr_stmt|;
name|fieldInfos
index|[
name|numStoredFields
index|]
operator|=
name|fieldInfo
expr_stmt|;
name|numStoredFields
operator|++
expr_stmt|;
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"StoredFieldsWriterPerThread.processFields.writeField"
argument_list|)
assert|;
block|}
block|}
end_class
end_unit
