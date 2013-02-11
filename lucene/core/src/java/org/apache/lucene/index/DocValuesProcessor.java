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
name|codecs
operator|.
name|DocValuesFormat
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
name|IOUtils
import|;
end_import
begin_class
DECL|class|DocValuesProcessor
specifier|final
class|class
name|DocValuesProcessor
extends|extends
name|StoredFieldsConsumer
block|{
comment|// TODO: somewhat wasteful we also keep a map here; would
comment|// be more efficient if we could "reuse" the map/hash
comment|// lookup DocFieldProcessor already did "above"
DECL|field|writers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DocValuesWriter
argument_list|>
name|writers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocValuesWriter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|method|DocValuesProcessor
specifier|public
name|DocValuesProcessor
parameter_list|(
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|int
name|docID
parameter_list|,
name|StorableField
name|field
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
specifier|final
name|DocValuesType
name|dvType
init|=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|docValueType
argument_list|()
decl_stmt|;
if|if
condition|(
name|dvType
operator|!=
literal|null
condition|)
block|{
name|fieldInfo
operator|.
name|setDocValuesType
argument_list|(
name|dvType
argument_list|)
expr_stmt|;
if|if
condition|(
name|dvType
operator|==
name|DocValuesType
operator|.
name|BINARY
condition|)
block|{
name|addBinaryField
argument_list|(
name|fieldInfo
argument_list|,
name|docID
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dvType
operator|==
name|DocValuesType
operator|.
name|SORTED
condition|)
block|{
name|addSortedField
argument_list|(
name|fieldInfo
argument_list|,
name|docID
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dvType
operator|==
name|DocValuesType
operator|.
name|SORTED_SET
condition|)
block|{
name|addSortedSetField
argument_list|(
name|fieldInfo
argument_list|,
name|docID
argument_list|,
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dvType
operator|==
name|DocValuesType
operator|.
name|NUMERIC
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|field
operator|.
name|numericValue
argument_list|()
operator|instanceof
name|Long
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal type "
operator|+
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
literal|": DocValues types must be Long"
argument_list|)
throw|;
block|}
name|addNumericField
argument_list|(
name|fieldInfo
argument_list|,
name|docID
argument_list|,
name|field
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
literal|false
operator|:
literal|"unrecognized DocValues.Type: "
operator|+
name|dvType
assert|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|flush
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|writers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|DocValuesFormat
name|fmt
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getCodec
argument_list|()
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
name|DocValuesConsumer
name|dvConsumer
init|=
name|fmt
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|DocValuesWriter
name|writer
range|:
name|writers
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|finish
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|(
name|state
argument_list|,
name|dvConsumer
argument_list|)
expr_stmt|;
block|}
comment|// TODO: catch missing DV fields here?  else we have
comment|// null/"" depending on how docs landed in segments?
comment|// but we can't detect all cases, and we should leave
comment|// this behavior undefined. dv is not "schemaless": its column-stride.
name|writers
operator|.
name|clear
argument_list|()
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|dvConsumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dvConsumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|addBinaryField
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|DocValuesWriter
name|writer
init|=
name|writers
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
name|BinaryDocValuesWriter
name|binaryWriter
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|binaryWriter
operator|=
operator|new
name|BinaryDocValuesWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|writers
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|binaryWriter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|writer
operator|instanceof
name|BinaryDocValuesWriter
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Incompatible DocValues type: field \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" changed from "
operator|+
name|getTypeDesc
argument_list|(
name|writer
argument_list|)
operator|+
literal|" to binary"
argument_list|)
throw|;
block|}
else|else
block|{
name|binaryWriter
operator|=
operator|(
name|BinaryDocValuesWriter
operator|)
name|writer
expr_stmt|;
block|}
name|binaryWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addSortedField
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|DocValuesWriter
name|writer
init|=
name|writers
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
name|SortedDocValuesWriter
name|sortedWriter
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|sortedWriter
operator|=
operator|new
name|SortedDocValuesWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|writers
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|sortedWriter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|writer
operator|instanceof
name|SortedDocValuesWriter
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Incompatible DocValues type: field \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" changed from "
operator|+
name|getTypeDesc
argument_list|(
name|writer
argument_list|)
operator|+
literal|" to sorted"
argument_list|)
throw|;
block|}
else|else
block|{
name|sortedWriter
operator|=
operator|(
name|SortedDocValuesWriter
operator|)
name|writer
expr_stmt|;
block|}
name|sortedWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addSortedSetField
name|void
name|addSortedSetField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|DocValuesWriter
name|writer
init|=
name|writers
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
name|SortedSetDocValuesWriter
name|sortedSetWriter
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|sortedSetWriter
operator|=
operator|new
name|SortedSetDocValuesWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|writers
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|sortedSetWriter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|writer
operator|instanceof
name|SortedSetDocValuesWriter
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Incompatible DocValues type: field \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" changed from "
operator|+
name|getTypeDesc
argument_list|(
name|writer
argument_list|)
operator|+
literal|" to sorted"
argument_list|)
throw|;
block|}
else|else
block|{
name|sortedSetWriter
operator|=
operator|(
name|SortedSetDocValuesWriter
operator|)
name|writer
expr_stmt|;
block|}
name|sortedSetWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addNumericField
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|docID
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|DocValuesWriter
name|writer
init|=
name|writers
operator|.
name|get
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
name|NumericDocValuesWriter
name|numericWriter
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|numericWriter
operator|=
operator|new
name|NumericDocValuesWriter
argument_list|(
name|fieldInfo
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|writers
operator|.
name|put
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|numericWriter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|writer
operator|instanceof
name|NumericDocValuesWriter
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Incompatible DocValues type: field \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" changed from "
operator|+
name|getTypeDesc
argument_list|(
name|writer
argument_list|)
operator|+
literal|" to numeric"
argument_list|)
throw|;
block|}
else|else
block|{
name|numericWriter
operator|=
operator|(
name|NumericDocValuesWriter
operator|)
name|writer
expr_stmt|;
block|}
name|numericWriter
operator|.
name|addValue
argument_list|(
name|docID
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getTypeDesc
specifier|private
name|String
name|getTypeDesc
parameter_list|(
name|DocValuesWriter
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|BinaryDocValuesWriter
condition|)
block|{
return|return
literal|"binary"
return|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|NumericDocValuesWriter
condition|)
block|{
return|return
literal|"numeric"
return|;
block|}
else|else
block|{
assert|assert
name|obj
operator|instanceof
name|SortedDocValuesWriter
assert|;
return|return
literal|"sorted"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|DocValuesWriter
name|writer
range|:
name|writers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|writer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
block|}
name|writers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
