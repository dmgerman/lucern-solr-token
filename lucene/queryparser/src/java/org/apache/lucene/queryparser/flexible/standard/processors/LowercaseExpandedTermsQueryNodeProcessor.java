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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|config
operator|.
name|QueryConfigHandler
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
name|RangeQueryNode
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
name|TextableQueryNode
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
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
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
name|RegexpQueryNode
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
begin_comment
comment|/**  * This processor verifies if   * {@link ConfigurationKeys#LOWERCASE_EXPANDED_TERMS} is defined in the  * {@link QueryConfigHandler}. If it is and the expanded terms should be  * lower-cased, it looks for every {@link WildcardQueryNode},  * {@link FuzzyQueryNode} and children of a {@link RangeQueryNode} and lower-case its  * term.  *   * @see ConfigurationKeys#LOWERCASE_EXPANDED_TERMS  */
end_comment
begin_class
DECL|class|LowercaseExpandedTermsQueryNodeProcessor
specifier|public
class|class
name|LowercaseExpandedTermsQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|LowercaseExpandedTermsQueryNodeProcessor
specifier|public
name|LowercaseExpandedTermsQueryNodeProcessor
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|process
specifier|public
name|QueryNode
name|process
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|Boolean
name|lowercaseExpandedTerms
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|LOWERCASE_EXPANDED_TERMS
argument_list|)
decl_stmt|;
if|if
condition|(
name|lowercaseExpandedTerms
operator|!=
literal|null
operator|&&
name|lowercaseExpandedTerms
condition|)
block|{
return|return
name|super
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
return|return
name|queryTree
return|;
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
name|Locale
name|locale
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|LOCALE
argument_list|)
decl_stmt|;
if|if
condition|(
name|locale
operator|==
literal|null
condition|)
block|{
name|locale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|instanceof
name|WildcardQueryNode
operator|||
name|node
operator|instanceof
name|FuzzyQueryNode
operator|||
operator|(
name|node
operator|instanceof
name|FieldQueryNode
operator|&&
name|node
operator|.
name|getParent
argument_list|()
operator|instanceof
name|RangeQueryNode
operator|)
operator|||
name|node
operator|instanceof
name|RegexpQueryNode
condition|)
block|{
name|TextableQueryNode
name|txtNode
init|=
operator|(
name|TextableQueryNode
operator|)
name|node
decl_stmt|;
name|CharSequence
name|text
init|=
name|txtNode
operator|.
name|getText
argument_list|()
decl_stmt|;
name|txtNode
operator|.
name|setText
argument_list|(
name|text
operator|!=
literal|null
condition|?
name|UnescapedCharSequence
operator|.
name|toLowerCase
argument_list|(
name|text
argument_list|,
name|locale
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|node
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
