begin_unit
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|AtomicLong
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import
begin_comment
comment|/**  * A LFU cache implementation based upon ConcurrentHashMap.  *<p/>  * This is not a terribly efficient implementation.  The tricks used in the  * LRU version were not directly usable, perhaps it might be possible to  * rewrite them with LFU in mind.  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.6  */
end_comment
begin_class
DECL|class|ConcurrentLFUCache
specifier|public
class|class
name|ConcurrentLFUCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConcurrentLFUCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|map
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Object
argument_list|,
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|map
decl_stmt|;
DECL|field|upperWaterMark
DECL|field|lowerWaterMark
specifier|private
specifier|final
name|int
name|upperWaterMark
decl_stmt|,
name|lowerWaterMark
decl_stmt|;
DECL|field|markAndSweepLock
specifier|private
specifier|final
name|ReentrantLock
name|markAndSweepLock
init|=
operator|new
name|ReentrantLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|isCleaning
specifier|private
name|boolean
name|isCleaning
init|=
literal|false
decl_stmt|;
comment|// not volatile... piggybacked on other volatile vars
DECL|field|newThreadForCleanup
specifier|private
specifier|final
name|boolean
name|newThreadForCleanup
decl_stmt|;
DECL|field|islive
specifier|private
specifier|volatile
name|boolean
name|islive
init|=
literal|true
decl_stmt|;
DECL|field|stats
specifier|private
specifier|final
name|Stats
name|stats
init|=
operator|new
name|Stats
argument_list|()
decl_stmt|;
DECL|field|acceptableWaterMark
specifier|private
specifier|final
name|int
name|acceptableWaterMark
decl_stmt|;
DECL|field|lowHitCount
specifier|private
name|long
name|lowHitCount
init|=
literal|0
decl_stmt|;
comment|// not volatile, only accessed in the cleaning method
DECL|field|evictionListener
specifier|private
specifier|final
name|EvictionListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|evictionListener
decl_stmt|;
DECL|field|cleanupThread
specifier|private
name|CleanupThread
name|cleanupThread
decl_stmt|;
DECL|field|timeDecay
specifier|private
specifier|final
name|boolean
name|timeDecay
decl_stmt|;
DECL|method|ConcurrentLFUCache
specifier|public
name|ConcurrentLFUCache
parameter_list|(
name|int
name|upperWaterMark
parameter_list|,
specifier|final
name|int
name|lowerWaterMark
parameter_list|,
name|int
name|acceptableSize
parameter_list|,
name|int
name|initialSize
parameter_list|,
name|boolean
name|runCleanupThread
parameter_list|,
name|boolean
name|runNewThreadForCleanup
parameter_list|,
name|EvictionListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|evictionListener
parameter_list|,
name|boolean
name|timeDecay
parameter_list|)
block|{
if|if
condition|(
name|upperWaterMark
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"upperWaterMark must be> 0"
argument_list|)
throw|;
if|if
condition|(
name|lowerWaterMark
operator|>=
name|upperWaterMark
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerWaterMark must be< upperWaterMark"
argument_list|)
throw|;
name|map
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
name|newThreadForCleanup
operator|=
name|runNewThreadForCleanup
expr_stmt|;
name|this
operator|.
name|upperWaterMark
operator|=
name|upperWaterMark
expr_stmt|;
name|this
operator|.
name|lowerWaterMark
operator|=
name|lowerWaterMark
expr_stmt|;
name|this
operator|.
name|acceptableWaterMark
operator|=
name|acceptableSize
expr_stmt|;
name|this
operator|.
name|evictionListener
operator|=
name|evictionListener
expr_stmt|;
name|this
operator|.
name|timeDecay
operator|=
name|timeDecay
expr_stmt|;
if|if
condition|(
name|runCleanupThread
condition|)
block|{
name|cleanupThread
operator|=
operator|new
name|CleanupThread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cleanupThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ConcurrentLFUCache
specifier|public
name|ConcurrentLFUCache
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|lowerWatermark
parameter_list|)
block|{
name|this
argument_list|(
name|size
argument_list|,
name|lowerWatermark
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|lowerWatermark
operator|+
name|size
operator|)
operator|/
literal|2
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
literal|0.75
operator|*
name|size
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setAlive
specifier|public
name|void
name|setAlive
parameter_list|(
name|boolean
name|live
parameter_list|)
block|{
name|islive
operator|=
name|live
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
block|{
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|e
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|islive
condition|)
name|stats
operator|.
name|missCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|islive
condition|)
block|{
name|e
operator|.
name|lastAccessed
operator|=
name|stats
operator|.
name|accessCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|e
operator|.
name|hits
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|e
operator|.
name|value
return|;
block|}
DECL|method|remove
specifier|public
name|V
name|remove
parameter_list|(
name|K
name|key
parameter_list|)
block|{
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cacheEntry
init|=
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheEntry
operator|!=
literal|null
condition|)
block|{
name|stats
operator|.
name|size
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
return|return
name|cacheEntry
operator|.
name|value
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|put
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|e
init|=
operator|new
name|CacheEntry
argument_list|<>
argument_list|(
name|key
argument_list|,
name|val
argument_list|,
name|stats
operator|.
name|accessCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|oldCacheEntry
init|=
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|int
name|currentSize
decl_stmt|;
if|if
condition|(
name|oldCacheEntry
operator|==
literal|null
condition|)
block|{
name|currentSize
operator|=
name|stats
operator|.
name|size
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentSize
operator|=
name|stats
operator|.
name|size
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|islive
condition|)
block|{
name|stats
operator|.
name|putCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stats
operator|.
name|nonLivePutCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|// Check if we need to clear out old entries from the cache.
comment|// isCleaning variable is checked instead of markAndSweepLock.isLocked()
comment|// for performance because every put invokation will check until
comment|// the size is back to an acceptable level.
comment|//
comment|// There is a race between the check and the call to markAndSweep, but
comment|// it's unimportant because markAndSweep actually aquires the lock or returns if it can't.
comment|//
comment|// Thread safety note: isCleaning read is piggybacked (comes after) other volatile reads
comment|// in this method.
if|if
condition|(
name|currentSize
operator|>
name|upperWaterMark
operator|&&
operator|!
name|isCleaning
condition|)
block|{
if|if
condition|(
name|newThreadForCleanup
condition|)
block|{
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|markAndSweep
argument_list|()
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cleanupThread
operator|!=
literal|null
condition|)
block|{
name|cleanupThread
operator|.
name|wakeThread
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|markAndSweep
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|oldCacheEntry
operator|==
literal|null
condition|?
literal|null
else|:
name|oldCacheEntry
operator|.
name|value
return|;
block|}
comment|/**    * Removes items from the cache to bring the size down    * to an acceptable value ('acceptableWaterMark').    *<p/>    * It is done in two stages. In the first stage, least recently used items are evicted.    * If, after the first stage, the cache size is still greater than 'acceptableSize'    * config parameter, the second stage takes over.    *<p/>    * The second stage is more intensive and tries to bring down the cache size    * to the 'lowerWaterMark' config parameter.    */
DECL|method|markAndSweep
specifier|private
name|void
name|markAndSweep
parameter_list|()
block|{
if|if
condition|(
operator|!
name|markAndSweepLock
operator|.
name|tryLock
argument_list|()
condition|)
return|return;
try|try
block|{
name|long
name|lowHitCount
init|=
name|this
operator|.
name|lowHitCount
decl_stmt|;
name|isCleaning
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|lowHitCount
operator|=
name|lowHitCount
expr_stmt|;
comment|// volatile write to make isCleaning visible
name|int
name|sz
init|=
name|stats
operator|.
name|size
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|wantToRemove
init|=
name|sz
operator|-
name|lowerWaterMark
decl_stmt|;
name|TreeSet
argument_list|<
name|CacheEntry
argument_list|>
name|tree
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|ce
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
comment|// set hitsCopy to avoid later Atomic reads
name|ce
operator|.
name|hitsCopy
operator|=
name|ce
operator|.
name|hits
operator|.
name|get
argument_list|()
expr_stmt|;
name|ce
operator|.
name|lastAccessedCopy
operator|=
name|ce
operator|.
name|lastAccessed
expr_stmt|;
if|if
condition|(
name|timeDecay
condition|)
block|{
name|ce
operator|.
name|hits
operator|.
name|set
argument_list|(
name|ce
operator|.
name|hitsCopy
operator|>>>
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tree
operator|.
name|size
argument_list|()
operator|<
name|wantToRemove
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the hits are not equal, we can remove before adding
comment|// which is slightly faster
if|if
condition|(
name|ce
operator|.
name|hitsCopy
operator|<
name|tree
operator|.
name|first
argument_list|()
operator|.
name|hitsCopy
condition|)
block|{
name|tree
operator|.
name|remove
argument_list|(
name|tree
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ce
operator|.
name|hitsCopy
operator|==
name|tree
operator|.
name|first
argument_list|()
operator|.
name|hitsCopy
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
name|tree
operator|.
name|remove
argument_list|(
name|tree
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|e
range|:
name|tree
control|)
block|{
name|evictEntry
argument_list|(
name|e
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|isCleaning
operator|=
literal|false
expr_stmt|;
comment|// set before markAndSweep.unlock() for visibility
name|markAndSweepLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|evictEntry
specifier|private
name|void
name|evictEntry
parameter_list|(
name|K
name|key
parameter_list|)
block|{
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|o
init|=
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return;
name|stats
operator|.
name|size
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|stats
operator|.
name|evictionCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|evictionListener
operator|!=
literal|null
condition|)
name|evictionListener
operator|.
name|evictedEntry
argument_list|(
name|o
operator|.
name|key
argument_list|,
name|o
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns 'n' number of least used entries present in this cache.    *<p/>    * This uses a TreeSet to collect the 'n' least used items ordered by ascending hitcount    * and returns a LinkedHashMap containing 'n' or less than 'n' entries.    *    * @param n the number of items needed    * @return a LinkedHashMap containing 'n' or less than 'n' entries    */
DECL|method|getLeastUsedItems
specifier|public
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getLeastUsedItems
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
return|return
name|result
return|;
name|TreeSet
argument_list|<
name|CacheEntry
argument_list|>
name|tree
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// we need to grab the lock since we are changing the copy variables
name|markAndSweepLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|CacheEntry
name|ce
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ce
operator|.
name|hitsCopy
operator|=
name|ce
operator|.
name|hits
operator|.
name|get
argument_list|()
expr_stmt|;
name|ce
operator|.
name|lastAccessedCopy
operator|=
name|ce
operator|.
name|lastAccessed
expr_stmt|;
if|if
condition|(
name|tree
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the hits are not equal, we can remove before adding
comment|// which is slightly faster
if|if
condition|(
name|ce
operator|.
name|hitsCopy
operator|<
name|tree
operator|.
name|first
argument_list|()
operator|.
name|hitsCopy
condition|)
block|{
name|tree
operator|.
name|remove
argument_list|(
name|tree
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ce
operator|.
name|hitsCopy
operator|==
name|tree
operator|.
name|first
argument_list|()
operator|.
name|hitsCopy
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
name|tree
operator|.
name|remove
argument_list|(
name|tree
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|markAndSweepLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|e
range|:
name|tree
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|e
operator|.
name|key
argument_list|,
name|e
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Returns 'n' number of most used entries present in this cache.    *<p/>    * This uses a TreeSet to collect the 'n' most used items ordered by descending hitcount    * and returns a LinkedHashMap containing 'n' or less than 'n' entries.    *    * @param n the number of items needed    * @return a LinkedHashMap containing 'n' or less than 'n' entries    */
DECL|method|getMostUsedItems
specifier|public
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getMostUsedItems
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
return|return
name|result
return|;
name|TreeSet
argument_list|<
name|CacheEntry
argument_list|>
name|tree
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// we need to grab the lock since we are changing the copy variables
name|markAndSweepLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|ce
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ce
operator|.
name|hitsCopy
operator|=
name|ce
operator|.
name|hits
operator|.
name|get
argument_list|()
expr_stmt|;
name|ce
operator|.
name|lastAccessedCopy
operator|=
name|ce
operator|.
name|lastAccessed
expr_stmt|;
if|if
condition|(
name|tree
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the hits are not equal, we can remove before adding
comment|// which is slightly faster
if|if
condition|(
name|ce
operator|.
name|hitsCopy
operator|>
name|tree
operator|.
name|last
argument_list|()
operator|.
name|hitsCopy
condition|)
block|{
name|tree
operator|.
name|remove
argument_list|(
name|tree
operator|.
name|last
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ce
operator|.
name|hitsCopy
operator|==
name|tree
operator|.
name|last
argument_list|()
operator|.
name|hitsCopy
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|ce
argument_list|)
expr_stmt|;
name|tree
operator|.
name|remove
argument_list|(
name|tree
operator|.
name|last
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|markAndSweepLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|e
range|:
name|tree
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|e
operator|.
name|key
argument_list|,
name|e
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|stats
operator|.
name|size
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getMap
specifier|public
name|Map
argument_list|<
name|Object
argument_list|,
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|map
return|;
block|}
DECL|class|CacheEntry
specifier|private
specifier|static
class|class
name|CacheEntry
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|Comparable
argument_list|<
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
DECL|field|key
name|K
name|key
decl_stmt|;
DECL|field|value
name|V
name|value
decl_stmt|;
DECL|field|hits
specifier|volatile
name|AtomicLong
name|hits
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|hitsCopy
name|long
name|hitsCopy
init|=
literal|0
decl_stmt|;
DECL|field|lastAccessed
specifier|volatile
name|long
name|lastAccessed
init|=
literal|0
decl_stmt|;
DECL|field|lastAccessedCopy
name|long
name|lastAccessedCopy
init|=
literal|0
decl_stmt|;
DECL|method|CacheEntry
specifier|public
name|CacheEntry
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|,
name|long
name|lastAccessed
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|lastAccessed
operator|=
name|lastAccessed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|CacheEntry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|hitsCopy
operator|==
name|that
operator|.
name|hitsCopy
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|lastAccessedCopy
operator|==
name|that
operator|.
name|lastAccessedCopy
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|this
operator|.
name|lastAccessedCopy
operator|<
name|that
operator|.
name|lastAccessedCopy
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
return|return
name|this
operator|.
name|hitsCopy
operator|<
name|that
operator|.
name|hitsCopy
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|value
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
literal|"key: "
operator|+
name|key
operator|+
literal|" value: "
operator|+
name|value
operator|+
literal|" hits:"
operator|+
name|hits
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|field|isDestroyed
specifier|private
name|boolean
name|isDestroyed
init|=
literal|false
decl_stmt|;
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|cleanupThread
operator|!=
literal|null
condition|)
block|{
name|cleanupThread
operator|.
name|stopThread
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|isDestroyed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|getStats
specifier|public
name|Stats
name|getStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
DECL|class|Stats
specifier|public
specifier|static
class|class
name|Stats
block|{
DECL|field|accessCounter
specifier|private
specifier|final
name|AtomicLong
name|accessCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|,
DECL|field|putCounter
name|putCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|,
DECL|field|nonLivePutCounter
name|nonLivePutCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|,
DECL|field|missCounter
name|missCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|AtomicInteger
name|size
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|evictionCounter
specifier|private
name|AtomicLong
name|evictionCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|getCumulativeLookups
specifier|public
name|long
name|getCumulativeLookups
parameter_list|()
block|{
return|return
operator|(
name|accessCounter
operator|.
name|get
argument_list|()
operator|-
name|putCounter
operator|.
name|get
argument_list|()
operator|-
name|nonLivePutCounter
operator|.
name|get
argument_list|()
operator|)
operator|+
name|missCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCumulativeHits
specifier|public
name|long
name|getCumulativeHits
parameter_list|()
block|{
return|return
name|accessCounter
operator|.
name|get
argument_list|()
operator|-
name|putCounter
operator|.
name|get
argument_list|()
operator|-
name|nonLivePutCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCumulativePuts
specifier|public
name|long
name|getCumulativePuts
parameter_list|()
block|{
return|return
name|putCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCumulativeEvictions
specifier|public
name|long
name|getCumulativeEvictions
parameter_list|()
block|{
return|return
name|evictionCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCurrentSize
specifier|public
name|int
name|getCurrentSize
parameter_list|()
block|{
return|return
name|size
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCumulativeNonLivePuts
specifier|public
name|long
name|getCumulativeNonLivePuts
parameter_list|()
block|{
return|return
name|nonLivePutCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCumulativeMisses
specifier|public
name|long
name|getCumulativeMisses
parameter_list|()
block|{
return|return
name|missCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Stats
name|other
parameter_list|)
block|{
name|accessCounter
operator|.
name|addAndGet
argument_list|(
name|other
operator|.
name|accessCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|putCounter
operator|.
name|addAndGet
argument_list|(
name|other
operator|.
name|putCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|nonLivePutCounter
operator|.
name|addAndGet
argument_list|(
name|other
operator|.
name|nonLivePutCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|missCounter
operator|.
name|addAndGet
argument_list|(
name|other
operator|.
name|missCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|evictionCounter
operator|.
name|addAndGet
argument_list|(
name|other
operator|.
name|evictionCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|size
operator|.
name|set
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|size
operator|.
name|get
argument_list|()
argument_list|,
name|other
operator|.
name|size
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|EvictionListener
specifier|public
specifier|static
interface|interface
name|EvictionListener
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|method|evictedEntry
specifier|public
name|void
name|evictedEntry
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
function_decl|;
block|}
DECL|class|CleanupThread
specifier|private
specifier|static
class|class
name|CleanupThread
extends|extends
name|Thread
block|{
DECL|field|cache
specifier|private
name|WeakReference
argument_list|<
name|ConcurrentLFUCache
argument_list|>
name|cache
decl_stmt|;
DECL|field|stop
specifier|private
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
DECL|method|CleanupThread
specifier|public
name|CleanupThread
parameter_list|(
name|ConcurrentLFUCache
name|c
parameter_list|)
block|{
name|cache
operator|=
operator|new
name|WeakReference
argument_list|<>
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|stop
condition|)
break|break;
try|try
block|{
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{           }
block|}
if|if
condition|(
name|stop
condition|)
break|break;
name|ConcurrentLFUCache
name|c
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
break|break;
name|c
operator|.
name|markAndSweep
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|wakeThread
name|void
name|wakeThread
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|stopThread
name|void
name|stopThread
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|stop
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
operator|!
name|isDestroyed
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"ConcurrentLFUCache was not destroyed prior to finalize(), indicates a bug -- POSSIBLE RESOURCE LEAK!!!"
argument_list|)
expr_stmt|;
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
