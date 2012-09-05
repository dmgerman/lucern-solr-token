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
name|io
operator|.
name|ShapeReadWriter
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
name|io
operator|.
name|sample
operator|.
name|SampleData
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
name|io
operator|.
name|sample
operator|.
name|SampleDataReader
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
name|document
operator|.
name|StringField
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
name|IndexableField
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
name|junit
operator|.
name|Assert
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
name|io
operator|.
name|InputStream
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
name|Collections
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
name|Iterator
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_class
DECL|class|StrategyTestCase
specifier|public
specifier|abstract
class|class
name|StrategyTestCase
extends|extends
name|SpatialTestCase
block|{
DECL|field|DATA_SIMPLE_BBOX
specifier|public
specifier|static
specifier|final
name|String
name|DATA_SIMPLE_BBOX
init|=
literal|"simple-bbox.txt"
decl_stmt|;
DECL|field|DATA_STATES_POLY
specifier|public
specifier|static
specifier|final
name|String
name|DATA_STATES_POLY
init|=
literal|"states-poly.txt"
decl_stmt|;
DECL|field|DATA_STATES_BBOX
specifier|public
specifier|static
specifier|final
name|String
name|DATA_STATES_BBOX
init|=
literal|"states-bbox.txt"
decl_stmt|;
DECL|field|DATA_COUNTRIES_POLY
specifier|public
specifier|static
specifier|final
name|String
name|DATA_COUNTRIES_POLY
init|=
literal|"countries-poly.txt"
decl_stmt|;
DECL|field|DATA_COUNTRIES_BBOX
specifier|public
specifier|static
specifier|final
name|String
name|DATA_COUNTRIES_BBOX
init|=
literal|"countries-bbox.txt"
decl_stmt|;
DECL|field|DATA_WORLD_CITIES_POINTS
specifier|public
specifier|static
specifier|final
name|String
name|DATA_WORLD_CITIES_POINTS
init|=
literal|"world-cities-points.txt"
decl_stmt|;
DECL|field|QTEST_States_IsWithin_BBox
specifier|public
specifier|static
specifier|final
name|String
name|QTEST_States_IsWithin_BBox
init|=
literal|"states-IsWithin-BBox.txt"
decl_stmt|;
DECL|field|QTEST_States_Intersects_BBox
specifier|public
specifier|static
specifier|final
name|String
name|QTEST_States_Intersects_BBox
init|=
literal|"states-Intersects-BBox.txt"
decl_stmt|;
DECL|field|QTEST_Cities_IsWithin_BBox
specifier|public
specifier|static
specifier|final
name|String
name|QTEST_Cities_IsWithin_BBox
init|=
literal|"cities-IsWithin-BBox.txt"
decl_stmt|;
DECL|field|QTEST_Simple_Queries_BBox
specifier|public
specifier|static
specifier|final
name|String
name|QTEST_Simple_Queries_BBox
init|=
literal|"simple-Queries-BBox.txt"
decl_stmt|;
DECL|field|log
specifier|private
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|argsParser
specifier|protected
specifier|final
name|SpatialArgsParser
name|argsParser
init|=
operator|new
name|SpatialArgsParser
argument_list|()
decl_stmt|;
DECL|field|strategy
specifier|protected
name|SpatialStrategy
name|strategy
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|storeShape
specifier|protected
name|boolean
name|storeShape
init|=
literal|true
decl_stmt|;
DECL|method|executeQueries
specifier|protected
name|void
name|executeQueries
parameter_list|(
name|SpatialMatchConcern
name|concern
parameter_list|,
name|String
modifier|...
name|testQueryFile
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"testing queried for strategy "
operator|+
name|strategy
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|testQueryFile
control|)
block|{
name|Iterator
argument_list|<
name|SpatialTestQuery
argument_list|>
name|testQueryIterator
init|=
name|getTestQueries
argument_list|(
name|path
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|runTestQueries
argument_list|(
name|testQueryIterator
argument_list|,
name|concern
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAddAndVerifyIndexedDocuments
specifier|protected
name|void
name|getAddAndVerifyIndexedDocuments
parameter_list|(
name|String
name|testDataFile
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|testDocuments
init|=
name|getDocuments
argument_list|(
name|testDataFile
argument_list|)
decl_stmt|;
name|addDocumentsAndCommit
argument_list|(
name|testDocuments
argument_list|)
expr_stmt|;
name|verifyDocumentsIndexed
argument_list|(
name|testDocuments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getDocuments
specifier|protected
name|List
argument_list|<
name|Document
argument_list|>
name|getDocuments
parameter_list|(
name|String
name|testDataFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|SampleData
argument_list|>
name|sampleData
init|=
name|getSampleData
argument_list|(
name|testDataFile
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|documents
init|=
operator|new
name|ArrayList
argument_list|<
name|Document
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|sampleData
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SampleData
name|data
init|=
name|sampleData
operator|.
name|next
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|data
operator|.
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"name"
argument_list|,
name|data
operator|.
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|Shape
name|shape
init|=
operator|new
name|ShapeReadWriter
argument_list|(
name|ctx
argument_list|)
operator|.
name|readShape
argument_list|(
name|data
operator|.
name|shape
argument_list|)
decl_stmt|;
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
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeShape
condition|)
name|document
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
name|ctx
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
return|return
name|documents
return|;
block|}
DECL|method|getSampleData
specifier|protected
name|Iterator
argument_list|<
name|SampleData
argument_list|>
name|getSampleData
parameter_list|(
name|String
name|testDataFile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SampleDataReader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"data/"
operator|+
name|testDataFile
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTestQueries
specifier|protected
name|Iterator
argument_list|<
name|SpatialTestQuery
argument_list|>
name|getTestQueries
parameter_list|(
name|String
name|testQueryFile
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|testQueryFile
argument_list|)
decl_stmt|;
return|return
name|SpatialTestQuery
operator|.
name|getTestQueries
argument_list|(
name|argsParser
argument_list|,
name|ctx
argument_list|,
name|testQueryFile
argument_list|,
name|in
argument_list|)
return|;
block|}
DECL|method|runTestQueries
specifier|public
name|void
name|runTestQueries
parameter_list|(
name|Iterator
argument_list|<
name|SpatialTestQuery
argument_list|>
name|queries
parameter_list|,
name|SpatialMatchConcern
name|concern
parameter_list|)
block|{
while|while
condition|(
name|queries
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpatialTestQuery
name|q
init|=
name|queries
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|msg
init|=
name|q
operator|.
name|line
decl_stmt|;
comment|//"Query: " + q.args.toString(ctx);
name|SearchResults
name|got
init|=
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
name|q
operator|.
name|args
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|storeShape
operator|&&
name|got
operator|.
name|numFound
operator|>
literal|0
condition|)
block|{
comment|//check stored value is there& parses
name|assertNotNull
argument_list|(
operator|new
name|ShapeReadWriter
argument_list|(
name|ctx
argument_list|)
operator|.
name|readShape
argument_list|(
name|got
operator|.
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|document
operator|.
name|get
argument_list|(
name|strategy
operator|.
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|concern
operator|.
name|orderIsImportant
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|q
operator|.
name|ids
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|SearchResult
name|r
range|:
name|got
operator|.
name|results
control|)
block|{
name|String
name|id
init|=
name|r
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ids
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|msg
operator|+
literal|" :: Did not get enough results.  Expect"
operator|+
name|q
operator|.
name|ids
operator|+
literal|", got: "
operator|+
name|got
operator|.
name|toDebugString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"out of order: "
operator|+
name|msg
argument_list|,
name|ids
operator|.
name|next
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ids
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|msg
operator|+
literal|" :: expect more results then we got: "
operator|+
name|ids
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// We are looking at how the results overlap
if|if
condition|(
name|concern
operator|.
name|resultsAreSuperset
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|found
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SearchResult
name|r
range|:
name|got
operator|.
name|results
control|)
block|{
name|found
operator|.
name|add
argument_list|(
name|r
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|q
operator|.
name|ids
control|)
block|{
if|if
condition|(
operator|!
name|found
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Results are mising id: "
operator|+
name|s
operator|+
literal|" :: "
operator|+
name|found
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|found
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SearchResult
name|r
range|:
name|got
operator|.
name|results
control|)
block|{
name|found
operator|.
name|add
argument_list|(
name|r
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sort both so that the order is not important
name|Collections
operator|.
name|sort
argument_list|(
name|q
operator|.
name|ids
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|found
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|q
operator|.
name|ids
operator|.
name|toString
argument_list|()
argument_list|,
name|found
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
