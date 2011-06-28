begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Copyright 2009 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collection
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
name|HashSet
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
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
operator|.
name|CacheEntry
import|;
end_import
begin_comment
comment|/**   * Provides methods for sanity checking that entries in the FieldCache   * are not wasteful or inconsistent.  *</p>  *<p>  * Lucene 2.9 Introduced numerous enhancements into how the FieldCache   * is used by the low levels of Lucene searching (for Sorting and   * ValueSourceQueries) to improve both the speed for Sorting, as well   * as reopening of IndexReaders.  But these changes have shifted the   * usage of FieldCache from "top level" IndexReaders (frequently a   * MultiReader or DirectoryReader) down to the leaf level SegmentReaders.    * As a result, existing applications that directly access the FieldCache   * may find RAM usage increase significantly when upgrading to 2.9 or   * Later.  This class provides an API for these applications (or their   * Unit tests) to check at run time if the FieldCache contains "insane"   * usages of the FieldCache.  *</p>  * @lucene.experimental  * @see FieldCache  * @see FieldCacheSanityChecker.Insanity  * @see FieldCacheSanityChecker.InsanityType  */
end_comment
begin_class
DECL|class|FieldCacheSanityChecker
specifier|public
specifier|final
class|class
name|FieldCacheSanityChecker
block|{
DECL|field|ramCalc
specifier|private
name|RamUsageEstimator
name|ramCalc
init|=
literal|null
decl_stmt|;
DECL|method|FieldCacheSanityChecker
specifier|public
name|FieldCacheSanityChecker
parameter_list|()
block|{
comment|/* NOOP */
block|}
comment|/**    * If set, will be used to estimate size for all CacheEntry objects     * dealt with.    */
DECL|method|setRamUsageEstimator
specifier|public
name|void
name|setRamUsageEstimator
parameter_list|(
name|RamUsageEstimator
name|r
parameter_list|)
block|{
name|ramCalc
operator|=
name|r
expr_stmt|;
block|}
comment|/**     * Quick and dirty convenience method    * @see #check    */
DECL|method|checkSanity
specifier|public
specifier|static
name|Insanity
index|[]
name|checkSanity
parameter_list|(
name|FieldCache
name|cache
parameter_list|)
block|{
return|return
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
return|;
block|}
comment|/**     * Quick and dirty convenience method that instantiates an instance with     * "good defaults" and uses it to test the CacheEntrys    * @see #check    */
DECL|method|checkSanity
specifier|public
specifier|static
name|Insanity
index|[]
name|checkSanity
parameter_list|(
name|CacheEntry
modifier|...
name|cacheEntries
parameter_list|)
block|{
name|FieldCacheSanityChecker
name|sanityChecker
init|=
operator|new
name|FieldCacheSanityChecker
argument_list|()
decl_stmt|;
comment|// doesn't check for interned
name|sanityChecker
operator|.
name|setRamUsageEstimator
argument_list|(
operator|new
name|RamUsageEstimator
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sanityChecker
operator|.
name|check
argument_list|(
name|cacheEntries
argument_list|)
return|;
block|}
comment|/**    * Tests a CacheEntry[] for indication of "insane" cache usage.    *<p>    *<B>NOTE:</b>FieldCache CreationPlaceholder objects are ignored.    * (:TODO: is this a bad idea? are we masking a real problem?)    *</p>    */
DECL|method|check
specifier|public
name|Insanity
index|[]
name|check
parameter_list|(
name|CacheEntry
modifier|...
name|cacheEntries
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|cacheEntries
operator|||
literal|0
operator|==
name|cacheEntries
operator|.
name|length
condition|)
return|return
operator|new
name|Insanity
index|[
literal|0
index|]
return|;
if|if
condition|(
literal|null
operator|!=
name|ramCalc
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cacheEntries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cacheEntries
index|[
name|i
index|]
operator|.
name|estimateSize
argument_list|(
name|ramCalc
argument_list|)
expr_stmt|;
block|}
block|}
comment|// the indirect mapping lets MapOfSet dedup identical valIds for us
comment|//
comment|// maps the (valId) identityhashCode of cache values to
comment|// sets of CacheEntry instances
specifier|final
name|MapOfSets
argument_list|<
name|Integer
argument_list|,
name|CacheEntry
argument_list|>
name|valIdToItems
init|=
operator|new
name|MapOfSets
argument_list|<
name|Integer
argument_list|,
name|CacheEntry
argument_list|>
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|CacheEntry
argument_list|>
argument_list|>
argument_list|(
literal|17
argument_list|)
argument_list|)
decl_stmt|;
comment|// maps ReaderField keys to Sets of ValueIds
specifier|final
name|MapOfSets
argument_list|<
name|ReaderField
argument_list|,
name|Integer
argument_list|>
name|readerFieldToValIds
init|=
operator|new
name|MapOfSets
argument_list|<
name|ReaderField
argument_list|,
name|Integer
argument_list|>
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ReaderField
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|(
literal|17
argument_list|)
argument_list|)
decl_stmt|;
comment|//
comment|// any keys that we know result in more then one valId
specifier|final
name|Set
argument_list|<
name|ReaderField
argument_list|>
name|valMismatchKeys
init|=
operator|new
name|HashSet
argument_list|<
name|ReaderField
argument_list|>
argument_list|()
decl_stmt|;
comment|// iterate over all the cacheEntries to get the mappings we'll need
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cacheEntries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CacheEntry
name|item
init|=
name|cacheEntries
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Object
name|val
init|=
name|item
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|FieldCache
operator|.
name|CreationPlaceholder
condition|)
continue|continue;
specifier|final
name|ReaderField
name|rf
init|=
operator|new
name|ReaderField
argument_list|(
name|item
operator|.
name|getReaderKey
argument_list|()
argument_list|,
name|item
operator|.
name|getFieldName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|valId
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|identityHashCode
argument_list|(
name|val
argument_list|)
argument_list|)
decl_stmt|;
comment|// indirect mapping, so the MapOfSet will dedup identical valIds for us
name|valIdToItems
operator|.
name|put
argument_list|(
name|valId
argument_list|,
name|item
argument_list|)
expr_stmt|;
if|if
condition|(
literal|1
operator|<
name|readerFieldToValIds
operator|.
name|put
argument_list|(
name|rf
argument_list|,
name|valId
argument_list|)
condition|)
block|{
name|valMismatchKeys
operator|.
name|add
argument_list|(
name|rf
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|Insanity
argument_list|>
name|insanity
init|=
operator|new
name|ArrayList
argument_list|<
name|Insanity
argument_list|>
argument_list|(
name|valMismatchKeys
operator|.
name|size
argument_list|()
operator|*
literal|3
argument_list|)
decl_stmt|;
name|insanity
operator|.
name|addAll
argument_list|(
name|checkValueMismatch
argument_list|(
name|valIdToItems
argument_list|,
name|readerFieldToValIds
argument_list|,
name|valMismatchKeys
argument_list|)
argument_list|)
expr_stmt|;
name|insanity
operator|.
name|addAll
argument_list|(
name|checkSubreaders
argument_list|(
name|valIdToItems
argument_list|,
name|readerFieldToValIds
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|insanity
operator|.
name|toArray
argument_list|(
operator|new
name|Insanity
index|[
name|insanity
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**     * Internal helper method used by check that iterates over     * valMismatchKeys and generates a Collection of Insanity     * instances accordingly.  The MapOfSets are used to populate     * the Insanity objects.     * @see InsanityType#VALUEMISMATCH    */
DECL|method|checkValueMismatch
specifier|private
name|Collection
argument_list|<
name|Insanity
argument_list|>
name|checkValueMismatch
parameter_list|(
name|MapOfSets
argument_list|<
name|Integer
argument_list|,
name|CacheEntry
argument_list|>
name|valIdToItems
parameter_list|,
name|MapOfSets
argument_list|<
name|ReaderField
argument_list|,
name|Integer
argument_list|>
name|readerFieldToValIds
parameter_list|,
name|Set
argument_list|<
name|ReaderField
argument_list|>
name|valMismatchKeys
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Insanity
argument_list|>
name|insanity
init|=
operator|new
name|ArrayList
argument_list|<
name|Insanity
argument_list|>
argument_list|(
name|valMismatchKeys
operator|.
name|size
argument_list|()
operator|*
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|valMismatchKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we have multiple values for some ReaderFields
specifier|final
name|Map
argument_list|<
name|ReaderField
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|rfMap
init|=
name|readerFieldToValIds
operator|.
name|getMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|CacheEntry
argument_list|>
argument_list|>
name|valMap
init|=
name|valIdToItems
operator|.
name|getMap
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ReaderField
name|rf
range|:
name|valMismatchKeys
control|)
block|{
specifier|final
name|List
argument_list|<
name|CacheEntry
argument_list|>
name|badEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|CacheEntry
argument_list|>
argument_list|(
name|valMismatchKeys
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Integer
name|value
range|:
name|rfMap
operator|.
name|get
argument_list|(
name|rf
argument_list|)
control|)
block|{
for|for
control|(
specifier|final
name|CacheEntry
name|cacheEntry
range|:
name|valMap
operator|.
name|get
argument_list|(
name|value
argument_list|)
control|)
block|{
name|badEntries
operator|.
name|add
argument_list|(
name|cacheEntry
argument_list|)
expr_stmt|;
block|}
block|}
name|CacheEntry
index|[]
name|badness
init|=
operator|new
name|CacheEntry
index|[
name|badEntries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|badness
operator|=
name|badEntries
operator|.
name|toArray
argument_list|(
name|badness
argument_list|)
expr_stmt|;
name|insanity
operator|.
name|add
argument_list|(
operator|new
name|Insanity
argument_list|(
name|InsanityType
operator|.
name|VALUEMISMATCH
argument_list|,
literal|"Multiple distinct value objects for "
operator|+
name|rf
operator|.
name|toString
argument_list|()
argument_list|,
name|badness
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|insanity
return|;
block|}
comment|/**     * Internal helper method used by check that iterates over     * the keys of readerFieldToValIds and generates a Collection     * of Insanity instances whenever two (or more) ReaderField instances are     * found that have an ancestry relationships.      *    * @see InsanityType#SUBREADER    */
DECL|method|checkSubreaders
specifier|private
name|Collection
argument_list|<
name|Insanity
argument_list|>
name|checkSubreaders
parameter_list|(
name|MapOfSets
argument_list|<
name|Integer
argument_list|,
name|CacheEntry
argument_list|>
name|valIdToItems
parameter_list|,
name|MapOfSets
argument_list|<
name|ReaderField
argument_list|,
name|Integer
argument_list|>
name|readerFieldToValIds
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Insanity
argument_list|>
name|insanity
init|=
operator|new
name|ArrayList
argument_list|<
name|Insanity
argument_list|>
argument_list|(
literal|23
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReaderField
argument_list|,
name|Set
argument_list|<
name|ReaderField
argument_list|>
argument_list|>
name|badChildren
init|=
operator|new
name|HashMap
argument_list|<
name|ReaderField
argument_list|,
name|Set
argument_list|<
name|ReaderField
argument_list|>
argument_list|>
argument_list|(
literal|17
argument_list|)
decl_stmt|;
name|MapOfSets
argument_list|<
name|ReaderField
argument_list|,
name|ReaderField
argument_list|>
name|badKids
init|=
operator|new
name|MapOfSets
argument_list|<
name|ReaderField
argument_list|,
name|ReaderField
argument_list|>
argument_list|(
name|badChildren
argument_list|)
decl_stmt|;
comment|// wrapper
name|Map
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|CacheEntry
argument_list|>
argument_list|>
name|viToItemSets
init|=
name|valIdToItems
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|ReaderField
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|rfToValIdSets
init|=
name|readerFieldToValIds
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ReaderField
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|ReaderField
argument_list|>
argument_list|(
literal|17
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ReaderField
argument_list|>
name|readerFields
init|=
name|rfToValIdSets
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ReaderField
name|rf
range|:
name|readerFields
control|)
block|{
if|if
condition|(
name|seen
operator|.
name|contains
argument_list|(
name|rf
argument_list|)
condition|)
continue|continue;
name|List
argument_list|<
name|Object
argument_list|>
name|kids
init|=
name|getAllDescendentReaderKeys
argument_list|(
name|rf
operator|.
name|readerKey
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|kidKey
range|:
name|kids
control|)
block|{
name|ReaderField
name|kid
init|=
operator|new
name|ReaderField
argument_list|(
name|kidKey
argument_list|,
name|rf
operator|.
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|badChildren
operator|.
name|containsKey
argument_list|(
name|kid
argument_list|)
condition|)
block|{
comment|// we've already process this kid as RF and found other problems
comment|// track those problems as our own
name|badKids
operator|.
name|put
argument_list|(
name|rf
argument_list|,
name|kid
argument_list|)
expr_stmt|;
name|badKids
operator|.
name|putAll
argument_list|(
name|rf
argument_list|,
name|badChildren
operator|.
name|get
argument_list|(
name|kid
argument_list|)
argument_list|)
expr_stmt|;
name|badChildren
operator|.
name|remove
argument_list|(
name|kid
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rfToValIdSets
operator|.
name|containsKey
argument_list|(
name|kid
argument_list|)
condition|)
block|{
comment|// we have cache entries for the kid
name|badKids
operator|.
name|put
argument_list|(
name|rf
argument_list|,
name|kid
argument_list|)
expr_stmt|;
block|}
name|seen
operator|.
name|add
argument_list|(
name|kid
argument_list|)
expr_stmt|;
block|}
name|seen
operator|.
name|add
argument_list|(
name|rf
argument_list|)
expr_stmt|;
block|}
comment|// every mapping in badKids represents an Insanity
for|for
control|(
specifier|final
name|ReaderField
name|parent
range|:
name|badChildren
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|ReaderField
argument_list|>
name|kids
init|=
name|badChildren
operator|.
name|get
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CacheEntry
argument_list|>
name|badEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|CacheEntry
argument_list|>
argument_list|(
name|kids
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
comment|// put parent entr(ies) in first
block|{
for|for
control|(
specifier|final
name|Integer
name|value
range|:
name|rfToValIdSets
operator|.
name|get
argument_list|(
name|parent
argument_list|)
control|)
block|{
name|badEntries
operator|.
name|addAll
argument_list|(
name|viToItemSets
operator|.
name|get
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now the entries for the descendants
for|for
control|(
specifier|final
name|ReaderField
name|kid
range|:
name|kids
control|)
block|{
for|for
control|(
specifier|final
name|Integer
name|value
range|:
name|rfToValIdSets
operator|.
name|get
argument_list|(
name|kid
argument_list|)
control|)
block|{
name|badEntries
operator|.
name|addAll
argument_list|(
name|viToItemSets
operator|.
name|get
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|CacheEntry
index|[]
name|badness
init|=
operator|new
name|CacheEntry
index|[
name|badEntries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|badness
operator|=
name|badEntries
operator|.
name|toArray
argument_list|(
name|badness
argument_list|)
expr_stmt|;
name|insanity
operator|.
name|add
argument_list|(
operator|new
name|Insanity
argument_list|(
name|InsanityType
operator|.
name|SUBREADER
argument_list|,
literal|"Found caches for descendants of "
operator|+
name|parent
operator|.
name|toString
argument_list|()
argument_list|,
name|badness
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|insanity
return|;
block|}
comment|/**    * Checks if the seed is an IndexReader, and if so will walk    * the hierarchy of subReaders building up a list of the objects     * returned by obj.getFieldCacheKey()    */
DECL|method|getAllDescendentReaderKeys
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|getAllDescendentReaderKeys
parameter_list|(
name|Object
name|seed
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|17
argument_list|)
decl_stmt|;
comment|// will grow as we iter
name|all
operator|.
name|add
argument_list|(
name|seed
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|all
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|obj
init|=
name|all
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|IndexReader
condition|)
block|{
name|IndexReader
index|[]
name|subs
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|obj
operator|)
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
operator|(
literal|null
operator|!=
name|subs
operator|)
operator|&&
operator|(
name|j
operator|<
name|subs
operator|.
name|length
operator|)
condition|;
name|j
operator|++
control|)
block|{
name|all
operator|.
name|add
argument_list|(
name|subs
index|[
name|j
index|]
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// need to skip the first, because it was the seed
return|return
name|all
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|all
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Simple pair object for using "readerKey + fieldName" a Map key    */
DECL|class|ReaderField
specifier|private
specifier|final
specifier|static
class|class
name|ReaderField
block|{
DECL|field|readerKey
specifier|public
specifier|final
name|Object
name|readerKey
decl_stmt|;
DECL|field|fieldName
specifier|public
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|method|ReaderField
specifier|public
name|ReaderField
parameter_list|(
name|Object
name|readerKey
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|readerKey
operator|=
name|readerKey
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
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
name|System
operator|.
name|identityHashCode
argument_list|(
name|readerKey
argument_list|)
operator|*
name|fieldName
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
name|that
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|that
operator|instanceof
name|ReaderField
operator|)
condition|)
return|return
literal|false
return|;
name|ReaderField
name|other
init|=
operator|(
name|ReaderField
operator|)
name|that
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|readerKey
operator|==
name|other
operator|.
name|readerKey
operator|&&
name|this
operator|.
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
argument_list|)
operator|)
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
name|readerKey
operator|.
name|toString
argument_list|()
operator|+
literal|"+"
operator|+
name|fieldName
return|;
block|}
block|}
comment|/**    * Simple container for a collection of related CacheEntry objects that     * in conjunction with each other represent some "insane" usage of the     * FieldCache.    */
DECL|class|Insanity
specifier|public
specifier|final
specifier|static
class|class
name|Insanity
block|{
DECL|field|type
specifier|private
specifier|final
name|InsanityType
name|type
decl_stmt|;
DECL|field|msg
specifier|private
specifier|final
name|String
name|msg
decl_stmt|;
DECL|field|entries
specifier|private
specifier|final
name|CacheEntry
index|[]
name|entries
decl_stmt|;
DECL|method|Insanity
specifier|public
name|Insanity
parameter_list|(
name|InsanityType
name|type
parameter_list|,
name|String
name|msg
parameter_list|,
name|CacheEntry
modifier|...
name|entries
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|type
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Insanity requires non-null InsanityType"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|entries
operator|||
literal|0
operator|==
name|entries
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Insanity requires non-null/non-empty CacheEntry[]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|msg
operator|=
name|msg
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
block|}
comment|/**      * Type of insane behavior this object represents      */
DECL|method|getType
specifier|public
name|InsanityType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Description of hte insane behavior      */
DECL|method|getMsg
specifier|public
name|String
name|getMsg
parameter_list|()
block|{
return|return
name|msg
return|;
block|}
comment|/**      * CacheEntry objects which suggest a problem      */
DECL|method|getCacheEntries
specifier|public
name|CacheEntry
index|[]
name|getCacheEntries
parameter_list|()
block|{
return|return
name|entries
return|;
block|}
comment|/**      * Multi-Line representation of this Insanity object, starting with       * the Type and Msg, followed by each CacheEntry.toString() on it's       * own line prefaced by a tab character      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|String
name|m
init|=
name|getMsg
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|m
condition|)
name|buf
operator|.
name|append
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|CacheEntry
index|[]
name|ce
init|=
name|getCacheEntries
argument_list|()
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
name|ce
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|ce
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * An Enumeration of the different types of "insane" behavior that     * may be detected in a FieldCache.    *    * @see InsanityType#SUBREADER    * @see InsanityType#VALUEMISMATCH    * @see InsanityType#EXPECTED    */
DECL|class|InsanityType
specifier|public
specifier|final
specifier|static
class|class
name|InsanityType
block|{
DECL|field|label
specifier|private
specifier|final
name|String
name|label
decl_stmt|;
DECL|method|InsanityType
specifier|private
name|InsanityType
parameter_list|(
specifier|final
name|String
name|label
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
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
name|label
return|;
block|}
comment|/**       * Indicates an overlap in cache usage on a given field       * in sub/super readers.      */
DECL|field|SUBREADER
specifier|public
specifier|final
specifier|static
name|InsanityType
name|SUBREADER
init|=
operator|new
name|InsanityType
argument_list|(
literal|"SUBREADER"
argument_list|)
decl_stmt|;
comment|/**       *<p>      * Indicates entries have the same reader+fieldname but       * different cached values.  This can happen if different datatypes,       * or parsers are used -- and while it's not necessarily a bug       * it's typically an indication of a possible problem.      *</p>      *<p>      *<bPNOTE:</b> Only the reader, fieldname, and cached value are actually       * tested -- if two cache entries have different parsers or datatypes but       * the cached values are the same Object (== not just equal()) this method       * does not consider that a red flag.  This allows for subtle variations       * in the way a Parser is specified (null vs DEFAULT_LONG_PARSER, etc...)      *</p>      */
DECL|field|VALUEMISMATCH
specifier|public
specifier|final
specifier|static
name|InsanityType
name|VALUEMISMATCH
init|=
operator|new
name|InsanityType
argument_list|(
literal|"VALUEMISMATCH"
argument_list|)
decl_stmt|;
comment|/**       * Indicates an expected bit of "insanity".  This may be useful for       * clients that wish to preserve/log information about insane usage       * but indicate that it was expected.       */
DECL|field|EXPECTED
specifier|public
specifier|final
specifier|static
name|InsanityType
name|EXPECTED
init|=
operator|new
name|InsanityType
argument_list|(
literal|"EXPECTED"
argument_list|)
decl_stmt|;
block|}
block|}
end_class
end_unit
