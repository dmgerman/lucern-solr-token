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
name|BufferedIndexInput
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
name|Arrays
import|;
end_import
begin_class
DECL|class|TermVectorsReader
class|class
name|TermVectorsReader
implements|implements
name|Cloneable
implements|,
name|Closeable
block|{
comment|// NOTE: if you make a new format, it must be larger than
comment|// the current format
comment|// Changed strings to UTF8 with length-in-bytes not length-in-chars
DECL|field|FORMAT_UTF8_LENGTH_IN_BYTES
specifier|static
specifier|final
name|int
name|FORMAT_UTF8_LENGTH_IN_BYTES
init|=
literal|4
decl_stmt|;
comment|// NOTE: always change this if you switch to a new format!
comment|// whenever you add a new format, make it 1 larger (positive version logic)!
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_UTF8_LENGTH_IN_BYTES
decl_stmt|;
comment|// when removing support for old versions, leave the last supported version here
DECL|field|FORMAT_MINIMUM
specifier|static
specifier|final
name|int
name|FORMAT_MINIMUM
init|=
name|FORMAT_UTF8_LENGTH_IN_BYTES
decl_stmt|;
comment|//The size in bytes that the FORMAT_VERSION will take up at the beginning of each file
DECL|field|FORMAT_SIZE
specifier|static
specifier|final
name|int
name|FORMAT_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|STORE_POSITIONS_WITH_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_POSITIONS_WITH_TERMVECTOR
init|=
literal|0x1
decl_stmt|;
DECL|field|STORE_OFFSET_WITH_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_OFFSET_WITH_TERMVECTOR
init|=
literal|0x2
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|tvx
specifier|private
name|IndexInput
name|tvx
decl_stmt|;
DECL|field|tvd
specifier|private
name|IndexInput
name|tvd
decl_stmt|;
DECL|field|tvf
specifier|private
name|IndexInput
name|tvf
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|numTotalDocs
specifier|private
name|int
name|numTotalDocs
decl_stmt|;
comment|// The docID offset where our docs begin in the index
comment|// file.  This will be 0 if we have our own private file.
DECL|field|docStoreOffset
specifier|private
name|int
name|docStoreOffset
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|int
name|format
decl_stmt|;
DECL|method|TermVectorsReader
name|TermVectorsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|d
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|TermVectorsReader
name|TermVectorsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|d
argument_list|,
name|segment
argument_list|,
name|fieldInfos
argument_list|,
name|readBufferSize
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|TermVectorsReader
name|TermVectorsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|int
name|docStoreOffset
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|String
name|idxName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|tvx
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|idxName
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|format
operator|=
name|checkValidFormat
argument_list|(
name|tvx
argument_list|,
name|idxName
argument_list|)
expr_stmt|;
name|String
name|fn
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
decl_stmt|;
name|tvd
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fn
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
specifier|final
name|int
name|tvdFormat
init|=
name|checkValidFormat
argument_list|(
name|tvd
argument_list|,
name|fn
argument_list|)
decl_stmt|;
name|fn
operator|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|tvf
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fn
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
specifier|final
name|int
name|tvfFormat
init|=
name|checkValidFormat
argument_list|(
name|tvf
argument_list|,
name|fn
argument_list|)
decl_stmt|;
assert|assert
name|format
operator|==
name|tvdFormat
assert|;
assert|assert
name|format
operator|==
name|tvfFormat
assert|;
name|numTotalDocs
operator|=
call|(
name|int
call|)
argument_list|(
name|tvx
operator|.
name|length
argument_list|()
operator|>>
literal|4
argument_list|)
expr_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|docStoreOffset
condition|)
block|{
name|this
operator|.
name|docStoreOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|numTotalDocs
expr_stmt|;
assert|assert
name|size
operator|==
literal|0
operator|||
name|numTotalDocs
operator|==
name|size
assert|;
block|}
else|else
block|{
name|this
operator|.
name|docStoreOffset
operator|=
name|docStoreOffset
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
comment|// Verify the file is long enough to hold all of our
comment|// docs
assert|assert
name|numTotalDocs
operator|>=
name|size
operator|+
name|docStoreOffset
operator|:
literal|"numTotalDocs="
operator|+
name|numTotalDocs
operator|+
literal|" size="
operator|+
name|size
operator|+
literal|" docStoreOffset="
operator|+
name|docStoreOffset
assert|;
block|}
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
comment|// With lock-less commits, it's entirely possible (and
comment|// fine) to hit a FileNotFound exception above. In
comment|// this case, we want to explicitly close any subset
comment|// of things that were opened so that we don't have to
comment|// wait for a GC to do so.
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Used for bulk copy when merging
DECL|method|getTvdStream
name|IndexInput
name|getTvdStream
parameter_list|()
block|{
return|return
name|tvd
return|;
block|}
comment|// Used for bulk copy when merging
DECL|method|getTvfStream
name|IndexInput
name|getTvfStream
parameter_list|()
block|{
return|return
name|tvf
return|;
block|}
DECL|method|seekTvx
specifier|private
name|void
name|seekTvx
parameter_list|(
specifier|final
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
name|tvx
operator|.
name|seek
argument_list|(
operator|(
name|docNum
operator|+
name|docStoreOffset
operator|)
operator|*
literal|16L
operator|+
name|FORMAT_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|canReadRawDocs
name|boolean
name|canReadRawDocs
parameter_list|()
block|{
comment|// we can always read raw docs, unless the term vectors
comment|// didn't exist
return|return
name|format
operator|!=
literal|0
return|;
block|}
comment|/** Retrieve the length (in bytes) of the tvd and tvf    *  entries for the next numDocs starting with    *  startDocID.  This is used for bulk copying when    *  merging segments, if the field numbers are    *  congruent.  Once this returns, the tvf& tvd streams    *  are seeked to the startDocID. */
DECL|method|rawDocs
specifier|final
name|void
name|rawDocs
parameter_list|(
name|int
index|[]
name|tvdLengths
parameter_list|,
name|int
index|[]
name|tvfLengths
parameter_list|,
name|int
name|startDocID
parameter_list|,
name|int
name|numDocs
parameter_list|)
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
name|Arrays
operator|.
name|fill
argument_list|(
name|tvdLengths
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|tvfLengths
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
name|seekTvx
argument_list|(
name|startDocID
argument_list|)
expr_stmt|;
name|long
name|tvdPosition
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|seek
argument_list|(
name|tvdPosition
argument_list|)
expr_stmt|;
name|long
name|tvfPosition
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvf
operator|.
name|seek
argument_list|(
name|tvfPosition
argument_list|)
expr_stmt|;
name|long
name|lastTvdPosition
init|=
name|tvdPosition
decl_stmt|;
name|long
name|lastTvfPosition
init|=
name|tvfPosition
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|numDocs
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|docStoreOffset
operator|+
name|startDocID
operator|+
name|count
operator|+
literal|1
decl_stmt|;
assert|assert
name|docID
operator|<=
name|numTotalDocs
assert|;
if|if
condition|(
name|docID
operator|<
name|numTotalDocs
condition|)
block|{
name|tvdPosition
operator|=
name|tvx
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|tvfPosition
operator|=
name|tvx
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tvdPosition
operator|=
name|tvd
operator|.
name|length
argument_list|()
expr_stmt|;
name|tvfPosition
operator|=
name|tvf
operator|.
name|length
argument_list|()
expr_stmt|;
assert|assert
name|count
operator|==
name|numDocs
operator|-
literal|1
assert|;
block|}
name|tvdLengths
index|[
name|count
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|tvdPosition
operator|-
name|lastTvdPosition
argument_list|)
expr_stmt|;
name|tvfLengths
index|[
name|count
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|tvfPosition
operator|-
name|lastTvfPosition
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|lastTvdPosition
operator|=
name|tvdPosition
expr_stmt|;
name|lastTvfPosition
operator|=
name|tvfPosition
expr_stmt|;
block|}
block|}
DECL|method|checkValidFormat
specifier|private
name|int
name|checkValidFormat
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|String
name|fn
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|int
name|format
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|<
name|FORMAT_MINIMUM
condition|)
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|fn
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|FORMAT_CURRENT
argument_list|)
throw|;
if|if
condition|(
name|format
operator|>
name|FORMAT_CURRENT
condition|)
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|fn
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|FORMAT_CURRENT
argument_list|)
throw|;
return|return
name|format
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|false
argument_list|,
name|tvx
argument_list|,
name|tvd
argument_list|,
name|tvf
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @return The number of documents in the reader    */
DECL|method|size
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docNum
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
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
name|int
name|fieldNumber
init|=
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
argument_list|)
decl_stmt|;
comment|//We need to account for the FORMAT_SIZE at when seeking in the tvx
comment|//We don't need to do this in other seeks because we already have the
comment|// file pointer
comment|//that was written in another file
name|seekTvx
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
comment|//System.out.println("TVX Pointer: " + tvx.getFilePointer());
name|long
name|tvdPosition
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|seek
argument_list|(
name|tvdPosition
argument_list|)
expr_stmt|;
name|int
name|fieldCount
init|=
name|tvd
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("Num Fields: " + fieldCount);
comment|// There are only a few fields per document. We opt for a full scan
comment|// rather then requiring that they be ordered. We need to read through
comment|// all of the fields anyway to get to the tvf pointers.
name|int
name|number
init|=
literal|0
decl_stmt|;
name|int
name|found
init|=
operator|-
literal|1
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|number
operator|=
name|tvd
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|number
operator|==
name|fieldNumber
condition|)
name|found
operator|=
name|i
expr_stmt|;
block|}
comment|// This field, although valid in the segment, was not found in this
comment|// document
if|if
condition|(
name|found
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Compute position in the tvf file
name|long
name|position
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|found
condition|;
name|i
operator|++
control|)
name|position
operator|+=
name|tvd
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|mapper
operator|.
name|setDocumentNumber
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
name|readTermVector
argument_list|(
name|field
argument_list|,
name|position
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//System.out.println("Fieldable not found");
block|}
block|}
else|else
block|{
comment|//System.out.println("No tvx file");
block|}
block|}
comment|/**    * Retrieve the term vector for the given document and field    * @param docNum The document number to retrieve the vector for    * @param field The field within the document to retrieve    * @return The TermFreqVector for the document and field or null if there is no termVector for this field.    * @throws IOException if there is an error reading the term vector files    */
DECL|method|get
name|TermFreqVector
name|get
parameter_list|(
name|int
name|docNum
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if no term vectors are available for this segment at all
name|ParallelArrayTermVectorMapper
name|mapper
init|=
operator|new
name|ParallelArrayTermVectorMapper
argument_list|()
decl_stmt|;
name|get
argument_list|(
name|docNum
argument_list|,
name|field
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
return|return
name|mapper
operator|.
name|materializeVector
argument_list|()
return|;
block|}
comment|// Reads the String[] fields; you have to pre-seek tvd to
comment|// the right point
DECL|method|readFields
specifier|private
name|String
index|[]
name|readFields
parameter_list|(
name|int
name|fieldCount
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|number
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[
name|fieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|number
operator|=
name|tvd
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fields
index|[
name|i
index|]
operator|=
name|fieldInfos
operator|.
name|fieldName
argument_list|(
name|number
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
comment|// Reads the long[] offsets into TVF; you have to pre-seek
comment|// tvx/tvd to the right point
DECL|method|readTvfPointers
specifier|private
name|long
index|[]
name|readTvfPointers
parameter_list|(
name|int
name|fieldCount
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Compute position in the tvf file
name|long
name|position
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
index|[]
name|tvfPointers
init|=
operator|new
name|long
index|[
name|fieldCount
index|]
decl_stmt|;
name|tvfPointers
index|[
literal|0
index|]
operator|=
name|position
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|position
operator|+=
name|tvd
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|tvfPointers
index|[
name|i
index|]
operator|=
name|position
expr_stmt|;
block|}
return|return
name|tvfPointers
return|;
block|}
comment|/**    * Return all term vectors stored for this document or null if the could not be read in.    *     * @param docNum The document number to retrieve the vector for    * @return All term frequency vectors    * @throws IOException if there is an error reading the term vector files     */
DECL|method|get
name|TermFreqVector
index|[]
name|get
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
name|TermFreqVector
index|[]
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
comment|//We need to offset by
name|seekTvx
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
name|long
name|tvdPosition
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|seek
argument_list|(
name|tvdPosition
argument_list|)
expr_stmt|;
name|int
name|fieldCount
init|=
name|tvd
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|// No fields are vectorized for this document
if|if
condition|(
name|fieldCount
operator|!=
literal|0
condition|)
block|{
specifier|final
name|String
index|[]
name|fields
init|=
name|readFields
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|tvfPointers
init|=
name|readTvfPointers
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
name|result
operator|=
name|readTermVectors
argument_list|(
name|docNum
argument_list|,
name|fields
argument_list|,
name|tvfPointers
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//System.out.println("No tvx file");
block|}
return|return
name|result
return|;
block|}
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check if no term vectors are available for this segment at all
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
comment|//We need to offset by
name|seekTvx
argument_list|(
name|docNumber
argument_list|)
expr_stmt|;
name|long
name|tvdPosition
init|=
name|tvx
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|tvd
operator|.
name|seek
argument_list|(
name|tvdPosition
argument_list|)
expr_stmt|;
name|int
name|fieldCount
init|=
name|tvd
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|// No fields are vectorized for this document
if|if
condition|(
name|fieldCount
operator|!=
literal|0
condition|)
block|{
specifier|final
name|String
index|[]
name|fields
init|=
name|readFields
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|tvfPointers
init|=
name|readTvfPointers
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
name|mapper
operator|.
name|setDocumentNumber
argument_list|(
name|docNumber
argument_list|)
expr_stmt|;
name|readTermVectors
argument_list|(
name|fields
argument_list|,
name|tvfPointers
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//System.out.println("No tvx file");
block|}
block|}
DECL|method|readTermVectors
specifier|private
name|SegmentTermVector
index|[]
name|readTermVectors
parameter_list|(
name|int
name|docNum
parameter_list|,
name|String
name|fields
index|[]
parameter_list|,
name|long
name|tvfPointers
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentTermVector
name|res
index|[]
init|=
operator|new
name|SegmentTermVector
index|[
name|fields
operator|.
name|length
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ParallelArrayTermVectorMapper
name|mapper
init|=
operator|new
name|ParallelArrayTermVectorMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|setDocumentNumber
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
name|readTermVector
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|tvfPointers
index|[
name|i
index|]
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|res
index|[
name|i
index|]
operator|=
operator|(
name|SegmentTermVector
operator|)
name|mapper
operator|.
name|materializeVector
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|readTermVectors
specifier|private
name|void
name|readTermVectors
parameter_list|(
name|String
name|fields
index|[]
parameter_list|,
name|long
name|tvfPointers
index|[]
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|readTermVector
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|tvfPointers
index|[
name|i
index|]
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *     * @param field The field to read in    * @param tvfPointer The pointer within the tvf file where we should start reading    * @param mapper The mapper used to map the TermVector    * @throws IOException    */
DECL|method|readTermVector
specifier|private
name|void
name|readTermVector
parameter_list|(
name|String
name|field
parameter_list|,
name|long
name|tvfPointer
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Now read the data from specified position
comment|//We don't need to offset by the FORMAT here since the pointer already includes the offset
name|tvf
operator|.
name|seek
argument_list|(
name|tvfPointer
argument_list|)
expr_stmt|;
name|int
name|numTerms
init|=
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("Num Terms: " + numTerms);
comment|// If no terms - return a constant empty termvector. However, this should never occur!
if|if
condition|(
name|numTerms
operator|==
literal|0
condition|)
return|return;
name|boolean
name|storePositions
decl_stmt|;
name|boolean
name|storeOffsets
decl_stmt|;
name|byte
name|bits
init|=
name|tvf
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|storePositions
operator|=
operator|(
name|bits
operator|&
name|STORE_POSITIONS_WITH_TERMVECTOR
operator|)
operator|!=
literal|0
expr_stmt|;
name|storeOffsets
operator|=
operator|(
name|bits
operator|&
name|STORE_OFFSET_WITH_TERMVECTOR
operator|)
operator|!=
literal|0
expr_stmt|;
name|mapper
operator|.
name|setExpectations
argument_list|(
name|field
argument_list|,
name|numTerms
argument_list|,
name|storeOffsets
argument_list|,
name|storePositions
argument_list|)
expr_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|deltaLength
init|=
literal|0
decl_stmt|;
name|int
name|totalLength
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|byteBuffer
decl_stmt|;
comment|// init the buffer
name|byteBuffer
operator|=
operator|new
name|byte
index|[
literal|20
index|]
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|start
operator|=
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|deltaLength
operator|=
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|totalLength
operator|=
name|start
operator|+
name|deltaLength
expr_stmt|;
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|(
name|totalLength
argument_list|)
decl_stmt|;
comment|// Term stored as utf8 bytes
if|if
condition|(
name|byteBuffer
operator|.
name|length
operator|<
name|totalLength
condition|)
block|{
name|byteBuffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|byteBuffer
argument_list|,
name|totalLength
argument_list|)
expr_stmt|;
block|}
name|tvf
operator|.
name|readBytes
argument_list|(
name|byteBuffer
argument_list|,
name|start
argument_list|,
name|deltaLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|byteBuffer
argument_list|,
literal|0
argument_list|,
name|term
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|totalLength
argument_list|)
expr_stmt|;
name|term
operator|.
name|length
operator|=
name|totalLength
expr_stmt|;
name|int
name|freq
init|=
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
index|[]
name|positions
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|storePositions
condition|)
block|{
comment|//read in the positions
comment|//does the mapper even care about positions?
if|if
condition|(
operator|!
name|mapper
operator|.
name|isIgnoringPositions
argument_list|()
condition|)
block|{
name|positions
operator|=
operator|new
name|int
index|[
name|freq
index|]
expr_stmt|;
name|int
name|prevPosition
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|positions
index|[
name|j
index|]
operator|=
name|prevPosition
operator|+
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|prevPosition
operator|=
name|positions
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//we need to skip over the positions.  Since these are VInts, I don't believe there is anyway to know for sure how far to skip
comment|//
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|TermVectorOffsetInfo
index|[]
name|offsets
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|storeOffsets
condition|)
block|{
comment|//does the mapper even care about offsets?
if|if
condition|(
operator|!
name|mapper
operator|.
name|isIgnoringOffsets
argument_list|()
condition|)
block|{
name|offsets
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
name|freq
index|]
expr_stmt|;
name|int
name|prevOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|int
name|startOffset
init|=
name|prevOffset
operator|+
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|startOffset
operator|+
name|tvf
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|offsets
index|[
name|j
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
name|prevOffset
operator|=
name|endOffset
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|tvf
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|mapper
operator|.
name|map
argument_list|(
name|term
argument_list|,
name|freq
argument_list|,
name|offsets
argument_list|,
name|positions
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
specifier|final
name|TermVectorsReader
name|clone
init|=
operator|(
name|TermVectorsReader
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// These are null when a TermVectorsReader was created
comment|// on a segment that did not have term vectors saved
if|if
condition|(
name|tvx
operator|!=
literal|null
operator|&&
name|tvd
operator|!=
literal|null
operator|&&
name|tvf
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|tvx
operator|=
operator|(
name|IndexInput
operator|)
name|tvx
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|tvd
operator|=
operator|(
name|IndexInput
operator|)
name|tvd
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|tvf
operator|=
operator|(
name|IndexInput
operator|)
name|tvf
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * Models the existing parallel array structure  */
end_comment
begin_class
DECL|class|ParallelArrayTermVectorMapper
class|class
name|ParallelArrayTermVectorMapper
extends|extends
name|TermVectorMapper
block|{
DECL|field|terms
specifier|private
name|BytesRef
index|[]
name|terms
decl_stmt|;
DECL|field|termFreqs
specifier|private
name|int
index|[]
name|termFreqs
decl_stmt|;
DECL|field|positions
specifier|private
name|int
name|positions
index|[]
index|[]
decl_stmt|;
DECL|field|offsets
specifier|private
name|TermVectorOffsetInfo
name|offsets
index|[]
index|[]
decl_stmt|;
DECL|field|currentPosition
specifier|private
name|int
name|currentPosition
decl_stmt|;
DECL|field|storingOffsets
specifier|private
name|boolean
name|storingOffsets
decl_stmt|;
DECL|field|storingPositions
specifier|private
name|boolean
name|storingPositions
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
annotation|@
name|Override
DECL|method|setExpectations
specifier|public
name|void
name|setExpectations
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|storeOffsets
parameter_list|,
name|boolean
name|storePositions
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|terms
operator|=
operator|new
name|BytesRef
index|[
name|numTerms
index|]
expr_stmt|;
name|termFreqs
operator|=
operator|new
name|int
index|[
name|numTerms
index|]
expr_stmt|;
name|this
operator|.
name|storingOffsets
operator|=
name|storeOffsets
expr_stmt|;
name|this
operator|.
name|storingPositions
operator|=
name|storePositions
expr_stmt|;
if|if
condition|(
name|storePositions
condition|)
name|this
operator|.
name|positions
operator|=
operator|new
name|int
index|[
name|numTerms
index|]
index|[]
expr_stmt|;
if|if
condition|(
name|storeOffsets
condition|)
name|this
operator|.
name|offsets
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
name|numTerms
index|]
index|[]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|map
specifier|public
name|void
name|map
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|frequency
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|,
name|int
index|[]
name|positions
parameter_list|)
block|{
name|terms
index|[
name|currentPosition
index|]
operator|=
name|term
expr_stmt|;
name|termFreqs
index|[
name|currentPosition
index|]
operator|=
name|frequency
expr_stmt|;
if|if
condition|(
name|storingOffsets
condition|)
block|{
name|this
operator|.
name|offsets
index|[
name|currentPosition
index|]
operator|=
name|offsets
expr_stmt|;
block|}
if|if
condition|(
name|storingPositions
condition|)
block|{
name|this
operator|.
name|positions
index|[
name|currentPosition
index|]
operator|=
name|positions
expr_stmt|;
block|}
name|currentPosition
operator|++
expr_stmt|;
block|}
comment|/**    * Construct the vector    * @return The {@link TermFreqVector} based on the mappings.    */
DECL|method|materializeVector
specifier|public
name|TermFreqVector
name|materializeVector
parameter_list|()
block|{
name|SegmentTermVector
name|tv
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
name|terms
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|storingPositions
operator|||
name|storingOffsets
condition|)
block|{
name|tv
operator|=
operator|new
name|SegmentTermPositionVector
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|termFreqs
argument_list|,
name|positions
argument_list|,
name|offsets
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tv
operator|=
operator|new
name|SegmentTermVector
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|termFreqs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tv
return|;
block|}
block|}
end_class
end_unit
