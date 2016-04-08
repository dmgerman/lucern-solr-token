begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package
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
name|BitSet
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
begin_comment
comment|/**  * GeoConvexPolygon objects are generic building blocks of more complex structures.  * The only restrictions on these objects are: (1) they must be convex; (2) they must have  * a maximum extent no larger than PI.  Violating either one of these limits will  * cause the logic to fail.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|GeoConvexPolygon
class|class
name|GeoConvexPolygon
extends|extends
name|GeoBasePolygon
block|{
comment|/** The list of polygon points */
DECL|field|points
specifier|protected
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
decl_stmt|;
comment|/** A bitset describing, for each edge, whether it is internal or not */
DECL|field|isInternalEdges
specifier|protected
specifier|final
name|BitSet
name|isInternalEdges
decl_stmt|;
comment|/** The list of holes.  If a point is in the hole, it is *not* in the polygon */
DECL|field|holes
specifier|protected
specifier|final
name|List
argument_list|<
name|GeoPolygon
argument_list|>
name|holes
decl_stmt|;
comment|/** A list of edges */
DECL|field|edges
specifier|protected
name|SidedPlane
index|[]
name|edges
init|=
literal|null
decl_stmt|;
comment|/** The set of notable points for each edge */
DECL|field|notableEdgePoints
specifier|protected
name|GeoPoint
index|[]
index|[]
name|notableEdgePoints
init|=
literal|null
decl_stmt|;
comment|/** A point which is on the boundary of the polygon */
DECL|field|edgePoints
specifier|protected
name|GeoPoint
index|[]
name|edgePoints
init|=
literal|null
decl_stmt|;
comment|/** Set to true when the polygon is complete */
DECL|field|isDone
specifier|protected
name|boolean
name|isDone
init|=
literal|false
decl_stmt|;
comment|/** A bounds object for each sided plane */
DECL|field|eitherBounds
specifier|protected
name|Map
argument_list|<
name|SidedPlane
argument_list|,
name|Membership
argument_list|>
name|eitherBounds
init|=
literal|null
decl_stmt|;
comment|/**    * Create a convex polygon from a list of points.  The first point must be on the    * external edge.    *@param planetModel is the planet model.    *@param pointList is the list of points to create the polygon from.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|)
block|{
name|this
argument_list|(
name|planetModel
argument_list|,
name|pointList
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon from a list of points.  The first point must be on the    * external edge.    *@param planetModel is the planet model.    *@param pointList is the list of points to create the polygon from.    *@param holes is the list of GeoPolygon objects that describe holes in the complex polygon.  Null == no holes.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPolygon
argument_list|>
name|holes
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
name|this
operator|.
name|points
operator|=
name|pointList
expr_stmt|;
name|this
operator|.
name|holes
operator|=
name|holes
expr_stmt|;
name|this
operator|.
name|isInternalEdges
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
name|done
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon from a list of points, keeping track of which boundaries    * are internal.  This is used when creating a polygon as a building block for another shape.    *@param planetModel is the planet model.    *@param pointList is the set of points to create the polygon from.    *@param internalEdgeFlags is a bitset describing whether each edge is internal or not.    *@param returnEdgeInternal is true when the final return edge is an internal one.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|,
specifier|final
name|BitSet
name|internalEdgeFlags
parameter_list|,
specifier|final
name|boolean
name|returnEdgeInternal
parameter_list|)
block|{
name|this
argument_list|(
name|planetModel
argument_list|,
name|pointList
argument_list|,
literal|null
argument_list|,
name|internalEdgeFlags
argument_list|,
name|returnEdgeInternal
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon from a list of points, keeping track of which boundaries    * are internal.  This is used when creating a polygon as a building block for another shape.    *@param planetModel is the planet model.    *@param pointList is the set of points to create the polygon from.    *@param holes is the list of GeoPolygon objects that describe holes in the complex polygon.  Null == no holes.    *@param internalEdgeFlags is a bitset describing whether each edge is internal or not.    *@param returnEdgeInternal is true when the final return edge is an internal one.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|pointList
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPolygon
argument_list|>
name|holes
parameter_list|,
specifier|final
name|BitSet
name|internalEdgeFlags
parameter_list|,
specifier|final
name|boolean
name|returnEdgeInternal
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
name|this
operator|.
name|points
operator|=
name|pointList
expr_stmt|;
name|this
operator|.
name|holes
operator|=
name|holes
expr_stmt|;
name|this
operator|.
name|isInternalEdges
operator|=
name|internalEdgeFlags
expr_stmt|;
name|done
argument_list|(
name|returnEdgeInternal
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon, with a starting latitude and longitude.    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}    *@param planetModel is the planet model.    *@param startLatitude is the latitude of the first point.    *@param startLongitude is the longitude of the first point.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|startLatitude
parameter_list|,
specifier|final
name|double
name|startLongitude
parameter_list|)
block|{
name|this
argument_list|(
name|planetModel
argument_list|,
name|startLatitude
argument_list|,
name|startLongitude
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a convex polygon, with a starting latitude and longitude.    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}    *@param planetModel is the planet model.    *@param startLatitude is the latitude of the first point.    *@param startLongitude is the longitude of the first point.    *@param holes is the list of GeoPolygon objects that describe holes in the complex polygon.  Null == no holes.    */
DECL|method|GeoConvexPolygon
specifier|public
name|GeoConvexPolygon
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|startLatitude
parameter_list|,
specifier|final
name|double
name|startLongitude
parameter_list|,
specifier|final
name|List
argument_list|<
name|GeoPolygon
argument_list|>
name|holes
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
name|points
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|holes
operator|=
name|holes
expr_stmt|;
name|isInternalEdges
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|startLatitude
argument_list|,
name|startLongitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a point to the polygon.    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}    *    * @param latitude       is the latitude of the next point.    * @param longitude      is the longitude of the next point.    * @param isInternalEdge is true if the edge just added with this point should be considered "internal", and not    *                       intersected as part of the intersects() operation.    */
DECL|method|addPoint
specifier|public
name|void
name|addPoint
parameter_list|(
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|boolean
name|isInternalEdge
parameter_list|)
block|{
if|if
condition|(
name|isDone
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't call addPoint() if done() already called"
argument_list|)
throw|;
if|if
condition|(
name|isInternalEdge
condition|)
name|isInternalEdges
operator|.
name|set
argument_list|(
name|points
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish the polygon, by connecting the last added point with the starting point.    *@param isInternalReturnEdge is true if the return edge (back to start) is an internal one.    */
DECL|method|done
specifier|public
name|void
name|done
parameter_list|(
specifier|final
name|boolean
name|isInternalReturnEdge
parameter_list|)
block|{
if|if
condition|(
name|isDone
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't call done() more than once"
argument_list|)
throw|;
comment|// If fewer than 3 points, can't do it.
if|if
condition|(
name|points
operator|.
name|size
argument_list|()
operator|<
literal|3
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon needs at least three points."
argument_list|)
throw|;
if|if
condition|(
name|isInternalReturnEdge
condition|)
name|isInternalEdges
operator|.
name|set
argument_list|(
name|points
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|isDone
operator|=
literal|true
expr_stmt|;
comment|// Time to construct the planes.  If the polygon is truly convex, then any adjacent point
comment|// to a segment can provide an interior measurement.
name|edges
operator|=
operator|new
name|SidedPlane
index|[
name|points
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|notableEdgePoints
operator|=
operator|new
name|GeoPoint
index|[
name|points
operator|.
name|size
argument_list|()
index|]
index|[]
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
name|points
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|GeoPoint
name|start
init|=
name|points
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|end
init|=
name|points
operator|.
name|get
argument_list|(
name|legalIndex
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// We have to find the next point that is not on the plane between start and end.
comment|// If there is no such point, it's an error.
specifier|final
name|Plane
name|planeToFind
init|=
operator|new
name|Plane
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|int
name|endPointIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|legalIndex
argument_list|(
name|j
operator|+
name|i
operator|+
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|planeToFind
operator|.
name|evaluateIsZero
argument_list|(
name|points
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
condition|)
block|{
name|endPointIndex
operator|=
name|index
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|endPointIndex
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon points are all coplanar"
argument_list|)
throw|;
block|}
specifier|final
name|GeoPoint
name|check
init|=
name|points
operator|.
name|get
argument_list|(
name|endPointIndex
argument_list|)
decl_stmt|;
specifier|final
name|SidedPlane
name|sp
init|=
operator|new
name|SidedPlane
argument_list|(
name|check
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
decl_stmt|;
comment|//System.out.println("Created edge "+sp+" using start="+start+" end="+end+" check="+check);
name|edges
index|[
name|i
index|]
operator|=
name|sp
expr_stmt|;
name|notableEdgePoints
index|[
name|i
index|]
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|start
block|,
name|end
block|}
expr_stmt|;
block|}
comment|// In order to naively confirm that the polygon is convex, I would need to
comment|// check every edge, and verify that every point (other than the edge endpoints)
comment|// is within the edge's sided plane.  This is an order n^2 operation.  That's still
comment|// not wrong, though, because everything else about polygons has a similar cost.
for|for
control|(
name|int
name|edgeIndex
init|=
literal|0
init|;
name|edgeIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|edgeIndex
operator|++
control|)
block|{
specifier|final
name|SidedPlane
name|edge
init|=
name|edges
index|[
name|edgeIndex
index|]
decl_stmt|;
for|for
control|(
name|int
name|pointIndex
init|=
literal|0
init|;
name|pointIndex
operator|<
name|points
operator|.
name|size
argument_list|()
condition|;
name|pointIndex
operator|++
control|)
block|{
if|if
condition|(
name|pointIndex
operator|!=
name|edgeIndex
operator|&&
name|pointIndex
operator|!=
name|legalIndex
argument_list|(
name|edgeIndex
operator|+
literal|1
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|points
operator|.
name|get
argument_list|(
name|pointIndex
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Polygon is not convex: Point "
operator|+
name|points
operator|.
name|get
argument_list|(
name|pointIndex
argument_list|)
operator|+
literal|" Edge "
operator|+
name|edge
argument_list|)
throw|;
block|}
block|}
block|}
comment|// For each edge, create a bounds object.
name|eitherBounds
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|edges
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
name|eitherBounds
operator|.
name|put
argument_list|(
name|edge
argument_list|,
operator|new
name|EitherBound
argument_list|(
name|edge
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Pick an edge point arbitrarily
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|points
operator|.
name|get
argument_list|(
literal|0
argument_list|)
block|}
expr_stmt|;
block|}
comment|/** Compute a legal point index from a possibly illegal one, that may have wrapped.    *@param index is the index.    *@return the normalized index.    */
DECL|method|legalIndex
specifier|protected
name|int
name|legalIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
while|while
condition|(
name|index
operator|>=
name|points
operator|.
name|size
argument_list|()
condition|)
name|index
operator|-=
name|points
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|index
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
if|if
condition|(
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
if|if
condition|(
name|holes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|GeoPolygon
name|polygon
range|:
name|holes
control|)
block|{
if|if
condition|(
name|polygon
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|public
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|edgePoints
return|;
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|p
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|notablePoints
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
comment|//System.err.println("Checking for polygon intersection with plane "+p+"...");
for|for
control|(
name|int
name|edgeIndex
init|=
literal|0
init|;
name|edgeIndex
operator|<
name|edges
operator|.
name|length
condition|;
name|edgeIndex
operator|++
control|)
block|{
specifier|final
name|SidedPlane
name|edge
init|=
name|edges
index|[
name|edgeIndex
index|]
decl_stmt|;
specifier|final
name|GeoPoint
index|[]
name|points
init|=
name|this
operator|.
name|notableEdgePoints
index|[
name|edgeIndex
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|isInternalEdges
operator|.
name|get
argument_list|(
name|edgeIndex
argument_list|)
condition|)
block|{
if|if
condition|(
name|edge
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|p
argument_list|,
name|notablePoints
argument_list|,
name|points
argument_list|,
name|bounds
argument_list|,
name|eitherBounds
operator|.
name|get
argument_list|(
name|edge
argument_list|)
argument_list|)
condition|)
block|{
comment|//System.err.println(" intersects!");
return|return
literal|true
return|;
block|}
block|}
block|}
if|if
condition|(
name|holes
operator|!=
literal|null
condition|)
block|{
comment|// Each hole needs to be looked at for intersection too, since a shape can be entirely within the hole
for|for
control|(
specifier|final
name|GeoPolygon
name|hole
range|:
name|holes
control|)
block|{
if|if
condition|(
name|hole
operator|.
name|intersects
argument_list|(
name|p
argument_list|,
name|notablePoints
argument_list|,
name|bounds
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|//System.err.println(" no intersection");
return|return
literal|false
return|;
block|}
comment|/** A membership implementation representing polygon edges that all must apply.    */
DECL|class|EitherBound
specifier|protected
class|class
name|EitherBound
implements|implements
name|Membership
block|{
DECL|field|exception
specifier|protected
specifier|final
name|SidedPlane
name|exception
decl_stmt|;
comment|/** Constructor.       * @param exception is the one plane to exclude from the check.       */
DECL|method|EitherBound
specifier|public
name|EitherBound
parameter_list|(
specifier|final
name|SidedPlane
name|exception
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
if|if
condition|(
name|edge
operator|!=
name|exception
operator|&&
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
if|if
condition|(
name|edge
operator|!=
name|exception
operator|&&
operator|!
name|edge
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
comment|// Add all the points
for|for
control|(
specifier|final
name|GeoPoint
name|point
range|:
name|points
control|)
block|{
name|bounds
operator|.
name|addPoint
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
comment|// Add planes with membership.
for|for
control|(
specifier|final
name|SidedPlane
name|edge
range|:
name|edges
control|)
block|{
name|bounds
operator|.
name|addPlane
argument_list|(
name|planetModel
argument_list|,
name|edge
argument_list|,
name|eitherBounds
operator|.
name|get
argument_list|(
name|edge
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|outsideDistance
specifier|protected
name|double
name|outsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
name|double
name|minimumDistance
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
specifier|final
name|GeoPoint
name|edgePoint
range|:
name|points
control|)
block|{
specifier|final
name|double
name|newDist
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|edgePoint
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDist
operator|<
name|minimumDistance
condition|)
block|{
name|minimumDistance
operator|=
name|newDist
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|SidedPlane
name|edgePlane
range|:
name|edges
control|)
block|{
specifier|final
name|double
name|newDist
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|edgePlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|eitherBounds
operator|.
name|get
argument_list|(
name|edgePlane
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDist
operator|<
name|minimumDistance
condition|)
block|{
name|minimumDistance
operator|=
name|newDist
expr_stmt|;
block|}
block|}
return|return
name|minimumDistance
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|GeoConvexPolygon
operator|)
condition|)
return|return
literal|false
return|;
name|GeoConvexPolygon
name|other
init|=
operator|(
name|GeoConvexPolygon
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|other
operator|.
name|isInternalEdges
operator|.
name|equals
argument_list|(
name|isInternalEdges
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|other
operator|.
name|holes
operator|!=
literal|null
operator|||
name|holes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|holes
operator|==
literal|null
operator|||
name|holes
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|other
operator|.
name|holes
operator|.
name|equals
argument_list|(
name|holes
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
operator|(
name|other
operator|.
name|points
operator|.
name|equals
argument_list|(
name|points
argument_list|)
operator|)
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|points
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|holes
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|holes
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|result
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
literal|"GeoConvexPolygon: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", points="
operator|+
name|points
operator|+
literal|", internalEdges="
operator|+
name|isInternalEdges
operator|+
operator|(
operator|(
name|holes
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
literal|", holes="
operator|+
name|holes
operator|)
operator|+
literal|"}"
return|;
block|}
block|}
end_class
end_unit
