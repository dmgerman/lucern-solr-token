begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene42
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
name|codecs
operator|.
name|FieldInfosFormat
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
name|FieldInfosWriter
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Read-write version of {@link Lucene42Codec} for testing.  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|Lucene42RWCodec
specifier|public
class|class
name|Lucene42RWCodec
extends|extends
name|Lucene42Codec
block|{
DECL|field|dv
specifier|private
specifier|static
specifier|final
name|DocValuesFormat
name|dv
init|=
operator|new
name|Lucene42RWDocValuesFormat
argument_list|()
decl_stmt|;
DECL|field|norms
specifier|private
specifier|static
specifier|final
name|NormsFormat
name|norms
init|=
operator|new
name|Lucene42NormsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getDocValuesFormatForField
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|dv
return|;
block|}
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|norms
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
operator|new
name|Lucene42FieldInfosFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FieldInfosWriter
name|getFieldInfosWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|LuceneTestCase
operator|.
name|OLD_FORMAT_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|super
operator|.
name|getFieldInfosWriter
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|Lucene42FieldInfosWriter
argument_list|()
return|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
