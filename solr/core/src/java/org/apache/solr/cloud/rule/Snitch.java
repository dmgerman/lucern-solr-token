begin_unit
begin_package
DECL|package|org.apache.solr.cloud.rule
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|rule
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
name|Set
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|Snitch
specifier|public
specifier|abstract
class|class
name|Snitch
block|{
DECL|field|WELL_KNOWN_SNITCHES
specifier|static
name|Set
argument_list|<
name|Class
argument_list|>
name|WELL_KNOWN_SNITCHES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ImplicitSnitch
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getTags
specifier|public
specifier|abstract
name|void
name|getTags
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedTags
parameter_list|,
name|SnitchContext
name|ctx
parameter_list|)
function_decl|;
DECL|method|isKnownTag
specifier|public
specifier|abstract
name|boolean
name|isKnownTag
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
block|}
end_class
end_unit
