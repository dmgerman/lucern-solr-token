begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|ConcurrentLFUCache
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|TimeUnit
import|;
end_import
begin_comment
comment|/**  * SolrCache based on ConcurrentLFUCache implementation.  *<p>  * This implementation does not use a separate cleanup thread. Instead it uses the calling thread  * itself to do the cleanup when the size of the cache exceeds certain limits.  *<p>  * Also see<a href="http://wiki.apache.org/solr/SolrCaching">SolrCaching</a>  *<p>  *<b>This API is experimental and subject to change</b>  *  * @see org.apache.solr.util.ConcurrentLFUCache  * @see org.apache.solr.search.SolrCache  * @since solr 3.6  */
end_comment
begin_class
DECL|class|LFUCache
specifier|public
class|class
name|LFUCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|SolrCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
comment|// contains the statistics objects for all open caches of the same type
DECL|field|statsList
specifier|private
name|List
argument_list|<
name|ConcurrentLFUCache
operator|.
name|Stats
argument_list|>
name|statsList
decl_stmt|;
DECL|field|warmupTime
specifier|private
name|long
name|warmupTime
init|=
literal|0
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|autowarmCount
specifier|private
name|int
name|autowarmCount
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|regenerator
specifier|private
name|CacheRegenerator
name|regenerator
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
init|=
literal|"Concurrent LFU Cache"
decl_stmt|;
DECL|field|cache
specifier|private
name|ConcurrentLFUCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache
decl_stmt|;
DECL|field|showItems
specifier|private
name|int
name|showItems
init|=
literal|0
decl_stmt|;
DECL|field|timeDecay
specifier|private
name|Boolean
name|timeDecay
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|Object
name|init
parameter_list|(
name|Map
name|args
parameter_list|,
name|Object
name|persistence
parameter_list|,
name|CacheRegenerator
name|regenerator
parameter_list|)
block|{
name|state
operator|=
name|State
operator|.
name|CREATED
expr_stmt|;
name|this
operator|.
name|regenerator
operator|=
name|regenerator
expr_stmt|;
name|name
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
name|int
name|limit
init|=
name|str
operator|==
literal|null
condition|?
literal|1024
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|int
name|minLimit
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"minSize"
argument_list|)
expr_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
name|minLimit
operator|=
call|(
name|int
call|)
argument_list|(
name|limit
operator|*
literal|0.9
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|minLimit
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minLimit
operator|==
literal|0
condition|)
name|minLimit
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|limit
operator|<=
name|minLimit
condition|)
name|limit
operator|=
name|minLimit
operator|+
literal|1
expr_stmt|;
name|int
name|acceptableSize
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"acceptableSize"
argument_list|)
expr_stmt|;
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
name|acceptableSize
operator|=
call|(
name|int
call|)
argument_list|(
name|limit
operator|*
literal|0.95
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|acceptableSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
comment|// acceptable limit should be somewhere between minLimit and limit
name|acceptableSize
operator|=
name|Math
operator|.
name|max
argument_list|(
name|minLimit
argument_list|,
name|acceptableSize
argument_list|)
expr_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"initialSize"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|initialSize
init|=
name|str
operator|==
literal|null
condition|?
name|limit
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"autowarmCount"
argument_list|)
expr_stmt|;
name|autowarmCount
operator|=
name|str
operator|==
literal|null
condition|?
literal|0
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"cleanupThread"
argument_list|)
expr_stmt|;
name|boolean
name|newThread
init|=
name|str
operator|==
literal|null
condition|?
literal|false
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"showItems"
argument_list|)
expr_stmt|;
name|showItems
operator|=
name|str
operator|==
literal|null
condition|?
literal|0
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
expr_stmt|;
comment|// Don't make this "efficient" by removing the test, default is true and omitting the param will make it false.
name|str
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"timeDecay"
argument_list|)
expr_stmt|;
name|timeDecay
operator|=
operator|(
name|str
operator|==
literal|null
operator|)
condition|?
literal|true
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|description
operator|=
literal|"Concurrent LFU Cache(maxSize="
operator|+
name|limit
operator|+
literal|", initialSize="
operator|+
name|initialSize
operator|+
literal|", minSize="
operator|+
name|minLimit
operator|+
literal|", acceptableSize="
operator|+
name|acceptableSize
operator|+
literal|", cleanupThread="
operator|+
name|newThread
operator|+
literal|", timeDecay="
operator|+
name|Boolean
operator|.
name|toString
argument_list|(
name|timeDecay
argument_list|)
expr_stmt|;
if|if
condition|(
name|autowarmCount
operator|>
literal|0
condition|)
block|{
name|description
operator|+=
literal|", autowarmCount="
operator|+
name|autowarmCount
operator|+
literal|", regenerator="
operator|+
name|regenerator
expr_stmt|;
block|}
name|description
operator|+=
literal|')'
expr_stmt|;
name|cache
operator|=
operator|new
name|ConcurrentLFUCache
argument_list|<>
argument_list|(
name|limit
argument_list|,
name|minLimit
argument_list|,
name|acceptableSize
argument_list|,
name|initialSize
argument_list|,
name|newThread
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|timeDecay
argument_list|)
expr_stmt|;
name|cache
operator|.
name|setAlive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|statsList
operator|=
operator|(
name|List
argument_list|<
name|ConcurrentLFUCache
operator|.
name|Stats
argument_list|>
operator|)
name|persistence
expr_stmt|;
if|if
condition|(
name|statsList
operator|==
literal|null
condition|)
block|{
comment|// must be the first time a cache of this type is being created
comment|// Use a CopyOnWriteArrayList since puts are very rare and iteration may be a frequent operation
comment|// because it is used in getStatistics()
name|statsList
operator|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
expr_stmt|;
comment|// the first entry will be for cumulative stats of caches that have been closed.
name|statsList
operator|.
name|add
argument_list|(
operator|new
name|ConcurrentLFUCache
operator|.
name|Stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|statsList
operator|.
name|add
argument_list|(
name|cache
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|statsList
return|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
return|return
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setState
specifier|public
name|void
name|setState
parameter_list|(
name|State
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|cache
operator|.
name|setAlive
argument_list|(
name|state
operator|==
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|warm
specifier|public
name|void
name|warm
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|SolrCache
name|old
parameter_list|)
block|{
if|if
condition|(
name|regenerator
operator|==
literal|null
condition|)
return|return;
name|long
name|warmingStartTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|LFUCache
name|other
init|=
operator|(
name|LFUCache
operator|)
name|old
decl_stmt|;
comment|// warm entries
if|if
condition|(
name|autowarmCount
operator|!=
literal|0
condition|)
block|{
name|int
name|sz
init|=
name|other
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|autowarmCount
operator|!=
operator|-
literal|1
condition|)
name|sz
operator|=
name|Math
operator|.
name|min
argument_list|(
name|sz
argument_list|,
name|autowarmCount
argument_list|)
expr_stmt|;
name|Map
name|items
init|=
name|other
operator|.
name|cache
operator|.
name|getMostUsedItems
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
index|[]
name|itemsArr
init|=
operator|new
name|Map
operator|.
name|Entry
index|[
name|items
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|mapEntry
range|:
name|items
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|itemsArr
index|[
name|counter
operator|++
index|]
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|mapEntry
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|itemsArr
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
try|try
block|{
name|boolean
name|continueRegen
init|=
name|regenerator
operator|.
name|regenerateItem
argument_list|(
name|searcher
argument_list|,
name|this
argument_list|,
name|old
argument_list|,
name|itemsArr
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
argument_list|,
name|itemsArr
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|continueRegen
condition|)
break|break;
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
name|log
argument_list|,
literal|"Error during auto-warming of key:"
operator|+
name|itemsArr
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|warmupTime
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|warmingStartTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// add the stats to the cumulative stats object (the first in the statsList)
name|statsList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|add
argument_list|(
name|cache
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
name|statsList
operator|.
name|remove
argument_list|(
name|cache
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|LFUCache
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|CACHE
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// returns a ratio, not a percent.
DECL|method|calcHitRatio
specifier|private
specifier|static
name|String
name|calcHitRatio
parameter_list|(
name|long
name|lookups
parameter_list|,
name|long
name|hits
parameter_list|)
block|{
if|if
condition|(
name|lookups
operator|==
literal|0
condition|)
return|return
literal|"0.00"
return|;
if|if
condition|(
name|lookups
operator|==
name|hits
condition|)
return|return
literal|"1.00"
return|;
name|int
name|hundredths
init|=
call|(
name|int
call|)
argument_list|(
name|hits
operator|*
literal|100
operator|/
name|lookups
argument_list|)
decl_stmt|;
comment|// rounded down
if|if
condition|(
name|hundredths
operator|<
literal|10
condition|)
return|return
literal|"0.0"
operator|+
name|hundredths
return|;
return|return
literal|"0."
operator|+
name|hundredths
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Serializable
argument_list|>
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
return|return
name|lst
return|;
name|ConcurrentLFUCache
operator|.
name|Stats
name|stats
init|=
name|cache
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|long
name|lookups
init|=
name|stats
operator|.
name|getCumulativeLookups
argument_list|()
decl_stmt|;
name|long
name|hits
init|=
name|stats
operator|.
name|getCumulativeHits
argument_list|()
decl_stmt|;
name|long
name|inserts
init|=
name|stats
operator|.
name|getCumulativePuts
argument_list|()
decl_stmt|;
name|long
name|evictions
init|=
name|stats
operator|.
name|getCumulativeEvictions
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|stats
operator|.
name|getCurrentSize
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"lookups"
argument_list|,
name|lookups
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"hits"
argument_list|,
name|hits
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"hitratio"
argument_list|,
name|calcHitRatio
argument_list|(
name|lookups
argument_list|,
name|hits
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"inserts"
argument_list|,
name|inserts
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"evictions"
argument_list|,
name|evictions
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"size"
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"warmupTime"
argument_list|,
name|warmupTime
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"timeDecay"
argument_list|,
name|timeDecay
argument_list|)
expr_stmt|;
name|long
name|clookups
init|=
literal|0
decl_stmt|;
name|long
name|chits
init|=
literal|0
decl_stmt|;
name|long
name|cinserts
init|=
literal|0
decl_stmt|;
name|long
name|cevictions
init|=
literal|0
decl_stmt|;
comment|// NOTE: It is safe to iterate on a CopyOnWriteArrayList
for|for
control|(
name|ConcurrentLFUCache
operator|.
name|Stats
name|statistiscs
range|:
name|statsList
control|)
block|{
name|clookups
operator|+=
name|statistiscs
operator|.
name|getCumulativeLookups
argument_list|()
expr_stmt|;
name|chits
operator|+=
name|statistiscs
operator|.
name|getCumulativeHits
argument_list|()
expr_stmt|;
name|cinserts
operator|+=
name|statistiscs
operator|.
name|getCumulativePuts
argument_list|()
expr_stmt|;
name|cevictions
operator|+=
name|statistiscs
operator|.
name|getCumulativeEvictions
argument_list|()
expr_stmt|;
block|}
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_lookups"
argument_list|,
name|clookups
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_hits"
argument_list|,
name|chits
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_hitratio"
argument_list|,
name|calcHitRatio
argument_list|(
name|clookups
argument_list|,
name|chits
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_inserts"
argument_list|,
name|cinserts
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_evictions"
argument_list|,
name|cevictions
argument_list|)
expr_stmt|;
if|if
condition|(
name|showItems
operator|!=
literal|0
condition|)
block|{
name|Map
name|items
init|=
name|cache
operator|.
name|getMostUsedItems
argument_list|(
name|showItems
operator|==
operator|-
literal|1
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|showItems
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|e
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|items
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|k
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|v
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|ks
init|=
literal|"item_"
operator|+
name|k
decl_stmt|;
name|String
name|vs
init|=
name|v
operator|.
name|toString
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|ks
argument_list|,
name|vs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|lst
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
name|getStatistics
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
