begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|values
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|BytesReaderBase
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
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|BytesSourceBase
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
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|DerefBytesWriterBase
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
name|Counter
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
name|PagedBytes
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
comment|// Stores variable-length byte[] by deref, ie when two docs
end_comment
begin_comment
comment|// have the same value, they store only 1 byte[] and both
end_comment
begin_comment
comment|// docs reference that single source
end_comment
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|VarDerefBytesImpl
class|class
name|VarDerefBytesImpl
block|{
DECL|field|CODEC_NAME_IDX
specifier|static
specifier|final
name|String
name|CODEC_NAME_IDX
init|=
literal|"VarDerefBytesIdx"
decl_stmt|;
DECL|field|CODEC_NAME_DAT
specifier|static
specifier|final
name|String
name|CODEC_NAME_DAT
init|=
literal|"VarDerefBytesDat"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
comment|/*    * TODO: if impls like this are merged we are bound to the amount of memory we    * can store into a BytesRefHash and therefore how much memory a ByteBlockPool    * can address. This is currently limited to 2GB. While we could extend that    * and use 64bit for addressing this still limits us to the existing main    * memory as all distinct bytes will be loaded up into main memory. We could    * move the byte[] writing to #finish(int) and store the bytes in sorted    * order and merge them in a streamed fashion.     */
DECL|class|Writer
specifier|static
class|class
name|Writer
extends|extends
name|DerefBytesWriterBase
block|{
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME_IDX
argument_list|,
name|CODEC_NAME_DAT
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|Type
operator|.
name|BYTES_VAR_DEREF
argument_list|)
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkSize
specifier|protected
name|void
name|checkSize
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
comment|// allow var bytes sizes
block|}
comment|// Important that we get docCount, in case there were
comment|// some last docs that we didn't see
annotation|@
name|Override
DECL|method|finishInternal
specifier|public
name|void
name|finishInternal
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|fillDefault
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|long
index|[]
name|addresses
init|=
operator|new
name|long
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
name|int
name|addr
init|=
literal|0
decl_stmt|;
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|hash
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
name|addresses
index|[
name|i
index|]
operator|=
name|addr
expr_stmt|;
name|addr
operator|+=
name|writePrefixLength
argument_list|(
name|datOut
argument_list|,
name|bytesRef
argument_list|)
operator|+
name|bytesRef
operator|.
name|length
expr_stmt|;
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
name|bytesRef
operator|.
name|offset
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
comment|// write the max address to read directly on source load
name|idxOut
operator|.
name|writeLong
argument_list|(
name|addr
argument_list|)
expr_stmt|;
name|writeIndex
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|addresses
index|[
name|addresses
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|addresses
argument_list|,
name|docToEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|VarDerefReader
specifier|public
specifier|static
class|class
name|VarDerefReader
extends|extends
name|BytesReaderBase
block|{
DECL|field|totalBytes
specifier|private
specifier|final
name|long
name|totalBytes
decl_stmt|;
DECL|method|VarDerefReader
name|VarDerefReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME_IDX
argument_list|,
name|CODEC_NAME_DAT
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|,
name|context
argument_list|,
name|Type
operator|.
name|BYTES_VAR_DEREF
argument_list|)
expr_stmt|;
name|totalBytes
operator|=
name|idxIn
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|VarDerefSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|totalBytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DirectVarDerefSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|VarDerefSource
specifier|final
specifier|static
class|class
name|VarDerefSource
extends|extends
name|BytesSourceBase
block|{
DECL|field|addresses
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|addresses
decl_stmt|;
DECL|method|VarDerefSource
specifier|public
name|VarDerefSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|long
name|totalBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|totalBytes
argument_list|,
name|Type
operator|.
name|BYTES_VAR_DEREF
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
return|return
name|data
operator|.
name|fillSliceWithPrefix
argument_list|(
name|bytesRef
argument_list|,
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|DirectVarDerefSource
specifier|final
specifier|static
class|class
name|DirectVarDerefSource
extends|extends
name|DirectSource
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|index
decl_stmt|;
DECL|method|DirectVarDerefSource
name|DirectVarDerefSource
parameter_list|(
name|IndexInput
name|data
parameter_list|,
name|IndexInput
name|index
parameter_list|,
name|Type
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|data
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|PackedInts
operator|.
name|getDirectReader
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|protected
name|int
name|position
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|.
name|seek
argument_list|(
name|baseOffset
operator|+
name|index
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|sizeByte
init|=
name|data
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|sizeByte
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
comment|// length is 1 byte
return|return
name|sizeByte
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|sizeByte
operator|&
literal|0x7f
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
operator|)
operator|)
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
