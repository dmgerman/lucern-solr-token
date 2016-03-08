begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|distance
operator|.
name|DistanceUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
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
name|document
operator|.
name|Field
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
name|NumericDocValuesField
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
name|StoredField
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
name|DirectoryReader
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
name|IndexWriter
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|Sort
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
name|SortField
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
name|SpatialArgsParser
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
name|store
operator|.
name|RAMDirectory
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
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * This class serves as example code to show how to use the Lucene spatial  * module.  */
end_comment
begin_class
DECL|class|SpatialExample
specifier|public
class|class
name|SpatialExample
extends|extends
name|LuceneTestCase
block|{
comment|//Note: Test invoked via TestTestFramework.spatialExample()
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|SpatialExample
argument_list|()
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|()
expr_stmt|;
name|indexPoints
argument_list|()
expr_stmt|;
name|search
argument_list|()
expr_stmt|;
block|}
comment|/**    * The Spatial4j {@link SpatialContext} is a sort of global-ish singleton    * needed by Lucene spatial.  It's a facade to the rest of Spatial4j, acting    * as a factory for {@link Shape}s and provides access to reading and writing    * them from Strings.    */
DECL|field|ctx
specifier|private
name|SpatialContext
name|ctx
decl_stmt|;
comment|//"ctx" is the conventional variable name
comment|/**    * The Lucene spatial {@link SpatialStrategy} encapsulates an approach to    * indexing and searching shapes, and providing distance values for them.    * It's a simple API to unify different approaches. You might use more than    * one strategy for a shape as each strategy has its strengths and weaknesses.    *<p />    * Note that these are initialized with a field name.    */
DECL|field|strategy
specifier|private
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|()
block|{
comment|//Typical geospatial context
comment|//  These can also be constructed from SpatialContextFactory
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
name|int
name|maxLevels
init|=
literal|11
decl_stmt|;
comment|//results in sub-meter precision for geohash
comment|//TODO demo lookup by detail distance
comment|//  This can also be constructed from SpatialPrefixTreeFactory
name|SpatialPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
name|maxLevels
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
literal|"myGeoField"
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
block|}
DECL|method|indexPoints
specifier|private
name|void
name|indexPoints
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriterConfig
name|iwConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|iwConfig
argument_list|)
decl_stmt|;
comment|//Spatial4j is x-y order for arguments
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newSampleDocument
argument_list|(
literal|2
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|80.93
argument_list|,
literal|33.77
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Spatial4j has a WKT parser which is also "x y" order
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newSampleDocument
argument_list|(
literal|4
argument_list|,
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
literal|"POINT(60.9289094 -50.7693246)"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newSampleDocument
argument_list|(
literal|20
argument_list|,
name|ctx
operator|.
name|makePoint
argument_list|(
literal|0.1
argument_list|,
literal|0.1
argument_list|)
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
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|newSampleDocument
specifier|private
name|Document
name|newSampleDocument
parameter_list|(
name|int
name|id
parameter_list|,
name|Shape
modifier|...
name|shapes
parameter_list|)
block|{
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
name|StoredField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
comment|//Potentially more than one shape in this field is supported by some
comment|// strategies; see the javadocs of the SpatialStrategy impl to see.
for|for
control|(
name|Shape
name|shape
range|:
name|shapes
control|)
block|{
for|for
control|(
name|Field
name|f
range|:
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|//store it too; the format is up to you
comment|//  (assume point in this example)
name|Point
name|pt
init|=
operator|(
name|Point
operator|)
name|shape
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|pt
operator|.
name|getX
argument_list|()
operator|+
literal|" "
operator|+
name|pt
operator|.
name|getY
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|search
specifier|private
name|void
name|search
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|Sort
name|idSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
decl_stmt|;
comment|//--Filter by circle (<= distance from a point)
block|{
comment|//Search with circle
comment|//note: SpatialArgs can be parsed from a string
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
name|ctx
operator|.
name|makeCircle
argument_list|(
operator|-
literal|80.0
argument_list|,
literal|33.0
argument_list|,
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
literal|200
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|TopDocs
name|docs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
name|idSort
argument_list|)
decl_stmt|;
name|assertDocMatchedIds
argument_list|(
name|indexSearcher
argument_list|,
name|docs
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//Now, lets get the distance for the 1st doc via computing from stored point value:
comment|// (this computation is usually not redundant)
name|Document
name|doc1
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|doc1Str
init|=
name|doc1
operator|.
name|getField
argument_list|(
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|)
operator|.
name|stringValue
argument_list|()
decl_stmt|;
comment|//assume doc1Str is "x y" as written in newSampleDocument()
name|int
name|spaceIdx
init|=
name|doc1Str
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|double
name|x
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|doc1Str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|spaceIdx
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|y
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|doc1Str
operator|.
name|substring
argument_list|(
name|spaceIdx
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|doc1DistDEG
init|=
name|ctx
operator|.
name|calcDistance
argument_list|(
name|args
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|121.6d
argument_list|,
name|DistanceUtils
operator|.
name|degrees2Dist
argument_list|(
name|doc1DistDEG
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|)
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
comment|//or more simply:
name|assertEquals
argument_list|(
literal|121.6d
argument_list|,
name|doc1DistDEG
operator|*
name|DistanceUtils
operator|.
name|DEG_TO_KM
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
block|}
comment|//--Match all, order by distance ascending
block|{
name|Point
name|pt
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
literal|60
argument_list|,
operator|-
literal|50
argument_list|)
decl_stmt|;
name|ValueSource
name|valueSource
init|=
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|pt
argument_list|,
name|DistanceUtils
operator|.
name|DEG_TO_KM
argument_list|)
decl_stmt|;
comment|//the distance (in km)
name|Sort
name|distSort
init|=
operator|new
name|Sort
argument_list|(
name|valueSource
operator|.
name|getSortField
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|rewrite
argument_list|(
name|indexSearcher
argument_list|)
decl_stmt|;
comment|//false=asc dist
name|TopDocs
name|docs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|,
name|distSort
argument_list|)
decl_stmt|;
name|assertDocMatchedIds
argument_list|(
name|indexSearcher
argument_list|,
name|docs
argument_list|,
literal|4
argument_list|,
literal|20
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//To get the distance, we could compute from stored values like earlier.
comment|// However in this example we sorted on it, and the distance will get
comment|// computed redundantly.  If the distance is only needed for the top-X
comment|// search results then that's not a big deal. Alternatively, try wrapping
comment|// the ValueSource with CachingDoubleValueSource then retrieve the value
comment|// from the ValueSource now. See LUCENE-4541 for an example.
block|}
comment|//demo arg parsing
block|{
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
name|ctx
operator|.
name|makeCircle
argument_list|(
operator|-
literal|80.0
argument_list|,
literal|33.0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args2
init|=
operator|new
name|SpatialArgsParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"Intersects(BUFFER(POINT(-80 33),1))"
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|args
operator|.
name|toString
argument_list|()
argument_list|,
name|args2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertDocMatchedIds
specifier|private
name|void
name|assertDocMatchedIds
parameter_list|(
name|IndexSearcher
name|indexSearcher
parameter_list|,
name|TopDocs
name|docs
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
name|gotIds
init|=
operator|new
name|int
index|[
name|docs
operator|.
name|totalHits
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|gotIds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|gotIds
index|[
name|i
index|]
operator|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|ids
argument_list|,
name|gotIds
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit