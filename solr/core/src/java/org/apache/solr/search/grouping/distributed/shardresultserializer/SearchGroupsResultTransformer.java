begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.shardresultserializer
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
name|shardresultserializer
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
name|grouping
operator|.
name|SearchGroup
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
name|lucene
operator|.
name|util
operator|.
name|CharsRef
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
name|UnicodeUtil
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
name|schema
operator|.
name|FieldType
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
name|SolrIndexSearcher
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
name|Command
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
name|SearchGroupsFieldCommand
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
name|SearchGroupsFieldCommandResult
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
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Implementation for transforming {@link SearchGroup} into a {@link NamedList} structure and visa versa.  */
end_comment
begin_class
DECL|class|SearchGroupsResultTransformer
specifier|public
class|class
name|SearchGroupsResultTransformer
implements|implements
name|ShardResultTransformer
argument_list|<
name|List
argument_list|<
name|Command
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SearchGroupsFieldCommandResult
argument_list|>
argument_list|>
block|{
DECL|field|TOP_GROUPS
specifier|private
specifier|static
specifier|final
name|String
name|TOP_GROUPS
init|=
literal|"topGroups"
decl_stmt|;
DECL|field|GROUP_COUNT
specifier|private
specifier|static
specifier|final
name|String
name|GROUP_COUNT
init|=
literal|"groupCount"
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|method|SearchGroupsResultTransformer
specifier|public
name|SearchGroupsResultTransformer
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|transform
specifier|public
name|NamedList
name|transform
parameter_list|(
name|List
argument_list|<
name|Command
argument_list|>
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|result
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|data
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Command
name|command
range|:
name|data
control|)
block|{
specifier|final
name|NamedList
argument_list|<
name|Object
argument_list|>
name|commandResult
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|SearchGroupsFieldCommand
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|SearchGroupsFieldCommand
name|fieldCommand
init|=
operator|(
name|SearchGroupsFieldCommand
operator|)
name|command
decl_stmt|;
specifier|final
name|SearchGroupsFieldCommandResult
name|fieldCommandResult
init|=
name|fieldCommand
operator|.
name|result
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|searchGroups
init|=
name|fieldCommandResult
operator|.
name|getSearchGroups
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchGroups
operator|!=
literal|null
condition|)
block|{
name|commandResult
operator|.
name|add
argument_list|(
name|TOP_GROUPS
argument_list|,
name|serializeSearchGroup
argument_list|(
name|searchGroups
argument_list|,
name|fieldCommand
operator|.
name|getGroupSort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Integer
name|groupedCount
init|=
name|fieldCommandResult
operator|.
name|getGroupCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupedCount
operator|!=
literal|null
condition|)
block|{
name|commandResult
operator|.
name|add
argument_list|(
name|GROUP_COUNT
argument_list|,
name|groupedCount
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
continue|continue;
block|}
name|result
operator|.
name|add
argument_list|(
name|command
operator|.
name|getKey
argument_list|()
argument_list|,
name|commandResult
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|transformToNative
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SearchGroupsFieldCommandResult
argument_list|>
name|transformToNative
parameter_list|(
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|shardResponse
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SearchGroupsFieldCommandResult
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|shardResponse
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|>
name|command
range|:
name|shardResponse
control|)
block|{
name|List
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|searchGroups
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|NamedList
name|topGroupsAndGroupCount
init|=
name|command
operator|.
name|getValue
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|NamedList
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|rawSearchGroups
init|=
operator|(
name|NamedList
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
operator|)
name|topGroupsAndGroupCount
operator|.
name|get
argument_list|(
name|TOP_GROUPS
argument_list|)
decl_stmt|;
if|if
condition|(
name|rawSearchGroups
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|rawSearchGroup
range|:
name|rawSearchGroups
control|)
block|{
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|searchGroup
init|=
operator|new
name|SearchGroup
argument_list|<>
argument_list|()
decl_stmt|;
name|searchGroup
operator|.
name|groupValue
operator|=
name|rawSearchGroup
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|?
operator|new
name|BytesRef
argument_list|(
name|rawSearchGroup
operator|.
name|getKey
argument_list|()
argument_list|)
else|:
literal|null
expr_stmt|;
name|searchGroup
operator|.
name|sortValues
operator|=
name|rawSearchGroup
operator|.
name|getValue
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Comparable
index|[
name|rawSearchGroup
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchGroup
operator|.
name|sortValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|field
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
operator|!=
literal|null
condition|?
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|FieldType
name|fieldType
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchGroup
operator|.
name|sortValues
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|searchGroup
operator|.
name|sortValues
index|[
name|i
index|]
operator|=
name|fieldType
operator|.
name|unmarshalSortValue
argument_list|(
name|searchGroup
operator|.
name|sortValues
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|searchGroups
operator|.
name|add
argument_list|(
name|searchGroup
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Integer
name|groupCount
init|=
operator|(
name|Integer
operator|)
name|topGroupsAndGroupCount
operator|.
name|get
argument_list|(
name|GROUP_COUNT
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|command
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|SearchGroupsFieldCommandResult
argument_list|(
name|groupCount
argument_list|,
name|searchGroups
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|serializeSearchGroup
specifier|private
name|NamedList
name|serializeSearchGroup
parameter_list|(
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|data
parameter_list|,
name|Sort
name|groupSort
parameter_list|)
block|{
specifier|final
name|NamedList
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|data
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|searchGroup
range|:
name|data
control|)
block|{
name|Object
index|[]
name|convertedSortValues
init|=
operator|new
name|Object
index|[
name|searchGroup
operator|.
name|sortValues
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchGroup
operator|.
name|sortValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|sortValue
init|=
name|searchGroup
operator|.
name|sortValues
index|[
name|i
index|]
decl_stmt|;
name|SchemaField
name|field
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
operator|!=
literal|null
condition|?
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|FieldType
name|fieldType
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortValue
operator|!=
literal|null
condition|)
block|{
name|sortValue
operator|=
name|fieldType
operator|.
name|marshalSortValue
argument_list|(
name|sortValue
argument_list|)
expr_stmt|;
block|}
block|}
name|convertedSortValues
index|[
name|i
index|]
operator|=
name|sortValue
expr_stmt|;
block|}
name|String
name|groupValue
init|=
name|searchGroup
operator|.
name|groupValue
operator|!=
literal|null
condition|?
name|searchGroup
operator|.
name|groupValue
operator|.
name|utf8ToString
argument_list|()
else|:
literal|null
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|groupValue
argument_list|,
name|convertedSortValues
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
