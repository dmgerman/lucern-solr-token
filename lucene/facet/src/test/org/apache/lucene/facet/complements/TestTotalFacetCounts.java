begin_unit
begin_package
DECL|package|org.apache.lucene.facet.complements
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|complements
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|FacetTestCase
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
name|complements
operator|.
name|TotalFacetCounts
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
name|complements
operator|.
name|TotalFacetCountsCache
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
name|taxonomy
operator|.
name|FacetLabel
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
name|_TestUtil
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
DECL|class|TestTotalFacetCounts
specifier|public
class|class
name|TestTotalFacetCounts
extends|extends
name|FacetTestCase
block|{
DECL|method|initCache
specifier|private
specifier|static
name|void
name|initCache
parameter_list|()
block|{
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
operator|.
name|setCacheSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Set to keep one in mem
block|}
annotation|@
name|Test
DECL|method|testWriteRead
specifier|public
name|void
name|testWriteRead
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestWriteRead
argument_list|(
literal|14
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestWriteRead
specifier|private
name|void
name|doTestWriteRead
parameter_list|(
specifier|final
name|int
name|partitionSize
parameter_list|)
throws|throws
name|IOException
block|{
name|initCache
argument_list|()
expr_stmt|;
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
name|FacetIndexingParams
name|iParams
init|=
operator|new
name|FacetIndexingParams
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getPartitionSize
parameter_list|()
block|{
return|return
name|partitionSize
return|;
block|}
annotation|@
name|Override
specifier|public
name|CategoryListParams
name|getCategoryListParams
parameter_list|(
name|FacetLabel
name|category
parameter_list|)
block|{
return|return
operator|new
name|CategoryListParams
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|OrdinalPolicy
name|getOrdinalPolicy
parameter_list|(
name|String
name|dimension
parameter_list|)
block|{
return|return
name|OrdinalPolicy
operator|.
name|ALL_PARENTS
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
comment|// The counts that the TotalFacetCountsArray should have after adding
comment|// the below facets to the index.
name|int
index|[]
name|expectedCounts
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|1
block|,
literal|3
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
name|String
index|[]
name|categories
init|=
operator|new
name|String
index|[]
block|{
literal|"a/b"
block|,
literal|"c/d"
block|,
literal|"a/e"
block|,
literal|"a/d"
block|,
literal|"c/g"
block|,
literal|"c/z"
block|,
literal|"b/a"
block|,
literal|"1/2"
block|,
literal|"b/c"
block|}
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxoWriter
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|cat
range|:
name|categories
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FacetLabel
argument_list|(
name|cat
argument_list|,
literal|'/'
argument_list|)
argument_list|)
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
comment|// Commit Changes
name|IOUtils
operator|.
name|close
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
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
name|int
index|[]
name|intArray
init|=
operator|new
name|int
index|[
name|iParams
operator|.
name|getPartitionSize
argument_list|()
index|]
decl_stmt|;
name|TotalFacetCountsCache
name|tfcc
init|=
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|File
name|tmpFile
init|=
name|_TestUtil
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|"tmp"
argument_list|,
name|TEMP_DIR
argument_list|)
decl_stmt|;
name|tfcc
operator|.
name|store
argument_list|(
name|tmpFile
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
name|tfcc
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// not really required because TFCC overrides on load(), but in the test we need not rely on this.
name|tfcc
operator|.
name|load
argument_list|(
name|tmpFile
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
comment|// now retrieve the one just loaded
name|TotalFacetCounts
name|totalCounts
init|=
name|tfcc
operator|.
name|getTotalCounts
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
name|int
name|partition
init|=
literal|0
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
name|expectedCounts
operator|.
name|length
condition|;
name|i
operator|+=
name|partitionSize
control|)
block|{
name|totalCounts
operator|.
name|fillTotalCountsForPartition
argument_list|(
name|intArray
argument_list|,
name|partition
argument_list|)
expr_stmt|;
name|int
index|[]
name|partitionExpectedCounts
init|=
operator|new
name|int
index|[
name|partitionSize
index|]
decl_stmt|;
name|int
name|nToCopy
init|=
name|Math
operator|.
name|min
argument_list|(
name|partitionSize
argument_list|,
name|expectedCounts
operator|.
name|length
operator|-
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|expectedCounts
argument_list|,
name|i
argument_list|,
name|partitionExpectedCounts
argument_list|,
literal|0
argument_list|,
name|nToCopy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong counts! for partition "
operator|+
name|partition
operator|+
literal|"\nExpected:\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|partitionExpectedCounts
argument_list|)
operator|+
literal|"\nActual:\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|intArray
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|partitionExpectedCounts
argument_list|,
name|intArray
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|partition
expr_stmt|;
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
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
