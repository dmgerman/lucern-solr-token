begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
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
name|IndexReaderContext
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|Filter
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|NumberRangePrefixTree
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|NumberRangePrefixTree
operator|.
name|UnitNRShape
import|;
end_import
begin_comment
comment|/** A PrefixTree based on Number/Date ranges. This isn't very "spatial" on the surface (to the user) but  * it's implemented using spatial so that's why it's here extending a SpatialStrategy. When using this class, you will  * use various utility methods on the prefix tree implementation to convert objects/strings to/from shapes.  *  * To use with dates, pass in {@link org.apache.lucene.spatial.prefix.tree.DateRangePrefixTree}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|NumberRangePrefixTreeStrategy
specifier|public
class|class
name|NumberRangePrefixTreeStrategy
extends|extends
name|RecursivePrefixTreeStrategy
block|{
DECL|method|NumberRangePrefixTreeStrategy
specifier|public
name|NumberRangePrefixTreeStrategy
parameter_list|(
name|NumberRangePrefixTree
name|prefixTree
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|prefixTree
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|setPruneLeafyBranches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setPrefixGridScanLevel
argument_list|(
name|prefixTree
operator|.
name|getMaxLevels
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|//user might want to change, however
name|setPointsOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setDistErrPct
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGrid
specifier|public
name|NumberRangePrefixTree
name|getGrid
parameter_list|()
block|{
return|return
operator|(
name|NumberRangePrefixTree
operator|)
name|super
operator|.
name|getGrid
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createCellIteratorToIndex
specifier|protected
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|createCellIteratorToIndex
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|reuse
parameter_list|)
block|{
comment|//levels doesn't actually matter; NumberRange based Shapes have their own "level".
return|return
name|super
operator|.
name|createCellIteratorToIndex
argument_list|(
name|shape
argument_list|,
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|,
name|reuse
argument_list|)
return|;
block|}
comment|/** Unsupported. */
annotation|@
name|Override
DECL|method|makeDistanceValueSource
specifier|public
name|ValueSource
name|makeDistanceValueSource
parameter_list|(
name|Point
name|queryPoint
parameter_list|,
name|double
name|multiplier
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Calculates facets between {@code start} and {@code end} to a detail level one greater than that provided by the    * arguments. For example providing March to October of 2014 would return facets to the day level of those months.    * This is just a convenience method.    * @see #calcFacets(IndexReaderContext, Filter, Shape, int)    */
DECL|method|calcFacets
specifier|public
name|Facets
name|calcFacets
parameter_list|(
name|IndexReaderContext
name|context
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|UnitNRShape
name|start
parameter_list|,
name|UnitNRShape
name|end
parameter_list|)
throws|throws
name|IOException
block|{
name|Shape
name|facetRange
init|=
name|getGrid
argument_list|()
operator|.
name|toRangeShape
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|int
name|detailLevel
init|=
name|Math
operator|.
name|max
argument_list|(
name|start
operator|.
name|getLevel
argument_list|()
argument_list|,
name|end
operator|.
name|getLevel
argument_list|()
argument_list|)
operator|+
literal|1
decl_stmt|;
return|return
name|calcFacets
argument_list|(
name|context
argument_list|,
name|filter
argument_list|,
name|facetRange
argument_list|,
name|detailLevel
argument_list|)
return|;
block|}
comment|/**    * Calculates facets (aggregated counts) given a range shape (start-end span) and a level, which specifies the detail.    * To get the level of an existing shape, say a Calendar, call    * {@link org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree#toUnitShape(Object)} then call    * {@link org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree.UnitNRShape#getLevel()}.    * Facet computation is implemented by navigating the underlying indexed terms efficiently.    */
DECL|method|calcFacets
specifier|public
name|Facets
name|calcFacets
parameter_list|(
name|IndexReaderContext
name|context
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Shape
name|facetRange
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Facets
name|facets
init|=
operator|new
name|Facets
argument_list|(
name|level
argument_list|)
decl_stmt|;
name|PrefixTreeFacetCounter
operator|.
name|compute
argument_list|(
name|this
argument_list|,
name|context
argument_list|,
name|filter
argument_list|,
name|facetRange
argument_list|,
name|level
argument_list|,
operator|new
name|PrefixTreeFacetCounter
operator|.
name|FacetVisitor
argument_list|()
block|{
name|Facets
operator|.
name|FacetParentVal
name|parentFacet
decl_stmt|;
name|UnitNRShape
name|parentShape
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Cell
name|cell
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|<
name|level
operator|-
literal|1
condition|)
block|{
comment|//some ancestor of parent facet level, direct or distant
name|parentFacet
operator|=
literal|null
expr_stmt|;
comment|//reset
name|parentShape
operator|=
literal|null
expr_stmt|;
comment|//reset
name|facets
operator|.
name|topLeaves
operator|+=
name|count
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|level
operator|-
literal|1
condition|)
block|{
comment|//parent
comment|//set up FacetParentVal
name|setupParent
argument_list|(
operator|(
name|UnitNRShape
operator|)
name|cell
operator|.
name|getShape
argument_list|()
argument_list|)
expr_stmt|;
name|parentFacet
operator|.
name|parentLeaves
operator|+=
name|count
expr_stmt|;
block|}
else|else
block|{
comment|//at facet level
name|UnitNRShape
name|unitShape
init|=
operator|(
name|UnitNRShape
operator|)
name|cell
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|UnitNRShape
name|unitShapeParent
init|=
name|unitShape
operator|.
name|getShapeAtLevel
argument_list|(
name|unitShape
operator|.
name|getLevel
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentFacet
operator|==
literal|null
operator|||
operator|!
name|parentShape
operator|.
name|equals
argument_list|(
name|unitShapeParent
argument_list|)
condition|)
block|{
name|setupParent
argument_list|(
name|unitShapeParent
argument_list|)
expr_stmt|;
block|}
comment|//lazy init childCounts
if|if
condition|(
name|parentFacet
operator|.
name|childCounts
operator|==
literal|null
condition|)
block|{
name|parentFacet
operator|.
name|childCounts
operator|=
operator|new
name|int
index|[
name|parentFacet
operator|.
name|childCountsLen
index|]
expr_stmt|;
block|}
name|parentFacet
operator|.
name|childCounts
index|[
name|unitShape
operator|.
name|getValAtLevel
argument_list|(
name|cell
operator|.
name|getLevel
argument_list|()
argument_list|)
index|]
operator|+=
name|count
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setupParent
parameter_list|(
name|UnitNRShape
name|unitShape
parameter_list|)
block|{
name|parentShape
operator|=
name|unitShape
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|//Look for existing parentFacet (from previous segment), or create anew if needed
name|parentFacet
operator|=
name|facets
operator|.
name|parents
operator|.
name|get
argument_list|(
name|parentShape
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentFacet
operator|==
literal|null
condition|)
block|{
comment|//didn't find one; make a new one
name|parentFacet
operator|=
operator|new
name|Facets
operator|.
name|FacetParentVal
argument_list|()
expr_stmt|;
name|parentFacet
operator|.
name|childCountsLen
operator|=
name|getGrid
argument_list|()
operator|.
name|getNumSubCells
argument_list|(
name|parentShape
argument_list|)
expr_stmt|;
name|facets
operator|.
name|parents
operator|.
name|put
argument_list|(
name|parentShape
argument_list|,
name|parentFacet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|facets
return|;
block|}
comment|/** Facet response information */
DECL|class|Facets
specifier|public
specifier|static
class|class
name|Facets
block|{
comment|//TODO consider a variable-level structure -- more general purpose.
DECL|method|Facets
specifier|public
name|Facets
parameter_list|(
name|int
name|detailLevel
parameter_list|)
block|{
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
expr_stmt|;
block|}
comment|/** The bottom-most detail-level counted, as requested. */
DECL|field|detailLevel
specifier|public
specifier|final
name|int
name|detailLevel
decl_stmt|;
comment|/**      * The count of documents with ranges that completely spanned the parents of the detail level. In more technical      * terms, this is the count of leaf cells 2 up and higher from the bottom. Usually you only care about counts at      * detailLevel, and so you will add this number to all other counts below, including to omitted/implied children      * counts of 0. If there are no indexed ranges (just instances, i.e. fully specified dates) then this value will      * always be 0.      */
DECL|field|topLeaves
specifier|public
name|int
name|topLeaves
decl_stmt|;
comment|/** Holds all the {@link FacetParentVal} instances in order of the key. This is sparse; there won't be an      * instance if it's count and children are all 0. The keys are {@link org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree.UnitNRShape} shapes, which can be      * converted back to the original Object (i.e. a Calendar) via      * {@link NumberRangePrefixTree#toObject(org.apache.lucene.spatial.prefix.tree.NumberRangePrefixTree.UnitNRShape)}. */
DECL|field|parents
specifier|public
specifier|final
name|SortedMap
argument_list|<
name|UnitNRShape
argument_list|,
name|FacetParentVal
argument_list|>
name|parents
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Holds a block of detailLevel counts aggregated to their parent level. */
DECL|class|FacetParentVal
specifier|public
specifier|static
class|class
name|FacetParentVal
block|{
comment|/** The count of ranges that span all of the childCounts.  In more technical terms, this is the number of leaf        * cells found at this parent.  Treat this like {@link Facets#topLeaves}. */
DECL|field|parentLeaves
specifier|public
name|int
name|parentLeaves
decl_stmt|;
comment|/** The length of {@link #childCounts}. If childCounts is not null then this is childCounts.length, otherwise it        * says how long it would have been if it weren't null. */
DECL|field|childCountsLen
specifier|public
name|int
name|childCountsLen
decl_stmt|;
comment|/** The detail level counts. It will be null if there are none, and thus they are assumed 0. Most apps, when        * presenting the information, will add {@link #topLeaves} and {@link #parentLeaves} to each count. */
DECL|field|childCounts
specifier|public
name|int
index|[]
name|childCounts
decl_stmt|;
comment|//assert childCountsLen == childCounts.length
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|2048
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Facets: level="
operator|+
name|detailLevel
operator|+
literal|" topLeaves="
operator|+
name|topLeaves
operator|+
literal|" parentCount="
operator|+
name|parents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|UnitNRShape
argument_list|,
name|FacetParentVal
argument_list|>
name|entry
range|:
name|parents
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|1000
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"..."
argument_list|)
expr_stmt|;
break|break;
block|}
specifier|final
name|FacetParentVal
name|pVal
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" leafCount="
operator|+
name|pVal
operator|.
name|parentLeaves
argument_list|)
expr_stmt|;
if|if
condition|(
name|pVal
operator|.
name|childCounts
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|pVal
operator|.
name|childCounts
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
