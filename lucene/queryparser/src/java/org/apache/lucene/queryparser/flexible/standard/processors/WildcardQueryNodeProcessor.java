begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
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
name|standard
operator|.
name|processors
package|;
end_package
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
name|core
operator|.
name|QueryNodeException
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
name|nodes
operator|.
name|FieldQueryNode
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
name|nodes
operator|.
name|FuzzyQueryNode
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
name|nodes
operator|.
name|QueryNode
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
name|nodes
operator|.
name|QuotedFieldQueryNode
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
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|util
operator|.
name|UnescapedCharSequence
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
name|standard
operator|.
name|nodes
operator|.
name|PrefixWildcardQueryNode
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
name|standard
operator|.
name|nodes
operator|.
name|TermRangeQueryNode
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
name|standard
operator|.
name|nodes
operator|.
name|WildcardQueryNode
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
name|standard
operator|.
name|parser
operator|.
name|StandardSyntaxParser
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
name|search
operator|.
name|PrefixQuery
import|;
end_import
begin_comment
comment|/**  * The {@link StandardSyntaxParser} creates {@link PrefixWildcardQueryNode} nodes which  * have values containing the prefixed wildcard. However, Lucene  * {@link PrefixQuery} cannot contain the prefixed wildcard. So, this processor  * basically removed the prefixed wildcard from the  * {@link PrefixWildcardQueryNode} value.  *   * @see PrefixQuery  * @see PrefixWildcardQueryNode  */
end_comment
begin_class
DECL|class|WildcardQueryNodeProcessor
specifier|public
class|class
name|WildcardQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|WildcardQueryNodeProcessor
specifier|public
name|WildcardQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
comment|// the old Lucene Parser ignores FuzzyQueryNode that are also PrefixWildcardQueryNode or WildcardQueryNode
comment|// we do the same here, also ignore empty terms
if|if
condition|(
name|node
operator|instanceof
name|FieldQueryNode
operator|||
name|node
operator|instanceof
name|FuzzyQueryNode
condition|)
block|{
name|FieldQueryNode
name|fqn
init|=
operator|(
name|FieldQueryNode
operator|)
name|node
decl_stmt|;
name|CharSequence
name|text
init|=
name|fqn
operator|.
name|getText
argument_list|()
decl_stmt|;
comment|// do not process wildcards for TermRangeQueryNode children and
comment|// QuotedFieldQueryNode to reproduce the old parser behavior
if|if
condition|(
name|fqn
operator|.
name|getParent
argument_list|()
operator|instanceof
name|TermRangeQueryNode
operator|||
name|fqn
operator|instanceof
name|QuotedFieldQueryNode
operator|||
name|text
operator|.
name|length
argument_list|()
operator|<=
literal|0
condition|)
block|{
comment|// Ignore empty terms
return|return
name|node
return|;
block|}
comment|// Code below simulates the old lucene parser behavior for wildcards
if|if
condition|(
name|isPrefixWildcard
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|PrefixWildcardQueryNode
name|prefixWildcardQN
init|=
operator|new
name|PrefixWildcardQueryNode
argument_list|(
name|fqn
argument_list|)
decl_stmt|;
return|return
name|prefixWildcardQN
return|;
block|}
elseif|else
if|if
condition|(
name|isWildcard
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|WildcardQueryNode
name|wildcardQN
init|=
operator|new
name|WildcardQueryNode
argument_list|(
name|fqn
argument_list|)
decl_stmt|;
return|return
name|wildcardQN
return|;
block|}
block|}
return|return
name|node
return|;
block|}
DECL|method|isWildcard
specifier|private
name|boolean
name|isWildcard
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
if|if
condition|(
name|text
operator|==
literal|null
operator|||
name|text
operator|.
name|length
argument_list|()
operator|<=
literal|0
condition|)
return|return
literal|false
return|;
comment|// If a un-escaped '*' or '?' if found return true
comment|// start at the end since it's more common to put wildcards at the end
for|for
control|(
name|int
name|i
init|=
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|(
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'*'
operator|||
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'?'
operator|)
operator|&&
operator|!
name|UnescapedCharSequence
operator|.
name|wasEscaped
argument_list|(
name|text
argument_list|,
name|i
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|isPrefixWildcard
specifier|private
name|boolean
name|isPrefixWildcard
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
if|if
condition|(
name|text
operator|==
literal|null
operator|||
name|text
operator|.
name|length
argument_list|()
operator|<=
literal|0
operator|||
operator|!
name|isWildcard
argument_list|(
name|text
argument_list|)
condition|)
return|return
literal|false
return|;
comment|// Validate last character is a '*' and was not escaped
comment|// If single '*' is is a wildcard not prefix to simulate old queryparser
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'*'
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|UnescapedCharSequence
operator|.
name|wasEscaped
argument_list|(
name|text
argument_list|,
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
return|return
literal|false
return|;
comment|// Only make a prefix if there is only one single star at the end and no '?' or '*' characters
comment|// If single wildcard return false to mimic old queryparser
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'?'
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'*'
operator|&&
operator|!
name|UnescapedCharSequence
operator|.
name|wasEscaped
argument_list|(
name|text
argument_list|,
name|i
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
return|return
literal|true
return|;
else|else
return|return
literal|false
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class
end_unit
