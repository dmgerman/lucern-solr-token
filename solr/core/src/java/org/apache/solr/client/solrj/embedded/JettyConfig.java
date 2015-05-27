begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
name|embedded
package|;
end_package
begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
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
name|TreeMap
import|;
end_import
begin_class
DECL|class|JettyConfig
specifier|public
class|class
name|JettyConfig
block|{
DECL|field|port
specifier|public
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|context
specifier|public
specifier|final
name|String
name|context
decl_stmt|;
DECL|field|stopAtShutdown
specifier|public
specifier|final
name|boolean
name|stopAtShutdown
decl_stmt|;
DECL|field|waitForLoadingCoresToFinishMs
specifier|public
specifier|final
name|Long
name|waitForLoadingCoresToFinishMs
decl_stmt|;
DECL|field|extraServlets
specifier|public
specifier|final
name|Map
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
decl_stmt|;
DECL|field|extraFilters
specifier|public
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraFilters
decl_stmt|;
DECL|field|sslConfig
specifier|public
specifier|final
name|SSLConfig
name|sslConfig
decl_stmt|;
DECL|method|JettyConfig
specifier|private
name|JettyConfig
parameter_list|(
name|int
name|port
parameter_list|,
name|String
name|context
parameter_list|,
name|boolean
name|stopAtShutdown
parameter_list|,
name|Long
name|waitForLoadingCoresToFinishMs
parameter_list|,
name|Map
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
parameter_list|,
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraFilters
parameter_list|,
name|SSLConfig
name|sslConfig
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|stopAtShutdown
operator|=
name|stopAtShutdown
expr_stmt|;
name|this
operator|.
name|waitForLoadingCoresToFinishMs
operator|=
name|waitForLoadingCoresToFinishMs
expr_stmt|;
name|this
operator|.
name|extraServlets
operator|=
name|extraServlets
expr_stmt|;
name|this
operator|.
name|extraFilters
operator|=
name|extraFilters
expr_stmt|;
name|this
operator|.
name|sslConfig
operator|=
name|sslConfig
expr_stmt|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|JettyConfig
name|other
parameter_list|)
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|port
operator|=
name|other
operator|.
name|port
expr_stmt|;
name|builder
operator|.
name|context
operator|=
name|other
operator|.
name|context
expr_stmt|;
name|builder
operator|.
name|stopAtShutdown
operator|=
name|other
operator|.
name|stopAtShutdown
expr_stmt|;
name|builder
operator|.
name|extraServlets
operator|=
name|other
operator|.
name|extraServlets
expr_stmt|;
name|builder
operator|.
name|extraFilters
operator|=
name|other
operator|.
name|extraFilters
expr_stmt|;
name|builder
operator|.
name|sslConfig
operator|=
name|other
operator|.
name|sslConfig
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|port
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|context
name|String
name|context
init|=
literal|"/solr"
decl_stmt|;
DECL|field|stopAtShutdown
name|boolean
name|stopAtShutdown
init|=
literal|true
decl_stmt|;
DECL|field|waitForLoadingCoresToFinishMs
name|Long
name|waitForLoadingCoresToFinishMs
init|=
literal|300000L
decl_stmt|;
DECL|field|extraServlets
name|Map
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|extraFilters
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|extraFilters
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sslConfig
name|SSLConfig
name|sslConfig
init|=
literal|null
decl_stmt|;
DECL|method|setPort
specifier|public
name|Builder
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setContext
specifier|public
name|Builder
name|setContext
parameter_list|(
name|String
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|stopAtShutdown
specifier|public
name|Builder
name|stopAtShutdown
parameter_list|(
name|boolean
name|stopAtShutdown
parameter_list|)
block|{
name|this
operator|.
name|stopAtShutdown
operator|=
name|stopAtShutdown
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|waitForLoadingCoresToFinish
specifier|public
name|Builder
name|waitForLoadingCoresToFinish
parameter_list|(
name|Long
name|waitForLoadingCoresToFinishMs
parameter_list|)
block|{
name|this
operator|.
name|waitForLoadingCoresToFinishMs
operator|=
name|waitForLoadingCoresToFinishMs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withServlet
specifier|public
name|Builder
name|withServlet
parameter_list|(
name|ServletHolder
name|servlet
parameter_list|,
name|String
name|servletName
parameter_list|)
block|{
name|extraServlets
operator|.
name|put
argument_list|(
name|servlet
argument_list|,
name|servletName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withServlets
specifier|public
name|Builder
name|withServlets
parameter_list|(
name|Map
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|servlets
parameter_list|)
block|{
if|if
condition|(
name|servlets
operator|!=
literal|null
condition|)
name|extraServlets
operator|.
name|putAll
argument_list|(
name|servlets
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withFilter
specifier|public
name|Builder
name|withFilter
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filterClass
parameter_list|,
name|String
name|filterName
parameter_list|)
block|{
name|extraFilters
operator|.
name|put
argument_list|(
name|filterClass
argument_list|,
name|filterName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withFilters
specifier|public
name|Builder
name|withFilters
parameter_list|(
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
argument_list|,
name|String
argument_list|>
name|filters
parameter_list|)
block|{
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
name|extraFilters
operator|.
name|putAll
argument_list|(
name|filters
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withSSLConfig
specifier|public
name|Builder
name|withSSLConfig
parameter_list|(
name|SSLConfig
name|sslConfig
parameter_list|)
block|{
name|this
operator|.
name|sslConfig
operator|=
name|sslConfig
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|JettyConfig
name|build
parameter_list|()
block|{
return|return
operator|new
name|JettyConfig
argument_list|(
name|port
argument_list|,
name|context
argument_list|,
name|stopAtShutdown
argument_list|,
name|waitForLoadingCoresToFinishMs
argument_list|,
name|extraServlets
argument_list|,
name|extraFilters
argument_list|,
name|sslConfig
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
