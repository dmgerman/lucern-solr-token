begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|NormsConsumer
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
name|index
operator|.
name|SegmentWriteState
import|;
end_import
begin_comment
comment|/**  * plain-text norms format.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextNormsFormat
specifier|public
class|class
name|SimpleTextNormsFormat
extends|extends
name|NormsFormat
block|{
DECL|field|NORMS_SEG_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|NORMS_SEG_EXTENSION
init|=
literal|"len"
decl_stmt|;
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextNormsConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|NormsProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleTextNormsProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|/**    * Reads plain-text norms.    *<p>    *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>    *     * @lucene.experimental    */
DECL|class|SimpleTextNormsProducer
specifier|public
specifier|static
class|class
name|SimpleTextNormsProducer
extends|extends
name|NormsProducer
block|{
DECL|field|impl
specifier|private
specifier|final
name|SimpleTextDocValuesReader
name|impl
decl_stmt|;
DECL|method|SimpleTextNormsProducer
specifier|public
name|SimpleTextNormsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// All we do is change the extension from .dat -> .len;
comment|// otherwise this is a normal simple doc values file:
name|impl
operator|=
operator|new
name|SimpleTextDocValuesReader
argument_list|(
name|state
argument_list|,
name|NORMS_SEG_EXTENSION
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
block|}
comment|/**    * Writes plain-text norms.    *<p>    *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>    *     * @lucene.experimental    */
DECL|class|SimpleTextNormsConsumer
specifier|public
specifier|static
class|class
name|SimpleTextNormsConsumer
extends|extends
name|NormsConsumer
block|{
DECL|field|impl
specifier|private
specifier|final
name|SimpleTextDocValuesWriter
name|impl
decl_stmt|;
DECL|method|SimpleTextNormsConsumer
specifier|public
name|SimpleTextNormsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// All we do is change the extension from .dat -> .len;
comment|// otherwise this is a normal simple doc values file:
name|impl
operator|=
operator|new
name|SimpleTextDocValuesWriter
argument_list|(
name|state
argument_list|,
name|NORMS_SEG_EXTENSION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNormsField
specifier|public
name|void
name|addNormsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|addNumericField
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class
end_unit
