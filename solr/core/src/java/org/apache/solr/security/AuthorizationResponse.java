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
begin_comment
comment|/* This class currently only stores an int statusCode (HttpStatus) value and a message but can     be used to return ACLs and other information from the authorization plugin.  */
end_comment
begin_class
DECL|class|AuthorizationResponse
specifier|public
class|class
name|AuthorizationResponse
block|{
DECL|field|OK
specifier|public
specifier|static
specifier|final
name|AuthorizationResponse
name|OK
init|=
operator|new
name|AuthorizationResponse
argument_list|(
literal|200
argument_list|)
decl_stmt|;
DECL|field|FORBIDDEN
specifier|public
specifier|static
specifier|final
name|AuthorizationResponse
name|FORBIDDEN
init|=
operator|new
name|AuthorizationResponse
argument_list|(
literal|403
argument_list|)
decl_stmt|;
DECL|field|PROMPT
specifier|public
specifier|static
specifier|final
name|AuthorizationResponse
name|PROMPT
init|=
operator|new
name|AuthorizationResponse
argument_list|(
literal|401
argument_list|)
decl_stmt|;
DECL|field|statusCode
specifier|public
specifier|final
name|int
name|statusCode
decl_stmt|;
DECL|field|message
name|String
name|message
decl_stmt|;
DECL|method|AuthorizationResponse
specifier|public
name|AuthorizationResponse
parameter_list|(
name|int
name|httpStatusCode
parameter_list|)
block|{
name|this
operator|.
name|statusCode
operator|=
name|httpStatusCode
expr_stmt|;
block|}
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
DECL|method|setMessage
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
block|}
end_class
end_unit
