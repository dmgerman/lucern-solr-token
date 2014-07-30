begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|MockAnalyzer
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
name|MockTokenizer
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
name|TextField
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
name|taxonomy
operator|.
name|TaxonomyReader
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
name|taxonomy
operator|.
name|TaxonomyWriter
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
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|AtomicReader
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
name|AtomicReaderContext
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
operator|.
name|OpenMode
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
name|util
operator|.
name|IOUtils
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
begin_class
DECL|class|TestMultipleIndexFields
specifier|public
class|class
name|TestMultipleIndexFields
extends|extends
name|FacetTestCase
block|{
DECL|field|CATEGORIES
specifier|private
specifier|static
specifier|final
name|FacetField
index|[]
name|CATEGORIES
init|=
operator|new
name|FacetField
index|[]
block|{
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Mark Twain"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Stephen King"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Kurt Vonnegut"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"The Beatles"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Band"
argument_list|,
literal|"Punk"
argument_list|,
literal|"The Ramones"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"U2"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"REM"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|,
literal|"Dave Matthews Band"
argument_list|)
block|,
operator|new
name|FacetField
argument_list|(
literal|"Composer"
argument_list|,
literal|"Bach"
argument_list|)
block|,   }
decl_stmt|;
DECL|method|getConfig
specifier|private
name|FacetsConfig
name|getConfig
parameter_list|()
block|{
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setHierarchical
argument_list|(
literal|"Band"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
annotation|@
name|Test
DECL|method|testDefault
specifier|public
name|void
name|testDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|seedIndex
argument_list|(
name|tw
argument_list|,
name|iw
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
name|performSearch
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|)
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$facets"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|tw
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustom
specifier|public
name|void
name|testCustom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Author"
argument_list|,
literal|"$author"
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|tw
argument_list|,
name|iw
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
name|performSearch
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|facetsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Author"
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|,
literal|"$author"
argument_list|)
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|MultiFacets
argument_list|(
name|facetsMap
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|)
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facets
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$facets"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$author"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|tw
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoCustomsSameField
specifier|public
name|void
name|testTwoCustomsSameField
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Band"
argument_list|,
literal|"$music"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Composer"
argument_list|,
literal|"$music"
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|tw
argument_list|,
name|iw
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
name|performSearch
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|facetsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Facets
name|facets2
init|=
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|,
literal|"$music"
argument_list|)
decl_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Band"
argument_list|,
name|facets2
argument_list|)
expr_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Composer"
argument_list|,
name|facets2
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|MultiFacets
argument_list|(
name|facetsMap
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|)
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facets
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$facets"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$music"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$music"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|tw
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|assertOrdinalsExist
specifier|private
name|void
name|assertOrdinalsExist
parameter_list|(
name|String
name|field
parameter_list|,
name|IndexReader
name|ir
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|ir
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|r
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return;
comment|// not all segments must have this DocValues
block|}
block|}
name|fail
argument_list|(
literal|"no ordinals found for "
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDifferentFieldsAndText
specifier|public
name|void
name|testDifferentFieldsAndText
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Band"
argument_list|,
literal|"$bands"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Composer"
argument_list|,
literal|"$composers"
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|tw
argument_list|,
name|iw
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
name|performSearch
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|facetsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Band"
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|,
literal|"$bands"
argument_list|)
argument_list|)
expr_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Composer"
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|,
literal|"$composers"
argument_list|)
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|MultiFacets
argument_list|(
name|facetsMap
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|)
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facets
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$facets"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$bands"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$composers"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|tw
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSomeSameSomeDifferent
specifier|public
name|void
name|testSomeSameSomeDifferent
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create and open an index writer
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Band"
argument_list|,
literal|"$music"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Composer"
argument_list|,
literal|"$music"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"Author"
argument_list|,
literal|"$literature"
argument_list|)
expr_stmt|;
name|seedIndex
argument_list|(
name|tw
argument_list|,
name|iw
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// prepare index reader and taxonomy.
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
name|performSearch
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|facetsMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Facets
name|facets2
init|=
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|,
literal|"$music"
argument_list|)
decl_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Band"
argument_list|,
name|facets2
argument_list|)
expr_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Composer"
argument_list|,
name|facets2
argument_list|)
expr_stmt|;
name|facetsMap
operator|.
name|put
argument_list|(
literal|"Author"
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|,
literal|"$literature"
argument_list|)
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|MultiFacets
argument_list|(
name|facetsMap
argument_list|,
name|getTaxonomyFacetCounts
argument_list|(
name|tr
argument_list|,
name|config
argument_list|,
name|sfc
argument_list|)
argument_list|)
decl_stmt|;
comment|// Obtain facets results and hand-test them
name|assertCorrectResults
argument_list|(
name|facets
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$music"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|assertOrdinalsExist
argument_list|(
literal|"$literature"
argument_list|,
name|ir
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|tr
argument_list|,
name|ir
argument_list|,
name|iw
argument_list|,
name|tw
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCorrectResults
specifier|private
name|void
name|assertCorrectResults
parameter_list|(
name|Facets
name|facets
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|facets
operator|.
name|getSpecificValue
argument_list|(
literal|"Band"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=Band path=[] value=5 childCount=2\n  Rock& Pop (4)\n  Punk (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"Band"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=Band path=[Rock& Pop] value=4 childCount=4\n  The Beatles (1)\n  U2 (1)\n  REM (1)\n  Dave Matthews Band (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"Band"
argument_list|,
literal|"Rock& Pop"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=Author path=[] value=3 childCount=3\n  Mark Twain (1)\n  Stephen King (1)\n  Kurt Vonnegut (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"Author"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|performSearch
specifier|private
name|FacetsCollector
name|performSearch
parameter_list|(
name|TaxonomyReader
name|tr
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
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
return|return
name|fc
return|;
block|}
DECL|method|seedIndex
specifier|private
name|void
name|seedIndex
parameter_list|(
name|TaxonomyWriter
name|tw
parameter_list|,
name|RandomIndexWriter
name|iw
parameter_list|,
name|FacetsConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FacetField
name|ff
range|:
name|CATEGORIES
control|)
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
name|ff
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"alpha"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|tw
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
