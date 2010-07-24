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
name|HashMap
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
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/** This class implements {@link InvertedDocConsumer}, which  *  is passed each token produced by the analyzer on each  *  field.  It stores these tokens in a hash table, and  *  allocates separate byte streams per token.  Consumers of  *  this class, eg {@link FreqProxTermsWriter} and {@link  *  TermVectorsTermsWriter}, write their own byte streams  *  under each term.  */
end_comment
begin_class
DECL|class|TermsHash
specifier|final
class|class
name|TermsHash
extends|extends
name|InvertedDocConsumer
block|{
DECL|field|consumer
specifier|final
name|TermsHashConsumer
name|consumer
decl_stmt|;
DECL|field|nextTermsHash
specifier|final
name|TermsHash
name|nextTermsHash
decl_stmt|;
DECL|field|docWriter
specifier|final
name|DocumentsWriterPerThread
name|docWriter
decl_stmt|;
DECL|field|intPool
specifier|final
name|IntBlockPool
name|intPool
decl_stmt|;
DECL|field|bytePool
specifier|final
name|ByteBlockPool
name|bytePool
decl_stmt|;
DECL|field|termBytePool
name|ByteBlockPool
name|termBytePool
decl_stmt|;
DECL|field|primary
specifier|final
name|boolean
name|primary
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
comment|// Used when comparing postings via termRefComp, in TermsHashPerField
DECL|field|tr1
specifier|final
name|BytesRef
name|tr1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|tr2
specifier|final
name|BytesRef
name|tr2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|// Used by perField:
DECL|field|utf8
specifier|final
name|BytesRef
name|utf8
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|trackAllocations
name|boolean
name|trackAllocations
decl_stmt|;
DECL|method|TermsHash
specifier|public
name|TermsHash
parameter_list|(
specifier|final
name|DocumentsWriterPerThread
name|docWriter
parameter_list|,
specifier|final
name|TermsHashConsumer
name|consumer
parameter_list|,
specifier|final
name|TermsHash
name|nextTermsHash
parameter_list|)
block|{
name|this
operator|.
name|docState
operator|=
name|docWriter
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|nextTermsHash
operator|=
name|nextTermsHash
expr_stmt|;
name|intPool
operator|=
operator|new
name|IntBlockPool
argument_list|(
name|docWriter
argument_list|)
expr_stmt|;
name|bytePool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
name|docWriter
operator|.
name|ramAllocator
operator|.
name|byteBlockAllocator
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
comment|// We are primary
name|primary
operator|=
literal|true
expr_stmt|;
name|termBytePool
operator|=
name|bytePool
expr_stmt|;
name|nextTermsHash
operator|.
name|termBytePool
operator|=
name|bytePool
expr_stmt|;
block|}
else|else
block|{
name|primary
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setFieldInfos
name|void
name|setFieldInfos
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|)
block|{
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|consumer
operator|.
name|setFieldInfos
argument_list|(
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Clear all state
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|intPool
operator|.
name|reset
argument_list|()
expr_stmt|;
name|bytePool
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|primary
condition|)
block|{
name|bytePool
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocConsumerPerField
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
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|TermsHashConsumerPerField
argument_list|>
name|childFields
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|TermsHashConsumerPerField
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocConsumerPerField
argument_list|>
name|nextChildFields
decl_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextChildFields
operator|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocConsumerPerField
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|nextChildFields
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|FieldInfo
argument_list|,
name|InvertedDocConsumerPerField
argument_list|>
name|entry
range|:
name|fieldsToFlush
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|TermsHashPerField
name|perField
init|=
operator|(
name|TermsHashPerField
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|childFields
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|perField
operator|.
name|consumer
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextChildFields
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|perField
operator|.
name|nextPerField
argument_list|)
expr_stmt|;
block|}
block|}
name|consumer
operator|.
name|flush
argument_list|(
name|childFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|flush
argument_list|(
name|nextChildFields
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addField
name|InvertedDocConsumerPerField
name|addField
parameter_list|(
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|TermsHashPerField
argument_list|(
name|docInverterPerField
argument_list|,
name|this
argument_list|,
name|nextTermsHash
argument_list|,
name|fieldInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|consumer
operator|.
name|finishDocument
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|consumer
operator|.
name|finishDocument
argument_list|(
name|nextTermsHash
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
block|{
name|nextTermsHash
operator|.
name|consumer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
