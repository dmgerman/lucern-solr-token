begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|DOMUtil
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
name|SolrConfig
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
name|Config
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_comment
comment|/**  * Contains the knowledge of how cache config is  * stored in the solrconfig.xml file, and implements a  * factory to create caches.  *  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|CacheConfig
class|class
name|CacheConfig
block|{
DECL|field|nodeName
specifier|private
name|String
name|nodeName
decl_stmt|;
DECL|field|args
specifier|private
name|Map
name|args
decl_stmt|;
DECL|field|cacheImpl
specifier|private
name|String
name|cacheImpl
decl_stmt|;
DECL|field|clazz
specifier|private
name|Class
name|clazz
decl_stmt|;
DECL|field|persistence
specifier|private
name|Object
index|[]
name|persistence
init|=
operator|new
name|Object
index|[
literal|1
index|]
decl_stmt|;
DECL|field|regenImpl
specifier|private
name|String
name|regenImpl
decl_stmt|;
DECL|field|regenerator
specifier|private
name|CacheRegenerator
name|regenerator
decl_stmt|;
DECL|method|getRegenerator
specifier|public
name|CacheRegenerator
name|getRegenerator
parameter_list|()
block|{
return|return
name|regenerator
return|;
block|}
DECL|method|setRegenerator
specifier|public
name|void
name|setRegenerator
parameter_list|(
name|CacheRegenerator
name|regenerator
parameter_list|)
block|{
name|this
operator|.
name|regenerator
operator|=
name|regenerator
expr_stmt|;
block|}
DECL|method|getMultipleConfigs
specifier|public
specifier|static
name|CacheConfig
index|[]
name|getMultipleConfigs
parameter_list|(
name|String
name|configPath
parameter_list|)
block|{
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|SolrConfig
operator|.
name|config
operator|.
name|evaluate
argument_list|(
name|configPath
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
operator|||
name|nodes
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|CacheConfig
index|[]
name|configs
init|=
operator|new
name|CacheConfig
index|[
name|nodes
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|configs
index|[
name|i
index|]
operator|=
name|getConfig
argument_list|(
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|configs
return|;
block|}
DECL|method|getConfig
specifier|public
specifier|static
name|CacheConfig
name|getConfig
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|SolrConfig
operator|.
name|config
operator|.
name|getNode
argument_list|(
name|xpath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|getConfig
argument_list|(
name|node
argument_list|)
return|;
block|}
DECL|method|getConfig
specifier|public
specifier|static
name|CacheConfig
name|getConfig
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|CacheConfig
name|config
init|=
operator|new
name|CacheConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|nodeName
operator|=
name|node
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
name|config
operator|.
name|args
operator|=
name|DOMUtil
operator|.
name|toMap
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|nameAttr
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
comment|// OPTIONAL
if|if
condition|(
name|nameAttr
operator|==
literal|null
condition|)
block|{
name|config
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|config
operator|.
name|nodeName
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|cacheImpl
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|args
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
expr_stmt|;
name|config
operator|.
name|regenImpl
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|args
operator|.
name|get
argument_list|(
literal|"regenerator"
argument_list|)
expr_stmt|;
name|config
operator|.
name|clazz
operator|=
name|Config
operator|.
name|findClass
argument_list|(
name|config
operator|.
name|cacheImpl
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|regenImpl
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|regenerator
operator|=
operator|(
name|CacheRegenerator
operator|)
name|Config
operator|.
name|newInstance
argument_list|(
name|config
operator|.
name|regenImpl
argument_list|)
expr_stmt|;
block|}
return|return
name|config
return|;
block|}
DECL|method|newInstance
specifier|public
name|SolrCache
name|newInstance
parameter_list|()
block|{
try|try
block|{
name|SolrCache
name|cache
init|=
operator|(
name|SolrCache
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|persistence
index|[
literal|0
index|]
operator|=
name|cache
operator|.
name|init
argument_list|(
name|args
argument_list|,
name|persistence
index|[
literal|0
index|]
argument_list|,
name|regenerator
argument_list|)
expr_stmt|;
return|return
name|cache
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCache
operator|.
name|log
argument_list|,
literal|"Error instantiating cache"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// we can carry on without a cache... but should we?
comment|// in some cases (like an OOM) we probably should try to continue.
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
