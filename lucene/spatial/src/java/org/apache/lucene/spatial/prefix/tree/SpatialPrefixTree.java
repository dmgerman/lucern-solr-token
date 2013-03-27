begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
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
operator|.
name|tree
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
name|context
operator|.
name|SpatialContext
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
name|Rectangle
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Collection
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
name|List
import|;
end_import
begin_comment
comment|/**  * A spatial Prefix Tree, or Trie, which decomposes shapes into prefixed strings  * at variable lengths corresponding to variable precision.   Each string  * corresponds to a rectangular spatial region.  This approach is  * also referred to "Grids", "Tiles", and "Spatial Tiers".  *<p/>  * Implementations of this class should be thread-safe and immutable once  * initialized.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SpatialPrefixTree
specifier|public
specifier|abstract
class|class
name|SpatialPrefixTree
block|{
DECL|field|UTF8
specifier|protected
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|maxLevels
specifier|protected
specifier|final
name|int
name|maxLevels
decl_stmt|;
DECL|field|ctx
specifier|protected
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|method|SpatialPrefixTree
specifier|public
name|SpatialPrefixTree
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|int
name|maxLevels
parameter_list|)
block|{
assert|assert
name|maxLevels
operator|>
literal|0
assert|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|maxLevels
operator|=
name|maxLevels
expr_stmt|;
block|}
DECL|method|getSpatialContext
specifier|public
name|SpatialContext
name|getSpatialContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
DECL|method|getMaxLevels
specifier|public
name|int
name|getMaxLevels
parameter_list|()
block|{
return|return
name|maxLevels
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(maxLevels:"
operator|+
name|maxLevels
operator|+
literal|",ctx:"
operator|+
name|ctx
operator|+
literal|")"
return|;
block|}
comment|/**    * Returns the level of the largest grid in which its longest side is less    * than or equal to the provided distance (in degrees). Consequently {@code    * dist} acts as an error epsilon declaring the amount of detail needed in the    * grid, such that you can get a grid with just the right amount of    * precision.    *    * @param dist>= 0    * @return level [1 to maxLevels]    */
DECL|method|getLevelForDistance
specifier|public
specifier|abstract
name|int
name|getLevelForDistance
parameter_list|(
name|double
name|dist
parameter_list|)
function_decl|;
comment|/**    * Given a node having the specified level, returns the distance from opposite    * corners. Since this might very depending on where the node is, this method    * may over-estimate.    *    * @param level [1 to maxLevels]    * @return> 0    */
DECL|method|getDistanceForLevel
specifier|public
name|double
name|getDistanceForLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|level
argument_list|<
literal|1
operator|||
name|level
argument_list|>
name|getMaxLevels
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Level must be in 1 to maxLevels range"
argument_list|)
throw|;
comment|//TODO cache for each level
name|Node
name|node
init|=
name|getNode
argument_list|(
name|ctx
operator|.
name|getWorldBounds
argument_list|()
operator|.
name|getCenter
argument_list|()
argument_list|,
name|level
argument_list|)
decl_stmt|;
name|Rectangle
name|bbox
init|=
name|node
operator|.
name|getShape
argument_list|()
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|double
name|width
init|=
name|bbox
operator|.
name|getWidth
argument_list|()
decl_stmt|;
name|double
name|height
init|=
name|bbox
operator|.
name|getHeight
argument_list|()
decl_stmt|;
comment|//Use standard cartesian hypotenuse. For geospatial, this answer is larger
comment|// than the correct one but it's okay to over-estimate.
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|width
operator|*
name|width
operator|+
name|height
operator|*
name|height
argument_list|)
return|;
block|}
DECL|field|worldNode
specifier|private
specifier|transient
name|Node
name|worldNode
decl_stmt|;
comment|//cached
comment|/**    * Returns the level 0 cell which encompasses all spatial data. Equivalent to {@link #getNode(String)} with "".    * This cell is threadsafe, just like a spatial prefix grid is, although cells aren't    * generally threadsafe.    * TODO rename to getTopCell or is this fine?    */
DECL|method|getWorldNode
specifier|public
name|Node
name|getWorldNode
parameter_list|()
block|{
if|if
condition|(
name|worldNode
operator|==
literal|null
condition|)
block|{
name|worldNode
operator|=
name|getNode
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|worldNode
return|;
block|}
comment|/**    * The cell for the specified token. The empty string should be equal to {@link #getWorldNode()}.    * Precondition: Never called when token length> maxLevel.    */
DECL|method|getNode
specifier|public
specifier|abstract
name|Node
name|getNode
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
DECL|method|getNode
specifier|public
specifier|abstract
name|Node
name|getNode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
DECL|method|getNode
specifier|public
specifier|final
name|Node
name|getNode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|Node
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|==
literal|null
condition|)
block|{
return|return
name|getNode
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
name|target
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|target
return|;
block|}
comment|/**    * Returns the cell containing point {@code p} at the specified {@code level}.    */
DECL|method|getNode
specifier|protected
name|Node
name|getNode
parameter_list|(
name|Point
name|p
parameter_list|,
name|int
name|level
parameter_list|)
block|{
return|return
name|getNodes
argument_list|(
name|p
argument_list|,
name|level
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Gets the intersecting cells for the specified shape, without exceeding    * detail level. If a cell is within the query shape then it's marked as a    * leaf and none of its children are added.    *<p/>    * This implementation checks if shape is a Point and if so returns {@link    * #getNodes(com.spatial4j.core.shape.Point, int, boolean)}.    *    * @param shape       the shape; non-null    * @param detailLevel the maximum detail level to get cells for    * @param inclParents if true then all parent cells of leaves are returned    *                    too. The top world cell is never returned.    * @param simplify    for non-point shapes, this will simply/aggregate sets of    *                    complete leaves in a cell to its parent, resulting in    *                    ~20-25% fewer cells.    * @return a set of cells (no dups), sorted, immutable, non-null    */
DECL|method|getNodes
specifier|public
name|List
argument_list|<
name|Node
argument_list|>
name|getNodes
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|boolean
name|inclParents
parameter_list|,
name|boolean
name|simplify
parameter_list|)
block|{
comment|//TODO consider an on-demand iterator -- it won't build up all cells in memory.
if|if
condition|(
name|detailLevel
operator|>
name|maxLevels
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"detailLevel> maxLevels"
argument_list|)
throw|;
block|}
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
block|{
return|return
name|getNodes
argument_list|(
operator|(
name|Point
operator|)
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Node
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|inclParents
condition|?
literal|4096
else|:
literal|2048
argument_list|)
decl_stmt|;
name|recursiveGetNodes
argument_list|(
name|getWorldNode
argument_list|()
argument_list|,
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|,
name|simplify
argument_list|,
name|cells
argument_list|)
expr_stmt|;
return|return
name|cells
return|;
block|}
comment|/**    * Returns true if node was added as a leaf. If it wasn't it recursively    * descends.    */
DECL|method|recursiveGetNodes
specifier|private
name|boolean
name|recursiveGetNodes
parameter_list|(
name|Node
name|node
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|boolean
name|inclParents
parameter_list|,
name|boolean
name|simplify
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|result
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getLevel
argument_list|()
operator|==
name|detailLevel
condition|)
block|{
name|node
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
comment|//FYI might already be a leaf
block|}
if|if
condition|(
name|node
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|inclParents
operator|&&
name|node
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
name|node
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Node
argument_list|>
name|subCells
init|=
name|node
operator|.
name|getSubCells
argument_list|(
name|shape
argument_list|)
decl_stmt|;
name|int
name|leaves
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Node
name|subCell
range|:
name|subCells
control|)
block|{
if|if
condition|(
name|recursiveGetNodes
argument_list|(
name|subCell
argument_list|,
name|shape
argument_list|,
name|detailLevel
argument_list|,
name|inclParents
argument_list|,
name|simplify
argument_list|,
name|result
argument_list|)
condition|)
name|leaves
operator|++
expr_stmt|;
block|}
comment|//can we simplify?
if|if
condition|(
name|simplify
operator|&&
name|leaves
operator|==
name|node
operator|.
name|getSubCellsSize
argument_list|()
operator|&&
name|node
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
comment|//add node as the leaf
name|node
operator|.
name|setLeaf
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|inclParents
condition|)
comment|// otherwise it was already added up above
name|result
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * A Point-optimized implementation of    * {@link #getNodes(com.spatial4j.core.shape.Shape, int, boolean, boolean)}. That    * method in facts calls this for points.    *<p/>    * This implementation depends on {@link #getNode(String)} being fast, as its    * called repeatedly when incPlarents is true.    */
DECL|method|getNodes
specifier|public
name|List
argument_list|<
name|Node
argument_list|>
name|getNodes
parameter_list|(
name|Point
name|p
parameter_list|,
name|int
name|detailLevel
parameter_list|,
name|boolean
name|inclParents
parameter_list|)
block|{
name|Node
name|cell
init|=
name|getNode
argument_list|(
name|p
argument_list|,
name|detailLevel
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|inclParents
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|cell
argument_list|)
return|;
block|}
name|String
name|endToken
init|=
name|cell
operator|.
name|getTokenString
argument_list|()
decl_stmt|;
assert|assert
name|endToken
operator|.
name|length
argument_list|()
operator|==
name|detailLevel
assert|;
name|List
argument_list|<
name|Node
argument_list|>
name|cells
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|detailLevel
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|detailLevel
condition|;
name|i
operator|++
control|)
block|{
name|cells
operator|.
name|add
argument_list|(
name|getNode
argument_list|(
name|endToken
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|cells
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
return|return
name|cells
return|;
block|}
comment|/**    * Will add the trailing leaf byte for leaves. This isn't particularly efficient.    */
DECL|method|nodesToTokenStrings
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|nodesToTokenStrings
parameter_list|(
name|Collection
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
specifier|final
name|String
name|token
init|=
name|node
operator|.
name|getTokenString
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|token
operator|+
operator|(
name|char
operator|)
name|Node
operator|.
name|LEAF_BYTE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tokens
return|;
block|}
block|}
end_class
end_unit
