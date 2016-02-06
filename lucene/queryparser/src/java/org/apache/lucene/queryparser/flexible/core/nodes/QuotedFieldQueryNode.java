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
begin_comment
comment|/**  * A {@link QuotedFieldQueryNode} represents phrase query. Example:  * "life is great"  */
end_comment
begin_class
DECL|class|QuotedFieldQueryNode
specifier|public
class|class
name|QuotedFieldQueryNode
extends|extends
name|FieldQueryNode
block|{
comment|/**    * @param field    *          - field name    * @param text    *          - value    * @param begin    *          - position in the query string    * @param end    *          - position in the query string    */
DECL|method|QuotedFieldQueryNode
specifier|public
name|QuotedFieldQueryNode
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
name|super
argument_list|(
name|field
argument_list|,
name|text
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
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
literal|"\""
operator|+
name|getTermEscapeQuoted
argument_list|(
name|escaper
argument_list|)
operator|+
literal|"\""
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
literal|"\""
operator|+
name|getTermEscapeQuoted
argument_list|(
name|escaper
argument_list|)
operator|+
literal|"\""
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
literal|"<quotedfield start='"
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
literal|"' term='"
operator|+
name|this
operator|.
name|text
operator|+
literal|"'/>"
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|QuotedFieldQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|QuotedFieldQueryNode
name|clone
init|=
operator|(
name|QuotedFieldQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
comment|// nothing to do here
return|return
name|clone
return|;
block|}
block|}
end_class
end_unit
