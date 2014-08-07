begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|SolrException
operator|.
name|ErrorCode
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
name|CoreContainer
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
name|RequestHandlerBase
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
DECL|class|InfoHandler
specifier|public
class|class
name|InfoHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InfoHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|coreContainer
specifier|protected
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|threadDumpHandler
specifier|private
name|ThreadDumpHandler
name|threadDumpHandler
init|=
operator|new
name|ThreadDumpHandler
argument_list|()
decl_stmt|;
DECL|field|propertiesHandler
specifier|private
name|PropertiesRequestHandler
name|propertiesHandler
init|=
operator|new
name|PropertiesRequestHandler
argument_list|()
decl_stmt|;
DECL|field|loggingHandler
specifier|private
name|LoggingHandler
name|loggingHandler
decl_stmt|;
DECL|field|systemInfoHandler
specifier|private
name|SystemInfoHandler
name|systemInfoHandler
decl_stmt|;
comment|/**    * Overloaded ctor to inject CoreContainer into the handler.    *    * @param coreContainer Core Container of the solr webapp installed.    */
DECL|method|InfoHandler
specifier|public
name|InfoHandler
parameter_list|(
specifier|final
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
name|systemInfoHandler
operator|=
operator|new
name|SystemInfoHandler
argument_list|(
name|coreContainer
argument_list|)
expr_stmt|;
name|loggingHandler
operator|=
operator|new
name|LoggingHandler
argument_list|(
name|coreContainer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|final
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{    }
comment|/**    * The instance of CoreContainer this handler handles. This should be the CoreContainer instance that created this    * handler.    *    * @return a CoreContainer instance    */
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|coreContainer
return|;
block|}
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
name|Exception
block|{
comment|// Make sure the cores is enabled
name|CoreContainer
name|cores
init|=
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
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
name|BAD_REQUEST
argument_list|,
literal|"Core container instance missing"
argument_list|)
throw|;
block|}
name|String
name|path
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|int
name|i
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|path
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"properties"
argument_list|)
condition|)
block|{
name|propertiesHandler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"threads"
argument_list|)
condition|)
block|{
name|threadDumpHandler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"logging"
argument_list|)
condition|)
block|{
name|loggingHandler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"system"
argument_list|)
condition|)
block|{
name|systemInfoHandler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"info"
argument_list|)
condition|)
name|name
operator|=
literal|""
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Info Handler not found: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
literal|"System Information"
return|;
block|}
DECL|method|getPropertiesHandler
specifier|protected
name|PropertiesRequestHandler
name|getPropertiesHandler
parameter_list|()
block|{
return|return
name|propertiesHandler
return|;
block|}
DECL|method|getThreadDumpHandler
specifier|protected
name|ThreadDumpHandler
name|getThreadDumpHandler
parameter_list|()
block|{
return|return
name|threadDumpHandler
return|;
block|}
DECL|method|getLoggingHandler
specifier|protected
name|LoggingHandler
name|getLoggingHandler
parameter_list|()
block|{
return|return
name|loggingHandler
return|;
block|}
DECL|method|getSystemInfoHandler
specifier|protected
name|SystemInfoHandler
name|getSystemInfoHandler
parameter_list|()
block|{
return|return
name|systemInfoHandler
return|;
block|}
DECL|method|setPropertiesHandler
specifier|protected
name|void
name|setPropertiesHandler
parameter_list|(
name|PropertiesRequestHandler
name|propertiesHandler
parameter_list|)
block|{
name|this
operator|.
name|propertiesHandler
operator|=
name|propertiesHandler
expr_stmt|;
block|}
DECL|method|setThreadDumpHandler
specifier|protected
name|void
name|setThreadDumpHandler
parameter_list|(
name|ThreadDumpHandler
name|threadDumpHandler
parameter_list|)
block|{
name|this
operator|.
name|threadDumpHandler
operator|=
name|threadDumpHandler
expr_stmt|;
block|}
DECL|method|setLoggingHandler
specifier|protected
name|void
name|setLoggingHandler
parameter_list|(
name|LoggingHandler
name|loggingHandler
parameter_list|)
block|{
name|this
operator|.
name|loggingHandler
operator|=
name|loggingHandler
expr_stmt|;
block|}
DECL|method|setSystemInfoHandler
specifier|protected
name|void
name|setSystemInfoHandler
parameter_list|(
name|SystemInfoHandler
name|systemInfoHandler
parameter_list|)
block|{
name|this
operator|.
name|systemInfoHandler
operator|=
name|systemInfoHandler
expr_stmt|;
block|}
block|}
end_class
end_unit
