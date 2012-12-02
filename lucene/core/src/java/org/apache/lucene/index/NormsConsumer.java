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
name|DocValuesConsumer
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
name|codecs
operator|.
name|SimpleDVConsumer
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
name|SimpleNormsFormat
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
name|DocValues
operator|.
name|Type
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
comment|// TODO FI: norms could actually be stored as doc store
end_comment
begin_comment
comment|/** Writes norms.  Each thread X field accumulates the norms  *  for the doc/fields it saw, then the flush method below  *  merges all of these together into a single _X.nrm file.  */
end_comment
begin_class
DECL|class|NormsConsumer
specifier|final
class|class
name|NormsConsumer
extends|extends
name|InvertedDocEndConsumer
block|{
DECL|field|normsFormat
specifier|private
specifier|final
name|NormsFormat
name|normsFormat
decl_stmt|;
DECL|field|consumer
specifier|private
name|PerDocConsumer
name|consumer
decl_stmt|;
DECL|method|NormsConsumer
specifier|public
name|NormsConsumer
parameter_list|(
name|DocumentsWriterPerThread
name|dwpt
parameter_list|)
block|{
name|normsFormat
operator|=
name|dwpt
operator|.
name|codec
operator|.
name|normsFormat
argument_list|()
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
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
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
name|InvertedDocEndConsumerPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|SimpleDVConsumer
name|normsConsumer
init|=
literal|null
decl_stmt|;
name|boolean
name|anythingFlushed
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
name|SimpleNormsFormat
name|normsFormat
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getCodec
argument_list|()
operator|.
name|simpleNormsFormat
argument_list|()
decl_stmt|;
comment|// nocommit change this to assert normsFormat != null
if|if
condition|(
name|normsFormat
operator|!=
literal|null
condition|)
block|{
name|normsConsumer
operator|=
name|normsFormat
operator|.
name|normsConsumer
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FieldInfo
name|fi
range|:
name|state
operator|.
name|fieldInfos
control|)
block|{
specifier|final
name|NormsConsumerPerField
name|toWrite
init|=
operator|(
name|NormsConsumerPerField
operator|)
name|fieldsToFlush
operator|.
name|get
argument_list|(
name|fi
operator|.
name|name
argument_list|)
decl_stmt|;
comment|// we must check the final value of omitNorms for the fieldinfo, it could have
comment|// changed for this field since the first time we added it.
if|if
condition|(
operator|!
name|fi
operator|.
name|omitsNorms
argument_list|()
condition|)
block|{
if|if
condition|(
name|toWrite
operator|!=
literal|null
operator|&&
name|toWrite
operator|.
name|initialized
argument_list|()
condition|)
block|{
name|anythingFlushed
operator|=
literal|true
expr_stmt|;
specifier|final
name|Type
name|type
init|=
name|toWrite
operator|.
name|flush
argument_list|(
name|state
argument_list|,
name|normsConsumer
argument_list|)
decl_stmt|;
assert|assert
name|fi
operator|.
name|getNormType
argument_list|()
operator|==
name|type
assert|;
block|}
elseif|else
if|if
condition|(
name|fi
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
name|anythingFlushed
operator|=
literal|true
expr_stmt|;
assert|assert
name|fi
operator|.
name|getNormType
argument_list|()
operator|==
literal|null
operator|:
literal|"got "
operator|+
name|fi
operator|.
name|getNormType
argument_list|()
operator|+
literal|"; field="
operator|+
name|fi
operator|.
name|name
assert|;
block|}
block|}
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|anythingFlushed
operator|&&
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
comment|// nocommit do we also need to normsConsumer.abort!?
block|}
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
argument_list|,
name|normsConsumer
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
argument_list|,
name|normsConsumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|startDocument
name|void
name|startDocument
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|addField
name|InvertedDocEndConsumerPerField
name|addField
parameter_list|(
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
operator|new
name|NormsConsumerPerField
argument_list|(
name|docInverterPerField
argument_list|,
name|fieldInfo
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|newConsumer
name|DocValuesConsumer
name|newConsumer
parameter_list|(
name|PerDocWriteState
name|perDocWriteState
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Type
name|type
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
name|consumer
operator|=
name|normsFormat
operator|.
name|docsConsumer
argument_list|(
name|perDocWriteState
argument_list|)
expr_stmt|;
block|}
name|DocValuesConsumer
name|addValuesField
init|=
name|consumer
operator|.
name|addValuesField
argument_list|(
name|type
argument_list|,
name|fieldInfo
argument_list|)
decl_stmt|;
return|return
name|addValuesField
return|;
block|}
block|}
end_class
end_unit
