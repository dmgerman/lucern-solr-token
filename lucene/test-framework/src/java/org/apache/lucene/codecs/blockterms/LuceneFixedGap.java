begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.blockterms
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blockterms
package|;
end_package
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
import|;
end_import
begin_comment
comment|// javadocs
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
name|lucene50
operator|.
name|Lucene50PostingsReader
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
name|lucene50
operator|.
name|Lucene50PostingsWriter
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
comment|// TODO: we could make separate base class that can wrap
end_comment
begin_comment
comment|// any PostingsFormat and make it ord-able...
end_comment
begin_comment
comment|/**  * Customized version of {@link Lucene50PostingsFormat} that uses  * {@link FixedGapTermsIndexWriter}.  */
end_comment
begin_class
DECL|class|LuceneFixedGap
specifier|public
specifier|final
class|class
name|LuceneFixedGap
extends|extends
name|PostingsFormat
block|{
DECL|field|termIndexInterval
specifier|final
name|int
name|termIndexInterval
decl_stmt|;
DECL|method|LuceneFixedGap
specifier|public
name|LuceneFixedGap
parameter_list|()
block|{
name|this
argument_list|(
name|FixedGapTermsIndexWriter
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
argument_list|)
expr_stmt|;
block|}
DECL|method|LuceneFixedGap
specifier|public
name|LuceneFixedGap
parameter_list|(
name|int
name|termIndexInterval
parameter_list|)
block|{
name|super
argument_list|(
literal|"LuceneFixedGap"
argument_list|)
expr_stmt|;
name|this
operator|.
name|termIndexInterval
operator|=
name|termIndexInterval
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
name|docs
init|=
operator|new
name|Lucene50PostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// TODO: should we make the terms index more easily
comment|// pluggable?  Ie so that this codec would record which
comment|// index impl was used, and switch on loading?
comment|// Or... you must make a new Codec for this?
name|TermsIndexWriterBase
name|indexWriter
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|indexWriter
operator|=
operator|new
name|FixedGapTermsIndexWriter
argument_list|(
name|state
argument_list|,
name|termIndexInterval
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
name|docs
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
comment|// Must use BlockTermsWriter (not BlockTree) because
comment|// BlockTree doens't support ords (yet)...
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
name|docs
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
name|docs
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
name|postings
init|=
operator|new
name|Lucene50PostingsReader
argument_list|(
name|state
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
name|postings
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
name|postings
argument_list|,
name|state
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
name|postings
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
comment|/** Extension of freq postings file */
DECL|field|FREQ_EXTENSION
specifier|static
specifier|final
name|String
name|FREQ_EXTENSION
init|=
literal|"frq"
decl_stmt|;
comment|/** Extension of prox postings file */
DECL|field|PROX_EXTENSION
specifier|static
specifier|final
name|String
name|PROX_EXTENSION
init|=
literal|"prx"
decl_stmt|;
block|}
end_class
end_unit
