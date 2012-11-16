begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene41.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|values
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
name|PerDocProducer
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
name|codecs
operator|.
name|SimpleDVProducer
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
name|SimpleDocValuesFormat
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
name|IOUtils
import|;
end_import
begin_class
DECL|class|Lucene41SimpleDocValuesFormat
specifier|public
class|class
name|Lucene41SimpleDocValuesFormat
extends|extends
name|SimpleDocValuesFormat
block|{
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|SimpleDVConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene41DocValuesConsumer
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|SimpleDVProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit fixme
comment|//return new Lucene41PerDocProducer(state);
return|return
literal|null
return|;
block|}
comment|//nocommit this is equivalent to sep - we should pack in CFS
DECL|class|Lucene41PerDocProducer
specifier|private
specifier|static
specifier|final
class|class
name|Lucene41PerDocProducer
extends|extends
name|PerDocProducerBase
block|{
DECL|field|docValues
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
decl_stmt|;
comment|/**      * Creates a new {@link Lucene41PerDocProducer} instance and loads all      * {@link DocValues} instances for this segment and codec.      */
DECL|method|Lucene41PerDocProducer
specifier|public
name|Lucene41PerDocProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
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
annotation|@
name|Override
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
name|Type
name|type
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|VAR_INTS
case|:
case|case
name|FLOAT_32
case|:
case|case
name|FLOAT_64
case|:
return|return
operator|new
name|Lucene41NumericDocValuesProducer
argument_list|(
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DV_SEGMENT_SUFFIX
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
argument_list|,
name|docCount
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
comment|//nocommit cose in case of an exception
name|IndexInput
name|dataIn
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DV_SEGMENT_SUFFIX
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|IndexInput
name|indexIn
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DV_SEGMENT_SUFFIX
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|Lucene41BinaryDocValuesProducer
argument_list|(
name|dataIn
argument_list|,
name|indexIn
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unrecognized index values mode "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
