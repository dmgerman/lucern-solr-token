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
name|analysis
operator|.
name|TokenStream
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
name|DisjointSpatialFilter
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
name|CellIterator
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
name|LegacyCell
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
name|SpatialPrefixTree
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
name|query
operator|.
name|SpatialArgs
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
name|query
operator|.
name|SpatialOperation
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
name|query
operator|.
name|UnsupportedSpatialOperation
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
begin_comment
comment|/**  * A {@link PrefixTreeStrategy} which uses {@link AbstractVisitingPrefixTreeFilter}.  * This strategy has support for searching non-point shapes (note: not tested).  * Even a query shape with distErrPct=0 (fully precise to the grid) should have  * good performance for typical data, unless there is a lot of indexed data  * coincident with the shape's edge.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|RecursivePrefixTreeStrategy
specifier|public
class|class
name|RecursivePrefixTreeStrategy
extends|extends
name|PrefixTreeStrategy
block|{
comment|/* Future potential optimizations:      Each shape.relate(otherShape) result could be cached since much of the same relations will be invoked when     multiple segments are involved. Do this for "complex" shapes, not cheap ones, and don't cache when disjoint to     bbox because it's a cheap calc. This is one advantage TermQueryPrefixTreeStrategy has over RPT.     */
DECL|field|prefixGridScanLevel
specifier|protected
name|int
name|prefixGridScanLevel
decl_stmt|;
comment|//Formerly known as simplifyIndexedCells. Eventually will be removed. Only compatible with RPT
comment|// and a LegacyPrefixTree.
DECL|field|pruneLeafyBranches
specifier|protected
name|boolean
name|pruneLeafyBranches
init|=
literal|true
decl_stmt|;
DECL|field|pointsOnly
specifier|protected
name|boolean
name|pointsOnly
init|=
literal|false
decl_stmt|;
comment|//if true, there are no leaves
DECL|field|multiOverlappingIndexedShapes
specifier|protected
name|boolean
name|multiOverlappingIndexedShapes
init|=
literal|true
decl_stmt|;
DECL|method|RecursivePrefixTreeStrategy
specifier|public
name|RecursivePrefixTreeStrategy
parameter_list|(
name|SpatialPrefixTree
name|grid
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|grid
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|prefixGridScanLevel
operator|=
name|grid
operator|.
name|getMaxLevels
argument_list|()
operator|-
literal|4
expr_stmt|;
comment|//TODO this default constant is dependent on the prefix grid size
block|}
comment|/**    * Sets the grid level [1-maxLevels] at which indexed terms are scanned brute-force    * instead of by grid decomposition.  By default this is maxLevels - 4.  The    * final level, maxLevels, is always scanned.    *    * @param prefixGridScanLevel 1 to maxLevels    */
DECL|method|setPrefixGridScanLevel
specifier|public
name|void
name|setPrefixGridScanLevel
parameter_list|(
name|int
name|prefixGridScanLevel
parameter_list|)
block|{
comment|//TODO if negative then subtract from maxlevels
name|this
operator|.
name|prefixGridScanLevel
operator|=
name|prefixGridScanLevel
expr_stmt|;
block|}
comment|/** True if only indexed points shall be supported. There are no "leafs" in such a case.  See    *  {@link IntersectsPrefixTreeFilter#hasIndexedLeaves}. */
DECL|method|setPointsOnly
specifier|public
name|void
name|setPointsOnly
parameter_list|(
name|boolean
name|pointsOnly
parameter_list|)
block|{
name|this
operator|.
name|pointsOnly
operator|=
name|pointsOnly
expr_stmt|;
block|}
comment|/** See {@link ContainsPrefixTreeFilter#multiOverlappingIndexedShapes}. */
DECL|method|setMultiOverlappingIndexedShapes
specifier|public
name|void
name|setMultiOverlappingIndexedShapes
parameter_list|(
name|boolean
name|multiOverlappingIndexedShapes
parameter_list|)
block|{
name|this
operator|.
name|multiOverlappingIndexedShapes
operator|=
name|multiOverlappingIndexedShapes
expr_stmt|;
block|}
comment|/** An optional hint affecting non-point shapes: it will    * simplify/aggregate sets of complete leaves in a cell to its parent, resulting in ~20-25%    * fewer indexed cells. However, it will likely be removed in the future. (default=true)    */
DECL|method|setPruneLeafyBranches
specifier|public
name|void
name|setPruneLeafyBranches
parameter_list|(
name|boolean
name|pruneLeafyBranches
parameter_list|)
block|{
name|this
operator|.
name|pruneLeafyBranches
operator|=
name|pruneLeafyBranches
expr_stmt|;
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
name|str
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|"SPG:("
argument_list|)
operator|.
name|append
argument_list|(
name|grid
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
if|if
condition|(
name|pointsOnly
condition|)
name|str
operator|.
name|append
argument_list|(
literal|",pointsOnly"
argument_list|)
expr_stmt|;
if|if
condition|(
name|pruneLeafyBranches
condition|)
name|str
operator|.
name|append
argument_list|(
literal|",pruneLeafyBranches"
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefixGridScanLevel
operator|!=
name|grid
operator|.
name|getMaxLevels
argument_list|()
operator|-
literal|4
condition|)
name|str
operator|.
name|append
argument_list|(
literal|",prefixGridScanLevel:"
argument_list|)
operator|.
name|append
argument_list|(
literal|""
operator|+
name|prefixGridScanLevel
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|multiOverlappingIndexedShapes
condition|)
name|str
operator|.
name|append
argument_list|(
literal|",!multiOverlappingIndexedShapes"
argument_list|)
expr_stmt|;
return|return
name|str
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createTokenStream
specifier|protected
name|TokenStream
name|createTokenStream
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|Point
operator|||
operator|!
name|pruneLeafyBranches
condition|)
return|return
name|super
operator|.
name|createTokenStream
argument_list|(
name|shape
argument_list|,
name|detailLevel
argument_list|)
return|;
name|List
argument_list|<
name|Cell
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
name|recursiveTraverseAndPrune
argument_list|(
name|grid
operator|.
name|getWorldCell
argument_list|()
argument_list|,
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|cells
argument_list|)
expr_stmt|;
return|return
operator|new
name|CellTokenStream
argument_list|()
operator|.
name|setCells
argument_list|(
name|cells
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns true if cell was added as a leaf. If it wasn't it recursively descends. */
DECL|method|recursiveTraverseAndPrune
specifier|private
name|boolean
name|recursiveTraverseAndPrune
parameter_list|(
name|Cell
name|cell
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|List
argument_list|<
name|Cell
argument_list|>
name|result
parameter_list|)
block|{
comment|// Important: this logic assumes Cells don't share anything with other cells when
comment|// calling cell.getNextLevelCells(). This is only true for LegacyCell.
if|if
condition|(
operator|!
operator|(
name|cell
operator|instanceof
name|LegacyCell
operator|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"pruneLeafyBranches must be disabled for use with grid "
operator|+
name|grid
argument_list|)
throw|;
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|cell
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
comment|//FYI might already be a leaf
block|}
if|if
condition|(
name|cell
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|cell
operator|.
name|getLevel
argument_list|()
operator|!=
literal|0
condition|)
name|result
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
name|int
name|leaves
init|=
literal|0
decl_stmt|;
name|CellIterator
name|subCells
init|=
name|cell
operator|.
name|getNextLevelCells
argument_list|(
name|shape
argument_list|)
decl_stmt|;
while|while
condition|(
name|subCells
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Cell
name|subCell
init|=
name|subCells
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|recursiveTraverseAndPrune
argument_list|(
name|subCell
argument_list|,
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|result
argument_list|)
condition|)
name|leaves
operator|++
expr_stmt|;
block|}
comment|//can we prune?
if|if
condition|(
name|leaves
operator|==
operator|(
operator|(
name|LegacyCell
operator|)
name|cell
operator|)
operator|.
name|getSubCellsSize
argument_list|()
operator|&&
name|cell
operator|.
name|getLevel
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|//Optimization: substitute the parent as a leaf instead of adding all
comment|// children as leaves
comment|//remove the leaves
do|do
block|{
name|result
operator|.
name|remove
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|//remove last
block|}
do|while
condition|(
operator|--
name|leaves
operator|>
literal|0
condition|)
do|;
comment|//add cell as the leaf
name|cell
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|makeFilter
specifier|public
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
specifier|final
name|SpatialOperation
name|op
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|==
name|SpatialOperation
operator|.
name|IsDisjointTo
condition|)
return|return
operator|new
name|DisjointSpatialFilter
argument_list|(
name|this
argument_list|,
name|args
argument_list|,
name|getFieldName
argument_list|()
argument_list|)
return|;
name|Shape
name|shape
init|=
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|int
name|detailLevel
init|=
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|args
operator|.
name|resolveDistErr
argument_list|(
name|ctx
argument_list|,
name|distErrPct
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|pointsOnly
operator|||
name|op
operator|==
name|SpatialOperation
operator|.
name|Intersects
condition|)
block|{
return|return
operator|new
name|IntersectsPrefixTreeFilter
argument_list|(
name|shape
argument_list|,
name|getFieldName
argument_list|()
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|,
operator|!
name|pointsOnly
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|op
operator|==
name|SpatialOperation
operator|.
name|IsWithin
condition|)
block|{
return|return
operator|new
name|WithinPrefixTreeFilter
argument_list|(
name|shape
argument_list|,
name|getFieldName
argument_list|()
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|prefixGridScanLevel
argument_list|,
operator|-
literal|1
argument_list|)
return|;
comment|//-1 flag is slower but ensures correct results
block|}
elseif|else
if|if
condition|(
name|op
operator|==
name|SpatialOperation
operator|.
name|Contains
condition|)
block|{
return|return
operator|new
name|ContainsPrefixTreeFilter
argument_list|(
name|shape
argument_list|,
name|getFieldName
argument_list|()
argument_list|,
name|grid
argument_list|,
name|detailLevel
argument_list|,
name|multiOverlappingIndexedShapes
argument_list|)
return|;
block|}
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|op
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
