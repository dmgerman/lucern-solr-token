begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene41
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
package|;
end_package
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
name|FieldInfosFormat
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
name|codecs
operator|.
name|SegmentInfoFormat
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
name|StoredFieldsFormat
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
name|TermVectorsFormat
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
name|Lucene40RWDocValuesFormat
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
name|Lucene40RWFieldInfosFormat
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
name|Lucene40RWNormsFormat
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
name|Lucene40RWSegmentInfoFormat
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
name|Lucene40RWTermVectorsFormat
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Read-write version of 4.1 codec for testing  * @deprecated for test purposes only  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene41RWCodec
specifier|public
specifier|final
class|class
name|Lucene41RWCodec
extends|extends
name|Lucene41Codec
block|{
DECL|field|fieldsFormat
specifier|private
specifier|final
name|StoredFieldsFormat
name|fieldsFormat
init|=
operator|new
name|Lucene41RWStoredFieldsFormat
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfos
init|=
operator|new
name|Lucene40RWFieldInfosFormat
argument_list|()
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|DocValuesFormat
name|docValues
init|=
operator|new
name|Lucene40RWDocValuesFormat
argument_list|()
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|NormsFormat
name|norms
init|=
operator|new
name|Lucene40RWNormsFormat
argument_list|()
decl_stmt|;
DECL|field|vectors
specifier|private
specifier|final
name|TermVectorsFormat
name|vectors
init|=
operator|new
name|Lucene40RWTermVectorsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|fieldsFormat
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
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|norms
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|vectors
return|;
block|}
DECL|field|segmentInfos
specifier|private
specifier|static
specifier|final
name|SegmentInfoFormat
name|segmentInfos
init|=
operator|new
name|Lucene40RWSegmentInfoFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|segmentInfoFormat
specifier|public
name|SegmentInfoFormat
name|segmentInfoFormat
parameter_list|()
block|{
return|return
name|segmentInfos
return|;
block|}
block|}
end_class
end_unit
