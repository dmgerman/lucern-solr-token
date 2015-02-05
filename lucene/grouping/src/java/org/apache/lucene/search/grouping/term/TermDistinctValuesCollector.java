begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping.term
package|package
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
name|term
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
name|index
operator|.
name|LeafReaderContext
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
name|DocValues
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
name|SortedDocValues
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
name|AbstractDistinctValuesCollector
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
name|SentinelIntSet
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
begin_comment
comment|/**  * A term based implementation of {@link org.apache.lucene.search.grouping.AbstractDistinctValuesCollector} that relies  * on {@link SortedDocValues} to count the distinct values per group.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermDistinctValuesCollector
specifier|public
class|class
name|TermDistinctValuesCollector
extends|extends
name|AbstractDistinctValuesCollector
argument_list|<
name|TermDistinctValuesCollector
operator|.
name|GroupCount
argument_list|>
block|{
DECL|field|groupField
specifier|private
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|countField
specifier|private
specifier|final
name|String
name|countField
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|List
argument_list|<
name|GroupCount
argument_list|>
name|groups
decl_stmt|;
DECL|field|ordSet
specifier|private
specifier|final
name|SentinelIntSet
name|ordSet
decl_stmt|;
DECL|field|groupCounts
specifier|private
specifier|final
name|GroupCount
name|groupCounts
index|[]
decl_stmt|;
DECL|field|groupFieldTermIndex
specifier|private
name|SortedDocValues
name|groupFieldTermIndex
decl_stmt|;
DECL|field|countFieldTermIndex
specifier|private
name|SortedDocValues
name|countFieldTermIndex
decl_stmt|;
comment|/**    * Constructs {@link TermDistinctValuesCollector} instance.    *    * @param groupField The field to group by    * @param countField The field to count distinct values for    * @param groups The top N groups, collected during the first phase search    */
DECL|method|TermDistinctValuesCollector
specifier|public
name|TermDistinctValuesCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|String
name|countField
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|groups
parameter_list|)
block|{
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|countField
operator|=
name|countField
expr_stmt|;
name|this
operator|.
name|groups
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|group
range|:
name|groups
control|)
block|{
name|this
operator|.
name|groups
operator|.
name|add
argument_list|(
operator|new
name|GroupCount
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ordSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|groups
operator|.
name|size
argument_list|()
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|groupCounts
operator|=
operator|new
name|GroupCount
index|[
name|ordSet
operator|.
name|keys
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|slot
init|=
name|ordSet
operator|.
name|find
argument_list|(
name|groupFieldTermIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|<
literal|0
condition|)
block|{
return|return;
block|}
name|GroupCount
name|gc
init|=
name|groupCounts
index|[
name|slot
index|]
decl_stmt|;
name|int
name|countOrd
init|=
name|countFieldTermIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|doesNotContainOrd
argument_list|(
name|countOrd
argument_list|,
name|gc
operator|.
name|ords
argument_list|)
condition|)
block|{
if|if
condition|(
name|countOrd
operator|==
operator|-
literal|1
condition|)
block|{
name|gc
operator|.
name|uniqueValues
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesRef
name|term
init|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|countFieldTermIndex
operator|.
name|lookupOrd
argument_list|(
name|countOrd
argument_list|)
argument_list|)
decl_stmt|;
name|gc
operator|.
name|uniqueValues
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|gc
operator|.
name|ords
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|gc
operator|.
name|ords
argument_list|,
name|gc
operator|.
name|ords
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|gc
operator|.
name|ords
index|[
name|gc
operator|.
name|ords
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|countOrd
expr_stmt|;
if|if
condition|(
name|gc
operator|.
name|ords
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|gc
operator|.
name|ords
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doesNotContainOrd
specifier|private
name|boolean
name|doesNotContainOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|int
index|[]
name|ords
parameter_list|)
block|{
if|if
condition|(
name|ords
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|ords
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|ord
operator|!=
name|ords
index|[
literal|0
index|]
return|;
block|}
return|return
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|ords
argument_list|,
name|ord
argument_list|)
operator|<
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getGroups
specifier|public
name|List
argument_list|<
name|GroupCount
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|groupFieldTermIndex
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
name|countFieldTermIndex
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|countField
argument_list|)
expr_stmt|;
name|ordSet
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|GroupCount
name|group
range|:
name|groups
control|)
block|{
name|int
name|groupOrd
init|=
name|group
operator|.
name|groupValue
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|groupFieldTermIndex
operator|.
name|lookupTerm
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|groupValue
operator|!=
literal|null
operator|&&
name|groupOrd
operator|<
literal|0
condition|)
block|{
continue|continue;
block|}
name|groupCounts
index|[
name|ordSet
operator|.
name|put
argument_list|(
name|groupOrd
argument_list|)
index|]
operator|=
name|group
expr_stmt|;
name|group
operator|.
name|ords
operator|=
operator|new
name|int
index|[
name|group
operator|.
name|uniqueValues
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|group
operator|.
name|ords
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|value
range|:
name|group
operator|.
name|uniqueValues
control|)
block|{
name|int
name|countOrd
init|=
name|value
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|countFieldTermIndex
operator|.
name|lookupTerm
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|countOrd
operator|>=
literal|0
condition|)
block|{
name|group
operator|.
name|ords
index|[
name|i
operator|++
index|]
operator|=
name|countOrd
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Holds distinct values for a single group.    *    * @lucene.experimental */
DECL|class|GroupCount
specifier|public
specifier|static
class|class
name|GroupCount
extends|extends
name|AbstractDistinctValuesCollector
operator|.
name|GroupCount
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|ords
name|int
index|[]
name|ords
decl_stmt|;
DECL|method|GroupCount
name|GroupCount
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|)
block|{
name|super
argument_list|(
name|groupValue
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// TODO, maybe we don't?
block|}
block|}
end_class
end_unit
