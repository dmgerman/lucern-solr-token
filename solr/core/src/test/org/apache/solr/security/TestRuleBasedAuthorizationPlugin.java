begin_unit
begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|Enumeration
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|http
operator|.
name|auth
operator|.
name|BasicUserPrincipal
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
name|SolrTestCaseJ4
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
name|MapSolrParams
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
name|Utils
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
name|security
operator|.
name|AuthorizationContext
operator|.
name|CollectionRequest
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
name|security
operator|.
name|AuthorizationContext
operator|.
name|RequestType
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
name|singletonList
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
name|util
operator|.
name|Utils
operator|.
name|makeMap
import|;
end_import
begin_class
DECL|class|TestRuleBasedAuthorizationPlugin
specifier|public
class|class
name|TestRuleBasedAuthorizationPlugin
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|permissions
name|String
name|permissions
init|=
literal|"{"
operator|+
literal|"  user-role : {"
operator|+
literal|"    steve: [dev,user],"
operator|+
literal|"    tim: [dev,admin],"
operator|+
literal|"    joe: [user],"
operator|+
literal|"    noble:[dev,user]"
operator|+
literal|"  },"
operator|+
literal|"  permissions : ["
operator|+
literal|"    {name:'schema-edit',"
operator|+
literal|"     role:admin},"
operator|+
literal|"    {name:'collection-admin-read',"
operator|+
literal|"    role:null},"
operator|+
literal|"    {name:collection-admin-edit ,"
operator|+
literal|"    role:admin},"
operator|+
literal|"    {name:mycoll_update,"
operator|+
literal|"      collection:mycoll,"
operator|+
literal|"      path:'/update/*',"
operator|+
literal|"      role:[dev,admin]"
operator|+
literal|"    },"
operator|+
literal|"{name:read , role:dev },"
operator|+
literal|"{name:freeforall, path:'/foo', role:'*'}]}"
decl_stmt|;
DECL|method|testBasicPermissions
specifier|public
name|void
name|testBasicPermissions
parameter_list|()
block|{
name|int
name|STATUS_OK
init|=
literal|200
decl_stmt|;
name|int
name|FORBIDDEN
init|=
literal|403
decl_stmt|;
name|int
name|PROMPT_FOR_CREDENTIALS
init|=
literal|401
decl_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/update/json/docs"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"POST"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"unknownuser"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"freeforall"
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/update/json/docs"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"POST"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"tim"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"mycoll"
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/update/json/docs"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"POST"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"mycoll"
argument_list|)
argument_list|,
name|PROMPT_FOR_CREDENTIALS
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/schema"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"somebody"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"mycoll"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"POST"
argument_list|)
argument_list|,
name|FORBIDDEN
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/schema"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"somebody"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"mycoll"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"GET"
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/schema/fields"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"somebody"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"mycoll"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"GET"
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/schema"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"somebody"
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|"mycoll"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"POST"
argument_list|)
argument_list|,
name|FORBIDDEN
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/admin/collections"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"tim"
argument_list|,
literal|"requestType"
argument_list|,
name|RequestType
operator|.
name|ADMIN
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|null
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"GET"
argument_list|,
literal|"params"
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
literal|"action"
argument_list|,
literal|"LIST"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/admin/collections"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|null
argument_list|,
literal|"requestType"
argument_list|,
name|RequestType
operator|.
name|ADMIN
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|null
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"GET"
argument_list|,
literal|"params"
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
literal|"action"
argument_list|,
literal|"LIST"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/admin/collections"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|null
argument_list|,
literal|"requestType"
argument_list|,
name|RequestType
operator|.
name|ADMIN
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|null
argument_list|,
literal|"params"
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
literal|"action"
argument_list|,
literal|"CREATE"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|PROMPT_FOR_CREDENTIALS
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/admin/collections"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|null
argument_list|,
literal|"requestType"
argument_list|,
name|RequestType
operator|.
name|ADMIN
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|null
argument_list|,
literal|"params"
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
literal|"action"
argument_list|,
literal|"RELOAD"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|PROMPT_FOR_CREDENTIALS
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/admin/collections"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"somebody"
argument_list|,
literal|"requestType"
argument_list|,
name|RequestType
operator|.
name|ADMIN
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|null
argument_list|,
literal|"params"
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
literal|"action"
argument_list|,
literal|"CREATE"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|FORBIDDEN
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/admin/collections"
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"tim"
argument_list|,
literal|"requestType"
argument_list|,
name|RequestType
operator|.
name|ADMIN
argument_list|,
literal|"collectionRequests"
argument_list|,
literal|null
argument_list|,
literal|"params"
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|singletonMap
argument_list|(
literal|"action"
argument_list|,
literal|"CREATE"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|STATUS_OK
argument_list|)
expr_stmt|;
name|checkRules
argument_list|(
name|makeMap
argument_list|(
literal|"resource"
argument_list|,
literal|"/select"
argument_list|,
literal|"httpMethod"
argument_list|,
literal|"GET"
argument_list|,
literal|"collectionRequests"
argument_list|,
name|singletonList
argument_list|(
operator|new
name|CollectionRequest
argument_list|(
literal|"mycoll"
argument_list|)
argument_list|)
argument_list|,
literal|"userPrincipal"
argument_list|,
literal|"joe"
argument_list|)
argument_list|,
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
DECL|method|checkRules
specifier|private
name|void
name|checkRules
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
name|AuthorizationContext
name|context
init|=
operator|new
name|MockAuthorizationContext
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|RuleBasedAuthorizationPlugin
name|plugin
init|=
operator|new
name|RuleBasedAuthorizationPlugin
argument_list|()
decl_stmt|;
name|plugin
operator|.
name|init
argument_list|(
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSONString
argument_list|(
name|permissions
argument_list|)
argument_list|)
expr_stmt|;
name|AuthorizationResponse
name|authResp
init|=
name|plugin
operator|.
name|authorize
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|authResp
operator|.
name|statusCode
argument_list|)
expr_stmt|;
block|}
DECL|class|MockAuthorizationContext
specifier|private
specifier|static
class|class
name|MockAuthorizationContext
extends|extends
name|AuthorizationContext
block|{
DECL|field|values
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
decl_stmt|;
DECL|method|MockAuthorizationContext
specifier|private
name|MockAuthorizationContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|SolrParams
name|params
init|=
operator|(
name|SolrParams
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
return|return
name|params
operator|==
literal|null
condition|?
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
else|:
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|getUserPrincipal
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
name|Object
name|userPrincipal
init|=
name|values
operator|.
name|get
argument_list|(
literal|"userPrincipal"
argument_list|)
decl_stmt|;
return|return
name|userPrincipal
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BasicUserPrincipal
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|userPrincipal
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpHeader
specifier|public
name|String
name|getHttpHeader
parameter_list|(
name|String
name|header
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getHeaderNames
specifier|public
name|Enumeration
name|getHeaderNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteAddr
specifier|public
name|String
name|getRemoteAddr
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteHost
specifier|public
name|String
name|getRemoteHost
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCollectionRequests
specifier|public
name|List
argument_list|<
name|CollectionRequest
argument_list|>
name|getCollectionRequests
parameter_list|()
block|{
name|Object
name|collectionRequests
init|=
name|values
operator|.
name|get
argument_list|(
literal|"collectionRequests"
argument_list|)
decl_stmt|;
if|if
condition|(
name|collectionRequests
operator|instanceof
name|String
condition|)
block|{
return|return
name|singletonList
argument_list|(
operator|new
name|CollectionRequest
argument_list|(
operator|(
name|String
operator|)
name|collectionRequests
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|(
name|List
argument_list|<
name|CollectionRequest
argument_list|>
operator|)
name|collectionRequests
return|;
block|}
annotation|@
name|Override
DECL|method|getRequestType
specifier|public
name|RequestType
name|getRequestType
parameter_list|()
block|{
return|return
operator|(
name|RequestType
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"requestType"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpMethod
specifier|public
name|String
name|getHttpMethod
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"httpMethod"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getResource
specifier|public
name|String
name|getResource
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"resource"
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
