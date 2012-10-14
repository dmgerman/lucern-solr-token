begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|FilterCodec
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
name|StoredFieldsFormat
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
name|lucene41
operator|.
name|Lucene41Codec
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import
begin_comment
comment|/**  * A codec that uses {@link CompressingStoredFieldsFormat} for its stored  * fields and delegates to {@link Lucene41Codec} for everything else.  */
end_comment
begin_class
DECL|class|CompressingCodec
specifier|public
class|class
name|CompressingCodec
extends|extends
name|FilterCodec
block|{
comment|/**    * Create a random instance.    */
DECL|method|randomInstance
specifier|public
specifier|static
name|CompressingCodec
name|randomInstance
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
specifier|final
name|CompressionMode
name|mode
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|CompressionMode
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|chunkSize
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
decl_stmt|;
specifier|final
name|CompressingStoredFieldsIndex
name|index
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|CompressingStoredFieldsIndex
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompressingCodec
argument_list|(
name|mode
argument_list|,
name|chunkSize
argument_list|,
name|index
argument_list|)
return|;
block|}
DECL|field|storedFieldsFormat
specifier|private
specifier|final
name|CompressingStoredFieldsFormat
name|storedFieldsFormat
decl_stmt|;
comment|/**    * @see CompressingStoredFieldsFormat#CompressingStoredFieldsFormat(CompressionMode, int, CompressingStoredFieldsIndex)    */
DECL|method|CompressingCodec
specifier|public
name|CompressingCodec
parameter_list|(
name|CompressionMode
name|compressionMode
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|CompressingStoredFieldsIndex
name|storedFieldsIndexFormat
parameter_list|)
block|{
name|super
argument_list|(
literal|"Compressing"
argument_list|,
operator|new
name|Lucene41Codec
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|storedFieldsFormat
operator|=
operator|new
name|CompressingStoredFieldsFormat
argument_list|(
name|compressionMode
argument_list|,
name|chunkSize
argument_list|,
name|storedFieldsIndexFormat
argument_list|)
expr_stmt|;
block|}
DECL|method|CompressingCodec
specifier|public
name|CompressingCodec
parameter_list|()
block|{
name|this
argument_list|(
name|CompressionMode
operator|.
name|FAST
argument_list|,
literal|1
operator|<<
literal|14
argument_list|,
name|CompressingStoredFieldsIndex
operator|.
name|MEMORY_CHUNK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|storedFieldsFormat
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
name|getName
argument_list|()
operator|+
literal|"(storedFieldsFormat="
operator|+
name|storedFieldsFormat
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
