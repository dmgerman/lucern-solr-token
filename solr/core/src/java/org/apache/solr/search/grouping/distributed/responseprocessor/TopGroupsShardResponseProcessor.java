begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.responseprocessor
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
name|responseprocessor
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
name|Sort
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
name|TopDocs
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|SolrException
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
name|handler
operator|.
name|component
operator|.
name|ShardResponse
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
name|Grouping
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
name|ShardResponseProcessor
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
name|shardresultserializer
operator|.
name|TopGroupsResultTransformer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
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
name|HashMap
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
name|Map
import|;
end_import
begin_comment
comment|/**  * Concrete implementation for merging {@link TopGroups} instances from shard responses.  */
end_comment
begin_class
DECL|class|TopGroupsShardResponseProcessor
specifier|public
class|class
name|TopGroupsShardResponseProcessor
implements|implements
name|ShardResponseProcessor
block|{
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|shardRequest
parameter_list|)
block|{
name|Sort
name|groupSort
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getGroupSort
argument_list|()
decl_stmt|;
name|String
index|[]
name|fields
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|String
index|[]
name|queries
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getQueries
argument_list|()
decl_stmt|;
name|Sort
name|sortWithinGroup
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getSortWithinGroup
argument_list|()
decl_stmt|;
comment|// If group.format=simple group.offset doesn't make sense
name|int
name|groupOffsetDefault
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getResponseFormat
argument_list|()
operator|==
name|Grouping
operator|.
name|Format
operator|.
name|simple
operator|||
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|isMain
argument_list|()
condition|)
block|{
name|groupOffsetDefault
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|groupOffsetDefault
operator|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getGroupOffset
argument_list|()
expr_stmt|;
block|}
name|int
name|docsPerGroupDefault
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getGroupLimit
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|commandTopGroups
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|commandTopGroups
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|QueryCommandResult
argument_list|>
argument_list|>
name|commandTopDocs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|QueryCommandResult
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|query
range|:
name|queries
control|)
block|{
name|commandTopDocs
operator|.
name|put
argument_list|(
name|query
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|QueryCommandResult
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TopGroupsResultTransformer
name|serializer
init|=
operator|new
name|TopGroupsResultTransformer
argument_list|(
name|rb
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|shardInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|shardInfo
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|add
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
name|shardInfo
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ShardResponse
name|srsp
range|:
name|shardRequest
operator|.
name|responses
control|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|individualShardInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|shardInfo
operator|!=
literal|null
condition|)
block|{
name|individualShardInfo
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|t
init|=
name|srsp
operator|.
name|getException
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|SolrServerException
condition|)
block|{
name|t
operator|=
operator|(
operator|(
name|SolrServerException
operator|)
name|t
operator|)
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|individualShardInfo
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StringWriter
name|trace
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|trace
argument_list|)
argument_list|)
expr_stmt|;
name|individualShardInfo
operator|.
name|add
argument_list|(
literal|"trace"
argument_list|,
name|trace
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// summary for successful shard response is added down below
block|}
if|if
condition|(
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|individualShardInfo
operator|.
name|add
argument_list|(
literal|"time"
argument_list|,
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getElapsedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|srsp
operator|.
name|getShardAddress
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|individualShardInfo
operator|.
name|add
argument_list|(
literal|"shardAddress"
argument_list|,
name|srsp
operator|.
name|getShardAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|shardInfo
operator|.
name|add
argument_list|(
name|srsp
operator|.
name|getShard
argument_list|()
argument_list|,
name|individualShardInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|false
argument_list|)
operator|&&
name|srsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
continue|continue;
comment|// continue if there was an error and we're tolerant.
block|}
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|secondPhaseResult
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|>
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"secondPhase"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|result
init|=
name|serializer
operator|.
name|transformToNative
argument_list|(
name|secondPhaseResult
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|srsp
operator|.
name|getShard
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numFound
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|commandTopGroups
operator|.
name|keySet
argument_list|()
control|)
block|{
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
name|result
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|topGroups
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|individualShardInfo
operator|!=
literal|null
condition|)
block|{
comment|// keep track of this when shards.info=true
name|numFound
operator|+=
name|topGroups
operator|.
name|totalHitCount
expr_stmt|;
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|maxScore
argument_list|)
operator|||
name|topGroups
operator|.
name|maxScore
operator|>
name|maxScore
condition|)
name|maxScore
operator|=
name|topGroups
operator|.
name|maxScore
expr_stmt|;
block|}
name|commandTopGroups
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|add
argument_list|(
name|topGroups
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|query
range|:
name|queries
control|)
block|{
name|QueryCommandResult
name|queryCommandResult
init|=
operator|(
name|QueryCommandResult
operator|)
name|result
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|individualShardInfo
operator|!=
literal|null
condition|)
block|{
comment|// keep track of this when shards.info=true
name|numFound
operator|+=
name|queryCommandResult
operator|.
name|getMatches
argument_list|()
expr_stmt|;
name|float
name|thisMax
init|=
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|maxScore
argument_list|)
operator|||
name|thisMax
operator|>
name|maxScore
condition|)
name|maxScore
operator|=
name|thisMax
expr_stmt|;
block|}
name|commandTopDocs
operator|.
name|get
argument_list|(
name|query
argument_list|)
operator|.
name|add
argument_list|(
name|queryCommandResult
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|individualShardInfo
operator|!=
literal|null
condition|)
block|{
comment|// when shards.info=true
name|individualShardInfo
operator|.
name|add
argument_list|(
literal|"numFound"
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
name|individualShardInfo
operator|.
name|add
argument_list|(
literal|"maxScore"
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
for|for
control|(
name|String
name|groupField
range|:
name|commandTopGroups
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|topGroups
init|=
name|commandTopGroups
operator|.
name|get
argument_list|(
name|groupField
argument_list|)
decl_stmt|;
if|if
condition|(
name|topGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
index|[]
name|topGroupsArr
init|=
operator|new
name|TopGroups
index|[
name|topGroups
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|rb
operator|.
name|mergedTopGroups
operator|.
name|put
argument_list|(
name|groupField
argument_list|,
name|TopGroups
operator|.
name|merge
argument_list|(
name|topGroups
operator|.
name|toArray
argument_list|(
name|topGroupsArr
argument_list|)
argument_list|,
name|groupSort
argument_list|,
name|sortWithinGroup
argument_list|,
name|groupOffsetDefault
argument_list|,
name|docsPerGroupDefault
argument_list|,
name|TopGroups
operator|.
name|ScoreMergeMode
operator|.
name|None
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|query
range|:
name|commandTopDocs
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|QueryCommandResult
argument_list|>
name|queryCommandResults
init|=
name|commandTopDocs
operator|.
name|get
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TopDocs
argument_list|>
name|topDocs
init|=
operator|new
name|ArrayList
argument_list|<
name|TopDocs
argument_list|>
argument_list|(
name|queryCommandResults
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|mergedMatches
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QueryCommandResult
name|queryCommandResult
range|:
name|queryCommandResults
control|)
block|{
name|topDocs
operator|.
name|add
argument_list|(
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
argument_list|)
expr_stmt|;
name|mergedMatches
operator|+=
name|queryCommandResult
operator|.
name|getMatches
argument_list|()
expr_stmt|;
block|}
name|int
name|topN
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getOffset
argument_list|()
operator|+
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getLimit
argument_list|()
decl_stmt|;
name|TopDocs
name|mergedTopDocs
init|=
name|TopDocs
operator|.
name|merge
argument_list|(
name|sortWithinGroup
argument_list|,
name|topN
argument_list|,
name|topDocs
operator|.
name|toArray
argument_list|(
operator|new
name|TopDocs
index|[
name|topDocs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|rb
operator|.
name|mergedQueryCommandResults
operator|.
name|put
argument_list|(
name|query
argument_list|,
operator|new
name|QueryCommandResult
argument_list|(
name|mergedTopDocs
argument_list|,
name|mergedMatches
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
name|resultIds
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
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
name|ShardDoc
name|solrDoc
init|=
operator|(
name|ShardDoc
operator|)
name|scoreDoc
decl_stmt|;
name|solrDoc
operator|.
name|positionInResponse
operator|=
name|i
operator|++
expr_stmt|;
name|resultIds
operator|.
name|put
argument_list|(
name|solrDoc
operator|.
name|id
argument_list|,
name|solrDoc
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|queryCommandResult
operator|.
name|getTopDocs
argument_list|()
operator|.
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
name|solrDoc
operator|.
name|positionInResponse
operator|=
name|i
operator|++
expr_stmt|;
name|resultIds
operator|.
name|put
argument_list|(
name|solrDoc
operator|.
name|id
argument_list|,
name|solrDoc
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|resultIds
operator|=
name|resultIds
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
