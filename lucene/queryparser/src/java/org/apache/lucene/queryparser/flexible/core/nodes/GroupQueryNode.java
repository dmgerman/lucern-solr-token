begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|queryparser
operator|.
name|flexible
operator|.
name|messages
operator|.
name|MessageImpl
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeError
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
comment|/**  * A {@link GroupQueryNode} represents a location where the original user typed  * real parenthesis on the query string. This class is useful for queries like:  * a) a AND b OR c b) ( a AND b) OR c  *   * Parenthesis might be used to define the boolean operation precedence.  */
end_comment
begin_class
DECL|class|GroupQueryNode
specifier|public
class|class
name|GroupQueryNode
extends|extends
name|QueryNodeImpl
block|{
comment|/**    * This QueryNode is used to identify parenthesis on the original query string    */
DECL|method|GroupQueryNode
specifier|public
name|GroupQueryNode
parameter_list|(
name|QueryNode
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryNodeError
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|PARAMETER_VALUE_NOT_SUPPORTED
argument_list|,
literal|"query"
argument_list|,
literal|"null"
argument_list|)
argument_list|)
throw|;
block|}
name|allocate
argument_list|()
expr_stmt|;
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|getChild
specifier|public
name|QueryNode
name|getChild
parameter_list|()
block|{
return|return
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
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
literal|"<group>"
operator|+
literal|"\n"
operator|+
name|getChild
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"\n</group>"
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
name|escapeSyntaxParser
parameter_list|)
block|{
if|if
condition|(
name|getChild
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|""
return|;
return|return
literal|"( "
operator|+
name|getChild
argument_list|()
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
operator|+
literal|" )"
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|GroupQueryNode
name|clone
init|=
operator|(
name|GroupQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|setChild
specifier|public
name|void
name|setChild
parameter_list|(
name|QueryNode
name|child
parameter_list|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
