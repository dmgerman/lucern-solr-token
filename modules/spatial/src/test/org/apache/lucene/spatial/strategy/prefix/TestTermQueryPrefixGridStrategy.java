begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.strategy.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|strategy
operator|.
name|prefix
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
name|StringField
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
name|simple
operator|.
name|SimpleSpatialContext
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
name|prefix
operator|.
name|quad
operator|.
name|QuadPrefixTree
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
name|query
operator|.
name|SpatialArgsParser
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
name|simple
operator|.
name|PointImpl
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
name|strategy
operator|.
name|SimpleSpatialFieldInfo
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
name|SpatialTestCase
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
name|Arrays
import|;
end_import
begin_class
DECL|class|TestTermQueryPrefixGridStrategy
specifier|public
class|class
name|TestTermQueryPrefixGridStrategy
extends|extends
name|SpatialTestCase
block|{
annotation|@
name|Test
DECL|method|testNGramPrefixGridLosAngeles
specifier|public
name|void
name|testNGramPrefixGridLosAngeles
parameter_list|()
throws|throws
name|IOException
block|{
name|SimpleSpatialFieldInfo
name|fieldInfo
init|=
operator|new
name|SimpleSpatialFieldInfo
argument_list|(
literal|"geo"
argument_list|)
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SimpleSpatialContext
operator|.
name|GEO_KM
decl_stmt|;
name|TermQueryPrefixTreeStrategy
name|prefixGridStrategy
init|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
name|Shape
name|point
init|=
operator|new
name|PointImpl
argument_list|(
operator|-
literal|118.243680
argument_list|,
literal|34.052230
argument_list|)
decl_stmt|;
name|Document
name|losAngeles
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|losAngeles
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
literal|"Los Angeles"
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|losAngeles
operator|.
name|add
argument_list|(
name|prefixGridStrategy
operator|.
name|createField
argument_list|(
name|fieldInfo
argument_list|,
name|point
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addDocumentsAndCommit
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|losAngeles
argument_list|)
argument_list|)
expr_stmt|;
comment|// This won't work with simple spatial context...
name|SpatialArgsParser
name|spatialArgsParser
init|=
operator|new
name|SpatialArgsParser
argument_list|()
decl_stmt|;
comment|// TODO... use a non polygon query
comment|//    SpatialArgs spatialArgs = spatialArgsParser.parse(
comment|//        "IsWithin(POLYGON((-127.00390625 39.8125,-112.765625 39.98828125,-111.53515625 31.375,-125.94921875 30.14453125,-127.00390625 39.8125)))",
comment|//        new SimpleSpatialContext());
comment|//    Query query = prefixGridStrategy.makeQuery(spatialArgs, fieldInfo);
comment|//    SearchResults searchResults = executeQuery(query, 1);
comment|//    assertEquals(1, searchResults.numFound);
block|}
block|}
end_class
end_unit
