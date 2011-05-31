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
name|RamUsageEstimator
import|;
end_import
begin_class
DECL|class|TermVectorsTermsWriter
specifier|final
class|class
name|TermVectorsTermsWriter
extends|extends
name|TermsHashConsumer
block|{
DECL|field|docWriter
specifier|final
name|DocumentsWriterPerThread
name|docWriter
decl_stmt|;
DECL|field|freeCount
name|int
name|freeCount
decl_stmt|;
DECL|field|tvx
name|IndexOutput
name|tvx
decl_stmt|;
DECL|field|tvd
name|IndexOutput
name|tvd
decl_stmt|;
DECL|field|tvf
name|IndexOutput
name|tvf
decl_stmt|;
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|flushTerm
specifier|final
name|BytesRef
name|flushTerm
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|// Used by perField when serializing the term vectors
DECL|field|vectorSliceReader
specifier|final
name|ByteSliceReader
name|vectorSliceReader
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
DECL|field|hasVectors
name|boolean
name|hasVectors
decl_stmt|;
DECL|method|TermVectorsTermsWriter
specifier|public
name|TermVectorsTermsWriter
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
name|docState
operator|=
name|docWriter
operator|.
name|docState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|TermsHashConsumerPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
comment|// At least one doc in this run had term vectors enabled
name|fill
argument_list|(
name|state
operator|.
name|numDocs
argument_list|)
expr_stmt|;
assert|assert
name|state
operator|.
name|segmentName
operator|!=
literal|null
assert|;
name|String
name|idxName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|false
argument_list|,
name|tvx
argument_list|,
name|tvf
argument_list|,
name|tvd
argument_list|)
expr_stmt|;
name|tvx
operator|=
name|tvd
operator|=
name|tvf
operator|=
literal|null
expr_stmt|;
if|if
condition|(
literal|4
operator|+
operator|(
operator|(
name|long
operator|)
name|state
operator|.
name|numDocs
operator|)
operator|*
literal|16
operator|!=
name|state
operator|.
name|directory
operator|.
name|fileLength
argument_list|(
name|idxName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"after flush: tvx size mismatch: "
operator|+
name|state
operator|.
name|numDocs
operator|+
literal|" docs vs "
operator|+
name|state
operator|.
name|directory
operator|.
name|fileLength
argument_list|(
name|idxName
argument_list|)
operator|+
literal|" length in bytes of "
operator|+
name|idxName
operator|+
literal|" file exists?="
operator|+
name|state
operator|.
name|directory
operator|.
name|fileExists
argument_list|(
name|idxName
argument_list|)
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|hasVectors
operator|=
literal|false
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|TermsHashConsumerPerField
name|field
range|:
name|fieldsToFlush
operator|.
name|values
argument_list|()
control|)
block|{
name|TermVectorsTermsWriterPerField
name|perField
init|=
operator|(
name|TermVectorsTermsWriterPerField
operator|)
name|field
decl_stmt|;
name|perField
operator|.
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perField
operator|.
name|shrinkHash
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Fills in no-term-vectors for all docs we haven't seen    *  since the last doc that had term vectors. */
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
if|if
condition|(
name|lastDocID
operator|<
name|docID
condition|)
block|{
specifier|final
name|long
name|tvfPosition
init|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
while|while
condition|(
name|lastDocID
operator|<
name|docID
condition|)
block|{
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvd
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvfPosition
argument_list|)
expr_stmt|;
name|lastDocID
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|initTermVectorsWriter
specifier|private
specifier|final
name|void
name|initTermVectorsWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tvx
operator|==
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// If we hit an exception while init'ing the term
comment|// vector output files, we must abort this segment
comment|// because those files will be in an unknown
comment|// state:
name|tvx
operator|=
name|docWriter
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docWriter
operator|.
name|getSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|tvd
operator|=
name|docWriter
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docWriter
operator|.
name|getSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|tvf
operator|=
name|docWriter
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docWriter
operator|.
name|getSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeInt
argument_list|(
name|TermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeInt
argument_list|(
name|TermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeInt
argument_list|(
name|TermVectorsReader
operator|.
name|FORMAT_CURRENT
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
name|closeSafely
argument_list|(
literal|true
argument_list|,
name|tvx
argument_list|,
name|tvd
argument_list|,
name|tvf
argument_list|)
expr_stmt|;
block|}
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|(
name|TermsHash
name|termsHash
parameter_list|)
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
literal|"TermVectorsTermsWriter.finishDocument start"
argument_list|)
assert|;
if|if
condition|(
operator|!
name|hasVectors
condition|)
block|{
return|return;
block|}
name|initTermVectorsWriter
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|docState
operator|.
name|docID
argument_list|)
expr_stmt|;
comment|// Append term vectors to the real outputs:
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvd
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvf
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
name|numVectorFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|numVectorFields
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numVectorFields
condition|;
name|i
operator|++
control|)
block|{
name|tvd
operator|.
name|writeVInt
argument_list|(
name|perFields
index|[
name|i
index|]
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
block|}
name|long
name|lastPos
init|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|perFields
index|[
literal|0
index|]
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numVectorFields
condition|;
name|i
operator|++
control|)
block|{
name|long
name|pos
init|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|writeVLong
argument_list|(
name|pos
operator|-
name|lastPos
argument_list|)
expr_stmt|;
name|lastPos
operator|=
name|pos
expr_stmt|;
name|perFields
index|[
name|i
index|]
operator|.
name|finishDocument
argument_list|()
expr_stmt|;
comment|// commit the termVectors once successful success - FI will otherwise reset them
name|perFields
index|[
name|i
index|]
operator|.
name|fieldInfo
operator|.
name|commitVectors
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|lastDocID
operator|==
name|docState
operator|.
name|docID
operator|:
literal|"lastDocID="
operator|+
name|lastDocID
operator|+
literal|" docState.docID="
operator|+
name|docState
operator|.
name|docID
assert|;
name|lastDocID
operator|++
expr_stmt|;
name|termsHash
operator|.
name|reset
argument_list|()
expr_stmt|;
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
literal|"TermVectorsTermsWriter.finishDocument end"
argument_list|)
assert|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|hasVectors
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|true
argument_list|,
name|tvx
argument_list|,
name|tvd
argument_list|,
name|tvf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// cannot happen since we suppress exceptions
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|docWriter
operator|.
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docWriter
operator|.
name|getSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{     }
try|try
block|{
name|docWriter
operator|.
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docWriter
operator|.
name|getSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{     }
try|try
block|{
name|docWriter
operator|.
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|docWriter
operator|.
name|getSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{     }
name|tvx
operator|=
name|tvd
operator|=
name|tvf
operator|=
literal|null
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|field|numVectorFields
name|int
name|numVectorFields
decl_stmt|;
DECL|field|perFields
name|TermVectorsTermsWriterPerField
index|[]
name|perFields
decl_stmt|;
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|numVectorFields
operator|=
literal|0
expr_stmt|;
name|perFields
operator|=
operator|new
name|TermVectorsTermsWriterPerField
index|[
literal|1
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsHashConsumerPerField
name|addField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsTermsWriterPerField
argument_list|(
name|termsHashPerField
argument_list|,
name|this
argument_list|,
name|fieldInfo
argument_list|)
return|;
block|}
DECL|method|addFieldToFlush
name|void
name|addFieldToFlush
parameter_list|(
name|TermVectorsTermsWriterPerField
name|fieldToFlush
parameter_list|)
block|{
if|if
condition|(
name|numVectorFields
operator|==
name|perFields
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
name|numVectorFields
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
name|TermVectorsTermsWriterPerField
index|[]
name|newArray
init|=
operator|new
name|TermVectorsTermsWriterPerField
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|perFields
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|numVectorFields
argument_list|)
expr_stmt|;
name|perFields
operator|=
name|newArray
expr_stmt|;
block|}
name|perFields
index|[
name|numVectorFields
operator|++
index|]
operator|=
name|fieldToFlush
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|clearLastVectorFieldName
argument_list|()
assert|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// Called only by assert
DECL|method|clearLastVectorFieldName
specifier|final
name|boolean
name|clearLastVectorFieldName
parameter_list|()
block|{
name|lastVectorFieldName
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// Called only by assert
DECL|field|lastVectorFieldName
name|String
name|lastVectorFieldName
decl_stmt|;
DECL|method|vectorFieldsInOrder
specifier|final
name|boolean
name|vectorFieldsInOrder
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|lastVectorFieldName
operator|!=
literal|null
condition|)
return|return
name|lastVectorFieldName
operator|.
name|compareTo
argument_list|(
name|fi
operator|.
name|name
argument_list|)
operator|<
literal|0
return|;
else|else
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|lastVectorFieldName
operator|=
name|fi
operator|.
name|name
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
