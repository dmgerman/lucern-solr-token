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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|LiveDocsFormat
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
begin_comment
comment|/**  * plain text index format.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextCodec
specifier|public
specifier|final
class|class
name|SimpleTextCodec
extends|extends
name|Codec
block|{
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
operator|new
name|SimpleTextPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|storedFields
specifier|private
specifier|final
name|StoredFieldsFormat
name|storedFields
init|=
operator|new
name|SimpleTextStoredFieldsFormat
argument_list|()
decl_stmt|;
DECL|field|segmentInfos
specifier|private
specifier|final
name|SegmentInfoFormat
name|segmentInfos
init|=
operator|new
name|SimpleTextSegmentInfoFormat
argument_list|()
decl_stmt|;
DECL|field|fieldInfosFormat
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
init|=
operator|new
name|SimpleTextFieldInfosFormat
argument_list|()
decl_stmt|;
DECL|field|vectorsFormat
specifier|private
specifier|final
name|TermVectorsFormat
name|vectorsFormat
init|=
operator|new
name|SimpleTextTermVectorsFormat
argument_list|()
decl_stmt|;
comment|// TODO: need a plain-text impl
DECL|field|docValues
specifier|private
specifier|final
name|DocValuesFormat
name|docValues
init|=
operator|new
name|SimpleTextDocValuesFormat
argument_list|()
decl_stmt|;
comment|// TODO: need a plain-text impl (using the above)
DECL|field|normsFormat
specifier|private
specifier|final
name|NormsFormat
name|normsFormat
init|=
operator|new
name|SimpleTextNormsFormat
argument_list|()
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|LiveDocsFormat
name|liveDocs
init|=
operator|new
name|SimpleTextLiveDocsFormat
argument_list|()
decl_stmt|;
DECL|method|SimpleTextCodec
specifier|public
name|SimpleTextCodec
parameter_list|()
block|{
name|super
argument_list|(
literal|"SimpleText"
argument_list|)
expr_stmt|;
block|}
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
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|storedFields
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
name|vectorsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|fieldInfosFormat
return|;
block|}
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
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|normsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|liveDocsFormat
specifier|public
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
block|{
return|return
name|liveDocs
return|;
block|}
comment|// nocommit;
DECL|field|nocommit
specifier|private
specifier|final
name|SimpleDocValuesFormat
name|nocommit
init|=
operator|new
name|SimpleTextSimpleDocValuesFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|simpleDocValuesFormat
specifier|public
name|SimpleDocValuesFormat
name|simpleDocValuesFormat
parameter_list|()
block|{
return|return
name|nocommit
return|;
block|}
block|}
end_class
end_unit
