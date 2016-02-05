begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|spans
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldableNode
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
comment|/**  * This attribute is used by the {@link UniqueFieldQueryNodeProcessor}  * processor. It holds a value that defines which is the unique field name that  * should be set in every {@link FieldableNode}.  *   * @see UniqueFieldQueryNodeProcessor  */
end_comment
begin_class
DECL|class|UniqueFieldAttributeImpl
specifier|public
class|class
name|UniqueFieldAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|UniqueFieldAttribute
block|{
DECL|field|uniqueField
specifier|private
name|CharSequence
name|uniqueField
decl_stmt|;
DECL|method|UniqueFieldAttributeImpl
specifier|public
name|UniqueFieldAttributeImpl
parameter_list|()
block|{
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|uniqueField
operator|=
literal|""
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUniqueField
specifier|public
name|void
name|setUniqueField
parameter_list|(
name|CharSequence
name|uniqueField
parameter_list|)
block|{
name|this
operator|.
name|uniqueField
operator|=
name|uniqueField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUniqueField
specifier|public
name|CharSequence
name|getUniqueField
parameter_list|()
block|{
return|return
name|this
operator|.
name|uniqueField
return|;
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
if|if
condition|(
operator|!
operator|(
name|target
operator|instanceof
name|UniqueFieldAttributeImpl
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot copy the values from attribute UniqueFieldAttribute to an instance of "
operator|+
name|target
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|UniqueFieldAttributeImpl
name|uniqueFieldAttr
init|=
operator|(
name|UniqueFieldAttributeImpl
operator|)
name|target
decl_stmt|;
name|uniqueFieldAttr
operator|.
name|uniqueField
operator|=
name|uniqueField
operator|.
name|toString
argument_list|()
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
name|UniqueFieldAttributeImpl
condition|)
block|{
return|return
operator|(
operator|(
name|UniqueFieldAttributeImpl
operator|)
name|other
operator|)
operator|.
name|uniqueField
operator|.
name|equals
argument_list|(
name|this
operator|.
name|uniqueField
argument_list|)
return|;
block|}
return|return
literal|false
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
name|uniqueField
operator|.
name|hashCode
argument_list|()
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
name|UniqueFieldAttribute
operator|.
name|class
argument_list|,
literal|"uniqueField"
argument_list|,
name|uniqueField
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
