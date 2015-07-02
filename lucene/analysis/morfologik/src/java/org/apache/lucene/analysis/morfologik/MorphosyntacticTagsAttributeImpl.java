begin_unit
begin_comment
comment|// -*- c-basic-offset: 2 -*-
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
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
name|*
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
comment|/**  * Morphosyntactic annotations for surface forms.  * @see MorphosyntacticTagsAttribute  */
end_comment
begin_class
DECL|class|MorphosyntacticTagsAttributeImpl
specifier|public
class|class
name|MorphosyntacticTagsAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|MorphosyntacticTagsAttribute
implements|,
name|Cloneable
block|{
comment|/** Initializes this attribute with no tags */
DECL|method|MorphosyntacticTagsAttributeImpl
specifier|public
name|MorphosyntacticTagsAttributeImpl
parameter_list|()
block|{}
comment|/**    * A list of potential tag variants for the current token.    */
DECL|field|tags
specifier|private
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|tags
decl_stmt|;
comment|/**    * Returns the POS tag of the term. If you need a copy of this char sequence, copy    * its contents (and clone {@link StringBuilder}s) because it changes with     * each new term to avoid unnecessary memory allocations.    */
annotation|@
name|Override
DECL|method|getTags
specifier|public
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|getTags
parameter_list|()
block|{
return|return
name|tags
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|tags
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|MorphosyntacticTagsAttribute
condition|)
block|{
return|return
name|equal
argument_list|(
name|this
operator|.
name|getTags
argument_list|()
argument_list|,
operator|(
operator|(
name|MorphosyntacticTagsAttribute
operator|)
name|other
operator|)
operator|.
name|getTags
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|equal
specifier|private
name|boolean
name|equal
parameter_list|(
name|Object
name|l1
parameter_list|,
name|Object
name|l2
parameter_list|)
block|{
return|return
name|l1
operator|==
literal|null
condition|?
operator|(
name|l2
operator|==
literal|null
operator|)
else|:
operator|(
name|l1
operator|.
name|equals
argument_list|(
name|l2
argument_list|)
operator|)
return|;
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
name|this
operator|.
name|tags
operator|==
literal|null
condition|?
literal|0
else|:
name|tags
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Sets the internal tags reference to the given list. The contents    * is not copied.     */
annotation|@
name|Override
DECL|method|setTags
specifier|public
name|void
name|setTags
parameter_list|(
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|tags
parameter_list|)
block|{
name|this
operator|.
name|tags
operator|=
name|tags
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
name|List
argument_list|<
name|StringBuilder
argument_list|>
name|cloned
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tags
operator|!=
literal|null
condition|)
block|{
name|cloned
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|tags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|StringBuilder
name|b
range|:
name|tags
control|)
block|{
name|cloned
operator|.
name|add
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
operator|(
operator|(
name|MorphosyntacticTagsAttribute
operator|)
name|target
operator|)
operator|.
name|setTags
argument_list|(
name|cloned
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MorphosyntacticTagsAttributeImpl
name|clone
parameter_list|()
block|{
name|MorphosyntacticTagsAttributeImpl
name|cloned
init|=
operator|new
name|MorphosyntacticTagsAttributeImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|copyTo
argument_list|(
name|cloned
argument_list|)
expr_stmt|;
return|return
name|cloned
return|;
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
name|MorphosyntacticTagsAttribute
operator|.
name|class
argument_list|,
literal|"tags"
argument_list|,
name|tags
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
