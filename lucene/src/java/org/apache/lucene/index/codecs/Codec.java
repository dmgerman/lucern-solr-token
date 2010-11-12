begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|SegmentInfo
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
name|store
operator|.
name|Directory
import|;
end_import
begin_comment
comment|/** @lucene.experimental */
end_comment
begin_class
DECL|class|Codec
specifier|public
specifier|abstract
class|class
name|Codec
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|Codec
index|[]
name|EMPTY
init|=
operator|new
name|Codec
index|[
literal|0
index|]
decl_stmt|;
comment|/** Unique name that's used to retrieve this codec when    *  reading the index */
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
comment|/** Writes a new segment */
DECL|method|fieldsConsumer
specifier|public
specifier|abstract
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|debug
specifier|public
specifier|static
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
if|if
condition|(
name|desc
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" ["
operator|+
name|desc
operator|+
literal|"]:"
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|debug
specifier|public
specifier|static
name|void
name|debug
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|debug
argument_list|(
name|s
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Reads a segment.  NOTE: by the time this call    *  returns, it must hold open any files it will need to    *  use; else, those files may be deleted. */
DECL|method|fieldsProducer
specifier|public
specifier|abstract
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Gathers files associated with this segment */
DECL|method|files
specifier|public
specifier|abstract
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Records all file extensions this codec uses */
DECL|method|getExtensions
specifier|public
specifier|abstract
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class
end_unit
