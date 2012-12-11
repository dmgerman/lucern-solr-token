begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
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
name|TaxonomyReader
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Handler for facet results.  *<p>  * The facet results handler provided by the {@link FacetRequest} to   * a {@link FacetsAccumulator}.  *<p>  * First it is used by {@link FacetsAccumulator} to obtain a temporary   * facet result for each partition and to merge results of several partitions.  *<p>  * Later the accumulator invokes the handler to render the results, creating   * {@link FacetResult} objects.  *<p>  * Last the accumulator invokes the handler to label final results.   *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetResultsHandler
specifier|public
specifier|abstract
class|class
name|FacetResultsHandler
block|{
comment|/** Taxonomy for which facets are handled */
DECL|field|taxonomyReader
specifier|protected
specifier|final
name|TaxonomyReader
name|taxonomyReader
decl_stmt|;
comment|/**    * Facet request served by this handler.    */
DECL|field|facetRequest
specifier|protected
specifier|final
name|FacetRequest
name|facetRequest
decl_stmt|;
comment|/**    * Create a faceted search handler.    * @param taxonomyReader See {@link #getTaxonomyReader()}.    * @param facetRequest See {@link #getFacetRequest()}.    */
DECL|method|FacetResultsHandler
specifier|public
name|FacetResultsHandler
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetRequest
name|facetRequest
parameter_list|)
block|{
name|this
operator|.
name|taxonomyReader
operator|=
name|taxonomyReader
expr_stmt|;
name|this
operator|.
name|facetRequest
operator|=
name|facetRequest
expr_stmt|;
block|}
comment|/**    * Fetch results of a single partition, given facet arrays for that partition,    * and based on the matching documents and faceted search parameters.    *     * @param arrays    *          facet arrays for the certain partition    * @param offset    *          offset in input arrays where partition starts    * @return temporary facet result, potentially, to be passed back to    *<b>this</b> result handler for merging, or<b>null</b> in case that    *         constructor parameter,<code>facetRequest</code>, requests an    *         illegal FacetResult, like, e.g., a root node category path that    *         does not exist in constructor parameter<code>taxonomyReader</code>    *         .    * @throws IOException    *           on error    */
DECL|method|fetchPartitionResult
specifier|public
specifier|abstract
name|IntermediateFacetResult
name|fetchPartitionResult
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Merge results of several facet partitions. Logic of the merge is undefined    * and open for interpretations. For example, a merge implementation could    * keep top K results. Passed {@link IntermediateFacetResult} must be ones    * that were created by this handler otherwise a {@link ClassCastException} is    * thrown. In addition, all passed {@link IntermediateFacetResult} must have    * the same {@link FacetRequest} otherwise an {@link IllegalArgumentException}    * is thrown.    *     * @param tmpResults one or more temporary results created by<b>this</b>    *        handler.    * @return temporary facet result that represents to union, as specified by    *<b>this</b> handler, of the input temporary facet results.    * @throws IOException on error.    * @throws ClassCastException if the temporary result passed was not created    *         by this handler    * @throws IllegalArgumentException if passed<code>facetResults</code> do not    *         have the same {@link FacetRequest}    * @see IntermediateFacetResult#getFacetRequest()    */
DECL|method|mergeResults
specifier|public
specifier|abstract
name|IntermediateFacetResult
name|mergeResults
parameter_list|(
name|IntermediateFacetResult
modifier|...
name|tmpResults
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassCastException
throws|,
name|IllegalArgumentException
function_decl|;
comment|/**    * Create a facet result from the temporary result.    * @param tmpResult temporary result to be rendered as a {@link FacetResult}    * @throws IOException on error.    */
DECL|method|renderFacetResult
specifier|public
specifier|abstract
name|FacetResult
name|renderFacetResult
parameter_list|(
name|IntermediateFacetResult
name|tmpResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Perform any rearrangement as required on a facet result that has changed after    * it was rendered.    *<P>    * Possible use case: a sampling facets accumulator invoked another     * other facets accumulator on a sample set of documents, obtained    * rendered facet results, fixed their counts, and now it is needed     * to sort the results differently according to the fixed counts.     * @param facetResult result to be rearranged.    * @see FacetResultNode#setValue(double)    */
DECL|method|rearrangeFacetResult
specifier|public
specifier|abstract
name|FacetResult
name|rearrangeFacetResult
parameter_list|(
name|FacetResult
name|facetResult
parameter_list|)
function_decl|;
comment|/**    * Label results according to settings in {@link FacetRequest},     * such as {@link FacetRequest#getNumLabel()}.     * Usually invoked by {@link FacetsAccumulator#accumulate(ScoredDocIDs)}    * @param facetResult facet result to be labeled.     * @throws IOException on error     */
DECL|method|labelResult
specifier|public
specifier|abstract
name|void
name|labelResult
parameter_list|(
name|FacetResult
name|facetResult
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Return taxonomy reader used for current facets accumulation operation. */
DECL|method|getTaxonomyReader
specifier|public
specifier|final
name|TaxonomyReader
name|getTaxonomyReader
parameter_list|()
block|{
return|return
name|this
operator|.
name|taxonomyReader
return|;
block|}
comment|/** Return the facet request served by this handler. */
DECL|method|getFacetRequest
specifier|public
specifier|final
name|FacetRequest
name|getFacetRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|facetRequest
return|;
block|}
comment|/**    * Check if an array contains the partition which contains ordinal    *     * @param ordinal    *          checked facet    * @param facetArrays    *          facet arrays for the certain partition    * @param offset    *          offset in input arrays where partition starts    */
DECL|method|isSelfPartition
specifier|protected
name|boolean
name|isSelfPartition
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|partitionSize
init|=
name|facetArrays
operator|.
name|arrayLength
decl_stmt|;
return|return
name|ordinal
operator|/
name|partitionSize
operator|==
name|offset
operator|/
name|partitionSize
return|;
block|}
block|}
end_class
end_unit
