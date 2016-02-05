begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.nodes
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
name|core
operator|.
name|nodes
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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_comment
comment|/**  * A {@link FieldQueryNode} represents a element that contains field/text tuple  */
end_comment
begin_class
DECL|class|FieldQueryNode
specifier|public
class|class
name|FieldQueryNode
extends|extends
name|QueryNodeImpl
implements|implements
name|FieldValuePairQueryNode
argument_list|<
name|CharSequence
argument_list|>
implements|,
name|TextableQueryNode
block|{
comment|/**    * The term's field    */
DECL|field|field
specifier|protected
name|CharSequence
name|field
decl_stmt|;
comment|/**    * The term's text.    */
DECL|field|text
specifier|protected
name|CharSequence
name|text
decl_stmt|;
comment|/**    * The term's begin position.    */
DECL|field|begin
specifier|protected
name|int
name|begin
decl_stmt|;
comment|/**    * The term's end position.    */
DECL|field|end
specifier|protected
name|int
name|end
decl_stmt|;
comment|/**    * The term's position increment.    */
DECL|field|positionIncrement
specifier|protected
name|int
name|positionIncrement
decl_stmt|;
comment|/**    * @param field    *          - field name    * @param text    *          - value    * @param begin    *          - position in the query string    * @param end    *          - position in the query string    */
DECL|method|FieldQueryNode
specifier|public
name|FieldQueryNode
parameter_list|(
name|CharSequence
name|field
parameter_list|,
name|CharSequence
name|text
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|begin
operator|=
name|begin
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|setLeaf
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getTermEscaped
specifier|protected
name|CharSequence
name|getTermEscaped
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
return|return
name|escaper
operator|.
name|escape
argument_list|(
name|this
operator|.
name|text
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|,
name|EscapeQuerySyntax
operator|.
name|Type
operator|.
name|NORMAL
argument_list|)
return|;
block|}
DECL|method|getTermEscapeQuoted
specifier|protected
name|CharSequence
name|getTermEscapeQuoted
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
return|return
name|escaper
operator|.
name|escape
argument_list|(
name|this
operator|.
name|text
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|,
name|EscapeQuerySyntax
operator|.
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escaper
parameter_list|)
block|{
if|if
condition|(
name|isDefaultField
argument_list|(
name|this
operator|.
name|field
argument_list|)
condition|)
block|{
return|return
name|getTermEscaped
argument_list|(
name|escaper
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|field
operator|+
literal|":"
operator|+
name|getTermEscaped
argument_list|(
name|escaper
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<field start='"
operator|+
name|this
operator|.
name|begin
operator|+
literal|"' end='"
operator|+
name|this
operator|.
name|end
operator|+
literal|"' field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' text='"
operator|+
name|this
operator|.
name|text
operator|+
literal|"'/>"
return|;
block|}
comment|/**    * @return the term    */
DECL|method|getTextAsString
specifier|public
name|String
name|getTextAsString
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|text
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|this
operator|.
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * returns null if the field was not specified in the query string    *     * @return the field    */
DECL|method|getFieldAsString
specifier|public
name|String
name|getFieldAsString
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|field
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|this
operator|.
name|field
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getBegin
specifier|public
name|int
name|getBegin
parameter_list|()
block|{
return|return
name|this
operator|.
name|begin
return|;
block|}
DECL|method|setBegin
specifier|public
name|void
name|setBegin
parameter_list|(
name|int
name|begin
parameter_list|)
block|{
name|this
operator|.
name|begin
operator|=
name|begin
expr_stmt|;
block|}
DECL|method|getEnd
specifier|public
name|int
name|getEnd
parameter_list|()
block|{
return|return
name|this
operator|.
name|end
return|;
block|}
DECL|method|setEnd
specifier|public
name|void
name|setEnd
parameter_list|(
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|CharSequence
name|getField
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|CharSequence
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getPositionIncrement
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
name|this
operator|.
name|positionIncrement
return|;
block|}
DECL|method|setPositionIncrement
specifier|public
name|void
name|setPositionIncrement
parameter_list|(
name|int
name|pi
parameter_list|)
block|{
name|this
operator|.
name|positionIncrement
operator|=
name|pi
expr_stmt|;
block|}
comment|/**    * Returns the term.    *     * @return The "original" form of the term.    */
annotation|@
name|Override
DECL|method|getText
specifier|public
name|CharSequence
name|getText
parameter_list|()
block|{
return|return
name|this
operator|.
name|text
return|;
block|}
comment|/**    * @param text    *          the text to set    */
annotation|@
name|Override
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|FieldQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|FieldQueryNode
name|fqn
init|=
operator|(
name|FieldQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
name|fqn
operator|.
name|begin
operator|=
name|this
operator|.
name|begin
expr_stmt|;
name|fqn
operator|.
name|end
operator|=
name|this
operator|.
name|end
expr_stmt|;
name|fqn
operator|.
name|field
operator|=
name|this
operator|.
name|field
expr_stmt|;
name|fqn
operator|.
name|text
operator|=
name|this
operator|.
name|text
expr_stmt|;
name|fqn
operator|.
name|positionIncrement
operator|=
name|this
operator|.
name|positionIncrement
expr_stmt|;
name|fqn
operator|.
name|toQueryStringIgnoreFields
operator|=
name|this
operator|.
name|toQueryStringIgnoreFields
expr_stmt|;
return|return
name|fqn
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|CharSequence
name|getValue
parameter_list|()
block|{
return|return
name|getText
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|CharSequence
name|value
parameter_list|)
block|{
name|setText
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
