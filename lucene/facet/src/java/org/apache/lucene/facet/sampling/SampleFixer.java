begin_unit
begin_package
DECL|package|org.apache.lucene.facet.sampling
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sampling
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
name|old
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
name|FacetResultNode
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Fixer of sample facet accumulation results.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SampleFixer
specifier|public
specifier|abstract
class|class
name|SampleFixer
block|{
comment|/**    * Alter the input result, fixing it to account for the sampling. This    * implementation can compute accurate or estimated counts for the sampled    * facets. For example, a faster correction could just multiply by a    * compensating factor.    *     * @param origDocIds    *          full set of matching documents.    * @param fres    *          sample result to be fixed.    * @throws IOException    *           If there is a low-level I/O error.    */
DECL|method|fixResult
specifier|public
name|void
name|fixResult
parameter_list|(
name|ScoredDocIDs
name|origDocIds
parameter_list|,
name|FacetResult
name|fres
parameter_list|,
name|double
name|samplingRatio
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetResultNode
name|topRes
init|=
name|fres
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|fixResultNode
argument_list|(
name|topRes
argument_list|,
name|origDocIds
argument_list|,
name|samplingRatio
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fix result node count, and, recursively, fix all its children    *     * @param facetResNode    *          result node to be fixed    * @param docIds    *          docids in effect    * @throws IOException    *           If there is a low-level I/O error.    */
DECL|method|fixResultNode
specifier|protected
name|void
name|fixResultNode
parameter_list|(
name|FacetResultNode
name|facetResNode
parameter_list|,
name|ScoredDocIDs
name|docIds
parameter_list|,
name|double
name|samplingRatio
parameter_list|)
throws|throws
name|IOException
block|{
name|singleNodeFix
argument_list|(
name|facetResNode
argument_list|,
name|docIds
argument_list|,
name|samplingRatio
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|facetResNode
operator|.
name|subResults
control|)
block|{
name|fixResultNode
argument_list|(
name|frn
argument_list|,
name|docIds
argument_list|,
name|samplingRatio
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Fix the given node's value. */
DECL|method|singleNodeFix
specifier|protected
specifier|abstract
name|void
name|singleNodeFix
parameter_list|(
name|FacetResultNode
name|facetResNode
parameter_list|,
name|ScoredDocIDs
name|docIds
parameter_list|,
name|double
name|samplingRatio
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
