begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|index
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
name|index
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
name|index
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
name|values
operator|.
name|DirectSource
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
comment|// Stores fixed-length byte[] by deref, ie when two docs
end_comment
begin_comment
comment|// have the same value, they store only 1 byte[]
end_comment
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FixedDerefBytesImpl
class|class
name|FixedDerefBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"FixedDerefBytes"
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
DECL|class|Writer
specifier|public
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
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishInternal
specifier|protected
name|void
name|finishInternal
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numValues
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
block|{
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|(
name|size
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
name|numValues
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
block|}
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
name|idxOut
operator|.
name|writeInt
argument_list|(
name|numValues
argument_list|)
expr_stmt|;
name|writeIndex
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|numValues
argument_list|,
name|docToEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FixedDerefReader
specifier|public
specifier|static
class|class
name|FixedDerefReader
extends|extends
name|BytesReaderBase
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|numValuesStored
specifier|private
specifier|final
name|int
name|numValuesStored
decl_stmt|;
DECL|method|FixedDerefReader
name|FixedDerefReader
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
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|,
name|context
argument_list|,
name|ValueType
operator|.
name|BYTES_FIXED_DEREF
argument_list|)
expr_stmt|;
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|numValuesStored
operator|=
name|idxIn
operator|.
name|readInt
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
name|FixedDerefSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|,
name|numValuesStored
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
name|DirectFixedDerefSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|,
name|type
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|FixedDerefSource
specifier|static
specifier|final
class|class
name|FixedDerefSource
extends|extends
name|BytesSourceBase
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|addresses
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|addresses
decl_stmt|;
DECL|method|FixedDerefSource
specifier|protected
name|FixedDerefSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|int
name|size
parameter_list|,
name|long
name|numValues
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
name|size
operator|*
name|numValues
argument_list|,
name|ValueType
operator|.
name|BYTES_FIXED_DEREF
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
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
specifier|final
name|int
name|id
init|=
operator|(
name|int
operator|)
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
operator|(
name|id
operator|*
name|size
operator|)
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
DECL|class|DirectFixedDerefSource
specifier|final
specifier|static
class|class
name|DirectFixedDerefSource
extends|extends
name|DirectSource
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|RandomAccessReaderIterator
name|index
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|DirectFixedDerefSource
name|DirectFixedDerefSource
parameter_list|(
name|IndexInput
name|data
parameter_list|,
name|IndexInput
name|index
parameter_list|,
name|int
name|size
parameter_list|,
name|ValueType
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
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|PackedInts
operator|.
name|getRandomAccessReaderIterator
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
operator|*
name|size
argument_list|)
expr_stmt|;
return|return
name|size
return|;
block|}
block|}
block|}
end_class
end_unit
