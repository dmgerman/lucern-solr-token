begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
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
comment|/**  * Represents a grid cell. These are not necessarily threadsafe, although new Cell("") (world cell) must be.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Node
specifier|public
specifier|abstract
class|class
name|Node
implements|implements
name|Comparable
argument_list|<
name|Node
argument_list|>
block|{
DECL|field|LEAF_BYTE
specifier|public
specifier|static
specifier|final
name|byte
name|LEAF_BYTE
init|=
literal|'+'
decl_stmt|;
comment|//NOTE: must sort before letters& numbers
comment|/*   Holds a byte[] and/or String representation of the cell. Both are lazy constructed from the other.   Neither contains the trailing leaf byte.    */
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|b_off
specifier|private
name|int
name|b_off
decl_stmt|;
DECL|field|b_len
specifier|private
name|int
name|b_len
decl_stmt|;
DECL|field|token
specifier|private
name|String
name|token
decl_stmt|;
comment|//this is the only part of equality
DECL|field|shapeRel
specifier|protected
name|SpatialRelation
name|shapeRel
decl_stmt|;
comment|//set in getSubCells(filter), and via setLeaf().
DECL|field|spatialPrefixTree
specifier|private
name|SpatialPrefixTree
name|spatialPrefixTree
decl_stmt|;
DECL|method|Node
specifier|protected
name|Node
parameter_list|(
name|SpatialPrefixTree
name|spatialPrefixTree
parameter_list|,
name|String
name|token
parameter_list|)
block|{
name|this
operator|.
name|spatialPrefixTree
operator|=
name|spatialPrefixTree
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|token
operator|.
name|charAt
argument_list|(
name|token
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
operator|(
name|char
operator|)
name|LEAF_BYTE
condition|)
block|{
name|this
operator|.
name|token
operator|=
name|token
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|token
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|setLeaf
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getLevel
argument_list|()
operator|==
literal|0
condition|)
name|getShape
argument_list|()
expr_stmt|;
comment|//ensure any lazy instantiation completes to make this threadsafe
block|}
DECL|method|Node
specifier|protected
name|Node
parameter_list|(
name|SpatialPrefixTree
name|spatialPrefixTree
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|spatialPrefixTree
operator|=
name|spatialPrefixTree
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|b_off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|b_len
operator|=
name|len
expr_stmt|;
name|b_fixLeaf
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|getLevel
argument_list|()
operator|!=
literal|0
assert|;
name|token
operator|=
literal|null
expr_stmt|;
name|shapeRel
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|b_off
operator|=
name|off
expr_stmt|;
name|this
operator|.
name|b_len
operator|=
name|len
expr_stmt|;
name|b_fixLeaf
argument_list|()
expr_stmt|;
block|}
DECL|method|b_fixLeaf
specifier|private
name|void
name|b_fixLeaf
parameter_list|()
block|{
if|if
condition|(
name|bytes
index|[
name|b_off
operator|+
name|b_len
operator|-
literal|1
index|]
operator|==
name|LEAF_BYTE
condition|)
block|{
name|b_len
operator|--
expr_stmt|;
name|setLeaf
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getLevel
argument_list|()
operator|==
name|spatialPrefixTree
operator|.
name|getMaxLevels
argument_list|()
condition|)
block|{
name|setLeaf
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getShapeRel
specifier|public
name|SpatialRelation
name|getShapeRel
parameter_list|()
block|{
return|return
name|shapeRel
return|;
block|}
DECL|method|isLeaf
specifier|public
name|boolean
name|isLeaf
parameter_list|()
block|{
return|return
name|shapeRel
operator|==
name|SpatialRelation
operator|.
name|WITHIN
return|;
block|}
DECL|method|setLeaf
specifier|public
name|void
name|setLeaf
parameter_list|()
block|{
assert|assert
name|getLevel
argument_list|()
operator|!=
literal|0
assert|;
name|shapeRel
operator|=
name|SpatialRelation
operator|.
name|WITHIN
expr_stmt|;
block|}
comment|/**    * Note: doesn't contain a trailing leaf byte.    */
DECL|method|getTokenString
specifier|public
name|String
name|getTokenString
parameter_list|()
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|token
operator|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|b_off
argument_list|,
name|b_len
argument_list|,
name|SpatialPrefixTree
operator|.
name|UTF8
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
comment|/**    * Note: doesn't contain a trailing leaf byte.    */
DECL|method|getTokenBytes
specifier|public
name|byte
index|[]
name|getTokenBytes
parameter_list|()
block|{
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|b_off
operator|!=
literal|0
operator|||
name|b_len
operator|!=
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not supported if byte[] needs to be recreated."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|bytes
operator|=
name|token
operator|.
name|getBytes
argument_list|(
name|SpatialPrefixTree
operator|.
name|UTF8
argument_list|)
expr_stmt|;
name|b_off
operator|=
literal|0
expr_stmt|;
name|b_len
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
DECL|method|getLevel
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|token
operator|!=
literal|null
condition|?
name|token
operator|.
name|length
argument_list|()
else|:
name|b_len
return|;
block|}
comment|//TODO add getParent() and update some algorithms to use this?
comment|//public Cell getParent();
comment|/**    * Like {@link #getSubCells()} but with the results filtered by a shape. If that shape is a {@link com.spatial4j.core.shape.Point} then it    * must call {@link #getSubCell(com.spatial4j.core.shape.Point)};    * Precondition: Never called when getLevel() == maxLevel.    *    * @param shapeFilter an optional filter for the returned cells.    * @return A set of cells (no dups), sorted. Not Modifiable.    */
DECL|method|getSubCells
specifier|public
name|Collection
argument_list|<
name|Node
argument_list|>
name|getSubCells
parameter_list|(
name|Shape
name|shapeFilter
parameter_list|)
block|{
comment|//Note: Higher-performing subclasses might override to consider the shape filter to generate fewer cells.
if|if
condition|(
name|shapeFilter
operator|instanceof
name|Point
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|getSubCell
argument_list|(
operator|(
name|Point
operator|)
name|shapeFilter
argument_list|)
argument_list|)
return|;
block|}
name|Collection
argument_list|<
name|Node
argument_list|>
name|cells
init|=
name|getSubCells
argument_list|()
decl_stmt|;
if|if
condition|(
name|shapeFilter
operator|==
literal|null
condition|)
block|{
return|return
name|cells
return|;
block|}
name|List
argument_list|<
name|Node
argument_list|>
name|copy
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|(
name|cells
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|//copy since cells contractually isn't modifiable
for|for
control|(
name|Node
name|cell
range|:
name|cells
control|)
block|{
name|SpatialRelation
name|rel
init|=
name|cell
operator|.
name|getShape
argument_list|()
operator|.
name|relate
argument_list|(
name|shapeFilter
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel
operator|==
name|SpatialRelation
operator|.
name|DISJOINT
condition|)
continue|continue;
name|cell
operator|.
name|shapeRel
operator|=
name|rel
expr_stmt|;
name|copy
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
block|}
name|cells
operator|=
name|copy
expr_stmt|;
return|return
name|cells
return|;
block|}
comment|/**    * Performant implementations are expected to implement this efficiently by considering the current    * cell's boundary.    * Precondition: Never called when getLevel() == maxLevel.    * Precondition: this.getShape().relate(p) != DISJOINT.    */
DECL|method|getSubCell
specifier|public
specifier|abstract
name|Node
name|getSubCell
parameter_list|(
name|Point
name|p
parameter_list|)
function_decl|;
comment|//TODO Cell getSubCell(byte b)
comment|/**    * Gets the cells at the next grid cell level that cover this cell.    * Precondition: Never called when getLevel() == maxLevel.    *    * @return A set of cells (no dups), sorted. Not Modifiable.    */
DECL|method|getSubCells
specifier|protected
specifier|abstract
name|Collection
argument_list|<
name|Node
argument_list|>
name|getSubCells
parameter_list|()
function_decl|;
comment|/**    * {@link #getSubCells()}.size() -- usually a constant. Should be>=2    */
DECL|method|getSubCellsSize
specifier|public
specifier|abstract
name|int
name|getSubCellsSize
parameter_list|()
function_decl|;
DECL|method|getShape
specifier|public
specifier|abstract
name|Shape
name|getShape
parameter_list|()
function_decl|;
DECL|method|getCenter
specifier|public
name|Point
name|getCenter
parameter_list|()
block|{
return|return
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Node
name|o
parameter_list|)
block|{
return|return
name|getTokenString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getTokenString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|!
operator|(
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|Node
operator|)
operator|)
operator|&&
name|getTokenString
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Node
operator|)
name|obj
operator|)
operator|.
name|getTokenString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getTokenString
argument_list|()
operator|.
name|hashCode
argument_list|()
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
name|getTokenString
argument_list|()
operator|+
operator|(
name|isLeaf
argument_list|()
condition|?
operator|(
name|char
operator|)
name|LEAF_BYTE
else|:
literal|""
operator|)
return|;
block|}
block|}
end_class
end_unit
