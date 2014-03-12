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
name|IOException
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
name|List
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
name|FacetsConfig
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
name|sortedset
operator|.
name|DefaultSortedSetDocValuesReaderState
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
name|sortedset
operator|.
name|SortedSetDocValuesFacetCounts
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
name|sortedset
operator|.
name|SortedSetDocValuesFacetField
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
name|sortedset
operator|.
name|SortedSetDocValuesReaderState
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
comment|/** Shows simple usage of faceted indexing and search,  *  using {@link SortedSetDocValuesFacetField} and {@link  *  SortedSetDocValuesFacetCounts}.  */
end_comment
begin_class
DECL|class|SimpleSortedSetFacetsExample
specifier|public
class|class
name|SimpleSortedSetFacetsExample
block|{
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
DECL|field|config
specifier|private
specifier|final
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
comment|/** Empty constructor */
DECL|method|SimpleSortedSetFacetsExample
specifier|public
name|SimpleSortedSetFacetsExample
parameter_list|()
block|{   }
comment|/** Build the example index. */
DECL|method|index
specifier|private
name|void
name|index
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|indexWriter
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2010"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2010"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Lisa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2012"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Susan"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2012"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Frank"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"1999"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** User runs a query and counts facets. */
DECL|method|search
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|search
parameter_list|()
throws|throws
name|IOException
block|{
name|DirectoryReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
comment|// Aggregatses the facet counts
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
comment|// MatchAllDocsQuery is for "browsing" (counts facets
comment|// for all non-deleted docs in the index); normally
comment|// you'd use a "normal" query:
name|FacetsCollector
operator|.
name|search
argument_list|(
name|searcher
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|,
name|fc
argument_list|)
expr_stmt|;
comment|// Retrieve results
name|Facets
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"Author"
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"Publish Year"
argument_list|)
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|results
return|;
block|}
comment|/** User drills down on 'Publish Year/2010'. */
DECL|method|drillDown
specifier|private
name|FacetResult
name|drillDown
parameter_list|()
throws|throws
name|IOException
block|{
name|DirectoryReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
comment|// Now user drills down on Publish Year/2010:
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"Publish Year"
argument_list|,
literal|"2010"
argument_list|)
expr_stmt|;
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|FacetsCollector
operator|.
name|search
argument_list|(
name|searcher
argument_list|,
name|q
argument_list|,
literal|10
argument_list|,
name|fc
argument_list|)
expr_stmt|;
comment|// Retrieve results
name|Facets
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|FacetResult
name|result
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"Author"
argument_list|)
decl_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** Runs the search example. */
DECL|method|runSearch
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|runSearch
parameter_list|()
throws|throws
name|IOException
block|{
name|index
argument_list|()
expr_stmt|;
return|return
name|search
argument_list|()
return|;
block|}
comment|/** Runs the drill-down example. */
DECL|method|runDrillDown
specifier|public
name|FacetResult
name|runDrillDown
parameter_list|()
throws|throws
name|IOException
block|{
name|index
argument_list|()
expr_stmt|;
return|return
name|drillDown
argument_list|()
return|;
block|}
comment|/** Runs the search and drill-down examples and prints the results. */
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Facet counting example:"
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
name|SimpleSortedSetFacetsExample
name|example
init|=
operator|new
name|SimpleSortedSetFacetsExample
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|example
operator|.
name|runSearch
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Author: "
operator|+
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Publish Year: "
operator|+
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
literal|"Facet drill-down example (Publish Year/2010):"
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Author: "
operator|+
name|example
operator|.
name|runDrillDown
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
