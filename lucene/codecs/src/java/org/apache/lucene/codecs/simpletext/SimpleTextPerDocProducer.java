begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
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
name|simpletext
operator|.
name|SimpleTextDocValuesConsumer
operator|.
name|DOC
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
name|simpletext
operator|.
name|SimpleTextDocValuesConsumer
operator|.
name|END
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
name|simpletext
operator|.
name|SimpleTextDocValuesConsumer
operator|.
name|HEADER
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
name|simpletext
operator|.
name|SimpleTextDocValuesConsumer
operator|.
name|VALUE
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
name|simpletext
operator|.
name|SimpleTextDocValuesConsumer
operator|.
name|VALUE_SIZE
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|PerDocProducerBase
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
name|DocValues
operator|.
name|SortedSource
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
name|BytesRefHash
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
name|StringHelper
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
operator|.
name|Reader
import|;
end_import
begin_comment
comment|/**  * Reads plain-text DocValues.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextPerDocProducer
specifier|public
class|class
name|SimpleTextPerDocProducer
extends|extends
name|PerDocProducerBase
block|{
DECL|field|docValues
specifier|protected
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
decl_stmt|;
DECL|field|comp
specifier|private
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|field|segmentSuffix
specifier|private
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
comment|/**    * Creates a new {@link SimpleTextPerDocProducer} instance and loads all    * {@link DocValues} instances for this segment and codec.    */
DECL|method|SimpleTextPerDocProducer
specifier|public
name|SimpleTextPerDocProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
if|if
condition|(
name|anyDocValuesFields
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|)
condition|)
block|{
name|docValues
operator|=
name|load
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docValues
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
DECL|method|loadDocValues
specifier|protected
name|DocValues
name|loadDocValues
parameter_list|(
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|DocValues
operator|.
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
operator|new
name|SimpleTextDocValues
argument_list|(
name|dir
argument_list|,
name|context
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|comp
argument_list|,
name|segmentSuffix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|closeInternal
specifier|protected
name|void
name|closeInternal
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|closeables
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|closeables
argument_list|)
expr_stmt|;
block|}
DECL|class|SimpleTextDocValues
specifier|private
specifier|static
class|class
name|SimpleTextDocValues
extends|extends
name|DocValues
block|{
DECL|field|docCount
specifier|private
name|int
name|docCount
decl_stmt|;
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
name|super
operator|.
name|close
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
DECL|field|type
specifier|private
name|Type
name|type
decl_stmt|;
DECL|field|comp
specifier|private
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
decl_stmt|;
DECL|field|valueSize
specifier|private
name|int
name|valueSize
decl_stmt|;
DECL|field|input
specifier|private
specifier|final
name|IndexInput
name|input
decl_stmt|;
DECL|method|SimpleTextDocValues
specifier|public
name|SimpleTextDocValues
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IOContext
name|ctx
parameter_list|,
name|Type
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|segmentSuffix
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexInput
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|valueSize
operator|=
name|readHeader
argument_list|(
name|in
argument_list|)
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
name|in
argument_list|)
expr_stmt|;
block|}
block|}
name|input
operator|=
name|in
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexInput
name|in
init|=
name|input
operator|.
name|clone
argument_list|()
decl_stmt|;
try|try
block|{
name|Source
name|source
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|source
operator|=
name|read
argument_list|(
name|in
argument_list|,
operator|new
name|ValueReader
argument_list|(
name|type
argument_list|,
name|docCount
argument_list|,
name|comp
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
name|source
operator|=
name|read
argument_list|(
name|in
argument_list|,
operator|new
name|ValueReader
argument_list|(
name|type
argument_list|,
name|docCount
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown type: "
operator|+
name|type
argument_list|)
throw|;
block|}
assert|assert
name|source
operator|!=
literal|null
assert|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|source
return|;
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
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readHeader
specifier|private
name|int
name|readHeader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|HEADER
argument_list|)
assert|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|VALUE_SIZE
argument_list|)
assert|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|scratch
operator|.
name|offset
operator|+
name|VALUE_SIZE
operator|.
name|length
argument_list|,
name|scratch
argument_list|)
argument_list|)
return|;
block|}
DECL|method|read
specifier|private
name|Source
name|read
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|ValueReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
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
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|DOC
argument_list|)
operator|:
name|scratch
operator|.
name|utf8ToString
argument_list|()
assert|;
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|VALUE
argument_list|)
assert|;
name|reader
operator|.
name|fromString
argument_list|(
name|i
argument_list|,
name|scratch
argument_list|,
name|scratch
operator|.
name|offset
operator|+
name|VALUE
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|scratch
operator|.
name|equals
argument_list|(
name|END
argument_list|)
assert|;
return|return
name|reader
operator|.
name|getSource
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|getSource
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSize
specifier|public
name|int
name|getValueSize
parameter_list|()
block|{
return|return
name|valueSize
return|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
DECL|method|readString
specifier|public
specifier|static
name|String
name|readString
parameter_list|(
name|int
name|offset
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|,
name|scratch
operator|.
name|offset
operator|+
name|offset
argument_list|,
name|scratch
operator|.
name|length
operator|-
name|offset
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
return|;
block|}
DECL|class|ValueReader
specifier|private
specifier|static
specifier|final
class|class
name|ValueReader
block|{
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|shorts
specifier|private
name|short
index|[]
name|shorts
decl_stmt|;
DECL|field|ints
specifier|private
name|int
index|[]
name|ints
decl_stmt|;
DECL|field|longs
specifier|private
name|long
index|[]
name|longs
decl_stmt|;
DECL|field|floats
specifier|private
name|float
index|[]
name|floats
decl_stmt|;
DECL|field|doubles
specifier|private
name|double
index|[]
name|doubles
decl_stmt|;
DECL|field|source
specifier|private
name|Source
name|source
decl_stmt|;
DECL|field|hash
specifier|private
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|scratch
specifier|private
name|BytesRef
name|scratch
decl_stmt|;
DECL|method|ValueReader
specifier|public
name|ValueReader
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|maxDocs
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|Source
name|docValuesArray
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
name|shorts
operator|=
operator|new
name|short
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
operator|.
name|newFromArray
argument_list|(
name|shorts
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|ints
operator|=
operator|new
name|int
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
operator|.
name|newFromArray
argument_list|(
name|ints
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|longs
operator|=
operator|new
name|long
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
operator|.
name|newFromArray
argument_list|(
name|longs
argument_list|)
expr_stmt|;
break|break;
case|case
name|VAR_INTS
case|:
name|longs
operator|=
operator|new
name|long
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
operator|new
name|VarIntsArraySource
argument_list|(
name|type
argument_list|,
name|longs
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|bytes
operator|=
operator|new
name|byte
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
operator|.
name|newFromArray
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|floats
operator|=
operator|new
name|float
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
operator|.
name|newFromArray
argument_list|(
name|floats
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|doubles
operator|=
operator|new
name|double
index|[
name|maxDocs
index|]
expr_stmt|;
name|docValuesArray
operator|=
name|DocValuesArraySource
operator|.
name|forType
argument_list|(
name|type
argument_list|)
operator|.
name|newFromArray
argument_list|(
name|doubles
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
assert|assert
name|comp
operator|!=
literal|null
assert|;
name|hash
operator|=
operator|new
name|BytesRefHash
argument_list|()
expr_stmt|;
name|BytesSource
name|bytesSource
init|=
operator|new
name|BytesSource
argument_list|(
name|type
argument_list|,
name|comp
argument_list|,
name|maxDocs
argument_list|,
name|hash
argument_list|)
decl_stmt|;
name|ints
operator|=
name|bytesSource
operator|.
name|docIdToEntry
expr_stmt|;
name|source
operator|=
name|bytesSource
expr_stmt|;
name|scratch
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|docValuesArray
operator|!=
literal|null
condition|)
block|{
assert|assert
name|source
operator|==
literal|null
assert|;
name|this
operator|.
name|source
operator|=
name|docValuesArray
expr_stmt|;
block|}
block|}
DECL|method|fromString
specifier|public
name|void
name|fromString
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|ref
parameter_list|,
name|int
name|offset
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
assert|assert
name|shorts
operator|!=
literal|null
assert|;
name|shorts
index|[
name|ord
index|]
operator|=
name|Short
operator|.
name|parseShort
argument_list|(
name|readString
argument_list|(
name|offset
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
assert|assert
name|ints
operator|!=
literal|null
assert|;
name|ints
index|[
name|ord
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|offset
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
case|case
name|VAR_INTS
case|:
assert|assert
name|longs
operator|!=
literal|null
assert|;
name|longs
index|[
name|ord
index|]
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|readString
argument_list|(
name|offset
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
assert|assert
name|bytes
operator|!=
literal|null
assert|;
name|bytes
index|[
name|ord
index|]
operator|=
operator|(
name|byte
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|readString
argument_list|(
name|offset
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
assert|assert
name|floats
operator|!=
literal|null
assert|;
name|floats
index|[
name|ord
index|]
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|readString
argument_list|(
name|offset
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
assert|assert
name|doubles
operator|!=
literal|null
assert|;
name|doubles
index|[
name|ord
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|readString
argument_list|(
name|offset
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|scratch
operator|.
name|bytes
operator|=
name|ref
operator|.
name|bytes
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|ref
operator|.
name|length
operator|-
name|offset
expr_stmt|;
name|scratch
operator|.
name|offset
operator|=
name|ref
operator|.
name|offset
operator|+
name|offset
expr_stmt|;
name|int
name|key
init|=
name|hash
operator|.
name|add
argument_list|(
name|scratch
argument_list|)
decl_stmt|;
name|ints
index|[
name|ord
index|]
operator|=
name|key
operator|<
literal|0
condition|?
operator|(
operator|-
name|key
operator|)
operator|-
literal|1
else|:
name|key
expr_stmt|;
break|break;
block|}
block|}
DECL|method|getSource
specifier|public
name|Source
name|getSource
parameter_list|()
block|{
if|if
condition|(
name|source
operator|instanceof
name|BytesSource
condition|)
block|{
operator|(
operator|(
name|BytesSource
operator|)
name|source
operator|)
operator|.
name|maybeSort
argument_list|()
expr_stmt|;
block|}
return|return
name|source
return|;
block|}
block|}
DECL|class|BytesSource
specifier|private
specifier|static
specifier|final
class|class
name|BytesSource
extends|extends
name|SortedSource
block|{
DECL|field|hash
specifier|private
specifier|final
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|docIdToEntry
name|int
index|[]
name|docIdToEntry
decl_stmt|;
DECL|field|sortedEntries
name|int
index|[]
name|sortedEntries
decl_stmt|;
DECL|field|adresses
name|int
index|[]
name|adresses
decl_stmt|;
DECL|field|isSorted
specifier|private
specifier|final
name|boolean
name|isSorted
decl_stmt|;
DECL|method|BytesSource
specifier|protected
name|BytesSource
parameter_list|(
name|Type
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|BytesRefHash
name|hash
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|comp
argument_list|)
expr_stmt|;
name|docIdToEntry
operator|=
operator|new
name|int
index|[
name|maxDoc
index|]
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
name|isSorted
operator|=
name|type
operator|==
name|Type
operator|.
name|BYTES_FIXED_SORTED
operator|||
name|type
operator|==
name|Type
operator|.
name|BYTES_VAR_SORTED
expr_stmt|;
block|}
DECL|method|maybeSort
name|void
name|maybeSort
parameter_list|()
block|{
if|if
condition|(
name|isSorted
condition|)
block|{
name|adresses
operator|=
operator|new
name|int
index|[
name|hash
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|sortedEntries
operator|=
name|hash
operator|.
name|sort
argument_list|(
name|getComparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|adresses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|entry
init|=
name|sortedEntries
index|[
name|i
index|]
decl_stmt|;
name|adresses
index|[
name|entry
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
if|if
condition|(
name|isSorted
condition|)
block|{
return|return
name|hash
operator|.
name|get
argument_list|(
name|sortedEntries
index|[
name|ord
argument_list|(
name|docID
argument_list|)
index|]
argument_list|,
name|ref
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|hash
operator|.
name|get
argument_list|(
name|docIdToEntry
index|[
name|docID
index|]
argument_list|,
name|ref
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|asSortedSource
specifier|public
name|SortedSource
name|asSortedSource
parameter_list|()
block|{
if|if
condition|(
name|isSorted
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|isSorted
assert|;
try|try
block|{
return|return
name|adresses
index|[
name|docIdToEntry
index|[
name|docID
index|]
index|]
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getByOrd
specifier|public
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
assert|assert
name|isSorted
assert|;
return|return
name|hash
operator|.
name|get
argument_list|(
name|sortedEntries
index|[
name|ord
index|]
argument_list|,
name|bytesRef
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocToOrd
specifier|public
name|Reader
name|getDocToOrd
parameter_list|()
block|{
return|return
literal|null
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
return|return
name|hash
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|class|VarIntsArraySource
specifier|private
specifier|static
class|class
name|VarIntsArraySource
extends|extends
name|Source
block|{
DECL|field|array
specifier|private
specifier|final
name|long
index|[]
name|array
decl_stmt|;
DECL|method|VarIntsArraySource
specifier|protected
name|VarIntsArraySource
parameter_list|(
name|Type
name|type
parameter_list|,
name|long
index|[]
name|array
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|array
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
name|DocValuesArraySource
operator|.
name|copyLong
argument_list|(
name|ref
argument_list|,
name|getInt
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
block|}
block|}
end_class
end_unit
