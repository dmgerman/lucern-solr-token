begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
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
name|context
operator|.
name|SpatialContextFactory
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
name|distance
operator|.
name|DistanceUtils
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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|impl
operator|.
name|RectangleImpl
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
name|SpatialMatchConcern
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
name|RandomSpatialOpStrategyTestCase
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
name|util
operator|.
name|ShapeAreaValueSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_class
DECL|class|TestBBoxStrategy
specifier|public
class|class
name|TestBBoxStrategy
extends|extends
name|RandomSpatialOpStrategyTestCase
block|{
annotation|@
name|Override
DECL|method|randomIndexedShape
specifier|protected
name|Shape
name|randomIndexedShape
parameter_list|()
block|{
name|Rectangle
name|world
init|=
name|ctx
operator|.
name|getWorldBounds
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
comment|// increased chance of getting one of these
return|return
name|world
return|;
name|int
name|worldWidth
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|world
operator|.
name|getWidth
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|deltaLeft
init|=
name|nextIntInclusive
argument_list|(
name|worldWidth
argument_list|)
decl_stmt|;
name|int
name|deltaRight
init|=
name|nextIntInclusive
argument_list|(
name|worldWidth
operator|-
name|deltaLeft
argument_list|)
decl_stmt|;
name|int
name|worldHeight
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|world
operator|.
name|getHeight
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|deltaTop
init|=
name|nextIntInclusive
argument_list|(
name|worldHeight
argument_list|)
decl_stmt|;
name|int
name|deltaBottom
init|=
name|nextIntInclusive
argument_list|(
name|worldHeight
operator|-
name|deltaTop
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|isGeo
argument_list|()
operator|&&
operator|(
name|deltaLeft
operator|!=
literal|0
operator|||
name|deltaRight
operator|!=
literal|0
operator|)
condition|)
block|{
comment|//if geo& doesn't world-wrap, we shift randomly to potentially cross dateline
name|int
name|shift
init|=
name|nextIntInclusive
argument_list|(
literal|360
argument_list|)
decl_stmt|;
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|world
operator|.
name|getMinX
argument_list|()
operator|+
name|deltaLeft
operator|+
name|shift
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|world
operator|.
name|getMaxX
argument_list|()
operator|-
name|deltaRight
operator|+
name|shift
argument_list|)
argument_list|,
name|world
operator|.
name|getMinY
argument_list|()
operator|+
name|deltaBottom
argument_list|,
name|world
operator|.
name|getMaxY
argument_list|()
operator|-
name|deltaTop
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|world
operator|.
name|getMinX
argument_list|()
operator|+
name|deltaLeft
argument_list|,
name|world
operator|.
name|getMaxX
argument_list|()
operator|-
name|deltaRight
argument_list|,
name|world
operator|.
name|getMinY
argument_list|()
operator|+
name|deltaBottom
argument_list|,
name|world
operator|.
name|getMaxY
argument_list|()
operator|-
name|deltaTop
argument_list|)
return|;
block|}
block|}
comment|/** next int, inclusive, rounds to multiple of 10 if given evenly divisible. */
DECL|method|nextIntInclusive
specifier|private
name|int
name|nextIntInclusive
parameter_list|(
name|int
name|toInc
parameter_list|)
block|{
specifier|final
name|int
name|DIVIS
init|=
literal|10
decl_stmt|;
if|if
condition|(
name|toInc
operator|%
name|DIVIS
operator|==
literal|0
condition|)
block|{
return|return
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|toInc
operator|/
name|DIVIS
operator|+
literal|1
argument_list|)
operator|*
name|DIVIS
return|;
block|}
else|else
block|{
return|return
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|toInc
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|randomQueryShape
specifier|protected
name|Shape
name|randomQueryShape
parameter_list|()
block|{
return|return
name|randomIndexedShape
argument_list|()
return|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|20
argument_list|)
DECL|method|testOperations
specifier|public
name|void
name|testOperations
parameter_list|()
throws|throws
name|IOException
block|{
comment|//setup
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|//75% of the time choose geo (more interesting to test)
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
block|}
else|else
block|{
name|SpatialContextFactory
name|factory
init|=
operator|new
name|SpatialContextFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|geo
operator|=
literal|false
expr_stmt|;
name|factory
operator|.
name|worldBounds
operator|=
operator|new
name|RectangleImpl
argument_list|(
operator|-
literal|300
argument_list|,
literal|300
argument_list|,
operator|-
literal|100
argument_list|,
literal|100
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|factory
operator|.
name|newSpatialContext
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|strategy
operator|=
operator|new
name|BBoxStrategy
argument_list|(
name|ctx
argument_list|,
literal|"bbox"
argument_list|)
expr_stmt|;
for|for
control|(
name|SpatialOperation
name|operation
range|:
name|SpatialOperation
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|operation
operator|==
name|SpatialOperation
operator|.
name|Overlaps
condition|)
continue|continue;
comment|//unsupported
name|testOperationRandomShapes
argument_list|(
name|operation
argument_list|)
expr_stmt|;
name|deleteAll
argument_list|()
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIntersectsBugDatelineEdge
specifier|public
name|void
name|testIntersectsBugDatelineEdge
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|testOperation
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|160
argument_list|,
literal|180
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
operator|-
literal|160
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntersectsWorldDatelineEdge
specifier|public
name|void
name|testIntersectsWorldDatelineEdge
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|testOperation
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
literal|180
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|180
argument_list|,
literal|180
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithinBugDatelineEdge
specifier|public
name|void
name|testWithinBugDatelineEdge
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|testOperation
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|180
argument_list|,
literal|180
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
operator|-
literal|100
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainsBugDatelineEdge
specifier|public
name|void
name|testContainsBugDatelineEdge
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|testOperation
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
operator|-
literal|150
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|Contains
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|180
argument_list|,
literal|180
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWorldContainsXDL
specifier|public
name|void
name|testWorldContainsXDL
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|testOperation
argument_list|(
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|180
argument_list|,
literal|180
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
name|SpatialOperation
operator|.
name|Contains
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|170
argument_list|,
operator|-
literal|170
argument_list|,
operator|-
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setupGeo
specifier|private
name|void
name|setupGeo
parameter_list|()
block|{
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|BBoxStrategy
argument_list|(
name|ctx
argument_list|,
literal|"bbox"
argument_list|)
expr_stmt|;
block|}
comment|// OLD STATIC TESTS (worthless?)
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"Overlaps not supported"
argument_list|)
DECL|method|testBasicOperaions
specifier|public
name|void
name|testBasicOperaions
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_SIMPLE_BBOX
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|EXACT
argument_list|,
name|QTEST_Simple_Queries_BBox
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatesBBox
specifier|public
name|void
name|testStatesBBox
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_STATES_BBOX
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_States_IsWithin_BBox
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_States_Intersects_BBox
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCitiesIntersectsBBox
specifier|public
name|void
name|testCitiesIntersectsBBox
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_WORLD_CITIES_POINTS
argument_list|)
expr_stmt|;
name|executeQueries
argument_list|(
name|SpatialMatchConcern
operator|.
name|FILTER
argument_list|,
name|QTEST_Cities_Intersects_BBox
argument_list|)
expr_stmt|;
block|}
comment|/* Convert DATA_WORLD_CITIES_POINTS to bbox */
annotation|@
name|Override
DECL|method|convertShapeFromGetDocuments
specifier|protected
name|Shape
name|convertShapeFromGetDocuments
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
return|return
name|shape
operator|.
name|getBoundingBox
argument_list|()
return|;
block|}
DECL|method|testOverlapRatio
specifier|public
name|void
name|testOverlapRatio
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
comment|//Simply assert null shape results in 0
name|adoc
argument_list|(
literal|"999"
argument_list|,
operator|(
name|Shape
operator|)
literal|null
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|BBoxStrategy
name|bboxStrategy
init|=
operator|(
name|BBoxStrategy
operator|)
name|strategy
decl_stmt|;
name|checkValueSource
argument_list|(
name|bboxStrategy
operator|.
name|makeOverlapRatioValueSource
argument_list|(
name|randomRectangle
argument_list|()
argument_list|,
literal|0.0
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0f
block|}
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
comment|//we test raw BBoxOverlapRatioValueSource without actual indexing
for|for
control|(
name|int
name|SHIFT
init|=
literal|0
init|;
name|SHIFT
operator|<
literal|360
condition|;
name|SHIFT
operator|+=
literal|10
control|)
block|{
name|Rectangle
name|queryBox
init|=
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|40
argument_list|,
operator|-
literal|20
argument_list|,
literal|20
argument_list|,
name|SHIFT
argument_list|)
decl_stmt|;
comment|//40x40, 1600 area
specifier|final
name|boolean
name|MSL
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|double
name|minSideLength
init|=
name|MSL
condition|?
literal|0.1
else|:
literal|0.0
decl_stmt|;
name|BBoxOverlapRatioValueSource
name|sim
init|=
operator|new
name|BBoxOverlapRatioValueSource
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|queryBox
argument_list|,
literal|0.5
argument_list|,
name|minSideLength
argument_list|)
decl_stmt|;
name|int
name|nudge
init|=
name|SHIFT
operator|==
literal|0
condition|?
literal|0
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|*
literal|10
operator|-
literal|10
decl_stmt|;
comment|//-10, 0, or 10.  Keep 0 on first round.
specifier|final
name|double
name|EPS
init|=
literal|0.0000001
decl_stmt|;
name|assertEquals
argument_list|(
literal|"within"
argument_list|,
operator|(
literal|200d
operator|/
literal|1600d
operator|*
literal|0.5
operator|)
operator|+
operator|(
literal|0.5
operator|)
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|10
argument_list|,
literal|30
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|SHIFT
operator|+
name|nudge
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"in25%"
argument_list|,
literal|0.25
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|30
argument_list|,
literal|70
argument_list|,
operator|-
literal|20
argument_list|,
literal|20
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrap"
argument_list|,
literal|0.2794117
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|30
argument_list|,
literal|10
argument_list|,
operator|-
literal|20
argument_list|,
literal|20
argument_list|,
name|SHIFT
operator|+
name|nudge
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no intersection H"
argument_list|,
literal|0.0
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
operator|-
literal|10
argument_list|,
operator|-
literal|10
argument_list|,
operator|-
literal|20
argument_list|,
literal|20
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no intersection V"
argument_list|,
literal|0.0
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|,
operator|-
literal|30
argument_list|,
operator|-
literal|30
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"point"
argument_list|,
literal|0.5
operator|+
operator|(
name|MSL
condition|?
operator|(
literal|0.1
operator|*
literal|0.1
operator|/
literal|1600.0
operator|/
literal|2.0
operator|)
else|:
literal|0
operator|)
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line 25% intersection"
argument_list|,
literal|0.25
operator|/
literal|2
operator|+
operator|(
name|MSL
condition|?
operator|(
literal|10.0
operator|*
literal|0.1
operator|/
literal|1600.0
operator|/
literal|2.0
operator|)
else|:
literal|0.0
operator|)
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
operator|-
literal|30
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
comment|//test with point query
name|sim
operator|=
operator|new
name|BBoxOverlapRatioValueSource
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|0.5
argument_list|,
name|minSideLength
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"same"
argument_list|,
literal|1.0
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"contains"
argument_list|,
literal|0.5
operator|+
operator|(
name|MSL
condition|?
operator|(
literal|0.1
operator|*
literal|0.1
operator|/
operator|(
literal|30
operator|*
literal|10
operator|)
operator|/
literal|2.0
operator|)
else|:
literal|0.0
operator|)
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|30
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
comment|//test with line query (vertical this time)
name|sim
operator|=
operator|new
name|BBoxOverlapRatioValueSource
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|,
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|40
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|0.5
argument_list|,
name|minSideLength
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line 50%"
argument_list|,
literal|0.5
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|30
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"point"
argument_list|,
literal|0.5
operator|+
operator|(
name|MSL
condition|?
operator|(
literal|0.1
operator|*
literal|0.1
operator|/
operator|(
literal|20
operator|*
literal|0.1
operator|)
operator|/
literal|2.0
operator|)
else|:
literal|0.0
operator|)
argument_list|,
name|sim
operator|.
name|score
argument_list|(
name|shiftedRect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|30
argument_list|,
literal|30
argument_list|,
name|SHIFT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|,
name|EPS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shiftedRect
specifier|private
name|Rectangle
name|shiftedRect
parameter_list|(
name|double
name|minX
parameter_list|,
name|double
name|maxX
parameter_list|,
name|double
name|minY
parameter_list|,
name|double
name|maxY
parameter_list|,
name|int
name|xShift
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|minX
operator|+
name|xShift
argument_list|)
argument_list|,
name|DistanceUtils
operator|.
name|normLonDEG
argument_list|(
name|maxX
operator|+
name|xShift
argument_list|)
argument_list|,
name|minY
argument_list|,
name|maxY
argument_list|)
return|;
block|}
DECL|method|testAreaValueSource
specifier|public
name|void
name|testAreaValueSource
parameter_list|()
throws|throws
name|IOException
block|{
name|setupGeo
argument_list|()
expr_stmt|;
name|adoc
argument_list|(
literal|"100"
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|,
literal|40
argument_list|,
literal|80
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"999"
argument_list|,
operator|(
name|Shape
operator|)
literal|null
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|BBoxStrategy
name|bboxStrategy
init|=
operator|(
name|BBoxStrategy
operator|)
name|strategy
decl_stmt|;
name|checkValueSource
argument_list|(
operator|new
name|ShapeAreaValueSource
argument_list|(
name|bboxStrategy
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|ctx
argument_list|,
literal|false
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|800f
block|,
literal|0f
block|}
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkValueSource
argument_list|(
operator|new
name|ShapeAreaValueSource
argument_list|(
name|bboxStrategy
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|ctx
argument_list|,
literal|true
argument_list|)
argument_list|,
comment|//geo
operator|new
name|float
index|[]
block|{
literal|391.93f
block|,
literal|0f
block|}
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
