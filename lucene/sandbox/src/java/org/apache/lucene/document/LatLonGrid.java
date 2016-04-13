begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|Polygon
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
name|PointValues
operator|.
name|Relation
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|decodeLatitude
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
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|decodeLongitude
import|;
end_import
begin_comment
comment|/**  * This is a temporary hack, until some polygon methods have better performance!  *<p>  * When this file is removed then we have made good progress! In general we don't call  * the point-in-polygon algorithm that much, because of how BKD divides up the data. But  * today the method is very slow (general to all polygons, linear with the number of vertices).  * At the same time polygon-rectangle relation operations are also slow in the same way, this  * just really ensures they are the bottleneck by removing most of the point-in-polygon calls.  *<p>  * See the "grid" algorithm description here: http://erich.realtimerendering.com/ptinpoly/  * A few differences:  *<ul>  *<li> We work in an integer encoding, so edge cases are simpler.  *<li> We classify each grid cell as "contained", "not contained", or "don't know".  *<li> We form a grid over a potentially complex multipolygon with holes.  *<li> Construction is less efficient because we do not do anything "smart" such  *        as following polygon edges.   *<li> Instead we construct a baby tree to reduce the number of relation operations,  *        which are currently expensive.  *</ul>  */
end_comment
begin_comment
comment|// TODO: just make a more proper tree (maybe in-ram BKD)? then we can answer most
end_comment
begin_comment
comment|// relational operations as rectangle<-> rectangle relations in integer space in log(n) time..
end_comment
begin_class
DECL|class|LatLonGrid
specifier|final
class|class
name|LatLonGrid
block|{
comment|// must be a power of two!
DECL|field|GRID_SIZE
specifier|static
specifier|final
name|int
name|GRID_SIZE
init|=
literal|1
operator|<<
literal|5
decl_stmt|;
DECL|field|minLat
specifier|final
name|int
name|minLat
decl_stmt|;
DECL|field|maxLat
specifier|final
name|int
name|maxLat
decl_stmt|;
DECL|field|minLon
specifier|final
name|int
name|minLon
decl_stmt|;
DECL|field|maxLon
specifier|final
name|int
name|maxLon
decl_stmt|;
comment|// TODO: something more efficient than parallel bitsets? maybe one bitset?
DECL|field|haveAnswer
specifier|final
name|FixedBitSet
name|haveAnswer
init|=
operator|new
name|FixedBitSet
argument_list|(
name|GRID_SIZE
operator|*
name|GRID_SIZE
argument_list|)
decl_stmt|;
DECL|field|answer
specifier|final
name|FixedBitSet
name|answer
init|=
operator|new
name|FixedBitSet
argument_list|(
name|GRID_SIZE
operator|*
name|GRID_SIZE
argument_list|)
decl_stmt|;
DECL|field|latPerCell
specifier|final
name|long
name|latPerCell
decl_stmt|;
DECL|field|lonPerCell
specifier|final
name|long
name|lonPerCell
decl_stmt|;
DECL|field|polygons
specifier|final
name|Polygon
index|[]
name|polygons
decl_stmt|;
DECL|method|LatLonGrid
name|LatLonGrid
parameter_list|(
name|int
name|minLat
parameter_list|,
name|int
name|maxLat
parameter_list|,
name|int
name|minLon
parameter_list|,
name|int
name|maxLon
parameter_list|,
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
name|this
operator|.
name|minLat
operator|=
name|minLat
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|maxLat
expr_stmt|;
name|this
operator|.
name|minLon
operator|=
name|minLon
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|maxLon
expr_stmt|;
name|this
operator|.
name|polygons
operator|=
name|polygons
expr_stmt|;
if|if
condition|(
name|minLon
operator|>
name|maxLon
condition|)
block|{
comment|// maybe make 2 grids if you want this?
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Grid cannot cross the dateline"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minLat
operator|>
name|maxLat
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bogus grid"
argument_list|)
throw|;
block|}
name|long
name|latitudeRange
init|=
name|maxLat
operator|-
operator|(
name|long
operator|)
name|minLat
decl_stmt|;
name|long
name|longitudeRange
init|=
name|maxLon
operator|-
operator|(
name|long
operator|)
name|minLon
decl_stmt|;
comment|// if the range is too small, we can't divide it up in our grid nicely.
comment|// in this case of a tiny polygon, we just make an empty grid instead of complicating/slowing down code.
specifier|final
name|long
name|minRange
init|=
operator|(
name|GRID_SIZE
operator|-
literal|1
operator|)
operator|*
operator|(
name|GRID_SIZE
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|latitudeRange
operator|<
name|minRange
operator|||
name|longitudeRange
operator|<
name|minRange
condition|)
block|{
name|latPerCell
operator|=
name|lonPerCell
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
comment|// we spill over the edge of the bounding box in each direction a bit,
comment|// but it prevents edge case bugs.
name|latPerCell
operator|=
name|latitudeRange
operator|/
operator|(
name|GRID_SIZE
operator|-
literal|1
operator|)
expr_stmt|;
name|lonPerCell
operator|=
name|longitudeRange
operator|/
operator|(
name|GRID_SIZE
operator|-
literal|1
operator|)
expr_stmt|;
name|fill
argument_list|(
name|polygons
argument_list|,
literal|0
argument_list|,
name|GRID_SIZE
argument_list|,
literal|0
argument_list|,
name|GRID_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** fills a 2D range of grid cells [minLatIndex .. maxLatIndex) X [minLonIndex .. maxLonIndex) */
DECL|method|fill
name|void
name|fill
parameter_list|(
name|Polygon
index|[]
name|polygons
parameter_list|,
name|int
name|minLatIndex
parameter_list|,
name|int
name|maxLatIndex
parameter_list|,
name|int
name|minLonIndex
parameter_list|,
name|int
name|maxLonIndex
parameter_list|)
block|{
comment|// grid cells at the edge of the bounding box are typically smaller than normal, because we spill over.
name|long
name|cellMinLat
init|=
name|minLat
operator|+
operator|(
name|minLatIndex
operator|*
name|latPerCell
operator|)
decl_stmt|;
name|long
name|cellMaxLat
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxLat
argument_list|,
name|minLat
operator|+
operator|(
name|maxLatIndex
operator|*
name|latPerCell
operator|)
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|cellMinLon
init|=
name|minLon
operator|+
operator|(
name|minLonIndex
operator|*
name|lonPerCell
operator|)
decl_stmt|;
name|long
name|cellMaxLon
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxLon
argument_list|,
name|minLon
operator|+
operator|(
name|maxLonIndex
operator|*
name|lonPerCell
operator|)
operator|-
literal|1
argument_list|)
decl_stmt|;
assert|assert
name|cellMinLat
operator|<=
name|maxLat
operator|&&
name|cellMinLon
operator|<=
name|maxLon
assert|;
assert|assert
name|cellMaxLat
operator|>=
name|cellMinLat
assert|;
assert|assert
name|cellMaxLon
operator|>=
name|cellMinLon
assert|;
name|Relation
name|relation
init|=
name|Polygon
operator|.
name|relate
argument_list|(
name|polygons
argument_list|,
name|decodeLatitude
argument_list|(
operator|(
name|int
operator|)
name|cellMinLat
argument_list|)
argument_list|,
name|decodeLatitude
argument_list|(
operator|(
name|int
operator|)
name|cellMaxLat
argument_list|)
argument_list|,
name|decodeLongitude
argument_list|(
operator|(
name|int
operator|)
name|cellMinLon
argument_list|)
argument_list|,
name|decodeLongitude
argument_list|(
operator|(
name|int
operator|)
name|cellMaxLon
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|relation
operator|!=
name|Relation
operator|.
name|CELL_CROSSES_QUERY
condition|)
block|{
comment|// we know the answer for this region, fill the cell range
for|for
control|(
name|int
name|i
init|=
name|minLatIndex
init|;
name|i
operator|<
name|maxLatIndex
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|minLonIndex
init|;
name|j
operator|<
name|maxLonIndex
condition|;
name|j
operator|++
control|)
block|{
name|int
name|index
init|=
name|i
operator|*
name|GRID_SIZE
operator|+
name|j
decl_stmt|;
assert|assert
name|haveAnswer
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|==
literal|false
assert|;
name|haveAnswer
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|relation
operator|==
name|Relation
operator|.
name|CELL_INSIDE_QUERY
condition|)
block|{
name|answer
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|minLatIndex
operator|==
name|maxLatIndex
operator|-
literal|1
condition|)
block|{
comment|// nothing more to do: this is a single grid cell (leaf node) and
comment|// is an edge case for the polygon.
block|}
else|else
block|{
comment|// grid range crosses our polygon, keep recursing.
name|int
name|midLatIndex
init|=
operator|(
name|minLatIndex
operator|+
name|maxLatIndex
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|midLonIndex
init|=
operator|(
name|minLonIndex
operator|+
name|maxLonIndex
operator|)
operator|>>>
literal|1
decl_stmt|;
name|fill
argument_list|(
name|polygons
argument_list|,
name|minLatIndex
argument_list|,
name|midLatIndex
argument_list|,
name|minLonIndex
argument_list|,
name|midLonIndex
argument_list|)
expr_stmt|;
name|fill
argument_list|(
name|polygons
argument_list|,
name|minLatIndex
argument_list|,
name|midLatIndex
argument_list|,
name|midLonIndex
argument_list|,
name|maxLonIndex
argument_list|)
expr_stmt|;
name|fill
argument_list|(
name|polygons
argument_list|,
name|midLatIndex
argument_list|,
name|maxLatIndex
argument_list|,
name|minLonIndex
argument_list|,
name|midLonIndex
argument_list|)
expr_stmt|;
name|fill
argument_list|(
name|polygons
argument_list|,
name|midLatIndex
argument_list|,
name|maxLatIndex
argument_list|,
name|midLonIndex
argument_list|,
name|maxLonIndex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns true if inside one of our polygons, false otherwise */
DECL|method|contains
name|boolean
name|contains
parameter_list|(
name|int
name|latitude
parameter_list|,
name|int
name|longitude
parameter_list|)
block|{
comment|// first see if the grid knows the answer
name|int
name|index
init|=
name|index
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
comment|// outside of bounding box range
block|}
elseif|else
if|if
condition|(
name|haveAnswer
operator|.
name|get
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
name|answer
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|// the grid is unsure (boundary): do a real test.
name|double
name|docLatitude
init|=
name|decodeLatitude
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
name|double
name|docLongitude
init|=
name|decodeLongitude
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
return|return
name|Polygon
operator|.
name|contains
argument_list|(
name|polygons
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
return|;
block|}
comment|/** Returns grid index of lat/lon, or -1 if the value is outside of the bounding box. */
DECL|method|index
specifier|private
name|int
name|index
parameter_list|(
name|int
name|latitude
parameter_list|,
name|int
name|longitude
parameter_list|)
block|{
if|if
condition|(
name|latitude
argument_list|<
name|minLat
operator|||
name|latitude
argument_list|>
name|maxLat
operator|||
name|longitude
argument_list|<
name|minLon
operator|||
name|longitude
argument_list|>
name|maxLon
condition|)
block|{
return|return
operator|-
literal|1
return|;
comment|// outside of bounding box range
block|}
name|long
name|latRel
init|=
name|latitude
operator|-
operator|(
name|long
operator|)
name|minLat
decl_stmt|;
name|long
name|lonRel
init|=
name|longitude
operator|-
operator|(
name|long
operator|)
name|minLon
decl_stmt|;
name|int
name|latIndex
init|=
call|(
name|int
call|)
argument_list|(
name|latRel
operator|/
name|latPerCell
argument_list|)
decl_stmt|;
assert|assert
name|latIndex
operator|<
name|GRID_SIZE
assert|;
name|int
name|lonIndex
init|=
call|(
name|int
call|)
argument_list|(
name|lonRel
operator|/
name|lonPerCell
argument_list|)
decl_stmt|;
assert|assert
name|lonIndex
operator|<
name|GRID_SIZE
assert|;
return|return
name|latIndex
operator|*
name|GRID_SIZE
operator|+
name|lonIndex
return|;
block|}
block|}
end_class
end_unit
