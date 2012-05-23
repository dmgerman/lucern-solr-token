begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
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
name|CollectionUtil
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
begin_class
DECL|class|FreqProxTermsWriter
specifier|final
class|class
name|FreqProxTermsWriter
extends|extends
name|TermsHashConsumer
block|{
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{}
comment|// TODO: would be nice to factor out more of this, eg the
comment|// FreqProxFieldMergeState, and code to visit all Fields
comment|// under the same FieldInfo together, up into TermsHash*.
comment|// Other writers would presumably share alot of this...
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|TermsHashConsumerPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Gather all FieldData's that have postings, across all
comment|// ThreadStates
name|List
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
name|allFields
init|=
operator|new
name|ArrayList
argument_list|<
name|FreqProxTermsWriterPerField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TermsHashConsumerPerField
name|f
range|:
name|fieldsToFlush
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|FreqProxTermsWriterPerField
name|perField
init|=
operator|(
name|FreqProxTermsWriterPerField
operator|)
name|f
decl_stmt|;
if|if
condition|(
name|perField
operator|.
name|termsHashPerField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|allFields
operator|.
name|add
argument_list|(
name|perField
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|numAllFields
init|=
name|allFields
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Sort by field name
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|allFields
argument_list|)
expr_stmt|;
specifier|final
name|FieldsConsumer
name|consumer
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getCodec
argument_list|()
operator|.
name|postingsFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|TermsHash
name|termsHash
init|=
literal|null
decl_stmt|;
comment|/*     Current writer chain:       FieldsConsumer         -> IMPL: FormatPostingsTermsDictWriter           -> TermsConsumer             -> IMPL: FormatPostingsTermsDictWriter.TermsWriter               -> DocsConsumer                 -> IMPL: FormatPostingsDocsWriter                   -> PositionsConsumer                     -> IMPL: FormatPostingsPositionsWriter        */
for|for
control|(
name|int
name|fieldNumber
init|=
literal|0
init|;
name|fieldNumber
operator|<
name|numAllFields
condition|;
name|fieldNumber
operator|++
control|)
block|{
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|allFields
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
operator|.
name|fieldInfo
decl_stmt|;
specifier|final
name|FreqProxTermsWriterPerField
name|fieldWriter
init|=
name|allFields
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
comment|// If this field has postings then add them to the
comment|// segment
name|fieldWriter
operator|.
name|flush
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|consumer
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|TermsHashPerField
name|perField
init|=
name|fieldWriter
operator|.
name|termsHashPerField
decl_stmt|;
assert|assert
name|termsHash
operator|==
literal|null
operator|||
name|termsHash
operator|==
name|perField
operator|.
name|termsHash
assert|;
name|termsHash
operator|=
name|perField
operator|.
name|termsHash
expr_stmt|;
name|int
name|numPostings
init|=
name|perField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
decl_stmt|;
name|perField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perField
operator|.
name|shrinkHash
argument_list|(
name|numPostings
argument_list|)
expr_stmt|;
name|fieldWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|termsHash
operator|!=
literal|null
condition|)
block|{
name|termsHash
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|payload
name|BytesRef
name|payload
decl_stmt|;
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsHashConsumerPerField
name|addField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|FreqProxTermsWriterPerField
argument_list|(
name|termsHashPerField
argument_list|,
name|this
argument_list|,
name|fieldInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|(
name|TermsHash
name|termsHash
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{   }
block|}
end_class
end_unit
