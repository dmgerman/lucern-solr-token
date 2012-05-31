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
name|index
operator|.
name|CorruptIndexException
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
name|store
operator|.
name|Directory
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
name|facet
operator|.
name|index
operator|.
name|CategoryDocumentBuilder
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
name|DefaultFacetIndexingParams
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
name|FacetIndexingParams
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
name|FacetArrays
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
name|FacetResultsHandler
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
name|FacetsAccumulator
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
name|ScoredDocIDs
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
name|StandardFacetsAccumulator
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
name|TopKFacetResultsHandler
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
name|cache
operator|.
name|CategoryListCache
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
name|results
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
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|results
operator|.
name|IntermediateFacetResult
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
name|ScoredDocIdsUtils
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Test faceted search with creation of multiple category list iterators by the  * same CLP, depending on the provided facet request  */
end_comment
begin_class
DECL|class|MultiIteratorsPerCLParamsTest
specifier|public
class|class
name|MultiIteratorsPerCLParamsTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|perDocCategories
name|CategoryPath
index|[]
index|[]
name|perDocCategories
init|=
operator|new
name|CategoryPath
index|[]
index|[]
block|{
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"author"
argument_list|,
literal|"Mark Twain"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"date"
argument_list|,
literal|"2010"
argument_list|)
block|}
block|,
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"author"
argument_list|,
literal|"Robert Frost"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"date"
argument_list|,
literal|"2009"
argument_list|)
block|}
block|,
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"author"
argument_list|,
literal|"Artur Miller"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"date"
argument_list|,
literal|"2010"
argument_list|)
block|}
block|,
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"author"
argument_list|,
literal|"Edgar Allan Poe"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"date"
argument_list|,
literal|"2009"
argument_list|)
block|}
block|,
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"author"
argument_list|,
literal|"Henry James"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"date"
argument_list|,
literal|"2010"
argument_list|)
block|}
block|}
decl_stmt|;
DECL|field|countForbiddenDimension
name|String
name|countForbiddenDimension
decl_stmt|;
annotation|@
name|Test
DECL|method|testCLParamMultiIteratorsByRequest
specifier|public
name|void
name|testCLParamMultiIteratorsByRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCLParamMultiIteratorsByRequest
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCLParamMultiIteratorsByRequestCacheCLI
specifier|public
name|void
name|testCLParamMultiIteratorsByRequestCacheCLI
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestCLParamMultiIteratorsByRequest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestCLParamMultiIteratorsByRequest
specifier|private
name|void
name|doTestCLParamMultiIteratorsByRequest
parameter_list|(
name|boolean
name|cacheCLI
parameter_list|)
throws|throws
name|Exception
throws|,
name|CorruptIndexException
throws|,
name|IOException
block|{
comment|// Create a CLP which generates different CLIs according to the
comment|// FacetRequest's dimension
name|CategoryListParams
name|clp
init|=
operator|new
name|CategoryListParams
argument_list|()
decl_stmt|;
name|FacetIndexingParams
name|iParams
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|(
name|clp
argument_list|)
decl_stmt|;
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
name|populateIndex
argument_list|(
name|iParams
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
name|TaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|CategoryListCache
name|clCache
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cacheCLI
condition|)
block|{
comment|// caching the iteratorr, so:
comment|// 1: create the cached iterator, using original params
name|clCache
operator|=
operator|new
name|CategoryListCache
argument_list|()
expr_stmt|;
name|clCache
operator|.
name|loadAndRegister
argument_list|(
name|clp
argument_list|,
name|reader
argument_list|,
name|taxo
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
block|}
name|ScoredDocIDs
name|allDocs
init|=
name|ScoredDocIdsUtils
operator|.
name|createAllDocsScoredDocIDs
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Search index with 'author' should filter ONLY ordinals whose parent
comment|// is 'author'
name|countForbiddenDimension
operator|=
literal|"date"
expr_stmt|;
name|validateFacetedSearch
argument_list|(
name|iParams
argument_list|,
name|taxo
argument_list|,
name|reader
argument_list|,
name|clCache
argument_list|,
name|allDocs
argument_list|,
literal|"author"
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// Search index with 'date' should filter ONLY ordinals whose parent is
comment|// 'date'
name|countForbiddenDimension
operator|=
literal|"author"
expr_stmt|;
name|validateFacetedSearch
argument_list|(
name|iParams
argument_list|,
name|taxo
argument_list|,
name|reader
argument_list|,
name|clCache
argument_list|,
name|allDocs
argument_list|,
literal|"date"
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// Search index with both 'date' and 'author'
name|countForbiddenDimension
operator|=
literal|null
expr_stmt|;
name|validateFacetedSearch
argument_list|(
name|iParams
argument_list|,
name|taxo
argument_list|,
name|reader
argument_list|,
name|clCache
argument_list|,
name|allDocs
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"author"
block|,
literal|"date"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|validateFacetedSearch
specifier|private
name|void
name|validateFacetedSearch
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|TaxonomyReader
name|taxo
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|CategoryListCache
name|clCache
parameter_list|,
name|ScoredDocIDs
name|allDocs
parameter_list|,
name|String
name|dimension
parameter_list|,
name|int
name|expectedValue
parameter_list|,
name|int
name|expectedNumDescendants
parameter_list|)
throws|throws
name|IOException
block|{
name|validateFacetedSearch
argument_list|(
name|iParams
argument_list|,
name|taxo
argument_list|,
name|reader
argument_list|,
name|clCache
argument_list|,
name|allDocs
argument_list|,
operator|new
name|String
index|[]
block|{
name|dimension
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|expectedValue
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|expectedNumDescendants
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|validateFacetedSearch
specifier|private
name|void
name|validateFacetedSearch
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|TaxonomyReader
name|taxo
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|CategoryListCache
name|clCache
parameter_list|,
name|ScoredDocIDs
name|allDocs
parameter_list|,
name|String
index|[]
name|dimension
parameter_list|,
name|int
index|[]
name|expectedValue
parameter_list|,
name|int
index|[]
name|expectedNumDescendants
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetSearchParams
name|sParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|iParams
argument_list|)
decl_stmt|;
name|sParams
operator|.
name|setClCache
argument_list|(
name|clCache
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|dim
range|:
name|dimension
control|)
block|{
name|sParams
operator|.
name|addFacetRequest
argument_list|(
operator|new
name|PerDimCountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|dim
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FacetsAccumulator
name|acc
init|=
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|sParams
argument_list|,
name|reader
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
comment|// no use to test this with complement since at that mode all facets are taken
name|acc
operator|.
name|setComplementThreshold
argument_list|(
name|FacetsAccumulator
operator|.
name|DISABLE_COMPLEMENT
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|acc
operator|.
name|accumulate
argument_list|(
name|allDocs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong #results"
argument_list|,
name|dimension
operator|.
name|length
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FacetResult
name|res
init|=
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong num-descendants for dimension "
operator|+
name|dimension
index|[
name|i
index|]
argument_list|,
name|expectedNumDescendants
index|[
name|i
index|]
argument_list|,
name|res
operator|.
name|getNumValidDescendants
argument_list|()
argument_list|)
expr_stmt|;
name|FacetResultNode
name|resNode
init|=
name|res
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong value for dimension "
operator|+
name|dimension
index|[
name|i
index|]
argument_list|,
name|expectedValue
index|[
name|i
index|]
argument_list|,
operator|(
name|int
operator|)
name|resNode
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populateIndex
specifier|private
name|void
name|populateIndex
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|)
throws|throws
name|Exception
block|{
name|RandomIndexWriter
name|writer
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
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
for|for
control|(
name|CategoryPath
index|[]
name|categories
range|:
name|perDocCategories
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|CategoryDocumentBuilder
argument_list|(
name|taxoWriter
argument_list|,
name|iParams
argument_list|)
operator|.
name|setCategoryPaths
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|categories
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|PerDimCountFacetRequest
specifier|private
class|class
name|PerDimCountFacetRequest
extends|extends
name|CountFacetRequest
block|{
DECL|method|PerDimCountFacetRequest
specifier|public
name|PerDimCountFacetRequest
parameter_list|(
name|CategoryPath
name|path
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCategoryListIterator
specifier|public
name|CategoryListIterator
name|createCategoryListIterator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TaxonomyReader
name|taxo
parameter_list|,
name|FacetSearchParams
name|sParams
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
comment|// categories of certain dimension only
return|return
operator|new
name|PerDimensionCLI
argument_list|(
name|taxo
argument_list|,
name|super
operator|.
name|createCategoryListIterator
argument_list|(
name|reader
argument_list|,
name|taxo
argument_list|,
name|sParams
argument_list|,
name|partition
argument_list|)
argument_list|,
name|getCategoryPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|/** Override this method just for verifying that only specified facets are iterated.. */
DECL|method|createFacetResultsHandler
specifier|public
name|FacetResultsHandler
name|createFacetResultsHandler
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
return|return
operator|new
name|TopKFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|IntermediateFacetResult
name|fetchPartitionResult
parameter_list|(
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IntermediateFacetResult
name|res
init|=
name|super
operator|.
name|fetchPartitionResult
argument_list|(
name|facetArrays
argument_list|,
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|countForbiddenDimension
operator|!=
literal|null
condition|)
block|{
name|int
name|ord
init|=
name|taxonomyReader
operator|.
name|getOrdinal
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|countForbiddenDimension
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should not have accumulated for dimension '"
operator|+
name|countForbiddenDimension
operator|+
literal|"'!"
argument_list|,
literal|0
argument_list|,
name|facetArrays
operator|.
name|getIntArray
argument_list|()
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * a CLI which filters another CLI for the dimension of the provided    * category-path    */
DECL|class|PerDimensionCLI
specifier|private
specifier|static
class|class
name|PerDimensionCLI
implements|implements
name|CategoryListIterator
block|{
DECL|field|superCLI
specifier|private
specifier|final
name|CategoryListIterator
name|superCLI
decl_stmt|;
DECL|field|parentArray
specifier|private
specifier|final
name|int
index|[]
name|parentArray
decl_stmt|;
DECL|field|parentOrdinal
specifier|private
specifier|final
name|int
name|parentOrdinal
decl_stmt|;
DECL|method|PerDimensionCLI
name|PerDimensionCLI
parameter_list|(
name|TaxonomyReader
name|taxo
parameter_list|,
name|CategoryListIterator
name|superCLI
parameter_list|,
name|CategoryPath
name|requestedPath
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|superCLI
operator|=
name|superCLI
expr_stmt|;
if|if
condition|(
name|requestedPath
operator|==
literal|null
condition|)
block|{
name|parentOrdinal
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
name|requestedPath
operator|.
name|getComponent
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|parentOrdinal
operator|=
name|taxo
operator|.
name|getOrdinal
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
name|parentArray
operator|=
name|taxo
operator|.
name|getParentArray
argument_list|()
expr_stmt|;
block|}
DECL|method|init
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|superCLI
operator|.
name|init
argument_list|()
return|;
block|}
DECL|method|nextCategory
specifier|public
name|long
name|nextCategory
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|next
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|superCLI
operator|.
name|nextCategory
argument_list|()
operator|)
operator|<=
name|Integer
operator|.
name|MAX_VALUE
operator|&&
operator|!
name|isInDimension
argument_list|(
operator|(
name|int
operator|)
name|next
argument_list|)
condition|)
block|{       }
return|return
name|next
return|;
block|}
comment|/** look for original parent ordinal, meaning same dimension */
DECL|method|isInDimension
specifier|private
name|boolean
name|isInDimension
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
while|while
condition|(
name|ordinal
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|ordinal
operator|==
name|parentOrdinal
condition|)
block|{
return|return
literal|true
return|;
block|}
name|ordinal
operator|=
name|parentArray
index|[
name|ordinal
index|]
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|superCLI
operator|.
name|skipTo
argument_list|(
name|docId
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
