begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|ArrayUtil
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
name|ByteBlockPool
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
name|BytesRef
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
name|BytesRefIterator
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
name|SorterTemplate
import|;
end_import
begin_class
DECL|class|BytesRefList
specifier|final
class|class
name|BytesRefList
block|{
DECL|field|pool
specifier|private
specifier|final
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|field|offsets
specifier|private
name|int
index|[]
name|offsets
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|field|currentElement
specifier|private
name|int
name|currentElement
init|=
literal|0
decl_stmt|;
DECL|field|currentOffset
specifier|private
name|int
name|currentOffset
init|=
literal|0
decl_stmt|;
DECL|method|BytesRefList
specifier|public
name|BytesRefList
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectAllocator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|BytesRefList
specifier|public
name|BytesRefList
parameter_list|(
name|ByteBlockPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
DECL|method|append
specifier|public
name|int
name|append
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|currentElement
operator|>=
name|offsets
operator|.
name|length
condition|)
block|{
name|offsets
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|offsets
argument_list|,
name|offsets
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|copy
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|offsets
index|[
name|currentElement
operator|++
index|]
operator|=
name|currentOffset
expr_stmt|;
name|currentOffset
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
return|return
name|currentElement
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|currentElement
return|;
block|}
DECL|method|get
specifier|public
name|BytesRef
name|get
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|currentElement
operator|>
name|pos
condition|)
block|{
name|bytes
operator|.
name|offset
operator|=
name|offsets
index|[
name|pos
index|]
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|pos
operator|==
name|currentElement
operator|-
literal|1
condition|?
name|currentOffset
operator|-
name|bytes
operator|.
name|offset
else|:
name|offsets
index|[
name|pos
operator|+
literal|1
index|]
operator|-
name|bytes
operator|.
name|offset
expr_stmt|;
name|pool
operator|.
name|copyFrom
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"index "
operator|+
name|pos
operator|+
literal|" must be less than the size: "
operator|+
name|currentElement
argument_list|)
throw|;
block|}
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|()
block|{
specifier|final
name|int
name|numElements
init|=
name|currentElement
decl_stmt|;
return|return
operator|new
name|BytesRefIterator
argument_list|()
block|{
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|private
name|int
name|pos
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|<
name|numElements
condition|)
block|{
name|get
argument_list|(
name|spare
argument_list|,
name|pos
operator|++
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
return|;
block|}
DECL|method|sort
specifier|public
name|int
index|[]
name|sort
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|orderdEntries
init|=
operator|new
name|int
index|[
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
name|orderdEntries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|orderdEntries
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
operator|new
name|SorterTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|orderdEntries
index|[
name|i
index|]
decl_stmt|;
name|orderdEntries
index|[
name|i
index|]
operator|=
name|orderdEntries
index|[
name|j
index|]
expr_stmt|;
name|orderdEntries
index|[
name|j
index|]
operator|=
name|o
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ord1
init|=
name|orderdEntries
index|[
name|i
index|]
decl_stmt|,
name|ord2
init|=
name|orderdEntries
index|[
name|j
index|]
decl_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|get
argument_list|(
name|scratch1
argument_list|,
name|ord1
argument_list|)
argument_list|,
name|get
argument_list|(
name|scratch2
argument_list|,
name|ord2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|orderdEntries
index|[
name|i
index|]
decl_stmt|;
name|get
argument_list|(
name|pivot
argument_list|,
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|orderdEntries
index|[
name|j
index|]
decl_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|pivot
argument_list|,
name|get
argument_list|(
name|scratch2
argument_list|,
name|ord
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|BytesRef
name|pivot
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|,
name|scratch1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|,
name|scratch2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
block|}
operator|.
name|quickSort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|orderdEntries
return|;
block|}
block|}
end_class
end_unit
