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
name|BytesBaseSource
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
name|BytesWriterBase
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
name|AttributeSource
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|// Simplest storage: stores fixed length byte[] per
end_comment
begin_comment
comment|// document, with no dedup and no sorting.
end_comment
begin_class
DECL|class|FixedStraightBytesImpl
class|class
name|FixedStraightBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"FixedStraightBytes"
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
specifier|static
class|class
name|Writer
extends|extends
name|BytesWriterBase
block|{
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
comment|// start at -1 if the first added value is> 0
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|oneRecord
specifier|private
name|byte
index|[]
name|oneRecord
decl_stmt|;
DECL|method|Writer
specifier|protected
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
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
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// nocommit - impl bulk copy here!
annotation|@
name|Override
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
name|initDataOut
argument_list|()
expr_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|oneRecord
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|size
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expected bytes size="
operator|+
name|size
operator|+
literal|" but got "
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
name|fill
argument_list|(
name|docID
argument_list|)
expr_stmt|;
assert|assert
name|bytes
operator|.
name|bytes
operator|.
name|length
operator|>=
name|bytes
operator|.
name|length
assert|;
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.apache.lucene.index.values.Writer#merge(org.apache.lucene.index.values.Writer.MergeState)      */
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|MergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|.
name|bits
operator|==
literal|null
operator|&&
name|state
operator|.
name|reader
operator|instanceof
name|Reader
condition|)
block|{
name|Reader
name|reader
init|=
operator|(
name|Reader
operator|)
name|state
operator|.
name|reader
decl_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|reader
operator|.
name|maxDoc
decl_stmt|;
if|if
condition|(
name|maxDocs
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|reader
operator|.
name|size
expr_stmt|;
name|initDataOut
argument_list|()
expr_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|oneRecord
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
block|}
name|fill
argument_list|(
name|state
operator|.
name|docBase
argument_list|)
expr_stmt|;
comment|// nocommit should we add a transfer to API to each reader?
name|datOut
operator|.
name|copyBytes
argument_list|(
name|reader
operator|.
name|cloneData
argument_list|()
argument_list|,
name|size
operator|*
name|maxDocs
argument_list|)
expr_stmt|;
name|lastDocID
operator|+=
name|maxDocs
operator|-
literal|1
expr_stmt|;
block|}
else|else
name|super
operator|.
name|merge
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
comment|// Fills up to but not including this docID
DECL|method|fill
specifier|private
name|void
name|fill
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|size
operator|>=
literal|0
assert|;
for|for
control|(
name|int
name|i
init|=
name|lastDocID
operator|+
literal|1
init|;
name|i
operator|<
name|docID
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeBytes
argument_list|(
name|oneRecord
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
comment|// no data added
return|return;
name|fill
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|super
operator|.
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|BytesReaderBase
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|Reader
name|Reader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
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
literal|false
argument_list|)
expr_stmt|;
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
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
name|Source
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|,
name|maxDoc
argument_list|)
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
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|Source
specifier|private
specifier|static
class|class
name|Source
extends|extends
name|BytesBaseSource
block|{
comment|// TODO: paged data
DECL|field|data
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
DECL|field|bytesRef
specifier|private
specifier|final
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|Source
specifier|public
name|Source
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
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
specifier|final
name|int
name|sizeInBytes
init|=
name|size
operator|*
name|maxDoc
decl_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|sizeInBytes
index|]
expr_stmt|;
assert|assert
name|data
operator|.
name|length
operator|<=
name|datIn
operator|.
name|length
argument_list|()
operator|:
literal|" file size is less than the expected size diff: "
operator|+
operator|(
name|data
operator|.
name|length
operator|-
name|datIn
operator|.
name|length
argument_list|()
operator|)
operator|+
literal|" size: "
operator|+
name|size
operator|+
literal|" maxDoc "
operator|+
name|maxDoc
operator|+
literal|" pos: "
operator|+
name|datIn
operator|.
name|getFilePointer
argument_list|()
assert|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|sizeInBytes
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|bytes
operator|=
name|data
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bytes
specifier|public
name|BytesRef
name|bytes
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|bytesRef
operator|.
name|offset
operator|=
name|docID
operator|*
name|size
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|data
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FixedStraightBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|size
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
DECL|class|FixedStraightBytesEnum
specifier|private
specifier|static
specifier|final
class|class
name|FixedStraightBytesEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fp
specifier|private
specifier|final
name|long
name|fp
decl_stmt|;
DECL|field|ref
specifier|private
specifier|final
name|BytesRef
name|ref
decl_stmt|;
DECL|method|FixedStraightBytesEnum
specifier|public
name|FixedStraightBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|Values
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|)
expr_stmt|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|ref
operator|=
name|attr
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|ref
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|fp
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
block|{
name|ref
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
operator|(
name|target
operator|-
literal|1
operator|)
operator|!=
name|pos
condition|)
comment|// pos inc == 1
name|datIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
name|target
operator|*
name|size
argument_list|)
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|BYTES_FIXED_STRAIGHT
return|;
block|}
block|}
block|}
end_class
end_unit
