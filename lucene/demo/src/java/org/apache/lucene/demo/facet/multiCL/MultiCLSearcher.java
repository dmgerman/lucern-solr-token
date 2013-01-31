begin_unit
begin_package
DECL|package|org.apache.lucene.demo.facet.multiCL
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
operator|.
name|multiCL
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
name|Term
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
name|Query
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
name|TermQuery
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
name|TopScoreDocCollector
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
name|search
operator|.
name|MultiCollector
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
name|demo
operator|.
name|facet
operator|.
name|ExampleUtils
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
name|demo
operator|.
name|facet
operator|.
name|simple
operator|.
name|SimpleUtils
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
name|search
operator|.
name|params
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
name|params
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
name|directory
operator|.
name|DirectoryTaxonomyReader
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * MultiSearcher searches index with facets over an index with multiple  * category lists.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiCLSearcher
specifier|public
class|class
name|MultiCLSearcher
block|{
comment|/** No instance */
DECL|method|MultiCLSearcher
specifier|private
name|MultiCLSearcher
parameter_list|()
block|{}
comment|/**    * Search an index with facets.    *     * @param indexDir    *            Directory of the search index.    * @param taxoDir    *            Directory of the taxonomy index.    * @throws Exception    *             on error (no detailed exception handling here for sample    *             simplicity    * @return facet results    */
DECL|method|searchWithFacets
specifier|public
specifier|static
name|List
argument_list|<
name|FacetResult
argument_list|>
name|searchWithFacets
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|,
name|FacetIndexingParams
name|iParams
parameter_list|)
throws|throws
name|Exception
block|{
comment|// prepare index reader and taxonomy.
name|IndexReader
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
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// Get results
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|searchWithFacets
argument_list|(
name|indexReader
argument_list|,
name|taxo
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
comment|// we're done, close the index reader and the taxonomy.
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|results
return|;
block|}
comment|/**    * Search an index with facets.    *     * @param indexReader    *            Reader over the search index.    * @param taxo    *            taxonomy reader.    * @throws Exception    *             on error (no detailed exception handling here for sample    *             simplicity    * @return facet results    */
DECL|method|searchWithFacets
specifier|public
specifier|static
name|List
argument_list|<
name|FacetResult
argument_list|>
name|searchWithFacets
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxo
parameter_list|,
name|FacetIndexingParams
name|iParams
parameter_list|)
throws|throws
name|Exception
block|{
comment|// prepare searcher to search against
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
comment|// faceted search is working in 2 steps:
comment|// 1. collect matching documents
comment|// 2. aggregate facets for collected documents and
comment|// generate the requested faceted results from the aggregated facets
comment|// step 1: create a query for finding matching documents for which we
comment|// accumulate facets
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|SimpleUtils
operator|.
name|TEXT
argument_list|,
literal|"Quis"
argument_list|)
argument_list|)
decl_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"Query: "
operator|+
name|q
argument_list|)
expr_stmt|;
name|TopScoreDocCollector
name|topDocsCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Faceted search parameters indicate which facets are we interested in
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"5"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"5"
argument_list|,
literal|"5"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"6"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|FacetSearchParams
name|facetSearchParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|facetRequests
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
comment|// Facets collector is the simplest interface for faceted search.
comment|// It provides faceted search functions that are sufficient to many
comment|// application,
comment|// although it is insufficient for tight control on faceted search
comment|// behavior - in those
comment|// situations other, more low-level interfaces are available, as
comment|// demonstrated in other search examples.
name|FacetsCollector
name|facetsCollector
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|facetSearchParams
argument_list|,
name|indexReader
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
comment|// perform documents search and facets accumulation
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|topDocsCollector
argument_list|,
name|facetsCollector
argument_list|)
argument_list|)
expr_stmt|;
comment|// Obtain facets results and print them
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|facetsCollector
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetResult
name|facetResult
range|:
name|res
control|)
block|{
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"Res "
operator|+
operator|(
name|i
operator|++
operator|)
operator|+
literal|": "
operator|+
name|facetResult
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
