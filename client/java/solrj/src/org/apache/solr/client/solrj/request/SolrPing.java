begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
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
name|SolrServer
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
name|SolrServerException
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
name|SolrPingResponse
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
begin_comment
comment|/**  *   * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrPing
specifier|public
class|class
name|SolrPing
extends|extends
name|RequestBase
block|{
DECL|method|SolrPing
specifier|public
name|SolrPing
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|"/admin/ping"
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|process
specifier|public
name|SolrPingResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|SolrPingResponse
name|res
init|=
operator|new
name|SolrPingResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
