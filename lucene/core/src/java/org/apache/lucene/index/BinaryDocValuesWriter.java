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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
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
name|Iterator
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
name|BytesRefArray
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
begin_comment
comment|/** Buffers up pending byte[] per doc, then flushes when  *  segment flushes. */
end_comment
begin_class
DECL|class|BinaryDocValuesWriter
class|class
name|BinaryDocValuesWriter
extends|extends
name|DocValuesWriter
block|{
DECL|field|bytesRefArray
specifier|private
specifier|final
name|BytesRefArray
name|bytesRefArray
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|addedValues
specifier|private
name|int
name|addedValues
init|=
literal|0
decl_stmt|;
DECL|field|emptyBytesRef
specifier|private
specifier|final
name|BytesRef
name|emptyBytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
comment|// nocommit this needs to update bytesUsed?
DECL|method|BinaryDocValuesWriter
specifier|public
name|BinaryDocValuesWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|bytesRefArray
operator|=
operator|new
name|BytesRefArray
argument_list|(
name|iwBytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|bytesRefArray
operator|.
name|bytesUsed
argument_list|()
expr_stmt|;
name|this
operator|.
name|iwBytesUsed
operator|=
name|iwBytesUsed
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|addedValues
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" appears more than once in this document (only one value is allowed per field)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\": null value not allowed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|.
name|length
operator|>
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" is too large, must be<= "
operator|+
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
argument_list|)
throw|;
block|}
comment|// Fill in any holes:
while|while
condition|(
name|addedValues
operator|<
name|docID
condition|)
block|{
name|addedValues
operator|++
expr_stmt|;
name|bytesRefArray
operator|.
name|append
argument_list|(
name|emptyBytesRef
argument_list|)
expr_stmt|;
block|}
name|addedValues
operator|++
expr_stmt|;
name|bytesRefArray
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|updateBytesUsed
argument_list|()
expr_stmt|;
block|}
DECL|method|updateBytesUsed
specifier|private
name|void
name|updateBytesUsed
parameter_list|()
block|{
specifier|final
name|long
name|newBytesUsed
init|=
name|bytesRefArray
operator|.
name|bytesUsed
argument_list|()
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|newBytesUsed
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|newBytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|DocValuesConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|dvConsumer
operator|.
name|addBinaryField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|BytesRef
name|value
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|int
name|upto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|upto
operator|<
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|<
name|bytesRefArray
operator|.
name|size
argument_list|()
condition|)
block|{
name|bytesRefArray
operator|.
name|get
argument_list|(
name|value
argument_list|,
name|upto
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
name|upto
operator|++
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{   }
block|}
end_class
end_unit
