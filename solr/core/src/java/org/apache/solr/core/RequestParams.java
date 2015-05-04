begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|LinkedHashMap
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
name|cloud
operator|.
name|ZkSolrResourceLoader
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
comment|/**  * The class encapsulates the request time parameters . This is immutable and any changes performed  * returns a copy of the Object with the changed values  */
end_comment
begin_class
DECL|class|RequestParams
specifier|public
class|class
name|RequestParams
implements|implements
name|MapSerializable
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RequestParams
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|Map
name|data
decl_stmt|;
DECL|field|paramsets
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|VersionedParams
argument_list|>
name|paramsets
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|znodeVersion
specifier|private
specifier|final
name|int
name|znodeVersion
decl_stmt|;
DECL|method|RequestParams
specifier|public
name|RequestParams
parameter_list|(
name|Map
name|data
parameter_list|,
name|int
name|znodeVersion
parameter_list|)
block|{
if|if
condition|(
name|data
operator|==
literal|null
condition|)
name|data
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|Map
name|paramsets
init|=
operator|(
name|Map
operator|)
name|data
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|paramsets
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|paramsets
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|value
init|=
operator|(
name|Map
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Map
name|copy
init|=
name|getMapCopy
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|Map
name|meta
init|=
operator|(
name|Map
operator|)
name|copy
operator|.
name|remove
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|this
operator|.
name|paramsets
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|VersionedParams
argument_list|(
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|copy
argument_list|)
argument_list|,
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|znodeVersion
operator|=
name|znodeVersion
expr_stmt|;
block|}
DECL|method|getMapCopy
specifier|private
specifier|static
name|Map
name|getMapCopy
parameter_list|(
name|Map
name|value
parameter_list|)
block|{
name|Map
name|copy
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o1
range|:
name|value
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o1
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|copy
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|List
condition|)
block|{
name|List
name|l
init|=
operator|(
name|List
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
index|[]
name|sarr
init|=
operator|new
name|String
index|[
name|l
operator|.
name|size
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
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
literal|null
condition|)
name|sarr
index|[
name|i
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|copy
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|sarr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|copy
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|copy
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|copy
return|;
block|}
DECL|method|getParams
specifier|public
name|VersionedParams
name|getParams
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|paramsets
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
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
annotation|@
name|Override
DECL|method|toMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toMap
parameter_list|()
block|{
return|return
name|getMapWithVersion
argument_list|(
name|data
argument_list|,
name|znodeVersion
argument_list|)
return|;
block|}
DECL|method|getMapWithVersion
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getMapWithVersion
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
parameter_list|,
name|int
name|znodeVersion
parameter_list|)
block|{
name|Map
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|ConfigOverlay
operator|.
name|ZNODEVER
argument_list|,
name|znodeVersion
argument_list|)
expr_stmt|;
name|result
operator|.
name|putAll
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|setParams
specifier|public
name|RequestParams
name|setParams
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|values
parameter_list|)
block|{
name|Map
name|deepCopy
init|=
name|getDeepCopy
argument_list|(
name|data
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Map
name|p
init|=
operator|(
name|Map
operator|)
name|deepCopy
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
name|deepCopy
operator|.
name|put
argument_list|(
name|NAME
argument_list|,
name|p
operator|=
operator|new
name|LinkedHashMap
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|p
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
name|old
init|=
operator|(
name|Map
operator|)
name|p
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|version
init|=
literal|0
decl_stmt|;
name|Map
name|meta
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|meta
operator|=
operator|(
name|Map
operator|)
name|old
operator|.
name|get
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|Long
name|oldVersion
init|=
operator|(
name|Long
operator|)
name|old
operator|.
name|get
argument_list|(
literal|"v"
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldVersion
operator|!=
literal|null
condition|)
name|version
operator|=
name|oldVersion
operator|.
name|longValue
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
name|meta
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|meta
operator|.
name|put
argument_list|(
literal|"v"
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RequestParams
argument_list|(
name|deepCopy
argument_list|,
name|znodeVersion
argument_list|)
return|;
block|}
DECL|method|getFreshRequestParams
specifier|public
specifier|static
name|RequestParams
name|getFreshRequestParams
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|RequestParams
name|requestParams
parameter_list|)
block|{
if|if
condition|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|ZkSolrResourceLoader
name|resourceLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
decl_stmt|;
try|try
block|{
name|Stat
name|stat
init|=
name|resourceLoader
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|exists
argument_list|(
name|resourceLoader
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|RequestParams
operator|.
name|RESOURCE
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"latest version of {} in ZK  is : {}"
argument_list|,
name|resourceLoader
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
operator|+
name|RequestParams
operator|.
name|RESOURCE
argument_list|,
name|stat
operator|==
literal|null
condition|?
literal|""
else|:
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
name|requestParams
operator|=
operator|new
name|RequestParams
argument_list|(
name|Collections
operator|.
name|EMPTY_MAP
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|requestParams
operator|==
literal|null
operator|||
name|stat
operator|.
name|getVersion
argument_list|()
operator|>
name|requestParams
operator|.
name|getZnodeVersion
argument_list|()
condition|)
block|{
name|Object
index|[]
name|o
init|=
name|getMapAndVersion
argument_list|(
name|loader
argument_list|,
name|RequestParams
operator|.
name|RESOURCE
argument_list|)
decl_stmt|;
name|requestParams
operator|=
operator|new
name|RequestParams
argument_list|(
operator|(
name|Map
operator|)
name|o
index|[
literal|0
index|]
argument_list|,
operator|(
name|Integer
operator|)
name|o
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"request params refreshed to version {}"
argument_list|,
name|requestParams
operator|.
name|getZnodeVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
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
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
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
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|Object
index|[]
name|o
init|=
name|getMapAndVersion
argument_list|(
name|loader
argument_list|,
name|RequestParams
operator|.
name|RESOURCE
argument_list|)
decl_stmt|;
name|requestParams
operator|=
operator|new
name|RequestParams
argument_list|(
operator|(
name|Map
operator|)
name|o
index|[
literal|0
index|]
argument_list|,
operator|(
name|Integer
operator|)
name|o
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|requestParams
return|;
block|}
DECL|method|getMapAndVersion
specifier|private
specifier|static
name|Object
index|[]
name|getMapAndVersion
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|loader
operator|.
name|openResource
argument_list|(
name|name
argument_list|)
init|)
block|{
name|int
name|version
init|=
literal|0
decl_stmt|;
comment|//will be always 0 for file based resourceloader
if|if
condition|(
name|in
operator|instanceof
name|ZkSolrResourceLoader
operator|.
name|ZkByteArrayInputStream
condition|)
block|{
name|version
operator|=
operator|(
operator|(
name|ZkSolrResourceLoader
operator|.
name|ZkByteArrayInputStream
operator|)
name|in
operator|)
operator|.
name|getStat
argument_list|()
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"conf resource {} loaded . version : {} "
argument_list|,
name|name
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|m
block|,
name|version
block|}
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
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
literal|"Error parsing conf resource "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//no problem no overlay.json file
return|return
operator|new
name|Object
index|[]
block|{
name|Collections
operator|.
name|EMPTY_MAP
block|,
operator|-
literal|1
block|}
return|;
block|}
block|}
DECL|method|getDeepCopy
specifier|public
specifier|static
name|Map
name|getDeepCopy
parameter_list|(
name|Map
name|map
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
name|Map
name|copy
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|Object
name|v
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|instanceof
name|Map
operator|&&
name|maxDepth
operator|>
literal|0
condition|)
block|{
name|v
operator|=
name|getDeepCopy
argument_list|(
operator|(
name|Map
operator|)
name|v
argument_list|,
name|maxDepth
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|instanceof
name|Set
condition|)
block|{
name|v
operator|=
operator|new
name|HashSet
argument_list|(
operator|(
name|Set
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|instanceof
name|List
condition|)
block|{
name|v
operator|=
operator|new
name|ArrayList
argument_list|(
operator|(
name|List
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
name|copy
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
return|;
block|}
DECL|method|toByteArray
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
return|return
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|data
argument_list|)
return|;
block|}
DECL|field|USEPARAM
specifier|public
specifier|static
specifier|final
name|String
name|USEPARAM
init|=
literal|"useParams"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"params"
decl_stmt|;
DECL|field|RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE
init|=
literal|"params.json"
decl_stmt|;
DECL|class|VersionedParams
specifier|public
specifier|static
class|class
name|VersionedParams
extends|extends
name|MapSolrParams
block|{
DECL|field|meta
name|Map
name|meta
decl_stmt|;
DECL|method|VersionedParams
specifier|public
name|VersionedParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|,
name|Map
name|meta
parameter_list|)
block|{
name|super
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|this
operator|.
name|meta
operator|=
name|meta
expr_stmt|;
block|}
DECL|method|getRawMap
specifier|public
name|Map
name|getRawMap
parameter_list|()
block|{
return|return
name|meta
return|;
block|}
DECL|method|getVersion
specifier|public
name|Long
name|getVersion
parameter_list|()
block|{
return|return
name|meta
operator|==
literal|null
condition|?
literal|0l
else|:
operator|(
name|Long
operator|)
name|meta
operator|.
name|get
argument_list|(
literal|"v"
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
