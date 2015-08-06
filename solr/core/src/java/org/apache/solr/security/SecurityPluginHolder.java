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
begin_class
DECL|class|SecurityPluginHolder
specifier|public
class|class
name|SecurityPluginHolder
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|znodeVersion
specifier|private
specifier|final
name|int
name|znodeVersion
decl_stmt|;
DECL|field|plugin
specifier|public
specifier|final
name|T
name|plugin
decl_stmt|;
DECL|method|SecurityPluginHolder
specifier|public
name|SecurityPluginHolder
parameter_list|(
name|int
name|znodeVersion
parameter_list|,
name|T
name|plugin
parameter_list|)
block|{
name|this
operator|.
name|znodeVersion
operator|=
name|znodeVersion
expr_stmt|;
name|this
operator|.
name|plugin
operator|=
name|plugin
expr_stmt|;
block|}
DECL|method|getZnodeVersion
specifier|public
name|int
name|getZnodeVersion
parameter_list|()
block|{
return|return
name|znodeVersion
return|;
block|}
block|}
end_class
end_unit
