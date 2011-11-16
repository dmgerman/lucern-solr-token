begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.simpletext
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
operator|.
name|simpletext
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
name|index
operator|.
name|codecs
operator|.
name|SegmentInfosFormat
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
name|codecs
operator|.
name|SegmentInfosReader
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
name|codecs
operator|.
name|SegmentInfosWriter
import|;
end_import
begin_comment
comment|/**  * plain text segments file format.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleTextSegmentInfosFormat
specifier|public
class|class
name|SimpleTextSegmentInfosFormat
extends|extends
name|SegmentInfosFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|SegmentInfosReader
name|reader
init|=
operator|new
name|SimpleTextSegmentInfosReader
argument_list|()
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|SegmentInfosWriter
name|writer
init|=
operator|new
name|SimpleTextSegmentInfosWriter
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getSegmentInfosReader
specifier|public
name|SegmentInfosReader
name|getSegmentInfosReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfosWriter
name|getSegmentInfosWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
block|}
end_class
end_unit
