begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|processors
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|queryParser
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
name|queryParser
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
name|queryParser
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
name|queryParser
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
name|queryParser
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|FuzzyAttribute
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
name|FuzzyQuery
import|;
end_import
begin_comment
comment|/**  * This processor iterates the query node tree looking for every  * {@link FuzzyQueryNode}, when this kind of node is found, it checks on the  * query configuration for {@link FuzzyAttribute}, gets the fuzzy prefix length  * and default similarity from it and set to the fuzzy node. For more  * information about fuzzy prefix length check: {@link FuzzyQuery}.<br/>  *   * @see FuzzyAttribute  * @see FuzzyQuery  * @see FuzzyQueryNode  */
end_comment
begin_class
DECL|class|FuzzyQueryNodeProcessor
specifier|public
class|class
name|FuzzyQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
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
if|if
condition|(
name|node
operator|instanceof
name|FuzzyQueryNode
condition|)
block|{
name|FuzzyQueryNode
name|fuzzyNode
init|=
operator|(
name|FuzzyQueryNode
operator|)
name|node
decl_stmt|;
name|QueryConfigHandler
name|config
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|hasAttribute
argument_list|(
name|FuzzyAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|FuzzyAttribute
name|fuzzyAttr
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|FuzzyAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|fuzzyNode
operator|.
name|setPrefixLength
argument_list|(
name|fuzzyAttr
operator|.
name|getPrefixLength
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fuzzyNode
operator|.
name|getSimilarity
argument_list|()
operator|<
literal|0
condition|)
block|{
name|fuzzyNode
operator|.
name|setSimilarity
argument_list|(
name|fuzzyAttr
operator|.
name|getFuzzyMinSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fuzzyNode
operator|.
name|getSimilarity
argument_list|()
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No "
operator|+
name|FuzzyAttribute
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" set in the config"
argument_list|)
throw|;
block|}
block|}
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
