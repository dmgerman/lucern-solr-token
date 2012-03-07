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
comment|/**  * Exposes {@link Writer} and reader ({@link Source}) for 32 bit and 64 bit  * floating point values.  *<p>  * Current implementations store either 4 byte or 8 byte floating points with  * full precision without any compression.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Floats
specifier|public
class|class
name|Floats
block|{
DECL|field|CODEC_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Floats"
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
name|IOContext
name|context
parameter_list|,
name|Type
name|type
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FloatsWriter
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
return|return
operator|new
name|FloatsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
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
name|FLOAT_32
case|:
return|return
literal|4
return|;
case|case
name|FLOAT_64
case|:
return|return
literal|8
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
DECL|class|FloatsWriter
specifier|final
specifier|static
class|class
name|FloatsWriter
extends|extends
name|FixedStraightBytesImpl
operator|.
name|Writer
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|template
specifier|private
specifier|final
name|DocValuesArraySource
name|template
decl_stmt|;
DECL|method|FloatsWriter
specifier|public
name|FloatsWriter
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
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|size
operator|=
name|typeToSize
argument_list|(
name|type
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
name|type
argument_list|)
expr_stmt|;
assert|assert
name|template
operator|!=
literal|null
assert|;
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
name|doubleValue
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
name|double
name|value
init|=
name|source
operator|.
name|getFloat
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
block|}
DECL|class|FloatsReader
specifier|final
specifier|static
class|class
name|FloatsReader
extends|extends
name|FixedStraightBytesImpl
operator|.
name|FixedStraightReader
block|{
DECL|field|arrayTemplate
specifier|final
name|DocValuesArraySource
name|arrayTemplate
decl_stmt|;
DECL|method|FloatsReader
name|FloatsReader
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
name|size
operator|==
literal|4
operator|||
name|size
operator|==
literal|8
operator|:
literal|"wrong size="
operator|+
name|size
operator|+
literal|" type="
operator|+
name|type
operator|+
literal|" id="
operator|+
name|id
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
