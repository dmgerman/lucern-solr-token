begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|conn
operator|.
name|ClientConnectionManager
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingClientConnectionManager
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
name|impl
operator|.
name|HttpClientUtil
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
name|SolrException
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
name|ModifiableSolrParams
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
name|ExecutorUtil
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
name|SolrjNamedThreadFactory
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
name|core
operator|.
name|ConfigSolr
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|UpdateShardHandler
specifier|public
class|class
name|UpdateShardHandler
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UpdateShardHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|updateExecutor
specifier|private
name|ExecutorService
name|updateExecutor
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"updateExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|clientConnectionManager
specifier|private
name|PoolingClientConnectionManager
name|clientConnectionManager
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|HttpClient
name|client
decl_stmt|;
DECL|method|UpdateShardHandler
specifier|public
name|UpdateShardHandler
parameter_list|(
name|ConfigSolr
name|cfg
parameter_list|)
block|{
name|clientConnectionManager
operator|=
operator|new
name|PoolingClientConnectionManager
argument_list|()
expr_stmt|;
name|clientConnectionManager
operator|.
name|setDefaultMaxPerRoute
argument_list|(
name|cfg
operator|.
name|getMaxUpdateConnections
argument_list|()
argument_list|)
expr_stmt|;
name|clientConnectionManager
operator|.
name|setDefaultMaxPerRoute
argument_list|(
name|cfg
operator|.
name|getMaxUpdateConnectionsPerHost
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|,
name|cfg
operator|.
name|getDistributedSocketTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|,
name|cfg
operator|.
name|getDistributedConnectionTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_USE_RETRY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|client
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
name|params
argument_list|,
name|clientConnectionManager
argument_list|)
expr_stmt|;
block|}
DECL|method|getHttpClient
specifier|public
name|HttpClient
name|getHttpClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
DECL|method|getConnectionManager
specifier|public
name|ClientConnectionManager
name|getConnectionManager
parameter_list|()
block|{
return|return
name|clientConnectionManager
return|;
block|}
DECL|method|getUpdateExecutor
specifier|public
name|ExecutorService
name|getUpdateExecutor
parameter_list|()
block|{
return|return
name|updateExecutor
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|updateExecutor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|clientConnectionManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
