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
comment|/**  * Copyright 2004 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Fieldable
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
name|RAMOutputStream
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
name|store
operator|.
name|IndexInput
import|;
end_import
begin_class
DECL|class|FieldsWriter
specifier|final
class|class
name|FieldsWriter
block|{
DECL|field|FIELD_IS_TOKENIZED
specifier|static
specifier|final
name|byte
name|FIELD_IS_TOKENIZED
init|=
literal|0x1
decl_stmt|;
DECL|field|FIELD_IS_BINARY
specifier|static
specifier|final
name|byte
name|FIELD_IS_BINARY
init|=
literal|0x2
decl_stmt|;
comment|/** @deprecated Kept for backwards-compatibility with<3.0 indexes; will be removed in 4.0 */
annotation|@
name|Deprecated
DECL|field|FIELD_IS_COMPRESSED
specifier|static
specifier|final
name|byte
name|FIELD_IS_COMPRESSED
init|=
literal|0x4
decl_stmt|;
comment|// Original format
DECL|field|FORMAT
specifier|static
specifier|final
name|int
name|FORMAT
init|=
literal|0
decl_stmt|;
comment|// Changed strings to UTF8
DECL|field|FORMAT_VERSION_UTF8_LENGTH_IN_BYTES
specifier|static
specifier|final
name|int
name|FORMAT_VERSION_UTF8_LENGTH_IN_BYTES
init|=
literal|1
decl_stmt|;
comment|// Lucene 3.0: Removal of compressed fields
DECL|field|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
init|=
literal|2
decl_stmt|;
comment|// NOTE: if you introduce a new format, make it 1 higher
comment|// than the current one, and always change this if you
comment|// switch to a new format!
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsStream
specifier|private
name|IndexOutput
name|fieldsStream
decl_stmt|;
DECL|field|indexStream
specifier|private
name|IndexOutput
name|indexStream
decl_stmt|;
DECL|field|doClose
specifier|private
name|boolean
name|doClose
decl_stmt|;
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|final
name|String
name|fieldsName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
decl_stmt|;
try|try
block|{
name|fieldsStream
operator|=
name|d
operator|.
name|createOutput
argument_list|(
name|fieldsName
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeInt
argument_list|(
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
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Suppress so we keep throwing the original exception
block|}
try|try
block|{
name|d
operator|.
name|deleteFile
argument_list|(
name|fieldsName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Suppress so we keep throwing the original exception
block|}
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
specifier|final
name|String
name|indexName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
try|try
block|{
name|indexStream
operator|=
name|d
operator|.
name|createOutput
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
name|indexStream
operator|.
name|writeInt
argument_list|(
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
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{             }
try|try
block|{
name|d
operator|.
name|deleteFile
argument_list|(
name|fieldsName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Suppress so we keep throwing the original exception
block|}
try|try
block|{
name|d
operator|.
name|deleteFile
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Suppress so we keep throwing the original exception
block|}
block|}
block|}
name|doClose
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|IndexOutput
name|fdx
parameter_list|,
name|IndexOutput
name|fdt
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|fieldsStream
operator|=
name|fdt
expr_stmt|;
name|indexStream
operator|=
name|fdx
expr_stmt|;
name|doClose
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|setFieldsStream
name|void
name|setFieldsStream
parameter_list|(
name|IndexOutput
name|stream
parameter_list|)
block|{
name|this
operator|.
name|fieldsStream
operator|=
name|stream
expr_stmt|;
block|}
comment|// Writes the contents of buffer into the fields stream
comment|// and adds a new entry for this document into the index
comment|// stream.  This assumes the buffer was already written
comment|// in the correct fields format.
DECL|method|flushDocument
name|void
name|flushDocument
parameter_list|(
name|int
name|numStoredFields
parameter_list|,
name|RAMOutputStream
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|numStoredFields
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|fieldsStream
argument_list|)
expr_stmt|;
block|}
DECL|method|skipDocument
name|void
name|skipDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|fieldsStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doClose
condition|)
block|{
try|try
block|{
if|if
condition|(
name|fieldsStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fieldsStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|indexStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe2
parameter_list|)
block|{
comment|// Ignore so we throw only first IOException hit
block|}
throw|throw
name|ioe
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|writeField
specifier|final
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|fi
parameter_list|,
name|Fieldable
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|fi
operator|.
name|number
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
name|bits
operator||=
name|FieldsWriter
operator|.
name|FIELD_IS_TOKENIZED
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
name|bits
operator||=
name|FieldsWriter
operator|.
name|FIELD_IS_BINARY
expr_stmt|;
name|fieldsStream
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
block|{
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
specifier|final
name|int
name|offset
decl_stmt|;
name|data
operator|=
name|field
operator|.
name|getBinaryValue
argument_list|()
expr_stmt|;
name|len
operator|=
name|field
operator|.
name|getBinaryLength
argument_list|()
expr_stmt|;
name|offset
operator|=
name|field
operator|.
name|getBinaryOffset
argument_list|()
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldsStream
operator|.
name|writeString
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Bulk write a contiguous series of documents.  The      *  lengths array is the length (in bytes) of each raw      *  document.  The stream IndexInput is the      *  fieldsStream from which we should bulk-copy all      *  bytes. */
DECL|method|addRawDocuments
specifier|final
name|void
name|addRawDocuments
parameter_list|(
name|IndexInput
name|stream
parameter_list|,
name|int
index|[]
name|lengths
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|position
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|position
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|position
operator|+=
name|lengths
index|[
name|i
index|]
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|copyBytes
argument_list|(
name|stream
argument_list|,
name|position
operator|-
name|start
argument_list|)
expr_stmt|;
assert|assert
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
operator|==
name|position
assert|;
block|}
DECL|method|addDocument
specifier|final
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|storedCount
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Fieldable
argument_list|>
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|storedCount
operator|++
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|storedCount
argument_list|)
expr_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|writeField
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
