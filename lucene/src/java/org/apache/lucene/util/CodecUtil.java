begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|store
operator|.
name|IndexInput
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
name|CorruptIndexException
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
name|IndexFormatTooNewException
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
name|IndexFormatTooOldException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CodecUtil
specifier|public
specifier|final
class|class
name|CodecUtil
block|{
DECL|field|CODEC_MAGIC
specifier|private
specifier|final
specifier|static
name|int
name|CODEC_MAGIC
init|=
literal|0x3fd76c17
decl_stmt|;
DECL|method|writeHeader
specifier|public
specifier|static
name|void
name|writeHeader
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|start
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|CODEC_MAGIC
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|version
argument_list|)
expr_stmt|;
comment|// We require this so we can easily pre-compute header length
if|if
condition|(
name|out
operator|.
name|getFilePointer
argument_list|()
operator|-
name|start
operator|!=
name|codec
operator|.
name|length
argument_list|()
operator|+
literal|9
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec must be simple ASCII, less than 128 characters in length [got "
operator|+
name|codec
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|headerLength
specifier|public
specifier|static
name|int
name|headerLength
parameter_list|(
name|String
name|codec
parameter_list|)
block|{
return|return
literal|9
operator|+
name|codec
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|checkHeader
specifier|public
specifier|static
name|int
name|checkHeader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|String
name|codec
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Safety to guard against reading a bogus string:
specifier|final
name|int
name|actualHeader
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|actualHeader
operator|!=
name|CODEC_MAGIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"codec header mismatch: actual header="
operator|+
name|actualHeader
operator|+
literal|" vs expected header="
operator|+
name|CODEC_MAGIC
argument_list|)
throw|;
block|}
specifier|final
name|String
name|actualCodec
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|actualCodec
operator|.
name|equals
argument_list|(
name|codec
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"codec mismatch: actual codec="
operator|+
name|actualCodec
operator|+
literal|" vs expected codec="
operator|+
name|codec
argument_list|)
throw|;
block|}
specifier|final
name|int
name|actualVersion
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|actualVersion
operator|<
name|minVersion
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooOldException
argument_list|(
literal|null
argument_list|,
name|actualVersion
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
throw|;
block|}
if|if
condition|(
name|actualVersion
operator|>
name|maxVersion
condition|)
block|{
throw|throw
operator|new
name|IndexFormatTooNewException
argument_list|(
literal|null
argument_list|,
name|actualVersion
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
throw|;
block|}
return|return
name|actualVersion
return|;
block|}
block|}
end_class
end_unit
