begin_unit
begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
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
name|FacetTestUtils
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
name|params
operator|.
name|FacetSearchParams
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
name|CountFacetRequest
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
name|search
operator|.
name|FacetRequest
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
begin_class
DECL|class|TestSortedSetDocValuesFacets
specifier|public
class|class
name|TestSortedSetDocValuesFacets
extends|extends
name|FacetTestCase
block|{
comment|// NOTE: TestDrillSideways.testRandom also sometimes
comment|// randomly uses SortedSetDV
DECL|method|testSortedSetDocValuesAccumulator
specifier|public
name|void
name|testSortedSetDocValuesAccumulator
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"Test requires SortedSetDV support"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// Use a custom delim char to make sure the impls
comment|// respect it:
specifier|final
name|char
name|delim
init|=
literal|':'
decl_stmt|;
name|FacetIndexingParams
name|fip
init|=
operator|new
name|FacetIndexingParams
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|char
name|getFacetDelimChar
parameter_list|()
block|{
return|return
name|delim
return|;
block|}
block|}
decl_stmt|;
name|SortedSetDocValuesFacetFields
name|dvFields
init|=
operator|new
name|SortedSetDocValuesFacetFields
argument_list|(
name|fip
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Mixup order we add these paths, to verify tie-break
comment|// order is by label (unicode sort) and has nothing to
comment|// do w/ order we added them:
name|List
argument_list|<
name|FacetLabel
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetLabel
argument_list|>
argument_list|()
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
literal|"zoo"
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|paths
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
operator|+
name|FacetIndexingParams
operator|.
name|DEFAULT_FACET_DELIM_CHAR
argument_list|,
literal|"bazfoo"
argument_list|)
argument_list|)
expr_stmt|;
name|dvFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|dvFields
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
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
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
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
operator|+
name|FacetIndexingParams
operator|.
name|DEFAULT_FACET_DELIM_CHAR
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|doDimCount
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|CategoryListParams
name|clp
init|=
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
name|doDimCount
condition|?
name|OrdinalPolicy
operator|.
name|NO_PARENTS
else|:
name|OrdinalPolicy
operator|.
name|ALL_BUT_DIMENSION
return|;
block|}
block|}
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|FacetIndexingParams
argument_list|(
name|clp
argument_list|)
argument_list|,
name|requests
argument_list|)
decl_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|SortedSetDocValuesReaderState
argument_list|(
name|fip
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
comment|//SortedSetDocValuesCollector c = new SortedSetDocValuesCollector(state);
comment|//SortedSetDocValuesCollectorMergeBySeg c = new SortedSetDocValuesCollectorMergeBySeg(state);
name|FacetsCollector
name|c
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
operator|new
name|SortedSetDocValuesAccumulator
argument_list|(
name|state
argument_list|,
name|fsp
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
comment|//List<FacetResult> results = c.getFacetResults(requests);
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|c
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|dimCount
init|=
name|doDimCount
condition|?
literal|4
else|:
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a ("
operator|+
name|dimCount
operator|+
literal|")\n  foo (2)\n  bar (1)\n  zoo (1)\n"
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dimCount
operator|=
name|doDimCount
condition|?
literal|1
else|:
literal|0
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b ("
operator|+
name|dimCount
operator|+
literal|")\n  baz (1)\n"
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dimCount
operator|=
name|doDimCount
condition|?
literal|1
else|:
literal|0
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
operator|+
name|FacetIndexingParams
operator|.
name|DEFAULT_FACET_DELIM_CHAR
operator|+
literal|" ("
operator|+
name|dimCount
operator|+
literal|")\n  bazfoo (1)\n"
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// DrillDown:
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|fip
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"b"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|q
operator|=
operator|new
name|DrillDownQuery
argument_list|(
name|fip
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-5090
DECL|method|testStaleState
specifier|public
name|void
name|testStaleState
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"Test requires SortedSetDV support"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|SortedSetDocValuesFacetFields
name|dvFields
init|=
operator|new
name|SortedSetDocValuesFacetFields
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|dvFields
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
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
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
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|SortedSetDocValuesReaderState
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|dvFields
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
literal|"a"
argument_list|,
literal|"bar"
argument_list|)
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
name|dvFields
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
literal|"a"
argument_list|,
literal|"baz"
argument_list|)
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
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|requests
argument_list|)
decl_stmt|;
name|FacetsCollector
name|c
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
operator|new
name|SortedSetDocValuesAccumulator
argument_list|(
name|state
argument_list|,
name|fsp
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
try|try
block|{
name|c
operator|.
name|getFacetResults
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
