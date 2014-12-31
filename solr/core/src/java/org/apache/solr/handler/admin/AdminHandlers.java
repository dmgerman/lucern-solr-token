begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrRequestHandler
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
begin_comment
comment|/**  * A special Handler that registers all standard admin handlers  *   * @since solr 1.3  * @deprecated No need to register this requesthandler . All  * the plugins registered by this class are iplicitly registered by the system  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|AdminHandlers
specifier|public
class|class
name|AdminHandlers
implements|implements
name|SolrCoreAware
implements|,
name|SolrRequestHandler
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AdminHandlers
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|initArgs
name|NamedList
name|initArgs
init|=
literal|null
decl_stmt|;
DECL|class|StandardHandler
specifier|private
specifier|static
class|class
name|StandardHandler
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|handler
specifier|final
name|SolrRequestHandler
name|handler
decl_stmt|;
DECL|method|StandardHandler
specifier|public
name|StandardHandler
parameter_list|(
name|String
name|n
parameter_list|,
name|SolrRequestHandler
name|h
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|h
expr_stmt|;
block|}
block|}
comment|/**    * Save the args and pass them to each standard handler    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|this
operator|.
name|initArgs
operator|=
name|args
expr_stmt|;
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
name|String
name|path
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|entry
range|:
name|core
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|this
condition|)
block|{
name|path
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"The AdminHandler is not registered with the current core."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"The AdminHandler needs to be registered to a path.  Typically this is '/admin'"
argument_list|)
throw|;
block|}
comment|// Remove the parent handler
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|+=
literal|"/"
expr_stmt|;
block|}
name|StandardHandler
index|[]
name|list
init|=
operator|new
name|StandardHandler
index|[]
block|{
operator|new
name|StandardHandler
argument_list|(
literal|"luke"
argument_list|,
operator|new
name|LukeRequestHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"system"
argument_list|,
operator|new
name|SystemInfoHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"mbeans"
argument_list|,
operator|new
name|SolrInfoMBeanHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"plugins"
argument_list|,
operator|new
name|PluginInfoHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"threads"
argument_list|,
operator|new
name|ThreadDumpHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"properties"
argument_list|,
operator|new
name|PropertiesRequestHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"logging"
argument_list|,
operator|new
name|LoggingHandler
argument_list|()
argument_list|)
block|,
operator|new
name|StandardHandler
argument_list|(
literal|"file"
argument_list|,
operator|new
name|ShowFileRequestHandler
argument_list|()
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|StandardHandler
name|handler
range|:
name|list
control|)
block|{
if|if
condition|(
name|core
operator|.
name|getRequestHandler
argument_list|(
name|path
operator|+
name|handler
operator|.
name|name
argument_list|)
operator|==
literal|null
condition|)
block|{
name|handler
operator|.
name|handler
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
name|core
operator|.
name|registerRequestHandler
argument_list|(
name|path
operator|+
name|handler
operator|.
name|name
argument_list|,
name|handler
operator|.
name|handler
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|.
name|handler
operator|instanceof
name|SolrCoreAware
condition|)
block|{
operator|(
operator|(
name|SolrCoreAware
operator|)
name|handler
operator|.
name|handler
operator|)
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|log
operator|.
name|warn
argument_list|(
literal|"<requestHandler name=\"/admin/\" \n class=\"solr.admin.AdminHandlers\" /> is deprecated . It is not required anymore"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"The AdminHandler should never be called directly"
argument_list|)
throw|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Register Standard Admin Handlers"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
