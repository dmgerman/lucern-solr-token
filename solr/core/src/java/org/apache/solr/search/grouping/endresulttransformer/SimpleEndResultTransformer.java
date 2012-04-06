begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.endresulttransformer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|endresulttransformer
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ScoreDoc
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
name|grouping
operator|.
name|GroupDocs
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
name|grouping
operator|.
name|TopGroups
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
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Implementation of {@link EndResultTransformer} that transforms the grouped result into a single flat list.  */
end_comment
begin_class
DECL|class|SimpleEndResultTransformer
specifier|public
class|class
name|SimpleEndResultTransformer
implements|implements
name|EndResultTransformer
block|{
comment|/**    * {@inheritDoc}    */
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|result
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrDocumentSource
name|solrDocumentSource
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|commands
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|result
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|TopGroups
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|value
argument_list|)
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|topGroups
init|=
operator|(
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
operator|)
name|value
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|command
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"matches"
argument_list|,
name|rb
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|topGroups
operator|.
name|totalGroupCount
operator|!=
literal|null
condition|)
block|{
name|command
operator|.
name|add
argument_list|(
literal|"ngroups"
argument_list|,
name|topGroups
operator|.
name|totalGroupCount
argument_list|)
expr_stmt|;
block|}
name|SolrDocumentList
name|docList
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|docList
operator|.
name|setStart
argument_list|(
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|docList
operator|.
name|setNumFound
argument_list|(
name|topGroups
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
name|Float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|group
range|:
name|topGroups
operator|.
name|groups
control|)
block|{
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|group
operator|.
name|scoreDocs
control|)
block|{
if|if
condition|(
name|maxScore
operator|<
name|scoreDoc
operator|.
name|score
condition|)
block|{
name|maxScore
operator|=
name|scoreDoc
operator|.
name|score
expr_stmt|;
block|}
name|docList
operator|.
name|add
argument_list|(
name|solrDocumentSource
operator|.
name|retrieve
argument_list|(
name|scoreDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxScore
operator|!=
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
name|docList
operator|.
name|setMaxScore
argument_list|(
name|maxScore
argument_list|)
expr_stmt|;
block|}
name|command
operator|.
name|add
argument_list|(
literal|"doclist"
argument_list|,
name|docList
argument_list|)
expr_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"grouped"
argument_list|,
name|commands
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
