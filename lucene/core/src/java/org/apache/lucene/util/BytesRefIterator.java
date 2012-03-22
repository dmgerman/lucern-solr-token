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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/**  * A simple iterator interface for {@link BytesRef} iteration.  */
end_comment
begin_interface
DECL|interface|BytesRefIterator
specifier|public
interface|interface
name|BytesRefIterator
block|{
comment|/** Singleton BytesRefIterator that iterates over 0 BytesRefs. */
DECL|field|EMPTY_ITERATOR
specifier|public
specifier|static
specifier|final
name|BytesRefIterator
name|EMPTY_ITERATOR
init|=
operator|new
name|EmptyBytesRefIterator
argument_list|()
decl_stmt|;
comment|/**    * Increments the iteration to the next {@link BytesRef} in the iterator.    * Returns the resulting {@link BytesRef} or<code>null</code> if the end of    * the iterator is reached. The returned BytesRef may be re-used across calls    * to next. After this method returns null, do not call it again: the results    * are undefined.    *     * @return the next {@link BytesRef} in the iterator or<code>null</code> if    *         the end of the iterator is reached.    * @throws IOException    */
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the {@link BytesRef} Comparator used to sort terms provided by the    * iterator. This may return null if there are no items or the iterator is not    * sorted. Callers may invoke this method many times, so it's best to cache a    * single instance& reuse it.    */
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
function_decl|;
comment|// TODO: private?
comment|/** Iterates over 0 BytesRefs. */
DECL|class|EmptyBytesRefIterator
specifier|public
specifier|final
specifier|static
class|class
name|EmptyBytesRefIterator
implements|implements
name|BytesRefIterator
block|{
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_interface
end_unit
