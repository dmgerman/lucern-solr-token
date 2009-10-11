begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
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
name|Token
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
name|TokenFilter
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
name|TokenStream
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
name|PayloadAttribute
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
name|index
operator|.
name|Payload
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
comment|/**  * Assigns a payload to a token based on the {@link org.apache.lucene.analysis.Token#type()}  *  **/
end_comment
begin_class
DECL|class|NumericPayloadTokenFilter
specifier|public
class|class
name|NumericPayloadTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|typeMatch
specifier|private
name|String
name|typeMatch
decl_stmt|;
DECL|field|thePayload
specifier|private
name|Payload
name|thePayload
decl_stmt|;
DECL|field|payloadAtt
specifier|private
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|method|NumericPayloadTokenFilter
specifier|public
name|NumericPayloadTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|float
name|payload
parameter_list|,
name|String
name|typeMatch
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
comment|//Need to encode the payload
name|thePayload
operator|=
operator|new
name|Payload
argument_list|(
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeMatch
operator|=
name|typeMatch
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|typeAtt
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|typeMatch
argument_list|)
condition|)
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|thePayload
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class
end_unit
