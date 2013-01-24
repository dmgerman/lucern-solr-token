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
name|search
operator|.
name|similarities
operator|.
name|Similarity
import|;
end_import
begin_class
DECL|class|NormsConsumerPerField
specifier|final
class|class
name|NormsConsumerPerField
extends|extends
name|InvertedDocEndConsumerPerField
implements|implements
name|Comparable
argument_list|<
name|NormsConsumerPerField
argument_list|>
block|{
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|private
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|fieldState
specifier|private
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|consumer
specifier|private
name|NumericDocValuesWriter
name|consumer
decl_stmt|;
DECL|method|NormsConsumerPerField
specifier|public
name|NormsConsumerPerField
parameter_list|(
specifier|final
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|,
name|NormsConsumer
name|parent
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|docInverterPerField
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|docInverterPerField
operator|.
name|fieldState
expr_stmt|;
name|similarity
operator|=
name|docState
operator|.
name|similarity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|NormsConsumerPerField
name|other
parameter_list|)
block|{
return|return
name|fieldInfo
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|()
operator|&&
operator|!
name|fieldInfo
operator|.
name|omitsNorms
argument_list|()
condition|)
block|{
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
comment|// nocommit wrongish?  what about the
comment|// only-doc-with-norms-enabled-hits-exc case?
name|fieldInfo
operator|.
name|setNormValueType
argument_list|(
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
name|consumer
operator|=
operator|new
name|NumericDocValuesWriter
argument_list|(
name|fieldInfo
argument_list|,
name|docState
operator|.
name|docWriter
operator|.
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|addValue
argument_list|(
name|docState
operator|.
name|docID
argument_list|,
name|similarity
operator|.
name|computeNorm
argument_list|(
name|fieldState
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flush
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|DocValuesConsumer
name|normsWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docCount
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
return|return;
comment|// null type - not omitted but not written -
comment|// nocommit meaning the only docs that had
comment|// norms hit exceptions?
block|}
name|consumer
operator|.
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|flush
argument_list|(
name|state
argument_list|,
name|normsWriter
argument_list|)
expr_stmt|;
block|}
DECL|method|isEmpty
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|consumer
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
comment|//
block|}
block|}
end_class
end_unit
