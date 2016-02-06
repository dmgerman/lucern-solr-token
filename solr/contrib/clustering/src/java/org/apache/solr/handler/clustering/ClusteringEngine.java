begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.clustering
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
package|;
end_package
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
begin_comment
comment|/**  * A base class for {@link SearchClusteringEngine} and {@link DocumentClusteringEngine}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ClusteringEngine
specifier|public
specifier|abstract
class|class
name|ClusteringEngine
block|{
DECL|field|ENGINE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ENGINE_NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|DEFAULT_ENGINE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_ENGINE_NAME
init|=
literal|"default"
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|name
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|ENGINE_NAME
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|isAvailable
specifier|public
specifier|abstract
name|boolean
name|isAvailable
parameter_list|()
function_decl|;
block|}
end_class
end_unit
