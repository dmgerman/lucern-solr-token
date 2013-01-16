begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|params
package|;
end_package
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|index
operator|.
name|FacetFields
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|PerDimensionIndexingParams
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
name|search
operator|.
name|CategoryListIterator
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
name|search
operator|.
name|DocValuesCategoryListIterator
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
name|CategoryPath
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
name|facet
operator|.
name|util
operator|.
name|MultiCategoryListIterator
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
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
name|encoding
operator|.
name|IntDecoder
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|MultiCategoryListIteratorTest
specifier|public
class|class
name|MultiCategoryListIteratorTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testMultipleCategoryLists
specifier|public
name|void
name|testMultipleCategoryLists
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|numDimensions
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// at least 2 dimensions
name|String
index|[]
name|dimensions
init|=
operator|new
name|String
index|[
name|numDimensions
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
name|numDimensions
condition|;
name|i
operator|++
control|)
block|{
name|dimensions
index|[
name|i
index|]
operator|=
literal|"dim"
operator|+
name|i
expr_stmt|;
block|}
comment|// build the PerDimensionIndexingParams
name|HashMap
argument_list|<
name|CategoryPath
argument_list|,
name|CategoryListParams
argument_list|>
name|clps
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryPath
argument_list|,
name|CategoryListParams
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dim
range|:
name|dimensions
control|)
block|{
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
name|dim
argument_list|)
decl_stmt|;
name|CategoryListParams
name|clp
init|=
operator|new
name|CategoryListParams
argument_list|(
literal|"$"
operator|+
name|dim
argument_list|)
decl_stmt|;
name|clps
operator|.
name|put
argument_list|(
name|cp
argument_list|,
name|clp
argument_list|)
expr_stmt|;
block|}
name|PerDimensionIndexingParams
name|indexingParams
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|(
name|clps
argument_list|)
decl_stmt|;
comment|// index some documents
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
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxoWriter
argument_list|,
name|indexingParams
argument_list|)
decl_stmt|;
name|int
name|ndocs
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|10
argument_list|)
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|numCategories
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numDimensions
argument_list|)
operator|+
literal|1
decl_stmt|;
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
name|categories
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numCategories
condition|;
name|j
operator|++
control|)
block|{
name|String
name|dimension
init|=
name|dimensions
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|dimensions
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|categories
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|dimension
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|categories
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
comment|// test the multi iterator
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
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|CategoryListIterator
index|[]
name|iterators
init|=
operator|new
name|CategoryListIterator
index|[
name|numDimensions
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
name|iterators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CategoryListParams
name|clp
init|=
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|dimensions
index|[
name|i
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|IntDecoder
name|decoder
init|=
name|clp
operator|.
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
decl_stmt|;
name|iterators
index|[
name|i
index|]
operator|=
operator|new
name|DocValuesCategoryListIterator
argument_list|(
name|clp
operator|.
name|field
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
block|}
name|MultiCategoryListIterator
name|cli
init|=
operator|new
name|MultiCategoryListIterator
argument_list|(
name|iterators
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|indexReader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"failed to init multi-iterator"
argument_list|,
name|cli
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
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
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
name|cli
operator|.
name|getOrdinals
argument_list|(
name|i
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document "
operator|+
name|i
operator|+
literal|" does not have categories"
argument_list|,
name|ordinals
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ordinals
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|CategoryPath
name|cp
init|=
name|taxoReader
operator|.
name|getPath
argument_list|(
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"ordinal "
operator|+
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
operator|+
literal|" not found in taxonomy"
argument_list|,
name|cp
argument_list|)
expr_stmt|;
if|if
condition|(
name|cp
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|int
name|globalDoc
init|=
name|i
operator|+
name|context
operator|.
name|docBase
decl_stmt|;
name|assertEquals
argument_list|(
literal|"invalid category for document "
operator|+
name|globalDoc
argument_list|,
name|globalDoc
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|cp
operator|.
name|components
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
