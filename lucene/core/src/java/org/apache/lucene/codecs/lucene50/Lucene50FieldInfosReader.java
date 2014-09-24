begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|Collections
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
name|FieldInfosReader
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
name|FieldInfo
operator|.
name|DocValuesType
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
operator|.
name|IndexOptions
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
begin_comment
comment|/**  * Lucene 5.0 FieldInfos reader.  *   * @lucene.experimental  * @see Lucene50FieldInfosFormat  */
end_comment
begin_class
DECL|class|Lucene50FieldInfosReader
specifier|final
class|class
name|Lucene50FieldInfosReader
extends|extends
name|FieldInfosReader
block|{
comment|/** Sole constructor. */
DECL|method|Lucene50FieldInfosReader
specifier|public
name|Lucene50FieldInfosReader
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|read
specifier|public
name|FieldInfos
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentName
argument_list|,
name|segmentSuffix
argument_list|,
name|Lucene50FieldInfosFormat
operator|.
name|EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|input
init|=
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
name|FieldInfo
name|infos
index|[]
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|Lucene50FieldInfosFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene50FieldInfosFormat
operator|.
name|FORMAT_START
argument_list|,
name|Lucene50FieldInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//read in the size
name|infos
operator|=
operator|new
name|FieldInfo
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldNumber
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldNumber
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid field number for field: "
operator|+
name|name
operator|+
literal|", fieldNumber="
operator|+
name|fieldNumber
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|byte
name|bits
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isIndexed
init|=
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|IS_INDEXED
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storeTermVector
init|=
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|STORE_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|omitNorms
init|=
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|OMIT_NORMS
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storePayloads
init|=
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|STORE_PAYLOADS
operator|)
operator|!=
literal|0
decl_stmt|;
specifier|final
name|IndexOptions
name|indexOptions
decl_stmt|;
if|if
condition|(
operator|!
name|isIndexed
condition|)
block|{
name|indexOptions
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|OMIT_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene50FieldInfosFormat
operator|.
name|STORE_OFFSETS_IN_POSTINGS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
expr_stmt|;
block|}
else|else
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
block|}
comment|// DV Types are packed in one byte
name|byte
name|val
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
specifier|final
name|DocValuesType
name|docValuesType
init|=
name|getDocValuesType
argument_list|(
name|input
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|val
operator|&
literal|0x0F
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesType
name|normsType
init|=
name|getDocValuesType
argument_list|(
name|input
argument_list|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|val
operator|>>>
literal|4
operator|)
operator|&
literal|0x0F
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|long
name|dvGen
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|input
operator|.
name|readStringStringMap
argument_list|()
decl_stmt|;
try|try
block|{
name|infos
index|[
name|i
index|]
operator|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|,
name|normsType
argument_list|,
name|dvGen
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|attributes
argument_list|)
argument_list|)
expr_stmt|;
name|infos
index|[
name|i
index|]
operator|.
name|checkConsistency
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid fieldinfo for field: "
operator|+
name|name
operator|+
literal|", fieldNumber="
operator|+
name|fieldNumber
argument_list|,
name|input
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldInfos
argument_list|(
name|infos
argument_list|)
return|;
block|}
block|}
DECL|method|getDocValuesType
specifier|private
specifier|static
name|DocValuesType
name|getDocValuesType
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|NUMERIC
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|2
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|BINARY
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|3
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|SORTED
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|4
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|SORTED_SET
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|5
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|SORTED_NUMERIC
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docvalues byte: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
