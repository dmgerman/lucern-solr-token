begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|AttributeImpl
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
name|AttributeReflector
import|;
end_import
begin_comment
comment|/** Default implementation of {@link KeywordAttribute}. */
end_comment
begin_class
DECL|class|KeywordAttributeImpl
specifier|public
specifier|final
class|class
name|KeywordAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|KeywordAttribute
block|{
DECL|field|keyword
specifier|private
name|boolean
name|keyword
decl_stmt|;
comment|/** Initialize this attribute with the keyword value as false. */
DECL|method|KeywordAttributeImpl
specifier|public
name|KeywordAttributeImpl
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|keyword
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|KeywordAttribute
name|attr
init|=
operator|(
name|KeywordAttribute
operator|)
name|target
decl_stmt|;
name|attr
operator|.
name|setKeyword
argument_list|(
name|keyword
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|keyword
condition|?
literal|31
else|:
literal|37
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|KeywordAttributeImpl
name|other
init|=
operator|(
name|KeywordAttributeImpl
operator|)
name|obj
decl_stmt|;
return|return
name|keyword
operator|==
name|other
operator|.
name|keyword
return|;
block|}
annotation|@
name|Override
DECL|method|isKeyword
specifier|public
name|boolean
name|isKeyword
parameter_list|()
block|{
return|return
name|keyword
return|;
block|}
annotation|@
name|Override
DECL|method|setKeyword
specifier|public
name|void
name|setKeyword
parameter_list|(
name|boolean
name|isKeyword
parameter_list|)
block|{
name|keyword
operator|=
name|isKeyword
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|reflector
operator|.
name|reflect
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|,
literal|"keyword"
argument_list|,
name|keyword
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
