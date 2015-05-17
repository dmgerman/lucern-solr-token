begin_unit
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
name|List
import|;
end_import
begin_import
import|import
name|net
operator|.
name|agkn
operator|.
name|hll
operator|.
name|HLL
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
name|MultiDocValues
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
name|index
operator|.
name|SortedSetDocValues
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
name|DocIdSetIterator
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
name|FixedBitSet
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
name|LongValues
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
name|Hash
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
begin_class
DECL|class|UniqueSlotAcc
specifier|abstract
class|class
name|UniqueSlotAcc
extends|extends
name|SlotAcc
block|{
DECL|field|factory
name|HLLAgg
operator|.
name|HLLFactory
name|factory
decl_stmt|;
DECL|field|field
name|SchemaField
name|field
decl_stmt|;
DECL|field|arr
name|FixedBitSet
index|[]
name|arr
decl_stmt|;
DECL|field|currentDocBase
name|int
name|currentDocBase
decl_stmt|;
DECL|field|counts
name|int
index|[]
name|counts
decl_stmt|;
comment|// populated with the cardinality once
DECL|field|nTerms
name|int
name|nTerms
decl_stmt|;
DECL|method|UniqueSlotAcc
specifier|public
name|UniqueSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|HLLAgg
operator|.
name|HLLFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|arr
operator|=
operator|new
name|FixedBitSet
index|[
name|numSlots
index|]
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|counts
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|FixedBitSet
name|bits
range|:
name|arr
control|)
block|{
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
continue|continue;
name|bits
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|()
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
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|currentDocBase
operator|=
name|readerContext
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fcontext
operator|.
name|isShard
argument_list|()
condition|)
block|{
return|return
name|getShardValue
argument_list|(
name|slot
argument_list|)
return|;
block|}
if|if
condition|(
name|counts
operator|!=
literal|null
condition|)
block|{
comment|// will only be pre-populated if this was used for sorting.
return|return
name|counts
index|[
name|slot
index|]
return|;
block|}
name|FixedBitSet
name|bs
init|=
name|arr
index|[
name|slot
index|]
decl_stmt|;
return|return
name|bs
operator|==
literal|null
condition|?
literal|0
else|:
name|bs
operator|.
name|cardinality
argument_list|()
return|;
block|}
DECL|method|getShardHLL
specifier|private
name|Object
name|getShardHLL
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|ords
init|=
name|arr
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|ords
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// TODO: when we get to refinements, may be useful to return something???
name|HLL
name|hll
init|=
name|factory
operator|.
name|getHLL
argument_list|()
decl_stmt|;
name|long
name|maxOrd
init|=
name|ords
operator|.
name|length
argument_list|()
decl_stmt|;
name|Hash
operator|.
name|LongPair
name|hashResult
init|=
operator|new
name|Hash
operator|.
name|LongPair
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
operator|-
literal|1
init|;
operator|++
name|ord
operator|<
name|maxOrd
condition|;
control|)
block|{
name|ord
operator|=
name|ords
operator|.
name|nextSetBit
argument_list|(
name|ord
argument_list|)
expr_stmt|;
if|if
condition|(
name|ord
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
name|BytesRef
name|val
init|=
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
comment|// way to avoid recomputing hash across slots?  Prob not worth space
name|Hash
operator|.
name|murmurhash3_x64_128
argument_list|(
name|val
operator|.
name|bytes
argument_list|,
name|val
operator|.
name|offset
argument_list|,
name|val
operator|.
name|length
argument_list|,
literal|0
argument_list|,
name|hashResult
argument_list|)
expr_stmt|;
comment|// idea: if the set is small enough, just send the hashes?  We can add at the top
comment|// level or even just do a hash table at the top level.
name|hll
operator|.
name|addRaw
argument_list|(
name|hashResult
operator|.
name|val1
argument_list|)
expr_stmt|;
block|}
name|SimpleOrderedMap
name|map
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"hll"
argument_list|,
name|hll
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
DECL|method|getShardValue
specifier|private
name|Object
name|getShardValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
return|return
name|getShardHLL
argument_list|(
name|slot
argument_list|)
return|;
name|FixedBitSet
name|ords
init|=
name|arr
index|[
name|slot
index|]
decl_stmt|;
name|int
name|unique
decl_stmt|;
if|if
condition|(
name|counts
operator|!=
literal|null
condition|)
block|{
name|unique
operator|=
name|counts
index|[
name|slot
index|]
expr_stmt|;
block|}
else|else
block|{
name|unique
operator|=
name|ords
operator|==
literal|null
condition|?
literal|0
else|:
name|ords
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
name|SimpleOrderedMap
name|map
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"unique"
argument_list|,
name|unique
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"nTerms"
argument_list|,
name|nTerms
argument_list|)
expr_stmt|;
name|int
name|maxExplicit
init|=
literal|100
decl_stmt|;
comment|// TODO: make configurable
comment|// TODO: share values across buckets
if|if
condition|(
name|unique
operator|>
literal|0
condition|)
block|{
name|List
name|lst
init|=
operator|new
name|ArrayList
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|unique
argument_list|,
name|maxExplicit
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|maxOrd
init|=
name|ords
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|ords
operator|!=
literal|null
operator|&&
name|ords
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|lst
operator|.
name|size
argument_list|()
operator|<
name|maxExplicit
condition|;
control|)
block|{
name|ord
operator|=
name|ords
operator|.
name|nextSetBit
argument_list|(
name|ord
argument_list|)
expr_stmt|;
if|if
condition|(
name|ord
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
name|BytesRef
name|val
init|=
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|field
operator|.
name|getType
argument_list|()
operator|.
name|toObject
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|ord
operator|>=
name|maxOrd
condition|)
break|break;
block|}
block|}
name|map
operator|.
name|add
argument_list|(
literal|"vals"
argument_list|,
name|lst
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|lookupOrd
specifier|protected
specifier|abstract
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// we only calculate all the counts when sorting by count
DECL|method|calcCounts
specifier|public
name|void
name|calcCounts
parameter_list|()
block|{
name|counts
operator|=
operator|new
name|int
index|[
name|arr
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FixedBitSet
name|bs
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
name|counts
index|[
name|i
index|]
operator|=
name|bs
operator|==
literal|null
condition|?
literal|0
else|:
name|bs
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
if|if
condition|(
name|counts
operator|==
literal|null
condition|)
block|{
comment|// TODO: a more efficient way to do this?  prepareSort?
name|calcCounts
argument_list|()
expr_stmt|;
block|}
return|return
name|counts
index|[
name|slotA
index|]
operator|-
name|counts
index|[
name|slotB
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|arr
operator|=
name|resizer
operator|.
name|resize
argument_list|(
name|arr
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|UniqueSinglevaluedSlotAcc
class|class
name|UniqueSinglevaluedSlotAcc
extends|extends
name|UniqueSlotAcc
block|{
DECL|field|topLevel
specifier|final
name|SortedDocValues
name|topLevel
decl_stmt|;
DECL|field|subDvs
specifier|final
name|SortedDocValues
index|[]
name|subDvs
decl_stmt|;
DECL|field|ordMap
specifier|final
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordMap
decl_stmt|;
DECL|field|toGlobal
name|LongValues
name|toGlobal
decl_stmt|;
DECL|field|subDv
name|SortedDocValues
name|subDv
decl_stmt|;
DECL|method|UniqueSinglevaluedSlotAcc
specifier|public
name|UniqueSinglevaluedSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|HLLAgg
operator|.
name|HLLFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|field
argument_list|,
name|numSlots
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|topLevel
operator|=
name|FieldUtil
operator|.
name|getSortedDocValues
argument_list|(
name|fcontext
operator|.
name|qcontext
argument_list|,
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nTerms
operator|=
name|topLevel
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|topLevel
operator|instanceof
name|MultiDocValues
operator|.
name|MultiSortedDocValues
condition|)
block|{
name|ordMap
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedDocValues
operator|)
name|topLevel
operator|)
operator|.
name|mapping
expr_stmt|;
name|subDvs
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedDocValues
operator|)
name|topLevel
operator|)
operator|.
name|values
expr_stmt|;
block|}
else|else
block|{
name|ordMap
operator|=
literal|null
expr_stmt|;
name|subDvs
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|protected
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|topLevel
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|subDvs
operator|!=
literal|null
condition|)
block|{
name|subDv
operator|=
name|subDvs
index|[
name|readerContext
operator|.
name|ord
index|]
expr_stmt|;
name|toGlobal
operator|=
name|ordMap
operator|.
name|getGlobalOrds
argument_list|(
name|readerContext
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|readerContext
operator|.
name|ord
operator|==
literal|0
operator|||
name|topLevel
operator|.
name|getValueCount
argument_list|()
operator|==
literal|0
assert|;
name|subDv
operator|=
name|topLevel
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
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|int
name|segOrd
init|=
name|subDv
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|segOrd
operator|<
literal|0
condition|)
return|return;
comment|// -1 means missing
name|int
name|ord
init|=
name|toGlobal
operator|==
literal|null
condition|?
name|segOrd
else|:
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|segOrd
argument_list|)
decl_stmt|;
name|FixedBitSet
name|bits
init|=
name|arr
index|[
name|slotNum
index|]
decl_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|nTerms
argument_list|)
expr_stmt|;
name|arr
index|[
name|slotNum
index|]
operator|=
name|bits
expr_stmt|;
block|}
name|bits
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|UniqueMultiDvSlotAcc
class|class
name|UniqueMultiDvSlotAcc
extends|extends
name|UniqueSlotAcc
block|{
DECL|field|topLevel
specifier|final
name|SortedSetDocValues
name|topLevel
decl_stmt|;
DECL|field|subDvs
specifier|final
name|SortedSetDocValues
index|[]
name|subDvs
decl_stmt|;
DECL|field|ordMap
specifier|final
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordMap
decl_stmt|;
DECL|field|toGlobal
name|LongValues
name|toGlobal
decl_stmt|;
DECL|field|subDv
name|SortedSetDocValues
name|subDv
decl_stmt|;
DECL|method|UniqueMultiDvSlotAcc
specifier|public
name|UniqueMultiDvSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|HLLAgg
operator|.
name|HLLFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|field
argument_list|,
name|numSlots
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|topLevel
operator|=
name|FieldUtil
operator|.
name|getSortedSetDocValues
argument_list|(
name|fcontext
operator|.
name|qcontext
argument_list|,
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nTerms
operator|=
operator|(
name|int
operator|)
name|topLevel
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|topLevel
operator|instanceof
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
condition|)
block|{
name|ordMap
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
operator|)
name|topLevel
operator|)
operator|.
name|mapping
expr_stmt|;
name|subDvs
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
operator|)
name|topLevel
operator|)
operator|.
name|values
expr_stmt|;
block|}
else|else
block|{
name|ordMap
operator|=
literal|null
expr_stmt|;
name|subDvs
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|protected
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|topLevel
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|subDvs
operator|!=
literal|null
condition|)
block|{
name|subDv
operator|=
name|subDvs
index|[
name|readerContext
operator|.
name|ord
index|]
expr_stmt|;
name|toGlobal
operator|=
name|ordMap
operator|.
name|getGlobalOrds
argument_list|(
name|readerContext
operator|.
name|ord
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|readerContext
operator|.
name|ord
operator|==
literal|0
operator|||
name|topLevel
operator|.
name|getValueCount
argument_list|()
operator|==
literal|0
assert|;
name|subDv
operator|=
name|topLevel
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
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|subDv
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|segOrd
init|=
operator|(
name|int
operator|)
name|subDv
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|segOrd
operator|<
literal|0
condition|)
return|return;
name|FixedBitSet
name|bits
init|=
name|arr
index|[
name|slotNum
index|]
decl_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|nTerms
argument_list|)
expr_stmt|;
name|arr
index|[
name|slotNum
index|]
operator|=
name|bits
expr_stmt|;
block|}
do|do
block|{
name|int
name|ord
init|=
name|toGlobal
operator|==
literal|null
condition|?
name|segOrd
else|:
operator|(
name|int
operator|)
name|toGlobal
operator|.
name|get
argument_list|(
name|segOrd
argument_list|)
decl_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|segOrd
operator|=
operator|(
name|int
operator|)
name|subDv
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|segOrd
operator|>=
literal|0
condition|)
do|;
block|}
block|}
end_class
begin_class
DECL|class|UniqueMultivaluedSlotAcc
class|class
name|UniqueMultivaluedSlotAcc
extends|extends
name|UniqueSlotAcc
implements|implements
name|UnInvertedField
operator|.
name|Callback
block|{
DECL|field|uif
specifier|private
name|UnInvertedField
name|uif
decl_stmt|;
DECL|field|docToTerm
specifier|private
name|UnInvertedField
operator|.
name|DocToTerm
name|docToTerm
decl_stmt|;
DECL|method|UniqueMultivaluedSlotAcc
specifier|public
name|UniqueMultivaluedSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|HLLAgg
operator|.
name|HLLFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|field
argument_list|,
name|numSlots
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|uif
operator|=
name|UnInvertedField
operator|.
name|getUnInvertedField
argument_list|(
name|field
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|docToTerm
operator|=
name|uif
operator|.
expr|new
name|DocToTerm
argument_list|()
expr_stmt|;
name|fcontext
operator|.
name|qcontext
operator|.
name|addCloseHook
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// TODO: find way to close accumulators instead of using close hook?
name|nTerms
operator|=
name|uif
operator|.
name|numTerms
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|protected
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docToTerm
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
DECL|field|bits
specifier|private
name|FixedBitSet
name|bits
decl_stmt|;
comment|// bits for the current slot, only set for the callback
annotation|@
name|Override
DECL|method|call
specifier|public
name|void
name|call
parameter_list|(
name|int
name|termNum
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|termNum
argument_list|)
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
parameter_list|,
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
name|bits
operator|=
name|arr
index|[
name|slotNum
index|]
expr_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|nTerms
argument_list|)
expr_stmt|;
name|arr
index|[
name|slotNum
index|]
operator|=
name|bits
expr_stmt|;
block|}
name|docToTerm
operator|.
name|getTerms
argument_list|(
name|doc
operator|+
name|currentDocBase
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// this will call back to our Callback.call(int termNum)
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|docToTerm
operator|!=
literal|null
condition|)
block|{
name|docToTerm
operator|.
name|close
argument_list|()
expr_stmt|;
name|docToTerm
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
