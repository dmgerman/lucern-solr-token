begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.mockintblock
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|mockintblock
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
name|blockterms
operator|.
name|BlockTermsReader
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
name|blockterms
operator|.
name|BlockTermsWriter
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
name|blockterms
operator|.
name|FixedGapTermsIndexReader
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
name|blockterms
operator|.
name|FixedGapTermsIndexWriter
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
name|blockterms
operator|.
name|TermsIndexReaderBase
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
name|blockterms
operator|.
name|TermsIndexWriterBase
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
name|intblock
operator|.
name|FixedIntBlockIndexInput
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
name|intblock
operator|.
name|FixedIntBlockIndexOutput
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
name|sep
operator|.
name|IntIndexInput
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
name|sep
operator|.
name|IntIndexOutput
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
name|sep
operator|.
name|IntStreamFactory
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
name|sep
operator|.
name|SepPostingsReader
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
name|sep
operator|.
name|SepPostingsWriter
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
name|store
operator|.
name|*
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
name|BytesRef
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
comment|/**  * A silly test codec to verify core support for fixed  * sized int block encoders is working.  The int encoder  * used here just writes each block as a series of vInt.  */
end_comment
begin_class
DECL|class|MockFixedIntBlockPostingsFormat
specifier|public
specifier|final
class|class
name|MockFixedIntBlockPostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|MockFixedIntBlockPostingsFormat
specifier|public
name|MockFixedIntBlockPostingsFormat
parameter_list|()
block|{
name|this
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|MockFixedIntBlockPostingsFormat
specifier|public
name|MockFixedIntBlockPostingsFormat
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"MockFixedIntBlock"
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|blockSize
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
name|getName
argument_list|()
operator|+
literal|"(blockSize="
operator|+
name|blockSize
operator|+
literal|")"
return|;
block|}
comment|// only for testing
DECL|method|getIntFactory
specifier|public
name|IntStreamFactory
name|getIntFactory
parameter_list|()
block|{
return|return
operator|new
name|MockIntFactory
argument_list|(
name|blockSize
argument_list|)
return|;
block|}
comment|/**    * Encodes blocks as vInts of a fixed block size.    */
DECL|class|MockIntFactory
specifier|public
specifier|static
class|class
name|MockIntFactory
extends|extends
name|IntStreamFactory
block|{
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|MockIntFactory
specifier|public
name|MockIntFactory
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IntIndexInput
name|openInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FixedIntBlockIndexInput
argument_list|(
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BlockReader
name|getBlockReader
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|,
specifier|final
name|int
index|[]
name|buffer
parameter_list|)
block|{
return|return
operator|new
name|BlockReader
argument_list|()
block|{
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
block|{}
specifier|public
name|void
name|readBlock
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IntIndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FixedIntBlockIndexOutput
name|ret
init|=
operator|new
name|FixedIntBlockIndexOutput
argument_list|(
name|out
argument_list|,
name|blockSize
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|flushBlock
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|postingsWriter
init|=
operator|new
name|SepPostingsWriter
argument_list|(
name|state
argument_list|,
operator|new
name|MockIntFactory
argument_list|(
name|blockSize
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|TermsIndexWriterBase
name|indexWriter
decl_stmt|;
try|try
block|{
name|indexWriter
operator|=
operator|new
name|FixedGapTermsIndexWriter
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTermsWriter
argument_list|(
name|indexWriter
argument_list|,
name|state
argument_list|,
name|postingsWriter
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
try|try
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|postingsReader
init|=
operator|new
name|SepPostingsReader
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
operator|new
name|MockIntFactory
argument_list|(
name|blockSize
argument_list|)
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|TermsIndexReaderBase
name|indexReader
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|indexReader
operator|=
operator|new
name|FixedGapTermsIndexReader
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
name|state
operator|.
name|termsIndexDivisor
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTermsReader
argument_list|(
name|indexReader
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
name|segmentInfo
argument_list|,
name|postingsReader
argument_list|,
name|state
operator|.
name|context
argument_list|,
literal|1024
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
try|try
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
