begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|SegmentInfo
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
name|util
operator|.
name|NamedSPILoader
import|;
end_import
begin_comment
comment|/**   * Encodes/decodes terms, postings, and proximity data.  * @lucene.experimental */
end_comment
begin_class
DECL|class|PostingsFormat
specifier|public
specifier|abstract
class|class
name|PostingsFormat
implements|implements
name|NamedSPILoader
operator|.
name|NamedSPI
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|NamedSPILoader
argument_list|<
name|PostingsFormat
argument_list|>
name|loader
init|=
operator|new
name|NamedSPILoader
argument_list|<
name|PostingsFormat
argument_list|>
argument_list|(
name|PostingsFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|PostingsFormat
index|[]
name|EMPTY
init|=
operator|new
name|PostingsFormat
index|[
literal|0
index|]
decl_stmt|;
comment|/** Unique name that's used to retrieve this format when    *  reading the index.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|PostingsFormat
specifier|protected
name|PostingsFormat
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Writes a new segment */
DECL|method|fieldsConsumer
specifier|public
specifier|abstract
name|InvertedFieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Reads a segment.  NOTE: by the time this call    *  returns, it must hold open any files it will need to    *  use; else, those files may be deleted. */
DECL|method|fieldsProducer
specifier|public
specifier|abstract
name|InvertedFieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gathers files associated with this segment    *     * @param segmentInfo the {@link SegmentInfo} for this segment     * @param segmentSuffix the format's suffix within this segment    * @param files the of files to add the codec files to.    */
DECL|method|files
specifier|public
specifier|abstract
name|void
name|files
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PostingsFormat(name="
operator|+
name|name
operator|+
literal|")"
return|;
block|}
comment|/** looks up a format by name */
DECL|method|forName
specifier|public
specifier|static
name|PostingsFormat
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|loader
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available format names */
DECL|method|availablePostingsFormats
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availablePostingsFormats
parameter_list|()
block|{
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
block|}
end_class
end_unit
