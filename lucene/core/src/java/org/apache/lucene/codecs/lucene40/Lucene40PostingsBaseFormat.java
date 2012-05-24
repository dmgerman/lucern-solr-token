begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
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
name|PostingsBaseFormat
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
name|PostingsReaderBase
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
name|PostingsWriterBase
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
comment|/**   * Provides a {@link PostingsReaderBase} and {@link  * PostingsWriterBase}.  *  * @lucene.experimental */
end_comment
begin_comment
comment|// TODO: should these also be named / looked up via SPI?
end_comment
begin_class
DECL|class|Lucene40PostingsBaseFormat
specifier|public
specifier|final
class|class
name|Lucene40PostingsBaseFormat
extends|extends
name|PostingsBaseFormat
block|{
DECL|method|Lucene40PostingsBaseFormat
specifier|public
name|Lucene40PostingsBaseFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene40"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postingsReaderBase
specifier|public
name|PostingsReaderBase
name|postingsReaderBase
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40PostingsReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|postingsWriterBase
specifier|public
name|PostingsWriterBase
name|postingsWriterBase
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40PostingsWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
end_class
end_unit
