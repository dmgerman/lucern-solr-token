begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.requestfactory
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
name|distributed
operator|.
name|requestfactory
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|GroupParams
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|ShardParams
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
name|StrUtils
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
name|ShardDoc
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
name|ShardRequest
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
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|ShardRequestFactory
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
operator|.
name|QueryCommandResult
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|StoredFieldsShardRequestFactory
specifier|public
class|class
name|StoredFieldsShardRequestFactory
implements|implements
name|ShardRequestFactory
block|{
annotation|@
name|Override
DECL|method|constructRequest
specifier|public
name|ShardRequest
index|[]
name|constructRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|ShardDoc
argument_list|>
argument_list|>
name|shardMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|topGroups
range|:
name|rb
operator|.
name|mergedTopGroups
operator|.
name|values
argument_list|()
control|)
block|{
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
name|mapShardToDocs
argument_list|(
name|shardMap
argument_list|,
name|group
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|QueryCommandResult
name|queryCommandResult
range|:
name|rb
operator|.
name|mergedQueryCommandResults
operator|.
name|values
argument_list|()
control|)
block|{
name|mapShardToDocs
argument_list|(
name|shardMap
argument_list|,
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
name|ShardRequest
index|[]
name|shardRequests
init|=
operator|new
name|ShardRequest
index|[
name|shardMap
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|SchemaField
name|uniqueField
init|=
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Collection
argument_list|<
name|ShardDoc
argument_list|>
name|shardDocs
range|:
name|shardMap
operator|.
name|values
argument_list|()
control|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
expr_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|shardDocs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|shard
block|}
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|GroupParams
operator|.
name|GROUP
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|ResponseBuilder
operator|.
name|FIELD_SORT_VALUES
argument_list|)
expr_stmt|;
name|String
name|fl
init|=
name|sreq
operator|.
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
decl_stmt|;
if|if
condition|(
name|fl
operator|!=
literal|null
condition|)
block|{
name|fl
operator|=
name|fl
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// currently, "score" is synonymous with "*,score" so
comment|// don't add "id" if the fl is empty or "score" or it would change the meaning.
if|if
condition|(
name|fl
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
literal|"score"
operator|.
name|equals
argument_list|(
name|fl
argument_list|)
operator|&&
operator|!
literal|"*"
operator|.
name|equals
argument_list|(
name|fl
argument_list|)
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|fl
operator|+
literal|','
operator|+
name|uniqueField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|shardDocs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardDoc
name|shardDoc
range|:
name|shardDocs
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|shardDoc
operator|.
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|ShardParams
operator|.
name|IDS
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|ids
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|shardRequests
index|[
name|i
operator|++
index|]
operator|=
name|sreq
expr_stmt|;
block|}
return|return
name|shardRequests
return|;
block|}
DECL|method|mapShardToDocs
specifier|private
name|void
name|mapShardToDocs
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|ShardDoc
argument_list|>
argument_list|>
name|shardMap
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|)
block|{
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|scoreDocs
control|)
block|{
name|ShardDoc
name|solrDoc
init|=
operator|(
name|ShardDoc
operator|)
name|scoreDoc
decl_stmt|;
name|Set
argument_list|<
name|ShardDoc
argument_list|>
name|shardDocs
init|=
name|shardMap
operator|.
name|get
argument_list|(
name|solrDoc
operator|.
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardDocs
operator|==
literal|null
condition|)
block|{
name|shardMap
operator|.
name|put
argument_list|(
name|solrDoc
operator|.
name|shard
argument_list|,
name|shardDocs
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|shardDocs
operator|.
name|add
argument_list|(
name|solrDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
