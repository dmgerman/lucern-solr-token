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
name|DocValuesProducer
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
name|NormsProducer
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
name|NumericDocValues
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
name|Accountable
import|;
end_import
begin_comment
comment|/**  * Reads 4.0/4.1 norms.  * @deprecated Only for reading old 4.0 and 4.1 segments  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40NormsReader
specifier|final
class|class
name|Lucene40NormsReader
extends|extends
name|NormsProducer
block|{
DECL|field|impl
specifier|private
specifier|final
name|DocValuesProducer
name|impl
decl_stmt|;
comment|// clone for merge
DECL|method|Lucene40NormsReader
name|Lucene40NormsReader
parameter_list|(
name|DocValuesProducer
name|impl
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|impl
operator|=
name|impl
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
DECL|method|Lucene40NormsReader
name|Lucene40NormsReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|=
operator|new
name|Lucene40DocValuesReader
argument_list|(
name|state
argument_list|,
name|filename
argument_list|,
name|Lucene40FieldInfosFormat
operator|.
name|LEGACY_NORM_TYPE_KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNorms
specifier|public
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|impl
operator|.
name|getNumeric
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|impl
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|impl
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|impl
operator|.
name|getChildResources
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|impl
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|NormsProducer
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40NormsReader
argument_list|(
name|impl
argument_list|)
return|;
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|impl
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
