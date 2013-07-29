begin_unit
begin_package
DECL|package|org.apache.lucene.facet.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Defines parameters that are needed for faceted search: the list of facet  * {@link FacetRequest facet requests} which should be aggregated as well as the  * {@link FacetIndexingParams indexing params} that were used to index them.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetSearchParams
specifier|public
class|class
name|FacetSearchParams
block|{
DECL|field|indexingParams
specifier|public
specifier|final
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
DECL|field|facetRequests
specifier|public
specifier|final
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
decl_stmt|;
comment|/**    * Initializes with the given {@link FacetRequest requests} and default    * {@link FacetIndexingParams#DEFAULT}. If you used a different    * {@link FacetIndexingParams}, you should use    * {@link #FacetSearchParams(FacetIndexingParams, List)}.    */
DECL|method|FacetSearchParams
specifier|public
name|FacetSearchParams
parameter_list|(
name|FacetRequest
modifier|...
name|facetRequests
parameter_list|)
block|{
name|this
argument_list|(
name|FacetIndexingParams
operator|.
name|DEFAULT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|facetRequests
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes with the given {@link FacetRequest requests} and default    * {@link FacetIndexingParams#DEFAULT}. If you used a different    * {@link FacetIndexingParams}, you should use    * {@link #FacetSearchParams(FacetIndexingParams, List)}.    */
DECL|method|FacetSearchParams
specifier|public
name|FacetSearchParams
parameter_list|(
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
parameter_list|)
block|{
name|this
argument_list|(
name|FacetIndexingParams
operator|.
name|DEFAULT
argument_list|,
name|facetRequests
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes with the given {@link FacetRequest requests} and    * {@link FacetIndexingParams}.    */
DECL|method|FacetSearchParams
specifier|public
name|FacetSearchParams
parameter_list|(
name|FacetIndexingParams
name|indexingParams
parameter_list|,
name|FacetRequest
modifier|...
name|facetRequests
parameter_list|)
block|{
name|this
argument_list|(
name|indexingParams
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|facetRequests
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes with the given {@link FacetRequest requests} and    * {@link FacetIndexingParams}.    */
DECL|method|FacetSearchParams
specifier|public
name|FacetSearchParams
parameter_list|(
name|FacetIndexingParams
name|indexingParams
parameter_list|,
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
parameter_list|)
block|{
if|if
condition|(
name|facetRequests
operator|==
literal|null
operator|||
name|facetRequests
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least one FacetRequest must be defined"
argument_list|)
throw|;
block|}
name|this
operator|.
name|facetRequests
operator|=
name|facetRequests
expr_stmt|;
name|this
operator|.
name|indexingParams
operator|=
name|indexingParams
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|String
name|INDENT
init|=
literal|"  "
decl_stmt|;
specifier|final
name|char
name|NEWLINE
init|=
literal|'\n'
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"IndexingParams: "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
name|INDENT
argument_list|)
operator|.
name|append
argument_list|(
name|indexingParams
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
literal|"FacetRequests:"
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetRequest
name|facetRequest
range|:
name|facetRequests
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
name|INDENT
argument_list|)
operator|.
name|append
argument_list|(
name|facetRequest
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
