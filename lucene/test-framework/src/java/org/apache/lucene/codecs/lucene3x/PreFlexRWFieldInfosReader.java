begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|Set
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
name|DocValues
operator|.
name|Type
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
name|IndexFormatTooNewException
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
name|IndexFormatTooOldException
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
name|SegmentInfo
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
comment|/**  * @lucene.internal  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PreFlexRWFieldInfosReader
class|class
name|PreFlexRWFieldInfosReader
extends|extends
name|FieldInfosReader
block|{
DECL|field|FORMAT_MINIMUM
specifier|static
specifier|final
name|int
name|FORMAT_MINIMUM
init|=
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_START
decl_stmt|;
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
name|IOContext
name|iocontext
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
literal|""
argument_list|,
name|PreFlexRWFieldInfosWriter
operator|.
name|FIELD_INFOS_EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|iocontext
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|format
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|>
name|FORMAT_MINIMUM
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
name|input
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
block|}
if|if
condition|(
name|format
operator|<
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_CURRENT
operator|&&
name|format
operator|!=
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_PREFLEX_RW
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
name|input
argument_list|,
name|format
argument_list|,
name|FORMAT_MINIMUM
argument_list|,
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_CURRENT
argument_list|)
throw|;
block|}
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
name|FieldInfo
name|infos
index|[]
init|=
operator|new
name|FieldInfo
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
name|format
operator|==
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_PREFLEX_RW
condition|?
name|input
operator|.
name|readInt
argument_list|()
else|:
name|i
decl_stmt|;
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
name|PreFlexRWFieldInfosWriter
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
name|PreFlexRWFieldInfosWriter
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
name|PreFlexRWFieldInfosWriter
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
name|PreFlexRWFieldInfosWriter
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
operator|(
name|bits
operator|&
name|PreFlexRWFieldInfosWriter
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
name|PreFlexRWFieldInfosWriter
operator|.
name|OMIT_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|format
operator|<=
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_OMIT_POSITIONS
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupt fieldinfos, OMIT_POSITIONS set but format="
operator|+
name|format
operator|+
literal|" (resource: "
operator|+
name|input
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
comment|// LUCENE-3027: past indices were able to write
comment|// storePayloads=true when omitTFAP is also true,
comment|// which is invalid.  We correct that, here:
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
name|Type
name|normType
init|=
name|isIndexed
operator|&&
operator|!
name|omitNorms
condition|?
name|Type
operator|.
name|FIXED_INTS_8
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|format
operator|==
name|PreFlexRWFieldInfosWriter
operator|.
name|FORMAT_PREFLEX_RW
operator|&&
name|normType
operator|!=
literal|null
condition|)
block|{
comment|// RW can have norms but doesn't write them
name|normType
operator|=
name|input
operator|.
name|readByte
argument_list|()
operator|!=
literal|0
condition|?
name|Type
operator|.
name|FIXED_INTS_8
else|:
literal|null
expr_stmt|;
block|}
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
literal|null
argument_list|,
name|normType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|getFilePointer
argument_list|()
operator|!=
name|input
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"did not read all bytes from file \""
operator|+
name|fileName
operator|+
literal|"\": read "
operator|+
name|input
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" vs size "
operator|+
name|input
operator|.
name|length
argument_list|()
operator|+
literal|" (resource: "
operator|+
name|input
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
operator|new
name|FieldInfos
argument_list|(
name|infos
argument_list|)
return|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|PreFlexRWFieldInfosWriter
operator|.
name|FIELD_INFOS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
