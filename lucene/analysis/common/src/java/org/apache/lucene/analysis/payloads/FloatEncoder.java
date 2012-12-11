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
name|BytesRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  Encode a character array Float as a {@link BytesRef}.  *<p/>  * @see org.apache.lucene.analysis.payloads.PayloadHelper#encodeFloat(float, byte[], int)  *  **/
end_comment
begin_class
DECL|class|FloatEncoder
specifier|public
class|class
name|FloatEncoder
extends|extends
name|AbstractEncoder
implements|implements
name|PayloadEncoder
block|{
annotation|@
name|Override
DECL|method|encode
specifier|public
name|BytesRef
name|encode
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|float
name|payload
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
comment|//TODO: improve this so that we don't have to new Strings
name|byte
index|[]
name|bytes
init|=
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|BytesRef
name|result
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
