begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.builders
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
name|builders
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
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|index
operator|.
name|Term
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
name|builders
operator|.
name|QueryTreeBuilder
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
name|standard
operator|.
name|nodes
operator|.
name|MultiPhraseQueryNode
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
name|MultiPhraseQuery
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
name|TermQuery
import|;
end_import
begin_comment
comment|/**  * Builds a {@link MultiPhraseQuery} object from a {@link MultiPhraseQueryNode}  * object.  */
end_comment
begin_class
DECL|class|MultiPhraseQueryNodeBuilder
specifier|public
class|class
name|MultiPhraseQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|MultiPhraseQueryNodeBuilder
specifier|public
name|MultiPhraseQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|MultiPhraseQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|MultiPhraseQueryNode
name|phraseNode
init|=
operator|(
name|MultiPhraseQueryNode
operator|)
name|queryNode
decl_stmt|;
name|MultiPhraseQuery
name|phraseQuery
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|phraseNode
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Term
argument_list|>
argument_list|>
name|positionTermMap
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Term
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|FieldQueryNode
name|termNode
init|=
operator|(
name|FieldQueryNode
operator|)
name|child
decl_stmt|;
name|TermQuery
name|termQuery
init|=
operator|(
name|TermQuery
operator|)
name|termNode
operator|.
name|getTag
argument_list|(
name|QueryTreeBuilder
operator|.
name|QUERY_TREE_BUILDER_TAGID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|termList
init|=
name|positionTermMap
operator|.
name|get
argument_list|(
name|termNode
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|termList
operator|==
literal|null
condition|)
block|{
name|termList
operator|=
operator|new
name|LinkedList
argument_list|<
name|Term
argument_list|>
argument_list|()
expr_stmt|;
name|positionTermMap
operator|.
name|put
argument_list|(
name|termNode
operator|.
name|getPositionIncrement
argument_list|()
argument_list|,
name|termList
argument_list|)
expr_stmt|;
block|}
name|termList
operator|.
name|add
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|positionIncrement
range|:
name|positionTermMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Term
argument_list|>
name|termList
init|=
name|positionTermMap
operator|.
name|get
argument_list|(
name|positionIncrement
argument_list|)
decl_stmt|;
name|phraseQuery
operator|.
name|add
argument_list|(
name|termList
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|termList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|positionIncrement
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|phraseQuery
return|;
block|}
block|}
end_class
end_unit
