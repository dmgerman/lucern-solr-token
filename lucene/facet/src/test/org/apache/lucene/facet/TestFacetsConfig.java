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
name|util
operator|.
name|Arrays
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import
begin_class
DECL|class|TestFacetsConfig
specifier|public
class|class
name|TestFacetsConfig
extends|extends
name|FacetTestCase
block|{
DECL|method|testPathToStringAndBack
specifier|public
name|void
name|testPathToStringAndBack
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numParts
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|String
index|[]
name|parts
init|=
operator|new
name|String
index|[
name|numParts
index|]
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
name|numParts
condition|;
name|j
operator|++
control|)
block|{
name|String
name|s
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
break|break;
block|}
block|}
name|parts
index|[
name|j
index|]
operator|=
name|s
expr_stmt|;
block|}
name|String
name|s
init|=
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|parts
argument_list|)
decl_stmt|;
name|String
index|[]
name|parts2
init|=
name|FacetsConfig
operator|.
name|stringToPath
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|parts
argument_list|,
name|parts2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAddSameDocTwice
specifier|public
name|void
name|testAddSameDocTwice
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-5367: this was a problem with the previous code, making sure it
comment|// works with the new code.
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|,
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetsConfig
name|facetsConfig
init|=
operator|new
name|FacetsConfig
argument_list|()
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
name|FacetField
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|facetsConfig
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
expr_stmt|;
comment|// these two addDocument() used to fail
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
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
name|DirectoryTaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
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
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|facetsConfig
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|FacetResult
name|res
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|res
operator|.
name|labelValues
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|res
operator|.
name|labelValues
index|[
literal|0
index|]
operator|.
name|value
argument_list|)
expr_stmt|;
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
comment|/** LUCENE-5479 */
DECL|method|testCustomDefault
specifier|public
name|void
name|testCustomDefault
parameter_list|()
block|{
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|DimConfig
name|getDefaultDimConfig
parameter_list|()
block|{
name|DimConfig
name|config
init|=
operator|new
name|DimConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|hierarchical
operator|=
literal|true
expr_stmt|;
return|return
name|config
return|;
block|}
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|config
operator|.
name|getDimConfig
argument_list|(
literal|"foobar"
argument_list|)
operator|.
name|hierarchical
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
