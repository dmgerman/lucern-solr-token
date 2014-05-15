begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.nestedpulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|nestedpulsing
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
name|FieldsConsumer
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
name|FieldsProducer
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
name|codecs
operator|.
name|blocktree
operator|.
name|BlockTreeTermsReader
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
name|blocktree
operator|.
name|BlockTreeTermsWriter
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
name|lucene41
operator|.
name|Lucene41PostingsReader
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
name|lucene41
operator|.
name|Lucene41PostingsWriter
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
name|pulsing
operator|.
name|PulsingPostingsReader
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
name|pulsing
operator|.
name|PulsingPostingsWriter
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Pulsing(1, Pulsing(2, Lucene41))  * @lucene.experimental  */
end_comment
begin_comment
comment|// TODO: if we create PulsingPostingsBaseFormat then we
end_comment
begin_comment
comment|// can simplify this? note: I don't like the *BaseFormat
end_comment
begin_comment
comment|// hierarchy, maybe we can clean that up...
end_comment
begin_class
DECL|class|NestedPulsingPostingsFormat
specifier|public
specifier|final
class|class
name|NestedPulsingPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|method|NestedPulsingPostingsFormat
specifier|public
name|NestedPulsingPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"NestedPulsing"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsWriterBase
name|docsWriter
init|=
literal|null
decl_stmt|;
name|PostingsWriterBase
name|pulsingWriterInner
init|=
literal|null
decl_stmt|;
name|PostingsWriterBase
name|pulsingWriter
init|=
literal|null
decl_stmt|;
comment|// Terms dict
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|docsWriter
operator|=
operator|new
name|Lucene41PostingsWriter
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|pulsingWriterInner
operator|=
operator|new
name|PulsingPostingsWriter
argument_list|(
name|state
argument_list|,
literal|2
argument_list|,
name|docsWriter
argument_list|)
expr_stmt|;
name|pulsingWriter
operator|=
operator|new
name|PulsingPostingsWriter
argument_list|(
name|state
argument_list|,
literal|1
argument_list|,
name|pulsingWriterInner
argument_list|)
expr_stmt|;
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|pulsingWriter
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
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
name|docsWriter
argument_list|,
name|pulsingWriterInner
argument_list|,
name|pulsingWriter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsReaderBase
name|docsReader
init|=
literal|null
decl_stmt|;
name|PostingsReaderBase
name|pulsingReaderInner
init|=
literal|null
decl_stmt|;
name|PostingsReaderBase
name|pulsingReader
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|docsReader
operator|=
operator|new
name|Lucene41PostingsReader
argument_list|(
name|state
operator|.
name|directory
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
expr_stmt|;
name|pulsingReaderInner
operator|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|state
argument_list|,
name|docsReader
argument_list|)
expr_stmt|;
name|pulsingReader
operator|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|state
argument_list|,
name|pulsingReaderInner
argument_list|)
expr_stmt|;
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTreeTermsReader
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|pulsingReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
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
name|docsReader
argument_list|,
name|pulsingReaderInner
argument_list|,
name|pulsingReader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
