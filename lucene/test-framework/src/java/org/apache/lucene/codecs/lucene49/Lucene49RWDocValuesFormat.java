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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/** Read-write version of {@link Lucene49DocValuesFormat} for testing */
end_comment
begin_class
DECL|class|Lucene49RWDocValuesFormat
specifier|public
class|class
name|Lucene49RWDocValuesFormat
extends|extends
name|Lucene49DocValuesFormat
block|{
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
if|if
condition|(
name|LuceneTestCase
operator|.
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
condition|)
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
block|{
annotation|@
name|Override
name|void
name|checkCanWrite
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
comment|// allow writing all fields
block|}
block|}
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit