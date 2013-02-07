begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.cheapbastard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|cheapbastard
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
name|codecs
operator|.
name|diskdv
operator|.
name|DiskDocValuesConsumer
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
name|diskdv
operator|.
name|DiskDocValuesFormat
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
begin_comment
comment|/**  * DocValues format that keeps everything on disk.  *<p>  * Internally there are only 2 field types:  *<ul>  *<li>BINARY: a big byte[].  *<li>NUMERIC: packed ints  *</ul>  * SORTED is encoded as BINARY + NUMERIC  *<p>  * NOTE: Don't use this format in production (its not very efficient).  * Most likely you would want some parts in RAM, other parts on disk.   *<p>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CheapBastardDocValuesFormat
specifier|public
specifier|final
class|class
name|CheapBastardDocValuesFormat
extends|extends
name|DocValuesFormat
block|{
DECL|method|CheapBastardDocValuesFormat
specifier|public
name|CheapBastardDocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"CheapBastard"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|DocValuesConsumer
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
name|DiskDocValuesConsumer
argument_list|(
name|state
argument_list|,
name|DiskDocValuesFormat
operator|.
name|DATA_CODEC
argument_list|,
name|DiskDocValuesFormat
operator|.
name|DATA_EXTENSION
argument_list|,
name|DiskDocValuesFormat
operator|.
name|META_CODEC
argument_list|,
name|DiskDocValuesFormat
operator|.
name|META_EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|DocValuesProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CheapBastardDocValuesProducer
argument_list|(
name|state
argument_list|,
name|DiskDocValuesFormat
operator|.
name|DATA_CODEC
argument_list|,
name|DiskDocValuesFormat
operator|.
name|DATA_EXTENSION
argument_list|,
name|DiskDocValuesFormat
operator|.
name|META_CODEC
argument_list|,
name|DiskDocValuesFormat
operator|.
name|META_EXTENSION
argument_list|)
return|;
block|}
block|}
end_class
end_unit
