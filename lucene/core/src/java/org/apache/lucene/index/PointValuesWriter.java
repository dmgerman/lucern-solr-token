begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|PointReader
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
name|PointWriter
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
name|ByteBlockPool
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
begin_comment
comment|/** Buffers up pending byte[][] value(s) per doc, then flushes when segment flushes. */
end_comment
begin_class
DECL|class|PointValuesWriter
class|class
name|PointValuesWriter
block|{
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|ByteBlockPool
name|bytes
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|docIDs
specifier|private
name|int
index|[]
name|docIDs
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|packedValue
specifier|private
specifier|final
name|byte
index|[]
name|packedValue
decl_stmt|;
DECL|method|PointValuesWriter
specifier|public
name|PointValuesWriter
parameter_list|(
name|DocumentsWriterPerThread
name|docWriter
parameter_list|,
name|FieldInfo
name|fieldInfo
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
name|iwBytesUsed
operator|=
name|docWriter
operator|.
name|bytesUsed
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
operator|new
name|ByteBlockPool
argument_list|(
name|docWriter
operator|.
name|byteBlockAllocator
argument_list|)
expr_stmt|;
name|docIDs
operator|=
operator|new
name|int
index|[
literal|16
index|]
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
literal|16
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|packedValue
operator|=
operator|new
name|byte
index|[
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|*
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
index|]
expr_stmt|;
block|}
comment|// TODO: if exactly the same value is added to exactly the same doc, should we dedup?
DECL|method|addPackedValue
specifier|public
name|void
name|addPackedValue
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
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field="
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|": point value cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|.
name|length
operator|!=
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|*
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field="
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|": this field's value has length="
operator|+
name|value
operator|.
name|length
operator|+
literal|" but should be "
operator|+
operator|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|*
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|docIDs
operator|.
name|length
operator|==
name|numDocs
condition|)
block|{
name|docIDs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docIDs
argument_list|,
name|numDocs
operator|+
literal|1
argument_list|)
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|docIDs
operator|.
name|length
operator|-
name|numDocs
operator|)
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|docIDs
index|[
name|numDocs
index|]
operator|=
name|docID
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|PointWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|PointReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName must be the same"
argument_list|)
throw|;
block|}
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
name|bytes
operator|.
name|readBytes
argument_list|(
name|packedValue
operator|.
name|length
operator|*
name|i
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|,
name|packedValue
operator|.
name|length
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|docIDs
index|[
name|i
index|]
argument_list|,
name|packedValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
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
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{                         }
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
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
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
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
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
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
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
