begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.appending
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|appending
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|DefaultDocValuesFormat
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
name|codecs
operator|.
name|DefaultFieldsFormat
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
name|codecs
operator|.
name|FieldsFormat
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|SegmentInfosFormat
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40Codec
import|;
end_import
begin_comment
comment|/**  * This codec extends {@link Lucene40Codec} to work on append-only outputs, such  * as plain output streams and append-only filesystems.  *  *<p>Note: compound file format feature is not compatible with  * this codec.  You must call both  * LogMergePolicy.setUseCompoundFile(false) and  * LogMergePolicy.setUseCompoundDocStore(false) to disable  * compound file format.</p>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AppendingCodec
specifier|public
class|class
name|AppendingCodec
extends|extends
name|Codec
block|{
DECL|method|AppendingCodec
specifier|public
name|AppendingCodec
parameter_list|()
block|{
name|super
argument_list|(
literal|"Appending"
argument_list|)
expr_stmt|;
block|}
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
operator|new
name|AppendingPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|infos
specifier|private
specifier|final
name|SegmentInfosFormat
name|infos
init|=
operator|new
name|AppendingSegmentInfosFormat
argument_list|()
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|FieldsFormat
name|fields
init|=
operator|new
name|DefaultFieldsFormat
argument_list|()
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|DocValuesFormat
name|docValues
init|=
operator|new
name|DefaultDocValuesFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|postings
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsFormat
specifier|public
name|FieldsFormat
name|fieldsFormat
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|docValues
return|;
block|}
annotation|@
name|Override
DECL|method|segmentInfosFormat
specifier|public
name|SegmentInfosFormat
name|segmentInfosFormat
parameter_list|()
block|{
return|return
name|infos
return|;
block|}
block|}
end_class
end_unit
