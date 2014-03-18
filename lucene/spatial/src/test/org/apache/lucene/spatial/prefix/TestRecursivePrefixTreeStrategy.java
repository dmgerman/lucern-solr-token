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
name|StrategyTestCase
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
name|GeohashPrefixTree
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_class
DECL|class|TestRecursivePrefixTreeStrategy
specifier|public
class|class
name|TestRecursivePrefixTreeStrategy
extends|extends
name|StrategyTestCase
block|{
DECL|field|maxLength
specifier|private
name|int
name|maxLength
decl_stmt|;
comment|//Tests should call this first.
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|int
name|maxLength
parameter_list|)
block|{
name|this
operator|.
name|maxLength
operator|=
name|maxLength
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
name|GeohashPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilterWithVariableScanLevel
specifier|public
name|void
name|testFilterWithVariableScanLevel
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|getAddAndVerifyIndexedDocuments
argument_list|(
name|DATA_WORLD_CITIES_POINTS
argument_list|)
expr_stmt|;
comment|//execute queries for each prefix grid scan level
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|maxLength
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
operator|)
operator|.
name|setPrefixGridScanLevel
argument_list|(
name|i
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
block|}
annotation|@
name|Test
DECL|method|testOneMeterPrecision
specifier|public
name|void
name|testOneMeterPrecision
parameter_list|()
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|GeohashPrefixTree
name|grid
init|=
call|(
name|GeohashPrefixTree
call|)
argument_list|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|strategy
argument_list|)
operator|.
name|getGrid
argument_list|()
decl_stmt|;
comment|//DWS: I know this to be true.  11 is needed for one meter
name|double
name|degrees
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
literal|0.001
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|grid
operator|.
name|getLevelForDistance
argument_list|(
name|degrees
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrecision
specifier|public
name|void
name|testPrecision
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|GeohashPrefixTree
operator|.
name|getMaxLevelsPossible
argument_list|()
argument_list|)
expr_stmt|;
name|Point
name|iPt
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2.8028712999999925
argument_list|,
literal|48.3708044
argument_list|)
decl_stmt|;
comment|//lon, lat
name|addDocument
argument_list|(
name|newDoc
argument_list|(
literal|"iPt"
argument_list|,
name|iPt
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|Point
name|qPt
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|2.4632387000000335
argument_list|,
literal|48.6003516
argument_list|)
decl_stmt|;
specifier|final
name|double
name|KM2DEG
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
literal|1
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
decl_stmt|;
specifier|final
name|double
name|DEG2KM
init|=
literal|1
operator|/
name|KM2DEG
decl_stmt|;
specifier|final
name|double
name|DIST
init|=
literal|35.75
decl_stmt|;
comment|//35.7499...
name|assertEquals
argument_list|(
name|DIST
argument_list|,
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|iPt
argument_list|,
name|qPt
argument_list|)
operator|*
name|DEG2KM
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
comment|//distErrPct will affect the query shape precision. The indexed precision
comment|// was set to nearly zilch via init(GeohashPrefixTree.getMaxLevelsPossible());
specifier|final
name|double
name|distErrPct
init|=
literal|0.025
decl_stmt|;
comment|//the suggested default, by the way
specifier|final
name|double
name|distMult
init|=
literal|1
operator|+
name|distErrPct
decl_stmt|;
name|assertTrue
argument_list|(
literal|35.74
operator|*
name|distMult
operator|>=
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|35.74
operator|*
name|KM2DEG
argument_list|,
name|distErrPct
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|30
operator|*
name|distMult
operator|<
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|30
operator|*
name|KM2DEG
argument_list|,
name|distErrPct
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|33
operator|*
name|distMult
operator|<
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|33
operator|*
name|KM2DEG
argument_list|,
name|distErrPct
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|34
operator|*
name|distMult
operator|<
name|DIST
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|q
argument_list|(
name|qPt
argument_list|,
literal|34
operator|*
name|KM2DEG
argument_list|,
name|distErrPct
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|q
specifier|private
name|SpatialArgs
name|q
parameter_list|(
name|Point
name|pt
parameter_list|,
name|double
name|distDEG
parameter_list|,
name|double
name|distErrPct
parameter_list|)
block|{
name|Shape
name|shape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|pt
argument_list|,
name|distDEG
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
argument_list|)
decl_stmt|;
name|args
operator|.
name|setDistErrPct
argument_list|(
name|distErrPct
argument_list|)
expr_stmt|;
return|return
name|args
return|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
index|[]
name|assertIds
parameter_list|)
block|{
name|SearchResults
name|got
init|=
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|args
argument_list|,
name|assertNumFound
argument_list|,
name|got
operator|.
name|numFound
argument_list|)
expr_stmt|;
if|if
condition|(
name|assertIds
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|gotIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SearchResult
name|result
range|:
name|got
operator|.
name|results
control|)
block|{
name|gotIds
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|result
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|assertId
range|:
name|assertIds
control|)
block|{
name|assertTrue
argument_list|(
literal|"has "
operator|+
name|assertId
argument_list|,
name|gotIds
operator|.
name|contains
argument_list|(
name|assertId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
