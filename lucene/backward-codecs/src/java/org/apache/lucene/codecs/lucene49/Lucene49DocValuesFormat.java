begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|DocValuesFormat
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
comment|/**  * Lucene 4.9 DocValues format.  * @deprecated only for old 4.x segments  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene49DocValuesFormat
specifier|public
class|class
name|Lucene49DocValuesFormat
extends|extends
name|DocValuesFormat
block|{
comment|/** Sole Constructor */
DECL|method|Lucene49DocValuesFormat
specifier|public
name|Lucene49DocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene49"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|DocValuesConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene49DocValuesConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|META_CODEC
argument_list|,
name|META_EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
specifier|final
name|DocValuesProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene49DocValuesProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|META_CODEC
argument_list|,
name|META_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene49DocValuesData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dvd"
decl_stmt|;
DECL|field|META_CODEC
specifier|static
specifier|final
name|String
name|META_CODEC
init|=
literal|"Lucene49ValuesMetadata"
decl_stmt|;
DECL|field|META_EXTENSION
specifier|static
specifier|final
name|String
name|META_EXTENSION
init|=
literal|"dvm"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|NUMERIC
specifier|static
specifier|final
name|byte
name|NUMERIC
init|=
literal|0
decl_stmt|;
DECL|field|BINARY
specifier|static
specifier|final
name|byte
name|BINARY
init|=
literal|1
decl_stmt|;
DECL|field|SORTED
specifier|static
specifier|final
name|byte
name|SORTED
init|=
literal|2
decl_stmt|;
DECL|field|SORTED_SET
specifier|static
specifier|final
name|byte
name|SORTED_SET
init|=
literal|3
decl_stmt|;
DECL|field|SORTED_NUMERIC
specifier|static
specifier|final
name|byte
name|SORTED_NUMERIC
init|=
literal|4
decl_stmt|;
block|}
end_class
end_unit
