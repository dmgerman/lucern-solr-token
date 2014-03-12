begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|BufferedUpdatesStream
operator|.
name|QueryAndLimit
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
name|MergedIterator
import|;
end_import
begin_class
DECL|class|CoalescedUpdates
class|class
name|CoalescedUpdates
block|{
DECL|field|queries
specifier|final
name|Map
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|queries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|iterables
specifier|final
name|List
argument_list|<
name|Iterable
argument_list|<
name|Term
argument_list|>
argument_list|>
name|iterables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numericDVUpdates
specifier|final
name|List
argument_list|<
name|NumericUpdate
argument_list|>
name|numericDVUpdates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// note: we could add/collect more debugging information
return|return
literal|"CoalescedUpdates(termSets="
operator|+
name|iterables
operator|.
name|size
argument_list|()
operator|+
literal|",queries="
operator|+
name|queries
operator|.
name|size
argument_list|()
operator|+
literal|",numericUpdates="
operator|+
name|numericDVUpdates
operator|.
name|size
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|FrozenBufferedUpdates
name|in
parameter_list|)
block|{
name|iterables
operator|.
name|add
argument_list|(
name|in
operator|.
name|termsIterable
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|queryIdx
init|=
literal|0
init|;
name|queryIdx
operator|<
name|in
operator|.
name|queries
operator|.
name|length
condition|;
name|queryIdx
operator|++
control|)
block|{
specifier|final
name|Query
name|query
init|=
name|in
operator|.
name|queries
index|[
name|queryIdx
index|]
decl_stmt|;
name|queries
operator|.
name|put
argument_list|(
name|query
argument_list|,
name|BufferedUpdates
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|NumericUpdate
name|nu
range|:
name|in
operator|.
name|updates
control|)
block|{
name|NumericUpdate
name|clone
init|=
operator|new
name|NumericUpdate
argument_list|(
name|nu
operator|.
name|term
argument_list|,
name|nu
operator|.
name|field
argument_list|,
name|nu
operator|.
name|value
argument_list|)
decl_stmt|;
name|clone
operator|.
name|docIDUpto
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|numericDVUpdates
operator|.
name|add
argument_list|(
name|clone
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|termsIterable
specifier|public
name|Iterable
argument_list|<
name|Term
argument_list|>
name|termsIterable
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Term
argument_list|>
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|Term
argument_list|>
name|subs
index|[]
init|=
operator|new
name|Iterator
index|[
name|iterables
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
name|iterables
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|subs
index|[
name|i
index|]
operator|=
name|iterables
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
name|subs
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|queriesIterable
specifier|public
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
name|queriesIterable
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QueryAndLimit
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|QueryAndLimit
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|iter
init|=
name|queries
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryAndLimit
name|next
parameter_list|()
block|{
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|ent
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|QueryAndLimit
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
