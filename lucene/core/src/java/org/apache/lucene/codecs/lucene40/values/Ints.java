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
name|DocValuesArraySource
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
name|index
operator|.
name|DocValues
operator|.
name|Source
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
name|index
operator|.
name|DocValues
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
name|IndexableField
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
begin_comment
comment|/**  * Stores ints packed and fixed with fixed-bit precision.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Ints
specifier|public
specifier|final
class|class
name|Ints
block|{
DECL|field|CODEC_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Ints"
decl_stmt|;
DECL|field|VERSION_START
specifier|protected
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|protected
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|method|Ints
specifier|private
name|Ints
parameter_list|()
block|{   }
DECL|method|getWriter
specifier|public
specifier|static
name|DocValuesConsumer
name|getWriter
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
name|Type
name|type
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|type
operator|==
name|Type
operator|.
name|VAR_INTS
condition|?
operator|new
name|PackedIntValues
operator|.
name|PackedIntsWriter
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
else|:
operator|new
name|IntsWriter
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
specifier|static
name|DocValues
name|getValues
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|Type
name|type
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|type
operator|==
name|Type
operator|.
name|VAR_INTS
condition|?
operator|new
name|PackedIntValues
operator|.
name|PackedIntsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|numDocs
argument_list|,
name|context
argument_list|)
else|:
operator|new
name|IntsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|numDocs
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
block|}
DECL|method|sizeToType
specifier|private
specifier|static
name|Type
name|sizeToType
parameter_list|(
name|int
name|size
parameter_list|)
block|{
switch|switch
condition|(
name|size
condition|)
block|{
case|case
literal|1
case|:
return|return
name|Type
operator|.
name|FIXED_INTS_8
return|;
case|case
literal|2
case|:
return|return
name|Type
operator|.
name|FIXED_INTS_16
return|;
case|case
literal|4
case|:
return|return
name|Type
operator|.
name|FIXED_INTS_32
return|;
case|case
literal|8
case|:
return|return
name|Type
operator|.
name|FIXED_INTS_64
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal size "
operator|+
name|size
argument_list|)
throw|;
block|}
block|}
DECL|method|typeToSize
specifier|private
specifier|static
name|int
name|typeToSize
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
return|return
literal|2
return|;
case|case
name|FIXED_INTS_32
case|:
return|return
literal|4
return|;
case|case
name|FIXED_INTS_64
case|:
return|return
literal|8
return|;
case|case
name|FIXED_INTS_8
case|:
return|return
literal|1
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal type "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
DECL|class|IntsWriter
specifier|static
class|class
name|IntsWriter
extends|extends
name|FixedStraightBytesImpl
operator|.
name|Writer
block|{
DECL|field|template
specifier|private
specifier|final
name|DocValuesArraySource
name|template
decl_stmt|;
DECL|method|IntsWriter
specifier|public
name|IntsWriter
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
parameter_list|,
name|Type
name|valueType
parameter_list|)
throws|throws
name|IOException
block|{
name|this
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
argument_list|,
name|valueType
argument_list|)
expr_stmt|;
block|}
DECL|method|IntsWriter
specifier|protected
name|IntsWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|codecName
parameter_list|,
name|int
name|version
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|Type
name|valueType
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
name|codecName
argument_list|,
name|version
argument_list|,
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|size
operator|=
name|typeToSize
argument_list|(
name|valueType
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesRef
operator|=
operator|new
name|BytesRef
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|template
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|valueType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setMergeBytes
specifier|protected
name|void
name|setMergeBytes
parameter_list|(
name|Source
name|source
parameter_list|,
name|int
name|sourceDoc
parameter_list|)
block|{
specifier|final
name|long
name|value
init|=
name|source
operator|.
name|getInt
argument_list|(
name|sourceDoc
argument_list|)
decl_stmt|;
name|template
operator|.
name|toBytes
argument_list|(
name|value
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|IndexableField
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|template
operator|.
name|toBytes
argument_list|(
name|value
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
name|bytesSpareField
operator|.
name|setBytesValue
argument_list|(
name|bytesRef
argument_list|)
expr_stmt|;
name|super
operator|.
name|add
argument_list|(
name|docID
argument_list|,
name|bytesSpareField
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tryBulkMerge
specifier|protected
name|boolean
name|tryBulkMerge
parameter_list|(
name|DocValues
name|docValues
parameter_list|)
block|{
comment|// only bulk merge if value type is the same otherwise size differs
return|return
name|super
operator|.
name|tryBulkMerge
argument_list|(
name|docValues
argument_list|)
operator|&&
name|docValues
operator|.
name|type
argument_list|()
operator|==
name|template
operator|.
name|type
argument_list|()
return|;
block|}
block|}
DECL|class|IntsReader
specifier|final
specifier|static
class|class
name|IntsReader
extends|extends
name|FixedStraightBytesImpl
operator|.
name|FixedStraightReader
block|{
DECL|field|arrayTemplate
specifier|private
specifier|final
name|DocValuesArraySource
name|arrayTemplate
decl_stmt|;
DECL|method|IntsReader
name|IntsReader
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
parameter_list|,
name|Type
name|type
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
name|maxDoc
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|arrayTemplate
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
expr_stmt|;
assert|assert
name|arrayTemplate
operator|!=
literal|null
assert|;
assert|assert
name|type
operator|==
name|sizeToType
argument_list|(
name|size
argument_list|)
assert|;
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
specifier|final
name|IndexInput
name|indexInput
init|=
name|cloneData
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|arrayTemplate
operator|.
name|newFromInput
argument_list|(
name|indexInput
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|indexInput
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
