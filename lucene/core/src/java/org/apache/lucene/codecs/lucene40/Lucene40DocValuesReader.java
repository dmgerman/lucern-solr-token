begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40FieldInfosReader
operator|.
name|LegacyDocValuesType
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
name|store
operator|.
name|CompoundFileDirectory
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
name|IOUtils
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
begin_class
DECL|class|Lucene40DocValuesReader
class|class
name|Lucene40DocValuesReader
extends|extends
name|DocValuesProducer
block|{
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|SegmentReadState
name|state
decl_stmt|;
DECL|field|legacyKey
specifier|private
specifier|final
name|String
name|legacyKey
decl_stmt|;
comment|// ram instances we have already loaded
DECL|field|numericInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericDocValues
argument_list|>
name|numericInstances
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|binaryInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryDocValues
argument_list|>
name|binaryInstances
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|BinaryDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|sortedInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedDocValues
argument_list|>
name|sortedInstances
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|SortedDocValues
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Lucene40DocValuesReader
name|Lucene40DocValuesReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|legacyKey
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|legacyKey
operator|=
name|legacyKey
expr_stmt|;
name|this
operator|.
name|dir
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|filename
argument_list|,
name|state
operator|.
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
specifier|synchronized
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|instance
init|=
name|numericInstances
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
switch|switch
condition|(
name|LegacyDocValuesType
operator|.
name|valueOf
argument_list|(
name|field
operator|.
name|getAttribute
argument_list|(
name|legacyKey
argument_list|)
argument_list|)
condition|)
block|{
case|case
name|VAR_INTS
case|:
name|instance
operator|=
name|loadVarIntsField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|instance
operator|=
name|loadByteField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|instance
operator|=
name|loadShortField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|instance
operator|=
name|loadIntField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|instance
operator|=
name|loadLongField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|instance
operator|=
name|loadFloatField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|instance
operator|=
name|loadDoubleField
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|numericInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|loadVarIntsField
specifier|private
name|NumericDocValues
name|loadVarIntsField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|byte
name|header
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|header
operator|==
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_FIXED_64
condition|)
block|{
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
specifier|final
name|long
name|values
index|[]
init|=
operator|new
name|long
index|[
name|maxDoc
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
name|header
operator|==
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_PACKED
condition|)
block|{
specifier|final
name|long
name|minValue
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|defaultValue
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
init|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|input
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|long
name|value
init|=
name|reader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
name|defaultValue
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|minValue
operator|+
name|value
return|;
block|}
block|}
block|}
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid VAR_INTS header byte: "
operator|+
name|header
operator|+
literal|" (resource="
operator|+
name|input
operator|+
literal|")"
argument_list|)
throw|;
block|}
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadByteField
specifier|private
name|NumericDocValues
name|loadByteField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
specifier|final
name|byte
name|values
index|[]
init|=
operator|new
name|byte
index|[
name|maxDoc
index|]
decl_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadShortField
specifier|private
name|NumericDocValues
name|loadShortField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
specifier|final
name|short
name|values
index|[]
init|=
operator|new
name|short
index|[
name|maxDoc
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadIntField
specifier|private
name|NumericDocValues
name|loadIntField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
specifier|final
name|int
name|values
index|[]
init|=
operator|new
name|int
index|[
name|maxDoc
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadLongField
specifier|private
name|NumericDocValues
name|loadLongField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
specifier|final
name|long
name|values
index|[]
init|=
operator|new
name|long
index|[
name|maxDoc
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadFloatField
specifier|private
name|NumericDocValues
name|loadFloatField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|FLOATS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|FLOATS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|FLOATS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
specifier|final
name|int
name|values
index|[]
init|=
operator|new
name|int
index|[
name|maxDoc
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadDoubleField
specifier|private
name|NumericDocValues
name|loadDoubleField
parameter_list|(
name|FieldInfo
name|field
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|dir
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
name|input
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|FLOATS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|FLOATS_VERSION_START
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|FLOATS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
specifier|final
name|long
name|values
index|[]
init|=
operator|new
name|long
index|[
name|maxDoc
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
block|}
return|;
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
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
specifier|synchronized
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
specifier|synchronized
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
name|AssertionError
argument_list|()
throw|;
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
