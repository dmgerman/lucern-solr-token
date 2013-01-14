begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene41
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
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
name|SimpleDVProducer
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
begin_class
DECL|class|Lucene41SimpleDocValuesFormat
specifier|public
class|class
name|Lucene41SimpleDocValuesFormat
extends|extends
name|SimpleNormsFormat
block|{
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|SimpleDVConsumer
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
name|Lucene41SimpleDocValuesConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|SimpleDVProducer
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
name|Lucene41SimpleDocValuesProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene41DocValuesData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dvd"
decl_stmt|;
DECL|field|METADATA_CODEC
specifier|private
specifier|static
specifier|final
name|String
name|METADATA_CODEC
init|=
literal|"Lucene41DocValuesMetadata"
decl_stmt|;
DECL|field|METADATA_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|METADATA_EXTENSION
init|=
literal|"dvm"
decl_stmt|;
block|}
end_class
end_unit
