begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.processors
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
name|*
import|;
end_import
begin_comment
comment|/**  *<p>  * A {@link NoChildOptimizationQueryNodeProcessor} removes every  * BooleanQueryNode, BoostQueryNode, TokenizedPhraseQueryNode or  * ModifierQueryNode that do not have a valid children.  *</p>  *<p>  * Example: When the children of these nodes are removed for any reason then the  * nodes may become invalid.  *</p>  */
end_comment
begin_class
DECL|class|NoChildOptimizationQueryNodeProcessor
specifier|public
class|class
name|NoChildOptimizationQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|NoChildOptimizationQueryNodeProcessor
specifier|public
name|NoChildOptimizationQueryNodeProcessor
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
if|if
condition|(
name|node
operator|instanceof
name|BooleanQueryNode
operator|||
name|node
operator|instanceof
name|BoostQueryNode
operator|||
name|node
operator|instanceof
name|TokenizedPhraseQueryNode
operator|||
name|node
operator|instanceof
name|ModifierQueryNode
condition|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
name|children
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|child
operator|instanceof
name|DeletedQueryNode
operator|)
condition|)
block|{
return|return
name|node
return|;
block|}
block|}
block|}
return|return
operator|new
name|MatchNoDocsQueryNode
argument_list|()
return|;
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
