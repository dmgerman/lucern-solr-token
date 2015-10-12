begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ReaderUtil
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
name|SortingMergePolicy
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
name|BitSet
import|;
end_import
begin_comment
comment|/**  * Helper class to sort readers that contain blocks of documents.  *<p>  * Note that this class is intended to used with {@link SortingMergePolicy},  * and for other purposes has some limitations:  *<ul>  *<li>Cannot yet be used with {@link IndexSearcher#searchAfter(ScoreDoc, Query, int, Sort) IndexSearcher.searchAfter}  *<li>Filling sort field values is not yet supported.  *</ul>  * @lucene.experimental  */
end_comment
begin_comment
comment|// TODO: can/should we clean this thing up (e.g. return a proper sort value)
end_comment
begin_comment
comment|// and move to the join/ module?
end_comment
begin_class
DECL|class|BlockJoinComparatorSource
specifier|public
class|class
name|BlockJoinComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|parentsFilter
specifier|final
name|Query
name|parentsFilter
decl_stmt|;
DECL|field|parentSort
specifier|final
name|Sort
name|parentSort
decl_stmt|;
DECL|field|childSort
specifier|final
name|Sort
name|childSort
decl_stmt|;
comment|/**    * Create a new BlockJoinComparatorSource, sorting only blocks of documents    * with {@code parentSort} and not reordering children with a block.    *    * @param parentsFilter Filter identifying parent documents    * @param parentSort Sort for parent documents    */
DECL|method|BlockJoinComparatorSource
specifier|public
name|BlockJoinComparatorSource
parameter_list|(
name|Query
name|parentsFilter
parameter_list|,
name|Sort
name|parentSort
parameter_list|)
block|{
name|this
argument_list|(
name|parentsFilter
argument_list|,
name|parentSort
argument_list|,
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new BlockJoinComparatorSource, specifying the sort order for both    * blocks of documents and children within a block.    *    * @param parentsFilter Filter identifying parent documents    * @param parentSort Sort for parent documents    * @param childSort Sort for child documents in the same block    */
DECL|method|BlockJoinComparatorSource
specifier|public
name|BlockJoinComparatorSource
parameter_list|(
name|Query
name|parentsFilter
parameter_list|,
name|Sort
name|parentSort
parameter_list|,
name|Sort
name|childSort
parameter_list|)
block|{
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|parentSort
operator|=
name|parentSort
expr_stmt|;
name|this
operator|.
name|childSort
operator|=
name|childSort
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|Integer
argument_list|>
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we keep parallel slots: the parent ids and the child ids
specifier|final
name|int
name|parentSlots
index|[]
init|=
operator|new
name|int
index|[
name|numHits
index|]
decl_stmt|;
specifier|final
name|int
name|childSlots
index|[]
init|=
operator|new
name|int
index|[
name|numHits
index|]
decl_stmt|;
name|SortField
name|parentFields
index|[]
init|=
name|parentSort
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|int
name|parentReverseMul
index|[]
init|=
operator|new
name|int
index|[
name|parentFields
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|parentComparators
index|[]
init|=
operator|new
name|FieldComparator
index|[
name|parentFields
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
name|parentFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|parentReverseMul
index|[
name|i
index|]
operator|=
name|parentFields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
name|parentComparators
index|[
name|i
index|]
operator|=
name|parentFields
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|SortField
name|childFields
index|[]
init|=
name|childSort
operator|.
name|getSort
argument_list|()
decl_stmt|;
specifier|final
name|int
name|childReverseMul
index|[]
init|=
operator|new
name|int
index|[
name|childFields
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|FieldComparator
argument_list|<
name|?
argument_list|>
name|childComparators
index|[]
init|=
operator|new
name|FieldComparator
index|[
name|childFields
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
name|childFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|childReverseMul
index|[
name|i
index|]
operator|=
name|childFields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|1
expr_stmt|;
name|childComparators
index|[
name|i
index|]
operator|=
name|childFields
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|(
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: we could return parent ID as value but really our sort "value" is more complex...
comment|// So we throw UOE for now. At the moment you really should only use this at indexing time.
return|return
operator|new
name|FieldComparator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|int
name|bottomParent
decl_stmt|;
name|int
name|bottomChild
decl_stmt|;
name|BitSet
name|parentBits
decl_stmt|;
name|LeafFieldComparator
index|[]
name|parentLeafComparators
decl_stmt|;
name|LeafFieldComparator
index|[]
name|childLeafComparators
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
try|try
block|{
return|return
name|compare
argument_list|(
name|childSlots
index|[
name|slot1
index|]
argument_list|,
name|parentSlots
index|[
name|slot1
index|]
argument_list|,
name|childSlots
index|[
name|slot2
index|]
argument_list|,
name|parentSlots
index|[
name|slot2
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTopValue
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
comment|// we dont have enough information (the docid is needed)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this comparator cannot be used with deep paging"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|LeafFieldComparator
name|getLeafComparator
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentBits
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This comparator can only be used on a single segment"
argument_list|)
throw|;
block|}
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|parentsFilter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|parents
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|parents
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"LeafReader "
operator|+
name|context
operator|.
name|reader
argument_list|()
operator|+
literal|" contains no parents!"
argument_list|)
throw|;
block|}
name|parentBits
operator|=
name|BitSet
operator|.
name|of
argument_list|(
name|parents
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|parentLeafComparators
operator|=
operator|new
name|LeafFieldComparator
index|[
name|parentComparators
operator|.
name|length
index|]
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
name|parentComparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|parentLeafComparators
index|[
name|i
index|]
operator|=
name|parentComparators
index|[
name|i
index|]
operator|.
name|getLeafComparator
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|childLeafComparators
operator|=
operator|new
name|LeafFieldComparator
index|[
name|childComparators
operator|.
name|length
index|]
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
name|childComparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|childLeafComparators
index|[
name|i
index|]
operator|=
name|childComparators
index|[
name|i
index|]
operator|.
name|getLeafComparator
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LeafFieldComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compare
argument_list|(
name|bottomChild
argument_list|,
name|bottomParent
argument_list|,
name|doc
argument_list|,
name|parent
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we dont have enough information (the docid is needed)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this comparator cannot be used with deep paging"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|childSlots
index|[
name|slot
index|]
operator|=
name|doc
expr_stmt|;
name|parentSlots
index|[
name|slot
index|]
operator|=
name|parent
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|bottomParent
operator|=
name|parentSlots
index|[
name|slot
index|]
expr_stmt|;
name|bottomChild
operator|=
name|childSlots
index|[
name|slot
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
for|for
control|(
name|LeafFieldComparator
name|comp
range|:
name|parentLeafComparators
control|)
block|{
name|comp
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LeafFieldComparator
name|comp
range|:
name|childLeafComparators
control|)
block|{
name|comp
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
comment|// really our sort "value" is more complex...
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"filling sort field values is not yet supported"
argument_list|)
throw|;
block|}
name|int
name|parent
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|parentBits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
return|;
block|}
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|parent1
parameter_list|,
name|int
name|docID2
parameter_list|,
name|int
name|parent2
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parent1
operator|==
name|parent2
condition|)
block|{
comment|// both are in the same block
if|if
condition|(
name|docID1
operator|==
name|parent1
operator|||
name|docID2
operator|==
name|parent2
condition|)
block|{
comment|// keep parents at the end of blocks
return|return
name|docID1
operator|-
name|docID2
return|;
block|}
else|else
block|{
return|return
name|compare
argument_list|(
name|docID1
argument_list|,
name|docID2
argument_list|,
name|childLeafComparators
argument_list|,
name|childReverseMul
argument_list|)
return|;
block|}
block|}
else|else
block|{
name|int
name|cmp
init|=
name|compare
argument_list|(
name|parent1
argument_list|,
name|parent2
argument_list|,
name|parentLeafComparators
argument_list|,
name|parentReverseMul
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
return|return
name|parent1
operator|-
name|parent2
return|;
block|}
else|else
block|{
return|return
name|cmp
return|;
block|}
block|}
block|}
name|int
name|compare
parameter_list|(
name|int
name|docID1
parameter_list|,
name|int
name|docID2
parameter_list|,
name|LeafFieldComparator
name|comparators
index|[]
parameter_list|,
name|int
name|reverseMul
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// TODO: would be better if copy() didnt cause a term lookup in TermOrdVal& co,
comment|// the segments are always the same here...
name|comparators
index|[
name|i
index|]
operator|.
name|copy
argument_list|(
literal|0
argument_list|,
name|docID1
argument_list|)
expr_stmt|;
name|comparators
index|[
name|i
index|]
operator|.
name|setBottom
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|comp
init|=
name|reverseMul
index|[
name|i
index|]
operator|*
name|comparators
index|[
name|i
index|]
operator|.
name|compareBottom
argument_list|(
name|docID2
argument_list|)
decl_stmt|;
if|if
condition|(
name|comp
operator|!=
literal|0
condition|)
block|{
return|return
name|comp
return|;
block|}
block|}
return|return
literal|0
return|;
comment|// no need to docid tiebreak
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"blockJoin(parentSort="
operator|+
name|parentSort
operator|+
literal|",childSort="
operator|+
name|childSort
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
