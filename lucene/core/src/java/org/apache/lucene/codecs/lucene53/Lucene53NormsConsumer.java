begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.lucene53
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene53
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
name|NormsConsumer
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
name|SegmentWriteState
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
name|IOUtils
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene53
operator|.
name|Lucene53NormsFormat
operator|.
name|VERSION_CURRENT
import|;
end_import
begin_comment
comment|/**  * Writer for {@link Lucene53NormsFormat}  */
end_comment
begin_class
DECL|class|Lucene53NormsConsumer
class|class
name|Lucene53NormsConsumer
extends|extends
name|NormsConsumer
block|{
DECL|field|data
DECL|field|meta
name|IndexOutput
name|data
decl_stmt|,
name|meta
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|Lucene53NormsConsumer
name|Lucene53NormsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|dataCodec
parameter_list|,
name|String
name|dataExtension
parameter_list|,
name|String
name|metaCodec
parameter_list|,
name|String
name|metaExtension
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|String
name|dataName
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
name|dataExtension
argument_list|)
decl_stmt|;
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|dataName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|String
name|metaName
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
name|metaExtension
argument_list|)
decl_stmt|;
name|meta
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|meta
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
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
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addNormsField
specifier|public
name|void
name|addNormsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
if|if
condition|(
name|nv
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal norms data for field "
operator|+
name|field
operator|.
name|name
operator|+
literal|", got null for value: "
operator|+
name|count
argument_list|)
throw|;
block|}
specifier|final
name|long
name|v
init|=
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|!=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal norms data for field "
operator|+
name|field
operator|.
name|name
operator|+
literal|", expected count="
operator|+
name|maxDoc
operator|+
literal|", got="
operator|+
name|count
argument_list|)
throw|;
block|}
if|if
condition|(
name|minValue
operator|==
name|maxValue
condition|)
block|{
name|addConstant
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|addByte1
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Short
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Short
operator|.
name|MAX_VALUE
condition|)
block|{
name|addByte2
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Integer
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|addByte4
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addByte8
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addConstant
specifier|private
name|void
name|addConstant
parameter_list|(
name|long
name|constant
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|constant
argument_list|)
expr_stmt|;
block|}
DECL|method|addByte1
specifier|private
name|void
name|addByte1
parameter_list|(
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|value
range|:
name|values
control|)
block|{
name|data
operator|.
name|writeByte
argument_list|(
name|value
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addByte2
specifier|private
name|void
name|addByte2
parameter_list|(
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|value
range|:
name|values
control|)
block|{
name|data
operator|.
name|writeShort
argument_list|(
name|value
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addByte4
specifier|private
name|void
name|addByte4
parameter_list|(
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|value
range|:
name|values
control|)
block|{
name|data
operator|.
name|writeInt
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addByte8
specifier|private
name|void
name|addByte8
parameter_list|(
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|8
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|value
range|:
name|values
control|)
block|{
name|data
operator|.
name|writeLong
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// write EOF marker
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|meta
argument_list|)
expr_stmt|;
comment|// write checksum
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// write checksum
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
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
name|meta
operator|=
name|data
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
