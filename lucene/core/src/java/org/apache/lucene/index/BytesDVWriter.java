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
name|BinaryDocValuesConsumer
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
name|SimpleDVConsumer
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
begin_comment
comment|// nocommit name?
end_comment
begin_comment
comment|// nocommit make this a consumer in the chain?
end_comment
begin_class
DECL|class|BytesDVWriter
class|class
name|BytesDVWriter
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
comment|// -2 means not set yet; -1 means length isn't fixed;
comment|// -otherwise it's the fixed length seen so far:
DECL|field|fixedLength
name|int
name|fixedLength
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
DECL|field|totalSize
name|int
name|totalSize
decl_stmt|;
DECL|method|BytesDVWriter
specifier|public
name|BytesDVWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|counter
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
name|counter
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalSize
operator|=
literal|0
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
comment|// nocommit improve message
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null binaryValue not allowed (field="
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|mergeLength
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|mergeLength
argument_list|(
literal|0
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
block|}
DECL|method|mergeLength
specifier|private
name|void
name|mergeLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|fixedLength
operator|==
operator|-
literal|2
condition|)
block|{
name|fixedLength
operator|=
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fixedLength
operator|!=
name|length
condition|)
block|{
name|fixedLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|totalSize
operator|+=
name|length
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
block|{
if|if
condition|(
name|addedValues
operator|<
name|maxDoc
condition|)
block|{
name|mergeLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
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
name|SimpleDVConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValuesConsumer
name|consumer
init|=
name|dvConsumer
operator|.
name|addBinaryField
argument_list|(
name|fieldInfo
argument_list|,
name|fixedLength
operator|>=
literal|0
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bufferedDocCount
init|=
name|addedValues
decl_stmt|;
name|BytesRef
name|value
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|bufferedDocCount
condition|;
name|docID
operator|++
control|)
block|{
name|bytesRefArray
operator|.
name|get
argument_list|(
name|value
argument_list|,
name|docID
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
name|value
operator|.
name|length
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|docID
init|=
name|bufferedDocCount
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|consumer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
comment|//System.out.println("FLUSH");
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|bytesRefArray
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fixedLength
operator|=
operator|-
literal|2
expr_stmt|;
name|maxLength
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class
end_unit
