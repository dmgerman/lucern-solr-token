begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|*
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
name|*
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
name|index
operator|.
name|DocValues
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
name|SortedDocValues
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
name|TermsEnum
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
name|DocIdSet
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
name|Filter
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
name|BytesRefBuilder
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
name|CharsRefBuilder
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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|FacetParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|FieldType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocSet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|BoundedTreeSet
import|;
end_import
begin_class
DECL|class|PerSegmentSingleValuedFaceting
class|class
name|PerSegmentSingleValuedFaceting
block|{
comment|// input params
DECL|field|searcher
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|docs
name|DocSet
name|docs
decl_stmt|;
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|offset
name|int
name|offset
decl_stmt|;
DECL|field|limit
name|int
name|limit
decl_stmt|;
DECL|field|mincount
name|int
name|mincount
decl_stmt|;
DECL|field|missing
name|boolean
name|missing
decl_stmt|;
DECL|field|sort
name|String
name|sort
decl_stmt|;
DECL|field|prefix
name|String
name|prefix
decl_stmt|;
DECL|field|baseSet
name|Filter
name|baseSet
decl_stmt|;
DECL|field|nThreads
name|int
name|nThreads
decl_stmt|;
DECL|method|PerSegmentSingleValuedFaceting
specifier|public
name|PerSegmentSingleValuedFaceting
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|mincount
parameter_list|,
name|boolean
name|missing
parameter_list|,
name|String
name|sort
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|mincount
operator|=
name|mincount
expr_stmt|;
name|this
operator|.
name|missing
operator|=
name|missing
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|setNumThreads
specifier|public
name|void
name|setNumThreads
parameter_list|(
name|int
name|threads
parameter_list|)
block|{
name|nThreads
operator|=
name|threads
expr_stmt|;
block|}
DECL|method|getFacetCounts
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getFacetCounts
parameter_list|(
name|Executor
name|executor
parameter_list|)
throws|throws
name|IOException
block|{
name|CompletionService
argument_list|<
name|SegFacet
argument_list|>
name|completionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|executor
argument_list|)
decl_stmt|;
comment|// reuse the translation logic to go from top level set to per-segment set
name|baseSet
operator|=
name|docs
operator|.
name|getTopFilter
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
comment|// The list of pending tasks that aren't immediately submitted
comment|// TODO: Is there a completion service, or a delegating executor that can
comment|// limit the number of concurrent tasks submitted to a bigger executor?
name|LinkedList
argument_list|<
name|Callable
argument_list|<
name|SegFacet
argument_list|>
argument_list|>
name|pending
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|threads
init|=
name|nThreads
operator|<=
literal|0
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|nThreads
decl_stmt|;
for|for
control|(
specifier|final
name|LeafReaderContext
name|leave
range|:
name|leaves
control|)
block|{
specifier|final
name|SegFacet
name|segFacet
init|=
operator|new
name|SegFacet
argument_list|(
name|leave
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|SegFacet
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|SegFacet
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SegFacet
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|segFacet
operator|.
name|countTerms
argument_list|()
expr_stmt|;
return|return
name|segFacet
return|;
block|}
block|}
decl_stmt|;
comment|// TODO: if limiting threads, submit by largest segment first?
if|if
condition|(
operator|--
name|threads
operator|>=
literal|0
condition|)
block|{
name|completionService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pending
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now merge the per-segment results
name|PriorityQueue
argument_list|<
name|SegFacet
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|SegFacet
argument_list|>
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SegFacet
name|a
parameter_list|,
name|SegFacet
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|tempBR
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|tempBR
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
decl_stmt|;
name|boolean
name|hasMissingCount
init|=
literal|false
decl_stmt|;
name|int
name|missingCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|leaves
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|SegFacet
name|seg
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Future
argument_list|<
name|SegFacet
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
name|seg
operator|=
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|pending
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|completionService
operator|.
name|submit
argument_list|(
name|pending
operator|.
name|removeFirst
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|cause
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error in per-segment faceting on field: "
operator|+
name|fieldName
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|seg
operator|.
name|startTermIndex
operator|<
name|seg
operator|.
name|endTermIndex
condition|)
block|{
if|if
condition|(
name|seg
operator|.
name|startTermIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|hasMissingCount
operator|=
literal|true
expr_stmt|;
name|missingCount
operator|+=
name|seg
operator|.
name|counts
index|[
literal|0
index|]
expr_stmt|;
name|seg
operator|.
name|pos
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|seg
operator|.
name|pos
operator|=
name|seg
operator|.
name|startTermIndex
expr_stmt|;
block|}
if|if
condition|(
name|seg
operator|.
name|pos
operator|<
name|seg
operator|.
name|endTermIndex
condition|)
block|{
name|seg
operator|.
name|tenum
operator|=
name|seg
operator|.
name|si
operator|.
name|termsEnum
argument_list|()
expr_stmt|;
name|seg
operator|.
name|tenum
operator|.
name|seekExact
argument_list|(
name|seg
operator|.
name|pos
argument_list|)
expr_stmt|;
name|seg
operator|.
name|tempBR
operator|=
name|seg
operator|.
name|tenum
operator|.
name|term
argument_list|()
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|seg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|FacetCollector
name|collector
decl_stmt|;
if|if
condition|(
name|sort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT
argument_list|)
operator|||
name|sort
operator|.
name|equals
argument_list|(
name|FacetParams
operator|.
name|FACET_SORT_COUNT_LEGACY
argument_list|)
condition|)
block|{
name|collector
operator|=
operator|new
name|CountSortedFacetCollector
argument_list|(
name|offset
argument_list|,
name|limit
argument_list|,
name|mincount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collector
operator|=
operator|new
name|IndexSortedFacetCollector
argument_list|(
name|offset
argument_list|,
name|limit
argument_list|,
name|mincount
argument_list|)
expr_stmt|;
block|}
name|BytesRefBuilder
name|val
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SegFacet
name|seg
init|=
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
comment|// we will normally end up advancing the term enum for this segment
comment|// while still using "val", so we need to make a copy since the BytesRef
comment|// may be shared across calls.
name|val
operator|.
name|copyBytes
argument_list|(
name|seg
operator|.
name|tempBR
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|+=
name|seg
operator|.
name|counts
index|[
name|seg
operator|.
name|pos
operator|-
name|seg
operator|.
name|startTermIndex
index|]
expr_stmt|;
comment|// TODO: OPTIMIZATION...
comment|// if mincount>0 then seg.pos++ can skip ahead to the next non-zero entry.
name|seg
operator|.
name|pos
operator|++
expr_stmt|;
if|if
condition|(
name|seg
operator|.
name|pos
operator|>=
name|seg
operator|.
name|endTermIndex
condition|)
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|seg
operator|=
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|seg
operator|.
name|tempBR
operator|=
name|seg
operator|.
name|tenum
operator|.
name|next
argument_list|()
expr_stmt|;
name|seg
operator|=
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|seg
operator|!=
literal|null
operator|&&
name|val
operator|.
name|get
argument_list|()
operator|.
name|compareTo
argument_list|(
name|seg
operator|.
name|tempBR
argument_list|)
operator|==
literal|0
condition|)
do|;
name|boolean
name|stop
init|=
name|collector
operator|.
name|collect
argument_list|(
name|val
operator|.
name|get
argument_list|()
argument_list|,
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|stop
condition|)
break|break;
block|}
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|res
init|=
name|collector
operator|.
name|getFacetCounts
argument_list|()
decl_stmt|;
comment|// convert labels to readable form
name|FieldType
name|ft
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|int
name|sz
init|=
name|res
operator|.
name|size
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|res
operator|.
name|setName
argument_list|(
name|i
argument_list|,
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|res
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|missing
condition|)
block|{
if|if
condition|(
operator|!
name|hasMissingCount
condition|)
block|{
name|missingCount
operator|=
name|SimpleFacets
operator|.
name|getFieldMissingCount
argument_list|(
name|searcher
argument_list|,
name|docs
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|missingCount
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|class|SegFacet
class|class
name|SegFacet
block|{
DECL|field|context
name|LeafReaderContext
name|context
decl_stmt|;
DECL|method|SegFacet
name|SegFacet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|field|si
name|SortedDocValues
name|si
decl_stmt|;
DECL|field|startTermIndex
name|int
name|startTermIndex
decl_stmt|;
DECL|field|endTermIndex
name|int
name|endTermIndex
decl_stmt|;
DECL|field|counts
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
comment|// only used when merging
DECL|field|tenum
name|TermsEnum
name|tenum
decl_stmt|;
comment|// only used when merging
DECL|field|tempBR
name|BytesRef
name|tempBR
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|countTerms
name|void
name|countTerms
parameter_list|()
throws|throws
name|IOException
block|{
name|si
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
comment|// SolrCore.log.info("reader= " + reader + "  FC=" + System.identityHashCode(si));
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|BytesRefBuilder
name|prefixRef
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|prefixRef
operator|.
name|copyChars
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|startTermIndex
operator|=
name|si
operator|.
name|lookupTerm
argument_list|(
name|prefixRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|startTermIndex
operator|<
literal|0
condition|)
name|startTermIndex
operator|=
operator|-
name|startTermIndex
operator|-
literal|1
expr_stmt|;
name|prefixRef
operator|.
name|append
argument_list|(
name|UnicodeUtil
operator|.
name|BIG_TERM
argument_list|)
expr_stmt|;
comment|// TODO: we could constrain the lower endpoint if we had a binarySearch method that allowed passing start/end
name|endTermIndex
operator|=
name|si
operator|.
name|lookupTerm
argument_list|(
name|prefixRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|endTermIndex
operator|<
literal|0
assert|;
name|endTermIndex
operator|=
operator|-
name|endTermIndex
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|startTermIndex
operator|=
operator|-
literal|1
expr_stmt|;
name|endTermIndex
operator|=
name|si
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|nTerms
init|=
name|endTermIndex
operator|-
name|startTermIndex
decl_stmt|;
if|if
condition|(
name|nTerms
operator|>
literal|0
condition|)
block|{
comment|// count collection array only needs to be as big as the number of terms we are
comment|// going to collect counts for.
specifier|final
name|int
index|[]
name|counts
init|=
name|this
operator|.
name|counts
operator|=
operator|new
name|int
index|[
name|nTerms
index|]
decl_stmt|;
name|DocIdSet
name|idSet
init|=
name|baseSet
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// this set only includes live docs
name|DocIdSetIterator
name|iter
init|=
name|idSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|////
name|int
name|doc
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
comment|// specialized version when collecting counts for all terms
while|while
condition|(
operator|(
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|counts
index|[
literal|1
operator|+
name|si
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
index|]
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// version that adjusts term numbers because we aren't collecting the full range
while|while
condition|(
operator|(
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|term
init|=
name|si
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|int
name|arrIdx
init|=
name|term
operator|-
name|startTermIndex
decl_stmt|;
if|if
condition|(
name|arrIdx
operator|>=
literal|0
operator|&&
name|arrIdx
operator|<
name|nTerms
condition|)
name|counts
index|[
name|arrIdx
index|]
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class
begin_class
DECL|class|FacetCollector
specifier|abstract
class|class
name|FacetCollector
block|{
comment|/*** return true to stop collection */
DECL|method|collect
specifier|public
specifier|abstract
name|boolean
name|collect
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
DECL|method|getFacetCounts
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getFacetCounts
parameter_list|()
function_decl|;
block|}
end_class
begin_comment
comment|// This collector expects facets to be collected in index order
end_comment
begin_class
DECL|class|CountSortedFacetCollector
class|class
name|CountSortedFacetCollector
extends|extends
name|FacetCollector
block|{
DECL|field|spare
specifier|private
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|offset
specifier|final
name|int
name|offset
decl_stmt|;
DECL|field|limit
specifier|final
name|int
name|limit
decl_stmt|;
DECL|field|maxsize
specifier|final
name|int
name|maxsize
decl_stmt|;
DECL|field|queue
specifier|final
name|BoundedTreeSet
argument_list|<
name|SimpleFacets
operator|.
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|queue
decl_stmt|;
DECL|field|min
name|int
name|min
decl_stmt|;
comment|// the smallest value in the top 'N' values
DECL|method|CountSortedFacetCollector
specifier|public
name|CountSortedFacetCollector
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|mincount
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
name|limit
operator|=
name|limit
expr_stmt|;
name|maxsize
operator|=
name|limit
operator|>
literal|0
condition|?
name|offset
operator|+
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
expr_stmt|;
name|queue
operator|=
operator|new
name|BoundedTreeSet
argument_list|<>
argument_list|(
name|maxsize
argument_list|)
expr_stmt|;
name|min
operator|=
name|mincount
operator|-
literal|1
expr_stmt|;
comment|// the smallest value in the top 'N' values
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|boolean
name|collect
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|>
name|min
condition|)
block|{
comment|// NOTE: we use c>min rather than c>=min as an optimization because we are going in
comment|// index order, so we already know that the keys are ordered.  This can be very
comment|// important if a lot of the counts are repeated (like zero counts would be).
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|SimpleFacets
operator|.
name|CountPair
argument_list|<>
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>=
name|maxsize
condition|)
name|min
operator|=
name|queue
operator|.
name|last
argument_list|()
operator|.
name|val
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getFacetCounts
specifier|public
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getFacetCounts
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|off
init|=
name|offset
decl_stmt|;
name|int
name|lim
init|=
name|limit
operator|>=
literal|0
condition|?
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|// now select the right page from the results
for|for
control|(
name|SimpleFacets
operator|.
name|CountPair
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|p
range|:
name|queue
control|)
block|{
if|if
condition|(
operator|--
name|off
operator|>=
literal|0
condition|)
continue|continue;
if|if
condition|(
operator|--
name|lim
operator|<
literal|0
condition|)
break|break;
name|res
operator|.
name|add
argument_list|(
name|p
operator|.
name|key
argument_list|,
name|p
operator|.
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
begin_comment
comment|// This collector expects facets to be collected in index order
end_comment
begin_class
DECL|class|IndexSortedFacetCollector
class|class
name|IndexSortedFacetCollector
extends|extends
name|FacetCollector
block|{
DECL|field|spare
specifier|private
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|offset
name|int
name|offset
decl_stmt|;
DECL|field|limit
name|int
name|limit
decl_stmt|;
DECL|field|mincount
specifier|final
name|int
name|mincount
decl_stmt|;
DECL|field|res
specifier|final
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|res
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|IndexSortedFacetCollector
specifier|public
name|IndexSortedFacetCollector
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|mincount
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
name|limit
operator|=
name|limit
operator|>
literal|0
condition|?
name|limit
else|:
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|this
operator|.
name|mincount
operator|=
name|mincount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|boolean
name|collect
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|<
name|mincount
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|offset
operator|--
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|limit
operator|>
literal|0
condition|)
block|{
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|limit
operator|--
expr_stmt|;
block|}
return|return
name|limit
operator|<=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getFacetCounts
specifier|public
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|getFacetCounts
parameter_list|()
block|{
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
