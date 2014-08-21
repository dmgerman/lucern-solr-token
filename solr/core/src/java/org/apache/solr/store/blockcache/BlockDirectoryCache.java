begin_unit
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BlockDirectoryCache
specifier|public
class|class
name|BlockDirectoryCache
implements|implements
name|Cache
block|{
DECL|field|blockCache
specifier|private
specifier|final
name|BlockCache
name|blockCache
decl_stmt|;
DECL|field|counter
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|names
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|names
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|keys
specifier|private
name|Set
argument_list|<
name|BlockCacheKey
argument_list|>
name|keys
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|Metrics
name|metrics
decl_stmt|;
DECL|method|BlockDirectoryCache
specifier|public
name|BlockDirectoryCache
parameter_list|(
name|BlockCache
name|blockCache
parameter_list|,
name|String
name|path
parameter_list|,
name|Metrics
name|metrics
parameter_list|)
block|{
name|this
argument_list|(
name|blockCache
argument_list|,
name|path
argument_list|,
name|metrics
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockDirectoryCache
specifier|public
name|BlockDirectoryCache
parameter_list|(
name|BlockCache
name|blockCache
parameter_list|,
name|String
name|path
parameter_list|,
name|Metrics
name|metrics
parameter_list|,
name|boolean
name|releaseBlocks
parameter_list|)
block|{
name|this
operator|.
name|blockCache
operator|=
name|blockCache
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
if|if
condition|(
name|releaseBlocks
condition|)
block|{
name|keys
operator|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|BlockCacheKey
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert: mostly for tests    *     * @lucene.experimental    */
DECL|method|getBlockCache
specifier|public
name|BlockCache
name|getBlockCache
parameter_list|()
block|{
return|return
name|blockCache
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|names
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|blockId
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|Integer
name|file
init|=
name|names
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
name|file
operator|=
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|names
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
name|BlockCacheKey
name|blockCacheKey
init|=
operator|new
name|BlockCacheKey
argument_list|()
decl_stmt|;
name|blockCacheKey
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setBlock
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|blockCache
operator|.
name|store
argument_list|(
name|blockCacheKey
argument_list|,
name|blockOffset
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|keys
operator|!=
literal|null
condition|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|blockCacheKey
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|fetch
specifier|public
name|boolean
name|fetch
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|blockId
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|lengthToReadInBlock
parameter_list|)
block|{
name|Integer
name|file
init|=
name|names
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BlockCacheKey
name|blockCacheKey
init|=
operator|new
name|BlockCacheKey
argument_list|()
decl_stmt|;
name|blockCacheKey
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setBlock
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|blockCacheKey
operator|.
name|setFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|boolean
name|fetch
init|=
name|blockCache
operator|.
name|fetch
argument_list|(
name|blockCacheKey
argument_list|,
name|b
argument_list|,
name|blockOffset
argument_list|,
name|off
argument_list|,
name|lengthToReadInBlock
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetch
condition|)
block|{
name|metrics
operator|.
name|blockCacheHit
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|metrics
operator|.
name|blockCacheMiss
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|fetch
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|blockCache
operator|.
name|getSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|renameCacheFile
specifier|public
name|void
name|renameCacheFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
block|{
name|Integer
name|file
init|=
name|names
operator|.
name|remove
argument_list|(
name|source
argument_list|)
decl_stmt|;
comment|// possible if the file is empty
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|names
operator|.
name|put
argument_list|(
name|dest
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|releaseResources
specifier|public
name|void
name|releaseResources
parameter_list|()
block|{
if|if
condition|(
name|keys
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|BlockCacheKey
name|key
range|:
name|keys
control|)
block|{
name|blockCache
operator|.
name|release
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
