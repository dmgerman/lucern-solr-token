begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache.cl2o
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
operator|.
name|writercache
operator|.
name|cl2o
package|;
end_package
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * HashMap to store colliding labels. See {@link CompactLabelToOrdinal} for  * details.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CollisionMap
specifier|public
class|class
name|CollisionMap
block|{
DECL|field|capacity
specifier|private
name|int
name|capacity
decl_stmt|;
DECL|field|loadFactor
specifier|private
name|float
name|loadFactor
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|threshold
specifier|private
name|int
name|threshold
decl_stmt|;
DECL|class|Entry
specifier|static
class|class
name|Entry
block|{
DECL|field|offset
name|int
name|offset
decl_stmt|;
DECL|field|cid
name|int
name|cid
decl_stmt|;
DECL|field|next
name|Entry
name|next
decl_stmt|;
DECL|field|hash
name|int
name|hash
decl_stmt|;
DECL|method|Entry
name|Entry
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|cid
parameter_list|,
name|int
name|h
parameter_list|,
name|Entry
name|e
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|cid
operator|=
name|cid
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|e
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|h
expr_stmt|;
block|}
block|}
DECL|field|labelRepository
specifier|private
name|CharBlockArray
name|labelRepository
decl_stmt|;
DECL|field|entries
specifier|private
name|Entry
index|[]
name|entries
decl_stmt|;
DECL|method|CollisionMap
specifier|public
name|CollisionMap
parameter_list|(
name|CharBlockArray
name|labelRepository
parameter_list|)
block|{
name|this
argument_list|(
literal|16
operator|*
literal|1024
argument_list|,
literal|0.75f
argument_list|,
name|labelRepository
argument_list|)
expr_stmt|;
block|}
DECL|method|CollisionMap
specifier|public
name|CollisionMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|CharBlockArray
name|labelRepository
parameter_list|)
block|{
name|this
argument_list|(
name|initialCapacity
argument_list|,
literal|0.75f
argument_list|,
name|labelRepository
argument_list|)
expr_stmt|;
block|}
DECL|method|CollisionMap
specifier|private
name|CollisionMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|CharBlockArray
name|labelRepository
parameter_list|)
block|{
name|this
operator|.
name|labelRepository
operator|=
name|labelRepository
expr_stmt|;
name|this
operator|.
name|loadFactor
operator|=
name|loadFactor
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|CompactLabelToOrdinal
operator|.
name|determineCapacity
argument_list|(
literal|2
argument_list|,
name|initialCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|entries
operator|=
operator|new
name|Entry
index|[
name|this
operator|.
name|capacity
index|]
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|capacity
operator|*
name|this
operator|.
name|loadFactor
argument_list|)
expr_stmt|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|size
return|;
block|}
DECL|method|capacity
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|capacity
return|;
block|}
DECL|method|grow
specifier|private
name|void
name|grow
parameter_list|()
block|{
name|int
name|newCapacity
init|=
name|this
operator|.
name|capacity
operator|*
literal|2
decl_stmt|;
name|Entry
index|[]
name|newEntries
init|=
operator|new
name|Entry
index|[
name|newCapacity
index|]
decl_stmt|;
name|Entry
index|[]
name|src
init|=
name|this
operator|.
name|entries
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|src
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Entry
name|e
init|=
name|src
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|src
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
do|do
block|{
name|Entry
name|next
init|=
name|e
operator|.
name|next
decl_stmt|;
name|int
name|hash
init|=
name|e
operator|.
name|hash
decl_stmt|;
name|int
name|i
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|newCapacity
argument_list|)
decl_stmt|;
name|e
operator|.
name|next
operator|=
name|newEntries
index|[
name|i
index|]
expr_stmt|;
name|newEntries
index|[
name|i
index|]
operator|=
name|e
expr_stmt|;
name|e
operator|=
name|next
expr_stmt|;
block|}
do|while
condition|(
name|e
operator|!=
literal|null
condition|)
do|;
block|}
block|}
name|this
operator|.
name|capacity
operator|=
name|newCapacity
expr_stmt|;
name|this
operator|.
name|entries
operator|=
name|newEntries
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|capacity
operator|*
name|this
operator|.
name|loadFactor
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|CategoryPath
name|label
parameter_list|,
name|int
name|hash
parameter_list|)
block|{
name|int
name|bucketIndex
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|this
operator|.
name|capacity
argument_list|)
decl_stmt|;
name|Entry
name|e
init|=
name|this
operator|.
name|entries
index|[
name|bucketIndex
index|]
decl_stmt|;
while|while
condition|(
name|e
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|hash
operator|==
name|e
operator|.
name|hash
operator|&&
name|label
operator|.
name|equalsToSerialized
argument_list|(
name|this
operator|.
name|labelRepository
argument_list|,
name|e
operator|.
name|offset
argument_list|)
operator|)
condition|)
block|{
name|e
operator|=
name|e
operator|.
name|next
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
return|return
name|LabelToOrdinal
operator|.
name|InvalidOrdinal
return|;
block|}
return|return
name|e
operator|.
name|cid
return|;
block|}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|CategoryPath
name|label
parameter_list|,
name|int
name|prefixLen
parameter_list|,
name|int
name|hash
parameter_list|)
block|{
name|int
name|bucketIndex
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|this
operator|.
name|capacity
argument_list|)
decl_stmt|;
name|Entry
name|e
init|=
name|this
operator|.
name|entries
index|[
name|bucketIndex
index|]
decl_stmt|;
while|while
condition|(
name|e
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|hash
operator|==
name|e
operator|.
name|hash
operator|&&
name|label
operator|.
name|equalsToSerialized
argument_list|(
name|prefixLen
argument_list|,
name|this
operator|.
name|labelRepository
argument_list|,
name|e
operator|.
name|offset
argument_list|)
operator|)
condition|)
block|{
name|e
operator|=
name|e
operator|.
name|next
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
return|return
name|LabelToOrdinal
operator|.
name|InvalidOrdinal
return|;
block|}
return|return
name|e
operator|.
name|cid
return|;
block|}
DECL|method|addLabel
specifier|public
name|int
name|addLabel
parameter_list|(
name|CategoryPath
name|label
parameter_list|,
name|int
name|hash
parameter_list|,
name|int
name|cid
parameter_list|)
block|{
name|int
name|bucketIndex
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|this
operator|.
name|capacity
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
name|e
init|=
name|this
operator|.
name|entries
index|[
name|bucketIndex
index|]
init|;
name|e
operator|!=
literal|null
condition|;
name|e
operator|=
name|e
operator|.
name|next
control|)
block|{
if|if
condition|(
name|e
operator|.
name|hash
operator|==
name|hash
operator|&&
name|label
operator|.
name|equalsToSerialized
argument_list|(
name|this
operator|.
name|labelRepository
argument_list|,
name|e
operator|.
name|offset
argument_list|)
condition|)
block|{
return|return
name|e
operator|.
name|cid
return|;
block|}
block|}
comment|// new string; add to label repository
name|int
name|offset
init|=
name|this
operator|.
name|labelRepository
operator|.
name|length
argument_list|()
decl_stmt|;
try|try
block|{
name|label
operator|.
name|serializeAppendTo
argument_list|(
name|labelRepository
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// can't happen, because labelRepository.append() doesn't throw an exception
block|}
name|addEntry
argument_list|(
name|offset
argument_list|,
name|cid
argument_list|,
name|hash
argument_list|,
name|bucketIndex
argument_list|)
expr_stmt|;
return|return
name|cid
return|;
block|}
DECL|method|addLabel
specifier|public
name|int
name|addLabel
parameter_list|(
name|CategoryPath
name|label
parameter_list|,
name|int
name|prefixLen
parameter_list|,
name|int
name|hash
parameter_list|,
name|int
name|cid
parameter_list|)
block|{
name|int
name|bucketIndex
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|this
operator|.
name|capacity
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
name|e
init|=
name|this
operator|.
name|entries
index|[
name|bucketIndex
index|]
init|;
name|e
operator|!=
literal|null
condition|;
name|e
operator|=
name|e
operator|.
name|next
control|)
block|{
if|if
condition|(
name|e
operator|.
name|hash
operator|==
name|hash
operator|&&
name|label
operator|.
name|equalsToSerialized
argument_list|(
name|prefixLen
argument_list|,
name|this
operator|.
name|labelRepository
argument_list|,
name|e
operator|.
name|offset
argument_list|)
condition|)
block|{
return|return
name|e
operator|.
name|cid
return|;
block|}
block|}
comment|// new string; add to label repository
name|int
name|offset
init|=
name|this
operator|.
name|labelRepository
operator|.
name|length
argument_list|()
decl_stmt|;
try|try
block|{
name|label
operator|.
name|serializeAppendTo
argument_list|(
name|prefixLen
argument_list|,
name|labelRepository
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// can't happen, because labelRepository.append() doesn't throw an exception
block|}
name|addEntry
argument_list|(
name|offset
argument_list|,
name|cid
argument_list|,
name|hash
argument_list|,
name|bucketIndex
argument_list|)
expr_stmt|;
return|return
name|cid
return|;
block|}
comment|/**    * This method does not check if the same value is already    * in the map because we pass in an char-array offset, so    * so we now that we're in resize-mode here.     */
DECL|method|addLabelOffset
specifier|public
name|void
name|addLabelOffset
parameter_list|(
name|int
name|hash
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|cid
parameter_list|)
block|{
name|int
name|bucketIndex
init|=
name|indexFor
argument_list|(
name|hash
argument_list|,
name|this
operator|.
name|capacity
argument_list|)
decl_stmt|;
name|addEntry
argument_list|(
name|offset
argument_list|,
name|cid
argument_list|,
name|hash
argument_list|,
name|bucketIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|addEntry
specifier|private
name|void
name|addEntry
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|cid
parameter_list|,
name|int
name|hash
parameter_list|,
name|int
name|bucketIndex
parameter_list|)
block|{
name|Entry
name|e
init|=
name|this
operator|.
name|entries
index|[
name|bucketIndex
index|]
decl_stmt|;
name|this
operator|.
name|entries
index|[
name|bucketIndex
index|]
operator|=
operator|new
name|Entry
argument_list|(
name|offset
argument_list|,
name|cid
argument_list|,
name|hash
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|size
operator|++
operator|>=
name|this
operator|.
name|threshold
condition|)
block|{
name|grow
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|entryIterator
name|Iterator
argument_list|<
name|CollisionMap
operator|.
name|Entry
argument_list|>
name|entryIterator
parameter_list|()
block|{
return|return
operator|new
name|EntryIterator
argument_list|(
name|entries
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/**    * Returns index for hash code h.     */
DECL|method|indexFor
specifier|static
name|int
name|indexFor
parameter_list|(
name|int
name|h
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|h
operator|&
operator|(
name|length
operator|-
literal|1
operator|)
return|;
block|}
comment|/**    * Returns an estimate of the memory usage of this CollisionMap.    * @return The approximate number of bytes used by this structure.    */
DECL|method|getMemoryUsage
name|int
name|getMemoryUsage
parameter_list|()
block|{
name|int
name|memoryUsage
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|entries
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
name|e
range|:
name|this
operator|.
name|entries
control|)
block|{
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|memoryUsage
operator|+=
operator|(
literal|4
operator|*
literal|4
operator|)
expr_stmt|;
for|for
control|(
name|Entry
name|ee
init|=
name|e
operator|.
name|next
init|;
name|ee
operator|!=
literal|null
condition|;
name|ee
operator|=
name|ee
operator|.
name|next
control|)
block|{
name|memoryUsage
operator|+=
operator|(
literal|4
operator|*
literal|4
operator|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|memoryUsage
return|;
block|}
DECL|class|EntryIterator
specifier|private
class|class
name|EntryIterator
implements|implements
name|Iterator
argument_list|<
name|Entry
argument_list|>
block|{
DECL|field|next
name|Entry
name|next
decl_stmt|;
comment|// next entry to return
DECL|field|index
name|int
name|index
decl_stmt|;
comment|// current slot
DECL|field|ents
name|Entry
index|[]
name|ents
decl_stmt|;
DECL|method|EntryIterator
name|EntryIterator
parameter_list|(
name|Entry
index|[]
name|entries
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|ents
operator|=
name|entries
expr_stmt|;
name|Entry
index|[]
name|t
init|=
name|entries
decl_stmt|;
name|int
name|i
init|=
name|t
operator|.
name|length
decl_stmt|;
name|Entry
name|n
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|size
operator|!=
literal|0
condition|)
block|{
comment|// advance to first entry
while|while
condition|(
name|i
operator|>
literal|0
operator|&&
operator|(
name|n
operator|=
name|t
index|[
operator|--
name|i
index|]
operator|)
operator|==
literal|null
condition|)
block|{
comment|// advance
block|}
block|}
name|this
operator|.
name|next
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|i
expr_stmt|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|this
operator|.
name|next
operator|!=
literal|null
return|;
block|}
DECL|method|next
specifier|public
name|Entry
name|next
parameter_list|()
block|{
name|Entry
name|e
init|=
name|this
operator|.
name|next
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
name|Entry
name|n
init|=
name|e
operator|.
name|next
decl_stmt|;
name|Entry
index|[]
name|t
init|=
name|ents
decl_stmt|;
name|int
name|i
init|=
name|this
operator|.
name|index
decl_stmt|;
while|while
condition|(
name|n
operator|==
literal|null
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
name|n
operator|=
name|t
index|[
operator|--
name|i
index|]
expr_stmt|;
block|}
name|this
operator|.
name|index
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|n
expr_stmt|;
return|return
name|e
return|;
block|}
DECL|method|remove
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
block|}
end_class
end_unit
