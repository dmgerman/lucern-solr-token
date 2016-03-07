begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|solr
operator|.
name|request
operator|.
name|SolrRequestInfo
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
name|schema
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|Application
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|Restlet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|routing
operator|.
name|Router
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
comment|/**  * Restlet servlet handling /&lt;context&gt;/&lt;collection&gt;/schema/* URL paths  */
end_comment
begin_class
DECL|class|SolrSchemaRestApi
specifier|public
class|class
name|SolrSchemaRestApi
extends|extends
name|Application
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Returns reserved endpoints under /schema    */
DECL|method|getReservedEndpoints
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getReservedEndpoints
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|reservedEndpoints
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|reservedEndpoints
argument_list|)
return|;
block|}
DECL|field|router
specifier|private
name|Router
name|router
decl_stmt|;
DECL|method|SolrSchemaRestApi
specifier|public
name|SolrSchemaRestApi
parameter_list|()
block|{
name|router
operator|=
operator|new
name|Router
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|null
operator|!=
name|router
condition|)
block|{
name|router
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Bind URL paths to the appropriate ServerResource subclass.     */
annotation|@
name|Override
DECL|method|createInboundRoot
specifier|public
specifier|synchronized
name|Restlet
name|createInboundRoot
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"createInboundRoot started for /schema"
argument_list|)
expr_stmt|;
name|router
operator|.
name|attachDefault
argument_list|(
name|RestManager
operator|.
name|ManagedEndpoint
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// attach all the dynamically registered schema resources
name|RestManager
operator|.
name|getRestManager
argument_list|(
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
argument_list|)
operator|.
name|attachManagedResources
argument_list|(
name|RestManager
operator|.
name|SCHEMA_BASE_PATH
argument_list|,
name|router
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"createInboundRoot complete for /schema"
argument_list|)
expr_stmt|;
return|return
name|router
return|;
block|}
block|}
end_class
end_unit
