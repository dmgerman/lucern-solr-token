begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocSlice
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
import|;
end_import
begin_class
DECL|class|PageTool
specifier|public
class|class
name|PageTool
block|{
DECL|field|start
specifier|private
name|long
name|start
decl_stmt|;
DECL|field|results_per_page
specifier|private
name|int
name|results_per_page
init|=
literal|10
decl_stmt|;
DECL|field|results_found
specifier|private
name|long
name|results_found
decl_stmt|;
DECL|field|page_count
specifier|private
name|int
name|page_count
decl_stmt|;
DECL|field|current_page_number
specifier|private
name|int
name|current_page_number
decl_stmt|;
DECL|method|PageTool
specifier|public
name|PageTool
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
name|String
name|rows
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"rows"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rows
operator|!=
literal|null
condition|)
block|{
name|results_per_page
operator|=
operator|new
name|Integer
argument_list|(
name|rows
argument_list|)
expr_stmt|;
block|}
comment|//TODO: Handle group by results
name|Object
name|docs
init|=
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|docs
operator|instanceof
name|DocSlice
condition|)
block|{
name|DocSlice
name|doc_slice
init|=
operator|(
name|DocSlice
operator|)
name|docs
decl_stmt|;
name|results_found
operator|=
name|doc_slice
operator|.
name|matches
argument_list|()
expr_stmt|;
name|start
operator|=
name|doc_slice
operator|.
name|offset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|SolrDocumentList
name|doc_list
init|=
operator|(
name|SolrDocumentList
operator|)
name|docs
decl_stmt|;
name|results_found
operator|=
name|doc_list
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|start
operator|=
name|doc_list
operator|.
name|getStart
argument_list|()
expr_stmt|;
block|}
block|}
name|page_count
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|results_found
operator|/
operator|(
name|double
operator|)
name|results_per_page
argument_list|)
expr_stmt|;
name|current_page_number
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|start
operator|/
operator|(
name|double
operator|)
name|results_per_page
argument_list|)
operator|+
operator|(
name|page_count
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
block|}
DECL|method|getStart
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|getResults_per_page
specifier|public
name|int
name|getResults_per_page
parameter_list|()
block|{
return|return
name|results_per_page
return|;
block|}
DECL|method|getResults_found
specifier|public
name|long
name|getResults_found
parameter_list|()
block|{
return|return
name|results_found
return|;
block|}
DECL|method|getPage_count
specifier|public
name|int
name|getPage_count
parameter_list|()
block|{
return|return
name|page_count
return|;
block|}
DECL|method|getCurrent_page_number
specifier|public
name|int
name|getCurrent_page_number
parameter_list|()
block|{
return|return
name|current_page_number
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Found "
operator|+
name|results_found
operator|+
literal|" Page "
operator|+
name|current_page_number
operator|+
literal|" of "
operator|+
name|page_count
operator|+
literal|" Starting at "
operator|+
name|start
operator|+
literal|" per page "
operator|+
name|results_per_page
return|;
block|}
block|}
end_class
end_unit
