begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping
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
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|Collector
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
name|FieldCache
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
name|Scorer
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
name|TopDocsCollector
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
name|TopFieldCollector
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
name|TopScoreDocCollector
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
begin_comment
comment|/**  * See {@link FirstPassGroupingCollector}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SecondPassGroupingCollector
specifier|public
class|class
name|SecondPassGroupingCollector
extends|extends
name|Collector
block|{
DECL|field|groupMap
specifier|private
specifier|final
name|HashMap
argument_list|<
name|BytesRef
argument_list|,
name|SearchGroupDocs
argument_list|>
name|groupMap
decl_stmt|;
DECL|field|index
specifier|private
name|FieldCache
operator|.
name|DocTermsIndex
name|index
decl_stmt|;
DECL|field|groupField
specifier|private
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|maxDocsPerGroup
specifier|private
specifier|final
name|int
name|maxDocsPerGroup
decl_stmt|;
DECL|field|ordSet
specifier|private
specifier|final
name|SentinelIntSet
name|ordSet
decl_stmt|;
DECL|field|groupDocs
specifier|private
specifier|final
name|SearchGroupDocs
index|[]
name|groupDocs
decl_stmt|;
DECL|field|spareBytesRef
specifier|private
specifier|final
name|BytesRef
name|spareBytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|Collection
argument_list|<
name|SearchGroup
argument_list|>
name|groups
decl_stmt|;
DECL|field|withinGroupSort
specifier|private
specifier|final
name|Sort
name|withinGroupSort
decl_stmt|;
DECL|field|groupSort
specifier|private
specifier|final
name|Sort
name|groupSort
decl_stmt|;
DECL|field|totalHitCount
specifier|private
name|int
name|totalHitCount
decl_stmt|;
DECL|field|totalGroupedHitCount
specifier|private
name|int
name|totalGroupedHitCount
decl_stmt|;
DECL|method|SecondPassGroupingCollector
specifier|public
name|SecondPassGroupingCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|>
name|groups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("SP init");
if|if
condition|(
name|groups
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no groups to collect (groups.size() is 0)"
argument_list|)
throw|;
block|}
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
name|this
operator|.
name|withinGroupSort
operator|=
name|withinGroupSort
expr_stmt|;
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|maxDocsPerGroup
operator|=
name|maxDocsPerGroup
expr_stmt|;
name|groupMap
operator|=
operator|new
name|HashMap
argument_list|<
name|BytesRef
argument_list|,
name|SearchGroupDocs
argument_list|>
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
name|group
range|:
name|groups
control|)
block|{
comment|//System.out.println("  prep group=" + (group.groupValue == null ? "null" : group.groupValue.utf8ToString()));
specifier|final
name|TopDocsCollector
name|collector
decl_stmt|;
if|if
condition|(
name|withinGroupSort
operator|==
literal|null
condition|)
block|{
comment|// Sort by score
name|collector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|maxDocsPerGroup
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Sort by fields
name|collector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|fillSortFields
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|groupMap
operator|.
name|put
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
operator|new
name|SearchGroupDocs
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
name|collector
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ordSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|groupMap
operator|.
name|size
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|groupDocs
operator|=
operator|new
name|SearchGroupDocs
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
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|SearchGroupDocs
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|group
operator|.
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|int
name|slot
init|=
name|ordSet
operator|.
name|find
argument_list|(
name|index
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println("SP.collect doc=" + doc + " slot=" + slot);
name|totalHitCount
operator|++
expr_stmt|;
if|if
condition|(
name|slot
operator|>=
literal|0
condition|)
block|{
name|totalGroupedHitCount
operator|++
expr_stmt|;
name|groupDocs
index|[
name|slot
index|]
operator|.
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("SP.setNextReader");
for|for
control|(
name|SearchGroupDocs
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
name|group
operator|.
name|collector
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
name|index
operator|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTermsIndex
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
comment|// Rebuild ordSet
name|ordSet
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchGroupDocs
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
comment|//System.out.println("  group=" + (group.groupValue == null ? "null" : group.groupValue.utf8ToString()));
name|int
name|ord
init|=
name|group
operator|.
name|groupValue
operator|==
literal|null
condition|?
literal|0
else|:
name|index
operator|.
name|binarySearchLookup
argument_list|(
name|group
operator|.
name|groupValue
argument_list|,
name|spareBytesRef
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|groupDocs
index|[
name|ordSet
operator|.
name|put
argument_list|(
name|ord
argument_list|)
index|]
operator|=
name|group
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|getTopGroups
specifier|public
name|TopGroups
name|getTopGroups
parameter_list|(
name|int
name|withinGroupOffset
parameter_list|)
block|{
specifier|final
name|GroupDocs
index|[]
name|groupDocsResult
init|=
operator|new
name|GroupDocs
index|[
name|groups
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|groupIDX
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SearchGroup
name|group
range|:
name|groups
control|)
block|{
specifier|final
name|SearchGroupDocs
name|groupDocs
init|=
name|groupMap
operator|.
name|get
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
name|topDocs
init|=
name|groupDocs
operator|.
name|collector
operator|.
name|topDocs
argument_list|(
name|withinGroupOffset
argument_list|,
name|maxDocsPerGroup
argument_list|)
decl_stmt|;
name|groupDocsResult
index|[
name|groupIDX
operator|++
index|]
operator|=
operator|new
name|GroupDocs
argument_list|(
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|,
name|topDocs
operator|.
name|scoreDocs
argument_list|,
name|groupDocs
operator|.
name|groupValue
argument_list|,
name|group
operator|.
name|sortValues
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TopGroups
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|withinGroupSort
operator|==
literal|null
condition|?
literal|null
else|:
name|withinGroupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|totalHitCount
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|groupDocsResult
argument_list|)
return|;
block|}
block|}
end_class
begin_comment
comment|// TODO: merge with SearchGroup or not?
end_comment
begin_comment
comment|// ad: don't need to build a new hashmap
end_comment
begin_comment
comment|// disad: blows up the size of SearchGroup if we need many of them, and couples implementations
end_comment
begin_class
DECL|class|SearchGroupDocs
class|class
name|SearchGroupDocs
block|{
DECL|field|groupValue
specifier|public
specifier|final
name|BytesRef
name|groupValue
decl_stmt|;
DECL|field|collector
specifier|public
specifier|final
name|TopDocsCollector
name|collector
decl_stmt|;
DECL|method|SearchGroupDocs
specifier|public
name|SearchGroupDocs
parameter_list|(
name|BytesRef
name|groupValue
parameter_list|,
name|TopDocsCollector
name|collector
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
block|}
block|}
end_class
end_unit
