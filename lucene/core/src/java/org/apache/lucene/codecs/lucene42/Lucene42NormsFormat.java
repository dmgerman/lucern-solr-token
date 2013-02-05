begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene42
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
name|NormsFormat
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
name|util
operator|.
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/**  * Lucene 4.2 score normalization format.  *<p>  * NOTE: this uses the same format as {@link Lucene42DocValuesFormat}  * Numeric DocValues, but with different file extensions, and passing  * {@link PackedInts#FASTEST} for uncompressed encoding: trading off  * space for performance.  *<p>  * Files:  *<ul>  *<li><tt>.nvd</tt>: DocValues data</li>  *<li><tt>.nvm</tt>: DocValues metadata</li>  *</ul>  * @see Lucene42DocValuesFormat  */
end_comment
begin_class
DECL|class|Lucene42NormsFormat
specifier|public
specifier|final
class|class
name|Lucene42NormsFormat
extends|extends
name|NormsFormat
block|{
comment|/** Sole constructor */
DECL|method|Lucene42NormsFormat
specifier|public
name|Lucene42NormsFormat
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|DocValuesConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// note: we choose FASTEST here (otherwise our norms are half as big but 15% slower than previous lucene)
return|return
operator|new
name|Lucene42DocValuesConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|,
name|PackedInts
operator|.
name|FASTEST
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|DocValuesProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene42DocValuesProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene41NormsData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"nvd"
decl_stmt|;
DECL|field|METADATA_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|METADATA_CODEC
init|=
literal|"Lucene41NormsMetadata"
decl_stmt|;
DECL|field|METADATA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|METADATA_EXTENSION
init|=
literal|"nvm"
decl_stmt|;
block|}
end_class
end_unit
