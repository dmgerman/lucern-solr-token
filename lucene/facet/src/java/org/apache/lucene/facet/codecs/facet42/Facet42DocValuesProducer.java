begin_unit
begin_package
DECL|package|org.apache.lucene.facet.codecs.facet42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|codecs
operator|.
name|facet42
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
name|HashMap
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
name|DocValuesProducer
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
name|BinaryDocValues
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
name|NumericDocValues
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
name|SegmentReadState
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|Bits
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
begin_class
DECL|class|Facet42DocValuesProducer
class|class
name|Facet42DocValuesProducer
extends|extends
name|DocValuesProducer
block|{
DECL|field|fields
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Facet42BinaryDocValues
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Facet42BinaryDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|Facet42DocValuesProducer
name|Facet42DocValuesProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|Facet42DocValuesFormat
operator|.
name|EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|Facet42DocValuesFormat
operator|.
name|CODEC
argument_list|,
name|Facet42DocValuesFormat
operator|.
name|VERSION_START
argument_list|,
name|Facet42DocValuesFormat
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|int
name|fieldNumber
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldNumber
operator|!=
operator|-
literal|1
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
operator|new
name|Facet42BinaryDocValues
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|fieldNumber
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"FacetsDocValues only implements binary"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"FacetsDocValues only implements binary"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getSortedSet
specifier|public
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"FacetsDocValues only implements binary"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchAllBits
argument_list|(
name|maxDoc
argument_list|)
return|;
comment|// TODO: have codec impl this?
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
block|{   }
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Facet42BinaryDocValues
name|entry
range|:
name|fields
operator|.
name|values
argument_list|()
control|)
block|{
name|size
operator|+=
name|entry
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|Integer
operator|.
name|SIZE
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
end_class
end_unit
