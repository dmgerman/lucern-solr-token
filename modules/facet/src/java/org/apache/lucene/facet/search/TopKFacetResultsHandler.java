begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
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
name|params
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
name|results
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
name|results
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
name|results
operator|.
name|MutableFacetResultNode
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
name|results
operator|.
name|IntermediateFacetResult
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
name|TaxonomyReader
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
name|TaxonomyReader
operator|.
name|ChildrenArrays
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
name|util
operator|.
name|ResultSortUtils
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**   * Generate Top-K results for a particular FacetRequest.  *<p>  * K is global (among all results) and is defined by {@link FacetRequest#getNumResults()}.  *<p>   * Note: Values of 0 (Zero) are ignored by this results handler.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|TopKFacetResultsHandler
specifier|public
class|class
name|TopKFacetResultsHandler
extends|extends
name|FacetResultsHandler
block|{
comment|/**    * Construct top-K results handler.      * @param taxonomyReader taxonomy reader    * @param facetRequest facet request being served    */
DECL|method|TopKFacetResultsHandler
specifier|public
name|TopKFacetResultsHandler
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetRequest
name|facetRequest
parameter_list|)
block|{
name|super
argument_list|(
name|taxonomyReader
argument_list|,
name|facetRequest
argument_list|)
expr_stmt|;
block|}
comment|// fetch top K for specific partition.
annotation|@
name|Override
DECL|method|fetchPartitionResult
specifier|public
name|IntermediateFacetResult
name|fetchPartitionResult
parameter_list|(
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|TopKFacetResult
name|res
init|=
literal|null
decl_stmt|;
name|int
name|ordinal
init|=
name|taxonomyReader
operator|.
name|getOrdinal
argument_list|(
name|facetRequest
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinal
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|double
name|value
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|isSelfPartition
argument_list|(
name|ordinal
argument_list|,
name|facetArrays
argument_list|,
name|offset
argument_list|)
condition|)
block|{
name|int
name|partitionSize
init|=
name|facetArrays
operator|.
name|getArraysLength
argument_list|()
decl_stmt|;
name|value
operator|=
name|facetRequest
operator|.
name|getValueOf
argument_list|(
name|facetArrays
argument_list|,
name|ordinal
operator|%
name|partitionSize
argument_list|)
expr_stmt|;
block|}
comment|// TODO (Facet): should initial value of "residue" depend on aggregator if not sum?
name|MutableFacetResultNode
name|parentResultNode
init|=
operator|new
name|MutableFacetResultNode
argument_list|(
name|ordinal
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|heap
init|=
name|ResultSortUtils
operator|.
name|createSuitableHeap
argument_list|(
name|facetRequest
argument_list|)
decl_stmt|;
name|int
name|totalFacets
init|=
name|heapDescendants
argument_list|(
name|ordinal
argument_list|,
name|heap
argument_list|,
name|parentResultNode
argument_list|,
name|facetArrays
argument_list|,
name|offset
argument_list|)
decl_stmt|;
name|res
operator|=
operator|new
name|TopKFacetResult
argument_list|(
name|facetRequest
argument_list|,
name|parentResultNode
argument_list|,
name|totalFacets
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeap
argument_list|(
name|heap
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|// merge given top K results into current
annotation|@
name|Override
DECL|method|mergeResults
specifier|public
name|IntermediateFacetResult
name|mergeResults
parameter_list|(
name|IntermediateFacetResult
modifier|...
name|tmpResults
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|taxonomyReader
operator|.
name|getOrdinal
argument_list|(
name|facetRequest
operator|.
name|getCategoryPath
argument_list|()
argument_list|)
decl_stmt|;
name|MutableFacetResultNode
name|resNode
init|=
operator|new
name|MutableFacetResultNode
argument_list|(
name|ordinal
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|totalFacets
init|=
literal|0
decl_stmt|;
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|heap
init|=
literal|null
decl_stmt|;
comment|// merge other results in queue
for|for
control|(
name|IntermediateFacetResult
name|tmpFres
range|:
name|tmpResults
control|)
block|{
comment|// cast should succeed
name|TopKFacetResult
name|fres
init|=
operator|(
name|TopKFacetResult
operator|)
name|tmpFres
decl_stmt|;
name|totalFacets
operator|+=
name|fres
operator|.
name|getNumValidDescendants
argument_list|()
expr_stmt|;
comment|// set the value for the result node representing the facet request
name|resNode
operator|.
name|increaseValue
argument_list|(
name|fres
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|tmpHeap
init|=
name|fres
operator|.
name|getHeap
argument_list|()
decl_stmt|;
if|if
condition|(
name|heap
operator|==
literal|null
condition|)
block|{
name|heap
operator|=
name|tmpHeap
expr_stmt|;
continue|continue;
block|}
comment|// bring sub results from heap of tmp res into result heap
for|for
control|(
name|int
name|i
init|=
name|tmpHeap
operator|.
name|size
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|FacetResultNode
name|a
init|=
name|heap
operator|.
name|insertWithOverflow
argument_list|(
name|tmpHeap
operator|.
name|pop
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|resNode
operator|.
name|increaseResidue
argument_list|(
name|a
operator|.
name|getResidue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|TopKFacetResult
name|res
init|=
operator|new
name|TopKFacetResult
argument_list|(
name|facetRequest
argument_list|,
name|resNode
argument_list|,
name|totalFacets
argument_list|)
decl_stmt|;
name|res
operator|.
name|setHeap
argument_list|(
name|heap
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * Finds the top K descendants of ordinal, which are at most facetRequest.getDepth()    * deeper than facetRequest.getCategoryPath (whose ordinal is input parameter ordinal).     * Candidates are restricted to current "counting list" and current "partition",    * they join the overall priority queue pq of size K.      * @return total number of descendants considered here by pq, excluding ordinal itself.    */
DECL|method|heapDescendants
specifier|private
name|int
name|heapDescendants
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|pq
parameter_list|,
name|MutableFacetResultNode
name|parentResultNode
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|partitionSize
init|=
name|facetArrays
operator|.
name|getArraysLength
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|offset
operator|+
name|partitionSize
decl_stmt|;
name|ChildrenArrays
name|childrenArray
init|=
name|taxonomyReader
operator|.
name|getChildrenArrays
argument_list|()
decl_stmt|;
name|int
index|[]
name|youngestChild
init|=
name|childrenArray
operator|.
name|getYoungestChildArray
argument_list|()
decl_stmt|;
name|int
index|[]
name|olderSibling
init|=
name|childrenArray
operator|.
name|getOlderSiblingArray
argument_list|()
decl_stmt|;
name|FacetResultNode
name|reusable
init|=
literal|null
decl_stmt|;
name|int
name|localDepth
init|=
literal|0
decl_stmt|;
name|int
name|depth
init|=
name|facetRequest
operator|.
name|getDepth
argument_list|()
decl_stmt|;
name|int
index|[]
name|ordinalStack
init|=
operator|new
name|int
index|[
literal|2
operator|+
name|Math
operator|.
name|min
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|,
name|depth
argument_list|)
index|]
decl_stmt|;
name|int
name|childrenCounter
init|=
literal|0
decl_stmt|;
name|int
name|tosOrdinal
decl_stmt|;
comment|// top of stack element
name|int
name|yc
init|=
name|youngestChild
index|[
name|ordinal
index|]
decl_stmt|;
while|while
condition|(
name|yc
operator|>=
name|endOffset
condition|)
block|{
name|yc
operator|=
name|olderSibling
index|[
name|yc
index|]
expr_stmt|;
block|}
comment|// make use of the fact that TaxonomyReader.INVALID_ORDINAL == -1,< endOffset
comment|// and it, too, can stop the loop.
name|ordinalStack
index|[
operator|++
name|localDepth
index|]
operator|=
name|yc
expr_stmt|;
comment|/*      * stack holds input parameter ordinal in position 0.      * Other elements are< endoffset.      * Only top of stack can be TaxonomyReader.INVALID_ORDINAL, and this if and only if      * the element below it exhausted all its children: has them all processed.      *       * stack elements are processed (counted and accumulated) only if they       * belong to current partition (between offset and endoffset) and first time      * they are on top of stack       *       * loop as long as stack is not empty of elements other than input ordinal, or for a little while -- it sibling      */
while|while
condition|(
name|localDepth
operator|>
literal|0
condition|)
block|{
name|tosOrdinal
operator|=
name|ordinalStack
index|[
name|localDepth
index|]
expr_stmt|;
if|if
condition|(
name|tosOrdinal
operator|==
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
comment|// element below tos has all its children, and itself, all processed
comment|// need to proceed to its sibling
name|localDepth
operator|--
expr_stmt|;
comment|// change element now on top of stack to its sibling.
name|ordinalStack
index|[
name|localDepth
index|]
operator|=
name|olderSibling
index|[
name|ordinalStack
index|[
name|localDepth
index|]
index|]
expr_stmt|;
continue|continue;
block|}
comment|// top of stack is not invalid, this is the first time we see it on top of stack.
comment|// collect it, if belongs to current partition, and then push its kids on itself, if applicable
if|if
condition|(
name|tosOrdinal
operator|>=
name|offset
condition|)
block|{
comment|// tosOrdinal resides in current partition
name|int
name|relativeOrdinal
init|=
name|tosOrdinal
operator|%
name|partitionSize
decl_stmt|;
name|double
name|value
init|=
name|facetRequest
operator|.
name|getValueOf
argument_list|(
name|facetArrays
argument_list|,
name|relativeOrdinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|0
operator|&&
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// Count current ordinal -- the TOS
if|if
condition|(
name|reusable
operator|==
literal|null
condition|)
block|{
name|reusable
operator|=
operator|new
name|MutableFacetResultNode
argument_list|(
name|tosOrdinal
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// it is safe to cast since reusable was created here.
operator|(
operator|(
name|MutableFacetResultNode
operator|)
name|reusable
operator|)
operator|.
name|reset
argument_list|(
name|tosOrdinal
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
operator|++
name|childrenCounter
expr_stmt|;
name|reusable
operator|=
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|reusable
argument_list|)
expr_stmt|;
if|if
condition|(
name|reusable
operator|!=
literal|null
condition|)
block|{
comment|// TODO (Facet): is other logic (not add) needed, per aggregator?
name|parentResultNode
operator|.
name|increaseResidue
argument_list|(
name|reusable
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|localDepth
operator|<
name|depth
condition|)
block|{
comment|// push kid of current tos
name|yc
operator|=
name|youngestChild
index|[
name|tosOrdinal
index|]
expr_stmt|;
while|while
condition|(
name|yc
operator|>=
name|endOffset
condition|)
block|{
name|yc
operator|=
name|olderSibling
index|[
name|yc
index|]
expr_stmt|;
block|}
name|ordinalStack
index|[
operator|++
name|localDepth
index|]
operator|=
name|yc
expr_stmt|;
block|}
else|else
block|{
comment|// localDepth == depth; current tos exhausted its possible children, mark this by pushing INVALID_ORDINAL
name|ordinalStack
index|[
operator|++
name|localDepth
index|]
operator|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
expr_stmt|;
block|}
block|}
comment|// endof while stack is not empty
return|return
name|childrenCounter
return|;
comment|// we're done
block|}
annotation|@
name|Override
DECL|method|renderFacetResult
specifier|public
name|FacetResult
name|renderFacetResult
parameter_list|(
name|IntermediateFacetResult
name|tmpResult
parameter_list|)
block|{
name|TopKFacetResult
name|res
init|=
operator|(
name|TopKFacetResult
operator|)
name|tmpResult
decl_stmt|;
comment|// cast is safe by contract of this class
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|heap
init|=
name|res
operator|.
name|getHeap
argument_list|()
decl_stmt|;
name|MutableFacetResultNode
name|resNode
init|=
operator|(
name|MutableFacetResultNode
operator|)
name|res
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
comment|// cast safe too
for|for
control|(
name|int
name|i
init|=
name|heap
operator|.
name|size
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|resNode
operator|.
name|insertSubResult
argument_list|(
name|heap
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|rearrangeFacetResult
specifier|public
name|FacetResult
name|rearrangeFacetResult
parameter_list|(
name|FacetResult
name|facetResult
parameter_list|)
block|{
name|TopKFacetResult
name|res
init|=
operator|(
name|TopKFacetResult
operator|)
name|facetResult
decl_stmt|;
comment|// cast is safe by contract of this class
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|heap
init|=
name|res
operator|.
name|getHeap
argument_list|()
decl_stmt|;
name|heap
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// just to be safe
name|MutableFacetResultNode
name|topFrn
init|=
operator|(
name|MutableFacetResultNode
operator|)
name|res
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
comment|// safe cast
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|topFrn
operator|.
name|getSubResults
argument_list|()
control|)
block|{
name|heap
operator|.
name|add
argument_list|(
name|frn
argument_list|)
expr_stmt|;
block|}
name|int
name|size
init|=
name|heap
operator|.
name|size
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|subResults
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|heap
operator|.
name|size
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|subResults
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|heap
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|topFrn
operator|.
name|setSubResults
argument_list|(
name|subResults
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
comment|// label top K sub results
DECL|method|labelResult
specifier|public
name|void
name|labelResult
parameter_list|(
name|FacetResult
name|facetResult
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|facetResult
operator|!=
literal|null
condition|)
block|{
comment|// any result to label?
name|FacetResultNode
name|facetResultNode
init|=
name|facetResult
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|facetResultNode
operator|!=
literal|null
condition|)
block|{
comment|// any result to label?
name|facetResultNode
operator|.
name|getLabel
argument_list|(
name|taxonomyReader
argument_list|)
expr_stmt|;
name|int
name|num2label
init|=
name|facetRequest
operator|.
name|getNumLabel
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|facetResultNode
operator|.
name|getSubResults
argument_list|()
control|)
block|{
if|if
condition|(
operator|--
name|num2label
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|frn
operator|.
name|getLabel
argument_list|(
name|taxonomyReader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|////////////////////////////////////////////////////////////////////////////////////
comment|////////////////////////////////////////////////////////////////////////////////////
comment|/**    * Private Mutable implementation of result of faceted search.    */
DECL|class|TopKFacetResult
specifier|private
specifier|static
class|class
name|TopKFacetResult
extends|extends
name|FacetResult
implements|implements
name|IntermediateFacetResult
block|{
comment|// TODO (Facet): is it worth to override PriorityQueue.getSentinelObject()
comment|// for any of our PQs?
DECL|field|heap
specifier|private
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|heap
decl_stmt|;
comment|/**      * Create a Facet Result.      * @param facetRequest Request for which this result was obtained.      * @param facetResultNode top result node for this facet result.      * @param totalFacets - number of children of the targetFacet, up till the requested depth.      */
DECL|method|TopKFacetResult
name|TopKFacetResult
parameter_list|(
name|FacetRequest
name|facetRequest
parameter_list|,
name|MutableFacetResultNode
name|facetResultNode
parameter_list|,
name|int
name|totalFacets
parameter_list|)
block|{
name|super
argument_list|(
name|facetRequest
argument_list|,
name|facetResultNode
argument_list|,
name|totalFacets
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the heap      */
DECL|method|getHeap
specifier|public
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|getHeap
parameter_list|()
block|{
return|return
name|heap
return|;
block|}
comment|/**      * Set the heap for this result.      * @param heap heap top be set.      */
DECL|method|setHeap
specifier|public
name|void
name|setHeap
parameter_list|(
name|Heap
argument_list|<
name|FacetResultNode
argument_list|>
name|heap
parameter_list|)
block|{
name|this
operator|.
name|heap
operator|=
name|heap
expr_stmt|;
block|}
block|}
comment|//////////////////////////////////////////////////////
block|}
end_class
end_unit
