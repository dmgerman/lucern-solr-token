begin_unit
begin_package
DECL|package|org.apache.lucene.collation.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
operator|.
name|tokenattributes
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|CharTermAttributeImpl
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * Extension of {@link CharTermAttributeImpl} that encodes the term  * text as a binary Unicode collation key instead of as UTF-8 bytes.  */
end_comment
begin_class
DECL|class|CollatedTermAttributeImpl
specifier|public
class|class
name|CollatedTermAttributeImpl
extends|extends
name|CharTermAttributeImpl
block|{
DECL|field|collator
specifier|private
specifier|final
name|Collator
name|collator
decl_stmt|;
comment|/**    * Create a new CollatedTermAttributeImpl    * @param collator Collation key generator    */
DECL|method|CollatedTermAttributeImpl
specifier|public
name|CollatedTermAttributeImpl
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
comment|// clone in case JRE doesnt properly sync,
comment|// or to reduce contention in case they do
name|this
operator|.
name|collator
operator|=
operator|(
name|Collator
operator|)
name|collator
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillBytesRef
specifier|public
name|int
name|fillBytesRef
parameter_list|()
block|{
name|BytesRef
name|bytes
init|=
name|getBytesRef
argument_list|()
decl_stmt|;
name|bytes
operator|.
name|bytes
operator|=
name|collator
operator|.
name|getCollationKey
argument_list|(
name|toString
argument_list|()
argument_list|)
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|bytes
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|bytes
operator|.
name|bytes
operator|.
name|length
expr_stmt|;
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
