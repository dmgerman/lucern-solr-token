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
begin_comment
comment|/**  * A DocIdSet contains a set of doc ids. Implementing classes must  * only implement {@link #iterator} to provide access to the set.   */
end_comment
begin_class
DECL|class|DocIdSet
specifier|public
specifier|abstract
class|class
name|DocIdSet
block|{
comment|/** Provides a {@link DocIdSetIterator} to access the set.    * This implementation can return<code>null</code> if there    * are no docs that match. */
DECL|method|iterator
specifier|public
specifier|abstract
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Optionally provides a {@link Bits} interface for random access    * to matching documents.    * @return {@code null}, if this {@code DocIdSet} does not support random access.    * In contrast to {@link #iterator()}, a return value of {@code null}    *<b>does not</b> imply that no documents match the filter!    * The default implementation does not provide random access, so you    * only need to implement this method if your DocIdSet can    * guarantee random access to every docid in O(1) time without    * external disk access (as {@link Bits} interface cannot throw    * {@link IOException}). This is generally true for bit sets    * like {@link org.apache.lucene.util.FixedBitSet}, which return    * itself if they are used as {@code DocIdSet}.    */
DECL|method|bits
specifier|public
name|Bits
name|bits
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * This method is a hint for {@link CachingWrapperFilter}, if this<code>DocIdSet</code>    * should be cached without copying it into a BitSet. The default is to return    *<code>false</code>. If you have an own<code>DocIdSet</code> implementation    * that does its iteration very effective and fast without doing disk I/O,    * override this method and return<code>true</code>.    */
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
