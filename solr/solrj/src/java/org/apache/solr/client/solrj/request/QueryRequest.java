begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|ContentStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|QueryRequest
specifier|public
class|class
name|QueryRequest
extends|extends
name|SolrRequest
argument_list|<
name|QueryResponse
argument_list|>
block|{
DECL|field|query
specifier|private
name|SolrParams
name|query
decl_stmt|;
DECL|method|QueryRequest
specifier|public
name|QueryRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryRequest
specifier|public
name|QueryRequest
parameter_list|(
name|SolrParams
name|q
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|query
operator|=
name|q
expr_stmt|;
block|}
DECL|method|QueryRequest
specifier|public
name|QueryRequest
parameter_list|(
name|SolrParams
name|q
parameter_list|,
name|METHOD
name|method
parameter_list|)
block|{
name|super
argument_list|(
name|method
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|query
operator|=
name|q
expr_stmt|;
block|}
comment|/**    * Use the params 'QT' parameter if it exists    */
annotation|@
name|Override
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|String
name|qt
init|=
name|query
operator|==
literal|null
condition|?
literal|null
else|:
name|query
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
if|if
condition|(
name|qt
operator|==
literal|null
condition|)
block|{
name|qt
operator|=
name|super
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|qt
operator|!=
literal|null
operator|&&
name|qt
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|qt
return|;
block|}
return|return
literal|"/select"
return|;
block|}
comment|//---------------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|protected
name|QueryResponse
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|QueryResponse
argument_list|(
name|client
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|query
return|;
block|}
block|}
end_class
end_unit
