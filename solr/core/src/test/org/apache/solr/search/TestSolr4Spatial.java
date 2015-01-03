begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|RandomizedTest
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
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|AbstractSpatialFieldType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|SpatialUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
begin_comment
comment|/**  * Test Solr 4's new spatial capabilities from the new Lucene spatial module. Don't thoroughly test it here because  * Lucene spatial has its own tests.  Some of these tests were ported from Solr 3 spatial tests.  */
end_comment
begin_class
DECL|class|TestSolr4Spatial
specifier|public
class|class
name|TestSolr4Spatial
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|TestSolr4Spatial
specifier|public
name|TestSolr4Spatial
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
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
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"srpt_geohash"
block|}
block|,
block|{
literal|"srpt_quad"
block|}
block|,
block|{
literal|"stqpt_geohash"
block|}
block|,
block|{
literal|"pointvector"
block|}
block|,
block|{
literal|"bbox"
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-spatial.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadShapeParse400
specifier|public
name|void
name|testBadShapeParse400
parameter_list|()
block|{
name|assertQEx
argument_list|(
literal|null
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|fieldName
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|"}Intersects(NonexistentShape(89.9,-130 d=9))"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|null
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|fieldName
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|"}Intersects(NonexistentShape(89.9,-130 d=9"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
comment|//missing parens
name|assertQEx
argument_list|(
literal|null
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|fieldName
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|"}Intersectssss"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"NonexistentShape"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"-1"
argument_list|,
name|fieldName
argument_list|,
literal|"NonexistentShape"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|unIgnoreException
argument_list|(
literal|"NonexistentShape"
argument_list|)
expr_stmt|;
block|}
DECL|method|setupDocs
specifier|private
name|void
name|setupDocs
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldName
argument_list|,
literal|"32.7693246, -79.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|fieldName
argument_list|,
literal|"33.7693246, -80.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|fieldName
argument_list|,
literal|"-32.7693246, 50.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|fieldName
argument_list|,
literal|"-50.7693246, 60.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|fieldName
argument_list|,
literal|"0,0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|fieldName
argument_list|,
literal|"0.1,0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
name|fieldName
argument_list|,
literal|"-0.1,-0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
name|fieldName
argument_list|,
literal|"0,179.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
name|fieldName
argument_list|,
literal|"0,-179.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
name|fieldName
argument_list|,
literal|"89.9,50"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
name|fieldName
argument_list|,
literal|"89.9,-130"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
name|fieldName
argument_list|,
literal|"-89.9,50"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
name|fieldName
argument_list|,
literal|"-89.9,-130"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntersectFilter
specifier|public
name|void
name|testIntersectFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|setupDocs
argument_list|()
expr_stmt|;
comment|//Try some edge cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"1,1"
argument_list|,
literal|175
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,179.8"
argument_list|,
literal|200
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"89.8, 50"
argument_list|,
literal|200
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
expr_stmt|;
comment|//this goes over the north pole
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"-89.8, 50"
argument_list|,
literal|200
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"33.0,-80.0"
argument_list|,
literal|300
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//large distance
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"1,1"
argument_list|,
literal|5000
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|"0.1,0.1"
argument_list|,
literal|15
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|//try some more
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"14"
argument_list|,
name|fieldName
argument_list|,
literal|"0,5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"15"
argument_list|,
name|fieldName
argument_list|,
literal|"0,15"
argument_list|)
argument_list|)
expr_stmt|;
comment|//3000KM from 0,0, see http://www.movable-type.co.uk/scripts/latlong.html
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"16"
argument_list|,
name|fieldName
argument_list|,
literal|"18.71111,19.79750"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"17"
argument_list|,
name|fieldName
argument_list|,
literal|"44.043900,-95.436643"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|1000
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|1
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|2000
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|2
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|"0,0"
argument_list|,
literal|3000
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|3001
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|3000.1
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"43.517030,-96.789603"
argument_list|,
literal|109
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//falls outside of the real distance, but inside the bounding box
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
literal|"43.517030,-96.789603"
argument_list|,
literal|110
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|"43.517030,-96.789603"
argument_list|,
literal|110
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|1
argument_list|,
literal|17
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkResultFormat
specifier|public
name|void
name|checkResultFormat
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Check input and output format is the same
name|String
name|IN
init|=
literal|"89.9,-130"
decl_stmt|;
comment|//lat,lon
name|String
name|OUT
init|=
name|IN
decl_stmt|;
comment|//IDENTICAL!
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
name|fieldName
argument_list|,
name|IN
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,"
operator|+
name|fieldName
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!bbox sfield="
operator|+
name|fieldName
operator|+
literal|" pt="
operator|+
name|IN
operator|+
literal|" d=9}"
argument_list|)
argument_list|,
literal|"//result/doc/*[@name='"
operator|+
name|fieldName
operator|+
literal|"']//text()='"
operator|+
name|OUT
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkQueryEmptyIndex
specifier|public
name|void
name|checkQueryEmptyIndex
parameter_list|()
throws|throws
name|ParseException
block|{
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|100
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//doesn't error
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|pt
parameter_list|,
name|double
name|distKM
parameter_list|,
name|double
name|sphereRadius
parameter_list|,
name|int
name|count
parameter_list|,
name|int
modifier|...
name|docIds
parameter_list|)
throws|throws
name|ParseException
block|{
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
name|pt
argument_list|,
name|distKM
argument_list|,
name|sphereRadius
argument_list|,
name|count
argument_list|,
name|docIds
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|exact
parameter_list|,
name|String
name|ptStr
parameter_list|,
name|double
name|distKM
parameter_list|,
name|double
name|sphereRadius
parameter_list|,
name|int
name|count
parameter_list|,
name|int
modifier|...
name|docIds
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|exact
operator|&&
name|fieldName
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"bbox"
argument_list|)
condition|)
block|{
return|return;
comment|// bbox field only supports rectangular query
block|}
name|String
index|[]
name|tests
init|=
operator|new
name|String
index|[
name|docIds
operator|!=
literal|null
operator|&&
name|docIds
operator|.
name|length
operator|>
literal|0
condition|?
name|docIds
operator|.
name|length
operator|+
literal|1
else|:
literal|1
index|]
decl_stmt|;
comment|//test for presence of required ids first
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|docIds
operator|!=
literal|null
operator|&&
name|docIds
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|docId
range|:
name|docIds
control|)
block|{
name|tests
index|[
name|i
operator|++
index|]
operator|=
literal|"//result/doc/*[@name='id'][.='"
operator|+
name|docId
operator|+
literal|"']"
expr_stmt|;
block|}
block|}
comment|//check total length last; maybe response includes ids it shouldn't.  Nicer to check this last instead of first so
comment|// that there may be a more specific detailed id to investigate.
name|tests
index|[
name|i
operator|++
index|]
operator|=
literal|"*[count(//doc)="
operator|+
name|count
operator|+
literal|"]"
expr_stmt|;
comment|//Test using the Lucene spatial syntax
block|{
comment|//never actually need the score but lets test
name|String
name|score
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|"none"
block|,
literal|"distance"
block|,
literal|"recipDistance"
block|}
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
index|]
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
name|Point
name|point
init|=
name|SpatialUtils
operator|.
name|parsePoint
argument_list|(
name|ptStr
argument_list|,
name|SpatialContext
operator|.
name|GEO
argument_list|)
decl_stmt|;
name|String
name|circleStr
init|=
literal|"BUFFER(POINT("
operator|+
name|point
operator|.
name|getX
argument_list|()
operator|+
literal|" "
operator|+
name|point
operator|.
name|getY
argument_list|()
operator|+
literal|"),"
operator|+
name|distDEG
operator|+
literal|")"
decl_stmt|;
name|String
name|shapeStr
decl_stmt|;
if|if
condition|(
name|exact
condition|)
block|{
name|shapeStr
operator|=
name|circleStr
expr_stmt|;
block|}
else|else
block|{
comment|//bbox
comment|//the GEO is an assumption
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
name|Rectangle
name|bbox
init|=
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
name|circleStr
argument_list|)
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|shapeStr
operator|=
literal|"ENVELOPE("
operator|+
name|bbox
operator|.
name|getMinX
argument_list|()
operator|+
literal|", "
operator|+
name|bbox
operator|.
name|getMaxX
argument_list|()
operator|+
literal|", "
operator|+
name|bbox
operator|.
name|getMaxY
argument_list|()
operator|+
literal|", "
operator|+
name|bbox
operator|.
name|getMinY
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
comment|//FYI default distErrPct=0.025 works with the tests in this file
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
operator|(
name|score
operator|==
literal|null
condition|?
literal|""
else|:
literal|" score="
operator|+
name|score
operator|)
operator|+
literal|"}Intersects("
operator|+
name|shapeStr
operator|+
literal|")"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
comment|//Test using geofilt
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!"
operator|+
operator|(
name|exact
condition|?
literal|"geofilt"
else|:
literal|"bbox"
operator|)
operator|+
literal|" sfield="
operator|+
name|fieldName
operator|+
literal|" pt='"
operator|+
name|ptStr
operator|+
literal|"' d="
operator|+
name|distKM
operator|+
literal|" sphere_radius="
operator|+
name|sphereRadius
operator|+
literal|"}"
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRangeSyntax
specifier|public
name|void
name|testRangeSyntax
parameter_list|()
block|{
name|setupDocs
argument_list|()
expr_stmt|;
comment|//match docId 1
name|int
name|docId
init|=
literal|1
decl_stmt|;
name|int
name|count
init|=
literal|1
decl_stmt|;
name|String
name|score
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"none"
else|:
literal|"distance"
decl_stmt|;
comment|//never actually need the score but lets test
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
comment|// testing quotes in range too
literal|"fq"
argument_list|,
literal|"{! score="
operator|+
name|score
operator|+
literal|" df="
operator|+
name|fieldName
operator|+
literal|"}[32,-80 TO \"33 , -79\"]"
argument_list|)
argument_list|,
comment|//lower-left to upper-right
literal|"//result/doc/*[@name='id'][.='"
operator|+
name|docId
operator|+
literal|"']"
argument_list|,
literal|"*[count(//doc)="
operator|+
name|count
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|,
name|fieldName
argument_list|,
literal|"1,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
name|fieldName
argument_list|,
literal|"4,-1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"999"
argument_list|,
name|fieldName
argument_list|,
literal|"70,70"
argument_list|)
argument_list|)
expr_stmt|;
comment|//far away from these queries
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//test absence of score=distance means it doesn't score
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|radiusQuery
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|1e-9
argument_list|,
literal|"/response/docs/[0]/score==1.0"
argument_list|,
literal|"/response/docs/[1]/score==1.0"
argument_list|)
expr_stmt|;
comment|//score by distance
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|radiusQuery
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|,
literal|"distance"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"score asc"
argument_list|)
comment|//want ascending due to increasing distance
argument_list|,
literal|1e-3
argument_list|,
literal|"/response/docs/[0]/id=='100'"
argument_list|,
literal|"/response/docs/[0]/score==2.827493"
argument_list|,
literal|"/response/docs/[1]/id=='101'"
argument_list|,
literal|"/response/docs/[1]/score==5.089807"
argument_list|)
expr_stmt|;
comment|//score by recipDistance
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|radiusQuery
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|,
literal|"recipDistance"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"score desc"
argument_list|)
comment|//want descending
argument_list|,
literal|1e-3
argument_list|,
literal|"/response/docs/[0]/id=='100'"
argument_list|,
literal|"/response/docs/[0]/score==0.3099695"
argument_list|,
literal|"/response/docs/[1]/id=='101'"
argument_list|,
literal|"/response/docs/[1]/score==0.19970943"
argument_list|)
expr_stmt|;
comment|//score by distance and don't filter
name|assertJQ
argument_list|(
name|req
argument_list|(
comment|//circle radius is small and shouldn't match either, but we disable filtering
literal|"q"
argument_list|,
name|radiusQuery
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|0.000001
argument_list|,
literal|"distance"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"score asc"
argument_list|)
comment|//want ascending due to increasing distance
argument_list|,
literal|1e-3
argument_list|,
literal|"/response/docs/[0]/id=='100'"
argument_list|,
literal|"/response/docs/[0]/score==2.827493"
argument_list|,
literal|"/response/docs/[1]/id=='101'"
argument_list|,
literal|"/response/docs/[1]/score==5.089807"
argument_list|)
expr_stmt|;
comment|//query again with the query point closer to #101, and check the new ordering
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|radiusQuery
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|9
argument_list|,
literal|"distance"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"score asc"
argument_list|)
comment|//want ascending due to increasing distance
argument_list|,
literal|1e-4
argument_list|,
literal|"/response/docs/[0]/id=='101'"
argument_list|,
literal|"/response/docs/[1]/id=='100'"
argument_list|)
expr_stmt|;
comment|//use sort=query(...)
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-id:999"
argument_list|,
comment|//exclude that doc
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"query($sortQuery) asc"
argument_list|,
comment|//want ascending due to increasing distance
literal|"sortQuery"
argument_list|,
name|radiusQuery
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|,
literal|"distance"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|,
literal|1e-4
argument_list|,
literal|"/response/docs/[0]/id=='100'"
argument_list|,
literal|"/response/docs/[1]/id=='101'"
argument_list|)
expr_stmt|;
comment|//check reversed direction with query point closer to #101
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"-id:999"
argument_list|,
comment|//exclude that doc
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"query($sortQuery) asc"
argument_list|,
comment|//want ascending due to increasing distance
literal|"sortQuery"
argument_list|,
name|radiusQuery
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|9
argument_list|,
literal|"distance"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|,
literal|1e-4
argument_list|,
literal|"/response/docs/[0]/id=='101'"
argument_list|,
literal|"/response/docs/[1]/id=='100'"
argument_list|)
expr_stmt|;
block|}
DECL|method|radiusQuery
specifier|private
name|String
name|radiusQuery
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|dDEG
parameter_list|,
name|String
name|score
parameter_list|,
name|String
name|filter
parameter_list|)
block|{
comment|//Choose between the Solr/Geofilt syntax, and the Lucene spatial module syntax
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"bbox"
argument_list|)
operator|||
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|//we cheat for bbox strategy which doesn't do radius, only rect.
specifier|final
name|String
name|qparser
init|=
name|fieldName
operator|.
name|equals
argument_list|(
literal|"bbox"
argument_list|)
condition|?
literal|"bbox"
else|:
literal|"geofilt"
decl_stmt|;
return|return
literal|"{!"
operator|+
name|qparser
operator|+
literal|" "
operator|+
literal|"sfield="
operator|+
name|fieldName
operator|+
literal|" "
operator|+
operator|(
name|score
operator|!=
literal|null
condition|?
literal|"score="
operator|+
name|score
else|:
literal|""
operator|)
operator|+
literal|" "
operator|+
operator|(
name|filter
operator|!=
literal|null
condition|?
literal|"filter="
operator|+
name|filter
else|:
literal|""
operator|)
operator|+
literal|" "
operator|+
literal|"pt="
operator|+
name|lat
operator|+
literal|","
operator|+
name|lon
operator|+
literal|" d="
operator|+
operator|(
name|dDEG
comment|/* DistanceUtils.DEG_TO_KM*/
operator|)
operator|+
literal|"}"
return|;
block|}
else|else
block|{
return|return
literal|"{! "
operator|+
operator|(
name|score
operator|!=
literal|null
condition|?
literal|"score="
operator|+
name|score
else|:
literal|""
operator|)
operator|+
literal|" "
operator|+
operator|(
name|filter
operator|!=
literal|null
condition|?
literal|"filter="
operator|+
name|filter
else|:
literal|""
operator|)
operator|+
literal|" "
operator|+
literal|"}"
operator|+
name|fieldName
operator|+
literal|":\"Intersects(BUFFER(POINT("
operator|+
name|lon
operator|+
literal|" "
operator|+
name|lat
operator|+
literal|"),"
operator|+
name|dDEG
operator|+
literal|"))\""
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSortMultiVal
specifier|public
name|void
name|testSortMultiVal
parameter_list|()
throws|throws
name|Exception
block|{
name|RandomizedTest
operator|.
name|assumeFalse
argument_list|(
literal|"Multivalue not supported for this field"
argument_list|,
name|fieldName
operator|.
name|equals
argument_list|(
literal|"pointvector"
argument_list|)
operator|||
name|fieldName
operator|.
name|equals
argument_list|(
literal|"bbox"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|,
name|fieldName
argument_list|,
literal|"1,2"
argument_list|)
argument_list|)
expr_stmt|;
comment|//1 point
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
name|fieldName
argument_list|,
literal|"4,-1"
argument_list|,
name|fieldName
argument_list|,
literal|"3,5"
argument_list|)
argument_list|)
expr_stmt|;
comment|//2 points, 2nd is pretty close to query point
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|radiusQuery
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|9
argument_list|,
literal|"distance"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"sort"
argument_list|,
literal|"score asc"
argument_list|)
comment|//want ascending due to increasing distance
argument_list|,
literal|1e-4
argument_list|,
literal|"/response/docs/[0]/id=='101'"
argument_list|,
literal|"/response/docs/[0]/score==0.99862987"
comment|//dist to 3,5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|solr4OldShapeSyntax
specifier|public
name|void
name|solr4OldShapeSyntax
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"Mostly just valid for prefix-tree"
argument_list|,
name|fieldName
operator|.
name|equals
argument_list|(
literal|"pointvector"
argument_list|)
argument_list|)
expr_stmt|;
comment|//we also test that the old syntax is parsed in worldBounds in the schema
block|{
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|AbstractSpatialFieldType
name|type
init|=
operator|(
name|AbstractSpatialFieldType
operator|)
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"stqpt_u_oldworldbounds"
argument_list|)
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|type
operator|.
name|getStrategy
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getSpatialContext
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|RectangleImpl
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|,
name|ctx
argument_list|)
argument_list|,
name|ctx
operator|.
name|getWorldBounds
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//syntax supported in Solr 4 but not beyond
comment|//   See Spatial4j LegacyShapeReadWriterFormat
name|String
name|rect
init|=
literal|"-74.093 41.042 -69.347 44.558"
decl_stmt|;
comment|//minX minY maxX maxY
name|String
name|circ
init|=
literal|"Circle(4.56,1.23 d=0.0710)"
decl_stmt|;
comment|//show we can index this (without an error)
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"rect"
argument_list|,
name|fieldName
argument_list|,
name|rect
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
literal|"bbox"
argument_list|)
condition|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"circ"
argument_list|,
name|fieldName
argument_list|,
name|circ
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//only testing no error
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|"}Intersects("
operator|+
name|rect
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
literal|"bbox"
argument_list|)
condition|)
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|"}Intersects("
operator|+
name|circ
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadScoreParam
specifier|public
name|void
name|testBadScoreParam
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQEx
argument_list|(
literal|"expect friendly error message"
argument_list|,
literal|"none"
argument_list|,
name|req
argument_list|(
name|radiusQuery
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"bogus"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
