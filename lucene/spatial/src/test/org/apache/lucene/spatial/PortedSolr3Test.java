begin_unit
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
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
name|Name
import|;
end_import
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
name|ParametersFactory
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
name|search
operator|.
name|FilteredQuery
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
name|MatchAllDocsQuery
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
name|Query
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
name|RecursivePrefixTreeStrategy
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
name|TermQueryPrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|QuadPrefixTree
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
name|vector
operator|.
name|PointVectorStrategy
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
name|HashSet
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
name|Set
import|;
end_import
begin_comment
comment|/**  * Based off of Solr 3's SpatialFilterTest.  */
end_comment
begin_class
DECL|class|PortedSolr3Test
specifier|public
class|class
name|PortedSolr3Test
extends|extends
name|StrategyTestCase
block|{
annotation|@
name|ParametersFactory
DECL|method|parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|ctorArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
name|SpatialPrefixTree
name|grid
decl_stmt|;
name|SpatialStrategy
name|strategy
decl_stmt|;
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"recursive_geohash"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|grid
operator|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"recursive_quad"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
literal|"termquery_geohash"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|strategy
operator|=
operator|new
name|PointVectorStrategy
argument_list|(
name|ctx
argument_list|,
literal|"pointvector"
argument_list|)
expr_stmt|;
name|ctorArgs
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|new
name|Param
argument_list|(
name|strategy
argument_list|)
block|}
argument_list|)
expr_stmt|;
return|return
name|ctorArgs
return|;
block|}
comment|// this is a hack for clover! (otherwise strategy.toString() used as file name)
DECL|class|Param
specifier|static
class|class
name|Param
block|{
DECL|field|strategy
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|method|Param
name|Param
parameter_list|(
name|SpatialStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
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
return|return
name|strategy
operator|.
name|getFieldName
argument_list|()
return|;
block|}
block|}
comment|//  private String fieldName;
DECL|method|PortedSolr3Test
specifier|public
name|PortedSolr3Test
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"strategy"
argument_list|)
name|Param
name|param
parameter_list|)
block|{
name|SpatialStrategy
name|strategy
init|=
name|param
operator|.
name|strategy
decl_stmt|;
name|this
operator|.
name|ctx
operator|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
DECL|method|setupDocs
specifier|private
name|void
name|setupDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|adoc
argument_list|(
literal|"1"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|79.9289094
argument_list|,
literal|32.7693246
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"2"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|80.9289094
argument_list|,
literal|33.7693246
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"3"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|50.9289094
argument_list|,
operator|-
literal|32.7693246
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"4"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|60.9289094
argument_list|,
operator|-
literal|50.7693246
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"5"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"6"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0.1
argument_list|,
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"7"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|0.1
argument_list|,
operator|-
literal|0.1
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"8"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|179.9
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"9"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|179.9
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"10"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|50
argument_list|,
literal|89.9
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"11"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|130
argument_list|,
literal|89.9
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"12"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|50
argument_list|,
operator|-
literal|89.9
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"13"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|130
argument_list|,
operator|-
literal|89.9
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntersections
specifier|public
name|void
name|testIntersections
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDocs
argument_list|()
expr_stmt|;
comment|//Try some edge cases
comment|//NOTE: 2nd arg is distance in kilometers
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|175
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|179.8
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|50
argument_list|,
literal|89.8
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
expr_stmt|;
comment|//this goes over the north pole
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|50
argument_list|,
operator|-
literal|89.8
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|12
argument_list|,
literal|13
argument_list|)
expr_stmt|;
comment|//this goes over the south pole
comment|//try some normal cases
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|80.0
argument_list|,
literal|33.0
argument_list|)
argument_list|,
literal|300
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//large distance
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|5000
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|//Because we are generating a box based on the west/east longitudes and the south/north latitudes, which then
comment|//translates to a range query, which is slightly more inclusive.  Thus, even though 0.0 is 15.725 kms away,
comment|//it will be included, b/c of the box calculation.
name|checkHitsBBox
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0.1
argument_list|,
literal|0.1
argument_list|)
argument_list|,
literal|15
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|//try some more
name|deleteAll
argument_list|()
expr_stmt|;
name|adoc
argument_list|(
literal|"14"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"15"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|15
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//3000KM from 0,0, see http://www.movable-type.co.uk/scripts/latlong.html
name|adoc
argument_list|(
literal|"16"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|19.79750
argument_list|,
literal|18.71111
argument_list|)
argument_list|)
expr_stmt|;
name|adoc
argument_list|(
literal|"17"
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|95.436643
argument_list|,
literal|44.043900
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|2000
argument_list|,
literal|2
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|checkHitsBBox
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|3000
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|3001
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|3000.1
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
comment|//really fine grained distance and reflects some of the vagaries of how we are calculating the box
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|96.789603
argument_list|,
literal|43.517030
argument_list|)
argument_list|,
literal|109
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// falls outside of the real distance, but inside the bounding box
name|checkHitsCircle
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|96.789603
argument_list|,
literal|43.517030
argument_list|)
argument_list|,
literal|110
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkHitsBBox
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|96.789603
argument_list|,
literal|43.517030
argument_list|)
argument_list|,
literal|110
argument_list|,
literal|1
argument_list|,
literal|17
argument_list|)
expr_stmt|;
block|}
comment|//---- these are similar to Solr test methods
DECL|method|checkHitsCircle
specifier|private
name|void
name|checkHitsCircle
parameter_list|(
name|Point
name|pt
parameter_list|,
name|double
name|distKM
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
modifier|...
name|assertIds
parameter_list|)
block|{
name|_checkHits
argument_list|(
literal|false
argument_list|,
name|pt
argument_list|,
name|distKM
argument_list|,
name|assertNumFound
argument_list|,
name|assertIds
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHitsBBox
specifier|private
name|void
name|checkHitsBBox
parameter_list|(
name|Point
name|pt
parameter_list|,
name|double
name|distKM
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
modifier|...
name|assertIds
parameter_list|)
block|{
name|_checkHits
argument_list|(
literal|true
argument_list|,
name|pt
argument_list|,
name|distKM
argument_list|,
name|assertNumFound
argument_list|,
name|assertIds
argument_list|)
expr_stmt|;
block|}
DECL|method|_checkHits
specifier|private
name|void
name|_checkHits
parameter_list|(
name|boolean
name|bbox
parameter_list|,
name|Point
name|pt
parameter_list|,
name|double
name|distKM
parameter_list|,
name|int
name|assertNumFound
parameter_list|,
name|int
modifier|...
name|assertIds
parameter_list|)
block|{
name|SpatialOperation
name|op
init|=
name|SpatialOperation
operator|.
name|Intersects
decl_stmt|;
name|double
name|distDEG
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
name|distKM
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|bbox
condition|)
name|shape
operator|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
expr_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
argument_list|)
decl_stmt|;
comment|//args.setDistPrecision(0.025);
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|query
operator|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|strategy
operator|.
name|makeFilter
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SearchResults
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|shape
argument_list|,
name|assertNumFound
argument_list|,
name|results
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
name|resultIds
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
name|results
operator|.
name|results
control|)
block|{
name|resultIds
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
name|resultIds
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
