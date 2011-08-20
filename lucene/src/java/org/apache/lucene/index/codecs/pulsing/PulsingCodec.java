begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.pulsing
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
name|pulsing
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
name|PerDocWriteState
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
name|index
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
name|index
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
name|codecs
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
name|index
operator|.
name|codecs
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
name|DefaultDocValuesConsumer
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
name|DefaultDocValuesProducer
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
name|index
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
name|index
operator|.
name|codecs
operator|.
name|PerDocConsumer
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
name|PerDocValues
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
name|standard
operator|.
name|StandardCodec
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
name|standard
operator|.
name|StandardPostingsReader
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
name|standard
operator|.
name|StandardPostingsWriter
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
begin_comment
comment|/** This codec "inlines" the postings for terms that have  *  low docFreq.  It wraps another codec, which is used for  *  writing the non-inlined terms.  *  *  Currently in only inlines docFreq=1 terms, and  *  otherwise uses the normal "standard" codec.   *  @lucene.experimental */
end_comment
begin_class
DECL|class|PulsingCodec
specifier|public
class|class
name|PulsingCodec
extends|extends
name|Codec
block|{
DECL|field|freqCutoff
specifier|private
specifier|final
name|int
name|freqCutoff
decl_stmt|;
DECL|field|minBlockSize
specifier|private
specifier|final
name|int
name|minBlockSize
decl_stmt|;
DECL|field|maxBlockSize
specifier|private
specifier|final
name|int
name|maxBlockSize
decl_stmt|;
DECL|method|PulsingCodec
specifier|public
name|PulsingCodec
parameter_list|()
block|{
name|this
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|PulsingCodec
specifier|public
name|PulsingCodec
parameter_list|(
name|int
name|freqCutoff
parameter_list|)
block|{
name|this
argument_list|(
name|freqCutoff
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MIN_BLOCK_SIZE
argument_list|,
name|BlockTreeTermsWriter
operator|.
name|DEFAULT_MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** Terms with freq<= freqCutoff are inlined into terms    *  dict. */
DECL|method|PulsingCodec
specifier|public
name|PulsingCodec
parameter_list|(
name|int
name|freqCutoff
parameter_list|,
name|int
name|minBlockSize
parameter_list|,
name|int
name|maxBlockSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"Pulsing"
argument_list|)
expr_stmt|;
name|this
operator|.
name|freqCutoff
operator|=
name|freqCutoff
expr_stmt|;
name|this
operator|.
name|minBlockSize
operator|=
name|minBlockSize
expr_stmt|;
assert|assert
name|minBlockSize
operator|>
literal|1
assert|;
name|this
operator|.
name|maxBlockSize
operator|=
name|maxBlockSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|"(freqCutoff="
operator|+
name|freqCutoff
operator|+
literal|" minBlockSize="
operator|+
name|minBlockSize
operator|+
literal|" maxBlockSize="
operator|+
name|maxBlockSize
operator|+
literal|")"
return|;
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
comment|// We wrap StandardPostingsWriter, but any PostingsWriterBase
comment|// will work:
name|PostingsWriterBase
name|docsWriter
init|=
operator|new
name|StandardPostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// Terms that have<= freqCutoff number of docs are
comment|// "pulsed" (inlined):
name|PostingsWriterBase
name|pulsingWriter
init|=
operator|new
name|PulsingPostingsWriter
argument_list|(
name|freqCutoff
argument_list|,
name|docsWriter
argument_list|)
decl_stmt|;
comment|// Terms dict
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
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
name|minBlockSize
argument_list|,
name|maxBlockSize
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
name|pulsingWriter
operator|.
name|close
argument_list|()
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
comment|// We wrap StandardPostingsReader, but any StandardPostingsReader
comment|// will work:
name|PostingsReaderBase
name|docsReader
init|=
operator|new
name|StandardPostingsReader
argument_list|(
name|state
operator|.
name|dir
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
name|codecId
argument_list|)
decl_stmt|;
name|PostingsReaderBase
name|pulsingReader
init|=
operator|new
name|PulsingPostingsReader
argument_list|(
name|docsReader
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTreeTermsReader
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
operator|.
name|name
argument_list|,
name|pulsingReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|codecId
argument_list|,
name|state
operator|.
name|termsIndexDivisor
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
name|pulsingReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFreqCutoff
specifier|public
name|int
name|getFreqCutoff
parameter_list|()
block|{
return|return
name|freqCutoff
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|int
name|codecID
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|StandardPostingsReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|codecID
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|BlockTreeTermsReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|codecID
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|DefaultDocValuesConsumer
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|codecID
argument_list|,
name|files
argument_list|,
name|getDocValuesUseCFS
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|StandardCodec
operator|.
name|getStandardExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|DefaultDocValuesConsumer
operator|.
name|getDocValuesExtensions
argument_list|(
name|extensions
argument_list|,
name|getDocValuesUseCFS
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultDocValuesConsumer
argument_list|(
name|state
argument_list|,
name|getDocValuesSortComparator
argument_list|()
argument_list|,
name|getDocValuesUseCFS
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocValues
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultDocValuesProducer
argument_list|(
name|state
operator|.
name|segmentInfo
argument_list|,
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
name|codecId
argument_list|,
name|getDocValuesUseCFS
argument_list|()
argument_list|,
name|getDocValuesSortComparator
argument_list|()
argument_list|,
name|state
operator|.
name|context
argument_list|)
return|;
block|}
block|}
end_class
end_unit
