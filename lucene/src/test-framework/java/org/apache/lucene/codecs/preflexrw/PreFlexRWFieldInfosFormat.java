begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.preflexrw
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|preflexrw
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|FieldInfosReader
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
name|lucene3x
operator|.
name|Lucene3xFieldInfosFormat
import|;
end_import
begin_comment
comment|/**  *   * @lucene.internal  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PreFlexRWFieldInfosFormat
specifier|public
class|class
name|PreFlexRWFieldInfosFormat
extends|extends
name|Lucene3xFieldInfosFormat
block|{
annotation|@
name|Override
DECL|method|getFieldInfosReader
specifier|public
name|FieldInfosReader
name|getFieldInfosReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|PreFlexRWFieldInfosReader
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfosWriter
specifier|public
name|FieldInfosWriter
name|getFieldInfosWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|PreFlexRWFieldInfosWriter
argument_list|()
return|;
block|}
block|}
end_class
end_unit
