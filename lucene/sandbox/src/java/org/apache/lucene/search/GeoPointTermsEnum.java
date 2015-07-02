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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|Attribute
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
name|AttributeImpl
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
name|AttributeReflector
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
name|AttributeSource
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
comment|/**  * computes all ranges along a space-filling curve that represents  * the given bounding box and enumerates all terms contained within those ranges  */
end_comment
begin_class
DECL|class|GeoPointTermsEnum
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
specifier|private
name|Range
name|currentRange
decl_stmt|;
DECL|field|currentLowerBound
DECL|field|currentUpperBound
specifier|private
name|BytesRef
name|currentLowerBound
decl_stmt|,
name|currentUpperBound
decl_stmt|;
DECL|field|rangesAtt
specifier|private
specifier|final
name|ComputedRangesAttribute
name|rangesAtt
decl_stmt|;
DECL|field|rangeBounds
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Range
argument_list|>
name|rangeBounds
decl_stmt|;
DECL|field|DETAIL_LEVEL
specifier|private
specifier|static
specifier|final
name|short
name|DETAIL_LEVEL
init|=
literal|16
decl_stmt|;
DECL|method|GeoPointTermsEnum
name|GeoPointTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|,
name|AttributeSource
name|atts
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
name|this
operator|.
name|rangesAtt
operator|=
name|atts
operator|.
name|addAttribute
argument_list|(
name|ComputedRangesAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|rangeBounds
operator|=
name|rangesAtt
operator|.
name|ranges
argument_list|()
expr_stmt|;
if|if
condition|(
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|Collections
operator|.
name|sort
argument_list|(
name|rangeBounds
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|long
name|upperMax
init|=
name|term
operator||
operator|(
operator|(
literal|0x1L
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
decl_stmt|;
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
literal|62
operator|-
name|res
operator|>>>
literal|1
argument_list|)
decl_stmt|;
comment|// if cell is within and a factor of the precision step, add the range
comment|// if cell cellCrosses
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
name|cellCrosses
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
name|rangeBounds
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|res
argument_list|,
name|level
argument_list|,
operator|!
name|within
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|level
operator|<=
name|DETAIL_LEVEL
operator|&&
name|cellIntersects
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
DECL|method|cellCrosses
specifier|protected
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
block|{
return|return
name|GeoUtils
operator|.
name|rectCrosses
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
DECL|method|cellWithin
specifier|protected
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
block|{
return|return
name|GeoUtils
operator|.
name|rectWithin
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
DECL|method|cellIntersects
specifier|protected
name|boolean
name|cellIntersects
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
name|GeoUtils
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
name|removeFirst
argument_list|()
expr_stmt|;
name|currentLowerBound
operator|=
name|currentRange
operator|.
name|lower
expr_stmt|;
assert|assert
name|currentUpperBound
operator|==
literal|null
operator|||
name|currentUpperBound
operator|.
name|compareTo
argument_list|(
name|currentRange
operator|.
name|lower
argument_list|)
operator|<=
literal|0
operator|:
literal|"The current upper bound must be<= the new lower bound"
assert|;
name|currentUpperBound
operator|=
name|currentRange
operator|.
name|upper
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
name|currentUpperBound
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
name|currentLowerBound
argument_list|)
operator|>
literal|0
operator|)
condition|?
name|term
else|:
name|currentLowerBound
return|;
block|}
comment|// no more sub-range enums available
assert|assert
name|rangeBounds
operator|.
name|isEmpty
argument_list|()
assert|;
name|currentLowerBound
operator|=
name|currentUpperBound
operator|=
literal|null
expr_stmt|;
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
name|currentUpperBound
operator|==
literal|null
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|currentUpperBound
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
return|return
name|AcceptStatus
operator|.
name|END
return|;
comment|// peek next sub-range, only seek if the current term is smaller than next lower bound
if|if
condition|(
name|term
operator|.
name|compareTo
argument_list|(
name|rangeBounds
operator|.
name|getFirst
argument_list|()
operator|.
name|lower
argument_list|)
operator|<
literal|0
condition|)
return|return
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
comment|// step forward to next range without seeking, as next lower range bound is less or equal current term
name|nextRange
argument_list|()
expr_stmt|;
block|}
comment|// final-filter boundary ranges by bounding box
if|if
condition|(
name|currentRange
operator|.
name|boundary
condition|)
block|{
specifier|final
name|long
name|val
init|=
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lon
init|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|val
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lat
init|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|GeoUtils
operator|.
name|bboxContains
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
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
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
DECL|interface|ComputedRangesAttribute
specifier|public
specifier|static
interface|interface
name|ComputedRangesAttribute
extends|extends
name|Attribute
block|{
DECL|method|ranges
specifier|public
name|LinkedList
argument_list|<
name|Range
argument_list|>
name|ranges
parameter_list|()
function_decl|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|ComputedRangesAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|ComputedRangesAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|ComputedRangesAttribute
block|{
DECL|field|rangeBounds
specifier|public
specifier|final
name|LinkedList
argument_list|<
name|Range
argument_list|>
name|rangeBounds
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|ranges
specifier|public
name|LinkedList
argument_list|<
name|Range
argument_list|>
name|ranges
parameter_list|()
block|{
return|return
name|rangeBounds
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|rangeBounds
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|rangeBounds
operator|.
name|hashCode
argument_list|()
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
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|ComputedRangesAttributeImpl
operator|)
condition|)
return|return
literal|false
return|;
return|return
name|rangeBounds
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ComputedRangesAttributeImpl
operator|)
name|other
operator|)
operator|.
name|rangeBounds
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Range
argument_list|>
name|targetRanges
init|=
operator|(
operator|(
name|ComputedRangesAttribute
operator|)
name|target
operator|)
operator|.
name|ranges
argument_list|()
decl_stmt|;
name|targetRanges
operator|.
name|clear
argument_list|()
expr_stmt|;
name|targetRanges
operator|.
name|addAll
argument_list|(
name|rangeBounds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|AttributeImpl
name|clone
parameter_list|()
block|{
name|ComputedRangesAttributeImpl
name|c
init|=
operator|(
name|ComputedRangesAttributeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
empty_stmt|;
name|copyTo
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|reflector
operator|.
name|reflect
argument_list|(
name|ComputedRangesAttribute
operator|.
name|class
argument_list|,
literal|"rangeBounds"
argument_list|,
name|rangeBounds
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Internal class to represent a range along the space filling curve    */
DECL|class|Range
specifier|private
specifier|final
class|class
name|Range
implements|implements
name|Comparable
argument_list|<
name|Range
argument_list|>
block|{
DECL|field|lower
specifier|final
name|BytesRef
name|lower
decl_stmt|;
DECL|field|upper
specifier|final
name|BytesRef
name|upper
decl_stmt|;
DECL|field|level
specifier|final
name|short
name|level
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
name|long
name|upper
parameter_list|,
specifier|final
name|short
name|res
parameter_list|,
specifier|final
name|short
name|level
parameter_list|,
name|boolean
name|boundary
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|boundary
operator|=
name|boundary
expr_stmt|;
name|BytesRefBuilder
name|brb
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCodedBytes
argument_list|(
name|lower
argument_list|,
name|boundary
condition|?
literal|0
else|:
name|res
argument_list|,
name|brb
argument_list|)
expr_stmt|;
name|this
operator|.
name|lower
operator|=
name|brb
operator|.
name|get
argument_list|()
expr_stmt|;
name|NumericUtils
operator|.
name|longToPrefixCodedBytes
argument_list|(
name|upper
argument_list|,
name|boundary
condition|?
literal|0
else|:
name|res
argument_list|,
operator|(
name|brb
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|upper
operator|=
name|brb
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
name|Range
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|lower
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|lower
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
