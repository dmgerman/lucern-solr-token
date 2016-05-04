begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|DocIdSetIterator
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|PriorityQueue
import|;
end_import
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
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
import|;
end_import
begin_comment
comment|/** Utility class to help merging documents from sub-readers according to either simple  *  concatenated (unsorted) order, or by a specified index-time sort, skipping  *  deleted documents and remapping non-deleted documents. */
end_comment
begin_class
DECL|class|DocIDMerger
specifier|public
class|class
name|DocIDMerger
parameter_list|<
name|T
extends|extends
name|DocIDMerger
operator|.
name|Sub
parameter_list|>
block|{
DECL|field|subs
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|subs
decl_stmt|;
comment|// Used when indexSort != null:
DECL|field|queue
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|T
argument_list|>
name|queue
decl_stmt|;
DECL|field|first
specifier|private
name|boolean
name|first
decl_stmt|;
comment|// Used when indexIsSorted
DECL|field|current
specifier|private
name|T
name|current
decl_stmt|;
DECL|field|nextIndex
specifier|private
name|int
name|nextIndex
decl_stmt|;
DECL|class|Sub
specifier|public
specifier|static
specifier|abstract
class|class
name|Sub
block|{
DECL|field|mappedDocID
specifier|public
name|int
name|mappedDocID
decl_stmt|;
DECL|field|docMap
specifier|final
name|MergeState
operator|.
name|DocMap
name|docMap
decl_stmt|;
DECL|field|liveDocs
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
comment|// nocommit isn't liveDocs redundant?  docMap returns -1 for us?
DECL|method|Sub
specifier|public
name|Sub
parameter_list|(
name|MergeState
operator|.
name|DocMap
name|docMap
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
block|{
name|this
operator|.
name|docMap
operator|=
name|docMap
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
block|}
comment|/** Returns the next document ID from this sub reader, and {@link DocIdSetIterator#NO_MORE_DOCS} when done */
DECL|method|nextDoc
specifier|public
specifier|abstract
name|int
name|nextDoc
parameter_list|()
function_decl|;
block|}
DECL|method|DocIDMerger
specifier|public
name|DocIDMerger
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|subs
parameter_list|,
name|int
name|maxCount
parameter_list|,
name|boolean
name|indexIsSorted
parameter_list|)
block|{
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
if|if
condition|(
name|indexIsSorted
condition|)
block|{
name|queue
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|T
argument_list|>
argument_list|(
name|maxCount
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Sub
name|a
parameter_list|,
name|Sub
name|b
parameter_list|)
block|{
assert|assert
name|a
operator|.
name|mappedDocID
operator|!=
name|b
operator|.
name|mappedDocID
assert|;
return|return
name|a
operator|.
name|mappedDocID
operator|<
name|b
operator|.
name|mappedDocID
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
comment|// We simply concatentate
name|queue
operator|=
literal|null
expr_stmt|;
block|}
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// nocommit it's awkward that we must pass in this boolean, when the subs should "know" this based on what docMap they have?
DECL|method|DocIDMerger
specifier|public
name|DocIDMerger
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|subs
parameter_list|,
name|boolean
name|indexIsSorted
parameter_list|)
block|{
name|this
argument_list|(
name|subs
argument_list|,
name|subs
operator|.
name|size
argument_list|()
argument_list|,
name|indexIsSorted
argument_list|)
expr_stmt|;
block|}
comment|/** Reuse API, currently only used by postings during merge */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
block|{
assert|assert
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
for|for
control|(
name|T
name|sub
range|:
name|subs
control|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|docID
init|=
name|sub
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|NO_MORE_DOCS
condition|)
block|{
comment|// all docs in this sub were deleted; do not add it to the queue!
break|break;
block|}
elseif|else
if|if
condition|(
name|sub
operator|.
name|liveDocs
operator|!=
literal|null
operator|&&
name|sub
operator|.
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// nocommit is it sub's job to skip deleted docs?
continue|continue;
block|}
else|else
block|{
name|sub
operator|.
name|mappedDocID
operator|=
name|sub
operator|.
name|docMap
operator|.
name|get
argument_list|(
name|docID
argument_list|)
expr_stmt|;
assert|assert
name|sub
operator|.
name|mappedDocID
operator|!=
operator|-
literal|1
assert|;
name|queue
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|first
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|subs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|current
operator|=
name|subs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nextIndex
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
name|nextIndex
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
comment|/** Returns null when done */
DECL|method|next
specifier|public
name|T
name|next
parameter_list|()
block|{
comment|// Loop until we find a non-deleted document
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
block|{
name|T
name|top
init|=
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|==
literal|null
condition|)
block|{
comment|// NOTE: it's annoying that caller is allowed to call us again even after we returned null before
return|return
literal|null
return|;
block|}
if|if
condition|(
name|first
operator|==
literal|false
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|docID
init|=
name|top
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|top
operator|=
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|top
operator|.
name|liveDocs
operator|!=
literal|null
operator|&&
name|top
operator|.
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
else|else
block|{
name|top
operator|.
name|mappedDocID
operator|=
name|top
operator|.
name|docMap
operator|.
name|get
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|top
operator|=
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
name|first
operator|=
literal|false
expr_stmt|;
return|return
name|top
return|;
block|}
else|else
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
comment|// NOTE: it's annoying that caller is allowed to call us again even after we returned null before
return|return
literal|null
return|;
block|}
name|int
name|docID
init|=
name|current
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|nextIndex
operator|==
name|subs
operator|.
name|size
argument_list|()
condition|)
block|{
name|current
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
name|current
operator|=
name|subs
operator|.
name|get
argument_list|(
name|nextIndex
argument_list|)
expr_stmt|;
name|nextIndex
operator|++
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|current
operator|.
name|liveDocs
operator|!=
literal|null
operator|&&
name|current
operator|.
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// Document is deleted
continue|continue;
block|}
name|current
operator|.
name|mappedDocID
operator|=
name|current
operator|.
name|docMap
operator|.
name|get
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
name|current
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
