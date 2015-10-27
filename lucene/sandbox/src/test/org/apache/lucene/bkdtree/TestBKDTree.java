begin_unit
begin_package
DECL|package|org.apache.lucene.bkdtree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|bkdtree
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|DocValuesFormat
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
name|codecs
operator|.
name|lucene54
operator|.
name|Lucene54Codec
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
name|Document
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
name|IndexReader
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
name|IndexWriterConfig
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
name|RandomIndexWriter
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
name|IndexSearcher
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
name|search
operator|.
name|TopDocs
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
name|store
operator|.
name|Directory
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
name|Accountable
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
name|Accountables
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
name|BaseGeoPointTestCase
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
name|GeoRect
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
name|IOUtils
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
name|SloppyMath
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
name|TestUtil
import|;
end_import
begin_comment
comment|// TODO: can test framework assert we don't leak temp files?
end_comment
begin_class
DECL|class|TestBKDTree
specifier|public
class|class
name|TestBKDTree
extends|extends
name|BaseGeoPointTestCase
block|{
annotation|@
name|Override
DECL|method|addPointToDoc
specifier|protected
name|void
name|addPointToDoc
parameter_list|(
name|String
name|field
parameter_list|,
name|Document
name|doc
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BKDPointField
argument_list|(
name|field
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newBBoxQuery
specifier|protected
name|Query
name|newBBoxQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|GeoRect
name|rect
parameter_list|)
block|{
return|return
operator|new
name|BKDPointInBBoxQuery
argument_list|(
name|field
argument_list|,
name|rect
operator|.
name|minLat
argument_list|,
name|rect
operator|.
name|maxLat
argument_list|,
name|rect
operator|.
name|minLon
argument_list|,
name|rect
operator|.
name|maxLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newDistanceQuery
specifier|protected
name|Query
name|newDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
comment|// return new BKDDistanceQuery(field, centerLat, centerLon, radiusMeters);
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|newDistanceRangeQuery
specifier|protected
name|Query
name|newDistanceRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|minRadiusMeters
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|newPolygonQuery
specifier|protected
name|Query
name|newPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
index|[]
name|lats
parameter_list|,
name|double
index|[]
name|lons
parameter_list|)
block|{
return|return
operator|new
name|BKDPointInPolygonQuery
argument_list|(
name|FIELD_NAME
argument_list|,
name|lats
argument_list|,
name|lons
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initIndexWriterConfig
specifier|protected
name|void
name|initIndexWriterConfig
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
name|IndexWriterConfig
name|iwc
parameter_list|)
block|{
specifier|final
name|DocValuesFormat
name|dvFormat
init|=
name|getDocValuesFormat
argument_list|()
decl_stmt|;
name|Codec
name|codec
init|=
operator|new
name|Lucene54Codec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
return|return
name|dvFormat
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getDocValuesFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rectContainsPoint
specifier|protected
name|Boolean
name|rectContainsPoint
parameter_list|(
name|GeoRect
name|rect
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
assert|assert
name|Double
operator|.
name|isNaN
argument_list|(
name|pointLat
argument_list|)
operator|==
literal|false
assert|;
name|int
name|rectLatMinEnc
init|=
name|BKDTreeWriter
operator|.
name|encodeLat
argument_list|(
name|rect
operator|.
name|minLat
argument_list|)
decl_stmt|;
name|int
name|rectLatMaxEnc
init|=
name|BKDTreeWriter
operator|.
name|encodeLat
argument_list|(
name|rect
operator|.
name|maxLat
argument_list|)
decl_stmt|;
name|int
name|rectLonMinEnc
init|=
name|BKDTreeWriter
operator|.
name|encodeLon
argument_list|(
name|rect
operator|.
name|minLon
argument_list|)
decl_stmt|;
name|int
name|rectLonMaxEnc
init|=
name|BKDTreeWriter
operator|.
name|encodeLon
argument_list|(
name|rect
operator|.
name|maxLon
argument_list|)
decl_stmt|;
name|int
name|pointLatEnc
init|=
name|BKDTreeWriter
operator|.
name|encodeLat
argument_list|(
name|pointLat
argument_list|)
decl_stmt|;
name|int
name|pointLonEnc
init|=
name|BKDTreeWriter
operator|.
name|encodeLon
argument_list|(
name|pointLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|rect
operator|.
name|minLon
operator|<
name|rect
operator|.
name|maxLon
condition|)
block|{
return|return
name|pointLatEnc
operator|>=
name|rectLatMinEnc
operator|&&
name|pointLatEnc
operator|<
name|rectLatMaxEnc
operator|&&
name|pointLonEnc
operator|>=
name|rectLonMinEnc
operator|&&
name|pointLonEnc
operator|<
name|rectLonMaxEnc
return|;
block|}
else|else
block|{
comment|// Rect crosses dateline:
return|return
name|pointLatEnc
operator|>=
name|rectLatMinEnc
operator|&&
name|pointLatEnc
operator|<
name|rectLatMaxEnc
operator|&&
operator|(
name|pointLonEnc
operator|>=
name|rectLonMinEnc
operator|||
name|pointLonEnc
operator|<
name|rectLonMaxEnc
operator|)
return|;
block|}
block|}
DECL|field|POLY_TOLERANCE
specifier|private
specifier|static
specifier|final
name|double
name|POLY_TOLERANCE
init|=
literal|1e-7
decl_stmt|;
annotation|@
name|Override
DECL|method|polyRectContainsPoint
specifier|protected
name|Boolean
name|polyRectContainsPoint
parameter_list|(
name|GeoRect
name|rect
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|rect
operator|.
name|minLat
operator|-
name|pointLat
argument_list|)
operator|<
name|POLY_TOLERANCE
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|rect
operator|.
name|maxLat
operator|-
name|pointLat
argument_list|)
operator|<
name|POLY_TOLERANCE
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|rect
operator|.
name|minLon
operator|-
name|pointLon
argument_list|)
operator|<
name|POLY_TOLERANCE
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|rect
operator|.
name|maxLon
operator|-
name|pointLon
argument_list|)
operator|<
name|POLY_TOLERANCE
condition|)
block|{
comment|// The poly check quantizes slightly differently, so we allow for boundary cases to disagree
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|rectContainsPoint
argument_list|(
name|rect
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|circleContainsPoint
specifier|protected
name|Boolean
name|circleContainsPoint
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
name|double
name|distanceKM
init|=
name|SloppyMath
operator|.
name|haversin
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|distanceKM
operator|*
literal|1000.0
operator|<=
name|radiusMeters
decl_stmt|;
comment|//System.out.println("  shouldMatch?  centerLon=" + centerLon + " centerLat=" + centerLat + " pointLon=" + pointLon + " pointLat=" + pointLat + " result=" + result + " distanceMeters=" + (distanceKM * 1000));
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|distanceRangeContainsPoint
specifier|protected
name|Boolean
name|distanceRangeContainsPoint
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|minRadiusMeters
parameter_list|,
name|double
name|radiusMeters
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
specifier|final
name|double
name|d
init|=
name|SloppyMath
operator|.
name|haversin
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
operator|*
literal|1000.0
decl_stmt|;
return|return
name|d
operator|>=
name|minRadiusMeters
operator|&&
name|d
operator|<=
name|radiusMeters
return|;
block|}
DECL|method|testEncodeDecode
specifier|public
name|void
name|testEncodeDecode
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|boolean
name|small
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|double
name|lat
init|=
name|randomLat
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|double
name|latQuantized
init|=
name|BKDTreeWriter
operator|.
name|decodeLat
argument_list|(
name|BKDTreeWriter
operator|.
name|encodeLat
argument_list|(
name|lat
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|lat
argument_list|,
name|latQuantized
argument_list|,
name|BKDTreeWriter
operator|.
name|TOLERANCE
argument_list|)
expr_stmt|;
name|double
name|lon
init|=
name|randomLon
argument_list|(
name|small
argument_list|)
decl_stmt|;
name|double
name|lonQuantized
init|=
name|BKDTreeWriter
operator|.
name|decodeLon
argument_list|(
name|BKDTreeWriter
operator|.
name|encodeLon
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|lon
argument_list|,
name|lonQuantized
argument_list|,
name|BKDTreeWriter
operator|.
name|TOLERANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEncodeDecodeMax
specifier|public
name|void
name|testEncodeDecodeMax
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|x
init|=
name|BKDTreeWriter
operator|.
name|encodeLat
argument_list|(
name|Math
operator|.
name|nextAfter
argument_list|(
literal|90.0
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|x
operator|<
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|int
name|y
init|=
name|BKDTreeWriter
operator|.
name|encodeLon
argument_list|(
name|Math
operator|.
name|nextAfter
argument_list|(
literal|180.0
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|y
operator|<
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|testAccountableHasDelegate
specifier|public
name|void
name|testAccountableHasDelegate
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|getDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysDocValuesFormat
argument_list|(
name|getDocValuesFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BKDPointField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|-
literal|18.2861
argument_list|,
literal|147.7
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
comment|// We can't wrap with "exotic" readers because the BKD query must see the BKDDVFormat:
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Need to run a query so the DV field is really loaded:
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|BKDPointInBBoxQuery
argument_list|(
name|FIELD_NAME
argument_list|,
operator|-
literal|30
argument_list|,
literal|0
argument_list|,
literal|140
argument_list|,
literal|150
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Accountables
operator|.
name|toString
argument_list|(
operator|(
name|Accountable
operator|)
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"delegate"
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|w
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocValuesFormat
specifier|private
specifier|static
name|DocValuesFormat
name|getDocValuesFormat
parameter_list|()
block|{
name|int
name|maxPointsInLeaf
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|16
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|int
name|maxPointsSortInHeap
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxPointsInLeaf
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  BKD params: maxPointsInLeaf="
operator|+
name|maxPointsInLeaf
operator|+
literal|" maxPointsSortInHeap="
operator|+
name|maxPointsSortInHeap
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BKDTreeDocValuesFormat
argument_list|(
name|maxPointsInLeaf
argument_list|,
name|maxPointsSortInHeap
argument_list|)
return|;
block|}
DECL|method|getDirectory
specifier|private
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|noVirusChecker
argument_list|(
name|newDirectory
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
