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
name|LinkedList
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
name|document
operator|.
name|GeoPointField
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
name|FilteredTermsEnum
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
name|TermsEnum
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
name|BytesRefBuilder
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
name|GeoRelationUtils
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
name|GeoUtils
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
name|NumericUtils
import|;
end_import
begin_comment
comment|/**  * computes all ranges along a space-filling curve that represents  * the given bounding box and enumerates all terms contained within those ranges  *  *  @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointTermsEnum
specifier|abstract
class|class
name|GeoPointTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|minLon
specifier|protected
specifier|final
name|double
name|minLon
decl_stmt|;
DECL|field|minLat
specifier|protected
specifier|final
name|double
name|minLat
decl_stmt|;
DECL|field|maxLon
specifier|protected
specifier|final
name|double
name|maxLon
decl_stmt|;
DECL|field|maxLat
specifier|protected
specifier|final
name|double
name|maxLat
decl_stmt|;
DECL|field|currentRange
specifier|protected
name|Range
name|currentRange
decl_stmt|;
DECL|field|currentCell
specifier|private
specifier|final
name|BytesRefBuilder
name|currentCell
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|nextSubRange
specifier|private
specifier|final
name|BytesRefBuilder
name|nextSubRange
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|rangeBounds
specifier|private
specifier|final
name|List
argument_list|<
name|Range
argument_list|>
name|rangeBounds
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// detail level should be a factor of PRECISION_STEP limiting the depth of recursion (and number of ranges)
DECL|field|DETAIL_LEVEL
specifier|protected
specifier|final
name|short
name|DETAIL_LEVEL
decl_stmt|;
DECL|method|GeoPointTermsEnum
name|GeoPointTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
specifier|final
name|long
name|rectMinHash
init|=
name|GeoUtils
operator|.
name|mortonHash
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|)
decl_stmt|;
specifier|final
name|long
name|rectMaxHash
init|=
name|GeoUtils
operator|.
name|mortonHash
argument_list|(
name|maxLon
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
name|this
operator|.
name|minLon
operator|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|rectMinHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|minLat
operator|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|rectMinHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|rectMaxHash
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|rectMaxHash
argument_list|)
expr_stmt|;
name|DETAIL_LEVEL
operator|=
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
name|GeoUtils
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
name|computeMaxShift
argument_list|()
operator|)
operator|/
literal|2
argument_list|)
expr_stmt|;
name|computeRange
argument_list|(
literal|0L
argument_list|,
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
name|GeoUtils
operator|.
name|BITS
operator|)
operator|<<
literal|1
operator|)
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
assert|;
name|Collections
operator|.
name|sort
argument_list|(
name|rangeBounds
argument_list|)
expr_stmt|;
block|}
comment|/**    * entry point for recursively computing ranges    */
DECL|method|computeRange
specifier|private
specifier|final
name|void
name|computeRange
parameter_list|(
name|long
name|term
parameter_list|,
specifier|final
name|short
name|shift
parameter_list|)
block|{
specifier|final
name|long
name|split
init|=
name|term
operator||
operator|(
literal|0x1L
operator|<<
name|shift
operator|)
decl_stmt|;
assert|assert
name|shift
operator|<
literal|64
assert|;
specifier|final
name|long
name|upperMax
decl_stmt|;
if|if
condition|(
name|shift
operator|<
literal|63
condition|)
block|{
name|upperMax
operator|=
name|term
operator||
operator|(
operator|(
literal|1L
operator|<<
operator|(
name|shift
operator|+
literal|1
operator|)
operator|)
operator|-
literal|1
operator|)
expr_stmt|;
block|}
else|else
block|{
name|upperMax
operator|=
literal|0xffffffffffffffffL
expr_stmt|;
block|}
specifier|final
name|long
name|lowerMax
init|=
name|split
operator|-
literal|1
decl_stmt|;
name|relateAndRecurse
argument_list|(
name|term
argument_list|,
name|lowerMax
argument_list|,
name|shift
argument_list|)
expr_stmt|;
name|relateAndRecurse
argument_list|(
name|split
argument_list|,
name|upperMax
argument_list|,
name|shift
argument_list|)
expr_stmt|;
block|}
comment|/**    * recurse to higher level precision cells to find ranges along the space-filling curve that fall within the    * query box    *    * @param start starting value on the space-filling curve for a cell at a given res    * @param end ending value on the space-filling curve for a cell at a given res    * @param res spatial res represented as a bit shift (MSB is lower res)    */
DECL|method|relateAndRecurse
specifier|private
name|void
name|relateAndRecurse
parameter_list|(
specifier|final
name|long
name|start
parameter_list|,
specifier|final
name|long
name|end
parameter_list|,
specifier|final
name|short
name|res
parameter_list|)
block|{
specifier|final
name|double
name|minLon
init|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|start
argument_list|)
decl_stmt|;
specifier|final
name|double
name|minLat
init|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|start
argument_list|)
decl_stmt|;
specifier|final
name|double
name|maxLon
init|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|end
argument_list|)
decl_stmt|;
specifier|final
name|double
name|maxLat
init|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|end
argument_list|)
decl_stmt|;
specifier|final
name|short
name|level
init|=
call|(
name|short
call|)
argument_list|(
operator|(
name|GeoUtils
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
name|res
operator|>>>
literal|1
argument_list|)
decl_stmt|;
comment|// if cell is within and a factor of the precision step, or it crosses the edge of the shape add the range
specifier|final
name|boolean
name|within
init|=
name|res
operator|%
name|GeoPointField
operator|.
name|PRECISION_STEP
operator|==
literal|0
operator|&&
name|cellWithin
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
if|if
condition|(
name|within
operator|||
operator|(
name|level
operator|==
name|DETAIL_LEVEL
operator|&&
name|cellIntersectsShape
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|)
condition|)
block|{
specifier|final
name|short
name|nextRes
init|=
call|(
name|short
call|)
argument_list|(
name|res
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextRes
operator|%
name|GeoPointField
operator|.
name|PRECISION_STEP
operator|==
literal|0
condition|)
block|{
name|rangeBounds
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|start
argument_list|,
name|nextRes
argument_list|,
operator|!
name|within
argument_list|)
argument_list|)
expr_stmt|;
name|rangeBounds
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|start
operator||
operator|(
literal|1L
operator|<<
name|nextRes
operator|)
argument_list|,
name|nextRes
argument_list|,
operator|!
name|within
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rangeBounds
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|start
argument_list|,
name|res
argument_list|,
operator|!
name|within
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|level
operator|<
name|DETAIL_LEVEL
operator|&&
name|cellIntersectsMBR
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
condition|)
block|{
name|computeRange
argument_list|(
name|start
argument_list|,
call|(
name|short
call|)
argument_list|(
name|res
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|computeMaxShift
specifier|protected
name|short
name|computeMaxShift
parameter_list|()
block|{
comment|// in this case a factor of 4 brings the detail level to ~0.002/0.001 degrees lon/lat respectively (or ~222m/111m)
return|return
name|GeoPointField
operator|.
name|PRECISION_STEP
operator|*
literal|4
return|;
block|}
comment|/**    * Determine whether the quad-cell crosses the shape    */
DECL|method|cellCrosses
specifier|protected
specifier|abstract
name|boolean
name|cellCrosses
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
function_decl|;
comment|/**    * Determine whether quad-cell is within the shape    */
DECL|method|cellWithin
specifier|protected
specifier|abstract
name|boolean
name|cellWithin
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
function_decl|;
comment|/**    * Default shape is a rectangle, so this returns the same as {@code cellIntersectsMBR}    */
DECL|method|cellIntersectsShape
specifier|protected
specifier|abstract
name|boolean
name|cellIntersectsShape
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
function_decl|;
comment|/**    * Primary driver for cells intersecting shape boundaries    */
DECL|method|cellIntersectsMBR
specifier|protected
name|boolean
name|cellIntersectsMBR
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoRelationUtils
operator|.
name|rectIntersects
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLon
argument_list|,
name|this
operator|.
name|maxLat
argument_list|)
return|;
block|}
comment|/**    * Return whether quad-cell contains the bounding box of this shape    */
DECL|method|cellContains
specifier|protected
name|boolean
name|cellContains
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoRelationUtils
operator|.
name|rectWithin
argument_list|(
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLon
argument_list|,
name|this
operator|.
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
return|;
block|}
DECL|method|boundaryTerm
specifier|public
name|boolean
name|boundaryTerm
parameter_list|()
block|{
if|if
condition|(
name|currentRange
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"GeoPointTermsEnum empty or not initialized"
argument_list|)
throw|;
block|}
return|return
name|currentRange
operator|.
name|boundary
return|;
block|}
DECL|method|nextRange
specifier|private
name|void
name|nextRange
parameter_list|()
block|{
name|currentRange
operator|=
name|rangeBounds
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|currentRange
operator|.
name|fillBytesRef
argument_list|(
name|currentCell
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextSeekTerm
specifier|protected
specifier|final
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
while|while
condition|(
operator|!
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|currentRange
operator|==
literal|null
condition|)
block|{
name|nextRange
argument_list|()
expr_stmt|;
block|}
comment|// if the new upper bound is before the term parameter, the sub-range is never a hit
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|currentCell
operator|.
name|get
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
name|nextRange
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
block|}
comment|// never seek backwards, so use current term if lower bound is smaller
return|return
operator|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|currentCell
operator|.
name|get
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
condition|?
name|term
else|:
name|currentCell
operator|.
name|get
argument_list|()
return|;
block|}
comment|// no more sub-range enums available
assert|assert
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
literal|null
return|;
block|}
comment|/**    * The two-phase query approach. {@link #nextSeekTerm} is called to obtain the next term that matches a numeric    * range of the bounding box. Those terms that pass the initial range filter are then compared against the    * decoded min/max latitude and longitude values of the bounding box only if the range is not a "boundary" range    * (e.g., a range that straddles the boundary of the bbox).    * @param term term for candidate document    * @return match status    */
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
comment|// validate value is in range
while|while
condition|(
name|currentCell
operator|==
literal|null
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|currentCell
operator|.
name|get
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
comment|// peek next sub-range, only seek if the current term is smaller than next lower bound
name|rangeBounds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|fillBytesRef
argument_list|(
name|this
operator|.
name|nextSubRange
argument_list|)
expr_stmt|;
if|if
condition|(
name|term
operator|.
name|compareTo
argument_list|(
name|this
operator|.
name|nextSubRange
operator|.
name|get
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
block|}
comment|// step forward to next range without seeking, as next range is less or equal current term
name|nextRange
argument_list|()
expr_stmt|;
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
DECL|method|postFilter
specifier|protected
specifier|abstract
name|boolean
name|postFilter
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|)
function_decl|;
comment|/**    * Internal class to represent a range along the space filling curve    */
DECL|class|Range
specifier|protected
specifier|final
class|class
name|Range
implements|implements
name|Comparable
argument_list|<
name|Range
argument_list|>
block|{
DECL|field|shift
specifier|final
name|short
name|shift
decl_stmt|;
DECL|field|start
specifier|final
name|long
name|start
decl_stmt|;
DECL|field|boundary
specifier|final
name|boolean
name|boundary
decl_stmt|;
DECL|method|Range
name|Range
parameter_list|(
specifier|final
name|long
name|lower
parameter_list|,
specifier|final
name|short
name|shift
parameter_list|,
name|boolean
name|boundary
parameter_list|)
block|{
name|this
operator|.
name|boundary
operator|=
name|boundary
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|shift
operator|=
name|shift
expr_stmt|;
block|}
comment|/**      * Encode as a BytesRef using a reusable object. This allows us to lazily create the BytesRef (which is      * quite expensive), only when we need it.      */
DECL|method|fillBytesRef
specifier|private
name|void
name|fillBytesRef
parameter_list|(
name|BytesRefBuilder
name|result
parameter_list|)
block|{
assert|assert
name|result
operator|!=
literal|null
assert|;
name|NumericUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|start
argument_list|,
name|shift
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Range
name|other
parameter_list|)
block|{
specifier|final
name|int
name|result
init|=
name|Short
operator|.
name|compare
argument_list|(
name|this
operator|.
name|shift
argument_list|,
name|other
operator|.
name|shift
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|this
operator|.
name|start
argument_list|,
name|other
operator|.
name|start
argument_list|)
return|;
block|}
return|return
name|result
return|;
block|}
block|}
block|}
end_class
end_unit
