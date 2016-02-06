begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
package|;
end_package
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
begin_comment
comment|/**  * LRUHashMap is an extension of Java's HashMap, which has a bounded size();  * When it reaches that size, each time a new element is added, the least  * recently used (LRU) entry is removed.  *<p>  * Java makes it very easy to implement LRUHashMap - all its functionality is  * already available from {@link java.util.LinkedHashMap}, and we just need to  * configure that properly.  *<p>  * Note that like HashMap, LRUHashMap is unsynchronized, and the user MUST  * synchronize the access to it if used from several threads. Moreover, while  * with HashMap this is only a concern if one of the threads is modifies the  * map, with LURHashMap every read is a modification (because the LRU order  * needs to be remembered) so proper synchronization is always necessary.  *<p>  * With the usual synchronization mechanisms available to the user, this  * unfortunately means that LRUHashMap will probably perform sub-optimally under  * heavy contention: while one thread uses the hash table (reads or writes), any  * other thread will be blocked from using it - or even just starting to use it  * (e.g., calculating the hash function). A more efficient approach would be not  * to use LinkedHashMap at all, but rather to use a non-locking (as much as  * possible) thread-safe solution, something along the lines of  * java.util.concurrent.ConcurrentHashMap (though that particular class does not  * support the additional LRU semantics, which will need to be added separately  * using a concurrent linked list or additional storage of timestamps (in an  * array or inside the entry objects), or whatever).  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|LRUHashMap
specifier|public
class|class
name|LRUHashMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|LinkedHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|maxSize
specifier|private
name|int
name|maxSize
decl_stmt|;
comment|/**    * Create a new hash map with a bounded size and with least recently    * used entries removed.    * @param maxSize    *     the maximum size (in number of entries) to which the map can grow    *     before the least recently used entries start being removed.<BR>    *      Setting maxSize to a very large value, like    *      {@link Integer#MAX_VALUE} is allowed, but is less efficient than    *      using {@link java.util.HashMap} because our class needs    *      to keep track of the use order (via an additional doubly-linked    *      list) which is not used when the map's size is always below the    *      maximum size.     */
DECL|method|LRUHashMap
specifier|public
name|LRUHashMap
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
literal|16
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
comment|/**    * Return the max size    */
DECL|method|getMaxSize
specifier|public
name|int
name|getMaxSize
parameter_list|()
block|{
return|return
name|maxSize
return|;
block|}
comment|/**    * setMaxSize() allows changing the map's maximal number of elements    * which was defined at construction time.    *<P>    * Note that if the map is already larger than maxSize, the current     * implementation does not shrink it (by removing the oldest elements);    * Rather, the map remains in its current size as new elements are    * added, and will only start shrinking (until settling again on the    * give maxSize) if existing elements are explicitly deleted.      */
DECL|method|setMaxSize
specifier|public
name|void
name|setMaxSize
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
comment|// We override LinkedHashMap's removeEldestEntry() method. This method
comment|// is called every time a new entry is added, and if we return true
comment|// here, the eldest element will be deleted automatically. In our case,
comment|// we return true if the size of the map grew beyond our limit - ignoring
comment|// what is that eldest element that we'll be deleting.
annotation|@
name|Override
DECL|method|removeEldestEntry
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|maxSize
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|clone
specifier|public
name|LRUHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|clone
parameter_list|()
block|{
return|return
operator|(
name|LRUHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class
end_unit
