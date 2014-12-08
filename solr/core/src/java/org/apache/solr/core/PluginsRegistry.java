begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|NamedList
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
name|PingRequestHandler
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
name|RealTimeGetHandler
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
name|ReplicationHandler
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
name|SchemaHandler
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
name|SolrConfigHandler
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
name|UpdateRequestHandler
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
name|admin
operator|.
name|LoggingHandler
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
name|admin
operator|.
name|LukeRequestHandler
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
name|admin
operator|.
name|PluginInfoHandler
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
name|admin
operator|.
name|PropertiesRequestHandler
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
name|admin
operator|.
name|ShowFileRequestHandler
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
name|admin
operator|.
name|SolrInfoMBeanHandler
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
name|admin
operator|.
name|SystemInfoHandler
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
name|admin
operator|.
name|ThreadDumpHandler
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
name|SolrRequestHandler
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
operator|.
name|makeMap
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|PluginInfo
operator|.
name|DEFAULTS
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|PluginInfo
operator|.
name|INVARIANTS
import|;
end_import
begin_class
DECL|class|PluginsRegistry
specifier|public
class|class
name|PluginsRegistry
block|{
DECL|method|getHandlers
specifier|public
specifier|static
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|getHandlers
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|implicits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//update handle implicits
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/update"
argument_list|,
name|UpdateRequestHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
name|UpdateRequestHandler
operator|.
name|JSON_PATH
argument_list|,
name|UpdateRequestHandler
operator|.
name|class
argument_list|,
name|singletonMap
argument_list|(
literal|"update.contentType"
argument_list|,
literal|"application/json"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
name|UpdateRequestHandler
operator|.
name|CSV_PATH
argument_list|,
name|UpdateRequestHandler
operator|.
name|class
argument_list|,
name|singletonMap
argument_list|(
literal|"update.contentType"
argument_list|,
literal|"application/csv"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
name|UpdateRequestHandler
operator|.
name|DOC_PATH
argument_list|,
name|UpdateRequestHandler
operator|.
name|class
argument_list|,
name|makeMap
argument_list|(
literal|"update.contentType"
argument_list|,
literal|"application/json"
argument_list|,
literal|"json.command"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//solrconfighandler
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/config"
argument_list|,
name|SolrConfigHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//schemahandler
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/schema"
argument_list|,
name|SchemaHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//register replicationhandler always for SolrCloud
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/replication"
argument_list|,
name|ReplicationHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/get"
argument_list|,
name|RealTimeGetHandler
operator|.
name|class
argument_list|,
name|makeMap
argument_list|(
literal|"omitHeader"
argument_list|,
literal|"true"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//register adminHandlers
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/luke"
argument_list|,
name|LukeRequestHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/system"
argument_list|,
name|SystemInfoHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/mbeans"
argument_list|,
name|SolrInfoMBeanHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/plugins"
argument_list|,
name|PluginInfoHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/threads"
argument_list|,
name|ThreadDumpHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/properties"
argument_list|,
name|PropertiesRequestHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/logging"
argument_list|,
name|LoggingHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getReqHandlerInfo
argument_list|(
literal|"/admin/file"
argument_list|,
name|ShowFileRequestHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|PluginInfo
name|ping
init|=
name|getReqHandlerInfo
argument_list|(
literal|"/admin/ping"
argument_list|,
name|PingRequestHandler
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ping
operator|.
name|initArgs
operator|.
name|add
argument_list|(
name|INVARIANTS
argument_list|,
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|makeMap
argument_list|(
literal|"echoParams"
argument_list|,
literal|"all"
argument_list|,
literal|"q"
argument_list|,
literal|"solrpingquery"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|ping
argument_list|)
expr_stmt|;
return|return
name|implicits
return|;
block|}
DECL|method|getReqHandlerInfo
specifier|public
specifier|static
name|PluginInfo
name|getReqHandlerInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
name|clz
parameter_list|,
name|Map
name|defaults
parameter_list|)
block|{
if|if
condition|(
name|defaults
operator|==
literal|null
condition|)
name|defaults
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|,
literal|"class"
argument_list|,
name|clz
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PluginInfo
argument_list|(
name|SolrRequestHandler
operator|.
name|TYPE
argument_list|,
name|m
argument_list|,
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|singletonMap
argument_list|(
name|DEFAULTS
argument_list|,
operator|new
name|NamedList
argument_list|(
name|defaults
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class
end_unit