begin_unit
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|text
operator|.
name|ParseException
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|DoubleField
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
name|expressions
operator|.
name|Expression
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
name|expressions
operator|.
name|SimpleBindings
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
name|expressions
operator|.
name|js
operator|.
name|JavascriptCompiler
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
name|facet
operator|.
name|DrillDownQuery
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
name|facet
operator|.
name|FacetResult
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
name|facet
operator|.
name|Facets
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
name|facet
operator|.
name|FacetsCollector
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
name|facet
operator|.
name|range
operator|.
name|DoubleRange
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
name|facet
operator|.
name|range
operator|.
name|DoubleRangeFacetCounts
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
name|ConstantScoreQuery
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
begin_comment
comment|/** Shows simple usage of dynamic range faceting, using the  *  expressions module to calculate distance. */
end_comment
begin_class
DECL|class|DistanceFacetsExample
specifier|public
class|class
name|DistanceFacetsExample
implements|implements
name|Closeable
block|{
DECL|field|ONE_KM
specifier|final
name|DoubleRange
name|ONE_KM
init|=
operator|new
name|DoubleRange
argument_list|(
literal|"< 1 km"
argument_list|,
literal|0.0
argument_list|,
literal|true
argument_list|,
literal|1.0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|TWO_KM
specifier|final
name|DoubleRange
name|TWO_KM
init|=
operator|new
name|DoubleRange
argument_list|(
literal|"< 2 km"
argument_list|,
literal|0.0
argument_list|,
literal|true
argument_list|,
literal|2.0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|FIVE_KM
specifier|final
name|DoubleRange
name|FIVE_KM
init|=
operator|new
name|DoubleRange
argument_list|(
literal|"< 5 km"
argument_list|,
literal|0.0
argument_list|,
literal|true
argument_list|,
literal|5.0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|TEN_KM
specifier|final
name|DoubleRange
name|TEN_KM
init|=
operator|new
name|DoubleRange
argument_list|(
literal|"< 10 km"
argument_list|,
literal|0.0
argument_list|,
literal|true
argument_list|,
literal|10.0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|indexDir
specifier|private
specifier|final
name|Directory
name|indexDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/** Empty constructor */
DECL|method|DistanceFacetsExample
specifier|public
name|DistanceFacetsExample
parameter_list|()
block|{}
comment|/** Build the example index. */
DECL|method|index
specifier|public
name|void
name|index
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|FacetExamples
operator|.
name|EXAMPLES_VER
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|FacetExamples
operator|.
name|EXAMPLES_VER
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add documents with latitude/longitude location:
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
name|DoubleField
argument_list|(
literal|"latitude"
argument_list|,
literal|40.759011
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"longitude"
argument_list|,
operator|-
literal|73.9844722
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"latitude"
argument_list|,
literal|40.718266
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"longitude"
argument_list|,
operator|-
literal|74.007819
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"latitude"
argument_list|,
literal|40.7051157
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"longitude"
argument_list|,
operator|-
literal|74.0088305
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// Open near-real-time searcher
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDistanceValueSource
specifier|private
name|ValueSource
name|getDistanceValueSource
parameter_list|()
block|{
name|Expression
name|distance
decl_stmt|;
try|try
block|{
name|distance
operator|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"haversin(40.7143528,-74.0059731,latitude,longitude)"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
comment|// Should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|pe
argument_list|)
throw|;
block|}
name|SimpleBindings
name|bindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"latitude"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
name|bindings
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"longitude"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|distance
operator|.
name|getValueSource
argument_list|(
name|bindings
argument_list|)
return|;
block|}
comment|/** User runs a query and counts facets. */
DECL|method|search
specifier|public
name|FacetResult
name|search
parameter_list|()
throws|throws
name|IOException
block|{
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|DoubleRangeFacetCounts
argument_list|(
literal|"field"
argument_list|,
name|getDistanceValueSource
argument_list|()
argument_list|,
name|fc
argument_list|,
name|ONE_KM
argument_list|,
name|TWO_KM
argument_list|,
name|FIVE_KM
argument_list|,
name|TEN_KM
argument_list|)
decl_stmt|;
return|return
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"field"
argument_list|)
return|;
block|}
comment|/** User drills down on the specified range. */
DECL|method|drillDown
specifier|public
name|TopDocs
name|drillDown
parameter_list|(
name|DoubleRange
name|range
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Passing no baseQuery means we drill down on all
comment|// documents ("browse only"):
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"field"
argument_list|,
operator|new
name|ConstantScoreQuery
argument_list|(
name|range
operator|.
name|getFilter
argument_list|(
name|getDistanceValueSource
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Runs the search and drill-down examples and prints the results. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|DistanceFacetsExample
name|example
init|=
operator|new
name|DistanceFacetsExample
argument_list|()
decl_stmt|;
name|example
operator|.
name|index
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Distance facet counting example:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-----------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|example
operator|.
name|search
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Distance facet drill-down example (field/< 2 km):"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------------"
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|example
operator|.
name|drillDown
argument_list|(
name|example
operator|.
name|TWO_KM
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|totalHits
operator|+
literal|" totalHits"
argument_list|)
expr_stmt|;
name|example
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit