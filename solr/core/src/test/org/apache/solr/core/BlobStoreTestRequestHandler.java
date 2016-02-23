begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|solr
operator|.
name|handler
operator|.
name|DumpRequestHandler
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import
begin_class
DECL|class|BlobStoreTestRequestHandler
specifier|public
class|class
name|BlobStoreTestRequestHandler
extends|extends
name|DumpRequestHandler
implements|implements
name|Runnable
implements|,
name|SolrCoreAware
block|{
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
init|=
literal|0
decl_stmt|;
DECL|field|watchedVal
specifier|private
name|String
name|watchedVal
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"class"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"x"
argument_list|,
name|watchedVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|RequestParams
name|p
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getRequestParams
argument_list|()
decl_stmt|;
name|RequestParams
operator|.
name|ParamSet
name|v
init|=
name|p
operator|.
name|getParams
argument_list|(
literal|"watched"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|watchedVal
operator|=
literal|null
expr_stmt|;
name|version
operator|=
operator|-
literal|1
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|v
operator|.
name|getVersion
argument_list|()
operator|!=
name|version
condition|)
block|{
name|watchedVal
operator|=
name|v
operator|.
name|getParams
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|core
operator|.
name|addConfListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
