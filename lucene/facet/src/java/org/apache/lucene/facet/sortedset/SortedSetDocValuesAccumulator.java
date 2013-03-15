begin_unit
begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|search
operator|.
name|CountFacetRequest
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
name|facet
operator|.
name|search
operator|.
name|FacetArrays
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
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
name|facet
operator|.
name|search
operator|.
name|FacetResult
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
name|facet
operator|.
name|search
operator|.
name|FacetResultNode
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
name|facet
operator|.
name|search
operator|.
name|FacetsAccumulator
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
name|facet
operator|.
name|search
operator|.
name|FacetsAggregator
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
name|facet
operator|.
name|search
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
operator|.
name|MultiSortedSetDocValues
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
name|PriorityQueue
import|;
end_import
begin_comment
comment|/** A {@link FacetsAccumulator} that uses previously  *  indexed {@link SortedSetDocValuesFacetField} to perform faceting,  *  without require a separate taxonomy index.  Faceting is  *  a bit slower (~25%), and there is added cost on every  *  {@link IndexReader} open to create a new {@link  *  SortedSetDocValuesReaderState}.  Furthermore, this does  *  not support hierarchical facets; only flat (dimension +  *  label) facets, but it uses quite a bit less RAM to do so. */
end_comment
begin_class
DECL|class|SortedSetDocValuesAccumulator
specifier|public
class|class
name|SortedSetDocValuesAccumulator
extends|extends
name|FacetsAccumulator
block|{
DECL|field|state
specifier|final
name|SortedSetDocValuesReaderState
name|state
decl_stmt|;
DECL|field|dv
specifier|final
name|SortedSetDocValues
name|dv
decl_stmt|;
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|SortedSetDocValuesAccumulator
specifier|public
name|SortedSetDocValuesAccumulator
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|,
name|SortedSetDocValuesReaderState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fsp
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|FacetArrays
argument_list|(
operator|(
name|int
operator|)
name|state
operator|.
name|getDocValues
argument_list|()
operator|.
name|getValueCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|state
operator|.
name|getField
argument_list|()
expr_stmt|;
name|dv
operator|=
name|state
operator|.
name|getDocValues
argument_list|()
expr_stmt|;
comment|// Check params:
for|for
control|(
name|FacetRequest
name|request
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|request
operator|instanceof
name|CountFacetRequest
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this collector only supports CountFacetRequest; got "
operator|+
name|request
argument_list|)
throw|;
block|}
if|if
condition|(
name|request
operator|.
name|categoryPath
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this collector only supports depth 1 CategoryPath; got "
operator|+
name|request
operator|.
name|categoryPath
argument_list|)
throw|;
block|}
if|if
condition|(
name|request
operator|.
name|getDepth
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this collector only supports depth=1; got "
operator|+
name|request
operator|.
name|getDepth
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|dim
init|=
name|request
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
decl_stmt|;
name|SortedSetDocValuesReaderState
operator|.
name|OrdRange
name|ordRange
init|=
name|state
operator|.
name|getOrdRange
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordRange
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dim \""
operator|+
name|dim
operator|+
literal|"\" does not exist"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getAggregator
specifier|public
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
return|return
operator|new
name|FacetsAggregator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|aggregate
parameter_list|(
name|MatchingDocs
name|matchingDocs
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetDocValues
name|segValues
init|=
name|matchingDocs
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|segValues
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|int
index|[]
name|counts
init|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|matchingDocs
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
assert|assert
name|maxDoc
operator|==
name|matchingDocs
operator|.
name|bits
operator|.
name|length
argument_list|()
assert|;
if|if
condition|(
name|dv
operator|instanceof
name|MultiSortedSetDocValues
condition|)
block|{
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordinalMap
init|=
operator|(
operator|(
name|MultiSortedSetDocValues
operator|)
name|dv
operator|)
operator|.
name|mapping
decl_stmt|;
name|int
name|segOrd
init|=
name|matchingDocs
operator|.
name|context
operator|.
name|ord
decl_stmt|;
name|int
name|numSegOrds
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|matchingDocs
operator|.
name|totalHits
operator|<
name|numSegOrds
operator|/
literal|10
condition|)
block|{
comment|// Remap every ord to global ord as we iterate:
specifier|final
name|int
index|[]
name|segCounts
init|=
operator|new
name|int
index|[
name|numSegOrds
index|]
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|maxDoc
operator|&&
operator|(
name|doc
operator|=
name|matchingDocs
operator|.
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|segValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|term
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|counts
index|[
operator|(
name|int
operator|)
name|ordinalMap
operator|.
name|getGlobalOrd
argument_list|(
name|segOrd
argument_list|,
name|term
argument_list|)
index|]
operator|++
expr_stmt|;
name|term
operator|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// First count in seg-ord space:
specifier|final
name|int
index|[]
name|segCounts
init|=
operator|new
name|int
index|[
name|numSegOrds
index|]
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|maxDoc
operator|&&
operator|(
name|doc
operator|=
name|matchingDocs
operator|.
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|segValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|term
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|segCounts
index|[
name|term
index|]
operator|++
expr_stmt|;
name|term
operator|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
operator|++
name|doc
expr_stmt|;
block|}
comment|// Then, migrate to global ords:
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|numSegOrds
condition|;
name|ord
operator|++
control|)
block|{
name|int
name|count
init|=
name|segCounts
index|[
name|ord
index|]
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
name|counts
index|[
operator|(
name|int
operator|)
name|ordinalMap
operator|.
name|getGlobalOrd
argument_list|(
name|segOrd
argument_list|,
name|ord
argument_list|)
index|]
operator|+=
name|count
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// No ord mapping (e.g., single segment index):
comment|// just aggregate directly into counts:
name|int
name|doc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|maxDoc
operator|&&
operator|(
name|doc
operator|=
name|matchingDocs
operator|.
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|segValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|int
name|term
init|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|counts
index|[
name|term
index|]
operator|++
expr_stmt|;
name|term
operator|=
operator|(
name|int
operator|)
name|segValues
operator|.
name|nextOrd
argument_list|()
expr_stmt|;
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollupValues
parameter_list|(
name|FacetRequest
name|fr
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
comment|// Nothing to do here: we only support flat (dim +
comment|// label) facets, and in accumulate we sum up the
comment|// count for the dimension.
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|requiresDocScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
comment|/** Keeps highest count results. */
DECL|class|TopCountPQ
specifier|static
class|class
name|TopCountPQ
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|TopCountPQ
specifier|public
name|TopCountPQ
parameter_list|(
name|int
name|topN
parameter_list|)
block|{
name|super
argument_list|(
name|topN
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|a
parameter_list|,
name|FacetResultNode
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|<
name|b
operator|.
name|value
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|value
operator|>
name|b
operator|.
name|value
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|ordinal
operator|>
name|b
operator|.
name|ordinal
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|accumulate
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|accumulate
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetsAggregator
name|aggregator
init|=
name|getAggregator
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|getCategoryLists
argument_list|()
control|)
block|{
for|for
control|(
name|MatchingDocs
name|md
range|:
name|matchingDocs
control|)
block|{
name|aggregator
operator|.
name|aggregate
argument_list|(
name|md
argument_list|,
name|clp
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
block|}
comment|// compute top-K
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
name|int
index|[]
name|counts
init|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|request
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|String
name|dim
init|=
name|request
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
decl_stmt|;
name|SortedSetDocValuesReaderState
operator|.
name|OrdRange
name|ordRange
init|=
name|state
operator|.
name|getOrdRange
argument_list|(
name|dim
argument_list|)
decl_stmt|;
comment|// checked in ctor:
assert|assert
name|ordRange
operator|!=
literal|null
assert|;
if|if
condition|(
name|request
operator|.
name|numResults
operator|>=
name|ordRange
operator|.
name|end
operator|-
name|ordRange
operator|.
name|start
operator|+
literal|1
condition|)
block|{
comment|// specialize this case, user is interested in all available results
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|dimCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
name|ordRange
operator|.
name|start
init|;
name|ord
operator|<=
name|ordRange
operator|.
name|end
condition|;
name|ord
operator|++
control|)
block|{
comment|//System.out.println("  ord=" + ord + " count= "+ counts[ord] + " bottomCount=" + bottomCount);
if|if
condition|(
name|counts
index|[
name|ord
index|]
operator|!=
literal|0
condition|)
block|{
name|dimCount
operator|+=
name|counts
index|[
name|ord
index|]
expr_stmt|;
name|FacetResultNode
name|node
init|=
operator|new
name|FacetResultNode
argument_list|(
name|ord
argument_list|,
name|counts
index|[
name|ord
index|]
argument_list|)
decl_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|node
operator|.
name|label
operator|=
operator|new
name|CategoryPath
argument_list|(
name|scratch
operator|.
name|utf8ToString
argument_list|()
operator|.
name|split
argument_list|(
name|state
operator|.
name|separatorRegex
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResultNode
name|o1
parameter_list|,
name|FacetResultNode
name|o2
parameter_list|)
block|{
comment|// First by highest count
name|int
name|value
init|=
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|value
operator|-
name|o1
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
comment|// ... then by lowest ord:
name|value
operator|=
name|o1
operator|.
name|ordinal
operator|-
name|o2
operator|.
name|ordinal
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|CategoryListParams
operator|.
name|OrdinalPolicy
name|op
init|=
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|request
operator|.
name|categoryPath
argument_list|)
operator|.
name|getOrdinalPolicy
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
name|CategoryListParams
operator|.
name|OrdinalPolicy
operator|.
name|ALL_BUT_DIMENSION
condition|)
block|{
name|dimCount
operator|=
literal|0
expr_stmt|;
block|}
name|FacetResultNode
name|rootNode
init|=
operator|new
name|FacetResultNode
argument_list|(
operator|-
literal|1
argument_list|,
name|dimCount
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|label
operator|=
operator|new
name|CategoryPath
argument_list|(
operator|new
name|String
index|[]
block|{
name|dim
block|}
argument_list|)
expr_stmt|;
name|rootNode
operator|.
name|subResults
operator|=
name|nodes
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|FacetResult
argument_list|(
name|request
argument_list|,
name|rootNode
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|TopCountPQ
name|q
init|=
operator|new
name|TopCountPQ
argument_list|(
name|request
operator|.
name|numResults
argument_list|)
decl_stmt|;
name|int
name|bottomCount
init|=
literal|0
decl_stmt|;
comment|//System.out.println("collect");
name|int
name|dimCount
init|=
literal|0
decl_stmt|;
name|FacetResultNode
name|reuse
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
name|ordRange
operator|.
name|start
init|;
name|ord
operator|<=
name|ordRange
operator|.
name|end
condition|;
name|ord
operator|++
control|)
block|{
comment|//System.out.println("  ord=" + ord + " count= "+ counts[ord] + " bottomCount=" + bottomCount);
if|if
condition|(
name|counts
index|[
name|ord
index|]
operator|>
name|bottomCount
condition|)
block|{
name|dimCount
operator|+=
name|counts
index|[
name|ord
index|]
expr_stmt|;
comment|//System.out.println("    keep");
if|if
condition|(
name|reuse
operator|==
literal|null
condition|)
block|{
name|reuse
operator|=
operator|new
name|FacetResultNode
argument_list|(
name|ord
argument_list|,
name|counts
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reuse
operator|.
name|ordinal
operator|=
name|ord
expr_stmt|;
name|reuse
operator|.
name|value
operator|=
name|counts
index|[
name|ord
index|]
expr_stmt|;
block|}
name|reuse
operator|=
name|q
operator|.
name|insertWithOverflow
argument_list|(
name|reuse
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|.
name|size
argument_list|()
operator|==
name|request
operator|.
name|numResults
condition|)
block|{
name|bottomCount
operator|=
operator|(
name|int
operator|)
name|q
operator|.
name|top
argument_list|()
operator|.
name|value
expr_stmt|;
comment|//System.out.println("    new bottom=" + bottomCount);
block|}
block|}
block|}
name|CategoryListParams
operator|.
name|OrdinalPolicy
name|op
init|=
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|request
operator|.
name|categoryPath
argument_list|)
operator|.
name|getOrdinalPolicy
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
name|CategoryListParams
operator|.
name|OrdinalPolicy
operator|.
name|ALL_BUT_DIMENSION
condition|)
block|{
name|dimCount
operator|=
literal|0
expr_stmt|;
block|}
name|FacetResultNode
name|rootNode
init|=
operator|new
name|FacetResultNode
argument_list|(
operator|-
literal|1
argument_list|,
name|dimCount
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|label
operator|=
operator|new
name|CategoryPath
argument_list|(
operator|new
name|String
index|[]
block|{
name|dim
block|}
argument_list|)
expr_stmt|;
name|FacetResultNode
index|[]
name|childNodes
init|=
operator|new
name|FacetResultNode
index|[
name|q
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|childNodes
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|childNodes
index|[
name|i
index|]
operator|=
name|q
operator|.
name|pop
argument_list|()
expr_stmt|;
name|dv
operator|.
name|lookupOrd
argument_list|(
name|childNodes
index|[
name|i
index|]
operator|.
name|ordinal
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|childNodes
index|[
name|i
index|]
operator|.
name|label
operator|=
operator|new
name|CategoryPath
argument_list|(
name|scratch
operator|.
name|utf8ToString
argument_list|()
operator|.
name|split
argument_list|(
name|state
operator|.
name|separatorRegex
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rootNode
operator|.
name|subResults
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|childNodes
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
operator|new
name|FacetResult
argument_list|(
name|request
argument_list|,
name|rootNode
argument_list|,
name|childNodes
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
block|}
end_class
end_unit
