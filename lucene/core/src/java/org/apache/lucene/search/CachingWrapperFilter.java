begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
operator|.
name|EMPTY
import|;
end_import
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
name|HashMap
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
name|WeakHashMap
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
name|LeafReader
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
name|LeafReaderContext
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
name|util
operator|.
name|Accountable
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
name|util
operator|.
name|Accountables
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|RoaringDocIdSet
import|;
end_import
begin_comment
comment|/**  * Wraps another {@link Filter}'s result and caches it.  The purpose is to allow  * filters to simply filter, and then wrap with this class  * to add caching.  */
end_comment
begin_class
DECL|class|CachingWrapperFilter
specifier|public
class|class
name|CachingWrapperFilter
extends|extends
name|Filter
implements|implements
name|Accountable
block|{
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|DocIdSet
argument_list|>
name|cache
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|Object
argument_list|,
name|DocIdSet
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Wraps another filter's result and caches it.    * @param filter Filter to cache results of    */
DECL|method|CachingWrapperFilter
specifier|public
name|CachingWrapperFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * Gets the contained filter.    * @return the contained filter.    */
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|/**     *  Provide the DocIdSet to be cached, using the DocIdSet provided    *  by the wrapped Filter.<p>This implementation returns the given {@link DocIdSet},    *  if {@link DocIdSet#isCacheable} returns<code>true</code>, else it calls    *  {@link #cacheImpl(DocIdSetIterator, org.apache.lucene.index.LeafReader)}    *<p>Note: This method returns {@linkplain DocIdSet#EMPTY} if the given docIdSet    *  is<code>null</code> or if {@link DocIdSet#iterator()} return<code>null</code>. The empty    *  instance is use as a placeholder in the cache instead of the<code>null</code> value.    */
DECL|method|docIdSetToCache
specifier|protected
name|DocIdSet
name|docIdSetToCache
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docIdSet
operator|==
literal|null
operator|||
name|docIdSet
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
return|return
name|docIdSet
return|;
block|}
else|else
block|{
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|cacheImpl
argument_list|(
name|it
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Default cache implementation: uses {@link RoaringDocIdSet}.    */
DECL|method|cacheImpl
specifier|protected
name|DocIdSet
name|cacheImpl
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|iterator
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|// for testing
DECL|field|hitCount
DECL|field|missCount
name|int
name|hitCount
decl_stmt|,
name|missCount
decl_stmt|;
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|key
init|=
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|!=
literal|null
condition|)
block|{
name|hitCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|missCount
operator|++
expr_stmt|;
name|docIdSet
operator|=
name|docIdSetToCache
argument_list|(
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
comment|// We use EMPTY as a sentinel for the empty set, which is cacheable
name|docIdSet
operator|=
name|EMPTY
expr_stmt|;
block|}
assert|assert
name|docIdSet
operator|.
name|isCacheable
argument_list|()
assert|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|docIdSet
argument_list|)
expr_stmt|;
block|}
return|return
name|docIdSet
operator|==
name|EMPTY
condition|?
literal|null
else|:
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|docIdSet
argument_list|,
name|acceptDocs
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|filter
operator|+
literal|")"
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|!
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
specifier|final
name|CachingWrapperFilter
name|other
init|=
operator|(
name|CachingWrapperFilter
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|filter
argument_list|)
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
operator|(
name|filter
operator|.
name|hashCode
argument_list|()
operator|^
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// Sync only to pull the current set of values:
name|List
argument_list|<
name|DocIdSet
argument_list|>
name|docIdSets
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|docIdSets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cache
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DocIdSet
name|dis
range|:
name|docIdSets
control|)
block|{
name|total
operator|+=
name|dis
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
comment|// Sync to pull the current set of values:
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// no need to clone, Accountable#namedAccountables already copies the data
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"segment"
argument_list|,
name|cache
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
