begin_unit
begin_package
DECL|package|org.apache.solr.handler.clustering
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|solr
operator|.
name|common
operator|.
name|SolrDocument
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
name|search
operator|.
name|DocList
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
name|util
operator|.
name|SolrPluginUtils
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|SearchClusteringEngine
specifier|public
specifier|abstract
class|class
name|SearchClusteringEngine
extends|extends
name|ClusteringEngine
block|{
annotation|@
name|Deprecated
DECL|method|cluster
specifier|public
specifier|abstract
name|Object
name|cluster
parameter_list|(
name|Query
name|query
parameter_list|,
name|DocList
name|docList
parameter_list|,
name|SolrQueryRequest
name|sreq
parameter_list|)
function_decl|;
comment|// TODO: need DocList, too?
DECL|method|cluster
specifier|public
specifier|abstract
name|Object
name|cluster
parameter_list|(
name|Query
name|query
parameter_list|,
name|SolrDocumentList
name|solrDocumentList
parameter_list|,
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
parameter_list|,
name|SolrQueryRequest
name|sreq
parameter_list|)
function_decl|;
comment|/**    * Returns the set of field names to load.    * Concrete classes can override this method if needed.    * Default implementation returns null, that is, all stored fields are loaded.    * @return set of field names to load    */
DECL|method|getFieldsToLoad
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getFieldsToLoad
parameter_list|(
name|SolrQueryRequest
name|sreq
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|getSolrDocumentList
specifier|public
name|SolrDocumentList
name|getSolrDocumentList
parameter_list|(
name|DocList
name|docList
parameter_list|,
name|SolrQueryRequest
name|sreq
parameter_list|,
name|Map
argument_list|<
name|SolrDocument
argument_list|,
name|Integer
argument_list|>
name|docIds
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SolrPluginUtils
operator|.
name|docListToSolrDocumentList
argument_list|(
name|docList
argument_list|,
name|sreq
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|getFieldsToLoad
argument_list|(
name|sreq
argument_list|)
argument_list|,
name|docIds
argument_list|)
return|;
block|}
block|}
end_class
end_unit
