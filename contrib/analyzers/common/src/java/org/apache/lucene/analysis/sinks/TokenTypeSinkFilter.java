begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
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
name|analysis
operator|.
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TypeAttribute
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
name|AttributeSource
import|;
end_import
begin_class
DECL|class|TokenTypeSinkFilter
specifier|public
class|class
name|TokenTypeSinkFilter
extends|extends
name|SinkFilter
block|{
DECL|field|typeToMatch
specifier|private
name|String
name|typeToMatch
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|method|TokenTypeSinkFilter
specifier|public
name|TokenTypeSinkFilter
parameter_list|(
name|String
name|typeToMatch
parameter_list|)
block|{
name|this
operator|.
name|typeToMatch
operator|=
name|typeToMatch
expr_stmt|;
block|}
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|typeAtt
operator|==
literal|null
condition|)
block|{
name|typeAtt
operator|=
name|source
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|//check to see if this is a Category
return|return
operator|(
name|typeToMatch
operator|.
name|equals
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
operator|)
return|;
block|}
block|}
end_class
end_unit
